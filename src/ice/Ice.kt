package ice

import arc.util.OS
import ice.async.ParcelProcess
import ice.audio.SoundControl
import ice.content.*
import ice.content.block.IBlocks
import ice.core.SettingValue
import ice.entities.IceRegister
import ice.entities.bullet.base.IceBullet
import ice.game.IceTeam
import ice.library.EventType
import ice.library.IFiles
import ice.library.Schematics
import ice.library.struct.log
import ice.ui.Documents
import ice.ui.UI
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import mindustry.Vars
import mindustry.mod.Mod
import singularity.Recipes
import singularity.Singularity
import singularity.type.SglCategory
import tmi.RecipeEntryPoint
import universecore.UncCore

open class Ice : @RecipeEntryPoint(Recipes::class) Mod() {
  companion object {
    val singularity = Singularity()
  }

  init {
    UncCore.setup()
    IFiles.setup()
    SettingValue.setup()
    IceRegister.setup()
    IceBullet.setup()
    EventType.setup()
    IceTeam.setup()
  }

  override fun init() {
    Vars.control.sound = SoundControl()
    UncCore.init()
    singularity.init()
    //  SglTechTreeDialog().show()
    UI.init()
    Remainss.init()
    Schematics.init()
    Vars.asyncCore.processes.add(ParcelProcess)
    Documents.init()
  }

  override fun loadContent() {
    singularity.loadContent()
    Noise2dBlock("noise2d").apply {
      requirements(SglCategory.matrix, IItems.钴锭, 10)
    }
    AtomSchematics.load()
    IItems.load()
    ILiquids.load()
    IStatus.load()
    IUnitTypes.load()
    IBlocks.load()
    IWeathers.load()
    IPlanets.load()
    //  SglTechThree.load()
    BaseBundle.load()
  }
}
