package ice.library.content.unit.entity

import arc.math.Angles
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IUnitTypes.蚀虻End
import ice.content.IUnitTypes.蚀虻Middle
import ice.library.content.unit.entity.CorrodflyHead.Companion.SEGMENT_DISTANCE
import ice.library.content.unit.entity.base.FleshEntity
import mindustry.entities.units.AIController
import mindustry.game.Team
import mindustry.gen.Groups

class CorrodflyHead : FleshEntity() {
    companion object {
        // 共同变量
        const val SEGMENT_DISTANCE = 16f
    }

    var middleUnit: CorrodflyMiddle? = null
    var endUnit: CorrodflyEnd? = null
    var endUnitId = -1
    var middleUnitId = -1
    var shouldSpawn = true
    override fun aim(x: Float, y: Float) {
        endUnit?.aim(x, y)
        super.aim(x, y)
    }

    override fun controlWeapons(rotate: Boolean, shoot: Boolean) {
        super.controlWeapons(rotate, shoot)
        endUnit?.controlWeapons(rotate, shoot)
        (endUnit?.controller() as? ice.ai.AIController)?.aimUnit = shoot
    }
    override fun team(team: Team?) {
        super.team(team)
        endUnit?.team(team)
        middleUnit?.team(team)
    }

    override fun update() {
        super.update()
        if (shouldSpawn) {
            shouldSpawn = false
            val corrodflyMiddle = 蚀虻Middle.spawn(team, this) as CorrodflyMiddle
            val corrodflyEnd = 蚀虻End.spawn(team, this) as CorrodflyEnd

            corrodflyMiddle.unitHead = this
            corrodflyEnd.unitHead = this

            corrodflyMiddle.let {
                middleUnit = it
                corrodflyEnd.unitMiddle = it
            }
            corrodflyEnd.let {
                endUnit = it
                corrodflyMiddle.unitEnd = it
            }
        }
        middleUnit?.let { middle ->
            val maxHealthDifference = 5f
            if (middle.health > health + maxHealthDifference && health < (maxHealth - maxHealthDifference)) {
                middle.health -= 10f
                health += 10f
            }
            if (health > middle.health + maxHealthDifference && middle.health < (middle.maxHealth - maxHealthDifference)) {
                middle.health += 10f
                health -= 10f
            }
        }
        if (endUnitId != -1 && endUnit == null) {
            endUnit = Groups.unit.getByID(endUnitId) as CorrodflyEnd
        }
        if (middleUnitId != -1 && middleUnit == null) {
            middleUnit = Groups.unit.getByID(middleUnitId) as CorrodflyMiddle
        }
    }

    override fun kill() {
        super.kill()
        middleUnit?.let {
            if (!it.dead) it.kill()
        }
        endUnit?.let {
            if (!it.dead) it.kill()
        }
    }

    override fun read(read: Reads) {
        super.read(read)
        shouldSpawn = read.bool()
        endUnitId = read.i()
        middleUnitId = read.i()
    }

    override fun write(write: Writes) {
        super.write(write)
        write.bool(shouldSpawn)
        write.i(endUnit?.id ?: -1)
        write.i(middleUnit?.id ?: -1)
    }
}

class CorrodflyMiddle : FleshEntity() {
    var unitEnd: CorrodflyEnd? = null
    var unitHead: CorrodflyHead? = null
    var endUnitId = -1
    var headUnitId = -1
    override fun canPassOn() = true
    override fun kill() {
        super.kill()
        unitHead?.let {
            if (!it.dead) it.kill()
        }
        unitEnd?.let {
            if (!it.dead) it.kill()
        }
    }

    override fun update() {
        super.update()
        unitHead?.let {
            team(it.team)
            val circleLength = hitSize / 2 + it.hitSize / 2 + 2
            Tmp.v1.set(circleLength, 0f).setAngle(it.rotation + 180f)
            (controller as AIController).moveTo(Tmp.v1.add(it), 1f, 3f)
            // 方法1：使用带符号的角度差计算
            val angleDiff = Angles.angleDist(rotation, it.rotation)
            if (angleDiff > 10f) {
                // 使用 moveToward 自动处理方向，避免突然旋转
                rotation = Angles.moveToward(rotation, it.rotation, Time.delta * it.type.rotateSpeed)
            }
            if (Tmp.v1.set(it).sub(this).len() > SEGMENT_DISTANCE) {
                Tmp.v2.set(0f, SEGMENT_DISTANCE).setAngle(it.rotation + 180)
                set(Tmp.v2.x + it.x, Tmp.v2.y + it.y)
            }
        }
        unitEnd?.let { end ->
            val maxHealthDifference = 10f
            if (end.health > health + maxHealthDifference && health < (maxHealth - maxHealthDifference)) {
                end.health -= 10f
                health += 10f
            }
            if (health > end.health + maxHealthDifference && end.health < (end.maxHealth - maxHealthDifference)) {
                end.health += 10f
                health -= 10f
            }
        }

    }

    override fun afterReadAll() {
        super.afterReadAll()
        if (endUnitId != -1) {
            unitEnd = Groups.unit.getByID(endUnitId) as CorrodflyEnd
        }
        if (headUnitId != -1) {
            unitHead = Groups.unit.getByID(headUnitId) as CorrodflyHead
        }
    }

    override fun read(read: Reads) {
        super.read(read)
        endUnitId = read.i()
        headUnitId = read.i()
    }

    override fun write(write: Writes) {
        super.write(write)
        write.i(unitEnd?.id ?: -1)
        write.i(unitHead?.id ?: -1)
    }
}

class CorrodflyEnd : FleshEntity() {
    var unitMiddle: CorrodflyMiddle? = null
    var unitHead: CorrodflyHead? = null
    var middleUnitId = -1
    var headUnitId = -1
    override fun canPassOn() = true
    override fun kill() {
        super.kill()
        unitHead?.let {
            if (!it.dead) it.kill()
        }
        unitMiddle?.let {
            if (!it.dead) it.kill()
        }
    }

    override fun update() {
        super.update()
        unitMiddle?.let {
            val circleLength = hitSize / 2 + it.hitSize / 2 + 2
            Tmp.v1.set(circleLength, 0f).setAngle(it.rotation + 180f)
            (controller as? AIController)?.moveTo(Tmp.v1.add(it), 1f, 3f)
            val angleDiff = Angles.angleDist(rotation, it.rotation)
            if (angleDiff > 10f) {
                // 使用 moveToward 自动处理方向，避免突然旋转
                rotation = Angles.moveToward(rotation, it.rotation, Time.delta * it.type.rotateSpeed)
            }
            if (Tmp.v1.set(it).sub(this).len() > SEGMENT_DISTANCE) {
                Tmp.v2.set(0f, SEGMENT_DISTANCE).setAngle(it.rotation + 180)
                set(Tmp.v2.x + it.x, Tmp.v2.y + it.y)
            }
        }

    }

    override fun afterReadAll() {
        super.afterReadAll()
        if (headUnitId != -1) {
            unitHead = Groups.unit.getByID(headUnitId) as CorrodflyHead
        }
        if (middleUnitId != -1) {
            unitMiddle = Groups.unit.getByID(middleUnitId) as CorrodflyMiddle
        }
    }

    override fun read(read: Reads) {
        super.read(read)
        headUnitId = read.i()
        middleUnitId = read.i()
    }

    override fun write(write: Writes) {
        super.write(write)
        write.i(unitHead?.id ?: -1)
        write.i(unitMiddle?.id ?: -1)
    }
}
