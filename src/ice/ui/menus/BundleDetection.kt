package ice.ui.menus

import arc.scene.ui.layout.Table
import ice.content.blocks.IceBlocks
import ice.library.IFiles
import ice.type.content.IceItem
import ice.type.content.IceLiquid
import ice.type.content.IceStatusEffect
import ice.ui.TableExtend
import ice.ui.TableExtend.tableG
import ice.ui.tex.Colors
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import java.util.*

/**检查bundle未使用字段*/
object BundleDetection {
    private lateinit var cont: Table
    private val file = IFiles.find("bundle_zh_CN.properties")

    /**过滤掉不需要检测的key*/
    private val filtration = arrayOf("category", "iceStat")
    private val properties = Properties()

    init {
        properties.load(file.reader())
    }

    fun set(table: Table) {
        table.tableG {
            TableExtend.addCleanButton(it, "bundle检查", ::begin).row()
        }
        table.tableG { t1 ->

            t1.table { cont = it }
        }
    }


    private fun begin() {
        cont.clear()
        cont.add("过滤的条目").color(Colors.b1).row()
        filtration.forEach {
            cont.add("[$it]").color(Colors.b1).pad(2f).row()
        }
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
        cont.add("未使用的条目").color(Colors.b1).row()
        properties.forEach { t, u ->
            cont.add("未使用属性 键:$t,值:$u").pad(2f).color(Colors.b1).row()
        }
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