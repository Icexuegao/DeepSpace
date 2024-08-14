package ice.ui.stat;

import arc.Core;
import arc.struct.Seq;
import mindustry.world.meta.Stat;

public class IceStat extends Stat {

    public static Stat nutrientConcentration;
    public static Seq<Stat> allStat = new Seq<>();

    static {
        nutrientConcentration = new IceStat("nutrientConcentration", "[red]", "{shake}");
    }

    public String color;
    public String defaultToken;

    public IceStat(String name) {
        this(name, "[lightgray]");
    }

    public IceStat(String name, String color) {
        this(name, color, "");
        this.color = color;
    }

    public IceStat(String name, String color, String defaultToken) {
        super(name);
        allStat.add(this);
        this.color = color;
        this.defaultToken = defaultToken;
    }

    @Override
    public String localized() {
        return Core.bundle.get("stat." + name);
    }
}
