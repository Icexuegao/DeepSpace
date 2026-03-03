package ice.world.meta

import ice.ui.bundle.BaseBundle
import ice.ui.bundle.BaseBundle.Bundle.Companion.localizedName
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.meta.IceStatCats.create
import mindustry.world.meta.StatCat
import universecore.util.handler.FieldHandler

object IceStatCats {
  val 结构 = create("structure") {
    desc(zh_CN, "结构")
  }

  val neutron = create("neutron", 3){
    desc(zh_CN, "中子能")
  }
  val matrix = create("matrix", 5){
    desc(zh_CN, "矩阵")
  }
  val heat = create("heat", 6){
    desc(zh_CN, "热能")
  }
  val reaction = create("reaction")

  private fun create(name: String, index: Int =StatCat.all.size, desc: IceStatCat.() -> Unit ={}): IceStatCat {
    val all = StatCat.all
    val res = IceStatCat(name).apply {
      bundle {
        desc()
      }
    }

    all.remove(res)
    all.insert(index, res)

    for (i in 0..<all.size) {
      FieldHandler.setValueDefault(all.get(i), "id", i)
    }

    return res
  }

  class IceStatCat(name: String) : StatCat(name), BaseBundle.Bundle {
    override fun localized(): String = localizedName
  }
}