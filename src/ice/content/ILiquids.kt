package ice.content

import arc.graphics.Color
import arc.math.Mathf
import arc.struct.ObjectSet
import ice.entities.IcePuddle
import ice.library.EventType
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.liquid.IceLiquid
import mindustry.content.Fx
import mindustry.content.Fx.flakExplosionBig
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.gen.Puddle
import mindustry.graphics.Pal
import mindustry.type.CellLiquid
import mindustry.type.Liquid
import mindustry.world.Tile
import singularity.graphic.SglDraw
import singularity.graphic.SglShaders
import singularity.type.ReactLiquid
import singularity.type.ReactLiquid.effectWith
import singularity.world.SglFx

@Suppress("unused")
object ILiquids : Load {
  val 腐殖浆体 = IceLiquid("liquid_humusSlurry", "a09bbd") {
    viscosity = 0.6f
    temperature = 0.3f
    bundle {
      desc(zh_CN, "腐殖浆体", "一种富含有机质的浆体,可用于土壤改良")
    }
  }
  val 温热孢液 = IceLiquid("liquid_warmSpore", "fa9c28") {
    temperature = 0.8f
    viscosity = 0.5f
    bundle {
      desc(zh_CN, "温热孢液", "一种温暖的孢子悬浮液,具有生物活性")
    }
  }
  val 芥蒂液 = IceLiquid("liquid_cressLiquid", "7f7f7f") {
    viscosity = 0.4f
    temperature = 0.5f
    bundle {
      desc(zh_CN, "芥蒂液", "一种灰色的中性液体,可用于中和反应")
    }
  }
  val 废水 = IceLiquid("liquid_wasteWater", "666666") {
    effect = IStatus.辐射
    heatCapacity = 0.25f
    viscosity = 0.99f
    temperature = 1.5f
    bundle {
      desc(zh_CN, "废水", "一种由工业生产排放的强放射性废水,被其污染过的地区极难再次使用")
    }
  }
  val 浓稠血浆 = IceLiquid("liquid_thickPlasma", "cc3737") {
    nutrientConcentration = 0.2f
    bundle {
      desc(zh_CN, "浓稠血浆", "从朔方蔓延而来")
    }
    setUpdate { pud ->
      pud.buildOn()?.let {
        if (pud is IcePuddle) {
          if (it.team != pud.team) {
            it.damage(30f / 60f)
          }
        }
      }
    }
  }
  val 急冻液 = IceLiquid("liquid_swiftCryofluid", "E1E9F0") {
    lightColor = Color.valueOf("E1E9F09A")
    effect = StatusEffects.freezing
    heatCapacity = 1.4f
    viscosity = 0.6f
    temperature = 0.15f
    bundle {
      desc(zh_CN, "急冻液", "由低温化合物与冷却液混合而成,比冷却液效果更强")
    }
  }
  val 灵液 = IceLiquid("liquid_ichors", "ffaa5f") {
    viscosity = 0.7f
    boilPoint = 1.7f
    bundle {
      desc(zh_CN, "灵液", "一种酸性极强的溶液,可以用来处理金属")
    }
  }
  val 血肉赘生物 = CellLiquid("liquid_bloodSlime").apply {
    incinerable = false
    cells = 8
    maxSpread = 0.5f
    spreadDamage = 0.1f
    spreadTarget = Liquids.water
    spreadConversion = 0.5f
    effect = IStatus.熔融
    colorFrom = Color.valueOf("FF5845")
    colorTo = Color.valueOf("BF3E47")
    color = Color.valueOf("C74E48")
    lightColor = Color.valueOf("C74E489A")
    canStayOn = ObjectSet.with(Liquids.water)
    particleEffect = flakExplosionBig
    explosiveness = 0.75f
    flammability = 0.5f
    heatCapacity = 0.25f
    viscosity = 0.9f
    temperature = 0.7f
    bundle {
      desc(zh_CN, "血肉赘生物", "一种高温且易燃易爆的烈性流体液体,制取或运输该液体时,请使用专用管道!", "[red]鲜血必将流淌[]")
    }
  }
  val 超临界流体 = IceLiquid("liquid_supercriticalFluids", "E1776A") {
    incinerable = false
    lightColor = Color.valueOf("E1776A9A")
    effect = IStatus.蚀骨
    canStayOn = ObjectSet.with(Liquids.water)
    heatCapacity = 2.1f
    viscosity = 0.5f
    temperature = 0.1f
    bundle {
      desc(zh_CN, "超临界流体", "一种通过复杂工业化处理萃取出的特殊流体,具有良好的传质、传热及溶解性能")
    }
  }
  val 暮光液 = IceLiquid("liquid_duskLiquid", "deedff") {
    temperature = 0.2f
    bundle {
      desc(zh_CN, "暮光液", "暮光液")
    }
  }
  var 纯净水 = object : Liquid("liquid_purified_water", Color.valueOf("#C3DFFF").a(0.8f)) {
    init {
      bundle {
        desc(zh_CN, "纯净水", "分离掉其中的杂质的水,在各类严格的流程中是必要的", "为避免引入杂质,作为溶剂的水必须经过净化去除其中可能影响产品质量的其他物质")
      }
      heatCapacity = 0.45f
      temperature = 0.4f
      flammability = 0f
      explosiveness = 0f
      viscosity = 0.5f

      boilPoint = 0.5f
    }

    override fun react(other: Liquid?, amount: Float, tile: Tile?, x: Float, y: Float): Float {
      if (other === Liquids.water) {
        return amount
      }
      return 0f
    }

    override fun update(puddle: Puddle) {
      super.update(puddle)
      puddle.liquid = Liquids.water
    }
  }
  var 藻泥 = object : CellLiquid("liquid_algae_mud", Color.valueOf("#6EA145")) {
    init {
      bundle {
        desc(zh_CN, "藻泥浆", "藻类微生物繁衍的集合体,用途广泛", "微生物在极端恶劣的环境下会脱去水分进入休眠状态,抗逆性极大提高")
      }
      heatCapacity = 0.4f
      explosiveness = 0f
      flammability = 0f
      temperature = 0.5f
      viscosity = 0.5f

      coolant = false

      boilPoint = 0.5f

      spreadDamage = 0f
      spreadTarget = Liquids.water
      spreadConversion = 1.1f
      maxSpread = 0.4f

      colorFrom = color.cpy().lerp(Color.white, 0.25f)
      colorTo = color.cpy().lerp(Color.white, 0.5f)

      canStayOn.addAll(Liquids.oil, Liquids.water)
    }
  }
  var 酸液: ReactLiquid = ReactLiquid("liquid_acid", Color.valueOf("#EDF3A9").a(0.75f)).apply {
    bundle {
      desc(zh_CN, "酸液", "复合酸液,工业用途广泛,金属冶炼和物质合成都不可或缺", "“当心腐蚀”\n“穿戴防护措施”\n”挥发性“")
    }
    heatCapacity = 0.5f
    temperature = 0.45f
    flammability = 0f
    explosiveness = 0f
    viscosity = 0.5f
    coolant = false
    boilPoint = 0.55f
    EventType.addContentInitEvent {
      reactWith(碱液, effectWith(Fx.vapor, 0.1f, Color.white, -1f))
    }
  }
  var 碱液 = ReactLiquid("liquid_lye", Color.valueOf("#DBFAFF").a(0.75f)).apply {
    bundle {
      desc(zh_CN, "碱液", "复合碱液,工业用途广泛,金属冶炼和物质合成都不可或缺", "“当心腐蚀”\n“穿戴防护措施”\n”挥发性“")
    }
    temperature = 0.45f
    flammability = 0f
    explosiveness = 0f
    heatCapacity = 0.5f
    viscosity = 0.5f
    coolant = false
    boilPoint = 0.55f
    EventType.addContentInitEvent {
      reactWith(酸液, effectWith(Fx.vapor, 0.1f, Color.white, -1f))
    }
  }
  var 氯化硅溶胶 = IceLiquid("liquid_silicon_chloride_sol", Color.valueOf("#C0B4B0").a(0.8f)) {
    bundle {
      desc(zh_CN, "氯化硅溶胶", "富含硅的胶状化合物,易富集硅元素,可用于制造硅或者气凝胶")
    }
    heatCapacity = 0.65f
    temperature = 0.6f
    flammability = 0.3f
    explosiveness = 0f
    viscosity = 0.85f

    coolant = false

    boilPoint = 1.5f

  }
  var 复合矿物溶液 = IceLiquid("liquid_mixed_ore_solution", Color.valueOf("#CBE0E0")) {
    bundle {
      desc(zh_CN, "复合矿物溶液", "含有各种矿物离子的盐溶液,经过电离可以获得各种金属产物", "通常来说在自然地壳中的金属矿物会有一定的富集作用,往往矿物集团伴生的金属种类不会很多,但在软流层上部这一规律似乎就不适用了,岩浆流会把各种矿物搅和在一起,在靠近那里开采的矿石里几乎什么都能弄到")
    }
    heatCapacity = 0.6f
    temperature = 0.65f
    flammability = 0f
    explosiveness = 0f
    viscosity = 0.5f

    coolant = false

    boilPoint = 1f

  }
  var 铀盐溶液 = IceLiquid("liquid_uranium_salt_solution", Color.valueOf("#DAF2AA")) {
    bundle {
      desc(zh_CN, "铀盐溶液", "富含大量铀金属离子的溶液,是铀矿物处理的中间物")
    }
    heatCapacity = 0.6f
    temperature = 0.65f
    flammability = 0f
    explosiveness = 0f
    viscosity = 0.5f
    coolant = false
    boilPoint = 1f
  }
  var FEX流体 = object : Liquid("liquid_FEX_liquid", Color.valueOf("#E34248")) {
    init {
      bundle {
        desc(zh_CN, "FEX流体", "经分离杂质后的FEX的原始形态,一种半流体,需要结晶为高纯度的晶体才能满足工业需求", "流动的越快,流动就会越慢...流动速度会决定FEX的粘度,它会在任何接触的致密介质上发生富集和弱结晶")
      }
      heatCapacity = 1f
      explosiveness = 0f
      flammability = 0f
      temperature = 0.35f
      viscosity = 0f

      effect = IStatus.结晶化
    }

    val taskID: Int = SglDraw.nextTaskID()

    override fun drawPuddle(puddle: Puddle?) {
      SglDraw.drawTask<Puddle?, SglShaders.WaveShader?>(taskID, puddle, SglShaders.wave, { s: SglShaders.WaveShader? ->
        s!!.waveMix = Pal.lightishGray
        s.mixAlpha = 0.2f + Mathf.absin(5f, 0.2f)
        s.waveScl = 0.2f
        s.maxThreshold = 1f
        s.minThreshold = 0.4f
      }, { puddle: Puddle? -> super.drawPuddle(puddle) })
    }
  }
  var 相位态FEX流体 = object : Liquid("liquid_phase_FEX_liquid", Color.valueOf("#E34248")) {
    init {
      bundle {
        desc(zh_CN, "相位态FEX流体", "相位化后的FEX流体,物理性质改变,表面张力有自发性的剧烈波动,且具会与其接触介质发生共振,性能优越的流质能量载体", "严禁在无谐振防护的情况下靠近储存相位态FEX的储罐或者储液槽")
      }
      heatCapacity = 1.25f
      explosiveness = 0f
      flammability = 0f
      temperature = 0f
      viscosity = 0f

      particleEffect = SglFx.crystalFragFex
      particleSpacing = 48f
    }

    val taskID: Int = SglDraw.nextTaskID()

    override fun drawPuddle(puddle: Puddle?) {
      SglDraw.drawTask<Puddle, SglShaders.WaveShader?>(taskID, puddle, SglShaders.wave, { s: SglShaders.WaveShader? ->
        s!!.waveMix = Color.white
        s.mixAlpha = 0.2f + Mathf.absin(3f, 0.4f)
        s.waveScl = 0.3f
        s.maxThreshold = 0.9f
        s.minThreshold = 0.5f
      }, { puddle: Puddle? -> super.drawPuddle(puddle) })
    }
  }

  var 二氧化碳 = IceLiquid("liquid_carbon_dioxide", Color.white) {
    bundle {
      desc(zh_CN, "二氧化碳", "大气中普遍存在的温室气体,在工业生产中,二氧化碳常被用作制冷剂,惰性保护气体")
    }
    gas = true
    heatCapacity = 1.2f
    explosiveness = 0f
    flammability = 0f
    temperature = 0.4f
    viscosity = 0f
  }
  var 二氧化硫 = IceLiquid("liquid_sulfur_dioxide", Color.valueOf("#FFCF76")) {
    bundle {
      desc(zh_CN, "二氧化硫", "一种氧化性气体,通常用于制备硫酸")
    }
    gas = true

    heatCapacity = 0.65f
    explosiveness = 0f
    flammability = 0f
    temperature = 0.4f
    viscosity = 0f

  }
  val 沼气 = IceLiquid("liquid_methane", "bb2912") {
    gas = true
    explosiveness = 0.5f
    flammability = 0.8f
    bundle {
      desc(zh_CN, "沼气", "一种天然气体,主要成分是甲烷,可替代部分工厂的燃料需求")
    }
  }
  val 氢气 = IceLiquid("liquid_hydrogen", Color.valueOf("#9eabf7")) {
    bundle {
      desc(zh_CN, "氢气")
    }
    gas = true
    flammability = 1f
  }
  var 氦气 = IceLiquid("liquid_helium", Color.valueOf("#D6FFFC")) {
    bundle {
      desc(zh_CN, "氦气", "较为常见的0族惰性气体,常用作工业保护气或通过中子流轰击生产核聚变燃料", "一般来说氦气在行星岩层中的分布会比较丰富,由于原子质量过轻,在有大气的行星地表很难大量存在")
    }
    gas = true
    heatCapacity = 0.4f
    explosiveness = 0f
    flammability = 0f
    temperature = 0.4f
    viscosity = 0f
  }
  var 氯气 = IceLiquid("liquid_chlorine", Color.valueOf("#DAF2AA")) {
    bundle {
      desc(zh_CN, "氯气", "生物毒性的气体,水系当中往往含有一定量的氯及其盐离子,工业上十分常用的气体")
    }
    gas = true
    heatCapacity = 0.35f
    explosiveness = 0.3f
    flammability = 0.2f
    temperature = 0.4f
    viscosity = 0f
  }

  var 孢子云 = IceLiquid("liquid_spore_cloud", Pal.spore) {
    //仅用于绘制
    gas = true
    hidden = true
  }
}
