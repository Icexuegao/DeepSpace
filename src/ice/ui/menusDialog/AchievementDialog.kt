package ice.ui.menusDialog

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.math.Interp
import arc.math.Mathf
import arc.scene.actions.Actions
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import ice.DeepSpace
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.EventType
import ice.library.scene.action.IceActions
import ice.library.scene.ui.*
import ice.library.struct.asDrawable
import ice.ui.MenusDialog
import ice.ui.dialog.BaseMenusDialog
import ice.world.meta.IceStats
import mindustry.gen.Icon
import mindustry.ui.Styles
import singularity.Sgl
import singularity.ui.fragments.notification.Notification
import universecore.util.DataPackable

object AchievementDialog : BaseMenusDialog(IceStats.成就.localized(), IStyles.menusButton_host) {
  val achievements = Seq<Achievement>()
  private lateinit var list: Table

  init {
    Events.on(EventType.AchievementUnlockEvent::class.java) { e ->
      Sgl.ui.notificationFrag.notify(AchievementNotification(e.achievement.name, e.achievement.description))
    }
    Achievement("孢子进化论", "升级一次孢子单位", 2)
    Achievement("阴霾之下", "使用孢子建筑生产迷雾", 2)
    Achievement("生化危机", "一局内存在50个孢子单位", 3)
    Achievement("灾祸", "一直都无法避免,不是吗", 5)
    Achievement("外星医生", "杀死一只米戈", 2)
    Achievement("血田", "挺过一场血雨", 3)
    Achievement("异端末路", "建造5个传教者", 5)
    Achievement("生物引擎", "建筑泵腔", 1)
    Achievement("你看见我的腿了吗?", "生产带腿单位", 5)
    Achievement("世界树", "形成孢子网络", 5)
    Achievement("空间风暴I", "未知之处涌现的风暴", 8)
    Achievement("空间风暴II", "将秩序囚于混沌的牢笼", 10)
    Achievement("空间风暴III", "长梯下破碎的现实与你", 20)
  }

  override fun build(cont: Table) {
    cont.table { k ->
      k.table { t ->
        t.image(TextureRegionDrawable(IStyles.achievement)).scaling(Scaling.fit)
      }.height(170f).pad(20f).padTop(5f).padBottom(5f).row()

      k.table(Styles.grayPanel) { ta ->
        ta.table { img ->
          img.image().height(34f).color(IceColor.b4).growX()
          img.row()
          img.image().height(6f).color(IceColor.b4.cpy().mul(0.8f, 0.8f, 0.8f, 1f)).growX()
        }.growX().row()

        ta.table { jk ->

          jk.iPane { cns ->

            cns.iTableG { cn ->
              cn.top()
              cn.defaults().growX()

              cn.iTable(IStyles.paneBottom) {
                it.button("全部", Styles.nonet, ::allAchievement).height(40f).grow()
                it.button("已锁定", Styles.nonet) {
                  flunActions {
                    list.clearChildren()
                    achievements.select { it1 -> !it1.unlocked() }.forEach(::flunOne)
                  }
                }.height(40f).marginRight(-2f).grow()
                it.button("已完成", Styles.nonet) {
                  flunActions {
                    list.clearChildren()
                    achievements.select { it1 -> it1.unlocked() }.forEach(::flunOne)
                  }
                }.height(40f).grow()
              }.row()

              cn.table(IStyles.paneBottom) {
                it.table { table ->
                  table.image(IStyles.afehs).color(IceColor.b4).size(80f).padRight(4f)
                  val pross = (achievements.select { it1 -> it1.unlocked() }.size.toFloat() / achievements.size)
                  table.add(Label { "${Mathf.round(pross * 100f)}%" }.apply {
                    setFontScale(2f)
                  }).color(IceColor.b4)
                }.row()
                it.table { table ->
                  table.addCR({"已完成 ${achievements.select { achievement -> achievement.unlocked() }.size} 个 共 ${achievements.size} 个"})
                }.pad(2f)
              }.row()
              cn.addLine()

              cn.table(IStyles.paneBottom) {
                it.left()
                it.table { it1 ->
                  it1.image(IStyles.achievementHourglass).color(IceColor.b4).size(50f)
                }.padRight(10f)
                it.table { it1 ->
                  it1.left()
                  it1.addCR("进行中").growX().pad(2f)
                  it1.addCR("0").growX().pad(2f)
                }.grow()
              }.row()
              cn.addLine()

              cn.table(IStyles.paneBottom) {
                it.left()
                it.table { it1 ->
                  it1.image(IStyles.achievementGodQuality).color(IceColor.b4).size(50f)
                }.padRight(10f)
                it.table { it1 ->
                  it1.left()
                  it1.addCR("神质").growX().pad(2f).row()
                  it1.addCR({"${SettingValue.神质}"}).growX().pad(2f).row()
                }.grow()
              }.row()
              cn.addLine()
            }.growX()

          }.minWidth(400f).growY()

          jk.table { t2 ->

            t2.iPaneG {
              it.top()
              list = it
              //优先展示已解锁
              achievements.select { it1 -> it1.unlocked() }.forEach(::flunOne)
              achievements.select { it1 -> !it1.unlocked() }.forEach(::flunOne)

            }
          }.grow().padRight(30f)

        }.grow()

      }.grow()

    }.grow()
  }

  fun allAchievement() {
    flunActions {
      list.clearChildren()
      //优先展示已解锁
      achievements.select { it1 -> it1.unlocked() }.forEach(::flunOne)
      achievements.select { it1 -> !it1.unlocked() }.forEach(::flunOne)
    }
  }

  private fun flunActions(run: Runnable) {
    list.actions(Actions.alpha(0f, 0.2f), Actions.run(run::run), Actions.alpha(1f, 0.2f))
  }

  fun flunOne(ach: Achievement) {
    list.iTableGX(if (ach.unlocked()) IStyles.background101 else IStyles.background91) { b ->
      b.image(if (ach.unlocked()) IStyles.achievementUnlock else IStyles.achievementLock).size(80f).pad(5f)
      b.iTableG { b1 ->
        b1.add(Label(ach.name).colorR(if (ach.unlocked()) IceColor.b4 else Color.gray)).padTop(10f).row()
        b1.add(Label(ach.description).colorR(if (ach.unlocked()) IceColor.b4 else Color.gray)).growX().expandY().wrap()
      }
      if (ach.unlocked()&& SettingValue.启用调试模式) {
        b.button(Icon.trash, IStyles.button3) {
          ach.clearUnlock()
          b.actions(IceActions.moveToAlphaAction(b.width + 50f, b.y, 1f, 0f, Interp.pow2In), Actions.remove())
        }.size(40f).pad(12f).expandY().bottom()
      } else {
        b.table().size(40f).pad(12f).expandY()
      }
    }.minHeight(100f).pad(2f).margin(MenusDialog.backMargin + 2).row()
  }

  open class Achievement(var name: String, var description: String, var dot: Int) {
    private var unlocked = false

    init {
      achievements.add(this)
      unlocked = Core.settings.getBool("${DeepSpace.name}-achievement-$name", false)
    }

    fun unlocked() = unlocked
    fun unlock() {
      if (!unlocked) {
        unlocked = true
        Core.settings.put("${DeepSpace.name}-achievement-$name", true)
        Events.fire(EventType.AchievementUnlockEvent(this))
        SettingValue.神质+=dot
      }
    }

    fun clearUnlock() {
      if (unlocked) {
        unlocked = false
        Core.settings.put("${DeepSpace.name}-achievement-$name", false)
      }
    }
  }

  open class AchievementNotification(name: String, description: String) : Notification("成就解锁: $name", description) {
    companion object {
      const val typeID: Long = 12133159028768494L
      fun assign() {
        DataPackable.assignType(typeID) { args: Array<Any> ->
        }
      }
    }

    override fun getIcon() = IStyles.afehs.asDrawable()

    override fun activity() {
    }

    override fun buildWindow(table: Table?) {
    }

    override fun getIconColor(): Color {
      return IceColor.b4
    }

    override fun getTitleColor(): Color {
      return IceColor.b4
    }

    override fun getInformationColor(): Color {
      return IceColor.b4
    }

    override fun typeID() = typeID
  }
}