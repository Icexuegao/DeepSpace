package singularity.contents

import arc.Core
import arc.func.Cons
import arc.func.Floatf
import arc.func.Func
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.*
import mindustry.world.meta.BuildVisibility
import mindustry.world.meta.Stats
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.type.SglCategory
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.nuclear.*
import singularity.world.blocks.nuclear.EnergyContainer.EnergyContainerBuild
import singularity.world.blocks.product.NormalCrafter
import singularity.world.blocks.product.NormalCrafter.NormalCrafterBuild
import singularity.world.consumers.SglConsumers
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawExpandPlasma
import singularity.world.draw.DrawReactorHeat
import singularity.world.draw.DrawRegionDynamic
import singularity.world.meta.SglStat
import singularity.world.particles.SglParticleModels
import universecore.world.consumers.ConsumeItems
import universecore.world.consumers.ConsumeLiquids
import universecore.world.particles.MultiParticleModel
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel
import universecore.world.particles.models.*
import kotlin.math.max

class NuclearBlocks : ContentList {
    companion object {
        var 中子能量节点: Block? = null
        var 相位核能塔: Block? = null
        var 中子缓冲器: Block? = null
        var 晶簇缓冲器: Block? = null
        var 高压缓冲器: Block? = null
        var 中子缓冲矩阵: Block? = null
        var 晶体储能簇: Block? = null
        var 环形电磁储能簇: Block? = null
        var 衰变仓: Block? = null
        var 中子能发电机: Block? = null
        var 核子冲击反应堆: Block? = null
        var 核反应堆: Block? = null
        var 晶格反应堆: Block? = null
        var 超限裂变反应堆: Block? = null
        var 托卡马克点火装置: Block? = null
        var 超导约束轨道: Block? = null
        var 潮汐约束轨道: Block? = null
        var 核能源: Block? = null
        var 核能黑洞: Block? = null
    }

    override fun load() {
        中子能量节点 = NuclearNode("nuclear_pipe_node").apply {
            bundle {
                desc(zh_CN, "中子能量节点", "中子能传输节点,用于传输核能量,以链接多个节点的方式构建核能运输网络")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 8,
                    SglItems.crystal_FEX, 4
                )
            )
            size = 2

            energyCapacity = 4096f
        }

        相位核能塔 = NuclearNode("phase_pipe_node").apply {
            bundle {
                desc(zh_CN, "相位能量塔", "大型中子能运输传输设备,可以承载更高的能量负载和更多的链接数量")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 24,
                    SglItems.crystal_FEX, 16,
                    Items.phaseFabric, 15
                )
            )
            size = 3

            maxLinks = 18
            linkRange = 22f

            energyCapacity = 16384f
        }

        中子缓冲器 = EnergyBuffer("energy_buffer").apply {
            bundle {
                desc(zh_CN, "中子缓冲器", "小型能量缓冲设施,用于稳定能量水平和能量升降压,可进行低能区调压")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 40,
                    SglItems.crystal_FEX, 50,
                    SglItems.aerogel, 40,
                    Items.silicon, 60
                )
            )
            size = 2
            energyCapacity = 1024f
            minPotential = 128f
            maxPotential = 1024f
        }

        晶簇缓冲器 = EnergyBuffer("crystal_buffer").apply {
            bundle {
                desc(zh_CN, "晶体势垒", "中型能量缓冲设施,具有更大的能量缓冲空间,可进行中压区调压")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 60,
                    SglItems.crystal_FEX, 75,
                    SglItems.aerogel, 50,
                    Items.silicon, 75,
                    Items.phaseFabric, 80
                )
            )
            size = 3
            energyCapacity = 4096f
            minPotential = 512f
            maxPotential = 4096f
        }

        高压缓冲器 = EnergyBuffer("high_voltage_buffer").apply {
            bundle {
                desc(zh_CN, "高压缓冲器", "大型能量缓冲设施,更大的缓冲空间基本可以满足任何情况的能量缓冲,可用于进行高压区调压")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 90,
                    SglItems.crystal_FEX, 120,
                    SglItems.crystal_FEX_power, 80,
                    SglItems.iridium, 50,
                    Items.silicon, 125,
                    Items.phaseFabric, 90,
                    Items.surgeAlloy, 80
                )
            )
            size = 4
            energyCapacity = 16384f
            minPotential = 2048f
            maxPotential = 16384f
        }

        中子缓冲矩阵 = EnergyBuffer("neutron_matrix_buffer").apply {
            bundle {
                desc(zh_CN, "中子缓冲矩阵", "超大型能量缓冲阵列,复合缓冲具备最大的缓冲容量,其具备从低压到超高压的全域调压范围")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 120,
                    SglItems.crystal_FEX, 140,
                    SglItems.crystal_FEX_power, 100,
                    SglItems.iridium, 75,
                    SglItems.matrix_alloy, 80,
                    Items.phaseFabric, 100,
                    Items.surgeAlloy, 80
                )
            )
            size = 5
            energyCapacity = 65536f
            minPotential = 1f
            maxPotential = 65536f
        }

        晶体储能簇 = EnergyContainer("crystal_container").apply {
            bundle {
                desc(zh_CN, "晶体储能簇", "晶体式中子能存储器,用于存储中子能")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.crystal_FEX, 160,
                    SglItems.aerogel, 80,
                    SglItems.matrix_alloy, 80,
                    SglItems.strengthening_alloy, 100,
                    Items.silicon, 60,
                    Items.phaseFabric, 55
                )
            )
            size = 3
            energyCapacity = (2 shl 16).toFloat()
            energyPotential = 1024f
            maxEnergyPressure = 4096f

            draw = DrawMulti(
                DrawBottom(),
                DrawDefault(),
                object : DrawRegionDynamic<EnergyContainerBuild>("_top") {
                    init {
                        layer = Layer.effect
                        color = Func { e: EnergyContainerBuild? -> SglDrawConst.fexCrystal }
                        alpha = Floatf { e: EnergyContainerBuild? -> Mathf.clamp(e!!.getEnergy() / e.energyCapacity()) }
                    }
                }
            )
        }

        环形电磁储能簇 = EnergyContainer("magnetic_energy_container").apply {
            bundle {
                desc(zh_CN, "环形电磁储能簇", "约束式主动中子能存储设备,可以存储极大量的能量,但是需要消耗电力,若电力供应不足会发生泄漏")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.crystal_FEX, 200,
                    SglItems.crystal_FEX_power, 100,
                    SglItems.matrix_alloy, 120,
                    SglItems.strengthening_alloy, 120,
                    SglItems.aerogel, 100,
                    Items.surgeAlloy, 80,
                    Items.silicon, 120
                )
            )
            size = 5
            energyCapacity = (2 shl 19).toFloat()
            energyPotential = 4096f
            maxEnergyPressure = 16384f

            warmupSpeed = 0.02f

            newConsume()
            consume!!.power(12f)

            setStats = Cons { s: Stats? ->
                s!!.add(SglStat.special, Core.bundle.format("infos.nonCons", Core.bundle.format("infos.energyContainerLeak", 3600)))
            }

            nonCons = Cons { ne: EnergyContainerBuild? ->
                val leak: Float = ne!!.getEnergy().coerceAtMost(60f)
                if (leak > 0) {
                    ne.energy.handle(-leak * Time.delta * (1 - ne.warmup))
                    val rate = Mathf.clamp(ne.getEnergy() / ne.energyCapacity())
                    if (rate > 0.5f) {
                        if (Mathf.chanceDelta((rate * 0.007f).toDouble())) {
                            SglTurrets.spilloverEnergy!!.create(ne, Team.derelict, ne.x, ne.y, Mathf.random(360f), Mathf.random(0.4f, 1f))
                        }
                    }

                    if (Mathf.chanceDelta(((1 - ne.warmup) * 0.05f).toDouble())) {
                        Angles.randLenVectors(
                            System.nanoTime(), 1, 2f, 3.5f
                        ) { x: Float, y: Float ->
                            val create = SglParticleModels.floatParticle.create(ne.x, ne.y, SglDrawConst.fexCrystal, x, y, 2.3f)
                            create.strength = 0.4f
                        }
                    }

                    if (Mathf.chanceDelta(((1 - ne.warmup) * 0.075f).toDouble())) {
                        SglFx.circleSparkMini.at(ne.x, ne.y, Tmp.c1.set(SglDrawConst.fexCrystal).lerp(SglDrawConst.matrixNet, Mathf.random(0f, 1f)))
                    }
                }
            }

            draw = DrawMulti(
                DrawBottom(),
                object : DrawBlock() {
                    override fun draw(build: Building) {
                        LiquidBlock.drawTiledFrames(
                            build.block.size,
                            build.x, build.y,
                            4f,
                            SglLiquids.spore_cloud,
                            build.warmup()
                        )
                    }
                },
                object : DrawBlock() {
                    override fun draw(build: Building?) {
                        super.draw(build)

                        SglDraw.drawBloomUnderBlock<EnergyContainerBuild?>(build as EnergyContainerBuild?) { e: EnergyContainerBuild? ->
                            MathRenderer.setThreshold(0.65f, 0.8f)
                            MathRenderer.setDispersion(0.7f * e!!.warmup)
                            Draw.color(SglDrawConst.fexCrystal)
                            MathRenderer.drawCurveCircle(e.x, e.y, 9.5f, 4, 6f, -Time.time * 0.8f)
                            Draw.color(SglDrawConst.matrixNet)
                            MathRenderer.drawCurveCircle(e.x, e.y, 9.5f, 3, 6f, Time.time * 1.2f)
                        }
                        Draw.z(Layer.block + 5)
                    }
                },
                DrawDefault(),
                object : DrawBlock() {
                    val param: FloatArray = FloatArray(9)
                    val rand: Rand = Rand()

                    override fun draw(build: Building?) {
                        super.draw(build)
                        val e = build as EnergyContainerBuild
                        val l = Interp.pow2Out.apply(Mathf.clamp(e.getEnergy() / e.energyCapacity()))

                        Draw.z(Layer.effect)
                        Draw.color(SglDrawConst.fexCrystal)
                        Fill.circle(e.x, e.y, 6 * l)
                        Draw.color(Color.white)
                        Fill.circle(e.x, e.y, 4 * l)

                        rand.setSeed(build.id.toLong())
                        for (i in 0..2) {
                            val bool = rand.random(1f) > 0.5f
                            for (d in 0..2) {
                                param[d * 3] = rand.random(2f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
                                param[d * 3 + 1] = rand.random(360f)
                                param[d * 3 + 2] = rand.random(5f, 8f) / ((d + 1) * (d + 1))
                            }
                            val v = MathTransform.fourierSeries(Time.time, *param).scl(l)

                            Draw.color(SglDrawConst.fexCrystal, SglDrawConst.matrixNet, Mathf.absin(Time.time * rand.random(4.8f, 7.2f), 1f))
                            Fill.circle(e.x + v.x, e.y + v.y, 1.3f * l)
                        }
                    }
                }
            )
        }

        衰变仓 = NormalCrafter("decay_bin").apply {
            bundle {
                desc(zh_CN, "衰变仓", "放射性物质进行衰变产生少量的核能量,可能存在副产物")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 60,
                    SglItems.crystal_FEX, 40,
                    Items.silicon, 50,
                    Items.lead, 80,
                    Items.metaglass, 40
                )
            )
            size = 2
            autoSelect = true
            canSelect = false

            newConsume()
            consume!!.time(600f)
            consume!!.item(SglItems.uranium_235, 1)
            newProduce()
            produce!!.energy(0.25f)
            produce!!.item(Items.thorium, 1)
            newConsume()
            consume!!.time(540f)
            consume!!.item(SglItems.plutonium_239, 1)
            newProduce()
            produce!!.energy(0.35f)
            newConsume()
            consume!!.time(900f)
            consume!!.item(SglItems.uranium_238, 1)
            newProduce()
            produce!!.energy(0.12f)
            newConsume()
            consume!!.time(450f)
            consume!!.item(Items.thorium, 1)
            newProduce()
            produce!!.energy(0.2f)

            updateEffect = Fx.generatespark
            updateEffectChance = 0.01f

            draw = DrawMulti(
                DrawBottom(),
                DrawDefault(),
                object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
                    init {
                        color = Func { e: NormalCrafterBuild? ->
                            val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as SglConsumers).first()
                            when (cons) {
                                is ConsumeLiquids<*> -> {
                                    var liquid = cons.consLiquids!![0].liquid
                                    if (liquid === Liquids.water) liquid = cons.consLiquids!![1].liquid
                                    return@Func liquid.color
                                }

                                is ConsumeItems<*> -> {
                                    val item = cons.consItems!![0].item
                                    return@Func item.color
                                }

                                else -> return@Func Color.white
                            }
                        }
                        alpha = Floatf { e: NormalCrafterBuild? ->
                            val cons = if (e!!.consumer.current == null) null else ((e.consumer.current) as SglConsumers).first()
                            when (cons) {
                                is ConsumeLiquids<*> -> {
                                    var liquid = cons.consLiquids!![0].liquid
                                    if (liquid === Liquids.water) liquid = cons.consLiquids!![1].liquid
                                    return@Floatf e.liquids.get(liquid) / e.block.liquidCapacity
                                }

                                is ConsumeItems<*> -> {
                                    val item = cons.consItems!![0].item
                                    return@Floatf e.items.get(item).toFloat() / e.block.itemCapacity
                                }

                                else -> return@Floatf 0f
                            }
                        }
                    }
                }
            )
        }


        中子能发电机 = NormalCrafter("neutron_generator").apply {
            bundle {
                desc(zh_CN, "中子能发电机", "利用经典的中子分解技术,使用核能量生产大量电力")
            }
            requirements(
                Category.power, ItemStack.with(
                    SglItems.strengthening_alloy, 100,
                    SglItems.crystal_FEX_power, 80,
                    SglItems.uranium_238, 75,
                    Items.phaseFabric, 70,
                    SglItems.aerogel, 90
                )
            )
            size = 3

            energyCapacity = 1024f
            basicPotentialEnergy = 256f
            warmupSpeed = 0.0075f

            newConsume()
            consume!!.energy(4f)
            newProduce()
            produce!!.power(50f)

            draw = DrawMulti(
                DrawBottom(),
                DrawDefault(),
                object : DrawPlasma() {
                    init {
                        suffix = "_plasma_"
                        plasma1 = Pal.reactorPurple
                        plasma2 = Pal.reactorPurple2
                    }
                },
                DrawRegion("_top")
            )
        }

        核子冲击反应堆 = NormalCrafter("nuclear_impact_reactor").apply {
            bundle {
                desc(zh_CN, "核子冲击反应堆", "先进的核内爆式冲击反应堆,利用力场约束使核爆炸以最高的效率推动压电转子发电")
            }
            requirements(
                Category.power, ItemStack.with(
                    SglItems.strengthening_alloy, 260,
                    SglItems.aerogel, 240,
                    SglItems.uranium_238, 300,
                    Items.plastanium, 220,
                    Items.silicon, 280,
                    Items.phaseFabric, 160,
                    Items.surgeAlloy, 200
                )
            )
            size = 5
            itemCapacity = 30
            liquidCapacity = 35f

            craftEffect = SglFx.explodeImpWaveBig
            craftEffectColor = Pal.reactorPurple

            updateEffect = SglFx.impWave
            effectRange = 2f
            updateEffectChance = 0.025f
               ambientSound = Sounds.loopMachineSpin
            ambientSoundVolume = 0.55f
              craftedSound = Sounds.explosionPlasmaSmall
            craftedSoundVolume = 1f
            val model: ParticleModel = MultiParticleModel(
                SizeVelRelatedParticle(),
                TargetMoveParticle().apply {
                    dest = Func { p: Particle -> p.dest }
                    deflection = Floatf { p: Particle -> p.eff }
                },
                RandDeflectParticle().apply {
                    deflectAngle = 0f
                    strength = 0.125f
                },
                TrailFadeParticle().apply {
                    trailFade = 0.04f
                    fadeColor = Pal.lightishGray
                    colorLerpSpeed = 0.03f
                },
                ShapeParticle(),
                DrawDefaultTrailParticle()
            )

            craftTrigger = Cons { e: NormalCrafterBuild ->
                for (particle in Particle.get { p -> p.x < e.x + 20 && p.x > e.x - 20 && p.y < e.y + 20 && p.y > e.y - 20 }) {
                    particle!!.remove()
                }
                Effect.shake(4f, 18f, e.x, e.y)
                Angles.randLenVectors(System.nanoTime(), Mathf.random(5, 9), 4.75f, 6.25f) { x: Float, y: Float ->
                    Tmp.v1.set(x, y).setLength(4f)
                    val p: Particle = model.create(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Pal.reactorPurple, x, y, Mathf.random(5f, 7f))
                    p.dest = Vec2(e.x, e.y)
                    p.eff = e.workEfficiency() * 0.15f
                }
            }
            crafting = Cons { e: NormalCrafterBuild? ->
                if (Mathf.chanceDelta(0.02)) Angles.randLenVectors(
                    System.nanoTime(), 1, 2f, 3.5f
                ) { x: Float, y: Float ->
                    SglParticleModels.floatParticle.create(e!!.x, e.y, Pal.reactorPurple, x, y, Mathf.random(3.25f, 4f)) }
            }

            warmupSpeed = 0.0008f

            newConsume()!!.consValidCondition { e: NormalCrafterBuild? -> e!!.power.status >= 0.99f }
            consume!!.item(SglItems.concentration_uranium_235, 1)
            consume!!.power(80f)
            consume!!.liquid(Liquids.cryofluid, 0.6f)
            consume!!.time(180f)
            newProduce()
            produce!!.power(400f)

            newConsume()!!.consValidCondition { e: NormalCrafterBuild? -> e!!.power.status >= 0.99f }
            consume!!.item(SglItems.concentration_plutonium_239, 1)
            consume!!.power(80f)
            consume!!.liquid(Liquids.cryofluid, 0.6f)
            consume!!.time(150f)
            newProduce()
            produce!!.power(425f)

            draw = DrawMulti(
                DrawBottom(),
                object : DrawExpandPlasma() {
                    init {
                        plasmas = 2
                    }
                },
                DrawDefault()
            )
        }

        核反应堆 = NuclearReactor("nuclear_reactor").apply {
            bundle {
                desc(zh_CN, "核反应堆", "标准的核裂变反应堆,使用压缩核燃料以高效率产出核能,燃料越紧凑效率越高。需要冷却,反应堆温度超过限制温度时会造成堆芯熔毁,引发剧烈的[accent]爆炸[]")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 200,
                    SglItems.crystal_FEX, 160,
                    SglItems.aerogel, 180,
                    SglItems.uranium_238, 200,
                    Items.lead, 180,
                    Items.phaseFabric, 140
                )
            )
            size = 4
            itemCapacity = 35
            liquidCapacity = 25f
            energyCapacity = 4096f

            hasLiquids = true

            ambientSoundVolume = 0.4f

            newReact(SglItems.concentration_uranium_235, 450f, 8f, true)
            newReact(SglItems.concentration_plutonium_239, 420f, 9.5f, true)

            addCoolant(0.25f)
            consume!!.liquid(Liquids.cryofluid, 0.2f)

            addTransfer(ItemStack(SglItems.plutonium_239, 1))
            consume!!.time(180f)
            consume!!.item(SglItems.uranium_238, 1)

            addTransfer(ItemStack(SglItems.hydrogen_fusion_fuel, 1))
            consume!!.time(210f)
            consume!!.item(SglItems.encapsulated_hydrogen_cell, 1)

            addTransfer(ItemStack(SglItems.helium_fusion_fuel, 1))
            consume!!.time(240f)
            consume!!.item(SglItems.encapsulated_helium_cell, 1)

            draw = DrawMulti(
                DrawDefault(),
                object : DrawLiquidRegion(Liquids.cryofluid) {
                    init {
                        suffix = "_top"
                    }
                },
                DrawReactorHeat()
            )
        }


        晶格反应堆 = NuclearReactor("lattice_reactor").apply {
            bundle {
                desc(zh_CN, "晶格反应堆", "特制的缓速反应堆,不使用压缩燃料,直接对燃料晶格结构排列化进行可控裂变,产能较低,但利用率极高。\\n需要冷却,反应堆温度超过限制温度时会造成堆芯熔毁,引发小范围[accent]爆炸[]")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 120,
                    SglItems.crystal_FEX, 90,
                    SglItems.crystal_FEX_power, 70,
                    SglItems.uranium_238, 100,
                    Items.phaseFabric, 60,
                    Items.surgeAlloy, 80
                )
            )
            size = 3
            itemCapacity = 25
            liquidCapacity = 20f
            energyCapacity = 1024f

            hasLiquids = true

            explosionDamageBase = 260
            explosionRadius = 12

            productHeat = 0.1f

            newReact(SglItems.uranium_235, 1200f, 6f, false)
            newReact(SglItems.plutonium_239, 1020f, 7f, false)
            newReact(Items.thorium, 900f, 4.5f, false)

            addCoolant(0.25f)
            consume!!.liquid(Liquids.cryofluid, 0.2f)

            addTransfer(ItemStack(SglItems.plutonium_239, 1))
            consume!!.time(420f)
            consume!!.item(SglItems.uranium_238, 1)

            addTransfer(ItemStack(SglItems.hydrogen_fusion_fuel, 1))
            consume!!.time(480f)
            consume!!.item(SglItems.encapsulated_hydrogen_cell, 1)

            addTransfer(ItemStack(SglItems.helium_fusion_fuel, 1))
            consume!!.time(540f)
            consume!!.item(SglItems.encapsulated_helium_cell, 1)

            draw = DrawMulti(
                DrawDefault(),
                object : DrawLiquidRegion(Liquids.cryofluid) {
                    init {
                        suffix = "_top"
                    }
                },
                DrawReactorHeat()
            )
        }


        超限裂变反应堆 = NuclearReactor("overrun_reactor").apply {
            bundle {
                desc(zh_CN, "超核临界反应堆", "先进的特大型反应堆,内部力场进一步压缩燃料使反应更加剧烈,具有极高的产能效率,且不会产生核废料\\n需要特殊的冷却手段控制堆温,反应堆温度超过限制温度时会造成堆芯熔毁,引发大范围毁灭性[red]核爆[]")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    SglItems.strengthening_alloy, 400,
                    SglItems.crystal_FEX, 260,
                    SglItems.crystal_FEX_power, 280,
                    SglItems.degenerate_neutron_polymer, 100,
                    SglItems.uranium_238, 320,
                    Items.surgeAlloy, 375,
                    Items.phaseFabric, 240
                )
            )
            size = 6
            hasLiquids = true
            itemCapacity = 50
            liquidCapacity = 50f
            energyCapacity = 16384f

            explosionDamageBase = 580
            explosionRadius = 32

            explosionSoundVolume = 5f
            explosionSoundPitch = 0.4f

            productHeat = 0.35f

            warmupSpeed = 0.0015f
            //  ambientSound = Sounds.pulse
            ambientSoundVolume = 0.6f

            newReact(SglItems.concentration_uranium_235, 240f, 22f, false)
            newReact(SglItems.concentration_plutonium_239, 210f, 25f, false)

            addTransfer(ItemStack(SglItems.hydrogen_fusion_fuel, 1))
            consume!!.time(120f)
            consume!!.item(SglItems.encapsulated_hydrogen_cell, 1)

            addTransfer(ItemStack(SglItems.helium_fusion_fuel, 1))
            consume!!.time(120f)
            consume!!.item(SglItems.encapsulated_helium_cell, 1)

            addCoolant(0.4f)
            consume!!.liquid(SglLiquids.phase_FEX_liquid, 0.4f)

            crafting = Cons { e: NormalCrafterBuild? ->
                if (Mathf.chanceDelta((0.06f * e!!.workEfficiency()).toDouble())) Angles.randVectors(System.nanoTime(), 1, 15f) { x: Float, y: Float ->
                    val iff = Mathf.random(0.4f, max(0.4f, e.workEfficiency()))
                    Tmp.v1.set(x, y).scl(0.5f * iff / 2)
                    SglParticleModels.floatParticle.create(e.x + x, e.y + y, Pal.reactorPurple, Tmp.v1.x, Tmp.v1.y, iff * 6.5f * e.workEfficiency())
                }
            }

            draw = DrawMulti(
                DrawBottom(),
                object : DrawPlasma() {
                    init {
                        suffix = "_plasma_"
                        plasma1 = Pal.reactorPurple
                        plasma2 = Pal.reactorPurple2
                    }
                },
                object : DrawRegionDynamic<NormalCrafterBuild?>("_liquid") {
                    init {
                        alpha = Floatf { e: NormalCrafterBuild? -> e!!.liquids.currentAmount() / e.block.liquidCapacity }
                        color = Func { e: NormalCrafterBuild? -> Tmp.c1.set(SglLiquids.phase_FEX_liquid.color).lerp(Color.white, 0.3f) }
                    }

                    override fun draw(build: Building?) {
                        SglDraw.drawBloomUnderBlock<Building?>(build) { e: Building? ->
                            super.draw(build)
                        }
                        Draw.z(35f)
                    }
                },
                object : DrawRegion("_rotator_0") {
                    init {
                        rotateSpeed = 5f
                    }
                },
                object : DrawRegion("_rotator_1") {
                    init {
                        rotateSpeed = -5f
                    }
                },
                DrawDefault(),
                DrawReactorHeat(),
                object : DrawBlock() {
                    override fun draw(build: Building) {
                        val e = build as NuclearReactor.NuclearReactorBuild
                        Draw.z(Layer.effect)
                        Draw.color(Pal.reactorPurple)
                        val shake = Mathf.random(-0.3f, 0.3f) * e.workEfficiency()
                        Tmp.v1.set(19 + shake, 0f).rotate(e.totalProgress() * 2)
                        Tmp.v2.set(0f, 19 + shake).rotate(e.totalProgress() * 2)
                        Fill.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3, 3f, e.totalProgress() * 2)
                        Fill.poly(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 3, 3f, e.totalProgress() * 2 + 90)
                        Fill.poly(e.x - Tmp.v1.x, e.y - Tmp.v1.y, 3, 3f, e.totalProgress() * 2 + 180)
                        Fill.poly(e.x - Tmp.v2.x, e.y - Tmp.v2.y, 3, 3f, e.totalProgress() * 2 + 270)

                        Tmp.v1.set(16f, 0f).rotate(-e.totalProgress() * 2)
                        Tmp.v2.set(0f, 16f).rotate(-e.totalProgress() * 2)
                        Fill.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 3, 3f, -e.totalProgress() * 2 - 180)
                        Fill.poly(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 3, 3f, -e.totalProgress() * 2 - 90)
                        Fill.poly(e.x - Tmp.v1.x, e.y - Tmp.v1.y, 3, 3f, -e.totalProgress() * 2)
                        Fill.poly(e.x - Tmp.v2.x, e.y - Tmp.v2.y, 3, 3f, -e.totalProgress() * 2 + 90)

                        Lines.stroke(1.8f * e.workEfficiency())
                        Lines.circle(e.x, e.y, 18 + shake)
                    }
                }
            )
        }


        托卡马克点火装置 = TokamakCore("tokamak_firer").apply {
            bundle {
                desc(zh_CN, "托卡马克点火装置", "托卡马克核聚变装置的核心组件,是添加材料与输出能量的端口,在一个核聚变装置中必须有且只有一个此设备。将此设备使用聚变约束导轨链接成一个闭环(这个闭环有且只能有4个拐角)构成完整的托卡马克聚变反应堆,而此反应堆的功率取决于整个结构的规模大小")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    Items.phaseFabric, 160,
                    Items.silicon, 200,
                    Items.surgeAlloy, 160,
                    Items.phaseFabric, 220,
                    SglItems.strengthening_alloy, 180,
                    SglItems.aerogel, 240,
                    SglItems.crystal_FEX, 160,
                    SglItems.crystal_FEX_power, 120,
                    SglItems.iridium, 100
                )
            )
            size = 5

            itemCapacity = 60
            liquidCapacity = 65f
            energyCapacity = 65536f

            warmupSpeed = 0.0005f
            stopSpeed = 0.001f

            conductivePower = true

            draw = DrawMulti(
                DrawBottom(),
                object : DrawPlasma() {
                    init {
                        suffix = "_plasma_"
                        plasma1 = SglDrawConst.matrixNet
                        plasma2 = Pal.reactorPurple
                    }
                },
                object : DrawDefault() {
                    override fun draw(build: Building?) {
                        Draw.z(Layer.blockOver)
                        super.draw(build)
                    }
                }
            )

            setFuel(28f)
            consume!!.time(60f)
            consume!!.item(SglItems.hydrogen_fusion_fuel, 1)
            consume!!.liquid(SglLiquids.phase_FEX_liquid, 0.1f)
            consume!!.power(32f)

            setFuel(30f)
            consume!!.time(60f)
            consume!!.item(SglItems.helium_fusion_fuel, 1)
            consume!!.liquid(SglLiquids.phase_FEX_liquid, 0.1f)
            consume!!.power(32f)
        }


        超导约束轨道 = TokamakOrbit("magnetic_confinement_orbit").apply {
            bundle {
                desc(zh_CN, "超导电磁约束导轨", "通过电磁场约束等离子体流的聚变约束导轨,需要消耗大量电力驱动")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    Items.phaseFabric, 60,
                    Items.surgeAlloy, 80,
                    Items.silicon, 100,
                    SglItems.strengthening_alloy, 120,
                    SglItems.crystal_FEX, 80,
                    SglItems.aerogel, 100,
                    SglItems.iridium, 60
                )
            )
            size = 3

            conductivePower = true

            newConsume()
            consume!!.power(3f)

            itemCapacity = 20
            liquidCapacity = 20f

            flueMulti = 1f
            efficiencyPow = 1.5f
        }


        潮汐约束轨道 = TokamakOrbit("tidal_confinement_orbit").apply {
            bundle {
                desc(zh_CN, "潮汐约束导轨", "利用引力场强制约束等离子流的聚变导轨,体积巨大,但具有非常高的功率倍数")
            }
            requirements(
                SglCategory.nuclear, ItemStack.with(
                    Items.phaseFabric, 100,
                    Items.surgeAlloy, 120,
                    SglItems.degenerate_neutron_polymer, 60,
                    SglItems.strengthening_alloy, 140,
                    SglItems.crystal_FEX, 100,
                    SglItems.crystal_FEX_power, 80,
                    SglItems.aerogel, 160,
                    SglItems.iridium, 120
                )
            )
            size = 5

            itemCapacity = 40
            liquidCapacity = 45f

            flueMulti = 2f
            efficiencyPow = 2f
        }


        核能源 = EnergySource("nuclear_energy_source").apply {
            bundle {
                desc(zh_CN, "核能源", "释放中型能量")
            }
            requirements(SglCategory.nuclear, BuildVisibility.sandboxOnly, ItemStack.empty)
        }
        核能黑洞 = EnergyVoid("nuclear_energy_void").apply {
            bundle {
                desc(zh_CN, "核能黑洞", "吸收中型能量")
            }
            requirements(SglCategory.nuclear, BuildVisibility.sandboxOnly, ItemStack.empty)
        }
    }
}