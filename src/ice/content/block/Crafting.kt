package ice.content.block

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Interp
import arc.math.Mathf
import ice.audio.ISounds
import ice.content.IItems
import ice.content.ILiquids
import ice.graphics.IceColor
import ice.library.world.ContentLoad
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.crafting.CeriumExtractor
import ice.world.content.blocks.crafting.GenericCrafter
import ice.world.content.blocks.crafting.multipleCrafter.MultipleCrafter
import ice.world.content.blocks.crafting.oreMultipleCrafter.OreFormula
import ice.world.content.blocks.crafting.oreMultipleCrafter.OreMultipleCrafter
import ice.world.draw.DrawArcSmelt
import ice.world.draw.DrawBuild
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import ice.world.meta.IceEffects
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.RadialEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.blocks.production.Incinerator
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.consumers.ConsumePower
import mindustry.world.draw.*

@Suppress("unused")
object Crafting : ContentLoad {
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
        craftEffect =
            MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin, IceEffects.hitLaserBlast)
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
                zh_CN,
                "碳控熔炉",
                "通过精确控制碳元素配比,在同一生产线灵活产出高碳钢和低碳钢,稳定的温度控制确保钢材质量始终达标"
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
                zh_CN,
                "普适冶炼阵列",
                "核心级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铜,锌,铅等多种金属原料,为后续生产提供稳定的金属供应"
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
                zh_CN,
                "特化冶炼阵列",
                "进阶级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铬,金,钴等多种金属原料,为后续生产提供稳定的金属供应"
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
            drawers = DrawMulti(
                DrawRegion("-bottom"),
                DrawLiquidRegion(),
                DrawDefault(),
                DrawRegion("-runner", 6f, true).apply {
                    x = 8.3f
                    y = 8.3f
                },
                DrawRegion("-runner", 6f, true).apply {
                    x = -8.3f
                    y = -8.3f
                },
                DrawRegion("-runner", 6f, true).apply {
                    x = 8.3f
                    y = -8.3f
                },
                DrawRegion("-runner", 6f, true).apply {
                    x = -8.3f
                    y = 8.3f
                })
            oreFormula.add(OreFormula().apply {
                crftTime = 60f
                addInput(IItems.方铅矿, 1)
                addInput(ConsumeLiquids(LiquidStack.with(Liquids.water, 15f)))
                addOutput(Items.lead, 1, 5)
                addOutput(Items.copper, 2, 60)
                addOutput(Items.beryllium, 3, 7)
            }, OreFormula().apply {
                crftTime = 30f
                addInput(IItems.黄铜矿, 1, IItems.生煤, 1)
                addOutput(Items.lead, 1, 50)
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
                zh_CN,
                "蜂巢陶瓷合成巢",
                "利用基因改造的硅基菌群分泌陶瓷基质,再经激光固化,生产过程中会发出蜂鸣般的共振声",
                "资源蜜蜂?"
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
            desc(zh_CN, "冲压锻炉", "快速大批量地熔炼铱锇矿并将其锻压为铱板", "熔炼,锻压,一次成型,是锻炉中的豪杰")
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
                zh_CN,
                "玳渊缚能厂",
                "枢机批准的能量设施,将狂暴的玳渊能量封印在稳定的矩阵结构中,每一块矩阵都蕴含着巨大的能量"
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
            Category.crafting,
            IItems.铱板,
            55,
            IItems.高碳钢,
            230,
            IItems.石英玻璃,
            30,
            IItems.铬锭,
            80,
            IItems.单晶硅,
            80
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
            Category.crafting,
            IItems.铬锭,
            185,
            IItems.铱板,
            100,
            IItems.石英玻璃,
            45,
            IItems.铱板,
            120,
            IItems.导能回路,
            80,
            IItems.铈锭,
            55
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
                zh_CN,
                "增压铈萃取器",
                "在特制的超高压密封反应釜内,通过液相沉淀的方式萃取铈\n相较初代密封性更强,具有更高的压力,能够更迅速的萃取铈"
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
        ambientSound = Sounds.extractLoop
        ambientSoundVolume = 0.08f
        requirements(Category.crafting, IItems.铱板, 140, IItems.单晶硅, 50, IItems.铪锭, 30, IItems.铬锭, 100)

        bundle {
            desc(
                zh_CN,
                "导能回路装配器",
                "持续开启相位时间场,减缓局部时间以同时进行多种精密零件的制作",
                "[#9B929D]为什么总有人管她叫灵魂熔炉[]"
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
        ambientSound = Sounds.grinding
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
                zh_CN,
                "高能陶钢聚合炉",
                "依靠高能激光持续熔融原料以快速熔炼陶钢\n相比普通熔炼炉,熔炼效率及产物质量都有显著提升"
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
}