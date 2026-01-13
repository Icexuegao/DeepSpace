package singularity.world.blocks.product

import arc.Core
import arc.func.*
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.scene.ui.Button
import arc.scene.ui.ImageButton
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.util.Scaling
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import arc.util.serialization.Jval
import mindustry.Vars
import mindustry.ai.UnitCommand
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.entities.Units
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.mod.Mods.LoadedMod
import mindustry.type.*
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.blocks.payloads.UnitPayload
import mindustry.world.blocks.units.Reconstructor
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValue
import singularity.Sgl
import singularity.core.ModsInteropAPI
import singularity.core.ModsInteropAPI.ConfigModel
import singularity.graphic.SglDrawConst
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.PayloadBuildComp
import singularity.world.components.distnet.DistElementBlockComp
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.consumers.SglConsumers
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.GridChildType
import singularity.world.distribution.buffers.ItemsBuffer
import singularity.world.distribution.request.DistRequestBase
import singularity.world.meta.SglStat
import singularity.world.modules.DistributeModule
import singularity.world.products.Producers
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeItemBase
import universecore.world.consumers.ConsumeType
import universecore.world.producers.BaseProducers
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader

open class SglUnitFactory(name: String) : PayloadCrafter(name), DistElementBlockComp {
    enum class ConfigCmd {
        CLEAR_TASK,
        ADD_TASK,
        SET_PRIORITY,
        QUEUE_MODE,
        SKIP_BLOCKED,
        ACTIVITY,
        REMOVE_TASK,
        RISE_TASK,
        DOWN_TASK,
        COMMAND
    }
    companion object {
        fun make(type: UnitType?, amount: Int, factoryIndex: Int): BuildTask {
            val task = Pools.obtain(BuildTask::class.java, Prov { BuildTask() })
            task.buildUnit = type
            task.factoryIndex = factoryIndex
            task.queueAmount = amount

            task.command = if (task.buildUnit!!.defaultCommand != null) task.buildUnit!!.defaultCommand else task.buildUnit!!.commands.first()

            return task
        }
        var costModels: ObjectMap<UnitType?, UnitCostModel?> = ObjectMap<UnitType?, UnitCostModel?>()

        init {
            /*添加mod交互式API模型，用于其他mod定义单位在单位建造机上的建造成本
    * 通常条目的格式：
    * ...
    * "unitFactoryCosts": {
    *   "$unitTypeName": {//选中单位的内部名称，mod名称前缀可选，默认选择本mod中的content，一般不建议跨mod配置单位数据
    *     "requirements": {"$itemName": #, "$itemName": #, ...},//键值对式声明，键为物品名称，值为数量
    *     //或者 "requirements": ["$itemName", #, "$itemName", #, ...],//数组式声明，第一个元素为物品名称，第二个元素为数量，以此类推
    *     //亦或者 ": ["$itemName/#", "$itemName/#", ...],//字符串声明模式，将物品名称和数量用‘/’进行连接
    *     "minLevel": #, //需要的工厂制造等级
    *     "baseBuildTime": # //基础建造时间
    *   },
    *   "$unitTypeName": {
    *     "requirements": {"$itemName": #, ...},
    *     "minLevel": #,
    *     "baseBuildTime": #
    *   },
    *   ...
    * }
    * ...
    *
    * 若要禁用某个单位的建造（默认情况下会自动分配单位的建造成本，可禁用），格式如下:
    * ...
    * "unitFactoryCosts": {
    *   "$unitTypeName": {
    *     "disabled": true
    *   },
    *   ...
    * }
    * ...
    * */
            if (Sgl.config.interopAssignUnitCosts) {
                Sgl.interopAPI.addModel(object : ConfigModel("unitFactoryCosts") {
                    override fun parse(mod: LoadedMod, declaring: Jval) {
                        for (entry in declaring.asObject()) {
                            val unit = ModsInteropAPI.selectContent<UnitType?>(ContentType.unit, entry.key, mod, true)
                            val model = UnitCostModel()

                            if (entry.value.getBool("disabled", false)) {
                                model.disabled = true
                                costModels.put(unit, model)
                                continue
                            }
                            val arr = entry.value.get("requirements").asArray()

                            model.requirements = arrayOfNulls<ItemStack>(arr.size)
                            for (i in 0..<arr.size) {
                                val stack = arr.get(i)
                                if (stack.isObject()) {
                                    val ent = stack.asObject()
                                    model.requirements!![i] = ItemStack(
                                        ModsInteropAPI.selectContent<Item?>(ContentType.item, ent.firstKey(), mod),
                                        ent.firstValue().asInt()
                                    )
                                } else if (stack.isArray()) {
                                    val ar = stack.asArray()
                                    model.requirements!![i] = ItemStack(
                                        ModsInteropAPI.selectContent<Item?>(ContentType.item, ar.get(0).asString(), mod),
                                        ar.get(1).asInt()
                                    )
                                } else {
                                    val str: Array<String?> = stack.asString().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                    model.requirements!![i] = ItemStack(
                                        ModsInteropAPI.selectContent<Item?>(ContentType.item, str[0], mod),
                                        str[1]!!.toInt()
                                    )
                                }
                            }
                            model.baseBuildTime = entry.value.getFloat("baseBuildTime", 0f)
                            model.minLevel = entry.value.getInt("minLevel", 0)

                            costModels.put(unit, model)
                        }
                    }

                    override fun disable(mod: LoadedMod?) {
                        for (unit in Vars.content.units()) {
                            if (unit.minfo.mod === mod) {
                                val model = UnitCostModel()
                                model.disabled = true
                                costModels.put(unit, model)
                            }
                        }
                    }
                }, false)
            }
        }

        fun setCost(unit: UnitType?, req: Array<ItemStack?>, level: Int, baseTime: Float) {
            costModels.put(unit, object : UnitCostModel() {
                init {
                    this.requirements = req
                    this.minLevel = level
                    this.baseBuildTime = baseTime
                }
            })
    }

    }
    var maxTasks: Int = 16
    var sizeLimit: Float = 0f
    var healthLimit: Float = 0f
    var timeMultiplier: Float = 20f
    var baseTimeScl: Float = 0.3f
    var machineLevel: Int = 0

    /**是否只能通过矩阵网络分配建造材料 */
    var matrixDistributeOnly: Boolean = false
    var consCustom: Cons2<UnitType?, SglConsumers?>? = null
    var byProduct: Cons2<UnitType?, Producers?>? = null

    init {
        autoSelect = false
        canSelect = false
        hasItems = true

        configurable = true
    }

    public override fun appliedConfig() {
        config<IntSeq?, SglUnitFactoryBuild?>(IntSeq::class.java, Cons2 { u: SglUnitFactoryBuild?, i: IntSeq? ->
            val handle = i!!.get(0)
            when (ConfigCmd.entries[handle]) {
                ConfigCmd.CLEAR_TASK -> u!!.clearTask()
                ConfigCmd.ADD_TASK -> u!!.appendTask(Vars.content.unit(i.get(1)), i.get(2), i.get(3))
                ConfigCmd.SET_PRIORITY -> u!!.priority(i.get(1))
                ConfigCmd.QUEUE_MODE -> u!!.queueMode = i.get(1) > 0
                ConfigCmd.SKIP_BLOCKED -> u!!.skipBlockedTask = i.get(1) > 0
                ConfigCmd.ACTIVITY -> u!!.activity = i.get(1) > 0
                ConfigCmd.REMOVE_TASK -> u!!.removeTask(u.getTask(i.get(1)))
                ConfigCmd.RISE_TASK -> u!!.riseTask(u.getTask(i.get(1)))
                ConfigCmd.DOWN_TASK -> u!!.downTask(u.getTask(i.get(1)))
                ConfigCmd.COMMAND -> {
                    val t = u!!.getTask(i.get(1))
                    t.targetPos = Vec2(i.get(2) / 1000f, i.get(3) / 1000f)

                    if (Vars.world.buildWorld(t.targetPos!!.x, t.targetPos!!.y) === u) {
                        t.targetPos = null
                    }
                    //  t.command = UnitCommand.assistCommand.ge(i.get(4));
                }

                else -> throw IllegalArgumentException("unknown operate code: " + handle)
            }
        })
    }

    override fun setStats() {
        super.setStats()

        stats.remove(Stat.itemCapacity)
        stats.remove(SglStat.recipes)
        stats.remove(SglStat.autoSelect)
        stats.remove(SglStat.controllable)

        stats.add(SglStat.sizeLimit, sizeLimit)
        stats.add(SglStat.healthLimit, healthLimit)
        stats.add(SglStat.buildLevel, machineLevel.toFloat())

        stats.add(SglStat.recipes, StatValue { t: Table? ->
            t!!.left().defaults().left().growX()
            if (matrixDistributeOnly) {
                t.row()
                t.add(Core.bundle.get("infos.matrixDistOnly")).color(Color.darkGray)
            }
            val table = Table(SglDrawConst.grayUIAlpha)
            val coll = Collapser(table, true)

            table.left().defaults().left().pad(3f).size(185f, 60f)
            var c = 0
            for (producer in producers!!) {
                if (producer.cons!!.selectable.get() == BaseConsumers.Visibility.hidden) continue
                val pd = producer.get<ProducePayload<*>>(ProduceType.payload)

                if (pd == null || pd.payloads.size != 1) continue
                val button: Button = object : Button(Styles.clearNonei) {
                    init {
                        left().defaults().left().padLeft(4f)
                        image(pd.payloads[0].item.fullIcon).scaling(Scaling.fit).size(50f)
                        add(pd.payloads[0].item.localizedName).growX()

                        clicked(Runnable {
                            Vars.ui.content.show(pd.payloads[0].item)
                        })

                        setDisabled(Boolp { producer.cons!!.selectable.get() == BaseConsumers.Visibility.unusable })
                    }

                    override fun draw() {
                        super.draw()

                        if (producer.cons!!.selectable.get() == BaseConsumers.Visibility.unusable) {
                            Draw.color(Pal.darkerGray)
                            Draw.alpha(0.8f * parentAlpha)

                            Fill.rect(x + width / 2, y + height / 2, width, height)

                            Draw.color(Color.lightGray, parentAlpha)
                            Icon.lock.draw(x + width / 2, y + height / 2, 28f, 28f)
                        }
                    }
                }

                table.add<Button?>(button)

                c++
                if (c % 4 == 0) {
                    c = 0
                    table.row()
                }
            }

            t.row()
            t.table(Cons { inf: Table? ->
                inf!!.left().defaults().left()
                inf.add(Core.bundle.get("infos.buildableList") + ": ")
                inf.button(Icon.downOpen, Styles.emptyi, Runnable { coll.toggle(false) }).update(Cons { i: ImageButton? -> i!!.getStyle().imageUp = (if (!coll.isCollapsed()) Icon.upOpen else Icon.downOpen) }).size(8f).padLeft(16f).expandX()
            })
            t.row()
            t.add<Collapser?>(coll)
        })
    }

    override fun init() {
        setupProducts()
        super.init()
    }

    override fun setBars() {
        super.setBars()

        addBar<SglUnitFactoryBuild?>("progress", Func { entity: SglUnitFactoryBuild? ->
            Bar(
                Prov { Core.bundle.format("bar.numprogress", Strings.autoFixed(entity!!.progress() * 100, 1)) },
                Prov { Pal.ammo },
                Floatp { entity!!.progress() })
        }
        )
    }

    fun setupProducts() {
        for (unit in Vars.content.units()) {
            if (unit.hitSize < sizeLimit && unit.health < healthLimit) {
                val req: Array<ItemStack?>
                val buildTime: Float
                var level = 1
                val costModel = costModels.get(unit)
                if (costModel != null) {
                    if (costModel.disabled) continue

                    req = costModel.requirements!!  as Array<ItemStack?>
                    buildTime = costModel.baseBuildTime * timeMultiplier * (baseTimeScl + (1 - baseTimeScl) * Mathf.sqrt((unit.hitSize / sizeLimit) * (unit.health / healthLimit)))
                    level = costModel.minLevel
                } else {
                    req = unit.getTotalRequirements()
                    buildTime = unit.getBuildTime() * timeMultiplier * (baseTimeScl + (1 - baseTimeScl) * Mathf.sqrt((unit.hitSize / sizeLimit) * (unit.health / healthLimit)))
                    val tmp: Array<UnitType?> = arrayOf<UnitType>(unit) as Array<UnitType?>
                    while (tmp[0] != null) {
                        val lis = arrayOfNulls<Array<UnitType?>>(1)
                        if (Vars.content.blocks().contains(Boolf { b: Block? -> b is Reconstructor && (b.upgrades.find(Boolf { u: Array<UnitType?>? -> u!![1] === tmp[0] }).also { lis[0] = it }) != null })) {
                            tmp[0] = lis[0]!![0]
                        } else tmp[0] = null

                        level++
                    }
                }

                if (level <= machineLevel) {
                    if (req.size == 0) continue

                    newConsume()
                    val req1 = req as Array<ItemStack>
                    consume!!.items(*req1).displayLim = -1
                    consume!!.time(buildTime)

                    consume!!.selectable = Prov { if (!unit.supportsEnv(Vars.state.rules.env)) BaseConsumers.Visibility.hidden else if (unit.unlockedNow()) BaseConsumers.Visibility.usable else BaseConsumers.Visibility.unusable }

                    if (consCustom != null) consCustom!!.get(unit, consume)
                    newProduce()
                    produce!!.add(ProducePayload(PayloadStack.with(unit, 1), Boolf2 { b: SglUnitFactoryBuild?, c: UnlockableContent? -> b!!.payloads()!!.total() <= 0 }))
                    if (byProduct != null) byProduct!!.get(unit, produce)
                }
            }
        }
    }

    override fun topologyUse(): Int {
        return 1
    }

    override fun matrixEnergyUse(): Float {
        return 0.5f
    }

    override val isNetLinker=false

    open class UnitCostModel {
        var requirements: Array<ItemStack?>?=null
        var baseBuildTime: Float = 0f
        var minLevel: Int = 0
        var disabled: Boolean = false
    }

    open inner class SglUnitFactoryBuild : PayloadCrafterBuild(), DistElementBuildComp {
        var activity: Boolean = false
        var queueMode: Boolean = false
        var skipBlockedTask: Boolean = false
        private var buildCount = 0
        private var taskCount = 0
        private var taskQueueHead: BuildTask? = null
        private var taskQueueLast: BuildTask? = null
        var currentTask: BuildTask? = null
            private set
        protected var lastOutputProgress: Float = 0f
        var priority: Int = 0
        var distributor: DistributeModule? = null
        var itemsBuffer: ItemsBuffer? = null
        var pullItemsRequest: PullMaterialRequest? = null

        override fun getPayload()=  payloads()!!.get()
        fun buildCount(): Int {
            return buildCount
        }

        fun taskCount(): Int {
            return taskCount
        }

        fun popTask(): BuildTask {
            val res = taskQueueHead
            taskQueueHead = taskQueueHead!!.next
            if (taskQueueHead != null) {
                taskQueueHead!!.pre = null
            } else taskQueueLast = null

            taskCount--

            queueUpdated()

            return res!!
        }

        fun clearTask() {
            taskQueueHead = null
            taskQueueLast = taskQueueHead
            taskCount = 0
            queueUpdated()
        }

        fun riseTask(task: BuildTask) {
            if (task.pre == null) return

            if (task.pre === taskQueueHead) {
                val next = task.next

                task.next = taskQueueHead
                taskQueueHead!!.pre = task

                taskQueueHead!!.next = next
                if (next != null) next.pre = taskQueueHead

                taskQueueHead = task
                taskQueueHead!!.pre = null
            } else {
                val pre = task.pre
                val pre1 = pre!!.pre
                val next = task.next

                pre1!!.next = task
                task.pre = pre1

                pre.next = next
                if (next != null) next.pre = pre

                task.next = pre
                pre.pre = task
            }

            if (task === taskQueueLast) {
                taskQueueLast = task.next
            }

            queueUpdated()
        }

        fun downTask(task: BuildTask) {
            if (task.next == null) return

            if (task.next === taskQueueLast) {
                val pre = task.pre

                task.pre = taskQueueLast
                taskQueueLast!!.next = task

                if (pre != null) pre.next = taskQueueLast
                taskQueueLast!!.pre = pre

                taskQueueLast = task
                taskQueueLast!!.next = null
            } else {
                val pre = task.pre
                val next = task.next
                val next1 = next!!.next

                task.next = next1
                next1!!.pre = task

                if (pre != null) pre.next = next
                next.pre = pre

                next.next = task
                task.pre = next
            }

            if (task === taskQueueHead) {
                taskQueueHead = task.pre
            }

            queueUpdated()
        }

        fun removeTask(task: BuildTask) {
            if (taskQueueHead === task) {
                taskQueueHead = taskQueueHead!!.next
                if (taskQueueHead != null) {
                    taskQueueHead!!.pre = null
                } else taskQueueLast = null
            } else if (taskQueueLast === task) {
                taskQueueLast = taskQueueLast!!.pre
                if (taskQueueLast != null) {
                    taskQueueLast!!.next = null
                } else taskQueueHead = null
            } else {
                if (task.pre != null) task.pre!!.next = task.next
                if (task.next != null) task.next!!.pre = task.pre
            }

            taskCount--
            Pools.free(task)
            queueUpdated()
        }

        fun pushTask(type: UnitType?, amount: Int, factoryIndex: Int) {
            pushTask(make(type, amount, factoryIndex))
        }

        fun pushTask(task: BuildTask) {
            if (taskQueueHead == null) {
                taskQueueLast = task
                taskQueueHead = taskQueueLast
            } else {
                task.next = taskQueueHead
                taskQueueHead!!.pre = task

                taskQueueHead = task
                task.pre = null
            }

            taskCount++

            queueUpdated()
        }

        fun appendTask(type: UnitType?, amount: Int, factoryIndex: Int) {
            appendTask(make(type, amount, factoryIndex))
        }

        fun appendTask(task: BuildTask) {
            if (taskQueueLast == null) {
                taskQueueLast = task
                taskQueueHead = taskQueueLast
            } else {
                task.pre = taskQueueLast
                taskQueueLast!!.next = task

                taskQueueLast = task
                task.next = null
            }

            taskCount++

            queueUpdated()
        }

        override fun getPayloads(): PayloadSeq? {
            PayloadBuildComp.temp.clear()
            for (payload in payloads()!!.iterate()) {
                PayloadBuildComp.temp.add(payload.content())
            }
            return PayloadBuildComp.temp
        }

        override fun priority(priority: Int) {
            this.priority = priority
            distributor!!.network.priorityModified(this)
        }

        override fun create(block: Block?, team: Team?): NormalCrafterBuild? {
            super.create(block, team)
            distributor = DistributeModule(this)
            distributor!!.setNet()

            itemsBuffer = ItemsBuffer()
            itemsBuffer!!.capacity = Int.Companion.MAX_VALUE
            items = itemsBuffer!!.generateBindModule()
            return this
        }

        override fun networkValided() {
            if (pullItemsRequest != null) pullItemsRequest!!.kill()

            pullItemsRequest = PullMaterialRequest()
            pullItemsRequest!!.init(distributor!!.network)

            distributor!!.assign(pullItemsRequest)
        }

        override fun getCommandPosition(): Vec2? {
            if (currentTask == null) return null
            return currentTask!!.targetPos
        }

        override fun onCommand(target: Vec2?) {
            for (task in taskQueueHead!!) {
                task!!.targetPos = target
            }
        }

        public override fun buildConfiguration(table: Table) {
            table.button(Icon.settings, Styles.cleari, 40f, Runnable { Sgl.ui.unitFactoryCfg.show(this) }).size(56f)
        }

        public override fun updateTile() {
            super.updateTile()

            if (pullItemsRequest != null) pullItemsRequest!!.update()

            if (currentTask == null) {
                recipeCurrent = -1
                return
            }

            if (buildCount >= currentTask!!.queueAmount || (skipBlockedTask && !Units.canCreate(team, currentTask!!.buildUnit))) {
                val task = popTask()

                if (queueMode) {
                    appendTask(task)
                } else {
                    Pools.free(task)
                }
            } else recipeCurrent = currentTask!!.factoryIndex
        }

        protected fun queueUpdated() {
            if (taskQueueHead !== currentTask) {
                buildCount = 0
                progress=0f
                currentTask = taskQueueHead
            }
        }

        override fun shouldConsume(): Boolean {
            return super.shouldConsume() && activity && currentTask != null && buildCount < currentTask!!.queueAmount
        }

        override fun craftTrigger() {
            super.craftTrigger()
            buildCount++

            if (currentTask == null || getPayloads()!!.isEmpty()) return
            val unit = (getPayload() as UnitPayload).unit
            if (unit.isCommandable()) {
                if (currentTask!!.targetPos != null) {
                    unit.command().commandPosition(currentTask!!.targetPos)
                }
                if (currentTask!!.command != null) unit.command().command(currentTask!!.command)
            }
        }

        override fun handleOutputPayload(): Float {
            return super.handleOutputPayload().also { lastOutputProgress = it }
        }

        override fun updateFactory() {
            if (recipeCurrent == -1 || producer!!.current == null) {
                warmup=(Mathf.lerpDelta(warmup(), 0f, stopSpeed))
                return
            }

            if (shouldConsume() && consumeValid()) {
                progress=(progress + progressIncrease(consumer!!.current!!.craftTime))
                warmup=(Mathf.lerpDelta(warmup(), 1f, warmupSpeed))

                onCraftingUpdate()
            } else if (!outputLocking() || lastOutputProgress >= 0.999f) {
                warmup=(Mathf.lerpDelta(warmup(), 0f, stopSpeed))
            }

            totalProgress=(totalProgress + consumer!!.consDelta())

            while (progress() >= 1) {
                progress=(progress - 1)
                consumer!!.trigger()
                producer!!.trigger()

                craftTrigger()
            }
        }

        fun statusText(): String? {
            if (!activity) return Core.bundle.get("infos.waiting")
            else if (currentTask == null) return Core.bundle.get("infos.noTask")
            else if (outputting() != null) {
                if (lastOutputProgress >= 0.999f) return Core.bundle.get("infos.cannotDump")
                else return "..."
            } else if (!consumeValid()) return Core.bundle.get("infos.leakMaterial")
            else return Core.bundle.get("infos.working")
        }

        override fun distributor(): DistributeModule {
            return distributor!!
        }

        fun getTask(index: Int): BuildTask {
            var index = index
            if (index >= taskCount || index < 0) throw IndexOutOfBoundsException("size: " + taskCount + ", index: " + index)
            var curr: BuildTask?

            if (index < taskCount / 2) {
                curr = taskQueueHead
                while (index > 0) {
                    curr = curr!!.next
                    index--
                }
            } else {
                curr = taskQueueLast
                while (index < taskCount - 1) {
                    curr = curr!!.pre
                    index++
                }
            }

            return curr!!
        }

        fun indexOfTask(task: BuildTask?): Int {
            var id = 0
            var t = taskQueueHead
            while (t != null) {
                if (task === t) return id

                t = t.next
                id++
            }

            return -1
        }

        fun serializeTasks(): String {
            val builder = StringBuilder()

            if (taskQueueHead == null) return "empty"
            for (task in taskQueueHead) {
                if (builder.length > 0) builder.append(Sgl.NL)
                builder.append(task!!.buildUnit!!.name).append(";")
                    .append(task.queueAmount).append(";")
                    .append(if (task.targetPos == null) "none" else task.targetPos!!.x).append(";")
                    .append(if (task.targetPos == null) "none" else task.targetPos!!.y).append(";")
                    .append(if (task.command == null) "none" else task.command!!.name).append(";")
            }

            return builder.toString()
        }

        fun deserializeTask(str: String, append: Boolean) {
            if (str == "empty") return

            if (!append) clearTask()
            var err: StringBuilder? = null
            try {
                BufferedReader(StringReader(str)).use { reader ->
                    var line: String?
                    while ((reader.readLine().also { line = it }) != null) {
                        try {
                            val args: Array<String?> = line!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                            require(!(args.size != 5 && !(args.size == 6 && args[5]!!.trim { it <= ' ' }.isEmpty()))) { "illegal build task args length, must be 5" }
                            val unit = Vars.content.unit(args[0])
                            val amount = args[1]!!.toInt()
                            val index = producers!!.indexOf(Boolf { p: BaseProducers? -> p!!.get<ProducePayload<*>>(ProduceType.payload) != null && p.get<ProducePayload<*>>(ProduceType.payload)!!.payloads[0].item === unit })

                            require(index >= 0) { "invalid task, this factory cannot product unit " + unit.localizedName }
                            val task = make(unit, amount, index)

                            if (args[2] != "none") {
                                task.targetPos = Vec2(args[2]!!.toFloat(), args[3]!!.toFloat())
                            }

                            if (args[4] != "none") {
                                // task.command = UnitCommand.all.find(c -> c.name.equals(args[4]));
                            }

                            appendTask(task)
                        } catch (e: Throwable) {
                            if (err == null) err = StringBuilder()
                            err.append(e).append(": ").append(e.getLocalizedMessage()).append(Sgl.NL)
                        }
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            if (err != null) Vars.ui.showErrorMessage(err.toString())
        }

        override fun acceptItem(source: Building, item: Item?): Boolean {
            return (!matrixDistributeOnly || source === distributor!!.network.core!!) && super.acceptItem(source, item)
        }

        override fun write(write: Writes) {
            super.write(write)

            write.bool(activity)
            write.bool(queueMode)
            write.bool(skipBlockedTask)
            write.i(buildCount)
            write.i(priority)

            write.str(serializeTasks())
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)

            activity = read.bool()
            queueMode = read.bool()
            skipBlockedTask = read.bool()
            buildCount = read.i()
            priority = read.i()
            val tmp1 = buildCount
            val tmp2 = progress
            deserializeTask(read.str(), false)
            buildCount = tmp1
            progress=tmp2
        }

        inner class PullMaterialRequest : DistRequestBase(this@SglUnitFactoryBuild) {
            var requests: ObjectSet<Item?> = ObjectSet<Item?>()
            var source: ItemsBuffer? = null

            public override fun init(target: DistributeNetwork) {
                super.init(target)
                source = target.core!!.getBuffer(DistBufferType.itemBuffer)
            }

            override fun preHandleTask(): Boolean {
                requests.clear()
                if (currentTask != null) {
                    tas@ for (stack in consumers.get(currentTask!!.factoryIndex).get<ConsumeItemBase<*>>(ConsumeType.item)!!.consItems!!) {
                        if (requests.add(stack.item) && items.get(stack.item) < stack.amount * itemCapacityMulti) {
                            for (grid in target!!.grids) {
                                for (entry in grid!!.get<Building?>(
                                    GridChildType.container,
                                    Boolf2 { e: Building?, c: TargetConfigure? ->
                                        e!!.block.hasItems && e.items != null && e.items.has(stack.item)
                                                && c!!.get(GridChildType.container, stack.item)
                                    })) {
                                    entry.entity.removeStack(stack.item, 1)
                                    source!!.put(stack.item, 1)
                                    source!!.dePutFlow(stack.item, 1)

                                    continue@tas
                                }
                            }
                        }
                    }
                }

                return true
            }

            override fun handleTask(): Boolean {
                var tst = false
                var allFull = true

                for (item in Vars.content.items()) {
                    if (requests.contains(item)) {
                        if (acceptItem(target!!.core!!.building, item)) {
                            if (source!!.get(item) >= 1) {
                                source!!.remove(item, 1)
                                items.add(item, 1)
                                tst = true
                            }

                            allFull = false
                        }
                    } else if (items.has(item)) {
                        items.remove(item, 1)
                        source!!.put(item, 1)
                    }
                }

                return tst || allFull
            }

            override fun afterHandleTask(): Boolean {
                return true
            }
        }


    }
    class BuildTask : Poolable, Iterable<BuildTask> {
        var buildUnit: UnitType? = null
        var factoryIndex: Int = 0
        var targetPos: Vec2? = null
        var command: UnitCommand? = UnitCommand.moveCommand
        var queueAmount: Int = 0
        var pre: BuildTask? = null
        var next: BuildTask? = null

        override fun reset() {
            buildUnit = null
            factoryIndex = -1
            targetPos = null
            command = UnitCommand.moveCommand
            queueAmount = 0

            pre = null
            next = null
        }

        override fun iterator(): Iterator<BuildTask> {
            return object : Iterator<BuildTask> {
                var curr: BuildTask? = this@BuildTask

                override fun hasNext(): Boolean {
                    return curr != null
                }

                override fun next(): BuildTask {
                    val res = curr
                    curr = curr!!.next
                    return res!!
                }
            }
        }


    }

}