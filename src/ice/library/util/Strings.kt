package ice.library.util

import ice.library.util.Strings.regex

object Strings {
    val regex = Regex("^[-+]?\\d*\\.?\\d+$")
}

fun String.isNumericWithSign() = matches(regex)

/**返回float保存x位数的字符串*/
fun Float.toStringi(precision: Int) = "%.${precision}f".format(this)
