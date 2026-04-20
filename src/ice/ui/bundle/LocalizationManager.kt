package ice.ui.bundle

import arc.Core
import ice.library.world.Load
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent
import singularity.world.meta.SglStat

/**本地化管理器 - 统一管理所有本地化数 */
object LocalizationManager :Load {
  private val registry = mutableMapOf<Any, LocalizationMap>()
  private var loaded = false

  override fun load() {
    if (loaded) return
    IceStats.load()
    SglStat.empArmor
    val currentLocale = Core.settings.getString("locale", "zh_CN")

    registry.forEach { (target, map) ->
      val data = map.getData(currentLocale) ?: map.getData("zh_CN")
      data?.let {
        when(target) {
          is Localizable -> {
            if (it.name.isNotEmpty()) target.localizedName=it.name
            if (it.description.isNotEmpty()) target.description=it.description
            if (it.details.isNotEmpty()) target.details=it.details
          }

          is UnlockableContent -> {
            if (it.name.isNotEmpty()) target.localizedName = it.name
            if (it.description.isNotEmpty()) target.description = it.description
            if (it.details.isNotEmpty()) target.details = it.details
          }
        }
      }
    }

    loaded = true
  }

  internal fun register(target: Any, map: LocalizationMap) {
    if (loaded && target is Localizable) {
      map.getData(Core.settings.getString("locale", "zh_CN"))?.let {
        if (it.name.isNotEmpty()) target.localizedName=it.name
        if (it.description.isNotEmpty()) target.description=it.description
        if (it.details.isNotEmpty()) target.details=it.details
      }
      return
    }

    // 检查类型
    require(target is Localizable || target is UnlockableContent) {
      "Target must be Localizable or UnlockableContent"
    }
    registry[target] = map
  }

  fun clear() {
    registry.clear()
    loaded = false
  }
}