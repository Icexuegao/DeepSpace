package ice.entities

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.gl.FrameBuffer
import arc.math.Angles
import arc.math.Interp
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import ice.entities.bullet.ContinuousBulletType
import universecore.math.slope
import mindustry.Vars
import mindustry.entities.Damage
import mindustry.entities.Units
import mindustry.game.EventType
import mindustry.gen.Building
import mindustry.gen.Bullet
import mindustry.gen.Healthc
import mindustry.gen.Hitboxc
import mindustry.graphics.Layer
import mindustry.world.blocks.ControlBlock

open class ArcFieldBulletType :ContinuousBulletType() {
  @JvmField var angle: Float = 80f
  @JvmField var lengthInterp: Interp = Interp.slope
  @JvmField var fieldAlpha = 0.3f
  @JvmField var highlightTime = damageInterval * 5f
  @JvmField var pointField = true
  @JvmField var pointFieldEnabledWhenPlayer = true
  @JvmField var lightenIntensity = -1f

  init {
    optimalLifeFract = 0.5f
    removeAfterPierce = false
    pierceArmor = true
  }

  override fun init() {
    super.init()
    if (!pointField) pointFieldEnabledWhenPlayer = false
  }

  override fun init(b: Bullet) {
    super.init(b)
    b.fdata = highlightTime
    bullets.add(b)
  }
  override fun update(b: Bullet) {
    super.update(b)
    b.fdata += Time.delta
  }

  var hasCausedDamage = false
  override fun applyDamage(b: Bullet) = b.run {
    hasCausedDamage = false
    val curLen = currentLength(this)

    Units.nearbyEnemies(team, x, y, curLen) {
      tryHit(it)
    }
    if (collidesGround) {
      Units.nearbyBuildings(x, y, curLen) {
        if (it.team != this.team || collidesTeam) tryHit(it)
      }
    }
    if (hasCausedDamage) {
      fdata = 0f
    }
  }

  fun Bullet.tryHit(t: Healthc) {
    val ang = rotation()
    val angToTarget = angleTo(t)
    if (Angles.within(ang, angToTarget, angle / 2f)) Damage.collidePoint(this, team, hitEffect, t.x, t.y)
  }

  fun Bullet.hitTarget(target: Hitboxc) {
    target.collision(this, target.x, target.y)
    this.collision(target, target.x, target.y)
  }

  fun Bullet.hitTarget(target: Building) {
    target.collision(this)
    hit(this, target.x, target.y)
  }

  fun isControlledByPlayer(b: Bullet): Boolean {
    val turret = b.owner as? ControlBlock
    return turret?.isControlled ?: false
  }

  companion object {
    val buffer = FrameBuffer()
    val bullets = Seq<Bullet>()

    init {
      Events.run(EventType.Trigger.draw) {
        buffer.resize(Core.graphics.width, Core.graphics.height)
        drawRange {
          for(bullet in bullets) {
            if (!bullet.isAdded) {
              bullets.remove(bullet)
              continue
            }
            val apply = (bullet.type as ArcFieldBulletType)
            Draw.color(apply.hitColor)
            Draw.alpha(0.6f)
            Fill.arc(bullet.x, bullet.y, apply.calculateRange() * bullet.fin().slope, apply.angle / 360f, bullet.rotation() - apply.angle / 2f)
          }
        }

      }
    }

    fun drawRange(runnable: Runnable) {
      Draw.draw(Layer.bullet + 1) {
        buffer.begin(Color.clear)
        runnable.run()
        buffer.end()
        Tmp.tr1.set(Draw.wrap(buffer.texture))
        Tmp.tr1.flip(false, true)
        Draw.scl(4 / Vars.renderer.displayScale)



        Draw.rect(Tmp.tr1, Core.camera.position.x, Core.camera.position.y)

        Draw.reset()
      }
    /*  Draw.draw(Layer.bullet + 1) {
        buffer.begin(Color.clear)
        runnable.run()
        buffer.end()
        buffer.blit(Shaders.water)
      }*/
    }

    fun Color.lighten(strength: Float): Color {
      r *= 1f - strength
      g *= 1f - strength
      b *= 1f - strength
      r += strength
      g += strength
      b += strength
      return this
    }

    inline operator fun invoke(config: ArcFieldBulletType.() -> Unit) = ArcFieldBulletType().apply(config)
  }
}