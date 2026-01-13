package singularity.contents

import arc.Core
import arc.func.*
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pools
import mindustry.Vars
import mindustry.audio.SoundLoop
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.*
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.ContinuousLaserBulletType
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.HaloPart
import mindustry.entities.part.RegionPart
import mindustry.entities.part.ShapePart
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.Singularity
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.ui.UIUtils
import singularity.util.MathTransform
import singularity.util.func.Floatc3
import singularity.world.SglFx
import singularity.world.SglUnitSorts
import singularity.world.blocks.SglBlock
import singularity.world.blocks.SglBlock.SglBuilding
import singularity.world.blocks.turrets.*
import singularity.world.blocks.turrets.SglTurret.SglTurretBuild
import singularity.world.draw.DrawSglTurret
import singularity.world.draw.part.CustomPart
import singularity.world.meta.SglStat
import singularity.world.particles.SglParticleModels
import singularity.world.particles.SglParticleModels.floatParticle
import universecore.util.funcs.Floatp2
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.LightningVertex
import universecore.world.lightnings.generator.CircleGenerator
import universecore.world.lightnings.generator.LightningGenerator
import universecore.world.lightnings.generator.RandomGenerator
import universecore.world.lightnings.generator.VectorLightningGenerator
import kotlin.math.max
import kotlin.math.min

class SglTurrets : ContentList {
  companion object {
    private val rand = Rand()

    /**碎冰 */
    var crushedIce: BulletType? = null

    /**极寒领域 */
    var freezingField: BulletType? = null

    @JvmField
            /**破碎FEX结晶 */
    var crushCrystal: BulletType? = null

    @JvmField
            /**溢出能量 */
    var spilloverEnergy: BulletType? = null

    /**闪光 */
    var flash: Block? = null

    /**伦琴 */
    var roentgen: Block? = null

    /**红移 */
    var redshift: Block? = null

    /**蓝移 */
    var blueshift: Block? = null

    /**遮幕 */
    var curtain: Block? = null

    /**迷雾 */
    var mist: Block? = null

    /**阴霾 */
    var haze: Block? = null

    /**惊蛰 */
    var thunder: Block? = null

    /**阵雨 */
    var shower: Block? = null

    /**白露 */
    var dew: Block? = null

    /**羿 */
    var yii: Block? = null //super weapon

    /**细雨 */
    var drizzle: Block? = null

    /**春分 */
    var spring: Block? = null

    /**吹雪 */
    var fubuki: Block? = null

    /**霜降 */
    var frost: Block? = null

    /**冬至 */
    var winter: Block? = null

    /**冥王 */
    var pluto: Block? = null //super weapon

    /**虚妄 */
    var mirage: Block? = null

    /**幻象 */
    var illusion: Block? = null

    /**边界 */
    var edge: Block? = null //super weapon

    /**火花 */
    var spark: Block? = null

    /**热流 */
    var heatflux: Block? = null

    @JvmField
            /**阳炎 */
    var soflame: Block? = null

    /**夏至 */
    var summer: Block? = null

    /**耀斑 */
    var sunflare: Block? = null //super weapon

    /**夜空 */
    var night: Block? = null //super weapon
    private val branch = RandomGenerator()

    @JvmOverloads
    fun lightning(lifeTime: Float, damage: Float, size: Float, color: Color?, gradient: Boolean, generator: Func<Bullet?, LightningGenerator>): BulletType {
      return lightning(lifeTime, if (gradient) lifeTime / 2 else 0f, damage, size, color, generator)
    }

    @JvmOverloads
    fun lightning(lifeTime: Float, time: Float, damage: Float, size: Float, color: Color?, generator: Func<Bullet?, LightningGenerator>): BulletType {
      return object : singularity.world.blocks.turrets.LightningBulletType(0f, damage) {
        init {
          lifetime = lifeTime
          collides = false
          hittable = false
          absorbable = false
          reflectable = false

          hitColor = color
          hitEffect = Fx.hitLancer
          shootEffect = Fx.none
          despawnEffect = Fx.none
          smokeEffect = Fx.none

          status = StatusEffects.shocked
          statusDuration = 18f

          drawSize = 120f
        }

        override fun init(b: Bullet, container: LightningContainer) {
          container.time = time
          container.lifeTime = lifeTime
          container.maxWidth = size
          container.minWidth = size * 0.85f
          container.lerp = Interp.linear

          container.trigger = Cons2 { last: LightningVertex?, vert: LightningVertex? ->
            if (!b.isAdded) return@Cons2
            Tmp.v1.set(vert!!.x - last!!.x, vert.y - last.y)
            val resultLength = Damage.findPierceLength(b, pierceCap, Tmp.v1.len())

            Damage.collideLine(b, b.team, b.x + last.x, b.y + last.y, Tmp.v1.angle(), resultLength, false, false, pierceCap)
            b.fdata = resultLength
          }
          val gen: LightningGenerator = generator.get(b)
          gen.blockNow = Floatp2 { last, vertex ->
            val abs = Damage.findAbsorber(b.team, b.x + last.x, b.y + last.y, b.x + vertex.x, b.y + vertex.y) ?: return@Floatp2 -1f
            val ox: Float = b.x + last.x
            val oy: Float = b.y + last.y
            Mathf.len(abs.x - ox, abs.y - oy)
          }
          container.create(gen)
        }
      }
    }
  }

  override fun load() {
    crushedIce = object : BulletType() {
      init {
        lifetime = 45f
        hitColor = SglDrawConst.frost
        hitEffect = SglFx.railShootRecoil
        damage = 18f
        speed = 2f
        collidesGround = true
        collidesAir = false
        pierceCap = 1
        hitSize = 2f
      }

      override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
        if (entity is Healthc) {
          entity.damage(b.damage)
        }

        if (entity is Unit) {
          entity.apply(OtherContents.frost, entity.getDuration(OtherContents.frost) + 6)
        }
      }

      override fun draw(b: Bullet) {
        super.draw(b)

        Draw.color(SglDrawConst.frost)
        SglDraw.drawDiamond(b.x, b.y, 6 * b.fout(), 3 * b.fout(), b.rotation())
      }
    }

    freezingField = object : BulletType() {
      init {
        lifetime = 600f
        hittable = false
        pierce = true
        absorbable = false
        collides = false
        despawnEffect = Fx.none
        hitEffect = Fx.none
        drawSize = 200f
      }

      override fun update(b: Bullet) {
        super.update(b)
        val radius = 200 * b.fout()
        Damage.damage(b.team, b.x, b.y, radius, 12 * Time.delta)

        Vars.control.sound.loop(Sounds.wind, b, 2f)

        if (Mathf.chanceDelta((0.075f * b.fout()).toDouble())) {
          SglFx.particleSpread.at(b.x, b.y, SglDrawConst.winter)
        }

        if (Mathf.chanceDelta((0.25f * b.fout(Interp.pow2Out)).toDouble())) {
          Angles.randLenVectors(Time.time.toLong(), 1, radius) { dx: Float, dy: Float ->
            if (Mathf.chanceDelta(0.7)) {
              SglFx.iceParticle.at(b.x + dx, b.y + dy, (-45 + Mathf.random(-15, 15)).toFloat(), SglDrawConst.frost)
            } else {
              SglFx.iceCrystal.at(b.x + dx, b.y + dy, SglDrawConst.frost)
            }
          }
        }

        Units.nearbyEnemies(b.team, b.x, b.y, radius, Cons { unit: Unit? ->
          unit!!.apply(OtherContents.frost, unit.getDuration(OtherContents.frost) + 2f * Time.delta)
        })
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.z(Layer.flyingUnit + 0.01f)
        Draw.color(SglDrawConst.winter)

        Draw.alpha(0f)
        val lerp = if (b.fin() <= 0.1f) 1 - Mathf.pow(1 - Mathf.clamp(b.fin() / 0.1f), 2f) else Mathf.clamp(b.fout() / 0.9f)
        SglDraw.gradientCircle(b.x, b.y, 215 * lerp, 0.8f)

        Draw.z(Layer.effect)
        Draw.alpha(1f)
        Lines.stroke(2 * lerp)
        SglDraw.dashCircle(b.x, b.y, 200 * b.fout(), 12, 180f, Time.time)
      }
    }

    crushCrystal = object : BulletType() {
      init {
        lifetime = 60f
        hitColor = SglDrawConst.fexCrystal
        hitEffect = SglFx.railShootRecoil
        damage = 48f
        speed = 3.5f
        collidesGround = true
        collidesAir = true
        pierceCap = 2
        hitSize = 2.2f

        trailColor = SglDrawConst.fexCrystal
        trailEffect = SglFx.trailLine
        trailInterval = 3f
        trailRotation = true

        homingRange = 130f
        homingPower = 0.065f
      }

      override fun update(b: Bullet) {
        super.update(b)
        b.vel.x = Mathf.lerpDelta(b.vel.x, 0f, 0.025f)
        b.vel.y = Mathf.lerpDelta(b.vel.y, 0f, 0.025f)
      }

      override fun draw(b: Bullet) {
        drawTrail(b)

        Draw.color(SglDrawConst.fexCrystal)
        SglDraw.drawDiamond(b.x, b.y, 8.6f, 4.4f, b.rotation())
      }
    }

    spilloverEnergy = object : BulletType() {
      init {
        collides = false
        absorbable = false

        splashDamage = 120f
        splashDamageRadius = 40f
        speed = 4.4f
        lifetime = 64f

        hitShake = 4f
        hitSize = 3f

        despawnHit = true
        hitEffect = MultiEffect(
          SglFx.explodeImpWaveSmall,
          SglFx.diamondSpark
        )
        hitColor = SglDrawConst.matrixNet

        trailColor = SglDrawConst.matrixNet
        trailEffect = SglFx.movingCrystalFrag
        trailRotation = true
        trailInterval = 4f

        fragBullet = object : LightningBulletType() {
          init {
            lightningLength = 14
            lightningLengthRand = 4
            damage = 24f
          }
        }
        fragBullets = 1
      }

      override fun update(b: Bullet) {
        super.update(b)

        b.vel.lerp(0f, 0f, 0.012f)

        if (b.timer(4, 3f)) {
          Angles.randLenVectors(
            System.nanoTime(), 2, 2.2f
          ) { x: Float, y: Float -> floatParticle.create(b.x, b.y, SglDrawConst.matrixNet, x, y, 2.2f).strength = 0.3f }
        }
      }

      override fun draw(b: Bullet) {
        Draw.color(hitColor)
        val fout = b.fout(Interp.pow3Out)
        Fill.circle(b.x, b.y, 5f * fout)
        Draw.color(Color.black)
        Fill.circle(b.x, b.y, 2.6f * fout)
      }
    }

    flash = object : SglTurret("flash") {
      init {
        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 35,
            Items.surgeAlloy, 40,
            Items.plastanium, 45
          )
        )
        size = 2

        itemCapacity = 20
        liquidCapacity = 30f
        range = 240f

        shootSound = Sounds.shootSmite
        //copy from smite
        newAmmo(object : BasicBulletType(6f, 72f) {
          init {
            sprite = "large-orb"
            width = 17f
            height = 21f
            hitSize = 8f

            recoilTime = 120f

            shootEffect = MultiEffect(Fx.shootTitan, Fx.colorSparkBig, object : WaveEffect() {
              init {
                colorTo = Pal.accent
                colorFrom = colorTo
                lifetime = 12f
                sizeTo = 20f
                strokeFrom = 3f
                strokeTo = 0.3f
              }
            })
            smokeEffect = Fx.shootSmokeSmite
            ammoMultiplier = 1f
            pierceCap = 3
            pierce = true
            pierceBuilding = true
            trailColor = Pal.accent
            backColor = trailColor
            hitColor = backColor
            frontColor = Color.white
            trailWidth = 2.8f
            trailLength = 9
            hitEffect = Fx.hitBulletColor
            buildingDamageMultiplier = 0.3f

            despawnEffect = MultiEffect(Fx.hitBulletColor, object : WaveEffect() {
              init {
                sizeTo = 30f
                colorTo = Pal.accent
                colorFrom = colorTo
                lifetime = 12f
              }
            })

            trailRotation = true
            trailEffect = Fx.disperseTrail
            trailInterval = 3f

            intervalBullet = object : LightningBulletType() {
              init {
                damage = 18f
                collidesAir = false
                ammoMultiplier = 1f
                lightningColor = Pal.accent
                lightningLength = 5
                lightningLengthRand = 10
                buildingDamageMultiplier = 0.25f
                lightningType = object : BulletType(0.0001f, 0f) {
                  init {
                    lifetime = Fx.lightning.lifetime
                    hitEffect = Fx.hitLancer
                    despawnEffect = Fx.none
                    status = StatusEffects.shocked
                    statusDuration = 10f
                    hittable = false
                    lightColor = Color.white
                    buildingDamageMultiplier = 0.25f
                  }
                }
              }
            }

            bulletInterval = 3f
          }
        }).setReloadAmount(3)
        consume?.apply {
          item(Items.surgeAlloy, 1)
          power(2.4f)
          time(45f)
        }

        newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)
      }
    }

    roentgen =  ProjectileTurret("roentgen") .apply{

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 120,
            SglItems.aerogel, 100,
            SglItems.aluminium, 60,
            SglItems.crystal_FEX, 40,
            Items.silicon, 75,
            Items.surgeAlloy, 45
          )
        )
        size = 4
        range = 240f
        shootY = 12f
        cooldownTime = 60f

        moveWhileCharging = false
        shoot.firstShotDelay = 40f
        shootSound = Sounds.shootLaser

        newAmmo(object : LightLaserBulletType() {
          init {
            length = 240f
            damage = 225f
            empDamage = 180f
            lightColor = Pal.reactorPurple
            chargeEffect = MultiEffect(SglFx.colorLaserChargeBegin, SglFx.colorLaserCharge, Fx.lightningCharge)
            status = StatusEffects.electrified
            statusDuration = 12f
            hitColor = Pal.reactorPurple
            shootEffect = MultiEffect(
              SglFx.crossLightMini,
              Fx.circleColorSpark
            )

            colors = arrayOf<Color?>(
              Pal.reactorPurple.cpy().mul(1f, 1f, 1f, 0.4f),
              Pal.reactorPurple,
              Color.white
            )

            generator.maxSpread = 6f
          }
        })
        consume!!.time(30f)
        consume!!.power(12.4f)

        newAmmoCoating(Core.bundle.get("coating.crystal_fex"), SglDrawConst.fexCrystal, { b: BulletType? ->
          val res = b!!.copy() as LightLaserBulletType
          res.damage *= 1.25f
          res.colors = arrayOf<Color?>(
            SglDrawConst.fexCrystal.cpy().mul(1f, 1f, 1f, 0.4f),
            SglDrawConst.fexCrystal,
            Color.white
          )
          res.lightColor = SglDrawConst.fexCrystal
          res.empDamage *= 0.8f
          res.status = OtherContents.crystallize
          res.statusDuration = 15f
          res
        }, { t: Table? ->
          t!!.add(SglStat.exDamageMultiplier.localized() + 125 + "%")
          t.row()
          t.add(Core.bundle.get("bullet.empDamageMulti") + 80 + "%")
          t.row()
          t.add(OtherContents.crystallize.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"))
        })
        consume!!.time(60f)
        consume!!.liquid(SglLiquids.FEX_liquid, 0.1f)

        newCoolant(1.5f, 20f)
        consume!!.liquid(SglLiquids.phase_FEX_liquid, 0.1f)

        draw = object : DrawSglTurret() {
        }
      }

    curtain =  SglTurret("curtain").apply {

        requirements(
          Category.turret, ItemStack.with(
            Items.titanium, 20,
            Items.graphite, 20,
            Items.lead, 12
          )
        )
        itemCapacity = 20
        range = 144f
        targetGround = false

        newAmmo(object : BasicBulletType(1.6f, 30f, "missile") {
          init {
            frontColor = Items.graphite.color.cpy().lerp(Color.white, 0.7f)
            backColor = Items.graphite.color
            width = 7f
            height = 12f
            lifetime = 90f
            ammoMultiplier = 1f
            hitShake = 0.35f
            scaleLife = true
            splashDamageRadius = 32f
            splashDamage = 12f
            collidesGround = false
            collidesTiles = false
            hitEffect = Fx.explosion
            trailEffect = Fx.smoke
            trailChance = 0.12f
            trailColor = Items.graphite.color

            hitSound = Sounds.explosion

            fragOnHit = true
            fragBullets = 1
            fragVelocityMin = 0f
            fragVelocityMax = 0f
            fragBullet = graphiteCloud(360f, 36f, true, ground = false, empDamage = 0.2f)
          }
        }, true) { bt: Table?, ammo: BulletType? ->
          bt!!.add(Core.bundle.format("bullet.damage", ammo!!.damage))
          bt.row()
          bt.add(Core.bundle.format("bullet.splashdamage", ammo.splashDamage.toInt(), Strings.fixed(ammo.splashDamageRadius / Vars.tilesize, 1)))
          bt.row()
          bt.add(Core.bundle.get("infos.curtainAmmo"))
          bt.row()
          bt.add(OtherContents.electric_disturb.emoji() + "[stat]" + OtherContents.electric_disturb.localizedName + "[lightgray] ~ [stat]2[lightgray] " + Core.bundle.get("unit.seconds"))
        }
        consume!!.item(Items.graphite, 1)
        consume!!.time(90f)
      }


    mist =SglTurret("mist").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 100,
            SglItems.aerogel, 120,
            Items.titanium, 100,
            Items.graphite, 80,
            Items.lead, 85
          )
        )
        size = 3

        itemCapacity = 36
        range = 300f
        minRange = 40f
        inaccuracy = 8f
        targetAir = false
        velocityRnd = 0.2f
        shake = 2.5f
        recoil = 6f
        recoilTime = 120f
        cooldownTime = 120f

        shootY = 0f
        shootEffect = MultiEffect(
          SglFx.crossLightMini,
          object : WaveEffect() {
            init {
              colorFrom = SglDrawConst.frost
              colorTo = Color.lightGray
              lifetime = 12f
              sizeTo = 40f
              strokeFrom = 6f
              strokeTo = 0.3f
            }
          }
        )

        scaledHealth = 180f

        shootSound = Sounds.shootPulsar

        shoot.shots = 4
        newAmmo(object : EmpArtilleryBulletType(3f, 20f) {
          init {
            empDamage = 40f
            empRange = 20f

            knockback = 1f
            lifetime = 80f
            height = 12f
            width = height
            collidesTiles = false
            splashDamageRadius = 20f
            splashDamage = 35f

            damage = 0f

            frontColor = Items.graphite.color.cpy().lerp(Color.white, 0.7f)
            backColor = Items.graphite.color

            fragOnHit = true
            fragBullets = 1
            fragVelocityMin = 0f
            fragVelocityMax = 0f
            fragBullet = graphiteCloud(360f, 40f, true, true, 0.35f)
          }
        }) { t: Table?, b: BulletType? ->
          t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
          t.row()
          t.table { table: Table? ->
            table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.35f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
            table.row()
            table.add(OtherContents.electric_disturb.emoji() + "[stat]" + OtherContents.electric_disturb.localizedName + "[lightgray] ~ [stat]6[lightgray] " + Core.bundle.get("unit.seconds"))
          }.padLeft(15f)
        }
        consume!!.item(Items.graphite, 6)
        consume!!.time(120f)
      }


    haze =  SglTurret("haze").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 180,
            SglItems.aerogel, 180,
            SglItems.matrix_alloy, 120,
            SglItems.uranium_238, 100,
            Items.surgeAlloy, 140,
            Items.graphite, 200
          )
        )
        size = 5

        accurateDelay = false
        accurateSpeed = false
        itemCapacity = 36
        range = 580f
        minRange = 100f
        shake = 7.5f
        recoil = 2f
        recoilTime = 150f
        cooldownTime = 150f

        shootSound = Sounds.shootMissile

        rotateSpeed = 1.25f

        shootY = 4f

        warmupSpeed = 0.015f
        fireWarmupThreshold = 0.94f
        linearWarmup = false

        scaledHealth = 200f
        val type: Func3<Float, Float, Float, EmpBulletType> = Func3 { dam: Float?, empD: Float?, r: Float? ->
          object : EmpBulletType() {
            init {
              lifetime = 180f
              splashDamage = dam!!
              splashDamageRadius = r!!

              damage = 0f
              empDamage = empD!!
              empRange = r

              hitSize = 5f

              hitShake = 16f
              despawnHit = true

              hitEffect = MultiEffect(
                Fx.shockwave,
                Fx.bigShockwave,
                SglFx.explodeImpWaveLarge,
                SglFx.spreadLightning
              )

              homingPower = 0.02f
              homingRange = 240f

              shootEffect = Fx.shootBig
              smokeEffect = Fx.shootSmokeMissile
              trailColor = Pal.redLight
              trailEffect = SglFx.shootSmokeMissileSmall
              trailInterval = 1f
              trailRotation = true
              hitColor = Items.graphite.color

              trailWidth = 3f
              trailLength = 28

              hitSound = Sounds.explosion
              hitSoundVolume = 1.2f

              speed = 0.1f

              fragOnHit = true
              fragBullets = 1
              fragVelocityMin = 0f
              fragVelocityMax = 0f
              fragBullet = object : BulletType(0f, 0f) {
                init {
                  lifetime = 450f
                  collides = false
                  pierce = true
                  hittable = false
                  absorbable = false
                  hitEffect = Fx.none
                  shootEffect = Fx.none
                  despawnEffect = Fx.none
                  smokeEffect = Fx.none
                  drawSize = r * 1.2f
                }

                val branch: RandomGenerator = RandomGenerator()
                val generator: RandomGenerator = RandomGenerator().apply {
                  maxLength = 100f
                  maxDeflect = 55f

                  branchChance = 0.2f
                  minBranchStrength = 0.8f
                  maxBranchStrength = 1f
                  branchMaker = Func2 { vert: LightningVertex?, strength: Float? ->
                    branch.maxLength = 60 * strength!!
                    branch.originAngle = vert!!.angle + Mathf.random(-90, 90)
                    branch
                  }
                }

                override fun init(b: Bullet) {
                  super.init(b)
                  val c = Pools.obtain(LightningContainer.PoolLightningContainer::class.java) { LightningContainer.PoolLightningContainer() }
                  b.data = c
                  c.maxWidth = 6f
                  c.lerp = Interp.linear
                  c.minWidth = 4f
                  c.lifeTime = 60f
                  c.time = 30f
                }

                override fun update(b: Bullet) {
                  super.update(b)
                  Units.nearbyEnemies(b.team, b.x, b.y, r, Cons { u: Unit? -> Sgl.empHealth.empDamage(u, 0.8f, false) })
                  if (b.timer(0, 6f)) {
                    Damage.status(b.team, b.x, b.y, r, OtherContents.electric_disturb, min(450 - b.time, 120f), true, true)
                  }
                  val data = b.data
                  if (data is LightningContainer) {
                    if (b.timer(2, 15 / Mathf.clamp((b.fout() - 0.15f) * 4))) {
                      generator.setOffset(Mathf.random(-45f, 45f), Mathf.random(-45f, 45f))
                      generator.originAngle = Mathf.random(0f, 360f)
                      data.create(generator)
                    }
                    data.update()
                  }
                }

                override fun draw(e: Bullet) {
                  Draw.z(Layer.bullet - 5)
                  Draw.color(Pal.stoneGray)
                  Draw.alpha(0.6f)
                  rand.setSeed(e.id.toLong())
                  Angles.randLenVectors(e.id.toLong(), 8 + 70, r * 1.2f) { x: Float, y: Float ->
                    val size = rand.random(14, 20).toFloat()
                    val i = e.fin(Interp.pow3Out)
                    Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out))
                  }

                  Draw.color(Items.graphite.color)
                  Draw.z(Layer.effect)
                  val data = e.data
                  if (data is LightningContainer) {
                    data.draw(e.x, e.y)
                  }
                }

                override fun removed(b: Bullet) {
                  val data = b.data
                  if (data is LightningContainer) {
                    Pools.free(data)
                  }
                  super.removed(b)
                }
              }
            }

            var regionOutline: TextureRegion? = null

            override fun init(b: Bullet) {
              super.init(b)
              b.data = SoundLoop(Sounds.loopMissileTrail, 0.65f)
            }

            override fun update(b: Bullet) {
              super.update(b)
              Tmp.v1.set(b.vel).setLength(28f)
              b.vel.approachDelta(Tmp.v1, 0.06f * Mathf.clamp((b.fin() - 0.10f) * 5f))
              val data = b.data
              if (data is SoundLoop) {
                data.update(b.x, b.y, true)
              }
            }

            override fun removed(b: Bullet) {
              super.removed(b)
              val data = b.data
              if (data is SoundLoop) {
                data.stop()
              }
            }

            override fun draw(b: Bullet) {
              drawTrail(b)
              Draw.z(Layer.effect + 1)
              Draw.rect(regionOutline, b.x, b.y, b.rotation() - 90)

              SglDraw.drawTransform(b.x, b.y, 0f, 4 * b.fin(), b.rotation() - 90) { x: Float, y: Float, r: Float ->
                Draw.rect(regionOutline, x, y, 4f, 10.5f, r)
              }
              SglDraw.drawTransform(b.x, b.y, 0f, -4f, b.rotation() - 90) { x: Float, y: Float, r: Float ->
                Draw.color(hitColor, 0.75f)
                Fill.circle(x, y, 2.5f)
                Draw.color(Color.white)
                Fill.circle(x, y, 1.5f)
              }
            }

            override fun load() {
              super.load()
              val r = Singularity.getModAtlas("haze_missile")
              /////val p = Core.atlas.getPixmap(r)
              regionOutline = Singularity.getModAtlas("haze_missile") //TextureRegion(Texture(Pixmaps.outline(p, Pal.darkOutline, 3)))
            }
          }
        }

        newAmmo(type.get(480f, 500f, 120f)) { t: Table?, b: BulletType? ->
          t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
          t.row()
          t.table { table: Table? ->
            table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.8f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
            table.row()
            table.add(OtherContents.electric_disturb.emoji() + "[stat]" + OtherContents.electric_disturb.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"))
          }.padLeft(15f)
        }
        consume!!.items(
          *ItemStack.with(
            Items.graphite, 12,
            SglItems.concentration_uranium_235, 1
          )
        )
        consume!!.time(480f)

        newAmmo(type.get(600f, 550f, 145f)) { t: Table?, b: BulletType? ->
          t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
          t.row()
          t.table { table: Table? ->
            table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.5f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
            table.row()
            table.add(OtherContents.electric_disturb.emoji() + "[stat]" + OtherContents.electric_disturb.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"))
          }.padLeft(15f)
        }
        consume!!.items(
          *ItemStack.with(
            Items.graphite, 12,
            SglItems.concentration_plutonium_239, 1
          )
        )
        consume!!.time(510f)

        draw = DrawSglTurret(
          object : RegionPart("_missile") {
            init {
              progress = PartProgress.warmup.mul(PartProgress.reload.inv())
              x = 0f
              y = -4f
              moveY = 8f
            }
          },
          object : RegionPart("_side") {
            init {
              progress = PartProgress.warmup
              mirror = true
              moveX = 4f
              moveY = 2f
              moveRot = -35f

              under = true
              layerOffset = -0.3f
              turretHeatLayer = Layer.turret - 0.2f

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, -10f))
            }
          },
          object : RegionPart("_spine") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup
              mirror = true
              outline = false

              heatColor = Items.graphite.color
              heatLayerOffset = 0f

              xScl = 1.5f
              yScl = 1.5f

              x = 3.3f
              y = 7.3f
              moveX = 10f
              moveY = 5f
              moveRot = -30f

              under = true
              layerOffset = -0.3f
              turretHeatLayer = Layer.turret - 0.2f

              moves.add(PartMove(PartProgress.recoil.delay(0.8f), -1.33f, 0f, 16f))
            }
          },
          object : RegionPart("_spine") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup
              mirror = true
              outline = false

              heatColor = Items.graphite.color
              heatLayerOffset = 0f

              xScl = 1.5f
              yScl = 1.5f

              x = 3.3f
              y = 7.3f
              moveX = 12.3f
              moveY = -2.6f
              moveRot = -45f

              under = true
              layerOffset = -0.3f
              turretHeatLayer = Layer.turret - 0.2f

              moves.add(PartMove(PartProgress.recoil.delay(0.4f), -1.33f, 0f, 24f))
            }
          },
          object : RegionPart("_spine") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup
              mirror = true
              outline = false

              heatColor = Items.graphite.color
              heatLayerOffset = 0f

              xScl = 1.5f
              yScl = 1.5f

              x = 3.3f
              y = 7.3f
              moveX = 13f
              moveY = -9.2f
              moveRot = -60f

              under = true
              layerOffset = -0.3f
              turretHeatLayer = Layer.turret - 0.2f

              moves.add(PartMove(PartProgress.recoil, -1.33f, 0f, 30f))
            }
          },
          object : RegionPart("_blade") {
            init {
              progress = PartProgress.warmup
              mirror = true
              moveX = 2.5f

              heatProgress = PartProgress.warmup
              heatColor = Items.graphite.color

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
            }
          },
          object : RegionPart("_body") {
            init {
              mirror = false
              heatProgress = PartProgress.warmup
              heatColor = Items.graphite.color
            }
          }
        )
      }


    thunder =  SglTurret("thunder").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 180,
            SglItems.aerogel, 150,
            Items.surgeAlloy, 120,
            SglItems.matrix_alloy, 100,
            SglItems.crystal_FEX, 100,
            SglItems.crystal_FEX_power, 80,
            SglItems.iridium, 80
          )
        )
        size = 5
        scaledHealth = 320f
        range = 400f
        val shootRan: Float = range
        warmupSpeed = 0.016f
        linearWarmup = false
        fireWarmupThreshold = 0.8f
        rotateSpeed = 1.6f
        cooldownTime = 90f
        recoil = 3.4f

        energyCapacity = 4096f
        basicPotentialEnergy = 2048f

        shootY = 22f

        shake = 4f
        shootSound = Sounds.shootCollaris

        newAmmo(object : BulletType() {
          init {
            speed = 0f
            lifetime = 60f
            collides = false
            hittable = false
            absorbable = false
            splashDamage = 1460f
            splashDamageRadius = 46f
            damage = 0f
            drawSize = shootRan

            hitColor = Pal.reactorPurple
            shootEffect = MultiEffect(SglFx.impactBubble, SglFx.shootRecoilWave, object : WaveEffect() {
              init {
                colorTo = Pal.reactorPurple
                colorFrom = colorTo
                lifetime = 12f
                sizeTo = 40f
                strokeFrom = 6f
                strokeTo = 0.3f
              }
            })

            hitEffect = Fx.none
            despawnEffect = Fx.none
            smokeEffect = Fx.none
            val g: RandomGenerator = RandomGenerator().apply {
              maxLength = 100f
              maxDeflect = 55f

              branchChance = 0.2f
              minBranchStrength = 0.8f
              maxBranchStrength = 1f
              branchMaker = Func2 { vert: LightningVertex?, strength: Float? ->
                branch.maxLength = 60 * strength!!
                branch.originAngle = vert!!.angle + Mathf.random(-90, 90)
                branch
              }
            }

            fragBullet = lightning(82f, 25f, 42f, 4.8f, Pal.reactorPurple) { b: Bullet? ->
              val u = Units.closest(b!!.team, b.x, b.y, 80f, Boolf { e: Unit? -> true })
              g.originAngle = if (u == null) b.rotation() else b.angleTo(u)
              g
            }
            fragSpread = 25f
            fragOnHit = false
          }

          val generator: VectorLightningGenerator = VectorLightningGenerator().apply {
            maxSpread = 14f
            minInterval = 8f
            maxInterval = 20f

            branchChance = 0.1f
            minBranchStrength = 0.5f
            maxBranchStrength = 0.8f
            branchMaker = Func2 { vert, strength ->
              branch.maxLength = (60 * strength)
              branch.originAngle = vert.angle + Mathf.random(-90, 90)
              branch
            }
          }

          override fun init(b: Bullet) {
            super.init(b)
            val container = Pools.obtain(LightningContainer.PoolLightningContainer::class.java) { LightningContainer.PoolLightningContainer() }
            container.lifeTime = lifetime
            container.minWidth = 5f
            container.maxWidth = 8f
            container.time = 6f
            container.lerp = Interp.linear
            b.data = container

            Tmp.v1.set(b.aimX - b.originX, b.aimY - b.originY)
            val scl = Mathf.clamp(Tmp.v1.len() / shootRan)
            Tmp.v1.setLength(shootRan).scl(scl)
            val shX: Float
            val shY: Float
            val absorber = Damage.findAbsorber(b.team, b.originX, b.originY, b.originX + Tmp.v1.x, b.originY + Tmp.v1.y)
            if (absorber != null) {
              shX = absorber.x
              shY = absorber.y
            } else {
              shX = b.x + Tmp.v1.x
              shY = b.y + Tmp.v1.y
            }

            generator.vector.set(
              shX - b.originX,
              shY - b.originY
            )
            val amount = Mathf.random(5, 7)
            for (i in 0..<amount) {
              container.create(generator)
            }

            Time.run(6f) {
              SglFx.lightningBoltWave.at(shX, shY, Pal.reactorPurple)
              createFrags(b, shX, shY)
              Effect.shake(6f, 6f, shX, shY)
              Sounds.explosion.at(shX, shY, hitSoundPitch, hitSoundVolume)
              Damage.damage(b.team, shX, shY, splashDamageRadius, splashDamage)
            }
          }

          override fun update(b: Bullet) {
            super.update(b)
            (b.data as LightningContainer).update()
          }

          override fun draw(b: Bullet) {
            val container: LightningContainer = b.data as LightningContainer
            Draw.z(Layer.bullet)
            Draw.color(Pal.reactorPurple)
            container.draw(b.x, b.y)
          }

          override fun createSplashDamage(b: Bullet?, x: Float, y: Float) {}

          override fun despawned(b: Bullet?) {}

          override fun removed(b: Bullet) {
            super.removed(b)
            val data = b.data
            if (data is LightningContainer.PoolLightningContainer) {
              Pools.free(data)
            }
          }
        })
        consume!!.item(SglItems.crystal_FEX_power, 2)
        consume!!.energy(2.2f)
        consume!!.time(180f)
        val generator = CircleGenerator().apply {
          radius = 8f
          maxSpread = 2.5f
          minInterval = 2f
          maxInterval = 2.5f
        }

        initialed = Cons { e: SglBuilding ->
          e.CONTAINER = object : LightningContainer() {
            init {
              lifeTime = 45f
              maxWidth = 2f
              lerp = Interp.linear
              time = 0f
            }
          }
        }
        val timeId = timers++
        updating = Cons { e: SglBuilding ->
          if (!Sgl.config.enableLightning) return@Cons
          e.CONTAINER?.update()
          val turret = e as SglTurretBuild
          if (turret.warmup > 0 && e.timer(timeId, 25 / turret.warmup)) {
            e.CONTAINER?.create(generator)
          }
          if (Mathf.chanceDelta((0.03f * turret.warmup).toDouble())) {
            Tmp.v1.set(0f, -16f).rotate(turret.drawrot())
            SglFx.randomLightning.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Pal.reactorPurple)
          }
        }

        newCoolant(1.45f, 20f)
        consume!!.liquid(SglLiquids.phase_FEX_liquid, 0.25f)

        draw = DrawMulti(
          DrawSglTurret(
            object : RegionPart("_center") {
              init {
                moveY = 8f
                progress = PartProgress.warmup
                heatColor = Pal.reactorPurple
                heatProgress = PartProgress.warmup.delay(0.25f)

                moves.add(PartMove(PartProgress.recoil, 0f, -4f, 0f))
              }
            },
            object : RegionPart("_body") {
              init {
                heatColor = Pal.reactorPurple
                heatProgress = PartProgress.warmup.delay(0.25f)
              }
            },
            object : RegionPart("_side") {
              init {
                mirror = true
                moveX = 5f
                moveY = -5f
                progress = PartProgress.warmup
                heatColor = Pal.reactorPurple
                heatProgress = PartProgress.warmup.delay(0.25f)
              }
            },
            object : ShapePart() {
              init {
                color = Pal.reactorPurple
                circle = true
                hollow = true
                stroke = 0f
                strokeTo = 2f
                y = -16f
                radius = 0f
                radiusTo = 10f
                progress = PartProgress.warmup
                layer = Layer.effect
              }
            },
            object : ShapePart() {
              init {
                circle = true
                y = -16f
                radius = 0f
                radiusTo = 3.5f
                color = Pal.reactorPurple
                layer = Layer.effect
                progress = PartProgress.warmup
              }
            },
            object : HaloPart() {
              init {
                progress = PartProgress.warmup
                color = Pal.reactorPurple
                layer = Layer.effect
                y = -16f
                haloRotation = 90f
                shapes = 2
                triLength = 0f
                triLengthTo = 30f
                haloRadius = 10f
                tri = true
                radius = 4f
              }
            },
            object : HaloPart() {
              init {
                progress = PartProgress.warmup
                color = Pal.reactorPurple
                layer = Layer.effect
                y = -16f
                haloRotation = 90f
                shapes = 2
                triLength = 0f
                triLengthTo = 6f
                haloRadius = 10f
                tri = true
                radius = 4f
                shapeRotation = 180f
              }
            },
            object : ShapePart() {
              init {
                circle = true
                y = 22f
                radius = 0f
                radiusTo = 5f
                color = Pal.reactorPurple
                layer = Layer.effect
                progress = PartProgress.warmup
              }
            },
            object : ShapePart() {
              init {
                color = Pal.reactorPurple
                circle = true
                hollow = true
                stroke = 0f
                strokeTo = 1.5f
                y = 22f
                radius = 0f
                radiusTo = 8f
                progress = PartProgress.warmup
                layer = Layer.effect
              }
            }
          ),
          object : DrawBlock() {
            override fun draw(build: Building) {
              DrawBlock.rand.setSeed(build.id.toLong())
              val turret = build as SglTurretBuild
              Draw.z(Layer.effect)
              Draw.color(Pal.reactorPurple)
              Tmp.v1.set(1f, 0f).setAngle(turret.rotationu)
              val sclX = Tmp.v1.x
              val sclY = Tmp.v1.y
              turret.CONTAINER?.draw(turret.x + sclX * 22, turret.y + sclY * 22)
              val step = 45 / 16f
              if (turret.warmup < 0.001f) return
              for (i in 0..15) {
                val x = turret.x + (step * i) * sclX * turret.warmup + 14 * sclX
                val y = turret.y + (step * i) * sclY * turret.warmup + 14 * sclY
                SglDraw.drawRectAsCylindrical(
                  x, y,
                  DrawBlock.rand.random(2, 18) * turret.warmup,
                  DrawBlock.rand.random(1.5f, 10f),
                  (10 + i * 0.75f + DrawBlock.rand.random(8)) * turret.warmup,
                  (Time.time * DrawBlock.rand.random(0.8f, 2f) + DrawBlock.rand.random(360))
                          * (if (DrawBlock.rand.random(1f) < 0.5) -1 else 1),
                  turret.drawrot(),
                  Pal.reactorPurple, Pal.reactorPurple2, Layer.bullet - 0.5f, Layer.effect
                )
              }
            }
          }
        )
      }


    dew = ProjectileTurret("dew").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 150,
            SglItems.aluminium, 110,
            SglItems.aerogel, 120,
            SglItems.matrix_alloy, 160,
            Items.thorium, 100,
            Items.silicon, 85,
            SglItems.uranium_238, 85
          )
        )
        size = 5
        scaledHealth = 360f
        rotateSpeed = 2.5f
        range = 350f
        shootY = 17.4f
        warmupSpeed = 0.035f
        linearWarmup = false
        recoil = 0f
        fireWarmupThreshold = 0.75f
        shootCone = 15f
        shake = 2.2f

        shootSound = Sounds.shootCollaris

        shoot = object : ShootPattern() {
          override fun shoot(totalShots: Int, handler: BulletHandler) {
            val off = totalShots % 2 - 0.5f

            for (i in 0..2) {
              handler.shoot(off * 16, 0f, 0f, firstShotDelay + 3 * i)
            }
          }
        }

        newAmmo(object : BulletType() {
          init {
            damage = 80f
            speed = 8f
            lifetime = 45f
            hitSize = 4.3f
            hitColor = SglDrawConst.matrixNet
            hitEffect = Fx.colorSpark
            despawnEffect = Fx.circleColorSpark
            trailEffect = SglFx.polyParticle
            trailRotation = true
            trailChance = 0.04f
            trailColor = SglDrawConst.matrixNet
            shootEffect = MultiEffect(Fx.shootBig, Fx.colorSparkBig)
            hittable = true
            pierceBuilding = true
            pierceCap = 4
          }

          override fun update(b: Bullet) {
            super.update(b)
            b.damage = b.type.damage + b.type.damage * b.fin() * 0.3f
          }

          override fun draw(b: Bullet) {
            SglDraw.drawDiamond(b.x, b.y, 18f, 6f, b.rotation(), SglDrawConst.matrixNet)
            Draw.color(SglDrawConst.matrixNet)
            for (i in Mathf.signs) {
              Drawf.tri(b.x, b.y, 6f * b.fin(), 20f * b.fin(), b.rotation() + 156f * i)
            }
          }
        })
        consume!!.item(Items.thorium, 1)
        consume!!.time(10f)

        newAmmoCoating(Core.bundle.get("coating.depletedUranium"), Pal.accent, { b: BulletType? ->
          object : WarpedBulletType(b) {
            init {
              damage = b!!.damage * 1.15f
              pierceArmor = true
              pierceCap = 5
            }

            override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
              if (entity is Unit) {
                if (entity.shield > 0) {
                  val damageShield = min(max(entity.shield, 0f), damage * 0.85f)
                  entity.shield -= damageShield
                  Fx.colorSparkBig.at(b.x, b.y, b.rotation(), Pal.bulletYellowBack)
                }
              }
              super.hitEntity(b, entity, health)
            }

            override fun draw(b: Bullet) {
              SglDraw.drawDiamond(b.x, b.y, 24f, 6f, b.rotation(), Pal.accent)
              Draw.color(SglDrawConst.matrixNet)
              for (i in Mathf.signs) {
                Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i)
              }
            }
          }
        }, { t: Table? ->
          t!!.add(SglStat.exDamageMultiplier.localized() + 115 + "%")
          t.row()
          t.add(SglStat.exShieldDamage.localized() + 85 + "%")
          t.row()
          t.add(SglStat.exPierce.localized() + ": 1")
          t.row()
          t.add("@bullet.armorpierce")
        })
        consume!!.time(10f)
        consume!!.item(SglItems.uranium_238, 1)

        newAmmoCoating(Core.bundle.get("coating.crystal_fex"), SglDrawConst.fexCrystal, { b: BulletType? ->
          object : WarpedBulletType(b) {
            init {
              damage = b!!.damage * 1.25f
              hitColor = SglDrawConst.fexCrystal
              trailEffect = SglFx.movingCrystalFrag
              trailInterval = 6f
              trailColor = SglDrawConst.fexCrystal

              status = OtherContents.crystallize
              statusDuration = 15f
            }

            override fun draw(b: Bullet) {
              SglDraw.drawDiamond(b.x, b.y, 24f, 6f, b.rotation(), hitColor)
              Draw.color(SglDrawConst.matrixNet)
              for (i in Mathf.signs) {
                Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i)
              }
            }
          }
        }, { t: Table? ->
          t!!.add(SglStat.exDamageMultiplier.localized() + 125 + "%")
          t.row()
          t.add(OtherContents.crystallize.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"))
        }, 2)
        consume!!.time(20f)
        consume!!.item(SglItems.crystal_FEX, 1)

        draw = DrawSglTurret(
          object : RegionPart("_blade") {
            init {
              mirror = true
              moveX = 4f
              progress = PartProgress.warmup
              heatColor = SglDrawConst.dew
              heatProgress = PartProgress.heat

              moves.add(PartMove(PartProgress.recoil, 0f, -2.6f, 0f))
            }
          },
          object : RegionPart("_side") {
            init {
              mirror = true
              moveX = 8f
              moveRot = -25f
              progress = PartProgress.warmup
              heatColor = SglDrawConst.dew
              heatProgress = PartProgress.warmup.delay(0.25f)

              moves.add(PartMove(PartProgress.recoil, 1f, -1f, -5f))
            }
          },
          object : RegionPart("_body") {
            init {
              heatColor = SglDrawConst.dew
              heatProgress = PartProgress.warmup.delay(0.25f)
            }
          },
          object : ShapePart() {
            init {
              layer = Layer.effect
              color = SglDrawConst.matrixNet
              x = 0f
              y = -16f
              circle = true
              hollow = true
              stroke = 0f
              strokeTo = 1.8f
              radius = 0f
              radiusTo = 8f
              progress = PartProgress.warmup
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              y = -16f
              shapes = 1
              triLength = 16f
              triLengthTo = 46f
              haloRadius = 0f
              tri = true
              radius = 0f
              radiusTo = 4f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              y = -16f
              shapes = 1
              triLength = 8f
              triLengthTo = 20f
              haloRotation = 180f
              haloRadius = 0f
              tri = true
              radius = 0f
              radiusTo = 4f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              y = -16f
              shapes = 2
              haloRotation = 90f
              triLength = 6f
              triLengthTo = 24f
              haloRadius = 0f
              tri = true
              radius = 0f
              radiusTo = 2.5f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.recoil.delay(0.3f)
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              mirror = true
              x = 2f
              y = -6f
              haloRotation = -135f
              shapes = 1
              triLength = 14f
              triLengthTo = 21f
              haloRadius = 10f
              tri = true
              radius = 0f
              radiusTo = 6f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.recoil.delay(0.3f)
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              mirror = true
              x = 2f
              y = -6f
              haloRotation = -135f
              shapes = 1
              triLength = 0f
              triLengthTo = 6f
              haloRadius = 10f
              tri = true
              radius = 0f
              radiusTo = 6f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.recoil.delay(0.3f)
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              mirror = true
              x = 22f
              y = -6f
              haloRotation = -135f
              shapes = 1
              triLength = 8f
              triLengthTo = 16f
              haloRadius = 0f
              tri = true
              radius = 0f
              radiusTo = 4.5f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.recoil.delay(0.3f)
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              mirror = true
              x = 22f
              y = -6f
              haloRotation = -135f
              shapes = 1
              triLength = 0f
              triLengthTo = 4f
              haloRadius = 0f
              tri = true
              radius = 0f
              radiusTo = 4.5f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.recoil.delay(0.3f)
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              mirror = true
              x = 12f
              y = -4f
              haloRotation = -160f
              shapes = 1
              triLength = 12f
              triLengthTo = 20f
              haloRadius = 0f
              tri = true
              radius = 0f
              radiusTo = 5f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.recoil.delay(0.3f)
              color = SglDrawConst.matrixNet
              layer = Layer.effect
              mirror = true
              x = 12f
              y = -4f
              haloRotation = -160f
              shapes = 1
              triLength = 0f
              triLengthTo = 5f
              haloRadius = 0f
              tri = true
              radius = 0f
              radiusTo = 5f
              shapeRotation = 180f
            }
          }
        )

        newCoolant(1f, 0.25f, { l: Liquid? -> l!!.heatCapacity > 0.7f && l.temperature < 0.35f }, 0.4f, 20f)
      }


    spring =  SglTurret("spring").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 120,
            SglItems.aluminium, 140,
            Items.phaseFabric, 80,
            SglItems.matrix_alloy, 100,
            SglItems.chlorella, 120,
            SglItems.crystal_FEX_power, 85,
            SglItems.iridium, 60
          )
        )
        size = 5
        scaledHealth = 450f
        recoil = 1.8f
        rotateSpeed = 3f
        warmupSpeed = 0.022f
        linearWarmup = false
        fireWarmupThreshold = 0.6f
        range = 400f
        targetGround = true
        targetHealUnit = true
        targetAir = true
        targetHealing = true
        shootY = 12f
        shootEffect = Fx.none

        energyCapacity = 4096f
        basicPotentialEnergy = 1024f

        shootSound = Sounds.shootMalign

        shoot = object : ShootPattern() {
          override fun shoot(totalShots: Int, handler: BulletHandler) {
            for (i in intArrayOf(-1, 1)) {
              for (a in 1..2) {
                handler.shoot(0f, 0f, 4.57f * i, 0f) { b: Bullet? ->
                  val len = b!!.time * 5f
                  b.moveRelative(0f, i * (4 * a - 0.01f * a * len) * Mathf.sin(0.04f * len + 4))
                }
              }
            }
          }
        }
        shoot.shots = 2

        newAmmo(object : BulletType() {
          init {
            damage = 42f
            lifetime = 80f
            speed = 5f
            drawSize = 24f
            pierceCap = 4
            pierceBuilding = true
            collidesTeam = true
            smokeEffect = Fx.none
            hitColor = Pal.heal
            hitEffect = Fx.circleColorSpark
            healEffect = SglFx.impactBubble
            shootEffect = Fx.none
            healAmount = 24f
            healPercent = 0.1f
            hitSize = 8f
            trailColor = Pal.heal
            trailRotation = true
            trailEffect = Fx.disperseTrail
            trailInterval = 3f
            trailWidth = hitSize
            trailLength = 24

            hitSound = Sounds.drillImpact
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(Pal.heal)
            Fill.circle(b.x, b.y, b.hitSize)
          }

          override fun update(b: Bullet) {
            super.update(b)
            Units.nearby(b.team, b.x, b.y, b.hitSize, Cons { u: Unit? ->
              if (u!!.damaged() && !b.hasCollided(u.id)) {
                b.collided.add(u.id)

                u.heal(u.maxHealth * (b.type.healPercent / 100) + b.type.healAmount)
                u.apply(OtherContents.spring_coming, 30f)
                b.type.healEffect.at(b.x, b.y, b.rotation(), b.type.healColor)
              }
            })
            Damage.status(b.team, b.x, b.y, b.hitSize, OtherContents.wild_growth, 12f, true, true)
          }
        }) { s: Table?, b: BulletType? ->
          s!!.add(
            (Core.bundle.get("misc.toTeam") + " " + OtherContents.spring_coming.emoji()
                    + "[stat]" + OtherContents.spring_coming.localizedName + "[lightgray] ~ [stat]0.5[lightgray] " + Core.bundle.get("unit.seconds") + "[]"
                    + Sgl.NL + Core.bundle.get("misc.toEnemy") + " " + OtherContents.wild_growth.emoji()
                    + "[stat]" + OtherContents.wild_growth.localizedName + "[lightgray] ~ [stat]0.2[lightgray] " + Core.bundle.get("unit.seconds") + "[]")
          )
        }
        consume!!.energy(2.6f)
        consume!!.time(60f)

        draw = DrawSglTurret(
          object : RegionPart("_side") {
            init {
              mirror = true
              moveX = 2f
              heatColor = Pal.heal
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup.delay(0.25f)

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, -6f))
            }
          },
          object : RegionPart("_blade") {
            init {
              mirror = true
              moveY = 4f
              moveRot = -30f
              heatColor = Pal.heal
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup.delay(0.25f)

              moves.add(PartMove(PartProgress.recoil, -2f, -2f, 0f))
            }
          },
          object : RegionPart("_body") {
            init {
              heatColor = Pal.heal
              heatProgress = PartProgress.warmup.delay(0.25f)
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 10f
              y = 16f
              drawRadius = 0f
              drawRadiusTo = 4f
              rotation = -30f

              moveY = -10f
              moveX = 2f
              moveRot = -35f
              progress = PartProgress.warmup
              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                Draw.color(Pal.heal)
                Drawf.tri(x, y, 4 * p, 6 + 10 * p, r)
                Drawf.tri(x, y, 4 * p, 4 * p, r + 180)
              }

              moves.add(PartMove(PartProgress.recoil, 0f, -1f, -3f))
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 10f
              y = 12f
              drawRadius = 0f
              drawRadiusTo = 4f
              rotation = -30f

              moveY = -10f
              moveX = 2f
              moveRot = -65f
              progress = PartProgress.warmup
              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                Draw.color(Pal.heal)
                Drawf.tri(x, y, 6 * p, 8 + 12 * p, r)
                Drawf.tri(x, y, 6 * p, 6 * p, r + 180)
              }

              moves.add(PartMove(PartProgress.recoil, 0f, -1f, -5f))
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 8f
              y = 16f
              drawRadius = 0f
              drawRadiusTo = 5f
              rotation = -30f

              moveY = -20f
              moveX = 4f
              moveRot = -90f
              progress = PartProgress.warmup
              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                Draw.color(Pal.heal)
                Drawf.tri(x, y, 8 * p, 8 + 16 * p, r)
                Drawf.tri(x, y, 8 * p, 8 * p, r + 180)
              }

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, -6f))
            }
          }
        )
      }


    fubuki = LaserTurret("fubuki").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 100,
            SglItems.aluminium, 140,
            SglItems.crystal_FEX_power, 60,
            SglItems.aerogel, 80,
            SglItems.iridium, 30,
            Items.phaseFabric, 60
          )
        )
        size = 4
        scaledHealth = 400f
        rotateSpeed = 2.4f
        warmupSpeed = 0.01f
        fireWarmupThreshold = 0f
        linearWarmup = false
        range = 300f
        targetGround = true
        targetAir = true

        energyCapacity = 1024f
        basicPotentialEnergy = 256f

        shootY = 12f

        needCooldown = false
        shootingConsume = true

        shootSound = Sounds.none



        newAmmo(object : BulletType() {
          val ice: BulletType = crushedIce!!.copy()
          val shootBullets: Array<BulletType> = arrayOf<BulletType>(
            ice,
            object : BulletType() {
              init {
                damage = 26f
                speed = 8f
                lifetime = 37.5f
                hitColor = Color.white
                despawnEffect = SglFx.cloudGradient

                trailWidth = 1.5f
                trailColor = Color.white
                trailLength = 18

                trailEffect = SglFx.iceParticle
                trailRotation = true
                trailChance = 0.07f

                knockback = 2f
              }

              override fun draw(b: Bullet) {
                super.draw(b)
                Draw.color(hitColor, 0f)

                Draw.z(Layer.flyingUnit + 1)
                SglDraw.gradientCircle(b.x, b.y, 14f, 0.6f)
                SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
                  Draw.color(hitColor)
                  SglDraw.drawDiamond(e.x, e.y, 14f, 6 + Mathf.absin(1f, 2f), e.rotation())
                }
              }

              override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
                super.hitEntity(b, entity, health)
                if (entity is Statusc) {
                  entity.apply(OtherContents.frost, entity.getDuration(OtherContents.frost) + 10f)
                }
              }
            },
            object : BulletType() {
              init {
                damage = 36f
                speed = 6f
                lifetime = 50f
                hitColor = SglDrawConst.frost
                despawnEffect = SglFx.cloudGradient

                trailWidth = 2f
                trailColor = Color.white
                trailLength = 22

                trailEffect = SglFx.particleSpread
                trailRotation = true
                trailChance = 0.06f

                knockback = 4f
              }

              override fun draw(b: Bullet) {
                super.draw(b)
                Draw.color(hitColor, 0f)
                Draw.z(Layer.flyingUnit + 1)
                SglDraw.gradientCircle(b.x, b.y, 14f, 0.6f)

                SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
                  Draw.color(Color.white)
                  Fill.circle(e.x, e.y, 2f)
                  Lines.stroke(1f, hitColor)
                  Lines.circle(e.x, e.y, 4f)
                  val step = 360f / 6
                  for (i in 0..5) {
                    SglDraw.drawTransform(e.x, e.y, 6f, 0f, step * i + Time.time * 2) { x: Float, y: Float, r: Float ->
                      Drawf.tri(x, y, 2.5f, 2.5f, r)
                      Drawf.tri(x, y, 2.5f, 6f, r + 180)
                    }
                  }
                  Draw.reset()
                }
              }

              override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
                super.hitEntity(b, entity, health)
                if (entity is Statusc) {
                  entity.apply(OtherContents.frost, entity.getDuration(OtherContents.frost) + 12f)
                }
              }
            }
          )

          init {
            ice.speed = 10f
            ice.lifetime = 30f
            ice.trailWidth = 1f
            ice.trailLength = 18
            ice.trailColor = SglDrawConst.frost
            ice.knockback = 1f

            speed = 0f
            lifetime = 10f
            rangeOverride = 300f
            despawnEffect = Fx.none
            hittable = false
            collides = false
            absorbable = false
          }

          val trans: Color? = Color.white.cpy().a(0f)

          override fun continuousDamage(): Float {
            var res = 0f
            for (i in shootBullets.indices) {
              res += shootBullets[i].damage * (1f / (i + 1))
            }
            return res * 4
          }

          override fun update(b: Bullet) {
            super.update(b)
            val owner = b.owner
            if (owner is SglTurretBuild && owner.isAdded) {
              b.keepAlive = owner.warmup > 0.01f

              owner.warmup = Mathf.lerpDelta(owner.warmup, (if (owner.wasShooting() && owner.shootValid()) 1 else 0).toFloat(), warmupSpeed)
              owner.reloadCounter = 0f

              if (b.timer(5, if (owner.warmup <= 0.01) Float.MAX_VALUE else 3 / owner.warmup)) {
                for (i in shootBullets.indices) {
                  val bu = shootBullets[i]

                  if (Mathf.chance((1f / (i + 1)).toDouble())) {
                    bu.create(b, b.x, b.y, b.rotation() + Mathf.range(12 * owner.warmup))
                  }
                }
              }
            } else b.remove()
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            val owner = b.owner
            if (owner is SglTurretBuild) {
              Draw.color(SglDrawConst.frost)
              Fill.circle(b.x, b.y, 3 * owner.warmup)
              Lines.stroke(0.7f * owner.warmup)
              SglDraw.dashCircle(b.x, b.y, 4f, Time.time * 1.5f)

              Draw.draw(Draw.z()) {
                rand.setSeed(owner.id.toLong())
                MathRenderer.setDispersion(0.2f * owner.warmup)
                MathRenderer.setThreshold(0.3f, 0.6f)
                MathRenderer.drawOval(
                  b.x, b.y,
                  8 * owner.warmup,
                  3 * owner.warmup,
                  Time.time * rand.random(1.5f, 3f)
                )
                MathRenderer.drawOval(
                  b.x, b.y,
                  9 * owner.warmup,
                  4f * owner.warmup,
                  -Time.time * rand.random(1.5f, 3f)
                )
              }

              Tmp.v1.set(range, 0f).setAngle(owner.rotationu).scl(owner.warmup)
              Tmp.v2.set(Tmp.v1).rotate(owner.warmup * 15)
              Tmp.v1.rotate(-owner.warmup * 15)

              Draw.z(Layer.flyingUnit)
              SglDraw.gradientLine(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, SglDrawConst.frost, trans, 0)
              SglDraw.gradientLine(b.x, b.y, b.x + Tmp.v2.x, b.y + Tmp.v2.y, SglDrawConst.frost, trans, 0)
            }
          }
        })
        consume!!.time(1f)
        consume!!.showTime = false
        consume!!.energy(3.2f)
        consume!!.liquid(Liquids.cryofluid, 0.2f)

        draw = DrawSglTurret(
          object : RegionPart("_blade") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup

              heatColor = SglDrawConst.frost

              moveX = 2f
              moveY = -6f

              mirror = true
            }
          },
          object : RegionPart("_body") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup
              heatColor = SglDrawConst.frost

              moveY = -4f
            }
          }
        )
      }


    frost =  LaserTurret("frost") .apply{

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 160,
            SglItems.aluminium, 110,
            Items.phaseFabric, 100,
            SglItems.matrix_alloy, 120,
            SglItems.crystal_FEX_power, 100,
            SglItems.iridium, 100
          )
        )
        size = 5
        scaledHealth = 420f
        recoil = 2.8f
        rotateSpeed = 2f
        warmupSpeed = 0.02f
        fireWarmupThreshold = 0.9f
        linearWarmup = false
        range = 360f
        targetGround = true
        targetAir = true
        shootEffect = SglFx.railShootRecoil

        energyCapacity = 4096f
        basicPotentialEnergy = 1024f

        shootSound = Sounds.shootLaser


        updating = Cons { e: SglBuilding ->
          val t = e as SglTurretBuild?
          if (Mathf.chanceDelta((0.08f * e!!.warmup()).toDouble())) SglFx.iceParticle.at(
            t!!.x + Angles.trnsx(t.rotationu, -12f),
            t.y + Angles.trnsy(t.rotationu, -12f),
            t.rotationu + 90 * Mathf.randomSign(),
            SglDrawConst.frost
          )
          if (Mathf.chanceDelta((0.05f * e.warmup()).toDouble())) SglFx.iceParticle.at(
            t!!.x + Angles.trnsx(t.rotationu, 22f),
            t.y + Angles.trnsy(t.rotationu, 22f),
            t.rotationu + 15 * Mathf.randomSign(),
            SglDrawConst.frost
          )
        }

        newAmmo(object : ContinuousLaserBulletType() {
          init {
            pierceCap = 5
            damage = 115f
            lifetime = 240f
            damageInterval = 6f
            fadeTime = 30f
            length = 360f
            width = 8f
            hitColor = SglDrawConst.frost
            fragBullet = crushedIce
            fragBullets = 2
            fragSpread = 10f
            fragOnHit = true
            despawnHit = false
            fragRandomSpread = 60f
            incendAmount = 0
            incendChance = 0f
            drawSize = 500f
            pointyScaling = 0.7f
            oscMag = 0.8f
            oscScl = 1.2f
            frontLength = 220f
            lightColor = SglDrawConst.frost
            colors = arrayOf<Color?>(
              Color.valueOf("6CA5FF").a(0.6f),
              Color.valueOf("6CA5FF").a(0.85f),
              Color.valueOf("ACE7FF"),
              Color.valueOf("DBFAFF")
            )
          }

          override fun update(b: Bullet) {
            super.update(b)
            val owner = b.owner
            if (owner is SglTurretBuild) {
              owner.heat = 1f
              owner.curRecoil = owner.heat
              owner.warmup = owner.curRecoil
            }
          }

          override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
            if (entity is Healthc) {
              entity.damage(b.damage)
            }

            if (entity is Unit) {
              entity.apply(OtherContents.frost, entity.getDuration(OtherContents.frost) + 10)
            }
          }
        })
        consume!!.liquid(Liquids.cryofluid, 0.4f)
        consume!!.energy(2.4f)
        consume!!.time(210f)

        draw = DrawSglTurret(
          object : RegionPart("_side") {
            init {
              mirror = true
              moveX = 8f
              moveRot = -22f
              heatColor = SglDrawConst.frost
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup.delay(0.5f)

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, -8f))
            }
          },
          object : RegionPart("_blade") {
            init {
              mirror = true
              moveY = 2f
              moveX = 4f
              moveRot = -24f
              heatColor = SglDrawConst.frost
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup.delay(0.5f)

              moves.add(PartMove(PartProgress.recoil, 0f, -1f, -6f))
            }
          },
          object : CustomPart() {
            init {
              y = 4f
              progress = PartProgress.warmup
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.gradientTri(x, y, 40 + 260 * p, 60 * p, r, SglDrawConst.frost, 0f)
                SglDraw.gradientTri(x, y, 40 * p, 60 * p, r + 180, SglDrawConst.frost, 0f)
              }
            }
          },
          object : RegionPart("_body") {
            init {
              heatColor = SglDrawConst.frost
              heatProgress = PartProgress.warmup.delay(0.5f)
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 16f
              y = 16f
              rotation = -12f

              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.gradientTri(x, y, 8 + 32 * p, 6 * p, r, SglDrawConst.frost, 0f)
                SglDraw.drawDiamond(x, y, 8 + 16 * p, 6 * p, r, SglDrawConst.frost)
              }
              progress = PartProgress.warmup.delay(0.5f)

              moves.add(PartMove(PartProgress.recoil, 0f, -1f, -8f))
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 30f
              y = 4f
              rotation = -45f

              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.gradientTri(x, y, 12 + 36 * p, 6 * p, r, SglDrawConst.frost, 0f)
                SglDraw.drawDiamond(x, y, 12 + 18 * p, 6 * p, r, SglDrawConst.frost)
              }
              progress = PartProgress.warmup.delay(0.5f)

              moves.add(PartMove(PartProgress.recoil, 2f, -1.5f, -9f))
            }
          },
          object : HaloPart() {
            init {
              color = SglDrawConst.frost
              tri = true
              y = -12f
              radius = 0f
              radiusTo = 8f
              triLength = 8f
              triLengthTo = 18f
              haloRadius = 0f
              shapes = 2
              layer = Layer.effect
              progress = PartProgress.warmup
            }
          },
          object : ShapePart() {
            init {
              circle = true
              color = Color.white
              y = 24f
              radius = 0f
              radiusTo = 6f
              layer = Layer.effect
              progress = PartProgress.warmup
            }
          },
          object : ShapePart() {
            init {
              circle = true
              color = SglDrawConst.frost
              y = 24f
              radius = 0f
              radiusTo = 6f
              hollow = true
              stroke = 0f
              strokeTo = 2.5f
              layer = Layer.effect
              progress = PartProgress.warmup
            }
          },
          object : CustomPart() {
            init {
              y = -12f
              layer = Layer.effect
              progress = PartProgress.warmup
              rotation = 90f

              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.drawDiamond(x, y, 20 + 76 * p, 32 * p, r, SglDrawConst.frost, 0f)
              }
            }
          }
        )
      }


    winter =  SglTurret("winter").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 210,
            SglItems.degenerate_neutron_polymer, 80,
            Items.phaseFabric, 180,
            SglItems.iridium, 100,
            SglItems.aerogel, 200,
            SglItems.aluminium, 220,
            SglItems.matrix_alloy, 160,
            SglItems.crystal_FEX_power, 180
          )
        )
        size = 6
        scaledHealth = 410f
        recoil = 3.6f
        rotateSpeed = 1.75f
        warmupSpeed = 0.015f
        shake = 6f
        fireWarmupThreshold = 0.925f
        linearWarmup = false
        range = 560f
        targetGround = true
        targetAir = true
        shootEffect = MultiEffect(
          SglFx.winterShooting,
          SglFx.shootRecoilWave,
          object : WaveEffect() {
            init {
              colorTo = Pal.reactorPurple
              colorFrom = colorTo
              lifetime = 12f
              sizeTo = 40f
              strokeFrom = 6f
              strokeTo = 0.3f
            }
          }
        )
        moveWhileCharging = true
        shootY = 4f

        unitSort = SglUnitSorts.denser

        energyCapacity = 4096f
        basicPotentialEnergy = 4096f

        shoot.firstShotDelay = 120f
        chargeSound = Sounds.chargeLancer
        chargeSoundPitch = 0.9f

        shootSound = Sounds.explosionReactor
        shootSoundPitch = 0.6f
        shootSoundVolume = 2f

        soundPitchRange = 0.05f

        newAmmo(object : BulletType() {
          init {
            lifetime = 20f
            speed = 28f
            collides = false
            absorbable = false
            scaleLife = true
            drawSize = 80f
            fragBullet = object : BulletType() {
              init {
                lifetime = 120f
                speed = 0.6f
                collides = false
                hittable = true
                absorbable = false
                despawnHit = true
                splashDamage = 2180f
                splashDamageRadius = 84f
                hitShake = 12f

                trailEffect = SglFx.particleSpread
                trailInterval = 10f
                trailColor = SglDrawConst.winter

                hitEffect = SglFx.iceExplode
                hitColor = SglDrawConst.winter

                hitSound = Sounds.explosionAfflict
                hitSoundPitch = 0.6f
                hitSoundVolume = 2.5f

                fragBullet = freezingField
                fragOnHit = false
                fragBullets = 1
                fragVelocityMin = 0f
                fragVelocityMax = 0f
              }

              override fun draw(b: Bullet?) {
                super.draw(b)
                Draw.color(SglDrawConst.winter)

                SglDraw.drawBloomUponFlyUnit<Bullet?>(b) { e: Bullet? ->
                  val rot = e!!.fin(Interp.pow2Out) * 3600
                  SglDraw.drawCrystal(
                    e.x, e.y, 30f, 14f, 8f, 0f, 0f, 0.8f,
                    Layer.effect, Layer.bullet, rot, e.rotation(), SglDrawConst.frost, SglDrawConst.winter
                  )

                  Draw.alpha(1f)
                  Fill.circle(e.x, e.y, 18 * e.fin(Interp.pow3In))
                  Draw.reset()
                }
              }

              override fun update(b: Bullet?) {
                super.update(b)
                Vars.control.sound.loop(Sounds.loopPulse, b, 2f)
              }
            }
            fragBullets = 1
            fragSpread = 0f
            fragRandomSpread = 0f
            fragAngle = 0f
            fragOnHit = false
            hitColor = SglDrawConst.winter

            hitEffect = Fx.none
            despawnEffect = Fx.none
            smokeEffect = Fx.none

            trailEffect = MultiEffect(
              SglFx.glowParticle,
              SglFx.railShootRecoil
            )
            trailRotation = true
            trailChance = 1f

            trailLength = 75
            trailWidth = 7f
            trailColor = SglDrawConst.winter

            chargeEffect = SglFx.shrinkIceParticleSmall
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.z(Layer.bullet)
            Draw.color(SglDrawConst.winter)
            val rot = b.fin() * 3600

            SglDraw.drawCrystal(
              b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f,
              Layer.effect, Layer.bullet, rot, b.rotation(), SglDrawConst.frost, SglDrawConst.winter
            )
          }
        }, true) { bt: Table?, ammo: BulletType? ->
          bt!!.add(Core.bundle.format("bullet.splashdamage", ammo!!.fragBullet.splashDamage.toInt(), Strings.fixed(ammo.fragBullet.splashDamageRadius / Vars.tilesize, 1)))
          bt.row()
          bt.add(Core.bundle.get("infos.winterAmmo"))
        }
        consume!!.time(720f)
        consume!!.energy(1.1f)
        consume!!.liquids(
          *LiquidStack.with(
            SglLiquids.phase_FEX_liquid, 0.2f,
            Liquids.cryofluid, 0.2f
          )
        )

        updating = Cons { e: SglBuilding? ->
          val t = e as SglTurretBuild?
          if (Mathf.chanceDelta((0.06f * t!!.warmup).toDouble())) {
            Tmp.v1.set(36f, 0f).setAngle(t.rotationu + 90 * Mathf.randomSign()).rotate(Mathf.random(-30, 30).toFloat())
            SglFx.iceParticle.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Tmp.v1.angle(), SglDrawConst.frost)
          }
        }

        draw = DrawSglTurret(
          object : CustomPart() {
            init {
              progress = PartProgress.warmup
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                Draw.color(SglDrawConst.winter)
                SglDraw.gradientTri(x, y, 70 + 120 * p, 92 * p, r, 0f)
                SglDraw.gradientTri(x, y, 40 + 68 * p, 92 * p, r + 180, 0f)
                Draw.color()
              }
            }
          },
          object : RegionPart("_blade") {
            init {
              mirror = true
              heatColor = SglDrawConst.winter
              heatProgress = PartProgress.warmup.delay(0.3f)
              moveX = 5f
              moveY = 4f
              moveRot = -15f
              progress = PartProgress.warmup

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
            }
          },
          object : RegionPart("_side") {
            init {
              mirror = true
              heatColor = SglDrawConst.winter
              heatProgress = PartProgress.warmup.delay(0.3f)
              moveX = 8f
              moveRot = -30f
              progress = PartProgress.warmup

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, -5f))
            }
          },
          object : RegionPart("_bot") {
            init {
              mirror = true
              heatColor = SglDrawConst.winter
              heatProgress = PartProgress.warmup.delay(0.3f)
              moveX = 6f
              moveY = 2f
              moveRot = -25f
              progress = PartProgress.warmup

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
            }
          },
          object : RegionPart("_body") {
            init {
              heatColor = SglDrawConst.winter
              heatProgress = PartProgress.warmup.delay(0.3f)
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 20f
              drawRadius = 0f
              drawRadiusTo = 20f
              rotation = -30f
              layer = Layer.effect
              progress = PartProgress.warmup
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.drawCrystal(
                  x, y, 8 + 8 * p, 6 * p, 4 * p, 0f, 0f, 0.4f * p,
                  Layer.effect, Layer.bullet - 1, Time.time * 1.24f, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
                )
              }
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 20f
              drawRadius = 0f
              drawRadiusTo = 28f
              rotation = -65f
              layer = Layer.effect
              progress = PartProgress.warmup.delay(0.15f)
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.drawCrystal(
                  x, y, 16 + 21 * p, 12 * p, 8 * p, 0f, 0f, 0.7f * p,
                  Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 45, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
                )
              }
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 20f
              drawRadius = 0f
              drawRadiusTo = 24f
              rotation = -105f
              layer = Layer.effect
              progress = PartProgress.warmup.delay(0.3f)
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.drawCrystal(
                  x, y, 12 + 14 * p, 10 * p, 6 * p, 0f, 0f, 0.6f * p,
                  Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 90, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
                )
              }
            }
          },
          object : CustomPart() {
            init {
              mirror = true
              x = 20f
              drawRadius = 0f
              drawRadiusTo = 20f
              rotation = -135f
              layer = Layer.effect
              progress = PartProgress.warmup.delay(0.45f)
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                SglDraw.drawCrystal(
                  x, y, 9 + 12 * p, 8 * p, 5 * p, 0f, 0f, 0.65f * p,
                  Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 135, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
                )
              }
            }
          },
          object : CustomPart() {
            init {
              progress = PartProgress.charge
              y = 4f
              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                Draw.color(SglDrawConst.winter)
                Drawf.tri(x, y, 10 * p, 12 * p, r)
                Drawf.tri(x, y, 10 * p, 8 * p, r + 180)
                Draw.color(SglDrawConst.frost)
                SglDraw.gradientCircle(x, y, 4 + 12 * p, -7 * p, 0f)
              }
            }
          },
          object : CustomPart() {
            init {
              progress = PartProgress.warmup
              y = -18f
              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                Draw.color(SglDrawConst.frost)
                Lines.stroke(1.8f * p)
                Lines.circle(x, y, 3.5f)
                Draw.alpha(0.7f)

                for (i in 0..5) {
                  SglDraw.drawTransform(x, y, 14 * p, 0f, r + Time.time * 1.5f + i * 60) { dx: Float, dy: Float, dr: Float ->
                    Drawf.tri(dx, dy, 4 * p, 4f, dr)
                    Drawf.tri(dx, dy, 4 * p, 14f, dr + 180f)
                  }
                }

                Draw.color(SglDrawConst.winter)
                val pl = Mathf.clamp((p - 0.3f) / 0.7f)
                for (i in 0..3) {
                  SglDraw.drawTransform(x, y, 16 * pl, 0f, r - Time.time + i * 90) { dx: Float, dy: Float, dr: Float ->
                    SglDraw.drawCrystal(
                      dx, dy, 12f, 8 * pl, 8 * pl, 0f, 0f, 0.5f * pl,
                      Layer.effect, Layer.bullet - 1, Time.time, dr, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
                    )
                  }
                }
              }
            }
          }
        )
      }


    mirage = SglTurret("mirage").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 260,
            SglItems.matrix_alloy, 120,
            SglItems.aerogel, 200,
            SglItems.uranium_238, 160,
            SglItems.iridium, 80,
            SglItems.crystal_FEX, 120
          )
        )
        size = 5

        scaledHealth = 380f
        recoil = 2.8f
        recoilTime = 120f
        rotateSpeed = 2f
        warmupSpeed = 0.023f
        shake = 3.6f
        fireWarmupThreshold = 0.92f
        linearWarmup = false
        range = 480f

        targetAir = true
        targetGround = true

        shootEffect = MultiEffect(
          SglFx.shootRail,
          SglFx.shootRecoilWave
        )
        smokeEffect = Fx.shootSmokeSmite

        shootSound = Sounds.blockExplode1Alt
        shootSoundVolume = 1.4f

        newAmmo(object : MultiTrailBulletType() {
          init {
            damage = 380f
            speed = 8f
            lifetime = 60f

            pierceCap = 4
            pierceBuilding = true

            hitSize = 6f

            knockback = 1.7f

            status = OtherContents.crystallize
            statusDuration = 150f

            hittable = false
            despawnHit = true

            hitEffect = MultiEffect(
              Fx.shockwave,
              SglFx.diamondSpark
            )

            fragOnHit = false
            fragOnAbsorb = true
            fragBullets = 8
            fragBullet = crushCrystal!!.copy()
            fragBullet.homingRange = 160f
            fragBullet.homingPower = 0.1f

            trailColor = SglDrawConst.fexCrystal
            trailWidth = 4f
            trailLength = 18
            trailEffect = Fx.colorSparkBig
            trailChance = 0.24f
            trailRotation = true

            hitColor = SglDrawConst.fexCrystal
            val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
              branchChance = 0.18f
              minBranchStrength = 0.8f
              maxBranchStrength = 1f

              minInterval = 5f
              maxInterval = 15f

              branchMaker = Func2 { vert, strength ->
                branch.maxLength = (40 * strength)
                branch.originAngle = vert.angle + Mathf.random(-90, 90)
                branch
              }
            }

            intervalBullet = lightning(30f, 45f, 4f, SglDrawConst.fexCrystal, true) { b: Bullet? ->
              val e = Units.bestEnemy(b!!.team, b.x, b.y, 80f, Boolf { u: Unit? -> true }, UnitSorts.farthest)
              if (e == null) {
                gen.vector.rnd(Mathf.random(40f, 80f))
              } else gen.vector.set(e.x - b.x, e.y - b.y).add(Mathf.random(-3f, 3f), Mathf.random(-3f, 3f))
              gen
            }
            bulletInterval = 1f
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.z(Layer.bullet)
            Draw.color(SglDrawConst.fexCrystal)
            val rot = b.fin() * 1800

            SglDraw.drawCrystal(
              b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f,
              Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
            )
          }
        }) { t: Table?, b: BulletType? ->
          t!!.add(Core.bundle.format("infos.generateLightning", 60 / b!!.bulletInterval, 45))
        }
        consume!!.item(SglItems.crystal_FEX, 1)
        consume!!.time(60f)

        newAmmo(object : MultiTrailBulletType() {
          init {
            damage = 520f
            speed = 6f
            lifetime = 80f

            pierceCap = 4
            pierceBuilding = true

            hitSize = 8f

            knockback = 1.7f

            subTrails = 3

            absorbable = false
            hittable = false
            despawnHit = true

            hitEffect = MultiEffect(
              Fx.shockwave,
              Fx.bigShockwave,
              SglFx.crossLight,
              SglFx.spreadSparkLarge,
              SglFx.diamondSparkLarge
            )

            fragBullets = 1
            fragBullet = object : singularity.world.blocks.turrets.LightningBulletType() {
              init {
                damage = 42f
                lifetime = 105f
                speed = 6f

                hitColor = SglDrawConst.fexCrystal

                collides = false
                pierceCap = 42
                hittable = false
                absorbable = false

                despawnEffect = MultiEffect(
                  Fx.shockwave,
                  SglFx.diamondSpark
                )

                trailColor = SglDrawConst.fexCrystal
                trailEffect = SglFx.movingCrystalFrag
                trailInterval = 4f
              }

              val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
                minInterval = 6f
                maxInterval = 16f
              }
              val s: singularity.world.blocks.turrets.LightningBulletType = this

              override fun continuousDamage(): Float {
                return damage * 20
              }

              override fun init(b: Bullet?, cont: LightningContainer) {
                super.init(b, cont)
                cont.lifeTime = 16f
                cont.minWidth = 2.5f
                cont.maxWidth = 4.5f
                cont.lerp = Interp.pow2Out
                cont.time = 0f
              }

              override fun update(b: Bullet, container: LightningContainer) {
                super.update(b, container)

                b.vel.x = Mathf.lerpDelta(b.vel.x, 0f, 0.05f)
                b.vel.y = Mathf.lerpDelta(b.vel.y, 0f, 0.05f)

                if (b.timer(4, 3f)) {
                  var tar: Hitboxc? = null
                  var dst = 0f
                  for (unit in Groups.unit.intersect(b.x - 180, b.y - 180, 360f, 360f)) {
                    if (unit.team === b.team || !unit.hasEffect(OtherContents.crystallize)) continue
                    val d = unit.dst(b)
                    if (d > 180) continue

                    if (tar == null || d > dst) {
                      tar = unit
                      dst = d
                    }
                  }

                  if (tar == null) {
                    dst = 0f
                    for (bullet in Groups.bullet.intersect(b.x - 180, b.y - 180, 360f, 360f)) {
                      if (bullet.team !== b.team || bullet.type !== s) continue
                      val d = bullet.dst(b)
                      if (d > 180) continue

                      if (tar == null || d > dst) {
                        tar = bullet
                        dst = d
                      }
                    }
                  }

                  if (tar == null) return

                  gen.vector.set(tar.x() - b.x, tar.y() - b.y)

                  container.create(gen)

                  Damage.collideLine(b, b.team, b.x, b.y, gen.vector.angle(), gen.vector.len(), false, false)
                }
              }

              override fun draw(b: Bullet, c: LightningContainer) {
                super.draw(b, c)
                val rot = b.fin(Interp.pow2Out) * 1800
                SglDraw.drawCrystal(
                  b.x, b.y, 30f, 14f, 9f, 0f, 0f, 0.6f,
                  Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
                )

                Lines.stroke(0.6f * b.fout(), SglDrawConst.fexCrystal)
                SglDraw.dashCircle(b.x, b.y, 180f, 6, 180f, Time.time * 1.6f)
              }

              override fun despawned(b: Bullet) {
                super.despawned(b)

                Damage.damage(b.team, b.x, b.y, 60f, 180f)
              }
            }

            trailColor = SglDrawConst.fexCrystal
            trailWidth = 5f
            trailLength = 22
            trailEffect = Fx.colorSparkBig
            trailChance = 0.24f
            trailRotation = true

            hitColor = SglDrawConst.fexCrystal
            val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
              branchChance = 0.17f
              minBranchStrength = 0.8f
              maxBranchStrength = 1f
              minInterval = 5f
              maxInterval = 15f
              branchMaker = Func2 { vert, strength ->
                branch.maxLength = (40 * strength)
                branch.originAngle = vert.angle + Mathf.random(-90, 90)
                branch
              }
            }
            intervalBullet = lightning(30f, 60f, 4f, SglDrawConst.fexCrystal, true) { b: Bullet? ->
              val e = Units.bestEnemy(b!!.team, b.x, b.y, 80f, Boolf { u: Unit? -> true }, UnitSorts.farthest)
              if (e == null) {
                gen.vector.rnd(Mathf.random(40f, 80f))
              } else gen.vector.set(e.x - b.x, e.y - b.y).add(Mathf.random(-3f, 3f), Mathf.random(-3f, 3f))
              gen
            }
            bulletInterval = 1.5f
          }

          override fun draw(b: Bullet) {
            super.draw(b)

            Draw.z(Layer.bullet)
            Draw.color(SglDrawConst.fexCrystal)
            val rot = b.fin() * 1800

            SglDraw.drawCrystal(
              b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f,
              Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
            )
          }

          override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
            super.hitEntity(b, entity, health)

            if (b.vel.len() > 0.3f) {
              b.time -= b.vel.len()
            }
            b.vel.scl(0.6f)

            if (entity is Unit && entity.hasEffect(OtherContents.crystallize)) {
              for (i in 0..4) {
                val len = Mathf.random(1f, 7f)
                val a = b.rotation() + Mathf.range(fragRandomSpread / 2) + fragAngle + ((i - 2) * fragSpread)
                crushCrystal!!.create(
                  b,
                  entity.x + Angles.trnsx(a, len),
                  entity.y + Angles.trnsy(a, len),
                  a,
                  Mathf.random(fragVelocityMin, fragVelocityMax),
                  Mathf.random(fragLifeMin, fragLifeMax)
                )
              }
            }
          }
        }, true) { table: Table?, b: BulletType? ->
          table!!.add(Core.bundle.format("bullet.damage", b!!.damage))
          table.row()
          table.add(Core.bundle.format("bullet.pierce", b.pierceCap))
          table.row()
          table.add(Core.bundle.format("bullet.frags", b.fragBullets))
          table.row()
          table.table { t: Table? ->
            t!!.add(
              Core.bundle.format(
                "infos.mirageLightningDamage",
                Strings.autoFixed(180f / Vars.tilesize, 1),
                (b.fragBullet.damage * 20).toString() + StatUnit.perSecond.localized(),
                OtherContents.crystallize.emoji() + OtherContents.crystallize.localizedName
              )
            )
          }.left().padLeft(15f)
          table.row()
          table.add(Core.bundle.format("infos.generateLightning", 60 / b.bulletInterval, 60))
        }
        consume!!.item(SglItems.crystal_FEX_power, 2)
        consume!!.time(120f)

        draw = DrawSglTurret(
          object : RegionPart("_shooter") {
            init {
              mirror = false
              heatProgress = PartProgress.warmup
              heatColor = SglDrawConst.fexCrystal

              progress = PartProgress.recoil

              moveY = -4f
            }
          },
          object : RegionPart("_side") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup

              heatColor = SglDrawConst.fexCrystal
              mirror = true

              moveX = 8f
              moveRot = -35f

              moves.add(PartMove(PartProgress.recoil, 0f, 0f, -10f))
            }
          },
          object : RegionPart("_blade") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup

              heatColor = SglDrawConst.fexCrystal
              mirror = true

              moveX = 2f
              moveY = -4f
              moveRot = 15f

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, 5f))
            }
          },
          object : RegionPart("_body") {
            init {
              heatProgress = PartProgress.warmup
              heatColor = SglDrawConst.fexCrystal

              mirror = false
            }
          }
        )
      }


    soflame = SglTurret("soflame").apply {

        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 150,
            SglItems.aluminium, 180,
            SglItems.crystal_FEX, 140,
            SglItems.crystal_FEX_power, 120,
            SglItems.aerogel, 180,
            SglItems.iridium, 60,
            Items.surgeAlloy, 120,
            Items.phaseFabric, 100
          )
        )
        size = 5
        recoil = 4f
        recoilTime = 120f
        rotateSpeed = 1.5f
        shootCone = 3f
        warmupSpeed = 0.018f
        fireWarmupThreshold = 0.9f
        linearWarmup = false
        range = 360f
        shootY = 8f
        shake = 8f

        energyCapacity = 4096f
        basicPotentialEnergy = 2048f

        shootEffect = SglFx.shootRail
        shootSound = Sounds.shootSmite
        smokeEffect = Fx.shootSmokeSmite

        unitSort = SglUnitSorts.denser
        val subBullet: BulletType = object : HeatBulletType() {
          init {
            speed = 4f
            lifetime = 90f

            damage = 0f
            splashDamage = 90f
            splashDamageRadius = 8f

            meltDownTime = 30f
            melDamageScl = 0.5f
            maxExDamage = 150f

            trailColor = Pal.lighterOrange
            hitColor = Pal.lighterOrange
            trailEffect = SglFx.glowParticle
            trailChance = 0.1f
            trailRotation = true

            hitEffect = MultiEffect(
              object : WaveEffect() {
                init {
                  colorFrom = Pal.lighterOrange
                  colorTo = Color.white
                  lifetime = 12f
                  sizeTo = 28f
                  strokeFrom = 6f
                  strokeTo = 0.3f
                }
              },
              Fx.circleColorSpark
            )
            despawnEffect = Fx.absorb
            despawnHit = true

            trailWidth = 2f
            trailLength = 24
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(hitColor)
            Fill.circle(b.x, b.y, 3f)
          }
        }
        newAmmo(object : HeatBulletType() {
          init {
            damage = 260f
            splashDamage = 540f
            splashDamageRadius = 32f
            hitSize = 5f
            speed = 4f
            lifetime = 90f

            hitShake = 14f

            hitColor = Pal.lighterOrange
            trailColor = Pal.lighterOrange

            hitSound = Sounds.explosion
            hitSoundVolume = 4f

            trailEffect = SglFx.trailParticle
            trailChance = 0.1f

            hitEffect = MultiEffect(
              object : WaveEffect() {
                init {
                  colorTo = Pal.lighterOrange
                  colorFrom = colorTo
                  lifetime = 12f
                  sizeTo = 50f
                  strokeFrom = 7f
                  strokeTo = 0.3f
                }
              },
              SglFx.explodeImpWaveLarge,
              SglFx.impactBubble
            )

            meltDownTime = 90f
            melDamageScl = 0.3f
          }

          override fun init(b: Bullet) {
            super.init(b)
            val p = SglParticleModels.heatBulletTrail.create(b.x, b.y, Pal.lighterOrange, 0f, 0f, 5f)
            p.owner = b
            p.bullet = SglParticleModels.defHeatTrailHitter.create(b, b.x, b.y, b.rotation())

            Tmp.v1.set(1f, 0f).setAngle(b.rotation())
            for (i in 0..3) {
              val off = Mathf.random(0f, Mathf.PI2)
              val scl = Mathf.random(3f, 6f)
              val x = b.x
              val y = b.y
              Time.run((i * 5).toFloat()) {
                for (sign in Mathf.signs) {
                  subBullet.create(b, x, y, b.rotation())
                    .mover = Mover { e: Bullet? -> e!!.moveRelative(0f, Mathf.sin(e.time + off, scl, ((1 + i) * sign).toFloat())) }
                }
              }
            }
          }
        }) { t: Table?, b: BulletType? ->
          t!!.table { child: Table? ->
            child!!.left().add(Core.bundle.format("infos.shots", 6)).color(Color.lightGray).left()
            UIUtils.buildAmmo(child, subBullet)
          }.padLeft(15f)
        }
        consume!!.time(180f)
        consume!!.energy(5f)

        draw = object : DrawSglTurret(
          object : RegionPart("_blade") {
            init {
              progress = PartProgress.warmup
              heatProgress = PartProgress.warmup
              mirror = true
              moveX = 4f
              heatColor = Pal.lightishOrange

              moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
            }
          },
          object : RegionPart("_body") {
            init {
              mirror = false
              heatProgress = PartProgress.warmup
              heatColor = Pal.lightishOrange
            }
          },
          object : ShapePart() {
            init {
              progress = PartProgress.warmup
              y = shootY
              circle = true
              radius = 0f
              radiusTo = 4f
              layer = Layer.effect
            }
          },
          object : CustomPart() {
            init {
              progress = PartProgress.warmup
              y = shootY
              layer = Layer.effect
              draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
                Lines.stroke(0.8f * p, Pal.lighterOrange)
                SglDraw.dashCircle(x, y, 6 * p, Time.time * 1.7f)
              }
            }
          },
          object : ShapePart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              circle = true
              y = -18f
              radius = 0f
              radiusTo = 4f
            }
          },
          object : ShapePart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              circle = true
              hollow = true
              y = -18f
              stroke = 0f
              strokeTo = 2f
              radius = 0f
              radiusTo = 10f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              tri = true
              y = -18f
              haloRadius = 10f
              haloRotateSpeed = 1f
              shapes = 4
              radius = 4f
              triLength = 0f
              triLengthTo = 8f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              tri = true
              y = -18f
              haloRadius = 10f
              haloRotateSpeed = 1f
              shapes = 4
              radius = 4f
              triLength = 0f
              triLengthTo = 4f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              tri = true
              shapes = 2
              haloRadius = 10f
              haloRotation = 90f
              radius = 5f
              triLength = 0f
              triLengthTo = 30f
              shapeRotation = 0f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              tri = true
              shapes = 2
              haloRadius = 10f
              haloRotation = 90f
              radius = 5f
              triLength = 0f
              triLengthTo = 5f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup.delay(0.2f)
              color = Pal.lighterOrange
              layer = Layer.effect
              y = 0f
              tri = true
              shapes = 2
              haloRadius = 18f
              haloRotation = 90f
              radius = 4f
              triLength = 0f
              triLengthTo = 20f
              shapeRotation = 0f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup.delay(0.2f)
              color = Pal.lighterOrange
              layer = Layer.effect
              y = 0f
              tri = true
              shapes = 2
              haloRadius = 18f
              haloRotation = 90f
              radius = 4f
              triLength = 0f
              triLengthTo = 4f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup.delay(0.4f)
              color = Pal.lighterOrange
              layer = Layer.effect
              y = 8f
              tri = true
              shapes = 2
              haloRadius = 15f
              haloRotation = 90f
              radius = 4f
              triLength = 0f
              triLengthTo = 16f
              shapeRotation = 0f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup.delay(0.4f)
              color = Pal.lighterOrange
              layer = Layer.effect
              y = 8f
              tri = true
              shapes = 2
              haloRadius = 15f
              haloRotation = 90f
              radius = 4f
              triLength = 0f
              triLengthTo = 4f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup.delay(0.6f)
              color = Pal.lighterOrange
              layer = Layer.effect
              y = 16f
              tri = true
              shapes = 2
              haloRadius = 12f
              haloRotation = 90f
              radius = 4f
              triLength = 0f
              triLengthTo = 12f
              shapeRotation = 0f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup.delay(0.6f)
              color = Pal.lighterOrange
              layer = Layer.effect
              y = 16f
              tri = true
              shapes = 2
              haloRadius = 12f
              haloRotation = 90f
              radius = 4f
              triLength = 0f
              triLengthTo = 4f
              shapeRotation = 180f
            }
          }
        ) {
          val param: FloatArray = FloatArray(9)

          override fun draw(build: Building) {
            super.draw(build)

            Draw.z(Layer.effect)
            DrawSglTurret.rand.setSeed(build.id.toLong())
            SglDraw.drawTransform(build.x, build.y, shootX, shootY, build.drawrot()) { ox: Float, oy: Float, rot: Float ->
              for (i in 0..2) {
                val bool = DrawSglTurret.rand.random(1f) > 0.5f
                for (d in 0..2) {
                  param[d * 3] = DrawSglTurret.rand.random(4f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
                  param[d * 3 + 1] = DrawSglTurret.rand.random(360f)
                  param[d * 3 + 2] = DrawSglTurret.rand.random(6f) / ((d + 1) * (d + 1))
                }
                val v = MathTransform.fourierSeries(Time.time, *param)

                v.add(ox, oy)
                Draw.color(Pal.lighterOrange)
                Fill.circle(v.x, v.y, 1.3f * build.warmup())
              }
            }
          }
        }
      }


    summer = object : SglTurret("summer") {
      init {
        requirements(
          Category.turret, ItemStack.with(
            SglItems.strengthening_alloy, 210,
            SglItems.degenerate_neutron_polymer, 80,
            Items.phaseFabric, 180,
            SglItems.iridium, 120,
            SglItems.aerogel, 240,
            SglItems.matrix_alloy, 140,
            SglItems.crystal_FEX_power, 150,
            SglItems.crystal_FEX, 100
          )
        )
        size = 6
        accurateDelay = false
        accurateSpeed = false
        scaledHealth = 410f
        recoil = 2f
        recoilTime = 120f
        rotateSpeed = 2f
        shootCone = 45f
        warmupSpeed = 0.025f
        fireWarmupThreshold = 0.85f
        linearWarmup = false
        range = 500f
        targetGround = true
        targetAir = true
        shootY = 8f
        shake = 2f

        energyCapacity = 4096f
        basicPotentialEnergy = 4096f

        unitSort = UnitSorts.strongest

        shootSound = Sounds.shootReign
        shootSoundPitch = 2f

        shoot = object : ShootPattern() {
          override fun shoot(totalShots: Int, handler: BulletHandler) {
            var i = 0
            while (i < shots) {
              for (sign in Mathf.signs) {
                Tmp.v1.set(sign.toFloat(), 1f).setLength(Mathf.random(2.5f)).scl(Mathf.randomSign().toFloat())
                handler.shoot(12 * sign + Tmp.v1.x, Tmp.v1.y, (-45 * sign + Mathf.random(-20, 20)).toFloat(), i / 2f * shotDelay) { b: Bullet? ->
                  if (b!!.owner is SglTurretBuild && (b.owner as SglTurretBuild).wasShooting()) {
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo((b.owner as SglTurretBuild).targetPos), b.type.homingPower * Time.delta * 50f))
                  }
                }
              }
              i += 2
            }
          }
        }
        shoot.shots = 12
        shoot.shotDelay = 5f

        newAmmo(object : HeatBulletType() {
          init {
            speed = 4.5f
            lifetime = 180f
            damage = 85f
            hitSize = 2f
            homingPower = 0.06f
            trailEffect = SglFx.glowParticle
            trailRotation = true
            trailChance = 0.12f
            trailColor = Pal.lightishOrange.cpy().a(0.7f)
            hitColor = Pal.lightishOrange
            shootEffect = Fx.shootSmallColor
            hitEffect = MultiEffect(
              Fx.absorb,
              Fx.circleColorSpark
            )
            smokeEffect = Fx.none
            despawnEffect = Fx.none
            despawnHit = false
            trailWidth = 2f
            trailLength = 26

            hitSound = Sounds.mechStep
            hitSoundPitch = 2f
            hitSoundVolume = 1.6f

            meltDownTime = 12f
            melDamageScl = 0.3f
            maxExDamage = 120f
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.z(Layer.bullet)
            Draw.color(Pal.lighterOrange)
            val fout = b.fout(Interp.pow4Out)
            val z = Draw.z()
            Draw.z(z - 0.0001f)
            b.trail.draw(trailColor, trailWidth * fout)
            Draw.z(z)

            SglDraw.drawLightEdge(b.x, b.y, 35 * fout + Mathf.absin(0.5f, 3.5f), 2f, 14 * fout + Mathf.absin(0.4f, 2.5f), 2f, 30f, Pal.lightishOrange)
            SglDraw.drawDiamond(b.x, b.y, 16 * fout + Mathf.absin(0.6f, 2f), 2f, 90f, Pal.lightishOrange)
            Fill.circle(b.x, b.y, 2.2f * fout)
          }

          override fun drawTrail(b: Bullet?) {}

          override fun removed(b: Bullet?) {}
        })
        consume!!.energy(5f)
        consume!!.time(60f)

        draw = DrawSglTurret(
          object : RegionPart("_side") {
            init {
              mirror = true
              moveX = 4f
              progress = PartProgress.warmup
              heatColor = Pal.lightishOrange
              heatProgress = PartProgress.warmup.delay(0.25f)
            }
          },
          object : RegionPart("_bot") {
            init {
              mirror = true
              moveY = -4f
              moveX = 2f
              progress = PartProgress.warmup
              heatColor = Pal.lightishOrange
              heatProgress = PartProgress.warmup.delay(0.25f)
            }
          },
          object : RegionPart("_body") {
            init {
              progress = PartProgress.recoil
              heatProgress = PartProgress.warmup.delay(0.25f)
              heatColor = Pal.lightishOrange
              moveY = -4f
            }
          },
          object : RegionPart("_blade") {
            init {
              mirror = true
              moveX = 2f
              moveY = 8f
              moveRot = -45f
              progress = PartProgress.warmup
              heatColor = Pal.lightishOrange
              heatProgress = PartProgress.warmup.delay(0.25f)
            }
          },
          object : ShapePart() {
            init {
              color = Pal.lighterOrange
              circle = true
              hollow = true
              stroke = 0f
              strokeTo = 2f
              y = -18f
              radius = 0f
              radiusTo = 12f
              progress = PartProgress.warmup
              layer = Layer.effect
            }
          },
          object : ShapePart() {
            init {
              circle = true
              y = -18f
              radius = 0f
              radiusTo = 3.5f
              color = Pal.lighterOrange
              layer = Layer.effect
              progress = PartProgress.warmup
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              haloRotation = 90f
              shapes = 2
              triLength = 0f
              triLengthTo = 32f
              haloRadius = 0f
              haloRadiusTo = 12f
              tri = true
              radius = 2f
              radiusTo = 5f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              haloRotation = 90f
              shapes = 2
              triLength = 0f
              triLengthTo = 8f
              haloRadius = 0f
              haloRadiusTo = 12f
              tri = true
              radius = 2f
              radiusTo = 5f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              haloRotation = 0f
              haloRotateSpeed = 1f
              shapes = 2
              triLength = 0f
              triLengthTo = 10f
              haloRadius = 16f
              tri = true
              radius = 6f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              haloRotation = 0f
              haloRotateSpeed = 1f
              shapes = 2
              triLength = 0f
              triLengthTo = 6f
              haloRadius = 16f
              tri = true
              radius = 6f
              shapeRotation = 180f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              haloRotation = 0f
              haloRotateSpeed = -1f
              shapes = 4
              triLength = 0f
              triLengthTo = 4f
              haloRadius = 12f
              tri = true
              radius = 5f
            }
          },
          object : HaloPart() {
            init {
              progress = PartProgress.warmup
              color = Pal.lighterOrange
              layer = Layer.effect
              y = -18f
              haloRotation = 0f
              haloRotateSpeed = -1f
              shapes = 4
              triLength = 0f
              triLengthTo = 6f
              haloRadius = 12f
              tri = true
              radius = 5f
              shapeRotation = 180f
            }
          }
        )
      }
    }
  }

  fun graphiteCloud(lifeTime: Float, size: Float, air: Boolean, ground: Boolean, empDamage: Float): BulletType {
    return object : BulletType(0f, 0f) {
      init {
        lifetime = lifeTime
        collides = false
        pierce = true
        hittable = false
        absorbable = false
        hitEffect = Fx.none
        shootEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none
        drawSize = size
      }

      override fun update(b: Bullet) {
        super.update(b)
        if (empDamage > 0) Units.nearbyEnemies(b.team, b.x, b.y, size, Cons { u: Unit? -> Sgl.empHealth.empDamage(u, empDamage, false) })
        if (b.timer(0, 6f)) {
          Damage.status(b.team, b.x, b.y, size, OtherContents.electric_disturb, min(lifeTime - b.time, 120f), air, ground)
        }
      }

      override fun draw(e: Bullet) {
        Draw.z(Layer.bullet - 5)
        Draw.color(Pal.stoneGray)
        Draw.alpha(0.6f)
        rand.setSeed(e.id.toLong())
        Angles.randLenVectors(e.id.toLong(), 8 + size.toInt() / 2, size * 1.2f) { x: Float, y: Float ->
          val size = rand.random(14, 20).toFloat()
          val i = e.fin(Interp.pow3Out)
          Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out))
        }
      }
    }
  }
}