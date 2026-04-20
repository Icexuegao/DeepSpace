package ice.ui.bundle

/** 支持本地化的对象接口。
 *
 * 实现此接口的类可以通过 [localization] 方法注册多语言文本，
 * 包括名称、描述和详细信息。本地化文本通过 [LocalizationMap]
 * 按语言代码（如 "zh_CN"）分组管理。
 *
 * 给你的对象实现本地化接口后,你应当自己处理抽象字段的使用
 * ```kotlin
 *   val 无名肉块 = IceItem("item_namelessCut", "bf3e47") {
 *     localization {
 *       zh_CN {
 *         name = "无名肉块"
 *         description = "一种有机生物材料,表面布满跳动的血管"
 *         details = "我们以为北极只是冷,但是没想到,寒冷竟也能如此饥饿..."
 *       }
 *     }
 *     nutrientConcentration = 0.5f
 *   }
 * ```
 * ps:
 * ```kotlin
override var localizedName: String by UnlockableContent::localizedName
override var description: String by UnlockableContent::description
override var details: String by UnlockableContent::details
 * ```
 *
 * @author Alon
 * @see LocalizationManager
 * @see LocalizationMap
 */
interface Localizable {
  var localizedName: String
  var description: String
  var details: String

  fun localization(block: LocalizationMap.() -> Unit) {
    LocalizationManager.register(this, LocalizationMap().apply(block))
  }
}