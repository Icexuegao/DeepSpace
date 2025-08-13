package ice.library.baseContent.blocks.effect

import arc.flabel.FConfig
import arc.flabel.FEffect
import arc.flabel.FGlyph
import arc.flabel.FLabel
import arc.func.Prov
import arc.math.Interp
import arc.math.Mathf
import arc.struct.FloatSeq
import ice.library.scene.element.IceDialog
import ice.library.scene.tex.Colors
import ice.library.struct.addP
import ice.library.baseContent.blocks.abstractBlocks.IceBlock

class LostBox(name: String) : IceBlock(name) {
    init {
        buildType = Prov(::LostBoxBuild)
    }

    inner class LostBoxBuild : IceBuild() {
        override fun tapped() {
            val tile = "祭祀残篇"
            val string = lostBoxStringMap[tile]

            IceDialog("遗弃匣").apply {
                cont.add(tile).padBottom(10f).color(Colors.r1).row()
                string?.split("\n")?.forEach { s ->
                    cont.add(FLabel(s.filterNot { it.isWhitespace() }).also {
                        it.defaultToken = "{dfd}"
                    }).color(Colors.r1).row()
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
            皆是祂赐予的永恒之印
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

    init {
        FConfig.registerEffect("dfd", ::ShakeEffects)
    }

    class ShakeEffects : FEffect() {
        private val lastOffsets = FloatSeq()
        var distance: Float = 3f // How far the glyphs should move
        var intensity: Float = 0.3f // How fast the glyphs should move
        override fun onApply(label: FLabel?, glyph: FGlyph, localIndex: Int, delta: Float) {
            // Make sure we can hold enough entries for the current index
            if (localIndex >= lastOffsets.size / 2) {
                lastOffsets.setSize(lastOffsets.size + 16)
            }
            // Get last offsets
            val lastX = lastOffsets.get(localIndex * 2)
            val lastY = lastOffsets.get(localIndex * 2 + 1)
            // Calculate new offsets
            var x = getLineHeight(label) * distance * Mathf.random(-1, 1) * defaultDistance
            var y = getLineHeight(label) * distance * Mathf.random(-1, 1) * defaultDistance
            // Apply intensity
            val normalIntensity = Mathf.clamp(intensity * defaultIntensity, 0f, 1f)
            x = Interp.linear.apply(lastX, x, normalIntensity)
            y = Interp.linear.apply(lastY, y, normalIntensity)
            // Apply fadeout
            val fadeout = calculateFadeout()
            x *= fadeout
            y *= fadeout
            x = Math.round(x).toFloat()
            y = Math.round(y).toFloat()
            // Store offsets for the next tick
            lastOffsets.set(localIndex * 2, x)
            lastOffsets.set(localIndex * 2 + 1, y)
            // Apply changes
            glyph.xoffset = (glyph.xoffset + x).toInt()
            glyph.yoffset = (glyph.yoffset + y).toInt()
        }

        companion object {
            private const val defaultDistance = 0.12f
            private const val defaultIntensity = 0.5f
        }
    }

}