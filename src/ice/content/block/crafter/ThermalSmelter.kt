package ice.content.block.crafter

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Mathf
import arc.util.Time
import ice.content.IItems
import ice.content.ILiquids

import universecore.world.draw.DrawMulti
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import kotlin.math.max

class ThermalSmelter : NormalCrafter("thermal_smelter") {
  init {
    localization {
      zh_CN {
        this.localizedName = "热能冶炼炉"
        description = "以焦炭和氢气为双热源的高温冶炼炉，用于生产强化合金与铱锭。"
        details = "用于冶炼金属的设备,可以制造气流进行金属化合物的高温煅烧"
      }
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 65, IItems.单晶硅, 70, IItems.铜锭, 60, IItems.钴锭, 60, IItems.钴钢, 70
      )
    )
    health = 550
    size = 3
    liquidCapacity = 120f
    itemCapacity = 30


    newConsume()
    consume!!.time(90f)
    consume!!.items(
      *ItemStack.with(
        IItems.铬锭, 3, IItems.钍锭, 2, IItems.焦炭, 1
      )
    )
    consume!!.liquid(ILiquids.氯化硅溶胶, 0.2f)
    consume!!.power(6f)
    newProduce()
    produce!!.item(IItems.强化合金, 4)

    newConsume()
    consume!!.time(120f)
    consume!!.items(
      *ItemStack.with(
        IItems.氯铱酸盐, 1, IItems.焦炭, 2
      )
    )
    consume!!.liquid(ILiquids.氢气, 0.4f)
    consume!!.power(3f)
    newProduce()
    produce!!.item(IItems.铱锭, 2)

    drawers = DrawMulti(DrawBottom(), object : DrawBlock() {
      val flameColor: Color = Color.valueOf("f58349")

      override fun draw(build: Building) {
        val base = (Time.time / 70)
        Draw.color(flameColor, 0.5f)
        rand.setSeed(build.id.toLong())
        for (i in 0..34) {
          val fin = (rand.random(1f) + base) % 1f
          val angle = rand.random(360f) + (Time.time / 1.5f) % 360f
          val len = 10 * Mathf.pow(fin, 1.5f)
          Draw.alpha(0.5f * build.warmup() * (1f - Mathf.curve(fin, 1f - 0.4f)))
          Fill.circle(
            build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), 3 * fin * build.warmup()
          )
        }

        Draw.blend()
        Draw.reset()
      }
    }, DrawDefault(), object : DrawFlame() {
      init {
        flameRadius = 2f
        flameRadiusScl = 4f
      }

      override fun load(block: Block) {
        top = Core.atlas.find(block.name + "_top")
        block.clipSize = max(block.clipSize, (lightRadius + lightSinMag) * 2f * block.size)
      }
    })
  }
}
