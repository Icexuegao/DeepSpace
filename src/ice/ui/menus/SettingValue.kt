package ice.ui.menus

import arc.Core
import arc.graphics.Color
import ice.music.IceMusics
import ice.ui.tex.Colors
import mindustry.Vars

object SettingValue {
    var difficulty = ModeDifficulty.General
    fun init() {
        IceMusics.setVolume("title", menuMusicVolume)
        Vars.renderer.maxZoom = maxZoom
        Vars.renderer.minZoom = minZoom
    }

    private var menuMusic = Core.settings.getBool("menuMusic", true)
    fun menuMusic(boolean: Boolean) {
        menuMusic = boolean
        IceMusics.toggle("title",boolean)
        Core.settings.put("menuMusic", boolean)
    }

    fun getMenuMusic(): Boolean {
        return menuMusic
    }

    private var menuMusicVolume = Core.settings.getFloat("menuMusicVolume", 1f)
    fun setTitleVolume(value: Float) {
        IceMusics.setVolume("title", menuMusicVolume)
        menuMusicVolume = value
        Core.settings.put("menuMusicVolume", value)
    }

    fun getMenuMusicVolume(): Float {
        return menuMusicVolume
    }

    private var planetSectorId = Core.settings.getBool("planetSectorId", false)
    fun planetSectorId(boolean: Boolean) {
        planetSectorId = boolean
        Core.settings.put("planetSectorId", boolean)
    }

    fun getPlanetSectorId(): Boolean {
        return planetSectorId
    }

    private var maxZoom = Core.settings.getFloat("maxZoom", Vars.renderer.maxZoom)
    fun setMaxZoomLim(value: Float) {
        maxZoom = value
        Vars.renderer.maxZoom = value
        Core.settings.put("maxZoom", value)
    }

    fun getMaxZoom(): Float {
        return maxZoom
    }

    private var minZoom = Core.settings.getFloat("minZoom", Vars.renderer.minZoom)
    fun setMinZoomLim(value: Float) {
        minZoom = value
        Vars.renderer.minZoom = value
        Core.settings.put("minZoom", value)
    }

    fun getMinZoom(): Float {
        return minZoom
    }

    private var debugMode = Core.settings.getBool("debugMode", false)
    fun setDebugMode(boolean: Boolean) {
        debugMode = boolean
        Core.settings.put("debugMode", boolean)
    }

    fun getDebugMode(): Boolean {
        return debugMode
    }

    enum class ModeDifficulty(val na: String, val color: Color, val bun: String) {
        Easy("神赐", Colors.y1, "圣水淅沥,与神同行,乐园就在此处..."),
        General(
            "洗礼", Colors.b1, "福祸未分,命途难测,神谕者缄口不言"
        ),
        Suffering("棘罪", Colors.r1, "圣光暗淡,神像蒙尘,亵渎者又将何去何从?")
    }
}