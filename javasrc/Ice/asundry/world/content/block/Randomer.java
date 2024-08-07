package Ice.asundry.world.content.block;


import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.world;

public class Randomer extends PowerGenerator {
    public static float heatOutput = 1000f;
    public float warmupRate = 1f;


    public Randomer(String name) {
        super(name);
        hasLiquids = true;
        hasItems = true;
        update = true;
        solid = true;
        hasPower = true;
        outputsPower = true;
        buildType = ItemSourceBuild::new;
        itemCapacity = 4000;
        liquidCapacity = 1000f;
        powerProduction = 100;

    }

    public void setBars() {
        addBar("heat", (ItemSourceBuild entity) -> new Bar("bar.heat", Pal.lightOrange, () -> entity.heat / heatOutput));
        if (hasPower && outputsPower) {

            addBar("power", (ItemSourceBuild entity) -> new Bar(() ->

                    Core.bundle.format("bar.poweroutput", Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),

                    () -> Pal.powerBar,

                    () -> 1));

        }
    }

    @Override
    public void setStats() {
        stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
        super.setStats();
    }

    public class ItemSourceBuild extends Building implements HeatBlock {
        public float heat;
        public TextureRegion f =Core.atlas.find("ice-Sprite-0001");

        @Override
        public float getPowerProduction() {
            return powerProduction;
        }

        @Override
        public void draw() {
            Draw.rect(f,x+8,y);

        }

        @Override
        public void updateTile() {

            heat = Mathf.approachDelta(heat, heatOutput * efficiency, warmupRate * delta());
            for (Liquid l : Vars.content.liquids()) {
                if (liquids.get(l) < liquidCapacity) {
                    liquids.add(l, 100);
                    dumpLiquid(l);
                    liquids.clear();
                }
            }
            for (Item i : Vars.content.items()) {
                if (items.get(i) < itemCapacity) {
                    items.add(i, 1);
                }
                Point2[] nearby = {
                        new Point2(tile.x, tile.y + 2),
                        new Point2(tile.x + 1, tile.y + 2),
                        new Point2(tile.x + 2, tile.y + 2),
                        new Point2(tile.x + 3, tile.y + 2),

                        new Point2(tile.x, tile.y -1),
                        new Point2(tile.x + 1, tile.y -1),
                        new Point2(tile.x + 2, tile.y -1),
                        new Point2(tile.x + 3, tile.y -1),

                        new Point2(tile.x-1,tile.y),
                        new Point2(tile.x-1,tile.y+1),

                        new Point2(tile.x+4,tile.y),
                        new Point2(tile.x+4,tile.y+1)
                };
                for (Point2 p : nearby) {
                    Building build = world.build(p.x, p.y);
                    if (build!=null){
                        if (build.items!=null){
                            if (build.acceptItem(this, i) && this.canDump(build, i)) {
                                build.handleItem(this, i);
                                this.items.remove(i, 1);
                            }
                        }

                    }
                }
            }
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            return heat / heatOutput;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            heat = read.f();
        }
    }
}