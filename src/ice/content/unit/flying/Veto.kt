包 ice.content.unit.flying

进口弧光.图形.颜色
进口arc.graphics.g2d.Draw
进口弧.图形.g2d.填充
进口弧.图形.g2d.线条
进口弧.数学.角度
进口arc.math.Interp
进口arc.math.Mathf
进口arc.math.Rand
进口arc.scene.ui.layout.Table
进口arc.util.Time
进口arc.util.Tmp
进口ice.content.IStatus
进口冰。ities。子弹。激光子弹类型
进口ice.ities.bullet.base.BasicBulletType
进口冰.实体.效果.多效果
进口ice.library.IFiles.appendModName
进口ice.library.util.toColor
进口ice.ui.bundle.bundle
进口ice.ui.bundle.desc
进口ice.world.content.unit.IceUnitType
进口mindustry.content.Fx
进口思维产业.实体.效果
进口心智。实体。能力。盾牌重生领域能力
进口思想工业，实体。子弹。ContinuousFlameBulletType
进口心智产业.实体.效果.爆炸效果
进口mindustry.ities.effect.ParticleEffect
进口mindustry.ities.效果.波浪效果
进口mindustry.ities.pattern.ShootHelix
进口mindustry.ities.pattern.ShootPattern
进口mindustry.gen.Bullet
进口mindustry.gen.Hitboxc
进口mindustry.gen.Sounds
进口mindustry.gen.Unit
进口思维工业.图形学。绘图
进口mindustry.graphics.Pal
进口mindustry.type.UnitType
进口mindustry.type。武器
进口java.lang.Float.max

班级否决：IceUnitType("单位_否决") {
Val byb:Color=Pal.BulletYellowBack
Val通过：color=Pal.Bullet黄色

  init {
捆绑{
DESC(
ZH_CN，
        "否决",
        "重型空中突击单位.舰首舰尾发射导弹,四门近程激光与两门远程磁轨炮交替射击,中央主炮投送高爆弹.加装护盾辅助发生器以维持友军护盾持续作战",
        "否决,人类?"
      )
    }
飞行=正确
低空=正确
健康=173000f
铠装=8F
hitSize=87F
速度=0.8F
加速度=0.04f
drag=0.05F
rotateSpeed=0.6F
engineSize=8F
engineOffset=45f
outlineColor="1F1F1F".toColor()
setEnginesMirror(单元发动机(12.25f, -64.25f, 6F, -90F))
tabilities.add(ShieldRegenFieldAbility(450F, 1800f, 160F, 240F))
武器.add(武器("否决主炮").应用{
X=0f
反冲=0f
重新加载=480F
      mirror = 假的
      chargeSound = Sounds.chargeLancer
      shootSound = Sounds.shootLancer
      shoot = ShootPattern().apply {
        firstShotDelay = 120f
      }
      bullet = homingMainBulletType(13f, 210f, 40f, 6f, true, floatArrayOf(33f, 48f, 52f)).apply {
        width = 16f
        height = 32f
        hitColor = byb
        trailWidth = 4f
        trailLength = 12
        trailColor = byb
        chargeEffect = Effect(120f) { e ->
          val rand = Rand()
          rand.setSeed(e.id.toLong())
          Angles.randLenVectors(e.id.toLong(), 24, rand.random(90f, 200f) * Mathf.curve(e.fout(), 0.25f, 1f)) { x, y ->
            Draw.color(byb)
            val rad = rand.random(9f, 18f)
            Fill.circle(e.x + x, e.y + y, e.fin() * rad)
            Draw.color(Color.white)
            Fill.circle(e.x + x, e.y + y, e.fin() * rad / 2)
            Drawf.light(e.x + x, e.y + y, e.fin() * rad * 1.5f, byb, 0.7f)
          }
        }
        hitEffect = Fx.titanExplosion.wrap(byb)
        despawnEffect = Fx.titanExplosion.wrap(byb)
        fragBullets = 3
        fragBullet = BasicBulletType(5f, 50f).apply {
          lifetime = 88f
          drag = -0.01f
          hitColor = byb
          trailWidth = 2f
          trailLength = 8
          trailColor = byb
          hitEffect = ExplosionEffect().apply {
            lifetime = 50f
            waveStroke = 5f
            waveLife = 8f
            waveColor = by
            sparkColor = byb
            smokeColor = byb
            waveRad = 40f
            smokeSize = 4f
            smokes = 7
            smokeSizeBase = 0f
            sparks = 10
            sparkRad = 40f
            sparkLen = 6f
            sparkStroke = 2f
          }
          despawnEffect = Fx.hitSquaresColor
        }

      }
    })

    for (i in Mathf.signs) {
      weapons.add(Weapon("否决副炮").apply {
        x = 22f * i
        y = 60f
        shake = 4f
        recoil = 0f
        shootY = 0f
        reload = 85f
        mirror = false
        shootCone = 5f
        inaccuracy = 10f
        shoot = ShootHelix().apply {
          shots = 4
          shotDelay = 10f
          mag = 3.6f
          scl = 18f
        }
        shootSound = Sounds.shootBeamPlasma

        bullet = homingMainBulletType(2f, 195f, 96f, 6f, false, floatArrayOf(33f * -i, 48f * -i, 52f * -i)).apply {
          width = 12f
          height = 18f
          drag = -0.02f
          hitColor = byb
          trailWidth = 3f
          trailLength = 12
          trailColor = byb
          trailChance = 1f
          trailEffect = ParticleEffect().apply {
            particles = 2
            lifetime = 20f
            length = 10f
            baseLength = 16f
            sizeFrom = 4f
            sizeTo = 0f
            colorFrom = byb
            colorTo = ("F9C27A80").toColor()
            cone = 360f
          }
          weaveMag = 2f
          weaveScale = 6f
          homingDelay = 33f
          homingRange = 108f
          homingPower = 0.08f
          shootEffect = Fx.shootTitan
          smokeEffect = Fx.shootSmokeTitan
          status = IStatus.破甲III
          statusDuration = 150f
          splashDamage = 200f
          splashDamageRadius = 40f
          hitShake = 3f
          hitSound = Sounds.explosionPlasmaSmall

          hitEffect = MultiEffect(ParticleEffect().apply {
            particles = 4
            lifetime = 60f
            sizeFrom = 0f
            sizeTo = 15f
            length = 20f
            baseLength = 48f
            interp = Interp.exp10Out
            sizeInterp = Interp.swingOut
            colorFrom = byb
            colorTo = ("F9C27A00").toColor()
            cone = 360f
          }, ParticleEffect().apply {
            particles = 22
            lifetime = 25f
            line = true
            strokeFrom = 3f
            strokeTo = 0f
            lenFrom = 20f
            lenTo = 0f
            length = 63f
            interp = Interp.exp10Out
            colorFrom = byb
            colorTo = by
            cone = 360f
          }, WaveEffect().apply {
            lifetime = 25f
            sizeFrom = 0f
            sizeTo = 66f
            strokeFrom = 3f
            strokeTo = 0f
            colorFrom = byb
            colorTo = by
          })

          fragBullets = 3
          fragBullet = BasicBulletType(5f, 225f, "arrows").apply {
            lifetime = 96f
            width = 8f
            height = 18f
            shrinkY = 0f
            drag = -0.01f
            hitColor = byb
            pierce = true
            weaveMag = 2f
            weaveScale = 6f
            trailWidth = 2f
            trailLength = 8
            trailColor = byb
            status = IStatus.损毁
            statusDuration = 60f
            hitEffect = Fx.hitSquaresColor
            despawnEffect = Fx.hitSquaresColor
          }

        }

      })
    }

    val arrmorBullet = ArmorBrokenBulletType(17f, 657f, 35f, 1.2f, 1f).apply {
      width = 6f
      height = 35f
      hitColor = byb
      trailColor = byb
      trailLength = 9
      trailWidth = 1.5f
      trailInterval = 3f
      trailRotation = true
      trailEffect = Effect(30f) { e ->
        Draw.color(e.color, Color.white, e.fin())
        val interp = Interp.pow5In.apply(e.fin())
        Lines.stroke((1.5f + 2f * e.fout()) * Interp.pow5In.apply(e.fslope()))
        Lines.ellipse(e.x, e.y, 5f, 16f * interp, 8f * interp, e.rotation - 90f)
      }

      status = IStatus.破甲I
      statusDuration = 120f
      pierceDamageFactor = 0.4f
      hitEffect = ExplosionEffect().apply {
        lifetime = 20f
        waveStroke = 2f
        waveColor = byb
        sparkColor = byb
        waveRad = 12f
        smokeSize = 0f
        smokeSizeBase = 0f
        sparks = 10
        sparkRad = 35f
        sparkLen = 4f
        sparkStroke = 1.5f
      }
      despawnEffect = Fx.hitSquaresColor
      hitSound = Sounds.explosion
      smokeEffect = Fx.shootSmokeSmite
      shootEffect = Fx.shootSmokeSquareBig

    }
    weapons.add(object : Weapon("units_veto-cannon".appendModName()) {
      override fun addStats(u: UnitType, t: Table) {
        super.addStats(u, t)
        t.row()
        t.add("[lightgray]每次穿透衰减[stat]${arrmorBullet.pierceFactor()}%[lightgray]伤害").row()
        t.add("[lightgray]对[stat]护甲[lightgray]额外造成[stat]${arrmorBullet.armorDamage()}倍护甲[lightgray]的伤害并降低[stat]${arrmorBullet.armorReduce()}[lightgray]点护甲")
      }
    }.apply {
      x = 26.5f
      y = 6f
      shake = 3f
      recoil = 2f
      shootY = 4f
      reload = 65f
      rotate = true
      shootCone = 5f
      recoilTime = 85f
      rotateSpeed = 2f
      rotationLimit = 150f
      cooldownTime = 85f
      shootSound = Sounds.shootConquer
      bullet = arrmorBullet
    })

    val engine = Weapon("否决引擎").apply {
      x = 0f
      y = -44f
      reload = 300f
      mirror = false
      shootY = 0f
      baseRotation = 180f
      useAmmo = false
      alwaysShooting = true
      alwaysContinuous = true
      shootSound = Sounds.none
      bullet = ContinuousFlameBulletType(75f).apply {
        colors = arrayOf(("FF58458C").toColor(), ("FF5845B2").toColor(), ("FF8663CC").toColor(), ("FF8663").toColor(), ("FEB380CC").toColor())
        lifetime = 30f
        width = 4f
        length = 45f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f
        hitEffect = MultiEffect(
          ParticleEffect().apply {
            line = true
            particles = 7
            lifetime = 15f
            length = 65f
            cone = 360f
            strokeFrom = 2.5f
            strokeTo = 0f
            lenFrom = 8f
            lenTo = 0f
            colorFrom = "FF5845".toColor()
            colorTo = "FEB380".toColor()
          },
          Fx.hitFlameBeam
        )
      }
    }

    weapons.addAll(lancer(24.25f, 31f), lancer(19.25f, -32.5f), engine)
  }

  fun lancer(wx: Float, wy: Float): Weapon {
    return Weapon("units_veto-closeDefense".appendModName()).apply {
      x = wx
      y = wy
      reload = 60f
      recoil = 2f
      shake = 3f
      rotate = true
      shootCone = 15f
      rotateSpeed = 3f
      alternate = false
      cooldownTime = 60f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(550f).apply {
        length = 320f
        shootEffect = Effect(24f) { e ->
          Draw.color(Pal.redLight, e.color, e.fin())
          for (i in Mathf.signs) {
            Drawf.tri(e.x, e.y, 9f * e.fout(), 72f, e.rotation + 90f * i)
          }
          Drawf.light(e.x, e.y, 180f, e.color, 0.9f * e.fout())
        }
        colors = arrayOf("FEB380".toColor(), "FF8663".toColor(), "FF5845".toColor())
        hitColor = "FF8663".toColor()
        ammoMultiplier = 1f
        status = IStatus.熔融
        statusDuration = 30f
        sideAngle = 22.5f
      }
    }
  }

  fun homingMainBulletType(speed: Float, damage: Float, lifetime: Float, power: Float, mirror: Boolean, angle: FloatArray?): BasicBulletType {
    return object : BasicBulletType(speed, damage) {
      init {
        shrinkY = 0f
        this.lifetime = lifetime
        reflectable = false
      }

      override fun createFrags(b: Bullet, x: Float, y: Float) {
        val e = b.owner as Unit
        for (i in 0 until fragBullets) {
          if (mirror) {
            for (j in Mathf.signs) {
              val ang = e.rotation() + 180 + fragAngle * j + ((i - fragBullets / 2f + 0.5f) * fragSpread) + (angle?.getOrNull(i)?.times(j) ?: 0f)
              fragBullet.create(b, b.team, e.x, e.y, ang, 1f, 1f) { frag ->
                if (frag.time < 40 / frag.type.speed) return@create
                frag.vel.setAngle(Angles.moveToward(frag.rotation(), frag.angleTo(x, y), Time.delta * power))
              }
            }
          } else {
            val ang = e.rotation() + 180 + fragAngle + ((i - fragBullets / 2f + 0.5f) * fragSpread) + (angle?.getOrNull(i) ?: 0f)
            fragBullet.create(b, b.team, e.x, e.y, ang, 1f, 1f) { frag ->
              if (frag.time < 40 / frag.type.speed) return@create
              frag.vel.setAngle(Angles.moveToward(frag.rotation(), frag.angleTo(x, y), Time.delta * power))
              frag.type.updateWeaving(b)
            }
          }
        }
      }
    }
  }

  fun homingBulletType(speed: Float, damage: Float, lifetime: Float, power: Float): BasicBulletType {
    var r = 0f
    var s = false
    var t = 0f

    return object : BasicBulletType(speed, damage) {
      init {
        this.lifetime = lifetime
        reflectable = false
      }

      override fun update(b: Bullet) {
        super.update(b)
        val e = b.owner
        if (e is Unit) {
          r = e.range()
          s = e.isShooting
          t = b.angleTo(e.aimX, e.aimY)
        }
        if (b.dst(e as Unit) > r * 1.2f) b.time = b.lifetime + 1 else if (s) b.vel.setAngle(Angles.moveToward(b.rotation(), t, Time.delta * power))
      }

      override fun draw(b: Bullet) {
        drawTrail(b)
        val type = b.type as BasicBulletType
        Tmp.v1.trns(b.rotation(), type.height / 2)

        for (i in Mathf.signs) {

          Tmp.v2.trns(b.rotation() - 90, type.width * i, -type.height)
          Draw.color(type.backColor)
          Fill.tri(Tmp.v1.x + b.x, Tmp.v1.y + b.y, -Tmp.v1.x + b.x, -Tmp.v1.y + b.y, Tmp.v2.x + b.x, Tmp.v2.y + b.y)
          Draw.color(type.frontColor)
          Fill.tri(Tmp.v1.x / 2 + b.x, Tmp.v1.y / 2 + b.y, -Tmp.v1.x / 2 + b.x, -Tmp.v1.y / 2 + b.y, Tmp.v2.x / 2 + b.x, Tmp.v2.y / 2 + b.y)
        }
      }
    }
  }

  class ArmorBrokenBulletType(speed: Float, damage: Float, lifetime: Float, private val percent: Float, private val num: Float) : BasicBulletType(speed, damage) {
    init {
      shrinkY = 0f
      this.lifetime = lifetime
      ammoMultiplier = 1f
      pierce = true
      pierceArmor = true
      pierceBuilding = true
      pierceDamageFactor = 0.6f
    }

    override fun hitEntity(b: Bullet, entity: Hitboxc, health: Float) {
      if (entity !is Unit) return super.hitEntity(b, entity, health)
      b.damage = damage + max(entity.armor() * percent, 0f)
      entity.armor -= num
      super.hitEntity(b, entity, health)
    }

    fun armorDamage(): Float = percent
    fun armorReduce(): Float = num
    fun pierceFactor(): Float = pierceDamageFactor * 100
  }
}
