package ice.library.file

import arc.util.Log
import ice.content.blocks.IceBlocks
import ice.type.IceItem
import ice.type.IceLiquid
import ice.type.IceStatusEffect
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import java.util.*

/**检查bundle未使用字段*/
object BundleDetection {
    private val file = IceFiles.fis["bundle_zh_CN.properties"]

    /**过滤掉不需要检测的key*/
    private val filtration = arrayOf("category", "IceStat")
    private val properties = Properties()

    init {
        properties.load(file.reader())
    }

    fun load() {
        properties.forEach { key, value ->
            if (!key.toString().contains(".")) {
                properties.remove(key, value)
            }
            filtration.forEach { s ->
                if (key.toString().contains(s)) {
                    properties.remove(key, value)
                }
            }
        }
        IceItem.items.each(BundleDetection::detection)
        IceLiquid.liquids.each(BundleDetection::detection)
        IceStatusEffect.stuts.each(BundleDetection::detection)
        IceBlocks.blocks.each(BundleDetection::detection)
        Vars.content.units().each(BundleDetection::detection)
        properties.forEach { t, u -> Log.info("@[Bundle]@ @", "[yellow]", "[]", "[yellow]未使用属性键$t,值:$u[]") }
    }

    private fun detection(content: UnlockableContent) {
        properties.forEach { key, value ->
            if (content.localizedName == value) {
                properties.remove(key, value)
            }
            if (content.description == value) {
                properties.remove(key, value)
            }
            if (content.details == value) {
                properties.remove(key, value)
            }
        }
    }
}