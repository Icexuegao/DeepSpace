package singularity.world.blocks.turrets

import arc.func.Boolf
import arc.func.Prov
import arc.math.Angles
import arc.struct.Seq
import mindustry.gen.Bullet
import mindustry.world.blocks.defense.turrets.Turret.BulletEntry

open class LaserTurret(name: String) : SglTurret(name) {
  private val timeId = timers++
  var shootingRotateSpeedScl: Float = 0.35f
  var shootEffInterval: Float = 5f
  var needCooldown: Boolean = true
  var shootingConsume: Boolean = false

  init {
    buildType = Prov(::LaserTurretBuild)
  }

  inner class LaserTurretBuild : SglTurretBuild() {
    var allLaser: Seq<BulletEntry> = Seq<BulletEntry>()

    override fun turnToTarget(targetRot: Float) {
      rotationu = Angles.moveToward(rotationu, targetRot, rotateSpeed * delta() * (if (allLaser.isEmpty) 1f else shootingRotateSpeedScl))
    }

    override fun updateTile() {
      super.updateTile()

      allLaser.removeAll { e: BulletEntry? -> e!!.bullet.type == null || !e.bullet.isAdded || e.bullet.owner !== this }

      if (allLaser.any()) {
        wasShooting = true

        for (entry in allLaser) {
          val bulletX = x + Angles.trnsx(rotationu - 90, shootX + entry.x, shootY + entry.y)
          val bulletY = y + Angles.trnsy(rotationu - 90, shootX + entry.x, shootY + entry.y)
          val angle = rotationu + entry.rotation

          entry.bullet.rotation(angle)
          entry.bullet.set(bulletX, bulletY)

          if (shootEffect != null && timer(timeId, shootEffInterval)) shootEffect!!.at(bulletX, bulletY, angle, entry.bullet.type.hitColor)
        }
      } else if (heat > 0 && needCooldown) {
        wasShooting = true
      }
    }

    override fun handleBullet(bullet: Bullet?, offsetX: Float, offsetY: Float, angleOffset: Float) {
      allLaser.add(BulletEntry(bullet, offsetX, offsetY, angleOffset, bullet!!.lifetime))
    }

    override fun shouldConsume(): Boolean {
      return super.shouldConsume() && (shootingConsume || allLaser.isEmpty) && !(heat > 0 && needCooldown)
    }

    override fun canShoot(): Boolean {
      return allLaser.isEmpty && !(heat > 0 && needCooldown)
    }
  }
}