package ice.entities.bullet

import arc.audio.Sound
import arc.func.Boolf2
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.SglUnitSorts
import ice.graphics.IceColor
import ice.graphics.lightnings.LightningContainer
import ice.graphics.lightnings.generator.VectorLightningGenerator
import ice.world.content.unit.IceUnitType.Companion.rand
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.UnitSorts
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.Unit

open class BlastLaser : EmpLightningBulletType() {
    companion object {
        val gen: VectorLightningGenerator = VectorLightningGenerator()
    }

    var blastDelay: Float = 30f
    var damageInterval: Float = 5f
    var laserShake: Float = 5f
    var damageShake: Float = 12f
    var laserEffect: Effect = Fx.none
    var laserSound: Sound = Sounds.shootLaser
    var blackZone: Boolean = true

    init {
        collides = false
        hittable = false
        absorbable = false
        pierce = true
        pierceBuilding = true
        pierceArmor = true

        fragOnHit = true //仅用于禁用despawnen时生成子弹

        keepVelocity = false

        speed = 0f

        hitColor = IceColor.matrixNet
    }

    override fun continuousDamage(): Float {
        return damage * (60 / damageInterval)
    }

    override fun init() {
        super.init()
        drawSize = range
    }

    override fun update(b: Bullet) {
        super.update(b)

        Effect.shake(laserShake, laserShake, b)
        if (b.timer(1, damageInterval)) {
            Damage.collideLaser(b, Mathf.len(b.aimX - b.x, b.aimY - b.y) * Mathf.clamp(b.time / blastDelay), true,
                false, -1)
        }
    }

    override fun updateBulletInterval(b: Bullet) {
        val n = Tmp.v1.set(b.x, b.y).lerp(b.aimX, b.aimY, Mathf.clamp(b.time / blastDelay))

        if (intervalBullet != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)) {
            val ang = b.rotation()
            for (i in 0..<intervalBullets) {
                intervalBullet.create(b, n.x, n.y, ang + Mathf.range(
                    intervalRandomSpread) + intervalAngle + ((i - (intervalBullets - 1f) / 2f) * intervalSpread),
                    rand.random(0.4f, 1f), rand.random(0.8f, 1f))
            }
        }
    }

    override fun hit(b: Bullet, x: Float, y: Float) {
        hitEffect.at(x, y, b.rotation(), hitColor)
        hitSound.at(x, y, hitSoundPitch, hitSoundVolume)

        Effect.shake(hitShake, hitShake, b)
    }

    override fun init(b: Bullet, c: LightningContainer) {
        gen.maxSpread = hitSize * 2.3f
        gen.minInterval = 2f * hitSize
        gen.maxInterval = 3.8f * hitSize

        gen.vector.set(b.aimX - b.x, b.aimY - b.y).limit(range)
        b.aimX = b.x + gen.vector.x
        b.aimY = b.y + gen.vector.y
        c.lerp = Interp.pow4Out
        c.lifeTime = lifetime
        c.time = blastDelay
        c.maxWidth = 5f
        c.minWidth = 3f

        val ax = b.aimX
        val ay = b.aimY

        (0..3).forEach { i ->
            c.create(gen)
        }
        Time.run(blastDelay) {
            Effect.shake(damageShake, damageShake, b)
            createSplashDamage(b, ax, ay)
            laserSound.at(ax, ay)
            laserEffect.at(ax, ay, b.rotation(), hitColor)
            createFrags(b, b.aimX, b.aimY)
        }
    }

    override fun createFrags(b: Bullet, x: Float, y: Float) {
        if (fragBullet != null && fragBullets > 0) {
            val arr = SglUnitSorts.findEnemies(fragBullets, b, fragBullet.range, Boolf2 { us: Array<Unit?>, u: Unit ->
                if (b.dst(u) < fragBullet.splashDamageRadius) return@Boolf2 false
                for (e in us) {
                    if (e == null) break
                    if (e.dst(u) < fragBullet.splashDamageRadius) return@Boolf2 false
                }
                true
            }, UnitSorts.farthest)
            for (i in arr.indices) {
                if (arr[i] != null) {
                    Tmp.v1.set(arr[i]!!.x - x, arr[i]!!.y - y)

                    fragBullet.create(b.owner, b.team, x, y, Tmp.v1.angle(), fragBullet.damage, 1f, 1f, null, null,
                        x + Tmp.v1.x, y + Tmp.v1.y)
                } else {
                    val a = b.rotation() + Mathf.range(
                        fragRandomSpread / 2) + fragAngle + ((i - fragBullets / 2f) * fragSpread)

                    Tmp.v1.set(fragBullet.range * Mathf.random(0.6f, 1f), 0f).setAngle(a)
                    fragBullet.create(b.owner, b.team, x, y, a, fragBullet.damage, 1f, 1f, null, null, x + Tmp.v1.x,
                        y + Tmp.v1.y)
                }
            }
        }
    }

    override fun draw(b: Bullet) {
        val `in` = Mathf.clamp(b.time / blastDelay)
        Tmp.v1.set(b.aimX - b.x, b.aimY - b.y).scl(`in`)

        val dx = b.x + Tmp.v1.x
        val dy = b.y + Tmp.v1.y

        Draw.color(hitColor)
        val fout = b.fout(Interp.pow3Out)
        Fill.circle(dx, dy, hitSize * 1.6f * fout)
        Lines.stroke(hitSize * (1 + `in`) * fout)
        Lines.line(b.x, b.y, dx, dy)

        Lines.stroke(hitSize * 0.1f * fout)
        Lines.circle(dx, dy, hitSize * 1.9f * fout)

        Draw.color(Color.white)
        Fill.circle(dx, dy, hitSize * 1.2f * fout)
        Lines.stroke(hitSize * (1 + `in`) * fout * 0.75f)
        Lines.line(b.x, b.y, dx, dy)

        if (blackZone) {
            val z = Draw.z()
            Draw.z(z + 0.0001f)
            Draw.color(Color.black)
            Fill.circle(dx, dy, hitSize * 0.6f * fout)
            Lines.stroke(hitSize * (1 + `in`) * fout / 2.8f)
            Lines.line(b.x, b.y, dx, dy)
            Draw.z(z)
        }

        Draw.color(hitColor)
        Fill.circle(b.x, b.y, hitSize * (1.2f + `in`) * fout)

        super.draw(b)
    }


}