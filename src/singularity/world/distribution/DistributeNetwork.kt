package singularity.world.distribution

import arc.Core
import arc.math.Mathf
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.OrderedSet
import arc.util.Time
import mindustry.gen.Building
import singularity.world.FinderContainerBase
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.components.distnet.DistNetworkCoreComp
import universecore.util.Empties
import universecore.util.colletion.TreeSeq
import kotlin.math.min

class DistributeNetwork : FinderContainerBase<DistElementBuildComp?>(), Iterable<DistElementBuildComp?> {
  var allElem: OrderedSet<DistElementBuildComp?> = OrderedSet<DistElementBuildComp?>()
  var elements: TreeSeq<DistElementBuildComp> = TreeSeq<DistElementBuildComp> { a: DistElementBuildComp, b: DistElementBuildComp -> if (b is DistNetworkCoreComp) 1 else b.priority - a.priority }
  var energyBuffer: OrderedSet<DistElementBuildComp> = OrderedSet<DistElementBuildComp>()
  private lateinit var elementsIterateArr: Array<DistElementBuildComp>
  var grids: TreeSeq<MatrixGrid?> = TreeSeq<MatrixGrid?> { a: MatrixGrid?, b: MatrixGrid? -> b!!.priority - a!!.priority }
  var cores: OrderedSet<DistNetworkCoreComp?> = OrderedSet<DistNetworkCoreComp?>()
  var vars: ObjectMap<String?, Any?> = ObjectMap<String?, Any?>()
  var topologyUsed: Int = 0
  var totalTopologyCapacity: Int = 0
  var energyProduct: Float = 0f
  var energyConsume: Float = 0f
  var energyBuffered: Float = 0f
  var energyCapacity: Float = 0f
  var energyStatus: Float = 0f
  private var structUpdated = true
  private var status = false
  private var lock = false
  private var handlingStat = false
  var frame: Long = 0

  fun putVar(key: String?, value: Any?) {
    vars.put(key, value)
  }

  fun <T> getVar(key: String?, def: T?): T? {
    return vars.get(key, def) as T?
  }

  fun <T> getVar(key: String?): T? {
    return getVar<T?>(key, null)
  }

  fun add(other: DistributeNetwork) {
    if (other !== this) {
      lock = true
      for (next in other) {
        add(next)
      }

      lock = false

      vars.putAll(other.vars)
      modified()
    }
  }

  override fun add(other: DistElementBuildComp?) {
    if (other == null || other.distributor.network == this) return

    elements.add(other)
    allElem.add(other)
    if (other.distBlock.matrixEnergyCapacity > 0) energyBuffer.add(other)
    if (other is DistNetworkCoreComp) cores.add(other)
    if (other is DistMatrixUnitBuildComp) grids.add(other.matrixGrid)

    other.distributor.setNet(this)
    modified()
  }

  fun netEfficiency(): Float {
    return if (netStructValid()) energyStatus else 0f
  }

  val core: DistNetworkCoreComp?
    get() = if (cores.size == 1) cores.first() else null

  fun netValid(): Boolean {
    return netStructValid() && energyStatus > 0.001f
  }

  fun netStructValid(): Boolean {
    val core = core
    val res = core != null && topologyUsed <= totalTopologyCapacity
    if (!res) status = false
    return res
  }

  fun update() {
    if (frame == Core.graphics.frameId) return
    frame = Core.graphics.frameId

    if (!status && netStructValid()) {
      status = true
      for (element in elementsIterateArr) {
        element.networkValided()
      }
      activityNetwork.add(this)
    }

    if (structUpdated) {
      for (element in elementsIterateArr) {
        element.networkUpdated()
      }
      structUpdated = false
    }

    handlingStat = true
    totalTopologyCapacity = 0
    val core = this.core
    if (core != null) {
      for (buffers in DistBufferType.all) {
        core.getBuffer(buffers).capacity = 0
      }

      core.distCore.calculatePower = 0
    }

    for (element in elements) {
      element.updateNetStat()
    }
    handlingStat = false

    updateEnergy()

    topologyUsed = 0

    for (element in elementsIterateArr) {
      topologyUsed += element.frequencyUse()
    }
  }

  fun handleTopologyCapacity(count: Int) {
    if (!handlingStat) return
    totalTopologyCapacity += count
  }

  fun handleBufferCapacity(type: DistBufferType<*>, count: Int) {
    if (!handlingStat) return
    val core = this.core ?: return

    core.getBuffer(type).capacity += count
  }

  fun handleCalculatePower(count: Int) {
    if (!handlingStat) return
    val core = this.core ?: return

    core.distCore.calculatePower += count
  }

  fun updateEnergy() {
    energyConsume = 0f
    energyProduct = energyConsume
    energyStatus = energyProduct
    energyBuffered = energyStatus
    energyCapacity = energyBuffered

    if (netStructValid()) {
      for (element in elementsIterateArr) {
        energyConsume += element.matrixEnergyConsume() * (if (element is Building) element.delta() else Time.delta)
      }
      for (element in elementsIterateArr) {
        energyProduct += element.matrixEnergyProduct() * (if (element is Building) element.delta() else Time.delta)
      }
      for (buff in energyBuffer) {
        energyBuffered += buff.matrixEnergyBuffered
        energyCapacity += buff.distBlock.matrixEnergyCapacity
      }
      var delta = if (energyBuffer.isEmpty()) 0f else min(energyCapacity - energyBuffered, energyProduct - energyConsume) / energyBuffer.size
      var counter = 0
      for (buff in energyBuffer) {
        counter++
        val origin = buff.matrixEnergyBuffered
        val set = Mathf.clamp(origin + delta, 0f, buff.distBlock.matrixEnergyCapacity)
        buff.matrixEnergyBuffered = (set)

        energyProduct -= set - origin
        delta += (delta - (set - origin)) / (energyBuffer.size - counter)
      }

      energyStatus = Mathf.clamp(energyProduct / energyConsume, 0f, 1f)
    }
  }

  fun modified() {
    if (lock) return

    elementsIterateArr = elements.toArray(EMP_ARR)
    structUpdated = true
  }

  override fun flow(origin: DistElementBuildComp?) {
    flow(origin, Empties.nilSetO<DistElementBuildComp?>())
  }

  fun flow(origin: DistElementBuildComp?, excl: ObjectSet<DistElementBuildComp?>?) {
    activityNetwork.remove(this)
    elements.clear()
    allElem.clear()
    grids.clear()
    cores.clear()

    restruct(origin, excl)
  }

  private fun restruct(origin: DistElementBuildComp?, excl: ObjectSet<DistElementBuildComp?>?) {
    activityNetwork.remove(this)
    excluded.clear()
    excluded.addAll(excl)
    lock = true
    super.flow(origin)
    lock = false

    modified()
  }

  fun remove(remove: DistElementBuildComp) {
    activityNetwork.remove(this)

    for (element in elementsIterateArr) {
      element.networkRemoved(remove)
    }

    tmp.clear()
    tmp.add(remove)
    for (other in elementsIterateArr) {
      if (other.distributor.network != this) continue

      DistributeNetwork().flow(other, tmp)
    }

    modified()
  }

  fun priorityModified(target: DistElementBuildComp?) {
    if (allElem.contains(target) && elements.remove(target)) {
      elements.add(target)
      elementsIterateArr = elements.toArray(EMP_ARR)
    }
    if (target is DistMatrixUnitBuildComp && grids.remove(target.matrixGrid)) grids.add(target.matrixGrid)
  }

  private val INST_ITR: Itr = Itr()

  override fun iterator(): Iterator<DistElementBuildComp> {
    INST_ITR.cursor = 0
    return INST_ITR
  }

  override fun getLinkVertices(distElementBuildComp: DistElementBuildComp?): Iterable<DistElementBuildComp>? {
    return distElementBuildComp!!.netLinked
  }

  override fun isDestination(distElementBuildComp: DistElementBuildComp?, vert1: DistElementBuildComp?): Boolean {
    return false
  }

  fun status(): NetStatus {
    if (netStructValid()) {
      if (netValid()) {
        for (value in this.core!!.buffers.values()) {
          if (value.space() <= 0) return NetStatus.bufferBlocked
        }
        for (task in this.core!!.distCore.requestTasks) {
          if (task!!.isBlocked) return NetStatus.requestBlocked
        }
        return NetStatus.ordinary
      }
      return NetStatus.energyLeak
    } else if (topologyUsed > totalTopologyCapacity) return NetStatus.topologyLeak

    return NetStatus.unknow
  }

  enum class NetStatus {
    ordinary,
    energyLeak,
    bufferBlocked,
    requestBlocked,
    topologyLeak,
    unknow;

    private val localized: String? = Core.bundle.get("status.$name")

    fun localized(): String? {
      return localized
    }
  }

  inner class Itr : Iterator<DistElementBuildComp> {
    var cursor: Int = 0

    override fun hasNext(): Boolean {
      return cursor < elementsIterateArr.size
    }

    override fun next(): DistElementBuildComp {
      return elementsIterateArr[cursor++]
    }
  }

  companion object {
    val activityNetwork: OrderedSet<DistributeNetwork?> = OrderedSet<DistributeNetwork?>()
    private val tmp = ObjectSet<DistElementBuildComp?>()
    val EMP_ARR = arrayOfNulls<DistElementBuildComp>(0)
  }
}