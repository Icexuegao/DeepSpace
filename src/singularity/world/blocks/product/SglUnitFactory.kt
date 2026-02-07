package singularity.world.blocks.product

import arc.Core
import arc.func.Boolf
import arc.func.Cons2
import arc.func.Prov
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
import arc.struct.Seq
import arc.util.Scaling
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import mindustry.Vars
import mindustry.ai.UnitCommand
import mindustry.entities.Units
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.PayloadStack
import mindustry.type.UnitType
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.blocks.payloads.UnitPayload
import mindustry.world.blocks.units.Reconstructor
import mindustry.world.meta.Stat
import singularity.Sgl
import singularity.graphic.SglDrawConst
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.distnet.DistElementBlockComp
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.consumers.SglConsumers
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.GridChildType
import singularity.world.distribution.MatrixGrid
import singularity.world.distribution.MatrixGrid.BuildingEntry
import singularity.world.distribution.buffers.ItemsBuffer
import singularity.world.distribution.request.DistRequestBase
import singularity.world.meta.SglStat
import singularity.world.modules.DistributeModule
import singularity.world.modules.PayloadModule
import singularity.world.products.Producers
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeItemBase
import universecore.world.consumers.ConsumeType
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader

open class SglUnitFactory(name: String) : PayloadCrafter(name), DistElementBlockComp {
  companion object {
    var costModels: ObjectMap<UnitType, UnitCostModel> = ObjectMap<UnitType, UnitCostModel>()
    fun setCost(unit: UnitType, req: Array<ItemStack>, level: Int, baseTime: Float) {
      costModels.put(unit, object : UnitCostModel() {
        init {
          this.requirements = req
          this.minLevel = level
          this.baseBuildTime = baseTime
        }
      })
    }

    fun make(type: UnitType?, amount: Int, factoryIndex: Int): BuildTask {
      val task = Pools.obtain(BuildTask::class.java) { BuildTask() } as BuildTask
      task.buildUnit = type
      task.factoryIndex = factoryIndex
      task.queueAmount = amount
      task.command = task.buildUnit!!.defaultCommand ?: task.buildUnit!!.commands.first()
      return task
    }
  }

  override var isNetLinker: Boolean = false
  override var topologyUse = 1
  override var matrixEnergyUse = 0.5f
  override var matrixEnergyCapacity: Float = 0f
  var maxTasks: Int = 16
  var sizeLimit: Float = 0f
  var healthLimit: Float = 0f
  var timeMultiplier: Float = 20.0f
  var baseTimeScl: Float = 0.3f
  var machineLevel: Int = 0
  var matrixDistributeOnly: Boolean = false
  var consCustom: Cons2<UnitType?, SglConsumers?>? = null
  var byProduct: Cons2<UnitType?, Producers?>? = null

  init {
    this.autoSelect = false
    this.canSelect = false
    this.hasItems = true
    this.configurable = true
    buildType = Prov(::SglUnitFactoryBuild)
  }

  override fun appliedConfig() {
    this.config(IntSeq::class.java) { u: SglUnitFactoryBuild, i: IntSeq ->
      val handle = i.get(0)
      when (ConfigCmd.entries[handle]) {
        ConfigCmd.CLEAR_TASK -> u.clearTask()
        ConfigCmd.ADD_TASK -> u.appendTask(Vars.content.unit(i.get(1)), i.get(2), i.get(3))
        ConfigCmd.SET_PRIORITY -> u.priority(i.get(1))
        ConfigCmd.QUEUE_MODE -> u.queueMode = i.get(1) > 0
        ConfigCmd.SKIP_BLOCKED -> u.skipBlockedTask = i.get(1) > 0
        ConfigCmd.ACTIVITY -> u.activity = i.get(1) > 0
        ConfigCmd.REMOVE_TASK -> u.removeTask(u.getTask(i.get(1))!!)
        ConfigCmd.RISE_TASK -> u.riseTask(u.getTask(i.get(1))!!)
        ConfigCmd.DOWN_TASK -> u.downTask(u.getTask(i.get(1))!!)
        ConfigCmd.COMMAND -> {
          val t: BuildTask = u.getTask(i.get(1))!!
          t.targetPos = Vec2(i.get(2).toFloat() / 1000.0f, i.get(3).toFloat() / 1000.0f)
          if (Vars.world.buildWorld(t.targetPos!!.x, t.targetPos!!.y) === u) {
            t.targetPos = null
          }
        }

        else -> throw IllegalArgumentException("unknown operate code: $handle")
      }
    }
  }

  override fun setStats() {
    super.setStats()
    this.stats.remove(Stat.itemCapacity)
    this.stats.remove(SglStat.recipes)
    this.stats.remove(SglStat.autoSelect)
    this.stats.remove(SglStat.controllable)
    this.stats.add(SglStat.sizeLimit, this.sizeLimit)
    this.stats.add(SglStat.healthLimit, this.healthLimit)
    this.stats.add(SglStat.buildLevel, this.machineLevel.toFloat())
    this.stats.add(SglStat.recipes) { t: Table? ->
      t!!.left().defaults().left().growX()
      if (this.matrixDistributeOnly) {
        t.row()
        t.add(Core.bundle.get("infos.matrixDistOnly")).color(Color.darkGray)
      }

      val table = Table(SglDrawConst.grayUIAlpha)
      val coll = Collapser(table, true)
      table.left().defaults().left().pad(3.0f).size(185.0f, 60.0f)
      var c = 0

      for (producer in this.producers) {
        if (producer.cons!!.selectable.get() != BaseConsumers.Visibility.hidden) {
          val pd = producer.get(ProduceType.payload)
          if (pd != null && pd.payloads.size == 1) {
            val button: Button = object : Button(Styles.clearNonei) {
              init {
                this.left().defaults().left().padLeft(4.0f)
                this.image(pd.payloads[0].item.fullIcon).scaling(Scaling.fit).size(50.0f)
                this.add(pd.payloads[0].item.localizedName).growX()
                this.clicked { Vars.ui.content.show(pd.payloads[0].item) }
                this.setDisabled { producer.cons!!.selectable.get() == BaseConsumers.Visibility.unusable }
              }

              override fun draw() {
                super.draw()
                if (producer.cons!!.selectable.get() == BaseConsumers.Visibility.unusable) {
                  Draw.color(Pal.darkerGray)
                  Draw.alpha(0.8f * this.parentAlpha)
                  Fill.rect(this.x + this.width / 2.0f, this.y + this.height / 2.0f, this.width, this.height)
                  Draw.color(Color.lightGray, this.parentAlpha)
                  Icon.lock.draw(this.x + this.width / 2.0f, this.y + this.height / 2.0f, 28.0f, 28.0f)
                }
              }
            }
            table.add(button)
            ++c
            if (c % 4 == 0) {
              c = 0
              table.row()
            }
          }
        }
      }

      t.row()
      t.table { inf: Table? ->
        inf!!.left().defaults().left()
        inf.add(Core.bundle.get("infos.buildableList") + ": ")
        inf.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }.update { i: ImageButton? -> i!!.style.imageUp = if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen }.size(8.0f).padLeft(16.0f).expandX()
      }
      t.row()
      t.add(coll)
    }
    this.setDistNetStats(this.stats)
  }

  override fun init() {
    this.setupProducts()
    super.init()
  }

  override fun setBars() {
    super.setBars()
    this.addBar("progress") { entity: Building ->

      Bar({ Core.bundle.format("bar.numprogress", *arrayOf<Any>(Strings.autoFixed(entity.progress() * 100.0f, 1))) }, { Pal.ammo }, { entity.progress() })
    }
  }

  fun setupProducts() {
    for (unit in Vars.content.units()) {
      if (unit.hitSize < this.sizeLimit && unit.health < this.healthLimit) {
        var level = 1
        val costModel = costModels.get(unit)
        val req: Array<ItemStack>
        val buildTime: Float
        if (costModel != null) {
          if (costModel.disabled) {
            continue
          }

          req = costModel.requirements
          buildTime = costModel.baseBuildTime * this.timeMultiplier * (this.baseTimeScl + (1.0f - this.baseTimeScl) * Mathf.sqrt(unit.hitSize / this.sizeLimit * (unit.health / this.healthLimit)))
          level = costModel.minLevel
        } else {
          req = unit.getTotalRequirements()
          buildTime = unit.getBuildTime() * this.timeMultiplier * (this.baseTimeScl + (1.0f - this.baseTimeScl) * Mathf.sqrt(unit.hitSize / this.sizeLimit * (unit.health / this.healthLimit)))

          val tmp: Array<UnitType?> = arrayOf(unit)
          while (tmp[0] != null) {
            val lis = arrayOfNulls<Array<UnitType?>>(1)
            if (Vars.content.blocks().contains(Boolf { b: Block? ->
                if (b is Reconstructor) {
                  if (((b.upgrades.find { u: Array<UnitType?>? -> u!![1] === tmp[0] } as Array<UnitType?>?).also { lis[0] = it }) != null) {
                    return@Boolf true
                  }
                }
                false
              })) {
              tmp[0] = lis[0]!![0]
            } else {
              tmp[0] = null
            }
            ++level
          }
        }

        if (level <= this.machineLevel && req.isNotEmpty()) {
          this.newConsume()
          this.consume!!.items(*req).displayLim = -1
          this.consume!!.time(buildTime)
          this.consume!!.selectable = Prov { if (!unit.supportsEnv(Vars.state.rules.env)) BaseConsumers.Visibility.hidden else (if (unit.unlockedNow()) BaseConsumers.Visibility.usable else BaseConsumers.Visibility.unusable) }
          if (this.consCustom != null) {
            this.consCustom!!.get(unit, this.consume)
          }

          this.newProduce()
          this.produce!!.add(ProducePayload(PayloadStack.with(unit, 1)) { b: SglUnitFactoryBuild, c: Any -> b.payloads.total() <= 0  })
          if (this.byProduct != null) {
            this.byProduct!!.get(unit, this.produce)
          }
        }
      }
    }
  }

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

  open class UnitCostModel {
    lateinit var requirements: Array<ItemStack>
    var baseBuildTime: Float = 0f
    var minLevel: Int = 0
    var disabled: Boolean = false
  }

  open inner class SglUnitFactoryBuild : PayloadCrafterBuild(), DistElementBuildComp {
    override var payloads = PayloadModule()
    override var matrixEnergyBuffered: Float = 0f
    override var netLinked = Seq<DistElementBuildComp>()
    var activity: Boolean = false
    var queueMode: Boolean = false
    var skipBlockedTask: Boolean = false
    private var buildCount = 0
    var taskCount = 0
    private var taskQueueHead: BuildTask? = null
    private var taskQueueLast: BuildTask? = null
    var currentTask: BuildTask? = null
      private set
    protected var lastOutputProgress: Float = 0f
    override var priority: Int = 0
    override var distributor = DistributeModule(this)
    var itemsBuffer: ItemsBuffer? = null
    var pullItemsRequest: PullMaterialRequest? = null

    fun buildCount(): Int {
      return this.buildCount
    }

    override fun totalProgress(): Float {
      return totalProgress
    }

    override fun progress(): Float {
      return progress
    }

    fun popTask(): BuildTask {
      val res = this.taskQueueHead
      this.taskQueueHead = this.taskQueueHead!!.next
      if (this.taskQueueHead != null) {
        this.taskQueueHead!!.pre = null
      } else {
        this.taskQueueLast = null
      }

      --this.taskCount
      this.queueUpdated()
      return res!!
    }

    fun clearTask() {
      this.taskQueueHead = null
      this.taskQueueLast = null
      this.taskCount = 0
      this.queueUpdated()
    }

    fun riseTask(task: BuildTask) {
      if (task.pre != null) {
        if (task.pre === this.taskQueueHead) {
          val next = task.next
          task.next = this.taskQueueHead
          this.taskQueueHead!!.pre = task
          this.taskQueueHead!!.next = next
          if (next != null) {
            next.pre = this.taskQueueHead
          }

          this.taskQueueHead = task
          this.taskQueueHead!!.pre = null
        } else {
          val pre = task.pre
          val pre1 = pre!!.pre
          val next = task.next
          pre1!!.next = task
          task.pre = pre1
          pre.next = next
          if (next != null) {
            next.pre = pre
          }

          task.next = pre
          pre.pre = task
        }

        if (task === this.taskQueueLast) {
          this.taskQueueLast = task.next
        }

        this.queueUpdated()
      }
    }

    fun downTask(task: BuildTask) {
      if (task.next != null) {
        if (task.next === this.taskQueueLast) {
          val pre = task.pre
          task.pre = this.taskQueueLast
          this.taskQueueLast!!.next = task
          if (pre != null) {
            pre.next = this.taskQueueLast
          }

          this.taskQueueLast!!.pre = pre
          this.taskQueueLast = task
          this.taskQueueLast!!.next = null
        } else {
          val pre = task.pre
          val next = task.next
          val next1 = next!!.next
          task.next = next1
          next1!!.pre = task
          if (pre != null) {
            pre.next = next
          }

          next.pre = pre
          next.next = task
          task.pre = next
        }

        if (task === this.taskQueueHead) {
          this.taskQueueHead = task.pre
        }

        this.queueUpdated()
      }
    }

    fun removeTask(task: BuildTask) {
      if (this.taskQueueHead === task) {
        this.taskQueueHead = this.taskQueueHead!!.next
        if (this.taskQueueHead != null) {
          this.taskQueueHead!!.pre = null
        } else {
          this.taskQueueLast = null
        }
      } else if (this.taskQueueLast === task) {
        this.taskQueueLast = this.taskQueueLast!!.pre
        if (this.taskQueueLast != null) {
          this.taskQueueLast!!.next = null
        } else {
          this.taskQueueHead = null
        }
      } else {
        if (task.pre != null) {
          task.pre!!.next = task.next
        }

        if (task.next != null) {
          task.next!!.pre = task.pre
        }
      }

      --this.taskCount
      Pools.free(task)
      this.queueUpdated()
    }

    fun pushTask(type: UnitType?, amount: Int, factoryIndex: Int) {
      this.pushTask(make(type, amount, factoryIndex))
    }

    fun pushTask(task: BuildTask) {
      if (this.taskQueueHead == null) {
        this.taskQueueLast = task
        this.taskQueueHead = this.taskQueueLast
      } else {
        task.next = this.taskQueueHead
        this.taskQueueHead!!.pre = task
        this.taskQueueHead = task
        task.pre = null
      }

      ++this.taskCount
      this.queueUpdated()
    }

    fun appendTask(type: UnitType?, amount: Int, factoryIndex: Int) {
      this.appendTask(make(type, amount, factoryIndex))
    }

    fun appendTask(task: BuildTask) {
      if (this.taskQueueLast == null) {
        this.taskQueueLast = task
        this.taskQueueHead = this.taskQueueLast
      } else {
        task.pre = this.taskQueueLast
        this.taskQueueLast!!.next = task
        this.taskQueueLast = task
        task.next = null
      }

      ++this.taskCount
      this.queueUpdated()
    }

    fun priority(priority: Int) {
      this.priority = priority
      this.distributor.network.priorityModified(this)
    }

    override fun create(block: Block, team: Team): NormalCrafterBuild {
      super.create(block, team)
      this.distributor.setNet()
      this.itemsBuffer = ItemsBuffer()
      this.itemsBuffer!!.capacity = Int.MAX_VALUE
      this.items = this.itemsBuffer!!.generateBindModule()
      return this
    }

    override fun networkValided() {
      if (this.pullItemsRequest != null) {
        this.pullItemsRequest!!.kill()
      }

      this.pullItemsRequest = PullMaterialRequest()
      this.pullItemsRequest!!.init(this.distributor.network)
      this.distributor.assign(this.pullItemsRequest)
    }

    override fun getCommandPosition(): Vec2? {
      return if (this.currentTask == null) null else this.currentTask!!.targetPos
    }

    override fun onCommand(target: Vec2) {
      for (task in this.taskQueueHead!!) {
        task.targetPos = target
      }
    }

    override fun buildConfiguration(table: Table) {
      table.button(Icon.settings, Styles.cleari, 40.0f) { Sgl.ui.unitFactoryCfg.show(this) }.size(56.0f)
    }

    override fun updateTile() {
      super.updateTile()
      if (this.pullItemsRequest != null) {
        this.pullItemsRequest!!.update()
      }

      if (this.currentTask == null) {
        this.recipeCurrent = -1
      } else {
        if (this.buildCount >= this.currentTask!!.queueAmount || this.skipBlockedTask && !Units.canCreate(this.team, this.currentTask!!.buildUnit)) {
          val task = this.popTask()
          if (this.queueMode) {
            this.appendTask(task)
          } else {
            Pools.free(task)
          }
        } else {
          this.recipeCurrent = this.currentTask!!.factoryIndex
        }
      }
    }

    protected fun queueUpdated() {
      if (this.taskQueueHead !== this.currentTask) {
        this.buildCount = 0
        this.progress = (0.0f)
        this.currentTask = this.taskQueueHead
      }
    }

    override fun shouldConsume(): Boolean {
      return super.shouldConsume() && this.activity && this.currentTask != null && this.buildCount < this.currentTask!!.queueAmount
    }

    override fun craftTrigger() {
      super.craftTrigger()
      ++this.buildCount
      if (this.currentTask != null && !this.payloads.isEmpty) {
        val unit = (this.payload as UnitPayload).unit
        if (unit.isCommandable) {
          if (this.currentTask!!.targetPos != null) {
            unit.command().commandPosition(this.currentTask!!.targetPos)
          }

          if (this.currentTask!!.command != null) {
            unit.command().command(this.currentTask!!.command)
          }
        }
      }
    }

    override fun handleOutputPayload(): Float {
      return super.handleOutputPayload().also { this.lastOutputProgress = it }
    }

    override fun updateFactory() {
      if (this.recipeCurrent != -1 && this.producer!!.current != null) {
        if (this.shouldConsume() && this.consumeValid()) {
          this.progress = (this.progress + this.progressIncrease(this.consumer.current!!.craftTime))
          this.warmup = (Mathf.lerpDelta(this.warmup, 1.0f, warmupSpeed))
          this.onCraftingUpdate()
        } else if (!this.outputLocking || this.lastOutputProgress >= 0.999f) {
          this.warmup = (Mathf.lerpDelta(this.warmup, 0.0f, stopSpeed))
        }

        this.totalProgress = (this.totalProgress + this.consumer.consDelta())

        while (this.progress >= 1.0f) {
          this.progress = (this.progress - 1.0f)
          this.consumer.trigger()
          this.producer?.trigger()
          this.craftTrigger()
        }
      } else {
        this.warmup = (Mathf.lerpDelta(this.warmup, 0.0f, stopSpeed))
      }
    }

    fun statusText(): String {
      return if (!this.activity) {
        Core.bundle.get("infos.waiting")
      } else if (this.currentTask == null) {
        Core.bundle.get("infos.noTask")
      } else if (this.outputting != null) {
        if (this.lastOutputProgress >= 0.999f) Core.bundle.get("infos.cannotDump") else "..."
      } else {
        if (!this.consumeValid()) Core.bundle.get("infos.leakMaterial") else Core.bundle.get("infos.working")
      }
    }

    fun distributor(): DistributeModule {
      return this.distributor
    }

    fun getTask(index: Int): BuildTask? {
      var index = index
      if (index < this.taskCount && index >= 0) {
        var curr: BuildTask?
        if (index < this.taskCount / 2) {
          curr = this.taskQueueHead
          while (index > 0) {
            curr = curr!!.next
            --index
          }
        } else {
          curr = this.taskQueueLast
          while (index < this.taskCount - 1) {
            curr = curr!!.pre
            ++index
          }
        }

        return curr
      } else {
        throw IndexOutOfBoundsException("size: " + this.taskCount + ", index: " + index)
      }
    }

    fun indexOfTask(task: BuildTask?): Int {
      var id = 0

      var t = this.taskQueueHead
      while (t != null) {
        if (task === t) {
          return id
        }

        t = t.next
        ++id
      }

      return -1
    }

    fun serializeTasks(): String {
      val builder = StringBuilder()
      if (this.taskQueueHead == null) {
        return "empty"
      } else {
        for (task in this.taskQueueHead) {
          if (builder.isNotEmpty()) {
            builder.append(Sgl.NL)
          }

          builder.append(task.buildUnit!!.name).append(";").append(task.queueAmount).append(";").append(if (task.targetPos == null) "none" else task.targetPos!!.x).append(";").append(if (task.targetPos == null) "none" else task.targetPos!!.y).append(";").append(if (task.command == null) "none" else task.command!!.name).append(";")
        }

        return builder.toString()
      }
    }

    fun deserializeTask(str: String, append: Boolean) {
      if (str != "empty") {
        if (!append) {
          this.clearTask()
        }

        var err: StringBuilder? = null

        try {
          val reader = BufferedReader(StringReader(str))

          var line: String?
          try {
            while ((reader.readLine().also { line = it }) != null) {
              try {
                val args: Array<String?> = line!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                require(!(args.size != 5 && (args.size != 6 || !args[5]!!.trim { it <= ' ' }.isEmpty()))) { "illegal build task args length, must be 5" }

                val unit = Vars.content.unit(args[0])
                val amount = args[1]!!.toInt()
                val index: Int = this@SglUnitFactory.producers.indexOf { p -> p.get(ProduceType.payload) != null && (p.get(ProduceType.payload) as ProducePayload<*>).payloads[0].item === unit }
                require(index >= 0) { "invalid task, this factory cannot product unit " + unit.localizedName }

                val task = make(unit, amount, index)
                if (args[2] != "none") {
                  task.targetPos = Vec2(args[2]!!.toFloat(), args[3]!!.toFloat())
                }

                if (args[4] != "none") {
                }

                this.appendTask(task)
              } catch (e: Throwable) {
                if (err == null) {
                  err = StringBuilder()
                }

                err.append(e).append(": ").append(e.localizedMessage).append(Sgl.NL)
              }
            }
          } catch (var13: Throwable) {
            try {
              reader.close()
            } catch (var11: Throwable) {
              var13.addSuppressed(var11)
            }

            throw var13
          }

          reader.close()
        } catch (e: IOException) {
          throw RuntimeException(e)
        }

        if (err != null) {
          Vars.ui.showErrorMessage(err.toString())
        }
      }
    }

    override fun acceptItem(source: Building, item: Item?): Boolean {
      return (!this@SglUnitFactory.matrixDistributeOnly || source === this.distributor.network.core) && super.acceptItem(source, item)
    }

    override fun write(write: Writes) {
      super.write(write)
      write.bool(this.activity)
      write.bool(this.queueMode)
      write.bool(this.skipBlockedTask)
      write.i(this.buildCount)
      write.i(this.priority)
      write.str(this.serializeTasks())
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.activity = read.bool()
      this.queueMode = read.bool()
      this.skipBlockedTask = read.bool()
      this.buildCount = read.i()
      this.priority = read.i()
      val tmp1 = this.buildCount
      val tmp2 = this.progress
      this.deserializeTask(read.str(), false)
      this.buildCount = tmp1
      this.progress = tmp2
    }

    fun priority(): Int {
      return this.priority
    }

    override fun onProximityAdded() {
      super.onProximityAdded()
      this.distNetAdd()
    }

    override fun onProximityRemoved() {
      super.onProximityRemoved()
      this.distNetRemove()
    }

    inner class PullMaterialRequest : DistRequestBase(this@SglUnitFactoryBuild) {
      var requests = ObjectSet<Item>()
      var source: ItemsBuffer? = null

      override fun init(target: DistributeNetwork) {
        super.init(target)
        this.source = target.core!!.getBuffer(DistBufferType.itemBuffer)
      }

      override fun preHandleTask(): Boolean {
        this.requests.clear()
        if (this@SglUnitFactoryBuild.currentTask != null) {
          for (stack in ((this@SglUnitFactory.consumers.get(this@SglUnitFactoryBuild.currentTask!!.factoryIndex) as BaseConsumers).get<ConsumeItemBase<*>>(ConsumeType.item) as ConsumeItemBase<*>).consItems!!) {
            if (this.requests.add(stack.item) && this@SglUnitFactoryBuild.items.get(stack.item).toFloat() < stack.amount.toFloat() * this@SglUnitFactory.itemCapacityMulti) {
              val var5 = this.target!!.grids.iterator()

              while (var5.hasNext()) {
                val grid = var5.next() as MatrixGrid
                val var7: MutableIterator<*> = grid.get<Any?>(GridChildType.container) { e: Building?, c: TargetConfigure? -> e!!.block.hasItems && e.items != null && e.items.has(stack.item) && c!!.get(GridChildType.container, stack.item) }.iterator()
                if (var7.hasNext()) {
                  val entry = var7.next() as BuildingEntry<Building>
                  entry.entity.removeStack(stack.item, 1)
                  this.source!!.put(stack.item, 1)
                  this.source!!.dePutFlow(stack.item, 1)
                  break
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
          if (this.requests.contains(item)) {
            if (this@SglUnitFactoryBuild.acceptItem(this.target!!.core!!.building, item)) {
              if (this.source!!.get(item) >= 1) {
                this.source!!.remove(item, 1)
                this@SglUnitFactoryBuild.items.add(item, 1)
                tst = true
              }

              allFull = false
            }
          } else if (this@SglUnitFactoryBuild.items.has(item)) {
            this@SglUnitFactoryBuild.items.remove(item, 1)
            this.source!!.put(item, 1)
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
    var command: UnitCommand?
    var queueAmount: Int = 0
    var pre: BuildTask? = null
    var next: BuildTask? = null

    init {
      this.command = UnitCommand.moveCommand
    }

    override fun reset() {
      this.buildUnit = null
      this.factoryIndex = -1
      this.targetPos = null
      this.command = UnitCommand.moveCommand
      this.queueAmount = 0
      this.pre = null
      this.next = null
    }

    override fun iterator(): Iterator<BuildTask> {
      return object : Iterator<BuildTask> {
        var curr: BuildTask? = this@BuildTask

        override fun hasNext(): Boolean {
          return this.curr != null
        }

        override fun next(): BuildTask {
          val res = this.curr
          this.curr = this.curr!!.next
          return res!!
        }
      }
    }
  }
}