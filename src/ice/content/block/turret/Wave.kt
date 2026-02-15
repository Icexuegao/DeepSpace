package ice.content.block.turret

import arc.graphics.Color
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.addAmmoType
import ice.entities.bullet.ArtilleryBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootAlternate
import mindustry.entities.pattern.ShootMulti
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.draw.DrawTurret

class Wave : ItemTurret("turret_wave") {
  init {
    BaseBundle.bundle {
      desc(zh_CN,"浪潮","大型抛射炮塔,能够快速交替发射散射炮弹\n使用聚能装药爆破弹,极大提升了炮弹毁伤力")
    }
    health = 2080
    size = 4
    shake = 2f
    recoils=2
    recoil = 3f
    reload = 55f
    range = 408f
    minRange = 80f
    inaccuracy = 8f
    maxAmmo = 60
    targetAir = false
    velocityRnd = 0.2f
    ammoPerShot = 3
    shoot = ShootMulti(ShootAlternate().apply { spread = 8f }, ShootPattern().apply { shots = 5 })
    consumeCoolant(1f)
    ammoEjectBack = 5f
    liquidCapacity = 20f
    coolantMultiplier = 1f
    shootSound = Sounds.shootArtillery
    ammoUseEffect = Fx.casing4

    addAmmoType(IItems.铬锭) {
      ArtilleryBulletType().apply {
        damage = 35f
        lifetime = 80f
        speed = 4.5f
        width = 12f
        height = 12f
        knockback = 1.2f
        splashDamage = 59f
        splashDamageRadius = 25f
      }
    }

    addAmmoType(IItems.单晶硅) {
      ArtilleryBulletType().apply {
        damage = 35f
        lifetime = 80f
        speed = 4.5f
        width = 12f
        height = 12f
        knockback = 1.2f
        homingPower = 0.1f
        homingRange = 60f
        ammoMultiplier = 3f
        reloadMultiplier = 1.2f
        splashDamage = 59f
        splashDamageRadius = 25f
      }
    }

    addAmmoType(IItems.硫化合物) {
      ArtilleryBulletType().apply {
        damage = 43f
        lifetime = 80f
        speed = 4.5f
        width = 14f
        height = 14f
        knockback = 1.2f
        ammoMultiplier = 4f
        splashDamage = 81f
        splashDamageRadius = 25f
        makeFire = true
        incendAmount = 3
        status = StatusEffects.burning
        statusDuration = 720f
        frontColor = Color.valueOf("F8AD42")
        backColor = Color.valueOf("F68021")
        trailEffect = Fx.incendTrail
        hitEffect = Fx.blastExplosion
      }
    }

    addAmmoType(IItems.爆炸化合物) {
      ArtilleryBulletType().apply {
        damage = 35f
        lifetime = 80f
        speed = 3f
        width = 15f
        height = 15f
        knockback = 1.2f
        status = StatusEffects.blasted
        statusDuration = 60f
        splashDamage = 99f
        splashDamageRadius = 45f
        frontColor = Color.valueOf("FFD2AE")
        backColor = Color.valueOf("E58956")
        hitEffect = Fx.blastExplosion
      }
    }

    addAmmoType(IItems.钴钢) {
      ArtilleryBulletType().apply {
        damage = 35f
        lifetime = 80f
        speed = 5.1f
        width = 14f
        height = 14f
        knockback = 1.5f
        frontColor = Color.valueOf("FFFAC6")
        backColor = Color.valueOf("D8D97F")
        splashDamage = 81f
        splashDamageRadius = 35f
        hitEffect = Fx.plasticExplosion
        fragBullets = 8
        fragBullet = ArtilleryBulletType().apply {
          damage = 19f
          speed = 2.5f
          width = 11f
          height = 13f
          shrinkY = 1f
          lifetime = 15f
          frontColor = Color.valueOf("FFFAC6")
          backColor = Color.valueOf("D8D97F")
          hitEffect = Fx.none
          despawnEffect = Fx.none
          collidesAir = false
          splashDamage = 7f
          splashDamageRadius = 4f
        }
      }
    }

    addAmmoType(IItems.铱板) {
      ArtilleryBulletType().apply {
        damage = 59f
        lifetime = 80f
        speed = 4.5f
        width = 15f
        height = 15f
        knockback = 1.2f
        inaccuracy = -6f
        ammoMultiplier = 3f
        reloadMultiplier = 0.9f
        status = IStatus.破甲I
        statusDuration = 120f
        splashDamage = 83f
        splashDamageRadius = 19f
        hitEffect = Fx.flakExplosion
      }
    }

    addAmmoType(IItems.铈凝块) {
      ArtilleryBulletType().apply {
        damage = 53f
        lifetime = 80f
        speed = 4.5f
        width = 14f
        height = 14f
        knockback = 1.2f
        inaccuracy = -3f
        makeFire = true
        incendAmount = 7
        status = IStatus.蚀骨
        statusDuration = 600f
        frontColor = Color.valueOf("BFC8E2")
        backColor = Color.valueOf("929DB5")
        ammoMultiplier = 4f
        splashDamage = 95f
        splashDamageRadius = 25f
        hitEffect = Fx.blastExplosion
      }
    }

    requirements(Category.turret, IItems.铜锭, 1360, IItems.铬锭, 300, IItems.钍锭, 325, IItems.铱板, 225, IItems.钴钢, 180)

    drawer = DrawTurret().apply {
      parts.add(RegionPart("-l").apply {
        under = true
        recoilIndex = 0
        heatColor = Color.valueOf("F03B0E")
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -2f
        })
      })
      parts.add(RegionPart("-r").apply {
        under = true
        recoilIndex = 1
        heatColor = Color.valueOf("F03B0E")
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -2f
        })
      })
    }
  }
}
