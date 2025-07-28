package ice.content

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.util.Time
import ice.Ice
import ice.library.IFiles
import ice.library.scene.texs.Colors
import ice.library.type.baseContent.BaseContentSeq
import ice.library.type.baseContent.blocks.crafting.EffectGenericCrafter
import ice.library.type.baseContent.blocks.crafting.multipleCrafter.Formula
import ice.library.type.baseContent.blocks.crafting.multipleCrafter.MultipleCrafter
import ice.library.type.baseContent.blocks.crafting.oreMultipleCrafter.OreFormula
import ice.library.type.baseContent.blocks.crafting.oreMultipleCrafter.OreMultipleCrafter
import ice.library.type.baseContent.blocks.distribution.*
import ice.library.type.baseContent.blocks.distribution.digitalStorage.DigitalConduit
import ice.library.type.baseContent.blocks.distribution.digitalStorage.DigitalInput
import ice.library.type.baseContent.blocks.distribution.digitalStorage.DigitalOutput
import ice.library.type.baseContent.blocks.distribution.digitalStorage.DigitalStorage
import ice.library.type.baseContent.blocks.distribution.droneNetwork.DroneDeliveryTerminal
import ice.library.type.baseContent.blocks.distribution.droneNetwork.DroneReceivingRnd
import ice.library.type.baseContent.blocks.distribution.itemNode.ItemNode
import ice.library.type.baseContent.blocks.effect.*
import ice.library.type.baseContent.blocks.environment.IceFloor
import ice.library.type.baseContent.blocks.environment.IceStaticWall
import ice.library.type.baseContent.blocks.environment.IceTreeBlock
import ice.library.type.baseContent.blocks.environment.TiledFloor
import ice.library.type.baseContent.blocks.liquid.LiquidClassifier
import ice.library.type.baseContent.blocks.liquid.MultipleLiquidBlock
import ice.library.type.baseContent.blocks.liquid.pumpChamber
import ice.library.type.baseContent.blocks.production.IceDrill
import ice.library.type.baseContent.blocks.production.MinerTower
import ice.library.type.baseContent.blocks.science.Laboratory
import ice.library.type.draw.IceDrawArcSmelt
import ice.library.type.draw.IceDrawMulti
import ice.library.type.meta.Attributes
import ice.library.type.meta.IceEffects
import ice.music.IceSounds
import ice.shader.IceShader
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.MissileBulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.RadialEffect
import mindustry.entities.part.DrawPart.PartParams
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootAlternate
import mindustry.entities.pattern.ShootSummon
import mindustry.gen.Sounds
import mindustry.graphics.CacheLayer
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.defense.Wall
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.blocks.distribution.Conveyor
import mindustry.world.blocks.environment.*
import mindustry.world.blocks.power.BeamNode
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.blocks.storage.Unloader
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.consumers.ConsumePower
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.Env
import kotlin.math.max
import kotlin.random.Random

object IceBlocks {
    fun load() {
        Vars.content.blocks().forEach {
            if (it.minfo.mod == Ice.ice) BaseContentSeq.blocks.add(it)
        }
    }

    val 多叶草: Block = Prop("leafyGrass").apply {
        variants = 3
        customShadow = true
    }
    val 霜寒草: Block = Prop("frostbiteGrass").apply {
        variants = 3
        customShadow = true
    }
    val 地笼草: Block = Prop("cageGrass").apply {
        variants = 3
        customShadow = true
    }
    val 枯棕枝: Block = Prop("deadwoodGrass").apply {
        variants = 3
        customShadow = true
    }
    val 绿羽: Block = Prop("featherGrass").apply {
        customShadow = true
        variants = 3
    }
    val 幽灵簇: Block = Seaweed("clusterGhosts").apply {
        size = 1
    }
    val 草嫣红: Block = Prop("flowers1").apply {
        variants = 3
        customShadow = true
    }
    val 绯叶绮: Block = Prop("flowers2").apply {
        variants = 3
        customShadow = true
    }
    val 叶嫣粉: Block = Prop("flowers3").apply {
        variants = 2
        customShadow = true
    }
    val 血晶尖刺: Block = TallBlock("bloodCrystalSpikes").apply {
        variants = 3
        clipSize = 128f
    }
    val 血孢子丛: Block = Prop("bloodSporophore").apply {
        hasShadow = true
        variants = 3
        breakSound = Sounds.plantBreak
    }
    val 云英石柱: Block = Prop("greisenPillar").apply {
        variants = 2
        customShadow = false
    }
    val 风蚀沙柱: Block = object : TallBlock("windErodedSandPillar") {
        init {
            hasShadow = true
            rotationRand = 30f
            variants = 2
        }

        override fun drawBase(tile: Tile) {
            val rot = Mathf.randomSeedRange((tile.pos() + 1).toLong(), rotationRand)
            Draw.rect(if (variants > 0) variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
                max(0, variantRegions.size - 1))] else region, tile.worldx(), tile.worldy(), rot)
            Draw.z(Layer.power)
        }
    }
    val 大风蚀沙柱: Block = object : TallBlock("windErodedSandPillarBig") {
        init {
            hasShadow = true
            size = 2
            variants = 1
            rotationRand = 45f
        }

        override fun drawBase(tile: Tile) {
            val i1 = tile.x + 1
            val i2 = tile.y + 1
            val t1: Tile? = Vars.world.tile(i1, tile.y.toInt())
            val t2: Tile? = Vars.world.tile(i1, i2)
            val t3: Tile? = Vars.world.tile(tile.x.toInt(), tile.y.toInt())
            if (t1?.block() === t2?.block() && t2?.block() === t3?.block()) {
                val rot = Mathf.randomSeedRange((tile.pos() + 1).toLong(), rotationRand)
                Draw.z(Layer.power + 1)
                Draw.rect(if (variants > 0) variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
                    max(0, (variantRegions.size - 1)))] else region, tile.worldx() + 4, tile.worldy() + 4, rot)
            }
        }
    }
    val 殷红树: Block = IceTreeBlock("bloodSporophoreTree").apply {
        attributes[Attributes.血囊孢子] = 1f
    }
    val 利芽: Block = TallBlock("edgeBud")
    val 红冰石: Block = Prop("redIceStone").apply {
        variants = 3
    }
    val 灵液池: Block = IceFloor("ichorPool", 0).apply {
        liquidDrop = IceLiquids.灵液
        cacheLayer = IceShader.ichorPoolCache
        drownTime = 200f
        speedMultiplier = 0.4f
        isLiquid = true
    }
    val 软红冰: Block = IceFloor("softRedIce", 3).apply {
        cacheLayer = IceShader.softRedIceCache
        speedMultiplier = 0.5f
    }
    val 红冰: Block = object : IceFloor("redIce", 3) {
        init {
            updateFloor = true
            decoration = 红冰石
        }

        override fun renderUpdate(tile: UpdateRenderState) {
            val nextInt = Random.nextInt(500)/* if (nextInt == 0) {
                 val random = IceEffects.rand.random(0, 3)
                 tile.tile.nearby(random)?.let {
                     if (it.floor() == tile.floor) return
                     it.setFloor(tile.floor)
                     Vars.renderer.blocks.updateFloors.add(UpdateRenderState(it, it.floor()))
                 }

             }*/
        }
    }
    val 红冰墙: Block = IceStaticWall("redIceWall")
    val 绿羽地: Block = IceFloor("sod", 3)
    val 绿羽墙: Block = IceStaticWall("sodWall")
    val 金珀沙: Block = IceFloor("goldPearlGrit", 3)
    val 金珀沙墙: Block = IceStaticWall("goldPearlGritWall")
    val 皎月银沙: Block = IceFloor("silverSand", 3)
    val 皎月银沙墙: Block = IceStaticWall("silverSandWall")
    val 风蚀沙地: Block = IceFloor("windErodedSand", 3)
    val 风蚀沙墙: Block = IceStaticWall("windErodedSandWall")
    val 风蚀砂地: Block = IceFloor("windErodedGrit", 5).apply {
        decoration = 风蚀沙柱
    }
    val 风蚀喷口: Block = SteamVent("windErodedSand-vent").apply {
        variants = 1
        parent = 风蚀沙地.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
    }
    val 光辉板岩: Block = IceFloor("brillianceSlate", 3)
    val 光辉板岩墙: Block = IceStaticWall("brillianceSlateWall")
    val 云英岩: Block = IceFloor("greisen", 3)
    val 云英岩墙: Block = IceStaticWall("greisenWall")
    val 红土: Block = IceFloor("redDir", 3).apply {
        decoration = 血孢子丛
    }
    val 红土墙: Block = IceStaticWall("redDirWall")
    val 流纹岩: Block = IceFloor("liparite", 3)
    val 流纹岩墙: Block = IceStaticWall("lipariteWall")
    val 潮汐水石: Block = IceFloor("nightTideStoneWater", 3).apply {
        cacheLayer = CacheLayer.water
        liquidDrop = Liquids.water
    }
    val 潮汐石: Block = IceFloor("nightTideStone", 4)
    val 潮汐石墙: Block = IceStaticWall("nightTideStoneWall")
    val 潮汐喷口: Block = SteamVent("nightTideStone-vent").apply {
        variants = 1
        parent = 潮汐石.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
    }
    val 侵蚀层地: Block = IceFloor("erosionalSlate", 3)
    val 侵蚀层地墙: Block = IceStaticWall("erosionalSlateWall")
    val 晶石地: Block = IceFloor("crystalStone", 3)
    val 晶石墙: Block = IceStaticWall("crystalStoneWall")
    val 幽灵草: Block = IceFloor("ghostGrass", 3)
    val 幽灵草墙: Block = IceStaticWall("ghostGrassWall")
    val 灰烬地: Block = IceFloor("ash", 6)
    val 灰烬墙: Block = IceStaticWall("ashWall")
    val 钢铁地板: Block = IceFloor("steelFloor", 3)
    val 钢铁墙: Block = IceStaticWall("steelFloorWall", 4)
    val 钢铁地板1: Block = IceFloor("steelFloor1", 8)
    val 钢铁墙1: Block = IceStaticWall("steelFloorWall1", 3)
    val 跨界钢板: Block = TiledFloor("bridgeSteel", 9, 6)
    val 跨界钢板墙: Block = IceStaticWall("bridgeSteelWall", 4)
    val 新月岩: Block = IceFloor("crescent", 3)
    val 新月岩墙: Block = IceStaticWall("crescentWall")
    val 新月喷口: Block = SteamVent("crescent-vent").apply {
        variants = 1
        parent = 新月岩.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
    }
    val 血池: Block = IceFloor("thickBlood", 0).apply {
        speedMultiplier = 0.5f
        status = StatusEffects.wet
        statusDuration = 90f
        liquidDrop = IceLiquids.浓稠血浆
        isLiquid = true
        cacheLayer = IceShader.bloodNeoplasma
        albedo = 0.9f
        supportsOverlay = true
    }
    val 深血池: Block = IceFloor("deepThickBlood", 0).apply {
        speedMultiplier = 0.2f
        liquidDrop = IceLiquids.浓稠血浆
        liquidMultiplier = 1.5f
        isLiquid = true
        status = StatusEffects.wet
        statusDuration = 120f
        drownTime = 200f
        cacheLayer = IceShader.bloodNeoplasma
        albedo = 0.9f
        supportsOverlay = true
    }
    val 血浅滩: Block = ShallowLiquid("bloodShoal").apply {
        mapColor = Color.valueOf("ff656a")
        cacheLayer = IceShader.bloodNeoplasma
        shallow = true
        variants = 0
        liquidDrop = IceLiquids.浓稠血浆
        speedMultiplier = 0.8f
        statusDuration = 50f
        albedo = 0.9f
        supportsOverlay = true
    }
    val 肿瘤地: Block = IceFloor("bloodNeoplasma", 11)
    val 肿瘤墙: Block = IceStaticWall("bloodNeoplasmaWall", 3)
    val 肿瘤喷口: Block = SteamVent("bloodNeoplasma-vent").apply {
        variants = 3
        parent = 肿瘤地.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
    }
    val 肿瘤井: Block = Prop("bloodNeoplasma-well").apply {
        size = 1
    }
    val 肉瘤菇: Block = TallBlock("bloodBall").apply {
        size = 1
        variants = 2
        customShadow = true
    }
    val 摄魂墙: Block = Block("soulCapturing").apply {
        solid = true
        breakable = true
    }
    val 能量节点: Block =BeamNode("powerNode").apply {
        laser= IFiles.findPng("powerNode-beam")
        laserEnd= IFiles.findPng("powerNode-beam-end")
        requirements(Category.power, ItemStack.with(IceItems.高碳钢, 8))
        laserColor1= Colors.b4
        laserColor2= Color.valueOf("bad7e6")
        consumesPower = true
        outputsPower = true
        health = 90
        range = 10
        fogRadius = 1
        buildCostMultiplier = 2.5f
        consumePowerBuffered(1000f)
    }

    /**防御*/
    val 铬墙: Block = Wall("chromeWall1").apply {
        health = 450
        armor
        size = 1
        requirements(Category.defense, ItemStack.with(IceItems.铬铁矿, 6))
    }
    val 大型铬墙: Block = Wall("chromeWall2").apply {
        size = 2
        health = 铬墙.health * 4
        requirements(Category.defense, ItemStack.with(IceItems.铬铁矿, 6 * 4))
    }
    val 巨型铬墙: Block = Wall("chromeWall3").apply {
        size = 3
        health = 铬墙.health * 9
        requirements(Category.defense, ItemStack.with(IceItems.铬铁矿, 6 * 9))
    }
    val 碳钢墙1: Block = Wall("CarbonSteelWall1").apply {
        size = 1
        armor = 5f
        health = 720
        chanceDeflect = 0.1f
        requirements(Category.defense, ItemStack.with(IceItems.高碳钢, 3, IceItems.低碳钢, 3))
    }
    val 碳钢墙2: Block = Wall("CarbonSteelWall2").apply {
        size = 2
        armor = 5f
        chanceDeflect = 0.15f
        health = 碳钢墙1.health * size * size
        requirements(Category.defense,
            ItemStack.with(IceItems.高碳钢, 3 * size * size, IceItems.低碳钢, 3 * size * size))
    }

    /**生产*/
    val 纤汲钻井: Block = IceDrill("deriveDrill").apply {
        tier = 3
        size = 2
        requirements(Category.production, ItemStack.with(IceItems.硫钴矿, 4, IceItems.低碳钢, 32))
        drillTime = 200f
    }
    val 蛮荒钻井: Block = IceDrill("uncivilizedDrill").apply {
        tier = 4
        size = 3
        drillTime = 150f
        requirements(Category.production, ItemStack.with(IceItems.硫钴矿, 4, IceItems.低碳钢, 32))
    }
    val 曼哈德钻井: Block = IceDrill("manhardDrill").apply {
        tier = 5
        drillTime = 100f
        size = 3
        requirements(Category.production, ItemStack.with(IceItems.硫钴矿, 4, IceItems.低碳钢, 32))
    }

    /**运输*/
    val 基础传送带: Block = Conveyor("baseConveyor").apply {
        size = 1
        speed = 0.12f
        requirements(Category.distribution, ItemStack.with(IceItems.铜锭, 1))
    }
    val 钴熠传送带: Block = IceStackConveyor("cobaltBrightConveyor").apply {
        speed = 50f / 600f
        requirements(Category.distribution, ItemStack.with(IceItems.硫钴矿, 20))
    }
    val 梯度传送带: Block = IceStackConveyor("gradedConveyor").apply {
        speed = 60f / 600f
        drawLastItems = false
        differentItem = true
        loadEffect = Effect(30.0f) { e ->
            Draw.color(Color.valueOf("b8bde1"))
            Lines.stroke(0.5f * e.fout())
            val spread = 4f
            Fx.rand.setSeed(e.id.toLong())
            Draw.alpha(e.fout())
            for (i in 0..7) {
                val ang = e.rotation + Fx.rand.range(8f) + i
                Fx.v.trns(ang, Fx.rand.random(e.fin() * 10f))
                Lines.lineAngle(e.x + Fx.v.x + Fx.rand.range(spread), e.y + Fx.v.y + Fx.rand.range(spread), ang,
                    e.fout() * Fx.rand.random(1f) + 1f)
            }
        }
        requirements(Category.distribution, ItemStack.with(IceItems.铪锭, 20))
    }
    val 基础路由器: Block = IceRouter("baseRouter").apply {
        size = 1
        health = 70
        instantTransfer = true
        requirements(Category.distribution, ItemStack.with(IceItems.低碳钢, 5))
    }
    val 转化分类器: Block = IceSorter("transformSorter").apply {
        size = 1
        health = 100
        requirements(Category.distribution, ItemStack.with(IceItems.低碳钢, 10))
    }
    val 转化溢流门: Block = TransformOverflowGate("transformOverflowGate").apply {
        size = 1
        health = 200
        requirements(Category.distribution, ItemStack.with(IceItems.铜锭, 5))
    }
    val 传输节点: Block = ItemNode("itemNode").apply {
        size = 1
        health = 200
        requirements(Category.distribution, ItemStack.with(IceItems.铜锭, 5))
    }
    val 电子存储: Block = DigitalStorage("digitalStorage")
    val 电子管道: Block = DigitalConduit("digitalConduit")
    val 电子管道输入: Block = DigitalInput("digitalInput")
    val 电子管道输出: Block = DigitalOutput("digitalOutput")
    val 无人机供货端: Block = DroneDeliveryTerminal("droneTeliveryTerminal").apply {}
    val 无人机需求端: Block = DroneReceivingRnd("droneReceivingRnd").apply {}
    val 随机源: Block = Randomer("randomSource")

    /**液体*/
    val 挖掘塔: Block = MinerTower("minerTower")
    val 泵腔: Block = pumpChamber("pumpChamber").apply {
        requirements(Category.liquid, ItemStack.with(IceItems.肌腱, 40, IceItems.碎骨, 10, IceItems.无名肉块, 60))
    }
    val 流体枢纽: Block = MultipleLiquidBlock("fluidJunction").apply {
        size = 3
        liquidCapacity = 1000f
        health = size * size * 100
        requirements(Category.liquid, ItemStack.with(Items.copper, 10))
    }
    val 液体抽离器: Block = LiquidClassifier("liquidClassifier").apply {
        size = 1
        requirements(Category.liquid, ItemStack.with(Items.copper, 1))
    }

    /**工厂*/
    val 量子蚀刻厂: Block = object : EffectGenericCrafter("integratedFactory") {
        init {
            drawer = DrawMulti(DrawRegion("-bottom"), DrawRegion("-top"))
            itemCapacity = 20
            health = 200
            outputItem = ItemStack(IceItems.电子元件, 1)
            consumeItems(*ItemStack.with(IceItems.单晶硅, 1, IceItems.石墨烯, 2, IceItems.石英玻璃, 1))
            craftTime = 60f
            craftEffect = MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin,
                IceEffects.hitLaserBlast)
            size = 3
            requirements(Category.crafting, ItemStack.with(IceItems.铜锭, 19))
        }
    }
    val 单晶硅厂: Block = object : GenericCrafter("monocrystallineSiliconFactory") {
        init {
            requirements(Category.crafting, ItemStack.with(IceItems.红冰, 12))
            outputItem = ItemStack(IceItems.单晶硅, 1)
            craftTime = 60f
            health = 360
            size = 3
            hasPower = true
            drawer = DrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate", 9f, true), DrawDefault(),
                DrawFlame(Color.valueOf("ff9c71")))
            consumeItems(*ItemStack.with(Items.pyratite, 1, IceItems.石英, 3))
            consumePower(1.8f)
        }
    }
    val 铸铜厂: Block = object : GenericCrafter("copperFoundry") {
        init {
            size = 4
            health = 200
            craftTime = 90f
            outputItem = ItemStack(IceItems.黄铜锭, 3)
            drawer = DrawMulti(DrawDefault(), DrawFlame())
            consumeItems(*ItemStack.with(IceItems.铜锭, 3, IceItems.锌锭, 1))
            requirements(Category.crafting, ItemStack.with(IceItems.铜锭, 200, IceItems.低碳钢, 150))
            craftEffect = IceEffects.square(IceItems.铜锭.color)
        }
    }
    val 碳控熔炉: Block = MultipleCrafter("carbonSteelFactory").apply {
        size = 3
        itemCapacity = 20
        val ct = RadialEffect().apply {
            effect = Fx.surgeCruciSmoke
            rotationSpacing = 0f
            lengthOffset = 0f
            amount = 4
        }
        formulas.addFormula(Formula().apply {
            craftTime = 60f
            craftEffect = ct
            setInput(ConsumeItems(ItemStack.with(IceItems.赤铁矿, 2)), ConsumePower(60 / 60f, 0f, false))
            setOutput(ItemStack(IceItems.低碳钢, 1))
        })
        formulas.addFormula(Formula().apply {
            craftTime = 60f
            craftEffect = ct
            setInput(ConsumeItems(ItemStack.with(IceItems.赤铁矿, 2, IceItems.生煤, 1)),
                ConsumePower(90 / 60f, 0f, false))
            setOutput(ItemStack(IceItems.高碳钢, 1))
        })

        drawers = IceDrawMulti(DrawRegion("-bottom"), IceDrawArcSmelt().apply {
            x += 8
            startAngle = 135f
            endAngle = 225f
        }, IceDrawArcSmelt().apply {
            x -= 8
            startAngle = -45f
            endAngle = 45f
        }, IceDrawArcSmelt().apply {
            y += 8
            startAngle = 180 + 45f
            endAngle = 360 - 45f
        }, IceDrawArcSmelt().apply {
            y -= 8
            startAngle = 0f + 45
            endAngle = 180f - 45f
        }, DrawDefault())
        requirements(Category.crafting, ItemStack.with(IceItems.铜锭, 2))
    }
    val 矿石粉碎机: Block = object : OreMultipleCrafter("mineralCrusher") {
        init {
            squareSprite = false
            hasLiquids = true
            drawer = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(),
                DrawRegion("-runner", 6f, true).apply {
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
                addInput(IceItems.方铅矿, 1)
                addInput(ConsumeLiquids(LiquidStack.with(Liquids.water, 15f)))
                addOutput(Items.lead, 1, 5)
                addOutput(Items.copper, 2, 60)
                addOutput(Items.beryllium, 3, 7)
            }, OreFormula().apply {
                crftTime = 30f
                addInput(IceItems.黄铜矿, 1, IceItems.生煤, 1)
                addOutput(Items.lead, 1, 50)
                addOutput(Items.graphite, 1, 50)
            })
        }
    }
    val 蜂巢陶瓷合成巢 = GenericCrafter("ceramicKiln").apply {
        size = 4
    }

    /**其他*/
    val 真菌塔: Block = FungusCore("fungusTower").apply {
        size = 2
        category = Category.effect
    }
    val 定向超速器: Block = OrientationProjector("orientationProjector").apply {
        size = 2
        buildSize = 5
        range = 8 * 20f
    }
    val 遗弃资源箱: Block = ResBox("resBox")
    val 遗弃匣: Block = LostBox("lostBox").apply {
        size = 2
        envEnabled = Env.any
        category = Category.effect
    }
    val 传输矿仓: Block = ItemExtractor("conveyOreWar").apply {
        size = 2
        buildSize = 8
        range = 10 * 8f
        allowLink.add(纤汲钻井)
        requirements(Category.effect, ItemStack.with(IceItems.低碳钢, 30))
    }
    val 开采核心: Block = object : CoreBlock("minerCore") {
        init {
            size = 3
            isFirstTier = true
            health = 5000
            itemCapacity = 2000
            requirements(Category.effect, ItemStack.with(IceItems.低碳钢, 4000, IceItems.锌锭, 1500))
        }
    }
    val 血肉枢纽: Block = FleshAndBloodCoreBlock("fleshAndBloodhinge").apply {
        health = -1
        size = 4
        itemCapacity = 6000
        squareSprite = false
        requirements(Category.effect, ItemStack.with(IceItems.无名肉块, 2300, IceItems.碎骨, 2000))
    }
    val 量子卸载器: Block = object : Unloader("electronicUninstaller") {
        init {
            requirements(Category.effect, ItemStack.with(IceItems.铜锭, 10, IceItems.单晶硅, 5))
            speed = 0.1f
            health = 100
            size = 1
            itemCapacity = 10
        }
    }
    val 基础实验室: Block = Laboratory("laboratory").apply {
        consumePower(100f / 60)
    }
    val 碎冰: Block = ItemTurret("trashIce").apply {
        size = 1
        health = 250
        recoil = 0.5f
        shootY = 3f
        reload = 45f
        range = 160f
        shootCone = 30f
        shoot = ShootSummon().apply {
            x = 0f
            y = 0f
            spread = 5f
            shots = 5
            shotDelay = 3f
        }
        shootSound = IceSounds.laser1
        shootEffect = Effect(8.0f) { e: EffectContainer ->
            Draw.color(Colors.b4, Color.white, e.fin())
            val w = 1.0f + 5.0f * e.fout()
            Drawf.tri(e.x, e.y, w, 15.0f * e.fout(), e.rotation)
            Drawf.tri(e.x, e.y, w, 3.0f * e.fout(), e.rotation + 180.0f)
        }
        ammo(IceItems.硫钴矿, BasicBulletType(5f, 9f).apply {
            width = 2f
            height = 9f
            lifetime = 30f
            ammoMultiplier = 2f
            despawnEffect = IceEffects.baseBulletBoom
            hitEffect = despawnEffect
            trailColor = Colors.b4
            backColor = Colors.b4
            hitColor = Colors.b4
            frontColor = Colors.b4
        })
        requirements(Category.turret, ItemStack.with(IceItems.铬铁矿, 10, IceItems.低碳钢, 20))
        drawer = DrawTurret().apply {
            parts.add(RegionPart("-barrel").apply {
                progress = PartProgress.recoil
                under = true
                moveY = -1.5f
            })
        }
    }
    val 神矢: Block = PowerTurret("divineArrow").apply {
        size = 2
        health = 1000
        requirements(Category.turret, ItemStack.with(IceItems.铬铁矿, 10, IceItems.低碳钢, 20))
        reload = 30f
        recoils = 2
        drawer = DrawTurret().apply {
            for (i in 0..1) {
                parts.add(object : RegionPart("-" + (if (i == 0) "l" else "r")) {
                    init {
                        progress = PartProgress.recoil
                        recoilIndex = i
                        under = true
                        moveY = -1.5f
                    }
                })
            }
            parts.add(ShapePart().apply {
                hollow = true
                radius = 4f
                layer = 110f
                sides = 4
                y = -4f
                color = Colors.b4
                rotateSpeed = 2f
                progress = PartProgress.recoil
            })
            parts.add(ShapePart().apply {
                hollow = true
                radius = 0f
                radiusTo = 4f
                layer = 110f
                sides = 4
                stroke = 0.5f
                rotateSpeed = 2f
                y = -4f
                color = Colors.b4
                progress = PartProgress { p: PartParams ->
                    PartProgress.warmup.get(p) * ((Time.time / 15) % 1)
                }
            })
        }
        shoot = object : ShootAlternate() {
            var scl: Float = 2f
            var mag: Float = 1.5f
            var offset: Float = Mathf.PI * 1.25f
            override fun shoot(totalShots: Int, handler: BulletHandler, barrelIncrementer: Runnable?) {
                for (i in 0..<shots) {
                    for (sign in Mathf.signs) {
                        val index = ((totalShots + i + barrelOffset) % barrels) - (barrels - 1) / 2f
                        handler.shoot(index * spread * -Mathf.sign(mirror), 0f, 0f,
                            firstShotDelay + shotDelay * i) { b ->
                            b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * sign))
                        }
                    }
                    barrelIncrementer?.run()
                }
            }
        }.apply {
            barrelOffset = 8
            spread = 5f
            shots = 2
            shotDelay = 15f
        }
        shootSound = Sounds.missile
        shootY = 6f
        shootEffect = IceEffects.squareAngle(range = 30f, color1 = Colors.b4, color2 = Color.white)
        shootType = MissileBulletType(6f, 30f).apply {
            splashDamageRadius = 30f
            splashDamage = 30f * 1.5f
            lifetime = 45f
            trailLength = 20
            trailWidth = 1.5f
            trailColor = Colors.b4
            backColor = Colors.b4
            hitColor = Colors.b4
            frontColor = Colors.b4
            despawnEffect = IceEffects.blastExplosion(Colors.b4)
            hitEffect = despawnEffect
        }
        range = shootType.speed * shootType.lifetime
    }
}