package universecore.util

import arc.graphics.Color

object Strings {
  val regex = Regex("^[-+]?\\d*\\.?\\d+$")
}

fun String.isNumericWithSign() = matches(Strings.regex)
/**扫描字符串中所有数字及百分比格式的子串，并对每个匹配项应用转换函数，将结果替换回原位置。
 *
 * <p>匹配规则：使用正则表达式 {@code \d+(?:\.\d+)?%?}，可识别以下格式：
 * <ul>
 *   <li>整数，如 {@code 234}、{@code 0}、{@code 812}</li>
 *   <li>小数，如 {@code 1.2}、{@code 0.4343}</li>
 *   <li>百分比，如 {@code 123%}、{@code 5.45%}</li>
 *   <li>嵌入文本中的数字，如 {@code abc123def} 中的 {@code 123}</li>
 * </ul>
 *
 * ```kotlin
 * val desc = "伤害 123% 速度 1.2 等级 234 暴击 5.45%"
 * val colored = desc.replaceNumericMatches { match ->
 *     when {
 *         match.endsWith("%") -> "[accent]$match[]"
 *         match.contains(".") -> "[sky]$match[]"
 *         else -> "[stat]$match[]"
 *     }
 * }
 * // colored: "伤害 [accent]123%[] 速度 [sky]1.2[] 等级 [stat]234[] 暴击 [accent]5.45%[]"
 * }
 * ```
 * @receiver 待处理的原始字符串
 * @param transform 转换函数，接收匹配到的子串，返回用于替换的新字符串
 * @return 经过全部替换处理后的新字符串
 * @author Alon */
fun String.replaceNumericMatches(transform: (String) -> String): String {
  return """\d+(?:\.\d+)?%?""".toRegex().replace(this) { transform(it.value) }
}

fun String.applyColor(color: String): String {
  return "$color$this$color"
}
fun String.applyColor(color: Color): String {
  return "[#$color]$this[#$color]"
}

fun String.toColor(): Color = Color.valueOf(this)
/**将Float格式化为指定精度，并自动去除末尾无意义的0。
 *
 * @param precision 小数保留位数
 * @return 格式化后的字符串
 *
 * @author Alon */
fun Float.toTrimmedString(precision: Int): String {
  val formatted = "%.${precision}f".format(this)
  return formatted.removeTrailingZeros()
}

private fun String.removeTrailingZeros(): String {
  if (!contains('.')) return this
  val trimmed = trimEnd('0')
  return trimmed.trimEnd('.')
}



