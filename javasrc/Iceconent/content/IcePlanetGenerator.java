package Iceconent.content;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.FloatSeq;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Tmp;
import arc.util.noise.Noise;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.ai.Astar;
import mindustry.ai.BaseRegistry;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.game.Rules;
import mindustry.game.Schematics;
import mindustry.game.Team;
import mindustry.game.Waves;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.maps.generators.BaseGenerator;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.*;

public class IcePlanetGenerator extends PlanetGenerator {
    public static boolean alt = false;
    BaseGenerator basegen = new BaseGenerator();
    float scl = 5.0F;
    float waterOffset = 0.07F;
    boolean genLakes = false;
    Block[][] arr;
    ObjectMap<Block, Block> dec;
    ObjectMap<Block, Block> tars;
    float water;

    public IcePlanetGenerator() {
        arr = new Block[][]{{Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone}, {Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone}, {Blocks.tar, Blocks.tar, Blocks.tar, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone}, {Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone, Blocks.redStone}};
        dec = ObjectMap.of(new Object[]{Blocks.redStone});
        tars = ObjectMap.of(new Object[]{Blocks.redStone});
        water = 2.0F / (float) this.arr[0].length;
    }

    float rawHeight(Vec3 position) {
        position = Tmp.v33.set(position).scl(this.scl);
        return (Mathf.pow(Simplex.noise3d(this.seed, 7.0, 0.5, 0.3333333432674408, position.x, position.y, position.z), 2.3F) + this.waterOffset) / (1.0F + this.waterOffset);
    }

    @Override
    public boolean allowLanding(Sector sector) {
        return super.allowLanding(sector);
    }

    @Override
    public void addWeather(Sector sector, Rules rules) {
        super.addWeather(sector, rules);
    }

    @Override
    public float getSizeScl() {
        return super.getSizeScl();
    }

    @Override
    public int getSectorSize(Sector sector) {
        return super.getSectorSize(sector);
    }

    @Override
    public void generate(Tiles tiles, Sector sec, int seed) {
        super.generate(tiles, sec, seed);
    }


    @Override
    public void postGenerate(Tiles tiles) {
        super.postGenerate(tiles);
    }

    public void generateSector(Sector sector) {
        if (sector.id != 154 && sector.id != 0) {
            PlanetGrid.Ptile tile = sector.tile;
            boolean any = false;
            float poles = Math.abs(tile.v.y);
            float noise = Noise.snoise3(tile.v.x, tile.v.y, tile.v.z, 0.001F, 0.58F);
            if ((double) noise + (double) poles / 7.1 > 0.12 && (double) poles > 0.23) {
                any = true;
            }

            if ((double) noise < 0.16) {
                PlanetGrid.Ptile[] var6 = tile.tiles;

                for (PlanetGrid.Ptile other : var6) {
                    Sector osec = sector.planet.getSector(other);
                    if (osec.id == sector.planet.startSector || osec.generateEnemyBase && (double) poles < 0.85 || sector.preset != null && (double) noise < 0.11) {
                        return;
                    }
                }
            }

            sector.generateEnemyBase = any;
        } else {
            sector.generateEnemyBase = true;
        }
    }

    public float getHeight(Vec3 position) {
        float height = this.rawHeight(position);
        return Math.max(height, this.water);
    }

    public Color getColor(Vec3 position) {
        Block block = this.getBlock(position);
        return block == Blocks.salt ? Blocks.sand.mapColor : Tmp.c1.set(block.mapColor).a(1.0F - block.albedo);
    }

    public void genTile(Vec3 position, TileGen tile) {
        tile.floor = this.getBlock(position);
        tile.block = tile.floor.asFloor().wall;
        if ((double) Ridged.noise3d(this.seed + 1, position.x, position.y, position.z, 2, 22.0F) > 0.31) {
            tile.block = Blocks.air;
        }

    }

    Block getBlock(Vec3 position) {
        float height = this.rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(this.scl);
        float rad = this.scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2.0F) / rad);
        float tnoise = Simplex.noise3d(this.seed, 7.0, 0.56, 0.3333333432674408, position.x, position.y + 999.0F, position.z);
        temp = Mathf.lerp(temp, tnoise, 0.5F);
        height *= 1.2F;
        height = Mathf.clamp(height);
        float tar = Simplex.noise3d(this.seed, 4.0, 0.550000011920929, 0.5, position.x, position.y + 999.0F, position.z) * 0.3F + Tmp.v31.dst(0.0F, 0.0F, 1.0F) * 0.2F;
        Block res = this.arr[Mathf.clamp((int) (temp * (float) this.arr.length), 0, this.arr[0].length - 1)][Mathf.clamp((int) (height * (float) this.arr[0].length), 0, this.arr[0].length - 1)];
        return tar > 0.5F ? this.tars.get(res, res) : res;
    }

    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag) {
        Vec3 v = this.sector.rect.project(x, y).scl(5.0F);
        return Simplex.noise3d(this.seed, octaves, falloff, 1.0 / scl, v.x, v.y, v.z) * (float) mag;
    }

    protected void generate() {

        class Room {
            final int x;
            final int y;
            final int radius;
            final ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void join(int x1, int y1, int x2, int y2) {
                float nscl = rand.random(100f, 140f) * 6f;
                int stroke = rand.random(3, 9);
                brush(pathfind(x1, y1, x2, y2, tile -> (tile.solid() ? 50f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan), stroke);
            }

            void connect(Room to) {
                if (!connected.add(to) || to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                if (alt) {
                    midpoint.add(Tmp.v2.set(1, 0f).setAngle(Angles.angle(to.x, to.y, x, y) + 90f * (rand.chance(0.5) ? 1f : -1f)).scl(Tmp.v1.dst(x, y) * 2f));
                } else {
                    //add randomized offset to avoid straight lines
                    midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                }

                midpoint.sub(width / 2f, height / 2f).limit(width / 2f / Mathf.sqrt3).add(width / 2f, height / 2f);

                int mx = (int) midpoint.x, my = (int) midpoint.y;

                join(x, y, mx, my);
                join(mx, my, to.x, to.y);
            }

            void joinLiquid(int x1, int y1, int x2, int y2) {
                float nscl = rand.random(100f, 140f) * 6f;
                int rad = rand.random(7, 11);
                int avoid = 2 + rad;
                Seq<Tile> path = pathfind(x1, y1, x2, y2, tile -> (tile.solid() || !tile.floor().isLiquid ? 70f : 0f) + noise(tile.x, tile.y, 2, 0.4f, 1f / nscl) * 500, Astar.manhattan);
                path.each(t -> {
                    //don't place liquid paths near the core
                    if (Mathf.dst2(t.x, t.y, x2, y2) <= avoid * avoid) {
                        return;
                    }

                    for (int x = -rad; x <= rad; x++) {
                        for (int y = -rad; y <= rad; y++) {
                            int wx = t.x + x, wy = t.y + y;
                            if (Structs.inBounds(wx, wy, width, height) && Mathf.within(x, y, rad)) {
                                Tile other = tiles.getn(wx, wy);
                                other.setBlock(Blocks.air);
                                if (Mathf.within(x, y, rad - 1) && !other.floor().isLiquid) {
                                    Floor floor = other.floor();
                                    other.setFloor((Floor) (floor == Blocks.sand || floor == Blocks.salt ? Blocks.sandWater : Blocks.redStone));
                                }
                            }
                        }
                    }
                });
            }

            void connectLiquid(Room to) {
                if (to == this) return;

                Vec2 midpoint = Tmp.v1.set(to.x, to.y).add(x, y).scl(0.5f);
                rand.nextFloat();

                //add randomized offset to avoid straight lines
                midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x, y)));
                midpoint.sub(width / 2f, height / 2f).limit(width / 2f / Mathf.sqrt3).add(width / 2f, height / 2f);

                int mx = (int) midpoint.x, my = (int) midpoint.y;

                joinLiquid(x, y, mx, my);
                joinLiquid(mx, my, to.x, to.y);
            }
        }

        cells(4);
        distort(10f, 12f);

        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 5);
        Seq<Room> roomseq = new Seq<>();

        for (int i = 0; i < rooms; i++) {
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint));
            float rx = (width / 2f + Tmp.v1.x);
            float ry = (height / 2f + Tmp.v1.y);
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            roomseq.add(new Room((int) rx, (int) ry, (int) rrad));
        }

        //check positions on the map to place the player spawn. this needs to be in the corner of the map
        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(1, Math.max((int) (sector.threat * 4), 1));
        int offset = rand.nextInt(360);
        float length = width / 2.55f - rand.random(13, 23);
        int angleStep = 5;
        int waterCheckRad = 5;
        for (int i = 0; i < 360; i += angleStep) {
            int angle = offset + i;
            int cx = (int) (width / 2 + Angles.trnsx(angle, length));
            int cy = (int) (height / 2 + Angles.trnsy(angle, length));

            int waterTiles = 0;

            //check for water presence
            for (int rx = -waterCheckRad; rx <= waterCheckRad; rx++) {
                for (int ry = -waterCheckRad; ry <= waterCheckRad; ry++) {
                    Tile tile = tiles.get(cx + rx, cy + ry);
                    if (tile == null || tile.floor().liquidDrop != null) {
                        waterTiles++;
                    }
                }
            }

            if (waterTiles <= 4 || (i + angleStep >= 360)) {
                roomseq.add(spawn = new Room(cx, cy, rand.random(8, 15)));

                for (int j = 0; j < enemySpawns; j++) {
                    float enemyOffset = rand.range(60f);
                    Tmp.v1.set(cx - (float) width / 2, cy - (float) height / 2).rotate(180f + enemyOffset).add((float) width / 2, (float) height / 2);
                    Room espawn = new Room((int) Tmp.v1.x, (int) Tmp.v1.y, rand.random(8, 16));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }

                break;
            }
        }

        //clear radius around each room
        for (Room room : roomseq) {
            erase(room.x, room.y, room.radius);
        }

        //randomly connect rooms together
        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for (int i = 0; i < connections; i++) {
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        for (Room room : roomseq) {
            if (spawn != null) {
                spawn.connect(room);
            }
        }

        Room fspawn = spawn;

        cells(1);

        int tlen = tiles.width * tiles.height;
        int total = 0, waters = 0;

        for (int i = 0; i < tlen; i++) {
            Tile tile = tiles.geti(i);
            if (tile.block() == Blocks.air) {
                total++;
                if (tile.floor().liquidDrop == Liquids.water) {
                    waters++;
                }
            }
        }

        boolean naval = (float) waters / total >= 0.19f;

        //create water pathway if the map is flooded
        if (naval) {
            for (Room room : enemies) {
                room.connectLiquid(spawn);
            }
        }

        distort(10f, 6f);

        //rivers
        pass((x, y) -> {
            if (block.solid) return;

            Vec3 v = sector.rect.project(x, y);

            float rr = Simplex.noise2d(sector.id, (float) 2, 0.6f, 1f / 7f, x, y) * 0.1f;
            float value = Ridged.noise3d(2, v.x, v.y, v.z, 1, 1f / 55f) + rr - rawHeight(v) * 0f;
            float rrscl = rr * 44 - 2;

            if (fspawn != null && value > 0.17f && !Mathf.within(x, y, fspawn.x, fspawn.y, 12 + rrscl)) {
                boolean deep = value > 0.17f + 0.1f && !Mathf.within(x, y, fspawn.x, fspawn.y, 15 + rrscl);
                boolean spore = floor != Blocks.sand && floor != Blocks.salt;
                //do not place rivers on ice, they're frozen
                //ignore pre-existing liquids
                if (!(floor == Blocks.ice || floor == Blocks.iceSnow || floor == Blocks.snow || floor.asFloor().isLiquid)) {
                    floor = spore ? (Blocks.redStone) : (deep ? Blocks.water : (floor == Blocks.sand || floor == Blocks.salt ? Blocks.sandWater : Blocks.darksandWater));
                }
            }
        });

        //shoreline setup
        pass((x, y) -> {
            int deepRadius = 3;

            if (floor.asFloor().isLiquid && floor.asFloor().shallow) {

                for (int cx = -deepRadius; cx <= deepRadius; cx++) {
                    for (int cy = -deepRadius; cy <= deepRadius; cy++) {
                        if ((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius) {
                            int wx = cx + x, wy = cy + y;

                            Tile tile = tiles.get(wx, wy);
                            if (tile != null && (!tile.floor().isLiquid || tile.block() != Blocks.air)) {
                                //found something solid, skip replacing anything
                                return;
                            }
                        }
                    }
                }

                floor = floor == Blocks.redStone ? Blocks.redStone : Blocks.water;
            }
        });

        if (naval) {
            int deepRadius = 2;

            //TODO code is very similar, but annoying to extract into a separate function
            pass((x, y) -> {
                if (floor.asFloor().isLiquid && !floor.asFloor().isDeep() && !floor.asFloor().shallow) {

                    for (int cx = -deepRadius; cx <= deepRadius; cx++) {
                        for (int cy = -deepRadius; cy <= deepRadius; cy++) {
                            if ((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius) {
                                int wx = cx + x, wy = cy + y;

                                Tile tile = tiles.get(wx, wy);
                                if (tile != null && (tile.floor().shallow || !tile.floor().isLiquid)) {
                                    //found something shallow, skip replacing anything
                                    return;
                                }
                            }
                        }
                    }

                    floor = floor == Blocks.water ? Blocks.deepwater : Blocks.redStone;
                }
            });
        }

        Seq<Block> ores = Seq.with(Blocks.oreCopper, Blocks.oreLead);
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 1f;
        float addscl = 1.3f;

        if (Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.25f * addscl) {
            ores.add(Blocks.oreCoal);
        }

        if (Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.5f * addscl) {
            ores.add(Blocks.oreTitanium);
        }

        if (Simplex.noise3d(seed, 2, 0.5, scl, sector.tile.v.x + 2, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.7f * addscl) {
            ores.add(Blocks.oreThorium);
        }

        if (rand.chance(0.25)) {
            ores.add(Blocks.oreScrap);
        }

        FloatSeq frequencies = new FloatSeq();
        for (int i = 0; i < ores.size; i++) {
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.04f);
        }

        pass((x, y) -> {
            if (!floor.asFloor().hasSurface()) return;

            int offsetX = x - 4, offsetY = y + 23;
            for (int i = ores.size - 1; i >= 0; i--) {
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if (Math.abs(0.5f - noise(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.22f + i * 0.01 && Math.abs(0.5f - noise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37f + freq) {
                    ore = entry;
                    break;
                }
            }

            if (ore == Blocks.oreScrap && rand.chance(0.33)) {
                floor = Blocks.metalFloorDamaged;
            }
        });

        trimDark();

        median(2);

        if (spawn != null) {
            inverseFloodFill(tiles.getn(spawn.x, spawn.y));
        }

        tech();

        pass((x, y) -> {
            //random moss
            if (floor == Blocks.sporeMoss) {
                if (Math.abs(0.5f - noise(x - 90, y, 4, 0.8, 65)) > 0.02) {
                    floor = Blocks.moss;
                }
            }

            //tar
            if (floor == Blocks.darksand) {
                if (Math.abs(0.5f - noise(x - 40, y, 2, 0.7, 80)) > 0.25f && Math.abs(0.5f - noise(x, y + sector.id * 10, 1, 1, 60)) > 0.41f && !(roomseq.contains(r -> Mathf.within(x, y, r.x, r.y, 30)))) {
                    floor = Blocks.tar;
                }
            }

            //hotrock tweaks
            if (floor == Blocks.hotrock) {
                if (Math.abs(0.5f - noise(x - 90, y, 4, 0.8, 80)) > 0.035) {
                    floor = Blocks.basalt;
                } else {
                    ore = Blocks.air;
                    boolean all = true;
                    for (Point2 p : Geometry.d4) {
                        Tile other = tiles.get(x + p.x, y + p.y);
                        if (other == null || (other.floor() != Blocks.hotrock && other.floor() != Blocks.magmarock)) {
                            all = false;
                        }
                    }
                    if (all) {
                        floor = Blocks.magmarock;
                    }
                }
            } else if (genLakes && floor != Blocks.basalt && floor != Blocks.ice && floor.asFloor().hasSurface()) {
                float noise = noise(x + 782, y, 5, 0.75f, 260f, 1f);
                if (noise > 0.67f && !roomseq.contains(e -> Mathf.within(x, y, e.x, e.y, 14))) {
                    if (noise > 0.72f) {
                        floor = noise > 0.78f ? Blocks.redStone : (floor == Blocks.sand ? Blocks.sandWater : Blocks.redStone);
                    } else {
                        floor = (floor == Blocks.sand ? floor : Blocks.darksand);
                    }
                }
            }

            if (rand.chance(0.0075)) {
                //random spore trees
                boolean any = false;
                boolean all = true;
                for (Point2 p : Geometry.d4) {
                    Tile other = tiles.get(x + p.x, y + p.y);
                    if (other != null && other.block() == Blocks.air) {
                        any = true;
                    } else {
                        all = false;
                    }
                }
                if (any && ((block == Blocks.snowWall || block == Blocks.iceWall) || (all && block == Blocks.air && floor == Blocks.snow && rand.chance(0.03)))) {
                    block = rand.chance(0.5) ? Blocks.whiteTree : Blocks.whiteTreeDead;
                }
            }

            //random stuff
            dec:
            {
                for (int i = 0; i < 4; i++) {
                    Tile near = world.tile(x + Geometry.d4[i].x, y + Geometry.d4[i].y);
                    if (near != null && near.block() != Blocks.air) {
                        break dec;
                    }
                }

                if (rand.chance(0.01) && floor.asFloor().hasSurface() && block == Blocks.air) {
                    block = dec.get(floor, floor.asFloor().decoration);
                }
            }
        });

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);

        int ruinCount = rand.random(-2, 4);
        if (ruinCount > 0) {
            int padding = 25;

            //create list of potential positions
            for (int x = padding; x < width - padding; x++) {
                for (int y = padding; y < height - padding; y++) {
                    Tile tile = tiles.getn(x, y);
                    if (!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)) {
                        ints.add(tile.pos());
                    }
                }
            }

            ints.shuffle(rand);

            int placed = 0;
            float diffRange = 0.4f;
            //try each position
            for (int i = 0; i < ints.size && placed < ruinCount; i++) {
                int val = ints.items[i];
                int x = Point2.x(val), y = Point2.y(val);

                //do not overwrite player spawn
                if (Mathf.within(x, y, spawn.x, spawn.y, 18f)) {
                    continue;
                }

                float range = difficulty + rand.random(diffRange);

                Tile tile = tiles.getn(x, y);
                BaseRegistry.BasePart part = null;
                if (tile.overlay().itemDrop != null) {
                    part = bases.forResource(tile.drop()).getFrac(range);
                } else if (tile.floor().liquidDrop != null && rand.chance(0.05)) {
                    part = bases.forResource(tile.floor().liquidDrop).getFrac(range);
                } else if (rand.chance(0.05)) { //ore-less parts are less likely to occur.
                    part = bases.parts.getFrac(range);
                }

                //actually place the part
                if (part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, (cx, cy) -> {
                    Tile other = tiles.getn(cx, cy);
                    if (other.floor().hasSurface()) {
                        other.setOverlay(Blocks.oreScrap);
                        for (int j = 1; j <= 2; j++) {
                            for (Point2 p : Geometry.d8) {
                                Tile t = tiles.get(cx + p.x * j, cy + p.y * j);
                                if (t != null && t.floor().hasSurface() && rand.chance(j == 1 ? 0.4 : 0.2)) {
                                    t.setOverlay(Blocks.oreScrap);
                                }
                            }
                        }
                    }
                })) {
                    placed++;

                    int debrisRadius = Math.max(part.schematic.width, part.schematic.height) / 2 + 3;
                    Geometry.circle(x, y, tiles.width, tiles.height, debrisRadius, (cx, cy) -> {
                        float dst = Mathf.dst(cx, cy, x, y);
                        float removeChance = Mathf.lerp(0.05f, 0.5f, dst / debrisRadius);

                        Tile other = tiles.getn(cx, cy);
                        if (other.build != null && other.isCenter()) {
                            if (other.team() == Team.derelict && rand.chance(removeChance)) {
                                other.remove();
                            } else if (rand.chance(0.5)) {
                                other.build.health = other.build.health - rand.random(other.build.health * 0.9f);
                            }
                        }
                    });
                }
            }
        }

        //remove invalid ores
        for (Tile tile : tiles) {
            if (tile.overlay().needsSurface && !tile.floor().hasSurface()) {
                tile.setOverlay(Blocks.air);
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        for (Room espawn : enemies) {
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if (sector.hasEnemyBase()) {
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = sector.info.attack = true;
        } else {
            state.rules.winWave = sector.info.winWave = 10 + 5 * (int) Math.max(difficulty * 10, 1);
        }

        float waveTimeDec = 0.4f;

        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60f * 60f * 1f, Math.max(difficulty - waveTimeDec, 0f));
        state.rules.waves = true;
        state.rules.env = sector.planet.defaultEnv;
        state.rules.enemyCoreBuildRadius = 600f;

        //spawn air only when spawn is blocked
        state.rules.spawns = Waves.generate(difficulty, new Rand(sector.id), state.rules.attackMode, state.rules.attackMode && spawner.countGroundSpawns() == 0, naval);
    }

}