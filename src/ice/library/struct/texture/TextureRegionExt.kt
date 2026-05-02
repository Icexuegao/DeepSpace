package ice.library.struct.texture

import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable

fun TextureRegion.asDrawable(scal: Float = 1f): TextureRegionDrawable = TextureRegionDrawable(this, scal)