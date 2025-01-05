package ice.music

import arc.audio.Music
import ice.Ice


object IceMusics {
    private val mus = HashMap<String, Music>()

    init {
        val find = Ice.ice.root.child("music")
        find.list().forEach {
            mus[it.nameWithoutExtension()] = Music(it).apply { isLooping = true }
        }
    }

    fun getPosition(name: String): Float {
        return get(name).position
    }

    private fun get(name: String): Music {
        return mus[name] ?: throw Exception("[${this.javaClass.name}]music:${name} 不存在!!")
    }

    fun toggle(name: String, boolean: Boolean = true) {
        stopAll()
        val music = get(name)
        if (boolean){ music.play()} else music.pause(true)
    }


    fun stopAll(not: String = "xxxxxx") {
        mus.forEach {
            if (it.key != not) it.value.pause(true)
        }
    }

    fun setVolume(name: String, value: Float) {
        get(name).volume = value
    }
}



