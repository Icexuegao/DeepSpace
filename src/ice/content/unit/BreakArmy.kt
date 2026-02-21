package ice.content.unit

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Rect
import ice.entities.bullet.BombBulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Pal

class BreakArmy : IceUnitType("breakArmy") {
  init {
    armor = 14f
    speed = 0.7f
    health = 1200f
    hitSize = 30f
    rotateSpeed = 3.4f
    squareShape = true
    drawCell = false
    omniMovement = false
    rotateMoveFirst = true
    treadRects = arrayOf(Rect(16f - (128 / 2), 11f - (149 / 2), 33f, 127f))
    bundle {
      desc(zh_CN, "破军")
    }
    setWeapon("weapon") {
      x = 0f
      shootY += 8f
      reload = 50f
      recoil = 2.5f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      shootSound = Sounds.explosionDull
      shootCone = 2f
      bullet = BombBulletType(50f, 8 * 5f,"missile-large").apply {
        makeFire = true
        collidesTiles = true
        collides = true
        collidesAir = true
        collidesGround = true
        lifetime = 30f
        keepVelocity = false
        hitColor = Pal.lightOrange
        speed = 7f
        smokeEffect = Fx.shootSmokeTitan
        shootEffect = Effect(10f) { e ->
          Draw.color(Pal.lightOrange, Pal.lightishOrange, e.fin())
          val w = 1.3f + 10 * e.fout()
          Drawf.tri(e.x, e.y, w, 35f * e.fout(), e.rotation)
          Drawf.tri(e.x, e.y, w, 6f * e.fout(), e.rotation + 180f)
        }
        width = 10f
        height = 10f
        bulletInterval = 10f
        trailWidth = 2f
        trailColor = Color.valueOf("ffa763")
        trailLength = 8
        despawnEffect = Effect(30f) { e ->
          Draw.color(Pal.engine)
          e.scaled(25f) { f ->
            Lines.stroke(f.fout() * 2f)
            Lines.circle(e.x, e.y, 4f + f.finpow() * splashDamageRadius)
          }

          Lines.stroke(e.fout() * 2f)
          Angles.randLenVectors(e.id.toLong(), 24, e.finpow() * splashDamageRadius) { x: Float, y: Float ->
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1f)
          }
        }
        hitEffect = despawnEffect
        intervalBullet = BombBulletType(40f, 3f).apply {
          speed = 0f
          lifetime = 0f
        }
      }
    }
  }
}