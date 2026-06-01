package ice.content.remains

import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.util.Time
import ice.DeepSpace
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.game.EventType
import mindustry.graphics.Pal
import universecore.math.slope
import universecore.scene.style.DynamicTextureDrawable
import universecore.world.particles.models.*

class 胎生百合 :Remains("remains_tumor_bush") {

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
val particleMode = MultiParticleModel(
    SizeVelRelatedParticle(), RandDeflectParticle().apply {
      deflectAngle = 0f
      strength = 0.125f
    }, TrailFadeParticle().apply {
      trailFade = 0.04f
      fadeColor = Pal.lightishGray
      colorLerpSpeed = 0.03f
    }, ShapeParticle(), DrawDefaultTrailParticle()
  )
  init {
    localization {
      zh_CN {
        localizedName = "胎生百合"
        description = "柔软的花瓣包裹着废墟的秘密,在寂静中轻轻摇曳"
        effect = "当方块被破坏时,将方块的[10%]的资源返还核心"
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
      if (!enabled) return@on
      val player = Vars.player ?: return@on
      val block = it.tile.block()
      val build = it.tile.build
      if (build.team != player.team()) return@on

      val requirements = block.requirements

      requirements.forEach {stack ->
        player.core()?.acceptStack(stack.item, stack.amount/10, build) ?.let { it1 ->
          Vars.player.core()?.handleStack(stack.item, it1, build)
        }
      }


      if (requirements.isNotEmpty()) {
        val particleCount = (block.size * 1.5f).toInt()

        placeBlock.at(build.x, build.y, build.block.size.toFloat(), effectColor)

        repeat(particleCount) {
          val len = Mathf.random(2f, 3f)
          val offsetx = build.block.size * 4f * Mathf.random(-1f, 1f)
          val offsety = build.block.size * 4f * Mathf.random(-1f, 1f)
          /*val particle = particleMode.create(build.x + offsetx, build.y + offsety, effectColor, 0f, len, 1f)
          particle.dest = Vec2(particle.x+offsetx, particle.y+offsety)
          particle.eff =1f*/

          Angles.randLenVectors(
            System.nanoTime(), 1, 2f, 3.5f
          ) { x: Float, y: Float ->
            particleMode.create(build.x,build.y,effectColor , x, y, Mathf.random(3.25f, 4f))
          }

        }
      }

    }
  }
}