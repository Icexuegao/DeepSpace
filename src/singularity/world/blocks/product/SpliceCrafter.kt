package singularity.world.blocks.product

import arc.Core
import arc.func.Cons
import arc.func.Floatf
import arc.func.Func
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.IntSet
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Strings
import ice.library.struct.AttachedProperty
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.modules.ItemModule
import mindustry.world.modules.LiquidModule
import singularity.world.modules.SglLiquidModule
import universecore.components.blockcomp.*
import universecore.world.blocks.chains.ChainsContainer
import universecore.world.blocks.modules.BaseConsumeModule
import universecore.world.blocks.modules.BaseProductModule
import universecore.world.blocks.modules.ChainsModule
import universecore.world.consumers.ConsumeType
import universecore.world.producers.ProduceType
import kotlin.math.min

@Suppress("UNCHECKED_CAST")
open class SpliceCrafter(name: String) : NormalCrafter(name), SpliceBlockComp {
  override var maxChainsWidth: Int = 10
  override var maxChainsHeight: Int = 10

  var structUpdated: Cons<SpliceCrafterBuild?>? = null

  override var interCorner: Boolean = false

  override var negativeSplice: Boolean = false

  var tempItemCapacity: Int = 0
  var tempLiquidCapacity: Float = 0f
  override fun setStats() {
    super.setStats()
    setChainsStats(stats)
  }

  override fun init() {
    super.init()
    tempItemCapacity = itemCapacity
    tempLiquidCapacity = liquidCapacity

    for (consumer in consumers) {
      for (cons in consumer.all()) {
        val old: Floatf<*>? = cons.consMultiplier
        cons.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild? -> e!!.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild ->
          old.let {

            (it as Floatf<SpliceCrafterBuild>).get(e) * e.chains.container.all.size
          }
        })
      }
    }
    for (consumer in optionalCons) {
      for (cons in consumer.all()) {
        val old: Floatf<*>? = cons.consMultiplier
        cons.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild? -> e!!.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild -> old.let { (it as Floatf<SpliceCrafterBuild>).get(e) * e.chains.container.all.size } })
      }
    }
    for (producer in producers) {
      for (prod in producer.all()) {
        val old: Floatf<*>? = prod.prodMultiplier
        prod.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild -> old.let { (it as Floatf<SpliceCrafterBuild>).get(e) * e.chains.container.all.size } })
      }
    }
  }

  override fun chainable(other: ChainsBlockComp): Boolean {
    return this === other
  }

  companion object {
    var ChainsContainer.items: SpliceItemModule? by AttachedProperty(null)
    var ChainsContainer.liquids: SpliceLiquidModule? by AttachedProperty(null)
    var ChainsContainer.consumer: SpliceConsumeModule? by AttachedProperty(null)
    var ChainsContainer.producer: SpliceProduceModule? by AttachedProperty(null)
    var ChainsContainer.build: SpliceCrafterBuild? by AttachedProperty(null)
    var ChainsContainer.curr: SpliceCrafterBuild? by AttachedProperty(null)
  }

  init {
    buildType = Prov(::SpliceCrafterBuild)
  }

  open inner class SpliceCrafterBuild : NormalCrafterBuild(), SpliceBuildComp {
    override var loadingInvalidPos = IntSet()
    override var chains = ChainsModule(this)
    var b: SpliceCrafterBuild? = this
    var handling: Boolean = false
    var updateModule: Boolean = true
    var firstInit: Boolean = true
    override var splice: Int = 0
    override lateinit var consumer: BaseConsumeModule
    override var producer: BaseProductModule? = null

    override fun items(): SpliceItemModule? {
      return items as SpliceItemModule?
    }

    override fun liquids(): SpliceLiquidModule? {
      return liquids as SpliceLiquidModule?
    }

    override fun create(block: Block, team: Team): NormalCrafterBuild {
      super.create(block, team)

      if (hasItems) items = SpliceItemModule(itemCapacity, true)
      if (hasLiquids) liquids = SpliceLiquidModule(liquidCapacity, true)
      consumer = SpliceConsumeModule(this)
      producer = SpliceProduceModule(this)

      return this
    }

    override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building {
      super.init(tile, team, shouldAdd, rotation)
      chains.newContainer()
      return this
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      updateModule = true
    }

    override fun displayBars(bars: Table) {
      if (recipeCurrent != -1 && producer!!.current != null && block.hasPower && block.outputsPower && producer!!.current!!.get(ProduceType.power) != null) {
        val bar = (Func { entity: Building? ->
          Bar({ Core.bundle.format("bar.poweroutput", Strings.fixed(entity!!.powerProduction * 60 * entity.timeScale(), 1)) }, { Pal.powerBar }, { powerProdEfficiency })
        })
        bars.add(bar.get(this)).growX()
        bars.row()
      }

      //显示流体存储量
      if (hasLiquids && displayLiquid) updateDisplayLiquid()
      if (!displayLiquids.isEmpty) {
        bars.table(Tex.buttonTrans) { t: Table? ->
          t!!.defaults().growX().height(18f).pad(4f)
          t.top().add(liquidsStr).padTop(0f)
          t.row()
          for (stack in displayLiquids) {
            val bar = Func { entity: Building? ->
              Bar({ stack.liquid.localizedName }, { stack.liquid.barColor ?: stack.liquid.color }, { min(entity!!.liquids.get(stack.liquid) / liquids()!!.allCapacity, 1f) })
            }
            t.add(bar.get(this)).growX()
            t.row()
          }
        }.height((26 * displayLiquids.size + 40).toFloat())
        bars.row()
      }

      if (recipeCurrent == -1 || consumer.current == null) return

      if (hasPower && consPower != null) {
        val buffered = consPower.buffered
        val capacity = consPower.capacity
        val bar = (Func { entity: Building? ->
          Bar({ if (buffered) Core.bundle.format("bar.poweramount", if ((entity!!.power.status * capacity).isNaN()) "<ERROR>" else (entity.power.status * capacity).toInt()) else Core.bundle.get("bar.power") }, { Pal.powerBar }, { if (Mathf.zero(consPower.requestedPower(entity)) && entity!!.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f) 1f else entity!!.power.status })
        })
        bars.add(bar.get(this)).growX()
        bars.row()
      }

      val cl = consumer.current!!.get(ConsumeType.liquid)
      if (cl != null) {
        bars.table(Tex.buttonEdge1) { t: Table? -> t!!.left().add(Core.bundle.get("fragment.bars.consume")).pad(4f) }.pad(0f).height(38f).padTop(4f)
        bars.row()
        bars.table { t: Table? ->
          t!!.defaults().grow().margin(0f)
          t.table(Tex.pane2) { liquid: Table? ->
            liquid!!.defaults().growX().margin(0f).pad(4f).height(18f)
            liquid.left().add(Core.bundle.get("misc.liquid")).color(Pal.gray)
            liquid.row()
            for (stack in cl.consLiquids!!) {
              val bar = (Func { entity: Building? ->
                Bar({ stack.liquid.localizedName }, { stack.liquid.barColor ?: stack.liquid.color }, { min(entity!!.liquids.get(stack.liquid) / liquids()!!.allCapacity, 1f) })
              })
              liquid.add(bar.get(this))
              liquid.row()
            }
          }
        }.height((46 + cl.consLiquids!!.size * 26).toFloat()).padBottom(0f).padTop(2f)
      }

      bars.row()

      if (recipeCurrent == -1 || producer!!.current == null) return

      val pl = producer!!.current!!.get(ProduceType.liquid)
      if (pl != null) {
        bars.table(Tex.buttonEdge1) { t: Table? -> t!!.left().add(Core.bundle.get("fragment.bars.product")).pad(4f) }.pad(0f).height(38f)
        bars.row()
        bars.table { t: Table? ->
          t!!.defaults().grow().margin(0f)
          t.table(Tex.pane2) { liquid: Table? ->
            liquid!!.defaults().growX().margin(0f).pad(4f).height(18f)
            liquid.add(Core.bundle.get("misc.liquid")).color(Pal.gray)
            liquid.row()
            for (stack in pl.liquids) {
              val bar = (Func { entity: Building? ->
                Bar({ stack.liquid.localizedName }, { stack.liquid.barColor ?: stack.liquid.color }, { min(entity!!.liquids.get(stack.liquid) / liquids()!!.allCapacity, 1f) })
              })
              liquid.add(bar.get(this))
              liquid.row()
            }
          }
        }.height((46 + pl.liquids.size * 26).toFloat()).padTop(2f)
      }
    }

    override fun update() {
      if (hasItems) itemCapacity = items()!!.allCapacity
      if (hasLiquids) liquidCapacity = liquids()!!.allCapacity
      super.update()
      itemCapacity = tempItemCapacity
      liquidCapacity = tempLiquidCapacity
    }

    override fun updateTile() {
      chains.container.update()
      if (updateModule) {
        if (hasItems) {
          val tItems: SpliceItemModule = chains.container.items!!
          if (!tItems.loaded) {
            tItems.set(items as SpliceItemModule?)
            tItems.loaded = true
          }
          if (items !== tItems) items = tItems
        }
        if (hasLiquids) {
          val tLiquids: SpliceLiquidModule = chains.container.liquids!!
          if (!tLiquids.loaded) {
            tLiquids.set((liquids as SpliceLiquidModule?)!!)
            tLiquids.loaded = true
          }
          if (liquids !== tLiquids) liquids = tLiquids
        }

        val tCons: SpliceConsumeModule = chains.container.consumer!!
        if (!tCons.loaded) {
          tCons.set(consumer as SpliceConsumeModule)
          tCons.loaded = true
        }
        if (consumer !== tCons) {
          consumer = tCons
        }
        b = chains.container.curr

        val tProd: SpliceProduceModule = chains.container.producer!!
        if (!tProd.loaded) {
          tProd.set(producer as SpliceProduceModule)
          tProd.loaded = true
        }
        if (producer !== tProd) producer = tProd

        splice = getSplice

        updateModule = false
      }

      super.updateTile()

      if (producer!!.entity !== this) producer!!.doDump(this)

      handling = false
    }

    override fun getLiquidDestination(from: Building?, liquid: Liquid?): Building? {
      liquidCapacity = liquids()!!.allCapacity
      handling = true
      return super.getLiquidDestination(from, liquid)
    }

    override fun acceptItem(source: Building, item: Item?): Boolean {
      val interactable: Boolean = source.interactable(this.team)
      val bool: Boolean = source is ChainsBuildComp && chains.container.all.contains(source as ChainsBuildComp)


      return interactable && hasItems && !bool && consFilter.filter(b!!, ConsumeType.item, item, acceptAll(ConsumeType.item)) && items.get(item) < items()!!.allCapacity
    }

    override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      return source.interactable(this.team) && hasLiquids && !(source is ChainsBuildComp && chains.container.all.contains(source as ChainsBuildComp)) && consFilter.filter(b!!, ConsumeType.liquid, liquid, acceptAll(ConsumeType.liquid)) && liquids.get(liquid) <= liquids()!!.allCapacity - 0.0001f
    }

    override fun draw() {
      if (hasItems) itemCapacity = items()!!.allCapacity
      if (hasLiquids) liquidCapacity = liquids()!!.allCapacity
      super.draw()
      itemCapacity = tempItemCapacity
      liquidCapacity = tempLiquidCapacity
    }

    override fun drawStatus() {
      if (this.block.enableDrawStatus && consumers.size > 0 && chains.container.build == this) {
        val multiplier = if (block.size > 1 || chains.container.all.size > 1) 1.0f else 0.64f
        val brcx = this.tile.drawx() + (this.block.size * 8).toFloat() / 2.0f - 8 * multiplier / 2
        val brcy = this.tile.drawy() - (this.block.size * 8).toFloat() / 2.0f + 8 * multiplier / 2
        Draw.z(71.0f)
        Draw.color(Pal.gray)
        Fill.square(brcx, brcy, 2.5f * multiplier, 45.0f)
        Draw.color(status()!!.color)
        Fill.square(brcx, brcy, 1.5f * multiplier, 45.0f)
        Draw.color()
      }
    }

    override fun containerCreated(old: ChainsContainer?) {
      chains.container.consumer = SpliceConsumeModule(this)
      chains.container.curr = this
      chains.container.producer = SpliceProduceModule(this)

      if (hasItems) chains.container.items = SpliceItemModule(itemCapacity, firstInit)
      if (hasLiquids) chains.container.liquids = SpliceLiquidModule(tempLiquidCapacity, firstInit)

      chains.container.build = this
      if (firstInit) firstInit = false
    }

    override fun chainsAdded(old: ChainsContainer) {
      if (old === chains.container) return
      if (block.hasItems) chains.container.items!!.add(old.items)
      if (block.hasLiquids) chains.container.liquids!!.add(old.liquids!!)

      val statDisplay: SpliceCrafterBuild = chains.container.build!!

      if (statDisplay !== this) {
        if (statDisplay.y >= building.y && statDisplay.x <= building.x) chains.container.build = this
      }

      updateModule = true
    }

    override fun chainsRemoved(children: Seq<ChainsBuildComp>) {
      val items: SpliceItemModule = chains.container.items!!
      val liquids: SpliceLiquidModule = chains.container.liquids!!

      val targetBlock: SpliceCrafter = getBlock(SpliceCrafter::class.java)

      val handled = ObjectSet<ChainsContainer>()
      var total = 0

      for (child in children) {
        if (handled.add(child.chains.container)) total += child.chains.container.all.size
      }

      for (otherContainer in handled) {
        val present = otherContainer.all.size.toFloat() / total
        val oItems: SpliceItemModule = otherContainer.items!!
        val oLiquids: SpliceLiquidModule = otherContainer.liquids!!

        if (targetBlock.hasItems) {
          oItems.allCapacity = ((items.allCapacity - block.itemCapacity) * present).toInt()
          oItems.clear()
          val totalPre = items.total().toFloat() / items.allCapacity
          items.each { item: Item?, amount: Int ->
            val pre = amount.toFloat() / items.total()
            oItems.set(item, ((items.total() - targetBlock.itemCapacity * totalPre) * present * pre).toInt())
          }
        }

        if (targetBlock.hasLiquids) {
          oLiquids.allCapacity = (liquids.allCapacity - targetBlock.tempLiquidCapacity) * present
          oLiquids.clear()
          val totalPre = liquids.total() / liquids.allCapacity
          liquids.each { liquid: Liquid?, amount: Float ->
            val pre = amount / liquids.total()
            oLiquids.set(liquid, ((liquids.total() - targetBlock.liquidCapacity * totalPre) * present * pre))
          }
        }
      }
    }

    override fun chainsFlowed(old: ChainsContainer?) {
      val statDisplay: SpliceCrafterBuild = chains.container.build!!
      if (statDisplay !== this) {
        if (statDisplay.y >= y && statDisplay.x <= building.x) chains.container.build = this
      }
      updateModule = true
    }

    override fun onChainsUpdated() {
      if (structUpdated != null) structUpdated!!.get(this)
    }
  }

  open class SpliceItemModule(var allCapacity: Int, firstLoad: Boolean) : ItemModule() {
    protected var added: ObjectSet<ItemModule?> = ObjectSet<ItemModule?>()
    var loaded: Boolean = !firstLoad
    var lastFrameId: Long = 0

    fun set(otherModule: SpliceItemModule?) {
      super.set(otherModule)
    }

    fun add(otherModule: SpliceItemModule) {
      if (added.add(otherModule)) {
        super.add(otherModule)
        allCapacity += otherModule.allCapacity
      }
    }

    override fun updateFlow() {
      if (lastFrameId == Core.graphics.frameId) return
      lastFrameId = Core.graphics.frameId

      super.updateFlow()
      added.clear()
    }
  }

  open class SpliceLiquidModule(var allCapacity: Float, firstLoad: Boolean) : SglLiquidModule() {
    protected var added: ObjectSet<LiquidModule?> = ObjectSet<LiquidModule?>()
    var loaded: Boolean = !firstLoad
    var lastFrameId: Long = 0

    fun set(otherModule: SpliceLiquidModule) {
      otherModule.each { liquid: Liquid?, amount: Float -> this.set(liquid, amount) }
    }

    fun add(otherModule: SpliceLiquidModule) {
      if (added.add(otherModule)) {
        otherModule.each { liquid: Liquid?, amount: Float -> this.add(liquid, amount) }
        allCapacity += otherModule.allCapacity
      }
    }

    override fun set(liquid: Liquid?, amount: Float) {
      val delta = get(liquid) - amount
      add(liquid, -delta)
    }

    override fun updateFlow() {
      if (lastFrameId == Core.graphics.frameId) return
      lastFrameId = Core.graphics.frameId

      super.updateFlow()
      added.clear()
    }
  }

  class SpliceConsumeModule(entity: ConsumerBuildComp) : BaseConsumeModule(entity) {
    var loaded: Boolean = false
    var lastFrameId: Long = 0

    fun set(module: SpliceConsumeModule?) {}

    override fun update() {
      if (lastFrameId == Core.graphics.frameId) return
      lastFrameId = Core.graphics.frameId

      super.update()
    }
  }

  class SpliceProduceModule(entity: ProducerBuildComp) : BaseProductModule(entity) {
    var loaded: Boolean = false
    var lastFrameId: Long = 0

    fun set(module: SpliceProduceModule?) {}

    override fun update() {
      if (lastFrameId == Core.graphics.frameId) return
      lastFrameId = Core.graphics.frameId

      super.update()
    }
  }
}