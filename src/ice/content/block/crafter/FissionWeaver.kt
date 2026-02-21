package ice.content.block.crafter

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.scene.ui.layout.Table
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawWeave
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.Stats
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.meta.SglStat
import universecore.world.consumers.BaseConsumers

class FissionWeaver:NormalCrafter("fission_weaver"){
  init{
  bundle {
    desc(zh_CN, "裂变编织器", "使用相控阵辐照压印技术,在有辐射源的情况下将铀的同位素压印为相位物")
  }
  requirements(
    Category.crafting, IItems.FEX水晶, 50, IItems.絮凝剂, 60, IItems.强化合金, 50, IItems.钴钢, 45, IItems.单晶硅, 70
  )
  size = 4
  oneOfOptionCons = true
  itemCapacity = 24

  newConsume()
  consume!!.time(90f)
  consume!!.power(2.5f)
  consume!!.items(
    *ItemStack.with(
      IItems.单晶硅, 4, IItems.铀238, 1
    )
  )
  consume!!.consValidCondition { e: NormalCrafterBuild? -> e!!.statusi > 0 }
  newProduce()
  produce!!.item(Items.phaseFabric, 6)

  craftEffect = Fx.smeltsmoke
  val recipe = Cons { item: Item? ->
    newOptionalConsume({ e: NormalCrafterBuild, c: BaseConsumers ->
      e.statusi = 2
    }, { s: Stats?, c: BaseConsumers? ->
      s!!.add(SglStat.effect) { t: Table? -> t!!.add(Core.bundle.get("misc.doConsValid")) }
    }).overdriveValid(false)
    consume!!.item(item!!, 1)
    consume!!.time(180f)
    consume!!.power(0.4f)
    consume!!.optionalAlwaysValid = true
  }
  recipe.get(IItems.铀235)
  recipe.get(IItems.钚239)

  buildType = Prov {
    object : NormalCrafterBuild() {
      override fun updateTile() {
        super.updateTile()
        statusi = if (statusi > 0) statusi - 1 else 0
      }

      override fun status(): BlockStatus? {
        val status = super.status()
        if (status == BlockStatus.noInput && statusi > 0) return BlockStatus.noOutput
        return status
      }
    }
  }

  draw = DrawMulti(DrawBottom(), object : DrawWeave() {
    override fun load(block: Block) {
      weave = Core.atlas.find(block.name + "_top")
    }
  }, DrawDefault(), object : DrawBlock() {
    override fun draw(build: Building) {
      val e = build as NormalCrafterBuild
      Draw.color(SglDrawConst.winter, e.workEfficiency() * (0.4f + Mathf.absin(6f, 0.15f)))
      SglDraw.gradientCircle(e.x, e.y, 8f, 10f, 0f)
      SglDraw.gradientCircle(e.x, e.y, 8f, -4f, 0f)
    }
  })
}
}