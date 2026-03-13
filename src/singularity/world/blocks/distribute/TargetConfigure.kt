package singularity.world.blocks.distribute

import arc.func.Cons
import arc.func.Cons2
import arc.func.Cons3
import arc.func.Prov
import arc.math.geom.Point2
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import singularity.world.distribution.GridChildType
import universecore.util.DataPackable
import universecore.util.Empties

open class TargetConfigure : DataPackable {
  @JvmField
  var offsetPos: Int = Point2.pack(0, 0)
  @JvmField
  var priority: Int = 0

  protected var data = ObjectMap<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>>()
  protected var directBits = ObjectMap<GridChildType, ObjectMap<UnlockableContent, ByteArray>>()

  fun set(type: GridChildType?, content: UnlockableContent, dirBit: ByteArray?) {
    data.get(type) {ObjectMap()}.get(content.contentType) {ObjectSet()}.add(content)
    directBits.get(type) {ObjectMap()}.put(content, dirBit)
  }

  fun remove(type: GridChildType?, content: UnlockableContent): Boolean {
    val result = data.get(type, Empties.nilMapO()).get(content.contentType, Empties.nilSetO()).remove(content)
    directBits.get(type, Empties.nilMapO()).remove(content)
    return result
  }

  fun get(type: GridChildType?, content: UnlockableContent): Boolean {
    return data.get(type, Empties.nilMapO()).get(content.contentType, Empties.nilSetO()).contains(content)
  }

  fun each(cons: Cons3<GridChildType?, ContentType?, UnlockableContent?>) {
    for (entry in data) {
      for (contEntry in entry.value) {
        for (content in contEntry.value) {
          cons.get(entry.key, contEntry.key, content)
        }
      }
    }
  }

  fun eachChildType(cons: Cons2<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>>) {
    for (entry in data) {
      for (value in entry.value.values()) {
        if (!value.isEmpty) {
          cons.get(entry.key, entry.value)
          break
        }
      }
    }
  }

  fun getDirectBit(type: GridChildType?, content: UnlockableContent?): ByteArray? {
    return directBits.get(type, Empties.nilMapO()).get(content, ByteArray(1))
  }

  fun directValid(type: GridChildType?, content: UnlockableContent?, match: Byte): Boolean {
    val bit = getDirectBit(type, content)!![0]
    if (bit <= 0 || match <= 0) return false
    return (bit.toInt() and match.toInt()) != 0
  }

  fun get(type: GridChildType?, t: ContentType?): ObjectSet<UnlockableContent>? {
    return data.get(type, Empties.nilMapO()).get(t, Empties.nilSetO())
  }

  fun get(): ObjectMap<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>> {
    clip()
    return data
  }

  fun any(): Boolean {
    for (conts in data.values()) {
      for (cont in conts.values()) {
        if (!cont.isEmpty) return true
      }
    }
    return false
  }

  fun clip() {
    for (entry in data) {
      if (entry.value != null) {
        if (entry.value.isEmpty) data.remove(entry.key)
        else {
          for (setEntry in entry.value) {
            if (setEntry.value != null && setEntry.value.isEmpty) entry.value.remove(setEntry.key)
          }
        }
      }
    }
  }

  val isContainer: Boolean
    get() {
      for (type in data.keys()) {
        if (type == GridChildType.container) {
          return true
        }
      }
      return false
    }

  override fun typeID(): Long {
    return typeID
  }

  override fun write(write: Writes) {
    write.i(offsetPos)
    write.i(priority)

    write.i(data.size)
    for (entry in data) {
      write.i(entry.key!!.ordinal)
      write.i(entry.value.size)
      for (dataEntry in entry.value) {
        write.i(dataEntry.key!!.ordinal)
        write.i(dataEntry.value.size)
        for (v in dataEntry.value) {
          write.i(v!!.id.toInt())
        }
      }
    }

    write.i(directBits.size)
    for (entry in directBits) {
      write.i(entry.key!!.ordinal)
      write.i(entry.value.size)
      for (cEntry in entry.value) {
        write.i(cEntry.key!!.contentType.ordinal)
        write.i(cEntry.key!!.id.toInt())
        write.b(cEntry.value!![0].toInt())
      }
    }
  }

  override fun read(read: Reads) {
    offsetPos = read.i()
    priority = read.i()

    data = ObjectMap<GridChildType, ObjectMap<ContentType, ObjectSet<UnlockableContent>>>()
    val count = read.i()
    var count2: Int
    var amount: Int
    for (i in 0..<count) {
      val map = data.get(GridChildType.entries[read.i()]) {ObjectMap()}
      count2 = read.i()
      for (l in 0..<count2) {
        val type: ContentType = ContentType.entries[read.i()]
        val set = map.get(type) {ObjectSet()}
        amount = read.i()
        for (i1 in 0..<amount) {
          set.add(Vars.content.getByID(type, read.i()))
        }
      }
    }

    directBits = ObjectMap<GridChildType, ObjectMap<UnlockableContent, ByteArray>>()
    val size = read.i()
    var length: Int
    for (i in 0..<size) {
      val map = directBits.get(GridChildType.entries[read.i()]) {ObjectMap()}
      length = read.i()
      for (l in 0..<length) {
        val typeId = read.i()
        map.get(Vars.content.getByID(ContentType.entries[typeId], read.i())) {byteArrayOf(read.b())}
      }
    }
  }

  fun rotateDir(direction: Int) {
    for (bitMap in directBits.values()) {
      for (arr in bitMap.values()) {
        var bits = arr!![0].toInt()
        if (direction >= 0) {
          bits = bits shl 1
          if ((bits and (1 shl 4)) != 0) {
            bits = ((bits or 1) and 15)
          }
        } else {
          val b = (bits and 1) != 0
          bits = bits shr 1
          if (b) bits = bits or (1 shl 3)
        }
        arr[0] = bits.toByte()
      }
    }
  }

  fun getDirection(type: GridChildType?, content: UnlockableContent?): Int {
    val dirBit = getDirectBit(type, content) ?: return -1
    val bits = dirBit[0].toInt()

    if (bits <= 0) return -1

    // 检查是否只有一个方向被激活
    var direction = -1
    var count = 0

    for (i in 0..<4) {
      if ((bits and (1 shl i)) != 0) {
        direction = i
        count++
      }
    }

    // 如果只有一个方向，返回该方向；否则返回 -1 表示多方向
    return if (count == 1) direction else -1
  }
  fun getDirections(type: GridChildType?, content: UnlockableContent?): IntArray {
    val dirBit = getDirectBit(type, content) ?: return intArrayOf()
    val bits = dirBit[0].toInt()

    if (bits <= 0) return intArrayOf()

    val directions = mutableListOf<Int>()
    for (i in 0..<4) {
      if ((bits and (1 shl i)) != 0) {
        directions.add(i)
      }
    }

    return directions.toIntArray()
  }

  fun flip(x: Boolean) {
    for (bitMap in directBits.values()) {
      for (arr in bitMap.values()) {
        var bits = arr!![0].toInt()

        if (x) {
          if ((bits and FLIP_X) != 0 && (bits and FLIP_X) != FLIP_X) {
            bits = bits xor FLIP_X
          }
        } else {
          if ((bits and FLIP_Y) != 0 && (bits and FLIP_Y) != FLIP_Y) {
            bits = bits xor FLIP_Y
          }
        }

        arr[0] = bits.toByte()
      }
    }
  }

  fun clear() {
    priority = 0
    data.clear()
    directBits.clear()
  }

  val isClear: Boolean
    get() {
      if (data.isEmpty) return true
      for (map in data.values()) {
        for (value in map.values()) {
          if (!value.isEmpty) return false
        }
      }
      return true
    }

  override fun toString(): String {
    return "TargetConfigure{" +
            "position=" + Point2.unpack(offsetPos) +
            ", priority=" + priority +
            ", data=" + data +
            ", directBits=" + directBits +
            '}'
  }

  fun clone(): TargetConfigure {
    val conf = TargetConfigure()
    conf.read(pack())
    return conf
  }

  fun configHandle(transformer: Cons<Point2>) {
    val res = Point2.unpack(offsetPos)
    transformer.get(res)
    offsetPos = res.pack()

    val t1 = Point2(4, 0)
    transformer.get(t1)

    when (t1.x) {
      0 if t1.y > 0 -> {
        rotateDir(1)
      }
      0 if t1.y < 0 -> {
        rotateDir(-1)
      }
      else -> flip(t1.x < 0 && t1.y == 0)
    }
  }

  companion object {
    const val typeID: Long = 6253491887543618527L
    const val FLIP_X: Int = 5
    const val FLIP_Y: Int = 10
  }
}