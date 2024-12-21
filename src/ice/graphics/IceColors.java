package ice.graphics;

import arc.graphics.Color;
import arc.math.Rand;
import arc.struct.Seq;

public class IceColors {
    public static Seq<Color> cos = new Seq<>();
    public static Color b1 = valueOf("b3cad9"), b2 = valueOf("#cfecf1"), b3 = valueOf("97abb7"), b4 = valueOf("d6f1ff"),b5=valueOf("ebf6ff");

    public static Color 紫色 = valueOf("#ed90df"), 淡紫色 = valueOf("#C384B7"), g1 = valueOf("#1DFF00"), 粉白色 = valueOf("#FFECF8FF"), 灰色 = valueOf("#4a4b53"), 深灰色 = valueOf("#2f2d39");

    public static Color getRand() {
        return cos.get(new Rand().nextInt(cos.size));
    }

    public static Color valueOf(String hex) {
        Color color = new Color();
        cos.add(color);
        return Color.valueOf(color, hex);
    }
}
