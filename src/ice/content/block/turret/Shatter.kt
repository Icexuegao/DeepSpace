package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.pattern.ShootAlternate
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.consumers.ConsumeCoolant

class Shatter : ItemTurret("turret_shatter") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "碎爆","新式炮台,可兼容各种弹药")
    }

    health = 1280
    size = 3
    shake = 1f
    reload = 15f
    range = 288f
    inaccuracy = 5f
    shootCone = 20f
    rotateSpeed = 8f
    maxAmmo = 60
    liquidCapacity = 30f
    coolantMultiplier = 4f
    shootSound = Sounds.shootSpectre
    shoot = ShootAlternate().apply {
      shots = 4
      spread = 8f
      shotDelay = 5f
    }
    consume(ConsumeCoolant(0.3f))

    ammo(
      IItems.铬锭, BasicBulletType(6f, 9f).apply {
        lifetime = 50f
        width = 6f
        height = 8f
        scaleLife = true
        ammoMultiplier = 3f
        reloadMultiplier = 1.2f
        splashDamage = 48f
        splashDamageRadius = 24f
        shootEffect = Fx.shootSmall
        hitEffect = Fx.flakExplosion
        fragBullets = 4
        fragBullet = BasicBulletType(3f, 4f).apply {
          width = 3f
          height = 4f
          shrinkY = 0f
          lifetime = 20f
          splashDamage = 4f
          splashDamageRadius = 4f
          hitEffect = Fx.flakExplosion
        }
      },

      IItems.钍锭, BasicBulletType(5f, 11f).apply {
        lifetime = 58f
        width = 6f
        height = 8f
        pierceCap = 2
        knockback = 1.2f
        status = IStatus.辐射
        statusDuration = 60f
        splashDamage = 8f
        splashDamageRadius = 11f
        shootEffect = Fx.shootSmall
        hitEffect = Fx.flakExplosion
        fragBullets = 2
        fragBullet = BasicBulletType(3f, 5f).apply {
          width = 3f
          height = 4f
          shrinkY = 0f
          lifetime = 20f
          status = IStatus.辐射
          statusDuration = 60f
          splashDamage = 4f
          splashDamageRadius = 4f
          hitEffect = Fx.flakExplosion
        }
      },

      IItems.单晶硅, BasicBulletType(4.5f, 7f).apply {
        lifetime = 66f
        width = 6f
        height = 8f
        homingPower = 0.12f
        homingRange = 89.6f
        reloadMultiplier = 1.5f
        ammoMultiplier = 5f
        splashDamage = 23f
        splashDamageRadius = 24f
        shootEffect = Fx.shootSmall
        hitEffect = Fx.flakExplosion
      },

      IItems.铱板, BasicBulletType(4f, 8f).apply {
        lifetime = 74f
        width = 6f
        height = 8f
        scaleLife = true
        ammoMultiplier = 4f
        reloadMultiplier = 0.8f
        status = IStatus.损毁
        statusDuration = 60f
        splashDamage = 4f
        splashDamageRadius = 8f
        shootEffect = Fx.shootSmall
        hitEffect = Fx.flakExplosion
        fragBullets = 4
        fragBullet = BasicBulletType(3f, 0f).apply {
          width = 3f
          height = 4f
          shrinkY = 0f
          lifetime = 20f
          status = IStatus.损毁
          statusDuration = 30f
          frontColor = Color.valueOf("FFFFFF")
          backColor = Color.valueOf("454545")
          splashDamage = 1f
          splashDamageRadius = 20f
          hitEffect = Fx.flakExplosion
        }
      },

      IItems.低温化合物, BasicBulletType(4f, 6f).apply {
        lifetime = 74f
        width = 6f
        height = 8f
        scaleLife = true
        ammoMultiplier = 2f
        reloadMultiplier = 0.4f
        shootEffect = Fx.shootSmall
        status = IStatus.封冻
        statusDuration = 30f
        splashDamage = 3f
        splashDamageRadius = 6f
        hitEffect = ParticleEffect().apply {
          region = "crystal".appendModName()
          particles = 3
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 30f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("87CEEB")
          colorTo = Color.valueOf("C0ECFF")
        }
        despawnEffect = ExplosionEffect().apply {
          sparkColor = Color.valueOf("87CEEB")
          smokeColor = Color.valueOf("6D90BC")
          waveColor = Color.valueOf("C0ECFF")
          waveStroke = 4f
          waveRad = 16f
          waveLife = 15f
          sparks = 5
          sparkRad = 16f
          sparkLen = 5f
          sparkStroke = 4f
        }
      },

      IItems.铈凝块, BasicBulletType(4.3f, 5f).apply {
        lifetime = 70f
        width = 6f
        height = 8f
        scaleLife = true
        ammoMultiplier = 3f
        frontColor = Color.valueOf("FFB90F")
        backColor = Color.valueOf("FF8C00")
        splashDamage = 21f
        splashDamageRadius = 32f
        shootEffect = Fx.shootSmall
        hitEffect = Fx.flakExplosion
        fragBullets = 3
        fragBullet = BasicBulletType(3f, 2f).apply {
          width = 3f
          height = 4f
          shrinkY = 0f
          lifetime = 20f
          makeFire = true
          incendAmount = 1
          status = IStatus.蚀骨
          statusDuration = 180f
          frontColor = Color.valueOf("FFB90F")
          backColor = Color.valueOf("FF8C00")
          splashDamage = 3f
          splashDamageRadius = 20f
          hitEffect = MultiEffect(
            Fx.burning, Fx.lava, Fx.fire
          )
        }
      })

    requirements(
      Category.turret, IItems.铜锭, 225, IItems.钍锭, 155, IItems.钴钢, 55,
    )
  }
}
