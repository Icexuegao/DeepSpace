package ice.music

import arc.Core
import arc.audio.Music
import arc.func.Cons

class IceMusics {
    companion object IceMusics {
        lateinit var expressOne: Music
        fun load() {
            Core.assets.load("music\\ExpressOne.sThanks.ogg", Music::class.java).loaded = Cons { expressOne =it }
        }
    }
}