package ice.ui.bundle

import arc.Core
import arc.struct.Seq
import ice.library.struct.AttachedProperty
import ice.library.world.Load
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent

class BaseBundle(val name: String) {
  companion object :Load {

    private val bundle = HashMap<String, BaseBundle>()
    var initializer = false

    override fun load() {
      IceStats.load()
      bundle["${Core.settings.getString("locale", "zh_CN")}"]?.load()
      initializer = true
    }

    val zh_CN = BaseBundle("zh_CN")
  }

  val runBun = Seq<() -> Unit>()

  init {
    bundle[name] = this
  }

  fun load() {
    runBun.forEach { it() }
    runBun.clear()
  }

}

fun bundle(bundle: BaseBundle.Companion.() -> Unit) {
  bundle.invoke(BaseBundle.Companion)
}

private var Bundle.localizedName: String by AttachedProperty("")
private var Bundle.description: String by AttachedProperty("")

interface Bundle {

  fun getLocalizedName()=localizedName
  fun setLocalizedName(localizedName: String) {
    this.localizedName = localizedName
  }
  fun getDescription()=description
  fun desc(bundle: BaseBundle, localizedName: String, description: String = "") {
    val d = {
      this.localizedName = localizedName
      this.description = description
    }
    if (BaseBundle.initializer) {
      d()
    } else {
      bundle.runBun.add(d)
    }
  }
}

fun <T :UnlockableContent> T.desc(bundle: BaseBundle, name: String, desc: String = "", deta: String = "") {
  bundle.runBun.add {
    localizedName = name
    description = desc
    details = deta
  }
}