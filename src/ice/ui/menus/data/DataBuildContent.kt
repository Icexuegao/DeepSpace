package ice.ui.menus.data

import arc.Core
import arc.scene.style.TextureRegionDrawable
import arc.struct.Seq
import ice.ui.TableExtend
import ice.ui.menus.data.DataDialog.contents
import mindustry.ctype.UnlockableContent
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.type.*
import mindustry.ui.Styles
import mindustry.world.Block

internal object DataBuildContent {
    val sisize = 50f
    private var indx = 0
    private val widthIndx=8

    private fun addbutt(content: UnlockableContent, runnable: Runnable) {
        contents.button(
            TextureRegionDrawable(content.uiIcon), Styles.clearNonei, sisize
        ) {
            runnable.run()
        }.tooltip(content.localizedName).pad(4f)
    }

    fun item(items: Seq<Item>) {
        contents.clear()
        items.forEach {
            indx++
            addbutt(it) { DataBuildInfo.item(it) }
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }

    fun liquid(liqudis: Seq<Liquid>) {
        contents.clear()
        liqudis.forEach {
            indx++
            addbutt(it) { DataBuildInfo.liquid(it) }
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }

    fun block(blocks: Seq<Block>) {
        contents.clear()
        val blockMaps = HashMap<String, Seq<Block>>()
        Category.entries.forEach {
            blockMaps[it.name] = Seq<Block>()
        }
        blocks.forEach {
            blockMaps[it.category.name]!!.add(it)
        }
        blockMaps.forEach { map ->
            TableExtend.addLinet(contents, map.key)
            contents.table {
                map.value.forEach { cat ->
                    indx++
                    it.button(
                        TextureRegionDrawable(cat.uiIcon), Styles.clearNonei, sisize
                    ) {
                        DataBuildInfo.block(cat)
                    }.tooltip(cat.localizedName).pad(4f)
                    if ((indx % widthIndx) == 0) it.row()
                }
                indx=0
            }
            indx = 0
            contents.row()
        }
    }

    fun bullet(bullets: Seq<BulletType>) {
        contents.clear()
        bullets.forEach {
            indx++
            val uiIcon = TextureRegionDrawable(if (it is BasicBulletType) it.backRegion else Core.atlas.find("error"))
            contents.button(
                uiIcon, Styles.clearNonei, sisize
            ) {
                DataBuildInfo.bullet(it)
            }.pad(3f)
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }

    fun status(statusEffects: Seq<StatusEffect>) {
        contents.clear()
        statusEffects.forEach {
            indx++
            addbutt(it) { DataBuildInfo.status(it) }
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }

    fun unit(unitTypes: Seq<UnitType>) {
        contents.clear()
        unitTypes.forEach {
            indx++
            addbutt(it) { DataBuildInfo.unit(it) }
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }

    fun weather(weathers: Seq<Weather>) {
        contents.clear()
        weathers.forEach {
            indx++
            addbutt(it) { DataBuildInfo.weathers(it) }
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }

    fun sector(sector: Seq<SectorPreset>) {
        contents.clear()
        sector.forEach {
            indx++
            addbutt(it) { DataBuildInfo.sector(it) }
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }

    fun planet(planets: Seq<Planet>) {
        contents.clear()
        planets.forEach {
            indx++
            addbutt(it) { DataBuildInfo.planets(it) }
            if ((indx % widthIndx) == 0) contents.row()
        }
        indx = 0
    }
}