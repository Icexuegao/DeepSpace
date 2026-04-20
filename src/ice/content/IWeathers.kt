package ice.content

import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.library.world.Load
import ice.ui.bundle.Localizable
import mindustry.content.StatusEffects
import mindustry.gen.Sounds
import mindustry.type.Weather
import mindustry.type.weather.ParticleWeather
import mindustry.type.weather.RainWeather
import mindustry.world.meta.Attribute

object IWeathers :Load {
  var 凌雪: ParticleWeather = object :ParticleWeather("tortureSnow"), Localizable {
    override fun setLocalizedName(localizedName: String) {
      this.localizedName = localizedName
    }

    override fun setDescription(description: String) {
      this.description = description
    }

    override fun setDetails(details: String) {
      this.details = details
    }
  }.apply {
    randomParticleRotation = true
    particleRegion = "tortureSnow".appendModName()
    sizeMax = 13f
    sizeMin = 2.6f
    density = 1200f
    attrs.set(Attribute.light, -0.15f)
    sound = Sounds.wind
    soundVol = 0f
    soundVolOscMag = 1.5f
    soundVolOscScl = 1100f
    soundVolMin = 0.02f
    localization {
      zh_CN {
        name = "凌雪"
      }
    }
  }
  var 血雨: Weather = object :RainWeather("bloodRain"), Localizable {
    override fun setLocalizedName(localizedName: String) {
      this.localizedName = localizedName
    }

    override fun setDescription(description: String) {
      this.description = description
    }

    override fun setDetails(details: String) {
      this.details = details
    }
  }.apply {
    attrs.set(Attribute.light, -0.2f)
    attrs.set(Attribute.water, 0.2f)
    status = StatusEffects.wet
    sound = Sounds.rain
    color = IceColor.r1
    soundVol = 0.25f
    localization {
      zh_CN {
        name = "血雨"
      }
    }
  }
}