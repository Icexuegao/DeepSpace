package ice.content.remains

import ice.content.IStatus
import ice.content.IUnitTypes
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import mindustry.content.StatusEffects
import mindustry.world.meta.Stats
import universecore.scene.style.DynamicTextureDrawable

class 血腥玛丽:Remains("remains_bloody_mary"){
  init {
    remainsColor = IceColor.r2
    localization {
      zh_CN {
        localizedName = "血腥玛丽"
        description = "血与酒液在杯中摇匀,辛辣之后,只余缓慢扩散的猩红"
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 13
      it.frameDuration = 15f
    }

    effect = "为核心机攻击附加流血效果"
    install = {
      for(type in IUnitTypes.getCoreUnits()) {
        for(weapon in type.weapons) {
          if (weapon.bullet.status == StatusEffects.none) {
            weapon.bullet.status = IStatus.流血
          }
        }
        type.stats = Stats()
        type.checkStats()
      }
    }
    uninstall = {
      for(type in IUnitTypes.getCoreUnits()) {
        for(weapon in type.weapons) {
          if (weapon.bullet.status == IStatus.流血) {
            weapon.bullet.status = StatusEffects.none
          }
        }
        type.stats = Stats()
        type.checkStats()
      }
    }
  }
}