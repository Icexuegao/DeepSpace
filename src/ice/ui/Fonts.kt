package ice.ui

import arc.freetype.FreeTypeFontGenerator
import arc.graphics.g2d.Font
import arc.scene.ui.layout.Scl
import ice.DeepSpace.mod

object Fonts {
  val mono: Font = FreeTypeFontGenerator(mod.root.child("fonts").child("JetBrainsMono.ttf")).generateFont(FreeTypeFontGenerator.FreeTypeFontParameter().apply {
    size = Scl.scl(19f).toInt()
    borderWidth = Scl.scl(0.3f)
    shadowOffsetY = 2
    incremental = true
    borderColor = color
  })
}