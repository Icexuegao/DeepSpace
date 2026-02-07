@file:Suppress("UNCHECKED_CAST")

package ice.ui.menusDialog

import arc.graphics.Color
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Image
import arc.scene.ui.Label
import arc.scene.ui.TextButton
import arc.scene.ui.TextField
import arc.scene.ui.layout.Table
import arc.struct.OrderedMap
import arc.struct.Seq
import arc.util.Scaling
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.library.scene.ui.*
import ice.library.scene.ui.layout.ITable
import ice.ui.MenusDialog
import ice.ui.dialog.BaseMenusDialog
import ice.world.content.BaseContentSeq
import ice.world.content.blocks.abstractBlocks.EnvironmentBlock
import ice.world.content.blocks.environment.Floor
import ice.world.meta.IceStats
import mindustry.ctype.UnlockableContent
import mindustry.type.*
import mindustry.world.Block
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue

object DataDialog : BaseMenusDialog(IceStats.数据.localized(), IStyles.menusButton_database) {
  init {
    ContentDialogBase<Item>("物品").apply {
      contents.addAll(BaseContentSeq.items)
      contentsHash.addAll(contents)
      color = { it.color }
    }
    ContentDialogBase<Liquid>("液体").apply {
      contents.addAll(BaseContentSeq.liquids)
      contentsHash.addAll(contents)
      color = { it.color }
    }
    object : ContentDialogBase<Block>("建筑") {
      override fun showList(table: ITable) {
        val values = Category.entries.toTypedArray()
        val tables = Array(values.size) { ITable().apply { setRowsize(5) } }
        var seq= Seq<String>().apply {
          Category.all.forEach{add(it.name)}
        }

        contents.select { content ->
          searchSelect(content)
        }.forEach { content ->
          values.forEach { category ->
            val child = tables[category.ordinal]
            if (content.category.name == category.name) {
              child.button(TextureRegionDrawable(content.uiIcon), IStyles.button, 40f) {
                currentContent = content
              }.size(60f).pad(2f).margin(5f).itooltip(content.localizedName)
            }
          }
        }


        values.forEach { category ->
          val child = tables[category.ordinal]
          if (child.children.size == 0) return@forEach
          table.iTableG { it1 ->
            it1.addLine(category.name).padBottom(5f)
            it1.add(child).grow().row()
          }.row()
        }
      }
    }.apply {
      contents.addAll(BaseContentSeq.blocks)
      contentsHash.addAll(contents)
    }
    ContentDialogBase<StatusEffect>("状态").apply {
      contents.addAll(BaseContentSeq.status)
      contentsHash.addAll(contents)
    }
    ContentDialogBase<UnitType>("单位").apply {
      contents.addAll(BaseContentSeq.units)
      contentsHash.addAll(contents)
    }
  }

  private var contentDialogs: ContentDialogBase<*> = ContentDialogBase.contentDialog.first()
  var currentContent: UnlockableContent = contentDialogs.contents.first()

  lateinit var flun: () -> Unit

  override fun build(cont: Table) {
    cont.iTableGX { ta ->
      ContentDialogBase.contentDialog.forEach {
        val textButton = TextButton(it.name, IStyles.button1)
        textButton.changed {
          contentDialogs = it
          it.changed()
        }
        textButton.update {
          textButton.isChecked = contentDialogs == it
        }
        ta.add(textButton).pad(1f).grow()
      }
    }.height(60f).row()
    cont.add(Image(IStyles.whiteui)).color(IceColor.b1).height(3f).growX().row()
    cont.iTableG { ta ->
      ta.table {
        var field: TextField? = null
        it.iTableGX { search ->
          search.image(IStyles.search).apply {
            get().tapped {
              contentDialogs.searchField = ""
              field!!.text = ""
              flun()
            }
          }.color(IceColor.b4).size(33f).padLeft(15f).padRight(8f)
          field = search.field(contentDialogs.searchField) { s ->
            contentDialogs.searchField = s
            flun()
          }.growX().get()
          val button = arc.scene.ui.Button(IStyles.button).apply {
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
          var tmp = contentDialogs
          flun = {
            p.clear()
            field!!.text = contentDialogs.searchField
            contentDialogs.showList(p)
          }
          flun()
          p.update {
            if (tmp != contentDialogs) {
              tmp = contentDialogs
              flun()
            }
          }
        }
      }.minWidth(350f).growY()
      ta.add(Image(IStyles.whiteui)).color(IceColor.b1).width(3f).growY()
      ta.iTableG { p ->
        var tmp = currentContent
        val r = {
          p.clear()
          p.iTableG {
            it.iTableG(IStyles.background31) { it1 ->
              it1.icePane { it2 ->
                contentDialogs.showInfo(it2)
              }.grow()
            }.margin(22f)
            it.iTableG(IStyles.background31) { it2 ->
              it2.icePane { it1 ->
                contentDialogs.showProperties(it1)
              }
            }.margin(22f)
          }.minHeight(300f).row()
          p.iTableG(IStyles.background31) {
            contentDialogs.showOther(it)
          }.margin(22f).minHeight(100f)
        }
        r()
        p.update {
          if (tmp != currentContent) {
            tmp = currentContent
            r()
          }
        }
      }
    }
  }

  fun showUnlockableContent(block: UnlockableContent) {
    var c: ContentDialogBase<*>? = null

    ContentDialogBase.contentDialog.forEach {
      if (it.contentsHash.contains(block)) c = it
    }
    if (c != null) {
      contentDialogs = c
    } else return

    if (!MenusDialog.isShown) MenusDialog.show()

    currentContent = block
    MenusDialog.button = this
    hide()
    build(MenusDialog.conts)
  }

  private open class ContentDialogBase<T : UnlockableContent>(val name: String) {
    companion object {
      var contentDialog = Seq<ContentDialogBase<*>>()
    }

    var searchField = ""
    var contents = Seq<T>()
    var contentsHash = LinkedHashSet<T>()
    var color: (T) -> Color = {
      IceColor.b4
    }

    init {
      contentDialog.add(this)
    }

    open fun showInfo(table: Table) {
      val color = color(currentContent as T)
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

        for (stat in map.keys()) {
          table.table { inset: Table ->
            inset.left()
            inset.add(stat.localized() + ": ").left().top()
            val arr = map[stat]
            for (value in arr) {
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

    open fun showOther(table: Table) {
    }

    open fun searchSelect(content: T): Boolean {
      if (searchField.isNotEmpty()) {
        val substring = searchField.substring(1)
        when (searchField[0]) {
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

    open fun showList(table: ITable) {
      table.setRowsize(5)
      contents.select { content ->
        searchSelect(content)
      }.forEach { content ->
        table.button(TextureRegionDrawable(content.uiIcon), IStyles.button, 40f) {
          currentContent = content
        }.size(60f).pad(2f).margin(5f).itooltip(content.localizedName)
      }
    }

    fun changed() {
    }
  }
}