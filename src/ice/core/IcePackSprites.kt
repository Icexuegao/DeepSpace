package ice.core

import arc.Core
import arc.files.Fi
import arc.graphics.Pixmap
import arc.graphics.Pixmaps
import arc.graphics.g2d.PixmapRegion
import ice.DeepSpace
import ice.library.struct.log
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType

object IcePackSprites {
   fun packSprites(
    packer: MultiPacker
  ) {
    val bleed = Core.settings.getBool("linear", true)
     val sprites= DeepSpace.modFile.child("sprites-out").findAll { f: Fi -> f.extension() == "png_" }
    for(file in sprites) {
      val baseName = file.nameWithoutExtension()

      val encodedData = file.readBytes()
      PngCrypto.processInPlace(encodedData)
      val pix = Pixmap(encodedData)
      //only bleeds when linear filtering is on at startup
      if (bleed) {
        Pixmaps.bleed(pix, 2)
      }
      //this returns a *runnable* which actually packs the resulting pixmap; this has to be done synchronously outside the method

      //don't prefix with mod name if it's already prefixed by a category, e.g. `block-modname-content-full`.
      val hyphen = baseName.indexOf('-')
      val fullName = (if (!(hyphen != -1 && baseName.substring(hyphen + 1)
          .startsWith("ice" + "-"))
      ) "ice" + "-" else "") + baseName

      log {  fullName}
      packer.add(getPage(file), fullName, PixmapRegion(pix))

      pix.dispose()

    }
  }

  private fun getPage(file: Fi): PageType {
    val path = file.path()
    return if (path.contains("sprites/blocks/environment") || path.contains("sprites-override/blocks/environment")) PageType.environment else if (path.contains(
        "sprites/rubble"
      ) || path.contains("sprites-override/rubble")
    ) PageType.rubble else if (path.contains("sprites/ui") || path.contains("sprites-override/ui")) PageType.ui else PageType.main
  }
}