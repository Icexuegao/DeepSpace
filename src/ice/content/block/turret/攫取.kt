package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.entities.bullet.SapBulletType
import mindustry.content.StatusEffects
import mindustry.entities.effect.ParticleEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret

class 攫取 :SglTurret("turret_grab") {
  init {
    localization {
      zh_CN {
        localizedName = "攫取"
        description = "快速发射汲取光束,攻击敌人的同时修复自身"
      }
    }
    health = 600
    recoil = 1f
    range = 120f
    rotateSpeed = 5f
    outlineRadius = 3
    shootSound = Sounds.shootSpectre
    requirements(Category.turret, IItems.钍锭, 40, IItems.铬锭, 30, IItems.单晶硅, 20)
  }

  override fun setAmmo() {
    newAmmo(SapBulletType().apply {
      damage = 20f
      length = 128f
      shootEffect = ParticleEffect().apply {
        particles = 4
        lifetime = 20f
        line = true
        strokeFrom = 3f
        strokeTo = 0f
        lenFrom = 6f
        lenTo = 6f
        cone = 30f
        length = 45f
        interp = Interp.fastSlow
        sizeInterp = Interp.slowFast
        lightColor = Color.valueOf("BF92F9")
        colorFrom = Color.valueOf("BF92F9")
        colorTo = Color.valueOf("BF92F9")
      }
      color = Color.valueOf("BF92F9")
      hitColor = Color.valueOf("BF92F9")
      knockback = -5f
      sapStrength = 1.5f
      ammoMultiplier = 1f
      status = StatusEffects.sapped
      statusDuration = 30f
    })
    consume?.apply {
      time(10f)
      power(1.5f)
    }
    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)
  }
}