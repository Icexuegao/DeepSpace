package ice.content.unit.flying

import arc.Core
import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.util.Time
import ice.content.IStatus
import ice.entities.bullet.MissileBulletType
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.base.Entity
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.type.Weapon

class 雨燕 :IceUnitType("unit_rainFly", EntityC::class.java) {
  init {
    localization {
      zh_CN {
        localizedName = "雨燕"
        description = "特快机动单位,轻量化装甲赋予了她无与伦比的机动性\n使用蜂群导弹及制导火箭弹攻击敌人"
      }
    }
    flying = true
    lowAltitude = true
    health = 18000f
    armor = 11f
    hitSize = 40f
    speed = 6f
    accel = 0.04f
    drag = 0.016f
    rotateSpeed = 3f
    engineOffset = -12f
    setWeapon()
  }

  fun setWeapon() {
    setWeapon("missile") {
      x = 19f
      y = 13.75f
      recoil = 1f
      shake = 1f
      reload = 150f
      shoot = ShootPattern().apply {
        shots = 6
        shotDelay = 3f
      }
      shootCone = 5f
      inaccuracy = 5f
      rotate = true
      rotateSpeed = 3f
      rotationLimit = 45f
      layerOffset = -0.001f
      ejectEffect = Fx.casing1
      shootSound = Sounds.shootMissile
      bullet = MissileBulletType(4f, 45f).apply {
        lifetime = 60f
        drag = -0.01f
        width = 8f
        height = 8f
        shrinkY = 0f
        knockback = 0.4f
        status = StatusEffects.blasted
        keepVelocity = false
        splashDamage = 115f
        splashDamageRadius = 16f
        hitEffect = Fx.blastExplosion
        despawnEffect = Fx.blastExplosion
      }
    }.copyAdd {
      x = 26.5f
      y = 10f
    }
    fun dw(weapon: Weapon) {
      weapon.apply {
        shoot = ShootPattern().apply {
          shots = 2
          shotDelay = 3f
        }
        shootCone = 5f
        inaccuracy = 5f
        rotate = true
        rotateSpeed = 3f
        rotationLimit = 45f
        layerOffset = -0.001f
        shootSound = Sounds.shootMissile
      }
    }
    setWeapon("missile") {
      x = 9f
      y = 30f
      recoil = 1f
      shake = 1f
      reload = 20f
      dw(this)
      ejectEffect = Fx.casing1
      bullet = MissileBulletType(4f, 15f).apply {
        lifetime = 60f
        drag = -0.01f
        width = 8f
        height = 8f
        shrinkY = 0f
        weaveMag = 1f
        weaveScale = 8f
        knockback = 0.4f
        status = StatusEffects.blasted
        keepVelocity = false
        splashDamage = 65f
        splashDamageRadius = 16f
        hitEffect = Fx.blastExplosion
        despawnEffect = Fx.blastExplosion
      }
    }

    setWeapon("missile") {
      x = 42f
      y = 5f
      recoil = 1f
      shake = 1f
      reload = 240f
      dw(this)
      ejectEffect = Fx.casing3
      bullet = MissileBulletType(6f, 85f, "missile-large").apply {
        lifetime = 45f
        drag = -0.01f
        recoil = 0.4f
        width = 16f
        height = 16f
        shrinkY = 0f
        knockback = 0.4f
        keepVelocity = false
        status = IStatus.熔融
        statusDuration = 60f
        splashDamage = 445f
        splashDamageRadius = 32f
        hitEffect = Fx.blastExplosion
        despawnEffect = Fx.blastExplosion
      }
    }
  }

  private class EntityC :Entity() {

    override fun drawBodyRegion(rotation: Float) {
      super.drawBodyRegion(rotation)
      Draw.alpha(0.7f)
      drawRotator(this, this.type.name + "-mainPropeller", 0f, 12f, 2f, 64f)
      drawRotator(this, this.type.name + "-vicePropeller", 32f, -5f, 2f, 30f, true)
      Draw.alpha(1f)
    }

    override fun drawShadowRegion(shadowX: Float, shadowY: Float, rotation: Float) {
      super.drawShadowRegion(shadowX, shadowY, rotation)
      drawRotatorShadow(this, this.type.name + "-mainPropeller", 0f, 12f, shadowX, shadowY, 2f, 64f)
      drawRotatorShadow(this, this.type.name + "-vicePropeller", 32f, -5f, shadowX, shadowY, 2f, 30f, true)
    }

    fun drawRotatorShadow(
      unit: Unit, name: String, x: Float, y: Float, shadowX: Float, shadowY: Float, speed: Float, s: Float, mirror: Boolean = false
    ) {
      val rot = unit.rotation - 90f
      val ux = unit.x + Angles.trnsx(rot, x, y) + shadowX
      val uy = unit.y + Angles.trnsy(rot, x, y) + shadowY
      val speedRot = Time.time * speed * 6f


      Draw.rect(Core.atlas.find(name), ux, uy, s, s, speedRot)
      Draw.rect(Core.atlas.find("$name-top"), ux, uy, rot)

      if (mirror) {
        val nx = unit.x + Angles.trnsx(rot, -x, y) + shadowX
        val ny = unit.y + Angles.trnsy(rot, -x, y) + shadowY
        Draw.rect(Core.atlas.find(name), nx, ny, s, s, -speedRot)
        Draw.rect(Core.atlas.find("$name-top"), nx, ny, rot)
      }
    }

    fun drawRotator(unit: Unit, name: String, x: Float, y: Float, speed: Float, s: Float, mirror: Boolean = false) {
      val rot = unit.rotation - 90f
      val ux = unit.x + Angles.trnsx(rot, x, y)
      val uy = unit.y + Angles.trnsy(rot, x, y)
      val speedRot = Time.time * speed * 6f


      Draw.rect(Core.atlas.find(name), ux, uy, s, s, speedRot)
      Draw.rect(Core.atlas.find("$name-top"), ux, uy, rot)

      if (mirror) {
        val nx = unit.x + Angles.trnsx(rot, -x, y)
        val ny = unit.y + Angles.trnsy(rot, -x, y)
        Draw.rect(Core.atlas.find(name), nx, ny, s, s, -speedRot)
        Draw.rect(Core.atlas.find("$name-top"), nx, ny, rot)
      }
    }
  }

}