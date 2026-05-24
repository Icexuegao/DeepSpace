package ice

import arc.Core
import arc.Events
import ice.audio.SoundControl
import ice.content.*
import ice.content.block.IBlocks
import ice.core.IFiles
import ice.core.IcePackSprites
import ice.core.SaveIO
import ice.core.SettingValue
import ice.entities.bullet.base.IceBullet
import ice.game.EventType
import ice.game.IceTeam
import ice.game.Schematics
import ice.ui.MenusDialog
import ice.ui.UI
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
import singularity.Sgl
import singularity.Singularity
import singularity.type.SglCategory
import singularity.type.SglContentType
import tmi.RecipeEntryPoint

@RecipeEntryPoint(Recipes::class)
open class Ice :Mod() {
  companion object {
    val singularity = Singularity()
  }

  init {
    Events.on(mindustry.game.EventType.ResizeEvent::class.java){
      Core.scene.marginTop=0f
      Core.scene.marginBottom=0f
    }
    DeepSpace.globals.load()
    Events.on(mindustry.game.EventType.MusicRegisterEvent::class.java) {
      SoundControl.init()
    }
    Events.on(mindustry.game.EventType.ModContentLoadEvent::class.java) {
      Vars.content.blocks().forEach {
        if (!it.hasBuilding() && it.category == Category.distribution) {
          it.category = SglCategory.environment
          it.buildVisibility = BuildVisibility.sandboxOnly
        }
      }
    }
    IFiles.setup()
    IAttribute.setup()
    SglCategory.setup()
    SettingValue.setup()
    IceBullet.setup()
    EventType.setup()
    IceTeam.setup()
  }

  override fun init() {

    Sgl.init()
    UI.init()
    Schematics.init()
    MenusDialog.init()
    SaveIO.init()

    Vars.content.each {
      if (it.minfo.mod == DeepSpace.mod && it is UnlockableContent) it.unlock()
    }
  }

  override fun loadContent() {
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

  }

  override fun packSprites(packer: MultiPacker) = IcePackSprites.packSprites(packer)
}
