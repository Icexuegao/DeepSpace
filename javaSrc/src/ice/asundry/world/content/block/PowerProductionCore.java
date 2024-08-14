package ice.asundry.world.content.block;

import arc.Core;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;

public class PowerProductionCore extends CoreBlock {
    public static float powerProduction;

    public PowerProductionCore(String name) {
        super(name);
        powerProduction = 0;
        buildType = PowerProductionCoreBlockBuild::new;
    }

    @Override
    public void setBars() {
        addBar("power", (PowerProductionCoreBlockBuild e) -> new Bar(() -> Core.bundle.format("bar.poweroutput", Strings.fixed(Math.max(e.getPowerProduction(), 0) * 60 * e.timeScale(), 1)), () -> Pal.powerBar, () -> e.potentialEfficiency));
        super.setBars();
    }

    @Override
    public void setStats() {
        stats.add(Stat.basePowerGeneration, powerProduction * 60);
        super.setStats();
    }

    public class PowerProductionCoreBlockBuild extends CoreBuild {
        @Override
        public float getPowerProduction() {
            return powerProduction;
        }
    }
}
