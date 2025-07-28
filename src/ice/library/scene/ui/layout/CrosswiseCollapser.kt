package ice.library.scene.ui.layout

import arc.func.Boolp
import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.scene.Action
import arc.scene.event.Touchable
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.util.ArcRuntimeException

class CrosswiseCollapser(var cons: Cons<Table>, var collapsedFunc: Boolp) : WidgetGroup() {
    private var table: Table = Table()
    private val collapseAction = CollapseAction()
    private var collapsed: Boolean = false
    private var actionRunning: Boolean = false
    private var currentWidth: Float = 0f
    var autoAnimate: Boolean = true
    var seconds: Float = 0.4f

    init {
        cons[table]
        isTransform = true
        updateTouchable()
        addChild(table)
    }

    fun setCollapsed(collapse: Boolean, withAnimation: Boolean) {
        this.collapsed = collapse
        updateTouchable()

        actionRunning = true

        if (withAnimation) {
            addAction(collapseAction)
        } else {
            if (collapse) {
                currentWidth = 0f
                collapsed = true
            } else {
                currentWidth = table.prefWidth
                collapsed = false
            }

            actionRunning = false
            invalidateHierarchy()
        }
    }

    fun isCollapsed(): Boolean {
        return collapsed
    }

    private fun updateTouchable() {
        this.touchable = if (collapsed) Touchable.disabled else Touchable.enabled
    }

    override fun draw() {
        if (currentWidth > 1) {
            Draw.flush()
            if (clipBegin(x, y, currentWidth, getHeight())) {
                super.draw()
                Draw.flush()
                clipEnd()
            }
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        parent.pack()
        val col = collapsedFunc.get()
        if (col != collapsed) {
            setCollapsed(col, autoAnimate)
        }
    }

    override fun layout() {
        table.setBounds(0f, 0f, getWidth(), getHeight())

        if (!actionRunning) {
            currentWidth = if (collapsed) 0f
            else table.prefWidth
        }
    }

    override fun getPrefWidth(): Float {
        if (!actionRunning) {
            return if (collapsed) 0f
            else table.prefWidth
        }

        return currentWidth
    }

    override fun getPrefHeight(): Float {
        return table.prefHeight
    }

    override fun getMinWidth(): Float {
        return 0f
    }

    override fun getMinHeight(): Float {
        return 0f
    }

    override fun childrenChanged() {
        super.childrenChanged()
        if (getChildren().size > 1) throw ArcRuntimeException("Only one actor can be added to CollapsibleWidget")
    }

    private inner class CollapseAction : Action() {
        override fun act(delta: Float): Boolean {
            if (collapsed) {
                currentWidth -= delta * table.prefWidth / seconds
                if (currentWidth <= 0) {
                    currentWidth = 0f
                    actionRunning = false
                }
            } else {
                currentWidth += delta * table.prefWidth / seconds
                if (currentWidth > table.prefWidth) {
                    currentWidth = table.prefWidth
                    actionRunning = false
                }
            }

            invalidateHierarchy()
            return !actionRunning
        }
    }
}