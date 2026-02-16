package ice.entities.bullet

import arc.math.Interp
import ice.entities.bullet.base.BasicBulletType
import mindustry.content.Fx
import mindustry.gen.Bullet
import mindustry.gen.Sounds

class ArtilleryBulletType(speed: Float = 1f, damage: Float = 1f, bulletSprite: String = "shell") : BasicBulletType( speed,damage, bulletSprite) {
  var trailMult: Float = 1f
  var trailSize: Float = 4f

  init {
    collidesTiles = false
    collides = false
    collidesAir = false
    scaleLife = true
    hitShake = 1f
    hitSound = Sounds.explosionArtillery
    hitEffect = Fx.flakExplosion
    shootEffect = Fx.shootBig
    trailEffect = Fx.artilleryTrail

    //default settings:
    shrinkX = 0.15f
    shrinkY = 0.5f
    shrinkInterp = Interp.slope

    //for trail:

    /*
        trailLength = 27;
        trailWidth = 3.5f;
        trailEffect = Fx.none;
        trailColor = Pal.bulletYellowBack;

        trailInterp = Interp.slope;

        shrinkX = 0.8f;
        shrinkY = 0.3f;
        */
  }

  override fun update(b: Bullet) {
    super.update(b)

    if (b.timer(0, (3 + b.fslope() * 2f) * trailMult)) {
      trailEffect.at(b.x, b.y, if (trailRotation) b.rotation() else b.fslope() * trailSize, backColor)
    }
  }
}