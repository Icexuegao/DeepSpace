package ice.library.scene.element

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Font
import arc.input.KeyCode
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.scene.Action
import arc.scene.Element
import arc.scene.Scene
import arc.scene.actions.Actions
import arc.scene.event.*
import arc.scene.style.Drawable
import arc.scene.style.Style
import arc.scene.ui.Label
import arc.scene.ui.Label.LabelStyle
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Align
import arc.util.pooling.Pools
import ice.library.scene.tex.Colors
import ice.library.scene.tex.IStyles
import mindustry.gen.Sounds
import mindustry.ui.Fonts
import kotlin.math.roundToInt

class IceDialog(
    title: String = ""
) : Table() {
    private var ignoreTouchDown: InputListener = object : InputListener() {
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
            event.cancel()
            return false
        }
    }
    var isMovable: Boolean = false
    var isModal: Boolean = true
    var isCentered: Boolean = true
    private var resizeBorder: Int = 8
    private var keepWithinStage: Boolean = true
    private var style: DialogStyle = DialogStyle()
    var lastWidth = -1f
    var lastHeight = -1f
    var previousKeyboardFocus: Element? = null
    var previousScrollFocus: Element? = null
    var focusListener: FocusListener
    var cont: Table
    var buttons: Table
    val title: Label
    val titleTable: Table

    init {
        touchable = Touchable.enabled
        clip = true
        this.title = Label(title, LabelStyle(style.titleFont, style.titleFontColor))
        this.title.setEllipsis(true)

        titleTable = Table()
        titleTable.add(this.title).expandX().fillX().minWidth(0f)
        add(titleTable).growX().row()

        setStyle(style)
        setWidth(150f)
        setHeight(150f)

        addCaptureListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                toFront()
                return false
            }
        })

        setOrigin(Align.center)
        defaults().pad(3f)
        add(Table().also { cont = it }).expand().fill()
        row()
        add(Table().also { buttons = it }).fillX()

        margin(8f)

        cont.defaults().pad(3f)
        buttons.defaults().pad(3f)

        focusListener = object : FocusListener() {
            override fun keyboardFocusChanged(event: FocusEvent, actor: Element, focused: Boolean) {
                if (!focused) focusChanged(event)
            }

            override fun scrollFocusChanged(event: FocusEvent, actor: Element, focused: Boolean) {
                if (!focused) focusChanged(event)
            }

            private fun focusChanged(event: FocusEvent) {
                val stage = scene
                if (isModal && stage != null && stage.root.children.size > 0 && stage.root.children.peek() === this@IceDialog) { // Dialog is top most actor.
                    val newFocusedActor = event.relatedActor
                    if (newFocusedActor != null && !newFocusedActor.isDescendantOf(this@IceDialog) &&
                        !(newFocusedActor == previousKeyboardFocus || newFocusedActor == previousScrollFocus)
                    ) event.cancel()
                }
            }
        }

        shown { this.updateScrollFocus() }

        setFillParent(true)
        this.title.setAlignment(Align.center)
        titleTable.row()
        titleTable.image(mindustry.gen.Tex.whiteui, Colors.b4).growX().height(3f).pad(4f)

        hidden { Sounds.back.play() }
    }

    fun setStyle(style: DialogStyle) {
        this.style = style
        background = style.background
        //title.setStyle(new LabelStyle(style.titleFont, style.titleFontColor));
        invalidateHierarchy()
    }

    fun keepWithinStage() {
        if (!keepWithinStage) return
        keepInStage()
    }

    override fun act(delta: Float) {
        super.act(delta)

        if (scene != null) {
            keepWithinStage()
            if (isCentered && !isMovable && actions.size == 0) {
                centerWindow()
            }
            //fire resize events.
            if (lastWidth >= 0 && lastHeight >= 0) {
                if (!Mathf.equal(lastWidth, Core.scene.root.width) || !Mathf.equal(lastHeight,
                        Core.scene.root.height)
                ) {
                    val e = Pools.obtain(SceneResizeEvent::class.java
                    ) { SceneResizeEvent() }
                    fire(e)
                    Pools.free(e)
                }
            }

            lastWidth = Core.scene.root.width
            lastHeight = Core.scene.root.height
        }
    }

    override fun draw() {
        val stage = scene

        if (stage.keyboardFocus == null) {
            //get top dialog in the scene and focus keyboard on that
            var highestDialog = -1
            val children: Seq<Element> = Core.scene.root.children
            for (i in children.size - 1 downTo 0) {
                if (children[i] is IceDialog) {
                    highestDialog = i
                    break
                }
            }
            stage.setKeyboardFocus(if (highestDialog == -1) this else children[highestDialog])
        }

        if (style.stageBackground != null) {
            stageToLocalCoordinates(tmpPosition.set(translation.x, translation.y))
            stageToLocalCoordinates(tmpSize.set(stage.width, stage.height))
            drawStageBackground(x + tmpPosition.x, y + tmpPosition.y, x + tmpSize.x, y + tmpSize.y)
        }

        super.draw()
    }

    private fun drawStageBackground(x: Float, y: Float, width: Float, height: Float) {
        val color = this.color
        Draw.color(color.r, color.g, color.b, color.a * parentAlpha)
        style.stageBackground!!.draw(x, y, width, height)
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Element {
        val hit = super.hit(x, y, touchable)
        if (hit == null && isModal && (!touchable || this.touchable == Touchable.enabled)) return this
        return hit
    }

    /**使对话框在场景中居中。  */
    fun centerWindow() {
        setPosition(
            (((Core.scene.width - Core.scene.marginLeft - Core.scene.marginRight) - getWidth()) / 2).roundToInt()
                .toFloat(),
            (((Core.scene.height - Core.scene.marginTop - Core.scene.marginBottom) - getHeight()) / 2).roundToInt()
                .toFloat())
    }

    fun setKeepWithinStage(keepWithinStage: Boolean) {
        this.keepWithinStage = keepWithinStage
    }

    fun setResizeBorder(resizeBorder: Int) {
        this.resizeBorder = resizeBorder
    }

    fun updateScrollFocus() {
        val done = booleanArrayOf(false)

        Core.app.post {
            forEach { child: Element? ->
                if (done[0]) return@forEach
                if (child is ScrollPane) {
                    Core.scene.scrollFocus = child
                    done[0] = true
                }
            }
        }
    }

    /** Adds a show() listener.  */
    fun shown(run: Runnable) {
        addListener(object : VisibilityListener() {
            override fun shown(): Boolean {
                run.run()
                return false
            }
        })
    }

    fun addCloseButton(width: Float = 210f) {
        buttons.defaults().size(width, 64f)

        buttons.button("返回", IStyles.rootCleanButton,::hide).size(width, 64f)

        addCloseListener()
    }

    fun addCloseListener() {
        closeOnBack()
    }

    /** Adds a hide() listener.  */
    fun hidden(run: Runnable) {
        addListener(object : VisibilityListener() {
            override fun hidden(): Boolean {
                run.run()
                return false
            }
        })
    }

    /** Runs the callback when this dialog is resized or hidden.  */
    fun resizedShown(run: Runnable) {
        resized(run)
        shown(run)
    }

    /** Adds a scene resize listener.  */
    fun resized(run: Runnable) {
        resized(false, run)
    }

    /** 添加场景调整大小侦听器，可选择立即调用它。 */
    fun resized(invoke: Boolean, run: Runnable) {
        if (invoke) {
            run.run()
        }
        addListener(object : ResizeListener() {
            override fun resized() {
                run.run()
                //refocus scrollpanes automatically after a rebuild
                updateScrollFocus()
            }
        })
    }

    /** 添加 Back/Esc 键的侦听器以隐藏此对话框。 */
    @JvmOverloads
    fun closeOnBack(callback: Runnable = Runnable {}) {
        keyDown { key: KeyCode ->
            if (key == KeyCode.escape || key == KeyCode.back) {
                Core.app.post { this.hide() }
                callback.run()
            }
        }
    }

    val isShown: Boolean
        get() = scene != null

    /** [Packs][.pack] the dialog and adds it to the stage with custom action which can be null for instant show  */
    fun show(stage: Scene, action: Action?): IceDialog {
        setOrigin(Align.center)
        clip = false
        isTransform = true

        this.fire(VisibilityEvent(false))

        clearActions()
        removeCaptureListener(ignoreTouchDown)

        previousKeyboardFocus = null
        var actor = stage.keyboardFocus
        if (actor != null && !actor.isDescendantOf(this)) previousKeyboardFocus = actor

        previousScrollFocus = null
        actor = stage.scrollFocus
        if (actor != null && !actor.isDescendantOf(this)) previousScrollFocus = actor

        pack()
        stage.add(this)
        stage.setKeyboardFocus(this)
        stage.setScrollFocus(this)

        if (action != null) addAction(action)
        pack()

        return this
    }

    fun show(): IceDialog {
        return show(Core.scene)
    }

    /** [Packs][.pack] the dialog and adds it to the stage, centered with default fadeIn action  */
    fun show(stage: Scene): IceDialog {
        show(stage, defaultShowAction.get())
        centerWindow()
        return this
    }

    /** Hides the dialog with the given action and then removes it from the stage.  */
    fun hide(action: Action?) {
        this.fire(VisibilityEvent(true))
        val stage = scene
        if (stage != null) {
            removeListener(focusListener)
            if (previousKeyboardFocus != null && previousKeyboardFocus!!.scene == null) previousKeyboardFocus = null
            var actor = stage.keyboardFocus
            if (actor == null || actor.isDescendantOf(this)) stage.setKeyboardFocus(previousKeyboardFocus)

            if (previousScrollFocus != null && previousScrollFocus!!.scene == null) previousScrollFocus = null
            actor = stage.scrollFocus
            if (actor == null || actor.isDescendantOf(this)) stage.scrollFocus = previousScrollFocus
        }
        if (action != null) {
            addCaptureListener(ignoreTouchDown)
            addAction(Actions.sequence(action, Actions.removeListener(ignoreTouchDown, true), Actions.remove()))
        } else remove()
    }

    /**
     * Hides the dialog. Called automatically when a button is clicked. The default implementation fades out the dialog over 400
     * milliseconds.
     */
    fun hide() {
        if (!isShown) return
        setOrigin(Align.center)
        clip = false
        isTransform = true

        hide(defaultHideAction.get())
    }

    class DialogStyle : Style() {
        /** Optional.  */
        var background: Drawable = IStyles.background121
        var titleFont: Font = Fonts.def

        /** Optional.  */
        var titleFontColor = Colors.b4

        /** Optional.  */
        var stageBackground: Drawable? = null
    }

    companion object {
        private var defaultShowAction = Prov<Action> {
            Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interp.fade))
        }
        private var defaultHideAction = Prov<Action> { Actions.fadeOut(0.4f, Interp.fade) }
        private val tmpPosition = Vec2()
        private val tmpSize = Vec2()
        fun setHideAction(prov: Prov<Action>) {
            defaultHideAction = prov
        }

        fun setShowAction(prov: Prov<Action>) {
            defaultShowAction = prov
        }
    }
}