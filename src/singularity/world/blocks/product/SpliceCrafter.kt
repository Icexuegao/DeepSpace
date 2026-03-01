package singularity.world.blocks.product

import arc.Core
import arc.Events
import arc.func.*
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.IntSet
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.struct.AttachedProperty
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName
import ice.world.meta.IceStats
import mindustry.game.EventType
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
import mindustry.world.modules.ItemModule.ItemConsumer
import mindustry.world.modules.LiquidModule
import mindustry.world.modules.LiquidModule.LiquidConsumer
import singularity.world.modules.SglLiquidModule
import universecore.components.blockcomp.*
import universecore.world.blocks.chains.ChainsContainer
import universecore.world.blocks.modules.BaseConsumeModule
import universecore.world.blocks.modules.BaseProductModule
import universecore.world.blocks.modules.ChainsModule
import universecore.world.consumers.cons.ConsumeLiquidBase
import universecore.world.consumers.ConsumeType
import universecore.world.producers.ProduceType
import kotlin.math.min

open class SpliceCrafter(name: String) : NormalCrafter(name), SpliceBlockComp {
  companion object{
    var ChainsContainer.items:SpliceItemModule? by AttachedProperty(null)
    var ChainsContainer.liquids: SpliceLiquidModule? by AttachedProperty(null)
    var ChainsContainer.consumer:SpliceConsumeModule? by AttachedProperty(null)
    var ChainsContainer.producer: SpliceProduceModule? by AttachedProperty(null)
    var ChainsContainer.curr: SpliceCrafterBuild? by AttachedProperty(null)
    var ChainsContainer.build:SpliceCrafterBuild? by AttachedProperty(null)
  }
  override var maxChainsWidth: Int = 10
  override var maxChainsHeight: Int = 10
  var structUpdated: Cons<SpliceCrafterBuild?>? = null
  override var interCorner: Boolean = false
  override  var negativeSplice: Boolean = false
  var tempItemCapacity: Int = 0
  var tempLiquidCapacity: Float = 0f
init {
  buildType= Prov(::SpliceCrafterBuild)
}
  public override fun init() {
    super.init()
    this.tempItemCapacity = this.itemCapacity
    this.tempLiquidCapacity = this.liquidCapacity

    for (consumer in consumers) {
      for (cons in consumer.all()) {
        val old: Floatf<SpliceCrafterBuild>? = cons.consMultiplier as Floatf<SpliceCrafterBuild>?
        cons.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild -> old!!.get(e) * e!!.chains.container.all.size })
      }
    }
    for (consumer in optionalCons) {
      for (cons in consumer.all()) {
        val old: Floatf<SpliceCrafterBuild>? = cons.consMultiplier as Floatf<SpliceCrafterBuild>?
        cons.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild -> old.get(e) * e!!.chains.container.all.size })
      }
    }
    for (producer in producers) {
      for (prod in producer.all()) {
        val old: Floatf<SpliceCrafterBuild>? = prod.prodMultiplier as Floatf<SpliceCrafterBuild>?
        prod.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild -> old.get(e) * e.chains.container.all.size })
      }
    }
  }

  override fun chainable(other: ChainsBlockComp): Boolean {
    return this === other
  }

  public override fun setStats() {
    super.setStats()
    this.setChainsStats(this.stats)
  }

  open inner class SpliceCrafterBuild : NormalCrafterBuild(), SpliceBuildComp {
    override var loadingInvalidPos: IntSet = IntSet()
    override var chains= ChainsModule(this)
    var b: SpliceCrafterBuild = this
    var handling: Boolean = false
    var updateModule: Boolean = true
    var firstInit: Boolean = true
    override var splice: Int = 0

    override fun items(): SpliceItemModule? {
      return this.items as SpliceItemModule?
    }

    override fun liquids(): SpliceLiquidModule? {
      return this.liquids as SpliceLiquidModule?
    }

    public override fun create(block: Block, team: Team): NormalCrafterBuild {
      super.create(block, team)

      if (this@SpliceCrafter.hasItems) {
        this.items = SpliceItemModule(this@SpliceCrafter.itemCapacity, true)
      }

      if (this@SpliceCrafter.hasLiquids) {
        this.liquids = SpliceLiquidModule(this@SpliceCrafter.liquidCapacity, true)
      }

      this.consumer = SpliceConsumeModule(this)
      this.producer = SpliceProduceModule(this)
      return this
    }

    public override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
      super.init(tile, team, shouldAdd, rotation)
      this.chains!!.newContainer()
      return this
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      this.updateModule = true
    }


    override fun remove() {
      this.updateModule = true
      super.remove()
    }

    public override fun displayBars(bars: Table) {
      if (this.recipeCurrent != -1 && this.producer!!.current != null && this.block.hasPower && this.block.outputsPower && this.producer!!.current!!.get(ProduceType.power) != null) {
        val bar = Func { entity: Building? -> Bar(Prov { Core.bundle.format("bar.poweroutput", *arrayOf<Any>(Strings.fixed(entity!!.getPowerProduction() * 60.0f * entity.timeScale(), 1))) }, Prov { Pal.powerBar }, Floatp { this.powerProdEfficiency }) }
        bars.add<Bar?>(bar.get(this)).growX()
        bars.row()
      }

      if (this@SpliceCrafter.hasLiquids && this@SpliceCrafter.displayLiquid) {
        this.updateDisplayLiquid()
      }

      if (!this.displayLiquids.isEmpty()) {
        bars.table(Tex.buttonTrans, Cons { t: Table? ->
          t!!.defaults().growX().height(18.0f).pad(4.0f)
          t.top().add(this@SpliceCrafter.liquidsStr).padTop(0.0f)
          t.row()
          for (stack in this.displayLiquids) {
            val bar = Func { entity: Building? -> Bar(Prov { stack.liquid.localizedName }, Prov { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color }, Floatp { min(entity!!.liquids.get(stack.liquid) / this.liquids()!!.allCapacity, 1.0f) }) }
            t.add<Bar?>(bar.get(this)).growX()
            t.row()
          }
        }).height((26 * this.displayLiquids.size + 40).toFloat())
        bars.row()
      }

      if (this.recipeCurrent != -1 && this.consumer.current != null) {
        if (this@SpliceCrafter.hasPower && this@SpliceCrafter.consPower != null) {
          val buffered = this@SpliceCrafter.consPower.buffered
          val capacity = this@SpliceCrafter.consPower.capacity
          val bar = Func { entity: Building? -> Bar(Prov { if (buffered) Core.bundle.format("bar.poweramount", *arrayOf<Any?>(if ((entity!!.power.status * capacity).isNaN()) "<ERROR>" else (entity.power.status * capacity).toInt())) else Core.bundle.get("bar.power") }, Prov { Pal.powerBar }, Floatp { if (Mathf.zero(this@SpliceCrafter.consPower.requestedPower(entity)) && entity!!.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0.0f) 1.0f else entity!!.power.status }) }
          bars.add<Bar?>(bar.get(this)).growX()
          bars.row()
        }

        val cl = this.consumer.current!!.get(ConsumeType.liquid)
        if (cl != null) {
          bars.table(Tex.buttonEdge1, Cons { t: Table? -> t!!.left().add(Core.bundle.get("fragment.bars.consume")).pad(4.0f) }).pad(0.0f).height(38.0f).padTop(4.0f)
          bars.row()
          bars.table(Cons { t: Table? ->
            t!!.defaults().grow().margin(0.0f)
            t.table(Tex.pane2, Cons { liquid: Table? ->
              liquid!!.defaults().growX().margin(0.0f).pad(4.0f).height(18.0f)
              liquid.left().add(IceStats.流体.localizedName).color(Pal.gray)
              liquid.row()
              for (stack in cl.consLiquids!!) {
                val bar = Func { entity: Building? -> Bar(Prov { stack.liquid.localizedName }, Prov { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color }, Floatp { min(entity!!.liquids.get(stack.liquid) / this.liquids()!!.allCapacity, 1.0f) }) }
                liquid.add<Bar?>(bar.get(this))
                liquid.row()
              }
            })
          }).height((46 + cl.consLiquids!!.size * 26).toFloat()).padBottom(0.0f).padTop(2.0f)
        }

        bars.row()
        if (this.recipeCurrent != -1 && this.producer!!.current != null) {
          val pl = this.producer!!.current!!.get(ProduceType.liquid)
          if (pl != null) {
            bars.table(Tex.buttonEdge1, Cons { t: Table? -> t!!.left().add(Core.bundle.get("fragment.bars.product")).pad(4.0f) }).pad(0.0f).height(38.0f)
            bars.row()
            bars.table(Cons { t: Table? ->
              t!!.defaults().grow().margin(0.0f)
              t.table(Tex.pane2, Cons { liquid: Table? ->
                liquid!!.defaults().growX().margin(0.0f).pad(4.0f).height(18.0f)
                liquid.add(IceStats.流体.localizedName).color(Pal.gray)
                liquid.row()
                for (stack in pl.liquids) {
                  val bar = Func { entity: Building? -> Bar(Prov { stack.liquid.localizedName }, Prov { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color }, Floatp { min(entity!!.liquids.get(stack.liquid) / this.liquids()!!.allCapacity, 1.0f) }) }
                  liquid.add<Bar?>(bar.get(this))
                  liquid.row()
                }
              })
            }).height((46 + pl.liquids.size * 26).toFloat()).padTop(2.0f)
          }
        }
      }
    }

    override fun update() {
      updateModule = true
      if (this@SpliceCrafter.hasItems) {
        this@SpliceCrafter.itemCapacity = this.items()!!.allCapacity
      }

      if (this@SpliceCrafter.hasLiquids) {
        this@SpliceCrafter.liquidCapacity = this.liquids()!!.allCapacity
      }

      super.update()
      this@SpliceCrafter.itemCapacity = this@SpliceCrafter.tempItemCapacity
      this@SpliceCrafter.liquidCapacity = this@SpliceCrafter.tempLiquidCapacity
    }

    public override fun updateTile() {
      this.chains.container.update()
      if (this.updateModule) {
        if (this@SpliceCrafter.hasItems) {
          val tItems = this.chains.container.items!!
          if (!tItems.loaded) {
            tItems.set(this.items as SpliceItemModule?)
            tItems.loaded = true
          }

          if (this.items !== tItems) {
            this.items = tItems
          }
        }

        if (this@SpliceCrafter.hasLiquids) {
          val tLiquids = this.chains.container.liquids!!
          if (!tLiquids.loaded) {
            tLiquids.set((this.liquids as SpliceCrafter.SpliceLiquidModule?)!!)
            tLiquids.loaded = true
          }

          if (this.liquids !== tLiquids) {
            this.liquids = tLiquids
          }
        }

        val tCons = this.chains.container.consumer!!
        if (!tCons.loaded) {
          tCons.set(this.consumer as SpliceConsumeModule)
          tCons.loaded = true
        }

        if (this.consumer !== tCons) {
          this.consumer = tCons
        }

        this.b = this.chains.container.curr!!
        val tProd = this.chains.container.producer!!
        if (!tProd.loaded) {
          tProd.set(this.producer as SpliceProduceModule)
          tProd.loaded = true
        }

        if (this.producer !== tProd) {
          this.producer = tProd
        }

        this.splice = this.getSplice
        this.updateModule = false
      }

      super.updateTile()
      if (this.producer!!.entity !== this) {
        this.producer!!.doDump(this)
      }

      this.handling = false
    }

    override fun getLiquidDestination(from: Building?, liquid: Liquid?): Building? {
      this@SpliceCrafter.liquidCapacity = this.liquids()!!.allCapacity
      this.handling = true
      return super.getLiquidDestination(from, liquid)
    }

    public override fun acceptItem(source: Building, item: Item?): Boolean {
      return source.interactable(this.team) && this@SpliceCrafter.hasItems &&
              (source !is ChainsBuildComp || !this.chains!!.container.all.contains(source as ChainsBuildComp))
              && this@SpliceCrafter.consFilter.filter(this.b, ConsumeType.item, item, this.acceptAll(ConsumeType.item))
              && this.items.get(item) < this.items()!!.allCapacity
    }

    public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      return source.interactable(this.team) && this@SpliceCrafter.hasLiquids &&
              (source !is ChainsBuildComp || !this.chains!!.container.all.contains(source as ChainsBuildComp))
              && this@SpliceCrafter.consFilter.filter(this.b, ConsumeType.liquid, liquid, this.acceptAll(ConsumeType.liquid))
              && this.liquids.get(liquid) <= this.liquids()!!.allCapacity - 1.0E-4f
    }

    public override fun draw() {

      if (this@SpliceCrafter.hasItems) {
        this@SpliceCrafter.itemCapacity = this.items()!!.allCapacity
      }

      if (this@SpliceCrafter.hasLiquids) {
        this@SpliceCrafter.liquidCapacity = this.liquids()!!.allCapacity
      }

      super.draw()
      this@SpliceCrafter.itemCapacity = this@SpliceCrafter.tempItemCapacity
      this@SpliceCrafter.liquidCapacity = this@SpliceCrafter.tempLiquidCapacity
    }

    public override fun drawStatus() {
      if (this.block.enableDrawStatus && this@SpliceCrafter.consumers.size > 0 && this.chains.container.build === this) {
        val multiplier = if (this.block.size <= 1 && this.chains!!.container.all.size <= 1) 0.64f else 1.0f
        val brcx = this.tile.drawx() + (this.block.size * 8).toFloat() / 2.0f - 8.0f * multiplier / 2.0f
        val brcy = this.tile.drawy() - (this.block.size * 8).toFloat() / 2.0f + 8.0f * multiplier / 2.0f
        Draw.z(71.0f)
        Draw.color(Pal.gray)
        Fill.square(brcx, brcy, 2.5f * multiplier, 45.0f)
        Draw.color(this.status()!!.color)
        Fill.square(brcx, brcy, 1.5f * multiplier, 45.0f)
        Draw.color()
      }
    }

    override fun containerCreated(old: ChainsContainer?) {
      this.chains!!.container.consumer=SpliceConsumeModule(this)
      this.chains!!.container.curr=this
      this.chains!!.container.producer=SpliceProduceModule(this)
      if (this@SpliceCrafter.hasItems) {
        this.chains!!.container.items=SpliceItemModule(this@SpliceCrafter.itemCapacity, this.firstInit)
      }

      if (this@SpliceCrafter.hasLiquids) {
        this.chains!!.container.liquids=SpliceLiquidModule(this@SpliceCrafter.tempLiquidCapacity, this.firstInit)
      }

      this.chains!!.container.build=this
      if (this.firstInit) {
        this.firstInit = false
      }
    }

    override fun chainsAdded(old: ChainsContainer) {
      if (old !== this.chains!!.container) {
        if (this.block.hasItems) {
          (this.chains!!.container.items as SpliceItemModule).add(old.items as SpliceItemModule?)
        }

        if (this.block.hasLiquids) {
          (this.chains!!.container.liquids as SpliceLiquidModule).add((old.liquids as SpliceCrafter.SpliceLiquidModule?)!!)
        }

        val statDisplay: SpliceCrafterBuild
        if (((this.chains!!.container.build as SpliceCrafterBuild).also { statDisplay = it }) !== this && statDisplay.y >= this.building.y && statDisplay.x <= this.building.x) {
          this.chains!!.container.build=this
        }

        this.updateModule = true
      }
    }

    override fun chainsRemoved(children: Seq<ChainsBuildComp>) {
      val items = this.chains.container.items
      val liquids = this.chains.container.liquids
      val targetBlock = this.getBlock<SpliceCrafter>(SpliceCrafter::class.java)
      val handled: ObjectSet<ChainsContainer?> = ObjectSet<ChainsContainer?>()
      var total = 0

      for (child in children) {
        if (handled.add(child.chains.container)) {
          total += child.chains.container.all.size
        }
      }

      val var13 = handled.iterator()

      while (var13.hasNext()) {
        val otherContainer = var13.next() as ChainsContainer
        val present = otherContainer.all.size.toFloat() / total.toFloat()
        val oItems = otherContainer.items
        val oLiquids = otherContainer.liquids
        if (targetBlock.hasItems) {
          oItems!!.allCapacity = ((items!!.allCapacity - this.block.itemCapacity).toFloat() * present).toInt()
          oItems.clear()
          val totalPre = items.total().toFloat() / items.allCapacity.toFloat()
          items.each(ItemConsumer { item: Item?, amount: Int ->
            val pre = amount.toFloat() / items.total().toFloat()
            oItems!!.set(item, ((items.total().toFloat() - targetBlock.itemCapacity.toFloat() * totalPre) * present * pre).toInt())
          })
        }

        if (targetBlock.hasLiquids) {
          oLiquids!!.allCapacity = (liquids!!.allCapacity - targetBlock.tempLiquidCapacity) * present
          oLiquids.clear()
          val totalPre = liquids!!.total() / liquids.allCapacity
          liquids.each(LiquidConsumer { liquid: Liquid?, amount: kotlin.Float ->
            val pre = amount / liquids.total()
            oLiquids.set(liquid, (liquids.total() - targetBlock.liquidCapacity * totalPre) * present * pre)
          })
        }
      }
    }

    override fun chainsFlowed(old: ChainsContainer?) {
      val statDisplay: SpliceCrafterBuild
      if (((this.chains!!.container.build as SpliceCrafterBuild).also { statDisplay = it }) !== this && statDisplay.y >= this.y && statDisplay.x <= this.building.x) {
        this.chains!!.container.build=this
      }

      this.updateModule = true
    }

    override fun onChainsUpdated() {
      if (this@SpliceCrafter.structUpdated != null) {
        this@SpliceCrafter.structUpdated!!.get(this)
      }
    }


    fun loadingInvalidPos(): IntSet {
      return this.loadingInvalidPos
    }

    override fun onProximityAdded() {
      super.onProximityAdded()
      this.onChainsAdded()
    }

    override fun onProximityRemoved() {
      super.onProximityRemoved()
      this.onChainsRemoved()
    }

    override fun write(write: Writes) {
      super.write(write)
      this.writeChains(write)
    }

    public override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.readChains(read)
    }
  }

  class SpliceItemModule(var allCapacity: Int, firstLoad: Boolean) : ItemModule() {
    protected var added: ObjectSet<ItemModule?> = ObjectSet<ItemModule?>()
    var loaded: Boolean
    var lastFrameId: Long = 0

    init {
      this.loaded = !firstLoad
    }

    fun set(otherModule: SpliceItemModule?) {
      super.set(otherModule)
    }

    fun add(otherModule: SpliceItemModule) {
      if (this.added.add(otherModule)) {
        super.add(otherModule)
        this.allCapacity += otherModule.allCapacity
      }
    }

    override fun updateFlow() {
      if (this.lastFrameId != Core.graphics.getFrameId()) {
        this.lastFrameId = Core.graphics.getFrameId()
        super.updateFlow()
        this.added.clear()
      }
    }
  }

  class SpliceLiquidModule(var allCapacity: kotlin.Float, firstLoad: Boolean) : SglLiquidModule() {
    protected var added: ObjectSet<LiquidModule> = ObjectSet<LiquidModule>()
    var loaded: Boolean
    var lastFrameId: Long = 0

    init {
      this.loaded = !firstLoad
    }

    fun set(otherModule: SpliceLiquidModule) {
      otherModule.each(LiquidConsumer { liquid: Liquid?, amount: kotlin.Float -> this.set(liquid, amount) })
    }

    fun add(otherModule: SpliceLiquidModule) {
      if (this.added.add(otherModule)) {
        otherModule.each(LiquidConsumer { liquid: Liquid?, amount: kotlin.Float -> this.add(liquid, amount) })
        this.allCapacity += otherModule.allCapacity
      }
    }

    override fun set(liquid: Liquid?, amount: kotlin.Float) {
      val delta = this.get(liquid) - amount
      this.add(liquid, -delta)
    }

    override fun updateFlow() {
      if (this.lastFrameId != Core.graphics.getFrameId()) {
        this.lastFrameId = Core.graphics.getFrameId()
        super.updateFlow()
        this.added.clear()
      }
    }
  }

  class SpliceConsumeModule(entity: ConsumerBuildComp) : BaseConsumeModule(entity) {
    var loaded: Boolean = false
    var lastFrameId: Long = 0

    fun set(module: SpliceConsumeModule?) {
    }

    override fun update() {
      if (this.lastFrameId != Core.graphics.getFrameId()) {
        this.lastFrameId = Core.graphics.getFrameId()
        super.update()
      }
    }
  }

  class SpliceProduceModule(entity: ProducerBuildComp) : BaseProductModule(entity) {
    var loaded: Boolean = false
    var lastFrameId: Long = 0

    fun set(module: SpliceProduceModule?) {
    }

    override fun update() {
      if (this.lastFrameId != Core.graphics.getFrameId()) {
        this.lastFrameId = Core.graphics.getFrameId()
        super.update()
      }
    }
  }
}
