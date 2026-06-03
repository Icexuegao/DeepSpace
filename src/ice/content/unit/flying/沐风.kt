package ice.content.unit.flying

import arc.graphics.Color
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.MissileBulletType
import ice.world.content.unit.IceUnitType
import mindustry.ai.UnitCommand
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.type.Weapon

class 沐风 :IceUnitType("unit_bathedWind") {
  init {
    localization {
      zh_CN {
        localizedName = "沐风"
      }
    }

    faceTarget = true
    lowAltitude = false
    drawMinimap = true
    aimDst = 64f
    health = 7000f
    armor = 12f
    hitSize = 36f
    range = 172f
    immunities.addAll(StatusEffects.freezing, StatusEffects.slow)
    buildRange = 288f
    buildSpeed = 3f
    buildBeamOffset = 7f
    mineRange = 100f
    mineSpeed = 6f
    mineTier = 4
    itemCapacity = 120
    itemOffsetY = 8f
    payloadCapacity = 1024f
    parts.add(object :RegionPart() {
      init {
        suffix = "-blade"
        mirror = true
        layerOffset = -0.001f
        x = 17.5f
        y = -5.5f
        moveX = 3.5f
        moveY = 3.5f
      }
    }, object :HaloPart() {
      init {
        tri = false
        hollow = true
        color = Color.valueOf("#A0FFA0")
        sides = 3
        shapes = 1
        y = -26.5f
        radius = 8f
        radiusTo = 16f
        stroke = 0f
        strokeTo = 4f
        rotateSpeed = 1f
        haloRadius = 0f
        layer = Layer.effect
      }
    })
    weapons.add(object :Weapon(name + "-weapon") {
      init {
        mirror = true
        rotate = false
        top = false
        continuous = true
        x = 6.25f
        y = 14.75f
        shootY = 11.25f
        shootX = -2f
        recoil = 0.75f
        reload = 30f
        shake = 2f
        shootSound = Sounds.shootLancer
        bullet = object :LaserBulletType(60f) {
          init {
            healPercent = 10f
            recoil = 0.02f
            width = 16f
            length = 180f
            sideAngle = 90f
            sideWidth = 1f
            sideLength = 20f
            lifetime = 20f
            status = StatusEffects.electrified
            statusDuration = 240f
            colors = arrayOf(Color.valueOf("#80FF80"), Color.valueOf("#FFFFFF"))
          }
        }
      }
    }, object :Weapon(name + "-mount") {
      init {
        mirror = true
        rotate = false
        top = true
        layerOffset = -0.001f
        x = 14.75f
        y = 0.25f
        shootY = 6f
        shootX = 0f
        recoil = 2f
        reload = 6f
        shake = 2f
        shootSound = Sounds.shootLaser
        bullet = object :MissileBulletType() {
          init {
            sprite = "circle-bullet"
            frontColor = Color.valueOf("#FFFFFF")
            backColor = Color.valueOf("#80FF80")
            trailColor = Color.valueOf("#80FF80")
            collidesTeam = true
            damage = 10f
            healPercent = 5f
            speed = 8f
            lifetime = 26f
            width = 8f
            height = 8f
            weaveMag = 4f
            weaveScale = 4f
            homingDelay = 4f
            homingPower = 0.2f
            homingRange = 32f
            trailLength = 4
            trailWidth = 4f
            shootEffect = Fx.none
            hitEffect = object :WaveEffect() {
              init {
                colorFrom = Color.valueOf("#FFFFFF")
                colorTo = Color.valueOf("#80FF80")
                lifetime = 12f
                sizeFrom = 1f
                sizeTo = 16f
                strokeFrom = 16f
                strokeTo = 0f
              }
            }
            despawnEffect = Fx.greenCloud
          }
        }
      }
    })
    defaultCommand = UnitCommand.rebuildCommand
    flying = true
    speed = 2.6f
    rotateSpeed = 4f
    engineOffset = 26.5f
    engineSize = 6f
    setEnginesMirror(UnitEngine(-14.5f, -20f, 4f, -112.5f), UnitEngine(14.5f, -20f, 4f, -67.5f))
    trailLength = 8

    drag = 0.05f
    accel = 0.1f
    lightRadius = 60f
    lightColor = Color.valueOf("#80FF80")

  }
}