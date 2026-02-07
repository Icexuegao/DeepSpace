package ice.entities.bullet

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.entities.bullet.base.BasicBulletType
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Lightning
import mindustry.gen.Bullet
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import kotlin.math.max

open class LaserBulletType(damage: Float): BasicBulletType(damage, 0f) {
    var colors = arrayOf(Pal.lancerLaser.cpy().mul(1f, 1f, 1f, 0.4f), Pal.lancerLaser, Color.white)
    var laserEffect: Effect = Fx.lancerLaserShootSmoke
    var length: Float = 160f
    var lengthFalloff: Float = 0.5f
    var sideLength: Float = 29f
    var sideWidth: Float = 0.7f
    var sideAngle: Float = 90f
    var lightningSpacing: Float = -1f
    var lightningDelay: Float = 0.1f
    var lightningAngleRand: Float = 0f
    var largeHit: Boolean = false

    init {
        width=15f
        hitEffect = Fx.hitLaserBlast
        hitColor = colors[2]
        despawnEffect = Fx.none
        shootEffect = Fx.hitLancer
        smokeEffect = Fx.none
        hitSize = 4f
        lifetime = 16f
        impact = true
        keepVelocity = false
        collides = false
        pierce = true
        hittable = false
        absorbable = false
        removeAfterPierce = false
        delayFrags = true
    }

    //assume it pierces at least 3 blocks
    override fun estimateDPS(): Float {
        return super.estimateDPS() * 3f
    }

    override fun init() {
        super.init()

        drawSize = max(drawSize, length * 2f)
    }

    override fun calculateRange(): Float {
        return max(length, maxRange)
    }

    override fun init(b: Bullet) {
        val resultLength = Damage.collideLaser(b, length, largeHit, laserAbsorb, pierceCap)
        val rot = b.rotation()

        laserEffect.at(b.x, b.y, rot, resultLength * 0.75f)

        if (lightningSpacing > 0) {
            var idx = 0
            var i = 0f
            while (i <= resultLength) {
                val cx = b.x + Angles.trnsx(rot, i)
                val cy = b.y + Angles.trnsy(rot, i)

                val f = idx++

                for (s in Mathf.signs) {
                    Time.run(f * lightningDelay) {
                        if (b.isAdded && b.type === this) {
                            Lightning.create(b, lightningColor,
                                if (lightningDamage < 0) damage else lightningDamage,
                                cx, cy, rot + 90 * s + Mathf.range(lightningAngleRand),
                                lightningLength + Mathf.random(lightningLengthRand))
                        }
                    }
                }
                i += lightningSpacing
            }
        }
    }

    override fun draw(b: Bullet) {
        val realLength = b.fdata

        val f = Mathf.curve(b.fin(), 0f, 0.2f)
        val baseLen = realLength * f
        var cwidth = width
        var compound = 1f

        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen)
        for (color in colors) {
            Draw.color(color)
            Lines.stroke((lengthFalloff.let { cwidth *= it; cwidth }) * b.fout())
            Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false)
            Tmp.v1.trns(b.rotation(), baseLen)
            Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Lines.getStroke(), cwidth * 2f + width / 2f, b.rotation())

            Fill.circle(b.x, b.y, 1f * cwidth * b.fout())
            for (i in Mathf.signs) {
                Drawf.tri(b.x, b.y, sideWidth * b.fout() * cwidth, sideLength * compound, b.rotation() + sideAngle * i)
            }

            compound *= lengthFalloff
        }
        Draw.reset()

        Tmp.v1.trns(b.rotation(), baseLen * 1.1f)
        Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, width * 1.4f * b.fout(), colors[0], 0.6f)
    }

    override fun drawLight(b: Bullet?) {
        //no light drawn here
    }
}