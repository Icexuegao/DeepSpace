package singularity.contents

import arc.Events
import arc.func.Cons2
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.IItems
import ice.content.ILiquids
import ice.content.block.turret.TurretBullets
import ice.library.struct.AttachedProperty
import ice.ui.bundle.BaseBundle
import mindustry.Vars
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.units.UnitController
import mindustry.game.EventType.ClientLoadEvent
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.Sgl
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.product.HoveringUnitFactory
import singularity.world.blocks.product.SglUnitFactory
import singularity.world.consumers.SglConsumers
import singularity.world.particles.SglParticleModels
import singularity.world.unit.SglUnitEntity
import singularity.world.unit.SglUnitType
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.generator.CircleGenerator
import universecore.world.lightnings.generator.ShrinkGenerator
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import kotlin.math.max
import kotlin.math.min

class SglUnits : ContentList {

  companion object {
    var SglUnitEntity.controlTime by AttachedProperty(0f)

    /**棱镜 */
    var prism: UnitType? = null

    /**流形 */
    var manifold: UnitType? = null

    /**虚宿 */
    var emptiness: UnitType? = null

    var unstable_energy_body: UnitType? = null

    /**机械构造坞 */
    var cstr_1: Block? = null
    var cstr_2: Block? = null
    var cstr_3: Block? = null
  }
  override fun load() {


    unstable_energy_body = object : SglUnitType<SglUnitEntity>("unstable_energy_body", SglUnitEntity::class.java) {
      val FULL_SIZE_ENERGY: Float = 3680f

      var SglUnitEntity.lightnings: LightningContainer? by AttachedProperty(null)
      var SglUnitEntity.lin: LightningContainer? by AttachedProperty(null)
      var SglUnitEntity.timer by AttachedProperty(15f)
      var SglUnitEntity.bullTime by AttachedProperty(0f)

      init {
        BaseBundle.bundle {
          desc(zh_CN, "不稳定能量体")
        }
        Events.on(ClientLoadEvent::class.java) { e: ClientLoadEvent? ->
          immunities.addAll(Vars.content.statusEffects())
          Sgl.empHealth.setEmpDisabled(this)
        }

        isEnemy = false

        health = 10f
        hidden = true
        hitSize = 32f
        playerControllable = false
        createWreck = false
        createScorch = false
        logicControllable = false
        useUnitCap = false

        aiController = Prov {
          object : UnitController {
            override fun unit(unit: Unit?) {
              //no ai
            }

            override fun unit(): Unit? {
              // no ai
              return null
            }

            override fun hit(bullet: Bullet?) {
            }

            override fun isValidController(): Boolean {
              return true
            }

            override fun isLogicControllable(): Boolean {
              return false
            }

            override fun updateUnit() {
            }

            override fun removed(unit: Unit?) {
            }

            override fun afterRead(unit: Unit?) {
            }
          }
        }
      }

      val generator: CircleGenerator = CircleGenerator()

      val linGen: ShrinkGenerator = ShrinkGenerator().apply {
        minInterval = 2.8f
        maxInterval = 4f
        maxSpread = 4f
      }

      override fun create(team: Team?): Unit {
        val res = super.create(team) as SglUnitEntity
        res.controlTime = Time.time
        return res
      }

      override fun init(unit: SglUnitEntity) {
        val cont = LightningContainer()
        cont.time = 0f
        cont.lifeTime = 18f
        cont.minWidth = 0.8f
        cont.maxWidth = 1.8f
        unit.lightnings = cont

        val lin = LightningContainer()
        lin.headClose = true
        lin.endClose = true
        lin.time = 12f
        lin.lifeTime = 22f
        lin.minWidth = 1.2f
        lin.maxWidth = 2.4f
        unit.lin = lin
      }

      override fun update(u: Unit?) {
        val unit = u as SglUnitEntity

        super.update(unit)

        val lightnings: LightningContainer = unit.lightnings!!
        val lin: LightningContainer = unit.lin!!
        if (Mathf.chanceDelta(0.08)) {
          generator.radius = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f)
          generator.minInterval = 4.5f
          generator.maxInterval = 6.5f
          generator.maxSpread = 5f
          lightnings.create(generator)

          Angles.randLenVectors(
            System.nanoTime(), 1, 1.8f, 2.75f
          ) { x: Float, y: Float ->
            SglParticleModels.floatParticle.create(u.x, u.y, Pal.reactorPurple, x, y, Mathf.random(3.55f, 4.25f)).strength = 0.22f
          }
        }

        if (Mathf.chanceDelta(0.1)) {
          linGen.maxRange = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f)
          linGen.minRange = linGen.maxRange
          val n = Mathf.random(1, 3)
          for (i in 0..<n) {
            lin.create(linGen)
          }
        }
        unit.timer -= Time.delta
        if (unit.timer <= 0f) {
          unit.timer = 12f
          generator.minInterval = 3.5f
          generator.maxInterval = 4.5f
          generator.maxSpread = 4f
          generator.radius = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f) / 2
          lightnings.create(generator)
        }

        lightnings.update()
        lin.update()

        unit.hitSize = hitSize * min(unit.health / FULL_SIZE_ENERGY, 2f)
        val controlTime: Float = 900 - Time.time + unit.controlTime
        if (controlTime <= 0) {
          if (unit.health >= 1280) {
            Effect.shake(8f, 120f, u.x, u.y)
            Damage.damage(u.x, u.y, unit.hitSize * 5, unit.health / FULL_SIZE_ENERGY * 4680)

            Sounds.explosionReactorNeoplasm.at(u.x, u.y, 0.8f, 3.5f)

            SglFx.reactorExplode.at(u.x, u.y, 0f, unit.hitSize * 5)
            Angles.randLenVectors(System.nanoTime(), Mathf.random(20, 34), 2.8f, 6.5f) { x: Float, y: Float ->
              val len = Tmp.v1.set(x, y).len()
              SglParticleModels.floatParticle.create(u.x, u.y, Pal.reactorPurple, x, y, Mathf.random(5f, 7f) * ((len - 3) / 4.5f))
            }
          }

          unit.kill()
        } else if (controlTime <= 300) {
          unit.bullTime -= Time.delta
          val bullTime: Float = unit.bullTime
          if (bullTime <= 0) {
            TurretBullets.溢出能量.create(u, u.team, u.x, u.y, Mathf.random(0f, 360f), Mathf.random(0.5f, 1f))
            unit.health -= 180f
            unit.bullTime = max(controlTime / 10, 2f)
          }

          if (Mathf.chanceDelta((1 - controlTime / 300).toDouble())) {
            val lerp: Float = (900 - Time.time + unit.controlTime) / 900
            Tmp.v1.rnd(Mathf.random(u.hitSize / (3 - lerp), max(u.hitSize / (2.5f - lerp), 15f)))
            SglFx.impWave.at(u.x + Tmp.v1.x, u.y + Tmp.v1.y)
          }
        }
      }

      override fun draw(u: Unit) {
        val unit = u as SglUnitEntity?

        Draw.z(Layer.effect)

        val radius = u.hitSize
        val lerp: Float = (900 - Time.time + unit!!.controlTime) / 900
        val lerpStart = Mathf.clamp((1 - lerp) / 0.1f)
        val lerpEnd = Interp.pow3Out.apply(Mathf.clamp(lerp / 0.2f))

        Lines.stroke(radius * 0.055f * lerpStart, Pal.reactorPurple)
        Lines.circle(u.x, u.y, radius * lerpEnd + radius * Interp.pow2In.apply(1 - lerpStart))

        Draw.draw(Draw.z()) {
          MathRenderer.setThreshold(0.4f, 0.7f)
          MathRenderer.setDispersion(lerpStart * 1.2f)
          Draw.color(Pal.reactorPurple)
          MathRenderer.drawCurveCircle(u.x, u.y, radius * 0.7f + radius * Interp.pow2In.apply(1 - lerpStart), 3, radius * 0.6f, Time.time * 1.2f)
          MathRenderer.setDispersion(lerpStart)
          Draw.color(SglDrawConst.matrixNet)
          MathRenderer.drawCurveCircle(u.x, u.y, radius * 0.72f + radius * Interp.pow2In.apply(1 - lerpStart), 4, radius * 0.67f, Time.time * 1.6f)
        }

        Draw.color(SglDrawConst.matrixNet)
        Fill.circle(u.x, u.y, radius / (2.4f - lerp) * Interp.pow2Out.apply(lerpStart) * lerpEnd)
        Lines.stroke(lerp)
        Lines.circle(u.x, u.y, radius * 1.2f * lerpEnd)
        unit.lightnings!!.draw(u.x, u.y)
        unit.lin!!.draw(u.x, u.y)

        Draw.color(Color.white)
        Fill.circle(u.x, u.y, Mathf.maxZero(radius / (2.6f - lerp)) * Interp.pow2Out.apply(lerpStart) * lerpEnd)
      }

      override fun read(sglUnitEntity: SglUnitEntity, read: Reads, revision: Int) {
        sglUnitEntity.controlTime = Time.time + read.f()
      }

      override fun write(sglUnitEntity: SglUnitEntity, write: Writes) {
        write.f(Time.time - sglUnitEntity.controlTime)
      }
    }

    cstr_1 = object : SglUnitFactory("cstr_1") {
      init {
        BaseBundle.bundle {
          desc(zh_CN,"起源构造器","高度集成的机械建造工厂,可以直接进行中小型单位的建造任务","比起繁杂冗长的重构工作,我们将所有工作都集成在了你看到的这坐机器当中,这可以节省出大量的空间与成本,为对抗敌人创造更多的优势")
        }
        requirements(
          Category.units, ItemStack.with(
            Items.silicon, 120, Items.graphite, 160, Items.thorium, 90, IItems.铝, 120, IItems.强化合金, 135
          )
        )
        size = 5
        liquidCapacity = 240f

        energyCapacity = 256f
        basicPotentialEnergy = 256f

        consCustom = Cons2 { u: UnitType?, c: SglConsumers? ->
          c!!.power(Mathf.round(u!!.health / u.hitSize) * 0.02f).showIcon = true
        }

        sizeLimit = 24f
        healthLimit = 7200f
        machineLevel = 4

        newBooster(1.5f)
        consume!!.liquid(Liquids.cryofluid, 2.4f)
        newBooster(1.8f)
        consume!!.liquid(ILiquids.FEX流体, 2f)
      }
    }

    cstr_2 = object : HoveringUnitFactory("cstr_2") {
      init {
        requirements(
          Category.units, ItemStack.with(
            Items.silicon, 180, Items.surgeAlloy, 160, Items.phaseFabric, 190, IItems.铝, 200, IItems.气凝胶, 120, IItems.强化合金, 215, IItems.矩阵合金, 180, IItems.FEX水晶, 140, IItems.铱锭, 100
          )
        )
        size = 7
        liquidCapacity = 280f
        energyCapacity = 1024f
        basicPotentialEnergy = 1024f
        squareSprite = false
        payloadSpeed = 1f

        consCustom = Cons2 { u: UnitType?, c: SglConsumers? ->
          c!!.power(Mathf.round(u!!.health / u.hitSize) * 0.02f).showIcon = true
          if (u.hitSize >= 38) c.energy(u.hitSize / 24)
        }

        matrixDistributeOnly = true

        sizeLimit = 68f
        healthLimit = 43000f
        machineLevel = 6
        timeMultiplier = 18f
        baseTimeScl = 0.25f

        outputRange = 340f

        hoverMoveMinRadius = 36f
        hoverMoveMaxRadius = 72f
        defHoverRadius = 23.5f

        laserOffY = 2f

        newBooster(1.6f)
        consume!!.liquid(Liquids.cryofluid, 3.2f)
        newBooster(1.9f)
        consume!!.liquid(ILiquids.FEX流体, 2.6f)
        newBooster(2.4f)
        consume!!.liquid(ILiquids.相位态FEX流体, 2.6f)

        draw = DrawMulti(
          DrawDefault(), object : DrawBlock() {
            override fun draw(build: Building?) {
              val b = build as HoveringUnitFactoryBuild
              Draw.z(Layer.effect)
              Draw.color(b.team.color)

              Lines.stroke(2 * b.warmup())

              Lines.circle(b.x, b.y, 12 * b.warmup())
              Lines.square(b.x, b.y, (size * Vars.tilesize).toFloat(), Time.time * 1.25f)
              Lines.square(b.x, b.y, 32f, Time.time * 3.25f)
              var p: ProducePayload<*>?

              b.producer?.current?.let {
                p = it.get(ProduceType.payload)
                if (p != null && p.payloads[0].item is UnitType) {
                  SglDraw.arc(b.x, b.y, (p.payloads[0].item as UnitType).hitSize + 8, 360 * b.progress(), -Time.time * 0.8f)
                }
              }


              Draw.color(Pal.reactorPurple)
              Lines.square(b.x, b.y, 28f, -MathTransform.gradientRotateDeg(Time.time, 0f) + 45f)

              for (i in 0..3) {
                for (j in 0..2) {
                  val lerp = b.warmup() * (1 - Mathf.clamp((Time.time + 30 * j) % 90 / 70))
                  SglDraw.drawTransform(b.x, b.y, (40 + j * 18).toFloat(), 0f, (i * 90 + 45).toFloat()) { rx: Float, ry: Float, r: Float ->
                    Draw.rect((SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), rx, ry, 15 * lerp, 15 * lerp, r + 90)
                  }
                }
              }
            }
          })
      }
    }

    cstr_3 = object : HoveringUnitFactory("cstr_3") {
      init {
        requirements(
          Category.units, ItemStack.with(
            Items.silicon, 240, Items.surgeAlloy, 240, Items.phaseFabric, 200, IItems.强化合金, 280, IItems.矩阵合金, 280, IItems.FEX水晶, 200, IItems.充能FEX水晶, 160, IItems.铱锭, 150, IItems.简并态中子聚合物, 100
          )
        )
        size = 9
        liquidCapacity = 420f
        energyCapacity = 4096f
        basicPotentialEnergy = 4096f
        squareSprite = false
        payloadSpeed = 1.2f

        consCustom = Cons2 { u: UnitType?, c: SglConsumers? ->
          c!!.power(Mathf.round(u!!.health / u.hitSize) * 0.02f).showIcon = true
          if (u.hitSize >= 38) c.energy(u.hitSize / 24)
        }

        matrixDistributeOnly = true

        sizeLimit = 120f
        healthLimit = 126000f
        machineLevel = 8
        timeMultiplier = 16f
        baseTimeScl = 0.22f

        beamWidth = 0.8f
        pulseRadius = 5f
        pulseStroke = 1.7f

        outputRange = 420f

        hoverMoveMinRadius = 48f
        hoverMoveMaxRadius = 98f
        defHoverRadius = 29f

        laserOffY = 4f

        newBooster(1.6f)
        consume!!.liquid(Liquids.cryofluid, 4f)
        newBooster(1.9f)
        consume!!.liquid(ILiquids.FEX流体, 3.8f)
        newBooster(2.4f)
        consume!!.liquid(ILiquids.相位态FEX流体, 3.8f)

        draw = DrawMulti(
          DrawDefault(), object : DrawBlock() {
            override fun draw(build: Building?) {
              val b = build as HoveringUnitFactoryBuild
              Draw.z(Layer.effect)
              Draw.color(b.team.color)

              Lines.stroke(2.2f * b.warmup())

              Lines.circle(b.x, b.y, 18 * b.warmup())
              Lines.square(b.x, b.y, (size * Vars.tilesize).toFloat(), Time.time * 1.25f)
              SglDraw.drawCornerTri(b.x, b.y, 58f, 14f, Time.time * 3.5f, true)
              var p: ProducePayload<*>? = null
              if (b.producer!!.current != null && (b.producer!!.current!!.get(ProduceType.payload).also { p = it }) != null && p!!.payloads[0].item is UnitType) {
                SglDraw.arc(b.x, b.y, (p.payloads[0].item as UnitType).hitSize + 8, 360 * b.progress(), -Time.time * 0.8f)
              }

              Draw.color(Pal.reactorPurple)
              Lines.square(b.x, b.y, 34f, -Time.time * 2.6f)
              SglDraw.drawCornerTri(b.x, b.y, 36f, 8f, -MathTransform.gradientRotateDeg(Time.time, 0f, 3) + 60, true)

              for (i in 0..3) {
                Draw.color(Pal.reactorPurple)
                for (j in 0..3) {
                  val lerp = b.warmup() * (1 - Mathf.clamp((Time.time + 30 * j) % 120 / 85f))
                  SglDraw.drawTransform(b.x, b.y, (50 + j * 20).toFloat(), 0f, (i * 90 + 45).toFloat()) { rx: Float, ry: Float, r: Float ->
                    Draw.rect((SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), rx, ry, 16 * lerp, 16 * lerp, r + 90)
                  }
                }

                Draw.color(b.team.color)
                for (j in 0..2) {
                  val lerp = b.warmup() * (1 - Mathf.clamp((Time.time + 24 * j) % 72 / 60f))
                  SglDraw.drawTransform(b.x, b.y, (40 + j * 20).toFloat(), 0f, (i * 90 + 45).toFloat()) { rx: Float, ry: Float, r: Float ->
                    Tmp.v1.set(18f, 0f).setAngle(r + 90)
                    Lines.stroke(2 * lerp)
                    Lines.square(rx + Tmp.v1.x, ry + Tmp.v1.y, 6 * lerp, r + 45)
                    Lines.square(rx - Tmp.v1.x, ry - Tmp.v1.y, 6 * lerp, r + 45)
                  }
                }
              }
            }
          })
      }
    }
  }

}