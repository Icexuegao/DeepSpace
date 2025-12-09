package ice.world.content.blocks.effect

import arc.flabel.FLabel
import arc.func.Prov
import ice.graphics.IceColor
import ice.library.scene.element.IceDialog
import ice.library.struct.addP
import ice.world.content.blocks.abstractBlocks.IceBlock

class LostBox(name: String) : IceBlock(name) {
    init {
        buildType = Prov(::LostBoxBuild)
    }

    inner class LostBoxBuild : IceBuild() {
        override fun tapped() {
            val tile = "祭祀残篇"
            val string = lostBoxStringMap[tile]

            IceDialog("遗弃匣").apply {
                root.add(tile).padBottom(10f).color(IceColor.r1).row()
                string?.split("\n")?.forEach { s ->
                    root.add(FLabel(s.filterNot { it.isWhitespace() }).also {
                        it.defaultToken = "{dfd}"
                    }).color(IceColor.r1).row()
                }
                addCloseButton()
                show()
            }
        }
    }

    companion object {
        val lostBoxStringMap = HashMap<String, String>().apply {
            addP("《猩红教章》 · 其一") {
                """
                不知何时, 灾祸降临在帝国边陲之地
                日复一日, 阴霾不断蚕食帝国的土地, 带来灾难与鲜血
                彼时, 昏庸无为的帝国之主却予以蔑视
                不以为意...
                直到灾祸肆意, 万里焦土, 一片生灵涂炭
                """.trimIndent()
            }
            addP("祭祀残篇") {
                """
            血肉涌动,赤潮翻腾 
            骨骼为阶,肌理为门 
            不可名状者自深渊苏醒
            赐吾等以蠕行之恩
            那剥落的皮囊之下
            万千猩红之舌颂唱真名
            每一滴垂落的甘露
            皆是赐予的永恒之印
            断裂的指节生出新芽
            溃烂的眼眶绽放繁花
            凡愚者恐惧溃散的形骸
            恰是圣体降临的温床
            赞美那无定之形
            脉搏即圣谕,伤口即经文
            当群星腐化,大地匍匐
            唯血肉终将吞没晨昏
                    """.trimIndent()
            }
        }
    }

}