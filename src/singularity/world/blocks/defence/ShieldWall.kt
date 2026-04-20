package singularity.world.blocks.defence

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.struct.texture.LazyTextureSingleDelegate
import ice.world.content.blocks.defense.Wall
import mindustry.Vars
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.world.meta.Stat
import kotlin.math.max
import kotlin.math.min

class ShieldWall(name: String) :Wall(name) {
  var shieldHealth: Float = 900f
  var breakCooldown: Float = 60f * 10f
  var regenSpeed: Float = 2f

  var glowColor: Color = Color.valueOf("ff7531").a(0.5f)
  var glowMag: Float = 0.6f
  var glowScl: Float = 8f

  var glowRegion: TextureRegion by LazyTextureSingleDelegate(this.name + "-glow")

  init {
    update = true
    buildType = Prov(::ShieldWallBuild)
  }

  override fun setStats() {
    super.setStats()

    stats.add(Stat.shieldHealth, shieldHealth)
  }

  inner class ShieldWallBuild :WallBuild() {
    var shield: Float = shieldHealth
    var shieldRadius: Float = 0f
    var breakTimer: Float = 0f

    override fun draw() {
      Draw.rect(block.region, x, y)

      if (shieldRadius > 0) {
        val radius = shieldRadius * Vars.tilesize * size / 2f

        Draw.z(Layer.shields)

        Draw.color(team.color, Color.white, Mathf.clamp(hit))

        if (Vars.renderer.animateShields) {
          Fill.square(x, y, radius)
        } else {
          Lines.stroke(1.5f)
          Draw.alpha(0.09f + Mathf.clamp(0.08f * hit))
          Fill.square(x, y, radius)
          Draw.alpha(1f)
          Lines.poly(x, y, 4, radius, 45f)
          Draw.reset()
        }

        Draw.reset()

        Drawf.additive(glowRegion, glowColor, (1f - glowMag + Mathf.absin(glowScl, glowMag)) * shieldRadius, x, y, 0f, Layer.blockAdditive)
      }
    }

    override fun updateTile() {
      if (breakTimer > 0) {
        breakTimer -= Time.delta
      } else {
        //regen when not broken
        shield = Mathf.clamp(shield + regenSpeed * edelta(), 0f, shieldHealth)
      }

      if (hit > 0) {
        hit -= Time.delta / 10f
        hit = max(hit, 0f)
      }

      shieldRadius = Mathf.lerpDelta(shieldRadius, if (broken()) 0f else 1f, 0.12f)
    }

    fun broken(): Boolean {
      return breakTimer > 0 || !canConsume()
    }

    override fun pickedUp() {
      shieldRadius = 0f
    }

    override fun damage(damage: Float) {
      val shieldTaken = if (broken()) 0f else min(shield, damage)

      shield -= shieldTaken
      if (shieldTaken > 0) {
        hit = 1f
      }

      //shield was destroyed, needs to go down
      if (shield <= 0.00001f && shieldTaken > 0) {
        breakTimer = breakCooldown
      }

      if (damage - shieldTaken > 0) {
        super.damage(damage - shieldTaken)
      }
    }

    override fun write(write: Writes) {
      super.write(write)
      write.f(shield)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      shield = read.f()
      if (shield > 0) shieldRadius = 1f
    }
  }
}
