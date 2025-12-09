package ice.content.block

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles.randLenVectors
import ice.content.IItems
import ice.content.ILiquids
import ice.library.world.ContentLoad
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.distribution.itemNode.TransferNode
import ice.world.content.blocks.liquid.LiquidClassifier
import ice.world.content.blocks.liquid.MultipleLiquidBlock
import ice.world.content.blocks.liquid.PumpChamber
import ice.world.content.blocks.liquid.SolidPump
import ice.world.content.blocks.liquid.base.LiquidRouter
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import ice.world.draw.DrawRegionNull
import mindustry.content.Items
import mindustry.entities.Effect
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.liquid.Conduit
import mindustry.world.blocks.liquid.LiquidJunction
import mindustry.world.blocks.production.Pump
import mindustry.world.draw.DrawBlurSpin
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute

@Suppress("unused")
object Liquid : ContentLoad {
    val 大型抽水机: Block = SolidPump("largeWaterPump").apply {
        size = 3
        drawers = DrawMulti(
            DrawDefault(), DrawLiquidRegion(), DrawRegionNull("-rotator",2f,true), DrawRegionNull("-top"))
        attribute = Attribute.water
        baseEfficiency = 1f
        pumpAmount = 0.6f
        liquidCapacity = 120f
        consumePower(6f)
        bundle {
            desc(zh_CN, "大型抽水机", "大型抽水机,可以抽取水源")
        }
        requirements(Category.liquid, IItems.石英玻璃,75,IItems.高碳钢, 40, IItems.铬锭, 70, IItems.单晶硅,60)
    }
    val 泵腔: Block = PumpChamber("pumpChamber").apply {
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
    val 心肌泵: Block = Pump("myocardialPump").apply {
        size = 4
        squareSprite = false
        pumpAmount = 0.625f
        liquidCapacity = 240f
        consumePower(8f)
        requirements(
            Category.liquid, IItems.石英玻璃, 120, IItems.铱板, 120, IItems.导能回路, 85, IItems.陶钢, 45, IItems.生物钢, 15
        )
        bundle {
            desc(zh_CN, "心肌泵", "终极液泵,生物科技的高级产物")
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
        drawers = DrawMulti(
            DrawRegion("-bottom"), DrawLiquidTile(result, 2f), DrawDefault(), DrawBlurSpin("-rotator", 2f).apply { blurThresh = 2f })
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
    val 装甲液体路由器: Block = LiquidRouter("armoredLiquidRouter").apply {
        armor = 4f
        liquidCapacity = 60f
        liquidPressure = 1.1f
        solid = false
        underBullets = true
        placeableLiquid = true
        requirements(Category.liquid, IItems.石英玻璃, 2, IItems.陶钢, 1, IItems.铱板, 3)
        bundle {
            desc(zh_CN, "装甲液体路由器", "向各个方向快速运输液体")
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
        range = 6
        hasItems = false
        hasPower = false
        liquidCapacity = 10f
        requirements(Category.liquid, IItems.石英玻璃, 10)
        bundle {
            desc(zh_CN, "导管桥", "在以自我为中心且边长为${2 * range + 1}的正方形范围内,向任意方向传输液体")
        }
    }
    val 长距导管桥 = TransferNode("bridgeConduitLarge").apply {
        range = 10
        hasItems = false
        liquidCapacity = 10f
        consumePower(30f / 60f)
        requirements(Category.liquid, IItems.石英玻璃, 20)

        bundle {
            desc(
                zh_CN, "长距导管桥", "消耗电力,在以自我为中心且边长为${2 * range + 1}的正方形范围内,向任意方向传输液体"
            )
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
    val 装甲储液罐 = LiquidRouter("armorLiquidStorage").apply {
        healAmount = 120f
        health = 3200
        armor = 8f
        size = 4
        liquidPadding = 4f
        liquidCapacity = 6400f
        placeableLiquid = true
        requirements(Category.liquid, IItems.铱板, 85, IItems.石英玻璃, 35, IItems.陶钢, 55)
        bundle {
            desc(zh_CN, "装甲储液罐", "双层复合装甲,内置的修复夹层可快速修复罐体,使其更安全地储存大量液体")
        }
    }
    val 流体枢纽: Block = MultipleLiquidBlock("fluidJunction").apply {
        size = 3
        liquidCapacity = 1000f
        health = size * size * 100
        requirements(Category.liquid, IItems.铜锭, 10)
        bundle {
            desc(
                zh_CN, "流体枢纽", "能将多种流体独立存储于同一单元,有效解决了复杂流水线中的空间占用问题,是高级化生产的必备设施"
            )
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
}