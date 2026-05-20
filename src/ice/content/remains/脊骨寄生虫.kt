package ice.content.remains

import arc.util.Scaling
import ice.content.IUnitTypes
import ice.core.IFiles.appendModName
import ice.graphics.IceColor
import ice.type.Remains
import mindustry.world.meta.Stats
import universecore.scene.style.DynamicTextureDrawable
import universecore.scene.ui.itooltip

class 脊骨寄生虫 :Remains("remains_spine_parasite") {
  init {
    localization {
      zh_CN {
        this.localizedName = "脊骨寄生虫"
        description = "一种具有高度神经亲和性的节状生物,渴望与血肉生物的中枢神经系统结合"
      }
    }
    remainsColor = IceColor.r2
    setDescriptionTable {
      it.table { table ->
        table.add("影响单位: ").pad(5f).color(remainsColor)
        table.image(IUnitTypes.蚀虻.uiIcon).size(45f).scaling(Scaling.fit).itooltip(IUnitTypes.蚀虻.localizedName)
      }
    }
    icon = DynamicTextureDrawable(name.appendModName()) {
      it.frameCount = 10
      it.frameDuration = 60f / 4f
    }
    val fg = 1.2f
    effect = "[爬行类]血肉畸变体速度提升[${((fg - 1) * 100).toInt()}%]"
    install = {
      IUnitTypes.蚀虻.speed *= fg
      IUnitTypes.蚀虻.stats = Stats()
      IUnitTypes.蚀虻.checkStats()
      IUnitTypes.蚀虻Middle.speed *= fg
      IUnitTypes.蚀虻Middle.stats = Stats()
      IUnitTypes.蚀虻Middle.checkStats()
      IUnitTypes.蚀虻End.speed *= fg
      IUnitTypes.蚀虻End.stats = Stats()
      IUnitTypes.蚀虻End.checkStats()
    }
    uninstall = {
      IUnitTypes.蚀虻.speed /= fg
      IUnitTypes.蚀虻.stats = Stats()
      IUnitTypes.蚀虻.checkStats()
      IUnitTypes.蚀虻Middle.speed /= fg
      IUnitTypes.蚀虻Middle.stats = Stats()
      IUnitTypes.蚀虻Middle.checkStats()
      IUnitTypes.蚀虻End.speed /= fg
      IUnitTypes.蚀虻End.stats = Stats()
      IUnitTypes.蚀虻End.checkStats()
    }
  }
}