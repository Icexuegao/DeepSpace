package ice.entities.bullet

import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType

fun jumpIn(unit: UnitType, x: Float, y: Float): Effect {
  val size = unit.hitSize
  Sounds.shootMalign.at(x, y)
  return Effect(120f) { e ->
    val rad = Interp.pow2In.apply(size * 1.2f, 0f, e.fin()) * 2
    Draw.mixcol(e.color, 1f)
    Draw.z((if (unit.lowAltitude) Layer.flyingUnitLow else Layer.flyingUnit) - 0.01f)

    Draw.rect(unit.fullIcon, e.x, e.y, rad, rad, e.rotation)
    Draw.reset()
    Draw.color(e.color)
    Drawf.light(e.x, e.y, rad * 1.25f, Pal.accent, 0.6f)
    val x = Angles.trnsx(e.rotation - 90, size / 2 * e.fout())
    val y = Angles.trnsy(e.rotation - 90, size / 2 * e.fout())
    Drawf.tri(e.x + x, e.y + y, size / 8 * e.fout(), size * 16 * e.fout(), e.rotation - 90)

  }
}

fun spawnBulletType(range: Float, unit: UnitType): BulletType {
  return object :BulletType() {
    override fun createUnits(b: Bullet, x: Float, y: Float) {
      val ux = x + Mathf.range(range)
      val uy = y + Mathf.range(range)
      despawnUnit.spawn((b.owner as Teamc).team(), ux, uy).rotation = b.rotation()
      jumpIn(despawnUnit, ux, uy).at(ux, uy, b.rotation() - 90, (b.owner as Teamc).team().color)
    }
  }.apply {
    damage = 0f
    speed = 8f
    lifetime = range * 2 / 8
    hittable = false
    collides = false
    shootEffect = Fx.none
    smokeEffect = Fx.none
    hitEffect = Fx.none
    despawnEffect = Fx.none
    instantDisappear = true
    despawnUnit = unit
  }
}

fun sizeBulletType(speed: Float, damage: Float, lifetime: Float) = object :BasicBulletType() {
  init {
    shrinkY = 0f
    this.speed = speed
    this.damage = damage
    this.lifetime = lifetime
    ammoMultiplier = 1f
    status = IStatus.损毁
    statusDuration = 180f
    pierce = true
    pierceBuilding = true
    pierceDamageFactor = 0.6f
  }

  override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
    val size = if (entity is Unit) entity.hitSize() else (entity as Building).block.size * 8f
    val owner = b.owner as Hitboxc
    val scale = if (owner.hitSize() / size > 1f) owner.hitSize() / size else 1f
    b.damage = damage * scale
    super.hitEntity(b, entity, health)
  }
}