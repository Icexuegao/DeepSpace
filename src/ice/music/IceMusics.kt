package ice.music

import arc.audio.Music
import arc.util.Log
import ice.library.file.IceFiles


object IceMusics {
    private val mus = HashMap<String, Music>()

    init {
        val find = IceFiles.pathFind("music")
        find.list().forEach {
            mus[it.nameWithoutExtension()] = Music(it)
        }
        mus.values.forEach { it.isLooping = true }
    }

    fun get(name: String): Music {
        val music = mus[name]
        return if (music != null) {
            music
        } else {
            Log.warn("[${this.javaClass.name}]music:${name} 不存在!!")
            mus["title"]!!
        }
    }

    fun toggle(name: String, boolean: Boolean) {
        val music = get(name)
        if (boolean) music.play() else music.stop()
    }
}

