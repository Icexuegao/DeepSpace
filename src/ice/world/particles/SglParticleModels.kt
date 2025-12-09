package ice.world.particles

import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Rect
import arc.util.Time
import ice.library.world.particles.MultiParticleModel
import ice.library.world.particles.Particle
import ice.library.world.particles.Particle.Cloud
import ice.library.world.particles.ParticleModel
import ice.library.world.particles.models.*
import ice.entities.bullet.HeatBulletType
import mindustry.content.Fx
import mindustry.entities.Units
import mindustry.entities.bullet.BulletType
import mindustry.gen.Bullet
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Pal

object SglParticleModels {
    val rect: Rect = Rect()
    val hitrect: Rect = Rect()

    var defHeatTrailHitter: BulletType = object : HeatBulletType() {
        init {
            damage = 20f
            melDamageScl = 0.8f
            meltDownTime = 6f
            lifetime = 60f
            speed = 0f
            collidesAir = false
            collidesGround = true
            collides = false
            pierce = true
            hittable = false
            absorbable = false
            hitEffect = Fx.circleColorSpark
            hitColor = Pal.lighterOrange
            despawnEffect = Fx.none
        }

        override fun draw(b: Bullet?) {}
    }

    var floatParticle: ParticleModel = MultiParticleModel(
        SizeVelRelatedParticle(), object : RandDeflectParticle() {
        init {
            deflectAngle = 90f
            strength = 0.2f
        }
    }, object : TrailFadeParticle() {
        init {
            trailFade = 0.04f
            fadeColor = Pal.lightishGray
            colorLerpSpeed = 0.03f
        }
    }, ShapeParticle(), DrawDefaultTrailParticle()
    )

    var heatBulletTrail: ParticleModel = object : ParticleModel() {
        val tmp1: Cloud = Cloud()
        val tmp2: Cloud = Cloud()


        override fun drawTrail(c: Particle) {
            for (cloud in c) {
                tmp1.color.set(Color.black)
                tmp1.x = cloud.x
                tmp1.y = cloud.y
                tmp1.size = cloud.size / 2
                tmp1.perCloud = cloud.perCloud
                tmp1.nextCloud = tmp2

                tmp2.x = cloud.nextCloud.x
                tmp2.y = cloud.nextCloud.y
                tmp2.size = cloud.nextCloud.size / 2

                Draw.z(Layer.bullet - 1)
                tmp1.draw()
                Draw.z(Layer.effect)
                cloud.draw()
                tmp1.draw()
            }
        }

        override fun draw(p: Particle) {
            Draw.z(Layer.bullet - 1)
            Fill.circle(p.x, p.y, p.size)
            Draw.z(Layer.effect)
            Lines.stroke(1f, Pal.lighterOrange)
            Lines.circle(p.x, p.y, p.size)
        }

        override fun currSize(p: Particle): Float {
            val bullet = p.bullet
            val b1 = bullet != null && bullet.isAdded
            return if (b1) p.defSize else Mathf.approachDelta(p.size, 0f, 0.04f)
        }

        override fun update(p: Particle) {
            val b = p.owner
            if (b != null) {
                if (b.isAdded) {
                    p.set(b.x, b.y)
                } else p.owner = null
            }

            if (p.bullet != null && p.bullet.isAdded) {
                p.bullet.keepAlive = true
            }
        }

        override fun updateTrail(p: Particle, c: Cloud) {
            c.size -= 0.03f * Time.delta

            val b = p.bullet
            if (b != null && b.isAdded && p.timer.get(5f) && c.nextCloud != null) {
                val dx = c.nextCloud.x - c.x
                val dy = c.nextCloud.y - c.y

                val expand = 3f

                rect.set(c.x, c.y, dx, dy).normalize().grow(expand * 2f)
                b.hitbox(hitrect)

                Units.nearbyEnemies(b.team, rect, Cons { u: Unit? ->
                    if (u!!.checkTarget(b.type.collidesAir, b.type.collidesGround) && u.hittable()) {
                        u.hitbox(hitrect)

                        val vec = Geometry.raycastRect(c.x, c.y, c.nextCloud.x, c.nextCloud.y, hitrect.grow(expand * 2))

                        if (vec != null) {
                            if (!b.collided.contains(u.id)) {
                                b.collision(u, u.x, u.y)
                            }
                        }
                    }
                })
            }
        }

        override fun isFaded(p: Particle, cloud: Cloud): Boolean {
            return cloud.size <= 0.03f
        }

        override fun trailColor(p: Particle): Color? {
            return p.color
        }

        override fun isFinal(p: Particle): Boolean {
            val b = p.owner
            return !(b != null && b.isAdded) && p.size <= 0.04f
        }
    }
}
