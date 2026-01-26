package singularity.world.distribution

import arc.func.Boolf2
import arc.func.Cons2
import arc.func.Prov
import arc.math.geom.Point2
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.Seq
import mindustry.Vars
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.world.blocks.storage.CoreBlock
import singularity.Sgl
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import universecore.util.colletion.TreeSeq

class MatrixGrid(val owner: DistMatrixUnitBuildComp) {
  val all: ObjectMap<Building, BuildingEntry<*>> = ObjectMap<Building, BuildingEntry<*>>()

  val output: TreeSeq<BuildingEntry<*>> = TreeSeq<BuildingEntry<*>>(Comparator { a: BuildingEntry<*>?, b: BuildingEntry<*>? -> b!!.config.priority - a!!.config.priority })
  val input: TreeSeq<BuildingEntry<*>> = TreeSeq<BuildingEntry<*>>(Comparator { a: BuildingEntry<*>?, b: BuildingEntry<*>? -> b!!.config.priority - a!!.config.priority })
  val acceptor: TreeSeq<BuildingEntry<*>> = TreeSeq<BuildingEntry<*>>(Comparator { a: BuildingEntry<*>?, b: BuildingEntry<*>? -> b!!.config.priority - a!!.config.priority })
  val container: TreeSeq<BuildingEntry<*>> = object : TreeSeq<BuildingEntry<*>>(Comparator { a: BuildingEntry<*>, b: BuildingEntry<*> -> b.config.priority - a.config.priority }) {
    override fun add(item: BuildingEntry<*>) {
      super.add(item)
      val cont = Sgl.matrixContainers.getContainer((item.entity as Building).block)
      if (cont == null) return
      for (entry in cont.capacities) {
        containerCapacities.get(entry.key, Prov { FloatArray(1) })!![0] += entry.value
      }
    }

    override fun remove(item: BuildingEntry<*>): Boolean {
      val res = super.remove(item)
      val cont = Sgl.matrixContainers.getContainer((item.entity as Building).block)
      if (cont == null) return res
      for (entry in cont.capacities) {
        containerCapacities.get(entry.key, Prov { FloatArray(1) })!![0] -= entry.value
      }

      return res
    }
  }
  val containerCapacities: ObjectMap<DistBufferType<*>?, FloatArray?> = ObjectMap<DistBufferType<*>?, FloatArray?>()

  val containerUsed: ObjectMap<DistBufferType<*>?, FloatArray> = ObjectMap<DistBufferType<*>?, FloatArray>()
  var statUsed: Boolean = false

  var priority: Int = 0

  fun update() {
    for (bu in all.keys()) {
      if (!(bu is CoreBlock.CoreBuild && bu.isAdded()) && bu.tile.build !== bu) {
        remove(bu)
      }
    }

    for (used in containerUsed.values()) {
      used[0] = 0f
    }
    if (statUsed) {
      for (entry in container) {
        val cont = Sgl.matrixContainers.getContainer((entry.entity as Building).block)
        if (cont == null) continue
        for (key in cont.capacities.keys()) {
          containerUsed.get(key, Prov { FloatArray(1) })[0] += key.containerUsed(entry.entity).toFloat()
        }
      }
    }
  }

  fun eachUsed(cons: Cons2<DistBufferType<*>?, Float?>) {
    for (entry in containerUsed) {
      cons.get(entry.key, entry.value[0])
    }
  }

  fun eachCapacity(cons: Cons2<DistBufferType<*>?, Float?>) {
    for (entry in containerCapacities) {
      cons.get(entry.key, entry.value!![0])
    }
  }

  fun contUsed(buff: DistBufferType<*>?): Float {
    return containerUsed.get(buff, DEF_VALUE)[0]
  }

  fun contCapacity(buff: DistBufferType<*>?): Float {
    return containerCapacities.get(buff, DEF_VALUE)!![0]
  }

  fun startStatContainer() {
    statUsed = true
  }

  fun endStatContainer() {
    statUsed = false
  }

  fun <T> get(type: GridChildType): Seq<BuildingEntry<T>> {
    return get(type, REQ, tmp)
  }

  fun <T> get(type: GridChildType, req: Boolf2<Building?, TargetConfigure?>): Seq<BuildingEntry<T>> {
    return get(type, req, tmp)
  }

  fun <T> get(type: GridChildType, req: Boolf2<Building?, TargetConfigure?>, temp: Seq<BuildingEntry<Building?>?>): Seq<BuildingEntry<T>> {
    temp.clear()
    each(type, req, Cons2 { e: T, entry: TargetConfigure? -> temp.add(all.get(e as Building?) as BuildingEntry<Building?>) })
    return temp as Seq<BuildingEntry<T>>
  }

  fun <T> each(type: GridChildType, req: Boolf2<Building?, TargetConfigure?>, cons: Cons2<T, TargetConfigure>) {
    val temp: TreeSeq<BuildingEntry<*>> = when (type) {
      GridChildType.output -> output
      GridChildType.input -> input
      GridChildType.acceptor -> acceptor
      GridChildType.container -> container
    }

    for (entry in temp) {
      if (req.get(entry.entity as T? as Building?, entry.config)) cons.get(entry.entity, entry.config)
    }
  }

  fun addConfig(c: TargetConfigure) {
    val t = Vars.world.build(owner.tile!!.x + Point2.x(c.offsetPos), owner.tile!!.y + Point2.y(c.offsetPos))
    if (t == null || !owner.tileValid(t.tile)) return
    val existed = all.containsKey(t)
    val entry: BuildingEntry<*> = all.get(t, BuildingEntry<Building?>(t, c))!!

    c.eachChildType(Cons2 { type: GridChildType, map: ObjectMap<ContentType, ObjectSet<UnlockableContent>> ->
      val temp: TreeSeq<BuildingEntry<*>> = when (type) {
        GridChildType.output -> output
        GridChildType.input -> input
        GridChildType.acceptor -> acceptor
        GridChildType.container -> container
      }
      if (existed) {
        entry.config.priority = priority
        temp.remove(entry)
      }
      temp.add(entry)
    })
    all.put(t, entry)
  }

  fun remove(building: Building?): Boolean {
    if (building == null) return false
    val entry = all.remove(building)
    if (entry != null) {
      output.remove(entry)
      input.remove(entry)
      acceptor.remove(entry)
      container.remove(entry)
      return true
    }
    return false
  }

  fun clear() {
    for (building in all.keys()) {
      remove(building)
    }
  }

  override fun toString(): String {
    return all.toString()
  }

  class BuildingEntry<T>(val entity: T, var config: TargetConfigure)
  companion object {
    private val tmp: Seq<BuildingEntry<Building?>?> = Seq<BuildingEntry<Building?>?>()
    val DEF_VALUE: FloatArray = floatArrayOf(0f)
    val REQ: Boolf2<Building?, TargetConfigure?> = Boolf2 { e: Any, c: TargetConfigure -> true }
  }
}