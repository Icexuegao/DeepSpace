package ice.content.block.crafter

import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.math.geom.Point2
import arc.scene.ui.layout.Table
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IItems
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.liquid.LiquidJunction
import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.ctype.ContentType
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.BlockStatus
import singularity.ui.tables.DistTargetConfigTable
import singularity.world.blocks.SglBlock
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.distribution.GridChildType
import universecore.util.DataPackable

open class 焚化炉 : SglBlock("incinerator") {
  var effect: Effect = Fx.fuelburn
  var flameColor: Color = Color.valueOf("ffad9d")

  init {
    BaseBundle.bundle {
      desc(zh_CN, "焚化炉", "智能销毁流体和物品,可进行二级面板配置,精准控制每一个输入源")
    }
    size = 1
    flameColor = IceColor.b4
    configurable = true

    conductivePower = true
    hasPower = true
    //  rotate = true
    //   rotateDraw = false
    drawArrow = false
    hasLiquids = true
    hasItems = true
    update = true
    solid = true
    configurable = true
    buildType = Prov(::IncineratorBuild)
    requirements(Category.crafting, IItems.高碳钢, 20, IItems.铜锭, 5, IItems.铅锭, 5)
    newConsume().apply {
      power(20 / 60f)
    }
    drawers = DrawMulti(DrawDefault(), DrawBuild<IncineratorBuild> {
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
    })
  }

  override fun pointConfig(config: Any?, transformer: Cons<Point2>): Any? {
    if (config is ByteArray) {
      val var5 = DataPackable.readObject<DataPackable?>(config, *arrayOfNulls<Any>(0))
      if (var5 is TargetConfigure) {
        var5.configHandle(transformer)
        return var5.pack()
      }
    }

    return config
  }

  override fun parseConfigObjects(e: SglBuilding, obj: Any) {
    super.parseConfigObjects(e, obj)
    val build = e as IncineratorBuild
    if (obj is TargetConfigure) {
      build.config = if (obj.isClear) TargetConfigure() else obj
    }
  }

  inner class IncineratorBuild : SglBuilding() {
    var heat: Float = 0f
    var config = TargetConfigure()

    override fun buildConfiguration(table: Table) {
      val distTargetConfigTable =
        DistTargetConfigTable(0, config, arrayOf(GridChildType.acceptor), arrayOf(ContentType.item, ContentType.liquid), true, { c ->
          configure(c.pack())
        }) {}
      table.add(distTargetConfigTable)
      table.background = IStyles.paneLeft
    }

    override fun config(): Any {
      return config.pack()
    }

    override fun updateTile() {
      heat = Mathf.approachDelta(heat, if (consumer.valid && enabled) 1f else 0f, 0.04f)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      val configLen = read.i()
      if (configLen > 0) {
        config.read(read.b(configLen))
      }
    }

    override fun write(write: Writes) {
      super.write(write)
      val bytes = config.pack()
      write.i(bytes.size)
      if (bytes.size > 0) {
        write.b(bytes)
      }
    }

    override fun status(): BlockStatus {
      return if (!enabled) BlockStatus.logicDisable else if (heat > 0.5f) BlockStatus.active else BlockStatus.noInput
    }

    override fun acceptItem(source: Building, item: Item): Boolean {
      if (config.any() && heat >= 0.5f) {
        val configuredContents = config.get(GridChildType.acceptor, ContentType.item)
        if (configuredContents != null && configuredContents.contains(item)) {
          val dirBit = config.getDirections(GridChildType.acceptor, item)
          for (it in dirBit) return heandDirBit(source, it)
        }
        return false
      }
      return heat > 0.5f
    }

    override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
      if (config.any() && heat >= 0.5f && liquid.incinerable) {
        val configuredContents = config.get(GridChildType.acceptor, ContentType.liquid)
        if (configuredContents != null && configuredContents.contains(liquid)) {
          val dirBit = config.getDirections(GridChildType.acceptor, liquid)
          for (it in dirBit) return heandDirBit(source, it)
        }
        return false
      }
      return heat > 0.5f && liquid.incinerable
    }

    fun heandDirBit(build: Building, dirBit: Int): Boolean {
      if (nearby(dirBit) == build) return true
      if (nearby(dirBit) is LiquidJunction.LiquidJunctionBuild || nearby(dirBit) is mindustry.world.blocks.liquid.LiquidJunction.LiquidJunctionBuild) return true
      return false
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