package ice.library

import arc.Core
import arc.Graphics
import arc.files.Fi
import arc.graphics.Pixmap
import arc.graphics.g2d.NinePatch
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import arc.scene.style.Drawable
import arc.scene.style.ScaledNinePatchDrawable
import arc.scene.style.TextureRegionDrawable
import arc.util.Log
import ice.Ice
import ice.library.world.Load
import universecore.util.mods.ModGetter
import universecore.util.mods.ModInfo

object IFiles : Load {
  private val rootDirectory = HashMap<String, Fi>()
  private val musics = HashMap<String, Fi>()
  private val sounds = HashMap<String, Fi>()
  private val shaders = HashMap<String, Fi>()
  private var initializer = false
  val modWithClass: ModInfo = ModGetter.getModWithClass(Ice::class.java)!!
  override fun setup() {
    if (initializer) return
    initializer = true
    modWithClass.file.list().forEach {
      rootDirectory[it.name()] = it
    }
    rootDirectory.forEach { entry ->
      when (entry.key) {
        "musics" -> {
          entry.value.findAll { it.extension().equals("ogg") }?.forEach {
            musics[it.name()] = it
          }
        }

        "sounds" -> {
          entry.value.findAll { it.extension().equals("ogg") }?.forEach {
            sounds[it.name()] = it
          }
        }

        "shaders" -> {
          entry.value.walk {
            if (!shaders.contains(it.name())) {
              shaders[it.name()] = it
            } else {
              Log.warn("已收录shader文件:${shaders[it.name()]?.path()},未收录:${it.path()}")
            }
          }
        }
      }
    }
  }

  fun findSound(name: String) = sounds[name] ?: throw Exception("未找到文件:$name")
  fun findMusic(name: String) = musics[name] ?: throw Exception("未找到文件:$name")
  fun findShader(name: String) = shaders[name] ?: throw Exception("未找到文件:$name")
  fun findPng(name: String): AtlasRegion = Core.atlas.find(name)
  fun findModPng(name: String): AtlasRegion = Core.atlas.find(name.appendModName())
  fun hasPng(name: String) = Core.atlas.has(name)
  fun String.appendModName(): String {
    return modWithClass.name + "-" + this
  }

  fun String.replaceModName(): String {
    return replace("${modWithClass.name}-", "")
  }

  fun createNinePatch(name: String): Drawable {
    val region: AtlasRegion? = Core.atlas.find("${modWithClass.name}-$name")
    if (region!!.splits != null) {
      val splits = region.splits
      val patch = NinePatch(region, splits!![0], splits[1], splits[2], splits[3])
      val pads = region.pads
      if (pads != null) patch.setPadding(pads[0].toFloat(), pads[1].toFloat(), pads[2].toFloat(), pads[3].toFloat())
      return ScaledNinePatchDrawable(patch, 1f)
    } else {
      return TextureRegionDrawable(region, 1f)
    }
  }

  fun newCursor(filename: String): Graphics.Cursor {
    val fi = modWithClass.file.child("sprites").child("cursors").child("$filename.png")
    val p = Pixmap(fi)
    val result = Core.graphics.newCursor(p, p.width / 2, p.height / 2)
    p.dispose()
    return result
  }
}

