package ice.graphics;

import arc.graphics.Color;
import arc.math.Rand;
import arc.struct.Seq;

public class IceColors {
    public static Seq<Color> cos = new Seq<>();
    public static Color b, b1, sp, sp2, g1, d, black1, black2;
    public static Color getRand(){
        return cos.get(new Rand().nextInt(cos.size));
    }
    static {
        black2 = valueOf("#2f2d39");
        black1 = valueOf("#4a4b53");
        b = valueOf("#cfecf1");
        b1 = valueOf("97abb7");
        sp = valueOf("#ed90df");
        sp2 = valueOf("#C384B7");
        g1 = valueOf("#1DFF00");
        d = valueOf("#FFECF8FF");
    }

    public static Color valueOf(String hex) {
        Color color = new Color();
        cos.add(color);
        return Color.valueOf(color, hex);
    }
}
