package singularity.world.unit

import arc.graphics.Color
import arc.scene.ui.layout.Table
import arc.util.io.Reads
import arc.util.io.Writes
import ice.entities.IceRegister
import ice.graphics.IceColor
import mindustry.entities.units.UnitController
import mindustry.gen.Hitboxc
import mindustry.gen.UnitEntity
import mindustry.ui.Bar
import singularity.Sgl
import singularity.world.unit.abilities.ICollideBlockerAbility

class SglUnitEntity : UnitEntity() {
  override fun classId(): Int {
    val id1 = IceRegister.getId(this::class.java)
    return id1
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
      (type as SglUnitType<SglUnitEntity>).init(this)
    } else throw RuntimeException("Unit type must be SglUnitType")
  }

  override fun read(read: Reads) {
    super.read(read)
    if (type is SglUnitType<*>) (type as SglUnitType<SglUnitEntity>).read(this, read, read.i())
    else throw RuntimeException("Unit type must be SglUnitType")
  }

  override fun write(write: Writes) {
    super.write(write)
    if (type is SglUnitType<*>) {
      write.i((type as SglUnitType<*>).version())
      (type as SglUnitType<SglUnitEntity>).write(this, write)
    } else throw RuntimeException("Unit type must be SglUnitType")
  }

  override fun controller(): UnitController? {
    return super.controller()
  }

  override fun display(table: Table) {
    super.display(table)
    val bars = table.children[1] as Table

    bars.add(Bar({ Sgl.empHealth.healthPresent(this).toString() }, { IceColor.b3 }, {
      Sgl.empHealth.healthPresent(this)
    }).blink(Color.white))
  }
}