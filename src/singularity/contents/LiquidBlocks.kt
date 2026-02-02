package singularity.contents

import arc.func.Boolf3
import ice.content.IItems
import mindustry.content.Items
import mindustry.game.Team
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.Tile
import singularity.world.blocks.liquid.ClusterConduit
import singularity.world.blocks.liquid.ClusterValve
import singularity.world.blocks.liquid.ConduitRiveting
import singularity.world.blocks.liquid.LiquidUnloader

class LiquidBlocks : ContentList {
  override fun load() {
    cluster_conduit = object : ClusterConduit("cluster_conduit") {
      init {
        requirements(Category.liquid, ItemStack.with(Items.titanium, 8, IItems.气凝胶, 10, IItems.铝, 10))
        liquidCapacity = 10f
        liquidPressure = 1.05f
        health = 360
      }
    }
    conduit_riveting = FakeBlock(object : ConduitRiveting("conduit_riveting") {
      init {
        requirements(Category.liquid, ItemStack.with(Items.plastanium, 18, IItems.气凝胶, 10, IItems.铝, 16))
        liquidCapacity = 10f
        health = 300
      }
    }) { tile: Tile?, team: Team?, rotation: Int? ->
      val build = tile!!.build
      build is ClusterConduit.ClusterConduitBuild && build.rotation == rotation
    }
    filter_valve = FakeBlock(object : ClusterValve("filter_valve") {
      init {
        requirements(Category.liquid, ItemStack.with(Items.titanium, 10, IItems.气凝胶, 15, Items.graphite, 12))
        liquidCapacity = 10f
        health = 300
      }
    }) { tile: Tile?, _: Team?, rotation: Int? ->
      val build = tile!!.build
      build is ClusterConduit.ClusterConduitBuild && build.rotation == rotation
    }
    liquid_unloader = object : LiquidUnloader("liquid_unloader") {
      init {
        requirements(Category.liquid, ItemStack.with(Items.silicon, 20, IItems.铝, 25, Items.graphite, 15))
        size = 1
      }
    }
  }

  class FakeBlock(maskedBlock: Block, placeValid: Boolf3<Tile?, Team?, Int?>?) : universecore.world.blocks.FakeBlock(maskedBlock, placeValid) {
    init {
      maskedBlock.alwaysUnlocked = true
    }
  }

  companion object {
    /**集束管道 */
    var cluster_conduit: Block? = null

    /**管道铆 */
    var conduit_riveting: Block? = null

    /**过滤阀 */
    var filter_valve: Block? = null

    /**液体提取器 */
    var liquid_unloader: Block? = null
  }
}