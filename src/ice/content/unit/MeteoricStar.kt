package ice.content.unit

import arc.graphics.Color
import arc.struct.Seq
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.entities.abilities.ShieldArcAbility
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Weapon
import mindustry.type.weapons.PointDefenseWeapon
import mindustry.type.weapons.RepairBeamWeapon
import mindustry.world.blocks.defense.turrets.PowerTurret

class MeteoricStar : IceUnitType("meteoricStar") {
  init {
    bundle {
      desc(zh_CN, "陨星", "多功能异构飞行器,具有强大的纳米修复系统,集群作战时尤为强大\n具有侧向弧形盾,可以抵挡两侧袭来的子弹")
    }
    flying = true
    lowAltitude = true
    health = 25300f
    armor = 29f
    hitSize = 44f
    speed = 1.3f
    accel = 0.04f
    drag = 0.04f
    rotateSpeed = 2.3f
    engineSize = 0f
    engineOffset = 24f
    healColor = Color.valueOf("FFA665")
    outlineColor = Color.valueOf("1F1F1F")
    engines = Seq.with(UnitEngine(3f, -22f, 6f, -90f))

    var b1 = ((Blocks.afflict as PowerTurret).shootType as mindustry.entities.bullet.BasicBulletType).apply {
      recoil = 1f
      val toColor: Color = "FFA665".toColor()
      hitColor = toColor
      backColor = toColor
      trailColor = toColor
      fragBullet.apply {
        (hitEffect as WaveEffect).apply {
          colorFrom = toColor
        }
        (despawnEffect as WaveEffect).apply {
          colorTo = toColor
        }
        hitColor = toColor
        backColor = toColor
        trailColor = toColor
      }
      intervalBullet = fragBullet
    }

    fun strafe(wx: Float, wy: Float): Weapon {
      return Weapon("$name-machineGun").apply {
        x = wx
        y = wy
        recoil = 1f
        shake = 1f
        reload = 43f
        rotate = true
        mirror = false
        shootCone = 5f
        inaccuracy = 1f
        rotateSpeed = 6f
        cooldownTime = 65f
        ejectEffect = Fx.casing2
        shoot.apply {
          shots = 2
          shotDelay = 4f
        }
        shootSound = Sounds.shoot
        this.bullet = BasicBulletType(7f, 73f).apply {
          width = 8f
          height = 12f
          lifetime = 39f
          hitColor = Color.valueOf("FFA665")
          splashDamage = 25f
          splashDamageRadius = 16f
          status = IStatus.损毁
          statusDuration = 60f
          hitEffect = Fx.flakExplosion
          shootEffect = Fx.shootSmokeSquare
          despawnEffect = Fx.hitSquaresColor
        }

      }
    }
    weapons.add(strafe(-11.25f, 20.75f))
    weapons.add(strafe(-15f, 12.25f))
    weapons.add(strafe(14.75f, 3.5f))
    setWeapon("secondaryCannon") {
      x = -12.75f
      y = -17.25f
      recoil = 2f
      shake = 3f
      reload = 35f
      mirror = false
      shootCone = 5f
      cooldownTime = 65f
      ejectEffect = Fx.casing1
      shoot.apply {
        shots = 4
        shotDelay = 4f
      }
      shootSound = Sounds.shoot
      bullet = BasicBulletType(9f, 73f).apply {
        width = 8f
        height = 12f
        lifetime = 41f
        hitColor = Color.valueOf("FFA665")
        splashDamage = 47f
        splashDamageRadius = 32f
        status = IStatus.破甲II
        statusDuration = 75f
        hitEffect = Fx.flakExplosion
        shootEffect = Fx.shootSmokeSquare
        despawnEffect = Fx.hitSquaresColor
      }
    }
    setWeapon("陨星主炮") {
      x = 3.25f
      y = 5f
      recoil = 0f
      shake = 3f
      reload = 165f
      mirror = false
      shootCone = 20f
      cooldownTime = 185f
      ejectEffect = Fx.casing4
      shootSound = Sounds.shootCorvus
      bullet = b1
    }
    setWeaponT<RepairBeamWeapon>("陨铁修复") {
      x = 3f
      y = -7.75f
      shootY = 0f
      mirror = false
      laserColor = Color.valueOf("FFA665")
      repairSpeed = 11f
      bullet = BulletType().apply {
        maxRange = 200f
      }
    }
    setWeaponT<PointDefenseWeapon>("陨铁点防") {
      x = 3f
      y = -7.75f
      shootY = 0f
      color = Color.valueOf("FFA665")
      reload = 5f
      targetInterval = reload
      targetSwitchInterval = reload
      bullet = BulletType().apply {
        damage = 131f
        maxRange = 256f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    }
    abilities.add(EnergyFieldAbility(255f, 85f, 240f).apply {
      x = 3f
      y = -7.75f
      healPercent = 2f
      effectRadius = 3f
      maxTargets = 20
      statusDuration = 180f
      color = Color.valueOf("FFA665")
      status = IStatus.熔融
    })
    abilities.add(ShieldArcAbility().apply {
      x = 33f
      regen = 5f
      max = 3000f
      cooldown = 60f * 8
      angleOffset = 90f
      angle = 75f
      radius = 63f
      width = 5f
      whenShooting = false
    }, ShieldArcAbility().apply {
      x = -6f
      y = -6f
      regen = 5f
      max = 3000f
      cooldown = 60f * 8
      angleOffset = -90f
      angle = 96f
      radius = 36f
      width = 5f
      whenShooting = false
    })
  }
}