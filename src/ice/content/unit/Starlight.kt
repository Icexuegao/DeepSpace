package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import ice.entities.bullet.ExplosionBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.FlarePart
import mindustry.gen.Sounds
import mindustry.type.Weapon
import mindustry.type.unit.MissileUnitType

class Starlight : MissileUnitType("starlight") {
  init {
    bundle {
      desc(zh_CN, "星光")
    }
    health = 130f
    hitSize = 4f
    speed = 7f
    lifetime = 185f
    rotateSpeed = 3.6f
    engineColor = Color.valueOf("FEB380")
    trailColor = Color.valueOf("FEB380")
    engineLayer = 110f
    engineSize = 2f
    trailLength = 12
    lowAltitude = false
    missileAccelTime = 45f
    weapons.add(Weapon().apply {
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.none
      bullet = ExplosionBulletType(24f, 72f).apply {
        hitEffect = MultiEffect(WaveEffect().apply {
          lifetime = 15f
          sizeFrom = 0f
          sizeTo = 30f
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("FEB380")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          particles = 3
          lifetime = 20f
          line = true
          strokeFrom = 1.5f
          strokeTo = 1.5f
          lenFrom = 5f
          lenTo = 5f
          cone = 360f
          length = 50f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FEB380")
          colorTo = Color.valueOf("FF8663")
        }, ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          line = true
          strokeFrom = 1.5f
          strokeTo = 1.5f
          lenFrom = 7f
          lenTo = 7f
          cone = 360f
          length = 30f
          interp = Interp.circleOut
          sizeInterp = Interp.pow5In
          colorFrom = Color.valueOf("FEB380")
          colorTo = Color.valueOf("FF8663")
        })
      }
    })
    parts.add(FlarePart().apply {
      color1 = Color.valueOf("FEB380")
      stroke = 4.5f
      radius = 0f
      radiusTo = 30f
      progress = DrawPart.PartProgress.life.curve(Interp.pow3Out)
    })
  }
}