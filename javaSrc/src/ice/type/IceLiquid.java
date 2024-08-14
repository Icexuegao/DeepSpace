package ice.type;

import ice.ui.stat.IceStat;
import ice.ui.stat.IceStats;
import arc.graphics.Color;
import mindustry.type.Liquid;

public class IceLiquid extends Liquid {
    public float nutrientConcentration=0;
    public IceLiquid(String name, Color color) {
        super(name, color);
    }
    public IceLiquid(String name, String color) {
        super(name, Color.valueOf(color));
    }
    public IceLiquid(String name) {
        this(name,new Color(Color.black));
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
