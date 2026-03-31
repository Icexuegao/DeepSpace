package ice.content.block.nuclear

import arc.func.Floatf
import arc.func.Func
import arc.graphics.Color
import ice.content.IItems
import ice.ui.bundle.bundle
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.type.SglCategory
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.cons.item.ConsumeItems
import universecore.world.consumers.cons.liquid.ConsumeLiquids

class 衰变仓:NormalCrafter("decay_bin"){init {
  bundle {
    desc(zh_CN, "衰变仓", "放射性物质进行衰变产生少量的核能量,可能存在副产物")
  }
  requirements(
    SglCategory.nuclear, IItems.强化合金, 60, IItems.FEX水晶, 40, IItems.单晶硅, 50, IItems.铅锭, 80, IItems.石英玻璃, 40
  )
  size = 2
  autoSelect = true
  canSelect = false

  newFormula { consumers, producers ->
    consumers.apply {
      time(600f)
      item(IItems.铀235, 1)
    }
    producers.apply {
      energy(0.25f)
      item(IItems.钍锭, 1)
    }
  }

  newFormula { consumers, producers ->
    consumers.apply {
      time(540f)
      item(IItems.钚239, 1)
    }
    producers.apply {
      energy(0.35f)
    }
  }
  newFormula { consumers, producers ->
    consumers.apply {
      time(900f)
      item(IItems.铀238, 1)
    }
    producers.apply {
      energy(0.12f)
    }
  }
  newFormula { consumers, producers ->
    consumers.apply {
      time(450f)
      item(IItems.钍锭, 1)
    }
    producers.apply {
      energy(0.2f)
    }
  }


  updateEffect = Fx.generatespark
  updateEffectChance = 0.01f

  drawers = DrawMulti(
    DrawBottom(), DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
      init {
        color = Func { e: NormalCrafterBuild? ->
          val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as BaseConsumers).first()
          when (cons) {
            is ConsumeLiquids<*> -> {
              var liquid = cons.consLiquids!![0].liquid
              if (liquid === Liquids.water) liquid = cons.consLiquids!![1].liquid
              return@Func liquid.color
            }

            is ConsumeItems<*> -> {
              val item = cons.consItems!![0].item
              return@Func item.color
            }

            else -> return@Func Color.white
          }
        }
        alpha = Floatf { e: NormalCrafterBuild? ->
          val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as BaseConsumers).first()
          when (cons) {
            is ConsumeLiquids<*> -> {
              var liquid = cons.consLiquids!![0].liquid
              if (liquid === Liquids.water) liquid = cons.consLiquids!![1].liquid
              return@Floatf e.liquids.get(liquid) / e.block.liquidCapacity
            }

            is ConsumeItems<*> -> {
              val item = cons.consItems!![0].item
              return@Floatf e.items.get(item).toFloat() / e.block.itemCapacity
            }

            else -> return@Floatf 0f
          }
        }
      }
    })
}}