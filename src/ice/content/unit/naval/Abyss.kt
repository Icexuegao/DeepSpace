package ice.content.unit.naval

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import ice.content.IStatus
import ice.entities.bullet.ArtilleryBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.abilities.ShieldRegenFieldAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.UnitWaterMove
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Weapon
import kotlin.math.min

class Abyss : IceUnitType("unit_abyss", UnitWaterMove::class.java) {
  var wave = Effect(50f) { e ->
    Draw.color(e.color.cpy().mul(1.5f))
    Fx.rand.setSeed(e.id.toLong())
    Draw.z(Layer.scorch - 0.01f)
    repeat(3) {
      Fx.v.trns(e.rotation + Fx.rand.range(40), Fx.rand.random(6 * e.finpow()))
      Fill.circle(e.x + Fx.v.x + Fx.rand.range(4), e.y + Fx.v.y + Fx.rand.range(4), min(e.fout(), e.fin() * e.lifetime / 8) * 4 * Fx.rand.random(0.8f, 1.1f) + 0.3f)
    }
  }

  val torpedo = Weapon("${this.name}-torpedoes").apply {
    x = 11.5f
    y = -10.5f
    recoil = 2f
    shootY = 2f
    reload = 300f
    rotate = true
    shootCone = 35f
    rotateSpeed = 4f
    rotationLimit = 20f
    baseRotation = -30f
    shootSound = Sounds.shootMissileLarge
    bullet = object : BasicBulletType() {
      init {
        damage = 850f
        lifetime = 300f
        speed = 0.6f
        width = 9.75f
        height = 15.75f
        shrinkY = 0f
        hitSize = 24f
        drag = -0.008f
        sprite = "鱼雷"
        backSprite = "none"
        homingRange = 720f
        homingPower = 0.02f
        rangeOverride = 480f
        layer = Layer.scorch
        collideFloor = true
        collidesAir = false
        absorbable = false
        keepVelocity = false
        splashDamage = 650f
        splashDamageRadius = 64f
        shootEffect = Fx.none
        smokeEffect = Fx.shootBigSmoke2
        status = StatusEffects.blasted
        hitSound = Sounds.explosionPlasmaSmall
        hitEffect = MultiEffect(Fx.blastExplosion, Fx.flakExplosion)
      }

      override fun update(b: Bullet) {
        super.update(b)
        if (b.tileOn() != null) {
          mixColorTo = b.tileOn().floor().mapColor
          if (b.timer.get(2, 10f)) {
            wave.at(b.x, b.y, b.tileOn().floor().mapColor)
          }
        }
      }
    }
  }

  init {
    BaseBundle.Companion.bundle {
      desc(zh_CN, "沧溟", "重型海栖突击单位.发射炮弹和大型鱼雷并辅以机炮攻击敌人,加装护盾辅助发生器维持友军护盾,同时为附近友军提供反扑效果,对舰攻击能力极强")
    }
    health = 8800f
    armor = 14f
    hitSize = 33f
    drag = 0.16f
    accel = 0.22f
    speed = 0.78f
    trailScl = 2.6f
    rotateSpeed = 2f
    trailLength = 36
    waveTrailX = 13f
    waveTrailY = -13f
    outlineColor = "1F1F1F".toColor()
    setWeapon("main") {
      x = 0f
      shake = 4f
      recoil = 3f
      shootY = 7f
      reload = 90f
      rotate = true
      mirror = false
      shootCone = 20f
      rotateSpeed = 2f
      ejectEffect = Fx.casing3
      shootSound = Sounds.shootArtillery
      bullet = ArtilleryBulletType(5f, 0f).apply {
        lifetime = 84f
        width = 15f
        height = 16f
        trailSize = 6f
        trailMult = 0.8f

        hitShake = 4f
        collidesTiles = false
        splashDamage = 545f
        splashDamageRadius = 40f
        shootEffect = Fx.shootBig2
        status = StatusEffects.blasted
        hitEffect = Fx.massiveExplosion
        trailEffect = Fx.artilleryTrail
        frontColor = Pal.missileYellow
        backColor = Pal.missileYellowBack
      }

    }

    weapons.add(
      gun(-6.75f, 16.25f, -20f), gun(13.5f, 7.75f, 20f), torpedo
    )

    abilities.add(
      ShieldRegenFieldAbility(80f, 800f, 240f, 200f), StatusFieldAbility(IStatus.反扑, 360f, 360f, 160f)
    )
  }

  fun gun(x: Float, y: Float, ang: Float): Weapon {
    return Weapon(this.name + "-cannon").apply {
      this.x = x
      this.y = y
      recoil = 2f
      shootY = 2f
      reload = 36f
      rotate = true
      shootCone = 20f
      rotateSpeed = 4f
      rotationLimit = 120f
      baseRotation = -ang
      shootSound = Sounds.shootBeamPlasma
      bullet = BasicBulletType(5f, 55f).apply {
        lifetime = 48f
        width = 6f
        height = 9f
        splashDamage = 25f
        splashDamageRadius = 16f
        shootEffect = Fx.shootBig
        status = StatusEffects.blasted
        hitEffect = Fx.flakExplosion
      }
    }
  }
}