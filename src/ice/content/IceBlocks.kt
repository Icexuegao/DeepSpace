@file:Suppress("LeakingThis")

package ice.content

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.struct.ObjectMap
import arc.struct.Seq
import ice.Ice
import ice.library.drawf.DrawLiquidOutputs
import ice.world.blocks.crafting.EffectGenericCrafter
import ice.world.blocks.crafting.multipleCrafter.Formula
import ice.world.blocks.crafting.multipleCrafter.MultipleCrafter
import ice.world.blocks.crafting.ore.OreBlock
import ice.world.blocks.crafting.ore.OreFormula
import ice.world.blocks.distribution.Randomer
import ice.world.blocks.distribution.digitalStorage.DigitalConduit
import ice.world.blocks.distribution.digitalStorage.DigitalInput
import ice.world.blocks.distribution.digitalStorage.DigitalOutput
import ice.world.blocks.distribution.digitalStorage.DigitalStorage
import ice.world.blocks.effects.ResBox
import ice.world.blocks.effects.fleshAndBloodCoreBlock
import ice.world.blocks.liquid.LiquidClassifier
import ice.world.blocks.liquid.MultipleLiquidBlock
import ice.world.blocks.liquid.pumpChamber
import ice.world.blocks.production.miner.MinerTower
import ice.world.meta.IceAttribute
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.effect.MultiEffect
import mindustry.gen.Sounds
import mindustry.graphics.CacheLayer
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.*
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.blocks.storage.Unloader
import mindustry.world.consumers.*
import mindustry.world.draw.*
import kotlin.math.max

object IceBlocks {
    val blocks: Seq<Block> = Seq()
    val floors = ObjectMap<String, Floor>()

    val 血晶尖刺 = object : TallBlock("bloodCrystalSpikes") {
        init {
            variants = 3
            clipSize = 128f
        }
    }
    val 血孢子丛 = object : Prop("bloodSporophore") {
        init {
            hasShadow = true
            variants = 3
            breakSound = Sounds.plantBreak
        }
    }
    val 风蚀沙柱 = object : TallBlock("windErodedSandPillar") {
        init {
            hasShadow = true
            rotationRand = 360f
            variants = 2
        }

        override fun drawBase(tile: Tile) {
            val rot = Mathf.randomSeedRange((tile.pos() + 1).toLong(), rotationRand)
            Draw.rect(if (variants > 0) variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
                max(0, variantRegions.size - 1))] else region, tile.worldx(), tile.worldy(), rot)
            Draw.z(Layer.power)
        }
    }
    val 大风蚀沙柱 = object : TallBlock("windErodedSandPillarBig") {
        init {
            hasShadow = true
            size = 2
            variants = 1
            rotationRand = 45f
        }

        override fun drawBase(tile: Tile) {
            val i1 = tile.x + 1
            val i2 = tile.y + 1
            val t1 = Vars.world.tile(i1, tile.y.toInt())
            val t2 = Vars.world.tile(i1, i2)
            val t3 = Vars.world.tile(tile.x.toInt(), tile.y.toInt())
            if (t1.block() === t2.block() && t2.block() === t3.block()) {
                val rot = Mathf.randomSeedRange((tile.pos() + 1).toLong(), rotationRand)
                Draw.z(Layer.power + 1)
                Draw.rect(if (variants > 0) variantRegions[Mathf.randomSeed(tile.pos().toLong(), 0,
                    max(0, (variantRegions.size - 1)))] else region, tile.worldx() + 4, tile.worldy() + 4, rot)
            }
        }
    }
    val 血孢子树 = object : TreeBlock("bloodSporophoreTree") {
        init {
            attributes[IceAttribute.bloodSpore] = 1f
        }
    }
    val 利芽 = TallBlock("edgeBud")

    val 血浅滩 = object : ShallowLiquid("bloodShoal") {
        init {
            mapColor = Color.valueOf("ff656a")
            cacheLayer = CacheLayer.water
            shallow = true
            variants = 0
            itemDrop = Items.sand
            liquidDrop = IceLiquids.血浆
            speedMultiplier = 0.8f
            statusDuration = 50f
            albedo = 0.9f
            supportsOverlay = true
        }
    }
    val 血池 = object : IceFloor("thickBlood", 0) {
        init {
            speedMultiplier = 0.5f
            status = StatusEffects.wet
            statusDuration = 90f
            liquidDrop = IceLiquids.血浆
            isLiquid = true
            cacheLayer = CacheLayer.water
            albedo = 0.9f
            supportsOverlay = true
        }
    }
    val 深血池 = object : IceFloor("deepThickBlood", 0) {
        init {
            speedMultiplier = 0.2f
            liquidDrop = IceLiquids.血浆
            liquidMultiplier = 1.5f
            isLiquid = true
            status = StatusEffects.wet
            statusDuration = 120f
            drownTime = 200f
            cacheLayer = CacheLayer.water
            albedo = 0.9f
            supportsOverlay = true
        }
    }
    val 潮汐水石 = object : IceFloor("nightTideStoneWater", 3) {
        init {
            cacheLayer = CacheLayer.water
            liquidDrop = Liquids.water
        }
    }
    val 硫酸池 = object : IceFloor("sulfatePool", 0) {
        init {
            liquidDrop = IceLiquids.硫酸
            cacheLayer = CacheLayer.water
            drownTime = 200f
            speedMultiplier = 0.4f
            isLiquid = true
        }
    }
    val 红冰 = IceFloor("redIce", 3)
    val 红冰墙 = IceStaticWall("redIceWall")
    val 金珀沙 = IceFloor("goldPearlGrit", 3)
    val 金珀沙墙 = IceStaticWall("goldPearlGritWall")
    val 皎月银沙 = IceFloor("silverSand", 3)
    val 皎月银沙墙 = IceStaticWall("silverSandWall")
    val 风蚀沙地 = IceFloor("windErodedSand", 3)
    val 风蚀沙墙 = IceStaticWall("windErodedSandWall")
    val 光辉板岩 = IceFloor("brillianceSlate", 3)
    val 光辉板岩墙 = IceStaticWall("brillianceSlateWall")
    val 风蚀砂地 = IceFloor("windErodedGrit", 3)
    val 云英岩 = IceFloor("greisen", 3)
    val 云英岩墙 = IceStaticWall("greisenWall")
    val 流纹岩 = IceFloor("liparite", 3)
    val 流纹岩墙 = IceStaticWall("lipariteWall")
    val 潮汐石 = IceFloor("nightTideStone", 4)
    val 潮汐石墙 = IceStaticWall("nightTideStoneWall")
    val 侵蚀层地 = IceFloor("erosionalSlate", 3)
    val 侵蚀层地墙 = IceStaticWall("erosionalSlateWall")

    val 电子存储 = DigitalStorage("digitalStorage")
    val 电子管道 = DigitalConduit("digitalConduit")
    val 电子管道输入 = DigitalInput("digitalInput")
    val 电子管道输出 = DigitalOutput("digitalOutput")
    val 挖掘塔 = MinerTower("minerTower")
    val 随机源 = object : Randomer("randomSource") {}


    val 泵腔 = object : pumpChamber("pumpChamber") {
        init {
            requirements(Category.liquid, ItemStack.with(IceItems.肌腱, 40, IceItems.碎骨, 10, IceItems.无名肉块, 60))
        }
    }
    val 流体枢纽 = object : MultipleLiquidBlock("fluidJunction") {
        init {
            liquidCapacity = 1000f
            size = 3
            health = size * size * 100
            requirements(Category.liquid, ItemStack.with(Items.copper, 10))
        }
    }
    val 液体分类器 = object : LiquidClassifier("liquidClassifier") {
        init {
            size = 1
            requirements(Category.liquid, ItemStack.with(Items.copper, 1))
        }
    }

    val 集成分发器 = object : EffectGenericCrafter("integratedFactory") {
        init {
            drawer = DrawMulti(DrawRegion("-bottom"), DrawRegion("-top"))
            itemCapacity = 20
            health = 200
            outputItem = ItemStack(IceItems.集成电路, 1)
            consumeItems(*ItemStack.with(IceItems.单晶硅, 1, IceItems.石墨烯, 2, IceItems.石英玻璃, 1))
            craftTime = 60f
            craftEffect =
                MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin, IceEffects.hitLaserBlast)
            size = 3
            requirements(Category.crafting, ItemStack.with(IceItems.铜锭, 19))
        }
    }
    val 单晶硅厂 = object : GenericCrafter("monocrystallineSiliconFactory") {
        init {
            requirements(Category.crafting, ItemStack.with(IceItems.红冰, 12))
            outputItem = ItemStack(IceItems.单晶硅, 1)
            craftTime = 60f
            health = 360
            size = 3
            hasPower = true
            drawer = DrawMulti(DrawRegion("-bottom"), DrawRegion("-rotate", 9f, true), DrawDefault(),
                DrawFlame(Color.valueOf("ff9c71")))
            consumeItems(*ItemStack.with(Items.pyratite, 1, IceItems.石英, 3))
            consumePower(1.8f)
        }
    }
    val 铸铜坊 = object : GenericCrafter("copperFoundry") {
        init {
            size = 4
            health = 200
            craftTime = 90f
            outputItem = ItemStack(IceItems.黄铜锭, 3)
            consumeItems(*ItemStack.with(IceItems.铜锭, 3, IceItems.锌锭, 1))
            requirements(Category.crafting, ItemStack.with(IceItems.铜锭, 200, IceItems.低碳钢, 150))
        }
    }
    val 多合成工厂 = object : MultipleCrafter("mineralProcessor") {
        init {
            outputsPower = true
            requirements(Category.crafting, ItemStack.with(Items.copper, 20))
            size = 3
            itemCapacity = 30
            liquidCapacity = 30f
            drawer = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(Liquids.slag, 2f), object : DrawPistons() {
                init {
                    sinMag = 2f
                }
            }, DrawRegion(), object : DrawLiquidOutputs("-output2", 2) {
                init {
                    alpha = 0.7f
                    color = Color.valueOf("9fff9c")
                    glowIntensity = 0.3f
                    glowScale = 6f
                }
            }, object : DrawLiquidOutputs("-output4", 2) {
                init {
                    alpha = 0.7f
                    color = Color.valueOf("9fff9c")
                    glowIntensity = 0.3f
                    glowScale = 6f
                }
            }, DrawFlame())
            formulas.addsFormulaArray(object : Formula() {
                init {
                    craftTime = 120f
                    craftEffect = Fx.absorb
                    setInput(
                        arrayOf(ConsumeItems(ItemStack.with(Items.copper, 1)), ConsumeLiquid(Liquids.cryofluid, 0.1f),
                            ConsumePower(0.1f, 0.0f, false)))
                    setOutput(ItemStack.with(Items.titanium, 2))
                    setOutput(LiquidStack.with(Liquids.water, 0.05f))
                }
            }, object : Formula() {
                init {
                    displayName = "11111"
                    craftEffect = Fx.absorb
                    craftTime = 90f
                    setInput(arrayOf(ConsumeItems(ItemStack.with(Items.copper, 1)), ConsumeLiquid(Liquids.oil, 0.1f)))
                    setOutput(ItemStack.with(Items.coal, 6))
                }
            }, object : Formula() {
                init {
                    craftEffect = Fx.absorb
                    craftTime = 120f
                    setInput(arrayOf<Consume>(ConsumeLiquid(Liquids.slag, 0.1f)))
                    setOutput(ItemStack.with(Items.scrap, 6))
                }
            }, object : Formula() {
                init {
                    craftEffect = Fx.absorb
                    craftTime = 30f
                    setInput(arrayOf<Consume>(ConsumeItemFlammable()))
                    setOutput(ItemStack.with(Items.sand, 1))
                }
            }, object : Formula() {
                init {
                    craftTime = 30f
                    craftEffect = Fx.absorb
                    setInput(arrayOf<Consume>(ConsumeLiquid(Liquids.water, 0.1f)))
                    liquidOutputDirections = intArrayOf(1, 3)
                    setOutput(LiquidStack.with(Liquids.slag, 0.05f, Liquids.arkycite, 0.05f))
                }
            })
        }
    }
    val 矿石加工机 = object : OreBlock("ddadw") {
        init {
            formula.with(object : OreFormula() {
                init {
                    crftTime = 60
                    addInput(IceItems.方铅矿, 1)
                    addInput(IceItems.石英砂, 1)
                    addOutput(Items.lead, 1, 5)
                    addOutput(Items.copper, 2, 60)
                    addOutput(Items.beryllium, 3, 7)
                }
            }, object : OreFormula() {
                init {
                    crftTime = 30
                    addInput(IceItems.黄铜矿, 4)
                    addInput(IceItems.生煤, 1)
                    addInput(IceItems.黄铜矿, 4)
                    addOutput(Items.lead, 8, 6)
                    addOutput(Items.graphite, 2, 69)
                }
            })
        }
    }

    val 遗弃资源箱 = ResBox("resBox")
    val 开采核心 = object : CoreBlock("minerCore") {
        init {
            size = 3
            health = 5000
            itemCapacity = 2000
            requirements(Category.effect, ItemStack.with(IceItems.低碳钢, 4000, IceItems.锌锭, 1500))
        }
    }
    val 血肉枢纽 = object : fleshAndBloodCoreBlock("fleshAndBloodhinge") {
        init {
            health = -1
            size = 4
            itemCapacity = 6000
            squareSprite = false
            requirements(Category.effect, ItemStack.with(IceItems.无名肉块, 2300, IceItems.碎骨, 2000))
        }
    }
    val 电子装卸器 = object : Unloader("electronicUninstaller") {
        init {
            requirements(Category.effect, ItemStack.with(IceItems.铜锭, 10, IceItems.单晶硅, 5))
            speed = 1.7142f
            health = 100
            size = 1
            itemCapacity = 10
        }
    }

    fun load() {
        Vars.content.blocks().each { b: Block ->
            if (b.minfo.mod === Ice.ice) {
                blocks.add(b)
                b.localizedName = Core.bundle["block." + b.name.replace(Ice.NAME + "-", "") + ".name", b.name]
                b.description = Core.bundle.getOrNull("block." + b.name.replace(Ice.NAME + "-", "") + ".description")
                b.details = Core.bundle.getOrNull("block." + b.name.replace(Ice.NAME + "-", "") + ".details")
            }
        }
    }

    open class IceStaticWall(name: String) : StaticWall(name) {
        init {
            floors[name.replace("Wall", "")]?.asFloor()?.wall = this
        }
    }

    open class IceFloor(name: String, variants: Int) : Floor(name, variants) {
        init {
            floors.put(name, this)
        }

        override fun icons(): Array<TextureRegion> {
            return if (variants == 0) arrayOf(Core.atlas.find(name)) else arrayOf(Core.atlas.find(name + "1"))
        }
    }
}


