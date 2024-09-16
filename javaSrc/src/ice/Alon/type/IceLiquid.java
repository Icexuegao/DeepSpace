package ice.Alon.type;

import arc.graphics.Color;
import ice.Alon.world.meta.stat.IceStat;
import ice.Alon.world.meta.stat.IceStats;
import mindustry.type.Liquid;

public class IceLiquid extends Liquid {
    public float nutrientConcentration = 0;

    public IceLiquid(String name, String color) {
        super(name, Color.valueOf(color));
    }

    @Override
    public void init() {
        stats = new IceStats();
        super.init();
    }

    @Override
    public void setStats() {
        IceStats stats1 = (IceStats) stats;
        if (nutrientConcentration != 0) {
            stats1.addPercentThrob(IceStat.nutrientConcentration, nutrientConcentration, "[red]");
        }
        super.setStats();
    }
}
