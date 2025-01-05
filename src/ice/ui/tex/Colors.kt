package ice.ui.tex

import arc.graphics.Color
import arc.math.Rand
import arc.struct.Seq


object Colors {
    private var cos = Seq<Color>()
    val r1 = valueOf("a12727")
    val y1 = valueOf("FAC158FF")
    val b1 = valueOf("b3cad9")
    val b2 = valueOf("#cfecf1")
    val b3 = valueOf("97abb7")
    val b4 = valueOf("d6f1ff")
    val b5 = valueOf("ebf6ff")
    val b6 = valueOf("b3f1ff")
    val 紫色 = valueOf("#ed90df")
    val 淡紫色 = valueOf("#C384B7")
    val g1 = valueOf("#1DFF00")
    val 粉白色 = valueOf("#FFECF8FF")
    val 灰色 = valueOf("#4a4b53")
    val 深灰色 = valueOf("#2f2d39")

    val rand: Color
        get() = cos[Rand().nextInt(cos.size)]

    fun valueOf(hex: String?): Color {
        val color = Color()
        cos.add(color)
        return Color.valueOf(color, hex)
    }
}

