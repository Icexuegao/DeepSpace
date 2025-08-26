package ice.library.scene.layout

import arc.graphics.g2d.TextureRegion
import arc.scene.style.TextureRegionDrawable
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles

open class Character(val name: String, val gal: TextureRegion) {
    companion object {
        val characters = Seq<Character>()
    }

    init {
        characters.add(this)
    }

}