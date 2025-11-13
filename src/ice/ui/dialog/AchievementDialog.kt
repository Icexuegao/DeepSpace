package ice.ui.dialog

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Label
import arc.struct.Seq
import arc.util.Scaling
import ice.Ice
import ice.library.EventType
import ice.library.meta.stat.IceStats
import ice.library.scene.element.BaseProgressBar
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IceColor
import ice.ui.colorR
import ice.ui.iPaneG
import ice.ui.iTableG
import ice.ui.iTableGX
import mindustry.gen.Icon

object AchievementDialog: BaseDialog(IceStats.成就.localized(), Icon.star){
    val achievements = Seq<Achievement>()
   override fun build() {
        cont.iTableG { t1 ->
            t1.table { t ->
                val findPng = TextureRegionDrawable(IStyles.achievement)
                t.image(findPng).scaling(Scaling.fit)
            }.pad(20f).row()
            t1.iTableGX(IStyles.background32) { table ->
                val pross = (achievements.select { it.unlocked() }.size.toFloat() / achievements.size)
                table.iTableGX{ table1 ->
                    table1.add(
                        Label { "已获得${achievements.select { it.unlocked() }.size}项成就,共${achievements.size}项" })
                        .color(IceColor.b4).expandX().left()
                    table1.add(Label {
                        "(${
                            Mathf.round(pross * 100f)
                        }%)"
                    })
                        .color(IceColor.b4).expandX().right()
                }.pad(5f).row()
                table.iTableGX { it ->
                    it.add(BaseProgressBar(IStyles.barBottlom, IStyles.barTop) { pross }.apply {
                        color.set(IceColor.b4)
                    }).grow()
                }.minHeight(60f)
            }.margin(21f).pad(20f).row()
            t1.iPaneG { k ->
                k.setRowsize(2)
                k.top()
                val buildAchievements: (Achievement) -> Unit = { ach ->
                    k.iTableGX(if (ach.unlocked()) IStyles.background101 else IStyles.background91) { b ->
                        b.image(if (ach.unlocked()) IStyles.achievementUnlock else IStyles.achievementLock).size(100f)
                            .pad(5f)
                        b.iTableG { b1 ->
                            b1.add(Label(ach.name).colorR(if (ach.unlocked()) IceColor.b4 else Color.gray)).padTop(10f)
                                .row()
                            b1.add(Label(ach.description).colorR(if (ach.unlocked()) IceColor.b4 else Color.gray))
                                .growX()
                                .expandY().wrap()
                        }
                        if (ach.unlocked()) {
                            b.button(Icon.trash, IStyles.button3) {
                                ach.clearUnlock()
                                build()
                            }.size(40f).pad(12f).expandY().bottom()

                        } else {
                            b.table().size(40f).pad(12f).expandY()
                        }
                    }.minSize(620f, 140f).pad(2f).margin(MenusDialog.backMargin + 2)
                }
                //优先展示已解锁
                achievements.select { it.unlocked() }.forEach(buildAchievements)
                achievements.select { !it.unlocked() }.forEach(buildAchievements)
            }.grow()
        }
    }

    init {
        Achievement("孢子进化论", "升级一次孢子单位")
        Achievement("世界树", "形成孢子网络")
        Achievement("阴霾之下", "使用孢子建筑生产迷雾")
        Achievement("生化危机", "一局内存在50个孢子单位")
        Achievement("狂乱的鸡尾酒", "拥有所有状态效果")
        Achievement("灾祸", "一直都无法避免,不是吗")
        Achievement("外星医生", "杀死一只米戈")
        Achievement("血田", "挺过一场血雨")
        Achievement("异端末路", "建造传教者")
        Achievement("阴霾之下", "用孢子建筑生产迷雾")
        Achievement("生物引擎", "建筑泵腔")
        Achievement("你看见我的腿了吗?", "生产带腿单位")
        Achievement("世界树", "形成孢子网络")
        Achievement("空间风暴I", "未知之处涌现的风暴")
        Achievement("空间风暴II", "将秩序囚于混沌的牢笼")
        Achievement("空间风暴III", "长梯下破碎的现实与你")
    }

    class Achievement(var name: String, var description: String) {
        private var unlocked = false

        init {
            achievements.add(this)
            unlocked = Core.settings.getBool("${Ice.name}-achievement-$name", false)
        }

        fun unlocked() = unlocked
        fun unlock() {
            if (!unlocked) {
                unlocked = true
                Core.settings.put("${Ice.name}-achievement-$name", true)
                build()
                Events.fire(EventType.AchievementUnlockEvent(this))
            }
        }

        fun clearUnlock() {
            if (unlocked) {
                unlocked = false
                Core.settings.put("${Ice.name}-achievement-$name", false)
            }
        }
    }

}