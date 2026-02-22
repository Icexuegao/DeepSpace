package ice.entities.bullet

import ice.entities.bullet.base.BasicBulletType
import mindustry.gen.Sounds
import mindustry.graphics.Pal

class MissileBulletType(speed: Float = 1f, damage: Float = 1f, bulletSprite: String = "missile") : BasicBulletType(speed, damage, bulletSprite) {
  init {
    backColor = Pal.missileYellowBack
    frontColor = Pal.missileYellow
    homingPower = 0.08f
    shrinkY = 0f
    width = 8f
    height = 8f
    hitSound = Sounds.explosion
    trailChance = 0.2f
    lifetime = 52f
  }
}