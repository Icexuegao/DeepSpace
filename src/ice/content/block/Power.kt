package ice.content.block

import arc.graphics.Color
import ice.content.IItems
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.world.ContentLoad
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawAnyLiquidTile
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.blocks.power.Battery
import mindustry.world.blocks.power.BeamNode
import mindustry.world.blocks.power.ConsumeGenerator
import mindustry.world.blocks.power.ThermalGenerator
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BlockGroup

@Suppress("unused")
object Power : ContentLoad {
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
    val 大型能量电池: Block = Battery("largePowerBattery").apply {
        size = 4
        armor = 4f
        absorbLasers = true
        emptyLightColor = IceColor.df
        fullLightColor = IceColor.b4
        consumePowerBuffered(1000000f)
        requirements(Category.power, IItems.铅锭, 150, IItems.铱板, 145, IItems.导能回路, 85, IItems.陶钢, 30)
        bundle {
            desc(zh_CN, "大型能量电池")
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
    val 地热发电机 = ThermalGenerator("geothermalGenerator").apply {
        size = 3
        floating = true
        attribute = Attribute.heat
        liquidCapacity = 36f
        powerProduction = 5f
        requirements(Category.power, IItems.石英玻璃, 90, IItems.铱板, 105, IItems.单晶硅, 35)
        effectChance = 0.1f
        generateEffect = Fx.redgeneratespark
        drawer = DrawMulti(DrawRegion("-bottom"), DrawAnyLiquidTile(),DrawDefault(), DrawGlowRegion())
        bundle {
            desc(zh_CN, "地热发电机", "")
        }
    }
}