package singularity.world.blocks.drills

import arc.Core
import arc.func.*
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.scene.ui.ImageButton
import arc.scene.ui.layout.Table
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Strings
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import mindustry.world.meta.*
import singularity.world.blocks.SglBlock
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsumers
import kotlin.math.min

/**基本的钻头，将钻头的材料消耗更改为使用重定义的consume，可在混合矿物地面选择一种进行开采 */
open class BaseDrill(name: String) : SglBlock(name) {
    /**钻头硬度，决定钻头可以开采的矿物，只有硬度大于矿物的硬度才可以开采 */
    var bitHardness: Int = 0

    /**基准采集时间，直接决定钻头的开采速度 */
    var drillTime: Float = 300f

    /**钻头预热速度，决定钻头提升到最大效率的速度 */
    var warmupSpeed: Float = 0.02f

    /**转子的旋转速度倍数 */
    var rotationSpeed: Float = 2f
    var maxRotationSpeed: Float = 3f
    var hardMultiple: Float = 50f

    /**每次采掘时触发的特效 */
    var drillEffect: Effect = Fx.mine

    /**钻头运行时产生的特效 */
    var updateEffect: Effect = Fx.pulverizeSmall

    /**钻头产生更新特效的频率 */
    var updateEffectChance: Float = 0.02f
    val ores: Seq<ItemStack> = Seq<ItemStack>()

    /**基本的钻头，将钻头的材料消耗更改为使用重定义的consume，可在混合矿物地面选择一种进行开采 */
    init {
        update = true
        solid = true
        sync = true
        hasItems = true
        outputItems = true
        configurable = true
        saveConfig = false
        oneOfOptionCons = true
        group = BlockGroup.drills
       // ambientSound = Sounds.drill
        ambientSoundVolume = 0.018f
    }

    public override fun appliedConfig() {
        super.appliedConfig()
        config<BooleanArray?, BaseDrillBuild?>(BooleanArray::class.java, Cons2 { entity: BaseDrillBuild?, b: BooleanArray -> entity!!.currentMines = b })
        configClear<BaseDrillBuild?>(Cons { e: BaseDrillBuild? -> e!!.currentMines = BooleanArray(e.currentMines.size) })
    }

    fun newBooster(increase: Float): BaseConsumers? {
        return newOptionalConsume(
            Cons2 { entity: ConsumerBuildComp?, cons: BaseConsumers? ->
                (entity as BaseDrillBuild).efficiencyIncrease = increase
                entity.boostTime = cons!!.craftTime
            },
            Cons2 { stats: Stats?, cons: BaseConsumers? ->
                stats!!.add(Stat.boostEffect, increase, StatUnit.timesSpeed)
            }
        )
    }

    public override fun setStats() {
        super.setStats()
        stats.add(Stat.drillTier, StatValues.blocks(Boolf { b: Block? -> b is Floor && !b.wallOre && b.itemDrop != null && b.itemDrop.hardness <= bitHardness && (Vars.indexer.isBlockPresent(b) || Vars.state.isMenu()) }))
        stats.add(Stat.drillSpeed, 60f / drillTime * size * size, StatUnit.itemsSecond)
    }

    override fun canPlaceOn(tile: Tile, team: Team?, rotate: Int): Boolean {
        if (isMultiblock()) {
            var re = false
            for (other in tile.getLinkedTilesAs(this, tempTiles)) {
                re = re or canMine(other)
            }
            return re
        } else {
            return canMine(tile)
        }
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        val tile = Vars.world.tile(x, y)
        if (tile == null) return

        getMines(tile, this, false, ores)

        if (ores.size > 0) {
            var line = 0
            for (stack in ores) {
                val width = if (stack.item.hardness <= bitHardness)  //可挖掘的矿物显示
                    drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / (drillTime + stack.item.hardness * hardMultiple) * stack.amount, 2), x, y - line, true) else  //不可挖掘的矿物显示
                    drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y - line, false)
                val dx = x * Vars.tilesize + offset - width / 2f - 4f
                val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5 - line * 8f
                Draw.mixcol(Color.darkGray, 1f)
                Draw.rect(stack.item.uiIcon, dx, dy - 1)
                Draw.reset()
                Draw.rect(stack.item.uiIcon, dx, dy)
                line++
            }
        }
    }

    fun getMines(tile: Tile, block: Block?): Seq<ItemStack> {
        return getMines(tile, block, Seq<ItemStack>())
    }

    fun getMines(tile: Tile, block: Block?, seq: Seq<ItemStack>): Seq<ItemStack> {
        return getMines(tile, block, true, seq)
    }

    fun getMines(tile: Tile, block: Block?, filtration: Boolean, seq: Seq<ItemStack>): Seq<ItemStack> {
        seq.clear()
        if (isMultiblock()) {
            for (other in tile.getLinkedTilesAs(block, tempTiles)) {
                if (if (filtration) canMine(other) else hasMine(other)) {
                    var mark = false
                    for (ores in seq) {
                        if (ores.item === other.drop()) {
                            ores.amount++
                            mark = true
                            break
                        }
                    }
                    if (!mark) seq.add(ItemStack(other.drop(), 1))
                }
            }
        } else {
            if (if (filtration) canMine(tile) else hasMine(tile)) seq.add(ItemStack(tile.drop(), 1))
        }
        return seq
    }

    fun canMine(tile: Tile): Boolean {
        return hasMine(tile) && tile.drop().hardness <= bitHardness
    }

    fun hasMine(tile: Tile?): Boolean {
        if (tile == null) return false
        return tile.drop() != null
    }

    open inner class BaseDrillBuild : SglBuilding() {
        var progress: FloatArray = floatArrayOf()
        var consumeProgress: Float = 0f
        var warmup: Float = 0f
        var currentMines: BooleanArray = booleanArrayOf()
        var rotatorAngle: Float = 0f
        var efficiencyIncrease: Float = 1f
        var boostTime: Float = 0f
        var lastDrillSpeed: FloatArray = floatArrayOf()
        var outputItems: Seq<ItemStack> = Seq<ItemStack>()
        var mineOreItems: ObjectSet<Item?> = ObjectSet<Item?>()
        var speed: Float = 0f

        override fun warmup(): Float {
            return warmup
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            getMines(tile, block, outputItems)
            var reset = mineOreItems.size != outputItems.size
            if (!reset) {
                for (stack in outputItems) {
                    if (!mineOreItems.contains(stack.item)) {
                        reset = true
                        break
                    }
                }
            }
            if (reset) {
                mineOreItems.clear()
                for (stack in outputItems) {
                    mineOreItems.add(stack.item)
                }
                currentMines = BooleanArray(outputItems.size)
                progress = FloatArray(outputItems.size)
                lastDrillSpeed = FloatArray(outputItems.size)
                if (outputItems.size == 1) currentMines[0] = true
            }
        }

        public override fun displayBars(bars: Table) {
            super.displayBars(bars)
            for (i in 0..<outputItems.size) {
                if (!currentMines[i]) continue
                val finalI = i
                bars.add<Bar?>(
                    Bar(
                        Prov { outputItems.get(finalI).item.localizedName + " : " + Core.bundle.format("bar.drillspeed", Strings.fixed(lastDrillSpeed[finalI] * 60 * timeScale(), 2)) },
                        Prov { Pal.ammo },
                        Floatp { warmup }
                    )).growX()
                bars.row()
            }
        }

        public override fun outputItems(): Seq<Item>? {
            return outputItems.map<Item?>(Func { e: ItemStack? -> e!!.item })
        }

        public override fun shouldConsume(): Boolean {
            return super.shouldConsume() && !this.isFull && miningAny()
        }

        fun miningAny(): Boolean {
            for (bool in currentMines) {
                if (bool) return true
            }
            return false
        }

        override fun buildConfiguration(table: Table) {
            super.buildConfiguration(table)
            if (outputItems.size > 1) {
                val mines = Table(Tex.buttonTrans)
                mines.defaults().grow().marginTop(0f).marginBottom(0f).marginRight(5f).marginRight(5f)
                mines.add(Core.bundle.get("fragment.buttons.selectMine")).padLeft(5f).padTop(5f).padBottom(5f)
                mines.row()
                val buttons = Table()
                for (i in 0..<outputItems.size) {
                    val f = i
                    val stack = outputItems.get(i)
                    val button = ImageButton(stack.item.uiIcon, Styles.selecti)
                    button.clicked(Runnable {
                        currentMines[f] = !currentMines[f]
                        configure(currentMines)
                    })
                    button.update(Runnable { button.setChecked(currentMines[f]) })
                    buttons.add<ImageButton?>(button).size(50f, 50f)
                    if ((i + 1) % 4 == 0) buttons.row()
                }
                mines.add<Table?>(buttons)
                table.add<Table?>(mines)
                table.row()
            }
        }

        override fun updateTile() {
            if (updateValid()) {
                speed = Mathf.lerpDelta(speed, efficiencyIncrease * consEfficiency(), warmupSpeed)
                warmup = Mathf.lerpDelta(warmup, 1f, warmupSpeed)
                consumeProgress += consumer!!.consDelta() * warmup
                if (Mathf.chanceDelta((updateEffectChance * warmup).toDouble())) {
                    updateEffect.at(getX() + Mathf.range(size * 4f), getY() + Mathf.range(size * 4))
                }
            } else {
                speed = Mathf.lerpDelta(speed, 0f, warmupSpeed * 1.5f)
                warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed * 1.5f)
            }

            if (currentMines.size != outputItems.size) {
                currentMines = BooleanArray(outputItems.size)
                progress = FloatArray(outputItems.size)
                lastDrillSpeed = FloatArray(outputItems.size)
            }
            for (index in 0..<outputItems.size) {
                if (!currentMines[index]) continue
                val ore = outputItems.get(index)

                progress[index] += consumer!!.consDelta() * ore.amount * warmup * speed
                lastDrillSpeed[index] = (ore.amount * warmup * speed) / (drillTime + ore.item.hardness * hardMultiple)
                val delay = drillTime + ore.item.hardness * hardMultiple

                if (progress[index] >= delay) {
                    val i: Int
                    items.add(ore.item, min((progress[index] / delay).toInt().also { i = it }, itemCapacity - items.total()))
                    progress[index] -= i * delay
                    drillEffect.at(getX() + Mathf.range(size), getY() + Mathf.range(size), outputItems.get(index).item.color)
                }

                dump(outputItems.get(index).item)
            }

            rotatorAngle += min(maxRotationSpeed, speed * warmup * Time.delta * rotationSpeed)

            if (consumeProgress >= 1) {
                consumer!!.trigger()
                consumeProgress = 0f
            }

            if (boostTime > 0) boostTime -= 1f
            if (boostTime <= 0) efficiencyIncrease = 1f
        }

        val isFull: Boolean
            get() = items.total() >= block.itemCapacity

        public override fun updateValid(): Boolean {
            return consumeValid() && !this.isFull && miningAny()
        }

        public override fun write(write: Writes) {
            super.write(write)
            write.f(consumeProgress)
            write.f(warmup)
            write.f(speed)

            write.i(outputItems.size)
            for (i in 0..<outputItems.size) {
                write.i(outputItems.get(i).item.id.toInt())
                write.bool(currentMines[i])
                write.f(progress[i])
            }
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            consumeProgress = read.f()
            warmup = read.f()
            speed = read.f()
            val length = read.i()
            currentMines = BooleanArray(length)
            progress = FloatArray(length)
            lastDrillSpeed = FloatArray(length)
            mineOreItems.clear()
            for (i in 0..<length) {
                mineOreItems.add(Vars.content.item(read.i()))
                currentMines[i] = read.bool()
                progress[i] = read.f()
            }
        }
    }
}