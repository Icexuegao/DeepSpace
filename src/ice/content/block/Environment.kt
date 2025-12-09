package ice.content.block

import arc.Core
import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.geom.Geometry
import arc.struct.Seq
import arc.util.Interval
import arc.util.Time
import ice.content.ILiquids
import ice.content.IStatus
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.world.ContentLoad
import ice.shader.IceShader
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.environment.*
import ice.world.meta.Attributes
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.game.EventType
import mindustry.gen.Sounds
import mindustry.graphics.CacheLayer
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.Attribute

@Suppress("unused")
object Environment : ContentLoad {
    val 多叶草 = Prop("leafyGrass").apply {
        bundle {
            desc(zh_CN, "多叶草")
        }
    }
    val 地笼草 = Prop("cageGrass").apply {
        bundle {
            desc(zh_CN, "地笼草")
        }
    }
    val 枯棕枝 = Prop("deadwoodGrass").apply {
        bundle {
            desc(zh_CN, "枯棕枝")
        }
    }
    val 绿羽 = Prop("featherGrass").apply {
        bundle {
            desc(zh_CN, "绿羽")
        }
    }
    val 草嫣红 = Prop("flowers1").apply {
        bundle {
            desc(zh_CN, "草嫣红")
        }
    }
    val 绯叶绮 = Prop("flowers2").apply {
        bundle {
            desc(zh_CN, "绯叶绮")
        }
    }
    val 叶嫣粉 = Prop("flowers3").apply {
        bundle {
            desc(zh_CN, "叶嫣粉")
        }
    }
    val 血孢子丛 = Prop("bloodNeoplasmSporophore", Sounds.plantBreak).apply {
        bundle {
            desc(zh_CN, "血孢子丛")
        }
    }
    val 血晶尖刺 = TallBlock("bloodCrystalSpikes").apply {
        bundle {
            desc(zh_CN, "血晶尖刺")
        }
    }
    val 殷红树 = TreeBlock("bloodSporophoreTree").apply {
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
    val 利芽 = TallBlock("edgeBud").apply {
        bundle {
            desc(zh_CN, "利芽")
        }
    }
    val 红冰石 = Prop("redIceStone").apply {
        bundle {
            desc(zh_CN, "红冰石")
        }
    }
    val 灵液 = Floor("ichor").apply {
        liquidDrop = ILiquids.灵液
        cacheLayer = IceShader.ichor
        drownTime = 200f
        speedMultiplier = 0.4f
        isLiquid = true
        bundle {
            desc(zh_CN, "灵液")
        }
    }
    val 软红冰 = Floor("softRedIce").apply {
        cacheLayer = IceShader.softRedIceCache
        speedMultiplier = 0.5f
        bundle {
            desc(zh_CN, "软红冰")
        }
    }
    val 绿羽地 = Floor("sod").apply {
        bundle {
            desc(zh_CN, "绿羽地")
        }
    }
    val 绿羽墙 = StaticWall("sodWall").apply {
        bundle {
            desc(zh_CN, "绿羽墙")
        }
    }
    val 金珀沙 = Floor("goldPearlGrit").apply {
        bundle {
            desc(zh_CN, "金珀沙")
        }
    }
    val 金珀沙水 = ShallowLiquid("goldPearlGritWater", 金珀沙).apply {
        bundle {
            desc(zh_CN, "金珀沙水")
        }
    }
    val 金珀沙墙 = StaticWall("goldPearlGritWall").apply {
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
    val 皎月银沙墙 = StaticWall("silverSandWall").apply {
        bundle {
            desc(zh_CN, "皎月银沙墙")
        }
    }
    val 风蚀沙柱 = TallBlock("windErodedSandPillar").apply {
        bundle {
            desc(zh_CN, "风蚀沙柱")
        }
    }
    val 风蚀砂地 = Floor("windErodedGrit").apply {
        bundle {
            desc(zh_CN, "风蚀砂地")
        }
    }
    val 风蚀沙地 = Floor("windErodedSand").apply {
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
    val 风蚀沙墙 = StaticWall("windErodedSandWall").apply {
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
    val 光辉板岩 = Floor("brillianceSlate").apply {
        bundle {
            desc(zh_CN, "光辉板岩")
        }
    }
    val 光辉板岩水 = ShallowLiquid("brillianceSlateWater", 光辉板岩).apply {
        bundle {
            desc(zh_CN, "光辉板岩水")
        }
    }
    val 光辉板岩墙 = StaticWall("brillianceSlateWall").apply {
        bundle {
            desc(zh_CN, "光辉板岩墙")
        }
    }
    val 云英石柱 = Prop("greisenPillar").apply {
        bundle {
            desc(zh_CN, "云英石柱")
        }
    }
    val 云英岩 = Floor("greisen").apply {
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
    val 云英岩墙 = StaticWall("greisenWall").apply {
        bundle {
            desc(zh_CN, "云英岩墙")
        }
    }
    val 红土石块 = Prop("redDirStone").apply {
        bundle {
            desc(zh_CN, "红土石块")
        }
    }
    val 红土 = Floor("redDir").apply {
        bundle {
            desc(zh_CN, "红土")
        }
    }
    val 红土墙 = StaticWall("redDirWall").apply {
        bundle {
            desc(zh_CN, "红土墙")
        }
    }
    val 流纹岩 = Floor("liparite").apply {
        bundle {
            desc(zh_CN, "流纹岩")
        }
    }
    val 流纹岩水 = ShallowLiquid("lipariteWater", 流纹岩).apply {
        bundle {
            desc(zh_CN, "流纹岩水")
        }
    }
    val 流纹岩墙 = StaticWall("lipariteWall").apply {
        bundle {
            desc(zh_CN, "流纹岩墙")
        }
    }
    val 潮汐水石 = Floor("nightTideStoneWater").apply {
        cacheLayer = CacheLayer.water
        liquidDrop = Liquids.water
        bundle {
            desc(zh_CN, "潮汐水石")
        }
    }
    val 潮汐石 = Floor("nightTideStone").apply {
        bundle {
            desc(zh_CN, "潮汐石")
        }
    }
    val 潮汐石墙 = StaticWall("nightTideStoneWall").apply {
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
    val 侵蚀层地 = Floor("erosionalSlate").apply {
        bundle {
            desc(zh_CN, "侵蚀层地")
        }
    }
    val 侵蚀层地水 = ShallowLiquid("erosionalSlateWater", 侵蚀层地).apply {
        bundle {
            desc(zh_CN, "侵蚀层地水")
        }
    }
    val 侵蚀层地墙 = StaticWall("erosionalSlateWall").apply {
        bundle {
            desc(zh_CN, "侵蚀层地墙")
        }
    }
    val 燃素晶簇 = TallBlock("phlogistonCrystalCluster").apply {
        bundle {
            desc(zh_CN, "燃素晶簇")
        }
    }
    val 晶石地 = Floor("crystalStone").apply {
        bundle {
            desc(zh_CN, "晶石地")
        }
    }
    val 晶石地水 = ShallowLiquid("crystalStoneWater", 晶石地).apply {
        bundle {
            desc(zh_CN, "晶石地水")
        }
    }
    val 晶石墙 = StaticWall("crystalStoneWall").apply {
        bundle {
            desc(zh_CN, "晶石墙")
        }
    }
    val 幽灵簇 = Seaweed("clusterGhosts").apply {
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
    val 幽灵草 = Floor("ghostGrass").apply {
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
    val 幽灵草墙 = StaticWall("ghostGrassWall").apply {
        bundle {
            desc(zh_CN, "幽灵草墙")
        }
    }
    val 灰烬地 = Floor("ash").apply {
        bundle {
            desc(zh_CN, "灰烬地")
        }
    }
    val 灰烬地水 = ShallowLiquid("ashWater", 灰烬地).apply {
        bundle {
            desc(zh_CN, "灰烬地水")
        }
    }
    val 灰烬墙 = StaticWall("ashWall").apply {
        bundle {
            desc(zh_CN, "灰烬墙")
        }
    }
    val 钢铁地板1 = Floor("steelFloor1").apply {
        bundle {
            desc(zh_CN, "钢铁地板1")
        }
    }
    val 钢铁墙1 = StaticWall("steelFloorWall1").apply {
        bundle {
            desc(zh_CN, "钢铁墙1")
        }
    }
    val 钢铁地板2 = Floor("steelFloor2").apply {
        bundle {
            desc(zh_CN, "钢铁地板")
        }
    }
    val 钢铁墙2 = StaticWall("steelFloorWall2").apply {
        bundle {
            desc(zh_CN, "钢铁墙")
        }
    }
    val 精钢甲板 = Floor("steelFloor3").apply {
        bundle {
            desc(zh_CN, "精钢甲板")
        }
    }
    val 跨界钢板 = TiledFloor("bridgeSteel", 9).apply {
        bundle {
            desc(zh_CN, "跨界钢板")
        }
    }
    val 跨界钢板墙 = StaticWall("bridgeSteelWall").apply {
        bundle {
            desc(zh_CN, "跨界钢板墙")
        }
    }
    val 供能板 = Floor("powerBoard").apply {
        bundle {
            desc(zh_CN, "供能板")
        }
    }
    val 供能墙 = StaticWall("powerWall").apply {
        bundle {
            desc(zh_CN, "供能墙")
        }
    }
    val 诅咒之地 = Floor("curseLand").apply {
        bundle {
            desc(zh_CN, "诅咒之地")
        }
    }
    val 诅咒之墙 = StaticWall("curseWall").apply {
        bundle {
            desc(zh_CN, "诅咒之墙")
        }
    }
    val 新月岩 = Floor("crescent").apply {
        bundle {
            desc(zh_CN, "新月岩")
        }
    }
    val 新月岩水 = ShallowLiquid("crescentWater", 新月岩).apply {
        bundle {
            desc(zh_CN, "新月岩水")
        }
    }
    val 新月岩墙 = StaticWall("crescentWall").apply {
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
    val 凌冰尖刺 = TallBlock("tortureIceSpikes").apply {
        bundle {
            desc(zh_CN, "凌冰尖刺")
        }
    }
    val 凌冰石块 = Prop("tortureIceStone").apply {
        bundle {
            desc(zh_CN, "凌冰石块")
        }
    }
    val 霜寒草 = Prop("frostbiteGrass").apply {
        bundle {
            desc(zh_CN, "霜寒草")
        }
    }
    val 凌冰 = Floor("tortureIce").apply {
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
    val 凌冰墙 = StaticWall("tortureIceWall").apply {
        bundle {
            desc(zh_CN, "凌冰墙")
        }
    }
    val 血浅滩 = Floor("bloodShoal").apply {
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
        }, Array(13) {
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
    val 深血池 = Floor("deepThickBlood").apply {
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
    val 浓稠深血池= Floor("denseDeepThickBlood").apply {
        speedMultiplier = 0.4f
        liquidDrop = ILiquids.浓稠血浆
        liquidMultiplier = 1.5f
        status = IStatus.染血
        shallow = true
        isLiquid = true
        statusDuration = 60 * 4f
        drownTime = 200f
        cacheLayer = IceShader.bloodShallow
        setInit {
            wall = 肿瘤墙
        }
        bundle {
            desc(zh_CN, "浓稠深血池")
        }
    }
    val 肿瘤地 = Floor("bloodNeoplasma").apply {
        decoration = 血孢子丛
        bundle {
            desc(zh_CN, "肿瘤地")
        }
    }
    val 碎骨地 = Floor("brokenBone").apply {
        bundle {
            desc(zh_CN, "碎骨地")
        }
    }
    val 碎骨墙 = StaticWall("brokenBoneWall").apply {
        bundle {
            desc(zh_CN, "碎骨墙")
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
    val 殷血粗沙墙 = StaticWall("bloodSandWall").apply {
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
    val 血痂岩 = Floor("bloodScarsStone").apply {
        bundle {
            desc(zh_CN, "血痂岩")
        }
    }
    val 血蚀岩石块 = Prop("bloodmoriteStone").apply {
        bundle {
            desc(zh_CN, "血蚀岩石块")
        }
    }
    val 血蚀岩 = Floor("bloodmorite").apply {
        decoration = 血蚀岩石块
        bundle {
            desc(zh_CN, "血蚀岩")
        }
    }
    val 血蚀墙 = StaticWall("bloodmoriteWall").apply {
        bundle {
            desc(zh_CN, "血蚀墙")
        }
    }
    val 肿瘤墙 = StaticWall("bloodNeoplasmaWall").apply {
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
    val 红冰墙 = StaticWall("redIceWall").apply {
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
    val 肿瘤喷口 = BloodNeoplasmaVent("bloodNeoplasmaVent").apply {
        parent = 肿瘤地.also { blendGroup = it }
        attributes.set(Attribute.steam, 1f)
        bundle {
            desc(zh_CN, "肿瘤喷口")
        }
    }
    val 肿瘤井 = object : Prop("bloodNeoplasmaWell") {
        override fun drawBase(tile: Tile) {
            Geometry.d8.forEach {
                if (Vars.world.tile(tile.x + it.x, tile.y + it.y)?.block() != this) return
            }
            super.drawBase(tile)
        }
    }.apply {
        breakable = false
        alwaysReplace = false
        instantDeconstruct = true
        allowRectanglePlacement = true
        unitMoveBreakable = false
        bundle {
            desc(zh_CN, "肿瘤井")
        }
    }
    val 肉瘤菇 = TallBlock("bloodBall").apply {
        bundle {
            desc(zh_CN, "肉瘤菇")
        }
    }
    val 血蚀囊胚=TallBlock("bloodBlastocyst").apply {
        bundle {
            desc(zh_CN, "血蚀囊胚")
        }
    }
    val 摄魂墙 = Block("soulCapturing").apply {
        solid = true
        breakable = true
        bundle {
            desc(zh_CN, "摄魂墙")
        }
    }
}
