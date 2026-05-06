package universecore.world.draw.part

import arc.Core
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Nullable
import arc.util.Tmp
import mindustry.entities.part.DrawPart
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import singularity.world.blocks.turrets.SglTurret.SglTurretBuild
import kotlin.math.min

/** 基于纹理区域的绘制部件，支持热效、光影、子部件及镜像绘制。
 * 拥有 liquid top 部件
 * @author Alon */
open class UncRegionPart() : DrawPart() {
  protected var childParam = PartParams()

  /** Appended to unit/weapon/block name and drawn. */
  var suffix = ""
  /** Overrides suffix if set. */
  @Nullable var name: String? = null
  lateinit var heat: TextureRegion
  lateinit var light: TextureRegion
  lateinit var liquid: TextureRegion
  lateinit var top: TextureRegion

  var regions = arrayOf<TextureRegion>()
  var outlines = arrayOf<TextureRegion>()

  /** If true, parts are mirrored across the turret. Requires -1 and -2 regions. */
  var mirror = false
  /** If true, an outline is drawn under the part. */
  var outline = true
  /** If true, the base + outline regions are drawn. Set to false for heat-only regions. */
  var drawRegion = true
  /** If true, the heat region produces light. */
  var heatLight = false
  /** Whether to clamp progress to (0-1). If false, allows usage of interps that go past the range, but may have unwanted visual bugs depending on values. */
  var clampProgress = true
  /** Progress function for determining position/rotation. */
  var progress: PartProgress = PartProgress.warmup
  /** Progress function for scaling. */
  var growProgress: PartProgress = PartProgress.warmup
  /** Progress function for heat alpha. */
  var heatProgress: PartProgress = PartProgress.heat
  var blending: Blending = Blending.normal
  var layer = -1f
  var layerOffset: Float = 0f
  var heatLayerOffset: Float = 1f
  var turretHeatLayer: Float = Layer.turretHeat
  var outlineLayerOffset = -0.001f

  /** note that origin DOES NOT AFFECT child parts */
  var x = 0f
  /** note that origin DOES NOT AFFECT child parts */
  var y = 0f
  /** note that origin DOES NOT AFFECT child parts */
  var xScl = 1f
  /** note that origin DOES NOT AFFECT child parts */
  var yScl = 1f
  /** note that origin DOES NOT AFFECT child parts */
  var rotation = 0f
  /** note that origin DOES NOT AFFECT child parts */
  var originX = 0f

  var originY = 0f
  var moveX = 0f
  var moveY = 0f
  var growX = 0f
  var growY = 0f
  var moveRot = 0f
  var heatLightOpacity = 0.3f

  var color: Color? = null
  var colorTo: Color? = null
  var mixColor: Color? = null
  var mixColorTo: Color? = null
  var heatColor: Color = Pal.turretHeat.cpy()
  var children = Seq<DrawPart>()
  var moves = Seq<PartMove>()

  constructor(region: String) :this() {
    this.suffix = region
  }

  constructor(region: String, blending: Blending, color: Color) : this(region) {
    this.blending = blending
    this.color = color
    outline = false
  }

  fun draw(params: PartParams, turret: SglTurretBuild) {
    val z = Draw.z()
    if (layer > 0) Draw.z(layer)
    //TODO 'under' should not be special cased like this...
    if (under && turretShading) Draw.z(z - 0.0001f)
    Draw.z(Draw.z() + layerOffset)

    val prevZ = Draw.z()
    val prog = progress.getClamp(params, clampProgress)
    val sclProg = growProgress.getClamp(params, clampProgress)
    var mx = moveX * prog
    var my = moveY * prog
    var mr = moveRot * prog + rotation
    var gx = growX * sclProg
    var gy = growY * sclProg

    if (!moves.isEmpty) {
      for (move in moves) {
        val p = move.progress.getClamp(params, clampProgress)
        mx += move.x * p
        my += move.y * p
        mr += move.rot * p
        gx += move.gx * p
        gy += move.gy * p
      }
    }

    val len = if (mirror && params.sideOverride == -1) 2 else 1
    val preXscl = Draw.xscl
    val preYscl = Draw.yscl
    Draw.xscl *= xScl + gx
    Draw.yscl *= yScl + gy

    for (s in 0..<len) {
      //use specific side if necessary
      val i = if (params.sideOverride == -1) s else params.sideOverride

      //can be null
      val region = if (drawRegion && regions.isNotEmpty()) regions[min(i, regions.size - 1)] else null
      val sign = (if (i == 0) 1 else -1) * params.sideMultiplier
      Tmp.v1.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad)

      Draw.xscl *= sign

      if (originX != 0f || originY != 0f) {
        //correct for offset caused by origin shift
        Tmp.v1.sub(
          Tmp.v2.set(-originX * Draw.xscl, -originY * Draw.yscl).rotate(params.rotation - 90f).add(originX * Draw.xscl, originY * Draw.yscl)
        )
      }

      val rx = params.x + Tmp.v1.x
      val ry = params.y + Tmp.v1.y
      val rot = mr * sign + params.rotation - 90

      if (outline && drawRegion) {
        Draw.z(prevZ + outlineLayerOffset)
        rect(outlines[min(i, regions.size - 1)], rx, ry, rot)
        Draw.z(prevZ)
      }

      if (drawRegion && region != null && region.found()) {
        if (color != null && colorTo != null) {
          Draw.color(color, colorTo, prog)
        } else if (color != null) {
          Draw.color(color)
        }

        if (mixColor != null && mixColorTo != null) {
          Draw.mixcol(mixColor, mixColorTo, prog)
        } else if (mixColor != null) {
          Draw.mixcol(mixColor, mixColor!!.a)
        }

        Draw.blend(blending)
        rect(region, rx, ry, rot)
        Draw.blend()
        if (color != null) Draw.color()
      }

      if (heat.found()) {
        val hprog = heatProgress.getClamp(params, clampProgress)
        heatColor.write(Tmp.c1).a(hprog * heatColor.a)
        Drawf.additive(heat, Tmp.c1, 1f, rx, ry, rot, if (turretShading) turretHeatLayer else Draw.z() + heatLayerOffset, originX, originY)
        if (heatLight) Drawf.light(rx, ry, if (light.found()) light else heat, rot, Tmp.c1, heatLightOpacity * hprog)
      }

      if (liquid.found()) {
        turret.liquids?.current()?.let {
          Draw.color(it.color)
          rect(liquid, rx, ry, rot)
        }
      }

      if (top.found()) {
        Draw.color()
        rect(top, rx, ry, rot)
      }

      Draw.xscl *= sign
    }

    Draw.color()
    Draw.mixcol()

    Draw.z(z)

    //draw child, if applicable - only at the end
    //TODO lots of copy-paste here
    if (!children.isEmpty) {
      for (s in 0..<len) {
        val i = if (params.sideOverride == -1) s else params.sideOverride
        val sign = (if (i == 1) -1 else 1) * params.sideMultiplier
        Tmp.v1.set((x + mx) * sign, y + my).rotateRadExact((params.rotation - 90) * Mathf.degRad)

        childParam.set(
          params.warmup,
          params.reload,
          params.smoothReload,
          params.heat,
          params.recoil,
          params.charge,
          params.x + Tmp.v1.x,
          params.y + Tmp.v1.y,
          mr * sign + params.rotation
        )
        childParam.sideMultiplier = params.sideMultiplier
        childParam.life = params.life
        childParam.sideOverride = i
        for (child in children) {
          child.draw(childParam)
        }
      }
    }

    Draw.scl(preXscl, preYscl)
  }

  fun rect(region: TextureRegion, x: Float, y: Float, rotation: Float) {
    val w = region.width * region.scl() * Draw.xscl
    val h = region.height * region.scl() * Draw.yscl
    Draw.rect(region, x, y, w, h, w / 2f + originX * Draw.xscl, h / 2f + originY * Draw.yscl, rotation)
  }

  override fun draw(params: PartParams) = Unit

  override fun load(name: String?) {
    val realName = if (this.name == null) name + suffix else this.name

    if (drawRegion) {
      if (mirror && turretShading) {
        regions = arrayOf(Core.atlas.find("$realName-r"), Core.atlas.find("$realName-l"))

        outlines = arrayOf(Core.atlas.find("$realName-r-outline"), Core.atlas.find("$realName-l-outline"))
      } else {
        regions = arrayOf(Core.atlas.find(realName))
        outlines = arrayOf(Core.atlas.find("$realName-outline"))
      }
    }

    heat = Core.atlas.find("$realName-heat")
    light = Core.atlas.find("$realName-light")
    liquid = Core.atlas.find("$realName-liquid")
    top = Core.atlas.find("$realName-top")
    for (child in children) {
      child.turretShading = turretShading
      child.load(name)
    }
  }

  override fun getOutlines(out: Seq<TextureRegion>) {
    if (outline && drawRegion) {
      out.addAll(*regions)
    }
    for (child in children) {
      child.getOutlines(out)
    }
  }
}
