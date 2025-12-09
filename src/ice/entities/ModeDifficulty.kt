package ice.entities

import arc.graphics.Color
import ice.graphics.IceColor

enum class ModeDifficulty(var na: String, var color: Color, var bun: String) {
    Easy("神赐", IceColor.y1, "{SICK=2}圣水淅沥,与神同行,乐园就在此处..."), General("洗礼", IceColor.b4,
        "{HANG=2;2}福祸未分,命途难测,{ENDHANG}{JUMP}神谕者缄口不言"),
    Suffering("棘罪", IceColor.r1, "{SHAKE}{SPEED=0.4}圣光暗淡,神像蒙尘,亵渎者又将何去何从?")
}