package singularity.world.unit

import arc.func.Prov
import arc.math.Mathf
import arc.util.io.Reads
import arc.util.io.Writes
import ice.entities.IceRegister
import mindustry.Vars
import mindustry.ai.ControlPathfinder
import mindustry.ai.Pathfinder
import mindustry.ai.types.FlyingAI
import mindustry.ai.types.GroundAI
import mindustry.entities.EntityCollisions.SolidPred
import mindustry.entities.units.AIController
import mindustry.gen.Building
import mindustry.gen.Hitboxc
import mindustry.gen.Unit
import mindustry.gen.UnitWaterMove
import mindustry.world.meta.Env
import singularity.world.unit.abilities.ICollideBlockerAbility

open class AirSeaAmphibiousUnit(name: String) : SglUnitType<AirSeaAmphibiousUnit.AirSeaUnit>(name,AirSeaUnit::class.java) {
  var airReloadMulti: Float = 0.75f
  var airShootingSpeedMulti: Float = 0.8f

  init {
    envEnabled = envEnabled or Env.space
    pathCost = ControlPathfinder.costHover
    canBoost = true
    aiController = Prov {
      object : GroundAI() {
        override fun fallback(): AIController {
          return object : FlyingAI() {
            override fun updateMovement() {
              val core: Building? = unit.closestEnemyCore()

              if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * Vars.tilesize / 2f)) {
                target = core
                for (mount in unit.mounts) {
                  if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                    mount.target = core
                  }
                }
              }
              var boosting = false
              if ((core == null || !unit.within(core, unit.type.range * 0.5f))) {
                var move = true

                if (core != null) {
                  if (unit.type.canBoost && Mathf.len((core.tileX() - unit.tileX()).toFloat(), (core.tileY() - unit.tileY()).toFloat()) > 50) {
                    unit.elevation = Mathf.approachDelta(unit.elevation, 1f, unit.type.riseSpeed)
                    boosting = true
                  }
                }

                if (Vars.state.rules.waves && unit.team === Vars.state.rules.defaultTeam) {
                  val spawner = closestSpawner
                  if (unit.type.canBoost && Mathf.len((spawner!!.x - unit.tileX()).toFloat(), (spawner.y - unit.tileY()).toFloat()) > 50) {
                    unit.elevation = Mathf.approachDelta(unit.elevation, 1f, unit.type.riseSpeed)
                    boosting = true
                  }
                  if (spawner != null && unit.within(spawner, Vars.state.rules.dropZoneRadius + 120f)) move = false
                  if (spawner == null && core == null) move = false
                }
                //no reason to move if there's nothing there
                if (core == null && (!Vars.state.rules.waves || closestSpawner == null)) {
                  move = false
                }

                if (move) {
                  moveTo(core ?: closestSpawner, Vars.state.rules.dropZoneRadius + 130f)
                }
              }

              if (unit.type.canBoost) {
                unit.elevation = Mathf.approachDelta(unit.elevation, if (boosting || unit.onSolid() || (unit.isFlying && !unit.canLand())) 1f else 0f, unit.type.riseSpeed)
              }

              faceTarget()
            }
          }
        }

        override fun useFallback(): Boolean {
          return unit.isFlying
        }

        override fun updateMovement() {
          val core: Building? = unit.closestEnemyCore()

          if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * Vars.tilesize / 2f)) {
            target = core
            for (mount in unit.mounts) {
              if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                mount.target = core
              }
            }
          }

          if ((core == null || !unit.within(core, unit.type.range * 0.5f))) {
            var move = true

            if (core != null) {
              if (unit.type.canBoost && Mathf.len((core.tileX() - unit.tileX()).toFloat(), (core.tileY() - unit.tileY()).toFloat()) > 50) {
                unit.elevation = Mathf.approachDelta(unit.elevation, 1f, unit.type.riseSpeed)
              }
            }

            if (Vars.state.rules.waves && unit.team === Vars.state.rules.defaultTeam) {
              val spawner = closestSpawner
              if (unit.type.canBoost && Mathf.len((spawner!!.x - unit.tileX()).toFloat(), (spawner.y - unit.tileY()).toFloat()) > 50) {
                unit.elevation = Mathf.approachDelta(unit.elevation, 1f, unit.type.riseSpeed)
              }
              if (spawner != null && unit.within(spawner, Vars.state.rules.dropZoneRadius + 120f)) move = false
              if (spawner == null && core == null) move = false
            }
            //no reason to move if there's nothing there
            if (core == null && (!Vars.state.rules.waves || closestSpawner == null)) {
              move = false
            }

            if (move) {
              pathfind(Pathfinder.fieldCore)
            }
          }

          if (unit.type.canBoost) {
            unit.elevation = Mathf.approachDelta(unit.elevation, if (unit.onSolid() || (unit.isFlying && !unit.canLand())) 1f else 0f, unit.type.riseSpeed)
          }

          faceTarget()
        }
      }
    }
  }

  override fun update(unit: Unit) {
    super.update(unit)
    if (unit.isFlying) {
      unit.reloadMultiplier *= airReloadMulti
      if (unit.isShooting) {
        unit.speedMultiplier *= airShootingSpeedMulti
      }
    }
  }

  class AirSeaUnit : UnitWaterMove() {
    override fun solidity(): SolidPred? {
      return null
    }

    override fun canShoot(): Boolean {
      return !this.disarmed && (!this.type.canBoost || elevation < 0.09f || elevation > 0.9f)
    }

    override fun classId() = IceRegister.getId(this::class.java)

    override fun collides(other: Hitboxc?): Boolean {
      for (ability in abilities) {
        if (ability is ICollideBlockerAbility && ability.blockedCollides(this, other)) return false
      }

      return super.collides(other)
    }

    override fun add() {
      super.add()
      if (type is SglUnitType<*>) (type as SglUnitType<AirSeaUnit>).init(this)
      else throw RuntimeException("Unit type must be SglUnitType")
    }

    override fun read(read: Reads) {
      super.read(read)
      if (type is SglUnitType<*>) (type as SglUnitType<AirSeaUnit>).read(this, read, read.i())
      else throw RuntimeException("Unit type must be SglUnitType")
    }

    override fun write(write: Writes) {
      super.write(write)
      if (type is SglUnitType<*>) {
        val type1 = type as SglUnitType<AirSeaUnit>
        write.i(type1.version())
        type1.write(this, write)
      } else throw RuntimeException("Unit type must be SglUnitType")
    }
  }
}