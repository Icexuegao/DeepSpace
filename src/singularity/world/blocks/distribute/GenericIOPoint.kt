package singularity.world.blocks.distribute

import arc.func.Prov
import arc.math.Mathf
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.modules.ItemModule
import mindustry.world.modules.LiquidModule
import singularity.world.blocks.distribute.matrixGrid.RequestHandlers.*
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import singularity.world.distribution.buffers.ItemsBuffer
import singularity.world.distribution.buffers.LiquidsBuffer
import kotlin.math.min

open class GenericIOPoint(name: String) : IOPoint(name) {
  init {
    this.size = 1
    this.hasLiquids = true
    this.hasItems = true
    this.displayFlow = false
    this.outputsLiquid = true
    this.outputItems = true
    this.allowConfigInventory = false
    this.itemCapacity = 16
    this.liquidCapacity = 16.0f
    buildType = Prov(::GenericIOPPointBuild)
  }

  override fun setupRequestFact() {
    this.setFactory(GridChildType.output, ContentType.item, ReadItemRequestHandler())
    this.setFactory(GridChildType.input, ContentType.item, PutItemRequestHandler())
    this.setFactory(GridChildType.acceptor, ContentType.item, AcceptItemRequestHandler())
    this.setFactory(GridChildType.output, ContentType.liquid, ReadLiquidRequestHandler())
    this.setFactory(GridChildType.input, ContentType.liquid, PutLiquidRequestHandler())
    this.setFactory(GridChildType.acceptor, ContentType.liquid, AcceptLiquidRequestHandler())
  }

  open inner class GenericIOPPointBuild : IOPointBuild() {
    var outItems: ItemModule? = null
    var outLiquid: LiquidModule? = null
    protected var siphoning: Boolean = false

    override fun create(block: Block, team: Team): Building {
      super.create(block, team)
      this.outItems = ItemModule()
      this.outLiquid = LiquidModule()
      return this
    }

    fun output(item: Item?, amount: Int): Int {
      val add = min(amount, this@GenericIOPoint.itemCapacity - this.outItems!!.get(item))
      this.outItems!!.add(item, add)
      return add
    }

    fun output(liquid: Liquid?, amount: Float): Float {
      val add = min(amount, this@GenericIOPoint.liquidCapacity - this.outLiquid!!.get(liquid))
      this.outLiquid!!.add(liquid, add)
      return add
    }

    override fun valid(unit: DistMatrixUnitBuildComp?, type: GridChildType?, content: Content?): Boolean {
      return if (this.config == null) {
        false
      } else if (content is Item) {
        this.config!!.get(GridChildType.output, content) && this.acceptItemOut(unit!!.building, content)
      } else if (content !is Liquid) {
        false
      } else {
        this.config!!.get(GridChildType.output, content) && this.acceptLiquidOut(unit!!.building, content)
      }
    }

    public override fun resourcesDump() {
      if (this.config != null) {
        var var1 = this.config!!.get(GridChildType.output, ContentType.item)!!.iterator()

        while (var1.hasNext()) {
          val item = var1.next()
          this.dump(item as Item?)
        }

        var1 = this.config!!.get(GridChildType.output, ContentType.liquid)!!.iterator()

        while (var1.hasNext()) {
          val liquid = var1.next()
          this.dumpLiquid(liquid as Liquid?)
        }
      }
    }

    override fun dump(toDump: Item?): Boolean {
      if (this.config != null && this.block.hasItems && this.outItems!!.total() != 0 && (toDump == null || this.outItems!!.has(toDump))) {
        if (this.proximity.size == 0) {
          return false
        } else {
          if (toDump == null) {
            for (ii in 0..<Vars.content.items().size) {
              val item = Vars.content.item(ii)
              val other = this.getNext("dumpItem") { e: Building? -> e!!.interactable(this.team) && this.outItems!!.has(item) && e.acceptItem(this, item) && this.canDump(e, item) && this.config!!.directValid(GridChildType.output, item, this.getDirectBit(e)) }
              if (other != null) {
                other.handleItem(this, item)
                this.outItems!!.remove(item, 1)
                this.incrementDump(this.proximity.size)
                return true
              }
            }
          } else {
            val other = this.getNext("dumpItem") { e: Building? -> e!!.interactable(this.team) && this.outItems!!.has(toDump) && e.acceptItem(this, toDump) && this.canDump(e, toDump) && this.config!!.directValid(GridChildType.output, toDump, this.getDirectBit(e)) }
            if (other != null) {
              other.handleItem(this, toDump)
              this.outItems!!.remove(toDump, 1)
              this.incrementDump(this.proximity.size)
              return true
            }
          }

          return false
        }
      } else {
        return false
      }
    }

    override fun dumpLiquid(liquid: Liquid, scaling: Float) {
      val dump = this.cdump
      if (this.config != null && !(this.outLiquid!!.get(liquid) <= 1.0E-4f)) {
        if (!Vars.net.client() && Vars.state.isCampaign && this.team === Vars.state.rules.defaultTeam) {
          liquid.unlock()
        }

        for (i in 0..<this.proximity.size) {
          this.incrementDump(this.proximity.size)
          var other = this.proximity.get((i + dump) % this.proximity.size)
          other = other!!.getLiquidDestination(this, liquid)
          if (other != null && other.interactable(this.team) && other.block.hasLiquids && this.canDumpLiquid(other, liquid) && other.liquids != null && this.config!!.directValid(GridChildType.output, liquid, this.getDirectBit(other))) {
            val ofract = other.liquids.get(liquid) / other.block.liquidCapacity
            val fract = this.outLiquid!!.get(liquid) / this.block.liquidCapacity
            if (ofract < fract) {
              this.outputLiquid(other, (fract - ofract) * this.block.liquidCapacity / scaling, liquid)
            }
          }
        }
      }
    }

    fun outputLiquid(next: Building, amount: Float, liquid: Liquid?): Float {
      val flow = min(next.block.liquidCapacity - next.liquids.get(liquid), amount)
      if (next.acceptLiquid(this, liquid)) {
        next.handleLiquid(this, liquid, flow)
        this.outLiquid!!.remove(liquid, flow)
      }

      return flow
    }

    public override fun resourcesSiphon() {
      this.siphoning = true
      if (this.config != null) {
        var var1 = this.config!!.get(GridChildType.input, ContentType.item)!!.iterator()

        while (var1.hasNext()) {
          val item = var1.next()
          this.siphonItem(item as Item?)
        }

        var1 = this.config!!.get(GridChildType.input, ContentType.liquid)!!.iterator()

        while (var1.hasNext()) {
          val liquid = var1.next()
          this.siphonLiquid(liquid as Liquid?)
        }

        this.siphoning = false
      }
    }

    public override fun transBack() {
      if (this.config != null) {
        val parentBuild = this.parentMat!!.building
        val itsB = this.parentMat!!.getBuffer<ItemsBuffer>(DistBufferType.itemBuffer)
        val lisB = this.parentMat!!.getBuffer<LiquidsBuffer>(DistBufferType.liquidBuffer)
        this.items.each { item: Item?, amount: Int ->
          val move = if (parentBuild.acceptItem(this, item)) min(itsB.remainingCapacity(), amount) else 0
          if (move > 0) {
            this.items.remove(item, move)
            itsB.put(item, move)
            itsB.dePutFlow(item, move)
          }
        }
        this.liquids.each { liquid: Liquid?, amount: Float ->
          val move = if (parentBuild.acceptLiquid(this, liquid)) min(lisB.remainingCapacity(), amount) else 0.0f
          if (move > 0.0f) {
            this.liquids.remove(liquid, move)
            lisB.put(liquid!!, move)
            lisB.dePutFlow(liquid, move)
          }
        }
        this.outItems!!.each { item: Item?, amount: Int ->
          if (!this.config!!.get(GridChildType.output, ContentType.item)!!.contains(item)) {
            val move = if (parentBuild.acceptItem(this, item)) min(itsB.remainingCapacity(), amount) else 0
            if (move > 0) {
              this.outItems!!.remove(item, move)
              itsB.put(item, move)
              itsB.dePutFlow(item, move)
            }
          }
        }
        this.outLiquid!!.each { liquid: Liquid?, amount: Float ->
          if (!this.config!!.get(GridChildType.output, ContentType.liquid)!!.contains(liquid)) {
            val move = if (parentBuild.acceptLiquid(this, liquid)) min(lisB.remainingCapacity(), amount) else 0.0f
            if (move > 0.0f) {
              this.outLiquid!!.remove(liquid, move)
              lisB.put(liquid!!, move)
              lisB.dePutFlow(liquid, move)
            }
          }
        }
      }
    }

    override fun moveLiquid(next: Building?, liquid: Liquid?): Float {
      var next = next
      if (next != null) {
        next = next.getLiquidDestination(this, liquid)
        if (next.interactable(this.team) && next.block.hasLiquids && this.liquids.get(liquid) > 0.0f) {
          val ofract = next.liquids.get(liquid) / next.block.liquidCapacity
          val fract = this.liquids.get(liquid) / this.block.liquidCapacity * this.block.liquidPressure
          var flow = min(Mathf.clamp(fract - ofract) * this.block.liquidCapacity, this.liquids.get(liquid))
          flow = min(flow, next.block.liquidCapacity - next.liquids.get(liquid))
          if (flow > 0.0f && ofract <= fract && next.acceptLiquid(this, liquid)) {
            next.handleLiquid(this, liquid, flow)
            this.outLiquid!!.remove(liquid, flow)
            return flow
          }
        }
      }

      return 0.0f
    }

    fun siphonItem(item: Item?) {
      if (this.config != null) {
        val other = this.getNext("siphonItem") { e: Building? -> e!!.block.hasItems && e.items.has(item) && this.config!!.directValid(GridChildType.input, item, this.getDirectBit(e)) }
        if (other != null && this.interactable(other.team) && this.acceptItem(other, item)) {
          other.removeStack(item, 1)
          this.handleItem(other, item)
        }
      }
    }

    fun siphonLiquid(liquid: Liquid?) {
      if (this.config != null) {
        val other = this.getNext("siphonLiquid") { e: Building? -> e!!.block.hasLiquids && e.liquids.get(liquid) > 0.0f && this.config!!.directValid(GridChildType.input, liquid, this.getDirectBit(e)) }
        if (other != null && this.acceptLiquid(other, liquid)) {
          other.moveLiquid(this, liquid)
        }
      }
    }

    override fun acceptItem(source: Building, item: Item?): Boolean {
      return if (this.siphoning) {
        super.acceptItem(source, item)
      } else {
        this.config != null && this.config!!.directValid(GridChildType.acceptor, item, this.getDirectBit(source)) && this.config!!.get(GridChildType.acceptor, ContentType.item)!!.contains(item) && super.acceptItem(source, item)
      }
    }

    fun acceptItemOut(source: Building, item: Item?): Boolean {
      return this.interactable(source.team) && this.outItems!!.get(item) < this@GenericIOPoint.itemCapacity
    }

    override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      return if (this.siphoning) {
        super.acceptLiquid(source, liquid)
      } else {
        this.config != null && this.config!!.directValid(GridChildType.acceptor, liquid, this.getDirectBit(source)) && this.config!!.get(GridChildType.acceptor, ContentType.liquid)!!.contains(liquid) && super.acceptLiquid(source, liquid)
      }
    }

    fun acceptLiquidOut(source: Building, liquid: Liquid?): Boolean {
      return this.interactable(source.team) && this.outLiquid!!.get(liquid) < this@GenericIOPoint.liquidCapacity
    }

    override fun write(write: Writes) {
      super.write(write)
      this.outItems!!.write(write)
      this.outLiquid!!.write(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.outItems!!.read(read)
      this.outLiquid!!.read(read)
      if (revision < 3 && read.i() > 0) {
        this.config = TargetConfigure()
        this.config!!.read(read)
      }
    }
  }
}