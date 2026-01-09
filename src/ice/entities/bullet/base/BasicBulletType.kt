package ice.entities.bullet.base

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Interp
import arc.math.Mathf
import arc.util.Nullable
import arc.util.Tmp
import ice.library.IFiles
import mindustry.gen.Bullet
import mindustry.graphics.Pal

open class BasicBulletType(speed: Float = 1f, damage: Float = 1f, var sprite: String = "bullet"): BulletType(speed, damage) {
    var backColor: Color = Pal.bulletYellowBack
    var frontColor: Color = Pal.bulletYellow
    var mixColorFrom: Color = Color(1f, 1f, 1f, 0f)
    var mixColorTo: Color = Color(1f, 1f, 1f, 0f)
    var width: Float = 5f
    var height: Float = 7f
    var shrinkX: Float = 0f
    var shrinkY: Float = 0.5f
    var shrinkInterp: Interp = Interp.linear
    var spin: Float = 0f
    var rotationOffset: Float = 0f

    @Nullable
    var backSprite: String? = null
    var backRegion: TextureRegion? = null
    var frontRegion: TextureRegion? = null



    override fun load() {
        super.load()

        backRegion = IFiles.findModPng(if (backSprite == null) ("$sprite-back") else backSprite!!)
        frontRegion = IFiles.findModPng(sprite)
    }

    override fun draw(b: Bullet) {
        super.draw(b)
        val shrink = shrinkInterp.apply(b.fout())
        val height = this.height * ((1f - shrinkY) + shrinkY * shrink)
        val width = this.width * ((1f - shrinkX) + shrinkX * shrink)
        val offset = -90 + (if (spin != 0f) Mathf.randomSeed(b.id.toLong(), 360f) + b.time * spin else 0f) + rotationOffset
        val mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin())

        Draw.mixcol(mix, mix.a)

        if (backRegion!!.found()) {
            Draw.color(backColor)
            Draw.rect(backRegion, b.x, b.y, width, height, b.rotation() + offset)
        }

        Draw.color(frontColor)
        Draw.rect(frontRegion, b.x, b.y, width, height, b.rotation() + offset)

        Draw.reset()
    }
}