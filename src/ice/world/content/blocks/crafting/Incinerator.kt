package ice.world.content.blocks.crafting

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IItems
import ice.content.ILiquids
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.ReqImage
import mindustry.ui.Styles
import mindustry.world.meta.BlockStatus

open class Incinerator : IceBlock("incinerator") {
  var effect: Effect = Fx.fuelburn
  var flameColor: Color = Color.valueOf("ffad9d")

  init {
    bundle {
      desc(zh_CN, "焚烧炉")
    }
    size = 1
    flameColor = IceColor.b4
    consumePower(20 / 60f)
    requirements(Category.crafting, IItems.高碳钢, 20, IItems.铜锭, 5, IItems.铅锭, 5)
  }

  init {
    conductivePower = true
    hasPower = true
    hasLiquids = true
    hasItems = true
    update = true
    solid = true
    configurable = true
    buildType = Prov(::IncineratorBuild)
  }

  inner class IncineratorBuild : IceBuild() {
    var heat: Float = 0f

    var incinerationLiquid = false
    var incinerationItem = false

    override fun buildConfiguration(table: Table) {
      table.button({
        it.add(ReqImage(IItems.铜锭.uiIcon) {
          incinerationItem
        })
      }, Styles.cleari) {
        incinerationItem = !incinerationItem
      }.size(43f)
      table.button({
        it.add(ReqImage(ILiquids.纯净水.uiIcon) {
          incinerationLiquid
        })
      }, Styles.cleari) {
        incinerationLiquid = !incinerationLiquid
      }.size(43f)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      incinerationItem = read.bool()
      incinerationLiquid = read.bool()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.bool(incinerationItem)
      write.bool(incinerationLiquid)
    }

    override fun updateTile() {
      heat = Mathf.approachDelta(heat, efficiency, 0.04f)
    }

    override fun status(): BlockStatus {
      return if (!enabled) BlockStatus.logicDisable else if (heat > 0.5f) BlockStatus.active else BlockStatus.noInput
    }

    override fun draw() {
      super.draw()

      if (heat > 0f) {
        val g = 0.3f
        val r = 0.06f

        Draw.alpha(((1f - g) + Mathf.absin(Time.time, 8f, g) + Mathf.random(r) - r) * heat)

        Draw.tint(flameColor)
        Fill.circle(x, y, 2f)
        Draw.color(1f, 1f, 1f, heat)
        Fill.circle(x, y, 1f)

        Draw.color()
      }
    }

    override fun acceptItem(source: Building?, item: Item?): Boolean {
      return hasItems && heat > 0.5f && incinerationItem
    }

    override fun acceptLiquid(source: Building?, liquid: Liquid): Boolean {
      return hasLiquids && heat > 0.5f && liquid.incinerable && incinerationLiquid
    }

    override fun handleItem(source: Building?, item: Item?) {
      if (Mathf.chance(0.3)) {
        effect.at(x, y)
      }
    }

    override fun handleLiquid(source: Building?, liquid: Liquid?, amount: Float) {
      if (Mathf.chance(0.02)) {
        effect.at(x, y)
      }
    }
  }
}
