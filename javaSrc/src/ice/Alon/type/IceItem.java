package ice.Alon.type;

import arc.graphics.Color;
import arc.struct.Seq;
import ice.Alon.world.meta.stat.IceStat;
import ice.Alon.world.meta.stat.IceStats;
import mindustry.type.Item;

public class IceItem extends Item {
    /** 物品合集 */
    public static Seq<IceItem> items = new Seq<>();
    /** 营养浓度 */
    public float nutrientConcentration = 0;

    public IceItem(String name) {
        this(name, new Color(Color.black));
    }

    public IceItem(String name, Color color) {
        super(name);
        this.color = color;
        items.add(this);
    }

    public IceItem(String name, String color) {
        this(name, Color.valueOf(color));
    }

    @Override
    public void init() {
        stats = new IceStats();
        super.init();
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setStats() {
        IceStats stats1 = (IceStats) stats;
        stats1.addPercent(IceStat.explosiveness, explosiveness);
        stats1.addPercent(IceStat.flammability, flammability);
        stats1.addPercent(IceStat.radioactivity, radioactivity);
        stats1.addPercent(IceStat.charge, charge);
        if (nutrientConcentration != 0) {
            stats1.addPercentThrob(IceStat.nutrientConcentration, nutrientConcentration, "[red]");
        }
        stats1.add(IceStat.cost, cost);
        stats1.add(IceStat.hardness, hardness);
        stats1.add(IceStat.healthScaling, healthScaling);
        stats1.add(IceStat.buildable, buildable);
    }
}
