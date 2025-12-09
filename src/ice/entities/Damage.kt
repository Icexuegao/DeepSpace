package ice.entities

import arc.Events
import arc.func.Cons
import arc.math.Mathf
import arc.math.geom.Point2
import arc.math.geom.Rect
import arc.math.geom.Vec2
import arc.struct.IntFloatMap
import arc.util.Nullable
import arc.util.Time
import ice.entities.Damage.damage
import ice.library.util.accessField
import mindustry.Vars
import mindustry.core.World
import mindustry.entities.Damage
import mindustry.entities.Units
import mindustry.game.EventType
import mindustry.game.EventType.UnitBulletDestroyEvent
import mindustry.game.EventType.UnitDamageEvent
import mindustry.game.Team
import mindustry.gen.Bullet
import mindustry.gen.Unit
import kotlin.math.*

object Damage {
    var bulletDamageEvents: UnitDamageEvent by Damage::class.accessField("bulletDamageEvent")
    val vec: Vec2 = Vec2()
    var rect: Rect = Rect()
    val damages: IntFloatMap = IntFloatMap()
    private fun calculateDamage(dist: Float, radius: Float, damage: Float): Float {
        val falloff = 0.4f
        val scaled = if (radius <= 0.00001f) 1f else Mathf.lerp(1f - dist / radius, 1f, falloff)
        return damage * scaled
    }

    private fun completeDamage(team: Team?, x: Float, y: Float, radius: Float, damage: Float) {
        val trad = (radius / Vars.tilesize).toInt()
        for (dx in -trad..trad) {
            for (dy in -trad..trad) {
                val tile = Vars.world.tile((x / Vars.tilesize).roundToInt() + dx, (y / Vars.tilesize).roundToInt() + dy)
                if (tile != null && tile.build != null && (team == null || team !== tile.team()) && dx * dx + dy * dy <= trad * trad) {
                    tile.build.damage(team, damage)
                }
            }
        }
    }

    fun tileDamage(team: Team?, tx: Int, ty: Int, baseRadius: Float, damage: Float, @Nullable source: Bullet?) {
        Time.run(0f) {
            // 限制坐标范围
            val x = Mathf.clamp(tx, -100, Vars.world.width() + 100)
            val y = Mathf.clamp(ty, -100, Vars.world.height() + 100)

            // 处理多格建筑的特殊情况
            val building = Vars.world.build(x, y)
            if (building != null && building.team !== team && building.block.size > 1 && building.health > damage) {
                // 计算等效伤害
                val scaledDamage = damage * min(building.block.size.toFloat(), baseRadius * 0.4f)
                if (source != null) {
                    building.damage(source, team, scaledDamage)
                } else {
                    building.damage(team, scaledDamage)
                }
            }

            // 限制最大半径防止性能问题
            val radius = min(baseRadius, 100f)
            val radiusSquared = radius * radius
            val rayCount = Mathf.ceil(radius * 2 * Mathf.pi)
            val angleStep = Math.PI * 2.0 / rayCount
            val damageMap = mutableMapOf<Int, Float>()

            // 从每个角度发射射线
            for (i in 0 until rayCount) {
                val angle = angleStep * i
                val rayEndX = (x + cos(angle) * radius).toInt()
                val rayEndY = (y + sin(angle) * radius).toInt()

                // Bresenham射线追踪
                var currentX = x
                var currentY = y
                val dx = abs(rayEndX - x)
                val dy = abs(rayEndY - y)
                val xStep = if (x < rayEndX) 1 else -1
                val yStep = if (y < rayEndY) 1 else -1
                var error = dx - dy

                while (currentX != rayEndX || currentY != rayEndY) {
                    val targetBuilding = Vars.world.build(currentX, currentY)
                    if (targetBuilding != null && targetBuilding.team !== team) {
                        // 计算边缘伤害衰减
                        val distanceSquared = Mathf.dst2(currentX.toFloat(), currentY.toFloat(), x.toFloat(), y.toFloat())
                        val edgeScale = 0.6f
                        val damageMultiplier = (1f - distanceSquared / radiusSquared + edgeScale) / (1f + edgeScale)
                        val damageAmount = damage * damageMultiplier

                        // 记录伤害
                        val packedPos = Point2.pack(currentX, currentY)
                        damageMap[packedPos] = max(damageMap.getOrDefault(packedPos, 0f), damageAmount)

                        // 如果伤害为零则停止追踪
                        if (damageAmount <= 0f) break
                    }

                    // Bresenham算法步进
                    val error2 = 2 * error
                    if (error2 > -dy) {
                        error -= dy
                        currentX += xStep
                    }
                    if (error2 < dx) {
                        error += dx
                        currentY += yStep
                    }
                }
            }

            // 应用伤害
            damageMap.forEach { (packedPos, damageAmount) ->
                val cx = Point2.x(packedPos).toInt()
                val cy = Point2.y(packedPos).toInt()
                val targetBuilding = Vars.world.build(cx, cy)
                if (targetBuilding != null) {
                    if (source != null) {
                        targetBuilding.damage(source, team, damageAmount)
                    } else {
                        targetBuilding.damage(team, damageAmount)
                    }
                }
            }
        }
    }

    /**
     * 造成伤害的方法，可以影响单位和建筑
     * @param team 目标队伍，null表示影响所有单位
     * @param x 伤害中心的x坐标
     * @param y 伤害中心的y坐标
     * @param radius 伤害范围半径
     * @param damage 基础伤害值
     * @param complete 是否造成完全伤害（忽略护甲等减伤效果）
     * @param air 是否对空中单位有效
     * @param ground 是否对地面单位有效
     * @param scaled 伤害是否随距离衰减
     * @param source 造成伤害的子弹来源，可为null
     */
    fun damage(team: Team?,
               x: Float,
               y: Float,
               radius: Float,
               damage: Float,
               complete: Boolean,
               air: Boolean,
               ground: Boolean,
               scaled: Boolean,
               @Nullable source: Bullet?
    ) {
        // 创建一个消费者，用于处理每个受影响的单位
        val cons = Cons { unit: Unit? ->
            // 检查单位是否在影响范围内且有效
            if (unit!!.team === team || !unit.checkTarget(air, ground) || !unit.hittable() || !unit.within(x, y,
                    radius + (if (scaled) unit.hitSize / 2f else 0f))
            ) {
                return@Cons
            }
            // 记录单位是否已经死亡
            val dead = unit.dead

            // 计算实际伤害值，考虑距离衰减
            val amount =
                calculateDamage(if (scaled) max(0f, unit.dst(x, y) - unit.type.hitSize / 2) else unit.dst(x, y), radius,
                    damage)
            // 对单位造成伤害
            unit.damage(amount)

            // 如果有子弹来源，触发相关事件
            if (source != null) {
                Events.fire(bulletDamageEvents.set(unit, source))
                unit.controller().hit(source)

                // 如果单位在受到伤害后死亡，触发单位死亡事件
                if (!dead && unit.dead) {
                    Events.fire<UnitBulletDestroyEvent?>(UnitBulletDestroyEvent(unit, source))
                }
            }
            //TODO 更好的速度位移
            val dst = vec.set(unit.x - x, unit.y - y).len()
            unit.vel.add(vec.setLength((if (radius > 0f) 1f - dst / radius else 1f) * 2f / unit.mass()))
            if (complete && damage >= 9999999f && unit.isPlayer) {
                Events.fire(EventType.Trigger.exclusionDeath)
            }
        }

        rect.setSize(radius * 2).setCenter(x, y)
        if (team != null) {
            Units.nearbyEnemies(team, rect, cons)
        } else {
            Units.nearby(rect, cons)
        }

        if (ground) {
            if (!complete) {
                tileDamage(team, World.toTile(x), World.toTile(y), radius / Vars.tilesize,
                    damage * (if (source == null) 1f else source.type.buildingDamageMultiplier), source)
            } else {
                completeDamage(team, x, y, radius,
                    damage * (if (source == null) 1f else source.type.buildingDamageMultiplier))
            }
        }
    }
}