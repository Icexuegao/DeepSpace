package ice.world.content.blocks.defense

import arc.audio.Sound
import arc.func.Prov
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.util.Time
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.entities.Lightning
import mindustry.entities.TargetPriority
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.abs

open class Wall(name: String) : IceBlock(name) {
    /** 点亮机会。-1 禁用  */
    var lightningChance: Float = -1f
    var lightningDamage: Float = 20f
    var lightningLength: Int = 17
    var lightningColor: Color? = Pal.surge
    var lightningSound: Sound = Sounds.spark

    /**子弹偏转几率。-1 禁用  */
    var chanceDeflect: Float = 0f
    var flashHit: Boolean = false
    var flashColor: Color = Color.white
    var deflectSound: Sound = Sounds.none

    init {
        solid = true
        canOverdrive = false
        drawDisabled = false
        destructible = true
        group = BlockGroup.walls
        buildCostMultiplier = 6f
        crushDamageMultiplier = 5f
        priority = TargetPriority.wall
        buildType = Prov(::WallBuild)
    }

    override fun setStats() {
        super.setStats()
        if (chanceDeflect > 0f) stats.add(IceStats.反射概率基数, chanceDeflect, StatUnit.none)
        if (lightningChance > 0f) {
            stats.add(Stat.lightningChance, lightningChance * 100f, StatUnit.percent)
            stats.add(Stat.lightningDamage, lightningDamage, StatUnit.none)
        }
    }

    inner class WallBuild : IceBuild() {
        var hit: Float = 0f
        override fun draw() {
            super.draw()
            //绘制闪烁的白色叠加层（如果启用）
            if (flashHit) {
                if (hit < 0.0001f) return
                Draw.color(flashColor)
                Draw.alpha(hit * 0.5f)
                Draw.blend(Blending.additive)
                Fill.rect(x, y, (Vars.tilesize * size).toFloat(), (Vars.tilesize * size).toFloat())
                Draw.blend()
                Draw.reset()

                if (!Vars.state.isPaused) {
                    hit = Mathf.clamp(hit - Time.delta / 10f)
                }
            }
        }

        override fun collision(bullet: Bullet): Boolean {
            super.collision(bullet)

            hit = 1f
            //create lightning if necessary
            if (lightningChance > 0f) {
                if (Mathf.chance(lightningChance.toDouble())) {
                    Lightning.create(team, lightningColor, lightningDamage, x, y, bullet.rotation() + 180f,
                        lightningLength)
                    lightningSound.at(tile, Mathf.random(0.9f, 1.1f))
                }
            }
            //必要时偏转子弹
            if (chanceDeflect > 0f) {
                //慢速子弹不会偏转
                if (bullet.vel.len() <= 0.1f || !bullet.type.reflectable) return true
                //子弹反射几率取决于子弹伤害
                if (!Mathf.chance(((chanceDeflect / bullet.damage()).toDouble()))) return true
                //make sound
                deflectSound.at(tile, Mathf.random(0.9f, 1.1f))
                //translate bullet back to where it was upon collision
                bullet.trns(-bullet.vel.x, -bullet.vel.y)
                val penX = abs(x - bullet.x)
                val penY = abs(y - bullet.y)

                if (penX > penY) {
                    bullet.vel.x *= -1f
                } else {
                    bullet.vel.y *= -1f
                }

                bullet.owner = this
                bullet.team = team
                bullet.time += 1f
                //通过返回 false 禁用子弹碰撞
                return false
            }

            return true
        }
    }
}