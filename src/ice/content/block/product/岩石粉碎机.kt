package ice.content.block.product

import arc.Core
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import ice.content.IItems
import ice.content.block.EnvironmentBlocks
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.FloorCrafter
import singularity.world.draw.DrawBottom
import universecore.world.consumers.cons.ConsumeFloor

class 岩石粉碎机 :FloorCrafter("rock_crusher") {
  init {
    bundle {
      desc(
        zh_CN,
        "岩石粉碎机",
        "将岩石粉碎成细小的颗粒,对于没有沙子的地方来说十分有用,同时某些岩石含盐量高,因此还可以从中得到一些有用的副产物",
        "事实上这台机器的效率并不算高,或者说它浪费掉的材料太多了,为了产出能够供应工业使用的富硅沙砾,几乎每生产一吨石英沙就要消耗掉几十吨原石,更别提硅纯度更低的一些岩石了"
      )
    }
    requirements(
      Category.production, IItems.强化合金, 40, IItems.气凝胶, 55, IItems.单晶硅, 60, IItems.铬锭, 50, IItems.黄铜锭, 60
    )
    size = 3

    warmupSpeed = 0.004f
    updateEffect = Fx.pulverizeSmall
    craftEffect = Fx.mine
    craftEffectColor = Items.sand.color

    oneOfOptionCons = false

    itemCapacity = 25
    liquidCapacity = 30f

    newFormula { consumers, producers ->
      consumers.apply {
        time(30f)
        power(2.2f)
        add(
          ConsumeFloor<FloorCrafterBuild>(
            EnvironmentBlocks.皎月银沙,
            0.8f / 9f,
            EnvironmentBlocks.风蚀沙地,
            1.2f / 9f,
            EnvironmentBlocks.风蚀砂地,
            1f / 9f,
            EnvironmentBlocks.潮汐石,
            0.6f / 9f,
            EnvironmentBlocks.新月岩,
            0.4f / 9f,
            EnvironmentBlocks.血蚀岩,
            0.4f / 9f
          )
        ).baseEfficiency = 0f
      }
      producers.item(IItems.金珀沙, 1)
    }
    newFormula { consumers, producers ->
      consumers.apply {
        time(45f)
        power(2.2f)
        add(
          ConsumeFloor<FloorCrafterBuild>(
            EnvironmentBlocks.云英岩, 0.4f / 9f
          )
        ).baseEfficiency = 0f
      }
      producers.item(IItems.石英, 1)
    }

    /*newOptionalProduct()
    consume!!.time(45f)
    consume!!.add(
      ConsumeFloor<FloorCrafterBuild>(
        EnvironmentBlocks.云英岩, 0.4f / 9f
      )
    ).baseEfficiency = 0f
    consume!!.optionalAlwaysValid = false
    produce!!.item(IItems.石英, 1)*/

    /*newOptionalProduct()
    consume!!.add(SglConsumeFloor<FloorCrafterBuild>(Attribute.spores, 1f)).baseEfficiency = 0f
    consume!!.optionalAlwaysValid = false
    produce!!.liquid(ILiquids.孢子云, 0.2f)*/

    newBooster(1.8f).liquid(Liquids.water, 0.12f)

    drawers = DrawMulti(
      DrawBottom(), DrawDefault(), object :DrawBlock() {
        var rim: TextureRegion? = null
        val heatColor: Color = Color.valueOf("ff5512")

        override fun draw(build: Building) {
          val e = build as NormalCrafterBuild

          Draw.color(heatColor)
          Draw.alpha(e.workEfficiency() * 0.6f * (1f - 0.3f + Mathf.absin(Time.time, 3f, 0.3f)))
          Draw.blend(Blending.additive)
          Draw.rect(rim, e.x, e.y)
          Draw.blend()
          Draw.color()
        }

        override fun load(block: Block) {
          rim = Core.atlas.find(block.name + "_rim")
        }
      }, object :DrawRegion("_rotator") {
        init {
          rotateSpeed = 2.8f
          spinSprite = true
        }
      }, DrawRegion("_top")
    )
  }
}