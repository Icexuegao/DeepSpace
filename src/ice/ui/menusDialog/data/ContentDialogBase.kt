package ice.ui.menusDialog.data

import arc.graphics.Color
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import arc.struct.OrderedMap
import arc.struct.Seq
import arc.util.Scaling
import ice.audio.ISounds
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.*
import ice.library.scene.ui.layout.ITable
import ice.ui.UI
import ice.world.content.blocks.abstractBlocks.EnvironmentBlock
import ice.world.content.blocks.environment.Floor
import mindustry.ctype.UnlockableContent
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue

abstract class ContentDialogBase<T :UnlockableContent>(val cName: String, val contetnArray: Seq<T>) :Table() {
  companion object {
    var contentDialog = Seq<ContentDialogBase<*>>()
  }

  lateinit var field: TextField
  var list = ITable()
  var info = Table()

  var currentContent: T = contetnArray.first { !it.isHidden }

  init {
    contentDialog.add(this)
    table { ta ->
      ta.table {

        it.iTableGX { search ->
          search.image(IStyles.search).apply {
            get().tapped {
              field.clearText()
            }
          }.color(IceColor.b4).size(33f).padLeft(15f).padRight(8f)
          field = search.field("") {
            flunList()
          }.growX().get()
          val button = Button(IStyles.button).apply {
            add("?")
            itooltip { it1 ->
              it1.addCR("默认搜索name和localizedName")
              it1.addCR("加[#]搜索简介")
              it1.addCR("加[%]搜索吐槽")
              it1.addCR("例如: #矿 %饿")
            }
          }
          search.add(button).size(40f).pad(8f)
        }.minHeight(60f).row()
        it.iPaneG { p ->
          p.top()
          p.add(list)
          flunList()
        }
      }.minWidth(350f).growY()
      ta.add(Image(IStyles.whiteui)).color(IceColor.b1).width(3f).growY()
      ta.iTableG { p ->
        p.add(info).grow()
        flunInfo()
      }
    }.grow()
  }

  fun setCurrent(content: UnlockableContent) {
    currentContent = content as T
    flunInfo()
    flunInfo()
  }
  fun flunAll(){
    flunList()
    flunInfo()
  }
  open fun flunList() {
    list.setRowsize(5)
    contetnArray.select { content ->
      searchSelect(content)
    }.forEach { content ->
      list.button(TextureRegionDrawable(content.uiIcon), IStyles.button, 40f) {
        currentContent = content
        flunInfo()
        UI.showUISoundCloseV(ISounds.数据板块内个体反馈)
      }.size(60f).pad(2f).margin(5f).itooltip(content.localizedName)
    }
  }

  open fun searchSelect(content: T): Boolean {
    if (content.isHidden && !SettingValue.启用调试模式) return false
    val searchField = field.text
    if (searchField.isNotEmpty()) {

      val substring = searchField.substring(1)
      when(searchField[0]) {
        '#' -> {
          content.description ?: return false
          return content.description.contains(substring)
        }

        '%' -> {
          content.details ?: return false
          return content.details.contains(substring)
        }

        else -> {
          return content.name.contains(searchField) || content.localizedName.contains(searchField)
        }
      }
    }
    if (SettingValue.启用调试模式) return true
    if (content is EnvironmentBlock || content is Floor) return true
    if (content.isHidden) return false
    return true
  }

  fun flunInfo() {
    info.clear()
    info.iTableG {
      it.iTableG(IStyles.background31) { it1 ->
        it1.icePane { it2 ->
          showInfo(it2)
        }.minWidth(200f).growX()
      }.margin(22f)
      it.iTableG(IStyles.background31) { it2 ->
        it2.icePane { it1 ->
          showProperties(it1)
        }
      }.margin(22f)
    }.minHeight(300f).row()
    info.iTableG(IStyles.background31) {

    }.margin(22f).minHeight(100f)
  }

  open fun getColor(): Color {
    return IceColor.b4
  }

  open fun showInfo(table: Table) {

    val color = getColor()
    table.iTableGX { it3 ->
      it3.image(currentContent.uiIcon).size(112f).scaling(Scaling.fit).row()
      it3.add(currentContent.localizedName).fontScale(1.5f).pad(2f).color(color)
    }.row()
    table.add(currentContent.description).pad(2f).growX().color(color).wrap().row()
    table.add(currentContent.details).pad(2f).growX().color(color.cpy().a(0.5f)).wrap().row()
  }

  open fun showProperties(table: Table) {
    table.add(currentContent.name).color(Color.valueOf("b7e1fb")).padBottom(3f).row()
    currentContent.checkStats()
    val stats = currentContent.stats
    val toMap = stats.toMap()
    toMap.keys().forEach { cat ->
      val map: OrderedMap<Stat, Seq<StatValue>> = toMap.get(cat)
      if (map.size == 0) return@forEach
      if (stats.useCategories) {
        table.add(cat.localized()).color(Color.valueOf("b7e1fb")).padTop(5f).fillX()
        table.row()
      }

      for(stat in map.keys()) {
        table.table { inset: Table ->
          inset.left()
          inset.add(stat.localized() + ": ").left().top()
          val arr = map[stat]
          for(value in arr) {
            value.display(inset)
            val element = inset.children[0]
            if (element is Label) {
              element.color.set(IceColor.b4)
            }
            inset.add().size(10f)
          }
        }.fillX().pad(1f).padLeft(10f)
        table.row()
      }
    }
  }

}