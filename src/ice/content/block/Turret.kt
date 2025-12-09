package ice.content.block

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import ice.audio.ISounds
import ice.library.world.ContentLoad
import ice.content.IItems
import ice.entities.bullet.base.BasicBulletType
import ice.graphics.IceColor
import ice.world.meta.IceEffects
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.bullet.MissileBulletType
import mindustry.entities.part.DrawPart.PartParams
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootAlternate
import mindustry.entities.pattern.ShootSummon
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.draw.DrawTurret
@Suppress("unused")
object Turret:ContentLoad {
    val 碎冰: Block = ItemTurret("trashIce").apply {
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
    val 神矢: Block = PowerTurret("divineArrow").apply {
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
                        handler.shoot(
                            index * spread * -Mathf.sign(mirror), 0f, 0f, firstShotDelay + shotDelay * i
                        ) { b ->
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
        shootSound = Sounds.missile
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
    val 绪终: Block = ItemTurret("thinkEnd").apply {
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
}