package singularity.ui.dialogs

import arc.Core
import arc.func.*
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.actions.Actions
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.*
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Cell
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.OrderedMap
import arc.struct.Seq
import arc.util.Strings
import ice.graphics.IStyles.checkCheckBoxStyle
import ice.graphics.IStyles.defaultSlider
import ice.graphics.IceColor.b4
import ice.library.scene.ui.icePane
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.ui.Fonts
import mindustry.ui.Styles
import universecore.util.Empties

class ModConfigDialog : Table() {
  var settings: Table? = null
  var hover: Table? = null
  var entries: OrderedMap<String, Seq<ConfigLayout>?> = OrderedMap<String, Seq<ConfigLayout>?>()
  var icons: ObjectMap<String?, Drawable?> = ObjectMap<String?, Drawable?>()

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
              Actions.alpha(0f, 0.3f),
              Actions.run {
                catTable!!.clearChildren()
                catTable!!.image(icons.get(currCat, Core.atlas.drawable("settings_$currCat"))).size(38f)
                catTable!!.add(Core.bundle.get("settings.category.$currCat"))
              },
              Actions.alpha(1f, 0.3f)
            )
          }

          cats.button(Icon.leftOpen, Styles.clearNonei) {
            currIndex = Mathf.mod(currIndex - 1, entries.size)
            rebuild.run()
            settings!!.clearActions()
            settings!!.actions(
              Actions.alpha(0f, 0.3f),
              Actions.run { this.rebuildSettings() },
              Actions.alpha(1f, 0.3f)
            )
          }.size(60f).padLeft(12f)
          cats.table(Tex.underline) { t: Table -> catTable = t }.height(60f).growX().padLeft(4f).padRight(4f)
          cats.button(Icon.rightOpen, Styles.clearNonei) {
            currIndex = Mathf.mod(currIndex + 1, entries.size)
            rebuild.run()
            settings!!.clearActions()
            settings!!.actions(
              Actions.alpha(0f, 0.3f),
              Actions.run { this.rebuildSettings() },
              Actions.alpha(1f, 0.3f)
            )
          }.size(60f).padRight(12f)

          rebuild.run()
        } else {
          cats.defaults().height(60f).growX()
          for (key in entries.keys()) {
            cats.button(
              Core.bundle.get("settings.category.$key"),
              icons.get(key, Core.atlas.drawable("settings_$key")),
              object : TextButtonStyle() {
                init {
                  font = Fonts.def
                  fontColor = Color.white
                  disabledFontColor = Color.lightGray
                  down = Styles.flatOver
                  checked = Styles.flatOver
                  up = Tex.underline
                  over = Tex.underlineOver
                  disabled = Tex.underlineDisabled
                }
              },
              38f
            ) {
              currCat = key
              settings!!.clearActions()
              settings!!.actions(
                Actions.alpha(0f, 0.3f),
                Actions.run { this.rebuildSettings() },
                Actions.alpha(1f, 0.3f)
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

  fun rebuildSettings() {
    if (currCat == null) {
      currCat = entries.orderedKeys().first()
    }

    settings!!.clearChildren()
    cfgCount = 0
    for (entry in entries.get(currCat)!!) {
      cfgCount++
      settings!!.table(
        (Tex.whiteui as TextureRegionDrawable).tint(Pal.darkestGray.cpy().a(0.5f * (cfgCount % 2)))
      ) { ent: Table ->
        ent.setClip(false)
        ent.defaults().growY()
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

  fun addConfig(category: String, vararg config: ConfigLayout?) {
    entries.get(category) { Seq() }!!.addAll(*config)
    if (category == currCat) rebuildSettings()
  }

  fun addConfig(category: String, icon: Drawable?, vararg config: ConfigLayout?) {
    entries.get(category) { Seq() }!!.addAll(*config)
    icons.put(category, icon)
    if (category == currCat) rebuildSettings()
  }

  fun removeCfg(category: String, name: String?) {
    entries.get(category, Empties.nilSeq())!!.remove { e: ConfigLayout? -> e!!.name == name }
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

  abstract class ConfigLayout(val name: String) {
    abstract fun build(table: Table)

  open  fun getHieght()= 50f
  }

  class ConfigSepLine(name: String, var string: String?) : ConfigLayout(name) {
    var lineColor: Color = b4
    var lineColorBack: Color = lineColor.cpy().mul(0.8f, 0.8f, 0.8f, 1f)

    override fun build(table: Table) {
      table.stack(
        Table { t: Table? ->
          t!!.image().color(lineColor).pad(0f).grow()
          t.row()
          t.image().color(lineColorBack).pad(0f).height(4f).growX()
        },
        Table { t: Table? ->
          t!!.left().add(string, Styles.outlineLabel).fill().left().padLeft(5f)
        }
      ).grow().pad(-5f).padBottom(4f).padTop(4f)
      table.row()
    }
  }

  abstract class ConfigEntry(name: String) : ConfigLayout(name) {
    var str: Prov<String?>? = null
    var tip: Prov<String?>? = null
    var disabled: Boolp = Boolp { false }

    init {
      if (Core.bundle.has("settings.tip.$name")) {
        tip = Prov { Core.bundle.get("settings.tip.$name") }
      }
    }

    override fun build(table: Table) {
      table.left().add(name).color(b4).left().padLeft(4f)
      table.right().table { t: Table ->
        t.setClip(false)
        t.right().defaults().right().padRight(0f)
        if (str != null) {
          t.add("").color(b4).update { l: Label? ->
            l!!.setText(str!!.get())
          }
        }
        buildCfg(t)
      }.growX().height(60f).padRight(4f)

      if (tip != null) {
        table.addListener(object :
          Tooltip(Cons { ta: Table? -> ta!!.add(tip!!.get()).update { l: Label? -> l!!.setText(tip!!.get()) } }) {
          init {
            allowMobile = true
          }
        })
      }
    }

    abstract fun buildCfg(table: Table)
  }

  class ConfigButton(name: String, var button: Prov<Button?>) : ConfigEntry(name) {
    override fun buildCfg(table: Table) {
      table.add<Button>(button.get()).width(180f).growY().pad(4f).get().setDisabled(disabled)
    }
  }

  class ConfigTableCfg(name: String, var table: Cons<Table?>, var handler: Cons<Cell<Table?>?>) : ConfigEntry(name) {
    override fun buildCfg(table: Table) {
      handler.get(table.table { t: Table ->
        t.setClip(false)
        this.table.get(t)
      })
    }
  }

  open class ConfigTable(name: String, var builder: Cons<Table>) : ConfigLayout(name) {
    override fun build(table: Table) {
      builder.get(table)
    }
  }

  open class ConfigCheck(name: String, var click: Boolc, var checked: Boolp) : ConfigEntry(name) {
    override fun buildCfg(table: Table) {
      val checkBox =
        table.check("", checked.get(), click).update { c: CheckBox -> c.setChecked(checked.get()) }.get()
      checkBox.setDisabled(disabled)
      checkBox.setStyle(checkCheckBoxStyle)
    }
  }

  open class ConfigSlider : ConfigEntry {
    var slided: Floatc?
    var curr: Floatp
    var show: Func<Float, String>
    var min: Float
    var max: Float
    var step: Float

    constructor(name: String, slided: Floatc?, curr: Floatp, min: Float, max: Float, step: Float) : super(name) {
      var step = step
      this.slided = slided
      this.curr = curr
      this.min = min
      this.max = max
      this.step = step

      val fix: Int
      step %= 1f
      var i = 0
      while (true) {
        if (Mathf.zero(step)) {
          fix = i
          break
        }
        step *= 10f
        step %= 1f
        i++
      }

      this.show = Func { f: Float? -> Strings.autoFixed(f!!, fix) }
    }

    constructor(
      name: String,
      show: Func<Float, String>,
      slided: Floatc?,
      curr: Floatp,
      min: Float,
      max: Float,
      step: Float
    ) : super(name) {
      this.show = show
      this.slided = slided
      this.curr = curr
      this.min = min
      this.max = max
      this.step = step
    }

    override fun buildCfg(table: Table) {
      if (str == null) {
        table.add("").update { l: Label? ->
          l!!.setColor(b4)
          l.setText(show.get(curr.get()))
        }.padRight(0f)
      }
      table.slider(min, max, step, curr.get(), slided).width(360f).height(45f).padLeft(4f).update { s: Slider? ->
        s!!.setValue(curr.get())
        s.isDisabled = disabled.get()
      }.get().setStyle(defaultSlider)
    }
  }

  companion object {
    var cfgCount: Int = 0
  }
}