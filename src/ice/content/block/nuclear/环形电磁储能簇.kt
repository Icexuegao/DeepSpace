package ice.content.block.nuclear

import arc.Core
import arc.func.Cons
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import ice.content.ILiquids
import ice.content.block.turret.TurretBullets
import ice.ui.bundle.bundle
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.Stats
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.type.SglCategory
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.nuclear.EnergyContainer
import singularity.world.draw.DrawBottom
import singularity.world.meta.SglStat
import singularity.world.particles.SglParticleModels

class 环形电磁储能簇:EnergyContainer("magnetic_energy_container"){
  init {
      bundle {
        desc(zh_CN, "环形电磁储能簇", "约束式主动中子能存储设备,可以存储极大量的能量,但是需要消耗电力,若电力供应不足会发生泄漏")
      }
      requirements(
        SglCategory.nuclear,
        IItems.FEX水晶,
        200,
        IItems.充能FEX水晶,
        100,
        IItems.矩阵合金,
        120,
        IItems.强化合金,
        120,
        IItems.气凝胶,
        100,
        IItems.暮光合金,
        80,
        IItems.单晶硅,
        120

      )
      size = 5
      energyCapacity = (2 shl 19).toFloat()
      energyPotential = 4096f
      maxEnergyPressure = 16384f
      squareSprite = false
      warmupSpeed = 0.02f

      newConsume()
      consume!!.power(12f)

      setStats = Cons { s: Stats? ->
        s!!.add(SglStat.special, Core.bundle.format("infos.nonCons", Core.bundle.format("infos.energyContainerLeak", 3600)))
      }

      nonCons = Cons { ne: EnergyContainerBuild? ->
        val leak: Float = ne!!.getEnergy().coerceAtMost(60f)
        if (leak > 0) {
          ne.energy.handle(-leak * Time.delta * (1 - ne.warmup))
          val rate = Mathf.clamp(ne.getEnergy() / ne.energyCapacity())
          if (rate > 0.5f) {
            if (Mathf.chanceDelta((rate * 0.007f).toDouble())) {
              TurretBullets.溢出能量.create(ne, Team.derelict, ne.x, ne.y, Mathf.random(360f), Mathf.random(0.4f, 1f))
            }
          }

          if (Mathf.chanceDelta(((1 - ne.warmup) * 0.05f).toDouble())) {
            Angles.randLenVectors(
              System.nanoTime(), 1, 2f, 3.5f
            ) { x: Float, y: Float ->
              val create = SglParticleModels.floatParticle.create(ne.x, ne.y, SglDrawConst.fexCrystal, x, y, 2.3f)
              create.strength = 0.4f
            }
          }

          if (Mathf.chanceDelta(((1 - ne.warmup) * 0.075f).toDouble())) {
            SglFx.circleSparkMini.at(ne.x, ne.y, Tmp.c1.set(SglDrawConst.fexCrystal).lerp(SglDrawConst.matrixNet, Mathf.random(0f, 1f)))
          }
        }
      }

      drawers = DrawMulti(DrawBottom(), object : DrawBlock() {
        override fun draw(build: Building) {
          LiquidBlock.drawTiledFrames(
            build.block.size, build.x, build.y, 4f, ILiquids.孢子云, build.warmup()
          )
        }
      }, object : DrawBlock() {
        override fun draw(build: Building?) {
          super.draw(build)

          SglDraw.drawBloomUnderBlock<EnergyContainerBuild?>(build as EnergyContainerBuild?) { e: EnergyContainerBuild? ->
            MathRenderer.setThreshold(0.65f, 0.8f)
            MathRenderer.setDispersion(0.7f * e!!.warmup)
            Draw.color(SglDrawConst.fexCrystal)
            MathRenderer.drawCurveCircle(e.x, e.y, 9.5f, 4, 6f, -Time.time * 0.8f)
            Draw.color(SglDrawConst.matrixNet)
            MathRenderer.drawCurveCircle(e.x, e.y, 9.5f, 3, 6f, Time.time * 1.2f)
          }
          Draw.z(Layer.block + 5)
        }
      }, DrawDefault(), object : DrawBlock() {
        val param: FloatArray = FloatArray(9)
        val rand: Rand = Rand()

        override fun draw(build: Building?) {
          super.draw(build)
          val e = build as EnergyContainerBuild
          val l = Interp.pow2Out.apply(Mathf.clamp(e.getEnergy() / e.energyCapacity()))

          Draw.z(Layer.effect)
          Draw.color(SglDrawConst.fexCrystal)
          Fill.circle(e.x, e.y, 6 * l)
          Draw.color(Color.white)
          Fill.circle(e.x, e.y, 4 * l)

          rand.setSeed(build.id.toLong())
          for (i in 0..2) {
            val bool = rand.random(1f) > 0.5f
            for (d in 0..2) {
              param[d * 3] = rand.random(2f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
              param[d * 3 + 1] = rand.random(360f)
              param[d * 3 + 2] = rand.random(5f, 8f) / ((d + 1) * (d + 1))
            }
            val v = MathTransform.fourierSeries(Time.time, *param).scl(l)

            Draw.color(SglDrawConst.fexCrystal, SglDrawConst.matrixNet, Mathf.absin(Time.time * rand.random(4.8f, 7.2f), 1f))
            Fill.circle(e.x + v.x, e.y + v.y, 1.3f * l)
          }
        }
      })
    }
}