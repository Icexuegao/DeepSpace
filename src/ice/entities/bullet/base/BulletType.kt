package ice.entities.bullet.base

import arc.Core
import arc.Events
import arc.graphics.g2d.Draw
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.entities.Damage.bulletDamageEvents
import ice.entities.bullet.EmpBulletType
import ice.library.world.Load
import ice.world.content.unit.entity.base.Entity
import ice.world.meta.IceStatValues.sep
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.content.StatusEffects
import mindustry.entities.Damage
import mindustry.entities.Fires
import mindustry.game.EventType.UnitBulletDestroyEvent
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit
import kotlin.math.max
import kotlin.math.min

open class BulletType(speed: Float = 1f, damage: Float = 1f) : mindustry.entities.bullet.BulletType(speed, damage) {
    companion object : Load{

        override fun setup() {
            Pools.get(Bullet::class.java, ::ZDBullet)
        }
        private class ZDBullet : Bullet() {
            override fun draw() {
                Draw.z(this.type.layer)
                if (this.type.underwater) {
                    Drawf.underwater { type.draw(this) }
                } else {
                    if (type is BulletType) {
                        (type as BulletType).drawCustom(this)
                    } else {
                        type.draw(this)
                    }
                }

                this.type.drawLight(this)
                Draw.reset()
            }

            override fun collision(other: Hitboxc, x: Float, y: Float) {
                if (type.sticky) {
                    if (stickyTarget == null) {
                        this.x = x + vel.x
                        this.y = y + vel.y
                        stickTo(other)
                    }
                } else {
                    type.hit(this, x, y)
                    if (!type.pierce) {
                        hit = true
                        remove()
                    } else {
                        collided.add(other.id())
                    }
                    hitEntity(type, this, other, if (other is Healthc) other.health() else 0.0f)
                }
            }
        }

        fun hitEntity(type: mindustry.entities.bullet.BulletType, b: Bullet, entity: Hitboxc, health: Float) {
            if (entity is Entity) {
                entity.hitEntity(b, health)
                return
            }

            val wasDead = entity is (Unit) && entity.dead
            var health = health


            if (entity is Healthc) {
                var damage = b.damage
                val shield = if (entity is Shieldc) max(entity.shield(), 0f) else 0f
                // 如果设置了最大伤害比例限制
                if (type.maxDamageFraction > 0) {
                    // 计算伤害上限（基于最大生命值和护盾值）
                    val cap = entity.maxHealth() * type.maxDamageFraction + shield
                    // 限制实际伤害不超过上限
                    damage = min(damage, cap)
                    //将生命值上限为 handlePierce 以正确处理它
                    health = min(health, cap)
                } else {
                    health += shield
                }
                val owner = b.owner
                if (type.lifesteal > 0f && owner is Healthc) {
                    val result = max(min(entity.health(), damage), 0f)
                    owner.heal(result * type.lifesteal)
                }
                if (type.pierceArmor) {
                    entity.damagePierce(damage)
                } else {
                    entity.damage(damage)
                }
            }

            if (entity is Unit) {
                Tmp.v3.set(entity).sub(b).nor().scl(type.knockback * 80f)
                if (type.impact) Tmp.v3.setAngle(b.rotation() + (if (type.knockback < 0) 180f else 0f))
                entity.impulse(Tmp.v3)
                entity.apply(type.status, type.statusDuration)
                Events.fire(bulletDamageEvents.set(entity, b))
            }

            if (!wasDead && entity is Unit && entity.dead) {
                Events.fire(UnitBulletDestroyEvent(entity, b))
            }
            type.handlePierce(b, health, entity.x(), entity.y())
        }
    }

    var drawOverride: Boolean = false
    var drawRun: (Bullet) -> kotlin.Unit = {}
    var updateRun: (Bullet) -> kotlin.Unit = {}
    var removedRun: (Bullet) -> kotlin.Unit = {}
    fun drawCustom(b: Bullet) {
        if (drawOverride) {
            drawRun(b)
        } else {
            draw(b)
            drawRun(b)
        }
    }

    override fun update(b: Bullet) {
        super.update(b)
        updateRun(b)
    }


    override fun removed(b: Bullet) {
        super.removed(b)
        removedRun(b)
    }

    fun setDraw(override: Boolean = false, run: (Bullet) -> kotlin.Unit) {
        drawOverride = override
        this.drawRun = run
    }

    open fun setUpdate(run: (Bullet) -> kotlin.Unit) {
        this.updateRun = run
    }

    open fun setRemoved(run: (Bullet) -> kotlin.Unit) {
        removedRun = run
    }

    open fun setDamageStats(bt: Table) {
        if (damage > 0 && (collides || splashDamage <= 0)) {
            if (continuousDamage() > 0) {
                bt.add(Core.bundle.format("bullet.damage", continuousDamage()) + StatUnit.perSecond.localized())
            } else {
                bt.add(Core.bundle.format("bullet.damage", damage))
            }
        }
    }

    fun setStats(table: Table) {
        table.left().defaults().padRight(3f).left()
        setDamageStats(table)
        if (this is EmpBulletType) {
            val string =
                if (empRange > 0) "[lightgray]~ [accent]" + empRange / Vars.tilesize + "[lightgray]" + StatUnit.blocks.localized() else ""
            sep(table, "[accent]$empDamage[lightgray] ${IceStats.电磁脉冲伤害.localized()}[]$string")
        }

        /*  if (this is HeatBulletType) {
              table.row()
              table.table(Cons { t: Table? ->
                  t!!.left().defaults().padRight(3f).left()
                  t.image(OtherContents.meltdown.uiIcon).size(25f).scaling(Scaling.fit)
                  t.add(Core.bundle.format("infos.heatAmmo", Strings.autoFixed(bullet.meltDownTime / 60, 1),
                      Strings.autoFixed(bullet.melDamageScl * 60, 1),
                      if (bullet.maxExDamage > 0) bullet.maxExDamage else Math.max(bullet.damage, bullet.splashDamage)))
              })
          }*/

        if (buildingDamageMultiplier != 1f) {
            sep(table, Core.bundle.format("bullet.buildingdamage", (buildingDamageMultiplier * 100).toInt()))
        }

        if (rangeChange != 0f) {
            sep(table, Core.bundle.format("bullet.range",
                (if (rangeChange > 0) "+" else "-") + Strings.autoFixed(rangeChange / Vars.tilesize, 1)))
        }

        if (splashDamage > 0) {
            sep(table, Core.bundle.format("bullet.splashdamage", splashDamage.toInt(),
                Strings.fixed(splashDamageRadius / Vars.tilesize, 1)))
        }

        if (knockback > 0) {
            sep(table, Core.bundle.format("bullet.knockback", Strings.autoFixed(knockback, 2)))
        }

        if (healPercent > 0f) {
            sep(table, Core.bundle.format("bullet.healpercent", Strings.autoFixed(healPercent, 2)))
        }

        if (healAmount > 0f) {
            sep(table, Core.bundle.format("bullet.healamount", Strings.autoFixed(healAmount, 2)))
        }

        if (pierce || pierceCap != -1) {
            sep(table,
                if (pierceCap == -1) "@bullet.infinitepierce" else Core.bundle.format("bullet.pierce", pierceCap))
        }

        if (incendAmount > 0) {
            sep(table, "@bullet.incendiary")
        }

        if (homingPower > 0.01f) {
            sep(table, "@bullet.homing")
        }

        if (lightning > 0) {
            sep(table,
                Core.bundle.format("bullet.lightning", lightning, if (lightningDamage < 0) damage else lightningDamage))
        }

        if (pierceArmor) {
            sep(table, "@bullet.armorpierce")
        }

        if (status !== StatusEffects.none && status != null) {
            sep(table,
                (if (status.minfo.mod == null) status.emoji() else "") + "[stat]" + status.localizedName + "[lightgray] ~ " + "[stat]" + Strings.autoFixed(
                    statusDuration / 60f, 1) + "[lightgray] " + Core.bundle.get("unit.seconds"))
        }

        if (intervalBullet != null) {
            table.row()
            val ic = Table()
            if (intervalBullet is BulletType) {
                (intervalBullet as BulletType).setStats(ic)
            } else {
                table.add("警告 分裂子弹类型不正确${intervalBullet != null}")
            }
            val coll = Collapser(ic, true)
            coll.setDuration(0.1f)
            table.table {
                it.left().defaults().left()
                it.add(
                    Core.bundle.format("bullet.interval", Strings.autoFixed(intervalBullets / bulletInterval * 60, 2)))
                it.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }.update { i ->
                    i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen)
                }.size(8f).padLeft(16f).expandX()
            }
            table.row()
            table.add(coll).padLeft(16f)
        }
        if (fragBullet != null) {
            table.row()
            val ic = Table()
            if (fragBullet is BulletType) {
                (fragBullet as BulletType).setStats(ic)
            } else {
                table.add("警告 破片子弹类型不正确${fragBullet::class.simpleName}").row()
            }
            val coll = Collapser(ic, true)
            coll.setDuration(0.1f)
            table.table { ft ->
                ft.left().defaults().left()
                ft.add(Core.bundle.format("bullet.frags", fragBullets))
                ft.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }.update { i ->
                    i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen)
                }.size(8f).padLeft(16f).expandX()
            }
            table.row()
            table.add(coll).padLeft(16f)
        }
        table.row()
    }


    override fun createSplashDamage(b: Bullet, x: Float, y: Float) {

        if (splashDamageRadius > 0) {
            ice.entities.Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(),
                splashDamagePierce, collidesAir, collidesGround, scaledSplashDamage, b)

            if (status !== StatusEffects.none) {
                Damage.status(b.team, x, y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround)
            }

            if (heals()) {
                Vars.indexer.eachBlock(b.team, x, y, splashDamageRadius, { obj -> obj.damaged() }, { other ->
                    healEffect.at(other.x, other.y, 0f, healColor, other.block)
                    other.heal(healPercent / 100f * other.maxHealth() + healAmount)
                })
            }
            if (makeFire) {
                Vars.indexer.eachBlock(null, x, y, splashDamageRadius, { other -> other.team !== b.team }, { other ->
                    Fires.create(other.tile)
                })
            }
        }
    }


}
