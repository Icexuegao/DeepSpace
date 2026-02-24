package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.type.weapons.PointDefenseWeapon

class SwordSpear : IceUnitType("unit_swordSpear") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "剑戟", "中型空中突击单位.在远处发射激光攻击敌人,尾迹灼烧途经的敌军.配备小型裂解炮抵御敌人的攻击,并对附近的友军提供屠戮效果,开火时减少所受伤害")
    }
    lowAltitude = true
    flying = true
    health = 8600f
    hitSize = 40f
    armor = 11f
    speed = 1.8f
    drag = 0.03f
    range = 600f
    engineSize = 0f
    engineOffset = -20f
    rotateSpeed = 3f
    outlineColor = Color.valueOf("1F1F1F")

    engines.add(
      UnitEngine().apply {
        x = 6.5f
        y = -29f
        radius = 4f
        rotation = -90f
      },
      UnitEngine().apply {
        x = -6.5f
        y = -29f
        radius = 4f
        rotation = -90f
      }
    )

    parts.add(
      RegionPart().apply {
        progress = DrawPart.PartProgress.heat
        suffix = "-glow"
        outline = false
        color = Color.valueOf("F03B0E00")
        colorTo = Color.valueOf("F03B0E")
        blending = Blending.additive
      }
    )

    setWeapon {
      x = 0f
      y = -3f
      recoil = 0f
      shake = 6f
      shootY = 0f
      reload = 720f
      mirror = false
      shootCone = 0.05f
      cooldownTime = 760f
      shootSound = ISounds.灼烧
      bullet = LaserBulletType(2495f).apply {
        length = 600f
        width = 36f
        largeHit = true
        laserAbsorb = false
        status = IStatus.湍能
        statusDuration = 240f
        buildingDamageMultiplier = 0.1f
        shootEffect = MultiEffect(
          ParticleEffect().apply {
            particles = 17
            lifetime = 15f
            line = true
            lenFrom = 10f
            lenTo = 10f
            cone = 15f
            length = 100f
            baseLength = -15f
            colorFrom = Color.valueOf("8CA9E8")
            colorTo = Color.valueOf("D1EFFF")
          },
          WaveEffect().apply {
            lifetime = 25f
            sizeTo = 75f
            strokeFrom = 4f
            lightColor = Color.valueOf("8CA9E8")
            colorFrom = Color.valueOf("8CA9E8")
            colorTo = Color.valueOf("D1EFFF")
          }
        )
        colors = arrayOf(
          Color.valueOf("6569C9"),
          Color.valueOf("8CA9E8"),
          Color.valueOf("D1EFFF")
        )
        hitEffect = ParticleEffect().apply {
          particles = 15
          lifetime = 30f
          line = true
          lenFrom = 9f
          lenTo = 0f
          cone = 360f
          length = 65f
          baseLength = -15f
          colorFrom = Color.valueOf("D1EFFF")
          colorTo = Color.valueOf("8CA9E8")
        }
      }
    }

    setWeaponT<PointDefenseWeapon>("pointWeapon") {
      x = 9.5f
      y = -4f
      recoil = 0f
      reload = 6f
      color = Color.valueOf("8CA9E8")
      targetInterval = 1f
      targetSwitchInterval = 1f
      shootSound = Sounds.shootLaser
      bullet = BulletType().apply {
        damage = 115f
        maxRange = 200f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    }

    setWeapon {
      x = 0f
      y = -20f
      shootY = 0f
      reload = 300f
      mirror = false
      useAmmo = false
      baseRotation = 180f
      shootSound = Sounds.none
      alwaysShooting = true
      alwaysContinuous = true
      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("8CA9E88C"),
          Color.valueOf("8CA9E8B2"),
          Color.valueOf("8CA9E8CC"),
          Color.valueOf("D1EFFF"),
          Color.valueOf("FFFFFFCC")
        )
        damage = 50f
        lifetime = 60f
        length = 24f
        width = 1.2f
        recoil = 0f
        drawFlare = false
        status = StatusEffects.melting
        statusDuration = 60f
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 7
          lifetime = 15f
          length = 65f
          cone = -360f
          strokeFrom = 2.5f
          strokeTo = 0f
          lenFrom = 8f
          lenTo = 0f
          colorFrom = Color.valueOf("8CA9E8")
          colorTo = Color.valueOf("D1EFFF")
        }
      }
    }

    abilities.add(
      StatusFieldAbility(IStatus.屠戮, 245f, 367f, 160f).apply {
        activeEffect = Fx.none
      },
      ArmorPlateAbility().apply {
        healthMultiplier = 0.8f
      }
    )
  }
}