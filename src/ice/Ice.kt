package ice

import arc.Events
import ice.async.ParcelProcess
import ice.audio.SoundControl
import ice.content.*
import ice.content.block.IBlocks
import ice.core.SaveIO
import ice.core.SettingValue
import ice.entities.IceRegister
import ice.entities.bullet.base.IceBullet
import ice.game.IceTeam
import ice.library.EventType
import ice.library.IFiles
import ice.library.Schematics
import ice.ui.Documents
import ice.ui.MenusDialog
import ice.ui.UI
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import ice.world.meta.IAttribute
import mindustry.Vars
import mindustry.ctype.UnlockableContent
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
open class Ice : Mod() {

  companion object {
    val singularity = Singularity()
  }

  init {
    DeepSpace.globals.load()
    // researches.init();
    Events.on(mindustry.game.EventType.MusicRegisterEvent::class.java){
      SoundControl.init()
    }
    IFiles.setup()
    IAttribute.setup()
    SglCategory.setup()
    UncCore.setup()
    SettingValue.setup()
    IceRegister.setup()
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
    Vars.asyncCore.processes.add(ParcelProcess)
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
