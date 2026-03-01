package singularity.world.blocks.product

import arc.Core
import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.struct.ObjectIntMap
import arc.struct.ObjectMap
import mindustry.Vars
import mindustry.game.Team
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import singularity.world.components.FloorCrafterBuildComp
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeType
import universecore.world.producers.BaseProducers

open class FloorCrafter(name: String) : NormalCrafter(name) {
  init {
    buildType= Prov(::FloorCrafterBuild)
  }
  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    var eff = 0f
    var c = 0
    var line = 0
    val t = Vars.world.tile(x, y)
    if (t != null) {
      for (consumer in consumers) {
        val cons = consumer.get(ConsumeType.floor)
        c++
        eff = cons?.getEff(FloorCrafterBuildComp.getFloors(t, this))?:0f
      }

      if (c == 0) eff = 1f
      for (boost in boosts) {
        val cons = boost.key!!.get(ConsumeType.floor)
        c++
        eff *= cons?.getEff(FloorCrafterBuildComp.getFloors(t, this))?:0f
      }

      for (product in optionalProducts) {
        if (!valid && !product.key!!.optionalAlwaysValid) continue
        val cons = product.key!!.get(ConsumeType.floor)
        val optEff = cons!!.getEff(FloorCrafterBuildComp.getFloors(t, this))
        if (optEff <= 0) continue
        val ta = buildIconsTable(product)
        val width = drawPlaceText(
          Core.bundle.format("bar.efficiency", (optEff * 100f).toInt()), x, y + line, valid
        )
        val dx = x * Vars.tilesize + offset - width / 2f
        val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5 - line * 8f
        ta.setPosition(dx - ta.getWidth() / 8f, dy - ta.getHeight() / 16f)
        ta.isTransform = true
        ta.setScale(1f / 8f)
        ta.draw()
        line++
      }
    }

    drawPlaceText(
      if (c == 1 && valid) Core.bundle.format("bar.efficiency", (eff * 100f).toInt()) else if (valid) Core.bundle.get("infos.placeValid") else Core.bundle.get("infos.placeInvalid"), x, y + line, valid
    )
  }

  override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
    var eff = 0f
    var c = 0

    for (product in optionalProducts) {
      val cons = product.key!!.get(ConsumeType.floor)
      if (cons != null) {
        if (product.key!!.optionalAlwaysValid && cons.getEff(FloorCrafterBuildComp.getFloors(tile, this)) > 0) return true
      }
    }

    for (consumer in consumers) {
      val cons = consumer.get(ConsumeType.floor)
      if (cons != null) {
        c++
        eff = cons.getEff(FloorCrafterBuildComp.getFloors(tile, this))
      }
    }

    if (c == 0) eff = 1f
    for (boost in boosts) {
      val cons = boost.key!!.get(ConsumeType.floor)
      if (cons != null) {
        c++
        eff *= cons.getEff(FloorCrafterBuildComp.getFloors(tile, this))
      }
    }

    return c > 0 && eff > 0
  }

  inner class FloorCrafterBuild : NormalCrafterBuild(), FloorCrafterBuildComp {
    override var floorCount = ObjectIntMap<Floor>()
    override fun onProximityUpdate() {
      super.onProximityUpdate()
      updateFloors()
    }
  }

  companion object {
    private val iconsTable = Table()

    private fun buildIconsTable(product: ObjectMap.Entry<BaseConsumers?, BaseProducers?>): Table {
      iconsTable.clear()
      var first = true
      for (produce in product.value!!.all()) {
        if (!produce.hasIcons()) continue

        if (!first) iconsTable.add("+").fillX().pad(4f)
        iconsTable.table { ca: Table? ->
          ca!!.defaults().padLeft(3f).fill()
          produce.buildIcons(ca)
        }.fill()

        first = false
      }
      iconsTable.pack()
      return iconsTable
    }
  }
}