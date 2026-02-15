package ice.content.unit

import arc.graphics.Blending
import arc.graphics.Color
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IStatus
import ice.entities.effect.MultiEffect
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.bullet.PointBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.gen.Sounds
import mindustry.type.UnitType

class DarkCarving:IceUnitType("darkCarving") {
  init {
    bundle {
      desc(zh_CN, "冥刻", "坚固的远程炮舰,可以对敌人进行远距离定点打击\n对抗单位时效果更佳")
    }
    accel = 0.04f
    drag = 0.04f
    flying = true
    health = 7300f
    armor = 12f
    hitSize = 30f
    speed = 1.2f
    rotateSpeed = 2.4f
    engineOffset = 13f
    engineSize = 4f
    lowAltitude = true
    ammoCapacity = 10
    outlineColor = "1F1F1F".toColor()
    engines.add(UnitType.UnitEngine(7.5f, -11f, 2f, -45f), UnitType.UnitEngine(-7.5f, -11f, 2f, -135f))
    abilities.add(StatusFieldAbility(IStatus.坚忍, 240f, 600f, 160f))
    parts.add(RegionPart().apply {
      suffix = "-glow"
      outline = false
      color = Color.valueOf("E6C4EE")
      blending = Blending.additive
    })
    parts.add(ShapePart().apply {
      progress = DrawPart.PartProgress.smoothReload
      y = 6f
      hollow = true
      circle = true
      stroke = 1.2f
      strokeTo = 0f
      radius = 7.5f
      color = Color.valueOf("E6C4EE")
      colorTo = Color.valueOf("AA88B2")
      layer = 110f
    })
    parts.add(ShapePart().apply {
      progress = DrawPart.PartProgress.smoothReload
      y = 6f
      sides = 4
      hollow = true
      stroke = 0.9f
      strokeTo = 0f
      radius = 3.6f
      rotateSpeed = -0.5f
      color = Color.valueOf("E6C4EE")
      colorTo = Color.valueOf("AA88B2")
      layer = 110f
    })
    parts.add(ShapePart().apply {
      progress = DrawPart.PartProgress.smoothReload
      y = 6f
      sides = 4
      hollow = true
      stroke = 0.9f
      strokeTo = 0f
      radius = 6.3f
      rotateSpeed = -0.5f
      color = Color.valueOf("E6C4EE")
      colorTo = Color.valueOf("AA88B2")
      layer = 110f
    })
    setWeapon("weapon") {
      x = 0f
      recoil = 0f
      shake = 1f
      shootY = 6f
      reload = 180f
      shootCone = 5f
      mirror = false
      heatColor = "E6C4EE".toColor()
      cooldownTime = 210f
      shootSound = ISounds.激射
      bullet = PointBulletType().apply {
        damage = 0f
        lifetime = 7.2f
        speed = 40f
        status = IStatus.秽蚀
        absorbable = false
        statusDuration = 300f
        splashDamage = 466f
        splashDamageRadius = 20f
        buildingDamageMultiplier = 0.4f
        smokeEffect = Fx.none
        hitSound = Sounds.explosion
        trailSpacing = 4f
        shootEffect = MultiEffect(ParticleEffect().apply {
          particles = 6
          lifetime = 30f
          line = true
          cone = 36f
          length = 15f
          baseLength = 3f
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeTo = 15f
          strokeFrom = 1f
          lightColor = Color.valueOf("E6C4EE")
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
        })
        despawnEffect = ParticleEffect().apply {
          line = true
          particles = 5
          lifetime = 30f
          lenFrom = 9f
          lenTo = 0f
          cone = 360f
          length = 32f
          baseLength = 3f
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
        }
        trailEffect = MultiEffect(ParticleEffect().apply {
          particles = 1
          lifetime = 30f
          line = true
          strokeFrom = 2.4f
          strokeTo = 0f
          lenFrom = 4.2f
          lenTo = 4.2f
          cone = 0f
          length = 1f
          baseLength = 1f
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
        }, ParticleEffect().apply {
          particles = 2
          lifetime = 25f
          sizeFrom = 1.2f
          length = 10f
          baseLength = 4.8f
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
          cone = 360f
        })
        hitEffect = MultiEffect(ParticleEffect().apply {
          particles = 8
          lifetime = 35f
          sizeFrom = 4f
          sizeTo = 0f
          cone = 360f
          length = 18f
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
        }, ParticleEffect().apply {
          particles = 8
          lifetime = 25f
          sizeFrom = 2f
          sizeTo = 0f
          cone = 360f
          length = 35f
          interp = Interp.pow5Out
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
        }, WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 1f
          sizeTo = 30f
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("E6C4EE")
          colorTo = Color.valueOf("AA88B2")
        })
      }
    }
  }
}