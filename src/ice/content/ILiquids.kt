package ice.content

import arc.graphics.Color
import arc.math.Mathf
import arc.struct.ObjectSet
import ice.entities.IcePuddle
import ice.game.EventType
import ice.world.content.liquid.IceLiquid
import mindustry.content.Fx
import mindustry.content.Fx.flakExplosionBig
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.gen.Puddle
import mindustry.graphics.Pal
import mindustry.type.Liquid
import mindustry.world.Tile
import singularity.graphic.SglDraw
import singularity.graphic.SglShaders
import singularity.type.CellLiquid
import singularity.type.ReactLiquid
import singularity.type.ReactLiquid.Companion.effectWith
import singularity.world.SglFx
import universecore.world.Load

@Suppress("unused")
object ILiquids :Load {
  init {
    EventType.addContentInitEvent {
      Liquids.water.shownPlanets.add(IPlanets.阿德里)
    }
  }

  val 腐殖浆体 = IceLiquid("liquid_humusSlurry", "a09bbd") {
    localization {
      zh_CN {
        this.localizedName = "腐殖浆体"
        description = "一种富含有机质的浆体,可用于土壤改良"
      }
      en {
        localizedName = "Humus Slurry"
        description = "An organic-rich slurry that can be used for soil improvement."
      }
    }
    viscosity = 0.6f
    temperature = 0.3f
  }
  val 温热孢液 = IceLiquid("liquid_warmSpore", "fa9c28") {
    localization {
      zh_CN {
        this.localizedName = "温热孢液"
        description = "一种温暖的孢子悬浮液,具有生物活性"
      }
      en {
        localizedName = "Warm Spore Fluid"
        description = "A warm spore suspension with biological activity."
      }
    }
    temperature = 0.8f
    viscosity = 0.5f

  }
  val 芥蒂液 = IceLiquid("liquid_cressLiquid", "7f7f7f") {
    localization {
      zh_CN {
        this.localizedName = "芥蒂液"
        description = "一种灰色的中性液体,可用于中和反应"
      }
      en {
        localizedName = "Cress Liquid"
        description = "A gray neutral liquid that can be used in neutralization reactions."
      }
    }
    viscosity = 0.4f
    temperature = 0.5f
  }
  val 废水 = IceLiquid("liquid_wasteWater", "666666") {
    localization {
      zh_CN {
        this.localizedName = "废水"
        description = "一种由工业生产排放的强放射性废水,被其污染过的地区极难再次使用"
      }
      en {
        localizedName = "Wastewater"
        description = "Highly radioactive wastewater discharged from industrial production. Areas contaminated by it are extremely difficult to reuse."
      }
    }
    incinerable = false
    effect = IStatus.辐射
    heatCapacity = 0.25f
    viscosity = 0.99f
    temperature = 1.5f
  }
  val 浓稠血浆 = IceLiquid("liquid_thickPlasma", "cc3737") {
    localization {
      zh_CN {
        this.localizedName = "浓稠血浆"
        description = "从朔方蔓延而来"
      }
      en {
        localizedName = "Thick Plasma"
        description = "It spreads from the far north."
      }
    }
    nutrientConcentration = 0.2f
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
    localization {
      zh_CN {
        this.localizedName = "急冻液"
        description = "由低温化合物与冷却液混合而成,比冷却液效果更强"
      }
      en {
        localizedName = "Swift Cryofluid"
        description = "Made by mixing cryogenic compounds with cryofluid. It provides stronger cooling than ordinary cryofluid."
      }
    }
    lightColor = Color.valueOf("E1E9F09A")
    effect = StatusEffects.freezing
    heatCapacity = 1.4f
    viscosity = 0.6f
    temperature = 0.15f
  }
  val 灵液 = IceLiquid("liquid_ichors", "ffaa5f") {
    localization {
      zh_CN {
        this.localizedName = "灵液"
        description = "一种酸性极强的溶液,可以用来处理金属"
      }
      en {
        localizedName = "Ichor"
        description = "An extremely acidic solution that can be used to process metals."
      }
    }
    viscosity = 0.7f
    boilPoint = 1.7f
  }
  val 血肉赘生物 = CellLiquid("liquid_bloodSlime", "C74E48").apply {
    localization {
      zh_CN {
        this.localizedName = "血肉赘生物"
        description = "一种高温且易燃易爆的烈性流体,制取或运输该液体时,请使用专用管道!"
        details = "[red]鲜血必将流淌[]"
      }
      en {
        localizedName = "Flesh Slime"
        description = "A hot, highly flammable and explosive fluid. Use dedicated conduits when producing or transporting it!"
        details = "[red]Blood shall flow[]"
      }
    }
    incinerable = false
    cells = 8
    maxSpread = 0.5f
    spreadDamage = 0.1f
    spreadTarget = Liquids.water
    spreadConversion = 0.5f
    effect = IStatus.熔融
    colorFrom = Color.valueOf("FF5845")
    colorTo = Color.valueOf("BF3E47")
    lightColor = Color.valueOf("C74E489A")
    canStayOn = ObjectSet.with(Liquids.water)
    particleEffect = flakExplosionBig
    explosiveness = 0.75f
    flammability = 0.5f
    heatCapacity = 0.25f
    viscosity = 0.9f
    temperature = 0.7f
  }
  val 超临界流体 = IceLiquid("liquid_supercriticalFluids", "E1776A") {
    localization {
      zh_CN {
        this.localizedName = "超临界流体"
        description = "一种通过复杂工业化处理萃取出的特殊流体,具有良好的传质、传热及溶解性能"
      }
      en {
        localizedName = "Supercritical Fluid"
        description = "A special fluid extracted through complex industrial processing, with excellent mass transfer, heat transfer and dissolving properties."
      }
    }
    incinerable = false
    lightColor = Color.valueOf("E1776A9A")
    effect = IStatus.蚀骨
    canStayOn = ObjectSet.with(Liquids.water)
    heatCapacity = 2.1f
    viscosity = 0.5f
    temperature = 0.1f
  }
  val 暮光液 = IceLiquid("liquid_duskLiquid", "deedff") {
    localization {
      zh_CN {
        this.localizedName = "暮光液"
        description = "暮光液"
      }
      en {
        localizedName = "Dusk Liquid"
        description = "Dusk liquid."
      }
    }
    temperature = 0.2f
  }
  var 纯净水 = object :IceLiquid("liquid_purified_water", Color.valueOf("#C3DFFF").a(0.8f)) {
    init {
      localization {
        zh_CN {
          this.localizedName = "纯净水"
          description = "分离掉其中的杂质的水,在各类严格的流程中是必要的"
          details = "为避免引入杂质,作为溶剂的水必须经过净化去除其中可能影响产品质量的其他物质"
        }
        en {
          localizedName = "Purified Water"
          description = "Water with impurities removed. It is necessary in many strict production processes."
          details = "To avoid introducing impurities, water used as a solvent must be purified to remove other substances that may affect product quality."
        }
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
  var 藻泥 = CellLiquid("liquid_algae_mud", Color.valueOf("#6EA145")).apply {
    localization {
      zh_CN {
        this.localizedName = "藻泥浆"
        description = "藻类微生物繁衍的集合体,用途广泛"
        details = "微生物在极端恶劣的环境下会脱去水分进入休眠状态,抗逆性极大提高"
      }
      en {
        localizedName = "Algae Mud"
        description = "A colony of algae microorganisms with a wide range of uses."
        details = "Microorganisms can dehydrate and enter dormancy under extremely harsh conditions, greatly improving their stress resistance."
      }
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

  var 酸液: ReactLiquid = ReactLiquid("liquid_acid", Color.valueOf("#EDF3A9").a(0.75f)).apply {
    localization {
      zh_CN {
        this.localizedName = "酸液"
        description = "复合酸液,工业用途广泛,金属冶炼和物质合成都不可或缺"
        details = "\"当心腐蚀\"\n\"穿戴防护措施\"\n\"挥发性\""
      }
      en {
        localizedName = "Acid"
        description = "A composite acid with broad industrial uses, indispensable for metal smelting and material synthesis."
        details = "\"Corrosive\"\n\"Wear protective equipment\"\n\"Volatile\""
      }
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
    localization {
      zh_CN {
        this.localizedName = "碱液"
        description = "复合碱液,工业用途广泛,金属冶炼和物质合成都不可或缺"
        details = "\"当心腐蚀\"\n\"穿戴防护措施\"\n\"挥发性\""
      }
      en {
        localizedName = "Lye"
        description = "A composite alkaline solution with broad industrial uses, indispensable for metal smelting and material synthesis."
        details = "\"Corrosive\"\n\"Wear protective equipment\"\n\"Volatile\""
      }
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
    localization {
      zh_CN {
        this.localizedName = "氯化硅溶胶"
        description = "富含硅的胶状化合物,易富集硅元素,可用于制造硅或者气凝胶"
      }
      en {
        localizedName = "Silicon Chloride Sol"
        description = "A silicon-rich gel compound that readily concentrates silicon and can be used to produce silicon or aerogel."
      }
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
    localization {
      zh_CN {
        this.localizedName = "复合矿物溶液"
        description = "含有各种矿物离子的盐溶液,经过电离可以获得各种金属产物"
        details =
          "通常来说在自然地壳中的金属矿物会有一定的富集作用,往往矿物集团伴生的金属种类不会很多,但在软流层上部这一规律似乎就不适用了,岩浆流会把各种矿物搅和在一起,在靠近那里开采的矿石里几乎什么都能弄到"
      }
      en {
        localizedName = "Mixed Ore Solution"
        description = "A salt solution containing various mineral ions. Electrolysis can yield various metal products."
        details = "In general, metallic minerals in the natural crust tend to be enriched to some degree, and associated mineral groups usually do not contain too many metal types. This rule, however, seems not to apply near the upper asthenosphere. Magma flows mix all kinds of minerals together, so ores mined near there can contain almost anything."
      }
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
    localization {
      zh_CN {
        this.localizedName = "铀盐溶液"
        description = "富含大量铀金属离子的溶液,是铀矿物处理的中间物"
      }
      en {
        localizedName = "Uranium Salt Solution"
        description = "A solution rich in uranium metal ions, used as an intermediate in uranium ore processing."
      }
    }
    heatCapacity = 0.6f
    temperature = 0.65f
    flammability = 0f
    explosiveness = 0f
    viscosity = 0.5f
    coolant = false
    boilPoint = 1f
  }
  var FEX流体 = object :IceLiquid("liquid_FEX_liquid", Color.valueOf("#E34248")) {
    init {
      localization {
        zh_CN {
          this.localizedName = "FEX流体"
          description = "经分离杂质后的FEX的原始形态,一种半流体,需要结晶为高纯度的晶体才能满足工业需求"
          details = "流动的越快,流动就会越慢...流动速度会决定FEX的粘度,它会在任何接触的致密介质上发生富集和弱结晶"
        }
        en {
          localizedName = "FEX Fluid"
          description = "The raw form of FEX after impurities are separated out. It is a semifluid that must crystallize into high-purity crystals to meet industrial requirements."
          details = "The faster it flows, the slower it flows... Its flow speed determines its viscosity, and it enriches and weakly crystallizes on any dense medium it contacts."
        }
      }
      heatCapacity = 1f
      explosiveness = 0f
      flammability = 0f
      temperature = 0.35f
      viscosity = 0f

      effect = IStatus.结晶化
    }

    val taskID: Int = SglDraw.nextTaskID()

    override fun drawPuddle(puddle: Puddle) {
      SglDraw.drawTask(taskID, puddle, SglShaders.wave, { s: SglShaders.WaveShader ->
        s.waveMix = Pal.lightishGray
        s.mixAlpha = 0.2f + Mathf.absin(5f, 0.2f)
        s.waveScl = 0.2f
        s.maxThreshold = 1f
        s.minThreshold = 0.4f
      }, { puddle -> super.drawPuddle(puddle) })
    }
  }
  var 相位态FEX流体 = object :IceLiquid("liquid_phase_FEX_liquid", Color.valueOf("#E34248")) {
    init {
      localization {
        zh_CN {
          this.localizedName = "相位态FEX流体"
          description = "相位化后的FEX流体,物理性质改变,表面张力有自发性的剧烈波动,且具会与其接触介质发生共振,性能优越的流质能量载体"
          details = "严禁在无谐振防护的情况下靠近储存相位态FEX的储罐或者储液槽"
        }
        en {
          localizedName = "Phased FEX Fluid"
          description = "Phase-shifted FEX fluid with altered physical properties. Its surface tension fluctuates violently on its own and resonates with media it contacts, making it an excellent fluid energy carrier."
          details = "Do not approach tanks or reservoirs storing phased FEX without resonance protection."
        }
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

  val 氧气 = IceLiquid("liquid_oxygen", Color.valueOf("#d7d9e2")) {
    localization {
      zh_CN {
        this.localizedName = "氧气"
        description = "最常用的气体,在工业生产中都作为氧化剂"
      }
      en {
        localizedName = "Oxygen"
        description = "The most commonly used gas, serving as an oxidizer in industrial production."
      }
    }
    gas = true
    explosiveness = 0f
    flammability = 0f
    temperature = 0.5f
    viscosity = 0f
  }
  var 二氧化碳 = IceLiquid("liquid_carbon_dioxide", Color.white) {
    localization {
      zh_CN {
        this.localizedName = "二氧化碳"
        description = "大气中普遍存在的温室气体,在工业生产中,二氧化碳常被用作制冷剂,惰性保护气体"
      }
      en {
        localizedName = "Carbon Dioxide"
        description = "A greenhouse gas commonly present in the atmosphere. In industry, carbon dioxide is often used as a refrigerant and inert shielding gas."
      }
    }
    gas = true
    heatCapacity = 1.2f
    explosiveness = 0f
    flammability = 0f
    temperature = 0.4f
    viscosity = 0f
  }
  var 二氧化硫 = IceLiquid("liquid_sulfur_dioxide", Color.valueOf("#FFCF76")) {
    localization {
      zh_CN {
        this.localizedName = "二氧化硫"
        description = "一种氧化性气体,通常用于制备硫酸"
      }
      en {
        localizedName = "Sulfur Dioxide"
        description = "An oxidizing gas usually used to produce sulfuric acid."
      }
    }
    gas = true

    heatCapacity = 0.65f
    explosiveness = 0f
    flammability = 0f
    temperature = 0.4f
    viscosity = 0f

  }
  val 沼气 = IceLiquid("liquid_methane", "bb2912") {
    localization {
      zh_CN {
        this.localizedName = "沼气"
        description = "一种天然气体,主要成分是甲烷,可替代部分工厂的燃料需求"
      }
      en {
        localizedName = "Methane"
        description = "A natural gas mainly composed of methane, capable of replacing part of a factory's fuel demand."
      }
    }
    gas = true
    explosiveness = 0.5f
    flammability = 0.8f
  }

  val 氢气 = IceLiquid("liquid_hydrogen", Color.valueOf("#9eabf7")) {
    localization {
      zh_CN {
        this.localizedName = "氢气"
        description = ""
      }
      en {
        localizedName = "Hydrogen"
        description = ""
      }
    }
    gas = true
    flammability = 1f
  }
  var 氦气 = IceLiquid("liquid_helium", Color.valueOf("#D6FFFC")) {
    localization {
      zh_CN {
        this.localizedName = "氦气"
        description = "较为常见的0族惰性气体,常用作工业保护气或通过中子流轰击生产核聚变燃料"
        details = "一般来说氦气在行星岩层中的分布会比较丰富,由于原子质量过轻,在有大气的行星地表很难大量存在"
      }
      en {
        localizedName = "Helium"
        description = "A relatively common group 0 inert gas, often used as an industrial shielding gas or produced as fusion fuel through neutron bombardment."
        details = "Helium is generally abundant in planetary rock layers. Because its atomic mass is too light, it is difficult for large amounts to remain on the surface of planets with atmospheres."
      }
    }
    gas = true
    heatCapacity = 0.4f
    explosiveness = 0f
    flammability = 0f
    temperature = 0.4f
    viscosity = 0f
  }
  var 氯气 = IceLiquid("liquid_chlorine", Color.valueOf("#DAF2AA")) {
    localization {
      zh_CN {
        this.localizedName = "氯气"
        description = "生物毒性的气体,水系当中往往含有一定量的氯及其盐离子,工业上十分常用的气体"
      }
      en {
        localizedName = "Chlorine"
        description = "A biologically toxic gas. Water systems often contain a certain amount of chlorine and chloride ions, making it very common in industry."
      }
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
