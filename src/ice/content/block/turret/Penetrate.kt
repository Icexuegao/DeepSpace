package ice.content.block.turret

import arc.graphics.Color
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.addAmmoType
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.bullet.BulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.consumers.ConsumeCoolant

class Penetrate : ItemTurret("turret_penetrate") {
  init {
    BaseBundle.bundle {
      desc(zh_CN,"贯通","向指定方位发射一道强劲的定向爆破束")
    }
    health = 1930
    armor = 4f
    size = 3
    recoil = 3f
    shootY = 3f
    shake = 3.5f
    range = 200f
    reload = 120f
    shootCone = 5f
    maxAmmo = 24
    recoilTime = 40f
    cooldownTime = 90f
    consume(ConsumeCoolant(0.3f))
    rotateSpeed = 5f
    ammoPerShot = 6
    coolantMultiplier = 2.5f
    shootSound = Sounds.shootArtillery
    shootEffect = Fx.bigShockwave
    ammoUseEffect = Fx.casing3Double

    // Graphite ammo
    addAmmoType(IItems.铬锭) {
      BasicBulletType().apply {
        damage = 135f
        lifetime = 20f
        speed = 10f
        width = 14f
        height = 25f
        collides = false
        pierce = true
        pierceBuilding = true
        bulletInterval = 1f
        intervalBullet = BulletType().apply {
          hitShake = 2f
          despawnShake = 1f
          splashDamage = 45f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }
        despawnEffect = ParticleEffect().apply {
          particles = 15
          line = true
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 10f
          lenTo = 0f
          length = 50f
          lifetime = 10f
          colorFrom = Color.valueOf("FFE176")
          colorTo = Color.white
          cone = 60f
        }
      }
    }

    // Titanium ammo
    addAmmoType(IItems.钴锭) {
      BasicBulletType().apply {
        damage = 185f
        lifetime = 20f
        speed = 10f
        width = 14f
        height = 25f
        ammoMultiplier = 1f
        reloadMultiplier = 1.4f
        collides = false
        pierce = true
        pierceBuilding = true
        bulletInterval = 1f
        intervalBullet = BulletType().apply {
          hitShake = 2f
          despawnShake = 1f
          splashDamage = 62f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }
        despawnEffect = ParticleEffect().apply {
          particles = 15
          line = true
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 10f
          lenTo = 0f
          length = 50f
          lifetime = 10f
          colorFrom = Color.valueOf("FFE176")
          colorTo = Color.white
          cone = 60f
        }
      }
    }

    // Thorium ammo
    addAmmoType(IItems.钍锭) {
      BasicBulletType().apply {
        damage = 235f
        lifetime = 27f
        speed = 10f
        width = 16f
        height = 25f
        rangeChange = 70f
        collides = false
        pierce = true
        pierceBuilding = true
        bulletInterval = 1f
        intervalBullet = BulletType().apply {
          hitShake = 2f
          despawnShake = 1f
          status = IStatus.辐射
          statusDuration = 60f
          splashDamage = 78f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }
        despawnEffect = ParticleEffect().apply {
          particles = 15
          line = true
          strokeFrom = 4f
          strokeTo = 0f
          lenFrom = 10f
          lenTo = 0f
          length = 70f
          baseLength = 0f
          lifetime = 10f
          colorFrom = Color.valueOf("FFE176")
          colorTo = Color.white
          cone = 60f
        }
      }
    }

    // Surge-alloy ammo
    addAmmoType(IItems.暮光合金) {
      BasicBulletType().apply {
        damage = 480f
        lifetime = 36f
        speed = 10f
        width = 10f
        height = 25f
        rangeChange = 160f
        ammoMultiplier = 1f
        collides = false
        pierce = true
        pierceBuilding = true
        bulletInterval = 1f
        intervalBullet = BulletType().apply {
          status = StatusEffects.shocked
          splashDamage = 160f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          lightning = 3
          lightningLength = 4
          lightningDamage = 16f
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }
        despawnEffect = ParticleEffect().apply {
          particles = 15
          line = true
          strokeFrom = 5f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 0f
          length = 100f
          lifetime = 10f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.white
          cone = 60f
        }
      }
    }

    // 铱板 ammo
    addAmmoType(IItems.铱板) {
      BasicBulletType().apply {
        damage = 420f
        lifetime = 32f
        speed = 10f
        width = 10f
        height = 25f
        rangeChange = 120f
        ammoMultiplier = 3f
        collides = false
        pierce = true
        pierceBuilding = true
        bulletInterval = 1f
        intervalBullet = BasicBulletType().apply {
          hitShake = 2f
          despawnShake = 1f
          status = IStatus.损毁
          statusDuration = 60f
          splashDamage = 140f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }
        despawnEffect = ParticleEffect().apply {
          particles = 15
          line = true
          strokeFrom = 5f
          strokeTo = 0f
          lenFrom = 16f
          lenTo = 0f
          length = 100f
          lifetime = 10f
          colorFrom = Color.valueOf("FFE176")
          colorTo = Color.white
          cone = 60f
        }
      }
    }

    requirements(Category.turret, IItems.铜锭, 135, IItems.钴锭, 95, IItems.钍锭, 65, IItems.爆炸化合物, 10)
  }
}
