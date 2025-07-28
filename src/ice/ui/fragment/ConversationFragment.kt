package ice.ui.fragment

import arc.Core
import arc.func.Boolp
import arc.math.Interp
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles
import ice.library.scene.action.IceActions
import ice.library.scene.texs.Colors
import ice.library.struct.asDrawable
import mindustry.Vars

object ConversationFragment {
    var group: Group = WidgetGroup()
    lateinit var table: Table
    var juqing = Seq<String>().apply {
       "我不喜欢你\n我喜欢你\n你喜欢我\n你不喜欢我".split("\n").forEach {
            add(it)
        }
    }.iterator()

    fun build(parent: Group) {
        group.name = "conversationFragment"
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp(Vars.ui.hudfrag::shown)
        parent.addChild(group)
        val asDrawable = IFiles.findIcePng("voiceoverFragment").asDrawable()
        val fl = Core.graphics.height.toFloat() / 4
        var iterator = flun("")
        val image = Image(asDrawable).apply {
            setSize(Core.graphics.width.toFloat(), fl)
            setPosition(0f, -fl)
            actions(Actions.alpha(0f), IceActions.moveToAlphaAction(0f, 0f, 2f, 0.9f, Interp.pow2Out))
            tapped {
                if (juqing.hasNext()) {
                    iterator = flun(juqing.next())
                }
            }
        }
        val fLabel = Label("").apply {
            setColor(Colors.b4)
            val interval = Interval(1)
            var tem = ""

            update {
                if (interval[8f]) {
                    if (iterator.hasNext()) {
                        tem += iterator.next()
                    } else {
                        tem = ""
                    }
                }
                if (!tem.isEmpty()) setText(tem)
            }
        }
        val table = Table().apply {
            setSize(image.width, image.height)
            add(fLabel)
        }
        Stack(image, table).apply {
            setSize(image.width, image.height)
            group.addChild(this)
        }
    }

    fun flun(string: String): Iterator<String> {
        val sseq = Seq<String>()
        string.split("").forEach {
            sseq.add(it)
        }
        return sseq.iterator()
    }
}