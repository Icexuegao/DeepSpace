package ice.content.block

import ice.audio.ISounds
import ice.content.IItems
import ice.library.util.toColor
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.liquid.SolidPump
import ice.world.content.blocks.production.IceDrill
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.entities.effect.ParticleEffect
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.production.BurstDrill

@Suppress("unused")
object Production : Load {
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
            desc(zh_CN, "热熔钻井", "通过多种合金制成的钻头融化地层以快速开采所有矿物")
        }
        requirements(Category.production, IItems.铱板, 125, IItems.导能回路, 85, IItems.陶钢, 55)
    }
    val 血肉钻井: Block = BurstDrill("fleshBloodDrill").apply {
        size = 5
        tier = 11
        drillTime = 41.66f
        itemCapacity = 600
        liquidCapacity = 60f
        consumePower(10f)
        consumeLiquids(Liquids.water, 0.5f)
        drillSound = ISounds.shotFiercely
        placeableLiquid = true
        drillEffect = ParticleEffect().apply {
            particles = 6
            lifetime = 90f
            sizeFrom = 2f
            sizeTo = 3f
            length = 15f
            baseLength = 30f
            colorFrom = "D75B6E".toColor()
            colorTo = "D75B6E00".toColor()
            cone = 360f
        }
        requirements(Category.production, IItems.铱板, 450, IItems.导能回路, 225, IItems.钴锭, 32, IItems.生物钢, 75, IItems.肃正协议, 1)
        bundle {
            desc(zh_CN, "血肉钻井", "骨骼构成了最坚硬的钻头,肌肉形成了最强劲的转子,预热时间较长,需要持续供给血肉赘生物,可以安置在水上")
        }
    }
    val 抽水机= SolidPump("waterPump").apply {
        size = 2
        baseEfficiency = 1f
        pumpAmount = 0.2f
        liquidCapacity = 60f
        consumePower(3f)
        bundle {
            desc(zh_CN, "抽水机", "抽水机,可以抽取水源")
        }
        requirements(Category.production, IItems.石英玻璃, 25, IItems.高碳钢, 20, IItems.单晶硅, 10)
    }
    val 大型抽水机 = SolidPump("largeWaterPump").apply {
        rotate
        size = 3
        baseEfficiency = 1f
        pumpAmount = 0.6f
        liquidCapacity = 120f
        consumePower(6f)
        bundle {
            desc(zh_CN, "大型抽水机", "大型抽水机,可以抽取水源")
        }
        requirements(Category.production, IItems.石英玻璃, 75, IItems.高碳钢, 40, IItems.铬锭, 70, IItems.单晶硅, 60)
    }
}
