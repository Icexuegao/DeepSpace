package universecore.util

import arc.graphics.Color

object Strings {
  val regex = Regex("^[-+]?\\d*\\.?\\d+$")
}

fun String.isNumericWithSign() = matches(Strings.regex)
/**扫描字符串中所有数字及百分比格式的子串，并对每个匹配项应用转换函数，将结果替换回原位置。
 *
 * 匹配规则：使用正则表达式 \d+(?:\.\d+)?%?，可识别以下格式：
 * - 整数，如 234、0、812
 * - 小数，如 1.2、0.4343
 * - 百分比，如 123%、5.45%
 * - 嵌入文本中的数字，如 abc123def 中的 123
 *
 * 使用示例：
 * ```
 * val desc = "伤害 123% 速度 1.2 等级 234 暴击 5.45%"
 * val colored = desc.replaceNumericMatches { match ->
 *     when {
 *         match.endsWith("%") -> "[accent]$match[]"
 *         match.contains(".") -> "[sky]$match[]"
 *         else -> "[stat]$match[]"
 *     }
 * }
 * // colored: "伤害 [accent]123%[] 速度 [sky]1.2[] 等级 [stat]234[] 暴击 [accent]5.45%[]"
 * ```
 *
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
 * 该方法会先按照指定的小数位数进行四舍五入格式化，然后移除小数部分末尾多余的零。
 * 如果小数部分全部被移除（即结果为整数），则会同时移除小数点。
 *
 * 格式化规则：
 * - 保留指定位数的小数（遵循四舍五入规则）
 * - 移除小数部分末尾所有的零（如 1.200 → 1.2）
 * - 如果小数部分全为零，则移除小数点（如 3.00 → 3）
 * - 不会对整数部分进行千位分隔或其他格式化
 *
 * 使用示例：
 * ```
 * 123.456f.toTrimmedString(2)  // 返回 "123.46" (四舍五入)
 * 100.0f.toTrimmedString(3)    // 返回 "100"   (去除小数点和末尾零)
 * 3.14000f.toTrimmedString(4)  // 返回 "3.14"  (保留有效小数)
 * 5.0f.toTrimmedString(1)      // 返回 "5"     (整数时移除小数点)
 * 2.71828f.toTrimmedString(3)  // 返回 "2.718" (保留3位小数)
 * ```
 *
 * @param precision 小数保留位数，必须为非负整数。如果为0，则返回四舍五入后的整数（无小数点）
 * @return 格式化后的字符串，不包含无意义的末尾零和多余的小数点
 * @throws IllegalArgumentException 如果 precision < 0
 * @author Alon
 */
fun Float.toTrimmedString(precision: Int) = "%.${precision}f".format(this).removeTrailingZeros()

fun String.removeTrailingZeros() = if (!contains('.')) this else trimEnd('0').trimEnd('.')



