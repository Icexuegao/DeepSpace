package ice.content.unit

import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IStatus
import ice.entities.IceRegister
import ice.entities.bullet.BombBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.graphics.IceColor
import ice.library.util.IMathf
import ice.library.util.toStringi
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.BarAbility
import ice.world.content.unit.entity.base.Entity
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.entities.bullet.LaserBulletType
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.HaloPart
import mindustry.gen.*
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Trail
import mindustry.ui.Bar
import mindustry.world.meta.Stat
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class ClusterLobes : IceUnitType("clusterLobes", ClusterLobesUnit::class.java) {
  init {
    setWeapon {
      shoot.apply {
        shots = 4
        shotDelay = 6f
      }
      mirror = false
      baseRotation = 90f
      shake = 3f
      shootCone = 360f
      rotate = false
      rotateSpeed = 0f
      reload = 60 * 3f
      inaccuracy = 60f
      bullet = object : BasicBulletType(40f, 8f) {
        init {
          smokeEffect = Fx.none
          shootSound = Sounds.shootMalign
          shootEffect = Fx.none
          lifetime = 60 * 8f / speed + 60
          trailLength = 24
          trailWidth = 3f
          trailColor = IceColor.b4
          homingRange = 100 * 8f
          homingPower = 0.2f
          homingDelay = 10f
          hitEffect = Effect(60f) { e ->
            Draw.color(IceColor.b4)
            Lines.stroke(Interp.pow3Out.apply(e.fout()) * 3)
            Lines.poly(e.x, e.y, 8, Interp.pow3Out.apply(e.fin()) * 36 + 36, e.rotation)
          }
          despawnEffect = hitEffect
          despawnHit = true
        }

        override fun removed(b: Bullet) {
          val bc = object : BombBulletType(15f, 40f) {
            init {
              collidesGround = true
              collides = true
              splashDamage = 15f
              collidesTiles = true
              speed = 0f
              collidesAir = true
              drag = 0f
              lifetime = 15f
              despawnEffect = WaveEffect().apply {
                lifetime = 15f
                sizeTo = 15f
                strokeFrom = 4f
                colorFrom = IceColor.b4
                colorTo = IceColor.b4
              }
              hitEffect = despawnEffect
            }

            override fun draw(b: Bullet) {
              Draw.color(IceColor.b4)
              b.vel.set(0f, 0f)
              Drawf.tri(b.x, b.y, 8f, 8f, b.data as Float)
              //  super.draw(b)
            }
          }
          (0 until 15).forEach { i ->
            val x = IceEffects.rand.random(-36, 36)
            val y = IceEffects.rand.random(-36, 36)
            bc.create(
              b, b.team, b.x + x, b.y + y, Random.nextInt(360).toFloat(), -1f, 1f, 1f, Random.nextInt(360).toFloat()
            )
          }
          super.removed(b)
        }

        override fun update(b: Bullet) {
          super.update(b)
          if (Mathf.chanceDelta(1f.toDouble())) {
            val x = IceEffects.rand.random(-8f, 8f)
            val y = IceEffects.rand.random(-8f, 8f)
            IceEffects.layerBullet.at(b.x + x, b.y + y, 0f, IceColor.b4, Random.nextInt(360))
          }
        }

        override fun draw(b: Bullet) {
          drawTrail(b)
          drawParts(b)
          val shrink = shrinkInterp.apply(b.fout())
          val height = this.height * ((1f - shrinkY) + shrinkY * shrink)
          val width = this.width * ((1f - shrinkX) + shrinkX * shrink)
          val mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin())

          Draw.mixcol(mix, mix.a)
          Draw.color(trailColor)
          Drawf.tri(b.x, b.y, width + 2, height, b.rotation())

          Draw.reset()
        }
      }
    }.copyAdd {
      baseRotation = 270f
    }
    abilities.add(BarAbility<ClusterLobesUnit> { unit, bars ->
      bars.add(Bar({ "${IceStats.格挡数量.localized()} ${unit.resistCont}" }, { IceColor.b4 }) { 1f }).row()
      bars.add(Bar({ "${Stat.armor.localized()}" }, { IceColor.b4 }) { unit.armor() / 100 }).row()
      bars.add(Bar({ "${IceStats.伤害减免.localized()} ${(unit.immunity() * 100).toStringi(2)}%" }, { IceColor.b4 }) {
        unit.immunity()
      }).row()
    })
    rotateSpeed = 2f
    drag = 0.05f
    flying = true
    hidden = false
    faceTarget = false
    lowAltitude = true
    drawBody = false
    drawCell = false
    health = 96000f
    armor = 0f
    hitSize = 32f
    speed = 18 / 7.5f
    rotateSpeed = 4f
    range = 8 * 60f
    engineSize = 0f
    itemCapacity = 0
    Vars.content.statusEffects().forEach {
      if (it.speedMultiplier == 1f) return@forEach
      immunities.add(it)
    }
    parts.add(HaloPart().apply {
      mirror = false
      shapes = 4
      radius = 6f
      triLength = 4f
      haloRadius = 16f
      haloRotateSpeed = -1f
    })
    bundle {
      desc(zh_CN, "裂片集群")
    }
  }

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

    var rs = 80f
    var r = 0.5f * rs
    var trailLength = 80
    var trailWidth = 4f
    var trailColor = IceColor.b4

    override fun draw() {
      if (trailLength > 0) {
        val z = Draw.z()
        Draw.z(Layer.effect)
        trails.forEach {
          it.draw(trailColor, trailWidth)
        }
        Draw.z(z)
      }

      //环线
      Draw.z(Layer.effect)

      Draw.color(IceColor.b4)
      Lines.stroke(3f)
      Fill.circle(xx, yy, IMathf.sint(0.5f, 0.1f, 0f, 5f))
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

    fun angleTrns(ang: Float, rad: Float): Vec2 {
      return Vec2(Angles.trnsx(ang, rad), Angles.trnsy(ang, rad))
    }

    val trails = arrayOf(Trail(trailLength), Trail(trailLength))
    var xx = 0f
    var yy = 0f
    override fun update() {
      super.update()


      if (!Vars.headless && trailLength > 0) {
        for (value in trails.withIndex()) {
          val angle0 = Time.time
          val angle1 = -1.1f * angle0
          val xy = angleTrns(angle0, rs)
          val xy2 = angleTrns(angle1, r)

          // 计算目标位置
          val targetX = this.x + xy.x + xy2.x
          val targetY = this.y + xy.y + xy2.y

          // 使用插值平滑移动
          val smoothSpeed = 0.01f // 调整这个值来改变平滑度，值越大移动越快
          this.xx = Mathf.lerpDelta(this.xx, targetX, smoothSpeed)
          this.yy = Mathf.lerpDelta(this.yy, targetY, smoothSpeed)
          value.value.length = trailLength
          value.value.update(xx, yy)
        }
      }

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
}