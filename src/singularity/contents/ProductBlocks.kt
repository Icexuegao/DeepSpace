package singularity.contents

import arc.Core
import arc.func.Boolf
import arc.func.Boolf2
import arc.func.Intf
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.drills.*
import singularity.world.blocks.drills.MatrixMinerSector.MatrixMinerSectorBuild
import singularity.world.blocks.product.FloorCrafter
import singularity.world.consumers.SglConsumeFloor
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDirSpliceBlock
import singularity.world.draw.DrawExpandPlasma
import singularity.world.meta.SglAttribute
import kotlin.math.pow

class ProductBlocks : ContentList {
    override fun load() {
        rock_drill = object : FloorCrafter("rock_drill") {
            init {
                requirements(Category.production, ItemStack.with(Items.titanium, 45, Items.lead, 30, Items.copper, 30))
                size = 2
                liquidCapacity = 24f
                oneOfOptionCons = true
                health = 180

                updateEffect = Fx.pulverizeSmall
                craftEffect = Fx.mine
                craftEffectColor = Pal.lightishGray

                warmupSpeed = 0.005f

                hasLiquids = true

                autoSelect = true

                newConsume()
                consume!!.time(90f)
                consume!!.liquid(Liquids.water, 0.2f)
                consume!!.power(1.75f)
                newProduce()
                produce!!.item(SglItems.rock_bitumen, 1)

                newConsume()
                consume!!.time(60f)
                consume!!.liquid(Liquids.cryofluid, 0.2f)
                consume!!.power(1.75f)
                newProduce()
                produce!!.item(SglItems.rock_bitumen, 2)

                newBooster(1f)
                consume!!.add(SglConsumeFloor(SglAttribute.bitumen, 1.12f))

                draw = DrawMulti(
                    DrawBottom(),
                    object : DrawLiquidRegion(Liquids.water) {
                        init {
                            suffix = "_liquid"
                        }
                    },
                    object : DrawRegion("_rotator") {
                        init {
                            rotateSpeed = 1.5f
                            spinSprite = true
                        }
                    },
                    DrawDefault(),
                    DrawRegion("_top")
                )
            }
        }

        rock_crusher = object : FloorCrafter("rock_crusher") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.strengthening_alloy, 40,
                        SglItems.aerogel, 55,
                        Items.silicon, 60,
                        Items.titanium, 50,
                        Items.graphite, 60
                    )
                )
                size = 3

                warmupSpeed = 0.004f
                updateEffect = Fx.pulverizeSmall
                craftEffect = Fx.mine
                craftEffectColor = Items.sand.color

                oneOfOptionCons = false

                itemCapacity = 25
                liquidCapacity = 30f

                newConsume()
                consume!!.time(30f)
                consume!!.power(2.2f)
                consume!!.add(
                    SglConsumeFloor<FloorCrafterBuild>(
                        Blocks.stone, 1.2f / 9f,
                        Blocks.craters, 0.8f / 9f,
                        Blocks.dacite, 0.8f / 9f,
                        Blocks.shale, 1f / 9f,
                        Blocks.salt, 1f / 9f,
                        Blocks.moss, 0.6f / 9f,
                        Blocks.sporeMoss, 0.4f / 9f
                    )
                )!!.baseEfficiency = 0f

                newProduce()
                produce!!.item(Items.sand, 1)

                newOptionalProduct()
                consume!!.time(45f)
                consume!!.add(
                    SglConsumeFloor<FloorCrafterBuild>(
                        Blocks.stone, 0.4f / 9f,
                        Blocks.craters, 0.5f / 9f,
                        Blocks.salt, 2f / 9f
                    )
                )!!.baseEfficiency = 0f
                consume!!.optionalAlwaysValid = false
                produce!!.item(SglItems.alkali_stone, 1)

                newOptionalProduct()
                consume!!.add(SglConsumeFloor<FloorCrafterBuild>(Attribute.spores, 1f)).baseEfficiency = 0f
                consume!!.optionalAlwaysValid = false
                produce!!.liquid(SglLiquids.spore_cloud, 0.2f)

                newBooster(1.8f)
                consume!!.liquid(Liquids.water, 0.12f)

                draw = DrawMulti(
                    DrawBottom(),
                    DrawDefault(),
                    object : DrawBlock() {
                        var rim: TextureRegion? = null
                        val heatColor: Color = Color.valueOf("ff5512")

                        override fun draw(build: Building?) {
                            val e = build as NormalCrafterBuild

                            Draw.color(heatColor)
                            Draw.alpha(e.workEfficiency() * 0.6f * (1f - 0.3f + Mathf.absin(Time.time, 3f, 0.3f)))
                            Draw.blend(Blending.additive)
                            Draw.rect(rim, e.x, e.y)
                            Draw.blend()
                            Draw.color()
                        }

                        override fun load(block: Block) {
                            rim = Core.atlas.find(block.name + "_rim")
                        }
                    },
                    object : DrawRegion("_rotator") {
                        init {
                            rotateSpeed = 2.8f
                            spinSprite = true
                        }
                    },
                    DrawRegion("_top")
                )
            }
        }

        tidal_drill = object : ExtendableDrill("tidal_drill") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.degenerate_neutron_polymer, 50,
                        SglItems.strengthening_alloy, 120,
                        SglItems.aerogel, 90,
                        SglItems.crystal_FEX_power, 75,
                        SglItems.iridium, 40,
                        Items.phaseFabric, 60
                    )
                )
                size = 4

                energyCapacity = 1024f
                basicPotentialEnergy = 1024f

                itemCapacity = 50
                liquidCapacity = 30f

                bitHardness = 10
                drillTime = 180f

                newConsume()
                consume!!.energy(1.25f)

                newBooster(4.2f)
                consume!!.liquid(SglLiquids.phase_FEX_liquid, 0.15f)
                newBooster(3.1f)
                consume!!.liquid(SglLiquids.FEX_liquid, 0.12f)

                draw = DrawMulti(
                    DrawBottom(),
                    object : DrawExpandPlasma() {
                        init {
                            plasmas = 2
                            plasma1 = Pal.reactorPurple
                            plasma2 = Pal.reactorPurple2
                        }
                    },
                    DrawDefault(),
                    object : DrawBlock() {
                        override fun draw(build: Building) {
                            val e = build as ExtendableDrillBuild
                            val z = Draw.z()
                            Draw.z(Layer.bullet)
                            Draw.color(Pal.reactorPurple)
                            val lerp = (-2.2 * e.warmup.toDouble().pow(2.0) + 3.2 * e.warmup).toFloat()
                            Fill.circle(e.x, e.y, 3 * e.warmup)
                            SglDraw.drawLightEdge(
                                e.x, e.y,
                                26 * lerp, 2.5f * lerp, e.rotatorAngle, 1f,
                                16 * lerp, 2f * lerp, -e.rotatorAngle, 1f
                            )
                            Draw.z(z)
                            Draw.color()
                        }
                    },
                    DrawRegion("_top")
                )
            }
        }

        force_field_extender = object : ExtendMiner("force_field_extender") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.degenerate_neutron_polymer, 20,
                        SglItems.crystal_FEX, 20,
                        SglItems.iridium, 8,
                        SglItems.strengthening_alloy, 30
                    )
                )
                size = 2

                master = tidal_drill as ExtendableDrill?
                mining = SglFx.shrinkParticle(10f, 1.5f, 120f, Pal.reactorPurple)

                draw = DrawMulti(
                    DrawBottom(),
                    DrawDefault(),
                    object : DrawDirSpliceBlock<ExtendMinerBuild?>() {
                        init {
                            simpleSpliceRegion = true

                            spliceBits = Intf { e: ExtendMinerBuild? -> e!!.spliceDirBits }

                            planSplicer = Boolf2 { plan: BuildPlan?, other: BuildPlan? ->
                                plan!!.block is ExtendMiner && other!!.block is ExtendMiner
                                        &&  (other!!.block as ExtendMiner).chainable((plan!!.block as ExtendMiner)) && (plan!!.block as ExtendMiner).chainable((other!!.block as ExtendMiner))
                            }

                            layerRec = false
                        }
                    },
                    object : DrawBlock() {
                        override fun draw(build: Building?) {
                            val e = build as ExtendMinerBuild

                            Draw.z(Layer.effect)
                            Draw.color(Pal.reactorPurple)
                            SglDraw.drawLightEdge(e.x, e.y, 8 * e.warmup, 2f * e.warmup, 8 * e.warmup, 2f * e.warmup, 45f)
                            SglDraw.drawLightEdge(e.x, e.y, 15 * e.warmup, 2f * e.warmup, 45f, 0.6f, 15 * e.warmup, 2f * e.warmup, 45f, 0.6f)
                        }
                    }
                )
            }
        }

        matrix_miner = object : MatrixMiner("matrix_miner") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.matrix_alloy, 130,
                        SglItems.crystal_FEX_power, 80,
                        SglItems.strengthening_alloy, 90,
                        SglItems.aerogel, 90,
                        Items.phaseFabric, 65,
                        Items.graphite, 90,
                        SglItems.iridium, 45
                    )
                )
                size = 5
                matrixEnergyUse = 0.6f

                baseRange = 32
            }
        }

        matrix_miner_node = object : MatrixMinerSector("matrix_miner_node") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.matrix_alloy, 30,
                        SglItems.crystal_FEX_power, 25,
                        SglItems.strengthening_alloy, 16,
                        SglItems.aerogel, 20
                    )
                )
                size = 3
                drillSize = 3

                clipSize = (64 * Vars.tilesize).toFloat()

                energyMulti = 2f
            }
        }

        matrix_miner_extend = object : MatrixMinerComponent("matrix_miner_extend") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.matrix_alloy, 40,
                        SglItems.crystal_FEX_power, 40,
                        SglItems.strengthening_alloy, 60,
                        SglItems.iridium, 12,
                        SglItems.degenerate_neutron_polymer, 20
                    )
                )
                size = 3

                drillSize = 5
                energyMulti = 4f

                clipSize = (64 * Vars.tilesize).toFloat()

                draw = DrawMulti(
                    DrawDefault(),
                    object : DrawBlock() {
                        override fun draw(build: Building?) {

                            if (build is MatrixMinerComponentBuild) {
                                Draw.z(Layer.effect)
                                Draw.color(SglDrawConst.matrixNet)
                                Fill.circle(build.x, build.y, 2 * build.warmup)

                                Draw.color(Pal.reactorPurple)
                                Lines.stroke(2f * build.warmup)
                                SglDraw.drawCornerTri(
                                    build.x, build.y,
                                    20 * build.warmup,
                                    4 * build.warmup,
                                    -Time.time * 1.5f,
                                    true
                                )

                                if (build.owner != null) {
                                    for (plugin in build.owner!!.plugins) {
                                        if (plugin is MatrixMinerSectorBuild) {
                                            Lines.stroke(2f * build.warmup * plugin.warmup)
                                            SglDraw.drawCornerTri(
                                                plugin.drillPos!!.x, plugin.drillPos!!.y,
                                                36 * build.warmup * plugin.warmup,
                                                8 * build.warmup * plugin.warmup,
                                                -Time.time * 1.5f,
                                                true
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

        matrix_miner_pierce = object : MatrixMinerComponent("matrix_miner_pierce") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.matrix_alloy, 40,
                        SglItems.crystal_FEX_power, 40,
                        SglItems.crystal_FEX, 50,
                        SglItems.strengthening_alloy, 30,
                        SglItems.iridium, 20,
                        Items.phaseFabric, 40
                    )
                )
                size = 3

                pierceBuild = true
                energyMulti = 4f

                clipSize = (64 * Vars.tilesize).toFloat()

                draw = DrawMulti(
                    DrawDefault(),
                    object : DrawBlock() {
                        val param: FloatArray = FloatArray(9)
                        val index: Array<String?> = arrayOf<String?>("t1", "t2", "t3", "t4")
                        val index2: Array<String?> = arrayOf<String?>("t11", "t12", "t13", "t14")
                        val indexSelf: Array<String?> = arrayOf<String?>("ts1", "ts2", "ts3")

                        override fun draw(build: Building) {

                            if (build is MatrixMinerComponentBuild) {
                                rand.setSeed(build.id.toLong())

                                Draw.z(Layer.effect)
                                Draw.color(SglDrawConst.matrixNet)
                                Fill.circle(build.x, build.y, 2 * build.warmup)
                                Draw.color(Pal.reactorPurple)

                                for (i in 0..2) {
                                    for (d in 0..2) {
                                        param[d * 3] = rand.random(2f, 4f) / (d + 1) * (if (i % 2 == 0) 1 else -1)
                                        param[d * 3 + 1] = rand.random(0f, 360f)
                                        param[d * 3 + 2] = rand.random(8f, 20f) / ((d + 1) * (d + 1))
                                    }
                                    val v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, *param)).scl(build.warmup)
                                    Draw.color(Pal.reactorPurple)
                                    Fill.circle(build.x + v.x, build.y + v.y, build.warmup)

                                 //   var trail: Trail? = build.getVar(indexSelf[i])
                                //    if (trail == null) build.setVar(indexSelf[i], Trail(60).also { trail = it })

                                 //   trail!!.update(build.x + v.x, build.y + v.y)

                                  //  trail.draw(Pal.reactorPurple, build.warmup)
                                }

                                if (build.owner != null) {
                                    var ind = 0
                                    for (plugin in build.owner!!.plugins) {
                                        if (plugin is MatrixMinerSectorBuild) {
                                            val bool = rand.random(1) > 0.5f
                                            for (d in 0..2) {
                                                param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) 1 else -1)
                                                param[d * 3 + 1] = rand.random(0f, 360f)
                                                param[d * 3 + 2] = rand.random(16f, 40f) / ((d + 1) * (d + 1))
                                            }
                                            val v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, *param))

                                            for (d in 0..2) {
                                                param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
                                                param[d * 3 + 1] = rand.random(0f, 360f)
                                                param[d * 3 + 2] = rand.random(12f, 30f) / ((d + 1) * (d + 1))
                                            }
                                            val v2 = Tmp.v2.set(MathTransform.fourierSeries(Time.time, *param))
                                            Draw.color(Pal.reactorPurple)
                                            Fill.circle(plugin.drillPos!!.x + v.x, plugin.drillPos!!.y + v.y, 1.5f * build.warmup * plugin.warmup)
                                            Fill.circle(plugin.drillPos!!.x + v2.x, plugin.drillPos!!.y + v2.y, build.warmup * plugin.warmup)


                                         //   var trail: Trail? = build.getVar(index[ind])
                                       //     if (trail == null) build.setVar(index[ind], Trail(72).also { trail = it })
                                       //     var trail2: Trail? = build.getVar(index2[ind])
                                       //     if (trail2 == null) build.setVar(index2[ind], Trail(72).also { trail2 = it })

                                         //   trail!!.draw(Pal.reactorPurple, 1.5f * build.warmup * plugin.warmup)
                                        //    trail.update(plugin.drillPos.x + v.x, plugin.drillPos.y + v.y)

                                        //    trail2!!.draw(Pal.reactorPurple, build.warmup * plugin.warmup)
                                        //    trail2.update(plugin.drillPos.x + v2.x, plugin.drillPos.y + v2.y)
                                        }

                                        ind++
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }

        matrix_miner_overdrive = object : MatrixMinerComponent("matrix_miner_overdrive") {
            init {
                requirements(
                    Category.production, ItemStack.with(
                        SglItems.matrix_alloy, 40,
                        SglItems.crystal_FEX_power, 50,
                        SglItems.strengthening_alloy, 40,
                        SglItems.aerogel, 40,
                        SglItems.iridium, 15,
                        Items.phaseFabric, 60
                    )
                )
                size = 3
                range = 16
                drillMoveMulti = 2f
                energyMulti = 2f

                clipSize = (10 * Vars.tilesize).toFloat()

                liquidCapacity = 40f

                newConsume()
                consume!!.time(180f)
                consume!!.item(Items.phaseFabric, 1)

                newBoost(1f, 0.6f, Boolf { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.3f)

                draw = DrawMulti(
                    DrawDefault(),
                    object : DrawBlock() {
                        override fun draw(build: Building?) {

                            if (build is MatrixMinerComponentBuild) {
                                Draw.z(Layer.effect)
                                Draw.color(SglDrawConst.matrixNet)
                                Fill.circle(build.x, build.y, 2 * build.warmup)

                                Lines.stroke(1.4f * build.warmup, Pal.reactorPurple)
                                SglDraw.dashCircle(build.x, build.y, 10f, 5, 180f, Time.time)

                                if (build.owner != null) {
                                    Lines.stroke(1.6f * build.warmup, Pal.reactorPurple)
                                    SglDraw.dashCircle(build.owner!!.x, build.owner!!.y, 18f, 6, 180f, -Time.time)
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    companion object {
        /**岩层钻井机 */
        var rock_drill: Block? = null

        /**岩石粉碎机 */
        var rock_crusher: Block? = null

        /**潮汐钻头 */
        var tidal_drill: Block? = null

        /**力场延展仓 */
        var force_field_extender: Block? = null

        /**矩阵矿床 */
        var matrix_miner: Block? = null

        /**采掘扇区 */
        var matrix_miner_node: Block? = null

        /**矩阵增幅器 */
        var matrix_miner_overdrive: Block? = null

        /**量子隧穿仪 */
        var matrix_miner_pierce: Block? = null

        /**谐振增压组件 */
        var matrix_miner_extend: Block? = null
    }
}