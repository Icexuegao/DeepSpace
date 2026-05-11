
package ice.content.remains

import arc.Events
import arc.graphics.Color
import arc.math.Mathf
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import mindustry.Vars
import mindustry.game.EventType
import universecore.scene.style.DynamicTextureDrawable
import universecore.struct.AttachedProperty
import universecore.world.particles.MultiParticleModel
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel
import universecore.world.particles.models.DrawDefaultTrailParticle
import universecore.world.particles.models.ShapeParticle
import universecore.world.particles.models.SizeVelRelatedParticle
import universecore.world.particles.models.TrailFadeParticle
import kotlin.math.abs

class 胎生百合 : Remains("remains_tumor_bush") {
  companion object {
    var Particle.moveAxis: Int by AttachedProperty(0)
  }

  val particleMode = MultiParticleModel(
    SizeVelRelatedParticle().apply {
      finalThreshold = 0.15f
    },
    TrailFadeParticle().apply {
      trailFade = 0.04f
      fadeColor = Color.valueOf("FFAAAA")
      colorLerpSpeed = 0.03f
    },
    ShapeParticle(),
    DrawDefaultTrailParticle(),
    object : ParticleModel() {
      override fun init(particle: Particle) {
        super.init(particle)
        val target = Vars.player.core()
        val dx = abs(target.x - particle.x)
        val dy = abs(target.y - particle.y)
        particle.moveAxis = if (dx >= dy) 1 else 2
      }

      override fun update(particle: Particle) {
        super.update(particle)
        val target = Vars.player.core()
        val dist = Mathf.len(target.x - particle.x, target.y - particle.y)

        if (dist < 3f) {
          particle.speed.scl(0.92f)
          return
        }

        when (particle.moveAxis) {
          1 -> {
            val dx = target.x - particle.x
            if (abs(dx) > 2f) {
              particle.speed.setAngle(if (dx > 0) 0f else 180f)
            } else {
              particle.moveAxis = 2
            }
          }
          2 -> {
            val dy = target.y - particle.y
            if (abs(dy) > 2f) {
              particle.speed.setAngle(if (dy > 0) 90f else 270f)
            } else {
              particle.moveAxis = 1
            }
          }
        }
      }
    }
  )

  init {
    Events.on(EventType.BlockDestroyEvent::class.java) {
      if (enabled) {
        val player = Vars.player ?: return@on
        val block = it.tile.block()
        val build = it.tile.build
        if (build.team != player.team()) return@on

        build.core()?.let { coreBuild ->
          val requirements = block.requirements
          if (requirements.isNotEmpty()) {
            for (stack in requirements) {
              val amount = (stack.amount / 10f).toInt()
              if (amount > 0) {
                val angle = Mathf.random(360f)
                val len = Mathf.random(1f, 3f)
                val vx = Mathf.cos(angle) * len
                val vy = Mathf.sin(angle) * len
                particleMode.create(build.x, build.y, IceColor.r2, vx, vy, 8f)
                val accepted = coreBuild.acceptStack(stack.item, amount, build)
                if (accepted > 0) {
                  coreBuild.items.add(stack.item, accepted)
                }
              }
            }
          }
        }
      }
    }
  }

  init {
    localization {
      zh_CN {
        localizedName = "胎生百合"
        effect = "当方块被破坏时,将方块的10%的资源返还核心"
      }
    }
    remainsColor = IceColor.r2
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 8
      it.frameDuration = 60f / 3f
    }
  }
}