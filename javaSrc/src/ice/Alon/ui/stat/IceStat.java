package ice.Alon.ui.stat;

import arc.Core;
import arc.struct.Seq;
import mindustry.world.meta.Stat;

public class IceStat extends Stat {

    public static Stat nutrientConcentration, cost, healthScaling, hardness, buildable;
    public static Seq<Stat> allStat = new Seq<>();
    public String color;
    public String defaultToken;
    public boolean Flable = false;

    static {
        nutrientConcentration = new IceStat("nutrientConcentration", "[red]", "{shake}",true);
        cost = new IceStat("cost");
        healthScaling = new IceStat("healthScaling");
        hardness = new IceStat("hardness");
        buildable = new IceStat("buildable");
    }


    public IceStat(String name) {
        this(name, "[lightgray]");
    }

    public IceStat(String name, String color) {
        this(name, color, "");
    }

    public IceStat(String name, String color, String defaultToken) {
        this(name, color, defaultToken,false);
    }

    public IceStat(String name, String color, String defaultToken, boolean Flable) {
        super(name);
        allStat.add(this);
        this.color = color;
        this.defaultToken = defaultToken;
        this.Flable = Flable;
    }

    @Override
    public String localized() {
        return Core.bundle.get("stat." + name);
    }
}
