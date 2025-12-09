package ice.ai

import arc.math.Rand
import arc.math.geom.Vec2
import ice.world.meta.IceEffects.lancerLaserShoot1
import mindustry.ai.types.FlyingAI
import mindustry.gen.Groups

class TeleportationAI : FlyingAI() {
    /**计时器*/
    private var i = 0

    /**冷却时间*/
    private val coolingTime = 30

    /**冷却*/
    private var cooling = true
    override fun updateMovement() {
        i++
        if (i >= 60 * coolingTime) {
            i = 0
            cooling = true
        }
        if (target == null) {
            /**寻找目标*/
            target = Groups.unit.find { it.team != unit.team }
            //super.updateMovement()
        }
        /**有目标就向目标移动*/
        if (target != null) {
            teleportation(unit.speed())
        }
    }

    private fun teleportation(speed: Float) {
        /**设置向量两点为单位和目标*/
        vec.set(target).sub(unit)
        /**判断单位武器和目标的距离 true就执行圆圈攻击,false就接着移动*/
        if (vec.len() < unit.range()) {
            circleAttack(120f)
        } else {
            /**距离大于指定距离 且冷却完成就进行瞬移 并执行特效反之接着移动*/
            if (vec.len() > 120f * 8 && cooling) {
                lancerLaserShoot1.at(unit.x, unit.y)
                cooling = false
                val tele = Vec2(target.x, target.y)
                val range = unit.range()
                val randomx = Rand().random(2 * 8f, range)
                val randomy = Rand().random(2 * 8f, range)
                val r1 = Rand().random(-1, 1)
                unit.x = tele.x + randomx * r1
                unit.y = tele.y + randomy * -r1
                lancerLaserShoot1.at(unit.x, unit.y)
            }
            vec.setLength(speed)
            unit.moveAt(vec)
        }
    }
}