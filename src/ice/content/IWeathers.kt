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

@Suppress("PROPERTY_HIDES_JAVA_FIELD")
object IWeathers :Load {
  var 凌雪: ParticleWeather = object :ParticleWeather("tortureSnow"), Localizable {
    override var localizedName: String
      get() = super.localizedName
      set(value) {
        super.localizedName = value
      }

    override var description: String
      get() = super.description
      set(value) {
        super.description = value
      }
    override var details: String
      get() = super.details
      set(value) {
        super.details = value
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
        this.localizedName = "凌雪"
      }
    }
  }
  var 血雨: Weather = object :RainWeather("bloodRain"), Localizable {
    override var localizedName: String
      get() = super.localizedName
      set(value) {
        super.localizedName = value
      }

    override var description: String
      get() = super.description
      set(value) {
        super.description = value
      }
    override var details: String
      get() = super.details
      set(value) {
        super.details = value
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
        this.localizedName = "血雨"
      }
    }
  }
}