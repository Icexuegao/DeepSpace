package ice.content.block.turret


import arc.graphics.Color
import arc.math.Mathf
import arc.util.Time
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import mindustry.entities.UnitSorts
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret

class 暮光 :SglTurret("turret_twilight") {
  init {
    localization {
      zh_CN {
        localizedName = "暮光"
        description = "重型激光炮塔,向指定方向发射一道超视距湍能激光束\n其聚能速度会随聚焦晶体的预热而逐渐提升"
      }
    }
    warmupSpeed=0.02f
    size = 6
    health = 12000
    range = 800f
    shake = 8f
    recoil = 5f
    shootY = 0f
    shootCone = 45f
    rotateSpeed = 2f
    recoilTime = 180f
    cooldownTime = 180f
    canOverdrive = false
    unitSort = UnitSorts.strongest
    shootSound = Sounds.shootMalign
    drawers = DrawSglTurret().apply {
      for(i in 0..4) {
        parts.add(RegionPart("-aim").apply {
          heatProgress = DrawPart.PartProgress{ partParams ->
            partParams.warmup * Mathf.sin(Time.time + (4 - i) * 10f, 20f, 1f)
          }
          heatLight = true
          drawRegion = false
          y = (i + 2) * 24f / (1f - i / 24f)
          xScl = 1f - i * 0.1f
          yScl = 1f - i * 0.1f
          heatColor = Color.valueOf("D1EFFF")
        })
      }

      parts.add(RegionPart("-glow").apply {
        heatProgress = DrawPart.PartProgress.warmup
        heatColor = Color.valueOf("F03B0E")
        drawRegion = false
      })
    }
    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)

    requirements(
      Category.turret,
      IItems.铜锭, 3300,
      IItems.铅锭, 2800,
      IItems.单晶硅, 2350,
      IItems.铱板, 1500,
      IItems.导能回路, 1900,
      IItems.陶钢, 900,
      IItems.肃正协议, 1
    )

    setAmmo()
  }

  fun setAmmo() {
    val laserColor1 = Color.valueOf("6569C9")
    val laserColor2 = Color.valueOf("8CA9E8")
    val laserColor3 = Color.valueOf("D1EFFF")

    newAmmo(object :LaserBulletType(3565f) {
      init {
        length = 800f
        width = 40f
        ammoMultiplier = 1f

        status = IStatus.湍能
        statusDuration = 120f
        buildingDamageMultiplier = 0.05f

        sideAngle = 22.5f

        colors = arrayOf(laserColor1, laserColor2, laserColor3)

        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = laserColor2
          colorFrom = laserColor3
          colorTo = laserColor2
        }

        hitEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 75f
          cone = 360f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = laserColor2
          colorFrom = laserColor3
          colorTo = laserColor2
        }
      }
    }).apply {
      consume?.apply {
        time(1200f)
        power(165f)
        liquid(ILiquids.急冻液, 1.5f)
      }
    }
  }

}
