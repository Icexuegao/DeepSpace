package ice.ui.bundle

import arc.Core
import ice.library.meta.stat.IceStats
import mindustry.ctype.UnlockableContent

open class BaseBundle {
    companion object {
         fun load() {
            bundle["${Core.settings.getString("locale")}"]?.load()
        }

        val bundle = HashMap<String, BaseBundle>().apply {
            put("zh_CN", ZNCH)
        }

    }

    fun desc(content: UnlockableContent, name: String, desc: String = "", deta: String = "") {
        content.apply {
            description = desc
            localizedName = name
            details = deta
        }
    }

    fun desc(stat: IceStats.IceStat, name: String) {
        stat.localizedName = name
    }

    open fun load() = Unit
}