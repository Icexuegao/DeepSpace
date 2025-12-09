package ice.library.scene.ui

import arc.Core
import arc.func.Prov
import arc.graphics.Color
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
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Table
import arc.util.Align
import arc.util.pooling.Pools
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.Fonts
import mindustry.ui.Styles
import kotlin.math.roundToInt

open class Dialog : Table {
    companion object {
        val defaultDialogStyle = DialogStyle().apply {
            stageBackground = Styles.black9
            titleFont = Fonts.def
            background = Tex.windowEmpty
            titleFontColor = Pal.accent
        }
        var defaultShowAction: Prov<Action> = Prov { Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interp.fade)) }
        var defaultHideAction: Prov<Action> = Prov { Actions.fadeOut(0.4f, Interp.fade) }
        val tmpPosition = Vec2()
        val tmpSize = Vec2()
        const val MOVE = 1 shl 5

        fun setHideAction(prov: Prov<Action>) {
            defaultHideAction = prov
        }

        fun setShowAction(prov: Prov<Action>) {
            defaultShowAction = prov
        }
    }

    protected val ignoreTouchDown = object : InputListener() {
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
            event.cancel()
            return false
        }
    }
    protected var edge = 0
    protected var dragging = false
    var isMovable = false
    var isModal = true
    var isResizable = false
    var center = true
    var resizeBorder = 8
    var keepWithinStage = true
    private var style: DialogStyle
    private var lastWidth = -1f
    private var lastHeight = -1f
    var previousKeyboardFocus: Element? = null
    var previousScrollFocus: Element? = null
    private var focusListener: FocusListener
    val root: Table

    constructor(style: DialogStyle = defaultDialogStyle) {
        this.style = style
        touchable = Touchable.enabled
        clip = true
        setStyle(style)
        setWidth(150f)
        setHeight(150f)
        addCaptureListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                toFront()
                return false
            }
        })
        addListener(object : InputListener() {
            private var startX = 0f
            private var startY = 0f
            private var lastX = 0f
            private var lastY = 0f

            private fun updateEdge(x: Float, y: Float) {
                var border = resizeBorder / 2f
                val width = width
                val height = height
                val padTop = marginTop
                val padRight = marginRight
                val right = width - padRight
                edge = 0
                if (isResizable && x >= marginLeft - border && x <= right + border && y >= marginBottom - border) {
                    if (x < marginLeft + border) edge = edge or Align.left
                    if (x > right - border) edge = edge or Align.right
                    if (y < marginBottom + border) edge = edge or Align.bottom
                    if (edge != 0) border += 25f
                    if (x < marginLeft + border) edge = edge or Align.left
                    if (x > right - border) edge = edge or Align.right
                    if (y < marginBottom + border) edge = edge or Align.bottom
                }
                if (isMovable && edge == 0 && y <= height && y >= height - padTop && x >= marginLeft && x <= right) edge = MOVE
            }

            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                if (button == KeyCode.mouseLeft) {
                    updateEdge(x, y)
                    dragging = edge != 0
                    startX = x
                    startY = y
                    lastX = x - width
                    lastY = y - height
                }
                return edge != 0 || isModal
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode) {
                dragging = false
            }

            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (!dragging) return
                var width = width
                var height = height
                var windowX = this@Dialog.x
                var windowY = this@Dialog.y
                val minWidth = minWidth
                val minHeight = minHeight
                val stage = scene
                val clampPosition = keepWithinStage && parent == stage.root

                if ((edge and MOVE) != 0) {
                    val amountX = x - startX
                    val amountY = y - startY
                    windowX += amountX
                    windowY += amountY
                }
                if ((edge and Align.left) != 0) {
                    var amountX = x - startX
                    if (width - amountX < minWidth) amountX = -(minWidth - width)
                    if (clampPosition && windowX + amountX < 0) amountX = -windowX
                    width -= amountX
                    windowX += amountX
                }
                if ((edge and Align.bottom) != 0) {
                    var amountY = y - startY
                    if (height - amountY < minHeight) amountY = -(minHeight - height)
                    if (clampPosition && windowY + amountY < 0) amountY = -windowY
                    height -= amountY
                    windowY += amountY
                }
                if ((edge and Align.right) != 0) {
                    var amountX = x - lastX - width
                    if (width + amountX < minWidth) amountX = minWidth - width
                    if (clampPosition && windowX + width + amountX > stage.width) amountX = stage.width - windowX - width
                    width += amountX
                }
                if ((edge and Align.top) != 0) {
                    var amountY = y - lastY - height
                    if (height + amountY < minHeight) amountY = minHeight - height
                    if (clampPosition && windowY + height + amountY > stage.height) amountY = stage.height - windowY - height
                    height += amountY
                }
                setBounds(
                    windowX.roundToInt().toFloat(), windowY.roundToInt().toFloat(), width.roundToInt().toFloat(), height.roundToInt().toFloat()
                )
            }

            override fun mouseMoved(event: InputEvent, x: Float, y: Float): Boolean {
                updateEdge(x, y)
                return isModal
            }

            override fun scrolled(event: InputEvent, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
                return isModal
            }

            override fun keyDown(event: InputEvent, keycode: KeyCode): Boolean {
                return isModal
            }

            override fun keyUp(event: InputEvent, keycode: KeyCode): Boolean {
                return isModal
            }

            override fun keyTyped(event: InputEvent, character: Char): Boolean {
                return isModal
            }
        })
        setOrigin(Align.center)
        root = Table()
        add(root).expand().fill()
        row()
        focusListener = object : FocusListener() {
            override fun keyboardFocusChanged(event: FocusEvent, actor: Element, focused: Boolean) {
                if (!focused) focusChanged(event)
            }

            override fun scrollFocusChanged(event: FocusEvent, actor: Element, focused: Boolean) {
                if (!focused) focusChanged(event)
            }

            private fun focusChanged(event: FocusEvent) {
                val stage = scene
                if (isModal && stage != null && stage.root.children.size > 0 && stage.root.children.peek() == this@Dialog) { // Dialog is top most actor.
                    val newFocusedActor = event.relatedActor
                    if (newFocusedActor != null && !newFocusedActor.isDescendantOf(this@Dialog) && !(newFocusedActor == previousKeyboardFocus || newFocusedActor == previousScrollFocus)) event.cancel()
                }
            }
        }

        shown { updateScrollFocus() }
    }

    fun setStyle(style: DialogStyle) {
        this.style = style
        setBackground(style.background)
        invalidateHierarchy()
    }

    private fun keepWithinStage() {
        if (!keepWithinStage) return
        keepInStage()
    }

    override fun act(delta: Float) {
        super.act(delta)

        if (scene != null) {
            keepWithinStage()
            if (center && !isMovable && this.actions.size == 0) {
                centerWindow()
            }
            // fire resize events.
            if (lastWidth >= 0 && lastHeight >= 0) {
                if (!Mathf.equal(lastWidth, scene.root.width) || !Mathf.equal(lastHeight, scene.root.height)) {
                    val e = Pools.obtain(SceneResizeEvent::class.java, ::SceneResizeEvent)
                    fire(e)
                    Pools.free(e)
                }
            }

            lastWidth = scene.root.width
            lastHeight = scene.root.height
        }
    }

    override fun draw() {
        val stage = scene
        if (stage.keyboardFocus == null) {
            // get top dialog in the scene and focus keyboard on that
            var highestDialog = -1
            val children = scene.root.children
            for (i in children.size - 1 downTo 0) {
                if (children[i] is Dialog) {
                    highestDialog = i
                    break
                }
            }
            stage.keyboardFocus = if (highestDialog == -1) this else children[highestDialog]
        }

        if (style.stageBackground != null) {
            stageToLocalCoordinates(tmpPosition.set(translation.x, translation.y))
            stageToLocalCoordinates(tmpSize.set(stage.width, stage.height))
            drawStageBackground(x + tmpPosition.x, y + tmpPosition.y, x + tmpSize.x, y + tmpSize.y)
        }

        super.draw()
    }

    protected fun drawStageBackground(x: Float, y: Float, width: Float, height: Float) {
        val color = this.color
        Draw.color(color.r, color.g, color.b, color.a * parentAlpha)
        style.stageBackground?.draw(x, y, width, height)
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Element? {
        val hit = super.hit(x, y, touchable)
        if (hit == null && isModal && (!touchable || this.touchable == Touchable.enabled)) return this
        return hit
    }

    /** Centers the dialog in the scene. */
    fun centerWindow() {
        setPosition(
            (((Core.scene.width - scene.marginLeft - scene.marginRight) - width) / 2).roundToInt().toFloat(), (((Core.scene.height - scene.marginTop - scene.marginBottom) - height) / 2).roundToInt().toFloat()
        )
    }

    fun isDragging(): Boolean = dragging

    fun updateScrollFocus() {
        var done = false
        Core.app.post {
            forEach { child ->
                if (done) return@forEach

                if (child is ScrollPane) {
                    Core.scene.scrollFocus = child
                    done = true
                }
            }
        }
    }

    override fun setScene(stage: Scene?) {
        if (stage == null) addListener(focusListener)
        else removeListener(focusListener)
        super.setScene(stage)
    }

    /** Adds a show() listener. */
    fun shown(run: Runnable) {
        addListener(object : VisibilityListener() {
            override fun shown(): Boolean {
                run.run()
                return false
            }
        })
    }

    /** Adds a hide() listener. */
    fun hidden(run: Runnable) {
        addListener(object : VisibilityListener() {
            override fun hidden(): Boolean {
                run.run()
                return false
            }
        })
    }

    /** Runs the callback when this dialog is resized or hidden. */
    fun resizedShown(run: Runnable) {
        resized(run)
        shown(run)
    }

    /** Adds a scene resize listener. */
    fun resized(run: Runnable) {
        resized(false, run)
    }

    /** Adds a scene resize listener, optionally invoking it immediately. */
    fun resized(invoke: Boolean, run: Runnable) {
        if (invoke) {
            run.run()
        }
        addListener(object : ResizeListener() {
            override fun resized() {
                run.run()
                // refocus scrollpanes automatically after a rebuild
                updateScrollFocus()
            }
        })
    }

    /** Adds a listener for back/escape keys to hide this dialog. */
    fun closeOnBack() {
        closeOnBack {}
    }

    fun closeOnBack(callback: Runnable) {
        keyDown { key ->
            if (key == KeyCode.escape || key == KeyCode.back) {
                Core.app.post { this.hide() }
                callback.run()
            }
        }
    }

    fun isShown(): Boolean = scene != null

    /** {@link #pack() Packs} the dialog and adds it to the stage with custom action which can be null for instant show */
    fun show(stage: Scene, action: Action?): Dialog {
        setOrigin(Align.center)
        clip = false
        isTransform = true

        fire(VisibilityEvent(false))

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
        stage.keyboardFocus = this
        stage.scrollFocus = this

        if (action != null) addAction(action)
        pack()

        return this
    }

    /** Shows this dialog if it was hidden, and vice versa. */
    fun toggle() {
        if (isShown()) {
            hide()
        } else {
            show()
        }
    }

    fun show(): Dialog = show(Core.scene)

    /** {@link #pack() Packs} the dialog and adds it to the stage, centered with default fadeIn action */
    fun show(stage: Scene): Dialog {
        show(stage, defaultShowAction.get())
        centerWindow()
        return this
    }

    /** Hides the dialog with the given action and then removes it from the stage. */
    fun hide(action: Action?) {
        fire(VisibilityEvent(true))
        val stage = scene
        if (stage != null) {
            removeListener(focusListener)
            if (previousKeyboardFocus != null && previousKeyboardFocus!!.scene == null) previousKeyboardFocus = null
            var actor = stage.keyboardFocus
            if (actor == null || actor.isDescendantOf(this)) stage.keyboardFocus = previousKeyboardFocus

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
        if (!isShown()) return
        setOrigin(Align.center)
        clip = false
        isTransform = true

        hide(defaultHideAction.get())
    }

    class DialogStyle : Style() {
        /** Optional. */
        var background: Drawable? = null
        var titleFont: Font? = null

        /** Optional. */
        var titleFontColor: Color = Color(1f, 1f, 1f, 1f)

        /** Optional. */
        var stageBackground: Drawable? = null
    }
}
