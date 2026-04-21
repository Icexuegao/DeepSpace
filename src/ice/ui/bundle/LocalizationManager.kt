package ice.ui.bundle

import arc.Core
import ice.library.world.Load
import mindustry.ctype.UnlockableContent

/**本地化管理器 - 统一管理所有本地化 */
object LocalizationManager :Load {
  private val registry = mutableMapOf<Any, LocalizationMap>()
  private var loaded = false
  private val parserMap = HashMap<Class<*>, (Any, LocalizationData) -> Unit>()

  init {
    registerParsing(UnlockableContent::class.java) { un, data ->
      un.localizedName = data.localizedName
      un.description = data.description
      un.details = data.details
    }
    registerParsing(Localizable::class.java) { localizable, data ->
      localizable.localizedName = data.localizedName
      localizable.description = data.description
      localizable.details = data.details
    }
  }

  override fun load() {
    if (loaded) return
    registry.forEach(::parsing)
    loaded = true
  }

  @Suppress("UNCHECKED_CAST")
  fun <T> registerParsing(classz: Class<T>, parser: (T, LocalizationData) -> Unit) {
    parserMap[classz] = parser as (Any, LocalizationData) -> Unit
  }

  private fun parsing(target: Any, map: LocalizationMap) {
    val data = map.getData(Core.settings.getString("locale", "zh_CN")) ?: map.getData("zh_CN")
    data?.let {
      findParser(target::class.java)?.invoke(target, it)
    }
  }

  private fun findParser(clazz: Class<*>): ((Any, LocalizationData) -> Unit)? {
    var current: Class<*>? = clazz
    while(current != null) {
      parserMap[current]?.let { return it }
      current.interfaces.forEach { iface ->
        parserMap[iface]?.let { return it }
      }
      current = current.superclass
    }
    return null
  }

  internal fun registerTarget(target: Any, map: LocalizationMap) {
    if (loaded) {
      parsing(target, map)
    } else registry[target] = map
  }

  fun clear() {
    registry.clear()
    loaded = false
  }
}