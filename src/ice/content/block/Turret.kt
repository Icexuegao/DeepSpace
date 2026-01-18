package ice.content.block

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import ice.audio.ISounds
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.RailBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.library.util.toColor
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.UnitSorts
import mindustry.entities.UnitSorts.strongest
import mindustry.entities.bullet.MissileBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.*
import mindustry.entities.part.DrawPart.PartParams
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.pattern.*
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.consumers.ConsumeCoolant
import mindustry.world.draw.DrawTurret

@Suppress("unused", "DuplicatedCode")
object Turret : Load {
    fun ItemTurret.addAmmoType(item: Item, bullet: () -> BulletType) {
        ammoTypes.put(item, bullet())
    }

    val 碎冰 = ItemTurret("trashIce").apply {
        bundle {
          desc(zh_CN, "碎冰")
        }
      size = 1
        health = 250
        recoil = 0.5f
        shootY = 3f
        reload = 45f
        range = 160f
        shootCone = 30f
        shoot = ShootSummon().apply {
            x = 0f
            y = 0f
            spread = 5f
            shots = 5
            shotDelay = 3f
        }
        shootSound = ISounds.laser1
        shootEffect = Effect(8.0f) { e: EffectContainer ->
            Draw.color(IceColor.b4, Color.white, e.fin())
            val w = 1.0f + 5.0f * e.fout()
            Drawf.tri(e.x, e.y, w, 15.0f * e.fout(), e.rotation)
            Drawf.tri(e.x, e.y, w, 3.0f * e.fout(), e.rotation + 180.0f)
        }
        ammo(IItems.硫钴矿, BasicBulletType(5f, 9f).apply {
            width = 2f
            height = 9f
            lifetime = 30f
            ammoMultiplier = 2f
            despawnEffect = IceEffects.baseHitEffect
            hitEffect = despawnEffect
            trailColor = IceColor.b4
            backColor = IceColor.b4
            hitColor = IceColor.b4
            frontColor = IceColor.b4
        })
        requirements(Category.turret, ItemStack.with(IItems.铬铁矿, 10, IItems.低碳钢, 20))
        drawer = DrawTurret().apply {
            parts.add(RegionPart("-barrel").apply {
                progress = PartProgress.recoil
                under = true
                heatColor = IceColor.b4
                heatProgress = PartProgress.recoil
                moveY = -1.5f
            })
        }
    }
    val 神矢 = PowerTurret("divineArrow").apply {
        bundle {
          desc(zh_CN, "神矢")
        }
      size = 2
        health = 1000
        requirements(Category.turret, ItemStack.with(IItems.铬铁矿, 10, IItems.低碳钢, 20))
        reload = 30f
        recoils = 2
        drawer = DrawTurret().apply {
            for (i in 0..1) {
                parts.add(object : RegionPart("-" + (if (i == 0) "l" else "r")) {
                    init {
                        progress = PartProgress.recoil
                        recoilIndex = i
                        under = true
                        moveY = -1.5f
                    }
                })
            }
            parts.add(ShapePart().apply {
                hollow = true
                radius = 4f
                layer = 110f
                sides = 4
                y = -4f
                color = IceColor.b4
                rotateSpeed = 2f
                progress = PartProgress.recoil
            })
            parts.add(ShapePart().apply {
                hollow = true
                radius = 0f
                radiusTo = 4f
                layer = 110f
                sides = 4
                stroke = 0.5f
                rotateSpeed = 2f
                y = -4f
                color = IceColor.b4
                progress = PartProgress { p: PartParams ->
                    PartProgress.warmup.get(p) * ((Time.time / 15) % 1)
                }
            })
        }
        shoot = object : ShootAlternate() {
            var scl: Float = 2f
            var mag: Float = 1.5f
            var offset: Float = Mathf.PI * 1.25f
            override fun shoot(totalShots: Int, handler: BulletHandler, barrelIncrementer: Runnable?) {
                for (i in 0..<shots) {
                    for (sign in Mathf.signs) {
                        val index = ((totalShots + i + barrelOffset) % barrels) - (barrels - 1) / 2f
                        handler.shoot(index * spread * -Mathf.sign(mirror), 0f, 0f, firstShotDelay + shotDelay * i) { b ->
                            b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * sign))
                        }
                    }
                    barrelIncrementer?.run()
                }
            }
        }.apply {
            barrelOffset = 8
            spread = 5f
            shots = 2
            shotDelay = 15f
        }
        shootSound = Sounds.shootMissile
        shootY = 6f
        shootEffect = IceEffects.squareAngle(range = 30f, color1 = IceColor.b4, color2 = Color.white)
        shootType = MissileBulletType(6f, 30f).apply {
            splashDamageRadius = 30f
            splashDamage = 30f * 1.5f
            lifetime = 45f
            trailLength = 20
            trailWidth = 1.5f
            trailColor = IceColor.b4
            backColor = IceColor.b4
            hitColor = IceColor.b4
            frontColor = IceColor.b4
            despawnEffect = IceEffects.blastExplosion(IceColor.b4)
            hitEffect = despawnEffect
        }
        range = shootType.speed * shootType.lifetime
    }
    val 绪终 = ItemTurret("thinkEnd").apply {
        bundle {
          desc(zh_CN, "绪终")
        }
      size = 5
        shoot.apply {
            firstShotDelay = 120f
            recoils = 1
            reload = 120f
            shootWarmupSpeed = 0.05f
        }
        ammo(IItems.暮光合金, BasicBulletType(4f, 4f))
        requirements(Category.turret, ItemStack.with(IItems.铜锭, 10, IItems.单晶硅, 5))
        drawer = DrawTurret().apply {
            parts.add(RegionPart("4-l").apply {
                moveY = -4f
                moveX = -8f
                moveRot = 60f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
            parts.add(RegionPart("4-r").apply {
                moveY = -4f
                moveX = 8f
                moveRot = -60f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
            parts.add(RegionPart("1").apply {
                moveY = 2f
                progress = PartProgress.warmup.curve(Interp.pow2)
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
            parts.add(RegionPart("2-l").apply {
                moveY = -2f
                moveRot = 25f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup.curve(Interp.pow5In)
            })
            parts.add(RegionPart("2-r").apply {
                moveY = -2f
                moveRot = -25f
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup.curve(Interp.pow5In)
            })

            parts.add(RegionPart("3").apply {
                heatColor = Color.valueOf("c3baff").a(0.5f)
                heatProgress = PartProgress.warmup
            })
          bundle {
            desc(zh_CN, "绪终")
          }
        }
    }
    val 冲穿 = ItemTurret("breakThrough").apply {
        health = 5600
        size = 5
        range = 720f
        reload = 300f
        shake = 5f
        recoil = 4f
        maxAmmo = 60
        recoilTime = 300f
        ammoPerShot = 15
        cooldownTime = 300f
        shootSound = Sounds.shootToxopidShotgun
        unitSort = strongest
        shootCone = 2f
        rotateSpeed = 1.4f
        minWarmup = 0.96f
        shootWarmupSpeed = 0.08f
        warmupMaintainTime = 300f
        shoot = ShootSpread().apply {
            shots = 5
            spread = 1f
        }
        liquidCapacity = 30f
        consumePower(25f)
        consume(ConsumeCoolant(1.5f))
        coolantMultiplier = 0.333f
      bundle {
        desc(zh_CN, "冲穿", "以临界速度发射五道远程穿透磁轨炮摧毁敌人,比裂颅更强")
      }
      addAmmoType(IItems.钍锭) {
            RailBulletType().apply {
                damage = 720f
                knockback = 10f
                lifetime = 30f
                length = 720f
                ammoMultiplier = 1f
                pierce = true
                pierceDamageFactor = 0.4f
                status = StatusEffects.unmoving
                statusDuration = 900f
                shootEffect = Fx.instShoot
                pointEffect = Fx.railTrail
                hitEffect = Fx.railHit
            }
        }
        addAmmoType(IItems.金锭) {
            RailBulletType().apply {
                damage = 1200f
                knockback = 20f
                lifetime = 30f
                length = 720f
                ammoMultiplier = 1f
                pierce = true
                pierceDamageFactor = 0.2f
                status = StatusEffects.melting
                statusDuration = 900f
                shootEffect = Fx.instShoot
                pointEffect = Fx.railTrail
                hitEffect = Fx.railHit
            }
        }
        addAmmoType(IItems.铱板) {
            RailBulletType().apply {
                damage = 960f
                knockback = 15f
                lifetime = 30f
                length = 720f
                ammoMultiplier = 1f
                pierce = true
                pierceDamageFactor = 0.3f
                status = StatusEffects.burning
                statusDuration = 900f
                shootEffect = Fx.instShoot
                pointEffect = Fx.railTrail
                hitEffect = Fx.railHit
                pierceEffect = ParticleEffect().apply {
                    line = true
                    particles = 60
                    offset = 0f
                    lifetime = 15f
                    length = 40f
                    cone = -7.5f
                    lenFrom = 6f
                    lenTo = 0f
                    colorFrom = "D86E56".toColor()
                    colorTo = "FFFFFF".toColor()
                }
            }
        }
        requirements(Category.turret, IItems.铜锭, 1800, IItems.钴锭, 840, IItems.铱板, 630, IItems.导能回路, 435, IItems.陶钢, 225)
        drawer = DrawTurret().apply {
            parts.addAll(RegionPart("-barrel").apply {
                moveY = -1.5f
                moves.add(DrawPart.PartMove(PartProgress.recoil, 0f, -3f, 0f))
                heatColor = "F03B0E".toColor()
            }, RegionPart("-top").apply {
                heatProgress = PartProgress.warmup
                mirror = true
                under = true
                moveX = 4f
                moveY = 8.25f
                heatColor = "F03B0E".toColor()
            }, RegionPart("-side").apply {
                heatProgress = PartProgress.warmup
                mirror = true
                under = true
                moveX = 4f
                moveY = 0f
                heatColor = "F03B0E".toColor()
            }, HaloPart().apply {
                tri = true
                y = -16f
                shapes = 2
                radius = 0f
                radiusTo = 3f
                triLength = 16f
                haloRadius = 15f
                haloRotation = 90f
                color = "FF5845".toColor()
                layer = 110f
            }, HaloPart().apply {
                tri = true
                y = -16f
                shapeRotation = 180f
                shapes = 2
                radius = 0f
                radiusTo = 3f
                triLength = 4f
                haloRadius = 15f
                haloRotation = 90f
                color = "FF5845".toColor()
                layer = 110f
            }, ShapePart().apply {
                y = -16f
                circle = true
                hollow = true
                radius = 4f
                stroke = 0f
                strokeTo = 1f
                color = "FF5845".toColor()
                layer = 110f
            }, ShapePart().apply {
                y = -16f
                circle = true
                hollow = true
                radius = 8f
                stroke = 0f
                strokeTo = 1f
                color = "FF5845".toColor()
                layer = 110f
            }, HaloPart().apply {
                hollow = false
                mirror = false
                tri = true
                x = 0f
                y = -16f
                shapeRotation = 0f
                moveX = 0f
                moveY = 0f
                shapeMoveRot = 0f
                shapes = 4
                sides = 5
                radius = 0f
                radiusTo = 3f
                stroke = 1f
                strokeTo = -1f
                triLength = 6f
                triLengthTo = -1f
                haloRadius = 12f
                haloRadiusTo = -1f
                haloRotateSpeed = 1f
                haloRotation = 0f
                rotateSpeed = 0f
                color = "FF5845".toColor()
                layer = 110f
            }, HaloPart().apply {
                tri = true
                y = -16f
                shapeRotation = 180f
                shapes = 4
                radius = 0f
                radiusTo = 3f
                triLength = 2f
                haloRadius = 12f
                haloRotateSpeed = 1f
                color = "FF5845".toColor()
                layer = 110f
            }, HaloPart().apply {
                tri = true
                y = -16f
                shapes = 4
                radius = 3f
                triLength = 0f
                triLengthTo = 2f
                haloRadius = 8f
                haloRotateSpeed = -1f
                color = "FF5845".toColor()
                layer = 110f
            })
        }
    }
    val 光谱 = PowerTurret("spectral").apply {
        health = 1380
        size = 3
        recoil = 2f
        shootY = 4f
        range = 256f
        reload = 120f
        shootCone = 3f
        rotateSpeed = 5f
        recoilTime = 210f
        cooldownTime = 210f
        minWarmup = 0.9f
        shootWarmupSpeed = 0.08f
        shoot = ShootSpread().apply {
            shots = 3
            shotDelay = 15f
        }
        shake = 2f
        consumePower(14f)
        consume(ConsumeCoolant(0.3f))
        liquidCapacity = 40f
        coolantMultiplier = 3f
        shootSound = Sounds.shootLaser
        shootType = LaserBulletType(135f).apply {
            length = 256f
            shootEffect = ParticleEffect().apply {
                line = true
                particles = 12
                lifetime = 20f
                length = 45f
                cone = 30f
                lenFrom = 6f
                lenTo = 6f
                strokeFrom = 3f
                interp = Interp.fastSlow
                colorFrom = "FFDCD8".toColor()
                colorTo = "FF5845".toColor()
            }
            colors = arrayOf("D75B6E".toColor(), "E78F92".toColor(), "FFF0F0".toColor())
            ammoMultiplier = 1f
            status = StatusEffects.melting
            statusDuration = 30f
            hitEffect = ParticleEffect().apply {
                line = true
                particles = 10
                lifetime = 20f
                length = 75f
                cone = -360f
                lenFrom = 6f
                lenTo = 6f
                strokeFrom = 3f
                strokeTo = 0f
                lightColor = "FF5845".toColor()
                colorFrom = "FFDCD8".toColor()
                colorTo = "FF5845".toColor()
            }
        }
        drawer = DrawTurret().apply {
            parts.add(RegionPart("-side").apply {
                mirror = true
                moveX = 1f
                children.add(RegionPart("-top").apply {
                    mirror = true
                    moveX = 0.25f
                    moveY = 1.75f
                })
            })
        }
        requirements(Category.turret, IItems.铜锭, 120, IItems.铬锭, 140, IItems.钍锭, 60, IItems.单晶硅, 120)
      bundle {
        desc(zh_CN, "光谱", "中型能量炮塔,可以快速向敌人发射高热激光")
      }
    }
    val 撕裂 = PowerTurret("tear").apply {
        health = 19200
        size = 8
        range = 768f
        reload = 60f
        cooldownTime = 45f
        shake = 4f
        shootY = 0f
        recoil = 8f
        recoilTime = 45f
        shootCone = 5f
        rotateSpeed = 0.8f
        minWarmup = 0.97f
        shootWarmupSpeed = 0.08f
        warmupMaintainTime = 300f
        consumePower(272f)
        consumeItems(IItems.肃正协议, 4)
        consumeLiquids(ILiquids.急冻液, 4f)
        itemCapacity = 4
        liquidCapacity = 120f
        canOverdrive = false
        shoot = ShootMulti(ShootHelix().apply {
            scl = 3f
            mag = 0.75f
        }, ShootHelix().apply {
            mag = 3f
            scl = 0.75f
        })
        shootSound = ISounds.聚爆
        drawer = DrawTurret().apply {
            parts.addAll(RegionPart("-side").apply {
                heatProgress = PartProgress.warmup
                under = true
                mirror = true
                moveX = 1f
                moveY = -1f
                heatColor = "F03B0E".toColor()
            }, RegionPart("-part").apply {
                heatProgress = PartProgress.warmup
                drawRegion = false
                heatColor = "F03B0E".toColor()
            })
        }
      bundle {
        desc(zh_CN, "撕裂", "一座强大的电磁轨道炮,超长轨道,超大力度,可以快速地进行精准射击")
      }
      requirements(Category.turret, IItems.铜锭, 9600, IItems.铬锭, 6400, IItems.铱板, 3600, IItems.导能回路, 2400, IItems.陶钢, 1920, IItems.生物钢, 1200)
        shootType = BasicBulletType(16f,840f,"gauss-bullet").apply {
            lifetime = 48f
            shrinkY = 0f
            height = 32f
            width = 26f
            ammoMultiplier = 1f
            frontColor = "FF8663".toColor()
            backColor = "FF5845".toColor()
            hittable = false
            pierceCap = 2
            status = StatusEffects.melting
            statusDuration = 180f
            splashDamage = 240f
            splashDamageRadius = 80f
            buildingDamageMultiplier = 0.5f
            trailColor = "FF5845".toColor()
            trailLength = 24
            trailWidth = 3f
            trailSinScl = 0.75f
            trailSinMag = 1.5f
            hitShake = 3f
            despawnShake = 4f
            knockback = 5f
            lightning = 4
            lightningLength = 12
            lightningDamage = 255f
            lightningColor = "FF5845".toColor()
            hitEffect = MultiEffect(WaveEffect().apply {
                lifetime = 20f
                sizeFrom = 0f
                sizeTo = 65f
                strokeFrom = 4f
                strokeTo = 0f
                lightColor = "FF5845".toColor()
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            }, ParticleEffect().apply {
                line = true
                particles = 11
                lifetime = 30f
                length = 85f
                baseLength = 20f
                cone = -360f
                lenFrom = 7f
                lenTo = 0f
                interp = Interp.exp10In
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            })

            despawnEffect = MultiEffect(ParticleEffect().apply {
                particles = 1
                sizeFrom = 45f
                sizeTo = 0f
                length = 0f
                interp = Interp.bounceOut
                lifetime = 60f
                region = "star".appendModName()
                lightColor = "FF5845".toColor()
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
                layer = 110f
            }, WaveEffect().apply {
                lifetime = 20f
                sizeFrom = 0f
                sizeTo = 65f
                strokeFrom = 4f
                strokeTo = 0f
                interp = Interp.elasticOut
                lightColor = "FF5845".toColor()
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            }, ParticleEffect().apply {
                line = true
                particles = 11
                lifetime = 30f
                length = 85f
                baseLength = 20f
                cone = -360f
                lenFrom = 7f
                lenTo = 0f
                interp = Interp.exp10In
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            })

            shootEffect = MultiEffect(ParticleEffect().apply {
                particles = 4
                sizeFrom = 6f
                sizeTo = 0f
                length = 70f
                lifetime = 30f
                interp = Interp.sineOut
                sizeInterp = Interp.sineIn
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
                cone = 15f
            }, WaveEffect().apply {
                lifetime = 30f
                sides = 0
                sizeFrom = 0f
                sizeTo = 40f
                strokeFrom = 4f
                strokeTo = 0f
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            })
        }
    }
    val 隧穿 = ItemTurret("tunnelOpening").apply {
        health = 3450
        size = 5
        recoil = 3f
        shootY = 10f
        shake = 3.5f
        range = 264f
        reload = 378f
        shootCone = 15f
        maxAmmo = 40
        recoilTime = 192f
        cooldownTime = 270f
        rotateSpeed = 2f
        ammoPerShot = 10
        liquidCapacity = 40f
        coolantMultiplier = 0.4f
        minWarmup = 0.96f
        shootWarmupSpeed = 0.08f
        warmupMaintainTime = 300f
        shootSound = Sounds.shootScathe
        unitSort = UnitSorts.farthest
        shootEffect = Fx.bigShockwave
        ammoUseEffect = Fx.casing3Double
        consumePower(17f)
        consume(ConsumeCoolant(1.5f))
        drawer = DrawTurret().apply {
            parts.addAll(RegionPart("-shot").apply {
                progress = PartProgress.recoil
                moveY = -4.5f
                children.add(RegionPart("-shot-glow").apply {
                    heatProgress = PartProgress.warmup
                    drawRegion = false
                    heatColor = "F03B0E".toColor()
                })
            }, RegionPart("-glow").apply {
                heatProgress = PartProgress.warmup
                drawRegion = false
                heatColor = "F03B0E".toColor()
            }, HaloPart().apply {
                tri = true
                y = -20f
                radius = 27f
                triLength = 0f
                triLengthTo = 3f
                haloRadius = 10f
                haloRotateSpeed = 0.5f
                color = "FF5845".toColor()
                layer = 110f
            }, HaloPart().apply {
                tri = true
                y = -20f
                radius = 3f
                triLength = 0f
                triLengthTo = 12f
                haloRadius = 11.5f
                shapeRotation = 150f
                haloRotateSpeed = 0.5f
                haloRotation = 30f
                color = "FF5845".toColor()
                layer = 110f
            }, HaloPart().apply {
                tri = true
                y = -20f
                radius = 3f
                triLength = 0f
                triLengthTo = 18f
                haloRadius = 12f
                shapeRotation = -15f
                haloRotateSpeed = 0.5f
                haloRotation = -135f
                color = "FF5845".toColor()
                layer = 110f
            })
        }
        requirements(Category.turret, IItems.铜锭, 1120, IItems.钴锭, 470, IItems.钍锭, 390, IItems.铬锭, 280, IItems.铱板, 225, IItems.爆炸化合物, 65)
      bundle {
        desc(zh_CN, "隧穿", "向指定方位发射三道强劲的定向爆破束,并在到达极限距离后原路返回")
      }
      shoot = ShootSpread().apply {
            shots = 3
            spread = 15f
        }
        addAmmoType(IItems.铬锭) {
            BasicBulletType().apply {
                damage = 135f
                lifetime = 120f
                speed = 30f
                drag = 0.1f
                width = 14f
                height = 25f
                pierce = true
                collides = false
                pierceBuilding = true
                bulletInterval = 1f

                intervalBullet = BulletType().apply {
                    damage = 0f
                    hitShake = 2f
                    despawnShake = 1f
                    splashDamage = 45f
                    splashDamageRadius = 20f
                    scaledSplashDamage = true
                    instantDisappear = true
                    hitEffect = Fx.flakExplosion
                    despawnEffect = Fx.flakExplosion
                }

                fragBullets = 1
                fragAngle = 180f
                fragVelocityMin = 1f
                fragRandomSpread = 0f

                fragBullet = BasicBulletType().apply {
                    damage = 135f
                    lifetime = 120f
                    speed = 0.1f
                    drag = -0.041f
                    width = 14f
                    height = 25f
                    pierce = true
                    collides = false
                    pierceBuilding = true
                    bulletInterval = 1f

                    intervalBullet = BulletType().apply {
                        damage = 0f
                        hitShake = 2f
                        despawnShake = 1f
                        splashDamage = 45f
                        splashDamageRadius = 20f
                        scaledSplashDamage = true
                        instantDisappear = true
                        hitEffect = Fx.flakExplosion
                        despawnEffect = Fx.flakExplosion
                    }

                    despawnSound = Sounds.shootPulsar

                    despawnEffect = ParticleEffect().apply {
                        particles = 15
                        line = true
                        strokeFrom = 3f
                        strokeTo = 0f
                        lenFrom = 10f
                        lenTo = 0f
                        length = 50f
                        lifetime = 10f
                        colorFrom = "FFE176".toColor()
                        colorTo = "FFFFFF".toColor()
                        cone = 60f
                    }
                }
            }
        }
        addAmmoType(IItems.钴锭) {
            BasicBulletType().apply {
                damage = 185f
                lifetime = 120f
                speed = 30f
                drag = 0.1f
                width = 14f
                height = 25f
                ammoMultiplier = 1f
                reloadMultiplier = 1.4f
                pierce = true
                collides = false
                pierceBuilding = true
                bulletInterval = 1f

                intervalBullet = BulletType().apply {
                    damage = 0f
                    hitShake = 2f
                    despawnShake = 1f
                    splashDamage = 62f
                    splashDamageRadius = 20f
                    scaledSplashDamage = true
                    instantDisappear = true
                    hitEffect = Fx.flakExplosion
                    despawnEffect = Fx.flakExplosion
                }

                fragBullets = 1
                fragAngle = 180f
                fragVelocityMin = 1f
                fragRandomSpread = 0f

                fragBullet = BasicBulletType().apply {
                    damage = 185f
                    lifetime = 120f
                    speed = 0.1f
                    drag = -0.041f
                    width = 14f
                    height = 25f
                    pierce = true
                    collides = false
                    pierceBuilding = true
                    bulletInterval = 1f

                    intervalBullet = BulletType().apply {
                        damage = 0f
                        hitShake = 2f
                        despawnShake = 1f
                        splashDamage = 62f
                        splashDamageRadius = 20f
                        scaledSplashDamage = true
                        instantDisappear = true
                        hitEffect = Fx.flakExplosion
                        despawnEffect = Fx.flakExplosion
                    }

                    despawnSound = Sounds.loopPulse

                    despawnEffect = ParticleEffect().apply {
                        particles = 15
                        line = true
                        strokeFrom = 3f
                        strokeTo = 0f
                        lenFrom = 10f
                        lenTo = 0f
                        length = 50f
                        lifetime = 10f
                        colorFrom = "FFE176".toColor()
                        colorTo = "FFFFFF".toColor()
                        cone = 60f
                    }
                }
            }
        }
        addAmmoType(IItems.钍锭) {
            BasicBulletType().apply {
                damage = 235f
                lifetime = 120f
                speed = 30f
                drag = 0.085f
                width = 16f
                height = 25f
                pierce = true
                collides = false
                pierceBuilding = true
                rangeChange = 56f
                bulletInterval = 1f

                intervalBullet = BulletType().apply {
                    damage = 0f
                    hitShake = 2f
                    despawnShake = 1f
                    status = IStatus.衰变
                    splashDamage = 78f
                    splashDamageRadius = 20f
                    scaledSplashDamage = true
                    instantDisappear = true
                    hitEffect = Fx.flakExplosion
                    despawnEffect = Fx.flakExplosion
                }

                fragBullets = 1
                fragAngle = 180f
                fragVelocityMin = 1f
                fragRandomSpread = 0f

                fragBullet = BasicBulletType().apply {
                    damage = 235f
                    lifetime = 120f
                    speed = 0.1f
                    drag = -0.043f
                    width = 16f
                    height = 25f
                    pierce = true
                    collides = false
                    pierceBuilding = true
                    bulletInterval = 1f

                    intervalBullet = BulletType().apply {
                        damage = 0f
                        hitShake = 2f
                        despawnShake = 1f
                        status = IStatus.衰变
                        splashDamage = 78f
                        splashDamageRadius = 20f
                        scaledSplashDamage = true
                        instantDisappear = true
                        hitEffect = Fx.flakExplosion
                        despawnEffect = Fx.flakExplosion
                    }

                    despawnSound = Sounds.shootPulsar

                    despawnEffect = ParticleEffect().apply {
                        particles = 15
                        line = true
                        strokeFrom = 4f
                        strokeTo = 0f
                        lenFrom = 10f
                        lenTo = 0f
                        length = 70f
                        baseLength = 0f
                        lifetime = 10f
                        colorFrom = "FFE176".toColor()
                        colorTo = "FFFFFF".toColor()
                        cone = 60f
                    }
                }
            }
        }
        addAmmoType(IItems.金锭) {
            BasicBulletType().apply {
                damage = 480f
                lifetime = 120f
                speed = 30f
                drag = 0.067f
                width = 10f
                height = 25f
                pierce = true
                collides = false
                pierceBuilding = true
                rangeChange = 144f
                ammoMultiplier = 1f
                bulletInterval = 1f

                intervalBullet = BulletType().apply {
                    damage = 0f
                    status = StatusEffects.shocked
                    splashDamage = 160f
                    splashDamageRadius = 20f
                    scaledSplashDamage = true
                    lightning = 3
                    lightningLength = 4
                    lightningDamage = 16f
                    instantDisappear = true
                    hitEffect = Fx.flakExplosion
                    despawnEffect = Fx.flakExplosion
                }

                fragBullets = 1
                fragAngle = 180f
                fragVelocityMin = 1f
                fragRandomSpread = 0f

                fragBullet = BasicBulletType().apply {
                    damage = 480f
                    lifetime = 120f
                    speed = 0.1f
                    drag = -0.045f
                    width = 10f
                    height = 25f
                    pierce = true
                    collides = false
                    pierceBuilding = true
                    bulletInterval = 1f

                    intervalBullet = BulletType().apply {
                        damage = 0f
                        status = StatusEffects.shocked
                        splashDamage = 160f
                        splashDamageRadius = 20f
                        scaledSplashDamage = true
                        lightning = 3
                        lightningLength = 4
                        lightningDamage = 16f
                        instantDisappear = true
                        hitEffect = Fx.flakExplosion
                        despawnEffect = Fx.flakExplosion
                    }

                    despawnSound = Sounds.shootPulsar

                    despawnEffect = ParticleEffect().apply {
                        particles = 15
                        line = true
                        strokeFrom = 5f
                        strokeTo = 0f
                        lenFrom = 16f
                        lenTo = 0f
                        length = 100f
                        lifetime = 10f
                        colorFrom = "F3E979".toColor()
                        colorTo = "FFFFFF".toColor()
                        cone = 60f
                    }
                }
            }
        }
        addAmmoType(IItems.铱板) {
            BasicBulletType().apply {
                damage = 420f
                lifetime = 120f
                speed = 30f
                drag = 0.075f
                width = 10f
                height = 25f
                pierce = true
                collides = false
                pierceBuilding = true
                rangeChange = 96f
                ammoMultiplier = 3f
                bulletInterval = 1f

                intervalBullet = BulletType().apply {
                    damage = 0f
                    hitShake = 2f
                    despawnShake = 1f
                    status = IStatus.衰变
                    statusDuration = 60f
                    splashDamage = 140f
                    splashDamageRadius = 20f
                    scaledSplashDamage = true
                    instantDisappear = true
                    hitEffect = Fx.flakExplosion
                    despawnEffect = Fx.flakExplosion
                }

                fragBullets = 1
                fragAngle = 180f
                fragVelocityMin = 1f
                fragRandomSpread = 0f

                fragBullet = BasicBulletType().apply {
                    damage = 420f
                    lifetime = 120f
                    speed = 0.1f
                    drag = -0.044f
                    width = 10f
                    height = 25f
                    pierce = true
                    collides = false
                    pierceBuilding = true
                    bulletInterval = 1f

                    intervalBullet = BulletType().apply {
                        damage = 0f
                        hitShake = 2f
                        despawnShake = 1f
                        status = IStatus.衰变
                        statusDuration = 60f
                        splashDamage = 140f
                        splashDamageRadius = 20f
                        scaledSplashDamage = true
                        instantDisappear = true
                        hitEffect = Fx.flakExplosion
                        despawnEffect = Fx.flakExplosion
                    }

                    despawnSound = Sounds.shootPulsar

                    despawnEffect = ParticleEffect().apply {
                        particles = 15
                        line = true
                        strokeFrom = 5f
                        strokeTo = 0f
                        lenFrom = 16f
                        lenTo = 0f
                        length = 100f
                        lifetime = 10f
                        colorFrom = "FFE176".toColor()
                        colorTo = "FFFFFF".toColor()
                        cone = 60f
                    }
                }
            }
        }
    }
    val 腥风 = PowerTurret("bloodyWind").apply {
        health = 6400
        size = 6
        armor = 8f
        range = 672f
        reload = 1.5f
        shake = 3f
        recoil = 4f
        recoils = 4
        shootY = 20f
        recoilTime = 6f
        inaccuracy = 3f
        shootCone = 60f
        rotateSpeed = 4f
        targetInterval = 1f
        cooldownTime = 20f
        shootSound = ISounds.速射
        liquidCapacity = 30f
        coolantMultiplier = 0.75f
        consumePower(53f)
        consume(ConsumeCoolant(3f))
        shoot = ShootBarrel().apply {
            barrels = floatArrayOf(-5.5f, 0f, 0f, 5.5f, 0f, 0f, -16f, 0f, 0f, 16f, 0f, 0f)
        }
        requirements(Category.turret, IItems.铬锭, 2200, IItems.石英玻璃, 570, IItems.铱板, 1200, IItems.导能回路, 625, IItems.钴钢, 825)

        drawer = DrawTurret().apply {
            parts.addAll(RegionPart("-l").apply {
                under = true
                recoilIndex = 0
                heatColor = "F03B0E".toColor()
                heatProgress = PartProgress.recoil
                moves.add(DrawPart.PartMove().apply {
                    progress = PartProgress.recoil
                    y = -4f
                })
            }, RegionPart("-r").apply {
                under = true
                recoilIndex = 1
                heatColor = "F03B0E".toColor()
                heatProgress = PartProgress.recoil
                moves.add(DrawPart.PartMove().apply {
                    progress = PartProgress.recoil
                    y = -4f
                })
            }, RegionPart("-ll").apply {
                under = true
                recoilIndex = 2
                heatColor = "F03B0E".toColor()
                heatProgress = PartProgress.recoil
                moves.add(DrawPart.PartMove().apply {
                    progress = PartProgress.recoil
                    y = -4f
                })
            }, RegionPart("-rr").apply {
                under = true
                recoilIndex = 3
                heatColor = "F03B0E".toColor()
                heatProgress = PartProgress.recoil
                moves.add(DrawPart.PartMove().apply {
                    progress = PartProgress.recoil
                    y = -4f
                })
            }, HoverPart().apply {
                color = "FF5845".toColor()
                phase = 120f
                circles = 3
                stroke = 1f
                layer = 100f
            })
        }
      bundle {
        desc(zh_CN, "腥风", "改进型四联速射粒子炮,向敌人发射高热的粒子束\n为了更强的电热转换回路拆除了部分气冷系统,使用液体时冷却效果更佳")
      }
      shootType = BasicBulletType().apply {
            damage = 121f
            lifetime = 33.6f
            speed = 20f
            width = 5f
            height = 75f
            pierce = true
            knockback = 1f
            status = IStatus.熔融
            statusDuration = 120f
            ammoMultiplier = 1f
            trailLength = 2
            trailWidth = 1.2f
            trailColor = "FF5845".toColor()
            frontColor = "FF8663".toColor()
            backColor = "FF5845".toColor()
            hitColor = "FF8663".toColor()

            shootEffect = WaveEffect().apply {
                lifetime = 6f
                sizeFrom = 1f
                sizeTo = 10f
                strokeFrom = 1.3f
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            }

            smokeEffect = Fx.shootBigSmoke

            hitEffect = MultiEffect(WaveEffect().apply {
                lifetime = 10f
                sizeTo = 6f
                strokeFrom = 4f
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            }, ParticleEffect().apply {
                line = true
                particles = 8
                lifetime = 10f
                length = 50f
                strokeFrom = 3f
                lenFrom = 10f
                lenTo = 4f
                cone = 60f
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            })

            despawnEffect = WaveEffect().apply {
                lifetime = 10f
                sizeTo = 6f
                strokeFrom = 4f
                colorFrom = "FF5845".toColor()
                colorTo = "FF8663".toColor()
            }
        }
    }
}







