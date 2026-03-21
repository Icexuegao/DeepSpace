package universecore.world.meta

import mindustry.world.meta.StatCat
import universecore.util.handler.FieldHandler

object UncStatCat {
  val other: StatCat = create("other")

  private fun create(name: String?, index: Int = StatCat.all.size): StatCat {
    val all = StatCat.all
    val res = StatCat(name)

    FieldHandler.setValueDefault(res, "id", index)
    all.insert(index, res)

    return res
  }
}