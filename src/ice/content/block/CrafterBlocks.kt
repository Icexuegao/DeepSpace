package ice.content.block

import arc.Core
import arc.func.*
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Geometry
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.layout.Table
import arc.util.Time
import arc.util.Tmp
import arc.util.noise.Noise
import ice.audio.ISounds
import ice.content.IItems
import ice.content.ILiquids
import ice.graphics.IceColor
import ice.library.struct.AttachedProperty
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.crafting.CeriumExtractor
import ice.world.content.blocks.crafting.GenericCrafter
import ice.world.content.blocks.crafting.Incinerator
import ice.world.content.blocks.crafting.multipleCrafter.MultipleCrafter
import ice.world.content.blocks.crafting.oreMultipleCrafter.OreFormula
import ice.world.content.blocks.crafting.oreMultipleCrafter.OreMultipleCrafter
import ice.world.draw.DrawArcSmelt
import ice.world.draw.DrawBuild
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.RadialEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.consumers.ConsumePower
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.Stats
import singularity.Singularity
import singularity.graphic.Distortion
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.graphic.SglShaders
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.SglBlock
import singularity.world.blocks.function.Destructor
import singularity.world.blocks.product.*
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.blocks.product.SpliceCrafter.SpliceCrafterBuild
import singularity.world.consumers.SglConsumeFloor
import singularity.world.consumers.SglConsumers
import singularity.world.draw.DrawAntiSpliceBlock
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDyColorCultivator
import singularity.world.draw.DrawRegionDynamic
import singularity.world.meta.SglStat
import singularity.world.particles.SglParticleModels
import universecore.components.blockcomp.FactoryBuildComp
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeType
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.generator.CircleGenerator
import universecore.world.lightnings.generator.LightningGenerator
import universecore.world.lightnings.generator.VectorLightningGenerator
import universecore.world.producers.ProduceType
import kotlin.math.max

@Suppress("unused")
object CrafterBlocks : Load {
  val 物品焚烧炉 = Incinerator("itemIncinerator").apply {
    size = 1
    hasLiquids = false
    flameColor = IceColor.c5
    consumePower(20 / 60f)
    requirements(Category.crafting, IItems.高碳钢, 20, IItems.铜锭, 5)
    bundle {
      desc(zh_CN, "物品焚烧炉")
    }
  }
  val 液体焚烧炉 = Incinerator("liquidIncinerator").apply {
    size = 1
    hasItems = false
    flameColor = IceColor.b7
    consumePower(20 / 60f)
    requirements(Category.crafting, IItems.高碳钢, 20, IItems.铜锭, 5)
    bundle {
      desc(zh_CN, "液体焚烧炉")
    }
  }
  val 焚烧炉 = Incinerator("incinerator").apply {
    size = 1
    flameColor = IceColor.b4
    consumePower(20 / 60f)
    requirements(Category.crafting, IItems.高碳钢, 20, IItems.铅锭, 5)
    bundle {
      desc(zh_CN, "焚烧炉")
    }
  }
  val 量子蚀刻厂 = GenericCrafter("integratedFactory").apply {
    drawers = DrawMulti(DrawRegion("-bottom"), DrawRegion("-top"))
    itemCapacity = 20
    health = 200
    outputItems(IItems.电子元件, 1)
    consumeItems(*ItemStack.with(IItems.单晶硅, 1, IItems.石墨烯, 2, IItems.石英玻璃, 1))
    craftTime = 60f
    craftEffect = MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin, IceEffects.hitLaserBlast)
    size = 3
    requirements(Category.crafting, ItemStack.with(IItems.铜锭, 19))
    bundle {
      desc(zh_CN, "量子蚀刻厂", "采用等离子蚀刻技术,在硅晶圆上雕刻出微米级电路,电子工业的基础设施")
    }
  }
  val 单晶硅厂 = GenericCrafter("monocrystallineSiliconFactory").apply {
    size = 4
    health = 460
    hasPower = true
    craftTime = 60f
    craftEffect = IceEffects.square(IItems.单晶硅.color)
    consumePower(1.8f)
    outputItems(IItems.单晶硅, 1)
    consumeItems(IItems.硫化合物, 1, IItems.石英, 3)
    val color = Color.valueOf("ffef99")
    setDrawMulti(DrawRegion("-bottom"), DrawBuild<GenericCrafter.GenericCrafterBuild> {
      Draw.color(color)
      Draw.alpha(warmup)
      Lines.lineAngleCenter(
        x + Mathf.sin(totalProgress(), 6f, Vars.tilesize / 3f * size), y, 90f, size * Vars.tilesize / 2f
      )
      Lines.lineAngleCenter(
        x, y + Mathf.sin(totalProgress(), 3f, Vars.tilesize / 3f * size), 0f, size * Vars.tilesize / 2f
      )
      Draw.color()
    }, DrawDefault(), DrawFlame(color))
    requirements(Category.crafting, IItems.铬锭, 55, IItems.高碳钢, 200, IItems.铜锭, 150)
    bundle {
      desc(zh_CN, "单晶硅厂", "使用硫化物和石英矿石生产纯度更高的单晶硅")
    }
  }
  val 铸铜厂 = GenericCrafter("copperFoundry").apply {
    size = 4
    health = 200
    craftTime = 90f
    outputItems(IItems.黄铜锭, 3)
    setDrawMulti(DrawDefault(), DrawFlame())
    consumeItems(*ItemStack.with(IItems.铜锭, 3, IItems.锌锭, 1))
    requirements(Category.crafting, ItemStack.with(IItems.铜锭, 200, IItems.低碳钢, 150))
    craftEffect = IceEffects.square(IItems.铜锭.color)
    bundle {
      desc(zh_CN, "铸铜厂")
    }
  }
  val 碳控熔炉 = MultipleCrafter("carbonSteelFactory").apply {
    bundle {
      desc(
        zh_CN, "碳控熔炉", "通过精确控制碳元素配比,在同一生产线灵活产出高碳钢和低碳钢,稳定的温度控制确保钢材质量始终达标"
      )
    }
    size = 3
    itemCapacity = 50
    alwaysUnlocked = true
    val ct = RadialEffect().apply {
      effect = Fx.surgeCruciSmoke
      rotationSpacing = 0f
      lengthOffset = 0f
      amount = 4
    }
    addFormula {
      craftTime = 45f
      craftEffect = ct
      setInput(ConsumeItems(ItemStack.with(IItems.赤铁矿, 2)), ConsumePower(60 / 60f, 0f, false))
      setOutput(ItemStack(IItems.低碳钢, 1))
    }
    addFormula {
      craftTime = 60f
      craftEffect = ct
      setInput(ConsumeItems(ItemStack.with(IItems.赤铁矿, 2, IItems.生煤, 3)), ConsumePower(90 / 60f, 0f, false))
      setOutput(ItemStack(IItems.高碳钢, 1))
    }
    setDrawMulti(DrawRegion("-bottom"), DrawArcSmelt().apply {
      x += 8
      startAngle = 135f
      endAngle = 225f
    }, DrawArcSmelt().apply {
      x -= 8
      startAngle = -45f
      endAngle = 45f
    }, DrawArcSmelt().apply {
      y += 8
      startAngle = 180 + 45f
      endAngle = 360 - 45f
    }, DrawArcSmelt().apply {
      y -= 8
      startAngle = 0f + 45
      endAngle = 180f - 45f
    }, DrawDefault())
    requirements(Category.crafting, IItems.铜锭, 10, IItems.低碳钢, 50)
  }
  val 普适冶炼阵列 = MultipleCrafter("universalSmelterArray").apply {
    bundle {
      desc(
        zh_CN, "普适冶炼阵列", "核心级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铜,锌,铅等多种金属原料,为后续生产提供稳定的金属供应"
      )
    }
    size = 3
    itemCapacity = 30
    addFormula {
      craftTime = 45f
      craftEffect = IceEffects.square(IceColor.b4)
      setInput(ConsumeItems(ItemStack.with(IItems.黄铜矿, 2)), ConsumePower(60 / 60f, 0f, false))
      setOutput(ItemStack(IItems.铜锭, 1))
    }
    addFormula {
      craftTime = 50f
      craftEffect = IceEffects.square(IceColor.b4)
      setInput(ConsumeItems(ItemStack.with(IItems.方铅矿, 2)), ConsumePower(60 / 60f, 0f, false))
      setOutput(ItemStack(IItems.铅锭, 1))
    }
    addFormula {
      craftTime = 60f
      craftEffect = IceEffects.square(IceColor.b4)
      setInput(ConsumeItems(ItemStack.with(IItems.闪锌矿, 2)), ConsumePower(60 / 60f, 0f, false))
      setOutput(ItemStack(IItems.锌锭, 1))
    }
    setDrawMulti(DrawDefault(), DrawFlame(IceColor.b4))
    requirements(Category.crafting, IItems.高碳钢, 150, IItems.低碳钢, 70)
  }
  val 硫化物混合器 = GenericCrafter("sulfideMixer").apply {
    size = 3
    itemCapacity = 36
    consumePower(1f)
    consumeItems(IItems.生煤, 4, IItems.铅锭, 6, IItems.金珀沙, 6)
    craftEffect = IceEffects.square(IItems.硫化合物.color)
    craftTime = 45f
    outputItems(IItems.硫化合物, 3)
    requirements(Category.crafting, IItems.高碳钢, 150, IItems.铜锭, 30, IItems.铬锭, 30)
    bundle {
      desc(zh_CN, "硫化物混合器", "将煤,铅,沙混合生成硫化合物")
    }
  }
  val 爆炸物混合器 = GenericCrafter("explosiveMixer").apply {
    size = 3
    itemCapacity = 36
    consumePower(2f)
    consumeItems(IItems.硫化合物, 3, IItems.燃素水晶, 1)
    craftTime = 45f
    craftEffect = IceEffects.square(IItems.爆炸化合物.color)
    outputItems(IItems.爆炸化合物, 3)
    requirements(Category.crafting, IItems.高碳钢, 80, IItems.铬锭, 50, IItems.单晶硅, 30)
    bundle {
      desc(zh_CN, "爆炸物混合器", "将硫化合物,燃素水晶混合生成爆炸物")
    }
  }
  val 特化冶炼阵列: MultipleCrafter = MultipleCrafter("specializedSmelterArray").apply {
    bundle {
      desc(
        zh_CN, "特化冶炼阵列", "进阶级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铬,金,钴等多种金属原料,为后续生产提供稳定的金属供应"
      )
    }
    size = 3
    itemCapacity = 35
    addFormula {
      craftTime = 60f
      craftEffect = IceEffects.square(IceColor.b4)
      setInput(ConsumeItems(ItemStack.with(IItems.铬铁矿, 2)), ConsumePower(60 / 60f, 0f, false))
      setOutput(ItemStack(IItems.铬锭, 1))
    }
    addFormula {
      craftTime = 50f
      craftEffect = IceEffects.square(IceColor.b4)
      setInput(ConsumeItems(ItemStack.with(IItems.硫钴矿, 2)), ConsumePower(60 / 60f, 0f, false))
      setOutput(ItemStack(IItems.钴锭, 1))
    }
    addFormula {
      craftTime = 60f
      craftEffect = IceEffects.square(IceColor.b4)
      setInput(ConsumeItems(ItemStack.with(IItems.金矿, 3)), ConsumePower(60 / 60f, 0f, false))
      setOutput(ItemStack(IItems.金锭, 1))
    }
    setDrawMulti(DrawDefault(), DrawFlame())
    requirements(Category.crafting, IItems.高碳钢, 150, IItems.铅锭, 40, IItems.铜锭, 30, IItems.锌锭, 30)
  }
  val 矿石粉碎机 = object : OreMultipleCrafter("mineralCrusher") {
    init {
      squareSprite = false
      hasLiquids = true
      drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(), DrawRegion("-runner", 6f, true).apply {
        x = 8.3f
        y = 8.3f
      }, DrawRegion("-runner", 6f, true).apply {
        x = -8.3f
        y = -8.3f
      }, DrawRegion("-runner", 6f, true).apply {
        x = 8.3f
        y = -8.3f
      }, DrawRegion("-runner", 6f, true).apply {
        x = -8.3f
        y = 8.3f
      })
      oreFormula.add(OreFormula().apply {
        crftTime = 60f
        addInput(IItems.方铅矿, 1)
        addInput(ConsumeLiquids(LiquidStack.with(Liquids.water, 15f)))
        addOutput(IItems.铅锭, 1, 5)
        addOutput(Items.copper, 2, 60)
        addOutput(Items.beryllium, 3, 7)
      }, OreFormula().apply {
        crftTime = 30f
        addInput(IItems.黄铜矿, 1, IItems.生煤, 1)
        addOutput(IItems.铅锭, 1, 50)
        addOutput(Items.graphite, 1, 50)
      })
    }
  }
  val 蜂巢陶瓷合成巢 = GenericCrafter("ceramicKiln").apply {
    size = 4
    health = 300
    craftTime = 120f
    squareSprite = false
    outputItems(IItems.复合陶瓷, 3)
    consumeItems(IItems.金珀沙, 10)
    consumeLiquids(ILiquids.异溶质, 32f / 60f)
    setDrawMulti(DrawRegion("-bottom"), DrawCultivator().apply {
      plantColor = ILiquids.异溶质.color
      plantColorLight = Color.valueOf("abbaff")
      spread = 2 * 8f - 6f
    }, DrawDefault())
    requirements(Category.crafting, IItems.铬锭, 50, IItems.铜锭, 20, IItems.锌锭, 30, IItems.黄铜锭, 10)
    bundle {
      desc(
        zh_CN, "蜂巢陶瓷合成巢", "利用基因改造的硅基菌群分泌陶瓷基质,再经激光固化,生产过程中会发出蜂鸣般的共振声", "资源蜜蜂?"
      )
    }
  }
  val 冲压锻炉 = GenericCrafter("pressingForge").apply {
    size = 5
    armor = 4f
    itemCapacity = 60
    liquidCapacity = 60f
    consumePower(27.5f)
    consumeItems(IItems.铱锇矿, 45)
    consumeLiquid(Liquids.water, 1f)
    craftTime = 45f
    updateEffect = Fx.fuelburn
    craftEffect = Fx.pulverizeMedium
    outputItems(IItems.铱板, 15)
    outputLiquids(ILiquids.废水, 1f)
    requirements(Category.crafting, IItems.高碳钢, 450, IItems.锌锭, 180, IItems.钴锭, 135)
    setDrawMulti(DrawRegion("-bottom"), DrawPistons().apply {
      sinMag = -2.6f
      sinScl = 3.5325f
      lenOffset = 0f
    }, DrawDefault(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 3.53429f
      color = Color.valueOf("F0511D")
    })
    ambientSoundVolume = 0.07f
    bundle {
      desc(zh_CN, "冲压锻炉", "快速大批量地熔炼铱锇矿并将其锻压为铱板")
    }
  }
  val 暮白高炉 = GenericCrafter("duskFactory").apply {
    size = 3
    craftTime = 120f
    itemCapacity = 20
    setDrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawArcSmelt().apply {
      y = 2f
      flameColor = IceColor.b4
      startAngle = 60f
      endAngle = 120f
    }, DrawArcSmelt().apply {
      y = -2f
      flameColor = IceColor.b4
      startAngle = 240f
      endAngle = 300f
    }, DrawArcSmelt().apply {
      x = 2f
      flameColor = IceColor.b4
      startAngle = -30f
      endAngle = 30f
    }, DrawArcSmelt().apply {
      x = -2f
      flameColor = IceColor.b4
      startAngle = 150f
      endAngle = 210f
    }, DrawDefault(), DrawFlame().apply {
      flameColor = IceColor.b4
    })
    craftEffect = IceEffects.square(IceColor.b4, length = 6f)
    outputItems(IItems.暮光合金, 3)
    consumeItems(IItems.低碳钢, 5, IItems.铬锭, 1, IItems.钴锭, 3, IItems.铪锭, 1)
    consumeLiquid(ILiquids.暮光液, 0.3f)
    requirements(Category.crafting, IItems.高碳钢, 200, IItems.铬锭, 50, IItems.钴锭, 30, IItems.铪锭, 10)
    bundle {
      desc(zh_CN, "暮白高炉", "将金属与信仰在苍白焰火中熔合,冶炼蕴含暮光之息的特殊合金")
    }
  }
  val 玳渊缚能厂 = GenericCrafter("tortoiseshellFactory").apply {
    size = 4
    health = 700
    craftTime = 120f
    itemCapacity = 20
    setDrawMulti(DrawDefault(), DrawFlame().apply {
      flameColor = Color.valueOf("c4aee4")
    })
    outputItems(IItems.玳渊矩阵, 1)
    consumeItems(IItems.铪锭, 10, IItems.暮光合金, 3, IItems.铱板, 1)
    requirements(Category.crafting, IItems.铬锭, 300, IItems.铪锭, 200, IItems.黄铜锭, 170)
    bundle {
      desc(
        zh_CN, "玳渊缚能厂", "为了生产大型能量武器设施,由枢机批准的能量生产建筑,将狂暴的玳渊能量封印在稳定的矩阵结构中,每一块矩阵都蕴含着巨大的能量"
      )
    }
  }
  val 萃取固化器 = GenericCrafter("concentrateSolidifier").apply {
    size = 3
    health = 400
    craftTime = 90f
    itemCapacity = 20
    craftEffect = IceEffects.square(IItems.铪锭.color, length = 8f)
    setDrawMulti(DrawDefault())
    consumePower(230f / 60f)
    outputItems(IItems.铪锭, 1)
    consumeItems(IItems.锆英石, 3)
    requirements(Category.crafting, IItems.高碳钢, 100, IItems.铬锭, 80, IItems.黄铜锭, 50, IItems.铜锭, 30)
    bundle {
      desc(zh_CN, "萃取固化器", "")
    }
  }
  val 电弧炉 = GenericCrafter("arcFurnace").apply {
    size = 3
    itemCapacity = 36
    consumePower(2.75f)
    craftTime = 4f * 60f
    outputItems(IItems.石英玻璃, 4)
    consumeItems(IItems.铅锭, 3, IItems.石英, 2, IItems.金珀沙, 2)
    setDrawMulti(DrawDefault(), DrawFlame())
    requirements(Category.crafting, IItems.高碳钢, 80, IItems.铅锭, 50, IItems.铜锭, 50, IItems.锌锭, 30)
    bundle {
      desc(zh_CN, "电弧炉")
    }
  }
  val 铈提取器 = CeriumExtractor("ceriumExtractor").apply {
    size = 3
    itemCapacity = 36
    liquidCapacity = 36f
    consumePower(7.6f)
    consumeItems(IItems.铈硅石, 5)
    consumeLiquids(ILiquids.异溶质, 15f / 60f)
    craftTime = 80f
    outputItems(IItems.铈锭, 2)
    outputLiquids(ILiquids.废水, 0.2f)
    requirements(
      Category.crafting, IItems.铱板, 55, IItems.高碳钢, 230, IItems.石英玻璃, 30, IItems.铬锭, 80, IItems.单晶硅, 80
    )
    setDrawMulti(DrawRegion("-bottom"), DrawLiquidTile(Liquids.water), DrawCultivator().apply {
      plantColor = Color.valueOf("A24FAA")
      plantColorLight = Color.valueOf("F9A3C7")
      bottomColor = Color.valueOf("474747")
      bubbles = 12
      sides = 16
      strokeMin = 0f
      spread = 3f
      timeScl = 90f
      recurrence = 6f
      radius = 3f
    }, DrawDefault(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 6.28f
      color = Color.valueOf("F0511D")
    }, DrawRegion("-top"), DrawParticles().apply {
      color = Color.valueOf("F9A3C7")
      alpha = 0.6f
      particles = 15
      particleLife = 300f
      particleRad = 12f
      particleSize = 2f
      fadeMargin = 0f
      rotateScl = 360f
      reverse = true
    })
    bundle {
      desc(zh_CN, "铈提取器", "在特制的高压反应釜内,通过液相沉淀的方式从钍中提取铈")
    }
  }
  val 增压铈萃取器 = CeriumExtractor("ceriumExtractorLarge").apply {
    size = 4
    itemCapacity = 48
    liquidCapacity = 48f
    consumePower(13.85f)
    consumeItems(IItems.铈硅石, 7)
    consumeLiquids(ILiquids.异溶质, 36f / 60f)
    craftTime = 35f
    outputItems(IItems.铈锭, 3)
    outputLiquids(ILiquids.废水, 30f / 60f)
    requirements(
      Category.crafting, IItems.铬锭, 185, IItems.铱板, 100, IItems.石英玻璃, 45, IItems.铱板, 120, IItems.导能回路, 80, IItems.铈锭, 55
    )
    setDrawMulti(DrawRegion("-bottom"), DrawLiquidTile(Liquids.water), DrawCultivator().apply {
      plantColor = Color.valueOf("A24FAA")
      plantColorLight = Color.valueOf("F9A3C7")
      bottomColor = Color.valueOf("474747")
      bubbles = 12
      sides = 16
      strokeMin = 0f
      spread = 4f
      timeScl = 90f
      recurrence = 6f
      radius = 5f
    }, DrawDefault(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 6.28f
      color = Color.valueOf("F0511D")
    }, DrawRegion("-top"), DrawParticles().apply {
      color = Color.valueOf("F9A3C7")
      alpha = 0.6f
      particles = 15
      particleLife = 300f
      particleRad = 16f
      particleSize = 3f
      fadeMargin = 0f
      rotateScl = 360f
      reverse = true
    })
    bundle {
      desc(
        zh_CN, "增压铈萃取器", "在特制的超高压密封反应釜内,通过液相沉淀的方式萃取铈\n相较初代密封性更强,具有更高的压力,能够更迅速的萃取铈"
      )
    }
  }
  val 导能回路装配器 = GenericCrafter("conductiveCircuitAssembler").apply {
    size = 5
    armor = 4f
    itemCapacity = 60
    canOverdrive = false
    consumePower(15.25f)
    consumeItems(IItems.单晶硅, 9, IItems.铪锭, 3)
    outputItems(IItems.导能回路, 6)
    updateEffect = Fx.mineBig
    craftTime = 120f
    craftEffect = WaveEffect().apply {
      lifetime = 180f
      sizeTo = 40f
      strokeFrom = 5f
      interp = Interp.circleOut
      colorFrom = Color.valueOf("B7B9C2")
      colorTo = Color.valueOf("B7B9C280")
    }
    setDrawMulti(DrawDefault(), DrawFlame().apply {
      flameColor = Color.valueOf("B7B9C2")
      lightRadius = 60f
      lightAlpha = 0.6f
      lightSinScl = 9.424778f
      lightSinMag = 6.2831855f
      flameRadius = 5f
      flameRadiusIn = 2f
      flameRadiusScl = 9.424778f
      flameRadiusMag = 3f
      flameRadiusInMag = 1.5f
    })
    ambientSound = Sounds.explosion
    ambientSoundVolume = 0.08f
    requirements(Category.crafting, IItems.铱板, 140, IItems.单晶硅, 50, IItems.铪锭, 30, IItems.铬锭, 100)

    bundle {
      desc(
        zh_CN, "导能回路装配器", "持续开启相位时间场,减缓局部时间以同时进行多种精密零件的制作", "[#9B929D]为什么总有人管她叫灵魂熔炉[]"
      )
    }
  }
  val 高速粉碎机 = GenericCrafter("highSpeedCrusher").apply {
    size = 2
    itemCapacity = 24
    craftEffect = Fx.pulverize
    updateEffect = Fx.pulverizeSmall
    consumePower(1f)
    craftTime = 10f
    consumeItems(IItems.黄玉髓, 2)
    outputItems(IItems.金珀沙, 4)
    setDrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate").apply {
      spinSprite = true
      rotateSpeed = 15f
    }, DrawDefault())
    ambientSound = Sounds.loopGrind
    ambientSoundVolume = 0.025f
    requirements(Category.crafting, IItems.高碳钢, 100, IItems.铜锭, 50, IItems.铅锭, 30, IItems.低碳钢, 20)
    bundle {
      desc(zh_CN, "高速粉碎机")
    }
  }
  val 钴钢压缩机 = GenericCrafter("cobaltSteelCompressor").apply {
    size = 3
    hasLiquids = true
    squareSprite = false
    itemCapacity = 36
    liquidCapacity = 36f
    consumePower(7.5f)
    craftTime = 36f
    craftEffect = IceEffects.square(IItems.钴钢.color)
    updateEffect = Fx.plasticburn
    consumeLiquids(ILiquids.异溶质, 20f / 60f)
    outputItems(IItems.钴钢, 3)
    consumeItems(IItems.钴锭, 4, IItems.铬锭, 2)
    requirements(Category.crafting, IItems.高碳钢, 150, IItems.铬锭, 100, IItems.锌锭, 50)
    setDrawMulti(DrawRegion("-bottom"), DrawPistons().apply {
      sinMag = 2.75f
      sinScl = 3f
      sides = 8
      sideOffset = 1.5707964f
    }, DrawDefault(), DrawRegion("-mid"), DrawLiquidRegion(Liquids.oil), DrawFade(), DrawGlowRegion().apply {
      alpha = 1f
      glowScale = 5.652f
      color = Color.valueOf("F0511D")
    })
    bundle {
      desc(zh_CN, "钴钢压缩机")
    }
  }
  val 陶钢熔炼炉 = GenericCrafter("ceramicSteelFurnace").apply {
    size = 3
    itemCapacity = 36
    consumePower(7.75f)
    consumeItems(IItems.石英玻璃, 1, IItems.钴钢, 1, IItems.铈锭, 1)
    outputItems(IItems.陶钢, 1)
    craftTime = 60f
    canOverdrive = false
    updateEffect = Fx.melting
    requirements(Category.crafting, IItems.铬锭, 130, IItems.钴钢, 45, IItems.铱板, 55, IItems.导能回路, 45)
    setDrawMulti(DrawDefault(), DrawFlame())
    ambientSound = ISounds.beamLoop
    ambientSoundVolume = 0.02f
    bundle {
      desc(zh_CN, "陶钢熔炼炉", "使用多种原料熔炼一种前所未见的多功能装甲材料-陶钢")
    }
  }
  val 高能陶钢聚合炉 = GenericCrafter("highEnergyCeramicSteelFurnace").apply {
    size = 5
    dumpTime = 2
    itemCapacity = 120
    liquidCapacity = 60f
    consumePower(23.8f)
    consumeItems(IItems.钴钢, 12, IItems.铈锭, 12, IItems.石英玻璃, 12)
    consumeLiquids(ILiquids.异溶质, 30 / 60f)
    outputItems(IItems.陶钢, 12)
    craftTime = 240f
    canOverdrive = false
    updateEffect = Fx.redgeneratespark
    craftEffect = MultiEffect(RadialEffect().apply {
      effect = Fx.surgeCruciSmoke
      rotationSpacing = 90f
      rotationOffset = 30f
      lengthOffset = 17f
      amount = 4
    }, RadialEffect().apply {
      effect = Fx.surgeCruciSmoke
      rotationSpacing = 90f
      rotationOffset = 60f
      lengthOffset = 17f
      amount = 4
    })
    requirements(Category.crafting, IItems.钴钢, 230, IItems.铱板, 115, IItems.导能回路, 85, IItems.陶钢, 45)
    setDrawMulti(DrawRegion("-bottom"), DrawCircles().apply {
      color = Color.valueOf("FEB380")
      amount = 3
      sides = 16
      strokeMax = 2f
      strokeMin = 1f
      timeScl = 240f
      radius = 11f
      radiusOffset = 8f
      strokeInterp = Interp.pow3In
    }, DrawMultiWeave().apply {
      glowColor = Color.valueOf("FF6666CD")
      fadeWeave = true
    }, DrawDefault(), DrawGlowRegion().apply {
      color = Color.valueOf("FF664D")
      alpha = 0.8f
      glowIntensity = 1f
      glowScale = 18.84f
    }, DrawGlowRegion("-heat").apply {
      color = Color.valueOf("F0511D")
      alpha = 0.8f
      glowIntensity = 1f
      glowScale = 4.71f
    })
    ambientSound = ISounds.beamLoop
    ambientSoundVolume = 0.03f
    bundle {
      desc(
        zh_CN, "高能陶钢聚合炉", "依靠高能激光持续熔融原料以快速熔炼陶钢\n相比普通熔炼炉,熔炼效率及产物质量都有显著提升"
      )
    }
  }
  val 铈凝块混合器 = GenericCrafter("ceriumBlockMixer").apply {
    size = 2
    itemCapacity = 36
    craftEffect = IceEffects.square(IItems.铈凝块.color)
    consumePower(2f)
    consumeItems(IItems.爆炸化合物, 3, IItems.铈锭, 2)
    craftTime = 90f
    outputItems(IItems.铈凝块, 2)
    requirements(Category.crafting, IItems.铬锭, 80, IItems.铪锭, 60, IItems.铈锭, 50, IItems.单晶硅, 35)
    setDrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate").apply {
      rotateSpeed = 3f
    }, DrawDefault(), DrawRegion("-top"))
    bundle {
      desc(zh_CN, "铈凝块混合器", "在特制的防静电车间内,研磨铈并与爆炸混合物混合后压制成型")
    }
  }

  var SpliceCrafterBuild.highlight: Boolean by AttachedProperty(false)
  var 裂变编织器 = NormalCrafter("fission_weaver").apply {
    bundle {
      desc(zh_CN, "裂变编织器", "使用相控阵辐照压印技术,在有辐射源的情况下将铀的同位素压印为相位物")
    }
    requirements(
      Category.crafting, IItems.FEX水晶, 50, Items.phaseFabric, 60, IItems.强化合金, 50, Items.plastanium, 45, IItems.单晶硅, 70
    )
    size = 4
    oneOfOptionCons = true
    itemCapacity = 24

    newConsume()
    consume!!.time(90f)
    consume!!.power(2.5f)
    consume!!.items(
      *ItemStack.with(
        IItems.单晶硅, 4, IItems.铀238, 1
      )
    )
    consume!!.consValidCondition { e: NormalCrafterBuild? -> e!!.statusi > 0 }
    newProduce()
    produce!!.item(Items.phaseFabric, 6)

    craftEffect = Fx.smeltsmoke
    val recipe = Cons { item: Item? ->
      newOptionalConsume({ e: NormalCrafterBuild, c: BaseConsumers ->
        e.statusi = 2
      }, { s: Stats?, c: BaseConsumers? ->
        s!!.add(SglStat.effect) { t: Table? -> t!!.add(Core.bundle.get("misc.doConsValid")) }
      })?.overdriveValid(false)
      consume!!.item(item!!, 1)
      consume!!.time(180f)
      consume!!.power(0.4f)
      consume!!.optionalAlwaysValid = true
    }
    recipe.get(IItems.铀235)
    recipe.get(IItems.钚239)

    buildType = Prov {
      object : NormalCrafterBuild() {
        override fun updateTile() {
          super.updateTile()
          statusi = if (statusi > 0) statusi - 1 else 0
        }

        override fun status(): BlockStatus? {
          val status = super.status()
          if (status == BlockStatus.noInput && statusi > 0) return BlockStatus.noOutput
          return status
        }
      }
    }

    draw = DrawMulti(DrawBottom(), object : DrawWeave() {
      override fun load(block: Block) {
        weave = Core.atlas.find(block.name + "_top")
      }
    }, DrawDefault(), object : DrawBlock() {
      override fun draw(build: Building) {
        val e = build as NormalCrafterBuild
        Draw.color(SglDrawConst.winter, e.workEfficiency() * (0.4f + Mathf.absin(6f, 0.15f)))
        SglDraw.gradientCircle(e.x, e.y, 8f, 10f, 0f)
        SglDraw.gradientCircle(e.x, e.y, 8f, -4f, 0f)
      }
    })
  }
  var 绿藻池 = object : SpliceCrafter("culturing_barn") {
    init {
      bundle {
        desc(zh_CN, "绿藻池", "使用光水培养低等的藻类生物,除氧气外,还能收获不少藻泥")
      }
      requirements(
        Category.production, ItemStack.with(
          Items.copper, 10, IItems.石英玻璃, 12, Items.graphite, 8
        )
      )
      hasLiquids = true
      negativeSplice = true

      newConsume()
      consume!!.liquid(Liquids.water, 0.02f)
      newProduce()
      produce!!.liquids(
        *LiquidStack.with(
          Liquids.ozone, 0.01f, ILiquids.藻泥, 0.006f
        )
      )


      structUpdated = Cons { e: SpliceCrafterBuild ->
        val right = e.nearby(0)
        val top = e.nearby(1)
        val left = e.nearby(2)
        val bottom = e.nearby(3)
        e.highlight = (right !is SpliceCrafterBuild || right.chains.container !== e.chains.container) && (top !is SpliceCrafterBuild || top.chains.container !== e.chains.container) && (left is SpliceCrafterBuild && left.chains.container === e.chains.container) && (bottom is SpliceCrafterBuild && bottom.chains.container === e.chains.container)

      }

      draw = DrawMulti(DrawBottom(), object : DrawBlock() {
        val rand: Rand = Rand()
        val drawID: Int = SglDraw.nextTaskID()

        override fun draw(build: Building) {
          Draw.z(Draw.z() + 0.001f)
          val cap = build.block.liquidCapacity
          val drawCell = Cons { b: Building? ->

            val alp = max(b!!.warmup(), 0.7f * b.liquids.get(ILiquids.藻泥) / cap)
            if (alp <= 0.01f) return@Cons

            rand.setSeed(b.id.toLong())
            val am = (1 + rand.random(3) * b.warmup()).toInt()
            val move = 0.2f * Mathf.sinDeg(Time.time + rand.random(360f)) * b.warmup()
            Draw.color(ILiquids.藻泥.color)
            Draw.alpha(alp)
            Angles.randLenVectors(b.id.toLong(), am, 3.5f) { dx: Float, dy: Float ->
              Fill.circle(
                b.x + dx + move, b.y + dy + move, (rand.random(0.2f, 0.8f) + Mathf.absin(5f, 0.1f)) * max(b.warmup(), b.liquids.get(ILiquids.藻泥) / cap)
              )
            }
            Draw.reset()
          }

          SglDraw.drawTask(drawID, build, SglShaders.boundWater) { e: Building ->
            drawCell.get(e)
            Draw.alpha(0.75f * (e.liquids.get(Liquids.water) / cap))
            Draw.rect(Blocks.water.region, e.x, e.y)
          }
        }
      }, object : DrawAntiSpliceBlock<SpliceCrafterBuild>() {
        init {
          planSplicer = Boolf2 { plan: BuildPlan, other: BuildPlan ->
            if (plan.block is SpliceCrafter && other.block is SpliceCrafter) {
              val block = plan.block as SpliceCrafter
              val otherBlock = other.block as SpliceCrafter
              return@Boolf2 block.chainable(otherBlock) && otherBlock.chainable(block)
            } else return@Boolf2 false

          }
          splicer = Intf { it.splice }
          layerRec = false
        }
      }, object : DrawRegionDynamic<SpliceCrafterBuild>("_highlight") {
        init {
          alpha = Floatf { e: SpliceCrafterBuild -> if (e.highlight) 1f else 0f }
        }
      })

      buildType = Prov {
        object : SpliceCrafterBuild() {
          var efficiency: Float = 0f

          override fun efficiency(): Float {
            return super.efficiency() * efficiency
          }

          override fun updateTile() {
            super.updateTile()

            efficiency = if (enabled) Mathf.maxZero(
              Attribute.light.env() + (if (Vars.state.rules.lighting) 1f - Vars.state.rules.ambientLight.a else 1f)
            ) else 0f
          }
        }
      }
    }

    override fun setBars() {
      super.setBars()
      addBar("efficiency") { entity: SglBuilding? ->
        Bar({ Core.bundle.format("bar.efficiency", (entity!!.efficiency() * 100).toInt()) }, { Pal.lightOrange }, { entity!!.efficiency() })
      }
    }
  }
  var 育菌箱 = object : FloorCrafter("incubator") {
    init {
      bundle {
        desc(zh_CN, "育菌箱", "从最原始的孢子培养技术发展而来的更高效的生物质培育设备")
      }
      requirements(
        Category.production, ItemStack.with(
          Items.plastanium, 85, IItems.铬锭, 90, IItems.气凝胶, 80, Items.copper, 90
        )
      )
      size = 3
      liquidCapacity = 20f

      newConsume()
      consume!!.time(45f)
      consume!!.power(2.2f)
      consume!!.liquids(
        *LiquidStack.with(
          Liquids.water, 0.4f, ILiquids.孢子云, 0.1f
        )
      )
      newProduce()
      produce!!.item(Items.sporePod, 3)

      newConsume()
      consume!!.time(30f)
      consume!!.power(2.2f)
      consume!!.liquids(
        *LiquidStack.with(
          ILiquids.纯净水, 0.3f, ILiquids.孢子云, 0.1f
        )
      )
      newProduce()
      produce!!.item(Items.sporePod, 3)

      newBooster(1f)
      consume!!.add(
        SglConsumeFloor<FloorCrafterBuild>(
          true, true, arrayOf<Any>(
            Attribute.heat, 0.22f, Attribute.spores, 0.36f
          )
        )
      )

      draw = DrawMulti(
        DrawBottom(), object : DrawCultivator() {
          override fun load(block: Block) {
            middle = Core.atlas.find(block.name + "_middle")
          }
        }, DrawDefault(), DrawRegion("_top")
      )
    }
  }
  var 电解机 = NormalCrafter("electrolytor").apply {
    bundle {
      desc(zh_CN, "电解机", "内置了几组电极以进行一系列电化学反应,将材料电解为一些有用的东西")
    }
    requirements(
      Category.crafting, IItems.铬锭, 80, Items.copper, 100, IItems.铅锭, 80, IItems.单晶硅, 50, IItems.石英玻璃, 60, Items.plastanium, 35
    )
    size = 3
    itemCapacity = 25
    liquidCapacity = 40f

    newConsume().apply {
      liquid(Liquids.water, 0.6f)
      power(6f)
    }
    newProduce().apply {
      liquids(
        *LiquidStack.with(
          Liquids.ozone, 0.6f, Liquids.hydrogen, 0.8f
        )
      )
    }

    newConsume()
    consume!!.liquid(ILiquids.纯净水, 0.4f)
    consume!!.power(5.8f)
    newProduce()
    produce!!.liquids(
      *LiquidStack.with(
        Liquids.ozone, 0.6f, Liquids.hydrogen, 0.8f
      )
    )

    newConsume()
    consume!!.time(120f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.复合矿物溶液, 0.4f, ILiquids.碱液, 0.2f
      )
    )
    consume!!.item(IItems.絮凝剂, 2)
    consume!!.power(3.5f)
    newProduce()
    produce!!.items(
      *ItemStack.with(
        IItems.铝, 4, IItems.铅锭, 3, IItems.铬锭, 1, Items.thorium, 2
      )
    )

    newConsume()
    consume!!.time(60f)
    consume!!.liquid(ILiquids.纯净水, 0.4f)
    consume!!.item(IItems.碱石, 1)
    consume!!.power(3f)
    newProduce()
    produce!!.liquids(
      *LiquidStack.with(
        ILiquids.碱液, 0.4f, ILiquids.氯气, 0.6f
      )
    )

    newConsume()
    consume!!.item(Items.sporePod, 1)
    consume!!.liquid(Liquids.water, 0.2f)
    consume!!.power(2.8f)
    consume!!.time(60f)
    newProduce()
    produce!!.liquid(ILiquids.孢子云, 0.4f)

    newConsume()
    consume!!.item(IItems.绿藻块, 1)
    consume!!.liquid(Liquids.water, 0.2f)
    consume!!.time(120f)
    consume!!.power(2.5f)
    newProduce()
    produce!!.item(IItems.绿藻素, 1)

    draw = DrawMulti(
      DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building?) {
        val e = build as NormalCrafterBuild
        if (e.consumer.current == null) return
        val l = e.consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!![0].liquid
        LiquidBlock.drawTiledFrames(size, e.x, e.y, 4f, l, e.liquids.get(l) / liquidCapacity)
      }
    }, object : DrawDyColorCultivator<NormalCrafterBuild>() {
      init {
        spread = 4f
        plantColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
        bottomColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
        plantColorLight = Func { e: NormalCrafterBuild? -> Color.white }
      }
    }, DrawDefault()
    )
  }
  var 渗透分离槽 = NormalCrafter("osmotic_separation_tank").apply {
    bundle {
      desc(zh_CN, "渗透分离槽", "内置加压可控粒径反渗透过滤器,用于进行一些需要分离颗粒的反应工")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 60, IItems.铅锭, 90, Items.graphite, 100, IItems.石英玻璃, 80, IItems.单晶硅, 70
      )
    )
    size = 3

    itemCapacity = 20
    liquidCapacity = 40f

    newConsume()
    consume!!.time(60f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.碱液, 0.2f, ILiquids.铀盐溶液, 0.2f, Liquids.ozone, 0.2f
      )
    )
    consume!!.item(IItems.絮凝剂, 1)
    consume!!.power(1.2f)
    newProduce()
    produce!!.item(IItems.铀原料, 2)

    newConsume()
    consume!!.time(120f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.酸液, 0.2f, Liquids.ozone, 0.4f
      )
    )
    consume!!.item(IItems.铱金混合物, 1)
    consume!!.power(1.2f)
    newProduce()
    produce!!.item(IItems.氯铱酸盐, 1)

    newConsume()
    consume!!.time(90f)
    consume!!.liquid(ILiquids.藻泥, 0.4f)
    consume!!.power(1f)
    newProduce()
    produce!!.item(IItems.绿藻块, 1)
    produce!!.liquid(ILiquids.纯净水, 0.2f)

    draw = DrawMulti(
      DrawBottom(), object : DrawBlock() {
        override fun draw(build: Building?) {
          val e = build as NormalCrafterBuild
          if (e.consumer.current == null) return
          val l = e.consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!![0].liquid
          LiquidBlock.drawTiledFrames(size, e.x, e.y, 4f, l, e.liquids.get(l) / liquidCapacity)
        }
      }, DrawDefault()
    )
  }
  var 反应仓 = NormalCrafter("reacting_pool").apply {
    bundle {
      desc(zh_CN, "反应仓", "一个精准控制进料的化学反应容器,是普遍使用的化工设备")
    }
    requirements(
      Category.crafting, IItems.铬锭, 100, IItems.石英玻璃, 100, IItems.铅锭, 80, Items.graphite, 85, IItems.单晶硅, 80, Items.plastanium, 7
    )
    size = 3

    itemCapacity = 35
    liquidCapacity = 45f

    newConsume()
    consume!!.time(60f)
    consume!!.item(IItems.黑晶石, 3)
    consume!!.liquid(ILiquids.酸液, 0.2f)
    consume!!.power(0.8f)
    newProduce()
    produce!!.liquid(ILiquids.复合矿物溶液, 0.4f)

    newConsume()
    consume!!.time(60f)
    consume!!.liquid(ILiquids.酸液, 0.2f)
    consume!!.item(IItems.铀原矿, 1)
    consume!!.power(0.8f)
    newProduce()
    produce!!.liquid(ILiquids.铀盐溶液, 0.2f)

    newConsume()
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.纯净水, 0.4f, ILiquids.二氧化硫, 0.4f, ILiquids.氯气, 0.2f
      )
    )
    consume!!.power(0.6f)
    newProduce()
    produce!!.liquid(ILiquids.酸液, 0.6f)

    newConsume()
    consume!!.time(120f)
    consume!!.items(
      *ItemStack.with(
        IItems.单晶硅, 2, IItems.絮凝剂, 1
      )
    )
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.纯净水, 0.4f, ILiquids.氯气, 0.2f
      )
    )
    consume!!.power(1.5f)
    newProduce()
    produce!!.liquids(
      *LiquidStack.with(
        ILiquids.氯化硅溶胶, 0.4f, ILiquids.酸液, 0.2f
      )
    )

    newConsume()
    consume!!.time(60f)
    consume!!.item(IItems.铝, 1)
    consume!!.liquid(ILiquids.碱液, 0.2f)
    consume!!.power(1f)
    newProduce()
    produce!!.item(IItems.絮凝剂, 2)

    newConsume()
    consume!!.time(30f)
    consume!!.item(Items.coal, 1)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.酸液, 0.2f, ILiquids.孢子云, 0.3f
      )
    )
    consume!!.power(1f)
    newProduce()
    produce!!.item(Items.blastCompound, 1)

    draw = DrawMulti(
      DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building?) {
        val e = build as NormalCrafterBuild
        if (e.consumer.current == null || e.producer!!.current == null) return
        val l = e.consumer.current!!.get(ConsumeType.liquid)!!.consLiquids!![0].liquid
        val region = Vars.renderer.fluidFrames[if (l.gas) 1 else 0][l.animationFrame]
        val toDraw = Tmp.tr1
        val bounds = size / 2f * Vars.tilesize - 3
        val color = Tmp.c1.set(l.color).a(1f).lerp(e.producer!!.current!!.color, e.warmup())

        for (sx in 0..<size) {
          for (sy in 0..<size) {
            val relx = sx - (size - 1) / 2f
            val rely = sy - (size - 1) / 2f

            toDraw.set(region)
            //truncate region if at border
            val rightBorder = relx * Vars.tilesize + 3
            val topBorder = rely * Vars.tilesize + 3
            val squishX = rightBorder + Vars.tilesize / 2f - bounds
            val squishY = topBorder + Vars.tilesize / 2f - bounds
            var ox = 0f
            var oy = 0f

            if (squishX >= 8 || squishY >= 8) continue
            //cut out the parts that don't fit inside the padding
            if (squishX > 0) {
              toDraw.setWidth(toDraw.width - squishX * 4f)
              ox = -squishX / 2f
            }

            if (squishY > 0) {
              toDraw.setY(toDraw.y + squishY * 4f)
              oy = -squishY / 2f
            }

            Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, max(e.warmup(), e.liquids.get(l) / liquidCapacity), color)
          }
        }
      }
    }, object : DrawDyColorCultivator<NormalCrafterBuild?>() {
      init {
        spread = 4f
        plantColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
        bottomColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
        plantColorLight = Func { e: NormalCrafterBuild? -> Color.white }
      }
    }, DrawDefault()
    )
  }
  var 燃烧室 = NormalCrafter("combustion_chamber").apply {
    bundle {
      desc(zh_CN, "反应仓", "一个精准控制进料的化学反应容器,是普遍使用的化工设备","密闭耐高温的舱室,用于执行化学燃烧过程,为最大化利用燃烧释放的能量,燃烧会将在活塞室内进行以推动线圈产生电力")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 90, Items.graphite, 80, IItems.石英玻璃, 80, IItems.单晶硅, 75
      )
    )
    size = 3
    liquidCapacity = 40f
    itemCapacity = 25


    newConsume()
    consume!!.liquid(Liquids.hydrogen, 0.8f)
    newProduce()
    produce!!.liquid(ILiquids.纯净水, 0.4f)
    produce!!.power(5f)

    newConsume()
    consume!!.time(120f)
    consume!!.item(Items.pyratite, 1)
    newProduce()
    produce!!.liquid(ILiquids.二氧化硫, 0.4f)
    produce!!.power(4.5f)

    newBooster(2.65f)
    consume!!.liquid(Liquids.ozone, 0.4f)

    draw = DrawMulti(
      DrawBottom(), DrawCrucibleFlame(), DrawDefault()
    )
  }
  var 真空坩埚 = NormalCrafter("vacuum_crucible").apply {
    bundle {
      desc(zh_CN, "真空坩埚", "在低压高温环境下进行特殊工序时使用的设备")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 90, IItems.单晶硅, 80, Items.plastanium, 60, IItems.石英玻璃, 75, Items.graphite, 80
      )
    )
    size = 3



    liquidCapacity = 45f
    itemCapacity = 30

    newConsume()
    consume!!.time(60f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.氯化硅溶胶, 0.2f, Liquids.hydrogen, 0.4f
      )
    )
    consume!!.item(Items.sand, 5)
    consume!!.power(2f)
    newProduce()
    produce!!.item(
      IItems.单晶硅, 8
    )

    newConsume()
    consume!!.time(120f)
    consume!!.liquid(ILiquids.氯化硅溶胶, 0.4f)
    consume!!.item(IItems.石英玻璃, 10)
    consume!!.power(1.8f)
    newProduce()
    produce!!.item(IItems.气凝胶, 5)

    newConsume()
    consume!!.time(120f)
    consume!!.item(IItems.绿藻块, 1)
    consume!!.liquid(ILiquids.酸液, 0.2f)
    consume!!.power(1.6f)
    newProduce()
    produce!!.item(IItems.絮凝剂, 1)

    newConsume()
    consume!!.time(120f)
    consume!!.item(Items.sporePod, 1)
    consume!!.liquid(ILiquids.碱液, 0.2f)
    consume!!.power(1.6f)
    newProduce()
    produce!!.item(IItems.絮凝剂, 1)

    draw = DrawMulti(
      DrawBottom(), DrawCrucibleFlame(), DrawDefault()
    )
  }
  var 热能冶炼炉 = NormalCrafter("thermal_smelter").apply {
    bundle {
      desc(zh_CN, "热能冶炼炉", "用于冶炼金属的设备,可以制造气流进行金属化合物的高温煅烧")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 65, IItems.单晶硅, 70, Items.copper, 60, Items.graphite, 60, Items.plastanium, 70
      )
    )
    size = 3

    itemCapacity = 20


    newConsume()
    consume!!.time(90f)
    consume!!.items(
      *ItemStack.with(
        IItems.铬锭, 3, Items.thorium, 2, IItems.焦炭, 1
      )
    )
    consume!!.liquid(ILiquids.氯化硅溶胶, 0.2f)
    consume!!.power(2.6f)
    newProduce()
    produce!!.item(IItems.强化合金, 4)

    newConsume()
    consume!!.time(120f)
    consume!!.items(
      *ItemStack.with(
        IItems.氯铱酸盐, 1, IItems.焦炭, 2
      )
    )
    consume!!.liquid(Liquids.hydrogen, 0.4f)
    consume!!.power(3f)
    newProduce()
    produce!!.item(IItems.铱锭, 2)

    draw = DrawMulti(DrawBottom(), object : DrawBlock() {
      val flameColor: Color = Color.valueOf("f58349")

      override fun draw(build: Building) {
        val base = (Time.time / 70)
        Draw.color(flameColor, 0.5f)
        DrawBlock.rand.setSeed(build.id.toLong())
        for (i in 0..34) {
          val fin = (DrawBlock.rand.random(1f) + base) % 1f
          val angle = DrawBlock.rand.random(360f) + (Time.time / 1.5f) % 360f
          val len = 10 * Mathf.pow(fin, 1.5f)
          Draw.alpha(0.5f * build.warmup() * (1f - Mathf.curve(fin, 1f - 0.4f)))
          Fill.circle(
            build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), 3 * fin * build.warmup()
          )
        }

        Draw.blend()
        Draw.reset()
      }
    }, DrawDefault(), object : DrawFlame() {
      init {
        flameRadius = 2f
        flameRadiusScl = 4f
      }

      override fun load(block: Block) {
        top = Core.atlas.find(block.name + "_top")
        block.clipSize = max(block.clipSize, (lightRadius + lightSinMag) * 2f * block.size)
      }
    })
  }
  var 干馏塔 = NormalCrafter("retort_column").apply {
    bundle {
      desc(zh_CN, "干馏塔", "通过隔绝空气的高温分离煤炭中的物质,以制造焦炭")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 70, Items.graphite, 75, Items.copper, 90, IItems.石英玻璃, 90, Items.plastanium, 50
      )
    )
    size = 3
    itemCapacity = 12
    liquidCapacity = 20f



    newConsume()
    consume!!.time(90f)
    consume!!.power(2f)
    consume!!.item(Items.coal, 3)
    newProduce()
    produce!!.items(
      *ItemStack.with(
        Items.pyratite, 1, IItems.焦炭, 1
      )
    )

    craftEffect = Fx.smeltsmoke

    draw = DrawMulti(
      DrawDefault(), object : DrawFlame() {
        override fun load(block: Block) {
          top = Core.atlas.find(block.name + "_top")
          block.clipSize = max(block.clipSize, (lightRadius + lightSinMag) * 2f * block.size)
        }
      })
  }
  var 激光解离机 = NormalCrafter("laser_resolver").apply {
    bundle {
      desc(zh_CN, "激光解离机", "使用不同频段的激光来定向分离物质以得到更加有用的东西")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.FEX水晶, 45, IItems.强化合金, 70, IItems.单晶硅, 90, Items.phaseFabric, 65, IItems.石英玻璃, 120
      )
    )
    size = 3
    itemCapacity = 20
    warmupSpeed = 0.01f


    newConsume()
    consume!!.time(60f)
    consume!!.power(3.2f)
    consume!!.item(IItems.核废料, 1)
    newProduce().color = IItems.核废料.color
    produce!!.items(
      *ItemStack.with(
        IItems.铱金混合物, 2, IItems.铅锭, 5, Items.thorium, 3
      )
    ).random()

    newConsume()
    consume!!.time(30f)
    consume!!.item(Items.scrap, 2)
    consume!!.liquid(Liquids.slag, 0.1f)
    consume!!.power(3.5f)
    newProduce().color = Items.scrap.color
    produce!!.items(
      *ItemStack.with(
        Items.thorium, 3, IItems.铬锭, 4, IItems.铅锭, 5, Items.copper, 3
      )
    ).random()

    newConsume()
    consume!!.time(60f)
    consume!!.item(IItems.黑晶石, 1)
    consume!!.power(2.8f)
    newProduce().color = IItems.黑晶石.color
    produce!!.items(
      *ItemStack.with(
        IItems.铬锭, 2, Items.thorium, 1, IItems.铅锭, 3, IItems.铝, 4
      )
    ).random()


    draw = DrawMulti(
      DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building) {
        val e = build as NormalCrafterBuild
        if (e.producer!!.current == null) return
        val region = Vars.renderer.fluidFrames[0][Liquids.water.animationFrame]
        val toDraw = Tmp.tr1
        val bounds = size / 2f * Vars.tilesize - 3
        val color = e.producer!!.current!!.color

        for (sx in 0..<size) {
          for (sy in 0..<size) {
            val relx = sx - (size - 1) / 2f
            val rely = sy - (size - 1) / 2f

            toDraw.set(region)
            val rightBorder = relx * Vars.tilesize + 3
            val topBorder = rely * Vars.tilesize + 3
            val squishX = rightBorder + Vars.tilesize / 2f - bounds
            val squishY = topBorder + Vars.tilesize / 2f - bounds
            var ox = 0f
            var oy = 0f

            if (squishX >= 8 || squishY >= 8) continue

            if (squishX > 0) {
              toDraw.setWidth(toDraw.width - squishX * 4f)
              ox = -squishX / 2f
            }

            if (squishY > 0) {
              toDraw.setY(toDraw.y + squishY * 4f)
              oy = -squishY / 2f
            }

            Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, e.warmup, color)
          }
        }
        Draw.rect()
      }
    }, DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>("_laser") {
      init {
        rotation = Floatf { e: NormalCrafterBuild? -> e!!.totalProgress * 1.5f }
        alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
      }

      override fun draw(build: Building?) {
        SglDraw.drawBloomUnderBlock(build) { build: Building -> super.draw(build) }
        Draw.z(Layer.block + 5)
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = 1.5f
        spinSprite = true
      }
    }, DrawRegion("_top")
    )
  }
  var 蒸馏净化器 = NormalCrafter("distill_purifier").apply {
    bundle {
      desc(zh_CN, "蒸馏净化器", "用原始的蒸馏方式分离水中的杂质")
    }
    requirements(
      Category.crafting, ItemStack.with(
        Items.copper, 30, IItems.单晶硅, 24, IItems.石英玻璃, 30, Items.graphite, 20
      )
    )
    size = 2
    hasLiquids = true
    liquidCapacity = 30f

    updateEffect = Fx.steam
    updateEffectChance = 0.035f

    newConsume()
    consume!!.time(120f)
    consume!!.liquid(Liquids.water, 0.5f)
    consume!!.power(1f)
    newProduce()
    produce!!.liquid(ILiquids.纯净水, 0.4f)
    produce!!.item(IItems.碱石, 1)

    draw = DrawMulti(
      DrawBottom(), DrawLiquidTile(Liquids.water, 3f), DrawDefault()
    )
  }
  var 渗透净化器 = NormalCrafter("osmotic_purifier").apply {
    bundle {
      desc(zh_CN, "渗透净化器", "使用物质吸附及反渗透过滤技术制造的高效净化装置,能更有效的分离水中的杂质")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铝, 50, Items.graphite, 60, IItems.单晶硅, 45, IItems.铬锭, 45, IItems.气凝胶, 50
      )
    )
    size = 3
    hasLiquids = true
    liquidCapacity = 30f

    newConsume()
    consume!!.time(60f)
    consume!!.liquid(Liquids.water, 2f)
    consume!!.item(Items.graphite, 1)
    consume!!.power(1f)
    newProduce()
    produce!!.liquid(ILiquids.纯净水, 2f)
    produce!!.item(IItems.碱石, 2)

    draw = DrawMulti(
      DrawBottom(), DrawLiquidTile(Liquids.water, 3f), object : DrawBlock() {
        override fun draw(build: Building?) {
          val e = build as NormalCrafterBuild
          val region = Vars.renderer.fluidFrames[0][Liquids.water.animationFrame]
          val toDraw = Tmp.tr1
          val bounds = size / 2f * Vars.tilesize - 8
          val color = ILiquids.纯净水.color

          for (sx in 0..<size) {
            for (sy in 0..<size) {
              val relx = sx - (size - 1) / 2f
              val rely = sy - (size - 1) / 2f

              toDraw.set(region)
              val rightBorder = relx * Vars.tilesize + 8
              val topBorder = rely * Vars.tilesize + 8
              val squishX = rightBorder + Vars.tilesize / 2f - bounds
              val squishY = topBorder + Vars.tilesize / 2f - bounds
              var ox = 0f
              var oy = 0f

              if (squishX >= 8 || squishY >= 8) continue

              if (squishX > 0) {
                toDraw.setWidth(toDraw.width - squishX * 4f)
                ox = -squishX / 2f
              }

              if (squishY > 0) {
                toDraw.setY(toDraw.y + squishY * 4f)
                oy = -squishY / 2f
              }

              Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, e.warmup(), color)
            }
          }
        }
      }, DrawDefault()
    )
  }
  var 洗矿机 = NormalCrafter("ore_washer").apply {
    bundle {
      desc(zh_CN, "洗矿机", "用高速的水流冲刷沥青粗矿以除去轻杂质,以及洗脱附着在岩石间的FEX物质")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.铬锭, 60, Items.graphite, 40, IItems.铅锭, 45, IItems.石英玻璃, 60
      )
    )
    size = 2
    hasLiquids = true
    itemCapacity = 20
    liquidCapacity = 24f


    newConsume()
    consume!!.time(120f)
    consume!!.liquid(Liquids.water, 0.2f)
    consume!!.item(IItems.岩层沥青, 1)
    consume!!.power(1.8f)
    newProduce()
    produce!!.liquid(ILiquids.FEX流体, 0.2f)
    produce!!.items(
      *ItemStack.with(
        Items.sand, 5, IItems.黑晶石, 3, IItems.铀原矿, 2
      )
    ).random()

    craftEffect = Fx.pulverizeMedium

    draw = DrawMulti(DrawDefault(), object : DrawLiquidRegion(Liquids.water) {
      init {
        suffix = "_liquid"
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = 4.5f
        spinSprite = true
      }
    }, DrawRegion("_top"), object : DrawRegionDynamic<NormalCrafterBuild?>("_point") {
      init {
        color = Func { e: NormalCrafterBuild? ->
          val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as SglConsumers).first()
          if (cons is universecore.world.consumers.ConsumeItems<*>) {
            val item = cons.consItems!![0].item
            return@Func item.color
          } else return@Func Color.white
        }
        alpha = Floatf { e: NormalCrafterBuild? ->
          val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as SglConsumers).first()
          if (cons is universecore.world.consumers.ConsumeItems<*>) {
            val item = cons.consItems!![0].item
            return@Floatf e.items.get(item).toFloat() / e.block.itemCapacity
          } else return@Floatf 0f
        }
      }
    })
  }
  var 结晶器 = NormalCrafter("crystallizer").apply {
    bundle {
      desc(zh_CN, "结晶器", "最早的FEX结晶技术,依赖电磁场波动,使FEX在载体金属上逐步形成结晶")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 35, IItems.单晶硅, 45, Items.copper, 40, IItems.石英玻璃, 50
      )
    )
    size = 2
    liquidCapacity = 16f

    newConsume()
    consume!!.time(240f)
    consume!!.item(IItems.强化合金, 1)
    consume!!.liquid(ILiquids.FEX流体, 0.2f)
    consume!!.power(2.8f)
    newProduce()
    produce!!.item(IItems.FEX水晶, 2)

    draw = DrawMulti(
      object : DrawCultivator() {
        init {
          plantColor = Color.valueOf("#C73A3A")
          plantColorLight = Color.valueOf("#E57D7D")
        }

        override fun load(block: Block) {
          middle = Core.atlas.find(block.name + "_middle")
        }
      }, DrawDefault()
    )
  }
  var FEX相位混合器 = NormalCrafter("FEX_phase_mixer").apply {
    bundle {
      desc(zh_CN, "FEX相位混合器", "重建FEX的液态物质结构,使其中的能量活性化")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 40, Items.plastanium, 90, Items.phaseFabric, 85, IItems.单晶硅, 80
      )
    )
    size = 2
    hasLiquids = true
    liquidCapacity = 12f


    newConsume()
    consume!!.time(120f)
    consume!!.item(Items.phaseFabric, 1)
    consume!!.liquid(ILiquids.FEX流体, 0.2f)
    consume!!.power(1.9f)
    newProduce()
    produce!!.liquid(ILiquids.相位态FEX流体, 0.2f)

    draw = DrawMulti(
      DrawBottom(), DrawLiquidTile(ILiquids.FEX流体), object : DrawLiquidTile(ILiquids.相位态FEX流体) {
        init {
          drawLiquidLight = true
        }
      }, DrawDefault(), DrawRegion("_top")
    )
  }
  var 燃料封装机 = NormalCrafter("fuel_packager").apply {
    bundle {
      desc(zh_CN, "燃料封装机", "利用力场固定低温技术制造亚绝对零度环境,将核燃料以极高的浓度和压力压缩封装起来")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 45, Items.phaseFabric, 40, IItems.单晶硅, 45, Items.graphite, 30
      )
    )
    size = 2
    autoSelect = true

    newConsume()
    consume!!.time(120f)
    consume!!.items(*ItemStack.with(IItems.铀235, 2, IItems.强化合金, 1))
    consume!!.power(1.5f)
    newProduce()
    produce!!.item(IItems.浓缩铀235核燃料, 1)
    newConsume()
    consume!!.time(120f)
    consume!!.items(*ItemStack.with(IItems.钚239, 2, IItems.强化合金, 1))
    consume!!.power(1.5f)
    newProduce()
    produce!!.item(IItems.浓缩钚239核燃料, 1)

    craftEffect = Fx.smeltsmoke

    draw = DrawMulti(DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>("_flue") {
      init {
        alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.get(IItems.强化合金) > 0 || e.progress() > 0.4f) 1f else 0f }
      }
    }, object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
      // 正确的写法
      init {
        alpha = Floatf<NormalCrafterBuild?> { it?.progress() ?: 0f }
        color = Func { e: NormalCrafterBuild? -> if (e!!.producer!!.current != null) e.producer!!.current!!.color else SglDrawConst.transColor }
      }
    })
  }
  var 气体相位封装机 = NormalCrafter("gas_phase_packer").apply {
    bundle {
      desc(zh_CN, "气体相位封装机", "用于将一份流体用相位物封装成中子靶丸,以进一步转变为核聚变所使用的燃料")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 80, IItems.气凝胶, 80, Items.phaseFabric, 60, IItems.单晶硅, 60, Items.graphite, 45
      )
    )
    size = 3

    hasLiquids = true
    liquidCapacity = 32f
    itemCapacity = 24

    warmupSpeed = 0.01f

    newConsume()
    consume!!.time(240f)
    consume!!.power(1.4f)
    consume!!.items(
      *ItemStack.with(
        Items.phaseFabric, 2, IItems.气凝胶, 2
      )
    )
    consume!!.liquid(Liquids.hydrogen, 0.4f)
    newProduce()
    produce!!.item(IItems.相位封装氢单元, 1)

    newConsume()
    consume!!.time(240f)
    consume!!.power(1.4f)
    consume!!.items(
      *ItemStack.with(
        Items.phaseFabric, 2, IItems.气凝胶, 2
      )
    )
    consume!!.liquid(ILiquids.氦气, 0.4f)
    newProduce()
    produce!!.item(IItems.相位封装氦单元, 1)

    draw = DrawMulti(
      DrawBottom(), DrawLiquidTile(), object : DrawBlock() {
      var piston: TextureRegion? = null

      override fun draw(build: Building) {

        for (i in 0..3) {
          val len = Mathf.absin(build.totalProgress() + 90 * i, 4f, 4f)
          val angle = i * 360f / 4

          Draw.rect(piston, build.x + Angles.trnsx(angle + 225, len), build.y + Angles.trnsy(angle + 225, len), angle)
        }
      }

      override fun load(block: Block) {
        piston = Core.atlas.find(block.name + "_piston")
      }
    }, object : DrawLiquidRegion() {
      init {
        suffix = "_liquid"
      }
    }, DrawDefault()
    )
  }
  var 热能离心机 = NormalCrafter("thermal_centrifuge").apply {
    bundle {
      desc(zh_CN, "热能离心机", "以极高的温度将物质熔化成液态,以差速离心分离其中不同质量的物质")
    }
    requirements(
      Category.crafting, IItems.强化合金, 100, IItems.气凝胶, 80, Items.copper, 120, IItems.单晶硅, 70, Items.plastanium, 75
    )
    size = 3
    itemCapacity = 28

    warmupSpeed = 0.006f

    newConsume()
    consume!!.time(120f)
    consume!!.item(IItems.铀原料, 4)
    consume!!.power(3.8f)
    newProduce().color = IItems.铀原料.color
    produce!!.items(*ItemStack.with(IItems.铀238, 3, IItems.铀235, 1))

    newConsume()
    consume!!.time(180f)
    consume!!.item(IItems.铱金混合物, 2)
    consume!!.power(3f)
    newProduce().color = IItems.氯铱酸盐.color
    produce!!.item(IItems.铱锭, 1)

    newConsume()
    consume!!.time(120f)
    consume!!.item(IItems.黑晶石, 5)
    consume!!.power(2.8f)
    setByProduct(0.3f, Items.thorium)
    newProduce().color = IItems.黑晶石.color
    produce!!.items(
      *ItemStack.with(
        IItems.铝, 3, IItems.铅锭, 2
      )
    )

    craftEffect = Fx.smeltsmoke
    updateEffect = Fx.plasticburn

    draw = DrawMulti(DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building?) {
        val e = build as NormalCrafterBuild
        if (e.producer!!.current == null) return

        val region = Vars.renderer.fluidFrames[0][Liquids.slag.animationFrame]
        val toDraw = Tmp.tr1
        val bounds = size / 2f * Vars.tilesize
        val color = Liquids.slag.color

        for (sx in 0..<size) {
          for (sy in 0..<size) {
            val relx = sx - (size - 1) / 2f
            val rely = sy - (size - 1) / 2f

            toDraw.set(region)
            val rightBorder = relx * Vars.tilesize
            val topBorder = rely * Vars.tilesize
            val squishX = rightBorder + Vars.tilesize / 2f - bounds
            val squishY = topBorder + Vars.tilesize / 2f - bounds
            var ox = 0f
            var oy = 0f

            if (squishX >= 8 || squishY >= 8) continue

            if (squishX > 0) {
              toDraw.setWidth(toDraw.width - squishX * 4f)
              ox = -squishX / 2f
            }

            if (squishY > 0) {
              toDraw.setY(toDraw.y + squishY * 4f)
              oy = -squishY / 2f
            }

            Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, e.warmup(), color)
          }
        }
      }
    }, object : DrawRegion("_rim") {
      init {
        rotateSpeed = 0.8f
        spinSprite = true
      }
    }, DrawDefault(), object : DrawRegion("_rotator") {
      init {
        rotateSpeed = 1.8f
        spinSprite = true
      }
    }, object : DrawRegion("_toprotator") {
      init {
        rotateSpeed = -1.2f
      }
    }, object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
      init {
        rotation = Floatf { e: NormalCrafterBuild? -> -e!!.totalProgress() * 1.2f }
        color = Func { e: NormalCrafterBuild? -> if (e!!.producer!!.current != null) e.producer!!.current!!.color else SglDrawConst.transColor }
        alpha = Floatf { e: NormalCrafterBuild? ->
          val cons = if (e!!.consumer.current == null) null else e.consumer.current!!.get(ConsumeType.item)
          val i = if (cons == null) null else cons.consItems!![0].item
          if (cons == null) 0f else (e.items.get(i).toFloat()) / itemCapacity
        }
      }
    })
  }
  var 晶格构建器 = NormalCrafter("lattice_constructor").apply {
    bundle {
      desc(zh_CN, "晶格构建器", "先进的FEX结晶技术,以光束引导和力场聚合的方式人工构建晶格结构,更高效地生产FEX结晶")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 80, IItems.充能FEX水晶, 60, IItems.FEX水晶, 75, Items.phaseFabric, 80
      )
    )
    size = 3

    itemCapacity = 20
    basicPotentialEnergy = 128f

    newConsume()
    consume!!.time(120f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
    consume!!.item(IItems.强化合金, 1)
    consume!!.energy(1.25f)
    newProduce()
    produce!!.item(IItems.FEX水晶, 4)

    craftEffect = SglFx.FEXsmoke



    initialed = Cons { e: SglBlock.SglBuilding ->
      e.drawAlphas = floatArrayOf(2.9f, 2.2f, 1.5f)
    }
    draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_framework") {
      init {
        alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.强化合金) || e.progress() > 0.4f) 1f else 0f }
      }
    }, object : DrawRegionDynamic<NormalCrafterBuild?>() {
      init {
        alpha = Floatf { e: FactoryBuildComp -> e.progress }
      }

      override fun load(block: Block?) {
        region = Singularity.getModAtlas("FEX_crystal")
      }
    }, object : DrawBlock() {
      override fun draw(build: Building?) {
        val e = build as NormalCrafterBuild
        Draw.alpha(e.workEfficiency())
        Lines.lineAngleCenter(
          e.x + Mathf.sin(e.totalProgress(), 6f, Vars.tilesize.toFloat() / 3 * size), e.y, 90f, size.toFloat() * Vars.tilesize / 2
        )
        Draw.color()
      }
    }, DrawDefault(), object : DrawBlock() {
      var wave: TextureRegion? = null

      override fun load(block: Block?) {
        wave = Core.atlas.find(name + "_wave")
      }

      override fun draw(build: Building?) {

        val e = build as NormalCrafterBuild
        val alphas: FloatArray = e.drawAlphas

        Draw.z(Layer.effect)
        for (dist in 2 downTo 0) {
          Draw.color(SglDrawConst.fexCrystal)
          Draw.alpha((if (alphas[dist] <= 1) alphas[dist] else (if (alphas[dist] <= 1.5) 1 else 0).toFloat()) * e.workEfficiency())
          if (e.workEfficiency() > 0) {
            if (alphas[dist] < 0.4) alphas[dist] += 0.6.toFloat()
            for (i in 0..3) {
              Draw.rect(
                wave, e.x + dist * Geometry.d4(i).x * 3 + 5 * (Geometry.d4(i).x.compareTo(0)), e.y + dist * Geometry.d4(i).y * 3 + 5 * (Geometry.d4(i).y.compareTo(0)), ((i + 1) * 90).toFloat()
              )
            }
            alphas[dist] -= (0.02 * e.edelta()).toFloat()
          } else {
            alphas[dist] = 1.5f + 0.7f * (2 - dist)
          }
        }
      }
    })
  }
  var FEX充能座 = NormalCrafter("FEX_crystal_charger").apply {
    bundle {
      desc(zh_CN, "FEX充能座", "对FEX结晶释放高能中子脉冲,合适的脉冲频率会令能量在晶格之内不断积累,叠加,使FEX晶体结构变得不稳定,并带来一些特别的效果")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 70, IItems.FEX水晶, 60, IItems.石英玻璃, 65, Items.phaseFabric, 70, Items.plastanium, 85
      )
    )
    size = 3

    itemCapacity = 15
    basicPotentialEnergy = 128f

    newConsume()
    consume!!.time(90f)
    consume!!.item(IItems.FEX水晶, 1)
    consume!!.energy(2f)
    newProduce()
    produce!!.item(IItems.充能FEX水晶, 1)

    updateEffect = SglFx.neutronWeaveMicro
    updateEffectChance = 0.04f
    updateEffectColor = SglDrawConst.fexCrystal
    craftEffect = SglFx.crystalConstructed
    craftEffectColor = SglDrawConst.fexCrystal



    crafting = Cons { e: NormalCrafterBuild? ->
      if (Mathf.chanceDelta((0.03f * e!!.workEfficiency()).toDouble())) {
        SglFx.shrinkParticleSmall.at(e.x, e.y, SglDrawConst.fexCrystal)
      }
    }

    draw = DrawMulti(DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>() {
      init {
        alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.FEX水晶) || e.progress() > 0.4f) 1f else 0f }
      }

      override fun load(block: Block?) {
        region = Singularity.getModAtlas("FEX_crystal")
      }
    }, object : DrawRegionDynamic<NormalCrafterBuild?>() {
      init {
        layer = Layer.effect
        alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.FEX水晶) || e.progress() > 0.4f) e.progress() else 0f }
      }

      override fun load(block: Block?) {
        region = Singularity.getModAtlas("FEX_crystal_power")
      }
    })
  }
  var 矩阵切割机 = NormalCrafter("matrix_cutter").apply {
    bundle {
      desc(zh_CN, "矩阵切割器", "以纳米尺度的高能激光将金属切割为纳米颗粒,并在上方雕刻微电路,以生产矩阵合金")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 80, IItems.充能FEX水晶, 75, IItems.石英玻璃, 80, Items.phaseFabric, 90, Items.surgeAlloy, 120
      )
    )
    size = 4

    itemCapacity = 20
    basicPotentialEnergy = 256f



    newConsume()
    consume!!.time(120f)
    consume!!.energy(4.85f)
    consume!!.items(
      *ItemStack.with(
        IItems.充能FEX水晶, 1, IItems.强化合金, 2
      )
    )
    consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
    newProduce()
    produce!!.item(IItems.矩阵合金, 1)

    craftEffect = Fx.smeltsmoke

    draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_alloy") {
      init {
        alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.get(IItems.强化合金) >= 2) 1f else 0f }
      }
    }, object : DrawBlock() {
      override fun draw(build: Building) {

        val e = build as NormalCrafterBuild?
        SglDraw.drawBloomUnderBlock<NormalCrafterBuild?>(e) { b: NormalCrafterBuild? ->
          val dx = 5 * Mathf.sinDeg(build.totalProgress() * 1.35f)
          val dy = 5 * Mathf.cosDeg(build.totalProgress() * 1.35f)

          Draw.color(SglDrawConst.fexCrystal)
          Lines.stroke(0.8f * e!!.workEfficiency())

          Lines.line(b!!.x + dx, b.y + 6, b.x + dx, b.y - 6)
          Lines.line(b.x + 6, b.y + dy, b.x - 6, b.y + dy)
        }
        Draw.z(35f)
        Draw.reset()
      }
    }, DrawDefault(), object : DrawBlock() {
      override fun draw(build: Building?) {

        Draw.z(Layer.effect)
        val e = build as NormalCrafterBuild
        val angle = e.totalProgress()
        val realRotA = MathTransform.gradientRotateDeg(angle, 0f, 4)
        val realRotB = MathTransform.gradientRotateDeg(angle, 180f, 4)

        Lines.stroke(1.4f * e.workEfficiency(), SglDrawConst.fexCrystal)
        Lines.square(e.x, e.y, 20 + 4 * Mathf.sinDeg(realRotB), 45 + realRotA)

        Lines.stroke(1.6f * e.workEfficiency())
        Lines.square(e.x, e.y, 20 + 4 * Mathf.sinDeg(realRotA), 45 - realRotB)
      }
    })
  }
  var 中子透镜 = NormalCrafter("neutron_lens").apply {
    bundle {
      desc(zh_CN, "中子透镜", "通过相位物折射及引力透镜偏转中子流进行对焦,使中子直接轰击靶材料,在舱内完成需要高能中子流轰击的过程")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 120, IItems.FEX水晶, 80, IItems.充能FEX水晶, 100, IItems.铱锭, 60, IItems.气凝胶, 120, Items.phaseFabric, 90
      )
    )
    size = 4
    itemCapacity = 20
    energyCapacity = 1024f
    basicPotentialEnergy = 256f

    warmupSpeed = 0.005f

    newConsume()
    consume!!.time(60f)
    consume!!.item(IItems.铀238, 1)
    consume!!.energy(1.2f)
    newProduce()
    produce!!.item(IItems.钚239, 1)

    newConsume()
    consume!!.time(60f)
    consume!!.item(IItems.相位封装氢单元, 1)
    consume!!.energy(1.5f)
    newProduce()
    produce!!.item(IItems.氢聚变燃料, 1)

    newConsume()
    consume!!.time(60f)
    consume!!.item(IItems.相位封装氦单元, 1)
    consume!!.energy(1.6f)
    newProduce()
    produce!!.item(IItems.氦聚变燃料, 1)

    newConsume()
    consume!!.time(90f)
    consume!!.item(IItems.核废料, 2)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
    consume!!.energy(2.2f)
    newProduce()
    produce!!.items(
      *ItemStack.with(
        IItems.铱金混合物, 1, IItems.强化合金, 1, Items.thorium, 1
      )
    )

    draw = DrawMulti(
      DrawBottom(), object : DrawBlock() {
      override fun draw(build: Building) {
        LiquidBlock.drawTiledFrames(
          build.block.size, build.x, build.y, 4f, ILiquids.孢子云, (build as NormalCrafterBuild).consEfficiency()
        )
      }
    }, object : DrawRegionDynamic<NormalCrafterBuild?>("_light") {
      init {
        alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
        color = Func { e: NormalCrafterBuild? -> Tmp.c1.set(Pal.slagOrange).lerp(Pal.accent, Mathf.absin(5f, 1f)) }
      }
    }, object : DrawBlock() {
      override fun draw(build: Building) {

        val e = build as NormalCrafterBuild?
        val angle1 = MathTransform.gradientRotateDeg(build.totalProgress() * 0.8f, 180f, 0.5f, 4)
        val angle2 = MathTransform.gradientRotateDeg(build.totalProgress() * 0.8f, 0f, 0.25f, 4)

        Draw.color(Color.black)
        Fill.square(build.x, build.y, 3 * e!!.consEfficiency(), angle2 + 45)

        SglDraw.drawBloomUnderBlock(e) { b: NormalCrafterBuild? ->
          Lines.stroke(0.75f * b!!.consEfficiency(), SglDrawConst.fexCrystal)
          Lines.square(b.x, b.y, 4 * b.consEfficiency(), angle2 + 45)

          Lines.stroke(0.8f * b.consEfficiency())
          Lines.square(b.x, b.y, 6 * b.consEfficiency(), -angle1 + 45)
          Draw.reset()
        }
        Draw.z(35f)
        Draw.reset()
      }
    }, DrawDefault(), DrawRegion("_top")
    )
  }
  var 聚合引力发生器 = NormalCrafter("polymer_gravitational_generator").apply {
    bundle {
      desc(zh_CN, "聚合引力发生器", "在真空仓内利用大量的能量制造一个引力漏斗,将物质紧密的挤压在一起至中子简并态,用负引力场外壳包裹为一份简并态中子聚合物")
    }
    requirements(
      Category.crafting, ItemStack.with(
        IItems.强化合金, 180, IItems.矩阵合金, 900, IItems.充能FEX水晶, 100, IItems.FEX水晶, 120, IItems.铱锭, 80, IItems.气凝胶, 100, Items.surgeAlloy, 80, Items.phaseFabric, 90
      )
    )
    size = 5
    itemCapacity = 20

    energyCapacity = 4096f
    basicPotentialEnergy = 1024f

    warmupSpeed = 0.0075f


    newConsume()
    consume!!.energy(10f)
    consume!!.items(
      *ItemStack.with(
        IItems.充能FEX水晶, 1, IItems.矩阵合金, 2, IItems.气凝胶, 3, IItems.铱锭, 1
      )
    )
    consume!!.time(240f)
    newProduce()
    produce!!.item(IItems.简并态中子聚合物, 1)

    craftEffect = SglFx.polymerConstructed
    val timeId = timers++

    draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_liquid") {
      init {
        color = Func { e: NormalCrafterBuild? -> SglDrawConst.ion }
        alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = 1.75f
        rotation = 45f
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = -1.75f
      }
    }, DrawDefault(), object : DrawBlock() {
      val dist: Distortion = Distortion()
      val taskID: Int = SglDraw.nextTaskID()

      override fun draw(build: Building?) {

        val e = build as NormalCrafterBuild
        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        Lines.stroke(0.4f * e.workEfficiency())
        Lines.square(e.x, e.y, 3 + Mathf.random(-0.15f, 0.15f))
        Lines.square(e.x, e.y, 4 + Mathf.random(-0.15f, 0.15f), 45f)

        Draw.z(Layer.flyingUnit + 0.5f)
        dist.setStrength(-32 * e.workEfficiency() * Vars.renderer.scale)
        SglDraw.drawDistortion(taskID, e, dist) { b: NormalCrafterBuild ->
          Distortion.drawVoidDistortion(b.x, b.y, 24 + Mathf.absin(6f, 4f), 32 * b.workEfficiency())
        }

        SglDraw.drawBloomUponFlyUnit(e) { b: NormalCrafterBuild ->
          Draw.color(Pal.reactorPurple)
          Lines.stroke(3 * b.workEfficiency())
          Lines.circle(b.x, b.y, 24 + Mathf.absin(6f, 4f))

          for (p in Geometry.d4) {
            Tmp.v1.set(p.x.toFloat(), p.y.toFloat()).scl(28 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f)
            Draw.rect(
              (SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), b.x + Tmp.v1.x, b.y + Tmp.v1.y, 8 * b.workEfficiency(), 8 * b.workEfficiency(), Tmp.v1.angle() + 90
            )

            Tmp.v2.set(p.x.toFloat(), p.y.toFloat()).scl(24 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f + 45)
            Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 4 * b.workEfficiency(), 4f, Tmp.v2.angle())
            Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 3 * b.workEfficiency(), 3f, Tmp.v2.angle() + 180)
          }
          Draw.reset()
        }

        if (e.timer(timeId, 15 / e.workEfficiency())) {
          SglFx.ploymerGravityField.at(e.x, e.y, 24 + Mathf.absin(6f, 4f), Pal.reactorPurple, e)
        }
      }
    })
  }
  var 质量生成器 = object : MediumCrafter("quality_generator") {
    init {
      bundle {
        desc(zh_CN, "质量生成器", "将能量无序逆向转换的设备,将大量中子能向物质质量进行转换,将产出除夸克类介质外的纯净无序介质")
      }
      requirements(Category.crafting, ItemStack.with())
      size = 4

      placeablePlayer = false

      energyCapacity = 16384f
      mediumCapacity = 32f

      newConsume()
      consume!!.energy(32f)
      newProduce()
      produce!!.medium(0.6f)
    }
  }
  var 物质逆化器 = object : MediumCrafter("substance_inverter") {
    init {
      bundle {
        desc(zh_CN,"物质逆化器","将介质反向建立物质的设备,主动分离正粒子以制造反物质,并盛装到引力容器中")
      }
      requirements(Category.crafting, ItemStack.with())
      size = 5

      placeablePlayer = false

      itemCapacity = 20
      energyCapacity = 1024f

      newConsume()
      consume!!.item(IItems.简并态中子聚合物, 1)
      consume!!.energy(5f)
      consume!!.medium(2.25f)
      consume!!.time(120f)
      newProduce()
      produce!!.item(IItems.反物质, 1)

      craftEffect = SglFx.explodeImpWaveBig
      craftEffectColor = Pal.reactorPurple

      craftedSound = Sounds.explosion
      craftedSoundVolume = 1f

      clipSize = 150f
      val generator: LightningGenerator = CircleGenerator().apply {

        radius = 13.5f
        minInterval = 1.5f
        maxInterval = 3f
        maxSpread = 2.25f

      }
      initialed = Cons { e: SglBuilding ->
        e.lightningDrawer = object : LightningContainer() {
          init {
            minWidth = 0.8f
            maxWidth = minWidth
            lifeTime = 24f
          }
        }
        e.lightnings = object : LightningContainer() {
          init {
            lerp = Interp.pow2Out
          }
        }
        e.lightningGenerator = VectorLightningGenerator().apply {
          maxSpread = 8f
          minInterval = 5f
          maxInterval = 12f
        }
      }

      crafting = Cons { e: NormalCrafterBuild? ->

        if (SglDraw.clipDrawable(e!!.x, e.y, clipSize) && Mathf.chanceDelta((e.workEfficiency() * 0.1f).toDouble())) e.lightningDrawer!!.create(generator)
        if (Mathf.chanceDelta((e.workEfficiency() * 0.04f).toDouble())) SglFx.randomLightning.at(e.x, e.y, 0f, Pal.reactorPurple)
      }

      craftTrigger = Cons { e: NormalCrafterBuild? ->
        if (!SglDraw.clipDrawable(e!!.x, e.y, clipSize)) return@Cons
        val a = Mathf.random(1, 3)

        for (i in 0..<a) {
          val gen: VectorLightningGenerator = e.lightningGenerator!!
          gen.vector.rnd(Mathf.random(65, 100).toFloat())
          val amount = Mathf.random(3, 5)
          for (i1 in 0..<amount) {
            e.lightnings!!.create(gen)
          }

          if (Mathf.chance(0.25)) {
            SglFx.explodeImpWave.at(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple)
            Angles.randLenVectors(
              System.nanoTime(), Mathf.random(4, 7), 2f, 3.5f
            ) { x: Float, y: Float -> SglParticleModels.floatParticle.create(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple, x, y, Mathf.random(3.25f, 4f)) }
          } else {
            SglFx.spreadLightning.at(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple)
          }
        }

        Effect.shake(5.5f, 20f, e.x, e.y)
      }

      draw = DrawMulti(DrawBottom(), object : DrawBlock() {
        override fun draw(build: Building?) {

          val e = build as NormalCrafterBuild

          SglDraw.drawBloomUnderBlock(e) { b: NormalCrafterBuild ->
            val c: LightningContainer = b.lightningDrawer!!
            if (!Vars.state.isPaused) c.update()

            Draw.color(Pal.reactorPurple)
            Draw.alpha(e.workEfficiency())
            c.draw(b.x, b.y)
          }
          Draw.z(35f)
          Draw.color()
        }
      }, DrawDefault(), object : DrawBlock() {
        override fun draw(build: Building?) {

          val e = build as NormalCrafterBuild
          Draw.z(Layer.effect)
          Draw.color(Pal.reactorPurple)
          val c: LightningContainer = e.lightnings!!
          if (!Vars.state.isPaused) c.update()
          c.draw(e.x, e.y)
          val lerp = Noise.noise(Time.time, 0f, 3.5f, 1f)
          val offsetH = 6 * lerp
          val offsetW = 14 * lerp

          SglDraw.drawLightEdge(
            e.x, e.y, (35 + offsetH) * e.workEfficiency(), 2.25f * e.workEfficiency(), (145 + offsetW) * e.workEfficiency(), 4 * e.workEfficiency()
          )

          Draw.z(Layer.bullet - 10)
          Draw.alpha(0.2f * e.workEfficiency() + lerp * 0.25f)
          SglDraw.gradientCircle(e.x, e.y, 72 * e.workEfficiency(), 6 + 5 * e.workEfficiency() + 2.3f * lerp, SglDrawConst.transColor)
          Draw.alpha(0.3f * e.workEfficiency() + lerp * 0.25f)
          SglDraw.gradientCircle(e.x, e.y, 41 * e.workEfficiency(), -6 * e.workEfficiency() - 2f * lerp, SglDrawConst.transColor)
          Draw.alpha(0.55f * e.workEfficiency() + lerp * 0.25f)
          SglDraw.gradientCircle(e.x, e.y, 18 * e.workEfficiency(), -3 * e.workEfficiency() - lerp, SglDrawConst.transColor)
          Draw.alpha(1f)
          SglDraw.drawLightEdge(
            e.x, e.y, (60 + offsetH) * e.workEfficiency(), 2.25f * e.workEfficiency(), 0f, 0.55f, (180 + offsetW) * e.workEfficiency(), 4 * e.workEfficiency(), 0f, 0.55f
          )
        }
      })
    }
  }
  var 析构器 = object : Destructor("destructor") {
    init {
      bundle {
        desc(zh_CN,"析构器","加速碰撞破坏物质的原子核结构,以分析物质的微观构成形态并建立原子空间构成的蓝图")
      }
      requirements(Category.effect, ItemStack.with())
      size = 5

      placeablePlayer = false
      recipeIndfo = Core.bundle.get("infos.destructItems")

      draw = DrawMulti(
        DrawBottom(), object : DrawPlasma() {
          init {
            suffix = "_plasma_"
            plasma1 = SglDrawConst.matrixNet
            plasma2 = SglDrawConst.matrixNetDark
          }
        }, DrawDefault()
      )
    }
  }
  var 强子重构仪 = object : AtomSchematicCrafter("hadron_reconstructor") {
    init {
      bundle {
        desc(zh_CN,"强子重构仪","微缩的定向大量强子对撞机,使得创造物质从理论成为现实")
      }
      requirements(
        Category.crafting, ItemStack.with(
          IItems.简并态中子聚合物, 80, IItems.铱锭, 120, IItems.充能FEX水晶, 120, IItems.矩阵合金, 90, IItems.气凝胶, 120, Items.surgeAlloy, 90
        )
      )
      size = 4
      itemCapacity = 24

      placeablePlayer = false

      craftEffect = SglFx.hadronReconstruct

      draw = DrawMulti(
        DrawBottom(), object : DrawPlasma() {
        init {
          suffix = "_plasma_"
          plasma1 = Pal.reactorPurple
          plasma2 = Pal.reactorPurple2
        }
      }, object : DrawBlock() {
        override fun draw(build: Building?) {
          val e = build as NormalCrafterBuild
          Draw.alpha(e.progress())
          if (e.producer!!.current != null) Draw.rect(e.producer!!.current!!.get(ProduceType.item)!!.items[0].item.uiIcon, e.x, e.y, 4f, 4f)
          Draw.color()
        }
      }, DrawDefault()
      )
    }
  }
}