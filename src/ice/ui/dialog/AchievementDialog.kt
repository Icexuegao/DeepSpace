package ice.ui.dialog

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Label
import arc.struct.Seq
import ice.Ice
import ice.library.EventType
import ice.library.scene.texs.Colors
import ice.library.scene.texs.Texs
import ice.library.struct.log
import ice.ui.*
import mindustry.gen.Icon

object AchievementDialog {
    val cont = MenusDialog.cont
    private val achievements = Seq<Achievement>()
    fun build() {
        cont.clearChildren()
        cont.iPaneG { t1 ->
            t1.table { t ->
                val findPng = TextureRegionDrawable(Texs.achievement, 0.5f)
                t.image(findPng)
            }.row()

            t1.iTable { k ->
                k.setRowsize(2)
                achievements.forEach { ach ->
                    k.iTableGX(if (ach.unlocked()) Texs.background101 else Texs.background91) { b ->
                        b.image(if (ach.unlocked()) Texs.achievementUnlock else Texs.achievementLock).size(100f).pad(5f)
                        b.iTableG { b1 ->
                            b1.add(Label(ach.name).colorR(if (ach.unlocked()) Colors.b4 else Color.gray)).padTop(10f)
                                .row()
                            b1.add(Label(ach.description).colorR(if (ach.unlocked()) Colors.b4 else Color.gray)).growX()
                                .expandY().wrap()
                        }
                        if (ach.unlocked()) {

                            b.button(Icon.trash, Texs.button3) {
                                    ach.clearUnlock()
                                    build()
                                }.size(40f).pad(12f).expandY().bottom()

                        }else{
                            b.table().size(40f).pad(12f).expandY()
                        }

                    }.size(620f, 140f).pad(2f).margin(MenusDialog.backMargin + 2)
                }
            }.grow()
        }
    }

    init {
        Achievement("灾祸", "一直都无法避免,不是吗").apply {
            unlock()

        }
        Achievement("外星医生", "杀死一只米戈").apply {
            unlock()
        }
        Achievement("血田", "挺过一场血雨")
        Achievement("异端末路", "建造传教者")
        Achievement("空间风暴I", "未知之处涌现的风暴").apply {
            unlock()
        }
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