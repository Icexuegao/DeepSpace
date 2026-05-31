package ice.content.block.turret

import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import ice.audio.ISounds
import ice.content.IItems
import ice.core.IFiles.appendModName
import ice.entities.bullet.BlockHoleBulletType
import ice.entities.bullet.RandomLightningBulletType
import ice.entities.effect.MultiEffect
import mindustry.Vars
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.entities.UnitSorts
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootPattern
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawPulseShape
import singularity.world.blocks.turrets.SglTurret
import singularity.world.draw.DrawSglTurret
import universecore.util.toColor
import kotlin.math.abs

class 罪碑 :SglTurret("turret_sin_monument") {
  fun AngleTrns(ang: Float, rad: Float, rad2: Float? = null): Vec2 {
    val result = Vec2()

    if (rad2 != null) {
      result.set(Angles.trnsx(ang, rad, rad2), Angles.trnsy(ang, rad, rad2))
    } else {
      result.set(Angles.trnsx(ang, rad), Angles.trnsy(ang, rad))
    }

    return result
  }

  init {
    localization {
      zh_CN {
        localizedName = "罪碑"
        description = "强大的超远程炮塔,通过投射不稳定的压缩黑洞持续杀伤敌人\n引力奇点坍缩器的极度不稳定性"
      }
    }
    warmupSpeed = 0.004f
    health = 256000
    armor = 24f
    size = 16
    chargeSound = ISounds.能量聚合
    shootSound = ISounds.能量释放
    range = 1840f
    shake = 12f
    shootY = 32f
    shootCone = 1f
    rotateSpeed = 0.25f
    recoil = 8f
    recoilTime = 600f
    cooldownTime = 810f
    liquidCapacity = 120f
    canOverdrive = false
    moveWhileCharging = false
    unitSort = UnitSorts.strongest
    ammoUseEffect = Effect(75f) { e ->
      Draw.color(Pal.lightOrange, Pal.lightishGray, Pal.lightishGray, e.fin())
      Draw.alpha(e.fout(0.5f))
      Draw.z(Layer.bullet)

      val rot = abs(e.rotation) + 90f

      for(i in Mathf.signs) {
        val len = (8f + e.finpow() * 40f) * i
        val lr = rot + Mathf.randomSeedRange((e.id + i + 6).toLong(), 20f * e.fin()) * i

        val xy = AngleTrns(lr, len)

        Draw.rect(
          "casing",
          e.x + xy.x + Mathf.randomSeedRange((e.id + i + 7).toLong(), 3f * e.fin()),
          e.y + xy.y + Mathf.randomSeedRange((e.id + i + 8).toLong(), 3f * e.fin()),
          8f,
          16f,
          rot + e.fin() * 50f * i
        )
      }
    }

    shoot = ShootPattern().apply {
      firstShotDelay = 360f
    }

    requirements(
      Category.turret,
      IItems.铜锭, 48000,
      IItems.铅锭, 48000,
      IItems.铬锭, 36000,
      IItems.铱板, 24000,
      IItems.导能回路, 21000,
      IItems.陶钢, 18000,
      IItems.生物钢, 12000,
      IItems.肃正协议, 256
    )
    val FF5845 = Color.valueOf("FF5845")
    val FF8663 = Color.valueOf("FF8663")
    val F03B0E = Color.valueOf("F03B0E")

    drawers = DrawMulti(
      DrawSglTurret().apply {
        // 添加4层双光环
        for(i in 0 until 4) {
          doubleHalo({
            progress = DrawPart.PartProgress.warmup.sin(i * 10f, 20f, 0.6f)
            x = -100f + i * 15f
            y = -68f + i * 28f
            shapeMoveRot = (3 - i) * 20f
            shapes = 1
            radius = 0f
            radiusTo = (24f - i * 4f) * 0.6f
            triLength = 80f - i * 10f
            triLengthTo = 180f - i * 20f
            haloRotation = 20f
          }, 4)
        }

        parts.addAll(
          RegionPart(),
          RegionPart("-barrel-l").apply {
            heatProgress = DrawPart.PartProgress.warmup
            under = true
            moveX = -5.75f
            moveY = -18.5f
            children = Seq.with(
              RegionPart("-top-l").apply {
                progress = DrawPart.PartProgress.warmup.delay(0.8f)
                heatProgress = DrawPart.PartProgress.warmup
                under = true
                moveY = 13.75f
                layerOffset = -0.0001f
                heatColor = F03B0E
              }
            )
            heatColor = F03B0E
            turretHeatLayer = 50f - 0.0001f
          },
          RegionPart("-barrel-r").apply {
            heatProgress = DrawPart.PartProgress.warmup
            under = true
            moveX = 5.75f
            moveY = -18.5f
            children = Seq.with(
              RegionPart("-top-r").apply {
                progress = DrawPart.PartProgress.warmup.delay(0.8f)
                heatProgress = DrawPart.PartProgress.warmup
                under = true
                moveY = 13.75f
                layerOffset = -0.0001f
                heatColor = F03B0E
              }
            )
            heatColor = F03B0E
            turretHeatLayer = 50f - 0.0001f
          },
          RegionPart("-shot").apply {
            heatProgress = DrawPart.PartProgress.warmup
            under = true
            moveY = 14.75f
            turretHeatLayer = 50f - 0.0001f
          },
          RegionPart("-bot").apply {
            under = true
          },
          RegionPart("-arrows").apply {
            progress = DrawPart.PartProgress.recoil
            heatProgress = DrawPart.PartProgress.warmup
            under = true
            y = 15f
            moveY = -15f
            heatColor = F03B0E
          },
          RegionPart("-part").apply {
            heatProgress = DrawPart.PartProgress.warmup
            drawRegion = false
            heatColor = F03B0E
          },
          RegionPart("-column1").apply {
            progress = DrawPart.PartProgress.warmup.delay(0.3f)
            mirror = true
            under = true
            moveX = 2.75f
            moveY = -2.75f
          },
          RegionPart("-column2").apply {
            progress = DrawPart.PartProgress.warmup.delay(0.6f)
            mirror = true
            under = true
            moveX = 2.75f
            moveY = -2.75f
          },
          RegionPart("-bottom").apply {
            mirror = true
            moveX = 13f
            moveY = -12f
          },
          ShapePart().apply {
            progress = DrawPart.PartProgress.warmup.sin(15f, 20f, 0.6f)
            hollow = true
            circle = true
            y = -80f
            radius = 16f
            stroke = 4f
            color = FF5845
            colorTo = FF8663
            layer = 110f
          },
          halo {
            progress = DrawPart.PartProgress.warmup.sin(15f, 20f, 0.6f)
            y = -80f
            mirror = false
            shapes = 4
            radius = 4f
            triLength = 16f
          },
          halo {
            progress = DrawPart.PartProgress.warmup.sin(15f, 20f, 0.6f)
            y = -80f
            radius = 4.8f
            triLength = 18f
            haloRotateSpeed = 1f
            haloRadius = 16f
          })
        doubleHalo({
          progress = DrawPart.PartProgress.warmup.sin(15f, 20f, 0.6f)
          y = -80f
          radius = 3f
          triLength = 24f
          haloRotateSpeed = -1f
          haloRotation = 90f
          haloRadius = 32f
        })
      },
      DrawPulseShape(false).apply {
        color = Color.valueOf("FF9C5A")
        timeScl = 300f
        stroke = 4f
        minStroke = 0f
        layer = 49f
      }
    )

    setAmmo()
    limitRange()
  }

  fun setAmmo() {
    val FF5845 = Color.valueOf("FF5845")
    val FF8663 = Color.valueOf("FF8663")
    val black = Color.black

    // 黑洞命中效果
    val hole = MultiEffect(
      WaveEffect().apply {
        lifetime = 30f
        sizeFrom = 0f
        sizeTo = 48f
        strokeFrom = 6f
        interp = Interp.circleOut
        lightColor = FF5845
        colorFrom = FF5845
        colorTo = FF8663
      },
      object :ParticleEffect() {

        override fun render(e: EffectContainer) {


          super.render(e)
        }
      }.apply {
        particles = 1
        lifetime = 48f
        sizeFrom = 18f
        length = 0f
        layer = 111f
        region = "plasma".appendModName()
        interp = Interp.swingIn
        colorFrom = black
        colorTo = black
      },
      ParticleEffect().apply {
        particles = 1
        lifetime = 48f
        sizeFrom = 20f
        length = 0f
        interp = Interp.swingIn
        colorFrom = FF5845
        colorTo = FF8663
      },
      ParticleEffect().apply {
        line = true
        particles = 18
        lifetime = 24f
        length = 96f
        baseLength = -96f
        strokeFrom = 0f
        strokeTo = 0.8f
        lenFrom = 0f
        lenTo = 4f
        cone = 360f
        interp = Interp.swingIn
        sizeInterp = Interp.exp10Out
        colorFrom = FF5845
        colorTo = FF8663
      }
    )

    // 充能黑洞效果
    val chargeHole = Effect(360f) { e ->
      fun s(to: Float): Float {
        return Interp.elasticOut.apply(0f, to, e.fin(Interp.slope)) * 2f
      }

      Draw.color(FF5845, FF8663, e.fin())
      Fill.circle(e.x, e.y, s(4.5f))

      Draw.color(black)
      Draw.z(111f)
      Draw.rect("plasma".appendModName(), e.x, e.y, s(8f), s(8f))

      val rand = Rand()
      rand.setSeed(e.id.toLong())

      for(i in 0 until 60) {
        val fin = (rand.random(2f) + Time.time / 60f) % 1f
        val fout = 1f - fin
        val angle = rand.random(360f) + (Time.time / 0.5f) % 360f

        Draw.alpha(0.6f * (1f - Mathf.curve(fin, 1f - 0.5f)))

        val xy = AngleTrns(angle, 160f * Interp.slope.apply(fout))
        Fill.circle(
          e.x + xy.x * e.fout(Interp.swing),
          e.y + xy.y * e.fout(Interp.swing),
          4f * Interp.slope.apply(fin) * e.fin()
        )
      }
    }

    // 引力子效果
    val graviton = Effect(15f) { e ->
      Draw.color(FF5845, FF8663, e.fin(Interp.pow10In))

      val rand = Rand()
      val rv = arc.math.geom.Vec2()

      Lines.stroke(Interp.exp10Out.apply(0f, 4f, e.fin()))
      val len = Interp.exp10Out.apply(0f, 16f, e.fin())

      rand.setSeed(e.id.toLong())

      for(i in 0 until 12) {
        val l = 320f * e.fin(Interp.pow10In) - 320f
        rv.trns(e.rotation + rand.range(360f), rand.random(l))
        val x = rv.x
        val y = rv.y

        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), len, true)
        Drawf.light(e.x + x, e.y + y, len * 2f, "FF8663".toColor(), 0.6f * Draw.getColor().a)
      }
    }

    // 黑洞主效果
    val blackhole = Effect(300f) { e ->

      Draw.color(black)
      Draw.z(111f)
      val f = e.fin(Interp.swingIn)
      fun s(to: Float): Float {
        return Interp.elasticOut.apply(0f, to, e.fin(Interp.slope)) * 2f
      }

      Draw.rect("plasma".appendModName(), e.x, e.y, s(160f), s(160f))

     Draw.blend(Blending.additive)
      Draw.color(FF5845, FF8663, e.fin())
      Draw.z(110f)

      Fill.circle(e.x, e.y, s(64f))

      for(i in Mathf.signs) {
        Drawf.tri(
          e.x + (i * 40f * e.fout(f)),
          e.y,
          40f * e.fout(f),
          640f * i * e.fin(Interp.exp10Out),
          i * 180f
        )
      }

      Lines.stroke(8f * e.fout(Interp.exp10Out))
      Lines.circle(e.x, e.y, 180f * e.fout(f))

      if (!Vars.state.isPaused) {
        graviton.at(e.x, e.y)
      }
      Draw.blend(Blending.normal)
    }

    // 主子弹类型
    val mainBullet = RandomLightningBulletType(
      damages = 6000f,
      range = 45f * 8f,
      radius = 40f,
      reload = 15f,
      num = 8,
      effect = hole
    ).apply {
      drawSize = 300f
      damage = 14400f
      lifetime = 320f
      speed = 12f

      chargeEffect = chargeHole
      shootEffect = Effect(120f) { e ->
        Draw.color(Tmp.c1.set(e.color).lerp(Color.valueOf("FF8663"), e.fout()))

        // 绘制4个旋转的三角形
        for(i in 0 until 4) {
          Drawf.tri(
            e.x,
            e.y,
            12f * e.fout(),
            90f,
            e.rotation + 90f * i + e.finpow() * 112f
          )
        }

        // 绘制5层复杂的三角形和方形效果
        for(h in 1..5) {
          val mul = h % 2
          val rm = 1f + mul * 0.5f
          val rot = 90f + (1f - e.finpow()) * Mathf.randomSeed((e.id + (mul * 2)).toLong(), 210f * rm, 360f * rm)

          for(i in 0 until 2) {
            val m = if (i == 0) 1f else 0.5f
            val w = 24f * e.fout() * m
            val length = (8f * 3f / (2f - mul)) * 3f
            val fxPos = Tmp.v1.trns(rot, length - 12f)

            val scaledLength = length * Interp.PowOut(25).apply(e.fout())

            // 在偏移位置绘制三角形对
            Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, scaledLength * m, rot + 180f)
            Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, scaledLength / 3f * m, rot)

            // 半透明绘制中心三角形对
            Draw.alpha(0.5f)
            Drawf.tri(e.x, e.y, w, scaledLength * m, rot + 360f)
            Drawf.tri(e.x, e.y, w, scaledLength / 3f * m, rot)

            // 绘制旋转的方形
            Fill.square(fxPos.x + e.x, fxPos.y + e.y, 3f * e.fout(), rot + 45f)
          }
        }
      }.wrap(FF8663)
      smokeEffect = Effect(70f, 370f) { e ->
        e.scaled(17f) { s ->
          Draw.color(Color.white, Color.lightGray, e.fin())
          Lines.stroke(s.fout() + 0.5f)
          Lines.circle(e.x, e.y, e.fin() * 185f)
        }

        Draw.color(Color.gray)
        Angles.randLenVectors(e.id.toLong(), 12, 5f + 135f * e.finpow()) { x, y ->
          Fill.circle(e.x + x, e.y + y, e.fout() * 22f + 0.5f)
          Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 9f)
        }

        Draw.color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin())
        Lines.stroke(1.5f * e.fout())
        Angles.randLenVectors(e.id.toLong() + 1, 14, 1f + 160f * e.finpow()) { x, y ->
          Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f)
        }
      }

      collidesTiles = false
      homingPower = 0.01f
      homingRange = 360f

      splashDamage = 8400f
      splashDamageRadius = 160f

      trailColor = FF8663
      trailLength = 32
      trailWidth = 8f
      trailSinScl = 5f
      trailSinMag = 0.5f
      trailInterp = Interp.pow10Out

      //hitShake = 480f
      hitEffect = blackhole

      fragBullets = 1
      fragBullet = BlockHoleBulletType(
        600f,
        16f,
        12000f,
        0.5f
      ).apply {
        lifetime = 300f
        splashDamage = 7200f
        splashDamageRadius = 80f
      }
    }

    // 添加子弹部件
    mainBullet.parts.add(
      ShapePart().apply {
        circle = true
        radius = 8f
        radiusTo = 32f
        color = black
        layer = 111f
      },
      ShapePart().apply {
        circle = true
        hollow = true
        radius = 8f
        radiusTo = 36f
        stroke = 2f
        strokeTo = 9f
        color = FF8663
        colorTo = FF8663
        layer = 110f
      }
    )

    newAmmo(mainBullet).apply {
      consume?.apply {
        time(1800f)
        power(8750f)
        liquid(Liquids.cryofluid, 60f)
      }
    }
  }

  fun halo(config: HaloPart.() -> Unit): HaloPart {
    return HaloPart().apply {
      progress = DrawPart.PartProgress.warmup
      mirror = true
      tri = true
      x = 0f
      y = 0f
      moveX = 0f
      moveY = 0f
      shapeMoveRot = 0f
      shapeRotation = 0f
      shapes = 2
      radius = 4f
      radiusTo = -1f
      triLength = 1f
      triLengthTo = -1f
      haloRadius = 0f
      haloRadiusTo = -1f
      haloRotation = 0f
      haloRotateSpeed = 0f
      color = Color.valueOf("FF5845")
      colorTo = Color.valueOf("FF8663")
      layer = 110f

      config(this)
    }
  }

  fun DrawSglTurret.doubleHalo(obj: HaloPart.() -> Unit, num: Int = 4) {
    parts.add(
      halo(obj),
      halo {
        obj(this)
        shapeRotation = 180f
        triLength /= num.toFloat()
        if (triLengthTo != -1f) triLengthTo /= num.toFloat()
      }
    )
  }

}
