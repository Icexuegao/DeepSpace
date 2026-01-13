package singularity.world.unit

import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Hitboxc
import mindustry.gen.Unit
import mindustry.gen.UnitEntity
import singularity.world.unit.abilities.ICollideBlockerAbility

class SglUnitEntity : UnitEntity() {
    var controlTime=0f
    override fun classId(): Int {
        return 51
    }

    override fun collides(other: Hitboxc?): Boolean {
        for (ability in abilities) {
            if (ability is ICollideBlockerAbility && ability.blockedCollides(this, other)) return false
        }

        return super.collides(other)
    }

    override fun add() {
        super.add()
        if (type is SglUnitType<*>) {
            val unit = this as Unit
            (type as SglUnitType<*>).init(unit as SglUnitEntity)
        } else throw RuntimeException("Unit type must be SglUnitType")
    }

    override fun read(read: Reads) {
        super.read(read)
        if (type is SglUnitType<*>) (type as SglUnitType<*>).read(this, read, read.i())
        else throw RuntimeException("Unit type must be SglUnitType")
    }

    override fun write(write: Writes) {
        super.write(write)
        if (type is SglUnitType<*>) {
            write.i((type as SglUnitType<*>).version())
            (type as SglUnitType<*>).write(this, write)
        } else throw RuntimeException("Unit type must be SglUnitType")
    }
}