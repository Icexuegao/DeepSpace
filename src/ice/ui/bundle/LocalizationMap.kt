package ice.ui.bundle

/** 多语言映射表*/
class LocalizationMap {
  private val locales = mutableMapOf<String, LocalizationData>()

  fun locale(code: String, block: LocalizationData.() -> Unit) {
    locales.getOrPut(code) { LocalizationData() }.apply(block)
  }

  fun zh_CN(block: LocalizationData.() -> Unit) = locale("zh_CN", block)
  fun en(block: LocalizationData.() -> Unit) = locale("en", block)

  internal fun getData(locale: String): LocalizationData? = locales[locale]

}