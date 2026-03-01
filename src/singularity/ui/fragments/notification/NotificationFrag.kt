package singularity.ui.fragments.notification

import arc.Core
import arc.Graphics
import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.input.KeyCode
import arc.math.Interp
import arc.math.Mathf
import arc.scene.Element
import arc.scene.Group
import arc.scene.actions.Actions
import arc.scene.actions.DelayAction
import arc.scene.event.SceneEvent
import arc.scene.event.Touchable
import arc.scene.style.BaseDrawable
import arc.scene.ui.Button
import arc.scene.ui.Dialog
import arc.scene.ui.Label
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Align
import arc.util.Scaling
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.graphics.IceColor.b4
import ice.library.scene.action.IceActions
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.Styles
import singularity.Sgl
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.ui.SglStyles
import universecore.util.DataPackable
import java.text.DateFormat
import kotlin.math.max

class NotificationFrag {
  private val history = Seq<Notification>()
  private val notifyQueue = Seq<Notification?>()

  private val logHistory = ObjectMap<Notification?, NotifyLogBar?>()
  private val showing = Seq<NotifyItemBar>()

  var showingDialogs: Int = 0
  var notifies: Group? = null
  var main: Table? = null
  var windowBack: Table? = null
  var historyPane: Table? = null
  var historyList: Table? = null

  var historyPaneShown: Boolean = false
  var anyHistoryUnsaved: Boolean = false


  fun build(parent: Group) {
    parent.fill { main: Table ->
      this.main = main
      main.touchable = Touchable.childrenOnly
      main.left().table { tab ->
        tab.left().button({ t -> t.image(Icon.rightOpen).size(24f).scaling(Scaling.fit) }, SglStyles.sideButtonRight, {
          notifyQueue.clear()
          for (bar in showing) {
            removeNotificationBar(bar)
          }
          showHistoryPane()
        }).disabled { _ -> !windowBack!!.hasActions() }.width(26f).height(520f).get()
        tab.table { pane ->
          pane!!.left()
          pane.add(
            object : Table(Cons { top ->
              val lab = top.left().button("\ue86f " + Core.bundle.get("misc.clear"), Styles.nonet) {
                notifyQueue.clear()
                for (bar in showing) {
                  removeNotificationBar(bar)
                }
              }.fill().margin(4f).padLeft(8f).padBottom(-4f).get().labelCell
              lab.reset()
              lab.fill()
            }) {
              init {
                color.a = 0f
              }

              var switching: Boolean = false

              override fun updateVisibility() {
                if (notifyQueue.any() || showing.any()) {
                  if (!visible) {
                    visible = true
                    actions(Actions.fadeIn(0.4f))
                  }
                } else if (visible && !switching) {
                  switching = true
                  actions(
                    Actions.fadeOut(0.4f),
                    Actions.hide(),
                    Actions.run { switching = false }

                  )
                }
              }
            }
          ).grow()
          pane.row()
          pane.add<Group?>(object : Group() {
            init {
              touchable = Touchable.childrenOnly
            }

            override fun act(delta: Float) {
              super.act(delta)

              if (!notifyQueue.isEmpty && showing.size < 5) {
                addNotificationBar(notifyQueue.remove(0)!!)
              }
            }
          }.also { notifies = it }).width(380f).height(440f)
          pane.row()
          pane.add(
            object : Table(Cons { bottom: Table? -> bottom!!.left().add("", Styles.outlineLabel).color(Pal.accent).fill().update { l: Label? -> l!!.setText("\uf181 +" + max(notifyQueue.size - (5 - showing.size), 0)) }.padLeft(8f) }) {
              init {
                color.a = 0f
              }

              var switching: Boolean = false

              override fun updateVisibility() {
                if (notifyQueue.size - (5 - showing.size) > 0) {
                  if (!visible) {
                    visible = true
                    actions(Actions.fadeIn(0.4f))
                  }
                } else if (visible && !switching) {
                  switching = true
                  actions(
                    Actions.fadeOut(0.4f),
                    Actions.run { switching = false },
                    Actions.hide()
                  )
                }
              }
            }
          ).grow()
        }.growY().fillX()
      }.fill()
    }

    parent.addChild(
      object : Table(SglDrawConst.darkgrayUIAlpha) {
        var acting: Boolean = false

        init {
          setFillParent(true)
        }

        override fun updateVisibility() {
          if (showingDialogs > 0) {
            if (!visible) {
              visible = true
              actions(Actions.fadeIn(0.4f))
            }
          } else if (visible && !acting) {
            acting = true
            actions(
              Actions.fadeOut(0.4f),
              Actions.hide(),
              Actions.run { acting = false }
            )
          }
        }
      }.also { windowBack = it }
    )

    parent.addChild(Table(Tex.pane).margin(4f).top().also { historyPane = it })

    if (Core.graphics.isPortrait){
      historyPane!!.setSize(
        Core.graphics.height * 0.75f,
        Core.graphics.width * 0.75f
      )
    }else{
      historyPane!!.setSize(
        Core.graphics.width * 0.75f,
        Core.graphics.height * 0.75f
      )
    }

    historyPane!!.setPosition(historyPane!!.getWidth() / 2, Core.graphics.height / 2f, Align.center)
    historyPane!!.visible = false

    historyPane!!.table { pane ->
      pane.top().table { top ->
        top.add().size(32f)
        top.add(Core.bundle.get("infos.notificationHistory"), Styles.outlineLabel).color(Pal.accent).fontScale(1.1f).growX().labelAlign(Align.center)
        top.button(Icon.cancel, Styles.clearNonei, 24f) { this.hideHistoryPane() }.size(32f)
      }.fillY().growX()
      pane.row()
      pane.pane { list: Table -> historyList = list }.fillY().growX()
    }.grow()

    historyPane!!.row()
    historyPane!!.table { bottom: Table? ->
      bottom!!.add(Core.bundle.format("infos.logItems", 0), Styles.outlineLabel).fill().fontScale(0.8f).left().color(Pal.accent).pad(4f)
        .update { l -> l!!.setText(Core.bundle.format("infos.logItems", history.size)) }
      bottom.add().growX()
      bottom.button({ b ->
        b!!.add(Core.bundle.get("infos.saveLatest100")).padLeft(4f).fontScale(0.8f)
          .update { l -> l.setColor(if (b.isDisabled()) Color.gray else Color.white) }
        b.image(Icon.layersSmall).size(26f)
          .update { l -> l.setColor(if (b.isDisabled()) Color.gray else Color.white) }
      }, Styles.clearNonei, {
        history.removeRange(100, history.size - 1)
        rebuildHistory()
        anyHistoryUnsaved = true
      }).fill().pad(4f).margin(4f).disabled { _ -> history.size <= 100 }
      bottom.button({ b ->
        b.add(Core.bundle.get("misc.clear")).padLeft(4f).fontScale(0.8f)
        b.image(Icon.trashSmall).size(26f)
      }, Styles.clearNonei, {
        history.clear()
        rebuildHistory()
        anyHistoryUnsaved = true
      }).fill().pad(4f).margin(4f)
    }.growX().fillY()

    historyPane!!.update {
      historyList!!.forEach { item: Element? ->
        if (item is NotifyLogBar && historyList!!.getCullingArea() != null && historyList!!.getCullingArea().overlaps(item.x, item.y, item.getWidth(), item.getHeight())) {
          if (!item.notification.readed) {
            item.notification.readed = true
            anyHistoryUnsaved = true
          }
        }
      }
    }
  }

  fun notify(notification: Notification?) {
    history.insert(0, notification)
    if (history.size > Sgl.config.maxNotifyHistories) history.removeRange(Sgl.config.maxNotifyHistories, history.size - 1)
    if (!historyPaneShown) notifyQueue.add(notification)

    rebuildHistory()

    anyHistoryUnsaved = true
  }

  private fun rebuildHistory() {
    if (Core.graphics.isPortrait){
      historyPane!!.setSize(
        Core.graphics.height * 0.75f,
        Core.graphics.width * 0.75f
      )
    }else{
      historyPane!!.setSize(
        Core.graphics.width * 0.75f,
        Core.graphics.height * 0.75f
      )
    }

    historyList!!.clearChildren()
    for (notification in history) {
      historyList!!.add(logHistory.get(notification) { NotifyLogBar(notification) }).growX().height(80f).pad(4f)
      historyList!!.row()
    }
  }

  private fun showHistoryPane() {
    if (historyPaneShown) return
    historyPaneShown = true
    showingDialogs++


    main!!.actions(
      Actions.fadeOut(0.5f),
      Actions.hide()
    )

    historyPane!!.visible = true
    historyPane!!.actions(
      Actions.moveToAligned(-historyPane!!.getWidth() / 2, Core.graphics.height / 2f, Align.center),
      Actions.moveToAligned(
        Core.graphics.width / 2f, Core.graphics.height / 2f, Align.center,
        0.5f, Interp.pow3Out
      )
    )
  }

  private fun hideHistoryPane() {
    showingDialogs--
    historyPane!!.actions(
      Actions.moveToAligned(Core.graphics.width / 2f, Core.graphics.height / 2f, Align.center),
      Actions.moveToAligned(
        -historyPane!!.getWidth() / 2, Core.graphics.height / 2f, Align.center,
        0.5f, Interp.pow3In
      ),
      Actions.run {
        historyPaneShown = false
        main!!.visible = true
        main!!.actions(
          Actions.fadeIn(0.5f)
        )
      },
      Actions.hide()
    )
  }

  private fun addNotificationBar(notification: Notification) {
    val bar = NotifyItemBar(notification)
    bar.index = showing.size

    notifies!!.addChild(bar)
    bar.color.a = 0f
    bar.setSize(Scl.scl(380f), Scl.scl(80f))
    bar.x = -bar.getWidth()
    bar.y = if (showing.isEmpty) notifies!!.getHeight() - bar.getHeight() else showing.peek().getY(Align.bottom) - Scl.scl(10f) - bar.getHeight()

    showing.add(bar)
    if (notification.duration < 0) {
      bar.actions(
        Actions.parallel(
          Actions.fadeIn(0.3f),
          Actions.moveBy(bar.getWidth(), 0f, 0.3f, Interp.pow3Out)
        )
      )
    } else {
      bar.actions(
        Actions.parallel(
          Actions.fadeIn(0.3f),
          Actions.moveBy(bar.getWidth(), 0f, 0.3f, Interp.pow3Out)
        ),
        Actions.delay(notification.duration).also { bar.act = it },
        Actions.run { removeNotificationBar(bar) }
      )
    }

    if (notification.activeWindow && notification.buildWindow) {
      showWindow(notification)
    }

    if (bar.act != null) bar.act!!.setPool(null)
  }

  private fun showWindow(notification: Notification) {
    showingDialogs++
    object : Dialog("", SglStyles.transparentBack) {
      val lay: Int = showingDialogs

      init {
        titleTable.clear()

        cont.table(Tex.pane) { pane: Table? ->
          pane!!.table { topBar: Table? ->
            topBar!!.top().defaults().top()
            topBar.add().size(32f)
            topBar.add(notification.title).growX().padTop(12f).labelAlign(Align.center).fontScale(1.2f)
              .update { l: Label? -> l!!.setColor(notification.getTitleColor()) }
            topBar.button(Icon.cancel, Styles.clearNonei, 24f) {
              hide(null)
              showingDialogs--
            }.size(32f)
          }.growX().fillX()
          pane.row()
          pane.table { table: Table? -> notification.buildWindow(table) }.fillY().growX()
        }.fill().margin(4f)
      }

      override fun updateVisibility() {
        visible = lay == showingDialogs
      }
    }.show(Core.scene, null).centerWindow()
  }

  private fun removeNotificationBar(bar: NotifyItemBar) {
    val cont = showing.contains(bar)

    if (cont) {
      bar.clearActions()
      bar.actions(
        Actions.parallel(
          Actions.fadeOut(0.3f),
          Actions.moveBy(-bar.getWidth(), 0f, 0.3f, Interp.pow3In)
        ),
        Actions.run {
          showing.remove(bar)
          for (i in bar.index..<showing.size) {
            showing.get(i).index = i
          }
          bar.remove()
        }
      )
    }
  }

  fun shouldSave(): Boolean {
    return anyHistoryUnsaved
  }

  fun saveHistory(write: Writes) {
    write.i(history.size)
    for (n in history) {
      val bytes = n.pack()
      write.i(bytes.size)
      write.b(bytes)
    }
    anyHistoryUnsaved = false
  }

  fun loadHistory(read: Reads) {
    history.clear()
    val n = read.i()
    for (i in 0..<n) {
      history.add(DataPackable.readObject<Notification?>(read.b(read.i())))
    }
  }

  private inner class NotifyItemBar(noti: Notification) : Table(Tex.paneLeft) {
    val notification: Notification
    var act: DelayAction? = null
    var index: Int = 0

    var hovered: Boolean = false
    var hovering: Boolean = false
    var progress: Float = 0f
    var lerp: Float = 0f

    fun colorLerp(start: Float, end: Float) {
      val prog = Mathf.clamp((Interp.pow2.apply(lerp) - start) / (end - start))
      Draw.color(
        Tmp.c1.set(Pal.darkestGray).lerp(Pal.accent, prog),
        Mathf.lerp(0.3f, 0.7f, 1 - prog) * parentAlpha
      )
    }

    init {
      margin(0f)
      marginLeft(4f)
      this.notification = noti

      touchable = Touchable.enabled

      table(object : BaseDrawable() {
        override fun draw(x: Float, y: Float, width: Float, height: Float) {
          colorLerp(0f, 0.4f)
          Fill.rect(x + width / 2f, y + height / 2f, width, height)
        }
      }) { img: Table? ->
        img!!.table(object : BaseDrawable() {
          override fun draw(x: Float, y: Float, width: Float, height: Float) {
            if (act == null) return

            Draw.color(Pal.darkestGray, parentAlpha)
            Fill.circle(x + width / 2f, y + height / 2f, width / 2f - Scl.scl(4f))

            val col = noti.getIconColor()
            SglDraw.drawCircleProgress(
              x + width / 2f, y + height / 2f, width / 2f,
              Scl.scl(6f), Scl.scl(3f), Interp.pow2Out.apply(progress),
              col, Tmp.c1.set(col).lerp(Color.black, 0.4f)
            )
          }
        }) { i: Table? ->
          i!!.image(notification.getIcon()).size(32f).scaling(Scaling.fit).pad(10f)
            .update { im ->
              im!!.setColor(noti.getIconColor())
              im.setDrawable(notification.getIcon())
            }
        }.fill().pad(0f)
      }.fillX().growY().margin(4f)

      table(object : BaseDrawable() {
        override fun draw(x: Float, y: Float, width: Float, height: Float) {
          colorLerp(0f, 0.4f)
          Fill.tri(x, y, x, y + height, x + width / 3f, y)

          colorLerp(0.2f, 0.6f)
          Fill.quad(
            x + width / 3f + Scl.scl(45f), y,
            x + Scl.scl(45f), y + height,
            x + Scl.scl(95f), y + height,
            x + width / 3f + Scl.scl(95f), y
          )
          colorLerp(0.4f, 0.8f)
          Fill.quad(
            x + width / 3f + Scl.scl(130f), y,
            x + Scl.scl(130f), y + height,
            x + Scl.scl(160f), y + height,
            x + width / 3f + Scl.scl(160f), y
          )
          colorLerp(0.6f, 1f)
          Fill.quad(
            x + width / 3f + Scl.scl(190f), y,
            x + Scl.scl(190f), y + height,
            x + Scl.scl(200f), y + height,
            x + width / 3f + Scl.scl(200f), y
          )
        }
      }) { inf: Table? ->
        inf!!.add(notification.title).growX().labelAlign(Align.left).fontScale(1.1f)
          .update { l: Label? -> l!!.setColor(noti.getTitleColor()) }
        inf.row()
        inf.add(notification.information).growX().wrap().labelAlign(Align.left)
          .update { l: Label? -> l!!.setColor(noti.getInformationColor()) }
      }.pad(0f).grow().marginLeft(4f)

      button(Icon.leftOpenSmall, Styles.clearNonei, 22f) {
        removeNotificationBar(this)
        notification.readed = true
      }.margin(4f).growY().get().addListener { event: SceneEvent? ->
        event!!.stop()
        false
      }

      clicked(KeyCode.mouseLeft) {
        if (notification.buildWindow) showWindow(notification)
        noti.activity()
      }

      hovered {
        hovered = true
        hovering = true
        Core.graphics.cursor(Graphics.Cursor.SystemCursor.hand)
      }
      exited {
        hovering = false
        Core.graphics.restoreCursor()
      }
    }

    override fun act(delta: Float) {
      lerp = Mathf.lerpDelta(lerp, (if (hovering) 1 else 0).toFloat(), 0.04f)

      if (act != null && (hovered || showingDialogs > 0)) {
        act!!.restart()
      }

      if (act != null) {
        progress = Mathf.lerpDelta(progress, act!!.time / act!!.duration, 0.1f)
      }

      super.act(delta)

      if (index - 1 < showing.size && index > 0) {
        val last = showing.get(index - 1)
        y = Mathf.lerpDelta(y, last.getY(Align.bottom) - Scl.scl(10f) - height, 0.2f)
      } else {
        y = Mathf.lerpDelta(y, notifies!!.getHeight() - height, 0.2f)
      }
    }
  }

  private inner class NotifyLogBar(noti: Notification) : Button(SglStyles.underline) {
    val notification: Notification

    var lerp: Float

    fun colorLerp() {
      val prog = Interp.pow2.apply(lerp)
      Draw.color(
        Tmp.c1.set(Pal.darkestGray).lerp(b4, prog),
        Mathf.lerp(0.3f, 0.7f, 1 - prog) * parentAlpha
      )
    }

    init {
      margin(0f)
      marginBottom(4f)
      marginTop(4f)
      this.notification = noti
      this.lerp = (if (noti.readed) 0 else 1).toFloat()

      touchable = Touchable.enabled

      table(object : BaseDrawable() {
        override fun draw(x: Float, y: Float, width: Float, height: Float) {
          colorLerp()
          Fill.rect(x + width / 2f, y + height / 2f, width, height)
        }
      }) { img ->
        img.table { i ->
          i.image(notification.getIcon()).size(32f).scaling(Scaling.fit).pad(10f)
            .update { im ->
              im!!.setColor(noti.getIconColor())
              im.setDrawable(notification.getIcon())
            }
        }.fill().pad(0f)
      }.fillX().growY().margin(4f)

      table(object : BaseDrawable() {
        override fun draw(x: Float, y: Float, width: Float, height: Float) {
          colorLerp()
          val c1 = Draw.getColor().toFloatBits()
          val c2 = Tmp.c1.set(Draw.getColor()).a(0f).toFloatBits()
          val prog = Interp.pow2.apply(lerp)
          Fill.quad(
            x, y, c1,
            x, y + height, c1,
            x + width * prog, y + height, c2,
            x + width * prog, y, c2
          )
        }
      }) { inf ->
        inf.table { i ->
          i!!.add(notification.title).growX().labelAlign(Align.left).fontScale(1.1f)
            .update { lab -> lab!!.setColor(noti.getTitleColor()) }
          i.row()
          i.add(notification.information).growX().wrap().labelAlign(Align.left)
            .update { lab -> lab.setColor(noti.getInformationColor()) }
        }.growX()
        inf.table { dat ->
          dat.add(DateFormat.getDateInstance(DateFormat.DEFAULT, Core.bundle.locale).format(notification.date))
            .growX().labelAlign(Align.right).color(Color.lightGray)
          dat.row()
          dat.add(DateFormat.getTimeInstance(DateFormat.DEFAULT, Core.bundle.locale).format(notification.date))
            .growX().labelAlign(Align.right).color(Color.lightGray)
        }.fillX()
      }.pad(0f).grow().margin(4f)

      button(Icon.leftOpenSmall, Styles.clearNonei, 22f) {

        actions(IceActions.moveToAlphaAction(-width-10f,y,0.5f,0f), Actions.run {
          history.remove(noti)
          rebuildHistory()
          anyHistoryUnsaved = true
        })


      }.margin(4f).growY().get().addListener { event: SceneEvent? ->
        event!!.stop()
        false
      }

      clicked(KeyCode.mouseLeft) {
        if (noti.buildWindow) {
          showWindow(notification)
        }
        noti.activity()
      }

      hovered { Core.graphics.cursor(Graphics.Cursor.SystemCursor.hand) }
      exited { Core.graphics.restoreCursor() }
    }

    override fun act(delta: Float) {
      super.act(delta)

      if (notification.readed) {
        lerp = Mathf.approachDelta(lerp, 0f, 0.005f)
      }
    }
  }
}