package ice.library

import arc.Core
import arc.files.Fi
import arc.graphics.Texture
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import arc.graphics.g2d.TextureRegion
import arc.util.Log
import ice.Ice


object IFiles {
    private val files = HashMap<String, Fi>()
    private val directory = HashMap<String, Fi>()
    private val filter = arrayOf("ice", "kotlin", "org", "META-INF")

    init {
        Ice.ice.root.list().forEach {
            if (!filter.contains(it.name())) {
                loop(it)
            }
        }
    }

    private fun loop(fi: Fi) {
        if (fi.isDirectory) {
            directory[fi.name()] = fi
            fi.list().forEach {
                loop(it)
            }
        } else {
            if (files.contains(fi.name())) {
                Log.warn("已收录文件:${files[fi.name()]!!.path()},未收录:${fi.path()}")
            } else {
                files[fi.name()] = fi
            }
        }
    }

    fun find(name: String): Fi {
        if (!files.contains(name)) {
            Log.warn("未找到文件:$name")
        }
        return files[name]!!
    }

    fun findDirectory(name: String): Fi? {
        return directory[name]
    }

    fun findPng(name: String): TextureRegion {
        val textureRegion = TextureRegion(Texture(find("$name.png")))
        val are = AtlasRegion(textureRegion)
        return are
    }
    fun load(name: String): TextureRegion {
        return Core.atlas.find(name)
    }
}

