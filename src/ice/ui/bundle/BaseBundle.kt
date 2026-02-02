package ice.ui.bundle

import arc.Core
import arc.struct.Seq
import ice.library.world.Load
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent

 class BaseBundle(val name: String) {
  companion object : Load {
    fun bundle(bundle: Companion.() -> Unit) {
      bundle.invoke(Companion)
    }

    fun <T : UnlockableContent> T.desc(bundle: BaseBundle, name: String, desc: String = "", deta: String = "") {
      bundle.runBun.add {
        localizedName = name
        description = desc
        details = deta
      }
    }

    private val bundle = HashMap<String, BaseBundle>()
    override fun load() {
      IceStats.load()
      bundle["${Core.settings.getString("locale", "zh_CN")}"]?.load()
    }

    val zh_CN = BaseBundle("zh_CN")
  }

  private val runBun = Seq<Runnable>()

  init {
    bundle[name] = this
  }

  fun load() {
    runBun.forEach { it.run() }
  }

  interface Bundle {
    var localizedName: String
    fun desc(bundle: BaseBundle, name: String) {
      bundle.runBun.add {
        localizedName = name
      }
    }
  }
}