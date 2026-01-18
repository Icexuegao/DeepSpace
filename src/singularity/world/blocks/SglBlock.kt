package singularity.world.blocks

import arc.Core
import arc.func.Cons
import arc.func.Cons2
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.*
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Align
import arc.util.Eachable
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Iconc
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.MultiPacker
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.ui.Fonts
import mindustry.ui.fragments.PlacementFragment
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.*
import singularity.Sgl
import singularity.contents.SglUnits
import singularity.contents.SglUnits.Companion.controlTime
import singularity.graphic.PostAtlasGenerator
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.components.NuclearEnergyBuildComp
import singularity.world.consumers.SglConsumeType
import singularity.world.consumers.SglConsumers
import singularity.world.draw.DrawAtlasGenerator
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import singularity.world.modules.NuclearEnergyModule
import singularity.world.modules.SglConsumeModule
import singularity.world.modules.SglLiquidModule
import singularity.world.particles.SglParticleModels
import singularity.world.unit.SglUnitEntity
import universecore.components.blockcomp.ConsumerBlockComp
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.components.blockcomp.FactoryBlockComp
import universecore.components.blockcomp.Takeable
import universecore.util.DataPackable
import universecore.util.handler.FieldHandler
import universecore.world.blocks.modules.BaseConsumeModule
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsFilter
import universecore.world.consumers.ConsumeType
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.generator.VectorLightningGenerator
import universecore.world.meta.UncStat
import kotlin.math.min

/**此mod的基础方块类型，对block添加了完善的consume系统，并拥有中子能的基础模块 */
open class SglBlock(name: String) : Block(name), ConsumerBlockComp, PostAtlasGenerator {
  var autoSelect: Boolean = false
  var canSelect: Boolean = true
  var outputItems: Boolean = false
  override var consumers = Seq<BaseConsumers>()
  override var optionalCons = Seq<BaseConsumers>()
  var draw: DrawBlock = DrawDefault()

  /**这是一个指针，用于标记当前编辑的consume */
  var consume: SglConsumers? = null

  /**独立的物品栏，为true所有物品具有独立的容量，否则共用物品空间 */
  var independenceInventory: Boolean = true

  /**独立的液体储罐，为true所有液体具有独立的容量，否则共用液体空间 */
  var independenceLiquidTank: Boolean = true

  /**控制在多种可选输入都可用时是全部可用还是其中优先级最高的一种使用 */
  override var oneOfOptionCons: Boolean = true
  override var consFilter = ConsFilter()

  /**方块是否为核能设备 */
  var hasEnergy: Boolean = false
  var initialed: Cons<SglBuilding?>? = null
  var updating: Cons<SglBuilding?>? = null

  /**是否显示液体槽 */
  var displayLiquid: Boolean = true

  /**核能阻值，在运送核能时运输速度会减去这个数值 */
  var resident: Float = 0.1f

  /**方块是否输出核能量 */
  var outputEnergy: Boolean = false

  /**方块是否需求核能量 */
  var consumeEnergy: Boolean = false

  /**基准核势能，当能压小于此值时不接受核能传入 */
  var basicPotentialEnergy: Float = 0f

  /**核能容量。此变量将决定方块的最大核势能 */
  var energyCapacity: Float = 256f

  /**此方块接受的最大势能差，可设为-1将根据容量自动设置 */
  var maxEnergyPressure: Float = -1f

  /**方块是否有过压保护 */
  var energyProtect: Boolean = false
  var liquidsStr: String = Iconc.liquidWater.toString() + Core.bundle.get("fragment.bars.liquids")
  var recipeIndfo: String = Core.bundle.get("fragment.buttons.selectPrescripts")

  init {
    update = true
    consumesPower = false
    appliedConfig()
    config(ByteArray::class.java, Cons2 { e: SglBuilding?, code: ByteArray? ->
      if (code!!.isEmpty()) return@Cons2
      parseConfigObjects(e, DataPackable.readObject(code, e))
    })
    buildType = Prov(::SglBuilding)
  }

  open fun appliedConfig() {
    config(Int::class.javaObjectType) { e: SglBuilding?, i: Int? ->
      if (consumers.size > 1) {
        if (canSelect) e!!.recipeSelected = true
        e!!.reset()
        if (e.recipeCurrent == i || i == -1) {
          e.recipeCurrent = -1
          e.recipeSelected = false
        } else e.recipeCurrent = i!!
      }
    }
    configClear { e: SglBuilding? ->
      e!!.recipeSelected = false
      e.recipeCurrent = 0
    }
  }

  open fun parseConfigObjects(e: SglBuilding?, obj: Any?) {}

  override fun newConsume(): BaseConsumers? {
    consume = SglConsumers(false)
    this.consumers.add(consume)

    return consume
  }

  override fun <T : ConsumerBuildComp> newOptionalConsume(validDef: Cons2<T, BaseConsumers>, displayDef: Cons2<Stats, BaseConsumers>): BaseConsumers? {
    consume = object : SglConsumers(true) {
      init {
        this.optionalDef = validDef as Cons2<ConsumerBuildComp, BaseConsumers>
        this.display = displayDef
      }
    }
    this.optionalCons.add(consume)
    return consume!!
  }

  override fun init() {
    if (consumers.size > 1) {
      configurable = true
      saveConfig = true
    }
    val consume = Seq<BaseConsumers>()
    if (consumers.size > 0) consume.addAll(consumers)
    if (optionalCons.size > 0) consume.addAll(optionalCons)
    for (cons in consume) {
      if (cons.get(ConsumeType.item) != null) {
        hasItems = true
        if (cons.craftTime == 0f) cons.time(90f)
      }
      hasLiquids = hasLiquids or (cons.get(ConsumeType.liquid) != null)
      consumesPower = consumesPower or (cons.get(ConsumeType.power) != null)
      hasPower = hasPower or consumesPower
      consumeEnergy = consumeEnergy or (cons.get(SglConsumeType.energy) != null)
      hasEnergy = hasEnergy or consumeEnergy
    }

    if (hasEnergy && maxEnergyPressure == -1f) {
      maxEnergyPressure = if (energyProtect) Float.MAX_VALUE else energyCapacity * 4
    }

    for (consumer in consumers) {
      acceptsPayload = acceptsPayload or (consumer.get(ConsumeType.payload) != null)
    }
    super.init()

    initPower()
    initFilter()
  }

  override fun load() {
    super.load()
    draw.load(this)
  }

  override fun setStats() {
    stats.add(Stat.size, "@x@", size, size)
    stats.add(Stat.health, health.toFloat(), StatUnit.none)
    if (canBeBuilt()) {
      stats.add(Stat.buildTime, buildTime / 60, StatUnit.seconds)
      stats.add(Stat.buildCost, StatValues.items(false, *requirements))
    }

    if (instantTransfer) {
      stats.add(Stat.maxConsecutive, 2f, StatUnit.none)
    }

    if (this.hasLiquids) this.stats.add(Stat.liquidCapacity, this.liquidCapacity, StatUnit.liquidUnits)

    if (this.hasItems && this.itemCapacity > 0) this.stats.add(Stat.itemCapacity, this.itemCapacity.toFloat(), StatUnit.items)

    if (hasEnergy) {
      stats.add(SglStat.energyCapacity, energyCapacity, SglStatUnit.neutronFlux)
      stats.add(SglStat.energyResident, resident)
      if (basicPotentialEnergy > 0) stats.add(SglStat.basicPotentialEnergy, basicPotentialEnergy, SglStatUnit.neutronPotentialEnergy)
      if (maxEnergyPressure > 0) {
        if (energyProtect) {
          stats.add(SglStat.maxEnergyPressure, Core.bundle.get("misc.infinity"))
        } else stats.add(SglStat.maxEnergyPressure, maxEnergyPressure, SglStatUnit.neutronPotentialEnergy)
      }
    }

    if (!optionalCons.isEmpty) {
      stats.add(UncStat.optionalInputs) { t: Table? ->
        t!!.left().row()
        for (con in optionalCons) {
          t.table(SglDrawConst.grayUIAlpha) { ta: Table? ->
            ta!!.left().defaults().left()
            FactoryBlockComp.buildRecipe(ta, con, null)
          }.growX().fillY().pad(6f).left().margin(10f)
          t.row()
        }
      }
    }
  }

  override fun outputsItems(): Boolean {
    return hasItems && outputItems
  }

  override fun setBars() {
    addBar("health") { entity: Building? -> Bar("stat.health", Pal.health) { entity!!.healthf() }.blink(Color.white) }
  }

  override fun drawPlanRegion(plan: BuildPlan?, list: Eachable<BuildPlan?>?) {
    draw.drawPlan(this, plan, list)
  }

  override fun getRegionsToOutline(out: Seq<TextureRegion?>?) {
    draw.getRegionsToOutline(this, out)
  }

  public override fun icons(): Array<TextureRegion?>? {
    return draw.finalIcons(this)
  }

  override fun postLoad() {
    if (draw is DrawAtlasGenerator) {
      (draw as DrawAtlasGenerator).postLoad(this)
    }
  }

  override fun createIcons(packer: MultiPacker?) {
    super.createIcons(packer)
    if (draw is DrawAtlasGenerator) (draw as DrawAtlasGenerator).generateAtlas(this, packer)
  }

  companion object {
    const val BASE_EXBLOSIVE_ENERGY: Int = 128
    private val temp = Seq<Liquid?>()
    private val fieldHandler = FieldHandler(PlacementFragment::class.java)
  }

  open inner class SglBuilding : Building(), ConsumerBuildComp, NuclearEnergyBuildComp {
    var lightningDrawer: LightningContainer? = null
    var lightnings: LightningContainer? = null
    var lightningGenerator: VectorLightningGenerator? = null
    var drawAlphas: FloatArray = floatArrayOf()
    var CONTAINER: LightningContainer? = null
    override lateinit var consumer: BaseConsumeModule
    var energy = NuclearEnergyModule(this)
    var recipeSelected: Boolean = false
    protected val displayLiquids: Seq<LiquidStack> = Seq<LiquidStack>()

    //  @Annotations.FieldKey("consumeCurrent")
    var recipeCurrent: Int = -1
    override var consumeCurrent
      get() = recipeCurrent
      set(value) {}
    var lastRecipe: Int = 0
    var updateRecipe: Boolean = false
    var select: Int = 0
    var activation: Float = 0f
    var activateRecover: Float = 0f
    override fun energyLinked() = Seq<NuclearEnergyBuildComp>()
    override val resident: Float = this@SglBlock.resident

    fun superUpdate() {
      if ((Time.delta.let { this.timeScaleDuration -= it; this.timeScaleDuration }) <= 0.0f || !this.block.canOverdrive) {
        this.timeScale = 1.0f
      }

      if (!Vars.headless && this.block.ambientSound !== Sounds.none && this.shouldAmbientSound()) {
        Vars.control.sound.loop(this.block.ambientSound, this, this.block.ambientSoundVolume * this.ambientVolume())
      }

      this.updateConsumption()
      if (this.enabled || !this.block.noUpdateDisabled) {
        updateTile()
      }
    }

    override fun update() {
      updateEnergy()

      updateRecipe = false
      if (!recipeSelected && autoSelect && consumer.hasConsume() && (consumer.current == null || !consumer.valid())) {
        var f = -1
        for (ignored in consumers) {
          val n = select % consumers.size
          if (consumer.valid(n) && consumers.get(n).selectable.get() == BaseConsumers.Visibility.usable) {
            f = n
            break
          }
          select = (select + 1) % consumers.size
        }

        if (recipeCurrent != f && f >= 0) {
          recipeCurrent = f
        }
      }

      if (lastRecipe != recipeCurrent) updateRecipe = true
      lastRecipe = recipeCurrent

      if (updateRecipe) fieldHandler.setValue(Vars.ui.hudfrag.blockfrag, "wasHovered", false)


      superUpdate()

      if (updating != null) updating!!.get(this)

      activateRecover = Mathf.approachDelta(activateRecover, 0.005f, 0.0001f)
      activation = Mathf.approachDelta(activation, 0f, activateRecover)

      if (Mathf.chanceDelta((0.1f * activation).toDouble())) {
        SglFx.neutronWeaveMicro.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4), SglDrawConst.fexCrystal)
      }

      if (activation >= 0.5f) {
        damageContinuousPierce(maxHealth * min(activation - 0.5f, 0.5f) / 60)

        if (Mathf.chanceDelta((0.1f * Mathf.maxZero(activation - 0.5f)).toDouble())) {
          Angles.randLenVectors(
            System.nanoTime(), 1, 2f, 3.5f
          ) { x: Float, y: Float -> SglParticleModels.floatParticle.create(this.x, this.y, SglDrawConst.fexCrystal, x, y, 2.6f).strength = 0.4f }
        }
      }
      updateConsumer()
    }

    override fun hasEnergy() = hasEnergy
    override fun energyCapacity() = energyCapacity
    override fun outputEnergy() = outputEnergy
    override fun consumeEnergy() = consumeEnergy
    override fun basicPotentialEnergy() = basicPotentialEnergy
    override fun maxEnergyPressure() = maxEnergyPressure
    override fun energy(): NuclearEnergyModule = energy

    override fun create(block: Block?, team: Team?): Building? {
      super.create(block, team)

      liquids = SglLiquidModule()

      if (consumers.size == 1) recipeCurrent = 0

      consumer = SglConsumeModule(this)

      if (hasEnergy) energy = NuclearEnergyModule(this)
      return this
    }

    override fun liquids(): SglLiquidModule? {
      return liquids as SglLiquidModule?
    }

    override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
      super.init(tile, team, shouldAdd, rotation)
      if (initialed != null) {
        initialed!!.get(this)
      }
      return this
    }

    override fun onControlSelect(player: Unit?) {
      super.onControlSelect(player)
      FieldHandler.setValueDefault(Vars.ui.hudfrag.blockfrag, "lastDisplayState", null)
    }

    override fun shouldConsume(): Boolean {
      return super<Building>.shouldConsume() && super<ConsumerBuildComp>.shouldConsume()
    }

    //  @Override
    open fun efficiency(): Float {
      return consEfficiency()
    }

    fun dumpLiquid() {
      liquids.each { l: Liquid?, n: Float -> dumpLiquid(l) }
    }

    override fun displayConsumption(table: Table) {
      val rebuild = Runnable {
        val t = Table()
        consumer.build(t)

        table.clear()
        table.defaults().padTop(3f).padBottom(3f)
        table.left()
        val array = t.getChildren()
        val items = array.begin()
        var i = 0
        val n = array.size
        while (i < n) {
          val child = items[i]

          table.add(child)
          if ((i + 1) % 6 == 0) {
            table.row()
          }
          i++
        }
        array.end()
      }
      rebuild.run()

      if (consumer != null) table.update {
        if (updateRecipe) {
          rebuild.run()
        }
      }
    }

    override fun drawStatus() {
      if (this.block.enableDrawStatus && consumers.size > 0) {
        val multiplier = if (block.size > 1) 1.0f else 0.64f
        val brcx = this.tile.drawx() + (this.block.size * 8).toFloat() / 2.0f - 8 * multiplier / 2
        val brcy = this.tile.drawy() - (this.block.size * 8).toFloat() / 2.0f + 8 * multiplier / 2
        Draw.z(71.0f)
        Draw.color(Pal.gray)
        Fill.square(brcx, brcy, 2.5f * multiplier, 45.0f)
        Draw.color(status()!!.color)
        Fill.square(brcx, brcy, 1.5f * multiplier, 45.0f)
        Draw.color()
      }
    }

    fun drawActivation() {
      if (activation <= 0.001f) return
      val lerp = Interp.pow2.apply(activation)
      Draw.color(SglDrawConst.fexCrystal)
      Draw.alpha(0.6f * lerp)
      Fill.circle(x, y, Mathf.lerp(size / 2f, size.toFloat(), lerp) * Vars.tilesize)
      val layout = GlyphLayout.obtain()
      layout.setText(Fonts.outline, Core.bundle.get("infos.overloadWarn"))
      val w = layout.width * 0.185f
      val h = layout.height * 0.185f

      layout.free()
      Draw.color(Color.darkGray, 0.6f)
      Fill.quad(
        x - w / 2 - 2, y + size * Vars.tilesize / 2f + h + 2, x - w / 2 - 2, y + size * Vars.tilesize / 2f - 2, x + w / 2 + 2, y + size * Vars.tilesize / 2f - 2, x + w / 2 + 2, y + size * Vars.tilesize / 2f + h + 2
      )

      Fonts.outline.draw(Core.bundle.get("infos.overloadWarn"), x, y + size * Vars.tilesize / 2f + h, Color.crimson, 0.185f, false, Align.center)

      Draw.z(Layer.effect)
      Lines.stroke(1.5f * lerp, SglDrawConst.fexCrystal)
      SglDraw.arc(x, y, Mathf.lerp(size / 2f, size.toFloat(), lerp) * Vars.tilesize, 360 * activation, Time.time * 1.5f)
    }

    override fun consumeValid(): Boolean {
      return enabled && super.consumeValid()
    }

    override fun status(): BlockStatus? {
      return if (!enabled) BlockStatus.logicDisable else consumer.status()
    }

    override fun consume() {
      consumer.trigger()
    }

    open fun updateValid(): Boolean {
      return true
    }

    override fun consEfficiency(): Float {
      return if (consumer.hasConsume()) consumer.consEfficiency else 1f
    }

    open fun updateDisplayLiquid() {
      displayLiquids.clear()
      temp.clear()
      if (recipeCurrent >= 0 && consumer.current != null && consumer.current!!.get(ConsumeType.liquid) != null) {
        for (stack in consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!!) {
          temp.add(stack.liquid)
        }
      }
      liquids.each { key: Liquid?, `val`: Float ->
        if (!temp.contains(key) && `val` > 0.1f) displayLiquids.add(LiquidStack(key, `val`))
      }
    }

    open fun reset() {
      if (items != null) items.clear()
      if (liquids != null) liquids.clear()
    }

    override fun config(): Any? {
      return recipeCurrent
    }

    override fun display(table: Table) {
      super.display(table)
      displayEnergy(table)
    }

    override fun displayBars(bars: Table) {
      super.displayBars(bars)
      //显示流体存储量
      if (hasLiquids && displayLiquid) updateDisplayLiquid()
      if (!displayLiquids.isEmpty) {
        bars.top().left().add(liquidsStr).left().padBottom(0f)
        bars.row()
        for (stack in displayLiquids) {
          bars.add(Bar({ stack.liquid.localizedName }, { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color }, { min(liquids.get(stack.liquid) / block.liquidCapacity, 1f) })).growX()
          bars.row()
        }
      }

      if (recipeCurrent == -1 || consumer.current == null) return

      bars.defaults().grow().margin(0f).padTop(3f).padBottom(3f)
      bars.add(Iconc.download.toString() + Core.bundle.get("fragment.bars.consume")).left().padBottom(0f)
      bars.row()

      buildConsumerBars(bars)
    }

    override fun onOverpressure(energyPressure: Float) {
      activateRecover = 0f
      activation = Mathf.clamp(activation + min((energyPressure - maxEnergyPressure) / maxEnergyPressure * 0.01f, 0.008f) * Time.delta)
    }

    override fun onDestroyed() {
      super.onDestroyed()

      if (hasEnergy && getEnergy() >= BASE_EXBLOSIVE_ENERGY) {
        val u = SglUnits.unstable_energy_body!!.create(Sgl.none) as SglUnitEntity
        u.health = 10 * getEnergy()

        u.x = x
        u.y = y

        u.add()

        if (activation >= 0.5f) {
          u.controlTime = 0f
        }
      }
    }

    /**具有输出物品的方块返回可能输出的物品，没有则返回null */
    open fun outputItems(): Seq<Item>? {
      return null
    }

    /**具有输出液体的方块返回可能输出的液体，没有则返回null */
    open fun outputLiquids(): Seq<Liquid>? {
      return null
    }

    fun acceptAll(type: ConsumeType<*>?): Boolean {
      return autoSelect && (!canSelect || !recipeSelected)
    }

    override fun acceptItem(source: Building, item: Item?): Boolean {
      return source.interactable(this.team) && hasItems && ((source === this && consumer.current != null && consumer.current!!.selfAccess(ConsumeType.item, item)) || !(consumer.hasConsume() || consumer.hasOptional()) || consFilter.filter(this, ConsumeType.item, item, acceptAll(ConsumeType.item))) && (if (independenceInventory) items.get(item) else items.total()) < getMaximumAccepted(item)
    }

    override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
      return source.interactable(this.team) && hasLiquids && ((source === this && consumer.current != null && consumer.current!!.selfAccess(ConsumeType.liquid, liquid)) || !(consumer.hasConsume() || consumer.hasOptional()) || consFilter.filter(this, ConsumeType.liquid, liquid, acceptAll(ConsumeType.liquid))) && (if (independenceLiquidTank) liquids.get(liquid) else (liquids as SglLiquidModule).total()) <= getMaximumAccepted(liquid) - 0.0001f
    }

    open fun getMaximumAccepted(liquid: Liquid?): Float {
      return block.liquidCapacity
    }

    override fun draw() {
      draw.draw(this)
      drawStatus()
      drawActivation()
    }

    override fun drawLight() {
      draw.drawLight(this)
    }

    override fun version(): Byte {
      return 3
    }

    override fun write(write: Writes) {
      super.write(write)
      write.i(select)
      write.f(activation)

      write.i(recipeCurrent)
      if (consumer != null) consumer.write(write)
      if (energy != null) energy.write(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      if (revision >= 2) select = read.i()
      if (revision >= 3) activation = read.f()

      recipeCurrent = read.i()
      if (consumer != null) consumer.read(read, revision <= 2)
      if (energy != null) energy.read(read, revision <= 2)
    }

    override var heaps = ObjectMap<String, Takeable.Heaps<*>>()
  }
}
