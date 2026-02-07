package ice.entities.bullet

import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Tmp
import ice.entities.bullet.base.BasicBulletType
import ice.graphics.TextureRegionNoArrDelegate
import ice.library.IFiles.appendModName
import mindustry.gen.Bullet

class MultiBasicBulletType(sprite: String) : BasicBulletType() {
  var sprites: Array<TextureRegion> by TextureRegionNoArrDelegate(sprite.appendModName())
  var spriteBacks: Array<TextureRegion> by TextureRegionNoArrDelegate("$sprite-back".appendModName())

  override fun draw(b: Bullet) {
    drawTrail(b)
    drawParts(b)
    drawBase(b)
  }

  fun drawBase(b: Bullet) {
    val shrink = shrinkInterp.apply(b.fout())
    val height = this.height * ((1f - shrinkY) + shrinkY * shrink)
    val width = this.width * ((1f - shrinkX) + shrinkX * shrink)
    val offset = -90 + (if (spin != 0f) Mathf.randomSeed(b.id.toLong(), 360f) + b.time * spin else 0f) + rotationOffset
    val mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin())

    Draw.mixcol(mix, mix.a)

    val randomSeed = Mathf.randomSeed(b.id.toLong(), 0, sprites.size - 1)
    Draw.color(backColor)
    Draw.rect(spriteBacks[randomSeed], b.x, b.y, width, height, b.rotation() + offset)
    Draw.color(frontColor)
    Draw.rect(sprites[randomSeed], b.x, b.y, width, height, b.rotation() + offset)
    Draw.reset()
  }
}