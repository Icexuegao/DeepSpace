package ice.content.block.turret

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.addAmmoType
import ice.entities.bullet.PointBulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.UnitSorts
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.consumers.ConsumeCoolant

class Shock : ItemTurret("turret_shock") {

  init {
    BaseBundle.bundle {
      desc(zh_CN, "震击", "以相位技术传送炮弹,使其瞬间到达目标点")
    }
    health = 4680
    size = 5
    shake = 5f
    recoil = 4f
    range = 648f
    reload = 900f
    recoilTime = 630f
    cooldownTime = 630f
    shootCone = 1f
    maxAmmo = 50
    ammoPerShot = 10
    shootSound = Sounds.shootReign
    unitSort = UnitSorts.strongest
    rotateSpeed = 1.6f
    liquidCapacity = 30f
    coolantMultiplier = 0.5f
    consumePower(25f)
    consume(ConsumeCoolant(1.5f))
    addAmmoType(IItems.暮光合金) {
      PointBulletType().apply pointBulletType@{
        splashDamage = 2500f
        splashDamageRadius = 40f
        buildingDamageMultiplier = 0.3f
        knockback = 10f
        lifetime = 8f
        speed = 81f
        ammoMultiplier = 1f
        shootEffect = Effect(24f) { e ->
          e.scaled(10f) { b ->
            Draw.color(Color.white, IceColor.b6, b.fin())
            Lines.stroke(b.fout() * 3f + 0.2f)
            Lines.circle(b.x, b.y, b.fin() * 50f)
          }
          Draw.color(IceColor.b6)

          for (i in Mathf.signs) {
            Drawf.tri(e.x, e.y, 13f * e.fout(), 85f, e.rotation + 90f * i)
            Drawf.tri(e.x, e.y, 13f * e.fout(), 50f, e.rotation + 20f * i)
          }
          Drawf.light(e.x, e.y, 180f, IceColor.b6, 0.9f * e.fout())
        }
        smokeEffect = Fx.smokeCloud
        trailEffect = Effect(30f) { e: EffectContainer ->
          for (i in 0..1) {
            Draw.color(if (i == 0) IceColor.b6 else IceColor.b4)

            val m = if (i == 0) 1f else 0.5f

            val rot = e.rotation + 180f
            val w = 15f * e.fout() * m
            Drawf.tri(e.x, e.y, w, (30f + Mathf.randomSeedRange(e.id.toLong(), 15f)) * m, rot)
            Drawf.tri(e.x, e.y, w, 10f * m, rot + 180f)
          }
          Drawf.light(e.x, e.y, 60f, IceColor.b6, 0.6f * e.fout())
        }
        trailSpacing = 20f
        hitEffect = Effect(20f, 200f) { e ->
          Draw.color(IceColor.b6)
          for (i in 0..1) {
            Draw.color(if (i == 0) IceColor.b6 else IceColor.b4)

            val m = if (i == 0) 1f else 0.5f

            for (j in 0..4) {
              val rot = e!!.rotation + Mathf.randomSeedRange((e.id + j).toLong(), 50f)
              val w = 23f * e.fout() * m
              Drawf.tri(e.x, e.y, w, (80f + Mathf.randomSeedRange((e.id + j).toLong(), 40f)) * m, rot)
              Drawf.tri(e.x, e.y, w, 20f * m, rot + 180f)
            }
          }

          e!!.scaled(10f) { c: EffectContainer? ->
            Draw.color(IceColor.b4)
            Lines.stroke(c!!.fout() * 2f + 0.2f)
            Lines.circle(e.x, e.y, c.fin() * 30f)
          }
          e.scaled(12f) { c: EffectContainer? ->
            Draw.color(IceColor.b6)
            Angles.randLenVectors(e.id.toLong(), 25, 5f + e.fin() * 80f, e.rotation, 60f) { x: Float, y: Float ->
              Fill.square(e.x + x, e.y + y, c!!.fout() * 3f, 45f)
            }
          }
        }
        despawnEffect = Effect(15f, 100f) { e: EffectContainer ->
          Draw.color(IceColor.b6)
          Lines.stroke(e.fout() * 4f)
          Lines.circle(e.x, e.y, 4f + e.finpow() * 20f)

          for (i in 0..3) {
            Drawf.tri(e.x, e.y, 6f, 80f * e.fout(), (i * 90 + 45).toFloat())
          }

          Draw.color()
          for (i in 0..3) {
            Drawf.tri(e.x, e.y, 3f, 30f * e.fout(), (i * 90 + 45).toFloat())
          }
          Drawf.light(e.x, e.y, 150f, IceColor.b6, 0.9f * e.fout())
        }
        status = StatusEffects.shocked
        fragBullets = 5
        fragBullet = PointBulletType().apply {
          speed = 8f
          lifetime = 15f
          splashDamage = 75f
          splashDamageRadius = 32f
          despawnEffect = this@pointBulletType.despawnEffect
          trailEffect = this@pointBulletType.trailEffect
          trailSpacing = 20f
          lightning = 3
          lightningDamage = 35f
          lightningLength = 9
        }
      }
    }
    addAmmoType(IItems.生物钢) {
      PointBulletType().apply pointBulletType@{
        splashDamage = 3780f
        splashDamageRadius = 80f
        buildingDamageMultiplier = 0.4f
        knockback = 10f
        lifetime = 8f
        speed = 81f
        ammoMultiplier = 1f
        shootEffect = Effect(24f) { e ->
          e.scaled(10f) { b ->
            Draw.color(Color.white, IceColor.b6, b.fin())
            Lines.stroke(b.fout() * 3f + 0.2f)
            Lines.circle(b.x, b.y, b.fin() * 50f)
          }
          Draw.color(IceColor.b6)

          for (i in Mathf.signs) {
            Drawf.tri(e.x, e.y, 13f * e.fout(), 85f, e.rotation + 90f * i)
            Drawf.tri(e.x, e.y, 13f * e.fout(), 50f, e.rotation + 20f * i)
          }
          Drawf.light(e.x, e.y, 180f, IceColor.b6, 0.9f * e.fout())
        }
        smokeEffect = Fx.smokeCloud
        trailEffect = Effect(30f) { e: EffectContainer ->
          for (i in 0..1) {
            Draw.color(if (i == 0) IceColor.b6 else IceColor.b4)

            val m = if (i == 0) 1f else 0.5f

            val rot = e.rotation + 180f
            val w = 15f * e.fout() * m
            Drawf.tri(e.x, e.y, w, (30f + Mathf.randomSeedRange(e.id.toLong(), 15f)) * m, rot)
            Drawf.tri(e.x, e.y, w, 10f * m, rot + 180f)
          }
          Drawf.light(e.x, e.y, 60f, IceColor.b6, 0.6f * e.fout())
        }
        trailSpacing = 20f
        hitEffect = Effect(20f, 200f) { e ->
          Draw.color(IceColor.b6)
          for (i in 0..1) {
            Draw.color(if (i == 0) IceColor.b6 else IceColor.b4)

            val m = if (i == 0) 1f else 0.5f

            for (j in 0..4) {
              val rot = e!!.rotation + Mathf.randomSeedRange((e.id + j).toLong(), 50f)
              val w = 23f * e.fout() * m
              Drawf.tri(e.x, e.y, w, (80f + Mathf.randomSeedRange((e.id + j).toLong(), 40f)) * m, rot)
              Drawf.tri(e.x, e.y, w, 20f * m, rot + 180f)
            }
          }

          e!!.scaled(10f) { c: EffectContainer? ->
            Draw.color(IceColor.b4)
            Lines.stroke(c!!.fout() * 2f + 0.2f)
            Lines.circle(e.x, e.y, c.fin() * 30f)
          }
          e.scaled(12f) { c: EffectContainer? ->
            Draw.color(IceColor.b6)
            Angles.randLenVectors(e.id.toLong(), 25, 5f + e.fin() * 80f, e.rotation, 60f) { x: Float, y: Float ->
              Fill.square(e.x + x, e.y + y, c!!.fout() * 3f, 45f)
            }
          }
        }
        despawnEffect = Effect(15f, 100f) { e: EffectContainer ->
          Draw.color(IceColor.b6)
          Lines.stroke(e.fout() * 4f)
          Lines.circle(e.x, e.y, 4f + e.finpow() * 20f)

          for (i in 0..3) {
            Drawf.tri(e.x, e.y, 6f, 80f * e.fout(), (i * 90 + 45).toFloat())
          }

          Draw.color()
          for (i in 0..3) {
            Drawf.tri(e.x, e.y, 3f, 30f * e.fout(), (i * 90 + 45).toFloat())
          }
          Drawf.light(e.x, e.y, 150f, IceColor.b6, 0.9f * e.fout())
        }
        status = IStatus.熔融
        statusDuration = 300f
        fragBullets = 10
        fragBullet = PointBulletType().apply {
          speed = 8f
          lifetime = 15f
          splashDamage = 75f
          splashDamageRadius = 40f
          status = IStatus.熔融
          statusDuration = 60f
          trailEffect = this@pointBulletType.trailEffect
          trailSpacing = 20f
          despawnEffect = MultiEffect(ParticleEffect().apply {
            particles = 1
            sizeFrom = 2f
            sizeTo = 15f
            length = 0f
            spin = 10f
            layer = 109f
            lifetime = 10f
            region = "star".appendModName()
            colorFrom = Color.valueOf("D75B6E")
            colorTo = Color.valueOf("D75B6E")
          }, WaveEffect().apply {
            lifetime = 10f
            sizeFrom = 0f
            sizeTo = 20f
            strokeFrom = 3f
            strokeTo = 0f
            colorFrom = Color.valueOf("D75B6E")
            colorTo = Color.valueOf("D75B6E")
          })
        }
      }
    }

    requirements(Category.turret, IItems.铜锭, 1600, IItems.石英玻璃, 550, IItems.钍锭, 725, IItems.钴钢, 550, IItems.暮光合金, 325)
  }
}