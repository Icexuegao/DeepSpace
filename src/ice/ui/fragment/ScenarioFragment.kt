package ice.ui.fragment

import arc.Core
import arc.func.Boolp
import arc.math.Interp
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.Label
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.util.Align
import arc.util.Log
import ice.library.scene.tex.Characters
import ice.library.scene.tex.IStyles
import mindustry.Vars
import mindustry.gen.Sounds

object ScenarioFragment {
    val foutTime: Float = 0.6f
    var group: Group = WidgetGroup()
    var cuScenario: ScenarioAct? = null
    var play = false
    fun build(parent: Group) {
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp(Vars.ui.hudfrag::shown)
        group.update {
            if (!play) {
                display()
            }
        }
        parent.addChild(group)
    }

    fun flun() {
        cuScenario = ScenarioAct()
    }

    fun display() {
        cuScenario ?: return
        val list = cuScenario!!.list
        if (!list.hasNext()) return
        val next = list.next()
        val image = Table()

        image.image(Characters.娜雅.gal).row()
        image.add(Label(Characters.娜雅.name).apply {

        }).expand()
        //  image.image(IFiles.findPng("男孩")).expand()
        image.pack()
        image.setPosition(0f, 100f + Core.graphics.height / 2)

        group.addChild(image)

        play = true
        val table = Table(IStyles.background23)
        group.addChild(table)
        table.margin(10f).marginLeft(25f).add(next.first).width(if (Vars.mobile) 270f else 400f).left().labelAlign(Align.left)
            .wrap()
        table.pack()
        table.setPosition(100f, 180f + Core.graphics.height / 2)

        image.actions(
            Actions.alpha(0f), Actions.run { Sounds.message.play() }, Actions.alpha(1f, 1f, Interp.smooth)
        )
        table.actions(
            Actions.alpha(0f), Actions.run { Sounds.message.play() }, Actions.alpha(1f, 1f, Interp.smooth)
        )
        var indx = 0
        var b = true
        table.update {
             indx++
            if (indx >= 180 && b) {
                b = false
                table.actions(
                    Actions.parallel(
                        Actions.alpha(0f, foutTime, Interp.smooth),
                        Actions.translateBy(0f, Scl.scl(-200f), foutTime, Interp.smooth)
                    ), Actions.run {
                        play = false
                        next.second()
                    }, Actions.remove()
                )

                image.actions(
                    Actions.parallel(
                        Actions.alpha(0f, foutTime, Interp.smooth),
                        Actions.translateBy(0f, Scl.scl(-200f), foutTime, Interp.smooth)
                    ), Actions.remove()
                )

            }
        }
    }

    class ScenarioAct {
        private val texts = LinkedHashMap<String, () -> Unit>()
        fun LinkedHashMap<String, () -> Unit>.putR(string: String, run: () -> Unit = {}) {
            put(string, run)
        }

        init {

            texts.putR("把你发配到这种地方真是便宜你了") { Log.info(11) }
            texts.putR("")
            texts.putR("哦对了,dlife那家伙被罚去德列浦了") { Log.info(44) }
            texts.putR("你就在这好好接受处罚吧")
        }

        val list = texts.toList().listIterator()
    }
}