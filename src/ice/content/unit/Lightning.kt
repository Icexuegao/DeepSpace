package ice.content.unit

import arc.graphics.Color
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.ui.bundle.BaseBundle
import ice.ui.bundle.BaseBundle.Companion.desc
import mindustry.content.Fx
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.HaloPart
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.type.unit.MissileUnitType
import mindustry.type.weapons.PointDefenseWeapon
import mindustry.type.weapons.RepairBeamWeapon

class Lightning :MissileUnitType("unit_lightning") {
  init {
    desc(BaseBundle.zh_CN, "雷精")

    health = 480f
    armor = 8f
    hitSize = 0f
    speed = 0f
    range = 240f
    lifetime = 1800f
    hittable = false
    targetable = false
    drawCell = false
    drawBody = false
    lowAltitude = true
    engineSize = 0f
    lightRadius = 240f
    lightColor = Color.valueOf("FF5845")

    parts.addAll(
      HaloPart().apply {
        tri = true
        shapes = 6
        stroke = 1f
        radius = 4.5f
        triLength = 9f
        haloRadius = 18f
        haloRotateSpeed = 0.5f
        color = Color.valueOf("FF5845")
        layer = Layer.bullet - 1f
      },
      HaloPart().apply {
        tri = true
        shapeRotation = 180f
        shapes = 6
        radius = 4.5f
        triLength = 2f
        haloRadius = 18f
        haloRotateSpeed = 0.5f
        color = Color.valueOf("FF5845")
        layer = Layer.bullet - 1f
      },
      HaloPart().apply {
        tri = true
        shapeRotation = 180f
        shapes = 6
        radius = 4.5f
        triLength = 4f
        haloRadius = 12f
        haloRotation = -7.2f
        haloRotateSpeed = -0.5f
        color = Color.valueOf("FF5845")
        layer = Layer.bullet - 1f
      },
      HaloPart().apply {
        tri = true
        shapes = 6
        radius = 4.5f
        triLength = 2f
        haloRadius = 12f
        haloRotation = -7.2f
        haloRotateSpeed = -0.5f
        color = Color.valueOf("FF5845")
        layer = Layer.bullet - 1f
      }
    )

    weapons.add(RepairBeamWeapon().apply {
      mirror = false
      x = 0f
      shootY = 0f
      reload = 1f
      recoil = 0f
      shake = 0f
      rotate = true
      rotateSpeed = 3f
      laserColor = Color.valueOf("FF5845")
      controllable = false
      autoTarget = true
      beamWidth = 0.9f
      pulseRadius = 6f
      pulseStroke = 2f
      repairSpeed = 5f

      bullet = BulletType().apply {
        maxRange = 120f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 20
          offset = 35f
          lifetime = 22f
          length = 35f
          cone = -360f
          lenFrom = 5f
          lenTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FFDCD8")
        }
      }
    })

    weapons.add(PointDefenseWeapon().apply {
      x = 0f
      recoil = 0f
      reload = 2f
      color = Color.valueOf("FF5845")
      targetInterval = 2f
      targetSwitchInterval = 2f
      shootSound = Sounds.shootLaser

      bullet = BulletType().apply {
        damage = 150f
        maxRange = 240f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    })

    abilities.add(EnergyFieldAbility(445f, 60f, 240f).apply {
      maxTargets = 20
      status = IStatus.熔融
      statusDuration = 30f
      healPercent = 4f
      sectors = 6
      sectorRad = 0.12f
      color = Color.valueOf("FF5845")
      effectRadius = 4f
      rotateSpeed = 0.5f
    })

    deathExplosionEffect = ParticleEffect().apply {
      line = true
      particles = 24
      lifetime = 20f
      length = 75f
      cone = -360f
      lenFrom = 8f
      lenTo = 4f
      strokeFrom = 3f
      strokeTo = 0f
      lightColor = Color.valueOf("FF5845")
      colorFrom = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FFDCD8")
    }

  }
}