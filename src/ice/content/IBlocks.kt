package ice.content

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles.randLenVectors
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.struct.Seq
import arc.util.Interval
import arc.util.Time
import ice.library.EventType.lazyInit
import ice.library.IFiles
import ice.library.content.blocks.abstractBlocks.IceBlock.Companion.desc
import ice.library.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.library.content.blocks.crafting.GenericCrafter
import ice.library.content.blocks.crafting.multipleCrafter.MultipleCrafter
import ice.library.content.blocks.crafting.oreMultipleCrafter.OreFormula
import ice.library.content.blocks.crafting.oreMultipleCrafter.OreMultipleCrafter
import ice.library.content.blocks.defense.Wall
import ice.library.content.blocks.distribution.*
import ice.library.content.blocks.distribution.digitalStorage.HubConduit
import ice.library.content.blocks.distribution.digitalStorage.LogisticsHub
import ice.library.content.blocks.distribution.digitalStorage.LogisticsInput
import ice.library.content.blocks.distribution.digitalStorage.LogisticsOutput
import ice.library.content.blocks.distribution.droneNetwork.DroneDeliveryTerminal
import ice.library.content.blocks.distribution.droneNetwork.DroneReceivingRnd
import ice.library.content.blocks.distribution.itemNode.TransferNode
import ice.library.content.blocks.effect.*
import ice.library.content.blocks.environment.*
import ice.library.content.blocks.liquid.LiquidClassifier
import ice.library.content.blocks.liquid.MultipleLiquidBlock
import ice.library.content.blocks.liquid.SolidPump
import ice.library.content.blocks.liquid.pumpChamber
import ice.library.content.blocks.production.IceDrill
import ice.library.content.blocks.production.MinerTower
import ice.library.content.blocks.science.Laboratory
import ice.library.draw.drawer.DrawArcSmelt
import ice.library.draw.drawer.DrawBuild
import ice.library.draw.drawer.DrawLiquidRegion
import ice.library.draw.drawer.DrawMulti
import ice.library.entities.bullet.IceBasicBulletType
import ice.library.meta.Attributes
import ice.library.meta.IceEffects
import ice.library.scene.tex.IceColor
import ice.music.ISounds
import ice.shader.IceShader
import ice.ui.BaseBundle.Companion.bundle
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.Units
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
import mindustry.game.EventType
import mindustry.gen.Sounds
import mindustry.graphics.CacheLayer
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.blocks.distribution.Conveyor
import mindustry.world.blocks.liquid.Conduit
import mindustry.world.blocks.liquid.LiquidJunction
import mindustry.world.blocks.liquid.LiquidRouter
import mindustry.world.blocks.power.Battery
import mindustry.world.blocks.power.BeamNode
import mindustry.world.blocks.power.ConsumeGenerator
import mindustry.world.blocks.power.ThermalGenerator
import mindustry.world.blocks.production.Incinerator
import mindustry.world.blocks.production.Pump
import mindustry.world.blocks.storage.Unloader
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.consumers.ConsumeItems
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.consumers.ConsumePower
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Env

@Suppress("unused")
object IBlocks {
    fun load() = Unit

    //region环境
    val 多叶草: Block = Prop("leafyGrass").apply {
        bundle {
            desc(zh_CN, "多叶草")
        }
    }
    val 地笼草: Block = Prop("cageGrass").apply {
        bundle {
            desc(zh_CN, "地笼草")
        }
    }
    val 枯棕枝: Block = Prop("deadwoodGrass").apply {
        bundle {
            desc(zh_CN, "枯棕枝")
        }
    }
    val 绿羽: Block = Prop("featherGrass").apply {
        bundle {
            desc(zh_CN, "绿羽")
        }
    }
    val 草嫣红: Block = Prop("flowers1").apply {
        bundle {
            desc(zh_CN, "草嫣红")
        }
    }
    val 绯叶绮: Block = Prop("flowers2").apply {
        bundle {
            desc(zh_CN, "绯叶绮")
        }
    }
    val 叶嫣粉: Block = Prop("flowers3").apply {
        bundle {
            desc(zh_CN, "叶嫣粉")
        }
    }
    val 血孢子丛: Block = Prop("bloodNeoplasmSporophore", Sounds.plantBreak).apply {
        bundle {
            desc(zh_CN, "血孢子丛")
        }
    }
    val 血晶尖刺: Block = TallBlock("bloodCrystalSpikes").apply {
        bundle {
            desc(zh_CN, "血晶尖刺")
        }
    }
    val 殷红树: Block = TreeBlock("bloodSporophoreTree").apply {
        attributes[Attributes.血囊孢子] = 1f
        var range = 40f
        setUpdate { tile ->
            val mouseWorld = Core.input.mouseWorld()
            val tileWorld = Vars.world.tileWorld(mouseWorld.x, mouseWorld.y)
            if (tile == tileWorld) {
                Drawf.circles(tile.drawx(), tile.drawy(), range, IceColor.b4)
            }
            Units.nearby(Vars.player.team(), tile.worldx(), tile.worldy(), range) {
                it.apply(IStatus.寄生, 3 * 60f)
            }
        }
        bundle {
            desc(zh_CN, "殷红树")
        }
    }
    val 利芽: Block = TallBlock("edgeBud").apply {
        bundle {
            desc(zh_CN, "利芽")
        }
    }
    val 红冰石: Block = Prop("redIceStone").apply {
        bundle {
            desc(zh_CN, "红冰石")
        }
    }
    val 灵液: Block = Floor("ichor").apply {
        liquidDrop = ILiquids.灵液
        cacheLayer = IceShader.ichor
        drownTime = 200f
        speedMultiplier = 0.4f
        isLiquid = true
        bundle {
            desc(zh_CN, "灵液")
        }
    }
    val 软红冰: Block = Floor("softRedIce").apply {
        cacheLayer = IceShader.softRedIceCache
        speedMultiplier = 0.5f
        bundle {
            desc(zh_CN, "软红冰")
        }
    }
    val 绿羽地: Block = Floor("sod").apply {
        bundle {
            desc(zh_CN, "绿羽地")
        }
    }
    val 绿羽墙: Block = StaticWall("sodWall").apply {
        bundle {
            desc(zh_CN, "绿羽墙")
        }
    }
    val 金珀沙: Block = Floor("goldPearlGrit").apply {
        bundle {
            desc(zh_CN, "金珀沙")
        }
    }
    val 金珀沙水 = ShallowLiquid("goldPearlGritWater", 金珀沙).apply {
        bundle {
            desc(zh_CN, "金珀沙水")
        }
    }
    val 金珀沙墙: Block = StaticWall("goldPearlGritWall").apply {
        bundle {
            desc(zh_CN, "金珀沙墙")
        }
    }
    val 皎月银沙石块 = Prop("silverSandStone").apply {
        bundle {
            desc(zh_CN, "皎月银沙石块")
        }
    }
    val 皎月银沙 = Floor("silverSand").apply {
        bundle {
            desc(zh_CN, "皎月银沙")
        }
    }
    val 皎月银沙水 = ShallowLiquid("silverSandWater", 皎月银沙).apply {
        bundle {
            desc(zh_CN, "皎月银沙水")
        }
    }
    val 皎月银沙墙: Block = StaticWall("silverSandWall").apply {
        bundle {
            desc(zh_CN, "皎月银沙墙")
        }
    }
    val 风蚀沙柱: Block = TallBlock("windErodedSandPillar").apply {
        bundle {
            desc(zh_CN, "风蚀沙柱")
        }
    }
    val 风蚀砂地: Block = Floor("windErodedGrit").apply {
        bundle {
            desc(zh_CN, "风蚀砂地")
        }
    }
    val 风蚀沙地: Block = Floor("windErodedSand").apply {
        decoration = 风蚀沙柱
        bundle {
            desc(zh_CN, "风蚀沙地")
        }
    }
    val 风蚀沙水 = ShallowLiquid("windErodedSandWater", 风蚀沙地).apply {
        bundle {
            desc(zh_CN, "风蚀沙水")
        }
    }
    val 风蚀沙深水 = ShallowDeepLiquid("windErodedSandDeepWater", 风蚀沙地).apply {
        bundle {
            desc(zh_CN, "风蚀沙深水")
        }
    }
    val 风蚀沙墙: Block = StaticWall("windErodedSandWall").apply {
        bundle {
            desc(zh_CN, "风蚀沙墙")
        }
    }
    val 风蚀喷口 = SteamVent("windErodedSand-vent").apply {
        parent = 风蚀沙地.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
        bundle {
            desc(zh_CN, "风蚀喷口")
        }
    }
    val 光辉板岩: Block = Floor("brillianceSlate").apply {
        bundle {
            desc(zh_CN, "光辉板岩")
        }
    }
    val 光辉板岩水 = ShallowLiquid("brillianceSlateWater", 光辉板岩).apply {
        bundle {
            desc(zh_CN, "光辉板岩水")
        }
    }
    val 光辉板岩墙: Block = StaticWall("brillianceSlateWall").apply {
        bundle {
            desc(zh_CN, "光辉板岩墙")
        }
    }
    val 云英石柱: Block = Prop("greisenPillar").apply {
        bundle {
            desc(zh_CN, "云英石柱")
        }
    }
    val 云英岩: Block = Floor("greisen").apply {
        bundle {
            desc(zh_CN, "云英岩")
        }
    }
    val 云英岩水 = ShallowLiquid("greisenWater", 云英岩).apply {
        bundle {
            desc(zh_CN, "云英岩水")
        }
    }
    val 云英岩深水 = ShallowDeepLiquid("greisenDeepWater", 云英岩).apply {
        bundle {
            desc(zh_CN, "云英岩深水")
        }
    }
    val 云英岩墙: Block = StaticWall("greisenWall").apply {
        bundle {
            desc(zh_CN, "云英岩墙")
        }
    }
    val 红土石块 = Prop("redDirStone").apply {
        bundle {
            desc(zh_CN, "红土石块")
        }
    }
    val 红土: Block = Floor("redDir").apply {
        bundle {
            desc(zh_CN, "红土")
        }
    }
    val 红土墙: Block = StaticWall("redDirWall").apply {
        bundle {
            desc(zh_CN, "红土墙")
        }
    }
    val 流纹岩: Block = Floor("liparite").apply {
        bundle {
            desc(zh_CN, "流纹岩")
        }
    }
    val 流纹岩水 = ShallowLiquid("lipariteWater", 流纹岩).apply {
        bundle {
            desc(zh_CN, "流纹岩水")
        }
    }
    val 流纹岩墙: Block = StaticWall("lipariteWall").apply {
        bundle {
            desc(zh_CN, "流纹岩墙")
        }
    }
    val 潮汐水石: Block = Floor("nightTideStoneWater").apply {
        cacheLayer = CacheLayer.water
        liquidDrop = Liquids.water
        bundle {
            desc(zh_CN, "潮汐水石")
        }
    }
    val 潮汐石: Block = Floor("nightTideStone").apply {
        bundle {
            desc(zh_CN, "潮汐石")
        }
    }
    val 潮汐石墙: Block = StaticWall("nightTideStoneWall").apply {
        bundle {
            desc(zh_CN, "潮汐石墙")
        }
    }
    val 潮汐喷口 = SteamVent("nightTideStone-vent").apply {
        parent = 潮汐石.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
        bundle {
            desc(zh_CN, "潮汐喷口")
        }
    }
    val 侵蚀层地: Block = Floor("erosionalSlate").apply {
        bundle {
            desc(zh_CN, "侵蚀层地")
        }
    }
    val 侵蚀层地水 = ShallowLiquid("erosionalSlateWater", 侵蚀层地).apply {
        bundle {
            desc(zh_CN, "侵蚀层地水")
        }
    }
    val 侵蚀层地墙: Block = StaticWall("erosionalSlateWall").apply {
        bundle {
            desc(zh_CN, "侵蚀层地墙")
        }
    }
    val 燃素晶簇 = TallBlock("phlogistonCrystalCluster").apply {
        bundle {
            desc(zh_CN, "燃素晶簇")
        }
    }
    val 晶石地: Block = Floor("crystalStone").apply {
        bundle {
            desc(zh_CN, "晶石地")
        }
    }
    val 晶石地水 = ShallowLiquid("crystalStoneWater", 晶石地).apply {
        bundle {
            desc(zh_CN, "晶石地水")
        }
    }
    val 晶石墙: Block = StaticWall("crystalStoneWall").apply {
        bundle {
            desc(zh_CN, "晶石墙")
        }
    }
    val 幽灵簇: Block = Seaweed("clusterGhosts").apply {
        bundle {
            desc(zh_CN, "幽灵簇")
        }
    }
    val 幽冥蕨 = TallBlock("ghostGrassFern").apply {
        bundle {
            desc(zh_CN, "幽冥蕨")
        }
    }
    val 缠怨花 = TreeBlock("ghostGrassFlower").apply {
        bundle {
            desc(zh_CN, "缠怨花")
        }
    }
    val 幽灵草: Block = Floor("ghostGrass").apply {
        bundle {
            desc(zh_CN, "幽灵草")
        }
    }
    val 幽灵草水 = ShallowLiquid("ghostGrassWater", 幽灵草).apply {
        bundle {
            desc(zh_CN, "幽灵草水")
        }
    }
    val 幽灵草深水 = ShallowDeepLiquid("ghostGrassDeepWater", 幽灵草).apply {
        bundle {
            desc(zh_CN, "幽灵草深水")
        }
    }
    val 幽灵草墙: Block = StaticWall("ghostGrassWall").apply {
        bundle {
            desc(zh_CN, "幽灵草墙")
        }
    }
    val 灰烬地: Block = Floor("ash").apply {
        bundle {
            desc(zh_CN, "灰烬地")
        }
    }
    val 灰烬地水 = ShallowLiquid("ashWater", 灰烬地).apply {
        bundle {
            desc(zh_CN, "灰烬地水")
        }
    }
    val 灰烬墙: Block = StaticWall("ashWall").apply {
        bundle {
            desc(zh_CN, "灰烬墙")
        }
    }
    val 钢铁地板1: Block = Floor("steelFloor1").apply {
        bundle {
            desc(zh_CN, "钢铁地板1")
        }
    }
    val 钢铁墙1: Block = StaticWall("steelFloorWall1").apply {
        bundle {
            desc(zh_CN, "钢铁墙1")
        }
    }
    val 钢铁地板2: Block = Floor("steelFloor2").apply {
        bundle {
            desc(zh_CN, "钢铁地板")
        }
    }
    val 钢铁墙2: Block = StaticWall("steelFloorWall2").apply {
        bundle {
            desc(zh_CN, "钢铁墙")
        }
    }
    val 跨界钢板: Block = TiledFloor("bridgeSteel", 9).apply {
        bundle {
            desc(zh_CN, "跨界钢板")
        }
    }
    val 跨界钢板墙: Block = StaticWall("bridgeSteelWall").apply {
        bundle {
            desc(zh_CN, "跨界钢板墙")
        }
    }
    val 新月岩: Block = Floor("crescent").apply {
        bundle {
            desc(zh_CN, "新月岩")
        }
    }
    val 新月岩水 = ShallowLiquid("crescentWater", 新月岩).apply {
        bundle {
            desc(zh_CN, "新月岩水")
        }
    }
    val 新月岩墙: Block = StaticWall("crescentWall").apply {
        bundle {
            desc(zh_CN, "新月岩墙")
        }
    }
    val 新月喷口 = SteamVent("crescent-vent").apply {
        effectSpacing = 30f
        effect = Effect(140f) { e ->
            Draw.color(Color.valueOf("acb4eb"), Color.valueOf("d8ddff"), e.fin())
            Draw.alpha(e.fslope() * 0.78f)
            val length = 3f + e.finpow() * 10f
            Fx.rand.setSeed(e.id.toLong())
            for (i in 0..<Fx.rand.random(3, 5)) {
                Fx.v.trns(Fx.rand.random(360f), Fx.rand.random(length))
                Fill.circle(e.x + Fx.v.x, e.y + Fx.v.y, Fx.rand.random(1.2f, 3.5f) + e.fslope() * 1.1f)
            }
        }.layer(Layer.darkness - 1)
        parent = 新月岩.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
        bundle {
            desc(zh_CN, "新月喷口")
        }
    }
    val 凌冰尖刺: Block = TallBlock("tortureIceSpikes").apply {
        bundle {
            desc(zh_CN, "凌冰尖刺")
        }
    }
    val 凌冰石块 = Prop("tortureIceStone").apply {
        bundle {
            desc(zh_CN, "凌冰石块")
        }
    }
    val 霜寒草: Block = Prop("frostbiteGrass").apply {
        bundle {
            desc(zh_CN, "霜寒草")
        }
    }
    val 凌冰: Block = Floor("tortureIce").apply {
        bundle {
            desc(zh_CN, "凌冰")
        }
    }
    val 凌冰水 = ShallowLiquid("tortureIceWater", 凌冰).apply {
        bundle {
            desc(zh_CN, "凌冰水")
        }
    }
    val 凌冰深水 = ShallowDeepLiquid("tortureIceDeepWater", 凌冰).apply {
        bundle {
            desc(zh_CN, "凌冰深水")
        }
    }
    val 凌冰墙: Block = StaticWall("tortureIceWall").apply {
        bundle {
            desc(zh_CN, "凌冰墙")
        }
    }
    val 血浅滩: Block = Floor("bloodShoal").apply {
        albedo = 0.9f
        shallow = true
        isLiquid = true
        cacheLayer = IceShader.thickBlood
        liquidDrop = ILiquids.浓稠血浆
        statusDuration = 50f
        speedMultiplier = 0.8f
        supportsOverlay = true
        status = IStatus.染血
        statusDuration = 60 * 3f
        setInit {
            wall = 肿瘤墙
        }
        bundle {
            desc(zh_CN, "血浅滩")
        }
    }
    val 血池 = object : Floor("thickBlood") {
        var array = Seq<Dup>()
        val foors = Seq<Tile>()
        val texs = arrayOf(Array(13) {
            IFiles.findPng("thickBloodHubble1-" + (it + 1))
        },
            Array(13) {
                IFiles.findPng("thickBloodHubble2-" + (it + 1))
            }, Array(13) {
                IFiles.findPng("thickBloodHubble3-" + (it + 1))
            })

        override fun init() {
            Events.on(EventType.ResetEvent::class.java) {
                array.clear()
                foors.clear()
            }
            Events.run(EventType.Trigger.draw) {
                array.forEach {
                    it.draw()
                }
            }
            Events.run(EventType.Trigger.update) {
                array.forEach {
                    it.update()
                }
                if (array.size <= foors.size / 5) {
                    foors.random()?.let {
                        Dup(it)
                    }
                }
            }
            super.init()
        }

        override fun drawBase(tile: Tile) {
            foors.addUnique(tile)
            super.drawBase(tile)
        }

        inner class Dup(val tiles: Tile) {
            init {
                array.add(this)
            }

            var offxTime = IceEffects.rand.random(60f)
            var indx = 0
            var inty = Interval(1)
            val offx = IceEffects.rand.random(-4f, 4f)
            val offy = IceEffects.rand.random(-4f, 4f)
            var i = 0f
            val indxtexs = IceEffects.rand.random(0, texs.size - 1)
            fun update() {
                i += Time.delta
                if (i > offxTime && inty[10f]) {
                    if (indx + 1 != 13) indx++ else array.remove(this)
                }
            }

            fun draw() {
                Draw.rect(texs[indxtexs][indx], tiles.drawx() + offx, tiles.drawy() + offy)
            }
        }
    }.apply {
        speedMultiplier = 0.5f
        status = IStatus.染血
        statusDuration = 60 * 3f
        liquidDrop = ILiquids.浓稠血浆
        shallow = true
        isLiquid = true
        cacheLayer = IceShader.thickBlood
        supportsOverlay = true
        setInit {
            wall = 肿瘤墙
        }
        bundle {
            desc(zh_CN, "血池")
        }
    }
    val 深血池: Block = Floor("deepThickBlood").apply {
        speedMultiplier = 0.2f
        liquidDrop = ILiquids.浓稠血浆
        liquidMultiplier = 1.5f
        status = IStatus.染血
        shallow = true
        isLiquid = true
        statusDuration = 60 * 4f
        drownTime = 200f
        cacheLayer = IceShader.thickBlood
        supportsOverlay = true
        setInit {
            wall = 肿瘤墙
        }
        bundle {
            desc(zh_CN, "深血池")
        }
    }
    val 肿瘤地 = Floor("bloodNeoplasma").apply {
        bundle {
            desc(zh_CN, "肿瘤地")
        }
    }
    val 血沙石块 = Prop("bloodSandStone").apply {
        bundle {
            desc(zh_CN, "血沙石块")
        }
    }
    val 殷血粗沙 = Floor("bloodSand").apply {
        bundle {
            desc(zh_CN, "殷血粗沙")
        }
    }
    val 殷血粗沙墙: Block = StaticWall("bloodSandWall").apply {
        bundle {
            desc(zh_CN, "殷血粗沙墙")
        }
    }
    val 骸骨地 = Floor("humanBones").apply {
        bundle {
            desc(zh_CN, "骸骨地")
        }
    }
    val 血痂地 = Floor("bloodScars").apply {
        setInit {
            wall = 肿瘤墙
        }
        bundle {
            desc(zh_CN, "血痂地")
        }
    }
    val 肿瘤墙: Block = StaticWall("bloodNeoplasmaWall").apply {
        bundle {
            desc(zh_CN, "肿瘤墙")
        }
    }
    val 红冰 = object : Floor("redIce") {
        init {
            updateFloor = true
            bundle {
                desc(zh_CN, "红冰")
            }
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
    val 红冰墙: Block = StaticWall("redIceWall").apply {
        bundle {
            desc(zh_CN, "红冰墙")
        }
    }
    val 赤雪 = Floor("bloodIceSnow").apply {
        bundle {
            desc(zh_CN, "赤雪")
        }
    }
    val 红霜石块 = Prop("bloodSnowStone").apply {
        bundle {
            desc(zh_CN, "红霜石块")
        }
    }
    val 红霜 = Floor("bloodSnow").apply {
        bundle {
            desc(zh_CN, "红霜")
        }
    }
    val 红霜墙 = StaticWall("bloodSnowWall").apply {
        bundle {
            desc(zh_CN, "红霜墙")
        }
    }
    val 肿瘤喷口: Block = BloodNeoplasmaVent("bloodNeoplasmaVent").apply {
        parent = 肿瘤地.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
        bundle {
            desc(zh_CN, "肿瘤喷口")
        }
    }
    val 肿瘤井: Block = object : Prop("bloodNeoplasmaWell") {
        override fun drawBase(tile: Tile) {
            Geometry.d8.forEach {
                if (Vars.world.tile(tile.x + it.x, tile.y + it.y)?.block() != this) return
            }
            super.drawBase(tile)
        }
    }.apply {
        breakable = false
        alwaysReplace = false
        instantDeconstruct = false
        unitMoveBreakable = false
        bundle {
            desc(zh_CN, "肿瘤井")
        }
    }
    val 肉瘤菇: Block = TallBlock("bloodBall").apply {
        size = 1
        bundle {
            desc(zh_CN, "肉瘤菇")
        }
    }
    val 摄魂墙: Block = Block("soulCapturing").apply {
        solid = true
        breakable = true
        bundle {
            desc(zh_CN, "摄魂墙")
        }
    }

    //endregion
    //region 防御
    val 铬墙: Block = Wall("chromeWall").apply {
        health = 450
        size = 1
        requirements(Category.defense, ItemStack.with(IItems.铬锭, 6))
        bundle {
            desc(zh_CN, "铬墙")
        }
    }
    val 大型铬墙: Block = Wall("chromeWallLarge").apply {
        size = 2
        health = 铬墙.health * 4
        requirements(Category.defense, ItemStack.with(IItems.铬锭, 6 * 4))
        bundle {
            desc(zh_CN, "大型铬墙")
        }
    }
    val 碳钢墙: Block = Wall("carbonSteelWall").apply {
        size = 1
        armor = 5f
        health = 720
        chanceDeflect = 0.1f
        requirements(Category.defense, ItemStack.with(IItems.高碳钢, 3, IItems.低碳钢, 3))
        bundle {
            desc(zh_CN, "碳钢墙")
        }
    }
    val 大型碳钢墙: Block = Wall("carbonSteelWallLarge").apply {
        size = 2
        armor = 5f
        chanceDeflect = 0.15f
        health = 碳钢墙.health * size * size
        requirements(Category.defense,
            ItemStack.with(IItems.高碳钢, 3 * size * size, IItems.低碳钢, 3 * size * size))
        bundle {
            desc(zh_CN, "大型碳钢墙")
        }
    }
    val 流金墙: Block = Wall("fluxGoldWall").apply {
        size = 1
        armor = 5f
        health = 1000
        requirements(Category.defense, ItemStack.with(IItems.金锭, 10))
        bundle {
            desc(zh_CN, "流金墙", "熔融金锭构筑的壁垒,随时间缓慢自愈", "财富值++")
        }
    }
    val 大型流金墙: Block = Wall("fluxGoldWallLarge").apply {
        size = 2
        armor = 5f
        health = 1000
        requirements(Category.defense, ItemStack.with(IItems.金锭, 10))
        bundle {
            desc(zh_CN, "大型流金墙", "熔融金锭构筑的壁垒,随时间缓慢自愈", "财富值++")
        }
    }
    val 钴钢墙: Block = Wall("cobaltSteelWall").apply {
        size = 1
        health = 700
        requirements(Category.defense, IItems.钴钢, 8)
        bundle {
            desc(zh_CN, "钴钢墙")
        }
    }
    val 大型钴钢墙: Block = Wall("cobaltSteelWallLarge").apply {
        size = 2
        health = 钴钢墙.health * 4
        requirements(Category.defense, IItems.钴钢, 32)
        bundle {
            desc(zh_CN, "大型钴钢墙")
        }
    }

    //endregion
    //region生产
    val 纤汲钻井: Block = IceDrill("deriveDrill").apply {
        tier = 2
        size = 2
        requirements(Category.production, IItems.高碳钢, 10, IItems.低碳钢, 5)
        drillTime = 200f
        bundle {
            desc(zh_CN, "纤汲钻井")
        }
    }
    val 蛮荒钻井: Block = IceDrill("uncivilizedDrill").apply {
        tier = 4
        size = 3
        drillTime = 150f
        requirements(Category.production, ItemStack.with(IItems.硫钴矿, 4, IItems.低碳钢, 32))
        bundle {
            desc(zh_CN, "蛮荒钻井")
        }
    }
    val 曼哈德钻井: Block = IceDrill("manhardDrill").apply {
        tier = 5
        size = 3
        drillTime = 100f
        requirements(Category.production, ItemStack.with(IItems.硫钴矿, 4, IItems.低碳钢, 32))
        bundle {
            desc(zh_CN, "曼哈德钻井")
        }
    }

    ///*endregion生产*/
    //region运输
    val 基础传送带 = Conveyor("baseConveyor").apply {
        size = 1
        speed = 0.2f
        health = 30
        displayedSpeed = speed * 140f
        lazyInit {
            junctionReplacement = 基础交叉器
        }
        requirements(Category.distribution, ItemStack.with(IItems.低碳钢, 1))
        bundle {
            desc(zh_CN, "基础传送带")
        }
    }
    val 钴熠传送带 = IceStackConveyor("cobaltBrightConveyor").apply {
        speed = 50f / 600f
        requirements(Category.distribution, ItemStack.with(IItems.硫钴矿, 20))
        bundle {
            desc(zh_CN, "钴熠传送带")
        }
    }
    val 梯度传送带 = IceStackConveyor("gradedConveyor").apply {
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
        bundle {
            desc(zh_CN, "梯度传送带")
        }
    }
    val 基础路由器: Block = Router("baseRouter").apply {
        size = 1
        health = 70
        requirements(Category.distribution, ItemStack.with(IItems.低碳钢, 5))
        bundle {
            desc(zh_CN, "基础路由器", "将物品平均分配至其他三个方向")
        }
    }
    val 基础交叉器: Block = Junction("baseJunction").apply {
        size = 1
        health = 100
        requirements(Category.distribution, ItemStack.with(IItems.低碳钢, 5))
        bundle {
            desc(zh_CN, "基础交叉器", "两条交叉传送带的桥梁")
        }
    }
    val 转换分类器: Block = Sorter("transformSorter").apply {
        size = 1
        health = 100
        requirements(Category.distribution, IItems.高碳钢, 8, IItems.低碳钢, 6)
        bundle {
            desc(zh_CN, "转换分类器", "通过配置调整分类状态")
        }
    }
    val 转换溢流门: Block = TransformOverflowGate("transformOverflowGate").apply {
        size = 1
        health = 200
        requirements(Category.distribution, IItems.高碳钢, 8, IItems.低碳钢, 6)
        bundle {
            desc(zh_CN, "转换溢流门", "通过配置调整溢流状态")
        }
    }
    val 传输节点: Block = TransferNode("transferNode").apply {
        size = 1
        health = 200
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 5))
        bundle {
            desc(zh_CN, "传输节点")
        }
    }
    val 物流枢纽: Block = LogisticsHub("logisticsHub").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 10))
        bundle {
            desc(zh_CN, "物流枢纽")
        }
    }
    val 枢纽管道: Block = HubConduit("hubConduit").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
        bundle {
            desc(zh_CN, "枢纽管道")
        }
    }
    val 物流输入器: Block = LogisticsInput("logisticsInput").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
        bundle {
            desc(zh_CN, "物流输入器")
        }
    }
    val 物流输出器: Block = LogisticsOutput("logisticsOutput").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
        bundle {
            desc(zh_CN, "物流输出器")
        }
    }
    val 无人机供货端: Block = DroneDeliveryTerminal("droneTeliveryTerminal").apply {
        squareSprite = false
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))

        bundle {
            desc(zh_CN, "无人机供货端")
        }
    }
    val 无人机需求端: Block = DroneReceivingRnd("droneReceivingRnd").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))

        bundle {
            desc(zh_CN, "无人机需求端")
        }
    }
    val 随机源: Block = Randomer("randomSource").apply {
        requirements(Category.distribution, ItemStack.with(IItems.铜锭, 1))
        bundle {
            desc(zh_CN, "随机源", "随机输出所有资源")
        }
    }

    /*endregion运输*/
    /*region 液体*/
    val 挖掘塔: Block = MinerTower("minerTower").apply {
        bundle {
            desc(zh_CN, "挖掘塔")
        }
    }
    val 泵腔: Block = pumpChamber("pumpChamber").apply {
        requirements(Category.liquid, ItemStack.with(IItems.肌腱, 40, IItems.碎骨, 10, IItems.无名肉块, 60))
        bundle {
            desc(zh_CN, "泵腔")
        }
    }
    val 动力泵: Block = Pump("kineticPump").apply {
        size = 1
        squareSprite = false
        requirements(Category.liquid, IItems.高碳钢, 40, IItems.锌锭, 10)
        bundle {
            desc(zh_CN, "动力泵")
        }
    }
    val 谐振泵: Block = Pump("resonancePump").apply {
        size = 2
        squareSprite = false
        requirements(Category.liquid, IItems.高碳钢, 40, IItems.锌锭, 10)
        bundle {
            desc(zh_CN, "谐振泵")
        }
    }
    val 异质析取器 = SolidPump("solutePump").apply {
        size = 3
        pumpAmount = 0.2f
        squareSprite = false
        liquidCapacity = 40f
        result = ILiquids.异溶质
        attribute = Attribute.water
        consumePower(2f)
        updateEffect = Effect(50f) { e ->
            Draw.color(Pal.lancerLaser)
            randLenVectors(
                e.id.toLong(), 4, 1 + 20f * e.fout(),
            ) { x: Float, y: Float ->
                Draw.color(result.color)
                Fill.circle(e.x + x, e.y + y, e.fout() * 3f)
            }
        }
        drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(result, 2f), DrawDefault(),
            DrawBlurSpin("-rotator", 2f).apply { blurThresh = 2f })
        requirements(Category.liquid, IItems.高碳钢, 10, IItems.锌锭, 20)
        bundle {
            desc(zh_CN, "异质析取器", "从环境中提取${ILiquids.异溶质.localizedName}")
        }
    }
    val 谐振导管: Block = Conduit("resonanceConduit").apply {
        requirements(Category.liquid, IItems.高碳钢, 10, IItems.锌锭, 2)
        bundle {
            desc(zh_CN, "谐振导管")
        }
    }
    val 流金导管 = Conduit("fluxGoldConduit").apply {
        liquidCapacity = 40f
        liquidPressure = 1.025f
        requirements(Category.liquid, IItems.金锭, 10, IItems.锌锭, 2)
        bundle {
            desc(zh_CN, "流金导管")
        }
    }
    val 基础液体路由器 = LiquidRouter("baseLiquidRouter").apply {
        size = 1
        health = 100
        requirements(Category.liquid, IItems.石英玻璃, 5)
        bundle {
            desc(zh_CN, "基础液体路由器", "接受一个方向的液体输入,并平均输出到其他3个方向,可以储存一定量的液体")
        }
    }
    val 基础液体交叉器 = LiquidJunction("baseLiquidJunction").apply {
        size = 1
        health = 80
        requirements(Category.liquid, IItems.石英玻璃, 5)

        bundle {
            desc(zh_CN, "基础液体交叉器")
        }
    }
    val 导管桥 = TransferNode("bridgeConduit").apply {
        rangeb = 6
        hasItems = false
        hasPower = false
        liquidCapacity = 10f
        requirements(Category.liquid, IItems.石英玻璃, 10)
        bundle {
            desc(zh_CN, "导管桥", "在以自我为中心且边长为${2 * rangeb + 1}的正方形范围内,向任意方向传输液体")
        }
    }
    val 长距导管桥 = TransferNode("bridgeConduitLarge").apply {
        rangeb = 10
        hasItems = false
        liquidCapacity = 10f
        consumePower(30f / 60f)
        requirements(Category.liquid, IItems.石英玻璃, 20)

        bundle {
            desc(zh_CN, "长距导管桥",
                "消耗电力,在以自我为中心且边长为${2 * rangeb + 1}的正方形范围内,向任意方向传输液体")
        }
    }
    val 流体容器: Block = LiquidRouter("liquidContainer").apply {
        size = 2
        solid = true
        health = 500
        squareSprite = false
        liquidPadding = 6f / 4f
        liquidCapacity = 800f
        requirements(Category.liquid, IItems.铜锭, 30)
        bundle {
            desc(zh_CN, "流体容器")
        }
    }
    val 流体仓库 = LiquidRouter("liquidStorage").apply {
        size = 3
        solid = true
        health = 1000
        squareSprite = false
        liquidPadding = 6f / 4f
        liquidCapacity = 2000f
        requirements(Category.liquid, IItems.铜锭, 30)
        bundle {
            desc(zh_CN, "流体仓库")
        }
    }
    val 流体枢纽: Block = MultipleLiquidBlock("fluidJunction").apply {
        size = 3
        liquidCapacity = 1000f
        health = size * size * 100
        requirements(Category.liquid, IItems.铜锭, 10)
        bundle {
            desc(zh_CN, "流体枢纽",
                "能将多种流体独立存储于同一单元,有效解决了复杂流水线中的空间占用问题,是高级化生产的必备设施")
        }
    }
    val 流体抽离器: Block = LiquidClassifier("liquidClassifier").apply {
        size = 1
        liquidCapacity = 0f
        requirements(Category.liquid, Items.copper, 1)

        bundle {
            desc(zh_CN, "流体抽离器", "流体枢纽的流体卸载装置,将流体卸载于相邻的可输入建筑,本身并不存储流体")
        }
    }

    //endregion
    //region能量
    val 能量节点: Block = BeamNode("powerNode").apply {
        laser = IFiles.findPng("powerNode-beam")
        laserEnd = IFiles.findPng("powerNode-beam-end")
        requirements(Category.power, IItems.高碳钢, 2, IItems.锌锭, 5, IItems.铜锭, 5)
        laserColor1 = IceColor.b4
        laserColor2 = Color.valueOf("bad7e6")
        consumesPower = true
        outputsPower = true
        health = 90
        range = 10
        fogRadius = 1
        buildCostMultiplier = 2.5f
        consumePowerBuffered(200f)
        bundle {
            desc(zh_CN, "能量节点")
        }
    }
    val 小型能量电池: Block = Battery("smallPowerBattery").apply {
        size = 1
        health = 50
        baseExplosiveness = 1f
        emptyLightColor = IceColor.df
        fullLightColor = IceColor.b4
        consumePowerBuffered(3500f)
        requirements(Category.power, IItems.低碳钢, 5, IItems.高碳钢, 20, IItems.铅锭, 20)
        bundle {
            desc(zh_CN, "小型能量电池")
        }
    }
    val 能量电池: Block = Battery("powerBattery").apply {
        size = 2
        health = 300
        baseExplosiveness = 1f
        emptyLightColor = IceColor.df
        fullLightColor = IceColor.b4
        consumePowerBuffered(15000f)
        requirements(Category.power, IItems.低碳钢, 10, IItems.高碳钢, 20, IItems.黄铜锭, 30, IItems.铅锭, 50)
        bundle {
            desc(zh_CN, "能量电池")
        }
    }
    val 燃烧发电机 = ConsumeGenerator("combustionGenerator").apply {
        powerProduction = 1f
        itemDuration = 120f
        ambientSound = Sounds.smelter
        ambientSoundVolume = 0.03f
        generateEffect = Fx.generatespark
        consume(ConsumeItemFlammable())
        drawer = DrawMulti(DrawDefault(), DrawWarmupRegion())
        requirements(Category.power, IItems.高碳钢, 20, IItems.锌锭, 20)
        bundle {
            desc(zh_CN, "燃烧发电机")
        }
    }
    val 蒸汽冷凝机 = ThermalGenerator("steamCondenser").apply {
        size = 3
        fogRadius = 3
        hasLiquids = true
        attribute = Attribute.steam
        group = BlockGroup.liquids
        displayEfficiencyScale = 1f / 9f
        minEfficiency = 9f - 0.0001f
        powerProduction = 3f / 9f
        displayEfficiency = false
        generateEffect = Fx.turbinegenerate
        effectChance = 0.04f
        ambientSound = Sounds.hum
        ambientSoundVolume = 0.06f
        requirements(Category.power, IItems.高碳钢, 80)
        drawer = DrawMulti(DrawDefault(), DrawBlurSpin("-rotator", 0.6f * 9f).apply {
            blurThresh = 0.01f
        })
        outputLiquid = LiquidStack(Liquids.water, 5f / 60f / 9f)
        liquidCapacity = 20f
        bundle {
            desc(zh_CN, "蒸汽冷凝机")
        }
    }

    //endregion
    //region 工厂
    val 焚烧炉 = Incinerator("incinerator").apply {
        size = 1
        flameColor = IceColor.b4
        consumePower(20 / 60f)
        requirements(Category.crafting, IItems.高碳钢, 20, IItems.铅锭, 5)
        bundle {
            desc(zh_CN, "焚烧炉")
        }
    }
    val 量子蚀刻厂: Block = GenericCrafter("integratedFactory").apply {
        drawers = DrawMulti(DrawRegion("-bottom"), DrawRegion("-top"))
        itemCapacity = 20
        health = 200
        outputItems(IItems.电子元件, 1)
        consumeItems(*ItemStack.with(IItems.单晶硅, 1, IItems.石墨烯, 2, IItems.石英玻璃, 1))
        craftTime = 60f
        craftEffect = MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin,
            IceEffects.hitLaserBlast)
        size = 3
        requirements(Category.crafting, ItemStack.with(IItems.铜锭, 19))
        bundle {
            desc(zh_CN, "量子蚀刻厂", "采用等离子蚀刻技术,在硅晶圆上雕刻出微米级电路,电子工业的基础设施")
        }
    }
    val 单晶硅厂: Block = GenericCrafter("monocrystallineSiliconFactory").apply {
        size = 2
        health = 360
        hasPower = true
        craftTime = 60f
        craftEffect = IceEffects.square(IItems.单晶硅.color)
        consumePower(1.8f)
        outputItems(IItems.单晶硅, 1)
        consumeItems(*ItemStack.with(Items.pyratite, 1, IItems.石英, 3))
        val color = Color.valueOf("ffef99")
        setDrawMulti(DrawRegion("-bottom"), DrawBuild<GenericCrafter.GenericCrafterBuild> {
            Draw.color(color)
            Draw.alpha(warmup)
            Lines.lineAngleCenter(
                x + Mathf.sin(totalProgress(), 6f, Vars.tilesize / 3f * size),
                y,
                90f,
                size * Vars.tilesize / 2f)
            Lines.lineAngleCenter(
                x,
                y + Mathf.sin(totalProgress(), 3f, Vars.tilesize / 3f * size),
                0f,
                size * Vars.tilesize / 2f)
            Draw.color()
        }, DrawDefault(),
            DrawFlame(color))
        requirements(Category.crafting, ItemStack.with(IItems.红冰, 12))
        bundle {
            desc(zh_CN, "单晶硅厂", "使用硫化物和石英矿石生产纯度更高的单晶硅")
        }
    }
    val 铸铜厂: Block = GenericCrafter("copperFoundry").apply {
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
    val 碳控熔炉: MultipleCrafter = MultipleCrafter("carbonSteelFactory").apply {
        bundle {
            desc(zh_CN, "碳控熔炉",
                "通过精确控制碳元素配比,在同一生产线灵活产出高碳钢和低碳钢,稳定的温度控制确保钢材质量始终达标")
        }
        size = 3
        itemCapacity = 20
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
            setInput(ConsumeItems(ItemStack.with(IItems.赤铁矿, 2, IItems.生煤, 3)),
                ConsumePower(90 / 60f, 0f, false))
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
    val 普适冶炼阵列: MultipleCrafter = MultipleCrafter("universalSmelterArray").apply {
        bundle {
            desc(zh_CN, "普适冶炼阵列",
                "核心级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铜,锌,铅等多种金属原料,为后续生产提供稳定的金属供应")
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
        requirements(Category.crafting, IItems.高碳钢, 150, IItems.低碳钢, 70, IItems.铜锭, 30)
    }
    val 特化冶炼阵列: MultipleCrafter = MultipleCrafter("specializedSmelterArray").apply {
        bundle {
            desc(zh_CN, "特化冶炼阵列",
                "进阶级金属处理设施,专门用于将原始矿石转化为高纯度金属锭,高效处理铬,金,钴等多种金属原料,为后续生产提供稳定的金属供应")
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
        requirements(Category.crafting, IItems.高碳钢, 150, IItems.低碳钢, 70, IItems.铜锭, 30)
    }
    val 矿石粉碎机: Block = object : OreMultipleCrafter("mineralCrusher") {
        init {
            squareSprite = false
            hasLiquids = true
            drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(),
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
        setDrawMulti(DrawRegion("-bottom"), DrawDefault())
        requirements(Category.crafting, ItemStack.with(IItems.铜锭, 2))
        bundle {
            desc(zh_CN, "蜂巢陶瓷合成巢",
                "利用基因改造的硅基菌群分泌陶瓷基质,再经激光固化,生产过程中会发出蜂鸣般的共振声", "资源蜜蜂?")
        }
    }
    val 暮白高炉: Block = GenericCrafter("duskFactory").apply {
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
        requirements(Category.crafting, ItemStack.with(IItems.金锭, 200, IItems.钴锭, 70))
        bundle {
            desc(zh_CN, "暮白高炉", "将金属与信仰在苍白焰火中熔合,冶炼蕴含暮光之息的特殊合金")
        }
    }

    //endregion
    //region 其他
    val 基础卸载器 = Unloader("baseUninstalle").apply {
        size = 1
        speed = 60f / 15f
        health = 50
        squareSprite = false
        bundle {
            desc(zh_CN, "基础卸载器", "卸载物品")
        }
        requirements(Category.effect, IItems.高碳钢, 30, IItems.低碳钢, 10, IItems.黄铜锭, 15)
    }
    val 量子卸载器: Block = Unloader("electronicUninstaller").apply {
        size = 1
        speed = 1f
        health = 200
        squareSprite = false
        bundle {
            desc(zh_CN, "量子卸载器", "通过零损耗电子迁移,像虹吸液体般优雅地转移高密度数据流")
        }
        requirements(Category.effect, IItems.电子元件, 25, IItems.单晶硅, 5, IItems.钴锭, 25f)
    }
    val 虔信方垒 = CoreBlock("pietasCornerstone").apply {
        size = 3
        health = 1000
        unitType = IUnitTypes.路西法
        isFirstTier = true
        itemCapacity = 4000
        unitCapModifier = 8
        alwaysUnlocked = true
        buildCostMultiplier = 2f
        requirements(Category.effect, IItems.赤铁矿, 500, IItems.铜锭, 200)
        bundle {
            desc(zh_CN, "虔信方垒")
        }
    }
    val 真菌塔: Block = FungusCore("fungusTower").apply {
        size = 2
        category = Category.effect
        bundle {
            desc(zh_CN, "真菌塔")
        }
    }
    val 定向超速器: Block = OrientationProjector("orientationProjector").apply {
        size = 2
        buildSize = 5
        range = 8 * 20f
        bundle {
            desc(zh_CN, "定向超速器")
        }
    }
    val 遗弃资源箱: Block = ResBox("resBox").apply {
        bundle {
            desc(zh_CN, "遗弃资源箱")
        }
    }
    val 遗弃匣: Block = LostBox("lostBox").apply {
        size = 2
        envEnabled = Env.any
        category = Category.effect
        bundle {
            desc(zh_CN, "遗弃匣")
        }
    }
    val 传输矿仓: Block = ItemExtractor("conveyOreWar").apply {
        size = 2
        buildSize = 8
        range = 10 * 8f
        allowLink.add(纤汲钻井)
        requirements(Category.effect, ItemStack.with(IItems.低碳钢, 30))
        bundle {
            desc(zh_CN, "传输矿仓")
        }
    }
    val 开采核心: Block = object : CoreBlock("minerCore") {
        init {
            size = 3
            isFirstTier = true
            health = 5000
            itemCapacity = 2000
            requirements(Category.effect, IItems.低碳钢, 4000, IItems.锌锭, 1500)
        }
    }
    val 血肉枢纽: Block = FleshAndBloodCoreBlock("fleshAndBloodhinge").apply {
        health = -1
        size = 4
        itemCapacity = 6000
        squareSprite = false
        requirements(Category.effect, IItems.无名肉块, 2300, IItems.碎骨, 2000)
        bundle {
            desc(zh_CN, "血肉枢纽")
        }
    }
    val 基础实验室: Block = Laboratory("laboratory").apply {
        consumePower(100f / 60)
        bundle {
            desc(zh_CN, "基础实验室")
        }
    }
    val 碎冰: Block = ItemTurret("trashIce").apply {
        bundle {
            desc(zh_CN, "碎冰")
        }
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
        shootSound = ISounds.laser1
        shootEffect = Effect(8.0f) { e: EffectContainer ->
            Draw.color(IceColor.b4, Color.white, e.fin())
            val w = 1.0f + 5.0f * e.fout()
            Drawf.tri(e.x, e.y, w, 15.0f * e.fout(), e.rotation)
            Drawf.tri(e.x, e.y, w, 3.0f * e.fout(), e.rotation + 180.0f)
        }
        ammo(IItems.硫钴矿, BasicBulletType(5f, 9f).apply {
            width = 2f
            height = 9f
            lifetime = 30f
            ammoMultiplier = 2f
            despawnEffect = IceEffects.baseHitEffect
            hitEffect = despawnEffect
            trailColor = IceColor.b4
            backColor = IceColor.b4
            hitColor = IceColor.b4
            frontColor = IceColor.b4
        })
        requirements(Category.turret, ItemStack.with(IItems.铬铁矿, 10, IItems.低碳钢, 20))
        drawer = DrawTurret().apply {
            parts.add(RegionPart("-barrel").apply {
                progress = PartProgress.recoil
                under = true
                heatColor = IceColor.b4
                heatProgress = PartProgress.recoil
                moveY = -1.5f
            })
        }
    }
    val 神矢: Block = PowerTurret("divineArrow").apply {
        bundle {
            desc(zh_CN, "神矢")
        }
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
                color = IceColor.b4
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
                color = IceColor.b4
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
        shootEffect = IceEffects.squareAngle(range = 30f, color1 = IceColor.b4, color2 = Color.white)
        shootType = MissileBulletType(6f, 30f).apply {
            splashDamageRadius = 30f
            splashDamage = 30f * 1.5f
            lifetime = 45f
            trailLength = 20
            trailWidth = 1.5f
            trailColor = IceColor.b4
            backColor = IceColor.b4
            hitColor = IceColor.b4
            frontColor = IceColor.b4
            despawnEffect = IceEffects.blastExplosion(IceColor.b4)
            hitEffect = despawnEffect
        }
        range = shootType.speed * shootType.lifetime
    }
    val 绪终: Block = ItemTurret("thinkEnd").apply {
        bundle {
            desc(zh_CN, "绪终")
        }
        size = 5
        shoot.apply {
            firstShotDelay = 120f
            recoils = 1
            reload = 120f
            shootWarmupSpeed = 0.05f
        }
        ammo(IItems.暮光合金, IceBasicBulletType(4f, 4f))
        requirements(Category.turret, ItemStack.with(IItems.铜锭, 10, IItems.单晶硅, 5))
        drawer = DrawTurret().apply {
            parts.add(RegionPart("4-l").apply {
                moveY = -4f
                moveX = -8f
                moveRot = 60f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
            parts.add(RegionPart("4-r").apply {
                moveY = -4f
                moveX = 8f
                moveRot = -60f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
            parts.add(RegionPart("1").apply {
                moveY = 2f
                progress = PartProgress.warmup.curve(Interp.pow2)
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
            parts.add(RegionPart("2-l").apply {
                moveY = -2f
                moveRot = 25f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup.curve(Interp.pow5In)
            })
            parts.add(RegionPart("2-r").apply {
                moveY = -2f
                moveRot = -25f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup.curve(Interp.pow5In)
            })

            parts.add(RegionPart("3").apply {
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
            bundle {
                desc(zh_CN, "绪终")
            }
        }
    }
    //endregion
}