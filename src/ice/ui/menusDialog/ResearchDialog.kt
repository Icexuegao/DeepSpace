package ice.ui.menusDialog

import arc.Events
import arc.input.KeyCode
import arc.math.Mathf
import arc.scene.actions.Actions
import arc.scene.event.ElementGestureListener
import arc.scene.event.InputEvent
import arc.scene.event.InputListener
import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Align
import ice.content.IItems
import ice.content.block.*
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.iTableG
import ice.ui.dialog.BaseMenusDialog
import ice.ui.dialog.research.View
import ice.ui.dialog.research.node.Node
import ice.ui.dialog.research.node.UCLinkNode
import ice.world.meta.IceStats

@Suppress("unused", "LocalVariableName")
object ResearchDialog : BaseMenusDialog(IceStats.研究.localized(), IStyles.menusButton_tech_point) {
  val view = View()
  var selectANode: Node? = null

  class SelectANodeEvent

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

  val minfo = Table().apply {
    top()
    Events.on(SelectANodeEvent::class.java) {
      clearChildren()
      selectANode?.let {
        if (!it.shouldShown()) return@let
        table(IStyles.background101) { ta ->
          ta.margin(18f)
          it.show(ta)
          ta.actions(Actions.alpha(0f), Actions.alpha(1f, 0.25f))
          ta.pack()
        }.width(400f)
      }
    }
  }

  override fun build(cont: Table) {
    cont.touchable = Touchable.childrenOnly
    cont.iTableG { t ->
      t.clip = true
      val element = Stack(view.apply {
        touchable = Touchable.childrenOnly
      }, minfo)
      t.add(element).grow()
      t.addListener(zoomListener)
      t.touchable = Touchable.enabled
      t.addCaptureListener(moveListener)
    }
  }

  init {
    val 虔信方垒 = UCLinkNode(EffectBlocks.虔信方垒, 0f, 0f)
    val 能量节点 = UCLinkNode(PowerBlocks.能量节点, 虔信方垒.x + 6 * 60, 虔信方垒.y).setParent(虔信方垒)
    val 燃烧发电机 = UCLinkNode(PowerBlocks.燃烧发电机, 能量节点.x, 能量节点.y + 2 * 60).setParent(能量节点)
    val 小型能量电池 = UCLinkNode(PowerBlocks.小型能量电池, 能量节点.x + 2 * 60, 能量节点.y).setParent(能量节点)
    val 能量电池 = UCLinkNode(PowerBlocks.能量电池, 小型能量电池.x + 2 * 60, 小型能量电池.y).setParent(小型能量电池)
    val 低碳钢 = UCLinkNode(IItems.低碳钢, 虔信方垒.x, 虔信方垒.y - 2 * 60).setParent(虔信方垒)
    val 高碳钢 = UCLinkNode(IItems.高碳钢, 低碳钢.x - 2 * 60, 低碳钢.y).setParent(低碳钢)
    val 铜锭 = UCLinkNode(IItems.铜锭, 高碳钢.x - 2 * 60, 高碳钢.y).setParent(高碳钢)
    val 铅锭 = UCLinkNode(IItems.铅锭, 高碳钢.x - 2 * 60, 高碳钢.y - 2 * 60).setParent(高碳钢)
    val 锌锭 = UCLinkNode(IItems.锌锭, 高碳钢.x, 高碳钢.y - 2 * 60).setParent(高碳钢)
    val 黄铜锭 = UCLinkNode(IItems.黄铜锭, 锌锭.x, 锌锭.y - 2 * 60).setParent(锌锭)
    val 碳控熔炉 = UCLinkNode(CrafterBlocks.碳控熔炉, 虔信方垒.x, 虔信方垒.y + 2 * 60).setParent(虔信方垒)
    val 纤汲钻井 = UCLinkNode(ProductBlocks.纤汲钻井, 碳控熔炉.x - 2 * 60, 碳控熔炉.y).setParent(碳控熔炉)
    val 蛮荒钻井 = UCLinkNode(ProductBlocks.蛮荒钻井, 纤汲钻井.x - 2 * 60, 纤汲钻井.y).setParent(纤汲钻井)
    val 普适冶炼阵列 = UCLinkNode(CrafterBlocks.普适冶炼阵列, 碳控熔炉.x, 碳控熔炉.y + 2 * 60).setParent(碳控熔炉)
    val 特化冶炼阵列 = UCLinkNode(CrafterBlocks.特化冶炼阵列, 普适冶炼阵列.x, 普适冶炼阵列.y + 2 * 60).setParent(普适冶炼阵列)

    val 铸铜厂 = UCLinkNode(CrafterBlocks.铸铜厂, 普适冶炼阵列.x + 2 * 60, 普适冶炼阵列.y).setParent(普适冶炼阵列)
    val 生煤 = UCLinkNode(IItems.生煤, 低碳钢.x, 低碳钢.y - 2 * 60).setParent(虔信方垒)
    val 赤铁矿 = UCLinkNode(IItems.赤铁矿, 生煤.x + 2 * 60, 生煤.y + 2 * 60).setParent(生煤)
    val 黄铜矿 = UCLinkNode(IItems.黄铜矿, 赤铁矿.x + 2 * 60, 赤铁矿.y).setParent(赤铁矿)

    Node(生煤.x + 3 * 60, 生煤.y, Image(IStyles.afehs).apply {
      setColor(IceColor.b4)
      setSize(Scl.scl(60f))
    })
    val 方铅矿 = UCLinkNode(IItems.方铅矿, 生煤.x + 2 * 60, 生煤.y - 2 * 60).setParent(生煤)
    val 闪锌矿 = UCLinkNode(IItems.闪锌矿, 方铅矿.x + 2 * 60, 方铅矿.y).setParent(方铅矿)
    val 铬铁矿 = UCLinkNode(IItems.铬铁矿, 生煤.x + 6 * 60, 生煤.y).setParent(闪锌矿).setParent(黄铜矿)
    val 金矿 = UCLinkNode(IItems.金矿, 铬铁矿.x, 铬铁矿.y - 2 * 60).setParent(铬铁矿)
    val 硫钴矿 = UCLinkNode(IItems.硫钴矿, 铬铁矿.x + 2 * 60, 铬铁矿.y - 2 * 60).setParent(铬铁矿)
    val 基础传送带 = UCLinkNode(Distribution.基础传送带, 虔信方垒.x - 4 * 60, 虔信方垒.y).setParent(虔信方垒)
    val 基础路由器 = UCLinkNode(Distribution.基础路由器, 基础传送带.x - 2 * 60, 基础传送带.y).setParent(基础传送带)
    val 基础交叉器 = UCLinkNode(Distribution.基础交叉器, 基础路由器.x - 2 * 60, 基础路由器.y).setParent(基础路由器)
    val 钴熠传送带 = UCLinkNode(Distribution.钴熠传送带, 基础交叉器.x, 基础交叉器.y - 2 * 60).setParent(基础交叉器)
    val 梯度传送带 = UCLinkNode(Distribution.梯度传送带, 钴熠传送带.x, 钴熠传送带.y - 2 * 60).setParent(钴熠传送带)
    val 转换分类器 = UCLinkNode(Distribution.转换分类器, 基础交叉器.x - 2 * 60, 基础交叉器.y).setParent(基础交叉器)
    val 转换溢流门 = UCLinkNode(Distribution.转换溢流门, 基础交叉器.x - 2 * 60, 基础交叉器.y + 2 * 60).setParent(
      基础交叉器
    )/*
                UnlockContentLinkNode(IItems.铜锭, 0f, 0f).also {
                    UnlockContentLinkNode(IItems.铅锭, 60f * 3, 0f).setParent(it).also { lead ->
                        UnlockContentLinkNode(IItems.石英, 60f * 5, 0f).setParent(lead).apply {
                            object : LinkNode("1", 60f * 7, 0f, {
                                Button(Button.ButtonStyle().apply {
                                    up = IStyles.background111
                                    over = IStyles.background111
                                    down = up
                                }).apply {
                                    setSize(120f)
                                    margin(10f)
                                    image(IItems.血囊孢子.uiIcon).grow()
                                }
                            }) {
                                override fun getOffset(): Float {
                                    return Scl.scl(90f) / 2f
                                }
                            }.setParent(this)
                        }
                    }
                    UnlockContentLinkNode(IItems.锌锭, 60f * 3, Scl.scl(120f)).setParent(it).apply {}
                }
                Node(-150f, 0f) {
                    Image(IFiles.findIcePng("精灵2")).apply {
                        setSize(Node.nodeSize + 50f);setColor(IceColor.b4)
                    }
                }
                object : MoveNode(-150f, 0f,
                    { Image(IFiles.findIcePng("精灵-0001")).apply { setSize(nodeSize + 50f);setColor(IceColor.b4) } }) {
                    var b = false
                    override fun update() {
                        if (movey < 0) b = true
                        if (movey > 10) b = false
                        if (b) movey += 0.002f else movey -= 0.002f
                        super.update()
                    }
                }
                Node(-150f, 150f) {
                    Image(IFiles.findIcePng("guillotine")).apply {
                        setSize(Node.nodeSize + 50f);setColor(IceColor.b4)
                    }
                }*/
  }
}