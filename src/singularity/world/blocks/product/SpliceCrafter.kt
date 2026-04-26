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
import arc.util.io.Reads
import arc.util.io.Writes
import ice.core.SettingValue
import ice.library.struct.AttachedProperty
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Layer
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

open class SpliceCrafter(name: String) :NormalCrafter(name), SpliceBlockComp {
  companion object {
    var ChainsContainer.items: SpliceItemModule? by AttachedProperty(null)
    var ChainsContainer.liquids: SpliceLiquidModule? by AttachedProperty(null)
    var ChainsContainer.consumer: SpliceConsumeModule? by AttachedProperty(null)
    var ChainsContainer.producer: SpliceProduceModule? by AttachedProperty(null)
    var ChainsContainer.curr: SpliceCrafterBuild? by AttachedProperty(null)
    var ChainsContainer.build: SpliceCrafterBuild? by AttachedProperty(null)
  }

  override var maxChainsWidth: Int = 10
  override var maxChainsHeight: Int = 10
  var structUpdated: Cons<SpliceCrafterBuild?>? = null
  override var interCorner: Boolean = false
  override var negativeSplice: Boolean = false
  var tempItemCapacity: Int = 0
  var tempLiquidCapacity: Float = 0f

  init {
    buildType = Prov(::SpliceCrafterBuild)
  }

  override fun init() {
    super.init()
    this.tempItemCapacity = this.itemCapacity
    this.tempLiquidCapacity = this.liquidCapacity

    for(consumer in consumers) {
      for(cons in consumer.all()) {
        val old = cons.consMultiplier as Floatf<SpliceCrafterBuild>?
        cons.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild ->
          old.get(
            e
          ) * e.chains.container.all.size
        })
      }
    }
    for(consumer in optionalCons) {
      for(cons in consumer.all()) {
        val old = cons.consMultiplier as Floatf<SpliceCrafterBuild>?
        cons.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild ->
          old.get(
            e
          ) * e.chains.container.all.size
        })
      }
    }
    for(producer in producers) {
      for(prod in producer.all()) {
        val old = prod.prodMultiplier as Floatf<SpliceCrafterBuild>?
        prod.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild ->
          old.get(
            e
          ) * e.chains.container.all.size
        })
      }
    }
  }

  override fun chainable(other: ChainsBlockComp): Boolean {
    return this === other
  }

  override fun setStats() {
    super.setStats()
    this.setChainsStats(this.stats)
  }

  open inner class SpliceCrafterBuild :NormalCrafterBuild(), SpliceBuildComp {
    override var loadingInvalidPos: IntSet = IntSet()
    override var chains = ChainsModule(this)
    var b: SpliceCrafterBuild = this
    var handling: Boolean = false
    var updateModule: Boolean = true
    var firstInit: Boolean = true
    override var splice: Int = 0

    override fun items(): SpliceItemModule? {
      return this.items as SpliceItemModule?
    }

    override fun liquids(): SpliceLiquidModule {
      return this.liquids as SpliceLiquidModule
    }

    override fun create(block: Block, team: Team): NormalCrafterBuild {
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

    override fun init(tile: Tile, team: Team, shouldAdd: Boolean, rotation: Int): Building {
      super.init(tile, team, shouldAdd, rotation)
      this.chains.newContainer()
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

    override fun displayBars(bars: Table) {
      if (this.recipeCurrent != -1 && this.producer!!.current != null && this.block.hasPower && this.block.outputsPower && this.producer!!.current!!.get(
          ProduceType.power
        ) != null
      ) {
        val bar = Func { entity: Building? ->
          Bar({
            Core.bundle.format(
              "bar.poweroutput", *arrayOf<Any>(Strings.fixed(entity!!.powerProduction * 60.0f * entity.timeScale(), 1))
            )
          }, { Pal.powerBar }, { this.powerProdEfficiency })
        }
        bars.add(bar.get(this)).growX()
        bars.row()
      }

      if (this@SpliceCrafter.hasLiquids && this@SpliceCrafter.displayLiquid) {
        this.updateDisplayLiquid()
      }

      if (!this.displayLiquids.isEmpty) {
        bars.table(Tex.buttonTrans) { t ->
          t.defaults().growX().height(18.0f).pad(4.0f)
          t.top().add(this@SpliceCrafter.liquidsStr).padTop(0.0f)
          t.row()
          for(stack in this.displayLiquids) {
            val bar = Func { entity: Building? ->
              Bar(
                { stack.liquid.localizedName },
                { stack.liquid.barColor ?: stack.liquid.color },
                { min(entity!!.liquids.get(stack.liquid) / this.liquids().allCapacity, 1.0f) })
            }
            t.add(bar.get(this)).growX()
            t.row()
          }
        }.height((26 * this.displayLiquids.size + 40).toFloat())
        bars.row()
      }

      if (this.recipeCurrent != -1 && this.consumer.current != null) {
        if (this@SpliceCrafter.hasPower && this@SpliceCrafter.consPower != null) {
          val buffered = this@SpliceCrafter.consPower.buffered
          val capacity = this@SpliceCrafter.consPower.capacity
          val bar = Func { entity: Building? ->
            Bar(
              {
              if (buffered) Core.bundle.format(
                "bar.poweramount",
                *arrayOf<Any?>(if ((entity!!.power.status * capacity).isNaN()) "<ERROR>" else (entity.power.status * capacity).toInt())
              ) else Core.bundle.get("bar.power")
            },
              { Pal.powerBar },
              { if (Mathf.zero(this@SpliceCrafter.consPower.requestedPower(entity)) && entity!!.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0.0f) 1.0f else entity!!.power.status })
          }
          bars.add(bar.get(this)).growX()
          bars.row()
        }

        val cl = this.consumer.current!!.get(ConsumeType.liquid)
        if (cl != null) {
          bars.table(Tex.buttonEdge1) { t -> t.left().add(Core.bundle.get("fragment.bars.consume")).pad(4.0f) }.pad(0.0f).height(38.0f)
            .padTop(4.0f)
          bars.row()
          bars.table { t ->
            t.defaults().grow().margin(0.0f)
            t.table(Tex.pane2) { liquid ->
              liquid!!.defaults().growX().margin(0.0f).pad(4.0f).height(18.0f)
              liquid.left().add(IceStats.流体.localized()).color(Pal.gray)
              liquid.row()
              for(stack in cl.consLiquids!!) {
                val bar = Func { entity: Building ->
                  Bar(
                    { stack.liquid.localizedName },
                    { stack.liquid.barColor ?: stack.liquid.color },
                    { min(entity.liquids.get(stack.liquid) / this.liquids().allCapacity, 1.0f) })
                }
                liquid.add(bar.get(this))
                liquid.row()
              }
            }
          }.height((46 + cl.consLiquids!!.size * 26).toFloat()).padBottom(0.0f).padTop(2.0f)
        }

        bars.row()
        if (this.recipeCurrent != -1 && this.producer!!.current != null) {
          val pl = this.producer!!.current!!.get(ProduceType.liquid)
          if (pl != null) {
            bars.table(Tex.buttonEdge1) { t -> t.left().add(Core.bundle.get("fragment.bars.product")).pad(4.0f) }.pad(0.0f).height(38.0f)
            bars.row()
            bars.table { t ->
              t!!.defaults().grow().margin(0.0f)
              t.table(Tex.pane2) { liquid ->
                liquid!!.defaults().growX().margin(0.0f).pad(4.0f).height(18.0f)
                liquid.add(IceStats.流体.localized()).color(Pal.gray)
                liquid.row()
                for(stack in pl.liquids) {
                  val bar = Func { entity: Building ->
                    Bar(
                      { stack.liquid.localizedName },
                      { stack.liquid.barColor ?: stack.liquid.color },
                      { min(entity.liquids.get(stack.liquid) / this.liquids().allCapacity, 1.0f) })
                  }
                  liquid.add(bar.get(this))
                  liquid.row()
                }
              }
            }.height((46 + pl.liquids.size * 26).toFloat()).padTop(2.0f)
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
        this@SpliceCrafter.liquidCapacity = this.liquids().allCapacity
      }

      super.update()
      this@SpliceCrafter.itemCapacity = this@SpliceCrafter.tempItemCapacity
      this@SpliceCrafter.liquidCapacity = this@SpliceCrafter.tempLiquidCapacity
    }

    override fun updateTile() {
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
            tLiquids.set((this.liquids as SpliceLiquidModule?)!!)
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
      this@SpliceCrafter.liquidCapacity = this.liquids().allCapacity
      this.handling = true
      return super.getLiquidDestination(from, liquid)
    }

    override fun acceptItem(source: Building, item: Item): Boolean {
      return source.interactable(this.team) && this@SpliceCrafter.hasItems && (source !is ChainsBuildComp || !this.chains.container.all.contains(
        source as ChainsBuildComp
      )) && this@SpliceCrafter.consFilter.filter(
        this.b,
        ConsumeType.item,
        item,
        this.acceptAll(ConsumeType.item)
      ) && this.items.get(item) < this.items()!!.allCapacity
    }

    override fun acceptLiquid(source: Building, liquid: Liquid): Boolean {
      return source.interactable(this.team) && this@SpliceCrafter.hasLiquids && (source !is ChainsBuildComp || !this.chains.container.all.contains(
        source as ChainsBuildComp
      )) && this@SpliceCrafter.consFilter.filter(
        this.b,
        ConsumeType.liquid,
        liquid,
        this.acceptAll(ConsumeType.liquid)
      ) && this.liquids.get(liquid) <= this.liquids().allCapacity - 1.0E-4f
    }

    override fun draw() {
      if (this@SpliceCrafter.hasItems) {
        this@SpliceCrafter.itemCapacity = this.items()!!.allCapacity
      }

      if (this@SpliceCrafter.hasLiquids) {
        this@SpliceCrafter.liquidCapacity = this.liquids().allCapacity
      }
      super.draw()
      this@SpliceCrafter.itemCapacity = this@SpliceCrafter.tempItemCapacity
      this@SpliceCrafter.liquidCapacity = this@SpliceCrafter.tempLiquidCapacity
    }

    override fun drawcornerMark(select: Boolean) {
      fun draw() {
        Draw.z(Layer.blockOver)
        producer?.current?.get(ProduceType.item)?.items[0]?.let {
          drawItemSelection(it.item)
          Draw.reset()
          return
        }
        producer?.current?.get(ProduceType.liquid)?.liquids[0]?.let {
          drawItemSelection(it.liquid)
        }
        Draw.reset()
      }
      if (SettingValue.启用多合成角标常显 && Vars.state.isGame && !select && chains.container.build ==this) {
        draw()
      }else if (
        (select && !SettingValue.启用多合成角标常显) && Vars.state.isGame
      )  draw()
    }

    override fun drawStatus() {
      if (this.block.enableDrawStatus && this@SpliceCrafter.consumers.size > 0 && this.chains.container.build === this) {
        val multiplier = if (this.block.size <= 1 && this.chains.container.all.size <= 1) 0.64f else 1.0f
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
      this.chains.container.consumer = SpliceConsumeModule(this)
      this.chains.container.curr = this
      this.chains.container.producer = SpliceProduceModule(this)
      if (this@SpliceCrafter.hasItems) {
        this.chains.container.items = SpliceItemModule(this@SpliceCrafter.itemCapacity, this.firstInit)
      }

      if (this@SpliceCrafter.hasLiquids) {
        this.chains.container.liquids = SpliceLiquidModule(this@SpliceCrafter.tempLiquidCapacity, this.firstInit)
      }

      this.chains.container.build = this
      if (this.firstInit) {
        this.firstInit = false
      }
    }

    override fun chainsAdded(old: ChainsContainer) {
      if (old !== this.chains.container) {
        if (this.block.hasItems) {
          (this.chains.container.items as SpliceItemModule).add(old.items)
        }

        if (this.block.hasLiquids) {
          (this.chains.container.liquids as SpliceLiquidModule).add((old.liquids)!!)
        }

        val statDisplay: SpliceCrafterBuild
        if (((this.chains.container.build as SpliceCrafterBuild).also {
            statDisplay = it
          }) !== this && statDisplay.y >= this.building.y && statDisplay.x <= this.building.x) {
          this.chains.container.build = this
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

      for(child in children) {
        if (handled.add(child.chains.container)) {
          total += child.chains.container.all.size
        }
      }

      val var13 = handled.iterator()

      while(var13.hasNext()) {
        val otherContainer = var13.next() as ChainsContainer
        val present = otherContainer.all.size.toFloat() / total.toFloat()
        val oItems = otherContainer.items
        val oLiquids = otherContainer.liquids
        if (targetBlock.hasItems) {
          oItems!!.allCapacity = ((items!!.allCapacity - this.block.itemCapacity).toFloat() * present).toInt()
          oItems.clear()
          val totalPre = items.total().toFloat() / items.allCapacity.toFloat()
          items.each { item: Item, amount: Int ->
            val pre = amount.toFloat() / items.total().toFloat()
            oItems.set(item, ((items.total().toFloat() - targetBlock.itemCapacity.toFloat() * totalPre) * present * pre).toInt())
          }
        }

        if (targetBlock.hasLiquids) {
          oLiquids!!.allCapacity = (liquids!!.allCapacity - targetBlock.tempLiquidCapacity) * present
          oLiquids.clear()
          val totalPre = liquids.total() / liquids.allCapacity
          liquids.each { liquid: Liquid, amount: Float ->
            val pre = amount / liquids.total()
            oLiquids.set(liquid, (liquids.total() - targetBlock.liquidCapacity * totalPre) * present * pre)
          }
        }
      }
    }

    override fun chainsFlowed(old: ChainsContainer?) {
      val statDisplay: SpliceCrafterBuild
      if (((this.chains.container.build as SpliceCrafterBuild).also {
          statDisplay = it
        }) !== this && statDisplay.y >= this.y && statDisplay.x <= this.building.x) {
        this.chains.container.build = this
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

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.readChains(read)
    }
  }

  open class SpliceItemModule(var allCapacity: Int, firstLoad: Boolean) :ItemModule() {
    protected var added: ObjectSet<ItemModule?> = ObjectSet<ItemModule?>()
    var loaded: Boolean = !firstLoad
    var lastFrameId: Long = 0

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
      if (this.lastFrameId != Core.graphics.frameId) {
        this.lastFrameId = Core.graphics.frameId
        super.updateFlow()
        this.added.clear()
      }
    }
  }

  open class SpliceLiquidModule(var allCapacity: Float, firstLoad: Boolean) :SglLiquidModule() {
    protected var added: ObjectSet<LiquidModule> = ObjectSet<LiquidModule>()
    var loaded: Boolean = !firstLoad
    var lastFrameId: Long = 0

    fun set(otherModule: SpliceLiquidModule) {
      otherModule.each { liquid: Liquid, amount: Float -> this.set(liquid, amount) }
    }

    fun add(otherModule: SpliceLiquidModule) {
      if (this.added.add(otherModule)) {
        otherModule.each { liquid: Liquid?, amount: Float -> this.add(liquid, amount) }
        this.allCapacity += otherModule.allCapacity
      }
    }

    override fun set(liquid: Liquid, amount: Float) {
      val delta = this.get(liquid) - amount
      this.add(liquid, -delta)
    }

    override fun updateFlow() {
      if (this.lastFrameId != Core.graphics.frameId) {
        this.lastFrameId = Core.graphics.frameId
        super.updateFlow()
        this.added.clear()
      }
    }
  }

  class SpliceConsumeModule(entity: ConsumerBuildComp) :BaseConsumeModule(entity) {
    var loaded: Boolean = false
    var lastFrameId: Long = 0

    fun set(module: SpliceConsumeModule?) {
    }

    override fun update() {
      if (this.lastFrameId != Core.graphics.frameId) {
        this.lastFrameId = Core.graphics.frameId
        super.update()
      }
    }
  }

  class SpliceProduceModule(entity: ProducerBuildComp) :BaseProductModule(entity) {
    var loaded: Boolean = false
    var lastFrameId: Long = 0

    fun set(module: SpliceProduceModule?) {
    }

    override fun update() {
      if (this.lastFrameId != Core.graphics.frameId) {
        this.lastFrameId = Core.graphics.frameId
        super.update()
      }
    }
  }
}
