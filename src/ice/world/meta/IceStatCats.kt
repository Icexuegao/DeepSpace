package ice.world.meta

import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.ui.bundle.Bundle
import ice.ui.bundle.bundle
import mindustry.world.meta.StatCat
import universecore.util.handler.FieldHandler

object IceStatCats {
  val 结构 = create("structure") {
    desc(zh_CN, "结构")
  }
  val 流体传输 = create("liquidTransport") {
    desc(zh_CN, "流体传输")
  }
  val 其他 = create("other") {
    desc(zh_CN, "其他")
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

  class IceStatCat(name: String) : StatCat(name), Bundle {
    override fun localized(): String = getLocalizedName()
  }
}