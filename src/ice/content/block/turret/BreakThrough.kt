package ice.content.block.turret

import ice.content.IItems
import ice.content.block.turret.TurretBullets.addAmmoType
import ice.entities.bullet.RailBulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.UnitSorts
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.consumers.ConsumeCoolant
import mindustry.world.draw.DrawTurret

class BreakThrough:ItemTurret("breakThrough"){
  init{
    health = 5600
    size = 5
    range = 720f
    reload = 300f
    shake = 5f
    recoil = 4f
    maxAmmo = 60
    recoilTime = 300f
    ammoPerShot = 15
    cooldownTime = 300f
    shootSound = Sounds.shootToxopidShotgun
    unitSort = UnitSorts.strongest
    shootCone = 2f
    rotateSpeed = 1.4f
    minWarmup = 0.96f
    shootWarmupSpeed = 0.08f
    warmupMaintainTime = 300f
    shoot = ShootSpread().apply {
      shots = 5
      spread = 1f
    }
    liquidCapacity = 30f
    consumePower(25f)
    consume(ConsumeCoolant(1.5f))
    coolantMultiplier = 0.333f
    BaseBundle.bundle {
      desc(zh_CN, "冲穿", "以临界速度发射五道远程穿透磁轨炮摧毁敌人,比裂颅更强")
    }
    addAmmoType(IItems.钍锭) {
      RailBulletType().apply {
        damage = 720f
        knockback = 10f
        lifetime = 30f
        length = 720f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.4f
        status = StatusEffects.unmoving
        statusDuration = 900f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
      }
    }
    addAmmoType(IItems.金锭) {
      RailBulletType().apply {
        damage = 1200f
        knockback = 20f
        lifetime = 30f
        length = 720f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.2f
        status = StatusEffects.melting
        statusDuration = 900f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
      }
    }
    addAmmoType(IItems.铱板) {
      RailBulletType().apply {
        damage = 960f
        knockback = 15f
        lifetime = 30f
        length = 720f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.3f
        status = StatusEffects.burning
        statusDuration = 900f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
        pierceEffect = ParticleEffect().apply {
          line = true
          particles = 60
          offset = 0f
          lifetime = 15f
          length = 40f
          cone = -7.5f
          lenFrom = 6f
          lenTo = 0f
          colorFrom = "D86E56".toColor()
          colorTo = "FFFFFF".toColor()
        }
      }
    }
    requirements(Category.turret, IItems.铜锭, 1800, IItems.钴锭, 840, IItems.铱板, 630, IItems.导能回路, 435, IItems.陶钢, 225)
    drawer = DrawTurret().apply {
      parts.addAll(RegionPart("-barrel").apply {
        moveY = -1.5f
        moves.add(DrawPart.PartMove(DrawPart.PartProgress.recoil, 0f, -3f, 0f))
        heatColor = "F03B0E".toColor()
      }, RegionPart("-top").apply {
        heatProgress = DrawPart.PartProgress.warmup
        mirror = true
        under = true
        moveX = 4f
        moveY = 8.25f
        heatColor = "F03B0E".toColor()
      }, RegionPart("-side").apply {
        heatProgress = DrawPart.PartProgress.warmup
        mirror = true
        under = true
        moveX = 4f
        moveY = 0f
        heatColor = "F03B0E".toColor()
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapes = 2
        radius = 0f
        radiusTo = 3f
        triLength = 16f
        haloRadius = 15f
        haloRotation = 90f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapeRotation = 180f
        shapes = 2
        radius = 0f
        radiusTo = 3f
        triLength = 4f
        haloRadius = 15f
        haloRotation = 90f
        color = "FF5845".toColor()
        layer = 110f
      }, ShapePart().apply {
        y = -16f
        circle = true
        hollow = true
        radius = 4f
        stroke = 0f
        strokeTo = 1f
        color = "FF5845".toColor()
        layer = 110f
      }, ShapePart().apply {
        y = -16f
        circle = true
        hollow = true
        radius = 8f
        stroke = 0f
        strokeTo = 1f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        hollow = false
        mirror = false
        tri = true
        x = 0f
        y = -16f
        shapeRotation = 0f
        moveX = 0f
        moveY = 0f
        shapeMoveRot = 0f
        shapes = 4
        sides = 5
        radius = 0f
        radiusTo = 3f
        stroke = 1f
        strokeTo = -1f
        triLength = 6f
        triLengthTo = -1f
        haloRadius = 12f
        haloRadiusTo = -1f
        haloRotateSpeed = 1f
        haloRotation = 0f
        rotateSpeed = 0f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapeRotation = 180f
        shapes = 4
        radius = 0f
        radiusTo = 3f
        triLength = 2f
        haloRadius = 12f
        haloRotateSpeed = 1f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapes = 4
        radius = 3f
        triLength = 0f
        triLengthTo = 2f
        haloRadius = 8f
        haloRotateSpeed = -1f
        color = "FF5845".toColor()
        layer = 110f
      })
    }
  }
}