package ice.library.scene.layout

import arc.graphics.g2d.TextureRegion
import arc.struct.Seq

class Character(val name: String, val gal: TextureRegion) {
    companion object {
        val characters = Seq<Character>()
    }

    init {
        characters.add(this)
    }
}