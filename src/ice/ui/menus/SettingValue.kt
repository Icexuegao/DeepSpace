package ice.ui.menus

import arc.Core
import ice.music.IceMusics
import mindustry.Vars

object SettingValue {

    fun init() {
        IceMusics.get("title").setVolume(menuMusicVolume)
        Vars.renderer.maxZoom = maxZoom
        Vars.renderer.minZoom = minZoom
    }

    var menuMusic = Core.settings.getBool("menuMusic", true)
    var menuMusicVolume = Core.settings.getFloat("menuMusicVolume", 1f)
    var shown = Core.settings.getBool("planetSectorId", false)
    var maxZoom = Core.settings.getFloat("maxZoom", Vars.renderer.maxZoom)
    var minZoom = Core.settings.getFloat("minZoom", Vars.renderer.minZoom)
    fun planetSectorId(boolean: Boolean) {
        shown = boolean
        Core.settings.put("planetSectorId", boolean)
    }

    fun menuMusic(boolean: Boolean) {
        menuMusic = boolean
        IceMusics.toggle("title", boolean)
        Core.settings.put("menuMusic", boolean)
    }

    fun setTitleVolume(value: Float) {
        IceMusics.get("title").setVolume(menuMusicVolume)
        menuMusicVolume = value
        Core.settings.put("menuMusicVolume", value)
    }

    fun setMaxZoomLim(value: Float) {
        maxZoom=value
        Vars.renderer.maxZoom = value
        Core.settings.put("maxZoom", value)
    }

    fun setMinZoomLim(value: Float) {
        minZoom=value
        Vars.renderer.minZoom = value
        Core.settings.put("minZoom", value)
    }
}