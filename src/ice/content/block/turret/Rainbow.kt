package ice.content.block.turret

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IItems
import ice.entities.bullet.PointBulletType
import ice.world.meta.IceStat
import ice.world.meta.IceStats
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.ui.Bar
import mindustry.world.meta.StatUnit
import singularity.world.blocks.turrets.SglTurret
import kotlin.math.max

class Rainbow :SglTurret("turret_rainbow") {
  var min = 6f
  var change = 5 * 60f

  init {
    localization {
      zh_CN {
        localizedName = "霓虹"
        description = "快速发射渐变霓虹攻击敌人\n其聚能速度会随持续射击而逐渐提升"
      }
    }
    health = 820
    size = 2
    range = 160f
    shootY += 2f
    recoil = 2f
    shootY = 2f
    shootCone = 15f
    rotateSpeed = 6f
    recoilTime = 30f
    cooldownTime = 30f
    canOverdrive = false
    shootSound = Sounds.shootLaser
    requirements(Category.turret, IItems.铜锭, 130, IItems.铅锭, 85, IItems.单晶硅, 45)
    buildType = Prov(::RainbowBuild)
    setAmmo()
  }

   fun setAmmo() {
    newAmmo(PointBulletType().apply {
      damage = 35f
      lifetime = 8f
      speed = 20f
      ammoMultiplier = 1f
      shootEffect = Effect(20f) { e ->
        val rgb = Color(1f, 1f, 1f, 1f)
        rgb.fromHsv((Time.time * 3) % 360, 1f, 1f)
        Draw.color(Color.white, rgb, e.fin())
        Lines.stroke(e.fout() * 1.3f + 0.7f)
        Angles.randLenVectors(e.id.toLong(), 8, 41f * e.fin(), e.rotation, 10f) { x: Float, y: Float ->
          Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 6f + 0.5f)
        }
      }

      trailSpacing = 24f
      trailEffect = Effect(20f) { e ->
        val rgb = Color(1f, 1f, 1f, 1f)
        rgb.fromHsv((Time.time * 3) % 360, 1f, 1f)
        Draw.color(Color.white, rgb, e.fin())
        Lines.stroke(e.fout() * 1.3f + 0.7f)
        Lines.lineAngle(e.x, e.y, e.rotation, 28f)
      }

      hitEffect = Fx.dynamicSpikes.wrap(Pal.redLight, 8f)
      despawnEffect = Fx.dynamicSpikes.wrap(Pal.redLight, 8f)
    })
    consume?.apply {
      power(7f)
      time(30f)
    }
  }

  override fun setStats() {
    super.setStats()
    val stat = IceStat("fullyCharged").apply {
      localization {
        zh_CN {
          description = "完全充能"
        }
      }
    }
    stats.add(stat, "${change / 60}", StatUnit.seconds)
  }

  override fun setBars() {
    super.setBars()
    addBar("speedUp") { e: RainbowBuild ->
      Bar({ "${IceStats.注能}: ${(e.speed * 100).toInt()}%" }, { Color.valueOf("FF5845") }, { e.speed })
    }
  }

  inner class RainbowBuild :SglTurretBuild() {
    var speed = 0f
    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      speed = read.f()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.f(speed)
    }

    override fun ammoReloadMultiplier(): Float {
      return super.ammoReloadMultiplier() * max(1f, speed * min)
    }

    override fun updateTile() {
      super.updateTile()
      val target = if (wasShooting) 1f else 0f
      speed = Mathf.approachDelta(speed, target, 1f / change * (if (target > 0) efficiency else 1f))
    }
  }
}