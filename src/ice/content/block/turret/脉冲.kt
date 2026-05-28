package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.type.Category
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.world.draw.DrawMulti

class 脉冲 :SglTurret("turret_pulse") {
  init {
    localization {
      zh_CN {
        localizedName = "脉冲"
        description = "发射强力脉冲能量攻击敌人，造成范围EMP伤害"
      }
      en {
        localizedName = "Pulse"
        description = "Fires powerful pulse energy to attack enemies, causing area EMP damage"
      }
    }

    health = 2560
    size = 4
    range = 240f
    cooldownTime = 1080f
    shake = 2f
    recoil = 0f
    shootY = 0f
    rotateSpeed = 0f
    shootCone = 360f
    canOverdrive = false
    liquidCapacity = 40f

    chargeSound = Sounds.chargeLancer
    shootSound = Sounds.shootLaser

    coolEffect = WaveEffect().apply {
      sides = 4
      lifetime = 60f
      sizeTo = 9f
      strokeFrom = 2f
      colorFrom = Color.valueOf("A9D8FF")
      colorTo = Color.valueOf("66B1FF")
    }

    shoot.apply {
      firstShotDelay = 40f
    }

    drawers = DrawMulti(DrawSglTurret().apply {
      parts.add(RegionPart())
      parts.add(RegionPart().apply {
        suffix = "-front"
        heatLight = true
        under = true
        moveY = 4.25f
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4.25f
        })
        children.add(RegionPart().apply {
          suffix = "-front-glow"
          heatProgress = DrawPart.PartProgress.charge
          drawRegion = false
          heatColor = Color.valueOf("5A58C4")
        })
      })
      parts.add(RegionPart().apply {
        suffix = "-back"
        heatLight = true
        under = true
        moveY = -4.25f
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = 4.25f
        })
        children.add(RegionPart().apply {
          suffix = "-back-glow"
          heatProgress = DrawPart.PartProgress.charge
          drawRegion = false
          heatColor = Color.valueOf("5A58C4")
        })
      })
    })

    requirements(
      Category.turret,
      IItems.铜锭, 200,
      IItems.铬锭, 280,
      IItems.钴钢, 240,
      IItems.导能回路, 100,
      IItems.强化合金, 125
    )

    setAmmo()
  }

  fun setAmmo() {
    newAmmo(object :BulletType() {
      init {
        damage = 0f
        lifetime = 1f
        speed = 0f

        shootEffect = ParticleEffect().apply {
          particles = 10
          lifetime = 30f
          length = 40f
          cone = 360f
          offset = 20f
          sizeFrom = 4f
          sizeTo = 0f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("A9D8FF")
          colorTo = Color.valueOf("66B1FF")
        }

        collides = false
        hittable = false
        absorbable = false
        reflectable = false
        pierceArmor = true

        splashDamage = 200f
        splashDamageRadius = 288f
        scaledSplashDamage = true
        splashDamagePierce = true

        status = IStatus.电磁脉冲
        statusDuration = 300f

        hitEffect = MultiEffect(
          ParticleEffect().apply {
            particles = 12
            lifetime = 36f
            length = 40f
            sizeFrom = 5f
            sizeTo = 0f
            cone = 360f
            interp = Interp.circleOut
            colorFrom = Color.valueOf("66B1FF")
            colorTo = Color.valueOf("A9D8FF")
          },
          ParticleEffect().apply {
            particles = 6
            lifetime = 15f
            length = 60f
            baseLength = 8f
            sizeFrom = 3f
            sizeTo = 0f
            cone = 360f
            colorFrom = Color.valueOf("66B1FF")
            colorTo = Color.valueOf("A9D8FF")
          },
          WaveEffect().apply {
            lifetime = 15f
            sizeFrom = 8f
            sizeTo = 50f
            strokeFrom = 2f
            strokeTo = 0f
            colorFrom = Color.valueOf("66B1FF")
            colorTo = Color.valueOf("A9D8FF")
          }
        )

        despawnEffect = MultiEffect(
          ParticleEffect().apply {
            particles = 1
            length = 0f
            lifetime = 15f
            sizeFrom = 288f
            sizeTo = 288f
            interp = Interp.circleOut
            colorFrom = Color.valueOf("66B1FF")
            colorTo = Color.valueOf("A9D8FF00")
          },
          WaveEffect().apply {
            lifetime = 60f
            sizeFrom = 288f
            sizeTo = 288f
            strokeFrom = 4f
            strokeTo = 0f
            colorFrom = Color.valueOf("66B1FF")
            colorTo = Color.valueOf("A9D8FF")
          }
        )
      }
    })

    consume!!.apply {
      power(40f)
    }
  }
}
