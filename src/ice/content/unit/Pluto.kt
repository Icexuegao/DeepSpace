package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.MoveEffectAbility
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds

class Pluto : IceUnitType("unit_pluto") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "冥")
    }
    circleTarget = true
    lowAltitude = true
    flying = true
    health = 21600f
    hitSize = 54f
    armor = 16f
    speed = 2.8f
    drag = 0.03f
    rotateSpeed = 8f
    engineSize = 0f
    range = 480f
    outlineColor = Color.valueOf("1F1F1F")
    abilities.add(RegenAbility().apply {
      percentAmount = 0.025f
    }, StatusFieldAbility(IStatus.屠戮, 90f, 60f, 160f).apply {
      applyEffect = Fx.none
      activeEffect = Fx.none
    }, ArmorPlateAbility().apply {
      healthMultiplier = 0.8f
    }, MoveEffectAbility().apply {
      rotateEffect = true
      minVelocity = 0f
      interval = 2f
      display = false
      effect = ParticleEffect().apply {
        region = "unit_pluto-full".appendModName()
        particles = 1
        lifetime = 15f
        sizeFrom = 40f
        sizeTo = 32f
        length = 0f
        offset = -90f
        layer = 89.9f
        layerDuration = 0.1f
        lightOpacity = 0f
        colorFrom = Color.white
        colorTo = Color.valueOf("FFFFFF00")
      }
    })

    setWeapon {
      x = 0f
      y = 11.5f
      reload = 300f
      mirror = false
      shootY = 0f
      shootCone = 360f
      baseRotation = 180f
      cooldownTime = 145f
      shootSound = Sounds.none
      loopSoundVolume = 1f
      loopSound = Sounds.beamLustre
      alwaysContinuous = true
      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"), Color.valueOf("FF5845B2"), Color.valueOf("FF5845CC"), Color.valueOf("FF6666"), Color.valueOf("FFDCD8CC")
        )
        damage = 100f
        lifetime = 60f
        length = 360f
        width = 7f
        recoil = -0.25f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f
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
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF6666")
        }
      }
    }

    setWeapon {
      x = 0f
      y = 36f
      shootY = 0f
      reload = 300f
      mirror = false
      shootCone = 360f
      cooldownTime = 145f
      shootSound = Sounds.shootConquer
      bullet = BulletType().apply {
        damage = 0f
        lifetime = 1f
        speed = 20f
        recoil = 24f
        status = IStatus.蚀骨
        statusDuration = 150f
        splashDamage = 910f
        splashDamageRadius = 40f
        shootEffect = MultiEffect(ParticleEffect().apply {
          followParent = false
          line = true
          particles = 11
          lifetime = 45f
          lenFrom = 14f
          lenTo = 8f
          strokeFrom = 4f
          strokeTo = 0f
          cone = 25f
          length = 320f
          interp = Interp.pow2Out
          sizeInterp = Interp.pow2In
          colorTo = Color.valueOf("FF5845")
        }, ParticleEffect().apply {
          followParent = false
          particles = 17
          lifetime = 90f
          sizeFrom = 7f
          sizeTo = 0f
          cone = 25f
          length = 350f
          interp = Interp.pow5Out
          sizeInterp = Interp.linear
          colorFrom = Color.valueOf("FF5845BB")
          colorTo = Color.valueOf("FF5845BB")
        }, ParticleEffect().apply {
          followParent = false
          particles = 13
          lifetime = 140f
          sizeFrom = 14f
          sizeTo = 0f
          cone = 25f
          length = 200f
          interp = Interp.pow5Out
          sizeInterp = Interp.linear
          colorFrom = Color.valueOf("FF5845bb")
          colorTo = Color.valueOf("FF5845bb")
        }, WaveEffect().apply {
          followParent = false
          lifetime = 90f
          sizeFrom = 0f
          sizeTo = 120f
          strokeFrom = 8f
          strokeTo = 0f
          interp = Interp.pow3Out
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF5845")
        })
      }
    }
  }
}