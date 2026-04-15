package ice

import arc.Core
import arc.Events
import arc.files.Fi
import arc.graphics.Pixmap
import arc.graphics.Pixmaps
import arc.graphics.g2d.PixmapRegion
import arc.struct.Seq
import ice.audio.SoundControl
import ice.content.*
import ice.content.block.IBlocks
import ice.core.PngCrypto
import ice.core.SaveIO
import ice.core.SettingValue
import ice.entities.bullet.base.IceBullet
import ice.game.IceTeam
import ice.library.EventType
import ice.library.IFiles
import ice.library.Schematics
import ice.library.struct.log
import ice.ui.MenusDialog
import ice.ui.UI
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import ice.world.meta.IAttribute
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType
import mindustry.mod.Mod
import mindustry.type.Category
import mindustry.world.meta.BuildVisibility
import singularity.Recipes
import singularity.Singularity
import singularity.type.SglCategory
import singularity.type.SglContentType
import tmi.RecipeEntryPoint
import universecore.UncCore

@RecipeEntryPoint(Recipes::class)
open class Ice :Mod() {

  companion object {
    val singularity = Singularity()
  }

  enum class A {
    d, f, w
  }

  private fun packSprites(
    packer: MultiPacker,
    sprites: Seq<Fi>,
  ) {
    val bleed = Core.settings.getBool("linear", true)

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

  override fun packSprites(packer: MultiPacker) {
    val sprites: Seq<Fi> = DeepSpace.modFile.child("sprites-out").findAll { f: Fi -> f.extension() == "png_" }
    log {
      sprites.size
    }
    packSprites(packer, sprites)
  }

  private fun getPage(file: Fi): PageType {
    val path = file.path()
    return if (path.contains("sprites/blocks/environment") || path.contains("sprites-override/blocks/environment")) PageType.environment else if (path.contains(
        "sprites/rubble"
      ) || path.contains("sprites-override/rubble")
    ) PageType.rubble else if (path.contains("sprites/ui") || path.contains("sprites-override/ui")) PageType.ui else PageType.main
  }

  init {
    /* Events.on(mindustry.game.EventType.AtlasPackEvent::class.java){packEvent ->
       IFiles.modWithClass.file.child("sprites_out").walk {
        if (it.extension()=="png_"){
          val pix = Pixmap(it.readBytes())
          packEvent.multiPacker.add(getPage(it),"ice-${it.nameWithoutExtension()}", PixmapRegion(pix))
          pix.dispose()
        }
       }
       //replace old atlas data
       packEvent.multiPacker.flush( TextureFilter.linear, Core.atlas)
       //generate new icons
       for(arr in Vars.content.contentMap) {
         arr.each(Cons { c: Content? ->
           if (c is UnlockableContent && c.minfo.mod == DeepSpace.mod) {
             log { c.localizedName }
             c.load()
             c.loadIcon()
             if (c.generateIcons && !c.minfo.mod.meta.pregenerated) {
               c.createIcons(packEvent.multiPacker)
             }
           }
         })
       }

     }

     val ad = A::class.accessEnum0()
     ad.newEnumInstance("xaw", 1)
     A.entries.toTypedArray().forEach {
       log { it.name + "  " + it.ordinal }
     }*/
  }

  init {

    DeepSpace.globals.load()
    // researches.init();
    Events.on(mindustry.game.EventType.MusicRegisterEvent::class.java) {
      SoundControl.init()
    }
    IFiles.setup()
    IAttribute.setup()
    SglCategory.setup()
    UncCore.setup()
    SettingValue.setup()
    IceBullet.setup()
    EventType.setup()
    IceTeam.setup()
    IVars.setup()
  }

  override fun init() {
    UncCore.init()
    singularity.init()
    //  SglTechTreeDialog().show()
    UI.init()
    Schematics.init()
    MenusDialog.init()
    SaveIO.init()


    Vars.content.each {
      if (it.minfo.mod == DeepSpace.mod && it is UnlockableContent) it.unlock()
    }

  }

  override fun loadContent() {
    //载入所有新内容类型
    SglContentType.load()

    Noise2dBlock("noise2d").apply {
      requirements(SglCategory.matrix, IItems.钴锭, 10)
    }
    IItems.load()
    AtomSchematics.load()
    ILiquids.load()
    IStatus.load()
    IUnitTypes.load()
    IBlocks.load()
    IWeathers.load()
    IPlanets.load()

    singularity.loadContent()
    BaseBundle.load()



    Vars.content.blocks().forEach {
      if (!it.hasBuilding() && it.category == Category.distribution) {
        it.category = SglCategory.environment
        it.buildVisibility = BuildVisibility.sandboxOnly
      }
    }
  }
}
