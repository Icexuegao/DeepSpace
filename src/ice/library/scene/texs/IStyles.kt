package ice.library.scene.texs

import ice.library.IFiles
import ice.library.scene.tex.ProgressAttribute

object IStyles {
    var pa1 = ProgressAttribute(Texs.barBackground, Texs.barTop).apply {
        color = Colors.b4
        starX = 5f
        starY = 23f
        drawHeight = Texs.barBackground.height - 42f
    }
    val scal = 0.7f
    var pa2 = ProgressAttribute(IFiles.findIcePng("bossProgress-box"), Texs.barTop, IFiles.findIcePng("bossProgress-right-cover"), scal).apply {
        color = Colors.b4.cpy().a(1f)
        drawHeight = (barBackground.height - 80f) * scal
        starX = 24f * scal
        starY = 38f * scal
    }
}