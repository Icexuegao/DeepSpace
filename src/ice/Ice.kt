package ice

import arc.Events
import ice.audio.SoundControl
import ice.content.*
import ice.content.block.IBlocks
import ice.core.IcePackSprites
import ice.core.SaveIO
import ice.core.SettingValue
import ice.entities.bullet.base.IceBullet
import ice.game.IceTeam
import ice.library.EventType
import ice.library.IFiles
import ice.library.Schematics
import ice.ui.MenusDialog
import ice.ui.UI
import ice.ui.bundle.LocalizationManager
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import ice.world.meta.IAttribute
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.graphics.MultiPacker
import mindustry.mod.Mod
import mindustry.type.Category
import mindustry.world.meta.BuildVisibility
import singularity.Recipes
import singularity.Singularity
import singularity.type.SglCategory
import singularity.type.SglContentType
import tmi.RecipeEntryPoint
import universe.util.reflect.Enums.accessEnum0
import universecore.UncCore

@RecipeEntryPoint(Recipes::class)
open class Ice :Mod() {

  companion object {
    val singularity = Singularity()
  }

  enum class A {
    d, f, w
  }



  init {
    val ad = A::class.accessEnum0()
    ad.newEnumInstance("xaw", 1)
    A.entries.toTypedArray().forEach {
    }
  }

  init {
    DeepSpace.globals.load()
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
    LocalizationManager.load()



    Vars.content.blocks().forEach {
      if (!it.hasBuilding() && it.category == Category.distribution) {
        it.category = SglCategory.environment
        it.buildVisibility = BuildVisibility.sandboxOnly
      }
    }
  }

  override fun packSprites(packer: MultiPacker) = IcePackSprites.packSprites(packer)
}
