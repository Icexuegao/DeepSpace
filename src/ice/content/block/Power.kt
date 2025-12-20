package ice.content.block

import arc.Core
import arc.graphics.Blending
import arc.graphics.Color
import arc.math.Interp
import ice.Ice
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.util.toColor
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.power.PowerNode
import ice.world.draw.DrawAnyLiquidTile
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.blocks.power.*
import mindustry.world.consumers.ConsumeItemFlammable
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BlockGroup

@Suppress("unused")
object Power : Load {
    val 能量节点 = BeamNode("powerNode").apply {
        laser = Core.atlas.find(this.name+"-beam")
        laserEnd = Core.atlas.find(this.name+"-beam-end")
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
    val 神经索节点 = PowerNode("neuralNode").apply {
        healAmount = 5f
        size = 1
        armor = 4f
        maxNodes = 12
        laserRange = 12f
        hasPower = true
        consumesPower = true
        consumePowerBuffered(16000f)
        laserColor1 = Color.valueOf("E78F92")
        laserColor2 = Color.valueOf("D75B6E")
        requirements(Category.power, IItems.铱板, 5, IItems.导能回路, 2, IItems.生物钢, 1)
        bundle {
            desc(zh_CN, "神经索节点", "向连接的建筑传输电力,生物和机械融合的第一步")
        }
    }
    val 神经束节点 = PowerNode("neuralBeamNode").apply {
        healAmount = 20f
        size = 2
        armor = 8f
        maxNodes = 24
        laserRange = 24f
        hasPower = true
        consumesPower = true
        consumePowerBuffered(80000f)
        laserColor1 = Color.valueOf("E78F92")
        laserColor2 = Color.valueOf("D75B6E")
        requirements(Category.power, IItems.铱板, 10, IItems.导能回路, 5, IItems.生物钢, 1)
        bundle {
            desc(zh_CN, "神经束节点", "具有更大范围的高级电力节点")
        }
    }
    val 远程能量节点 = PowerNode("remotePowerNode").apply {
        size = 3
        armor = 3f
        maxNodes = 4
        laserRange = 100f
        category = Category.power
        hasPower = true
        consumesPower = true
        consumePowerBuffered(50000f)
        requirements(Category.power, IItems.铅锭, 15, IItems.铱板, 15, IItems.导能回路, 10, IItems.暮光合金, 5)
        bundle {
            desc(zh_CN, "远程能量节点", "具有超大范围的高级电力节点")
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
        ambientSound = Sounds.shootMerui
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
        ambientSound = Sounds.loopHum
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
    val 太阳能板 = SolarGenerator("solarPanel").apply {
        size = 2
        powerProduction = 9f
        bundle {
            desc(zh_CN, "太阳能板")
        }
        requirements(Category.power, IItems.铈锭, 15, IItems.导能回路, 25, IItems.单晶硅, 30)
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
        drawer = DrawMulti(DrawRegion("-bottom"), DrawAnyLiquidTile(), DrawDefault(), DrawGlowRegion())
        bundle {
            desc(zh_CN, "地热发电机", "")
        }
    }
    val 热核裂变反应堆 = NuclearReactor("heatNuclearReactor").apply {
        fuelItem = IItems.钍锭
        health = 1200
        size = 3
        armor = 4f
        coolantPower = 1f
        heating = 0.00166f
        itemCapacity = 20
        liquidCapacity = 36f
        itemDuration = 600f
        powerProduction = 42f
        explosionRadius = 21
        explosionDamage = 7200
        consumeItems(IItems.钍锭, 1)
        consumeLiquids(ILiquids.急冻液, 0.036f)
        requirements(Category.power, IItems.导能回路, 50, IItems.铬锭, 380, IItems.铱板, 325, IItems.石英玻璃, 75, IItems.铅锭, 300)
        ambientSound = Sounds.loopHum
        ambientSoundVolume = 0.2f
        bundle {
            desc(zh_CN, "热核裂变反应堆")
        }
    }
    val 血肉反应堆 = ImpactReactor("bloodImpactReactor").apply {
        size = 5
        armor = 8f
        lightColor = "EBFFFE".toColor()
        itemDuration = 120f
        warmupSpeed = 0.0006f
        powerProduction = 800f
        itemCapacity = 20
        liquidCapacity = 60f
        canOverdrive = false
        consumeItems(IItems.生物钢, 1)
        consumePower(100f)
        consumeLiquids(ILiquids.急冻液, 1.5f)
        explosionShake = 8f
        explosionRadius = 40
        explosionDamage = 4800
        explodeSound = Sounds.explosion
        requirements(Category.power, IItems.导能回路, 475, IItems.石英玻璃, 325, IItems.生物钢, 255, IItems.陶钢, 375, IItems.铱板, 645, IItems.铪锭, 125, IItems.铬锭, 815)
        drawer = DrawMulti(DrawRegion("-bottom"), DrawSoftParticles().apply {
            particles = 27
            particleLife = 120f
            particleSize = 9f
            particleRad = 12f
            color = "FFDCD8".toColor()
            color2 = "FF5845".toColor()
            alpha = 0.35f
        }, DrawRegion("-mid"), DrawPlasma().apply {
            plasma1 = "FF5845".toColor()
            plasma2 = "FFDCD8".toColor()
        }, DrawGlowRegion(), DrawDefault(), DrawGlowRegion("-glow"))
        ambientSound = Sounds.loopPulse
        ambientSoundVolume = 0.08f
        bundle {
            desc(zh_CN, "血肉反应堆")
        }
    }
    val 终归反应堆 = ImpactReactor("endImpactReactor").apply {
        size = 6
        armor = 12f
        lightColor = "EBFFFE".toColor()
        itemDuration = 72f
        warmupSpeed = 0.0004f
        powerProduction = 10550f
        itemCapacity = 32
        liquidCapacity = 72f
        squareSprite = false
        canOverdrive = false
        consumeItems(IItems.以太能, 8)
        consumePower(55f)
        consumeLiquids(ILiquids.急冻液, 1f)
        explosionShake = 8f
        explosionRadius = 84
        explosionDamage = 12000
        explodeSound = Sounds.shootCollaris
        bundle {
            desc(zh_CN, "终归反应堆")
        }
        destroyEffect = MultiEffect(ParticleEffect().apply {
            particles = 1
            sizeFrom = 80f
            sizeTo = 9f
            length = 0f
            baseLength = 0f
            lifetime = 18f
            colorFrom = "F3E979".toColor()
            colorTo = "FFFFFF00".toColor()
            cone = 360f
        }, ParticleEffect().apply {
            particles = 20
            sizeFrom = 10f
            sizeTo = 0f
            length = 35f
            baseLength = 133f
            lifetime = 35f
            colorFrom = "F3E979".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 360f
        }, WaveEffect().apply {
            lifetime = 30f
            sizeTo = 160f
            strokeFrom = 11f
            colorFrom = "F3E979".toColor()
            colorTo = "FFFFFF".toColor()
        }, WaveEffect().apply {
            lifetime = 10f
            sizeTo = 78f
            strokeFrom = 8f
            colorFrom = "F3E979".toColor()
            colorTo = "FFFFFF".toColor()
        }, ParticleEffect().apply {
            particles = 13
            line = true
            lifetime = 22f
            strokeFrom = 6f
            lenFrom = 200f
            lenTo = 0f
            length = 1f
            baseLength = 3f
            colorFrom = "FFFFFF".toColor()
            colorTo = "F3E979".toColor()
            cone = 360f
        })

        destroyBullet = BasicBulletType().apply {
            damage = 0f
            splashDamage = 1250f
            splashDamageRadius = 120f
            lifetime = 600f
            speed = 0f
            height = 60f
            width = 60f
            spin = 2f
            shrinkY = 0f
            hittable = false
            collides = false
            absorbable = false
            reflectable = false
            frontColor = "FF8663".toColor()
            backColor = "FF5845".toColor()
            lightRadius = 240f
            despawnShake = 12f
            bulletInterval = 4f
            intervalBullets = 4
            intervalRandomSpread = 360f

            intervalBullet = BulletType().apply {
                damage = 0f
                lifetime = 0f
                speed = 0f
                hittable = false
                collides = false
                absorbable = false
                reflectable = false
                hitEffect = Fx.none
                despawnEffect = Fx.none
                fragBullets = 1
                fragLifeMin = 1f
                fragLifeMax = 2f
                fragVelocityMin = 1f
                fragVelocityMax = 2f

                fragBullet = BasicBulletType(sprite = "${Ice.name}-star").apply {
                    damage = 225f
                    lifetime = 60f
                    speed = 8f
                    height = 24f
                    width = 24f
                    spin = 2f
                    shrinkY = 0f
                    hittable = false
                    absorbable = false
                    reflectable = false
                    pierce = true
                    pierceBuilding = true
                    status = IStatus.蚀骨
                    statusDuration = 120f
                    splashDamage = 275f
                    splashDamageRadius = 40f
                    homingPower = 0.08f
                    homingRange = 120f
                    trailLength = 16
                    trailWidth = 2f
                    trailSinScl = 0.7853982f
                    trailSinMag = 1.5707964f
                    weaveMag = 5f
                    weaveScale = 5f
                    trailColor = "FF5845".toColor()
                    trailEffect = Fx.none
                    lightRadius = 40f
                    impact = true
                    knockback = -48f
                    hitShake = 2f
                    despawnShake = 4f

                    hitEffect = ParticleEffect().apply {
                        line = true
                        particles = 7
                        lifetime = 15f
                        length = 45f
                        cone = -360f
                        strokeFrom = 3f
                        strokeTo = 0f
                        lenFrom = 7f
                        lenTo = 0f
                        colorFrom = "FF5845".toColor()
                        colorTo = "FF8663".toColor()
                    }


                    despawnEffect = MultiEffect(ParticleEffect().apply {
                        particles = 1
                        sizeFrom = 16f
                        sizeTo = 0f
                        length = 0f
                        spin = 3f
                        interp = Interp.swing
                        lifetime = 80f
                        region = "${Ice.name}-star"
                        lightColor = "FF5845".toColor()
                        colorFrom = "FF5845".toColor()
                        colorTo = "FF8663".toColor()
                    }, ParticleEffect().apply {
                        line = true
                        particles = 7
                        lifetime = 15f
                        length = 45f
                        cone = -360f
                        strokeFrom = 3f
                        strokeTo = 0f
                        lenFrom = 7f
                        lenTo = 0f
                        colorFrom = "FF5845".toColor()
                        colorTo = "FF8663".toColor()
                    })


                    frontColor = "FF8663".toColor()
                    backColor = "FF5845".toColor()
                }
            }

            fragBullets = 60
            fragVelocityMin = 1.2f

            fragBullet = BulletType().apply {
                damage = 325f
                shrinkY = 0f
                speed = 4f
                lifetime = 75f
                drag = -0.01f

                shootEffect = ParticleEffect().apply {
                    particles = 10
                    length = 40f
                    lifetime = 25f
                    interp = Interp.circleOut
                    cone = 20f
                    offset = 20f
                    colorFrom = "FF8663".toColor()
                    colorTo = "FF5845".toColor()
                    sizeFrom = 4f
                    sizeTo = 0f
                }

                pierce = true
                pierceCap = 4
                hittable = false
                absorbable = false
                reflectable = false
                homingDelay = 45f
                homingRange = 80f
                homingPower = 0.12f
                frontColor = "FF5845".toColor()
                backColor = "FF8663".toColor()
                weaveMag = 1f
                weaveScale = 5f
                trailChance = 1f
                trailWidth = 2f
                trailLength = 25
                trailColor = "FF5845".toColor()

                trailEffect = ParticleEffect().apply {
                    particles = 6
                    length = 3f
                    baseLength = 3f
                    lifetime = 25f
                    interp = Interp.circleOut
                    cone = 360f
                    offset = 3f
                    colorFrom = "FF8663".toColor()
                    colorTo = "FF5845".toColor()
                    sizeFrom = 3f
                    sizeTo = 0f
                }

                splashDamage = 135f
                splashDamageRadius = 40f
                status = IStatus.熔融
                statusDuration = 60f
                hitSound = Sounds.explosion
                hitEffect = Fx.none

                despawnEffect = ParticleEffect().apply {
                    particles = 15
                    length = 40f
                    lifetime = 36f
                    interp = Interp.circleOut
                    cone = 360f
                    colorFrom = "FF8663".toColor()
                    colorTo = "FF5845".toColor()
                    sizeFrom = 5f
                    sizeTo = 0f
                }
            }

            despawnEffect = MultiEffect(WaveEffect().apply {
                lifetime = 20f
                sizeFrom = 0f
                sizeTo = 175f
                strokeFrom = 8f
                strokeTo = 0f
                lightColor = "FF5845".toColor()
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            }, WaveEffect().apply {
                lifetime = 20f
                sizeFrom = 0f
                sizeTo = 125f
                strokeFrom = 8f
                strokeTo = 0f
                lightColor = "FF5845".toColor()
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            }, ParticleEffect().apply {
                line = true
                particles = 11
                lifetime = 40f
                length = 85f
                baseLength = 20f
                cone = -360f
                strokeFrom = 6f
                strokeTo = 0f
                lenFrom = 7f
                lenTo = 0f
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            })
        }
        requirements(Category.power, IItems.铬锭, 4500, IItems.石英玻璃, 1150, IItems.铱板, 3300, IItems.生物钢, 1200, IItems.陶钢, 1800, IItems.导能回路, 2400)
        drawer = DrawMulti(DrawRegion("-bottom"), DrawArcSmelt().apply {
            alpha = 0.6f
            particles = 240
            particleLife = 90f
            particleLen = 3f
            particleRad = 22f
            particleStroke = 1.1f
            drawCenter = false
            blending = Blending.additive
        }, DrawRegion("-mid"), DrawSoftParticles().apply {
            particles = 27
            particleLife = 120f
            particleSize = 9f
            particleRad = 12f
            color = "FF8663".toColor()
            color2 = "FF5845".toColor()
            alpha = 0.35f
        }, DrawPlasma().apply {
            plasma1 = "FF5845".toColor()
            plasma2 = "FF8663".toColor()
        }, DrawDefault(), DrawParticles().apply {
            color = "FEB380".toColor()
            alpha = 0.6f
            particles = 60
            particleLife = 60f
            particleRad = 80f
            particleSize = 2f
            fadeMargin = 0.5f
            rotateScl = 3.6f
            blending = Blending.additive
            particleSizeInterp = Interp.linear
        }, DrawGlowRegion("-glow"))
        ambientSound = Sounds.loopPulse
        ambientSoundVolume = 0.12f
    }
}