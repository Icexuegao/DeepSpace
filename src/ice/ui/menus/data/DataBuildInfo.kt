package ice.ui.menus.data

import arc.Core
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.ImageButton.ImageButtonStyle
import ice.library.tool.ClassTool
import ice.ui.tex.Colors
import mindustry.ctype.Content
import mindustry.ctype.MappableContent
import mindustry.ctype.UnlockableContent
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.gen.Icon
import mindustry.type.*
import mindustry.ui.ItemDisplay
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.meta.Stat
import ice.ui.menus.data.DataDialog.contentInfo as info

object DataBuildInfo {
    private fun base(content: Content, icon: Drawable) {
        info.clear()

        info.table {
            it.image(icon).size(60f).row()
            it.add("-------------").row()
            it.add(content.javaClass.superclass.simpleName).row()
            it.add(content.javaClass.name).row()
            it.add("内容类型: ${content.contentType.name}").row()
            it.add("所属ID: ${content.id}").row()
            if (content is MappableContent) it.add("名称: ${content.name}").row()
            if (content is UnlockableContent) it.add("本地化名称: ${content.localizedName}").row()
        }.growX().row()

        if (content is UnlockableContent) {
            if (content.techNode != null && content.techNode.requirements != null) {
                info.table {
                    var b = false
                    it.table { ita ->
                        ita.add("研究:")
                        ita.button(Icon.upOpenSmall, 24f) {
                            b = !b
                        }.update { mg ->
                            mg.style = ImageButtonStyle(
                                null, null, null, if (b) Icon.downOpenSmall else Icon.upOpenSmall, null, null
                            )
                        }.color(Colors.b4)
                    }.row()

                    it.collapser({ t ->
                        content.techNode.requirements.forEach { item ->
                            val itemDisplay = ItemDisplay(item.item, item.amount)
                            itemDisplay.changed {
                                item(item.item)
                            }
                            t.add(itemDisplay).tooltip(item.item.localizedName).growX().row()
                        }
                    }, { b })
                }.growX().row()
            }
        }
    }

    fun item(item: Item) {
        val icon = TextureRegionDrawable(item.uiIcon)
        base(item, icon)
    }

    fun liquid(liquid: Liquid) {
        val icon = TextureRegionDrawable(liquid.uiIcon)
        base(liquid, icon)
    }

    fun block(block: Block) {
        val icon = TextureRegionDrawable(block.uiIcon)
        base(block, icon)


        info.table {
            var b = false
            it.table { ita ->
                ita.add("字段:")
                ita.button(Icon.upOpenSmall, 24f) {
                    b = !b
                }.update { mg ->
                    mg.style =
                        ImageButtonStyle(null, null, null, if (b) Icon.downOpenSmall else Icon.upOpenSmall, null, null)
                }.color(Colors.b4)
            }.row()

            it.collapser({ t ->
               ClassTool.getFields(block).forEach {field->
                   field.isAccessible=true
                   val get = field.get(block)
                   t.add("${field.name}: $get").pad(2f).row()
               }
            }, { b })
        }.growX().row()



        info.table {
            var b = false
            it.table { ita ->
                ita.add("相关:")
                ita.button(Icon.upOpenSmall, 24f) {
                    b = !b
                }.update { mg ->
                    mg.style =
                        ImageButtonStyle(null, null, null, if (b) Icon.downOpenSmall else Icon.upOpenSmall, null, null)
                }.color(Colors.b4)
            }.row()

            it.collapser({ t ->
                block.requirements.forEach { it1 ->
                    t.button(TextureRegionDrawable(it1.item.uiIcon), Styles.clearNonei, DataBuildContent.sisize) {
                        item(it1.item)
                    }.tooltip(it1.item.localizedName)
                }
            }, { b })
        }.growX().row()

        info.table { t ->
            val stats = block.stats
            block.checkStats()
            val keys = stats.toMap().keys()
            keys.forEach { cat ->
                val map = stats.toMap().get(cat)
                if (map.size == 0) return@forEach
                if (stats.useCategories) {
                    t.add("@category." + cat.name).color(Colors.b4).row()
                }
                for (stat in map.keys()) {
                    if (stat.id == Stat.affinities.id) continue
                    t.table {
                        it.add("${stat.localized()} : ").color(Colors.b5)
                        val statValues = map[stat]
                        statValues.forEach { value ->
                            value.display(it)
                        }
                    }.row()
                }
            }
        }.growX().row()
    }

    fun bullet(bullet: BulletType) {
        base(
            bullet,
            TextureRegionDrawable(if (bullet is BasicBulletType) bullet.backRegion else Core.atlas.find("error"))
        )
    }

    fun status(it: StatusEffect) {
        base(it, TextureRegionDrawable(it.uiIcon))
    }

    fun unit(it: UnitType) {
        base(it, TextureRegionDrawable(it.uiIcon))
    }

    fun weathers(it: Weather) {
        base(it, TextureRegionDrawable(it.uiIcon))
    }

    fun sector(it: SectorPreset) {
        base(it, TextureRegionDrawable(it.uiIcon))
    }

    fun planets(it: Planet) {
        base(it, TextureRegionDrawable(it.uiIcon))
    }
}