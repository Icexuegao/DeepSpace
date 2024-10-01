package ice.Alon.ai

import arc.math.Rand
import arc.math.geom.Position
import mindustry.Vars
import mindustry.ai.types.FlyingAI
import mindustry.content.Blocks
import mindustry.gen.Groups

/**围绕旋转Ai*/
class CircleAi : FlyingAI() {
    var maxhealth = false

    /** 围绕半径 */
    var rand: Float = 0f

    /**计时器*/
    private var i = 0
    private var reversal = false

    /**反转时间*/
    var reversalTime = Rand().random(10, 20)
    override fun init() {
        rand = Rand().random(32f, unit.range())
    }

    override fun circle(target: Position?, circleLength: Float) {
        circle(target, circleLength, unit.speed())
    }

    override fun circle(target: Position?, circleLength: Float, speed: Float) {
        if (target == null) return
        i++
        if (i / 60 >= reversalTime) {
            i = 0
            reversal = !reversal
        }
        vec.set(target).sub(unit)
        if (vec.len() < circleLength) {
            vec.rotate((if (reversal) -1 else 1) * (circleLength - vec.len()) / circleLength * 180f)
        }
        vec.setLength(speed)
        unit.moveAt(vec)
    }

    override fun updateMovement() {
        unloadPayloads()
        if (maxhealth) {
            if (target == null) {
                /**寻找目标*/
                target = Groups.unit.find { it.team != unit.team }
            } else if (unit.health <= unit.maxHealth * 0.2) {
                /**判断血量*/
                maxhealth = false
            }
        } else {
            target = null
        }

        if (target != null && unit.hasWeapons()) {
            /**有目标就攻击*/
            circle(target, rand)
        }
        /**没有就回家*/
        if (target == null && Vars.state.rules.waves) {
            val find = Groups.build.find { it.block == Blocks.repairTurret || it.block == Blocks.repairPoint }
            val core = unit.team.core()
            if (unit.health == unit.maxHealth || find == null) {
                moveTo(core, Vars.state.rules.dropZoneRadius + 130f)
                maxhealth = true
            } else {
                moveTo(find, 32f)
            }
        }

    }
}