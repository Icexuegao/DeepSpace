package singularity.ui.dialogs

import arc.Core
import arc.func.Cons
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.scene.ui.TextButton
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.OrderedMap
import arc.struct.Seq
import ice.audio.ISounds
import ice.graphics.IStyles
import ice.graphics.IceColor.b4
import ice.ui.UI
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.Fonts
import mindustry.ui.Styles
import singularity.ui.dialogs.layout.ConfigLayout
import universecore.scene.ui.icePane
import universecore.util.Empties

class ModConfigDialog :Table() {
  companion object {
    var cfgCount: Int = 0
  }

  var settings: Table? = null
  var hover: Table? = null
  var entries: OrderedMap<String, Seq<ConfigLayout>?> = OrderedMap<String, Seq<ConfigLayout>?>()
  var icons = ObjectMap<String, Drawable>()

  var currCat: String? = null
  var catTable: Table? = null
  var relaunchTip: Table? = null
  var currIndex: Int = 0

  var requireRelaunch: Boolean = false

  fun rebuild() {
    clearHover()

    clearChildren()
    table { main: Table ->
      main.table { cats: Table ->
        if (Scl.scl((entries.size * 280).toFloat()) > Core.graphics.width * 0.85f) {
          val rebuild = Runnable {
            currCat = entries.orderedKeys().get(currIndex)
            catTable!!.clearActions()
            catTable!!.actions(
              Actions.alpha(0f, 0.3f), Actions.run {
                catTable!!.clearChildren()
                catTable!!.image(icons.get(currCat, Core.atlas.drawable("settings_$currCat"))).size(38f)
                catTable!!.add(Core.bundle.get("settings.category.$currCat"))
              }, Actions.alpha(1f, 0.3f)
            )
          }

          cats.button(Icon.leftOpen, Styles.clearNonei) {
            currIndex = Mathf.mod(currIndex - 1, entries.size)
            rebuild.run()
            settings!!.clearActions()
            settings!!.actions(
              Actions.alpha(0f, 0.3f), Actions.run { this.rebuildSettings() }, Actions.alpha(1f, 0.3f)
            )
          }.size(60f).padLeft(12f)
          cats.table(Tex.underline) { t: Table -> catTable = t }.height(60f).growX().padLeft(4f).padRight(4f)
          cats.button(Icon.rightOpen, Styles.clearNonei) {
            currIndex = Mathf.mod(currIndex + 1, entries.size)
            rebuild.run()
            settings!!.clearActions()
            settings!!.actions(
              Actions.alpha(0f, 0.3f), Actions.run { this.rebuildSettings() }, Actions.alpha(1f, 0.3f)
            )
          }.size(60f).padRight(12f)

          rebuild.run()
        } else {
          cats.defaults().height(60f).growX()
          for(key in entries.keys()) {
            cats.button(
              Core.bundle.get("settings.category.$key"),
              icons.get(key, Core.atlas.drawable("settings_$key")),
              object :TextButtonStyle() {
                init {
                  font = Fonts.def
                  fontColor = b4
                  disabledFontColor = Color.lightGray
                  down = IStyles.background62
                  checked = IStyles.background62
                  up = IStyles.background61
                  over = IStyles.background61
                  disabled = IStyles.background61
                }
              },
              38f,
              {
                it.color(b4)
              }) {
              UI.showUISoundCloseV(ISounds.数据板块顶部选择按钮反馈)
              currCat = key
              settings!!.clearActions()
              settings!!.actions(
                Actions.alpha(0f, 0.3f), Actions.run { this.rebuildSettings() }, Actions.alpha(1f, 0.3f)
              )
            }.update { b: TextButton? -> b!!.setChecked(key == currCat) }
          }
        }
      }.growX().fillY()
      main.row()

      main.image().color(b4).height(4f).pad(2f).growX()
      main.row()
      main.top().icePane { pane: Table ->
        pane.top().table { settings: Table ->
          settings.defaults().top().growX() /*.height(50)*/
          this.settings = settings
        }.growX().top()
        hover = Table(Tex.pane).also { it.visible = false }
        pane.addChild(hover)
      }.growX().fillY().top().get().setScrollingDisabledX(true)
    }.grow()
    row()

    // relaunchTip = table(SglDrawConst.grayUIAlpha, t -> t.add(Core.bundle.get("infos.requireRelaunch")).color(Color.red)).fill().center().margin(10).pad(4).get();
    //relaunchTip.color.a(0);
    rebuildSettings()
  }

  fun Table.button(
    text: String, image: Drawable, style: TextButtonStyle, imagesize: Float, vc: Cons<Cell<Image>>, clicked: Runnable
  ): Cell<TextButton> {
    val button = TextButton(text, style)
    vc(button.add(Image(image)).size(imagesize))
    button.cells.reverse()
    button.clicked(clicked)
    return add(button)
  }

  operator fun <P> Cons<P>.invoke(p: P) = get(p)
  fun rebuildSettings() {
    if (currCat == null) {
      currCat = entries.orderedKeys().first()
    }

    settings!!.clearChildren()
    cfgCount = 0
    for(entry in entries.get(currCat)!!) {
      cfgCount++
      settings!!.table(
        (Tex.whiteui as TextureRegionDrawable).tint(Pal.darkestGray.cpy().a(0.5f * (cfgCount % 2)))
      ) { ent: Table ->
        entry.build(ent)
      }.height(entry.getHieght())
      settings!!.row()
    }
  }

  fun requireRelaunch() {
    requireRelaunch = true
    relaunchTip!!.clearActions()
    relaunchTip!!.actions(Actions.alpha(1f, 0.5f))
  }

  fun addConfig(category: String, vararg config: ConfigLayout) {
    entries.get(category) { Seq() }!!.addAll(*config)
    if (category == currCat) rebuildSettings()
  }

  fun addConfig(category: String, icon: Drawable, vararg config: ConfigLayout) {
    entries.get(category) { Seq() }!!.addAll(*config)
    icons.put(category, icon)
    if (category == currCat) rebuildSettings()
  }

  fun removeCfg(category: String, name: String?) {
    entries.get(category, Empties.nilSeq())!!.remove { e: ConfigLayout -> e.name == name }
    if (category == currCat) rebuildSettings()
  }

  fun removeCat(category: String?) {
    entries.remove(category)
    icons.remove(category)
  }

  fun clearHover() {
    if (hover == null) return
    hover!!.clear()
    hover!!.visible = false
  }

  fun setHover(build: Cons<Table>) {
    if (hover == null) return

    clearHover()
    build.get(hover)
  }

}