package ice.content.remains

import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Time
import ice.DeepSpace
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.game.EventType
import mindustry.type.ItemSeq
import mindustry.type.ItemStack
import universecore.math.pow2Intrp
import universecore.math.slope
import universecore.scene.style.DynamicTextureDrawable
import universecore.struct.AttachedProperty
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel
import universecore.world.particles.models.*
import kotlin.math.abs

class 胎生百合 :Remains("remains_tumor_bush") {
  companion object {
    var Particle.moveAxis: Int by AttachedProperty { 0 }
    var Particle.items: ItemSeq by AttachedProperty(::ItemSeq)
    var Particle.offsetX: Float by AttachedProperty { 0f }
    var Particle.offsetY: Float by AttachedProperty { 0f }
  }

  val placeBlock = Effect(60f) { e ->
    Draw.color(e.color)
    Draw.alpha(e.fin().slope)
    Lines.stroke(3f - e.fin() * 2f)
    val h = Vars.tilesize / 2f * e.rotation + e.fin() * 3f
    Lines.square(e.x, e.y, h)
    Lines.square(e.x, e.y, h + 2f, 45f)
    Draw.scl(e.rotation / 2f)
    Draw.rect("${DeepSpace.modName}-tumor_bush-flower", e.x, e.y, Time.time)
  }
  val effectColor: Color = Color.valueOf("ff4a4a")
  val particleMode = MultiParticleModel(SizeVelRelatedParticle(), object :ParticleModel() {
    override fun init(particle: Particle) {
      super.init(particle)
      val target = Vars.player.core() ?: return
      val dx = abs(target.x - particle.x)
      val dy = abs(target.y - particle.y)
      particle.moveAxis = if (dx >= dy) 1 else 2
      particle.offsetX = target.x
      particle.offsetY = target.y
    }

    override fun reset(particle: Particle) {
      super.reset(particle)
      particle.moveAxis = 0
      particle.items.clear()
      particle.offsetX = 0f
      particle.offsetY = 0f
    }

    override fun update(particle: Particle) {
      super.update(particle)

      val dist = Mathf.len(particle.offsetX - particle.x, particle.offsetY - particle.y)

      val speedFactor = Mathf.clamp(dist / 50f)
      particle.speed.setLength(particle.defSpeed * speedFactor.pow2Intrp)

      when(particle.moveAxis) {
        1 -> {
          val dx = particle.offsetX - particle.x
          if (abs(dx) > 2f) {
            particle.speed.setAngle(if (dx > 0) 0f else 180f)
          } else {
            particle.moveAxis = 2
            particle.offsetX = particle.x
          }
        }

        2 -> {
          val dy = particle.offsetY - particle.y
          if (abs(dy) > 2f) {
            particle.speed.setAngle(if (dy > 0) 90f else 270f)
          } else {
            particle.moveAxis = 1
          }
        }
      }
    }
// ... existing code ...

    override fun remove(particle: Particle) {
      super.remove(particle)
      particle.items.forEach { stack ->
        val accepted = Vars.player.core()?.acceptStack(stack.item, stack.amount, Vars.player.unit()) ?: return
        Vars.player.core()?.items?.add(stack.item, accepted)
      }
    }
  }, TrailFadeParticle().apply {
    trailFade = 0.08f
    fadeColor = effectColor
    colorLerpSpeed = 0.03f
  }, ShapeParticle(), DrawDefaultTrailParticle())

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

  init {
    Events.on(EventType.BlockDestroyEvent::class.java) {
      if (enabled) {
        val player = Vars.player ?: return@on
        val block = it.tile.block()
        val build = it.tile.build
        if (build.team != player.team()) return@on

        /*build.core()?.let { coreBuild ->
          val requirements = block.requirements
          if (requirements.isNotEmpty()) {
            for(stack in requirements) {
              val amount = (stack.amount / 10f).toInt()
              if (amount > 0) {
                val accepted = coreBuild.acceptStack(stack.item, amount, build)
                if (accepted > 0) {
                  coreBuild.items.add(stack.item, accepted)
                  placeBlock.at(build.x, build.y, build.block.size.toFloat(), effectColor)
                  repeat((block.size * 1.5f).toInt()) {
                    val len = Mathf.random(1f, 1.15f)
                    val offsetx = build.block.size * 4f * Mathf.random(-1f, 1f)
                    val offsety = build.block.size * 4f * Mathf.random(-1f, 1f)
                    val particle = particleMode.create(build.x + offsetx, build.y + offsety, effectColor, 0f, len, 1f)
                  }
                }
              }
            }
          }
        }*/

        val requirements = block.requirements
        if (requirements.isNotEmpty()) {
          val particleCount = (block.size * 1.5f).toInt()

          placeBlock.at(build.x, build.y, build.block.size.toFloat(), effectColor)

          val particles = Seq<Particle>()
          repeat(particleCount) {
            val len = Mathf.random(1f, 1.5f)
            val offsetx = build.block.size * 4f * Mathf.random(-1f, 1f)
            val offsety = build.block.size * 4f * Mathf.random(-1f, 1f)
            val particle = particleMode.create(build.x + offsetx, build.y + offsety, effectColor, 0f, len, 1f)
            particles.add(particle)
          }

          for(stack in requirements) {
            val totalAmount = (stack.amount / 10f).toInt()
            val perParticle = totalAmount / particleCount
            val remainder = totalAmount % particleCount

            for(i in 0 until particleCount) {
              var amount = perParticle
              if (i < remainder) amount++
              if (amount > 0) {
                particles[i].items.add(ItemStack(stack.item, amount))
              }
            }
          }
        }

      }
    }
  }
}