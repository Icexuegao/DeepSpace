package ice.content.block

import arc.Core
import arc.func.Boolf2
import arc.func.Intf
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.audio.ISounds
import ice.content.IItems
import ice.content.ILiquids
import ice.library.util.toColor
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.liquid.SolidPump
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.blocks.production.BurstDrill
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.drills.*
import singularity.world.blocks.drills.ExtendMiner.ExtendMinerBuild
import singularity.world.blocks.drills.MatrixMinerComponent.MatrixMinerComponentBuild
import singularity.world.blocks.drills.MatrixMinerSector.MatrixMinerSectorBuild
import singularity.world.blocks.product.FloorCrafter
import singularity.world.blocks.product.FloorCrafter.FloorCrafterBuild
import singularity.world.blocks.product.NormalCrafter
import singularity.world.consumers.SglConsumeFloor
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDirSpliceBlock
import singularity.world.draw.DrawExpandPlasma
import singularity.world.meta.SglAttribute
import kotlin.math.pow

@Suppress("unused")
object ProductBlocks : Load {
  val 纤汲钻井: Block = BaseDrill("deriveDrill").apply {
    bitHardness = 3
    size = 2
    requirements(Category.production, IItems.高碳钢, 10, IItems.低碳钢, 5)
    drillTime = 400f
    bundle {
      desc(zh_CN, "纤汲钻井", "一种基础钻井,配备了最基础的钻芯,可用于开采基础资源")
    }
  }
  val 蛮荒钻井: Block = BaseDrill("uncivilizedDrill").apply {
    bitHardness = 4
    size = 3
    drillTime = 350f
    requirements(Category.production, IItems.铬锭, 20, IItems.钴锭, 12)
    bundle {
      desc(zh_CN, "蛮荒钻井", "一种次级钻井,在纤汲钻井的基础上进行了迭代,钻芯材料改进,可用于开采更高级资源")
    }
  }
  val 曼哈德钻井: Block = BaseDrill("manhardDrill").apply {
    bitHardness = 5
    size = 3
    drillTime = 300f
    squareSprite = false
    requirements(Category.production, ItemStack.with(IItems.硫钴矿, 4, IItems.低碳钢, 32))
    bundle {
      desc(zh_CN, "曼哈德钻井", "一种高级钻井,不同于其他钻井,其完全舍弃了传统的钻探方案,选择应用曼哈德效应以实现较为高效资源开采")
    }
  }
  val 热熔钻井: Block = BaseDrill("hotMeltDrill").apply {
    size = 5
    bitHardness = 6
    itemCapacity = 60
    liquidCapacity = 60f
    drillTime = 200f
    newConsume().apply {
      power(4f)
    }
    newBooster(2f).apply {
      liquid(Liquids.water, 0.3f)
    }

    drillEffect = Fx.mine
    updateEffect = mindustry.entities.effect.WaveEffect().apply {
      lifetime = 60f
      sizeFrom = 0f
      sizeTo = 40f
      colorFrom = "FFD37F".toColor()
      colorTo = "FFD37F00".toColor()
    }
    rotator.rotateSpeed = 6f
    warmupSpeed = 0.06f
    bundle {
      desc(zh_CN, "热熔钻井", "通过多种合金制成的钻头融化地层以快速开采所有矿物")
    }
    requirements(Category.production, IItems.铱板, 125, IItems.导能回路, 85, IItems.陶钢, 55)
  }
  val 血肉钻井: Block = BurstDrill("fleshBloodDrill").apply {
    size = 5
    tier = 11
    drillTime = 41.66f
    itemCapacity = 600
    liquidCapacity = 60f
    consumePower(10f)
    consumeLiquids(Liquids.water, 0.5f)
    drillSound = ISounds.激射
    placeableLiquid = true
    drillEffect = ParticleEffect().apply {
      particles = 6
      lifetime = 90f
      sizeFrom = 2f
      sizeTo = 3f
      length = 15f
      baseLength = 30f
      colorFrom = "D75B6E".toColor()
      colorTo = "D75B6E00".toColor()
      cone = 360f
    }
    requirements(Category.production, IItems.铱板, 450, IItems.导能回路, 225, IItems.钴锭, 32, IItems.生物钢, 75, IItems.肃正协议, 1)
    bundle {
      desc(zh_CN, "血肉钻井", "骨骼构成了最坚硬的钻头,肌肉形成了最强劲的转子,预热时间较长,需要持续供给血肉赘生物,可以安置在水上")
    }
  }
  val 抽水机 = SolidPump("waterPump").apply {
    size = 2
    baseEfficiency = 1f
    pumpAmount = 0.2f
    liquidCapacity = 60f
    consumePower(3f)
    bundle {
      desc(zh_CN, "抽水机", "抽水机,可以抽取水源")
    }
    requirements(Category.production, IItems.石英玻璃, 25, IItems.高碳钢, 20, IItems.单晶硅, 10)
  }
  val 大型抽水机 = SolidPump("largeWaterPump").apply {
    rotate
    size = 3
    baseEfficiency = 1f
    pumpAmount = 0.6f
    liquidCapacity = 120f
    consumePower(6f)
    bundle {
      desc(zh_CN, "大型抽水机", "大型抽水机,可以抽取水源")
    }
    requirements(Category.production, IItems.石英玻璃, 75, IItems.高碳钢, 40, IItems.铬锭, 70, IItems.单晶硅, 60)
  }

  var 岩层钻井机 = FloorCrafter("rock_drill").apply {
    bundle {
      desc(zh_CN, "岩层钻井机", "钻探深层的地壳,将深埋在地壳深处的较高质量的矿物送至地表")
    }
    requirements(Category.production, ItemStack.with(Items.titanium, 45, Items.lead, 30, Items.copper, 30))
    size = 2
    liquidCapacity = 24f
    oneOfOptionCons = true
    health = 180

    updateEffect = Fx.pulverizeSmall
    craftEffect = Fx.mine
    craftEffectColor = Pal.lightishGray

    warmupSpeed = 0.005f

    hasLiquids = true

    autoSelect = true

    newConsume()
    consume!!.time(90f)
    consume!!.liquid(Liquids.water, 0.2f)
    consume!!.power(1.75f)
    newProduce()
    produce!!.item(IItems.岩层沥青, 1)

    newConsume()
    consume!!.time(60f)
    consume!!.liquid(Liquids.cryofluid, 0.2f)
    consume!!.power(1.75f)
    newProduce()
    produce!!.item(IItems.岩层沥青, 2)

    newBooster(1f)
    consume!!.add(SglConsumeFloor(SglAttribute.bitumen, 1.12f))

    draw = DrawMulti(
      DrawBottom(), object : DrawLiquidRegion(Liquids.water) {
      init {
        suffix = "_liquid"
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = 1.5f
        spinSprite = true
      }
    }, DrawDefault(), DrawRegion("_top")
    )
  }
  var 岩石粉碎机 = FloorCrafter("rock_crusher").apply {
    bundle {
      desc(zh_CN, "岩石粉碎机", "将岩石粉碎成细小的颗粒,对于没有沙子的地方来说十分有用,同时某些岩石含盐量高,因此还可以从中得到一些有用的副产物", "事实上这台机器的效率并不算高,或者说它浪费掉的材料太多了,为了产出能够供应工业使用的富硅沙砾,几乎每生产一吨石英沙就要消耗掉几十吨原石,更别提硅纯度更低的一些岩石了")
    }
    requirements(
      Category.production, ItemStack.with(
        IItems.强化合金, 40, IItems.气凝胶, 55, Items.silicon, 60, Items.titanium, 50, Items.graphite, 60
      )
    )
    size = 3

    warmupSpeed = 0.004f
    updateEffect = Fx.pulverizeSmall
    craftEffect = Fx.mine
    craftEffectColor = Items.sand.color

    oneOfOptionCons = false

    itemCapacity = 25
    liquidCapacity = 30f

    newConsume()
    consume!!.time(30f)
    consume!!.power(2.2f)
    consume!!.add(
      SglConsumeFloor<FloorCrafterBuild>(
        Blocks.stone, 1.2f / 9f, Blocks.craters, 0.8f / 9f, Blocks.dacite, 0.8f / 9f, Blocks.shale, 1f / 9f, Blocks.salt, 1f / 9f, Blocks.moss, 0.6f / 9f, Blocks.sporeMoss, 0.4f / 9f
      )
    ).baseEfficiency = 0f

    newProduce()
    produce!!.item(Items.sand, 1)

    newOptionalProduct()
    consume!!.time(45f)
    consume!!.add(
      SglConsumeFloor<FloorCrafterBuild>(
        Blocks.stone, 0.4f / 9f, Blocks.craters, 0.5f / 9f, Blocks.salt, 2f / 9f
      )
    ).baseEfficiency = 0f
    consume!!.optionalAlwaysValid = false
    produce!!.item(IItems.碱石, 1)

    newOptionalProduct()
    consume!!.add(SglConsumeFloor<FloorCrafterBuild>(Attribute.spores, 1f)).baseEfficiency = 0f
    consume!!.optionalAlwaysValid = false
    produce!!.liquid(ILiquids.孢子云, 0.2f)

    newBooster(1.8f)
    consume!!.liquid(Liquids.water, 0.12f)

    draw = DrawMulti(
      DrawBottom(), DrawDefault(), object : DrawBlock() {
      var rim: TextureRegion? = null
      val heatColor: Color = Color.valueOf("ff5512")

      override fun draw(build: Building?) {
        val e = build as NormalCrafter.NormalCrafterBuild

        Draw.color(heatColor)
        Draw.alpha(e.workEfficiency() * 0.6f * (1f - 0.3f + Mathf.absin(Time.time, 3f, 0.3f)))
        Draw.blend(Blending.additive)
        Draw.rect(rim, e.x, e.y)
        Draw.blend()
        Draw.color()
      }

      override fun load(block: Block) {
        rim = Core.atlas.find(block.name + "_rim")
      }
    }, object : DrawRegion("_rotator") {
      init {
        rotateSpeed = 2.8f
        spinSprite = true
      }
    }, DrawRegion("_top")
    )
  }
  var 潮汐钻头 = ExtendableDrill("tidal_drill").apply {
    bundle {
      desc(zh_CN, "潮汐钻头", "使用最前沿力场控制技术制造的高级钻头,以粒子束冲击破坏挖掘物的物质结构后通过控制引力场震荡完成矿石解体和采集的过程")
    }
    requirements(
      Category.production, IItems.简并态中子聚合物, 50, IItems.强化合金, 120, IItems.气凝胶, 90, IItems.充能FEX水晶, 75, IItems.铱锭, 40, Items.phaseFabric, 60
    )
    size = 4
    squareSprite = false
    energyCapacity = 1024f
    basicPotentialEnergy = 1024f

    itemCapacity = 50
    liquidCapacity = 30f

    bitHardness = 10
    drillTime = 180f

    newConsume()
    consume!!.energy(1.25f)

    newBooster(4.2f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.15f)
    newBooster(3.1f)
    consume!!.liquid(ILiquids.FEX流体, 0.12f)

    draw = DrawMulti(
      DrawBottom(), object : DrawExpandPlasma() {
      init {
        plasmas = 2
        plasma1 = Pal.reactorPurple
        plasma2 = Pal.reactorPurple2
      }
    }, DrawDefault(), object : DrawBlock() {
      override fun draw(build: Building) {
        val e = build as ExtendableDrill.ExtendableDrillBuild
        val z = Draw.z()
        Draw.z(Layer.bullet)
        Draw.color(Pal.reactorPurple)
        val lerp = (-2.2 * e.warmup.toDouble().pow(2.0) + 3.2 * e.warmup).toFloat()
        Fill.circle(e.x, e.y, 3 * e.warmup)
        SglDraw.drawLightEdge(
          e.x, e.y, 26 * lerp, 2.5f * lerp, e.rotatorAngle, 1f, 16 * lerp, 2f * lerp, -e.rotatorAngle, 1f
        )
        Draw.z(z)
        Draw.color()
      }
    }, DrawRegion("_top")
    )
  }
  var 引力延展室 = ExtendMiner("force_field_extender").apply {
    squareSprite = false
    bundle {
      desc(zh_CN, "引力延展室", "用于延伸潮汐钻头的设备,贴近潮汐钻头,并与其他延展室彼此正对连接可扩大钻头覆盖的范围")
    }
    requirements(
      Category.production, ItemStack.with(
        IItems.简并态中子聚合物, 20, IItems.FEX水晶, 20, IItems.铱锭, 8, IItems.强化合金, 30
      )
    )
    size = 2

    master = 潮汐钻头 as ExtendableDrill?
    mining = SglFx.shrinkParticle(10f, 1.5f, 120f, Pal.reactorPurple)

    draw = DrawMulti(DrawBottom(), DrawDefault(), object : DrawDirSpliceBlock<ExtendMinerBuild?>() {
      init {
        simpleSpliceRegion = true

        spliceBits = Intf { e: ExtendMinerBuild? -> e!!.spliceDirBits }

        planSplicer = Boolf2 { plan: BuildPlan?, other: BuildPlan? ->
          plan!!.block is ExtendMiner && other!!.block is ExtendMiner && (other.block as ExtendMiner).chainable((plan.block as ExtendMiner)) && (plan.block as ExtendMiner).chainable((other.block as ExtendMiner))
        }

        layerRec = false
      }
    }, object : DrawBlock() {
      override fun draw(build: Building?) {
        val e = build as ExtendMinerBuild

        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        SglDraw.drawLightEdge(e.x, e.y, 8 * e.warmup, 2f * e.warmup, 8 * e.warmup, 2f * e.warmup, 45f)
        SglDraw.drawLightEdge(e.x, e.y, 15 * e.warmup, 2f * e.warmup, 45f, 0.6f, 15 * e.warmup, 2f * e.warmup, 45f, 0.6f)
      }
    })
  }

  var 矩阵矿床 = MatrixMiner("matrix_miner").apply {
    bundle {
      desc(zh_CN, "矩阵矿床", "矩阵矿床的控制中心,四面可安装矿床的工作组件以进行开采工作")
    }
    requirements(Category.production, IItems.矩阵合金, 130, IItems.充能FEX水晶, 80, IItems.强化合金, 90, IItems.气凝胶, 90, Items.phaseFabric, 65, Items.graphite, 90, IItems.铱锭, 45)
    size = 5
    matrixEnergyUse = 0.6f
    squareSprite = false
    baseRange = 32
  }
  var 采掘扇区 = MatrixMinerSector("matrix_miner_node").apply {
    bundle {
      desc(zh_CN, "采掘扇区", "矩阵矿床的采掘工作组件,提供一个基础开采角度区间")
    }
    requirements(
      Category.production, ItemStack.with(
        IItems.矩阵合金, 30, IItems.充能FEX水晶, 25, IItems.强化合金, 16, IItems.气凝胶, 20
      )
    )
    size = 3
    drillSize = 3
    squareSprite = false
    clipSize = (64 * Vars.tilesize).toFloat()
    energyMulti = 2f
  }
  var 谐振增压组件 = MatrixMinerComponent("matrix_miner_extend").apply {
    bundle {
      desc(zh_CN, "谐振增压组件", "矩阵矿床的增幅组件,使矩阵矿床的采集范围增大,可以大幅提高钻头的采掘效率")
    }
    requirements(Category.production, IItems.矩阵合金, 40, IItems.充能FEX水晶, 40, IItems.强化合金, 60, IItems.铱锭, 12, IItems.简并态中子聚合物, 20)
    size = 3
    squareSprite = false
    drillSize = 5
    energyMulti = 4f
    clipSize = (64 * Vars.tilesize).toFloat()
    draw = DrawMulti(
      DrawDefault(), object : DrawBlock() {
        override fun draw(build: Building) {
          if (build is MatrixMinerComponentBuild) {
            Draw.z(Layer.effect)
            Draw.color(SglDrawConst.matrixNet)
            Fill.circle(build.x, build.y, 2 * build.warmup)
            Draw.color(Pal.reactorPurple)
            Lines.stroke(2f * build.warmup)
            SglDraw.drawCornerTri(
              build.x, build.y, 20 * build.warmup, 4 * build.warmup, -Time.time * 1.5f, true
            )
            if (build.owner != null) {
              for (plugin in build.owner!!.plugins) {
                if (plugin is MatrixMinerSectorBuild) {
                  Lines.stroke(2f * build.warmup * plugin.warmup)
                  SglDraw.drawCornerTri(
                    plugin.drillPos!!.x, plugin.drillPos!!.y, 36 * build.warmup * plugin.warmup, 8 * build.warmup * plugin.warmup, -Time.time * 1.5f, true
                  )
                }
              }
            }
          }
        }
      })
  }
  var 量子隧穿仪 = MatrixMinerComponent("matrix_miner_pierce").apply {

    bundle {
      desc(zh_CN, "量子隧穿仪", "矩阵矿床的增幅组件,安装此组件后,矩阵矿床将能够透过建筑挖掘被建筑覆盖的矿石")
    }
    requirements(
      Category.production, ItemStack.with(
        IItems.矩阵合金, 40, IItems.充能FEX水晶, 40, IItems.FEX水晶, 50, IItems.强化合金, 30, IItems.铱锭, 20, Items.phaseFabric, 40
      )
    )
    size = 3
    squareSprite = false
    pierceBuild = true
    energyMulti = 4f

    clipSize = (64 * Vars.tilesize).toFloat()

    draw = DrawMulti(
      DrawDefault(), object : DrawBlock() {
        val param: FloatArray = FloatArray(9)
        val index = arrayOf<String?>("t1", "t2", "t3", "t4")
        val index2 = arrayOf<String?>("t11", "t12", "t13", "t14")
        val indexSelf = arrayOf<String?>("ts1", "ts2", "ts3")

        override fun draw(build: Building) {

          if (build is MatrixMinerComponentBuild) {
            rand.setSeed(build.id.toLong())

            Draw.z(Layer.effect)
            Draw.color(SglDrawConst.matrixNet)
            Fill.circle(build.x, build.y, 2 * build.warmup)
            Draw.color(Pal.reactorPurple)

            for (i in 0..2) {
              for (d in 0..2) {
                param[d * 3] = rand.random(2f, 4f) / (d + 1) * (if (i % 2 == 0) 1 else -1)
                param[d * 3 + 1] = rand.random(0f, 360f)
                param[d * 3 + 2] = rand.random(8f, 20f) / ((d + 1) * (d + 1))
              }
              val v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, *param)).scl(build.warmup)
              Draw.color(Pal.reactorPurple)
              Fill.circle(build.x + v.x, build.y + v.y, build.warmup)

              //   var trail: Trail? = build.getVar(indexSelf[i])
              //    if (trail == null) build.setVar(indexSelf[i], Trail(60).also { trail = it })

              //   trail!!.update(build.x + v.x, build.y + v.y)

              //  trail.draw(Pal.reactorPurple, build.warmup)
            }

            if (build.owner != null) {
              for ((ind, plugin) in build.owner!!.plugins.withIndex()) {
                if (plugin is MatrixMinerSectorBuild) {
                  val bool = rand.random(1) > 0.5f
                  for (d in 0..2) {
                    param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) 1 else -1)
                    param[d * 3 + 1] = rand.random(0f, 360f)
                    param[d * 3 + 2] = rand.random(16f, 40f) / ((d + 1) * (d + 1))
                  }
                  val v = Tmp.v1.set(MathTransform.fourierSeries(Time.time, *param))

                  for (d in 0..2) {
                    param[d * 3] = rand.random(0.5f, 3f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
                    param[d * 3 + 1] = rand.random(0f, 360f)
                    param[d * 3 + 2] = rand.random(12f, 30f) / ((d + 1) * (d + 1))
                  }
                  val v2 = Tmp.v2.set(MathTransform.fourierSeries(Time.time, *param))
                  Draw.color(Pal.reactorPurple)
                  Fill.circle(plugin.drillPos!!.x + v.x, plugin.drillPos!!.y + v.y, 1.5f * build.warmup * plugin.warmup)
                  Fill.circle(plugin.drillPos!!.x + v2.x, plugin.drillPos!!.y + v2.y, build.warmup * plugin.warmup)

                  //   var trail: Trail? = build.getVar(index[ind])
                  //     if (trail == null) build.setVar(index[ind], Trail(72).also { trail = it })
                  //     var trail2: Trail? = build.getVar(index2[ind])
                  //     if (trail2 == null) build.setVar(index2[ind], Trail(72).also { trail2 = it })

                  //   trail!!.draw(Pal.reactorPurple, 1.5f * build.warmup * plugin.warmup)
                  //    trail.update(plugin.drillPos.x + v.x, plugin.drillPos.y + v.y)

                  //    trail2!!.draw(Pal.reactorPurple, build.warmup * plugin.warmup)
                  //    trail2.update(plugin.drillPos.x + v2.x, plugin.drillPos.y + v2.y)
                }
              }
            }
          }
        }
      })
  }
  var 矩阵增幅器 = MatrixMinerComponent("matrix_miner_overdrive").apply {

    bundle {
      desc(zh_CN, "矩阵增幅器", "矩阵矿床的增幅组件,提高矩阵矿床的最大范围,并消耗液体增加矩阵矿床的工作效率")
    }
    requirements(
      Category.production, ItemStack.with(
        IItems.矩阵合金, 40, IItems.充能FEX水晶, 50, IItems.强化合金, 40, IItems.气凝胶, 40, IItems.铱锭, 15, Items.phaseFabric, 60
      )
    )
    size = 3
    range = 16
    drillMoveMulti = 2f
    energyMulti = 2f
    squareSprite = false
    clipSize = (10 * Vars.tilesize).toFloat()

    liquidCapacity = 40f

    newConsume()
    consume!!.time(180f)
    consume!!.item(Items.phaseFabric, 1)

    newBoost(1f, 0.6f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.3f)

    draw = DrawMulti(
      DrawDefault(), object : DrawBlock() {
        override fun draw(build: Building?) {

          if (build is MatrixMinerComponentBuild) {
            Draw.z(Layer.effect)
            Draw.color(SglDrawConst.matrixNet)
            Fill.circle(build.x, build.y, 2 * build.warmup)

            Lines.stroke(1.4f * build.warmup, Pal.reactorPurple)
            SglDraw.dashCircle(build.x, build.y, 10f, 5, 180f, Time.time)

            if (build.owner != null) {
              Lines.stroke(1.6f * build.warmup, Pal.reactorPurple)
              SglDraw.dashCircle(build.owner!!.x, build.owner!!.y, 18f, 6, 180f, -Time.time)
            }
          }
        }
      })
  }
}
