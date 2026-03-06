package ice.world.content.blocks.distribution

import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import ice.library.struct.asDrawable
import mindustry.Vars
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemSeq
import mindustry.type.Liquid
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.BuildVisibility
import singularity.world.blocks.nuclear.EnergySource

open class Randomer(name: String) : EnergySource(name) {
  init {
    size = 1
    sync = true
    solid = true
    update = true
    hasItems = false
    hasPower = true
    hasLiquids = false
    outputsPower = true
    conductivePower = true
    consumesPower = false
    group = BlockGroup.power
    buildType = Prov(::ItemSourceBuild)
    buildVisibility = BuildVisibility.sandboxOnly
    rotate = true
    configurable = true
  }

  inner class ItemSourceBuild : EnergySourceBuild(), HeatBlock {
    override fun getPowerProduction(): Float {
      return 1000f
    }
    val itemsArray = ObjectMap<Item, Boolean>()
    val liquidsArray = ObjectMap<Liquid, Boolean>()
    override fun draw() {
      draw.draw(this)
    }
    override fun buildConfiguration(table: Table) {
      val nearby: Building = nearby(rotation) ?: return

      val item = Seq<Item>()
      val liquid = Seq<Liquid>()
      Vars.content.items().forEach {
        if (nearby.acceptItem(this, it)) {
          item.addUnique(it)
        }
      }
      Vars.content.liquids().forEach {
        if (nearby.acceptLiquid(this, it)) {
          liquid.addUnique(it)
        }
      }
      table.table{
        item.forEach {item ->
          it.button({it1 ->
            it1.image(item.uiIcon)
          }) {
            itemsArray.put(item,!itemsArray.get(item,false))
          }.size(64f)
        }
      }.row()
      table.table{
        liquid.forEach {liquid ->
          it.button({it1 ->
            it1.image(liquid.uiIcon)
          }) {
            liquidsArray.put(liquid,!liquidsArray.get(liquid,false))
          }.size(64f)
        }
      }

    }

    override fun updateTile() {
      super.updateTile()
      val nearby: Building = nearby(rotation) ?: return
      itemsArray.forEach {
        if (it.value&&nearby.acceptItem(this, it.key)) {
          nearby.handleItem(this,it.key)
        }
      }
      liquidsArray.forEach {
        if (it.value&&nearby.acceptLiquid(this, it.key)) {
          nearby.handleLiquid(this, it.key, nearby.block.liquidCapacity - nearby.liquids.get(it.key))
        }
      }
      /*for (liquid in Vars.content.liquids()) {
        proximity.forEach {
          if (it.acceptLiquid(this, liquid)) {
            it.handleLiquid(this, liquid, it.block.liquidCapacity - it.liquids.get(liquid))
          }
        }
      }
      Vars.content.items().forEach(::offload)*/
    }

    override fun handleItem(source: Building?, item: Item?) {
    }

    override fun heat(): Float {
      return 10000f
    }

    override fun heatFrac(): Float {
      return 1f
    }
  }
}