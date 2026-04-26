package ice.world.content.blocks.distribution

import arc.func.Cons
import arc.func.Prov
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button
import arc.scene.ui.ButtonGroup
import arc.scene.ui.Image
import arc.scene.ui.ImageButton
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import ice.graphics.IStyles.paneLeft
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.*
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.BaseTurret.BaseTurretBuild
import mindustry.world.blocks.heat.HeatBlock
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawHeatOutput
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.*
import singularity.world.blocks.SglBlock
import singularity.world.blocks.SglBlock.SglBuilding
import singularity.world.blocks.turrets.SglTurret.SglTurretBuild
import singularity.world.modules.NuclearEnergyModule
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeType.Companion.item
import universecore.world.consumers.ConsumeType.Companion.liquid
import universecore.world.consumers.cons.item.ConsumeItemBase
import universecore.world.consumers.cons.liquid.ConsumeLiquidBase
import java.util.*

class DirSource(name: String?) :Block(name) {
  var drawer: DrawBlock = DrawMulti(
    DrawDefault(), DrawHeatOutput()
  )

  init {
    hasPower = true
    outputsPower = true
    consumesPower = false
    conductivePower = true
    rotateDraw = false
    update = true
    solid = true
    canOverdrive = false
    group = BlockGroup.transportation
    schematicPriority = -9
    selectionRows = 5
    selectionColumns = 6
    noUpdateDisabled = true
    envEnabled = Env.any
    placeableLiquid = true
    alwaysUnlocked = true
    rotate = true
    regionRotated1 = 1
    configurable = true
    saveConfig = true
    clearOnDoubleTap = true
    quickRotate = false
    requirements(Category.distribution, BuildVisibility.sandboxOnly, ItemStack.with())
    config(IntArray::class.java) { tile: DirSourceBuild, index: IntArray ->
      tile.index[0] = index[0]
      tile.index[1] = index[1]
      tile.target?.items?.clear()
      tile.target?.liquids?.clear()
    }
    buildType = Prov(::DirSourceBuild)
  }

  override fun setStats() {
    super.setStats()
    stats.add(Stat("config", StatCat.function)) { table: Table ->
      table.row()
      table.table(Styles.grayPanel) { frame: Table ->
        val image = Image(region)
        frame.add(image).tooltip(localizedName, true).size(40f).pad(12f).left().center()
        frame.table { label ->
          label.label { localizedName }.color(Pal.stat).growX().left().row()
          label.label { "May we love each other in the future\njust like we do now." }.growX().left().row()
        }.growX().pad(12f).padLeft(0f).row()
      }.growX().pad(5f).row()
    }
  }

  override fun load() {
    super.load()
    drawer.load(this)
  }

  override fun drawPlanRegion(plan: BuildPlan?, list: Eachable<BuildPlan?>?) {
    drawer.drawPlan(this, plan, list)
  }

  inner class DirSourceBuild :Building(), HeatBlock {
    var target: Building? = null
    var consumes: Seq<Seq<UnlockableContent>> = Seq<Seq<UnlockableContent>>()
    var optionalCons: Seq<Seq<UnlockableContent>> = Seq<Seq<UnlockableContent>>()
    var index: IntArray = intArrayOf(-1, -1)
    private val uiSize = 40f

    var cons: Int
      get() = index[0]
      set(v) {
        index[0] = v
      }

    var opCons: Int
      get() = index[1]
      set(v) {
        index[1] = v
      }

    override fun drawSelect() {
      if (target != null) drawItemSelection(target!!.block)
    }

    override fun draw() {
      drawer.draw(this)
    }

    private fun baseConsumersUnpack(container: Seq<Seq<UnlockableContent>>, baseConsumers: Seq<BaseConsumers>) {
      for(consumers in baseConsumers) {
        val row = Seq<UnlockableContent>()
        val consumeItemBase: ConsumeItemBase<*>? = consumers.get(item)
        if (consumeItemBase != null) for(v in Objects.requireNonNull<Array<ItemStack>>(consumeItemBase.consItems)) row.add(v.item)
        val consumeLiquidBase: ConsumeLiquidBase<*>? = consumers.get(liquid)
        if (consumeLiquidBase != null) for(v in Objects.requireNonNull<Array<LiquidStack>>(consumeLiquidBase.consLiquids)) row.add(v.liquid)
        if (row.any()) container.add(row)
      }
    }

    private fun configTable(table: Table, container: Seq<Seq<UnlockableContent>>, holder: Prov<Int?>, consumer: Cons<Int?>) {
      table.table { t ->
        t!!.left().defaults().size(uiSize)
        val group = ButtonGroup<ImageButton?>()
        group.setMinCheckCount(0)
        for(i in 0..<container.size) {
          val v = container.get(i).get(0)
          val button =
            t.button(Tex.whiteui, Styles.clearNoneTogglei, 32f) {}.tooltip(v.localizedName, true).group<ImageButton?>(group).get()
          button.changed {
            consumer.get(if (button.isChecked()) i else -1)
            configure(index)
          }
          button.style.imageUp = TextureRegionDrawable(v.uiIcon)
          button.update { button.setChecked(holder.get() == i) }
        }
      }.growX().left().row()
    }

    override fun onProximityUpdate() {
      target = front()
      consumes = Seq<Seq<UnlockableContent>>()
      optionalCons = Seq<Seq<UnlockableContent>>()
      if (target is SglBuilding) {
        val sglBlock = target!!.block as SglBlock
        baseConsumersUnpack(consumes, sglBlock.consumers)
        if (target is SglTurretBuild) {
          val baseConsumers = sglBlock.optionalCons
          if (baseConsumers.size > 0){
            for(v in Objects.requireNonNull<Array<LiquidStack>>(
              Objects.requireNonNull(
                baseConsumers.get(0).get(
                  liquid
                )
              )!!.consLiquids
            )) {
              optionalCons.add(Seq.with(v.liquid))
            }
          }
        } else baseConsumersUnpack(optionalCons, sglBlock.optionalCons)
      } else if (target != null) {
        for(v in Vars.content.items()) if (target!!.block.consumesItem(v)) consumes.add(Seq.with(v))
        for(v in Vars.content.liquids()) if (target!!.block.consumesLiquid(v)) optionalCons.add(Seq.with(v))
      } else {
        this.cons = -1
        this.opCons = -1
      }
    }

    private fun consUpdate(container: Seq<Seq<UnlockableContent>>, holder: Prov<Int?>) {
      if (holder.get()!! >= 0 && holder.get()!! < container.size) for(v in container.get(holder.get()!!)) if (v is Item && target!!.acceptItem(
          this, v
        )
      ) target!!.handleItem(this, v)
      else if (v is Liquid && target!!.acceptLiquid(this, v)) target!!.handleLiquid(
        this, v, target!!.block.liquidCapacity - target!!.liquids.get(v)
      )
    }

    override fun updateTile() {
      if (target is SglBuilding) {
        val sglBlock = target!!.block as SglBlock
        val energy: NuclearEnergyModule = (target as SglBuilding).energyModule
        energy.handle(sglBlock.energyCapacity - energy.energy)
      } else if (target != null && target !is BaseTurretBuild) {
        // 两个大类共用容器还分别采用不同的方法, 这是极难维护的...有机会我会回来优化的, 至少需要一个新的思路.
        for(row in consumes) for(v in row) if (v is Item && target!!.acceptItem(this, v)) target!!.handleItem(this, v)
        else if (v is Liquid && target!!.acceptLiquid(this, v)) target!!.handleLiquid(
          this, v, target!!.block.liquidCapacity - target!!.liquids.get(v)
        )
        for(row in optionalCons) for(v in row) if (v is Item && target!!.acceptItem(this, v)) target!!.handleItem(this, v)
        else if (v is Liquid && target!!.acceptLiquid(this, v)) target!!.handleLiquid(
          this, v, target!!.block.liquidCapacity - target!!.liquids.get(v)
        )
      }
      if (target != null) {
        consUpdate(consumes) { this.cons }
        consUpdate(optionalCons) { this.opCons }
      }
    }

    override fun buildConfiguration(table: Table) {
      table.table { t -> t!!.background(Tex.paneLeft).image(Icon.download).size(uiSize).tooltip("tip", true).center() }.size(uiSize).top()
      table.table { frame ->
        frame.background(paneLeft)
        if (consumes.any() || optionalCons.any()) {
          if (target is SglBuilding) {
            if (consumes.any()) {
              val group = ButtonGroup<Button>()
              group.setMinCheckCount(0)
              for(i in 0..<consumes.size) {
                val button = frame.button({ t: Button ->
                  t.left()
                  for(item in consumes.get(i)) t.image(item.uiIcon).maxSize(32f).tooltip(item.localizedName, true)
                }, Styles.clearNoneTogglei, {}).minSize(uiSize).group(group).growX().get()
                button.changed {
                  this.cons = if (button.isChecked()) i else -1
                  configure(intArrayOf(this.cons, this.opCons))
                }
                button.update { button.setChecked(this.cons == i) }
                frame.row()
              }
            }
            if (optionalCons.any()) {
              configTable(frame, optionalCons, { this.opCons }, { v: Int? -> this.opCons = v!! })
              //                            for (var row : optionalCons)
//                                for (var v : row)
//                                    frame.image(v.uiIcon).maxSize(32f).tooltip(v.localizedName, true);
            }
          } else if (target is BaseTurretBuild) {
            configTable(frame, consumes, { this.cons }, { v: Int? -> this.cons = v!! })
            configTable(frame, optionalCons, { this.opCons }, { v: Int? -> this.opCons = v!! })
          } else if (target != null) {
            if (consumes.any()) for(row in consumes) for(v in row) frame.image(v.uiIcon).maxSize(32f).tooltip(v.localizedName, true)
            if (optionalCons.any()) for(row in optionalCons) for(v in row) frame.image(v.uiIcon).maxSize(32f).tooltip(v.localizedName, true)
          }
        } else frame.image(Icon.cancel).size(uiSize).center().row()
      }.left().row()
    }

    override fun acceptItem(source: Building?, item: Item?): Boolean {
      return false
    }

    override fun handleItem(source: Building?, item: Item?) {}

    override fun acceptLiquid(source: Building?, liquid: Liquid?): Boolean {
      return false
    }

    override fun handleLiquid(source: Building?, liquid: Liquid?, amount: Float) {}

    override fun getPowerProduction(): Float {
      return 1000000f / 60f
    }

    override fun heat(): Float {
      return 1000f
    }

    override fun heatFrac(): Float {
      return 1f
    }

    override fun config(): Any {
      return index
    }

    override fun write(write: Writes) {
      super.write(write)
      write.i(index[0])
      write.i(index[1])
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      index[0] = read.i()
      index[1] = read.i()
    }
  }
}