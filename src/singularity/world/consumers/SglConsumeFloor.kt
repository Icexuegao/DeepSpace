package singularity.world.consumers

import arc.Core
import arc.math.Mathf
import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.struct.ObjectFloatMap
import arc.struct.ObjectIntMap
import arc.struct.Seq
import arc.util.Scaling
import arc.util.Strings
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.blocks.environment.Floor
import mindustry.world.meta.Attribute
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import singularity.graphic.SglDrawConst
import singularity.world.components.FloorCrafterBuildComp
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

class SglConsumeFloor<T> : BaseConsume<T> where T : Building, T : ConsumerBuildComp, T : FloorCrafterBuildComp {
    val floorEff: ObjectFloatMap<Floor> = ObjectFloatMap<Floor>()
    var baseEfficiency: Float = 1f

    constructor(vararg floors: Any?) {
        var i = 0
        // 正确的写法
        while (i < floors.size) {
            val floor = floors[i]
            if (floor is Floor) {
                val effInc = floors[i + 1] as Float
                floorEff.put(floor, effInc)
            }
            i += 2
        }

    }

    constructor(attribute: Attribute?, scl: Float) : this(true, true, attribute, scl)

    constructor(checkDeep: Boolean, checkLiquid: Boolean, attribute: Attribute?, scl: Float) {
        for (block in Vars.content.blocks()) {
            if ((block !is Floor) || (checkDeep && block.isDeep) || (checkLiquid && block.isLiquid) || block.attributes.get(attribute) <= 0) continue

            floorEff.put(block, block.attributes.get(attribute) * scl)
        }
    }

    constructor(checkDeep: Boolean, checkLiquid: Boolean, attributes: Array<Any?>) {
        for (block in Vars.content.blocks()) {
            var i = 0
            while (i < attributes.size) {
                val attribute = attributes[i] as Attribute?
                val scl = attributes[i + 1] as Float

                if ((block !is Floor) || (checkDeep && block.isDeep) || (checkLiquid && block.isLiquid) || block.attributes.get(attribute) <= 0) {
                    i += 2
                    continue
                }

                floorEff.put(block, floorEff.get(block, 1f) * block.attributes.get(attribute) * scl)
                i += 2
            }
        }
    }

    fun getEff(floorCount: ObjectIntMap<Floor>): Float {
        var res = baseEfficiency

        for (entry in floorCount) {
            res += floorEff.get(entry.key, 0f) * entry.value
        }

        return res
    }

    override fun type(): ConsumeType<*> {
        return SglConsumeType.floor
    }

    override fun buildIcons(table: Table) {
        table.image(Icon.terrain)
    }

    override fun merge(other: BaseConsume<T>) {
        if (other is SglConsumeFloor<*>) {
            for (o in other.floorEff) {
                if (o is ObjectFloatMap<*>) {
                    for (entry in (o as ObjectFloatMap<Floor?>)) {
                        floorEff.put(entry.key, floorEff.get(entry.key, 1f) * entry.value)
                    }
                }
            }

            return
        }
        throw IllegalArgumentException("only merge consume with same type")
    }

    override fun consume(entity: T) {
        //no action
    }

    override fun update(entity: T) {
        //no action
    }

    override fun display(stats: Stats) {
        stats.add(Stat.tiles) { st: Table? ->
          st!!.row().table(SglDrawConst.grayUIAlpha) { t: Table? ->
            t!!.clearChildren()
            t.defaults().pad(5f).left()
            var c = 0
            for (entry in floorEff) {
              t.stack(
                Image(entry.key!!.uiIcon).setScaling(Scaling.fit),
                Table { table: Table? ->
                  table!!.top().right().add((if (entry.value < 0) "[scarlet]" else if (baseEfficiency == 0f) "[accent]" else "[accent]+") + (entry.value * 100).toInt() + "%").style(Styles.outlineLabel)
                  table.top().left().add("/" + StatUnit.blocks.localized()).color(Pal.gray)
                }
              ).fill().padRight(4f)
              t.add(entry.key!!.localizedName).left().padLeft(0f)
              c++

              if (c != 0 && c % 3 == 0) {
                t.row()
              }
            }
          }.fill()
        }
    }

    override fun build(entity: T, table: Table) { /*none*/
    }

    override fun buildBars(entity: T, bars: Table) {
        bars.row()
        bars.add(
            Bar(
                { Core.bundle.get("infos.floorEfficiency") + ": " + Strings.autoFixed(Mathf.round(efficiency(entity) * 100).toFloat(), 0) + "%" },
                { Pal.accent },
                { Mathf.clamp(efficiency(entity)) }
            )).growX().height(18f).pad(4f)
        bars.row()
    }

    override fun efficiency(entity: T): Float {
        return getEff(entity.floorCount)
    }

    override fun filter(): Seq<Content?>? {
        return null
    }
}