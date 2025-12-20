package ice.content.block

import ice.library.world.Load
import ice.content.IItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.defense.Wall
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.AutoDoor
import mindustry.world.blocks.defense.ShieldWall

@Suppress("unused")
object Defense : Load {
    val 铬墙: Block = Wall("chromeWall").apply {
        health = 450
        size = 1
        requirements(Category.defense, ItemStack.with(IItems.铬锭, 6))
        bundle {
            desc(zh_CN, "铬墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
        }
    }
    val 大型铬墙: Block = Wall("chromeWallLarge").apply {
        size = 2
        health = 铬墙.health * 4
        requirements(Category.defense, ItemStack.with(IItems.铬锭, 6 * 4))
        bundle {
            desc(zh_CN, "大型铬墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
        }
    }
    val 碳钢墙: Block = Wall("carbonSteelWall").apply {
        size = 1
        armor = 5f
        health = 320
        chanceDeflect = 0.1f
        requirements(Category.defense, ItemStack.with(IItems.高碳钢, 3, IItems.低碳钢, 3))
        bundle {
            desc(zh_CN, "碳钢墙", "保护己方建筑,挡下敌方炮弹")
        }
    }
    val 大型碳钢墙: Block = Wall("carbonSteelWallLarge").apply {
        size = 2
        armor = 5f
        chanceDeflect = 0.15f
        health = 碳钢墙.health * size * size
        requirements(Category.defense, ItemStack.with(IItems.高碳钢, 3 * size * size, IItems.低碳钢, 3 * size * size))
        bundle {
            desc(zh_CN, "大型碳钢墙", "保护己方建筑,挡下敌方炮弹")
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
        health = 流金墙.health * 4
        requirements(Category.defense, IItems.金锭, 10)
        bundle {
            desc(zh_CN, "大型流金墙", "熔融金锭构筑的壁垒,随时间缓慢自愈", "财富值++")
        }
    }
    val 钴钢墙: Block = Wall("cobaltSteelWall").apply {
        size = 1
        health = 700
        requirements(Category.defense, IItems.钴钢, 8)
        bundle {
            desc(zh_CN, "钴钢墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
        }
    }
    val 大型钴钢墙: Block = Wall("cobaltSteelWallLarge").apply {
        size = 2
        health = 钴钢墙.health * 4
        requirements(Category.defense, IItems.钴钢, 32)
        bundle {
            desc(zh_CN, "大型钴钢墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
        }
    }
    val 铱墙: Block = Wall("iridiumWall").apply {
        armor = 5f
        health = 1800
        crushDamageMultiplier = 2.5f
        bundle {
            desc(zh_CN, "铱墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
        }
        requirements(Category.defense, IItems.铱板, 6)
    }
    val 大型铱墙: Block = Wall("iridiumWallLarge").apply {
        size = 2
        armor = 20f
        health = 7200
        crushDamageMultiplier = 2.5f
        bundle {
            desc(zh_CN, "大型铱墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
        }
        requirements(Category.defense, IItems.铱板, 24)
    }
    val 装甲闸门: Block = AutoDoor("armorGate").apply {
        size = 2
        armor = 20f
        health = 6400
        insulated = true
        absorbLasers = true
        placeableLiquid = true
        crushDamageMultiplier = 1f
        requirements(Category.defense, IItems.铱板, 24, IItems.导能回路, 16)
        bundle {
            desc(zh_CN, "装甲闸门", "安全可靠,坚实耐用,自动开关")
        }
    }
    val 陶钢墙: Block = Wall("potterySteelWall").apply {
        health = 1200
        armor = 3f
        floating = true
        insulated = true
        absorbLasers = true
        bundle {
            desc(
                zh_CN,
                "陶钢墙",
                "坚固耐用,保护己方建筑,挡下敌方炮弹,能吸收激光和电弧,会阻止电力节点自动连接,可以放置在岸边"
            )
        }
        requirements(Category.defense, IItems.陶钢, 6)
    }
    val 大型陶钢墙: Block = Wall("potterySteelWallLarge").apply {
        health = 4800
        armor = 12f
        size = 2
        floating = true
        insulated = true
        absorbLasers = true
        bundle {
            desc(
                zh_CN,
                "大型陶钢墙",
                "坚固耐用,保护己方建筑,挡下敌方炮弹,能吸收激光和电弧,会阻止电力节点自动连接,可以放置在岸边"
            )
        }
        requirements(Category.defense, IItems.陶钢, 24)
    }
    val 相位合金墙: Block = ShieldWall("phaseAlloyWall").apply {
        health = 1500
        armor = 6f
        flashHit = true
        chanceDeflect = 75f
        lightningChance = 0.5f
        lightningDamage = 25f
        lightningLength = 5
        shieldHealth = 600f
        regenSpeed = 1f
        breakCooldown = 300f
        crushDamageMultiplier = 1f
        requirements(Category.defense, IItems.导能回路, 2, IItems.金锭, 2, IItems.铪锭, 2)
        bundle {
            desc(zh_CN, "相位合金墙", "创建一个力场保护自身,具有大多数墙的特点")
        }
    }
    val 大型相位合金墙: Block = ShieldWall("phaseAlloyWallLarge").apply {
        health = 6000
        armor = 24f
        size = 2
        flashHit = true
        chanceDeflect = 75f
        lightningChance = 0.5f
        lightningDamage = 50f
        lightningLength = 20
        shieldHealth = 2400f
        regenSpeed = 4f
        breakCooldown = 1200f
        crushDamageMultiplier = 1f
        requirements(Category.defense, IItems.导能回路, 8, IItems.金锭, 8, IItems.铪锭, 8)
        bundle {
            desc(zh_CN, "大型相位合金墙", "创建一个力场保护自身,具有大多数墙的特点")
        }
    }
    val 生物钢墙: Block = Wall("biologicalSteelWall").apply {
        health = 8000
        armor = 8f
        insulated = true
        absorbLasers = true
        placeableLiquid = true
        crushDamageMultiplier = 0.5f
        healAmount = 200f
        damageReduction = 0.8f
        requirements(Category.defense, IItems.生物钢, 8, IItems.铱板, 4, IItems.陶钢, 4)
        bundle {
            desc(
                zh_CN,
                "生物钢墙",
                "坚固耐用,复合装甲结构使其可以减免部分伤害,而活性生物质夹层允许其快速自我修复,\n能吸收激光和电弧,会阻止电力节点自动连接,可以放置在深水中"
            )
        }
    }
    val 大型生物钢墙: Block = Wall("biologicalSteelWallLarge").apply {
        health = 32000
        armor = 32f
        size = 2
        healAmount = 800f
        damageReduction = 0.8f
        insulated = true
        absorbLasers = true
        placeableLiquid = true
        crushDamageMultiplier = 0.5f
        requirements(Category.defense, IItems.生物钢, 32, IItems.铱板, 16, IItems.陶钢, 16)
        bundle {
            desc(
                zh_CN,
                "大型生物钢墙",
                "坚固耐用,复合装甲结构使其可以减免部分伤害,而活性生物质夹层允许其快速自我修复,\n能吸收激光和电弧,会阻止电力节点自动连接,可以放置在深水中"
            )
        }
    }
}