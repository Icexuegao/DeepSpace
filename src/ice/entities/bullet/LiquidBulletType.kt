package ice.entities.bullet

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.util.Nullable
import arc.util.Tmp
import ice.entities.bullet.base.BulletType
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Fires
import mindustry.entities.Puddles
import mindustry.gen.Bullet
import mindustry.type.Liquid

class LiquidBulletType @JvmOverloads constructor(@Nullable liquid: Liquid? = null) :BulletType(3.5f, 0f) {
  var liquid: Liquid? = null
  var puddleSize: Float = 6f
  var orbSize: Float = 3f
  var boilTime: Float = 5f

  init {
    if (liquid != null) {
      this.liquid = liquid
      this.status = liquid.effect
      hitColor = liquid.color
      lightColor = liquid.lightColor
      lightOpacity = liquid.lightColor.a
    }

    ammoMultiplier = 1f
    lifetime = 34f
    statusDuration = 60f * 2f
    despawnEffect = Fx.none
    hitEffect = Fx.hitLiquid
    smokeEffect = Fx.none
    shootEffect = Fx.none
    drag = 0.001f
    knockback = 0.55f
    displayAmmoMultiplier = false
  }

  override fun update(b: Bullet) {
    super.update(b)

    if (liquid!!.willBoil() && b.time >= Mathf.randomSeed(b.id.toLong(), boilTime)) {
      Fx.vaporSmall.at(b.x, b.y, liquid!!.gasColor)
      b.remove()
      return
    }

    if (liquid!!.canExtinguish()) {
      val tile = Vars.world.tileWorld(b.x, b.y)
      if (tile != null && Fires.has(tile.x.toInt(), tile.y.toInt())) {
        Fires.extinguish(tile, 100f)
        b.remove()
        hit(b)
      }
    }
  }

  override fun draw(b: Bullet) {
    super.draw(b)
    if (liquid!!.willBoil()) {
      Draw.color(liquid!!.color, Tmp.c3.set(liquid!!.gasColor).a(0.4f), b.time / Mathf.randomSeed(b.id.toLong(), boilTime))
      Fill.circle(b.x, b.y, orbSize * (b.fin() * 1.1f + 1f))
    } else {
      Draw.color(liquid!!.color, Color.white, b.fout() / 100f)
      Fill.circle(b.x, b.y, orbSize)
    }

    Draw.reset()
  }

  override fun despawned(b: Bullet) {
    super.despawned(b)

    //don't create liquids when the projectile despawns
    if (!liquid!!.willBoil()) {
      hitEffect.at(b.x, b.y, b.rotation(), liquid!!.color)
    }
  }

  override fun hit(b: Bullet, x: Float, y: Float, createFrags: Boolean) {
    hitEffect.at(x, y, liquid!!.color)
    Puddles.deposit(Vars.world.tileWorld(x, y), liquid, puddleSize)

    if (liquid!!.temperature <= 0.5f && liquid!!.flammability < 0.3f) {
      val intensity = 400f * puddleSize / 6f
      Fires.extinguish(Vars.world.tileWorld(x, y), intensity)
      for(p in Geometry.d4) {
        Fires.extinguish(Vars.world.tileWorld(x + p.x * Vars.tilesize, y + p.y * Vars.tilesize), intensity)
      }
    }
  }
}
