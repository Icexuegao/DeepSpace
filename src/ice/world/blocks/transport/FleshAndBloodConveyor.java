package ice.world.blocks.transport;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.distribution.ChainedBuilding;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.*;


public class FleshAndBloodConveyor extends Block implements Autotiler {
    private static final float itemSpace = 0.4f;
    private static final int capacity = 3;

    public TextureRegion[][] regions = new TextureRegion[6][6];

    public float speed = 0f;
    public float displayedSpeed = 0f;

    public  Block junctionReplacement;

    public FleshAndBloodConveyor(String name) {
        super(name);
        rotate = true;
        update = true;
        group = BlockGroup.transportation;
        hasItems = true;
        itemCapacity = capacity;
        priority = TargetPriority.transport;
        conveyorPlacement = true;
        underBullets = true;
        ambientSound = Sounds.conveyor;
        ambientSoundVolume = 0.0022f;
        unloadable = false;
        noUpdateDisabled = false;
        buildType = FleshAndBloodConveyorBuild::new;
    }

    @Override
    public void load() {
        for (int i = 0; i < 3; i++) {
            for (int h = 0; i < 3; i++) {
                regions[i][h] = Core.atlas.find(name + "-" + i + "-" + h);
                if (regions[i][h] == null) regions[i][h] = Core.atlas.find("error");
            }
        }
        region = regions[0][0];
        super.load();
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{regions[0][0]};
    }


   /* @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        int[] bits = getTiling(plan, list);

        if (bits == null) return;
        region.w
        TextureRegion region = regions[bits[0]][0];
        Draw.rect(region, plan.drawx(), plan.drawy(), region.width * bits[1] * region.scl(), region.height * bits[2] * region.scl(), plan.rotation * 90);
    }*/

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return (otherblock.outputsItems() || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems)) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
    }

    @Override
    public boolean canReplace(Block other) {
        return true;
        /**放置*/
    }

    @Override
    public boolean isAccessible() {
        return true;
        /**访问*/
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans) {
        if (junctionReplacement == null) return this;

        Boolf<Point2> cont = p->plans.contains(o->o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof Conveyor || req.block instanceof Junction));
        return cont.get(Geometry.d4(req.rotation)) && cont.get(Geometry.d4(req.rotation - 2)) && req.tile() != null && req.tile().block() instanceof Conveyor && Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    public class FleshAndBloodConveyorBuild extends Building implements ChainedBuilding {
        //parallel array data
        public Item[] ids = new Item[capacity];
        public float[] xs = new float[capacity], ys = new float[capacity];
        //amount of items, always < capacity
        public int len = 0;
        //next entity
        public  Building next;
        public  FleshAndBloodConveyorBuild nextc;
        //whether the next conveyor's rotation == tile rotation
        public boolean aligned;

        public int lastInserted, mid;
        public float minitem = 1;

        public int blendbits, blending;
        public int blendsclx = 1, blendscly = 1;

        public float clogHeat = 0f;

        @Override
        public void draw() {
            /**frame范围0-3 包含*/
            int frame = enabled && clogHeat <= 0.5f ? (int) (((Time.time * speed * 8f * timeScale * efficiency)) % 4) : 0;
            Draw.z(Layer.blockUnder);


            /**
             * (1<<i) i=0 1 2 3  就是1* 2的i次方
             * 结果是 1 2 4 8
             *
             * */
            /**blendbits 范围是0 -1 包含  */
//draw extra conveyors facing this one for non-square tiling purposes
           /* for (int i = 0; i < 4; i++) {
                if ((blending & (1 << i)) != 0) {
                    int dir = rotation - i;
                    float rot = i == 0 ? rotation * 90 : (dir) * 90;
   Draw.rect(sliced(regions[0][frame], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize * 0.75f, y + Geometry.d4y(dir) * tilesize * 0.75f, rot);
                }
            }

            Draw.z(Layer.block - 0.2f);*/

            //   Draw.rect(regions[blendbits][frame], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);

            Draw.z(Layer.block - 0.1f);



            /**物品*/
            float layer = Layer.block - 0.1f, wwidth = world.unitWidth(), wheight = world.unitHeight(), scaling = 0.01f;
            for (int i = 0; i < len; i++) {
                Item item = ids[i];
                Tmp.v1.trns(rotation * 90, tilesize, 0);
                Tmp.v2.trns(rotation * 90, -tilesize / 2f, xs[i] * tilesize / 2f);

                float ix = (x + Tmp.v1.x * ys[i] + Tmp.v2.x), iy = (y + Tmp.v1.y * ys[i] + Tmp.v2.y);

                //keep draw position deterministic.
                Draw.z(layer + (ix / wwidth + iy / wheight) * scaling);
                Draw.rect(item.fullIcon, ix, iy, itemSize, itemSize);
            }
        }

        @Override
        public void drawCracks() {
            Draw.z(Layer.block - 0.15f);
            super.drawCracks();
        }

        @Override
        public void overwrote(Seq<Building> builds) {
            if (builds.first() instanceof FleshAndBloodConveyorBuild build) {
                ids = build.ids.clone();
                xs = build.xs.clone();
                ys = build.ys.clone();
                len = build.len;
                clogHeat = build.clogHeat;
                lastInserted = build.lastInserted;
                mid = build.mid;
                minitem = build.minitem;
                items.add(build.items);
            }
        }

        @Override
        public boolean shouldAmbientSound() {
            return clogHeat <= 0.5f;
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
/**blendbits 范围是0 -1 包含  */
            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            blendsclx = bits[1];
            blendscly = bits[2];
            blending = bits[4];

            next = front();
            nextc = next instanceof FleshAndBloodConveyorBuild && next.team == team ? (FleshAndBloodConveyorBuild) next : null;
            aligned = nextc != null && rotation == next.rotation;
        }

        @Override
        public void updateTile() {
            minitem = 1f;
            mid = 0;

            //skip updates if possible
            if (len == 0) {
                clogHeat = 0f;
                sleep();
                return;
            }

            float nextMax = aligned ? 1f - Math.max(itemSpace - nextc.minitem, 0) : 1f;
            float moved = speed * edelta();

            for (int i = len - 1; i >= 0; i--) {
                float nextpos = (i == len - 1 ? 100f : ys[i + 1]) - itemSpace;
                float maxmove = Mathf.clamp(nextpos - ys[i], 0, moved);

                ys[i] += maxmove;

                if (ys[i] > nextMax) ys[i] = nextMax;
                if (ys[i] > 0.5 && i > 0) mid = i - 1;
                xs[i] = Mathf.approach(xs[i], 0, moved * 2);

                if (ys[i] >= 1f && pass(ids[i])) {
                    //align X position if passing forwards
                    if (aligned) {
                        nextc.xs[nextc.lastInserted] = xs[i];
                    }
                    //remove last item
                    items.remove(ids[i], len - i);
                    len = Math.min(i, len);
                } else if (ys[i] < minitem) {
                    minitem = ys[i];
                }
            }

            if (minitem < itemSpace + (blendbits == 1 ? 0.3f : 0f)) {
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / 60f);
            } else {
                clogHeat = 0f;
            }

            noSleep();
        }

        public boolean pass(Item item) {
            if (item != null && next != null && next.team == team && next.acceptItem(this, item)) {
                next.handleItem(this, item);
                return true;
            }
            return false;
        }

        @Override
        public int removeStack(Item item, int amount) {
            noSleep();
            int removed = 0;

            for (int j = 0; j < amount; j++) {
                for (int i = 0; i < len; i++) {
                    if (ids[i] == item) {
                        remove(i);
                        removed++;
                        break;
                    }
                }
            }
            items.remove(item, removed);
            return removed;
        }

        @Override
        public void getStackOffset(Item item, Vec2 trns) {
            trns.trns(rotdeg() + 180f, tilesize / 2f);
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            return Math.min((int) (minitem / itemSpace), amount);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            amount = Math.min(amount, capacity - len);
            for (int i = amount - 1; i >= 0; i--) {
                add(0);
                xs[0] = 0;
                ys[0] = i * itemSpace;
                ids[0] = item;
                items.add(item, 1);
            }

            noSleep();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (len >= capacity) return false;
            Tile facing = Edges.getFacingEdge(source.tile, tile);
            if (facing == null) return false;
            int direction = Math.abs(facing.relativeTo(tile.x, tile.y) - rotation);
            return (((direction == 0) && minitem >= itemSpace) || ((direction % 2 == 1) && minitem > 0.7f)) && !(source.block.rotate && next == source);
        }

        @Override
        public void handleItem(Building source, Item item) {
            if (len >= capacity) return;

            int r = rotation;
            Tile facing = Edges.getFacingEdge(source.tile, tile);
            int ang = ((facing.relativeTo(tile.x, tile.y) - r));
            float x = (ang == -1 || ang == 3) ? 1 : (ang == 1 || ang == -3) ? -1 : 0;

            noSleep();
            items.add(item, 1);

            if (Math.abs(facing.relativeTo(tile.x, tile.y) - r) == 0) { //idx = 0
                add(0);
                xs[0] = x;
                ys[0] = 0;
                ids[0] = item;
            } else { //idx = mid
                add(mid);
                xs[mid] = x;
                ys[mid] = 0.5f;
                ids[mid] = item;
            }
        }

        public final void add(int o) {
            for (int i = Math.max(o + 1, len); i > o; i--) {
                ids[i] = ids[i - 1];
                xs[i] = xs[i - 1];
                ys[i] = ys[i - 1];
            }
            len++;
        }

        public final void remove(int o) {
            for (int i = o; i < len - 1; i++) {
                ids[i] = ids[i + 1];
                xs[i] = xs[i + 1];
                ys[i] = ys[i + 1];
            }

            len--;
        }

        @Override
        public Building next() {
            return null;
        }
    }
}
