package ice.content.unit

import ice.content.IStatus
import ice.content.IUnitTypes
import ice.entities.bullet.spawnBulletType
import universecore.util.toColor

import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.DeathGiftAbility
import ice.world.content.unit.ability.FlashbackAbility
import mindustry.entities.abilities.RegenAbility

class Wripple {
  init {
    val wycu = getUnit("unit_wrippleCopy").apply {
      localization {
        zh_CN {
          this.localizedName = "文漪副本"
          description = "描述文本"
        }
      }
      hidden = true
    }

    getUnit("unit_wripple").apply {
      localization {
        zh_CN {
          this.localizedName = "文漪"
          description = "精英作战部队,能够呼叫增援,以生物钢作为主要材料,辅以陶钢作为电磁屏蔽层,一般装备甚至无法留下划痕,同时在澎湃的能量输出下,其回复速度令人惊异"
          details = "控制中枢与动力炉紧密相连,在内部结构大规模受损导致动力炉失稳融毁后会一同损毁"
        }
      }
      abilities.add(FlashbackAbility(0.1f, 4, 80f, IUnitTypes.陨星, wycu))
    }

  }

  fun getUnit(name: String): IceUnitType {
    return object :IceUnitType(name) {
      init {

        flying = true
        lowAltitude = true
        health = 57300f
        armor = 26f
        hitSize = 38f
        speed = 1.1f
        rotateSpeed = 1.6f
        engineSize = 6f
        engineOffset = 24f
        outlineColor = "1F1F1F".toColor()
        abilities.addAll(RegenAbility().also { it.percentAmount = 1f / 100f }, DeathGiftAbility(320f, IStatus.复仇, 900f, 0.2f, 500f))
        setWeapon {
          reload = 900f
          bullet = spawnBulletType(240f, IUnitTypes.陨石)
        }
        setWeapon {
          reload = 1500f
          bullet = spawnBulletType(240f, IUnitTypes.陨铁)
        }
      }
    }
  }
}