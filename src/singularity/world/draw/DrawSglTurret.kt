package singularity.world.draw

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Rand
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Nullable
import arc.util.Tmp
import ice.library.IFiles.appendModName
import mindustry.entities.part.DrawPart
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import singularity.world.blocks.turrets.SglTurret
import singularity.world.blocks.turrets.SglTurret.SglTurretBuild

open class DrawSglTurret :DrawBlock {
  companion object {
    @JvmStatic
    val rand: Rand = Rand()
  }

  var parts: Seq<DrawPart> = Seq<DrawPart>()
  var basePrefix: String? = ""

  @Nullable
  var liquidDraw: Liquid? = null
  lateinit var base: TextureRegion
  var liquid: TextureRegion? = null
  lateinit var top: TextureRegion
  var heat: TextureRegion? = null
  lateinit var preview: TextureRegion
  var outline: TextureRegion? = null

  constructor(vararg parts: DrawPart) {
    this.parts.addAll(*parts)
  }

  constructor(basePrefix: String, vararg parts: DrawPart) {
    this.basePrefix = basePrefix
    this.parts.addAll(*parts)
  }

  constructor(basePrefix: String?) {
    this.basePrefix = basePrefix
  }

  constructor()

  override fun drawPlan(block: Block, plan: BuildPlan, list: Eachable<BuildPlan?>?) {
    Draw.rect(base, plan.drawx(), plan.drawy())
    Draw.rect(preview, plan.drawx(), plan.drawy())
  }

  override fun draw(build: Building) {
    val turret = build.block as SglTurret
    val tb = build as SglTurretBuild

    Draw.rect(base, build.x, build.y)
    Draw.color()

    Draw.z(Layer.turret - 0.5f)

    Drawf.shadow(preview, build.x + tb.recoilOffset.x - turret.elevation, build.y + tb.recoilOffset.y - turret.elevation, tb.drawrot())

    Draw.z(Layer.turret)

    drawTurret(turret, tb)
    drawHeat(turret, tb)

    if (parts.size > 0) {
      if (outline!!.found()) {
        //draw outline under everything when parts are involved
        Draw.z(Layer.turret - 0.01f)
        Draw.rect(outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot())
        Draw.z(Layer.turret)
      }
      //            var params = DrawPart.params.set(build.warmup(), 1f - progress, 1f - progress, tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation);
      //            for(var part : parts){
      //                params.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null ? tb.curRecoils[part.recoilIndex] : tb.curRecoil);
      //                part.draw(params);
      //            }
      val progress = tb.progress()
      val params = DrawPart.params.set(
        build.warmup(),
        1f - progress,
        1f - progress,
        tb.heat,
        tb.curRecoil,
        tb.charge,
        tb.x + tb.recoilOffset.x,
        tb.y + tb.recoilOffset.y,
        tb.rotationu
      )

      for(part in parts) {
        params.setRecoil(if (part.recoilIndex >= 0) tb.curRecoils[part.recoilIndex] else tb.curRecoil)
        part.draw(params)
      }
    }
  }

  open fun drawTurret(block: SglTurret, build: SglTurretBuild) {
    if (block.region.found()) {
      Draw.rect(block.region, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot())
    }

    if (liquid!!.found()) {
      val toDraw: Liquid = (if (liquidDraw == null) build.liquids.current() else liquidDraw)!!
      Drawf.liquid(
        liquid,
        build.x + build.recoilOffset.x,
        build.y + build.recoilOffset.y,
        build.liquids.get(toDraw) / block.liquidCapacity,
        toDraw.color.write(Tmp.c1).a(1f),
        build.drawrot()
      )
    }

    if (top.found()) {
      Draw.rect(top, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot())
    }
  }

  fun drawHeat(block: SglTurret, build: SglTurretBuild) {
    if (build.heat <= 0.00001f || !heat!!.found()) return

    Drawf.additive(
      heat,
      block.heatColor!!.write(Tmp.c1).a(build.heat),
      build.x + build.recoilOffset.x,
      build.y + build.recoilOffset.y,
      build.drawrot(),
      Layer.turretHeat
    )
  }
  /** @return the generated icons to be used for this block. */
  override fun icons(block: Block): Array<TextureRegion> {
    val regions = if (top.found()) arrayOf(base, preview, top) else arrayOf(base, preview)
    return regions
  }

  override fun getRegionsToOutline(block: Block, out: Seq<TextureRegion>) {
    for(part in parts) {
      part.getOutlines(out)
    }

    val generatedIcons = block.getGeneratedIcons()

    if (block.region.found() && !(block.outlinedIcon > 0 && generatedIcons[block.outlinedIcon] == block.region)) {
      out.add(block.region)
    }
  }
  /** Load any relevant texture regions.  */
  override fun load(block: Block) {
    if (block !is SglTurret) throw ClassCastException("This drawer can only be used on turret(Sgl)s.")

    preview = Core.atlas.find(block.name + "_preview", block.region)
    outline = Core.atlas.find(block.name + "_outline")
    liquid = Core.atlas.find(block.name + "_liquid")
    top = Core.atlas.find(block.name + "_top")
    heat = Core.atlas.find(block.name + "_heat")
    base = Core.atlas.find(block.name + "_base")

    for(part in parts) {
      part.turretShading = true
      part.load(block.name)
    }
    if (!base.found()) base = Core.atlas.find((basePrefix + "block_" + block.size).appendModName())
    if (!base.found()) base = Core.atlas.find((basePrefix + "block-" + block.size).appendModName())
  }

}