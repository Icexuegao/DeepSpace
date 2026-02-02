package singularity.world.particles;

import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Rect
import arc.util.Time
import mindustry.content.Fx
import mindustry.entities.Units
import mindustry.entities.bullet.BulletType
import mindustry.gen.Bullet
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import singularity.world.blocks.turrets.HeatBulletType
import universecore.world.particles.MultiParticleModel
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel
import universecore.world.particles.models.*

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

        override fun draw(b: Bullet?) {
        }
    }
    var floatParticle: ParticleModel = MultiParticleModel(SizeVelRelatedParticle(), object : RandDeflectParticle() {
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
    }, ShapeParticle(), DrawDefaultTrailParticle())
    var heatBulletTrail: ParticleModel = object : ParticleModel() {
        val tmp1: Particle.Cloud = Particle.Cloud()
        val tmp2: Particle.Cloud = Particle.Cloud()

        public override fun drawTrail(particle: Particle) {
            for (cloud in particle) {
                tmp1.color.set(Color.black)
                tmp1.x = cloud!!.x
                tmp1.y = cloud.y
                tmp1.size = cloud.size / 2
                tmp1.perCloud = cloud.perCloud
                tmp1.nextCloud = tmp2

                tmp2.x = cloud.nextCloud!!.x
                tmp2.y = cloud.nextCloud!!.y
                tmp2.size = cloud.nextCloud!!.size / 2

                Draw.z(Layer.bullet - 1)
                tmp1.draw()
                Draw.z(Layer.effect)
                cloud.draw()
                tmp1.draw()
            }
        }

        public override fun draw(particle: Particle) {
            Draw.z(Layer.bullet - 1)
            Fill.circle(particle.x, particle.y, particle.size)
            Draw.z(Layer.effect)
            Lines.stroke(1f, Pal.lighterOrange)
            Lines.circle(particle.x, particle.y, particle.size)
        }

        public override fun currSize(particle: Particle): Float {
            return if (particle.owner != null && particle.owner!!.isAdded()) particle.defSize else Mathf.approachDelta(particle.size, 0f, 0.04f)
        }

        public override fun update(particle: Particle) {
            if (particle.bullet != null) {
                if (particle.bullet!!.isAdded()) {
                    particle.set(particle.bullet!!.x, particle.bullet!!.y)
                } else particle.owner = null
            }

            if (particle.bullet != null && particle.bullet!!.isAdded()) {
                particle.bullet!!.keepAlive = true
            }
        }

        public override fun updateTrail(particle: Particle, c: Particle.Cloud) {
            c.size -= 0.03f * Time.delta

            if (particle.bullet != null && particle.bullet!!.isAdded() && particle.timer.get(5f) && c.nextCloud != null) {
                val b: Bullet = particle.bullet!!
                val dx = c.nextCloud!!.x - c.x
                val dy = c.nextCloud!!.y - c.y
                val expand = 3f

                rect.set(c.x, c.y, dx, dy).normalize().grow(expand * 2f)
                b.hitbox(hitrect)

                Units.nearbyEnemies(b.team, rect, Cons { u: Unit? ->
                    if (u!!.checkTarget(b.type.collidesAir, b.type.collidesGround) && u.hittable()) {
                        u.hitbox(hitrect)
                        val vec = Geometry.raycastRect(c.x, c.y, c.nextCloud!!.x, c.nextCloud!!.y, hitrect.grow(expand * 2))

                        if (vec != null) {
                            if (!b.collided.contains(u.id)) {
                                b.collision(u, u.x, u.y)
                            }
                        }
                    }
                })
            }
        }

        public override fun isFaded(particle: Particle?, cloud: Particle.Cloud): Boolean {
            return cloud.size <= 0.03f
        }

        public override fun trailColor(particle: Particle): Color? {
            return particle.color
        }

        public override fun isFinal(particle: Particle): Boolean {
            return !(particle.bullet != null && particle.bullet!!.isAdded()) && particle.size <= 0.04f
        }
    }
}
