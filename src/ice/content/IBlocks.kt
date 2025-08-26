package ice.content

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.util.Time
import ice.Ice
import ice.library.IFiles
import ice.library.baseContent.BaseContentSeq
import ice.library.baseContent.blocks.crafting.GenericCrafter
import ice.library.baseContent.blocks.crafting.multipleCrafter.Formula
import ice.library.baseContent.blocks.crafting.multipleCrafter.MultipleCrafter
import ice.library.baseContent.blocks.crafting.oreMultipleCrafter.OreFormula
import ice.library.baseContent.blocks.crafting.oreMultipleCrafter.OreMultipleCrafter
import ice.library.baseContent.blocks.distribution.*
import ice.library.baseContent.blocks.distribution.digitalStorage.HubConduit
import ice.library.baseContent.blocks.distribution.digitalStorage.LogisticsHub
import ice.library.baseContent.blocks.distribution.digitalStorage.LogisticsInput
import ice.library.baseContent.blocks.distribution.digitalStorage.LogisticsOutput
import ice.library.baseContent.blocks.distribution.droneNetwork.DroneDeliveryTerminal
import ice.library.baseContent.blocks.distribution.droneNetwork.DroneReceivingRnd
import ice.library.baseContent.blocks.distribution.itemNode.ItemNode
import ice.library.baseContent.blocks.effect.*
import ice.library.baseContent.blocks.environment.IceFloor
import ice.library.baseContent.blocks.environment.IceStaticWall
import ice.library.baseContent.blocks.environment.IceTreeBlock
import ice.library.baseContent.blocks.environment.TiledFloor
import ice.library.baseContent.blocks.liquid.LiquidClassifier
import ice.library.baseContent.blocks.liquid.MultipleLiquidBlock
import ice.library.baseContent.blocks.liquid.pumpChamber
import ice.library.baseContent.blocks.production.IceDrill
import ice.library.baseContent.blocks.production.MinerTower
import ice.library.baseContent.blocks.science.Laboratory
import ice.library.draw.drawer.IceDrawArcSmelt
import ice.library.draw.drawer.IceDrawMulti
import ice.library.meta.Attributes
import ice.library.meta.IceEffects
import ice.library.scene.tex.Colors
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
import mindustry.world.blocks.distribution.Junction
import mindustry.world.blocks.environment.*
import mindustry.world.blocks.liquid.Conduit
import mindustry.world.blocks.liquid.LiquidRouter
import mindustry.world.blocks.power.Battery
import mindustry.world.blocks.power.BeamNode
import mindustry.world.blocks.production.Pump
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.blocks.storage.Unloader
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.consumers.ConsumePower
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.Env
import kotlin.math.max

object IBlocks {
    fun load() {
        Vars.content.blocks().forEach {
            if (it.minfo.mod == Ice.mod) BaseContentSeq.blocks.add(it)
        }
    }

    //region环境
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
        liquidDrop = ILiquids.灵液
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
            //val nextInt = Random.nextInt(500)
            /* if (nextInt == 0) {
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
        liquidDrop = ILiquids.浓稠血浆
        isLiquid = true
        cacheLayer = IceShader.bloodNeoplasma
        albedo = 0.9f
        supportsOverlay = true
    }
    val 深血池: Block = IceFloor("deepThickBlood", 0).apply {
        speedMultiplier = 0.2f
        liquidDrop = ILiquids.浓稠血浆
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
        liquidDrop = ILiquids.浓稠血浆
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
    val 能量节点: Block = BeamNode("powerNode").apply {
        laser = IFiles.findPng("powerNode-beam")
        laserEnd = IFiles.findPng("powerNode-beam-end")
        requirements(Category.power, ItemStack.with(IItems.高碳钢, 8))
        laserColor1 = Colors.b4
        laserColor2 = Color.valueOf("bad7e6")
        consumesPower = true
        outputsPower = true
        health = 90
        range = 10
        fogRadius = 1
        buildCostMultiplier = 2.5f
        consumePowerBuffered(1000f)
    }
    val 能量电池: Block = Battery("powerBattery").apply {
        size = 2
        baseExplosiveness = 1f
        emptyLightColor = Colors.df
        fullLightColor = Colors.b4
        consumePowerBuffered(4000f)
        requirements(Category.power, ItemStack.with(IItems.高碳钢, 5))
    }

    //endregion
    //region 防御
    val 铬墙: Block = Wall("chromeWall").apply {
        health = 450
        armor
        size = 1
        requirements(Category.defense, ItemStack.with(IItems.铬锭, 6))
    }
    val 大型铬墙: Block = Wall("chromeWall-large").apply {
        size = 2
        health = 铬墙.health * 4
        requirements(Category.defense, ItemStack.with(IItems.铬锭, 6 * 4))
    }
    val 碳钢墙: Block = Wall("CarbonSteelWall").apply {
        size = 1
        armor = 5f
        health = 720
        chanceDeflect = 0.1f
        requirements(Category.defense, ItemStack.with(IItems.高碳钢, 3, IItems.低碳钢, 3))
    }
    val 大型碳钢墙: Block = Wall("CarbonSteelWall-large").apply {
        size = 2
        armor = 5f
        chanceDeflect = 0.15f
        health = 碳钢墙.health * size * size
        requirements(Category.defense, ItemStack.with(IItems.高碳钢, 3 * size * size, IItems.低碳钢, 3 * size * size))
    }
    val 流金墙: Block = Wall("fluxGoldWall").apply {
        size = 1
        armor = 5f
        health = 1000
        requirements(Category.defense, ItemStack.with(IItems.金锭, 10))
    }
    val 大型流金墙: Block = Wall("fluxGoldWall-large").apply {
        size = 2
        armor = 5f
        health = 1000
        requirements(Category.defense, ItemStack.with(IItems.金锭, 10))
    }

    //endregion
    //region生产
    val 纤汲钻井: Block = IceDrill("deriveDrill").apply {
        tier = 3
        size = 2
        requirements(Category.production, ItemStack.with(IItems.硫钴矿, 4, IItems.低碳钢, 32))
        drillTime = 200f
    }
    val 蛮荒钻井: Block = IceDrill("uncivilizedDrill").apply {
        tier = 4
        size = 3
        drillTime = 150f
        requirements(Category.production, ItemStack.with(IItems.硫钴矿, 4, IItems.低碳钢, 32))
    }
    val 曼哈德钻井: Block = IceDrill("manhardDrill").apply {
        tier = 5
        drillTime = 100f
        size = 3
        requirements(Category.production, ItemStack.with(IItems.硫钴矿, 4, IItems.低碳钢, 32))
    }

    ///*endregion生产*/
    //region运输
    val 基础传送带: Block = Conveyor("baseConveyor").apply {
        size = 1
        speed = 0.2f
        displayedSpeed = speed * 140f
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
    }
    val 钴熠传送带: Block = IceStackConveyor("cobaltBrightConveyor").apply {
        speed = 50f / 600f
        requirements(Category.distribution, ItemStack.with(IItems.硫钴矿, 20))
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
        requirements(Category.distribution, ItemStack.with(IItems.铪锭, 20))
    }
    val 基础路由器: Block = IceRouter("baseRouter").apply {
        size = 1
        health = 70
        instantTransfer = true
        requirements(Category.distribution, ItemStack.with(IItems.低碳钢, 5))
    }
    val 基础交叉器: Block= Junction("baseJunction").apply {
        size = 1
        health = 100
        requirements(Category.distribution, ItemStack.with(IItems.低碳钢, 5))
    }
    val 转化分类器: Block = IceSorter("transformSorter").apply {
        size = 1
        health = 100
        requirements(Category.distribution, ItemStack.with(IItems.低碳钢, 10))
    }
    val 转化溢流门: Block = TransformOverflowGate("transformOverflowGate").apply {
        size = 1
        health = 200
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 5))
    }
    val 传输节点: Block = ItemNode("itemNode").apply {
        size = 1
        health = 200
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 5))
    }
    val 物流枢纽: Block = LogisticsHub("logisticsHub").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 10))
    }
    val 枢纽管道: Block = HubConduit("hubConduit").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
    }
    val 物流输入器: Block = LogisticsInput("logisticsInput").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
    }
    val 物流输出器: Block = LogisticsOutput("logisticsOutput").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
    }
    val 无人机供货端: Block = DroneDeliveryTerminal("droneTeliveryTerminal").apply {}
    val 无人机需求端: Block = DroneReceivingRnd("droneReceivingRnd").apply {}
    val 随机源: Block = Randomer("randomSource")

    /*endregion运输*/
    /*region 液体*/
    val 挖掘塔: Block = MinerTower("minerTower")
    val 泵腔: Block = pumpChamber("pumpChamber").apply {
        requirements(Category.liquid, ItemStack.with(IItems.肌腱, 40, IItems.碎骨, 10, IItems.无名肉块, 60))
    }
    val 谐振导管: Block = Conduit("resonanceConduit").apply {
        requirements(Category.liquid, ItemStack.with(IItems.高碳钢, 10, IItems.锌锭, 20))
    }
    val 谐振泵: Block = Pump("resonancePump").apply {
        size = 2
        squareSprite = false
        requirements(Category.liquid, ItemStack.with(IItems.高碳钢, 40, IItems.锌锭, 10))
    }
    val 流体容器: Block = LiquidRouter("liquidContainer").apply {
        requirements(Category.liquid, ItemStack.with(IItems.铜锭, 30))
        size = 2
        squareSprite = false
        liquidPadding = 6f / 4f
        solid = true
        liquidCapacity = 1000f
        health = 500
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

    //endregion
    //region 工厂
    val 量子蚀刻厂: Block = GenericCrafter("integratedFactory").apply {
        drawers = IceDrawMulti(DrawRegion("-bottom"), DrawRegion("-top"))
        itemCapacity = 20
        health = 200
        outputItems(IItems.电子元件, 1)
        consumeItems(*ItemStack.with(IItems.单晶硅, 1, IItems.石墨烯, 2, IItems.石英玻璃, 1))
        craftTime = 60f
        craftEffect = MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin,
            IceEffects.hitLaserBlast)
        size = 3
        requirements(Category.crafting, ItemStack.with(IItems.铜锭, 19))
    }
    val 单晶硅厂: Block = GenericCrafter("monocrystallineSiliconFactory").apply {
        requirements(Category.crafting, ItemStack.with(IItems.红冰, 12))
        outputItems(IItems.单晶硅, 1)
        craftTime = 60f
        health = 360
        size = 3
        hasPower = true
        drawers = IceDrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate", 9f, true), DrawDefault(),
            DrawFlame(Color.valueOf("ff9c71")))
        consumeItems(*ItemStack.with(Items.pyratite, 1, IItems.石英, 3))
        consumePower(1.8f)
    }
    val 铸铜厂: Block = GenericCrafter("copperFoundry").apply {
        size = 4
        health = 200
        craftTime = 90f
        outputItems(IItems.黄铜锭, 3)
        drawers = IceDrawMulti(DrawDefault(), DrawFlame())
        consumeItems(*ItemStack.with(IItems.铜锭, 3, IItems.锌锭, 1))
        requirements(Category.crafting, ItemStack.with(IItems.铜锭, 200, IItems.低碳钢, 150))
        craftEffect = IceEffects.square(IItems.铜锭.color)
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
            setInput(ConsumeItems(ItemStack.with(IItems.赤铁矿, 2)), ConsumePower(60 / 60f, 0f, false))
            setOutput(ItemStack(IItems.低碳钢, 1))
        })
        formulas.addFormula(Formula().apply {
            craftTime = 60f
            craftEffect = ct
            setInput(ConsumeItems(ItemStack.with(IItems.赤铁矿, 2, IItems.生煤, 1)), ConsumePower(90 / 60f, 0f, false))
            setOutput(ItemStack(IItems.高碳钢, 1))
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
        requirements(Category.crafting, ItemStack.with(IItems.铜锭, 2))
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
    }
    val 暮白高炉:Block=GenericCrafter("duskFactory").apply {
        size=3
        craftTime=120f
        outputItems(IItems.暮光合金,3)
        consumeItems(IItems.低碳钢,5,IItems.铬锭,1,IItems.钴锭,3,IItems.铪锭,1)
        consumeLiquid(ILiquids.暮光液,0.3f)
        requirements(Category.crafting,ItemStack.with(IItems.金锭,200,IItems.钴锭,70))
    }

    //endregion
    //region 其他
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
        requirements(Category.effect, ItemStack.with(IItems.低碳钢, 30))
    }
    val 开采核心: Block = object : CoreBlock("minerCore") {
        init {
            size = 3
            isFirstTier = true
            health = 5000
            itemCapacity = 2000
            requirements(Category.effect, ItemStack.with(IItems.低碳钢, 4000, IItems.锌锭, 1500))
        }
    }
    val 血肉枢纽: Block = FleshAndBloodCoreBlock("fleshAndBloodhinge").apply {
        health = -1
        size = 4
        itemCapacity = 6000
        squareSprite = false
        requirements(Category.effect, ItemStack.with(IItems.无名肉块, 2300, IItems.碎骨, 2000))
    }
    val 量子卸载器: Block = object : Unloader("electronicUninstaller") {
        init {
            requirements(Category.effect, ItemStack.with(IItems.铜锭, 10, IItems.单晶硅, 5))
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
        ammo(IItems.硫钴矿, BasicBulletType(5f, 9f).apply {
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
        requirements(Category.turret, ItemStack.with(IItems.铬铁矿, 10, IItems.低碳钢, 20))
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
        requirements(Category.turret, ItemStack.with(IItems.铬铁矿, 10, IItems.低碳钢, 20))
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
    //endregion
}