package ice.content.block.turret

import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.game.EventType
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.type.Category
import singularity.world.blocks.turrets.SglTurret

class 热泉 :SglTurret("turret_hotSprings") {
  var time = 3f

  init {
    localization {
      zh_CN {
        localizedName = "热泉"
      }
    }
    liquidCapacity=40f
    itemCapacity=20
    recoilTime=45f
    inaccuracy = 30f
    health = 1500
    armor = 4f
    size = 2
    range = 18 * 8f
    requirements(Category.turret, IItems.铅锭, 120, IItems.铜锭, 60, IItems.钴锭, 60, IItems.石英玻璃, 40)
    setAmmo()
    limitRange()
  }

  fun getBuller() = BulletType().apply {
    shootEffect = Fx.shootSmallFlame
    hitEffect = Fx.hitFlameSmall
    despawnEffect = Fx.none
    status = StatusEffects.burning
  }

  override fun setAmmo() {
    newAmmo(getBuller().apply {
      damage = 60f
      status = IStatus.冻结
      statusDuration = 1f * 60f
      EventType.addContentInitEvent {
        shootEffect = IceEffects.changeFlame(speed * lifetime, ILiquids.急冻液.color, IItems.低温化合物.color)
      }
    })
    consume?.apply {
      time(time)
      item(IItems.低温化合物, 1)
      liquids(ILiquids.二氧化碳, 6f / 60f)
    }

    newAmmo(getBuller().apply {
      damage = 280f
      status = StatusEffects.burning
      statusDuration = 0.5f * 60f
      EventType.addContentInitEvent {
        shootEffect = IceEffects.changeFlame(speed * lifetime, ILiquids.氢气.color, ILiquids.氢气.color.mul(1.2f))
      }
    })
    consume?.apply {
      time(time)
      liquids(ILiquids.氧气, 4f / 60f, ILiquids.氢气, 4f / 60f)
    }

    newAmmo(getBuller().apply {
      damage = 540f
      status = StatusEffects.burning
      statusDuration = 0.5f * 60f
      EventType.addContentInitEvent {
        shootEffect = IceEffects.changeFlame(speed * lifetime)
      }
    })
    consume?.apply {
      time(time)
      item(IItems.硫化合物, 1)
      liquids(ILiquids.氧气, 8f / 60f)
    }

    newAmmo(getBuller().apply {
      damage = 360f
      status = IStatus.蚀骨
      statusDuration = 1f * 60f
      EventType.addContentInitEvent {
        shootEffect = IceEffects.changeFlame(speed * lifetime, ILiquids.氯气.color, ILiquids.氢气.color)
      }
    })
    consume?.apply {
      time(time)
      liquids(ILiquids.氯气, 4f / 60f, ILiquids.氢气, 4f / 60f)
    }
  }
}