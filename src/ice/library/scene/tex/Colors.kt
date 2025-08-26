package ice.library.scene.tex

import arc.graphics.Color
import arc.math.Rand
import arc.struct.Seq

object Colors {
    private var cos = Seq<Color>()
    val g2 = valueOf("578c80")
    val c5 = valueOf("c6a699")
    val c4 = valueOf("efcdcb")
    val r3 = valueOf("ff7171")
    val r1 = valueOf("a12727")
    val r2 = valueOf("bf3e47")
    val y1 = valueOf("FAC158FF")
    val y2 = valueOf("f8df87")
    val b1 = valueOf("b3cad9")
    val b2 = valueOf("b4cbd6")
    val b3 = valueOf("97abb7")
    val b5 = valueOf("ebf6ff")
    val b4 = valueOf("deedff")
    val df = valueOf("9fbdcc")
    val b6 = valueOf("bfd7e3")
    val s1 = valueOf("#ed90df")
    val w1 = valueOf("#FFECF8FF")
    val w2 = valueOf("b0bac0")
    val 灰1 = valueOf("#4a4b53")

    //植物叶子
    val zhiwu = arrayOf("e1dded", "c7c5d5", "b4b3c7")

    //砖
    val zhuan = arrayOf("c5bdcc", "ada7b4")

    //柱子
    val zhu = arrayOf("ada9bc", "888497")
    val rand: Color
        get() = cos[Rand().nextInt(cos.size)]

    fun valueOf(hex: String?): Color {
        val color = Color()
        cos.add(color)
        return Color.valueOf(color, hex)
    }
}

