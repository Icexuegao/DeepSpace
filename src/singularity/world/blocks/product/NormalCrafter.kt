package singularity.world.blocks.product

import arc.Core
import arc.audio.Sound
import arc.func.Cons
import arc.func.Cons2
import arc.func.Floatf
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.math.Rand
import arc.scene.event.Touchable
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.Tooltip
import arc.scene.ui.layout.Table
import arc.struct.EnumSet
import arc.struct.ObjectFloatMap
import arc.struct.OrderedMap
import arc.struct.Seq
import arc.util.Scaling
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import ice.core.SettingValue
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.gen.*
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.meta.*
import singularity.graphic.SglDrawConst
import singularity.world.blocks.SglBlock
import singularity.world.meta.SglStat
import singularity.world.products.Producers
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.components.blockcomp.FactoryBlockComp
import universecore.components.blockcomp.FactoryBuildComp
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.blocks.modules.BaseProductModule
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeType
import universecore.world.producers.BaseProduce
import universecore.world.producers.BaseProducers
import universecore.world.producers.ProducePower
import universecore.world.producers.ProduceType
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@Suppress("UNCHECKED_CAST")
/**常规的工厂类方块，具有强大的consume-produce制造系统的近乎全能的制造类方块 */
open class NormalCrafter(name: String) : SglBlock(name), FactoryBlockComp {
  companion object {
    private fun buildRecipeSimple(cons: BaseConsumers, prod: BaseProducers, ta: Table) {
      var first = true
      for (consume in cons.all()) {
        if (!consume.hasIcons()) continue

        if (!first) ta.add("+").fillX().pad(4f)
        ta.table { c: Table? ->
          c!!.defaults().padLeft(3f).fill()
          consume.buildIcons(c)
        }.fill()

        first = false
      }

      ta.image(Icon.right).padLeft(8f).padRight(8f).size(30f)

      first = true
      for (produce in prod.all()) {
        if (!produce.hasIcons()) continue

        if (!first) ta.add("+").fillX().pad(4f)
        ta.table { c ->
          c.defaults().padLeft(3f).fill()
          produce.buildIcons(c)
        }.fill()

        first = false
      }
    }
  }

  protected var rand: Rand = Rand()
  var updateEffectChance: Float = 0.04f
  var updateEffect: Effect = Fx.none
  var updateEffectColor: Color = Color.white
  var craftEffect: Effect = Fx.none
  var craftEffectColor: Color = Color.white
  var effectRange: Float = -1f
  var craftedSound: Sound = Sounds.none
  var craftedSoundVolume: Float = 0.5f
  var shouldConfig: Boolean = false

  /**同样的，这也是一个指针，指向当前编辑的produce */
  var produce: Producers? = null
  var craftTrigger: Cons<out NormalCrafterBuild?>? = null
  var crafting: Cons<out NormalCrafterBuild?>? = null
  override var warmupSpeed = 0.02f
  override var stopSpeed = 0.02f
  var byproducts = OrderedMap<BaseConsumers?, Byproduct>()
  var optionalProducts = OrderedMap<BaseConsumers, BaseProducers>()
  var boosts = ObjectFloatMap<BaseConsumers?>()
  override var producers = Seq<BaseProducers>()

  init {
    update = true
    solid = true
    sync = true
    ambientSoundVolume = 0.03f
    flags = EnumSet.of(BlockFlag.factory)
    buildType = Prov(::NormalCrafterBuild)
  }

  override fun newProduce(): Producers {
    produce = Producers()
    producers.add(produce)
    return produce!!
  }

  fun newOptionalProduct() {
    produce = Producers()
    val prod: Producers = produce!!
    newOptionalConsume({ e: NormalCrafterBuild, c: BaseConsumers ->
      for (baseProduce in prod.all()) {
        val baseProduce1 = baseProduce as BaseProduce<ProducerBuildComp>
        if (baseProduce1.valid(e)) baseProduce.update(e)
        baseProduce.dump(e)
      }
    }, { s: Stats?, c: BaseConsumers? ->
      prod.display(s!!)
    })
    prod.cons = consume
    consume!!.setConsTrigger { e: NormalCrafterBuild ->
      for (baseProduce in prod.all()) {
        val baseProduce1 = baseProduce as BaseProduce<ProducerBuildComp>
        if (baseProduce1.valid(e)) baseProduce.produce(e)
      }
    }

    optionalProducts.put(consume, prod)
  }

  class Byproduct(var item: Item?, var chance: Float, var base: Int)

  /**对当前生产清单设置随机副产物
   *
   * @param chance 副产物产出的机会，为任意大于0的值，实际机会计算值从0到chance之间随机取值，结果将超出1的部分直接作为产出数量，与1取模后剩余机会取[Mathf.chance]
   * @param item 副产物物品
   */
  fun setByProduct(chance: Float, item: Item) {
    setByProduct(0, chance, item)
  }

  /**对当前生产清单设置随机副产物
   *
   * @param base 最小产出量，机会的计算结果会直接加上这个数值
   * @param chance 副产物产出的机会，为任意大于0的值，实际机会计算值从0到chance之间随机取值，结果将超出1的部分直接作为产出数量，与1取模后剩余机会取[Mathf.chance]
   * @param item 副产物物品
   */
  fun setByProduct(base: Int, chance: Float, item: Item) {
    byproducts.put(consume, Byproduct(item, chance, base))

    consume!!.addSelfAccess(ConsumeType.item, item)
    consume!!.setConsTrigger { e: NormalCrafterBuild ->
      var chanceV = chance + base
      while (chanceV >= 1) {
        if (e.acceptItem(e, item)) e.offload(item)
        chanceV--
      }
      if (rand.chance(chanceV.toDouble())) if (e.acceptItem(e, item)) e.offload(item)
    }
    val old: Cons2<Stats, BaseConsumers> = consume!!.display
    consume!!.display = Cons2 { s: Stats?, c: BaseConsumers? ->
      s!!.add(Stat.output) { t: Table? ->
        old.get(s, c)
        t!!.row()
        t.table { i: Table? ->
          i!!.add(Core.bundle.get("misc.extra") + ":")
          i.add(StatValues.displayItem(item, base)).left().padLeft(6f)
          i.add("[gray]" + (if (base > 0) " +" else "") + Strings.autoFixed(chance * 100, 2) + "%[]")
        }.left().padLeft(5f)
      }
    }
  }

  fun newBooster(boost: Float): BaseConsumers? {
    val ada: Array<Floatf<NormalCrafterBuild>> = arrayOfNulls<Floatf<*>>(1) as Array<Floatf<NormalCrafterBuild>>
    val res = newOptionalConsume({ e: NormalCrafterBuild, c: BaseConsumers ->
      e.currBoost = ada[0]
      e.mark = 2
    }, { s: Stats, c: BaseConsumers ->
      s.add(Stat.boostEffect) { t: Table? ->
        t!!.row()
        t.table { cons: Table? ->
          cons!!.table { req: Table? ->
            req!!.left().defaults().left().padLeft(3f)
            val stats = Stats()
            for (co in c.all()) {
              co.display(stats)
            }
            FactoryBlockComp.buildStatTable(req, stats)
          }.left().padRight(40f)
          cons.add(Core.bundle.get("misc.efficiency") + Strings.autoFixed(boost * 100, 1) + "%").growX().right()
        }
      }
    })
    boosts.put(res, boost)

    ada[0] = Floatf { e: NormalCrafterBuild ->
      var mul = 1f
      for (cons in res!!.all()) {
        mul *= (cons as BaseConsume<ConsumerBuildComp>).efficiency(e)
      }
      boost * mul * Mathf.clamp(e.consumer.consEfficiency) * e.consumer.getOptionalEff(res)
    }

    consume!!.customDisplayOnly = true
    consume!!.optionalAlwaysValid = false

    return res
  }

  override fun setBars() {
    super.setBars()
    addBar("efficiency") { e: NormalCrafterBuild ->
      Bar({ Core.bundle.get("misc.efficiency") + ": " + Mathf.round(e.workEfficiency() * 100) + "%" }, { Pal.accent }, { e.workEfficiency() })
    }
  }

  override fun init() {
    if (effectRange == -1f) effectRange = size.toFloat()

    if (producers.size > 0) for (prod in producers) {
      outputItems = outputItems or (prod.get(ProduceType.item) != null)
      hasItems = hasItems or outputItems
      outputsLiquid = outputsLiquid or (prod.get(ProduceType.liquid) != null)
      hasLiquids = hasLiquids or outputsLiquid
      outputsPower = outputsPower or (prod.get(ProduceType.power) != null && prod.get(ProduceType.power)!!.powerProduction != 0f)
      hasPower = hasPower or outputsPower
      outputEnergy = outputEnergy or (prod.get(ProduceType.energy) != null)
      hasEnergy = hasEnergy or outputEnergy
    }

    for (producer in producers) {
      outputsPayload = outputsPayload or (producer.get(ProduceType.payload) != null)
    }

    for (product in optionalProducts) {
      for (baseProduce in product.value!!.all()) {
        baseProduce.parent!!.cons = product.key
      }
    }

    super.init()

    if (producers.size > 1 && canSelect) configurable = true
    if (shouldConfig) configurable = true
    initProduct()
  }

  override fun setStats() {
    super.setStats()
    if (producers.size > 1) {
      stats.add(SglStat.autoSelect, autoSelect)
      stats.add(SglStat.controllable, canSelect)
    }

    stats.add(SglStat.recipes) { t: Table? ->
      t!!.left().row()
      t.add(Core.bundle.get("infos.touchShowDetails")).color(Color.gray).left()
      t.row()
      for (i in 0..<consumers.size) {
        val cons = consumers.get(i)
        val prod = producers.get(i)
        val details = Table()
        FactoryBlockComp.buildRecipe(details, cons, prod)
        val simple = Table { ta ->
          ta!!.left().defaults().left()
          if (cons.showTime) {
            ta.stack(Table { o ->
              o!!.left()
              o.add(Image(SglDrawConst.time)).size(32f).scaling(Scaling.fit)
            }, Table { o: Table? ->
              o!!.left().bottom()
              o.add(Strings.autoFixed(cons.craftTime / 60, 1) + StatUnit.seconds.localized()).style(Styles.outlineLabel)
              o.pack()
            })
            ta.add(" > ")
          }
          buildRecipeSimple(cons, prod, ta)
        }
        val isSim = AtomicBoolean(false)
        val rebuild = AtomicReference<Runnable?>()

        t.table(SglDrawConst.grayUIAlpha) { ta ->
          rebuild.set(Runnable {
            ta!!.clearChildren()
            ta.left().add(if (isSim.get()) details else simple)
            isSim.set(!isSim.get())
          })
          rebuild.get()!!.run()
          ta!!.touchable = Touchable.enabled
        }.margin(8f).left().growX().fillY().pad(3f).get().clicked {
          rebuild.get()!!.run()
        }
        t.row()
      }
    }
  }

  open inner class NormalCrafterBuild : SglBuilding(), FactoryBuildComp {
    var statusi = 2
    override var progress: Float = 0f
    override var totalProgress = 0f
    override var warmup: Float = 0f
    private val tempLiquid = Seq<Liquid>()

    var outputItems: Seq<Item>? = null
    var outputLiquids: Seq<Liquid>? = null
    override var powerProdEfficiency: Float = 0f
    override var producer: BaseProductModule? = null
    var currBoost: Floatf<NormalCrafterBuild> = Floatf { e: NormalCrafterBuild -> 1f }
    var real: Float = 0f
    var mark: Int = 0
    override var consumeCurrent
      get() = recipeCurrent
      set(value) {}

    override fun draw() {
      super.draw()
      if (SettingValue.启用多合成角标常显&& !Vars.state.isEditor) {
        drawcornerMark()
      }
    }

    override fun drawSelect() {
      super.drawSelect()
      if (!SettingValue.启用多合成角标常显&& !Vars.state.isEditor) {
        drawcornerMark()
      }
    }
    fun drawcornerMark(){
      Draw.z(Layer.block + 1f)
      producer?.current?.get(ProduceType.item)?.items[0]?.let {
        drawItemSelection(it.item)
        Draw.reset()
        return
      }
      producer?.current?.get(ProduceType.liquid)?.liquids[0]?.let {
        drawItemSelection(it.liquid)
      }
      Draw.reset()
    }

    override fun create(block: Block, team: Team): Building {

      val create: Building = super.create(block, team)

      producer = BaseProductModule(this)
      return create
    }

    override fun reset() {
      super.reset()
      progress = 0f
    }

    override fun updateDisplayLiquid() {
      if (!block.hasLiquids) return
      displayLiquids.clear()

      tempLiquid.clear()
      if (recipeCurrent >= 0 && consumer.current != null) {
        if (consumer.current!!.get(ConsumeType.liquid) != null) {
          for (stack in consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!!) {
            tempLiquid.add(stack.liquid)
          }
        }
      }
      if (recipeCurrent >= 0 && producer!!.current != null) {
        if (producer!!.current!!.get(ProduceType.liquid) != null) {
          for (stack in producer!!.current!!.get(ProduceType.liquid)!!.liquids) {
            tempLiquid.add(stack.liquid)
          }
        }
      }
      liquids.each { key: Liquid?, `val`: Float ->
        if (!tempLiquid.contains(key) && `val` > 0.1f) displayLiquids.add(LiquidStack(key, `val`))
      }
    }

    override fun displayBars(bars: Table) {
      super.displayBars(bars)

      if (recipeCurrent == -1 || producer!!.current == null || consumer.current == null) return
      val tab = Table()
      buildProducerBars(tab)

      if (tab.hasChildren()) {
        bars.row()
        bars.add(Iconc.upload.toString() + Core.bundle.get("fragment.bars.product")).left().padBottom(0f)
        val el = tab.getChildren()
        val items = el.begin()
        var i = 0
        val b = el.size
        while (i < b) {
          bars.row()
          bars.add(items[i])
          i++
        }
        el.end()

        bars.row()
        bars.image().color(Color.darkGray).growX().colspan(2).height(4f).padTop(3f).padBottom(3f).padLeft(-14f).padRight(-14f)
      }
    }

    override fun outputItems(): Seq<Item>? {
      if (recipeCurrent == -1) return null
      return outputItems
    }

    override fun outputLiquids(): Seq<Liquid>? {
      if (recipeCurrent == -1) return null
      return outputLiquids
    }

    override fun consEfficiency(): Float {
      val eff: Float = super<FactoryBuildComp>.consEfficiency()

      return real * eff * warmup
    }

    override fun status(): BlockStatus? {
      if (autoSelect && !canSelect && recipeCurrent == -1) return BlockStatus.noInput
      return super.status()
    }

    override fun update() {
      super.update()
      updateConsumer()

      updateProducer()

      updateFactory()
    }

    override fun handleItem(source: Building?, item: Item?) {
      super.handleItem(source, item)
      handleProductItem(this, item)
    }

    override fun getPowerProduction(): Float {
      if (!block!!.outputsPower || producer!!.current == null || producer!!.current!!.get(ProduceType.power) == null) return 0f
      powerProdEfficiency = Mathf.num(shouldConsume() && consumeValid()) * consEfficiency() * ((producer!!.current!!.get(ProduceType.power)) as ProducePower<ProducerBuildComp>).multiple(this)
      return producer!!.powerProduct * powerProdEfficiency
    }

    override fun shouldConsume(): Boolean {
      return super<FactoryBuildComp>.shouldConsume() && productValid()
    }

    override fun warmup() = warmup
    override fun totalProgress() = totalProgress

    override fun updateTile() {
      real = Mathf.lerpDelta(real, currBoost.get(this), 0.05f)

      if (mark > 0 && --mark <= 0) {
        currBoost = Floatf { _: NormalCrafterBuild -> 1f }
      }

      if (updateRecipe && producer!!.current != null) {
        if (producer!!.current!!.get(ProduceType.item) != null) outputItems = Seq(producer!!.current!!.get(ProduceType.item)!!.items).map { e: ItemStack? -> e!!.item }
        if (producer!!.current!!.get(ProduceType.liquid) != null) outputLiquids = Seq(producer!!.current!!.get(ProduceType.liquid)!!.liquids).map { e: LiquidStack? -> e!!.liquid }
      }

      for (byproduct in byproducts.values()) {
        dump(byproduct.item)
      }
    }

    override fun buildConfiguration(table: Table) {
      if (producers.size > 1 && canSelect) {
        table.table(Tex.buttonTrans) { prescripts ->
          prescripts!!.defaults().grow().marginTop(0f).marginBottom(0f).marginRight(5f).marginRight(5f)
          prescripts.add(Core.bundle.get("fragment.buttons.selectPrescripts")).padLeft(5f).padTop(5f).padBottom(5f)
          prescripts.row()
          prescripts.pane { buttons ->
            for (i in 0..<producers.size) {
              val p = producers.get(i)
              val c = consumers.get(i)

              if (c.selectable.get() == BaseConsumers.Visibility.hidden) continue

              buttons!!.left().button({ t ->
                t!!.left().defaults().left()
                buildRecipeSimple(c, p, t)
              }, Styles.underlineb, { configure(i) }).touchable { c.selectable.get()!!.buttonValid }.update { b: Button? -> b!!.setChecked(recipeCurrent == i) }.fillY().growX().left().margin(5f).marginTop(8f).marginBottom(8f).pad(4f).get().addListener(Tooltip { t: Table? -> t!!.table(Tex.paneLeft) { detail: Table? -> FactoryBlockComp.buildRecipe(detail!!, c, p) } })

              buttons.row()
            }
          }.fill().maxHeight(280f)
        }

        table.row()
      }
    }

    override fun sense(sensor: LAccess?): Double {
      if (sensor == LAccess.progress) {
        progress()
      }

      return super.sense(sensor)
    }

    override fun write(write: Writes) {
      super.write(write)
      write.bool(recipeSelected)
      writeFactory(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      recipeSelected = read.bool()
      readFactory(read)
    }

    override fun craftTrigger() {
      craftEffect.at(getX(), getY(), craftEffectColor)
      if (craftTrigger != null) (craftTrigger as Cons<NormalCrafterBuild?>).get(this)
      if (craftedSound !== Sounds.none) craftedSound.at(x, y, 1f, craftedSoundVolume)
    }

    override fun onCraftingUpdate() {
      if (Mathf.chanceDelta(updateEffectChance.toDouble())) {
        updateEffect.at(getX() + Mathf.range(effectRange * 4f), getY() + Mathf.range(effectRange * 4), updateEffectColor)
      }

      if (crafting != null) (crafting as Cons<NormalCrafterBuild?>).get(this)
    }
  }
}