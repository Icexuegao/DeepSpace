package ice.content

import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.StatusEffects
import mindustry.gen.Sounds
import mindustry.type.weather.ParticleWeather
import mindustry.type.weather.RainWeather
import mindustry.world.meta.Attribute

object IWeathers: Load {
    var 凌雪 = ParticleWeather("tortureSnow").apply {
        randomParticleRotation = true
        particleRegion = IFiles.getNormName("tortureSnow")
        sizeMax = 13f
        sizeMin = 2.6f
        density = 1200f
        attrs.set(Attribute.light, -0.15f)
        sound = Sounds.wind
        soundVol = 0f
        soundVolOscMag = 1.5f
        soundVolOscScl = 1100f
        soundVolMin = 0.02f
        bundle {
            desc(zh_CN, "凌雪")
        }
    }
    var 血雨 = RainWeather("bloodRain").apply {
        attrs.set(Attribute.light, -0.2f)
        attrs.set(Attribute.water, 0.2f)
        status = StatusEffects.wet
        sound = Sounds.rain
        color= IceColor.r1
        soundVol = 0.25f
        bundle {
            desc(zh_CN, "血雨")

        }
    }


}