package ice.ui.galgame

import arc.Core
import arc.scene.Element
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import arc.struct.Seq
import ice.graphics.Characters
import mindustry.Vars
import mindustry.gen.Tex
import universecore.struct.texture.asDrawable
import universecore.ui.actions.IceActions
import universecore.ui.reactive.ReactiveState
import universecore.ui.widgets.tables.tapped
import universecore.ui.widgets.typinglabel.TLabel

object DialogueEngineFragment {
  val group = WidgetGroup().apply { setFillParent(true); touchable = Touchable.childrenOnly }
  var text = ReactiveState("")
  var showOptions = false
  private var currentSession: DialogueSession? = null
  private lateinit var rootTable: Table

  fun build(parent: Group) {
    parent.addChild(group)
    group.fill { it1 ->
      rootTable = it1.table { table ->

        val elements2 = Image(Characters.alon.gal.asDrawable(3f)).apply { setColor(0.5f, 0.5f, 0.5f, 1f) }
        val zino = Image(Characters.zino.gal.asDrawable(3f)).apply {
          setColor(0.5f, 0.5f, 0.5f, 1f)
          region.flip(true, false)
        }

        table.table { images ->
          val elements = Table()
          elements.table(Tex.buttonEdge3) {
            it.label { "alon" }.grow()
          }.expand().bottom().left().margin(10f)
          images.stack(elements2, elements).expand().left()

          val elements1 = Table()
          elements1.table(Tex.buttonEdge2) {
            it.label { "zino" }.grow()
          }.expand().bottom().right().margin(10f)
          images.stack(zino, elements1).expand().right()

        }.growX().row()

        table.table(Tex.pane) {
          val element = TLabel(text.get())
          text.subscribe(false) {
            element.restart(text.get())
            val add = Seq<Element>().add(elements2, zino)
            val random = add.random()
            for(item in add.select { it != random }) {
              item.setColor(0.5f, 0.5f, 0.5f, 1f)
            }

            random.setColor(1f, 1f, 1f, 1f)
            val duration = 0.25f / 2f
            random.actions(
              Actions.moveBy(0f, 20f, duration), Actions.moveBy(0f, -20f, duration), Actions.moveBy(0f, 20f, duration), Actions.moveBy(
                0f, -20f, duration
              )
            )
          }
          it.add(element).expand().top().left().fillX().wrap()
        }.size(Core.graphics.width / 2f / Scl.scl(), 200f).margin(15f)
          .touchable { if (showOptions) Touchable.disabled else Touchable.enabled }.tapped {
            onScreenClick()
          }
      }.expand().bottom().get()
      rootTable.visible { Vars.ui.hudfrag.shown }
    }
    rootTable.actions(Actions.alpha(0f))
    group.touchable = Touchable.disabled
  }

  /** 由你手动调用，开始对话 */
  fun start() {
    val engine = DialogueEngineObject.engine
    currentSession = engine.start()
    updateText()
  }

  private fun onScreenClick() {
    val engine = DialogueEngineObject.engine
    val session = currentSession ?: return

    // 如果选项正在显示，不响应点击
    if (showOptions) {
      return
    }

    val hasMore = session.onClickNext()

    if (hasMore) {
      updateText()
    } else {
      // 当前节点的所有段落都显示完了
      if (engine.hasOptions()) {
        // 有选项，显示选项面板
        showOptions()
      } else {
        // 无选项，自动跳转下一个节点
        try {
          currentSession = engine.nextNode()
          updateText()
        } catch(e: IllegalStateException) {
          closeDialogue()
        }
      }
    }
  }

  fun selectOption(optionIndex: Int) {
    val engine = DialogueEngineObject.engine
    val options = engine.getCurrentOptions()
    if (optionIndex in options.indices) {
      currentSession = engine.selectOption(options[optionIndex])
      updateText()
    }
  }

  private fun updateText() {
    val session = currentSession ?: return
    text.update { session.getCurrentText() }
    // 不需要在这里调用 showOptions，改在 onScreenClick 中处理
  }

  private fun showOptions() {
    showOptions = true
    val engine = DialogueEngineObject.engine
    val options = engine.getCurrentOptions()
    val table = Table()
    for((index, option) in options.withIndex()) {
      table.button(option.label, Tex.pane) {
        showOptions = false  // 选择后隐藏选项标志
        selectOption(index)
        for((index, scf) in table.children.withIndex()) {
          scf.actions(Actions.delay(index * 0.25f), IceActions.moveToAlphaAction(scf.x - 500f, scf.y, 1f, 0f))
        }
        table.actions(Actions.delay(10f, Actions.remove()))
      }.disabled { !showOptions }.size(250f, 60f).row()
    }
    table.apply {
      setFillParent(true)
      group.addChild(this)
    }
  }

  fun showDialogue() {
    start()
    rootTable.actions(Actions.alpha(1f, 1f))
    group.touchable = Touchable.enabled
  }

  fun closeDialogue() {
    rootTable.actions(Actions.alpha(0f, 1f))
    group.touchable = Touchable.disabled
  }
}