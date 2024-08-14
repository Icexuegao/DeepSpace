package ice.type;

import ice.ui.stat.IceStat;
import ice.ui.stat.IceStats;
import arc.graphics.Color;
import mindustry.type.Item;

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
        if (nutrientConcentration != 0) {
            stats1.addPercentThrob(IceStat.nutrientConcentration, nutrientConcentration, "[red]");
        }
        super.setStats();
    }

    public interface ItemLambda {
        IceItem get(IceItem item);
    }
}
