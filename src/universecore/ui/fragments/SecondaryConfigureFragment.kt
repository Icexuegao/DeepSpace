package universecore.ui.fragments

import arc.Core
import arc.Events
import arc.func.Cons
import arc.math.Interp
import arc.scene.Element
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.util.Align
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.game.EventType.ResetEvent
import mindustry.gen.Building
import mindustry.ui.fragments.BlockConfigFragment
import universecore.components.blockcomp.SecondableConfigBuildComp

/**方块的二级配置面板，通常在[universecore.UncCore.secConfig]保存了一个实例，默认使用这个实例而不是创建一个新的
 * @author EBwilson
 * @since 1.5 */
open class SecondaryConfigureFragment {
  protected var config: BlockConfigFragment = Vars.control.input.config
  protected var table: Table = Table()

  protected var configCurrent: Building? = null
  protected var configuring: SecondableConfigBuildComp? = null

  fun build(parent: Group) {
    parent.addChild(table)

    Core.scene.add(object : Element() {
      override fun act(delta: Float) {
        super.act(delta)
        if (Vars.state.isMenu) {
          table.visible = false
          configCurrent = null
        } else {
          table.visible = config.isShown && configCurrent != null
          if (!table.visible) table.clearChildren()
          val b = config.getSelected()
          configuring = if (b is SecondableConfigBuildComp) b as SecondableConfigBuildComp else null
        }
      }
    })

    Events.on(ResetEvent::class.java) { e: ResetEvent? ->
      table.visible = false
      configCurrent = null
    }
  }

  /** 打对当前配置的方块打开对目标方块的二级配置菜单
   * @param target 二级配置执行的目标方块*/
  fun showOn(target: Building) {
    configCurrent = target

    table.visible = true
    table.clear()
    configuring!!.buildSecondaryConfig(table, target)
    table.pack()
    table.isTransform = true
    table.actions(Actions.scaleTo(0f, 1f), Actions.visible(true), Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out))

    table.update {
      table.setOrigin(Align.center)
      if (configuring == null || configCurrent == null || configCurrent!!.block === Blocks.air || !configCurrent!!.isValid) {
        hideConfig()
      } else {
        configCurrent!!.updateTableAlign(table)
      }
    }
  }

  fun getConfiguring(): Building? {
    return configCurrent
  }

  fun hideConfig() {
    configCurrent = null
    table.actions(Actions.scaleTo(0f, 1f, 0.06f, Interp.pow3Out), Actions.visible(false))
  }
}