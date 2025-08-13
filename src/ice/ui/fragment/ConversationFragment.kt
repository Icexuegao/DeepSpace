package ice.ui.fragment

import arc.func.Boolp
import arc.math.Interp
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.style.Drawable
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles
import ice.library.scene.action.IceActions
import ice.library.scene.tex.Colors
import ice.library.struct.asDrawable
import ice.vars.UI
import mindustry.Vars
import mindustry.gen.Tex

object ConversationFragment {
    private var group: Group = WidgetGroup()
    var voiceoverFragment = IFiles.findIcePng("voiceoverFragment").asDrawable()
    val heightEdge = UI.cgheight / 4f
    fun build(parent: Group) {
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp(Vars.ui.hudfrag::shown)
        parent.addChild(group)
    }

    fun blankScreenBottom(
        delayStart: Float = 2f,
        interpOut: Interp = Interp.pow2Out,
        delay: Float = 4f,
        delayEnd: Float = 2f,
        interpIn: Interp = Interp.pow2In,
        run: Runnable = Runnable {}
    ) {
        Image(voiceoverFragment).apply {
            setSize(UI.cgwidth, heightEdge)
            setPosition(0f, -heightEdge)
            actions(Actions.alpha(0f), IceActions.moveToAlphaAction(0f, 0f, delayStart, 1f, interpOut),
                Actions.run(run), Actions.delay(delay),
                IceActions.moveToAlphaAction(0f, -heightEdge, delayEnd, 1f, interpIn), Actions.remove())
            group.addChild(this)
        }
    }

    fun blankScreen(
        delayStart: Float = 1f,
        delay: Float = 4f,
        delayEnd: Float = 2f,
        interp: Interp = Interp.pow2Out,
        run: Runnable = Runnable {}
    ) {
        Image(voiceoverFragment).apply {
            setSize(UI.cgwidth, UI.cgheight)
            setPosition(0f, 0f)
            actions(Actions.alpha(0f), Actions.alpha(1f, delayStart), Actions.run(run), Actions.delay(delay),
                Actions.alpha(0f, delayEnd, interp), Actions.remove())
            group.addChild(this)
        }
    }

    fun blankScreenText(
        title: String? = null,
        text: String = "",
        delayStart: Float = 1f,
        delay: Float = 4f,
        delayEnd: Float = 2f,
        interp: Interp = Interp.pow2Out,
    ) {
        blankScreen(delayStart, delay + (text.split("").size * 8) / 60f, delayEnd, interp) {
            showText(title, text)
        }
    }

    fun showText(
        title: String? = null,
        text: String,
        delayStart: Float = 1f,
        delay: Float = 2f,
        delayEnd: Float = 1f,
        speed: Float = 8f,
        x: Float = UI.cgwidth / 2,
        y: Float = UI.cgheight / 2,
        back: Drawable? = null
    ) {
        val split = text.split("")
        val iterator = Seq(split.toTypedArray()).iterator()
        val interval = Interval(1)
        val fLabel = Label("").apply {
            update {
                if (interval[speed]) {
                    if (iterator.hasNext()) {
                        getText().append(iterator.next())
                        invalidateHierarchy()
                    }
                }
            }
        }
        val table = Table(back).apply {
            margin(20f)
            title?.let {
                add(Label(it).apply {
                    setFontScale(1.5f)
                }).color(Colors.b4).pad(10f).row()
            }
            add(fLabel).color(Colors.b4).row()
            image(Tex.whiteui).color(Colors.b4).growX()
            update {
                pack()
                setPosition(x - width / 2, y)
            }
            actions(Actions.alpha(0f), Actions.alpha(1f, delayStart), Actions.delay((split.size * speed) / 60),
                Actions.delay(delay), Actions.alpha(0f, delayEnd), Actions.remove())
        }
        group.addChild(table)
    }
}