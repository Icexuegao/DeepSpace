package singularity.world.draw

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.library.struct.AttachedProperty
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import singularity.graphic.SglDrawConst
import singularity.world.blocks.nuclear.NuclearReactor

class DrawReactorHeat : DrawBlock() {
  companion object {
    const val FLASH: String = "flash"
    var NuclearReactor.NuclearReactorBuild.flash: Float by AttachedProperty(0f)
  }
    var lightColor: Color? = Color.valueOf("7f19ea")
    var coolColor: Color = Color(1f, 1f, 1f, 0f)
    var hotColor: Color? = Color.valueOf("ff9575a3")
    var flashThreshold: Float = 46f
    var lightsRegion: TextureRegion? = null

    override fun load(block: Block) {
        lightsRegion = Core.atlas.find(block.name + "_light")
    }

    override fun draw(build: Building) {
        val e = build as NuclearReactor.NuclearReactorBuild

        Draw.color(coolColor, hotColor, e.heat / (e.block as NuclearReactor).maxHeat)
        Fill.rect(e.x, e.y, (e.block.size * Vars.tilesize).toFloat(), (e.block.size * Vars.tilesize).toFloat())

        if (e.heat > flashThreshold) {
          e.flash += (1f + ((e.heat - flashThreshold) / ((e.block as NuclearReactor).maxHeat - flashThreshold)) * 5.4f) * Time.delta

            val fa: Float = e.flash// e.handleVar(FLASH, { f: Float -> f + (1f + ((e.heat - flashThreshold) / ((e.block as NuclearReactor).maxHeat - flashThreshold)) * 5.4f) * Time.delta }, 0f)
            Draw.color(Color.red, Color.yellow, Mathf.absin(fa, 9f, 1f))
            Draw.alpha(0.3f)
            Draw.rect(lightsRegion, e.x, e.y)
        }

        Draw.reset()
    }

    override fun drawLight(build: Building) {
        val e = build as NuclearReactor.NuclearReactorBuild
        val smoothLight = e.smoothEfficiency
        Drawf.light(
            e.x, e.y, (90f + Mathf.absin(5f, 5f)) * smoothLight,
            Tmp.c1.set(lightColor).lerp(Color.scarlet, e.heat), 0.6f * smoothLight
        )
    }

    override fun icons(block: Block): Array<TextureRegion?> {
        return SglDrawConst.EMP_REGIONS
    }


}