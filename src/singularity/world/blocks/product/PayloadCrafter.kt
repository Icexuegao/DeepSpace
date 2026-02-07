package singularity.world.blocks.product

import arc.func.Boolf
import arc.func.Cons
import arc.func.Intf
import arc.graphics.g2d.Draw
import arc.util.Structs
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Building
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.PayloadSeq
import mindustry.world.blocks.payloads.Payload
import mindustry.world.meta.BlockGroup
import singularity.world.components.PayloadBuildComp
import singularity.world.components.PayloadBuildComp.Companion.temp
import singularity.world.draw.DrawPayloadFactory
import singularity.world.modules.PayloadModule
import universecore.world.consumers.ConsumeItemBase
import universecore.world.consumers.ConsumeType
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import kotlin.math.abs

open class PayloadCrafter(name: String) : NormalCrafter(name) {
  var itemCapacityMulti: Float = 2.0f
  var payloadCapacity: Int = 1
  var payloadSpeed: Float = 0.7f
  var payloadRotateSpeed: Float = 5.0f

  init {
    this.draw = object : DrawPayloadFactory<PayloadCrafterBuild>() {
      init {
        this.spliceBits = Intf{it.blendBit}
        this.drawPayload = Cons { e: PayloadCrafterBuild? ->
          e!!.drawConstructingPayload()
          e.drawPayload()
        }
      }
    }
    this.outputFacing = true
    this.outputsPayload = true
    this.rotate = true
    this.group = BlockGroup.payloads
    this.envEnabled = this.envEnabled or 6
  }

  open inner class PayloadCrafterBuild : NormalCrafterBuild(), PayloadBuildComp {
    override var payloads = PayloadModule()
    override var carried: Boolean = false
    override var outputLocking: Boolean = false
    override var stackAlpha: Float = 0f
    override var blendBit: Int = 0
    override var outputting: Payload? = null
    override var payloadCapacity: Int
      get() = this@PayloadCrafter.payloadCapacity
      set(value) {}
    override var payloadSpeed: Float
      get() = this@PayloadCrafter.payloadSpeed
      set(value) {}
    override var payloadRotateSpeed: Float
      get() = this@PayloadCrafter.payloadRotateSpeed
      set(value) {}
    override var inputting: Payload? = null

    override fun acceptUnitPayload(unit: Unit): Boolean {
      return this.inputting() == null && !this.consumer.hasConsume() || this@PayloadCrafter.consFilter.filter(this, ConsumeType.Companion.payload, unit.type, true)
    }

    override fun canControlSelect(unit: Unit): Boolean {
      return this@PayloadCrafter.acceptsPayload && !unit.spawnedByCore && unit.type.allowedInPayloads && this.payloads()!!.isEmpty() && this.acceptUnitPayload(unit) && unit.tileOn() != null && unit.tileOn().build === this
    }

    public override fun onControlSelect(player: Unit?) {
      this.handleUnitPayload(player, Cons { p: Payload? -> this.payloads()!!.add(p) })
    }

    public override fun shouldConsume(): Boolean {
      if (!super.shouldConsume()) {
        return false
      } else {
        return this.outputting() == null || abs(this.outputting()!!.x() - this.x) >= (this@PayloadCrafter.size * 8).toFloat() / 2.0f + 1.0f || abs(this.outputting()!!.y() - this.y) >= (this@PayloadCrafter.size * 8).toFloat() / 2.0f + 1.0f
      }
    }

    override fun acceptPayload(source: Building?, payload: Payload): Boolean {
      return (source === this || this@PayloadCrafter.acceptsPayload && this.inputting() == null && (!this.consumer.hasConsume() || this@PayloadCrafter.consFilter.filter(this, ConsumeType.payload, payload.content(), true))) && this.payloads()!!.total() < this.payloadCapacity()
    }

    public override fun sense(sensor: LAccess?): Double {
      return if (sensor == LAccess.payloadCount) this.payloads()!!.total().toDouble() else super.sense(sensor)
    }

    public override fun craftTrigger() {
      super.craftTrigger()
      if (!this.payloads()!!.isEmpty()) {
        this.getPayload()!!.set(this.x, this.y, this.rotdeg())
      }
    }

   open fun drawConstructingPayload() {
      var p: ProducePayload<*>?=null
      if (this.producer!!.current != null && (this.producer!!.current!!.get(ProduceType.payload).also { p = it }) != null) {
        Draw.draw(35.0f){
          Drawf.construct(this, p!!.payloads[0].item, this.rotdeg() - 90.0f, this.progress(), this.workEfficiency(), this.totalProgress()) }
      }
    }

    public override fun acceptItem(source: Building, item: Item?): Boolean {
      val stack: ItemStack?
      return source.interactable(this.team) && this@PayloadCrafter.hasItems && (source === this || !this.consumer.hasConsume() && !this.consumer.hasOptional() || this@PayloadCrafter.consFilter.filter(this, ConsumeType.item, item, this.acceptAll(ConsumeType.item))) && this.items.get(item).toFloat() < (if (((Structs.find<ItemStack>((this.consumer.current!!.get<ConsumeItemBase<*>>(ConsumeType.item) as ConsumeItemBase<*>).consItems, Boolf { e: ItemStack? -> e!!.item === item }) as ItemStack).also {
          stack = it
        }) != null) stack!!.amount.toFloat() * this@PayloadCrafter.itemCapacityMulti else 0.0f)
    }

    fun payloadCapacity(): Int {
      return this@PayloadCrafter.payloadCapacity
    }

    fun payloadSpeed(): Float {
      return this@PayloadCrafter.payloadSpeed
    }

    fun payloadRotateSpeed(): Float {
      return this@PayloadCrafter.payloadRotateSpeed
    }

    fun inputting(): Payload? {
      return this.inputting
    }

    fun inputting(payload: Payload?) {
      this.inputting = payload
    }

    fun outputting(): Payload? {
      return this.outputting
    }

    fun outputting(payload: Payload?) {
      this.outputting = payload
    }

    fun blendBit(): Int {
      return this.blendBit
    }

    fun blendBit(bit: Int) {
      this.blendBit = bit
    }

    fun stackAlpha(): Float {
      return this.stackAlpha
    }

    fun stackAlpha(alpha: Float) {
      this.stackAlpha = alpha
    }

    fun outputLocking(): Boolean {
      return this.outputLocking
    }

    fun outputLocking(locking: Boolean) {
      this.outputLocking = locking
    }

    fun carried(): Boolean {
      return this.carried
    }

    fun carried(carried: Boolean) {
      this.carried = carried
    }

    fun payloads(): PayloadModule? {
      return this.payloads
    }

    override fun onRemoved() {
      super.onRemoved()
      this.payloadBuildRemoved()
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      this.payloadProximityUpdated()
    }

    override fun pickedUp() {
      super.pickedUp()
      this.payloadPickedUp()
    }

    override fun drawTeamTop() {
      super.drawTeamTop()
      this.drawTeamTopEntry()
    }

    override fun getPayloads(): PayloadSeq {
      temp.clear()
      for (payload in payloads.iterate()) {
        temp.add(payload.content())
      }
      return temp
    }

    override fun takePayload(): Payload? {
      return super<PayloadBuildComp>.takePayload()
    }

    override fun getPayload(): Payload? {
      return payloads()?.get()
    }

    public override fun updateTile() {
      super.updateTile()
      this.updatePayloads()
    }

    override fun handlePayload(source: Building?, payload: Payload?) {
      super<NormalCrafterBuild>.handlePayload(source, payload)
      super<PayloadBuildComp>.handlePayload(source, payload)
    }

    public override fun write(write: Writes) {
      super.write(write)
      this.writePayloads(write)
    }

    public override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.readPayloads(read, revision)
    }
  }
}