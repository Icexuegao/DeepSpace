package ice.world.content.blocks.crafting.multipleCrafter

import arc.Core
import arc.func.Prov
import arc.graphics.Texture
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.scene.ui.Button
import arc.scene.ui.layout.Table
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Strings
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.core.SettingValue
import ice.graphics.IStyles
import ice.library.scene.element.display.ItemDisplay
import ice.library.scene.element.display.LiquidDisplay
import ice.library.scene.element.display.TimeDisplay
import ice.library.scene.ui.ConsumeTable.display
import ice.library.scene.ui.iTable
import ice.library.scene.ui.iTableG
import ice.library.scene.ui.iTableGY
import ice.library.util.percent
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.meta.IStatValues.formulas
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.gen.Building
import mindustry.gen.Iconc
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.world.consumers.Consume
import mindustry.world.consumers.ConsumeLiquid
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.consumers.ConsumePower
import mindustry.world.meta.BlockFlag
import kotlin.math.max
import kotlin.math.min

class MultipleCrafter(name: String) : IceBlock(name) {
    var formulas = FormulaStack()
    var dumpExtraLiquid = true
    var ignoreLiquidFullness = false

    init {
      saveConfig=true
        solid = true
        update = true
        conductivePower = true
        hasItems = true
        drawArrow = false
        configurable = true
        ambientSound = Sounds.massdriver
        ambientSoundVolume = 0.03f
        flags = EnumSet.of(BlockFlag.factory)
        config(Int::class.javaObjectType
        ) { build: MultipleCrafterBuilding, value: Int -> build.setIndex(value) }
        buildType = Prov(::MultipleCrafterBuilding)
        consumePowerDynamic<MultipleCrafterBuilding> {
            it.consPower?.usage ?: 0f
        }
    }

    override fun setStats() {
        super.setStats()
        stats.add(IceStats.配方, formulas(formulas, this))
    }

    override fun setBars() {
        super.setBars()
        var added = false
        var outPower = false
        val addedLiquids = Seq<Liquid>()
        for (f in formulas.formulas) {
            if (f.powerProduction > 0) outPower = true
            f.inputs?.let {
                for (cons in it) {
                    if (cons is ConsumeLiquid) {
                        added = true
                        if (addedLiquids.contains(cons.liquid)) continue
                        addedLiquids.add(cons.liquid)
                        addLiquidBar(cons.liquid)
                    } else if (cons is ConsumeLiquids) {
                        added = true
                        for (stack in cons.liquids) {
                            if (addedLiquids.contains(stack.liquid)) continue
                            addedLiquids.add(stack.liquid)
                            addLiquidBar(stack.liquid)
                        }
                    }
                }
            }
            f.outputLiquids?.let {
                for (out in it) {
                    if (addedLiquids.contains(out.liquid)) continue
                    addedLiquids.add(out.liquid)
                    addLiquidBar(out.liquid)
                }
            }

            if (!added) {
                if (formulas.outputLiquids()) {
                    addLiquidBar { build: Building -> build.liquids.current() }
                }
            }
        }
        if (outPower) {
            addBar("outPower"
            ) { entity: MultipleCrafterBuilding ->
                Bar(
                    {
                        Core.bundle.format("bar.poweroutput",
                            Strings.fixed(entity.powerProduction * 60 * entity.timeScale(), 1))
                    },
                    { Pal.powerBar }, { entity.efficiency })
            }
        }


        if (consPower != null) {
            addBar("power") { entity: MultipleCrafterBuilding ->
                Bar({
                    val cur = entity.power.status * (entity.consPower?.usage
                        ?: 0f) * 60 * entity.timeScale() * (if (entity.shouldConsume()) 1f else 0f)
                    Iconc.power + " " + percent(
                        cur,
                        (entity.consPower?.usage
                            ?: 0f) * 60 * entity.timeScale() * (if (entity.shouldConsume()) 1f else 0f),
                        entity.timeScale() * 100 * (if (entity.shouldConsume()) 1f else 0f) * entity.efficiency
                    )
                }, { Pal.powerBar }, {
                    if (Mathf.zero(consPower.requestedPower(
                            entity)) && entity.power.graph.powerProduced + entity.power.graph.batteryStored > 0f
                    ) 1f else entity.power.status
                })
            }
        }



        addBar("productionProgress") { build: MultipleCrafterBuilding ->
            Bar({ IceStats.生产进度.localized() }, { Pal.ammo }) { build.progress }
        }
    }

    override fun rotatedOutput(x: Int, y: Int): Boolean {
        return false
    }

    override fun init() {
        super.init()
        formulas.apply(this)
        if (hasPower && consumesPower) {
            val cs: ArrayList<ConsumePower> = ArrayList()
            formulas.formulas.forEach { f ->
                val p: ConsumePower? = f.consPower
                p?.let {
                    cs.add(it)
                }
            }

        }
        hasConsumers = true
    }

    override fun outputsItems(): Boolean {
        return formulas.outputItems()
    }

    fun addFormula(formula: Formula.() -> Unit) {
        val p1 = Formula()
        formula(p1)
        formulas.addFormulas(p1)
    }

    inner class MultipleCrafterBuilding : IceBuild() {
        var progress: Float = 0f
        var totalProgress: Float = 0f
        var warmup: Float = 0f
        var formulaIndex: Int = 0
        var formula: Formula = formulas.getFormula(formulaIndex)
        var outputItems: Array<ItemStack>? = formula.outputItems
        var outputLiquids: Array<LiquidStack>? = formula.outputLiquids
        var powerProductionTimer: Float = 0f
        var consPower: ConsumePower? = null
        override fun draw() {
            super.draw()

//            if (SettingValue.启用多合成角标常显&& !Vars.state.isEditor) {
//                drawcornerMark()
//            }
        }
        fun drawcornerMark(){
            Draw.z(Layer.block + 1f)
            outputItems?.let {
                drawItemSelection(it[0].item)
                return
            }
            outputLiquids?.let {
                drawItemSelection(it[0].liquid)
            }
            Draw.reset()
        }

        override fun shouldConsume(): Boolean {
            if (outputItems != null) {
                for (output in outputItems!!) {
                    if (items[output.item] + output.amount > itemCapacity) {
                        return false
                    }
                }
            }
            if (outputLiquids != null && !ignoreLiquidFullness) {
                var allFull = true
                for (output in outputLiquids!!) {
                    if (liquids[output.liquid] >= liquidCapacity - 0.001f) {
                        if (!dumpExtraLiquid) {
                            return false
                        }
                    } else {
                        //if there's still space left, it's not full for all liquids
                        allFull = false
                    }
                }
                //if there is no space left for any liquid, it can't reproduce
                if (allFull) {
                    return false
                }
            }
            return enabled
        }

        override fun config(): Int {
            return formulaIndex
        }

        override fun updateConsumption() {
            if (cheating()) {
                potentialEfficiency = if (enabled && productionValid()) 1.0f else 0.0f
                optionalEfficiency = if (shouldConsume()) potentialEfficiency else 0.0f
                efficiency = optionalEfficiency
                shouldConsumePower = true
                updateEfficiencyMultiplier()
                return
            }
            if (!enabled) {
                optionalEfficiency = 0.0f
                efficiency = optionalEfficiency
                potentialEfficiency = efficiency
                shouldConsumePower = false
                return
            }
            val update = shouldConsume() && productionValid()
            var minEfficiency = 1.0f
            optionalEfficiency = 1.0f
            efficiency = optionalEfficiency
            shouldConsumePower = true
            val nonOptionalConsumers = Seq(formula.inputs).select { consume ->
                !consume.optional && !consume.ignore()
            }.toArray<Consume>(Consume::class.java)
            val optionalConsumers = Seq(formula.inputs).select { consume ->
                consume.optional && !consume.ignore()
            }.toArray<Consume>(Consume::class.java)

            for (cons in nonOptionalConsumers) {
                val result = cons.efficiency(this)
                if (cons !== consPower && result <= 1.0E-7f) {
                    shouldConsumePower = false
                }
                minEfficiency = min(minEfficiency, result)
            }
            for (cons in optionalConsumers) {
                optionalEfficiency = min(optionalEfficiency, cons.efficiency(this))
            }
            efficiency = minEfficiency
            optionalEfficiency = min(optionalEfficiency, minEfficiency)
            potentialEfficiency = efficiency
            if (!update) {
                optionalEfficiency = 0.0f
                efficiency = optionalEfficiency
            }
            updateEfficiencyMultiplier()
            if (update && efficiency > 0) {
                for (cons in formula.inputs!!) {
                    cons.update(this)
                }
            }
        }

        override fun displayConsumption(table: Table) {
            super.displayConsumption(table)
            formula.build(this, table)
        }

        override fun updateTile() {
            formula = formulas.getFormula(formulaIndex)
            outputItems = formula.outputItems
            outputLiquids = formula.outputLiquids
            consPower = formula.consPower
            if (efficiency > 0) {
                progress += getProgressIncrease(formula.craftTime)
                warmup = Mathf.approachDelta(warmup, warmupTarget(), formula.warmupSpeed)
                //continuously output based on efficiency
                if (outputLiquids != null) {
                    val inc = getProgressIncrease(1f)
                    for (output in outputLiquids!!) {
                        handleLiquid(this, output.liquid,
                            min((output.amount * inc).toDouble(), (liquidCapacity - liquids[output.liquid]).toDouble())
                                .toFloat())
                    }
                }

                if (wasVisible && Mathf.chanceDelta(formula.updateEffectChance.toDouble())) {
                    formula.updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4))
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, formula.warmupSpeed)
            }
            totalProgress += warmup * Time.delta

            if (progress >= 1f) {
                craft()
            }

            dumpOutputs()
        }

        override fun drawSelect() {
            super.drawSelect()
            if (outputLiquids != null) {
                for (i in outputLiquids!!.indices) {
                    val dir = if (formula.liquidOutputDirections.size > i) formula.liquidOutputDirections[i] else -1

                    if (dir != -1) {
                        Draw.rect(outputLiquids!![i].liquid.fullIcon,
                            x + Geometry.d4x(dir + rotation) * (size * Vars.tilesize / 2f + 4),
                            y + Geometry.d4y(dir + rotation) * (size * Vars.tilesize / 2f + 4), 8f, 8f)
                    }
                }
            }
            if (!SettingValue.启用多合成角标常显&& !Vars.state.isEditor) {
                drawcornerMark()
            }
        }

        override fun getProgressIncrease(baseTime: Float): Float {
            if (ignoreLiquidFullness) {
                return super.getProgressIncrease(baseTime)
            }
            //limit progress increase by maximum amount of liquid it can produce
            var scaling = 1f
            var max = 1f
            if (outputLiquids != null) {
                max = 0f
                for (s in outputLiquids!!) {
                    val value = (liquidCapacity - liquids[s.liquid]) / (s.amount * edelta())
                    scaling = min(scaling, value)
                    max = max(max, value)
                }
            }
            //when dumping excess take the maximum value instead of the minimum.
            return super.getProgressIncrease(baseTime) * (if (dumpExtraLiquid) min(max, 1.0f) else scaling)
        }

        override fun getPowerProduction(): Float {
            return if (powerProductionTimer > 0f) formula.powerProduction * efficiency else 0f
        }

        fun warmupTarget(): Float {
            return 1f
        }

        override fun warmup(): Float {
            return warmup
        }

        override fun totalProgress(): Float {
            return totalProgress
        }

        fun craft() {
            formula.trigger(this)
            outputItems?.let {
                for (output in it) {
                    (1..output.amount).forEach { _ ->
                        offload(output.item)
                    }
                }
            }
            if (wasVisible) {
                formula.craftEffect.at(x, y)
            }
            progress %= 1f
            powerProductionTimer += formula.craftTime / efficiency + 1f
        }

        fun dumpOutputs() {
            if (outputItems != null && timer(timerDump, dumpTime / timeScale)) {
                for (output in outputItems!!) {
                    dump(output.item)
                }
            }
            outputLiquids?.let {
                for (i in it.indices) {
                    val dir = if (formula.liquidOutputDirections.size > i) formula.liquidOutputDirections[i] else -1

                    dumpLiquid(it[i].liquid, 2f, dir)
                }
            }

        }

        override fun buildConfiguration(table: Table) {
            super.buildConfiguration(table)
            table.iTable { itable ->
                for ((index, form) in formulas.formulas.withIndex()) {
                    itable.button({ button ->
                        button.iTableGY { inputTable ->
                            inputTable.table(IStyles.background42) {
                                form.inputs?.let { let ->
                                    let.forEach { cons ->
                                        cons.display(it)
                                    }
                                }
                                it.add(TimeDisplay(form.craftTime)).pad(5f)
                            }.margin(
                                5f
                            )
                        }.width(260f)
                        button.iTableG { arrow ->
                            arrow.image(IStyles.arrow1.apply {
                                texture.setFilter(Texture.TextureFilter.nearest)
                            }).size(180f, 80f)
                        }
                        button.iTableGY { out ->
                            out.table(IStyles.background42) {
                                form.outputItems?.let { items ->
                                    items.forEach { itemStack ->
                                        it.add(ItemDisplay(itemStack.item, itemStack.amount)).padRight(3f)
                                    }
                                }
                                form.outputLiquids?.let { liquids ->
                                    liquids.forEach { liquidStack ->
                                        it.add(
                                            LiquidDisplay(liquidStack.liquid, liquidStack.amount * 60,
                                                localizedName = false)).padRight(3f)
                                    }
                                }
                            }.margin(5f)
                        }.width(200f)
                        button.update {
                            button.isChecked = index == formulaIndex
                        }
                    }, Button.ButtonStyle().apply {
                        up = IStyles.background21
                        down = IStyles.background22
                        checked = IStyles.background22
                    }) {
                        setIndex(index)
                        configure(index)
                        Vars.control.input.config.hideConfig()
                    }.height(100f).margin(10f).pad(3f).row()
                }
            }
        }

        override fun sense(sensor: LAccess): Double {
            if (sensor == LAccess.progress) return progress().toDouble()
            return super.sense(sensor)
        }

        override fun progress(): Float {
            return Mathf.clamp(progress)
        }

        override fun getMaximumAccepted(item: Item): Int {
            return itemCapacity
        }

        override fun shouldAmbientSound(): Boolean {
            return efficiency > 0
        }

        fun setIndex(index: Int) {
            formulaIndex = index
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(progress)
            write.f(warmup)
            write.b(formulaIndex)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            progress = read.f()
            warmup = read.f()
            formulaIndex = read.b().toInt()
        }
    }
}

