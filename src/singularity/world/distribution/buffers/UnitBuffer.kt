package singularity.world.distribution.buffers

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.struct.OrderedSet
import arc.struct.Seq
import mindustry.ctype.UnlockableContent
import mindustry.graphics.Pal
import mindustry.type.PayloadStack
import mindustry.type.UnitType
import mindustry.world.blocks.payloads.Payload
import mindustry.world.blocks.payloads.UnitPayload
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.modules.PayloadModule

class UnitBuffer :BaseBuffer<PayloadStack, UnitPayload, UnitBuffer.UnitPacket>() {
  companion object {
    val temp: Seq<Payload> = Seq<Payload>()
  }

  private val tmp: UnitPacket = UnitPacket()

  override fun bufferType(): DistBufferType<*>? {
    return DistBufferType.unitBuffer
  }

  fun put(unit: UnitPayload) {
    tmp.payloads.clear()
    tmp.payloads.add(unit)
    tmp.obj!!.item = unit.content()
    tmp.obj!!.amount = 1
    put(tmp)
  }

  fun remove(unit: UnitPayload) {
    tmp.payloads.clear()
    tmp.payloads.add(unit)
    tmp.obj!!.item = unit.content()
    tmp.obj!!.amount = 1
    remove(tmp)
  }

  fun getAmount(type: UnitType): Int {
    val packet = get<UnitPacket?>(type.id.toInt())
    return if (packet == null) 0 else packet.obj!!.amount
  }

  override fun deReadFlow(ct: UnitPayload, amount: Number) {
    tmp.payloads.clear()
    tmp.payloads.add(ct)
    tmp.obj!!.item = ct.content()
    tmp.obj!!.amount = amount.toInt()

    deReadFlow(tmp)
  }

  override fun dePutFlow(ct: UnitPayload, amount: Number) {
    tmp.payloads.clear()
    tmp.payloads.add(ct)
    tmp.obj!!.item = ct.content()
    tmp.obj!!.amount = amount.toInt()

    dePutFlow(tmp)
  }

  fun take(): UnitPayload? {
    if (memory.values().hasNext()) {
      val p = memory.values().next()
      if (!p.isEmpty) {
        return p.take()
      }
    }
    return null
  }

  fun peek(): UnitPayload? {
    if (memory.values().hasNext()) {
      val p = memory.values().next()
      if (!p.isEmpty) {
        return p.get()
      }
    }
    return null
  }

  fun peekPacket(): UnitPacket? {
    if (memory.values().hasNext()) {
      return memory.values().next()
    }
    return null
  }

  override fun remainingCapacity(): Int? {
    return super.remainingCapacity() as Int?
  }

  //no container usable, only buffer
  override fun bufferContAssign(network: DistributeNetwork?) {}
  override fun bufferContAssign(network: DistributeNetwork?, ct: UnitPayload?) {}
  override fun bufferContAssign(network: DistributeNetwork?, ct: UnitPayload?, amount: Number?): Int {
    return 0
  }

  override fun bufferContAssign(network: DistributeNetwork?, ct: UnitPayload?, amount: Number?, deFlow: Boolean): Int {
    return 0
  }

  override fun generateBindModule(): PayloadModule {
    return UnitBufferModule()
  }

  override fun localization(): String? {
    return Core.bundle.get("misc.unit")
  }

  override fun displayColor(): Color? {
    return Pal.accent
  }

  override fun usedCapacity(): Int? {
    return super.usedCapacity() as Int?
  }

  inner class UnitPacket :Packet<PayloadStack, UnitPayload> {
    var payloads: OrderedSet<UnitPayload> = OrderedSet<UnitPayload>()

    constructor()

    constructor(units: Seq<UnitPayload>, amount: Int) {
      payloads.addAll(units)
      var t: UnitType? = null
      for(payload in units) {
        if (t == null) t = payload.unit.type()
        else require(t === payload.unit.type()) { "cannot put two type to a same packet" }
      }
      obj = PayloadStack(t, amount)
    }

    override fun id(): Int {
      return obj!!.item.id.toInt()
    }

    override fun get(): UnitPayload? {
      return payloads.orderedItems().peek()
    }

    fun take(): UnitPayload? {
      val res = get()
      remove(res)

      return res
    }

    override fun color(): Color? {
      return Pal.accent
    }

    override fun localization(): String? {
      return obj!!.item.localizedName
    }

    override fun icon(): TextureRegion? {
      return obj!!.item.fullIcon
    }

    override fun occupation(): Int {
      return obj!!.amount * bufferType()!!.unit()
    }

    override fun amount(): Int {
      return obj!!.amount
    }

    override fun setZero() {
      payloads.clear()
      readCaching += obj!!.amount
      obj!!.amount = 0
    }

    protected override fun merge(other: Packet<PayloadStack?, UnitPayload?>) {
      if (other.id() == id()) {
        val i = payloads.size
        payloads.addAll((other as UnitPacket).payloads)
        val o = payloads.size - i
        obj!!.amount += o
        putCaching += o * bufferType()!!.unit()
      }
    }

    protected override fun remove(other: Packet<PayloadStack?, UnitPayload?>) {
      if (other.id() == id()) {
        val i = payloads.size
        payloads.removeAll((other as UnitPacket).payloads.orderedItems())
        val o = i - payloads.size
        obj!!.amount -= o
        readCaching += o * bufferType()!!.unit()
      }
    }

    fun put(unit: UnitPayload?) {
      tmp.payloads.clear()
      tmp.payloads.add(unit)
      tmp.obj!!.item = obj!!.item
      tmp.obj!!.amount = 1
      this@UnitBuffer.put(tmp)
    }

    fun remove(unit: UnitPayload?) {
      tmp.payloads.clear()
      tmp.payloads.add(unit)
      tmp.obj!!.item = obj!!.item
      tmp.obj!!.amount = 1
      this@UnitBuffer.remove(tmp)
    }

    fun deRead(amount: Int) {
      tmp.payloads.clear()
      tmp.obj!!.item = obj!!.item
      tmp.obj!!.amount = amount
      this@UnitBuffer.deReadFlow(tmp)
    }

    fun dePut(amount: Int) {
      tmp.payloads.clear()
      tmp.obj!!.item = obj!!.item
      tmp.obj!!.amount = amount
      this@UnitBuffer.dePutFlow(tmp)
    }

    override fun copy(): Packet<PayloadStack, UnitPayload> {
      return UnitPacket(payloads.orderedItems(), obj!!.amount)
    }
  }

  inner class UnitBufferModule :PayloadModule() {
    override fun total(): Int {
      return usedCapacity()!!
    }

    override fun add(payload: Payload?) {
      if (payload is UnitPayload) {
        put(payload)
      }
    }

    override fun amountOf(type: UnlockableContent): Int {
      val packet = this@UnitBuffer.get<UnitPacket?>(type.id.toInt())
      return packet?.amount() ?: 0
    }

    override fun take(): Payload? {
      return this@UnitBuffer.take()
    }

    override fun get(): Payload? {
      return this@UnitBuffer.peek()
    }

    override fun get(type: UnlockableContent): Payload? {
      val packet = this@UnitBuffer.get<UnitPacket?>(type.id.toInt())
      return if (packet == null) null else packet.get()
    }

    override fun remove(type: UnlockableContent): Payload? {
      val packet = this@UnitBuffer.get<UnitPacket?>(type.id.toInt())
      return if (packet == null) null else packet.take()
    }

    override fun removeAll(type: UnlockableContent) {
      for(packet in this@UnitBuffer) {
        packet.setZero()
      }
    }

    override fun isEmpty(): Boolean {
      return this@UnitBuffer.usedCapacity()!! <= 0
    }

    override fun iterate(): Iterable<Payload?> {
      temp.clear()
      for(packet in this@UnitBuffer) {
        temp.addAll(packet.payloads)
      }
      return temp
    }
  }
}