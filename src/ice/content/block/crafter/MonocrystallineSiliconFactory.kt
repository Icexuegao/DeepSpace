package ice.content.block.crafter

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import ice.content.IItems

import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.NormalCrafter

class MonocrystallineSiliconFactory : NormalCrafter("monocrystallineSiliconFactory") {
  init {
    localization {
      zh_CN {
        this.localizedName = "单晶硅厂"
        description = "使用硫化物冲击石英生产单晶硅"
      }
    }
    size = 4
    health = 460
    hasPower = true
    craftEffect = IceEffects.square(IItems.单晶硅.color)
    newConsume().apply {
      time(60f)
      items(IItems.硫化合物, 1, IItems.石英, 3)
      power(1.8f)
    }
    newProduce().apply {
      items(IItems.单晶硅, 2)
    }

    val color = Color.valueOf("ffef99")
    drawers = DrawMulti(DrawRegion("-bottom"), DrawBuild<NormalCrafterBuild> {
      Draw.color(color)
      Draw.alpha(warmup)
      Lines.lineAngleCenter(
        x + Mathf.sin(totalProgress(), 6f, Vars.tilesize / 3f * size), y, 90f, size * Vars.tilesize / 2f
      )
      Lines.lineAngleCenter(
        x, y + Mathf.sin(totalProgress(), 3f, Vars.tilesize / 3f * size), 0f, size * Vars.tilesize / 2f
      )
      Draw.color()
    }, DrawDefault(), DrawFlame(color))
    requirements(Category.crafting, IItems.铬锭, 55, IItems.高碳钢, 150, IItems.铜锭, 100)
  }
}
