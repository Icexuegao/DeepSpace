package ice.content.unit

import arc.Events
import arc.graphics.Color
import arc.math.Interp
import arc.math.geom.Rect
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.world.content.unit.IceUnitType
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.game.EventType
import mindustry.gen.Bullet
import mindustry.graphics.Drawf
import singularity.core.UpdatePool
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.generator.VectorLightningGenerator

class ClearingGround : IceUnitType("unit_clearingGround") {
  init {
    localization {
      zh_CN {
        name = "涤罪"
        description = "涤罪是神殿[净罪计划]的产物"
      }
    }
    speed = 0.44f
    armor = 13f
    health = 21000f
    hitSize = 75f
    crushDamage = 25f / 5f
    outlines = false
    drawCell = false
    rotateSpeed = 0.8f
    treadPullOffset = 1
    squareShape = true
    omniMovement = false
    rotateMoveFirst = true
    outlineColor = Color.valueOf("24222B")
    treadRects = arrayOf(Rect(70f - (400 / 2), 53f - (500 / 2), 83f, 394f))

    setWeapon("propelledgun") {
      x = 0f
      y = -6f
      mirror = false
      shootY = 24f
      rotate = true
      rotateSpeed = 1f
      reload = 1 * 60f
      shake += 2f
      recoil += 7f

      bullet = BasicBulletType(16f, 840f, "gauss-bullet").apply {
        lifetime = 48f
        shrinkY = 0f
        height = 32f
        width = 26f
        ammoMultiplier = 1f
        frontColor = IceColor.b4
        backColor = IceColor.b5
        hittable = false
        pierceCap = 2
        status = StatusEffects.melting
        statusDuration = 180f
        splashDamage = 240f
        splashDamageRadius = 80f
        buildingDamageMultiplier = 0.5f
        trailColor = IceColor.b4
        trailLength = 24
        trailWidth = 3f
        trailSinScl = 0.75f
        trailSinMag = 1.5f
        hitShake = 3f
        despawnShake = 4f
        knockback = 5f
        lightningColor = IceColor.b4
        hitEffect = MultiEffect(WaveEffect().apply {
          lifetime = 20f
          sizeFrom = 0f
          sizeTo = 65f
          strokeFrom = 4f
          strokeTo = 0f
          lightColor = IceColor.b4
          colorFrom = IceColor.b4
          colorTo = IceColor.b5
        }, ParticleEffect().apply {
          line = true
          particles = 11
          lifetime = 30f
          length = 85f
          baseLength = 20f
          cone = -360f
          lenFrom = 7f
          lenTo = 0f
          interp = Interp.exp10In
          colorFrom = IceColor.b4
          colorTo = IceColor.b5
        })

        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 1
          sizeFrom = 45f
          sizeTo = 0f
          length = 0f
          interp = Interp.bounceOut
          lifetime = 60f
          region = "star".appendModName()
          lightColor = IceColor.b4
          colorFrom = IceColor.b4
          colorTo = IceColor.b5
          layer = 110f
        }, Effect(60f) {
          Drawf.light(it.x, it.y, it.fin() * splashDamageRadius, IceColor.b4, it.fout())
        }, WaveEffect().apply {
          lifetime = 60f
          sizeFrom = 0f
          sizeTo = splashDamageRadius
          strokeFrom = 4f
          strokeTo = 0f
          interp = Interp.elasticOut
          lightColor = IceColor.b4
          colorFrom = IceColor.b4
          colorTo = IceColor.b5
        }, ParticleEffect().apply {
          line = true
          particles = 11
          lifetime = 30f
          length = 85f
          baseLength = 20f
          cone = -360f
          lenFrom = 7f
          lenTo = 0f
          interp = Interp.exp10In
          colorFrom = IceColor.b4
          colorTo = IceColor.b5
        })

        shootEffect = MultiEffect(ParticleEffect().apply {
          particles = 4
          sizeFrom = 6f
          sizeTo = 0f
          length = 70f
          lifetime = 30f
          interp = Interp.sineOut
          sizeInterp = Interp.sineIn
          colorFrom = IceColor.b4
          colorTo = IceColor.b5
          cone = 15f
        }, WaveEffect().apply {
          lifetime = 30f
          sides = 0
          sizeFrom = 0f
          sizeTo = 40f
          strokeFrom = 4f
          strokeTo = 0f
          colorFrom = IceColor.b4
          colorTo = IceColor.b5
        })
        var con = LightningContainer()
        val vertex = VectorLightningGenerator()
        vertex.vector.set(21f, 22f)
        UpdatePool.receive("con") {

        }
        Events.run(EventType.Trigger.preDraw) {
          con.draw(1f, 2f)
        }

        intervalBullet = object : BulletType(0f, 0f) {
          override fun init(b: Bullet) {
            super.init(b)
            con.create(vertex)
          }
        }
        bulletInterval = 1f
      }
    }
  }

}