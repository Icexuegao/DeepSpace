package ice.ui.bundle

/** 支持本地化的对象接口 */
interface Localizable {
  fun setLocalizedName(localizedName: String)
  fun setDescription(description: String)
  fun setDetails(details: String)

  fun localization(block: LocalizationMap.() -> Unit) {
    LocalizationManager.register(this, LocalizationMap().apply(block))
  }
}