package ice.content.block.turret

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.util.Time
import ice.content.IItems
import ice.library.util.toStringi
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.bullet.PointBulletType
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.ui.Bar
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.meta.Stat

class Rainbow : PowerTurret("turret_rainbow") {
  var min = 6f
  var change = 120f

  init {
    BaseBundle.bundle {
      desc(zh_CN, "霓虹", "快速发射渐变霓虹攻击敌人\n其聚能速度会随持续射击而逐渐提升")
    }
    health = 820
    size = 2
    range = 160f
    reload = 30f
    shootType = PointBulletType().apply {
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
    }
    recoil = 2f
    shootY = 2f
    shootCone = 15f
    rotateSpeed = 6f
    recoilTime = 30f
    cooldownTime = 30f
    canOverdrive = false
    shootSound = Sounds.shootLaser
    consumePower(7f)
    requirements(Category.turret, IItems.铜锭, 130, IItems.铅锭, 85, IItems.单晶硅, 45)
    buildType = Prov(::RainbowBuild)
  }

  override fun setStats() {
    super.setStats()
    stats.add(Stat("完全充能"), "${change / 60}秒")
  }

  override fun setBars() {
    super.setBars()
    addBar("speedUp") { e: RainbowBuild -> Bar({ "注能: ${(e.speed * 100).toStringi(0)}%" }, { Color.valueOf("FF5845") }, { e.speed }) }
  }

  inner class RainbowBuild : PowerTurretBuild() {
    var speed = 0f
    var speedup = 0f

    override fun baseReloadSpeed(): Float {
      return if (efficiency * speedup > 1f) speedup else 1f
    }

    override fun updateTile() {
      super.updateTile()
      speedup = reload / min * speed
      val target = if (isShooting) 1f else 0f
      speed = Mathf.approachDelta(speed, target, 1f / change * (if (target > 0) efficiency else 1f))
    }
  }
}