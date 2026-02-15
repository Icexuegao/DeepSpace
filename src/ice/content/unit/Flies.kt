package ice.content.unit

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Interval
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.base.FleshEntity
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds

class Flies : IceUnitType("flies", FliesUnit::class.java) {
  init {
    bundle {
      desc(zh_CN, "糜蝇", "小型飞行生物单位,体型轻盈,机动性高\n虽然单体战斗力薄弱,但往往以群体形式出现,形成令人困扰的蜂群\n其死亡时会释放出具有腐蚀性的体液")
    }
    speed += 1f
    health = 300f
    flying = true
    drawCell = false
    engineSize = 0f
    outlineColor = IceColor.r2
    accel = 0.08f
    drag = 0.04f
    hitSize = 10f
    createWreck = false
    deathSound = Sounds.plantBreak
    createScorch = false
    range = 1f
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    setWeapon {
      bullet.apply {
        rangeOverride = 25f
        killShooter = true
        splashDamage = 80f
        splashDamageRadius = 5 * 8f
        instantDisappear = true
        hittable = false
        collidesAir = true
        despawnEffect = WaveEffect().apply {
          colorFrom = IceColor.r2
          colorTo = IceColor.r1
          sizeFrom = hitSize
          lifetime = 25f
          sizeTo = hitSize * 2f
        }
        hitEffect = despawnEffect
        shootEffect = Fx.none
      }
      shootCone = 360f
    }
  }

  class FliesUnit : FleshEntity() {
    companion object {
      val flies: Array<TextureRegion> by lazy {
        Array(4) {
          Core.atlas.find(("flies-${it + 1}").appendModName())
        }
      }
    }

    val interval = Interval(2)
    var index = 0
    override fun drawBodyRegion(rotation: Float) {
      super.drawBodyRegion(rotation)
      Draw.rect(flies[index % 3], x, y, rotation)
    }

    override fun drawShadowRegion(x: Float, y: Float, rotation: Float) {
      Draw.rect(flies[index % 3], x, y, rotation)
    }

    override fun update() {
      super.update()
      if (interval.get(5f)) {
        index++
        if (index > 1000) index = 0
      }
    }
  }
}