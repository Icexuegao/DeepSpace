package ice.ui

import arc.Core
import arc.struct.Seq
import ice.library.meta.stat.IceStats

open class BaseBundle(val name: String) {
    companion object {
        fun <T> T.bundle(bundle: Companion.() -> Unit) {
            bundle.invoke(Companion)
        }

        val bundle = HashMap<String, BaseBundle>()
        fun load() {
            IceStats.load()
            bundle["${Core.settings.getString("locale", "zh_CN")}"]?.load()
        }

        val zh_CN = BaseBundle("zh_CN")
    }

    val runBun = Seq<Runnable>()

    init {
        bundle[name] = this
    }

    fun load() {
        runBun.forEach { it.run() }
    }

}