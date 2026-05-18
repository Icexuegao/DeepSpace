package universecore.world.logic

import arc.struct.Seq
import mindustry.logic.LAccess
import universe.util.reflect.accessEnum2
import universe.util.reflect.accessField

object UncLAccess {
  val config2 = LAccess::class.accessEnum2<LAccess, Boolean, Array<String>>().appendEnumInstance("config2", true, arrayOf("to"))
  var senseable: Array<LAccess> by LAccess::class.accessField("senseable")
  var controls: Array<LAccess> by LAccess::class.accessField("controls")
  var all: Array<LAccess> by LAccess::class.accessField("all")

  fun setup() {
    all = LAccess.entries.toTypedArray()
    senseable = Seq.select(LAccess.entries.toTypedArray()) { it.params.size <= 1 }.toArray(LAccess::class.java)
    controls = Seq.select(LAccess.entries.toTypedArray()) { it.params.size > 0 }.toArray(LAccess::class.java)
  }
}