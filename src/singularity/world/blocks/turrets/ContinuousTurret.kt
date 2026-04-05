package singularity.world.blocks.turrets

import arc.func.Cons2
import arc.func.Prov
import arc.math.Angles
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Nullable
import arc.util.Tmp
import ice.entities.bullet.base.BulletType
import mindustry.gen.Bullet
import mindustry.type.ItemStack
import singularity.world.draw.DrawSglTurret
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.cons.item.ConsumeItems
import kotlin.math.min

open class ContinuousTurret(name: String):SglTurret(name) {


  var aimChangeSpeed: Float = Float.POSITIVE_INFINITY
  init {
    DrawSglTurret()
    buildType= Prov{ ContinuousTurretBuild() }
  }

  override fun newAmmo(ammoType: BulletType, override: Boolean, value: Cons2<Table, mindustry.entities.bullet.BulletType>): AmmoDataEntry {
    consume = object : BaseConsumers(false) {
      init {
        showTime = false
      }

      override fun time(time: Float): BaseConsumers {
        showTime = false
        craftTime = time
        return this
      }

      override fun items(vararg items: ItemStack): ConsumeItems<*> {
        val add1 = add(ConsumeItems(items))
        add1.showPerSecond = false
        return add1
      }
    }
    consumers.add(consume)
    val res: AmmoDataEntry
    ammoTypes.put(consume, AmmoDataEntry(ammoType, override).also { res = it })
    res.display(value)

    return res
  }
  open inner class ContinuousTurretBuild: SglTurretBuild() {
    var bullets= Seq<BulletEntry>()
    var lastLength = 0f

    override fun shouldConsume(): Boolean {
      return consumeCurrent != -1 &&enabled && !charging() && consumer.current != null && wasShooting

    }

    override fun drawSelect() {
      super.drawSelect()
    }
    override fun range(): Float {
      return currentAmmo?.bulletType?.range?:super.range()
    }
    override fun updateTile() {

      super.updateTile()
      bullets.remove{ b -> !b.bullet.isAdded || b.bullet.type == null || b.bullet.owner !== this }

      if (bullets.any()) {
        for(entry in bullets) {
          updateBullet(entry)
        }
        wasShooting = true
        heat = 1f
        curRecoil = recoil
      }
    }
    override fun handleBullet(@Nullable bullet: Bullet?, offsetX: Float, offsetY: Float, angleOffset: Float) {
      if (bullet != null) {
        bullets.add(BulletEntry(bullet, offsetX, offsetY, angleOffset))
        //make sure the length updates to the last set value
        Tmp.v1.trns(rotationu, shootY + lastLength).add(x, y)
        bullet.aimX = Tmp.v1.x
        bullet.aimY = Tmp.v1.y
      }
    }

    override fun updateReload() {
      val tarValid = validateTarget()
      if (wasShooting() && shootValid()) {
        if (canShoot()&&tarValid) {
          warmup = if (linearWarmup) Mathf.approachDelta(warmup, 1f, warmupSpeed * consEfficiency()) else Mathf.lerpDelta(warmup, 1f, warmupSpeed * consEfficiency())
          wasShooting = true

          if (!charging()&&bullets.size<1) {

                doShoot(currentAmmo!!.bulletType)

          }
        }
      } else if ((!chargingWarm || !charging())) {
        warmup = if (linearWarmup) Mathf.approachDelta(warmup, 0f, warmupSpeed) else Mathf.lerpDelta(warmup, 0f, warmupSpeed)
      }
    }


     fun updateBullet(entry: BulletEntry) {
      val bulletX = x + Angles.trnsx(rotationu - 90, shootX + entry.x, shootY + entry.y)
      val bulletY = y + Angles.trnsy(rotationu - 90, shootX + entry.x, shootY + entry.y)
      val angle = rotationu + entry.rotation

      entry.bullet.rotation(angle)
      entry.bullet.set(bulletX, bulletY)

      //target length of laser
      val shootLength = min(dst(targetPos), range())
      //current length of laser
      val curLength = dst(entry.bullet.aimX, entry.bullet.aimY)
      //resulting length of the bullet (smoothed)
      val resultLength = Mathf.approachDelta(curLength, shootLength, aimChangeSpeed)
      //actual aim end point based on length
      Tmp.v1.trns(rotationu, resultLength.also { lastLength = it }).add(x, y)

      entry.bullet.aimX = Tmp.v1.x
      entry.bullet.aimY = Tmp.v1.y


      if (wasShooting() && shootValid()) {
        entry.bullet.time = entry.bullet.lifetime * entry.bullet.type.optimalLifeFract * min(warmup, efficiency())
        entry.bullet.keepAlive = true
      }
    }
  }

  class BulletEntry(var bullet: Bullet, var x: Float, var y: Float, var rotation: Float)
}