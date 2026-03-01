package ice.library

import arc.Core
import arc.func.Boolp
import arc.graphics.Color
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import ice.DeepSpace
import ice.audio.ISounds
import ice.content.IUnitTypes
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.listener.DragInputListener
import ice.library.scene.ui.*
import ice.library.util.isNumericWithSign
import ice.ui.UI
import ice.ui.fragment.ConversationFragment
import ice.ui.fragment.FleshFragment
import ice.ui.menusDialog.AchievementDialog
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.entities.Effect
import mindustry.game.Gamemode
import mindustry.game.Rules
import mindustry.game.Team
import mindustry.gen.Groups
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.gen.Unit
import mindustry.type.UnitType
import mindustry.ui.Styles
import mindustry.world.meta.BuildVisibility

object DeBugFragment {
  val debug = Table()
  private lateinit var cont: Table
  private var index = 0
  private var check: String? = null
  private var windowWidth = 400f
  var group: Group = WidgetGroup(debug).apply {
    name = "debug"
    setFillParent(true)
    touchable = Touchable.childrenOnly
    visibility = Boolp { Vars.ui.hudfrag.shown && SettingValue.启用调试模式 }
  }

  fun build(parent: Group) {
    parent.addChild(group)
    debug.setSize(530f, windowWidth)
    buildPane(debug)
    buildWindow(debug)
  }

  private fun buildWindow(table: Table) {
    var windowVisibility = false
    var contvisibe = false
    table.table(IStyles.background41) { ta ->
      ta.setColor(IceColor.b4)
      ta.icePane {
        cont = it
      }.grow().visible {
        contvisibe
      }
      ta.setSize(windowWidth)

      debug.update {
        if (check == null) {
          if (!windowVisibility) return@update
          windowVisibility = false
          ta.actions(Actions.fadeOut(0.25f), Actions.run { contvisibe = false })
        } else {
          if (windowVisibility) return@update
          windowVisibility = true
          ta.actions(Actions.run { contvisibe = true }, Actions.fadeIn(0.25f))
        }
      }
      ta.color.a = 0f
    }.margin(4f).grow()
  }

  private fun buildPane(table: Table) {
    val pane = Table()
    pane.setPositions(500f, 100f).setSize(130f, 250f)
    pane.add(Image(Tex.whiteui).addListeners(DragInputListener(table))).color(IceColor.b4).height(40f).growX().row()
    pane.icePane {
      fun button(name: String, icon: Drawable, runnable: Runnable = Runnable {}) {
        it.image(Tex.underlineWhite, IceColor.b3).height(3f).growX().row()
        it.imageButton(name, icon, IStyles.txtCleari, 20f) {
          check = when (check) {
            name -> null
            null -> name
            else -> name
          }
          cont.clear()
          runnable.run()
        }.height(42f).growX().row()
      }
      button("游戏模式", Icon.editSmall, ::gmode)
      button("队伍", Icon.adminSmall, ::team)
      button("解禁", Icon.cancelSmall, ::allBlock)
      button("科技", Icon.treeSmall, ::unlock)
      button("物品", Icon.pasteSmall, ::items)
      button("清除日志", Icon.fileTextSmall, Vars.ui.consolefrag::clearMessages)
      button("传教", Icon.bookOpenSmall) {
        Incident.announce("[red]<<传教>> 你的信仰疑似有点动摇[]", 9f)
      }
      button("折跃", Icon.bookOpenSmall) {
      }
      button("剧情", Icon.bookOpenSmall) {
        ConversationFragment.showText("血肉肿瘤", "耳边萦绕着碎片的尖啸割裂了现实的帷幕")
      }
      button("清除天气", Icon.bookOpenSmall) {
        Groups.weather.clear()
      }

      button("血肉文本", Icon.fileTextSmall) {
        FleshFragment.addText()
      }
      button("游戏胜利", Icon.bookOpenSmall) {
        Vars.state.won = true
        Vars.state.rules.winWave
        Vars.state.rules.waves = false
        Vars.state.gameOver = true
      }
      button("修复损坏", Icon.up) {
        Vars.world.tiles.forEach { it1 ->
          it1.build?.let { it2 ->
            it2.team = Vars.player.team()
            it2.enabled = true
          }
        }
      }
      val weapon= IUnitTypes.路西法.weapons.last()
      val table= Table()
      table.button("关闭"){
        table.remove()
      }.row()
      table.field("x"){ it1 ->
        it1.toFloatOrNull().let { it2 ->
          it2?:return@let
          weapon.x=-it2
        }
      }.growX().row()
      table.field("y"){ it1 ->
        it1.toFloatOrNull().let { it1 ->
          it1?:return@let
          weapon.y=it1
        }
      }.growX().row()
      table.setSize(400f,400f)
      table.setPosition(UI.cgwidth/2f, UI.cgheight/2f)
      button("unitsWeapon", Icon.up) {
        Core.scene.add(table)
      }

      button("ac", Icon.up) {
        val random = AchievementDialog.achievements.random()
        random.unlock()
        random.clearUnlock()
      }
      button("帝国折跃", Icon.units) {
        /*  IceEffects.phaseJumpEmpire.at(Vars.player.x, Vars.player.y, 0f, Vars.content.unit("curse-of-flesh-无畏"))*/
      }
      button("kill", Icon.units) {
        Groups.unit.find { it1 -> it1.type == IUnitTypes.青壤 }?.kill()
      }
      button("effect", Icon.effect) {
        IceEffects.arc.at(Vars.player.x, Vars.player.y, 0f, IUnitTypes.虚宿)
      }
    }.width(130f).height(211f)
    table.add(pane).expandY().top()
  }

  fun spawnAction(unit: UnitType, x: Float, y: Float, rotate: Float, color: Color, team: Team): Unit {
    val spawn = unit.spawn(team, x, y)
    spawn.rotation = rotate
    IceEffects.jumpTrail.at(x, y, rotate, color, spawn.type)
    ISounds.foldJump.at(spawn)
    Effect.shake(spawn.hitSize / 3f, spawn.hitSize / 4f, spawn)
    return spawn
  }

  private fun addbutton(t: Table, content: UnlockableContent, run: Runnable) {
    index++

    try {
      val icon = TextureRegionDrawable(content.uiIcon)
      t.button(icon, IStyles.button, 24f, run).size(40f).pad(2f)
        .tooltip(content.localizedName)
      if (index % 8 == 0) t.row()
    } catch (e: Exception) {
      throw Exception("添加按钮失败: $content", e)
    }
  }

  private fun gmode() {
    cont.iTableG {
      it.addLine("游戏模式", Color.white)
      it.table { ta ->
        Gamemode.entries.forEach { m ->
          ta.button(m.name, Styles.flatBordert) {
            Vars.state.rules = m.apply(Rules())
          }.grow()
        }
      }.growX()
    }
  }

  private fun team() {
    index = 0
    cont.iTableG {
      Team.all.forEach { t ->
        index++
        it.button(t.name) {
          Vars.player.team(t)
        }.size(100f)
        if (index % 4 == 0) it.row()
      }
    }
  }

  private fun items() {
    var size = ""
    var add = true
    cont.iTableG {
      it.addLine("物品", Color.white)
      it.iTableG { ita ->
        Vars.content.items().forEach { item ->
          addbutton(ita, item) {
            if (add) {
              Vars.player.core().items.add(item, if (size.isNumericWithSign()) size.toInt() else 1000)
            } else {
              Vars.player.core().items.remove(item, if (size.isNumericWithSign()) size.toInt() else 1000)
            }
          }
        }
      }.row()
      it.iTableG { t ->
        t.button("添加", Styles.flatBordert) {
          add = !add
        }.apply {
          get().update {
            get().setText(if (add) "添加" else "删除")
          }
        }.growX().height(45f)
        t.add("物品数量: ")
        t.add(TextField("1000").apply {
          messageText = "1000"
          update { size = text }
        })
      }.row()
      it.iTableG { t ->
        t.button("添加全部", Styles.flatBordert) {
          Vars.content.items().forEach { item ->
            Vars.player.core()?.items?.add(item, 2000)
          }
        }.growX().pad(2f).height(45f).row()
        t.button("清除全部", Styles.flatBordert) {
          Vars.player.core()?.items?.clear()
        }.growX().pad(2f).height(45f).row()
      }.row()
    }
    index = 0
  }

  private fun unlock() {
    cont.iTableG { ta ->
      ta.iTableG {
        it.button("解锁全部", Styles.flatBordert) {
          Vars.content.each { cotent ->
            (cotent as? UnlockableContent)?.unlock()
          }
        }.size(160f, 45f)
        it.button("锁定全部", Styles.flatBordert) {
          Vars.content.each { cotent ->
            (cotent as? UnlockableContent)?.clearUnlock()
          }
        }.size(160f, 45f).row()
      }.row()
      ta.addLine("科技", Color.white)
      ta.iTableG {
        Vars.content.each { cotent ->
          if (cotent is UnlockableContent) {
            addbutton(it, cotent) {
              if (cotent.locked()) {
                cotent.unlock()
              } else {
                cotent.clearUnlock()
              }
            }
          }
        }
      }
    }
    index = 0
  }

  private fun allBlock() {
    Vars.content.blocks().forEach {
      if (it.minfo.mod == DeepSpace.mod) it.buildVisibility = BuildVisibility.shown
    }
    cont.iTableG {
      it.addLine("解禁方块", Color.white)
      it.iTableG { it1 ->
        Vars.content.blocks().forEach { block ->
          if (block is UnlockableContent) {
            addbutton(it1, block) {
              block.buildVisibility = BuildVisibility.shown
              Vars.control.input.block = block
            }
          }
        }
      }
    }
    index = 0
  }
}