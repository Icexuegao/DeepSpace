package ice.vars

import arc.Core
import arc.graphics.Color
import ice.Ice
import ice.library.scene.texs.Colors
import ice.music.IceMusics
import mindustry.Vars

object SettingValue {
    var difficulty = ModeDifficulty.General
        get() {
            return ModeDifficulty.valueOf(
                Core.settings.getString("${Ice.name}-modeDifficulty", ModeDifficulty.General.name))
        }
        set(value) {
            Core.settings.put("${Ice.name}-modeDifficulty", value.name)
            field = value
        }
    var menuMusic: Boolean = true
        get() {
            return Core.settings.getBool("${Ice.name}-menuMusic", true)
        }
        set(value) {
            field = value
            if (value) {
                IceMusics.title.play()
            } else {
                IceMusics.title.pause(true)
            }

            Core.settings.put("${Ice.name}-menuMusic", value)
        }
    var menuMusicVolume = 1f
        get() {
            IceMusics.title.isLooping = true
            val float = Core.settings.getFloat("${Ice.name}-menuMusicVolume", 1f)
            IceMusics.title.volume = float
            field = float
            return field
        }
        set(value) {
            IceMusics.title.volume = value
            field = value
            Core.settings.put("${Ice.name}-menuMusicVolume", value)
        }
    var planetSectorId = false
        get() {
            val bool = Core.settings.getBool("${Ice.name}-planetSectorId", false)
            field = bool
            return field
        }
        set(value) {
            field = value
            Core.settings.put("${Ice.name}-planetSectorId", field)
        }
    var maxZoom = 6f
        get() {
            val float = Core.settings.getFloat("${Ice.name}-maxZoom", Vars.renderer.maxZoom)
            maxZoom = float
            Vars.renderer.maxZoom = float
            return float
        }
        set(value) {
            field = value
            Vars.renderer.maxZoom = value
            Core.settings.put("${Ice.name}-maxZoom", value)
        }
    var minZoom = 1.5f
        get() {
            val float = Core.settings.getFloat("${Ice.name}-minZoom", Vars.renderer.minZoom)
            Vars.renderer.minZoom = float
            field = float
            return float
        }
        set(value) {
            field = value
            Vars.renderer.minZoom = value
            Core.settings.put("${Ice.name}-minZoom", value)
        }
    var debugMode = false
        get() {
            return Core.settings.getBool("${Ice.name}-debugMode", false)
        }
        set(value) {
            field = value
            Core.settings.put("${Ice.name}-debugMode", value)
        }
    enum class ModeDifficulty(val na: String, val color: Color, val bun: String) {
        Easy("神赐", Colors.y1, "圣水淅沥,与神同行,乐园就在此处..."),
        General("洗礼", Colors.b4, "福祸未分,命途难测,神谕者缄口不言"),
        Suffering("棘罪", Colors.r1, "圣光暗淡,神像蒙尘,亵渎者又将何去何从?")
    }
}