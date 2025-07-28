package ice.ui.bundle

import arc.Core
import ice.library.type.meta.stat.IceStatCats
import ice.library.type.meta.stat.IceStats
import mindustry.ctype.UnlockableContent

open class BaseBundle {
    companion object {
        fun load() {
            bundle["bundle_${Core.settings.getString("locale")}"]?.load()
        }

        var bundle = HashMap<String, BaseBundle>().apply {
            put("bundle_zh_CN", ZNCH)
        }

    }

    val unlockableContent = HashMap<UnlockableContent, Array<String>>()
    val stat = HashMap<IceStats.IceStat, String>()
    val statCat = HashMap<IceStatCats.IceStatCat, String>()
    open fun load() {
        unlockableContent.forEach { (t, u) ->
            when (u.size) {
                1 -> {
                    t.localizedName = u[0]
                }

                2 -> {
                    t.localizedName = u[0]
                    t.description = u[1]
                }

                3 -> {
                    t.localizedName = u[0]
                    t.description = u[1]
                    t.details = u[2]
                }
            }
        }
        stat.forEach { (t, u) -> t.localizedName = u }
        statCat.forEach { (t, u) -> t.localizedName = u }
    }
}