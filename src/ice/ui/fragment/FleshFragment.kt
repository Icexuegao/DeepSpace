package ice.ui.fragment

import arc.Core
import arc.flabel.FLabel
import arc.func.Boolp
import arc.graphics.Color
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import ice.library.type.meta.IceEffects
import ice.library.util.accessFloat
import ice.ui.colorR
import mindustry.Vars

object FleshFragment {
    var group: Group = WidgetGroup()
    var text = Seq<String>(String::class.java).apply {
        addAll("""
                都怪你
                你在哪
                我喜欢你
                捅死你喵
                为什么要给别人画贴图
                我哪里对你不好了
                不要走
                不要离开我好不好
                求你了
        """.trimIndent().split("\n"))
    }

    fun build(parent: Group) {
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp(Vars.ui.hudfrag::shown)
        parent.addChild(group)
    }

    fun addText() {
        for (i in 0 until 1000) {
            text()
        }
    }

    var FLabel.textSpeed by accessFloat("textSpeed")
    fun text() {
        val fLabel = FLabel("{shake}${text.random()}").colorR(Color.red)
        val d = fLabel.text.length * 0.3f
        fLabel.actions(Actions.delay(d), Actions.alpha(0f, 1f), Actions.remove())
        fLabel.scaleBy(5f)
        fLabel.textSpeed = 0.3f
        fLabel.setPosition(IceEffects.rand.nextFloat(Core.graphics.width.toFloat()),
            IceEffects.rand.nextFloat((Core.graphics.height).toFloat()))
        group.addChild(fLabel)
    }
}