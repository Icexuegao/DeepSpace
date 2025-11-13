package ice.library.util

import arc.math.Mathf
import ice.library.util.Strings.decimal
import ice.library.util.Strings.fixDecimals
import ice.library.util.Strings.regex
import kotlin.math.abs

object Strings {
    var decimal: Int = 2
    var fixDecimals: Boolean = false
    val regex = Regex("^[-+]?\\d*\\.?\\d+$")

}

fun String.isNumericWithSign() = matches(regex)

/**返回float保存x位数的字符串*/
fun Float.toStringi(precision: Int) = "%.${precision}f".format(this)
fun percent(cur: Float, max: Float, percent: Float = cur / max, showPercent: Boolean = percent < 0.95f): String {
    return buildString {
        append(format(cur))
        if (percent < 0.99f) {
            append('/')
            append(format(max))
        }
        if (showPercent) {
            append(" [lightgray]| ")
            append((percent * 100).toInt())
            append('%')
        }
    }
}

fun format(number: Float): String {
    if (java.lang.Float.isNaN(number)) return "NaN"
    if (number == Float.POSITIVE_INFINITY) return "Inf"
    if (number == Float.NEGATIVE_INFINITY) return "-Inf"
    val abs = abs(number)
    return when {
        abs <= java.lang.Float.MIN_NORMAL -> format0(0f)
        abs < Mathf.pow(10f, -decimal.toFloat()) -> scienceFormat(number)
        abs < 1e3f || abs < Mathf.pow(10f, 1f + decimal) -> format0(number) //直接渲染
        abs < 1e6f -> "${format0(number / 1e3f)}[gray]K[]"
        abs < 1e9f -> "${format0(number / 1e6f)}[gray]M[]"
        abs < 1e12f -> "${format0(number / 1e9f)}[gray]B[]"
        else -> scienceFormat(number)
    }
}

private fun format0(number: Float): String {
    if (fixDecimals) return arc.util.Strings.fixed(number, decimal)
    return fixedPrecision(number)
}

fun fixedPrecision(v: Float): String {
    val exponent = Mathf.floor(Mathf.log(10f, abs(v))).coerceAtLeast(0)
    if (exponent >= decimal) return v.toInt().toString()
    return arc.util.Strings.fixed(v, decimal - exponent)
}

fun scienceFormat(number: Float): String {
    val exponent = Mathf.floor(Mathf.log(10f, abs(number)))
    val mantissa = number / Mathf.pow(10f, exponent.toFloat())
    return "${arc.util.Strings.fixed(mantissa, decimal)}[gray]E$exponent[]"
}
