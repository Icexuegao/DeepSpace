package ice.ui.menus.data

import arc.func.Cons
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.ui.TableExtend.icePane
import ice.ui.TableExtend.rowc
import ice.ui.tex.Colors
import ice.ui.tex.IceTex
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.entities.bullet.BulletType
import mindustry.type.*
import mindustry.world.Block

object DataDialog {
    lateinit var contents: Table
    lateinit var contentInfo: Table

    fun set(table: Table) {
        rebuild(table)
    }

    private fun rebuild(table: Table) {
        table.table { ta ->
            addButton<Item>(ta, "物品", ContentType.item, DataBuildContent::item)
            addButton<Liquid>(ta, "流体", ContentType.liquid, DataBuildContent::liquid)
            addButton<Block>(ta, "建筑", ContentType.block, DataBuildContent::block)
            addButton<StatusEffect>(ta, "状态", ContentType.status, DataBuildContent::status)
            addButton<UnitType>(ta, "单位", ContentType.unit,DataBuildContent::unit)
            addButton<Weather>(ta, "天气", ContentType.weather,DataBuildContent::weather)
            addButton<BulletType>(ta, "子弹", ContentType.bullet, DataBuildContent::bullet)
            addButton<SectorPreset>(ta, "战役", ContentType.sector,DataBuildContent::sector)
            addButton<Planet>(ta, "星球", ContentType.planet,DataBuildContent::planet)
        }.height(60f).growX().rowc()
        table.add(Image(IceTex.whiteui)).color(Colors.b1).height(3f).growX().row()
        table.table { ta ->
            ta.icePane { p ->
                contents = p
            }.grow()
            ta.add(Image(IceTex.whiteui)).color(Colors.b1).width(3f).growY()
            ta.icePane { p ->
                contentInfo = p
            }.grow()
        }.grow()
    }

    private inline fun <reified T : Content> addButton(t: Table, name: String, c: ContentType, cs: Cons<Seq<T>>) {
        t.button(name, IceTex.rootButton) {
            val by = Vars.content.getBy<T>(c)
            cs.get(by)
        }.grow()
    }
}


