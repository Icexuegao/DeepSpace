package ice.content.block.effect

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.scene.ui.layout.Table
import arc.struct.FloatSeq
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.audio.ISounds
import ice.content.IItems
import ice.graphics.IceColor
import ice.library.scene.ui.updateE
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.gen.Icon
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.ui.Styles
import mindustry.world.Tile
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.SglBlock
import kotlin.math.absoluteValue
import kotlin.math.max

class ProspectingRadar : SglBlock("prospectingRadar") {
  var range = 50
  var baseSpeed = 0.5f

  init {
    BaseBundle.bundle {
      desc(zh_CN, "勘探雷达")
    }
    update = true
    configurable = true
    size = 3
    buildType = Prov(::ProspectingRadarBuild)
    draw = DrawMulti(DrawDefault(), DrawRegion("-radar", 3f, true))

    newConsume().apply {
      power(3.0f)
    }
    requirements(Category.effect, IItems.铬锭, 60, IItems.铪锭, 30, IItems.黄铜锭, 20, IItems.单晶硅, 10)
  }

  override fun init() {
    super.init()
    clipSize = range * 2 * Vars.tilesize.toFloat()
  }

  override fun setStats() {
    super.setStats()
    consumers.forEach {
      it.display(stats)
    }
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    Draw.alpha(1f)
    Drawf.circles((x * Vars.tilesize).toFloat(), (y * Vars.tilesize).toFloat(), (range * Vars.tilesize).toFloat(), IceColor.b4)
  }

  inner class ProspectingRadarBuild : SglBuilding() {
    val polyFloats = FloatSeq()
    val ores = Seq<OresData>()

    var warmup: Float = 0f
    var totalProgress: Float = 0f
    var speed: Float
      set(_) {}
      get() = baseSpeed * consEfficiency()
    var shown = true

    override fun buildConfiguration(table: Table) {
      table.button(Icon.eyeSmall, Styles.cleari) {
        shown = !shown
      }.get().updateE {
        it.style.imageUp = if (shown) Icon.eyeSmall else Icon.eyeOffSmall
      }
    }

    override fun write(write: Writes) {
      super.write(write)
      write.f(warmup)
      write.f(totalProgress)
      write.bool(shown)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      warmup = read.f()
      totalProgress = read.f()
      shown = read.bool()
    }

    inner class OresData(val tile: Tile, val ore: OreBlock) {
      val genTime = 2 * 60f
      var time = genTime
      fun update() {
        time -= Time.delta
        if (time < 0f) ores.remove(this)
      }

      fun draw() {
        if (time < 0f) return
        Draw.z(Layer.block + 0.51f)

        Draw.alpha(time / genTime)
        Draw.rect(ore.variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0, max(0, ore.variantRegions.size - 1))], tile.worldx(), tile.worldy())
      }
    }

    override fun totalProgress() = totalProgress
    fun updateFactory() {

      warmup = if (shouldConsume() && consumeValid()) {
        (Mathf.lerpDelta(warmup, 1f, 0.02f))
      } else {
        (Mathf.lerpDelta(warmup, 0f, 0.02f))
      }

      totalProgress += warmup
    }

    override fun updateTile() {
      super.updateTile()
      updateFactory()
      ores.forEach { it.update() }
      if (!(shouldConsume() && consumeValid())) return
      atSound()
      for (i in 1..range) {
        val rotate2: Vec2 = Tmp.v2.set(0f, i.toFloat()).rotate(-Time.time * speed - 90f)
        val tile1: Tile? = Vars.world.tile(tileX() + rotate2.x.toInt(), tileY() + rotate2.y.toInt())
        tile1?.overlay()?.let {
          if (it is OreBlock && tile1.block() == Blocks.air) {
            ores.add(OresData(tile1, it))
          }
        }
      }
    }

    override fun remove() {
      ISounds.radar.stop()
      super.remove()
    }

    override fun draw() {
      super.draw()
      ores.forEach { it.draw() }
      if (shown) drawRadar()
    }

    var lastRad = 0f
    fun atSound() {
      val maxRad = range * Vars.tilesize
      val rad: Float = Time.time % maxRad
      if (lastRad < maxRad * 0.5f && rad >= maxRad * 0.5f) {
        ISounds.radar.at(this)
      } else if (lastRad > maxRad * 0.9f && rad < maxRad * 0.1f) {
        ISounds.radar.at(this)
      }
      lastRad = rad
    }

    fun drawRadar() {
      if (!(shouldConsume() && consumeValid())) return
      Draw.color(IceColor.b4)
      val drawX = x
      val drawY = y
      Draw.alpha(0.1f)
      Fill.circle(drawX, drawY, (range * Vars.tilesize).toFloat())
      Draw.alpha(1f)
      Drawf.circles(drawX, drawY, (range * Vars.tilesize).toFloat(), IceColor.b4)

      Lines.stroke(5f, IceColor.b4)

      val rad: Float = Time.time % (range * Vars.tilesize).toFloat()
      Draw.alpha(0.4f * Interp.pow2In.apply(1 - rad / (range * Vars.tilesize)))
      Lines.circle(drawX, drawY, rad)

      Tmp.v1.set(0f, (Vars.tilesize * range).toFloat()).rotate(-Time.time * speed - 90f)
      val dx = Tmp.v1.x
      val dy = Tmp.v1.y
      Lines.stroke(1f, IceColor.b4)
      Lines.line(drawX, drawY, drawX + dx, drawY + dy)


      Draw.color()
      test(x, y, (Vars.tilesize * range).toFloat(), 0.07f, -Time.time * speed, IceColor.b4)

      Drawf.square(x, y, 1f, -Time.time * speed - 90f, IceColor.b4)
      Draw.reset()
    }

    override fun drawSelect() {
      super.drawSelect()
      if (!shown) drawRadar()
    }

    fun test(x: Float, y: Float, radius: Float, fraction: Float, rotation: Float, color1: Color, alphaTo: Float = 0f, sides: Int = 150) {
      val max = Mathf.ceil(sides * fraction.absoluteValue)
      polyFloats.clear()
      val centerColor = Tmp.c2.set(color1).a(alphaTo).toFloatBits()
      polyFloats.add(x, y, centerColor)

      Tmp.c1.set(color1)
      for (i in 0..max) {
        val f = i.toFloat() / max
        val a = fraction * f * 360f + rotation
        val x1 = Angles.trnsx(a, radius)
        val y1 = Angles.trnsy(a, radius)
        Tmp.c1.set(color1).a(Mathf.lerp(1f, alphaTo, f))

        polyFloats.add(x + x1, y + y1, Tmp.c1.toFloatBits())
      }
      polyFloats.add(x, y, centerColor)

      val items = polyFloats.items
      val size = polyFloats.size

      for (i in 3 until size - 6 step 6) Fill.quad(
        items[0], items[1], items[2], items[i], items[i + 1], items[i + 2], items[i + 3], items[i + 4], items[i + 5], items[i + 6], items[i + 7], items[i + 8]
      )
    }
  }
}