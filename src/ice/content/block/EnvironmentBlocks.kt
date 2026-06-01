package ice.content.block

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.geom.Geometry
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.game.EventType.addContentInitEvent
import ice.graphics.IceColor
import ice.shader.IceShader
import ice.type.Dup
import ice.world.content.blocks.environment.*
import ice.world.meta.IAttribute
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.Attribute
import universecore.ui.bundle.localization

@Suppress("unused")
object EnvironmentBlocks {
  fun load() = Unit
  val 多叶草 = Prop("leafyGrass").apply {
    localization {
      zh_CN {
        localizedName = "多叶草"
      }
      en {
        localizedName = "Leafy Grass"
      }
    }
  }
  val 地笼草 = Prop("cageGrass").apply {
    localization {
      zh_CN {
        localizedName = "地笼草"
      }
      en {
        localizedName = "Cage Grass"
      }
    }
  }
  val 枯棕枝 = Prop("deadwoodGrass").apply {
    localization {
      zh_CN {
        localizedName = "枯棕枝"
      }
      en {
        localizedName = "Deadwood Grass"
      }
    }
  }
  val 绿羽 = Prop("featherGrass").apply {
    localization {
      zh_CN {
        localizedName = "绿羽"
      }
      en {
        localizedName = "Feather Grass"
      }
    }
  }
  val 草嫣红 = Prop("springGrassRed").apply {
    localization {
      zh_CN {
        localizedName = "草嫣红"
      }
      en {
        localizedName = "Spring Grass Red"
      }
    }
  }
  val 绯叶绮 = Prop("scarletLeaf").apply {
    localization {
      zh_CN {
        localizedName = "绯叶绮"
      }
      en {
        localizedName = "Scarlet Leaf"
      }
    }
  }
  val 叶嫣粉 = Prop("leafBlush").apply {
    localization {
      zh_CN {
        localizedName = "叶嫣粉"
      }
      en {
        localizedName = "Leaf Blush"
      }
    }
  }
  val 血孢子丛 = Prop("bloodNeoplasmSporophore", Sounds.plantBreak).apply {
    localization {
      zh_CN {
        localizedName = "血孢子丛"
      }
      en {
        localizedName = "Blood Neoplasm Sporophore"
      }
    }
  }
  val 血晶尖刺 = TallBlock("bloodCrystalSpikes").apply {
    localization {
      zh_CN {
        localizedName = "血晶尖刺"
      }
      en {
        localizedName = "Blood Crystal Spikes"
      }
    }
  }
  val 殷红树 = TreeBlock("bloodSporophoreTree").apply {
    attributes[IAttribute.血囊孢子] = 1f
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
    localization {
      zh_CN {
        localizedName = "殷红树"
      }
      en {
        localizedName = "Crimson Sporophore Tree"
      }
    }
  }
  val 利芽 = TallBlock("edgeBud").apply {
    shadowOffset = -1f
    localization {
      zh_CN {
        localizedName = "利芽"
      }
      en {
        localizedName = "Edge Bud"
      }
    }
  }
  val 红冰石 = Prop("redIceStone").apply {
    localization {
      zh_CN {
        localizedName = "红冰石"
      }
      en {
        localizedName = "Red Ice Stone"
      }
    }
  }
  val 灵液 = Floor("ichor").apply {
    liquidDrop = ILiquids.灵液
    cacheLayer = IceShader.ichor
    drownTime = 200f
    speedMultiplier = 0.4f
    isLiquid = true
    localization {
      zh_CN {
        localizedName = "灵液"
      }
      en {
        localizedName = "Ichor"
      }
    }
  }
  val 软红冰 = Floor("softRedIce").apply {
    cacheLayer = IceShader.softRedIceCache
    speedMultiplier = 0.5f
    localization {
      zh_CN {
        localizedName = "软红冰"
      }
      en {
        localizedName = "Soft Red Ice"
      }
    }
  }
  val 绿羽地 = Floor("sod").apply {
    localization {
      zh_CN {
        localizedName = "绿羽地"
      }
      en {
        localizedName = "Sod"
      }
    }
  }
  val 绿羽墙 = StaticWall("sodWall").apply {
    localization {
      zh_CN {
        localizedName = "绿羽墙"
      }
      en {
        localizedName = "Sod Wall"
      }
    }
  }
  val 金珀沙 = Floor("goldPearlGrit").apply {
    localization {
      zh_CN {
        localizedName = "金珀沙"
      }
      en {
        localizedName = "Gold Pearl Grit"
      }
    }
    itemDrop = IItems.金珀沙
  }
  val 金珀沙水 = ShallowLiquid("goldPearlGritWater", 金珀沙).apply {
    localization {
      zh_CN {
        localizedName = "金珀沙水"
      }
      en {
        localizedName = "Gold Pearl Grit Water"
      }
    }
    itemDrop = IItems.金珀沙
  }
  val 金珀沙墙 = StaticWall("goldPearlGritWall").apply {
    localization {
      zh_CN {
        localizedName = "金珀沙墙"
      }
      en {
        localizedName = "Gold Pearl Grit Wall"
      }
    }
  }
  val 皎月银沙石块 = Prop("silverSandStone").apply {
    localization {
      zh_CN {
        localizedName = "皎月银沙石块"
      }
      en {
        localizedName = "Silver Sand Stone"
      }
    }
  }
  val 皎月银沙 = Floor("silverSand").apply {
    localization {
      zh_CN {
        localizedName = "皎月银沙"
      }
      en {
        localizedName = "Silver Sand"
      }
    }
  }
  val 皎月银沙水 = ShallowLiquid("silverSandWater", 皎月银沙).apply {
    localization {
      zh_CN {
        localizedName = "皎月银沙水"
      }
      en {
        localizedName = "Silver Sand Water"
      }
    }
  }
  val 皎月银沙墙 = StaticWall("silverSandWall").apply {
    localization {
      zh_CN {
        localizedName = "皎月银沙墙"
      }
      en {
        localizedName = "Silver Sand Wall"
      }
    }
  }
  val 风蚀沙柱 = TallBlock("windErodedSandPillar").apply {
    localization {
      zh_CN {
        localizedName = "风蚀沙柱"
      }
      en {
        localizedName = "Wind Eroded Sand Pillar"
      }
    }
  }
  val 风蚀砂地 = Floor("windErodedGrit").apply {
    localization {
      zh_CN {
        localizedName = "风蚀砂地"
      }
      en {
        localizedName = "Wind Eroded Grit"
      }
    }
  }
  val 风蚀沙地 = Floor("windErodedSand").apply {
    decoration = 风蚀沙柱
    localization {
      zh_CN {
        localizedName = "风蚀沙地"
      }
      en {
        localizedName = "Wind Eroded Sand"
      }
    }
  }
  val 风蚀沙水 = ShallowLiquid("windErodedSandWater", 风蚀沙地).apply {
    localization {
      zh_CN {
        localizedName = "风蚀沙水"
      }
      en {
        localizedName = "Wind Eroded Sand Water"
      }
    }
  }
  val 风蚀沙深水 = ShallowDeepLiquid("windErodedSandDeepWater", 风蚀沙地).apply {
    localization {
      zh_CN {
        localizedName = "风蚀沙深水"
      }
      en {
        localizedName = "Wind Eroded Sand Deep Water"
      }
    }
  }
  val 风蚀沙墙 = StaticWall("windErodedSandWall").apply {
    localization {
      zh_CN {
        localizedName = "风蚀沙墙"
      }
      en {
        localizedName = "Wind Eroded Sand Wall"
      }
    }
  }
  val 风蚀喷口 = SteamVent("windErodedSand-vent").apply {
    parent = 风蚀沙地.also { blendGroup = it }
    attributes.set(Attribute.steam, 1f)
    localization {
      zh_CN {
        localizedName = "风蚀喷口"
      }
      en {
        localizedName = "Wind Eroded Vent"
      }
    }
  }
  val 光辉板岩 = Floor("brillianceSlate").apply {
    localization {
      zh_CN {
        localizedName = "光辉板岩"
      }
      en {
        localizedName = "Brilliance Slate"
      }
    }
  }
  val 光辉板岩水 = ShallowLiquid("brillianceSlateWater", 光辉板岩).apply {
    localization {
      zh_CN {
        localizedName = "光辉板岩水"
      }
      en {
        localizedName = "Brilliance Slate Water"
      }
    }
  }
  val 光辉板岩墙 = StaticWall("brillianceSlateWall").apply {
    localization {
      zh_CN {
        localizedName = "光辉板岩墙"
      }
      en {
        localizedName = "Brilliance Slate Wall"
      }
    }
  }
  val 云英石柱 = Prop("greisenPillar").apply {
    localization {
      zh_CN {
        localizedName = "云英石柱"
      }
      en {
        localizedName = "Greisen Pillar"
      }
    }
  }
  val 云英岩 = Floor("greisen").apply {
    localization {
      zh_CN {
        localizedName = "云英岩"
      }
      en {
        localizedName = "Greisen"
      }
    }
  }
  val 云英岩水 = ShallowLiquid("greisenWater", 云英岩).apply {
    localization {
      zh_CN {
        localizedName = "云英岩水"
      }
      en {
        localizedName = "Greisen Water"
      }
    }
  }
  val 云英岩深水 = ShallowDeepLiquid("greisenDeepWater", 云英岩).apply {
    localization {
      zh_CN {
        localizedName = "云英岩深水"
      }
      en {
        localizedName = "Greisen Deep Water"
      }
    }
  }
  val 云英岩墙 = StaticWall("greisenWall").apply {
    localization {
      zh_CN {
        localizedName = "云英岩墙"
      }
      en {
        localizedName = "Greisen Wall"
      }
    }
  }
  val 红土石块 = Prop("redDirStone").apply {
    localization {
      zh_CN {
        localizedName = "红土石块"
      }
      en {
        localizedName = "Red Dirt Stone"
      }
    }
  }
  val 红土 = Floor("redDir").apply {
    localization {
      zh_CN {
        localizedName = "红土"
      }
      en {
        localizedName = "Red Dirt"
      }
    }
  }
  val 红土墙 = StaticWall("redDirWall").apply {
    localization {
      zh_CN {
        localizedName = "红土墙"
      }
      en {
        localizedName = "Red Dirt Wall"
      }
    }
  }
  val 流纹岩 = Floor("liparite").apply {
    localization {
      zh_CN {
        localizedName = "流纹岩"
      }
      en {
        localizedName = "Liparite"
      }
    }
  }
  val 流纹岩水 = ShallowLiquid("lipariteWater", 流纹岩).apply {
    localization {
      zh_CN {
        localizedName = "流纹岩水"
      }
      en {
        localizedName = "Liparite Water"
      }
    }
  }
  val 流纹岩墙 = StaticWall("lipariteWall").apply {
    localization {
      zh_CN {
        localizedName = "流纹岩墙"
      }
      en {
        localizedName = "Liparite Wall"
      }
    }
  }
  val 潮汐石 = Floor("nightTideStone").apply {
    localization {
      zh_CN {
        localizedName = "潮汐石"
      }
      en {
        localizedName = "Night Tide Stone"
      }
    }
  }
  val 潮汐水石 = ShallowLiquid("nightTideStoneWater", 潮汐石).apply {
    localization {
      zh_CN {
        localizedName = "潮汐水石"
      }
      en {
        localizedName = "Night Tide Stone Water"
      }
    }
  }
  val 潮汐石墙 = StaticWall("nightTideStoneWall").apply {
    localization {
      zh_CN {
        localizedName = "潮汐石墙"
      }
      en {
        localizedName = "Night Tide Stone Wall"
      }
    }
  }
  val 潮汐喷口 = SteamVent("nightTideStone-vent").apply {
    parent = 潮汐石.also { blendGroup = it }
    attributes.set(Attribute.steam, 1f)
    localization {
      zh_CN {
        localizedName = "潮汐喷口"
      }
      en {
        localizedName = "Night Tide Vent"
      }
    }
  }
  val 侵蚀层地 = Floor("erosionalSlate").apply {
    localization {
      zh_CN {
        localizedName = "侵蚀层地"
      }
      en {
        localizedName = "Erosional Slate"
      }
    }
  }
  val 侵蚀层地水 = ShallowLiquid("erosionalSlateWater", 侵蚀层地).apply {
    localization {
      zh_CN {
        localizedName = "侵蚀层地水"
      }
      en {
        localizedName = "Erosional Slate Water"
      }
    }
  }
  val 侵蚀层地墙 = StaticWall("erosionalSlateWall").apply {
    localization {
      zh_CN {
        localizedName = "侵蚀层地墙"
      }
      en {
        localizedName = "Erosional Slate Wall"
      }
    }

  }

  val 火成岩 = Floor("igneousRocks").apply {
    localization {
      zh_CN {
        localizedName = "火成岩"
      }
      en {
        localizedName = "Igneous Rocks"
      }
    }
  }
  val 炎晶矿脉 = Floor("incandescent-crystal-vein").apply {
    localization {
      zh_CN {
        localizedName = "炎晶矿脉"
      }
      en {
        localizedName = "Incandescent Crystal Vein"
      }
    }
    blendGroup = 火成岩
    attributes.set(Attribute.heat, 0.35f)
  }
  val 炎晶脉搏 = Floor("incandescent-crystal-vein-heat").apply {
    localization {
      zh_CN {
        localizedName = "炎晶脉搏"
      }
      en {
        localizedName = "Incandescent Crystal Pulse"
      }
    }
    blendGroup = 火成岩
    attributes.set(Attribute.heat, 0.85f)
  }
  val 燃素晶簇 = TallBlock("phlogistonCrystalCluster").apply {
    localization {
      zh_CN {
        localizedName = "燃素晶簇"
      }
      en {
        localizedName = "Phlogiston Crystal Cluster"
      }
    }
  }
  val 晶石地 = Floor("crystalStone").apply {
    localization {
      zh_CN {
        localizedName = "晶石地"
      }
      en {
        localizedName = "Crystal Stone"
      }
    }
    attributes.set(IAttribute.沥青, 0.1f)
  }
  val 晶石地水 = ShallowLiquid("crystalStoneWater", 晶石地).apply {
    localization {
      zh_CN {
        localizedName = "晶石地水"
      }
      en {
        localizedName = "Crystal Stone Water"
      }
    }
  }
  val 晶石墙 = StaticWall("crystalStoneWall").apply {
    localization {
      zh_CN {
        localizedName = "晶石墙"
      }
      en {
        localizedName = "Crystal Stone Wall"
      }
    }
  }
  val 幽灵簇 = Seaweed("clusterGhosts").apply {
    localization {
      zh_CN {
        localizedName = "幽灵簇"
      }
      en {
        localizedName = "Cluster Ghosts"
      }
    }
  }
  val 幽冥蕨 = TallBlock("ghostGrassFern").apply {
    localization {
      zh_CN {
        localizedName = "幽冥蕨"
      }
      en {
        localizedName = "Ghost Grass Fern"
      }
    }
  }
  val 缠怨花 = TreeBlock("ghostGrassFlower").apply {
    localization {
      zh_CN {
        localizedName = "缠怨花"
      }
      en {
        localizedName = "Ghost Grass Flower"
      }
    }
  }
  val 幽灵草 = Floor("ghostGrass").apply {
    localization {
      zh_CN {
        localizedName = "幽灵草"
      }
      en {
        localizedName = "Ghost Grass"
      }
    }
  }
  val 幽灵草水 = ShallowLiquid("ghostGrassWater", 幽灵草).apply {
    localization {
      zh_CN {
        localizedName = "幽灵草水"
      }
      en {
        localizedName = "Ghost Grass Water"
      }
    }
  }
  val 幽灵草深水 = ShallowDeepLiquid("ghostGrassDeepWater", 幽灵草).apply {
    localization {
      zh_CN {
        localizedName = "幽灵草深水"
      }
      en {
        localizedName = "Ghost Grass Deep Water"
      }
    }
  }
  val 幽灵草墙 = StaticWall("ghostGrassWall").apply {
    localization {
      zh_CN {
        localizedName = "幽灵草墙"
      }
      en {
        localizedName = "Ghost Grass Wall"
      }
    }
  }
  val 灰烬地 = Floor("ash").apply {
    localization {
      zh_CN {
        localizedName = "灰烬地"
      }
      en {
        localizedName = "Ash"
      }
    }
  }
  val 灰烬地水 = ShallowLiquid("ashWater", 灰烬地).apply {
    localization {
      zh_CN {
        localizedName = "灰烬地水"
      }
      en {
        localizedName = "Ash Water"
      }
    }
  }
  val 灰烬墙 = StaticWall("ashWall").apply {
    localization {
      zh_CN {
        localizedName = "灰烬墙"
      }
      en {
        localizedName = "Ash Wall"
      }
    }
  }
  val 钢铁地板1 = Floor("steelFloor1").apply {
    localization {
      zh_CN {
        localizedName = "钢铁地板1"
      }
      en {
        localizedName = "Steel Floor 1"
      }
    }
  }
  val 钢铁墙1 = StaticWall("steelFloorWall1").apply {
    localization {
      zh_CN {
        localizedName = "钢铁墙1"
      }
      en {
        localizedName = "Steel Wall 1"
      }
    }
  }
  val 钢铁地板2 = Floor("steelFloor2").apply {
    localization {
      zh_CN {
        localizedName = "钢铁地板"
      }
      en {
        localizedName = "Steel Floor"
      }
    }
  }
  val 钢铁墙2 = StaticWall("steelFloorWall2").apply {
    localization {
      zh_CN {
        localizedName = "钢铁墙"
      }
      en {
        localizedName = "Steel Wall"
      }
    }
  }
  val 精钢甲板 = Floor("steelFloor3").apply {
    localization {
      zh_CN {
        localizedName = "精钢甲板"
      }
      en {
        localizedName = "Steel Deck"
      }
    }
  }
  val 跨界钢板 = TiledFloor("bridgeSteel", 9).apply {
    localization {
      zh_CN {
        localizedName = "跨界钢板"
      }
      en {
        localizedName = "Bridge Steel"
      }
    }
  }
  val 跨界钢板墙 = StaticWall("bridgeSteelWall").apply {
    localization {
      zh_CN {
        localizedName = "跨界钢板墙"
      }
      en {
        localizedName = "Bridge Steel Wall"
      }
    }
  }
  val 供能板 = Floor("powerBoard").apply {
    localization {
      zh_CN {
        localizedName = "供能板"
      }
      en {
        localizedName = "Power Board"
      }
    }
  }
  val 供能墙 = StaticWall("powerWall").apply {
    localization {
      zh_CN {
        localizedName = "供能墙"
      }
      en {
        localizedName = "Power Wall"
      }
    }
  }
  val 诅咒之地 = Floor("curseLand").apply {
    localization {
      zh_CN {
        localizedName = "诅咒之地"
      }
      en {
        localizedName = "Curse Land"
      }
    }
  }
  val 诅咒之墙 = StaticWall("curseWall").apply {
    localization {
      zh_CN {
        localizedName = "诅咒之墙"
      }
      en {
        localizedName = "Curse Wall"
      }
    }
  }
  val 新月岩 = Floor("crescent").apply {
    localization {
      zh_CN {
        localizedName = "新月岩"
      }
      en {
        localizedName = "Crescent"
      }
    }
  }
  val 新月岩水 = ShallowLiquid("crescentWater", 新月岩).apply {
    localization {
      zh_CN {
        localizedName = "新月岩水"
      }
      en {
        localizedName = "Crescent Water"
      }
    }
  }
  val 新月岩墙 = StaticWall("crescentWall").apply {
    localization {
      zh_CN {
        localizedName = "新月岩墙"
      }
      en {
        localizedName = "Crescent Wall"
      }
    }
  }
  val 新月喷口 = SteamVent("crescent-vent").apply {
    effectSpacing = 30f
    effect = Effect(140f) { e ->
      Draw.color(Color.valueOf("acb4eb"), Color.valueOf("d8ddff"), e.fin())
      Draw.alpha(e.fslope() * 0.78f)
      val length = 3f + e.finpow() * 10f
      Fx.rand.setSeed(e.id.toLong())
      for(i in 0..<Fx.rand.random(3, 5)) {
        Fx.v.trns(Fx.rand.random(360f), Fx.rand.random(length))
        Fill.circle(e.x + Fx.v.x, e.y + Fx.v.y, Fx.rand.random(1.2f, 3.5f) + e.fslope() * 1.1f)
      }
    }.layer(Layer.darkness - 1)
    parent = 新月岩.also { blendGroup = it }
    attributes.set(Attribute.steam, 1f)
    localization {
      zh_CN {
        localizedName = "新月喷口"
      }
      en {
        localizedName = "Crescent Vent"
      }
    }
  }
  val 凌冰尖刺 = TallBlock("tortureIceSpikes").apply {
    localization {
      zh_CN {
        localizedName = "凌冰尖刺"
      }
      en {
        localizedName = "Torture Ice Spikes"
      }
    }
  }
  val 凌冰石块 = Prop("tortureIceStone").apply {
    localization {
      zh_CN {
        localizedName = "凌冰石块"
      }
      en {
        localizedName = "Torture Ice Stone"
      }
    }
  }
  val 霜寒草 = Prop("frostbiteGrass").apply {
    localization {
      zh_CN {
        localizedName = "霜寒草"
      }
      en {
        localizedName = "Frostbite Grass"
      }
    }
  }
  val 凌冰 = Floor("tortureIce").apply {
    localization {
      zh_CN {
        localizedName = "凌冰"
      }
      en {
        localizedName = "Torture Ice"
      }
    }
    attributes.set(IAttribute.寒冷, 1f)
  }
  val 凌冰水 = ShallowLiquid("tortureIceWater", 凌冰).apply {
    localization {
      zh_CN {
        localizedName = "凌冰水"
      }
      en {
        localizedName = "Torture Ice Water"
      }
    }
    attributes.set(IAttribute.寒冷, 0.7f)
  }
  val 凌冰深水 = ShallowDeepLiquid("tortureIceDeepWater", 凌冰).apply {
    localization {
      zh_CN {
        localizedName = "凌冰深水"
      }
      en {
        localizedName = "Torture Ice Deep Water"
      }
    }
    attributes.set(IAttribute.寒冷, 0.8f)
  }
  val 凌冰墙 = StaticWall("tortureIceWall").apply {
    localization {
      zh_CN {
        localizedName = "凌冰墙"
      }
      en {
        localizedName = "Torture Ice Wall"
      }
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
    addContentInitEvent {
      wall = 肿瘤墙
    }
    localization {
      zh_CN {
        localizedName = "血浅滩"
      }
      en {
        localizedName = "Blood Shoal"
      }
    }
  }
  val 血池 = object :Floor("thickBlood") {

    override fun drawBase(tile: Tile) {
      Dup.foors.addUnique(tile)
      super.drawBase(tile)
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
    addContentInitEvent {
      wall = 肿瘤墙
    }
    localization {
      zh_CN {
        localizedName = "血池"
      }
      en {
        localizedName = "Thick Blood"
      }
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
    addContentInitEvent {
      wall = 肿瘤墙
    }
    localization {
      zh_CN {
        localizedName = "深血池"
      }
      en {
        localizedName = "Deep Thick Blood"
      }
    }
  }
  val 浓稠深血池 = Floor("denseDeepThickBlood").apply {
    speedMultiplier = 0.4f
    liquidDrop = ILiquids.浓稠血浆
    liquidMultiplier = 1.5f
    status = IStatus.染血
    shallow = true
    isLiquid = true
    statusDuration = 60 * 4f
    drownTime = 200f
    cacheLayer = IceShader.bloodShallow
    addContentInitEvent {
      wall = 肿瘤墙
    }
    localization {
      zh_CN {
        localizedName = "浓稠深血池"
      }
      en {
        localizedName = "Dense Deep Thick Blood"
      }
    }
  }
  val 肿瘤地 = Floor("bloodNeoplasma").apply {
    decoration = 血孢子丛
    localization {
      zh_CN {
        localizedName = "肿瘤地"
      }
      en {
        localizedName = "Blood Neoplasma"
      }
    }
  }
  val 碎骨地 = Floor("brokenBone").apply {
    localization {
      zh_CN {
        localizedName = "碎骨地"
      }
      en {
        localizedName = "Broken Bone"
      }
    }
  }
  val 碎骨墙 = StaticWall("brokenBoneWall").apply {
    localization {
      zh_CN {
        localizedName = "碎骨墙"
      }
      en {
        localizedName = "Broken Bone Wall"
      }
    }
  }
  val 血沙石块 = Prop("bloodSandStone").apply {
    localization {
      zh_CN {
        localizedName = "血沙石块"
      }
      en {
        localizedName = "Blood Sand Stone"
      }
    }
  }
  val 殷血粗沙 = Floor("bloodSand").apply {
    localization {
      zh_CN {
        localizedName = "殷血粗沙"
      }
      en {
        localizedName = "Blood Sand"
      }
    }
  }
  val 殷血粗沙墙 = StaticWall("bloodSandWall").apply {
    localization {
      zh_CN {
        localizedName = "殷血粗沙墙"
      }
      en {
        localizedName = "Blood Sand Wall"
      }
    }
  }
  val 骸骨地 = Floor("humanBones").apply {
    localization {
      zh_CN {
        localizedName = "骸骨地"
      }
      en {
        localizedName = "Human Bones"
      }
    }
  }
  val 血痂地 = Floor("bloodScars").apply {
    addContentInitEvent {
      wall = 肿瘤墙
    }
    localization {
      zh_CN {
        localizedName = "血痂地"
      }
      en {
        localizedName = "Blood Scars"
      }
    }
  }
  val 血痂岩 = Floor("bloodScarsStone").apply {
    localization {
      zh_CN {
        localizedName = "血痂岩"
      }
      en {
        localizedName = "Blood Scars Stone"
      }
    }
  }
  val 血蚀岩石块 = Prop("bloodmoriteStone").apply {
    localization {
      zh_CN {
        localizedName = "血蚀岩石块"
      }
      en {
        localizedName = "Bloodmorite Stone"
      }
    }
  }
  val 血蚀岩 = Floor("bloodmorite").apply {
    decoration = 血蚀岩石块
    localization {
      zh_CN {
        localizedName = "血蚀岩"
      }
      en {
        localizedName = "Bloodmorite"
      }
    }
  }
  val 血蚀墙 = StaticWall("bloodmoriteWall").apply {
    localization {
      zh_CN {
        localizedName = "血蚀墙"
      }
      en {
        localizedName = "Bloodmorite Wall"
      }
    }
  }
  val 肿瘤墙 = StaticWall("bloodNeoplasmaWall").apply {
    localization {
      zh_CN {
        localizedName = "肿瘤墙"
      }
      en {
        localizedName = "Blood Neoplasma Wall"
      }
    }
  }
  val 红冰 = Floor("redIce").apply {
    localization {
      zh_CN {
        localizedName = "红冰"
      }
      en {
        localizedName = "Red Ice"
      }
    }
  }
  val 红冰墙 = StaticWall("redIceWall").apply {
    localization {
      zh_CN {
        localizedName = "红冰墙"
      }
      en {
        localizedName = "Red Ice Wall"
      }
    }
  }
  val 赤雪 = Floor("bloodIceSnow").apply {
    localization {
      zh_CN {
        localizedName = "赤雪"
      }
      en {
        localizedName = "Blood Ice Snow"
      }
    }
  }
  val 红霜石块 = Prop("bloodSnowStone").apply {
    localization {
      zh_CN {
        localizedName = "红霜石块"
      }
      en {
        localizedName = "Blood Snow Stone"
      }
    }
  }
  val 红霜 = Floor("bloodSnow").apply {
    localization {
      zh_CN {
        localizedName = "红霜"
      }
      en {
        localizedName = "Blood Snow"
      }
    }
  }
  val 红霜墙 = StaticWall("bloodSnowWall").apply {
    localization {
      zh_CN {
        localizedName = "红霜墙"
      }
      en {
        localizedName = "Blood Snow Wall"
      }
    }
  }
  val 肿瘤喷口 = BloodNeoplasmaVent("bloodNeoplasmaVent").apply {
    parent = 肿瘤地.also { blendGroup = it }
    attributes.set(Attribute.steam, 1f)
    effect = Fx.none
    localization {
      zh_CN {
        localizedName = "肿瘤喷口"
      }
      en {
        localizedName = "Blood Neoplasma Vent"
      }
    }
  }
  val 肿瘤井 = object :Prop("bloodNeoplasmaWell") {
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
    localization {
      zh_CN {
        localizedName = "肿瘤井"
      }
      en {
        localizedName = "Blood Neoplasma Well"
      }
    }
  }
  val 肉瘤菇 = TallBlock("bloodBall").apply {
    localization {
      zh_CN {
        localizedName = "肉瘤菇"
      }
      en {
        localizedName = "Blood Ball"
      }
    }
  }
  val 血蚀囊胚 = TallBlock("bloodBlastocyst").apply {
    localization {
      zh_CN {
        localizedName = "血蚀囊胚"
      }
      en {
        localizedName = "Blood Blastocyst"
      }
    }
  }
  val 缚肉树 = TallBlock("bloodFleshTree").apply {
    localization {
      zh_CN {
        localizedName = "缚肉树"
      }
      en {
        localizedName = "Blood Flesh Tree"
      }
    }
  }
  val 摄魂墙 = Block("soulCapturing").apply {
    solid = true
    breakable = true
    localization {
      zh_CN {
        localizedName = "摄魂墙"
      }
      en {
        localizedName = "Soul Capturing Wall"
      }
    }
  }
}
