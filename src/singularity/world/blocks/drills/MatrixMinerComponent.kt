package singularity.world.blocks.drills

import arc.Core
import arc.func.Boolf
import arc.func.Cons
import arc.func.Floatf
import arc.func.Prov
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Tex
import mindustry.type.Liquid
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import singularity.world.blocks.drills.MatrixMiner.MatrixMinerBuild
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeLiquidBase
import universecore.world.consumers.ConsumeLiquidCond
import universecore.world.consumers.ConsumeType
import kotlin.math.max

open class MatrixMinerComponent(name: String) : MatrixMinerPlugin(name) {
    fun newBoost(baseBoostScl: Float, attributeMultiplier: Float, filter: Boolf<Liquid?>, usageBase: Float) {
        newBoost(
            { liquid: Liquid? -> baseBoostScl + (liquid!!.heatCapacity * 1.2f - (liquid.temperature - 0.35f) * 0.6f) * attributeMultiplier },
            { liquid: Liquid? -> !liquid!!.gas && liquid.coolant && filter.get(liquid) },
            usageBase,
            { liquid: Liquid? -> usageBase / (liquid!!.heatCapacity * 0.7f) }
        )
    }
init {
  buildType= Prov(::MatrixMinerComponentBuild)
}
    fun newBoost(boostEff: Floatf<Liquid?>, filters: Boolf<Liquid?>?, usageBase: Float, usageMult: Floatf<Liquid?>) {
        newOptionalConsume({ e: MatrixMinerComponentBuild, c: BaseConsumers -> }, { s: Stats?, c: BaseConsumers? ->
            s!!.add(Stat.booster, StatValue { t: Table? ->
                t!!.row()
                if (c!!.get<ConsumeLiquidBase<*>>(ConsumeType.liquid) is ConsumeLiquidCond<*>) {
                    var cons= c!!.get(ConsumeType.liquid) as ConsumeLiquidCond<ConsumerBuildComp>
                    for (stack in cons.cons) {
                        val liquid: Liquid = stack.liquid

                        t.add<Table?>(StatValues.displayLiquid(liquid, usageBase * usageMult.get(liquid) * 60, true)).padRight(10f).left().top()
                        t.table(Tex.underline, Cons { bt: Table? ->
                            bt!!.left().defaults().padRight(3f).left()
                            bt.add("[lightgray]" + Core.bundle.get("misc.efficiency") + "[accent]" + Strings.autoFixed(boostEff.get(liquid) * 100, 2) + "%[]")
                        }).left().padTop(-9f)
                        t.row()
                    }
                }
            })
        })
        consume!!.optionalAlwaysValid = false
        consume!!.add(object : ConsumeLiquidCond<MatrixMinerComponentBuild>() {
            init {
                liquidEfficiency = boostEff
                filter = filters
                usage = usageBase
                usageMultiplier = usageMult

                maxFlammability = 0.1f
            }

            public override fun display(stats: Stats) {}
        })
    }

    inner class MatrixMinerComponentBuild : MatrixMinerPluginBuild() {
        var progress: Float = 0f

        public override fun updateTile() {
            super.updateTile()
            val curr = consumer!!.current
            if (curr == null) return

            if (consumeValid()) {
                progress += (1 / curr.craftTime) * curr.delta(this) * warmup
                while (progress > 1) {
                    progress--
                    consumer!!.trigger()
                }
            }
        }

        public override fun boost(): Float {
            if (consumer!!.optionalCurr == null) return consEfficiency()
            return max(consumer!!.getOptionalEff(consumer!!.optionalCurr!!), 1f) * consEfficiency()
        }

        public override fun updatePlugin(owner: MatrixMinerBuild?) {}

        override fun write(write: Writes) {
            super.write(write)
            write.f(progress)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            progress = read.f()
        }
    }
}