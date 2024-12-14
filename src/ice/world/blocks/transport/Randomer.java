package ice.world.blocks.transport;


import arc.Core;
import arc.math.Mathf;
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

/**
 * 物品液体电力热量源Text专属
 */
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
        addBar("heat", (ItemSourceBuild entity)->new Bar("bar.heat", Pal.lightOrange, ()->entity.heat / heatOutput));
        if (hasPower && outputsPower) {
            addBar("power", (ItemSourceBuild entity)->new Bar(()->Core.bundle.format("bar.poweroutput", Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)), ()->Pal.powerBar, ()->1));
        }
    }

    @Override
    public void setStats() {
        stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
        super.setStats();
    }

    public class ItemSourceBuild extends Building implements HeatBlock {
        public float heat;

        @Override
        public float getPowerProduction() {
            return powerProduction;
        }

        @Override
        public void draw() {
            super.draw();
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
            for (Item item : Vars.content.items()) {
                if (items.get(item) < itemCapacity) {
                    items.add(item, 100);
                    dump(item);
                    items.clear();
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