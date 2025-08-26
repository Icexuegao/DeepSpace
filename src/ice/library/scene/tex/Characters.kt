package ice.library.scene.tex

import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles
import ice.library.scene.layout.Character

object Characters {
    val 黛尹 = Character("黛尹", IFiles.findIcePng("黛尹"))
    val 枢机 = Character("枢机", IFiles.findIcePng("枢机"))
    val 耀威特 = Character("耀威特", IFiles.findIcePng("耀威特"))
    val 娜雅 = Character("娜雅", IFiles.findIcePng("娜雅"))
    val 迷惢思 = Character("迷惢思", IFiles.findIcePng("迷惢思"))
    val 斯恒 = Character("斯恒", IFiles.findIcePng("斯恒"))
    val alon = AlonCharacter("alon", IFiles.findIcePng("alon"))

    class AlonCharacter(name: String, gal: TextureRegion) : Character(name, gal) {
        val blinkTex = Array(5) { i ->
            TextureRegionDrawable(IFiles.findIcePng("$name-blink-${i+1}"))
        }
        val seq = Seq(blinkTex)
        var blink = false
        val blinktime = 6f
        var blinkTempTex = blinkTex.first()
        val interval = Interval(1)
        var iterator=seq.iterator()
        fun upfate() {
            if (!blink) return
            if (interval[blinktime]) {
                if (iterator.hasNext()) {
                    blinkTempTex = iterator.next()
                } else {
                    blink = false
                    iterator=seq.iterator()
                }
            }
        }
    }
}