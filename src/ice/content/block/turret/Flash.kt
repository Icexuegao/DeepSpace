package ice.content.block.turret

import arc.graphics.Color
import ice.content.IItems
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret

class Flash : SglTurret("flash") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "闪光", "发射带电巨浪子弹贯穿敌人,释放的大量闪电能够对集群造成十分可观的打击")
    }

    size = 2
    range = 240f
    itemCapacity = 20
    liquidCapacity = 30f
    shootSound = Sounds.shootSmite
    requirements(Category.turret, IItems.强化合金, 35, IItems.暮光合金, 40, IItems.钴锭, 45)
    newAmmo(object : BasicBulletType(6f, 72f) {
      init {
        sprite = "large-orb"
        width = 17f
        height = 21f
        hitSize = 8f

        recoilTime = 120f

        shootEffect = MultiEffect(Fx.shootTitan, Fx.colorSparkBig, object : WaveEffect() {
          init {
            colorTo = Pal.accent
            colorFrom = colorTo
            lifetime = 12f
            sizeTo = 20f
            strokeFrom = 3f
            strokeTo = 0.3f
          }
        })
        smokeEffect = Fx.shootSmokeSmite
        ammoMultiplier = 1f
        pierceCap = 3
        pierce = true
        pierceBuilding = true
        trailColor = Pal.accent
        backColor = trailColor
        hitColor = backColor
        frontColor = Color.white
        trailWidth = 2.8f
        trailLength = 9
        hitEffect = Fx.hitBulletColor
        buildingDamageMultiplier = 0.3f

        despawnEffect = MultiEffect(Fx.hitBulletColor, object : WaveEffect() {
          init {
            sizeTo = 30f
            colorTo = Pal.accent
            colorFrom = colorTo
            lifetime = 12f
          }
        })

        trailRotation = true
        trailEffect = Fx.disperseTrail
        trailInterval = 3f

        intervalBullet = object : LightningBulletType() {
          init {
            damage = 18f
            collidesAir = false
            ammoMultiplier = 1f
            lightningColor = Pal.accent
            lightningLength = 5
            lightningLengthRand = 10
            buildingDamageMultiplier = 0.25f
            lightningType = object : BulletType(0.0001f, 0f) {
              init {
                lifetime = Fx.lightning.lifetime
                hitEffect = Fx.hitLancer
                despawnEffect = Fx.none
                status = StatusEffects.shocked
                statusDuration = 10f
                hittable = false
                lightColor = Color.white
                buildingDamageMultiplier = 0.25f
              }
            }
          }
        }

        bulletInterval = 3f
      }
    }).setReloadAmount(3)

    consume?.apply {
      item(IItems.金锭, 1)
      power(2.4f)
      time(45f)
    }
    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)
  }
}