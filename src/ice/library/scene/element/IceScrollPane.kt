package ice.library.scene.element

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.ScissorStack
import arc.input.KeyCode
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Rect
import arc.math.geom.Vec2
import arc.scene.Element
import arc.scene.event.ElementGestureListener
import arc.scene.event.InputEvent
import arc.scene.event.InputEvent.InputEventType
import arc.scene.event.InputListener
import arc.scene.event.SceneEvent
import arc.scene.style.Drawable
import arc.scene.ui.ScrollPane.ScrollPaneStyle
import arc.scene.ui.Slider
import arc.scene.ui.TextField
import arc.scene.ui.layout.WidgetGroup
import arc.scene.utils.Cullable
import mindustry.ui.Styles
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class IceScrollPane() : WidgetGroup() {
    private val hScrollBounds = Rect()
    private val vScrollBounds = Rect()
    private val hKnobBounds = Rect()
    private val vKnobBounds = Rect()
    private val lastPoint = Vec2()
    private val widgetAreaBounds = Rect()
    private val widgetCullingArea = Rect()
    private val scissorBounds = Rect()
    private var disableX = false
    private var disableY: Boolean = false
    private var scrollX = false
    private var scrollY = false
    private var vScrollOnRight = true
    private var hScrollOnBottom = true
    private var amountX = 0f
    var amountY = 0f
    private var visualAmountX = 0f
    private var visualAmountY = 0f
    private var maxX = 0f
    private var maxY = 0f
    private var touchScrollH = false
    private var touchScrollV = false
    private var areaWidth = 0f
    private var areaHeight = 0f
    private var fadeAlpha = 1f
    private var fadeAlphaSeconds = 1f
    private var fadeDelay = 0f
    private var fadeDelaySeconds = 1f
    private var cancelTouchFocus = true
    private var flickScroll = true
    private var velocityX = 0f
    private var velocityY = 0f
    private var flingTimer = 0f
    private var flingTime = 1f
    private var draggingPointer = -1
    private lateinit var style: ScrollPaneStyle
    private var widget: Element? = null
    private lateinit var flickScrollListener: ElementGestureListener
    private var fadeScrollBars = false
    private var smoothScrolling: Boolean = true
    private var overscrollX = true
    private var overscrollY: Boolean = true
    private var overscrollDistance = 50f
    private var overscrollSpeedMin = 30f
    private var overscrollSpeedMax = 200f
    private var forceScrollX = false
    private var forceScrollY = false
    private var clamp = true
    private var scrollbarsOnTop = false
    private var variableSizeKnobs = true
    private var clip = true

    constructor(widget: Element) : this(widget, Styles.noBarPane)

    constructor(widget: Element, style: ScrollPaneStyle) : this() {
        requireNotNull(style) { "style cannot be null." }
        this.style = style
        setWidget(widget)
        setSize(150f, 150f)
        isTransform = true
        //拖拽
        addCaptureListener(object : InputListener() {
            private var handlePosition = 0f
            override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Element?) {
                requestScroll()
            }

            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                if (draggingPointer != -1) return false
                if (pointer == 0 && button != KeyCode.mouseLeft) return false
                requestScroll()
                if (!flickScroll) resetFade()
                if (fadeAlpha == 0f) return false
                if (scrollX && hScrollBounds.contains(x, y)) {
                    event.stop()
                    resetFade()
                    if (hKnobBounds.contains(x, y)) {
                        lastPoint[x] = y
                        handlePosition = hKnobBounds.x
                        touchScrollH = true
                        draggingPointer = pointer
                        return true
                    }
                    setScrollX(amountX + areaWidth * if (x < hKnobBounds.x) -1 else 1)
                    return true
                }
                if (scrollY && vScrollBounds.contains(x, y)) {
                    event.stop()
                    resetFade()
                    if (vKnobBounds.contains(x, y)) {
                        lastPoint[x] = y
                        handlePosition = vKnobBounds.y
                        touchScrollV = true
                        draggingPointer = pointer
                        return true
                    }
                    setScrollY(amountY + areaHeight * if (y < vKnobBounds.y) 1 else -1)
                    return true
                }
                return false
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode) {
                if (pointer != draggingPointer) return
                cancel()
            }

            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (pointer != draggingPointer) return
                if (touchScrollH) {
                    val delta = x - lastPoint.x
                    var scrollH = handlePosition + delta
                    handlePosition = scrollH
                    scrollH = max(hScrollBounds.x, scrollH)
                    scrollH = min(
                        (hScrollBounds.x + hScrollBounds.width - hKnobBounds.width), scrollH
                    )
                    val total = hScrollBounds.width - hKnobBounds.width
                    if (total != 0f) setScrollPercentX((scrollH - hScrollBounds.x) / total)
                    lastPoint[x] = y
                } else if (touchScrollV) {
                    val delta = y - lastPoint.y
                    var scrollV = handlePosition + delta
                    handlePosition = scrollV
                    scrollV = max(vScrollBounds.y, scrollV)
                    scrollV = min((vScrollBounds.y + vScrollBounds.height - vKnobBounds.height), scrollV)
                    val total = vScrollBounds.height - vKnobBounds.height
                    if (total != 0f) setScrollPercentY(1 - (scrollV - vScrollBounds.y) / total)
                    lastPoint[x] = y
                }
            }

            override fun mouseMoved(event: InputEvent, x: Float, y: Float): Boolean {
                if (!flickScroll) resetFade()
                requestScroll()
                return false
            }
        })
        flickScrollListener = object : ElementGestureListener() {
            override fun pan(event: InputEvent, x: Float, y: Float, deltaX: Float, deltaY: Float) {
                resetFade()
                amountX -= deltaX
                amountY += deltaY
                clamp()
                if (cancelTouchFocus && (scrollX && deltaX != 0f || scrollY && deltaY != 0f)) cancelTouchFocus()
            }

            override fun fling(event: InputEvent, x: Float, y: Float, button: KeyCode) {
                if (abs(x.toDouble()) > 150 && scrollX) {
                    flingTimer = flingTime
                    velocityX = x
                    if (cancelTouchFocus) cancelTouchFocus()
                }
                if (abs(y.toDouble()) > 150 && scrollY) {
                    flingTimer = flingTime
                    velocityY = -y
                    if (cancelTouchFocus) cancelTouchFocus()
                }
            }

            override fun handle(event: SceneEvent): Boolean {
                if (super.handle(event)) {
                    if ((event as InputEvent).type == InputEventType.touchDown) {
                        flingTimer = 0f
                    }
                    return true
                }
                return false
            }
        }
        addListener(flickScrollListener)
        addListener(object : InputListener() {
            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Element?) {
                if (toActor != this@IceScrollPane) {
                    scene?.setScrollFocus(null)
                }
                super.exit(event, x, y, pointer, toActor)
            }

            override fun scrolled(event: InputEvent, x: Float, y: Float, sx: Float, sy: Float): Boolean {
                resetFade()
                if (scrollY) setScrollY(amountY + getMouseWheelY() * sy)
                if (scrollX) setScrollX(amountX + getMouseWheelX() * sx)
                return scrollX || scrollY
            }
        })
        addCaptureListener(object : InputListener() {
            var on = false
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode): Boolean {
                val actor: Element? = this@IceScrollPane.hit(x, y, true)
                on = flickScroll
                if ((actor is Slider || actor is TextField) && on) {
                    this@IceScrollPane.setFlickScroll(false)
                    return true
                }
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode) {
                if (on) {
                    this@IceScrollPane.setFlickScroll(true)
                }
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }

    fun resetFade() {
        fadeAlpha = fadeAlphaSeconds
        fadeDelay = fadeDelaySeconds
    }

    /**
     * Cancels the stage's touch focus for all listeners except this scroll pane's flick scroll listener. This causes any widgets
     * inside the scrollpane that have received touchDown to receive touchUp.
     * @see .setCancelTouchFocus
     */
    fun cancelTouchFocus() {
        scene?.cancelTouchFocusExcept(flickScrollListener, this)
    }

    /**如果当前正在通过跟踪触摸向下滚动，请停止滚动。 */
    fun cancel() {
        draggingPointer = -1
        touchScrollH = false
        touchScrollV = false
        flickScrollListener.gestureDetector.cancel()
    }

    fun clamp() {
        if (!clamp) return
        scrollX(
            if (overscrollX) Mathf.clamp(amountX, -overscrollDistance, maxX + overscrollDistance) else Mathf.clamp(
                amountX, 0f, maxX
            )
        )
        scrollY(
            if (overscrollY) Mathf.clamp(amountY, -overscrollDistance, maxY + overscrollDistance) else Mathf.clamp(
                amountY, 0f, maxY
            )
        )
    }

    /**
     * Returns the scroll pane's style. Modifying the returned style may not have an effect until
     * [.setStyle] is called.
     */
    fun getStyle(): ScrollPaneStyle {
        return style
    }

    fun setStyle(style: ScrollPaneStyle?) {
        requireNotNull(style) { "style cannot be null." }
        this.style = style
        invalidateHierarchy()
    }

    override fun act(delta: Float) {
        super.act(delta)
        val panning = flickScrollListener.gestureDetector.isPanning
        var animating = false
        if (fadeAlpha > 0 && fadeScrollBars && !panning && !touchScrollH && !touchScrollV) {
            fadeDelay -= delta
            if (fadeDelay <= 0) fadeAlpha = max(0.0, (fadeAlpha - delta).toDouble()).toFloat()
            animating = true
        }
        if (flingTimer > 0) {
            resetFade()
            val alpha = flingTimer / flingTime
            amountX -= velocityX * alpha * delta
            amountY -= velocityY * alpha * delta
            clamp()

            // Stop fling if hit overscroll distance.
            if (amountX == -overscrollDistance) velocityX = 0f
            if (amountX >= maxX + overscrollDistance) velocityX = 0f
            if (amountY == -overscrollDistance) velocityY = 0f
            if (amountY >= maxY + overscrollDistance) velocityY = 0f
            flingTimer -= delta
            if (flingTimer <= 0) {
                velocityX = 0f
                velocityY = 0f
            }
            animating = true
        }
        if (smoothScrolling && flingTimer <= 0 && !panning &&  //
            // Scroll smoothly when grabbing the scrollbar if one pixel of scrollbar movement is > 10% of the scroll area.
            ((!touchScrollH || scrollX && maxX / (hScrollBounds.width - hKnobBounds.width) > areaWidth * 0.1f) //
                    && (!touchScrollV || scrollY && maxY / (vScrollBounds.height - vKnobBounds.height) > areaHeight * 0.1f)) //
        ) {
            if (visualAmountX != amountX) {
                if (visualAmountX < amountX) visualScrollX(
                    min(
                        amountX, (visualAmountX + max(
                            (200 * delta), ((amountX - visualAmountX) * 7 * delta)
                        ))
                    )
                ) else visualScrollX(
                    max(
                        amountX, (visualAmountX - max(
                            (200 * delta), ((visualAmountX - amountX) * 7 * delta)
                        ))
                    )
                )
                animating = true
            }
            if (visualAmountY != amountY) {
                if (visualAmountY < amountY) visualScrollY(
                    min(
                        amountY, (visualAmountY + max(
                            (200 * delta), ((amountY - visualAmountY) * 7 * delta)
                        ))
                    )
                ) else visualScrollY(
                    max(
                        amountY, (visualAmountY - max(
                            (200 * delta), ((visualAmountY - amountY) * 7 * delta)
                        ))
                    )
                )
                animating = true
            }
        } else {
            if (visualAmountX != amountX) visualScrollX(amountX)
            if (visualAmountY != amountY) visualScrollY(amountY)
        }
        if (!panning) {
            if (overscrollX && scrollX) {
                if (amountX < 0) {
                    resetFade()
                    amountX += ((overscrollSpeedMin + (overscrollSpeedMax - overscrollSpeedMin) * -amountX / overscrollDistance) * delta)
                    if (amountX > 0) scrollX(0f)
                    animating = true
                } else if (amountX > maxX) {
                    resetFade()
                    amountX -= (overscrollSpeedMin + (overscrollSpeedMax - overscrollSpeedMin) * -(maxX - amountX) / overscrollDistance) * delta
                    if (amountX < maxX) scrollX(maxX)
                    animating = true
                }
            }
            if (overscrollY && scrollY) {
                if (amountY < 0) {
                    resetFade()
                    amountY += ((overscrollSpeedMin + (overscrollSpeedMax - overscrollSpeedMin) * -amountY / overscrollDistance) * delta)
                    if (amountY > 0) scrollY(0f)
                    animating = true
                } else if (amountY > maxY) {
                    resetFade()
                    amountY -= (overscrollSpeedMin + (overscrollSpeedMax - overscrollSpeedMin) * -(maxY - amountY) / overscrollDistance) * delta
                    if (amountY < maxY) scrollY(maxY)
                    animating = true
                }
            }
        }
        if (animating) {
            val stage = scene
            if (stage != null && stage.actionsRequestRendering) Core.graphics.requestRendering()
        }
    }

    fun setClip(clip: Boolean) {
        this.clip = clip
    }

    override fun layout() {
        val bg = style.background
        val hScrollKnob = style.hScrollKnob
        val vScrollKnob = style.vScrollKnob
        var bgLeftWidth = 0f
        var bgRightWidth = 0f
        var bgTopHeight = 0f
        var bgBottomHeight = 0f
        if (bg != null) {
            bgLeftWidth = bg.leftWidth
            bgRightWidth = bg.rightWidth
            bgTopHeight = bg.topHeight
            bgBottomHeight = bg.bottomHeight
        }
        val width: Float = getWidth()
        val height: Float = getHeight()
        var scrollbarHeight = 0f
        if (hScrollKnob != null) scrollbarHeight = hScrollKnob.minHeight
        if (style.hScroll != null) scrollbarHeight =
            max(scrollbarHeight.toDouble(), style.hScroll.minHeight.toDouble()).toFloat()
        var scrollbarWidth = 0f
        if (vScrollKnob != null) scrollbarWidth = vScrollKnob.minWidth
        if (style.vScroll != null) scrollbarWidth =
            max(scrollbarWidth.toDouble(), style.vScroll.minWidth.toDouble()).toFloat()

        // Get available space size by subtracting background's padded area.
        areaWidth = width - bgLeftWidth - bgRightWidth
        areaHeight = height - bgTopHeight - bgBottomHeight
        if (widget == null) return

        // Get widget's desired width.
        var widgetWidth: Float
        var widgetHeight: Float
        widgetWidth = widget!!.prefWidth
        widgetHeight = widget!!.prefHeight


        // Determine if horizontal/vertical scrollbars are needed.
        scrollX = forceScrollX || widgetWidth > areaWidth && !disableX
        scrollY = forceScrollY || widgetHeight > areaHeight && !disableY
        if (!fadeScrollBars) {
            // Check again, now taking into account the area that's taken up by any enabled scrollbars.
            if (scrollY) {
                areaWidth -= scrollbarWidth
                if (!scrollX && widgetWidth > areaWidth && !disableX) scrollX = true
            }
            if (scrollX) {
                areaHeight -= scrollbarHeight
                if (!scrollY && widgetHeight > areaHeight && !disableY) {
                    scrollY = true
                    areaWidth -= scrollbarWidth
                }
            }
        }

        // 小组件的可滚动区域的边界。
        widgetAreaBounds[bgLeftWidth, bgBottomHeight, areaWidth] = areaHeight
        if (fadeScrollBars) {
            // Make sure widget is drawn under fading scrollbars.
            if (scrollX && scrollY) {
                areaHeight -= scrollbarHeight
                areaWidth -= scrollbarWidth
            }
        } else {
            if (scrollbarsOnTop) {
                // Make sure widget is drawn under non-fading scrollbars.
                if (scrollX) widgetAreaBounds.height += scrollbarHeight
                if (scrollY) widgetAreaBounds.width += scrollbarWidth
            } else {
                // Offset widget area y for horizontal scrollbar at bottom.
                if (scrollX && hScrollOnBottom) widgetAreaBounds.y += scrollbarHeight
                // Offset widget area x for vertical scrollbar at left.
                if (scrollY && !vScrollOnRight) widgetAreaBounds.x += scrollbarWidth
            }
        }

        // If the widget is smaller than the available space, make it take up the available space.
        widgetWidth = if (disableX) areaWidth else max(areaWidth.toDouble(), widgetWidth.toDouble()).toFloat()
        widgetHeight = if (disableY) areaHeight else max(areaHeight.toDouble(), widgetHeight.toDouble()).toFloat()
        maxX = widgetWidth - areaWidth
        maxY = widgetHeight - areaHeight
        if (fadeScrollBars) {
            // Make sure widget is drawn under fading scrollbars.
            if (scrollX && scrollY) {
                maxY -= scrollbarHeight
                maxX -= scrollbarWidth
            }
        }
        scrollX(Mathf.clamp(amountX, 0f, maxX))
        //scrollY(Mathf.clamp(amountY, 0, maxY));

        // Set the bounds and scroll knob sizes if scrollbars are needed.
        if (scrollX) {
            if (hScrollKnob != null) {
                val hScrollHeight = hScrollKnob.minHeight
                // The corner gap where the two scroll bars intersect might have to flip from right to left.
                val boundsX = if (vScrollOnRight) bgLeftWidth else bgLeftWidth + scrollbarWidth
                // Scrollbar on the top or bottom.
                val boundsY = if (hScrollOnBottom) bgBottomHeight else height - bgTopHeight - hScrollHeight
                hScrollBounds[boundsX, boundsY, areaWidth] = hScrollHeight
                if (variableSizeKnobs) hKnobBounds.width = max(
                    hScrollKnob.minWidth.toDouble(), (hScrollBounds.width * areaWidth / widgetWidth).toInt().toDouble()
                ).toFloat() else hKnobBounds.width = hScrollKnob.minWidth
                hKnobBounds.height = hScrollKnob.minHeight
                hKnobBounds.x =
                    hScrollBounds.x + ((hScrollBounds.width - hKnobBounds.width) * getScrollPercentX()).toInt()
                hKnobBounds.y = hScrollBounds.y
            } else {
                hScrollBounds[0f, 0f, 0f] = 0f
                hKnobBounds[0f, 0f, 0f] = 0f
            }
        }
        if (scrollY) {
            if (vScrollKnob != null) {
                val vScrollWidth = vScrollKnob.minWidth
                // the small gap where the two scroll bars intersect might have to flip from bottom to top
                val boundsX: Float
                val boundsY: Float
                boundsY = if (hScrollOnBottom) {
                    height - bgTopHeight - areaHeight
                } else {
                    bgBottomHeight
                }
                // bar on the left or right
                boundsX = if (vScrollOnRight) {
                    width - bgRightWidth - vScrollWidth
                } else {
                    bgLeftWidth
                }
                vScrollBounds[boundsX, boundsY, vScrollWidth] = areaHeight
                vKnobBounds.width = vScrollKnob.minWidth
                if (variableSizeKnobs) vKnobBounds.height = max(
                    vScrollKnob.minHeight.toDouble(),
                    (vScrollBounds.height * areaHeight / widgetHeight).toInt().toDouble()
                ).toFloat() else vKnobBounds.height = vScrollKnob.minHeight
                if (vScrollOnRight) {
                    vKnobBounds.x = width - bgRightWidth - vScrollKnob.minWidth
                } else {
                    vKnobBounds.x = bgLeftWidth
                }
                vKnobBounds.y =
                    vScrollBounds.y + ((vScrollBounds.height - vKnobBounds.height) * (1 - getScrollPercentY())).toInt()
            } else {
                vScrollBounds[0f, 0f, 0f] = 0f
                vKnobBounds[0f, 0f, 0f] = 0f
            }
        }
        widget!!.setSize(widgetWidth, widgetHeight)
        widget!!.validate()
    }

    override fun draw() {
        if (widget == null) return
        validate()

        // Setup transform for this group.
        applyTransform(computeTransform())
        if (scrollX) hKnobBounds.x =
            hScrollBounds.x + ((hScrollBounds.width - hKnobBounds.width) * getVisualScrollPercentX())
        if (scrollY) vKnobBounds.y =
            vScrollBounds.y + ((vScrollBounds.height - vKnobBounds.height) * (1 - getVisualScrollPercentY()))

        // Calculate the widget's position depending on the scroll state and available widget area.
        var y = widgetAreaBounds.y
        y -= if (!scrollY) maxY else (maxY - visualAmountY)
        var x = widgetAreaBounds.x
        if (scrollX) x -= visualAmountX
        if (!fadeScrollBars && scrollbarsOnTop) {
            if (scrollX && hScrollOnBottom) {
                var scrollbarHeight = 0f
                if (style.hScrollKnob != null) scrollbarHeight = style.hScrollKnob.minHeight
                if (style.hScroll != null) scrollbarHeight = max(scrollbarHeight, style.hScroll.minHeight)
                y += scrollbarHeight
            }
            if (scrollY && !vScrollOnRight) {
                var scrollbarWidth = 0f
                if (style.hScrollKnob != null) scrollbarWidth = style.hScrollKnob.minWidth
                if (style.hScroll != null) scrollbarWidth = max(scrollbarWidth, style.hScroll.minWidth)
                x += scrollbarWidth
            }
        }
        widget!!.setPosition(x, y)
        if (widget is Cullable) {
            widgetCullingArea.x = -widget!!.x + widgetAreaBounds.x
            widgetCullingArea.y = -widget!!.y + widgetAreaBounds.y
            widgetCullingArea.width = widgetAreaBounds.width
            widgetCullingArea.height = widgetAreaBounds.height
            (widget as Cullable).setCullingArea(widgetCullingArea)
        }

        // 绘制背景九块。
        if (style.background != null) style.background.draw(0f, 0f, getWidth(), getHeight())

        // 根据批量变换、可用控件区域和摄像机变换计算剪刀边界。我们需要
        // 将它们投影到屏幕坐标以供 OpenGL ES 使用。
        Core.scene.calculateScissors(widgetAreaBounds, scissorBounds)
        if (clip) {
            // 为 Widget 区域启用剪刀并绘制 Widget。
            if (ScissorStack.push(scissorBounds)) {
                drawChildren()
                ScissorStack.pop()
            }
        } else {
            drawChildren()
        }

        // Render scrollbars and knobs on top.
        Draw.color(color.r, color.g, color.b, color.a * parentAlpha * Interp.fade.apply(fadeAlpha / fadeAlphaSeconds))
        if (scrollX && scrollY) {
            if (style.corner != null) {
                style.corner.draw(
                    hScrollBounds.x + hScrollBounds.width, hScrollBounds.y, vScrollBounds.width, vScrollBounds.y
                )
            }
        }
        if (scrollX) {
            if (style.hScroll != null) style.hScroll.draw(
                hScrollBounds.x, hScrollBounds.y, hScrollBounds.width, hScrollBounds.height
            )
            if (style.hScrollKnob != null) style.hScrollKnob.draw(
                hKnobBounds.x, hKnobBounds.y, hKnobBounds.width, hKnobBounds.height
            )
        }
        if (scrollY) {
            if (style.vScroll != null) style.vScroll.draw(
                vScrollBounds.x, vScrollBounds.y, vScrollBounds.width, vScrollBounds.height
            )
            if (style.vScrollKnob != null) style.vScrollKnob.draw(
                vKnobBounds.x, vKnobBounds.y, vKnobBounds.width, vKnobBounds.height
            )
        }
        resetTransform()
    }

    /**
     * Generate fling gesture.
     * @param flingTime Time in seconds for which you want to fling last.
     * @param velocityX Velocity for horizontal direction.
     * @param velocityY Velocity for vertical direction.
     */
    fun fling(flingTime: Float, velocityX: Float, velocityY: Float) {
        this.flingTimer = flingTime
        this.velocityX = velocityX
        this.velocityY = velocityY
    }

    override fun getPrefWidth(): Float {
        var width = 0f
        if (widget != null) {
            validate()
            width = widget!!.prefWidth
        }
        if (style.background != null) width += style.background.leftWidth + style.background.rightWidth
        if (scrollY) {
            var scrollbarWidth = 0f
            if (style.vScrollKnob != null) scrollbarWidth = style.vScrollKnob.minWidth
            if (style.vScroll != null) scrollbarWidth =
                max(scrollbarWidth.toDouble(), style.vScroll.minWidth.toDouble()).toFloat()
            width += scrollbarWidth
        }
        return width
    }

    override fun getPrefHeight(): Float {
        var height = 0f
        if (widget != null) {
            validate()
            height = widget!!.prefHeight
        }
        if (style.background != null) height += style.background.topHeight + style.background.bottomHeight
        if (scrollX) {
            var scrollbarHeight = 0f
            if (style.hScrollKnob != null) scrollbarHeight = style.hScrollKnob.minHeight
            if (style.hScroll != null) scrollbarHeight =
                max(scrollbarHeight.toDouble(), style.hScroll.minHeight.toDouble()).toFloat()
            height += scrollbarHeight
        }
        return height
    }

    override fun getMinWidth(): Float {
        return 0f
    }

    override fun getMinHeight(): Float {
        return 0f
    }

    /** Returns the actor embedded in this scroll pane, or null.  */
    fun getWidget(): Element? {
        return widget
    }

    /**
     * Sets the [Element] 嵌入到此滚动窗格中。
     * @param widget 可以为空以删除任何当前Actor。
     */
    fun setWidget(widget: Element?) {
        require(widget !== this) { "widget cannot be the ScrollPane." }
        if (this.widget != null) super.removeChild(this.widget)
        this.widget = widget
        if (widget != null) super.addChild(widget)
    }

    override fun removeChild(actor: Element?): Boolean {
        requireNotNull(actor) { "actor cannot be null." }
        if (actor !== widget) return false
        setWidget(null)
        return true
    }

    override fun removeChild(actor: Element?, unfocus: Boolean): Boolean {
        requireNotNull(actor) { "actor cannot be null." }
        if (actor !== widget) return false
        this.widget = null
        return super.removeChild(actor, unfocus)
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Element? {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null
        if (scrollX && hScrollBounds.contains(x, y)) return this
        return if (scrollY && vScrollBounds.contains(x, y)) this else super.hit(x, y, touchable)
    }

    /** Called whenever the x scroll amount is changed.  */
    private fun scrollX(pixelsX: Float) {
        this.amountX = pixelsX
    }

    /** Called whenever the y scroll amount is changed.  */
    private fun scrollY(pixelsY: Float) {
        this.amountY = pixelsY
    }

    /** Called whenever the visual x scroll amount is changed.  */
    private fun visualScrollX(pixelsX: Float) {
        this.visualAmountX = pixelsX
    }

    /** Called whenever the visual y scroll amount is changed.  */
    private fun visualScrollY(pixelsY: Float) {
        this.visualAmountY = pixelsY
    }

    /** Returns the amount to scroll horizontally when the mouse wheel is scrolled.  */
    fun getMouseWheelX(): Float {
        return min(areaWidth, (max((areaWidth * 0.9f), (maxX * 0.1f)) / 4))
    }

    /** Returns the amount to scroll vertically when the mouse wheel is scrolled.  */
    fun getMouseWheelY(): Float {
        return min(
            areaHeight, (max((areaHeight * 0.9f), (maxY * 0.1f)) / 4)
        )
    }

    fun setScrollXForce(pixels: Float) {
        visualAmountX = pixels
        amountX = pixels
        scrollX = true
    }

    /** Returns the x scroll position in pixels, where 0 is the left of the scroll pane.  */
    fun getScrollX(): Float {
        return amountX
    }

    fun setScrollYForce(pixels: Float) {
        visualAmountY = pixels
        amountY = pixels
        scrollY = true
    }

    /** Returns the y scroll position in pixels, where 0 is the top of the scroll pane.  */
    fun getScrollY(): Float {
        return amountY
    }

    /**
     * Sets the visual scroll amount equal to the scroll amount. This can be used when setting the scroll amount without
     * animating.
     */
    fun updateVisualScroll() {
        visualAmountX = amountX
        visualAmountY = amountY
    }

    fun getVisualScrollX(): Float {
        return if (!scrollX) 0f else visualAmountX
    }

    fun getVisualScrollY(): Float {
        return if (!scrollY) 0f else visualAmountY
    }

    fun getVisualScrollPercentX(): Float {
        return Mathf.clamp(visualAmountX / maxX, 0f, 1f)
    }

    fun getVisualScrollPercentY(): Float {
        return Mathf.clamp(visualAmountY / maxY, 0f, 1f)
    }

    fun getScrollPercentX(): Float {
        return if (java.lang.Float.isNaN(amountX / maxX)) 1f else Mathf.clamp(amountX / maxX, 0f, 1f)
    }

    fun setScrollPercentX(percentX: Float) {
        scrollX(maxX * Mathf.clamp(percentX, 0f, 1f))
    }

    fun getScrollPercentY(): Float {
        return if (java.lang.Float.isNaN(amountY / maxY)) 1f else Mathf.clamp(amountY / maxY, 0f, 1f)
    }

    fun setScrollPercentY(percentY: Float) {
        scrollY(maxY * Mathf.clamp(percentY, 0f, 1f))
    }

    fun setFlickScroll(flickScroll: Boolean) {
        if (this.flickScroll == flickScroll) return
        this.flickScroll = flickScroll
        if (flickScroll) addListener(flickScrollListener) else removeListener(flickScrollListener)
        invalidate()
    }

    fun setFlickScrollTapSquareSize(halfTapSquareSize: Float) {
        flickScrollListener.gestureDetector.setTapSquareSize(halfTapSquareSize)
    }

    /**
     * Sets the scroll offset so the specified rectangle is fully in view, if possible. Coordinates are in the scroll pane
     * widget's coordinate system.
     */
    fun scrollTo(x: Float, y: Float, width: Float, height: Float) {
        scrollTo(x, y, width, height, centerHorizontal = false, centerVertical = false)
    }

    /**
     * Sets the scroll offset so the specified rectangle is fully in view, and optionally centered vertically and/or horizontally,
     * if possible. Coordinates are in the scroll pane widget's coordinate system.
     */
    fun scrollTo(x: Float, y: Float, width: Float, height: Float, centerHorizontal: Boolean, centerVertical: Boolean) {
        var amountX: Float = this.amountX
        if (centerHorizontal) {
            amountX = x - areaWidth / 2 + width / 2
        } else {
            if (x + width > amountX + areaWidth) amountX = x + width - areaWidth
            if (x < amountX) amountX = x
        }
        scrollX(Mathf.clamp(amountX, 0f, maxX))
        var amountY: Float = this.amountY
        if (centerVertical) {
            amountY = maxY - y + areaHeight / 2 - height / 2
        } else {
            if (amountY > maxY - y - height + areaHeight) amountY = maxY - y - height + areaHeight
            if (amountY < maxY - y) amountY = maxY - y
        }
        scrollY(Mathf.clamp(amountY, 0f, maxY))
    }

    /** Returns the maximum scroll value in the x direction.  */
    fun getMaxX(): Float {
        return maxX
    }

    /** 返回 y 方向上的最大滚动值。*/
    fun getMaxY(): Float {
        return maxY
    }

    fun getScrollBarHeight(): Float {
        if (!scrollX) return 0f
        var height = 0f
        if (style.hScrollKnob != null) height = style.hScrollKnob.minHeight
        if (style.hScroll != null) height = max(height, style.hScroll.minHeight)
        return height
    }

    fun getScrollBarWidth(): Float {
        if (!scrollY) return 0f
        var width = 0f
        if (style.vScrollKnob != null) width = style.vScrollKnob.minWidth
        if (style.vScroll != null) width = max(width, style.vScroll.minWidth)
        return width
    }

    /** Returns the width of the scrolled viewport.  */
    fun getScrollWidth(): Float {
        return areaWidth
    }

    /** Returns the height of the scrolled viewport.  */
    fun getScrollHeight(): Float {
        return areaHeight
    }

    /** Returns true if the widget is larger than the scroll pane horizontally.  */
    fun isScrollX(): Boolean {
        return scrollX
    }

    fun setScrollX(pixels: Float) {
        scrollX(Mathf.clamp(pixels, 0f, maxX))
    }

    /** Returns true if the widget is larger than the scroll pane vertically.  */
    fun isScrollY(): Boolean {
        return scrollY
    }

    fun setScrollY(pixels: Float) {
        scrollY(Mathf.clamp(pixels, 0f, maxY))
    }

    /** Disables scrolling in a direction. The widget will be sized to the FlickScrollPane in the disabled direction.  */
    fun setScrollingDisabled(x: Boolean, y: Boolean) {
        disableX = x
        disableY = y
    }

    fun setScrollingDisabledX(x: Boolean) {
        disableX = x
    }

    fun setScrollingDisabledY(y: Boolean) {
        disableY = y
    }

    fun isScrollingDisabledX(): Boolean {
        return disableX
    }

    fun isScrollingDisabledY(): Boolean {
        return disableY
    }

    fun isLeftEdge(): Boolean {
        return !scrollX || amountX <= 0
    }

    fun isRightEdge(): Boolean {
        return !scrollX || amountX >= maxX
    }

    fun isTopEdge(): Boolean {
        return !scrollY || amountY <= 0
    }

    fun isBottomEdge(): Boolean {
        return !scrollY || amountY >= maxY
    }

    fun isDragging(): Boolean {
        return draggingPointer != -1
    }

    fun isPanning(): Boolean {
        return flickScrollListener.gestureDetector.isPanning
    }

    fun isFlinging(): Boolean {
        return flingTimer > 0
    }

    /**
     * For flick scroll, if true the widget can be scrolled slightly past its bounds and will animate back to its bounds when
     * scrolling is stopped. Default is true.
     */
    fun setOverscroll(overscrollX: Boolean, overscrollY: Boolean) {
        this.overscrollX = overscrollX
        this.overscrollY = overscrollY
    }

    /**
     * For flick scroll, sets the overscroll distance in pixels and the speed it returns to the widget's bounds in seconds.
     * Default is 50, 30, 200.
     */
    fun setupOverscroll(distance: Float, speedMin: Float, speedMax: Float) {
        overscrollDistance = distance
        overscrollSpeedMin = speedMin
        overscrollSpeedMax = speedMax
    }

    /**
     * Forces enabling scrollbars (for non-flick scroll) and overscrolling (for flick scroll) in a direction, even if the contents
     * do not exceed the bounds in that direction.
     */
    fun setForceScroll(x: Boolean, y: Boolean) {
        forceScrollX = x
        forceScrollY = y
    }

    fun isForceScrollX(): Boolean {
        return forceScrollX
    }

    fun isForceScrollY(): Boolean {
        return forceScrollY
    }

    /** For flick scroll, sets the amount of time in seconds that a fling will continue to scroll. Default is 1.  */
    fun setFlingTime(flingTime: Float) {
        this.flingTime = flingTime
    }

    /** For flick scroll, prevents scrolling out of the widget's bounds. Default is true.  */
    fun setClamp(clamp: Boolean) {
        this.clamp = clamp
    }

    /** Set the position of the vertical and horizontal scroll bars.  */
    fun setScrollBarPositions(bottom: Boolean, right: Boolean) {
        hScrollOnBottom = bottom
        vScrollOnRight = right
    }

    /** When true the scrollbars don't reduce the scrollable size and fade out after some time of not being used.  */
    fun setFadeScrollBars(fadeScrollBars: Boolean) {
        if (this.fadeScrollBars == fadeScrollBars) return
        this.fadeScrollBars = fadeScrollBars
        if (!fadeScrollBars) fadeAlpha = fadeAlphaSeconds
        invalidate()
    }

    fun setupFadeScrollBars(fadeAlphaSeconds: Float, fadeDelaySeconds: Float) {
        this.fadeAlphaSeconds = fadeAlphaSeconds
        this.fadeDelaySeconds = fadeDelaySeconds
    }

    fun setSmoothScrolling(smoothScrolling: Boolean) {
        this.smoothScrolling = smoothScrolling
    }

    /**
     * When false (the default), the widget is clipped so it is not drawn under the scrollbars. When true, the widget is clipped
     * to the entire scroll pane bounds and the scrollbars are drawn on top of the widget. If [.setFadeScrollBars]
     * is true, the scroll bars are always drawn on top.
     */
    fun setScrollbarsOnTop(scrollbarsOnTop: Boolean) {
        this.scrollbarsOnTop = scrollbarsOnTop
        invalidate()
    }

    fun getVariableSizeKnobs(): Boolean {
        return variableSizeKnobs
    }

    /**
     * If true, the scroll knobs are sized based on [.getMaxX] or [.getMaxY]. If false, the scroll knobs are sized
     * based on [Drawable.getMinWidth] or [Drawable.getMinHeight]. Default is true.
     */
    fun setVariableSizeKnobs(variableSizeKnobs: Boolean) {
        this.variableSizeKnobs = variableSizeKnobs
    }

    /**
     * When true (default) and flick scrolling begins, [.cancelTouchFocus] is called. This causes any widgets inside the
     * scrollpane that have received touchDown to receive touchUp when flick scrolling begins.
     */
    fun setCancelTouchFocus(cancelTouchFocus: Boolean) {
        this.cancelTouchFocus = cancelTouchFocus
    }
}