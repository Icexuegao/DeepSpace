package ice.Alon.type;

import arc.graphics.Color;
import ice.Alon.ui.stat.IceStat;
import ice.Alon.ui.stat.IceStats;
import mindustry.type.Item;

import java.io.Serializable;

public class IceItem extends Item {
    public float nutrientConcentration = 0;

    public IceItem(String name) {
        this(name, new Color(Color.black));
    }

    public IceItem(String name, Color color) {
        super(name);
        this.color = color;
    }

    public IceItem(String name, String color) {
        super(name);
        this.color = Color.valueOf(color);
    }

    public IceItem(String name, String color, ItemLambda lambda) {
        super(name);
        lambda.get(this);
        this.color = Color.valueOf(color);
    }

    @Override
    public void init() {
        stats = new IceStats();
        super.init();
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

    public interface ItemLambda {
        IceItem get(IceItem item);
    }
}
