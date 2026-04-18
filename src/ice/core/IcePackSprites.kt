package ice.core

import arc.Core
import arc.files.Fi
import arc.graphics.Pixmap
import arc.graphics.Pixmaps
import arc.graphics.g2d.PixmapRegion
import ice.DeepSpace
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType
import java.io.File

object IcePackSprites {
  private const val KEY=920


  fun packSprites(
    packer: MultiPacker
  ) {

    val bleed = Core.settings.getBool("linear", true)
    val sprites = DeepSpace.modFile.child("sprites-out").findAll { f: Fi -> f.extension() == "png_" }
    for(file in sprites) {
      val baseName = file.nameWithoutExtension()

      val encodedData = file.readBytes()
      processInPlace(encodedData)
      val pix = Pixmap(encodedData)
      //only bleeds when linear filtering is on at startup
      if (bleed) {
        Pixmaps.bleed(pix, 2)
      }
      //this returns a *runnable* which actually packs the resulting pixmap; this has to be done synchronously outside the method

      //don't prefix with mod name if it's already prefixed by a category, e.g. `block-modname-content-full`.
      val hyphen = baseName.indexOf('-')
      val fullName = (if (!(hyphen != -1 && baseName.substring(hyphen + 1)
          .startsWith(DeepSpace.modName + "-"))

      ) DeepSpace.modName + "-" else "") + baseName
      packer.add(getPage(file), fullName, PixmapRegion(pix))

      pix.dispose()

    }
  }

  fun encrypt(input: File, output: File) {
    val data = input.readBytes()
    val encrypted = data.map { (it.toInt() xor KEY).toByte() }.toByteArray()
    output.writeBytes(encrypted)
  }

  fun decrypt(input: File, output: File) {
    encrypt(input, output) // XOR 是对称的
  }

  // 直接处理字节数组（更快）
  fun processInPlace(data: ByteArray) {
    for(i in data.indices) {
      data[i] = (data[i].toInt() xor KEY).toByte()
    }
  }

  private fun getPage(file: Fi): PageType {
    val path = file.path()
    return if (path.contains("sprites-out/blocks/environment") || path.contains("sprites-out-override/blocks/environment")) PageType.environment else if (path.contains(
        "sprites-out/rubble"
      ) || path.contains("sprites-override/rubble")
    ) PageType.rubble else if (path.contains("sprites-out/ui") || path.contains("sprites-out-override/ui")) PageType.ui else PageType.main
  }

}