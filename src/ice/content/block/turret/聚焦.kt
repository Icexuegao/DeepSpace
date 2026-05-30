package ice.content.block.turret


import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.entities.bullet.PointLaserBulletType
import ice.entities.effect.MultiEffect
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.UnitSorts
import mindustry.entities.effect.ParticleEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import singularity.world.blocks.turrets.ContinuousTurret

class 聚焦 :ContinuousTurret("turret_focus") {
  init {
    localization {
      zh_CN {
        localizedName = "聚焦"
        description = "持续发射高能激光束,对单个目标造成持续伤害并施加熔融状态"
      }
    }

    health = 1305
    armor = 2f
    size = 3
    range = 240f
    shootY = 7f
    rotateSpeed = 3f
    shootCone = 180f
    cooldownTime = 60f
    aimChangeSpeed = 3f
    unitSort = UnitSorts.strongest
    liquidCapacity = 30f
    loopSoundVolume = 1f
    loopSound = Sounds.shootLaser
    shootSound = Sounds.none

    requirements(
      Category.turret,
      IItems.铅锭, 380,
      IItems.石英玻璃, 80,
      IItems.铱板, 225,
      IItems.导能回路, 185
    )

    setAmmo()
    limitRange()
  }

  fun setAmmo() {
    newAmmo(object :PointLaserBulletType() {
      init {
        damage = 78f
        lifetime = 30f
        speed = 0f
        shake = 0.4f
        color = Color.valueOf("FFFFFF")
        ammoMultiplier = 1f

        status = StatusEffects.melting
        statusDuration = 30f

        damageInterval = 5f
        beamEffectInterval = 3f
        beamEffectSize = 3.5f

        oscScl = 8f
        oscMag = 0.3f

        beamEffect = MultiEffect().apply {
          effects = arrayOf(
            ParticleEffect().apply {
              particles = 6
              lifetime = 26f
              length = 40f
              sizeFrom = 3.5f
              sizeTo = 0f
              interp = Interp.circleOut
              colorFrom = Color.valueOf("F0A16D")
              colorTo = Color.valueOf("F08B5E")
            },
            ParticleEffect().apply {
              line = true
              particles = 5
              lifetime = 25f
              length = 75f
              cone = 360f
              lenFrom = 8f
              lenTo = 0f
              colorFrom = Color.valueOf("F0A16D")
              colorTo = Color.valueOf("F08B5E")
            }
          )
        }
      }
    }).apply {
      consume?.apply {
        time(360f)
        power(12.5f)
        liquid(Liquids.slag, 0.5f)
      }
    }
  }
}
