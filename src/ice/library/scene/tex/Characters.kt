package ice.library.scene.tex

import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles
import ice.library.meta.IceEffects

object Characters {
    val 黛尹 = Character("黛尹", IFiles.findIcePng("黛尹"))
    val 枢机 = Character("枢机", IFiles.findIcePng("枢机"))
    val 耀威特 = Character("耀威特", IFiles.findIcePng("耀威特"))
    val 娜雅 = Character("娜雅", IFiles.findIcePng("娜雅"))
    val 迷惢思 = Character("迷惢思", IFiles.findIcePng("迷惢思"))
    val 斯恒 = Character("斯恒", IFiles.findIcePng("斯恒"))
    val alon = AlonCharacter("alon", IFiles.findIcePng("alon"))
    val zino = ZionCharacter("zino", IFiles.findIcePng("zino"))

    class ZionCharacter(name: String, gal: TextureRegion) : AlonCharacter(name, gal) {
        var i = 0
        val blink1Tex = Array(6) { i ->
            TextureRegionDrawable(IFiles.findIcePng("$name-blink1-${i + 1}"))
        }
        val seq1 = Seq(blink1Tex)
        override fun endBlink() {
            i++
            blink = false
            iterator = if (i % 5 == 0) seq1.iterator() else seq.iterator()
        }
    }

    open class AlonCharacter(name: String, gal: TextureRegion) : Character(name, gal) {
        val blinkTex = Array(5) { i ->
            TextureRegionDrawable(IFiles.findIcePng("$name-blink-${i + 1}"))
        }
        val seq = Seq(blinkTex)
        var blink = false
        val blinktime = 6f
        var blinkTempTex = blinkTex.first()
        val interval = Interval(1)
        var iterator = seq.iterator()
        var rad = IceEffects.rand.random(2 * 60f, 5 * 60f)
        val inter = Interval(1)
        open fun upfate(image: Image, bool: Prov<Boolean> = Prov { true }) {
            if (inter[rad] && bool.get()) {
                blink = true
                rad = IceEffects.rand.random(2 * 60f, 5 * 60f)
            }

            if (!blink) return
            if (interval[blinktime]) {
                if (iterator.hasNext()) {
                    setDrawable(image)
                } else endBlink()
            }
        }

        open fun endBlink() {
            blink = false
            iterator = seq.iterator()
        }

        open fun setDrawable(image: Image) {
            blinkTempTex = iterator.next()
            image.drawable = blinkTempTex
        }
    }

    open class Character(val name: String, val gal: TextureRegion) {
        companion object {
            val characters = Seq<Character>()
        }

        init {
            characters.add(this)
        }
    }
}