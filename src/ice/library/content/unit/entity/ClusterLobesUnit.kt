package ice.library.content.unit.entity

import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IStatus
import ice.library.content.unit.entity.base.Entity
import ice.library.entities.IceRegister
import ice.library.scene.tex.IceColor
import ice.library.util.IMathf
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Units
import mindustry.entities.bullet.LaserBulletType
import mindustry.gen.Groups
import mindustry.gen.Healthc
import mindustry.gen.Teamc
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import kotlin.math.min
import kotlin.math.sin

class ClusterLobesUnit : Entity() {
    var timer = 0
    var target: Teamc? = null
    var resistCont = 0
    val armorCont = 50f
    val immunityCont = 250f
    val vec2Size = 20
    val eye = Vec2(0f, 8f)
    val tris = arrayOf(
        Vec2(0f, 24f), Vec2(5f, 24f), Vec2(10f, 24f), Vec2(0f, -24f), Vec2(-5f, -24f), Vec2(-10f, -24f)
    )
    val outsideRing = Array(vec2Size) { i ->
        val ang = 360f / vec2Size
        Vec2(0f, 80f).rotate(ang * i)
    }
    val laserBulletType = object : LaserBulletType(100f) {
        init {
            status = StatusEffects.shocked
            width = 19f
            hitEffect = Fx.hitLancer
            sideAngle = 175f
            sideWidth = 1f
            sideLength = 40f
            lifetime = 22f
            drawSize = 400f
            length = 180f
            pierceBuilding = true
            pierce = true
        }
    }

    init {
        shadowAlpha = 0f
    }

    override fun draw() {
        if (Vars.player.unit() == this) {
            Drawf.circles(x, y, laserBulletType.length, IceColor.b4)
        }
        //环线
        Draw.z(Layer.effect)
        Draw.color(IceColor.b4)
        Lines.stroke(3f)
        for (i in outsideRing.indices) {
            if (i + 1 >= outsideRing.size) continue
            val cur: Vec2 = outsideRing[i]
            val next: Vec2 = outsideRing[i + 1]
            Lines.line(cur.x + x, cur.y + y, next.x + x, next.y + y, false)
        }
        Lines.line(
            outsideRing.first().x + x,
            outsideRing.first().y + y,
            outsideRing.last().x + x,
            outsideRing.last().y + y,
            false
        )

        outsideRing.forEach {
            val fl = 10f * sin(Time.time * 0.1f - outsideRing.indexOf(it)) + 6f

            Drawf.tri(x + it!!.x, y + it.y, 8f, 8f + fl, it.angle())
            Drawf.tri(x + it.x, y + it.y, 8f, -8f - fl, it.angle())
        }
        tris.forEach {
            it.setLength(10 * sin(Time.time * 0.1f - tris.indexOf(it)) + Vec2(0f, 24f).len() + 8)
            Drawf.tri(x + it.x, y + it.y, 8f, 40f, it.angle())
        }
        //one 光环
        Lines.stroke(IMathf.sint(0.5f, 0.2f, 0f, 8f))
        Lines.circle(x, y, 24f)
        //eye
        val playUnit = Vars.player.unit()
        val angle = if (playUnit == this) {
            Angles.angle(x, y, aimX, aimY)
        } else if (target != null) {
            Angles.angle(x, y, target!!.x, target!!.y)
        } else {
            0f
        }
        eye.rotateTo(angle, 6 * Time.delta)
        Fill.circle(x + eye.x, y + eye.y, IMathf.sint(0.5f, 0.1f, 0f, 5f))
        //黑色环
        Draw.color(Color.black)
        Draw.z(99.9f)
        Fill.circle(x, y, 24f)
        Draw.reset()
        //super.draw()
    }

    override fun isFlying(): Boolean {
        return true
    }

    override fun update() {
        super.update()
        target = Units.closestTarget(team, x, y, range())
        timer++
        if (timer > 60 && target != null) {
            timer = 0
            outsideRing.forEach {
                val sub = Tmp.v1.set(it).add(x, y)
                if (target!!.within(sub, laserBulletType.length)) {
                    laserBulletType.create(this, sub.x, sub.y, Tmp.v2.set(it).add(x, y).sub(target).angle() + 180)
                }
            }
        }
        tris.forEach {
            it.rotate(1f)
        }
        Units.nearbyEnemies(team, x, y, 80f) {
            it.apply(IStatus.电磁脉冲, 60 * 30f)
        }
        outsideRing.forEach {
            Groups.bullet.intersect(it.x + x - 4, it.y + y, 8f, 8f, Cons { bullet ->
                if (bullet.team == team) return@Cons
                val owner = bullet.owner
                if (owner is Healthc) {
                    owner.damage(bullet.damage)
                }
                bullet.remove()
                resistCont++
            })
            it!!.rotate(-0.25f)
        }
        if (!(armor() >= 100)) armor(resistCont / armorCont)
    }

    fun immunity(): Float {
        return min(resistCont / immunityCont / 100, 0.3f)
    }

    override fun damage(amount: Float) {
        super.damage(amount - (amount * immunity()))
    }

    override fun classId(): Int {
        return IceRegister.getId(this::class.java)
    }

    override fun read(read: Reads) {
        super.read(read)
        resistCont = read.i()
    }

    override fun write(write: Writes) {
        super.write(write)
        write.i(resistCont)
    }
}