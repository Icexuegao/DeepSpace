package ice.ui.dialog.research

import arc.input.KeyCode
import arc.math.Mathf
import arc.scene.event.ElementGestureListener
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.event.Touchable
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.content.IItems
import ice.library.IFiles
import ice.library.scene.tex.Colors
import ice.library.scene.tex.IStyles
import ice.ui.dialog.MenusDialog
import ice.ui.dialog.research.node.LinkNode
import ice.ui.dialog.research.node.MoveNode
import ice.ui.dialog.research.node.Node
import ice.ui.dialog.research.node.UnlockContentLinkNode

object ResearchDialog {
    lateinit var cont: Table
    val moveListener = object : ElementGestureListener() {
        override fun zoom(event: InputEvent, initialDistance: Float, distance: Float) {
            if (view.lastZoom < 0) {
                view.lastZoom = view.scaleX
            }

            view.setScale(Mathf.clamp(distance / initialDistance * view.lastZoom, 0.25f, 1f))
            view.setOrigin(Align.center)
            view.isTransform = true
        }

        override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: KeyCode) {
            view.lastZoom = view.scaleX
        }

        override fun pan(event: InputEvent, x: Float, y: Float, deltaX: Float, deltaY: Float) {
            view.panX += deltaX / view.scaleX
            view.panY += deltaY / view.scaleY
        }
    }
    val zoomListener = object : InputListener() {
        override fun scrolled(event: InputEvent, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
            view.setScale(Mathf.clamp(view.scaleX - amountY / 10f * view.scaleX, 0.25f, 1f))
            view.setOrigin(Align.center)
            view.isTransform = true
            return true
        }

        override fun mouseMoved(event: InputEvent, x: Float, y: Float): Boolean {
            view.requestScroll()
            return super.mouseMoved(event, x, y)
        }
    }
    val view = View()
    fun show() {
        MenusDialog.cont.pane { t -> cont = t }.grow()
        cont.add(view).grow()
        cont.addListener(zoomListener)
        cont.touchable = Touchable.enabled
        cont.addCaptureListener(moveListener)
    }

    init {
        UnlockContentLinkNode(IItems.铜锭, 0f, 0f).also {
            UnlockContentLinkNode(IItems.铅锭, 60f * 3, 0f).setParent(it).also { lead->
                UnlockContentLinkNode(IItems.石英, 60f * 5, 0f).setParent(lead).apply {
                  object : LinkNode("1",60f * 7,0f,{
                        Button(Button.ButtonStyle().apply {
                            up=IStyles.background111
                            over=IStyles.background111
                            down=up
                        }).apply {
                            setSize(120f)
                            margin(10f)
                            image(IItems.血囊孢子.uiIcon).grow()
                        }
                    }){
                      override fun getOffset(): Float {
                          return Scl.scl(90f)/2f
                      }
                  }.setParent(this)
                }
            }
            UnlockContentLinkNode(IItems.锌锭, 60f * 3, 120f).setParent(it).apply {}
        }
        Node(-150f, 0f) { Image(IFiles.findIcePng("精灵2")).apply { setSize(Node.nodeSize + 50f);setColor(Colors.b4) } }
        object : MoveNode(-150f, 0f,
            { Image(IFiles.findIcePng("精灵-0001")).apply { setSize(Node.nodeSize + 50f);setColor(Colors.b4) } }) {
            var b = false
            override fun update() {
                if (movey < 0) b = true
                if (movey > 10) b = false
                if (b) movey += 0.002f else movey -= 0.002f
                super.update()
            }
        }
        Node(-150f,150f){ Image(IFiles.findIcePng("guillotine")).apply { setSize(Node.nodeSize + 50f);setColor(Colors.b4) } }

    }
}
