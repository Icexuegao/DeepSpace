package ice.type;

import arc.Core;
import arc.graphics.Color;
import arc.struct.Seq;
import ice.world.meta.stat.IceStat;
import ice.world.meta.stat.IceStats;
import mindustry.type.Liquid;

public class IceLiquid extends Liquid {
    public static Seq<IceLiquid> liquids = new Seq<>();
    public float nutrientConcentration = 0;

    public IceLiquid(String name, String color) {
        super(name, Color.valueOf(color));
        liquids.add(this);
        localizedName = Core.bundle.get(getContentType() + "." + name + ".name", name);
        description = Core.bundle.getOrNull(getContentType() + "." + name + ".description");
        details = Core.bundle.getOrNull(getContentType() + "." + name + ".details");
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
            stats1.addPercentThrob(IceStat.营养浓度, nutrientConcentration, "[red]");
        }
        super.setStats();
    }
}
