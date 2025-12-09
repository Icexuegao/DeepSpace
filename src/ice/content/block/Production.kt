package ice.content.block

import ice.content.IItems
import ice.library.util.toColor
import ice.library.world.ContentLoad
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.production.IceDrill
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block

@Suppress("unused")
object Production : ContentLoad {
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
        requirements(Category.production, IItems.铬锭, 20, IItems.钴锭, 12)
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
    val 热熔钻井: Block = IceDrill("hotMeltDrill").apply {
        size = 5
        tier = 9
        drillTime = 75f
        itemCapacity = 60
        liquidCapacity = 60f
        consumePower(4f)
        consumeLiquids(Liquids.water, 0.3f).apply {
            booster = true
            optional = true
        }
        drillEffect = Fx.mine
        updateEffect = mindustry.entities.effect.WaveEffect().apply {
            lifetime = 60f
            sizeFrom = 0f
            sizeTo = 40f
            colorFrom = "FFD37F".toColor()
            colorTo = "FFD37F00".toColor()
        }
        rotator.rotateSpeed = 6f
        warmupSpeed = 0.06f
        liquidBoostIntensity = 2f
        bundle {
            desc(zh_CN, "热熔钻井")
        }
        requirements(Category.production, IItems.铱板, 125, IItems.导能回路, 85, IItems.陶钢, 55)
    }
}
