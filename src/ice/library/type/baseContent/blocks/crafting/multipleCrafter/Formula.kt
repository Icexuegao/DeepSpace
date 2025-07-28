package ice.library.type.baseContent.blocks.crafting.multipleCrafter;

import arc.scene.ui.layout.Table
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.consumers.Consume
import mindustry.world.consumers.ConsumePower
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats

class Formula {
    var inputs: Array<Consume>? = null
    var outputItems: Array<ItemStack>? = null
    var outputLiquids: Array<LiquidStack>? = null
    var craftTime: Float = 60f
    var liquidOutputDirections: IntArray = intArrayOf(-1)
    var craftEffect: Effect = Fx.none
    var updateEffect: Effect = Fx.none
    var updateEffectChance: Float = 0.04f
    var warmupSpeed: Float = 0.019f
    var powerProduction: Float = 0f

    var consPower : ConsumePower?=null
    fun setInput(vararg input: Consume) {
        this.inputs = arrayOf(*input)
    }

    fun setOutput(vararg outputItems: ItemStack) {
        this.outputItems = arrayOf(*outputItems)
    }

    fun setOutput(vararg outputLiquids: LiquidStack) {
        this.outputLiquids = arrayOf(*outputLiquids)
    }

    fun set(`in`: Array<Consume>, outputItems: Array<ItemStack>, outputLiquids: Array<LiquidStack>): Formula {
        inputs = `in`
        this.outputItems = outputItems
        this.outputLiquids = outputLiquids
        return this
    }

    fun getPowerProduction(value: Float): Formula {
        this.powerProduction = value
        return this
    }


    fun apply(block: Block) {
        inputs?.let {
            for (c in it) {
                c.apply(block)
            }
            for (c in it) {
                if (c is ConsumePower) {
                    consPower = c
                }
                c.apply(block)
            }
        }


        if (powerProduction > 0) {
            block.hasPower = true
            block.outputsPower = true
        }
    }

    fun update(build: Building) {
        inputs?.let {
            for (c in it) {
                c.update(build)
            }
        }
    }

    fun trigger(build: Building) {
        inputs?.let {
            for (c in it) {
                c.trigger(build)
            }
        }
    }

    fun display(stats: Stats, block: Block) {
        stats.timePeriod = craftTime
        inputs?.let {
            for (c in it) {
                c.display(stats)
            }
        }
        if ((block.hasItems && block.itemCapacity > 0) || outputItems != null) {
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds)
        }

        outputItems?.let {
            stats.add(Stat.output, StatValues.items(craftTime, *it))
        }
        outputLiquids?.let {
            stats.add(Stat.output, StatValues.liquids(1f, *it))
        }

        if (powerProduction > 0) {
            stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond)
        }
    }

    fun build(build: Building, table: Table) {
        inputs?.let {
            table.pane { t: Table ->
                for (c in it) {
                    c.build(build, t)
                }
            }
        }

    }

    override fun toString(): String {
        return "Formula{" + "input=" + inputs.contentToString() + ", outputItems=" + outputItems.contentToString() + ", outputLiquids=" + outputLiquids.contentToString() + ", craftTime=" + craftTime + ", powerProduction=" + powerProduction + '}'
    }
}
