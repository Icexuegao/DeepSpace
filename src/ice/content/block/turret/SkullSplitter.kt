package ice.content.block.turret

import arc.graphics.Color
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.addAmmoType
import ice.entities.bullet.RailBulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.UnitSorts
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.*
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.draw.DrawTurret

class SkullSplitter : ItemTurret("turret_skullSplitter") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "裂颅", "以临界速度发射三道远程穿透磁轨炮摧毁敌人,能够扫除一切障碍")
    }
    health = 3600
    size = 4
    range = 600f
    reload = 300f
    recoil = 6f
    recoilTime = 330f
    cooldownTime = 420f
    shootCone = 2f
    shoot = ShootSpread().apply {
      shots = 3
      spread = 1f
    }
    shake = 3f
    rotateSpeed = 1.4f
    liquidCapacity = 30f
    consumePower(16f)
    consumeCoolant(1f)
    coolantMultiplier = 0.5f
    ammoPerShot = 15
    unitSort = UnitSorts.strongest
    shootSound = Sounds.shootReign
    minWarmup = 0.95f
    shootWarmupSpeed = 0.08f
    warmupMaintainTime = 300f

    addAmmoType(IItems.石墨烯) {
      RailBulletType().apply {
        damage = 480f
        knockback = 10f
        lifetime = 30f
        length = 600f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.6f
        status = StatusEffects.unmoving
        statusDuration = 600f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
      }
    }

    addAmmoType(IItems.暮光合金) {
      RailBulletType().apply {
        damage = 840f
        knockback = 20f
        lifetime = 30f
        length = 600f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.4f
        status = IStatus.熔融
        statusDuration = 600f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
      }
    }

    addAmmoType(IItems.铱板) {
      RailBulletType().apply {
        damage = 630f
        knockback = 15f
        lifetime = 30f
        length = 600f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.5f
        status = IStatus.破甲I
        statusDuration = 600f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
        pierceEffect = ParticleEffect().apply {
          line = true
          particles = 120
          offset = 0f
          lifetime = 15f
          length = 40f
          cone = -7.5f
          lenFrom = 6f
          lenTo = 0f
          colorFrom = "D86E56".toColor()
          colorTo = Color.white
        }
      }
    }

    requirements(
      Category.turret, IItems.铜锭, 1080, IItems.钍锭, 750, IItems.铱板, 625, IItems.单晶硅, 425, IItems.钴钢, 225
    )

    drawer = DrawTurret().apply {
      parts.add(RegionPart().apply {
        suffix = "-side"
        heatProgress = DrawPart.PartProgress.warmup
        under = true
        mirror = true
        moveX = 1.5f
        moveY = 4f
        heatColor = "F03B0E".toColor()
      })
      parts.add(RegionPart().apply {
        suffix = "-barrel"
        under = true
        moveY = 7.25f
      })
      parts.add(RegionPart().apply {
        suffix = "-barrel-part"
        heatProgress = DrawPart.PartProgress.warmup
        under = true
        drawRegion = false
        moveY = 7.25f
        heatColor = "F03B0E".toColor()
      })
      parts.add(RegionPart().apply {
        suffix = "-part"
        heatProgress = DrawPart.PartProgress.warmup
        drawRegion = false
        heatColor = "F03B0E".toColor()
      })
      parts.add(RegionPart().apply {
        suffix = "-trail1"
        under = true
        mirror = true
        moveX = 1.5f
        heatColor = "F03B0E".toColor()
      })
      parts.add(RegionPart().apply {
        suffix = "-trail2"
        mirror = true
        moveX = 2.5f
        moveY = -2.5f
        heatColor = "F03B0E".toColor()
      })
      parts.add(RegionPart().apply {
        suffix = "-trail31"
        mirror = true
        moveX = 2.5f
        moveY = -2.5f
        heatColor = "F03B0E".toColor()
      })
      parts.add(RegionPart().apply {
        suffix = "-trail32"
        mirror = true
        moveX = 5f
        moveY = -5f
        heatColor = "F03B0E".toColor()
      })
      parts.add(HaloPart().apply {
        mirror = true
        tri = true
        x = 16f
        y = 16f
        shapeRotation = -45f
        shapeMoveRot = 60f
        shapes = 1
        radius = 4f
        triLength = 0f
        triLengthTo = 32f
        haloRadius = 0f
        color = "FEB380".toColor()
        colorTo = "FEB380".toColor()
        layer = 110f
      })
      parts.add(HaloPart().apply {
        mirror = true
        tri = true
        x = 16f
        y = 16f
        shapeRotation = -45f
        shapeMoveRot = -135f
        shapes = 1
        radius = 4f
        triLength = 0f
        triLengthTo = 12f
        haloRadius = 0f
        color = "FEB380".toColor()
        colorTo = "FEB380".toColor()
        layer = 110f
      })
      parts.add(ShapePart().apply {
        x = 0f
        y = 21f
        rotation = -30f
        radius = 4f
        radiusTo = 4f
        color = Color.valueOf("FEB38000")
        colorTo = "FEB380".toColor()
        layer = 110f
      })
      parts.add(ShapePart().apply {
        x = 0f
        y = 21f
        rotation = -30f
        moveY = 16f
        radius = 4f
        radiusTo = 4f
        color = Color.valueOf("FEB38000")
        colorTo = "FEB380".toColor()
        layer = 110f
      })
      parts.add(ShapePart().apply {
        x = 0f
        y = 21f
        rotation = -30f
        moveY = 32f
        radius = 4f
        radiusTo = 4f
        color = Color.valueOf("FEB38000")
        colorTo = "FEB380".toColor()
        layer = 110f
      })
      parts.add(HoverPart().apply {
        color = "FEB380".toColor()
        phase = 120f
        circles = 3
        stroke = 1f
        y = -2f
        layer = 110f
      })
      parts.add(HaloPart().apply {
        progress = DrawPart.PartProgress.recoil
        mirror = true
        tri = true
        x = 2f
        y = -4f
        shapeRotation = -135f
        shapes = 1
        radius = 3f
        triLength = 0f
        triLengthTo = 16f
        haloRadius = 0f
        color = "FEB380".toColor()
        colorTo = "FEB380".toColor()
        layer = 110f
      })
    }
  }
}
