package ice.content.block

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import ice.content.IItems
import ice.graphics.IceColor
import ice.library.EventType.lazyInit
import ice.library.world.ContentLoad
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.distribution.*
import ice.world.content.blocks.distribution.conveyor.Conveyor
import ice.world.content.blocks.distribution.conveyor.PackStackConveyor
import ice.world.content.blocks.distribution.conveyor.StackConveyor
import ice.world.content.blocks.distribution.digitalStorage.HubConduit
import ice.world.content.blocks.distribution.digitalStorage.LogisticsHub
import ice.world.content.blocks.distribution.digitalStorage.LogisticsInput
import ice.world.content.blocks.distribution.digitalStorage.LogisticsOutput
import ice.world.content.blocks.distribution.droneNetwork.DroneDeliveryTerminal
import ice.world.content.blocks.distribution.droneNetwork.DroneReceivingRnd
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.bullet.PointBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.distribution.ArmoredConveyor
import mindustry.world.blocks.distribution.MassDriver
import mindustry.world.blocks.storage.Unloader

@Suppress("unused")
object Distribution : ContentLoad {
    val 基础传送带 = Conveyor("baseConveyor").apply {
        size = 1
        speed = 0.2f
        health = 30
        displayedSpeed = speed * 140f
        lazyInit {
            junctionReplacement = 基础交叉器
        }
        requirements(Category.distribution, IItems.低碳钢, 1)
        bundle {
            desc(zh_CN, "基础传送带", "基础的运输设施,用于在建筑之间运输物品,造价低廉")
        }
    }
    val 特种传送带 = ArmoredConveyor("specialConveyor").apply {
        size = 1
        speed = 0.21f
        health = 60
        displayedSpeed = speed * 140f
        lazyInit {
            junctionReplacement = 基础交叉器
        }
        requirements(Category.distribution, IItems.钴锭, 1, IItems.铬锭, 1, IItems.复合陶瓷, 1)
        bundle {
            desc(zh_CN, "特种传送带", "基础的运输设施,用于在建筑之间运输物品,不接收侧面输入")
        }
    }
    val 钴熠传送带 = StackConveyor("cobaltBrightConveyor").apply {
        speed = 50f / 600f
        requirements(Category.distribution, IItems.高碳钢, 2, IItems.钴钢, 1, IItems.铬锭, 1)
        bundle {
            desc(zh_CN, "钴熠传送带")
        }
        loadEffect= WaveEffect().apply {
            lifetime = 20f
            sides = 3
            sizeTo = 6f
            sizeFrom = 0f
            strokeTo = 0f
            strokeFrom = 3f
            colorFrom = IceColor.b4
            colorTo = IceColor.b4
        }
    }
    val 梯度传送带 = PackStackConveyor("gradedConveyor").apply {
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
                Lines.lineAngle(e.x + Fx.v.x + Fx.rand.range(spread), e.y + Fx.v.y + Fx.rand.range(spread), ang, e.fout() * Fx.rand.random(1f) + 1f)
            }
        }
        requirements(Category.distribution, ItemStack.with(IItems.铪锭, 20))
        bundle {
            desc(zh_CN, "梯度传送带")
        }
    }
    val 血肉装甲传送带 = Conveyor("fleshArmorConveyor").apply {
        health = 600
        armor = 8f
        speed = 0.30f
        healAmount=30f
        displayedSpeed = 36f
        placeableLiquid = true
        requirements(Category.distribution, IItems.生物钢, 1, IItems.铱板, 2)
        lazyInit {
            bridgeReplacement = 装甲传送带桥
            junctionReplacement = 交叉神经链路
        }
        bundle {
            desc(zh_CN, "血肉装甲传送带","在传送带内部模拟血肉蠕动来快速输送物品")
        }
    }
    val 生物钢传送带 = StackConveyor("biologicalSteelConveyor").apply {
        healAmount = 10f
        health = 300
        armor = 8f
        speed = 0.1f
        baseEfficiency = 1f
        itemCapacity = 100
        consumePower(0.05f)
        hasPower = true
        consumesPower = true
        conductivePower = true
        placeableLiquid = true
        unloadEffect = ParticleEffect().apply {
            particles = 7
            lifetime = 15f
            length = 25f
            cone = -360f
            lenFrom = 5f
            lenTo = 0f
            colorFrom = Color.valueOf("BF3E47")
            colorTo = Color.valueOf("E78F92")
        }
        destroyBullet = PointBulletType().apply {
            damage = 0f
            splashDamage = 30f
            splashDamageRadius = 24f
            hitShake = 2f
            status = StatusEffects.melting
            statusDuration = 30f * 60f
            speed = 0f
            lifetime = 1f
            hitEffect = Fx.none
            despawnEffect = Fx.none
        }
        researchCostMultiplier = 40f
        requirements(Category.distribution, IItems.导能回路, 1, IItems.生物钢, 1)
        bundle {
            desc(zh_CN, "生物钢传送带", "打包物品进行运输,一次能携带100件物品,比塑钢传送带更快,可以用电力加速")
        }
    }
    val 基础交叉器: Block = Junction("baseJunction").apply {
        size = 1
        health = 100
        requirements(Category.distribution, IItems.低碳钢, 5, IItems.高碳钢, 2)
        bundle {
            desc(zh_CN, "基础交叉器", "两条交叉传送带的桥梁")
        }
    }
    val 交叉神经链路: Block = Junction("junctionNeuralChain").apply {
        armor = 4f
        health = 250
        displayedSpeed = 30f
        placeableLiquid = true
        requirements(Category.distribution, IItems.生物钢, 1, IItems.铱板, 1)
        bundle {
            desc(zh_CN, "交叉神经链路", "连接两条交叉的传送带,比交叉器更快")
        }
    }
    val 基础路由器: Block = Router("baseRouter").apply {
        size = 1
        health = 70
        requirements(Category.distribution, IItems.低碳钢, 5)
        bundle {
            desc(zh_CN, "基础路由器", "将物品平均分配至其他三个方向")
        }
    }
    val 转换分类器: Block = Sorter("transformSorter").apply {
        size = 1
        health = 100
        requirements(Category.distribution, IItems.高碳钢, 8, IItems.低碳钢, 6)
        bundle {
            desc(zh_CN, "转换分类器", "像分类器一样处理物品,可以通过配置调整分类状态")
        }
    }
    val 转换溢流门: Block = TransformOverflowGate("transformOverflowGate").apply {
        size = 1
        health = 200
        requirements(Category.distribution, IItems.高碳钢, 8, IItems.低碳钢, 6)
        bundle {
            desc(zh_CN, "转换溢流门", "像溢流门一样处理物品,可以通过配置调整溢流状态")
        }
    }
    val 装甲传送带桥: Block = TransferNode("armorBridge").apply {
        health = 40
        armor = 4f
        range = 10
        hasLiquids = false
        fadeIn = false
        bridgeWidth = 8f
        hasPower = false
        arrowSpacing = 6f
        transportTime = 1.6f
        placeableLiquid = true
        selectionColumns = 6
        requirements(Category.distribution, IItems.陶钢, 4, IItems.铱板, 4)
        bundle {
            desc(zh_CN, "装甲传送带桥", "跨越任何地形货建筑传输物品,比普通桥更快,更远")
        }
    }
    val 基础卸载器 = Unloader("baseUninstalle").apply {
        speed = 60f / 10f
        health = 50
        bundle {
            desc(zh_CN, "基础卸载器", "卸载物品")
        }
        requirements(Category.distribution, IItems.高碳钢, 30, IItems.低碳钢, 10, IItems.铜锭, 15)
    }
    val 极速卸载器 = Unloader("speedUninstalle").apply {
        speed = 60f / 30f
        health = 80
        requirements(Category.distribution, IItems.铬锭, 30, IItems.铱板, 25, IItems.导能回路, 15)
        bundle {
            desc(zh_CN, "极速卸载器", "高速卸载物品")
        }
    }
    val 量子卸载器: Block = Unloader("electronicUninstaller").apply {
        speed = 60f / 60f
        health = 200
        squareSprite = false
        bundle {
            desc(zh_CN, "量子卸载器", "卸载物品,通过零损耗电子迁移转移高密度数据流")
        }
        requirements(Category.distribution, IItems.电子元件, 25, IItems.钴锭, 25f, IItems.导能回路, 5)
    }
    val 增生传送带桥: Block = TransferNode("growthBridge").apply {
        directionAny = false
        hasLiquids = false
        healAmount = 60f
        armor = 4f
        range = 37
        transportTime = 1f
        placeableLiquid = true
        consumePower(0.5f)
        requirements(Category.distribution, IItems.导能回路, 10, IItems.生物钢, 5)
        bundle {
            desc(zh_CN, "增生传送带桥", "先进的物品传输建筑,能超远距离快速传输物品")
        }
    }
    val 重型质量驱动器: Block = MassDriver("heavyDutyMassDrives").apply {
        size = 5
        reload = 600f
        range = 1280f
        consumePower(23.75f)
        dumpTime = 1
        knockback = 4f
        translation = 2f
        bulletSpeed = 12f
        rotateSpeed = 2f
        itemCapacity = 2400
        minDistribute = 600
        bulletLifetime = 160f
        shootSound = Sounds.artillery
        shootEffect = Fx.shootBig2
        smokeEffect = Fx.shootSmokeTitan
        receiveEffect = Fx.hitSquaresColor
        requirements(Category.distribution, IItems.钴锭, 335, IItems.铱板, 285, IItems.导能回路, 225, IItems.钴钢, 175)
        bundle {
            desc(zh_CN, "重型质量驱动器", "超远距离传输物品,收集一定物品后将其发射到另一个重型质量驱动器中,容量巨大但转速及发射速度缓慢")
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
}