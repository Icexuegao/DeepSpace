package universecore.world.meta

import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat
import universecore.util.handler.FieldHandler

object UncStat {
  val inputs: Stat = create("inputs", StatCat.crafting)


  private fun create(name: String?, cat: StatCat?): Stat {
    return create(name, Stat.all.size, cat)
  }

  private fun create(name: String?, index: Int, cat: StatCat?): Stat {
    val all = Stat.all
    val res = Stat(name, cat)

    FieldHandler.setValueDefault(res, "id", index)
    all.insert(index, res)

    return res
  }
}