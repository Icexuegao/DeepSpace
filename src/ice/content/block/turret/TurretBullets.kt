package ice.content.block.turret

import arc.func.Cons
import arc.func.Cons2
import arc.func.Func
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.util.Time
import arc.util.Tmp
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Damage
import mindustry.entities.Units
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.effect.MultiEffect
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.world.blocks.defense.turrets.ItemTurret
import singularity.Sgl
import singularity.contents.OtherContents
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.particles.SglParticleModels
import universecore.util.funcs.Floatp2
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.LightningVertex
import universecore.world.lightnings.generator.LightningGenerator
import universecore.world.lightnings.generator.RandomGenerator
import kotlin.math.min

object TurretBullets {
  val rand = Rand()
  var crushedIce = object : BulletType() {
    init {
      lifetime = 45f
      hitColor = SglDrawConst.frost
      hitEffect = SglFx.railShootRecoil
      damage = 18f
      speed = 2f
      collidesGround = true
      collidesAir = false
      pierceCap = 1
      hitSize = 2f
    }

    override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
      if (entity is Healthc) {
        entity.damage(b.damage)
      }

      if (entity is Unit) {
        entity.apply(OtherContents.frost, entity.getDuration(OtherContents.frost) + 6)
      }
    }

    override fun draw(b: Bullet) {
      super.draw(b)

      Draw.color(SglDrawConst.frost)
      SglDraw.drawDiamond(b.x, b.y, 6 * b.fout(), 3 * b.fout(), b.rotation())
    }
  }

  /**极寒领域 */
  var freezingField = object : BulletType() {
    init {
      lifetime = 600f
      hittable = false
      pierce = true
      absorbable = false
      collides = false
      despawnEffect = Fx.none
      hitEffect = Fx.none
      drawSize = 200f
    }

    override fun update(b: Bullet) {
      super.update(b)
      val radius = 200 * b.fout()
      Damage.damage(b.team, b.x, b.y, radius, 12 * Time.delta)

      Vars.control.sound.loop(Sounds.wind, b, 2f)

      if (Mathf.chanceDelta((0.075f * b.fout()).toDouble())) {
        SglFx.particleSpread.at(b.x, b.y, SglDrawConst.winter)
      }

      if (Mathf.chanceDelta((0.25f * b.fout(Interp.pow2Out)).toDouble())) {
        Angles.randLenVectors(Time.time.toLong(), 1, radius) { dx: Float, dy: Float ->
          if (Mathf.chanceDelta(0.7)) {
            SglFx.iceParticle.at(b.x + dx, b.y + dy, (-45 + Mathf.random(-15, 15)).toFloat(), SglDrawConst.frost)
          } else {
            SglFx.iceCrystal.at(b.x + dx, b.y + dy, SglDrawConst.frost)
          }
        }
      }

      Units.nearbyEnemies(b.team, b.x, b.y, radius, Cons { unit: Unit? ->
        unit!!.apply(OtherContents.frost, unit.getDuration(OtherContents.frost) + 2f * Time.delta)
      })
    }

    override fun draw(b: Bullet) {
      super.draw(b)
      Draw.z(Layer.flyingUnit + 0.01f)
      Draw.color(SglDrawConst.winter)

      Draw.alpha(0f)
      val lerp = if (b.fin() <= 0.1f) 1 - Mathf.pow(1 - Mathf.clamp(b.fin() / 0.1f), 2f) else Mathf.clamp(b.fout() / 0.9f)
      SglDraw.gradientCircle(b.x, b.y, 215 * lerp, 0.8f)

      Draw.z(Layer.effect)
      Draw.alpha(1f)
      Lines.stroke(2 * lerp)
      SglDraw.dashCircle(b.x, b.y, 200 * b.fout(), 12, 180f, Time.time)
    }
  }

  @JvmField
  var 破碎FEX结晶 = object : BulletType() {
    init {
      lifetime = 60f
      hitColor = SglDrawConst.fexCrystal
      hitEffect = SglFx.railShootRecoil
      damage = 48f
      speed = 3.5f
      collidesGround = true
      collidesAir = true
      pierceCap = 2
      hitSize = 2.2f

      trailColor = SglDrawConst.fexCrystal
      trailEffect = SglFx.trailLine
      trailInterval = 3f
      trailRotation = true

      homingRange = 130f
      homingPower = 0.065f
    }

    override fun update(b: Bullet) {
      super.update(b)
      b.vel.x = Mathf.lerpDelta(b.vel.x, 0f, 0.025f)
      b.vel.y = Mathf.lerpDelta(b.vel.y, 0f, 0.025f)
    }

    override fun draw(b: Bullet) {
      drawTrail(b)

      Draw.color(SglDrawConst.fexCrystal)
      SglDraw.drawDiamond(b.x, b.y, 8.6f, 4.4f, b.rotation())
    }
  }

  @JvmField
  var 溢出能量 = object : BulletType() {
    init {
      collides = false
      absorbable = false

      splashDamage = 120f
      splashDamageRadius = 40f
      speed = 4.4f
      lifetime = 64f

      hitShake = 4f
      hitSize = 3f

      despawnHit = true
      hitEffect = MultiEffect(
        SglFx.explodeImpWaveSmall, SglFx.diamondSpark
      )
      hitColor = SglDrawConst.matrixNet

      trailColor = SglDrawConst.matrixNet
      trailEffect = SglFx.movingCrystalFrag
      trailRotation = true
      trailInterval = 4f

      fragBullet = object : LightningBulletType() {
        init {
          lightningLength = 14
          lightningLengthRand = 4
          damage = 24f
        }
      }
      fragBullets = 1
    }

    override fun update(b: Bullet) {
      super.update(b)

      b.vel.lerp(0f, 0f, 0.012f)

      if (b.timer(4, 3f)) {
        Angles.randLenVectors(
          System.nanoTime(), 2, 2.2f
        ) { x: Float, y: Float -> SglParticleModels.floatParticle.create(b.x, b.y, SglDrawConst.matrixNet, x, y, 2.2f).strength = 0.3f }
      }
    }

    override fun draw(b: Bullet) {
      Draw.color(hitColor)
      val fout = b.fout(Interp.pow3Out)
      Fill.circle(b.x, b.y, 5f * fout)
      Draw.color(Color.black)
      Fill.circle(b.x, b.y, 2.6f * fout)
    }
  }
  val branch = RandomGenerator()
  fun lightning(lifeTime: Float, damage: Float, size: Float, color: Color?, gradient: Boolean, generator: Func<Bullet?, LightningGenerator>): BulletType {
    return lightning(lifeTime, if (gradient) lifeTime / 2 else 0f, damage, size, color, generator)
  }

  @JvmStatic
  fun lightning(lifeTime: Float, time: Float, damage: Float, size: Float, color: Color?, generator: Func<Bullet?, LightningGenerator>): BulletType {
    return object : singularity.world.blocks.turrets.LightningBulletType(0f, damage) {
      init {
        lifetime = lifeTime
        collides = false
        hittable = false
        absorbable = false
        reflectable = false

        hitColor = color
        hitEffect = Fx.hitLancer
        shootEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none

        status = StatusEffects.shocked
        statusDuration = 18f

        drawSize = 120f
      }

      override fun init(b: Bullet, container: LightningContainer) {
        container.time = time
        container.lifeTime = lifeTime
        container.maxWidth = size
        container.minWidth = size * 0.85f
        container.lerp = Interp.linear

        container.trigger = Cons2 { last: LightningVertex?, vert: LightningVertex? ->
          if (!b.isAdded) return@Cons2
          Tmp.v1.set(vert!!.x - last!!.x, vert.y - last.y)
          val resultLength = Damage.findPierceLength(b, pierceCap, Tmp.v1.len())

          Damage.collideLine(b, b.team, b.x + last.x, b.y + last.y, Tmp.v1.angle(), resultLength, false, false, pierceCap)
          b.fdata = resultLength
        }
        val gen: LightningGenerator = generator.get(b)
        gen.blockNow = Floatp2 { last, vertex ->
          val abs = Damage.findAbsorber(b.team, b.x + last.x, b.y + last.y, b.x + vertex.x, b.y + vertex.y) ?: return@Floatp2 -1f
          val ox: Float = b.x + last.x
          val oy: Float = b.y + last.y
          Mathf.len(abs.x - ox, abs.y - oy)
        }
        container.create(gen)
      }
    }
  }

  fun graphiteCloud(lifeTime: Float, size: Float, air: Boolean, ground: Boolean, empDamage: Float): mindustry.entities.bullet.BulletType {
    return object : BulletType(0f, 0f) {
      init {
        lifetime = lifeTime
        collides = false
        pierce = true
        hittable = false
        absorbable = false
        hitEffect = Fx.none
        shootEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none
        drawSize = size
      }

      override fun update(b: Bullet) {
        super.update(b)
        if (empDamage > 0) Units.nearbyEnemies(b.team, b.x, b.y, size, Cons { u: Unit? -> Sgl.empHealth.empDamage(u, empDamage, false) })
        if (b.timer(0, 6f)) {
          Damage.status(b.team, b.x, b.y, size, IStatus.电子干扰, min(lifeTime - b.time, 120f), air, ground)
        }
      }

      override fun draw(e: Bullet) {
        Draw.z(Layer.bullet - 5)
        Draw.color(Pal.stoneGray)
        Draw.alpha(0.6f)
        rand.setSeed(e.id.toLong())
        Angles.randLenVectors(e.id.toLong(), 8 + size.toInt() / 2, size * 1.2f) { x: Float, y: Float ->
          val size = rand.random(14, 20).toFloat()
          val i = e.fin(Interp.pow3Out)
          Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out))
        }
      }
    }
  }

  fun ItemTurret.addAmmoType(item: Item, bullet: () -> BulletType) {
    ammoTypes.put(item, bullet())
  }
}