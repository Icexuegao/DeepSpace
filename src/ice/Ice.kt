package ice

import ice.async.ParcelProcess
import ice.audio.SoundControl
import ice.content.*
import ice.content.block.IBlocks
import ice.core.SettingValue
import ice.entities.IceRegister
import ice.entities.bullet.base.BulletType
import ice.game.IceTeam
import ice.library.EventType
import ice.library.IFiles
import ice.library.Schematics
import ice.ui.UI
import ice.ui.bundle.BaseBundle
import ice.vars.SglTechThree
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import mindustry.Vars
import mindustry.mod.Mod
import mindustry.mod.Mods
import singularity.Singularity
import singularity.type.SglCategory
import universecore.UncCore

open class Ice : Mod() {
  companion object {
    val mod: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
    val name = IFiles.modWithClass.displayName
    var displayName = IFiles.modWithClass.displayName
    var version = IFiles.modWithClass.version
    val singularity = Singularity()
    const val githubProjectUrl = "https://github.com/Icexuegao/DeepSpace"
    const val qqGrops="https://qm.qq.com/q/3CR3cn2Wc8"
  }

  init {
    UncCore.setup()
    IFiles.setup()
    SettingValue.setup()
    IceRegister.setup()
    BulletType.setup()
    Vars.control.sound = SoundControl()
    EventType.setup()
    IceTeam.setup()
  }

  override fun init() {
    UncCore.init()
    singularity.init()
    //  SglTechTreeDialog().show()
    UI.init()
    Remainss.init()
    Schematics.init()
    Vars.asyncCore.processes.add(ParcelProcess)
  }

  override fun loadContent() {
    singularity.loadContent()
    Noise2dBlock("noise2d").apply {
      requirements(SglCategory.matrix, IItems.钴锭, 10)
    }
    IItems.load()
    ILiquids.load()
    IStatus.load()
    IUnitTypes.load()
    IBlocks.load()
    IWeathers.load()
    IPlanets.load()
    SglTechThree.load()
    BaseBundle.load()
  }
}
