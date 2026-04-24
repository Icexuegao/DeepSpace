package ice.content.block.product

import ice.content.IItems
import ice.content.ILiquids
import ice.world.meta.IAttribute
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidRegion
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import singularity.world.blocks.product.FloorCrafter
import singularity.world.draw.DrawBottom
import universecore.world.consumers.cons.ConsumeFloor

class 岩层钻井机 :FloorCrafter("rock_drill") {
  init {
    localization {
      zh_CN {
        localizedName = "岩层钻井机"
        description = "特种钻井,钻探深层的地壳,将深埋在地壳深处的较高质量的矿物送至地表"
      }
    }
    requirements(Category.production, IItems.铬锭, 45, IItems.铅锭, 30, IItems.铜锭, 30)
    size = 2
    liquidCapacity = 24f
    oneOfOptionCons = true
    health = 180

    updateEffect = Fx.pulverizeSmall
    craftEffect = Fx.mine
    craftEffectColor = Pal.lightishGray

    warmupSpeed = 0.005f

    hasLiquids = true

    autoSelect = true

    newConsume()
    consume!!.time(60f)
    consume!!.liquid(Liquids.water, 0.2f)
    consume!!.power(1.75f)
    newProduce()
    produce!!.item(IItems.岩层沥青, 1)

    newConsume()
    consume!!.time(60f)
    consume!!.liquid(ILiquids.急冻液, 0.2f)
    consume!!.power(1.75f)
    newProduce()
    produce!!.item(IItems.岩层沥青, 2)

    newBooster(1f)
    consume!!.add(ConsumeFloor(IAttribute.沥青, 1.12f))

    drawers = DrawMulti(
      DrawBottom(), object :DrawLiquidRegion(Liquids.water) {
        init {
          suffix = "_liquid"
        }
      }, object :DrawRegion("_rotator") {
        init {
          rotateSpeed = 1.5f
          spinSprite = true
        }
      }, DrawDefault(), DrawRegion("_top")
    )
  }
}