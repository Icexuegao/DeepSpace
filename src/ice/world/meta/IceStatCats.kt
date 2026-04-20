package ice.world.meta

import ice.ui.bundle.Localizable
import mindustry.world.meta.StatCat
import universecore.util.handler.FieldHandler

object IceStatCats {
  val 结构 = create("structure") {
    localization { zh_CN { name = "结构" } }
  }
  val 流体传输 = create("liquidTransport") {
    localization { zh_CN { name = "流体传输" } }
  }
  val 其他 = create("other") {
    localization { zh_CN { name = "其他" } }
  }

  val neutron = create("neutron", 3) {
    localization { zh_CN { name = "中子能" } }
  }
  val matrix = create("matrix", 5) {
    localization { zh_CN { name = "矩阵" } }
  }
  val heat = create("heat", 6) {
    localization { zh_CN { name = "热能" } }
  }
  val reaction = create("reaction")

  private fun create(name: String, index: Int = StatCat.all.size, desc: IceStatCat.() -> Unit = {}): IceStatCat {
    val all = StatCat.all
    val res = IceStatCat(name)
    desc(res)
    all.remove(res)
    all.insert(index, res)

    for(i in 0..<all.size) {
      FieldHandler.setValueDefault(all.get(i), "id", i)
    }

    return res
  }

  class IceStatCat(name: String) :StatCat(name), Localizable {
    override fun localized() = localizedName
    @JvmField var localizedName = name
    @JvmField var description = ""
    @JvmField var details = ""
    override fun setLocalizedName(localizedName: String) {
      this.localizedName = localizedName
    }

    override fun setDescription(description: String) {
      this.description = description
    }

    override fun setDetails(details: String) {
      this.details = details
    }
  }
}