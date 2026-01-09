package ice.graphics

import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles
import ice.world.meta.IceEffects

object Characters {
    val characters = Seq<Character>()
    val 黛尹 = Character("黛尹", IFiles.findModPng("黛尹"))
    val 枢机 = Character("枢机", IFiles.findModPng("枢机"))
    val 耀威特 = Character("耀威特", IFiles.findModPng("耀威特"))
    val 娜雅 = BlinkCharacter("娜雅", IFiles.findModPng("娜雅")).apply {
        color= IceColor.b5
    }
    val 迷惢思 = BlinkCharacter("迷惢思", IFiles.findModPng("迷惢思")).apply {
        color= IceColor.s1
    }
    val 斯恒 = Character("斯恒", IFiles.findModPng("斯恒"))
    val alon = BlinkCharacter("alon", IFiles.findModPng("alon"))
    val zino = ZionCharacter("zino", IFiles.findModPng("zino"))

    class ZionCharacter(name: String, gal: TextureRegion) : BlinkCharacter(name, gal) {
        var i = 0
        val blink1Tex = Array(6) { i ->
            TextureRegionDrawable(IFiles.findModPng("$name-blink1-${i + 1}"))
        }
        val seq1 = Seq(blink1Tex)

        override fun endBlink() {
            i++
            blink = false
            iterator = if (i % 5 == 0) seq1.iterator() else seq.iterator()
        }
    }

    open class BlinkCharacter(name: String, gal: TextureRegion) : Character(name, gal) {
        val blinkTex = Array(5) { i ->
            TextureRegionDrawable(IFiles.findModPng("$name-blink-${i + 1}"))
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
            image.setDrawable(blinkTempTex)
        }
    }

    open class Character(val name: String, val gal: TextureRegion) {
        var color= IceColor.b4
        init {
            characters.add(this)
        }
    }
}