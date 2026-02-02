package ice.world.meta

import ice.ui.bundle.BaseBundle
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import mindustry.world.meta.StatCat

object IceStatCats {
  val 结构 = getStat("structure") {
    desc(zh_CN, "结构")
  }

  private fun getStat(name: String, cat: StatCat = StatCat.general, desc: IceStatCat.() -> Unit): IceStatCat {
    return IceStatCat(name).apply {
      bundle {
        desc()
      }
    }
  }

  class IceStatCat(name: String) : StatCat(name), BaseBundle.Bundle {
    override var localizedName: String = name
    override fun localized(): String=localizedName
  }
}