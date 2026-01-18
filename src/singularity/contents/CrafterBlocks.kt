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
import arc.math.geom.Geometry
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.layout.Table
import arc.util.Time
import arc.util.Tmp
import arc.util.noise.Noise
import ice.content.IItems
import ice.content.ILiquids
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.*
import mindustry.world.meta.Attribute
import mindustry.world.meta.BlockStatus
import mindustry.world.meta.Stats
import singularity.Singularity
import singularity.graphic.Distortion
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.graphic.SglShaders
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.function.Destructor
import singularity.world.blocks.product.*
import singularity.world.consumers.SglConsumeFloor
import singularity.world.consumers.SglConsumers
import singularity.world.draw.DrawAntiSpliceBlock
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawDyColorCultivator
import singularity.world.draw.DrawRegionDynamic
import singularity.world.meta.SglStat
import singularity.world.particles.SglParticleModels
import universecore.components.blockcomp.FactoryBuildComp
import universecore.world.consumers.*
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.generator.CircleGenerator
import universecore.world.lightnings.generator.LightningGenerator
import universecore.world.lightnings.generator.VectorLightningGenerator
import universecore.world.producers.ProduceType
import kotlin.math.max

class CrafterBlocks : ContentList {
  companion object {
    const val HIGHLIGHT: String = "highlight"
    const val ANIMATEDWATER: String = "animatedwater"
    const val STATUS: String = "status"

    /**裂变编织器 */
    var fission_weaver: Block? = null

    /**绿藻池 */
    var culturing_barn: Block? = null

    /**育菌箱 */
    var incubator: Block? = null

    /**电解机 */
    var electrolytor: Block? = null

    /**渗透分离槽 */
    var osmotic_separation_tank: Block? = null

    /**反应仓 */
    var reacting_pool: Block? = null

    /**燃烧室 */
    var combustion_chamber: Block? = null

    /**真空坩埚 */
    var vacuum_crucible: Block? = null

    /**热能冶炼炉 */
    var thermal_smelter: Block? = null

    /**干馏塔 */
    var retort_column: Block? = null

    /**激光解离机 */
    var laser_resolver: Block? = null

    /**蒸馏净化器 */
    var distill_purifier: Block? = null

    /**渗透净化器 */
    var osmotic_purifier: Block? = null

    /**洗矿机 */
    var ore_washer: Block? = null

    /**结晶器 */
    var crystallizer: Block? = null

    /**FEX相位混合器 */
    var FEX_phase_mixer: Block? = null

    /**燃料封装机 */
    var fuel_packager: Block? = null

    /**气体相位封装机 */
    var gas_phase_packer: Block? = null

    /**热能离心机 */
    var thermal_centrifuge: Block? = null

    /**晶格构建器 */
    var lattice_constructor: Block? = null

    /**FEX充能座 */
    var FEX_crystal_charger: Block? = null

    /**矩阵切割机 */
    var matrix_cutter: Block? = null

    /**中子透镜 */
    var neutron_lens: Block? = null

    /**聚合引力发生器 */
    var polymer_gravitational_generator: Block? = null

    /**质量生成器 */
    var quality_generator: Block? = null

    /**析构器 */
    var destructor: Block? = null

    /**物质逆化器 */
    var substance_inverter: Block? = null

    /**强子重构仪 */
    var hadron_reconstructor: Block? = null
  }

  override fun load() {
    fission_weaver = object : NormalCrafter("fission_weaver") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.FEX水晶, 50, Items.phaseFabric, 60, IItems.强化合金, 50, Items.plastanium, 45, Items.silicon, 70
          )
        )
        size = 4
        oneOfOptionCons = true
        itemCapacity = 24


        newConsume()
        consume!!.time(90f)
        consume!!.power(2.5f)
        consume!!.items(*ItemStack.with(Items.silicon, 4, IItems.铀238, 1))
        consume!!.consValidCondition<NormalCrafterBuild?>(Boolf { e: NormalCrafterBuild? -> e!!.statusi > 0 })
        newProduce()
        produce!!.item(Items.phaseFabric, 6)

        craftEffect = Fx.smeltsmoke
        val recipe = Cons { item: Item? ->
          newOptionalConsume(Cons2 { e: NormalCrafterBuild, c: BaseConsumers ->
            e.statusi = 2
          }, Cons2 { s: Stats?, c: BaseConsumers? ->
            s!!.add(SglStat.effect) { t: Table? -> t!!.add(Core.bundle.get("misc.doConsValid")) }
          })?.overdriveValid(false)
          consume!!.item(item!!, 1)
          consume!!.time(180f)
          consume!!.power(0.4f)
          consume!!.optionalAlwaysValid = true
        }
        recipe.get(IItems.铀235)
        recipe.get(IItems.钚239)

        buildType = Prov {
          object : NormalCrafterBuild() {
            public override fun updateTile() {
              super.updateTile()
              statusi = if (statusi > 0) statusi - 1 else 0
            }

            override fun status(): BlockStatus? {
              val status = super.status()
              if (status == BlockStatus.noInput && statusi > 0) return BlockStatus.noOutput
              return status
            }
          }
        }

        draw = DrawMulti(DrawBottom(), object : DrawWeave() {
          override fun load(block: Block) {
            weave = Core.atlas.find(block.name + "_top")
          }
        }, DrawDefault(), object : DrawBlock() {
          override fun draw(build: Building) {
            val e = build as NormalCrafterBuild
            Draw.color(SglDrawConst.winter, e.workEfficiency() * (0.4f + Mathf.absin(6f, 0.15f)))
            SglDraw.gradientCircle(e.x, e.y, 8f, 10f, 0f)
            SglDraw.gradientCircle(e.x, e.y, 8f, -4f, 0f)
          }
        })
      }
    }

    culturing_barn = object : SpliceCrafter("culturing_barn") {
      init {
        requirements(
          Category.production, ItemStack.with(
            Items.copper, 10, Items.metaglass, 12, Items.graphite, 8
          )
        )
        hasLiquids = true
        negativeSplice = true

        newConsume()
        consume!!.liquid(Liquids.water, 0.02f)
        newProduce()
        produce!!.liquids(
          *LiquidStack.with(
            Liquids.ozone, 0.01f, ILiquids.藻泥, 0.006f
          )
        )


        structUpdated = Cons { e: SpliceCrafterBuild ->
          val right = e.nearby(0)
          val top = e.nearby(1)
          val left = e.nearby(2)
          val bottom = e.nearby(3)
          e.highlight = (right !is SpliceCrafterBuild || right.chains!!.container !== e.chains!!.container) && (top !is SpliceCrafterBuild || top.chains!!!!.container !== e.chains!!.container) && (left is SpliceCrafterBuild && left.chains!!!!.container === e.chains!!.container) && (bottom is SpliceCrafterBuild && bottom.chains!!.container === e.chains!!.container)

        }

        draw = DrawMulti(DrawBottom(), object : DrawBlock() {
          val rand: Rand = Rand()
          val drawID: Int = SglDraw.nextTaskID()

          override fun draw(build: Building) {
            Draw.z(Draw.z() + 0.001f)
            val cap = build.block.liquidCapacity
            val drawCell = Cons { b: Building? ->

              val alp = max(b!!.warmup(), 0.7f * b.liquids.get(ILiquids.藻泥) / cap)
              if (alp <= 0.01f) return@Cons

              rand.setSeed(b.id.toLong())
              val am = (1 + rand.random(3) * b.warmup()).toInt()
              val move = 0.2f * Mathf.sinDeg(Time.time + rand.random(360f)) * b.warmup()
              Draw.color(ILiquids.藻泥.color)
              Draw.alpha(alp)
              Angles.randLenVectors(b.id.toLong(), am, 3.5f, Floatc2 { dx: Float, dy: Float ->
                Fill.circle(
                  b.x + dx + move, b.y + dy + move, (rand.random(0.2f, 0.8f) + Mathf.absin(5f, 0.1f)) * max(b.warmup(), b.liquids.get(ILiquids.藻泥) / cap)
                )
              })
              Draw.reset()
            }

            if (Core.settings.getBool(ANIMATEDWATER)) {
              if (true) {
                SglDraw.drawTask(drawID, build, SglShaders.boundWater, SglDraw.DrawAcceptor { e: Building ->
                  drawCell.get(e)
                  Draw.alpha(0.75f * (e.liquids.get(Liquids.water) / cap))
                  Draw.rect(Blocks.water.region, e.x, e.y)
                })
              } else {
                drawCell.get(build)
                LiquidBlock.drawTiledFrames(
                  size, build.x, build.y, 0f, Liquids.water, 0.75f * (build.liquids.get(Liquids.water) / cap)
                )
              }
            } else {
              drawCell.get(build)
              Draw.alpha(0.75f * (build.liquids.get(Liquids.water) / cap))
              Draw.rect(Blocks.water.region, build.x, build.y)
              Draw.reset()
            }
          }
        }, object : DrawAntiSpliceBlock<SpliceCrafterBuild?>() {
          init {
            planSplicer = Boolf2 { plan: BuildPlan, other: BuildPlan ->
              if (plan!!.block is SpliceCrafter && other!!.block is SpliceCrafter) {
                val block = plan.block as SpliceCrafter
                val otherBlock = other.block as SpliceCrafter
                return@Boolf2 block.chainable(otherBlock) && otherBlock.chainable(block)
              } else return@Boolf2 false

            }
            splicer = Intf<SpliceCrafterBuild?> { it.splice }
            layerRec = false
          }
        }, object : DrawRegionDynamic<SpliceCrafterBuild>("_highlight") {
          init {
            alpha = Floatf { e: SpliceCrafterBuild -> if (e.highlight) 1f else 0f }
          }
        })

        buildType = Prov {
          object : SpliceCrafterBuild() {
            var efficiency: Float = 0f

            public override fun efficiency(): Float {
              return super.efficiency() * efficiency
            }

            override fun updateTile() {
              super.updateTile()

              efficiency = if (enabled) Mathf.maxZero(
                Attribute.light.env() + (if (Vars.state.rules.lighting) 1f - Vars.state.rules.ambientLight.a else 1f)
              ) else 0f
            }
          }
        }
      }

      override fun setBars() {
        super.setBars()
        addBar<SglBuilding?>("efficiency", Func { entity: SglBuilding? ->
          Bar({ Core.bundle.format("bar.efficiency", (entity!!.efficiency() * 100).toInt()) }, { Pal.lightOrange }, { entity!!.efficiency() })
        })
      }
    }

    incubator = object : FloorCrafter("incubator") {
      init {
        requirements(
          Category.production, ItemStack.with(
            Items.plastanium, 85, Items.titanium, 90, IItems.气凝胶, 80, Items.copper, 90
          )
        )
        size = 3
        liquidCapacity = 20f

        newConsume()
        consume!!.time(45f)
        consume!!.power(2.2f)
        consume!!.liquids(
          *LiquidStack.with(
            Liquids.water, 0.4f, ILiquids.孢子云, 0.1f
          )
        )
        newProduce()
        produce!!.item(Items.sporePod, 3)

        newConsume()
        consume!!.time(30f)
        consume!!.power(2.2f)
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.纯净水, 0.3f, ILiquids.孢子云, 0.1f
          )
        )
        newProduce()
        produce!!.item(Items.sporePod, 3)

        newBooster(1f)
        consume!!.add(
          SglConsumeFloor<FloorCrafterBuild>(
            true, true, arrayOf<Any>(
              Attribute.heat, 0.22f, Attribute.spores, 0.36f
            )
          )
        )

        draw = DrawMulti(
          DrawBottom(), object : DrawCultivator() {
            override fun load(block: Block) {
              middle = Core.atlas.find(block.name + "_middle")
            }
          }, DrawDefault(), DrawRegion("_top")
        )
      }
    }

    electrolytor = object : NormalCrafter("electrolytor") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 80, Items.copper, 100, Items.lead, 80, Items.silicon, 50, Items.metaglass, 60, Items.plastanium, 35
          )
        )
        size = 3
        itemCapacity = 25
        liquidCapacity = 40f

        newConsume()
        consume!!.liquid(Liquids.water, 0.6f)
        consume!!.power(6f)
        newProduce()
        produce!!.liquids(
          *LiquidStack.with(
            Liquids.ozone, 0.6f, Liquids.hydrogen, 0.8f
          )
        )

        newConsume()
        consume!!.liquid(ILiquids.纯净水, 0.4f)
        consume!!.power(5.8f)
        newProduce()
        produce!!.liquids(
          *LiquidStack.with(
            Liquids.ozone, 0.6f, Liquids.hydrogen, 0.8f
          )
        )

        newConsume()
        consume!!.time(120f)
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.复合矿物溶液, 0.4f, ILiquids.碱液, 0.2f
          )
        )
        consume!!.item(IItems.絮凝剂, 2)
        consume!!.power(3.5f)
        newProduce()
        produce!!.items(
          *ItemStack.with(
            IItems.铝, 4, Items.lead, 3, Items.titanium, 1, Items.thorium, 2
          )
        )

        newConsume()
        consume!!.time(60f)
        consume!!.liquid(ILiquids.纯净水, 0.4f)
        consume!!.item(IItems.碱石, 1)
        consume!!.power(3f)
        newProduce()
        produce!!.liquids(
          *LiquidStack.with(
            ILiquids.碱液, 0.4f, ILiquids.氯气, 0.6f
          )
        )

        newConsume()
        consume!!.item(Items.sporePod, 1)
        consume!!.liquid(Liquids.water, 0.2f)
        consume!!.power(2.8f)
        consume!!.time(60f)
        newProduce()
        produce!!.liquid(ILiquids.孢子云, 0.4f)

        newConsume()
        consume!!.item(IItems.绿藻块, 1)
        consume!!.liquid(Liquids.water, 0.2f)
        consume!!.time(120f)
        consume!!.power(2.5f)
        newProduce()
        produce!!.item(IItems.绿藻素, 1)

        draw = DrawMulti(
          DrawBottom(), object : DrawBlock() {
          override fun draw(build: Building?) {
            val e = build as NormalCrafterBuild
            if (e.consumer!!.current == null) return
            val l = e.consumer!!.current!!.get<ConsumeLiquidBase<*>>(ConsumeType.liquid)!!.consLiquids!![0].liquid
            LiquidBlock.drawTiledFrames(size, e.x, e.y, 4f, l, e.liquids.get(l) / liquidCapacity)
          }
        }, object : DrawDyColorCultivator<NormalCrafterBuild?>() {
          init {
            spread = 4f
            plantColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
            bottomColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
            plantColorLight = Func { e: NormalCrafterBuild? -> Color.white }
          }
        }, DrawDefault()
        )
      }
    }

    osmotic_separation_tank = object : NormalCrafter("osmotic_separation_tank") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 60, Items.lead, 90, Items.graphite, 100, Items.metaglass, 80, Items.silicon, 70
          )
        )
        size = 3

        itemCapacity = 20
        liquidCapacity = 40f

        newConsume()
        consume!!.time(60f)
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.碱液, 0.2f, ILiquids.铀盐溶液, 0.2f, Liquids.ozone, 0.2f
          )
        )
        consume!!.item(IItems.絮凝剂, 1)
        consume!!.power(1.2f)
        newProduce()
        produce!!.item(IItems.铀原料, 2)

        newConsume()
        consume!!.time(120f)
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.酸液, 0.2f, Liquids.ozone, 0.4f
          )
        )
        consume!!.item(IItems.铱金混合物, 1)
        consume!!.power(1.2f)
        newProduce()
        produce!!.item(IItems.氯铱酸盐, 1)

        newConsume()
        consume!!.time(90f)
        consume!!.liquid(ILiquids.藻泥, 0.4f)
        consume!!.power(1f)
        newProduce()
        produce!!.item(IItems.绿藻块, 1)
        produce!!.liquid(ILiquids.纯净水, 0.2f)

        draw = DrawMulti(
          DrawBottom(), object : DrawBlock() {
            override fun draw(build: Building?) {
              val e = build as NormalCrafterBuild
              if (e.consumer!!.current == null) return
              val l = e.consumer!!.current!!.get<ConsumeLiquidBase<*>>(ConsumeType.liquid)!!.consLiquids!![0].liquid
              LiquidBlock.drawTiledFrames(size, e.x, e.y, 4f, l, e.liquids.get(l) / liquidCapacity)
            }
          }, DrawDefault()
        )
      }
    }

    reacting_pool = object : NormalCrafter("reacting_pool") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 100, Items.metaglass, 100, Items.lead, 80, Items.graphite, 85, Items.silicon, 80, Items.plastanium, 75
          )
        )
        size = 3

        itemCapacity = 35
        liquidCapacity = 45f

        newConsume()
        consume!!.time(60f)
        consume!!.item(IItems.黑晶石, 3)
        consume!!.liquid(ILiquids.酸液, 0.2f)
        consume!!.power(0.8f)
        newProduce()
        produce!!.liquid(ILiquids.复合矿物溶液, 0.4f)

        newConsume()
        consume!!.time(60f)
        consume!!.liquid(ILiquids.酸液, 0.2f)
        consume!!.item(IItems.铀原矿, 1)
        consume!!.power(0.8f)
        newProduce()
        produce!!.liquid(ILiquids.铀盐溶液, 0.2f)

        newConsume()
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.纯净水, 0.4f, ILiquids.二氧化硫, 0.4f, ILiquids.氯气, 0.2f
          )
        )
        consume!!.power(0.6f)
        newProduce()
        produce!!.liquid(ILiquids.酸液, 0.6f)

        newConsume()
        consume!!.time(120f)
        consume!!.items(
          *ItemStack.with(
            Items.silicon, 2, IItems.絮凝剂, 1
          )
        )
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.纯净水, 0.4f, ILiquids.氯气, 0.2f
          )
        )
        consume!!.power(1.5f)
        newProduce()
        produce!!.liquids(
          *LiquidStack.with(
            ILiquids.氯化硅溶胶, 0.4f, ILiquids.酸液, 0.2f
          )
        )

        newConsume()
        consume!!.time(60f)
        consume!!.item(IItems.铝, 1)
        consume!!.liquid(ILiquids.碱液, 0.2f)
        consume!!.power(1f)
        newProduce()
        produce!!.item(IItems.絮凝剂, 2)

        newConsume()
        consume!!.time(30f)
        consume!!.item(Items.coal, 1)
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.酸液, 0.2f, ILiquids.孢子云, 0.3f
          )
        )
        consume!!.power(1f)
        newProduce()
        produce!!.item(Items.blastCompound, 1)

        draw = DrawMulti(
          DrawBottom(), object : DrawBlock() {
          override fun draw(build: Building?) {
            val e = build as NormalCrafterBuild
            if (e.consumer!!.current == null || e.producer!!.current == null) return
            val l = e.consumer!!.current!!.get<ConsumeLiquidBase<*>>(ConsumeType.liquid)!!.consLiquids!![0].liquid

            /* if (Sgl.config.animateLevel < 2) {
                 Draw.color(l.color, max(e.warmup(), e.liquids.get(l) / liquidCapacity))
                 Fill.rect(e.x, e.y, (e.block.size * Vars.tilesize - Vars.tilesize).toFloat(), (e.block.size * Vars.tilesize - Vars.tilesize).toFloat())
                 return
             }*/
            val region = Vars.renderer.fluidFrames[if (l.gas) 1 else 0][l.getAnimationFrame()]
            val toDraw = Tmp.tr1
            val bounds = size / 2f * Vars.tilesize - 3
            val color = Tmp.c1.set(l.color).a(1f).lerp(e.producer!!.current!!.color, e.warmup())

            for (sx in 0..<size) {
              for (sy in 0..<size) {
                val relx = sx - (size - 1) / 2f
                val rely = sy - (size - 1) / 2f

                toDraw.set(region)
                //truncate region if at border
                val rightBorder = relx * Vars.tilesize + 3
                val topBorder = rely * Vars.tilesize + 3
                val squishX = rightBorder + Vars.tilesize / 2f - bounds
                val squishY = topBorder + Vars.tilesize / 2f - bounds
                var ox = 0f
                var oy = 0f

                if (squishX >= 8 || squishY >= 8) continue
                //cut out the parts that don't fit inside the padding
                if (squishX > 0) {
                  toDraw.setWidth(toDraw.width - squishX * 4f)
                  ox = -squishX / 2f
                }

                if (squishY > 0) {
                  toDraw.setY(toDraw.getY() + squishY * 4f)
                  oy = -squishY / 2f
                }

                Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, max(e.warmup(), e.liquids.get(l) / liquidCapacity), color)
              }
            }
          }
        }, object : DrawDyColorCultivator<NormalCrafterBuild?>() {
          init {
            spread = 4f
            plantColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
            bottomColor = Func { e: NormalCrafterBuild? -> SglDrawConst.transColor }
            plantColorLight = Func { e: NormalCrafterBuild? -> Color.white }
          }
        }, DrawDefault()
        )
      }
    }

    combustion_chamber = object : NormalCrafter("combustion_chamber") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 90, Items.graphite, 80, Items.metaglass, 80, Items.silicon, 75
          )
        )
        size = 3
        liquidCapacity = 40f
        itemCapacity = 25


        newConsume()
        consume!!.liquid(Liquids.hydrogen, 0.8f)
        newProduce()
        produce!!.liquid(ILiquids.纯净水, 0.4f)
        produce!!.power(5f)

        newConsume()
        consume!!.time(120f)
        consume!!.item(Items.pyratite, 1)
        newProduce()
        produce!!.liquid(ILiquids.二氧化硫, 0.4f)
        produce!!.power(4.5f)

        newBooster(2.65f)
        consume!!.liquid(Liquids.ozone, 0.4f)

        draw = DrawMulti(
          DrawBottom(), DrawCrucibleFlame(), DrawDefault()
        )
      }
    }

    vacuum_crucible = object : NormalCrafter("vacuum_crucible") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 90, Items.silicon, 80, Items.plastanium, 60, Items.metaglass, 75, Items.graphite, 80
          )
        )
        size = 3



        liquidCapacity = 45f
        itemCapacity = 30

        newConsume()
        consume!!.time(60f)
        consume!!.liquids(
          *LiquidStack.with(
            ILiquids.氯化硅溶胶, 0.2f, Liquids.hydrogen, 0.4f
          )
        )
        consume!!.item(Items.sand, 5)
        consume!!.power(2f)
        newProduce()
        produce!!.item(Items.silicon, 8)

        newConsume()
        consume!!.time(120f)
        consume!!.liquid(ILiquids.氯化硅溶胶, 0.4f)
        consume!!.item(Items.metaglass, 10)
        consume!!.power(1.8f)
        newProduce()
        produce!!.item(IItems.气凝胶, 5)

        newConsume()
        consume!!.time(120f)
        consume!!.item(IItems.绿藻块, 1)
        consume!!.liquid(ILiquids.酸液, 0.2f)
        consume!!.power(1.6f)
        newProduce()
        produce!!.item(IItems.絮凝剂, 1)

        newConsume()
        consume!!.time(120f)
        consume!!.item(Items.sporePod, 1)
        consume!!.liquid(ILiquids.碱液, 0.2f)
        consume!!.power(1.6f)
        newProduce()
        produce!!.item(IItems.絮凝剂, 1)

        draw = DrawMulti(
          DrawBottom(), DrawCrucibleFlame(), DrawDefault()
        )
      }
    }

    thermal_smelter = object : NormalCrafter("thermal_smelter") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 65, Items.silicon, 70, Items.copper, 60, Items.graphite, 60, Items.plastanium, 70
          )
        )
        size = 3

        itemCapacity = 20


        newConsume()
        consume!!.time(90f)
        consume!!.items(
          *ItemStack.with(
            Items.titanium, 3, Items.thorium, 2, IItems.焦炭, 1
          )
        )
        consume!!.liquid(ILiquids.氯化硅溶胶, 0.2f)
        consume!!.power(2.6f)
        newProduce()
        produce!!.item(IItems.强化合金, 4)

        newConsume()
        consume!!.time(120f)
        consume!!.items(
          *ItemStack.with(
            IItems.氯铱酸盐, 1, IItems.焦炭, 2
          )
        )
        consume!!.liquid(Liquids.hydrogen, 0.4f)
        consume!!.power(3f)
        newProduce()
        produce!!.item(IItems.铱, 2)

        draw = DrawMulti(DrawBottom(), object : DrawBlock() {
          val flameColor: Color = Color.valueOf("f58349")

          override fun draw(build: Building) {
            val base = (Time.time / 70)
            Draw.color(flameColor, 0.5f)
            DrawBlock.rand.setSeed(build.id.toLong())
            for (i in 0..34) {
              val fin = (DrawBlock.rand.random(1f) + base) % 1f
              val angle = DrawBlock.rand.random(360f) + (Time.time / 1.5f) % 360f
              val len = 10 * Mathf.pow(fin, 1.5f)
              Draw.alpha(0.5f * build.warmup() * (1f - Mathf.curve(fin, 1f - 0.4f)))
              Fill.circle(
                build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), 3 * fin * build.warmup()
              )
            }

            Draw.blend()
            Draw.reset()
          }
        }, DrawDefault(), object : DrawFlame() {
          init {
            flameRadius = 2f
            flameRadiusScl = 4f
          }

          override fun load(block: Block) {
            top = Core.atlas.find(block.name + "_top")
            block.clipSize = max(block.clipSize, (lightRadius + lightSinMag) * 2f * block.size)
          }
        })
      }
    }

    retort_column = object : NormalCrafter("retort_column") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 70, Items.graphite, 75, Items.copper, 90, Items.metaglass, 90, Items.plastanium, 50
          )
        )
        size = 3
        itemCapacity = 12
        liquidCapacity = 20f



        newConsume()
        consume!!.time(90f)
        consume!!.power(2f)
        consume!!.item(Items.coal, 3)
        newProduce()
        produce!!.items(
          *ItemStack.with(
            Items.pyratite, 1, IItems.焦炭, 1
          )
        )

        craftEffect = Fx.smeltsmoke

        draw = DrawMulti(
          DrawDefault(), object : DrawFlame() {
            override fun load(block: Block) {
              top = Core.atlas.find(block.name + "_top")
              block.clipSize = max(block.clipSize, (lightRadius + lightSinMag) * 2f * block.size)
            }
          })
      }
    }

    laser_resolver = object : NormalCrafter("laser_resolver") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.FEX水晶, 45, IItems.强化合金, 70, Items.silicon, 90, Items.phaseFabric, 65, Items.metaglass, 120
          )
        )
        size = 3
        itemCapacity = 20
        warmupSpeed = 0.01f


        newConsume()
        consume!!.time(60f)
        consume!!.power(3.2f)
        consume!!.item(IItems.核废料, 1)
        newProduce()!!.color = IItems.核废料.color
        produce!!.items(
          *ItemStack.with(
            IItems.铱金混合物, 2, Items.lead, 5, Items.thorium, 3
          )
        ).random()

        newConsume()
        consume!!.time(30f)
        consume!!.item(Items.scrap, 2)
        consume!!.liquid(Liquids.slag, 0.1f)
        consume!!.power(3.5f)
        newProduce()!!.color = Items.scrap.color
        produce!!.items(
          *ItemStack.with(
            Items.thorium, 3, Items.titanium, 4, Items.lead, 5, Items.copper, 3
          )
        ).random()

        newConsume()
        consume!!.time(60f)
        consume!!.item(IItems.黑晶石, 1)
        consume!!.power(2.8f)
        newProduce()!!.color = IItems.黑晶石.color
        produce!!.items(
          *ItemStack.with(
            Items.titanium, 2, Items.thorium, 1, Items.lead, 3, IItems.铝, 4
          )
        ).random()


        draw = DrawMulti(
          DrawBottom(), object : DrawBlock() {
          override fun draw(build: Building) {
            val e = build as NormalCrafterBuild
            if (e.producer!!.current == null) return

            /*if (Sgl.config.animateLevel < 2) {
                Draw.color(e.producer!!.current!!.color, e.warmup)
                Fill.rect(e.x, e.y, (e.block.size * Vars.tilesize - Vars.tilesize).toFloat(), (e.block.size * Vars.tilesize - Vars.tilesize).toFloat())
                return
            }*/
            val region = Vars.renderer.fluidFrames[0][Liquids.water.animationFrame]
            val toDraw = Tmp.tr1
            val bounds = size / 2f * Vars.tilesize - 3
            val color = e.producer!!.current!!.color

            for (sx in 0..<size) {
              for (sy in 0..<size) {
                val relx = sx - (size - 1) / 2f
                val rely = sy - (size - 1) / 2f

                toDraw.set(region)
                val rightBorder = relx * Vars.tilesize + 3
                val topBorder = rely * Vars.tilesize + 3
                val squishX = rightBorder + Vars.tilesize / 2f - bounds
                val squishY = topBorder + Vars.tilesize / 2f - bounds
                var ox = 0f
                var oy = 0f

                if (squishX >= 8 || squishY >= 8) continue

                if (squishX > 0) {
                  toDraw.setWidth(toDraw.width - squishX * 4f)
                  ox = -squishX / 2f
                }

                if (squishY > 0) {
                  toDraw.setY(toDraw.y + squishY * 4f)
                  oy = -squishY / 2f
                }

                Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, e.warmup, color)
              }
            }
            Draw.rect()
          }
        }, DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>("_laser") {
          init {
            rotation = Floatf { e: NormalCrafterBuild? -> e!!.totalProgress * 1.5f }
            alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
          }

          override fun draw(build: Building?) {
            SglDraw.drawBloomUnderBlock(build) { build: Building -> super.draw(build) }
            Draw.z(Layer.block + 5)
          }
        }, object : DrawRegion("_rotator") {
          init {
            rotateSpeed = 1.5f
            spinSprite = true
          }
        }, DrawRegion("_top")
        )
      }
    }

    distill_purifier = object : NormalCrafter("distill_purifier") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.copper, 30, Items.silicon, 24, Items.metaglass, 30, Items.graphite, 20
          )
        )
        size = 2
        hasLiquids = true
        liquidCapacity = 30f

        updateEffect = Fx.steam
        updateEffectChance = 0.035f

        newConsume()
        consume!!.time(120f)
        consume!!.liquid(Liquids.water, 0.5f)
        consume!!.power(1f)
        newProduce()
        produce!!.liquid(ILiquids.纯净水, 0.4f)
        produce!!.item(IItems.碱石, 1)

        draw = DrawMulti(
          DrawBottom(), DrawLiquidTile(Liquids.water, 3f), DrawDefault()
        )
      }
    }

    osmotic_purifier = object : NormalCrafter("osmotic_purifier") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.铝, 50, Items.graphite, 60, Items.silicon, 45, Items.titanium, 45, IItems.气凝胶, 50
          )
        )
        size = 3
        hasLiquids = true
        liquidCapacity = 30f

        newConsume()
        consume!!.time(60f)
        consume!!.liquid(Liquids.water, 2f)
        consume!!.item(Items.graphite, 1)
        consume!!.power(1f)
        newProduce()
        produce!!.liquid(ILiquids.纯净水, 2f)
        produce!!.item(IItems.碱石, 2)

        draw = DrawMulti(
          DrawBottom(), DrawLiquidTile(Liquids.water, 3f), object : DrawBlock() {
            override fun draw(build: Building?) {
              val e = build as NormalCrafterBuild

              /*if (Sgl.config.animateLevel < 2) {
                  Draw.color(SglLiquids.purified_water.color, e.warmup())
                  Fill.rect(e.x, e.y, (e.block.size * Vars.tilesize - Vars.tilesize).toFloat(), (e.block.size * Vars.tilesize - Vars.tilesize).toFloat())
                  return
              }*/
              val region = Vars.renderer.fluidFrames[0][Liquids.water.getAnimationFrame()]
              val toDraw = Tmp.tr1
              val bounds = size / 2f * Vars.tilesize - 8
              val color = ILiquids.纯净水.color

              for (sx in 0..<size) {
                for (sy in 0..<size) {
                  val relx = sx - (size - 1) / 2f
                  val rely = sy - (size - 1) / 2f

                  toDraw.set(region)
                  val rightBorder = relx * Vars.tilesize + 8
                  val topBorder = rely * Vars.tilesize + 8
                  val squishX = rightBorder + Vars.tilesize / 2f - bounds
                  val squishY = topBorder + Vars.tilesize / 2f - bounds
                  var ox = 0f
                  var oy = 0f

                  if (squishX >= 8 || squishY >= 8) continue

                  if (squishX > 0) {
                    toDraw.setWidth(toDraw.width - squishX * 4f)
                    ox = -squishX / 2f
                  }

                  if (squishY > 0) {
                    toDraw.setY(toDraw.getY() + squishY * 4f)
                    oy = -squishY / 2f
                  }

                  Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, e.warmup(), color)
                }
              }
            }
          }, DrawDefault()
        )
      }
    }

    ore_washer = object : NormalCrafter("ore_washer") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            Items.titanium, 60, Items.graphite, 40, Items.lead, 45, Items.metaglass, 60
          )
        )
        size = 2
        hasLiquids = true
        itemCapacity = 20
        liquidCapacity = 24f


        newConsume()
        consume!!.time(120f)
        consume!!.liquid(Liquids.water, 0.2f)
        consume!!.item(IItems.岩层沥青, 1)
        consume!!.power(1.8f)
        newProduce()
        produce!!.liquid(ILiquids.FEX流体, 0.2f)
        produce!!.items(
          *ItemStack.with(
            Items.sand, 5, IItems.黑晶石, 3, IItems.铀原矿, 2
          )
        ).random()

        craftEffect = Fx.pulverizeMedium

        draw = DrawMulti(DrawDefault(), object : DrawLiquidRegion(Liquids.water) {
          init {
            suffix = "_liquid"
          }
        }, object : DrawRegion("_rotator") {
          init {
            rotateSpeed = 4.5f
            spinSprite = true
          }
        }, DrawRegion("_top"), object : DrawRegionDynamic<NormalCrafterBuild?>("_point") {
          init {
            color = Func { e: NormalCrafterBuild? ->
              val cons = if (e!!.consumer!!.current == null) null else ((e.consumer!!.current) as SglConsumers).first()
              if (cons is ConsumeItems<*>) {
                val item = cons.consItems!![0].item
                return@Func item.color
              } else return@Func Color.white
            }
            alpha = Floatf { e: NormalCrafterBuild? ->
              val cons = if (e!!.consumer!!.current == null) null else ((e.consumer!!.current) as SglConsumers).first()
              if (cons is ConsumeItems<*>) {
                val item = cons.consItems!![0].item
                return@Floatf e.items.get(item).toFloat() / e.block.itemCapacity
              } else return@Floatf 0f
            }
          }
        })
      }
    }

    crystallizer = object : NormalCrafter("crystallizer") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 35, Items.silicon, 45, Items.copper, 40, Items.metaglass, 50
          )
        )
        size = 2
        liquidCapacity = 16f

        newConsume()
        consume!!.time(240f)
        consume!!.item(IItems.强化合金, 1)
        consume!!.liquid(ILiquids.FEX流体, 0.2f)
        consume!!.power(2.8f)
        newProduce()
        produce!!.item(IItems.FEX水晶, 2)

        draw = DrawMulti(
          object : DrawCultivator() {
            init {
              plantColor = Color.valueOf("#C73A3A")
              plantColorLight = Color.valueOf("#E57D7D")
            }

            override fun load(block: Block) {
              middle = Core.atlas.find(block.name + "_middle")
            }
          }, DrawDefault()
        )
      }
    }

    FEX_phase_mixer = object : NormalCrafter("FEX_phase_mixer") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 40, Items.plastanium, 90, Items.phaseFabric, 85, Items.silicon, 80
          )
        )
        size = 2
        hasLiquids = true
        liquidCapacity = 12f


        newConsume()
        consume!!.time(120f)
        consume!!.item(Items.phaseFabric, 1)
        consume!!.liquid(ILiquids.FEX流体, 0.2f)
        consume!!.power(1.9f)
        newProduce()
        produce!!.liquid(ILiquids.相位态FEX流体, 0.2f)

        draw = DrawMulti(
          DrawBottom(), DrawLiquidTile(ILiquids.FEX流体), object : DrawLiquidTile(ILiquids.相位态FEX流体) {
            init {
              drawLiquidLight = true
            }
          }, DrawDefault(), DrawRegion("_top")
        )
      }
    }

    fuel_packager = object : NormalCrafter("fuel_packager") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 45, Items.phaseFabric, 40, Items.silicon, 45, Items.graphite, 30
          )
        )
        size = 2
        autoSelect = true

        newConsume()
        consume!!.time(120f)
        consume!!.items(*ItemStack.with(IItems.铀235, 2, IItems.强化合金, 1))
        consume!!.power(1.5f)
        newProduce()
        produce!!.item(IItems.浓缩铀235核燃料, 1)
        newConsume()
        consume!!.time(120f)
        consume!!.items(*ItemStack.with(IItems.钚239, 2, IItems.强化合金, 1))
        consume!!.power(1.5f)
        newProduce()
        produce!!.item(IItems.浓缩钚239核燃料, 1)

        craftEffect = Fx.smeltsmoke

        draw = DrawMulti(DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>("_flue") {
          init {
            alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.get(IItems.强化合金) > 0 || e.progress() > 0.4f) 1f else 0f }
          }
        }, object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
          // 正确的写法
          init {
            alpha = Floatf<NormalCrafterBuild?> { it?.progress() ?: 0f }
            color = Func { e: NormalCrafterBuild? -> if (e!!.producer!!.current != null) e.producer!!.current!!.color else SglDrawConst.transColor }
          }
        })
      }
    }

    gas_phase_packer = object : NormalCrafter("gas_phase_packer") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 80, IItems.气凝胶, 80, Items.phaseFabric, 60, Items.silicon, 60, Items.graphite, 45
          )
        )
        size = 3

        hasLiquids = true
        liquidCapacity = 32f
        itemCapacity = 24

        warmupSpeed = 0.01f

        newConsume()
        consume!!.time(240f)
        consume!!.power(1.4f)
        consume!!.items(
          *ItemStack.with(
            Items.phaseFabric, 2, IItems.气凝胶, 2
          )
        )
        consume!!.liquid(Liquids.hydrogen, 0.4f)
        newProduce()
        produce!!.item(IItems.相位封装氢单元, 1)

        newConsume()
        consume!!.time(240f)
        consume!!.power(1.4f)
        consume!!.items(
          *ItemStack.with(
            Items.phaseFabric, 2, IItems.气凝胶, 2
          )
        )
        consume!!.liquid(ILiquids.氦气, 0.4f)
        newProduce()
        produce!!.item(IItems.相位封装氦单元, 1)

        draw = DrawMulti(
          DrawBottom(), DrawLiquidTile(), object : DrawBlock() {
          var piston: TextureRegion? = null

          override fun draw(build: Building) {

            for (i in 0..3) {
              val len = Mathf.absin(build.totalProgress() + 90 * i, 4f, 4f)
              val angle = i * 360f / 4

              Draw.rect(piston, build.x + Angles.trnsx(angle + 225, len), build.y + Angles.trnsy(angle + 225, len), angle)
            }
          }

          override fun load(block: Block) {
            piston = Core.atlas.find(block.name + "_piston")
          }
        }, object : DrawLiquidRegion() {
          init {
            suffix = "_liquid"
          }
        }, DrawDefault()
        )
      }
    }

    thermal_centrifuge = object : NormalCrafter("thermal_centrifuge") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 100, IItems.气凝胶, 80, Items.copper, 120, Items.silicon, 70, Items.plastanium, 75
          )
        )
        size = 3
        itemCapacity = 28

        warmupSpeed = 0.006f

        newConsume()
        consume!!.time(120f)
        consume!!.item(IItems.铀原料, 4)
        consume!!.power(3.8f)
        newProduce()!!.color = IItems.铀原料.color
        produce!!.items(*ItemStack.with(IItems.铀238, 3, IItems.铀235, 1))

        newConsume()
        consume!!.time(180f)
        consume!!.item(IItems.铱金混合物, 2)
        consume!!.power(3f)
        newProduce()!!.color = IItems.氯铱酸盐.color
        produce!!.item(IItems.铱, 1)

        newConsume()
        consume!!.time(120f)
        consume!!.item(IItems.黑晶石, 5)
        consume!!.power(2.8f)
        setByProduct(0.3f, Items.thorium)
        newProduce()!!.color = IItems.黑晶石.color
        produce!!.items(
          *ItemStack.with(
            IItems.铝, 3, Items.lead, 2
          )
        )

        craftEffect = Fx.smeltsmoke
        updateEffect = Fx.plasticburn

        draw = DrawMulti(DrawBottom(), object : DrawBlock() {
          override fun draw(build: Building?) {
            val e = build as NormalCrafterBuild
            if (e.producer!!.current == null) return

            /*if (Sgl.config.animateLevel < 2) {
                Draw.color(Liquids.slag.color, e.warmup())
                Fill.rect(e.x, e.y, (e.block.size * Vars.tilesize - Vars.tilesize).toFloat(), (e.block.size * Vars.tilesize - Vars.tilesize).toFloat())
                return
            }*/
            val region = Vars.renderer.fluidFrames[0][Liquids.slag.getAnimationFrame()]
            val toDraw = Tmp.tr1
            val bounds = size / 2f * Vars.tilesize
            val color = Liquids.slag.color

            for (sx in 0..<size) {
              for (sy in 0..<size) {
                val relx = sx - (size - 1) / 2f
                val rely = sy - (size - 1) / 2f

                toDraw.set(region)
                val rightBorder = relx * Vars.tilesize
                val topBorder = rely * Vars.tilesize
                val squishX = rightBorder + Vars.tilesize / 2f - bounds
                val squishY = topBorder + Vars.tilesize / 2f - bounds
                var ox = 0f
                var oy = 0f

                if (squishX >= 8 || squishY >= 8) continue

                if (squishX > 0) {
                  toDraw.setWidth(toDraw.width - squishX * 4f)
                  ox = -squishX / 2f
                }

                if (squishY > 0) {
                  toDraw.setY(toDraw.getY() + squishY * 4f)
                  oy = -squishY / 2f
                }

                Drawf.liquid(toDraw, e.x + rightBorder + ox, e.y + topBorder + oy, e.warmup(), color)
              }
            }
          }
        }, object : DrawRegion("_rim") {
          init {
            rotateSpeed = 0.8f
            spinSprite = true
          }
        }, DrawDefault(), object : DrawRegion("_rotator") {
          init {
            rotateSpeed = 1.8f
            spinSprite = true
          }
        }, object : DrawRegion("_toprotator") {
          init {
            rotateSpeed = -1.2f
          }
        }, object : DrawRegionDynamic<NormalCrafterBuild?>("_top") {
          init {
            rotation = Floatf { e: NormalCrafterBuild? -> -e!!.totalProgress() * 1.2f }
            color = Func { e: NormalCrafterBuild? -> if (e!!.producer!!.current != null) e.producer!!.current!!.color else SglDrawConst.transColor }
            alpha = Floatf { e: NormalCrafterBuild? ->
              val cons = if (e!!.consumer!!.current == null) null else e.consumer!!.current!!.get<ConsumeItemBase<*>>(ConsumeType.item)
              val i = if (cons == null) null else cons.consItems!![0].item
              if (cons == null) 0f else (e.items.get(i).toFloat()) / itemCapacity
            }
          }
        })
      }
    }

    lattice_constructor = object : NormalCrafter("lattice_constructor") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 80, IItems.充能FEX水晶, 60, IItems.FEX水晶, 75, Items.phaseFabric, 80
          )
        )
        size = 3

        itemCapacity = 20
        basicPotentialEnergy = 128f

        newConsume()
        consume!!.time(120f)
        consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
        consume!!.item(IItems.强化合金, 1)
        consume!!.energy(1.25f)
        newProduce()
        produce!!.item(IItems.FEX水晶, 4)

        craftEffect = SglFx.FEXsmoke



        initialed = Cons { e: SglBuilding ->
          e.drawAlphas = floatArrayOf(2.9f, 2.2f, 1.5f)

          draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_framework") {
            init {
              alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.强化合金) || e.progress() > 0.4f) 1f else 0f }
            }
          }, object : DrawRegionDynamic<NormalCrafterBuild?>() {
            init {
              alpha = FactoryBuildComp::progress as Floatf<NormalCrafterBuild?>?
            }

            override fun load(block: Block?) {
              region = Singularity.getModAtlas("FEX_crystal")
            }
          }, object : DrawBlock() {
            override fun draw(build: Building?) {
              val e = build as NormalCrafterBuild
              Draw.alpha(e.workEfficiency())
              Lines.lineAngleCenter(
                e.x + Mathf.sin(e.totalProgress(), 6f, Vars.tilesize.toFloat() / 3 * size), e.y, 90f, size.toFloat() * Vars.tilesize / 2
              )
              Draw.color()
            }
          }, DrawDefault(), object : DrawBlock() {
            var wave: TextureRegion? = null

            override fun load(block: Block?) {
              wave = Core.atlas.find(name + "_wave")
            }

            override fun draw(build: Building?) {

              val e = build as NormalCrafterBuild
              val alphas: FloatArray = e.drawAlphas

              Draw.z(Layer.effect)
              for (dist in 2 downTo 0) {
                Draw.color(SglDrawConst.fexCrystal)
                Draw.alpha((if (alphas[dist] <= 1) alphas[dist] else (if (alphas[dist] <= 1.5) 1 else 0).toFloat()) * e.workEfficiency())
                if (e.workEfficiency() > 0) {
                  if (alphas[dist] < 0.4) alphas[dist] += 0.6.toFloat()
                  for (i in 0..3) {
                    Draw.rect(
                      wave, e.x + dist * Geometry.d4(i).x * 3 + 5 * (Integer.compare(Geometry.d4(i).x, 0)), e.y + dist * Geometry.d4(i).y * 3 + 5 * (Integer.compare(Geometry.d4(i).y, 0)), ((i + 1) * 90).toFloat()
                    )
                  }
                  alphas[dist] -= (0.02 * e.edelta()).toFloat()
                } else {
                  alphas[dist] = 1.5f + 0.7f * (2 - dist)
                }
              }
            }
          })
        }
      }
    }
    FEX_crystal_charger = object : NormalCrafter("FEX_crystal_charger") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 70, IItems.FEX水晶, 60, Items.metaglass, 65, Items.phaseFabric, 70, Items.plastanium, 85
          )
        )
        size = 3

        itemCapacity = 15
        basicPotentialEnergy = 128f

        newConsume()
        consume!!.time(90f)
        consume!!.item(IItems.FEX水晶, 1)
        consume!!.energy(2f)
        newProduce()
        produce!!.item(IItems.充能FEX水晶, 1)

        updateEffect = SglFx.neutronWeaveMicro
        updateEffectChance = 0.04f
        updateEffectColor = SglDrawConst.fexCrystal
        craftEffect = SglFx.crystalConstructed
        craftEffectColor = SglDrawConst.fexCrystal



        crafting = Cons { e: NormalCrafterBuild? ->
          if (Mathf.chanceDelta((0.03f * e!!.workEfficiency()).toDouble())) {
            SglFx.shrinkParticleSmall.at(e.x, e.y, SglDrawConst.fexCrystal)
          }
        }

        draw = DrawMulti(DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>() {
          init {
            alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.FEX水晶) || e.progress() > 0.4f) 1f else 0f }
          }

          override fun load(block: Block?) {
            region = Singularity.getModAtlas("FEX_crystal")
          }
        }, object : DrawRegionDynamic<NormalCrafterBuild?>() {
          init {
            layer = Layer.effect
            alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.FEX水晶) || e.progress() > 0.4f) e.progress() else 0f }
          }

          override fun load(block: Block?) {
            region = Singularity.getModAtlas("FEX_crystal_power")
          }
        })
      }
    }

    matrix_cutter = object : NormalCrafter("matrix_cutter") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 80, IItems.充能FEX水晶, 75, Items.metaglass, 80, Items.phaseFabric, 90, Items.surgeAlloy, 120
          )
        )
        size = 4

        itemCapacity = 20
        basicPotentialEnergy = 256f



        newConsume()
        consume!!.time(120f)
        consume!!.energy(4.85f)
        consume!!.items(
          *ItemStack.with(
            IItems.充能FEX水晶, 1, IItems.强化合金, 2
          )
        )
        consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
        newProduce()
        produce!!.item(IItems.矩阵合金, 1)

        craftEffect = Fx.smeltsmoke

        draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_alloy") {
          init {
            alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.get(IItems.强化合金) >= 2) 1f else 0f }
          }
        }, object : DrawBlock() {
          override fun draw(build: Building) {

            val e = build as NormalCrafterBuild?
            SglDraw.drawBloomUnderBlock<NormalCrafterBuild?>(e, SglDraw.DrawAcceptor { b: NormalCrafterBuild? ->
              val dx = 5 * Mathf.sinDeg(build.totalProgress() * 1.35f)
              val dy = 5 * Mathf.cosDeg(build.totalProgress() * 1.35f)

              Draw.color(SglDrawConst.fexCrystal)
              Lines.stroke(0.8f * e!!.workEfficiency())

              Lines.line(b!!.x + dx, b.y + 6, b.x + dx, b.y - 6)
              Lines.line(b.x + 6, b.y + dy, b.x - 6, b.y + dy)
            })
            Draw.z(35f)
            Draw.reset()
          }
        }, DrawDefault(), object : DrawBlock() {
          override fun draw(build: Building?) {

            Draw.z(Layer.effect)
            val e = build as NormalCrafterBuild
            val angle = e.totalProgress()
            val realRotA = MathTransform.gradientRotateDeg(angle, 0f, 4)
            val realRotB = MathTransform.gradientRotateDeg(angle, 180f, 4)

            Lines.stroke(1.4f * e.workEfficiency(), SglDrawConst.fexCrystal)
            Lines.square(e.x, e.y, 20 + 4 * Mathf.sinDeg(realRotB), 45 + realRotA)

            Lines.stroke(1.6f * e.workEfficiency())
            Lines.square(e.x, e.y, 20 + 4 * Mathf.sinDeg(realRotA), 45 - realRotB)
          }
        })
      }
    }

    neutron_lens = object : NormalCrafter("neutron_lens") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 120, IItems.FEX水晶, 80, IItems.充能FEX水晶, 100, IItems.铱, 60, IItems.气凝胶, 120, Items.phaseFabric, 90
          )
        )
        size = 4
        itemCapacity = 20
        energyCapacity = 1024f
        basicPotentialEnergy = 256f

        warmupSpeed = 0.005f

        newConsume()
        consume!!.time(60f)
        consume!!.item(IItems.铀238, 1)
        consume!!.energy(1.2f)
        newProduce()
        produce!!.item(IItems.钚239, 1)

        newConsume()
        consume!!.time(60f)
        consume!!.item(IItems.相位封装氢单元, 1)
        consume!!.energy(1.5f)
        newProduce()
        produce!!.item(IItems.氢聚变燃料, 1)

        newConsume()
        consume!!.time(60f)
        consume!!.item(IItems.相位封装氦单元, 1)
        consume!!.energy(1.6f)
        newProduce()
        produce!!.item(IItems.氦聚变燃料, 1)

        newConsume()
        consume!!.time(90f)
        consume!!.item(IItems.核废料, 2)
        consume!!.liquid(ILiquids.相位态FEX流体, 0.2f)
        consume!!.energy(2.2f)
        newProduce()
        produce!!.items(
          *ItemStack.with(
            IItems.铱金混合物, 1, IItems.强化合金, 1, Items.thorium, 1
          )
        )

        draw = DrawMulti(
          DrawBottom(), object : DrawBlock() {
          override fun draw(build: Building) {
            LiquidBlock.drawTiledFrames(
              build.block.size, build.x, build.y, 4f, ILiquids.孢子云, (build as NormalCrafterBuild).consEfficiency()
            )
          }
        }, object : DrawRegionDynamic<NormalCrafterBuild?>("_light") {
          init {
            alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
            color = Func { e: NormalCrafterBuild? -> Tmp.c1.set(Pal.slagOrange).lerp(Pal.accent, Mathf.absin(5f, 1f)) }
          }
        }, object : DrawBlock() {
          override fun draw(build: Building) {

            val e = build as NormalCrafterBuild?
            val angle1 = MathTransform.gradientRotateDeg(build.totalProgress() * 0.8f, 180f, 0.5f, 4)
            val angle2 = MathTransform.gradientRotateDeg(build.totalProgress() * 0.8f, 0f, 0.25f, 4)

            Draw.color(Color.black)
            Fill.square(build.x, build.y, 3 * e!!.consEfficiency(), angle2 + 45)

            SglDraw.drawBloomUnderBlock<NormalCrafterBuild?>(e, SglDraw.DrawAcceptor { b: NormalCrafterBuild? ->
              Lines.stroke(0.75f * b!!.consEfficiency(), SglDrawConst.fexCrystal)
              Lines.square(b.x, b.y, 4 * b.consEfficiency(), angle2 + 45)

              Lines.stroke(0.8f * b.consEfficiency())
              Lines.square(b.x, b.y, 6 * b.consEfficiency(), -angle1 + 45)
              Draw.reset()
            })
            Draw.z(35f)
            Draw.reset()
          }
        }, DrawDefault(), DrawRegion("_top")
        )
      }
    }

    polymer_gravitational_generator = object : NormalCrafter("polymer_gravitational_generator") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.强化合金, 180, IItems.矩阵合金, 900, IItems.充能FEX水晶, 100, IItems.FEX水晶, 120, IItems.铱, 80, IItems.气凝胶, 100, Items.surgeAlloy, 80, Items.phaseFabric, 90
          )
        )
        size = 5
        itemCapacity = 20

        energyCapacity = 4096f
        basicPotentialEnergy = 1024f

        warmupSpeed = 0.0075f


        newConsume()
        consume!!.energy(10f)
        consume!!.items(
          *ItemStack.with(
            IItems.充能FEX水晶, 1, IItems.矩阵合金, 2, IItems.气凝胶, 3, IItems.铱, 1
          )
        )
        consume!!.time(240f)
        newProduce()
        produce!!.item(IItems.简并态中子聚合物, 1)

        craftEffect = SglFx.polymerConstructed
        val timeId = timers++

        draw = DrawMulti(DrawBottom(), object : DrawRegionDynamic<NormalCrafterBuild?>("_liquid") {
          init {
            color = Func { e: NormalCrafterBuild? -> SglDrawConst.ion }
            alpha = Floatf { obj: NormalCrafterBuild? -> obj!!.workEfficiency() }
          }
        }, object : DrawRegion("_rotator") {
          init {
            rotateSpeed = 1.75f
            rotation = 45f
          }
        }, object : DrawRegion("_rotator") {
          init {
            rotateSpeed = -1.75f
          }
        }, DrawDefault(), object : DrawBlock() {
          val dist: Distortion = Distortion()
          val taskID: Int = SglDraw.nextTaskID()

          override fun draw(build: Building?) {

            val e = build as NormalCrafterBuild
            Draw.z(Layer.effect)
            Draw.color(Pal.reactorPurple)
            Lines.stroke(0.4f * e.workEfficiency())
            Lines.square(e.x, e.y, 3 + Mathf.random(-0.15f, 0.15f))
            Lines.square(e.x, e.y, 4 + Mathf.random(-0.15f, 0.15f), 45f)

            Draw.z(Layer.flyingUnit + 0.5f)
            dist.setStrength(-32 * e.workEfficiency() * Vars.renderer.getScale())
            SglDraw.drawDistortion<NormalCrafterBuild?>(taskID, e, dist, SglDraw.DrawAcceptor { b: NormalCrafterBuild ->
              Distortion.drawVoidDistortion(b.x, b.y, 24 + Mathf.absin(6f, 4f), 32 * b.workEfficiency())
            })

            SglDraw.drawBloomUponFlyUnit<NormalCrafterBuild?>(e, SglDraw.DrawAcceptor { b: NormalCrafterBuild ->
              Draw.color(Pal.reactorPurple)
              Lines.stroke(3 * b.workEfficiency())
              Lines.circle(b.x, b.y, 24 + Mathf.absin(6f, 4f))

              for (p in Geometry.d4) {
                Tmp.v1.set(p.x.toFloat(), p.y.toFloat()).scl(28 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f)
                Draw.rect(
                  (SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), b.x + Tmp.v1.x, b.y + Tmp.v1.y, 8 * b.workEfficiency(), 8 * b.workEfficiency(), Tmp.v1.angle() + 90
                )

                Tmp.v2.set(p.x.toFloat(), p.y.toFloat()).scl(24 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f + 45)
                Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 4 * b.workEfficiency(), 4f, Tmp.v2.angle())
                Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 3 * b.workEfficiency(), 3f, Tmp.v2.angle() + 180)
              }
              Draw.reset()
            })

            if (e.timer(timeId, 15 / e.workEfficiency())) {
              SglFx.ploymerGravityField.at(e.x, e.y, 24 + Mathf.absin(6f, 4f), Pal.reactorPurple, e)
            }
          }
        })
      }
    }

    quality_generator = object : MediumCrafter("quality_generator") {
      init {
        requirements(Category.crafting, ItemStack.with())
        size = 4

        placeablePlayer = false

        energyCapacity = 16384f
        mediumCapacity = 32f

        newConsume()
        consume!!.energy(32f)
        newProduce()
        produce!!.medium(0.6f)
      }
    }

    substance_inverter = object : MediumCrafter("substance_inverter") {
      init {
        requirements(Category.crafting, ItemStack.with())
        size = 5

        placeablePlayer = false

        itemCapacity = 20
        energyCapacity = 1024f

        newConsume()
        consume!!.item(IItems.简并态中子聚合物, 1)
        consume!!.energy(5f)
        consume!!.medium(2.25f)
        consume!!.time(120f)
        newProduce()
        produce!!.item(IItems.反物质, 1)

        craftEffect = SglFx.explodeImpWaveBig
        craftEffectColor = Pal.reactorPurple

        craftedSound = Sounds.explosion
        craftedSoundVolume = 1f

        clipSize = 150f
        val generator: LightningGenerator = CircleGenerator().apply {

          radius = 13.5f
          minInterval = 1.5f
          maxInterval = 3f
          maxSpread = 2.25f

        }
        initialed = Cons { e: SglBuilding ->
          e.lightningDrawer = object : LightningContainer() {
            init {
              minWidth = 0.8f
              maxWidth = minWidth
              lifeTime = 24f
            }
          }
          e.lightnings = object : LightningContainer() {
            init {
              lerp = Interp.pow2Out
            }
          }
          e.lightningGenerator = VectorLightningGenerator().apply {
            maxSpread = 8f
            minInterval = 5f
            maxInterval = 12f
          }
        }

        crafting = Cons { e: NormalCrafterBuild? ->

          if (SglDraw.clipDrawable(e!!.x, e.y, clipSize) && Mathf.chanceDelta((e.workEfficiency() * 0.1f).toDouble())) e.lightningDrawer!!.create(generator)
          if (Mathf.chanceDelta((e.workEfficiency() * 0.04f).toDouble())) SglFx.randomLightning.at(e.x, e.y, 0f, Pal.reactorPurple)
        }

        craftTrigger = Cons { e: NormalCrafterBuild? ->
          if (!SglDraw.clipDrawable(e!!.x, e.y, clipSize)) return@Cons
          val a = Mathf.random(1, 3)

          if (true) {
            for (i in 0..<a) {
              val gen: VectorLightningGenerator = e.lightningGenerator!!
              gen.vector.rnd(Mathf.random(65, 100).toFloat())
              val amount = Mathf.random(3, 5)
              for (i1 in 0..<amount) {
                e.lightnings!!.create(gen)
              }

              if (Mathf.chance(0.25)) {
                SglFx.explodeImpWave.at(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple)
                Angles.randLenVectors(
                  System.nanoTime(), Mathf.random(4, 7), 2f, 3.5f, Floatc2 { x: Float, y: Float -> SglParticleModels.floatParticle.create(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple, x, y, Mathf.random(3.25f, 4f)) })
              } else {
                SglFx.spreadLightning.at(e.x + gen.vector.x, e.y + gen.vector.y, Pal.reactorPurple)
              }
            }
          }
          Effect.shake(5.5f, 20f, e.x, e.y)
        }

        draw = DrawMulti(DrawBottom(), object : DrawBlock() {
          override fun draw(build: Building?) {

            val e = build as NormalCrafterBuild

            SglDraw.drawBloomUnderBlock<NormalCrafterBuild?>(e, SglDraw.DrawAcceptor { b: NormalCrafterBuild ->
              val c: LightningContainer = b.lightningDrawer!!
              if (!Vars.state.isPaused()) c.update()

              Draw.color(Pal.reactorPurple)
              Draw.alpha(e.workEfficiency())
              c.draw(b.x, b.y)
            })
            Draw.z(35f)
            Draw.color()
          }
        }, DrawDefault(), object : DrawBlock() {
          override fun draw(build: Building?) {

            val e = build as NormalCrafterBuild
            Draw.z(Layer.effect)
            Draw.color(Pal.reactorPurple)
            val c: LightningContainer = e.lightnings!!
            if (!Vars.state.isPaused()) c.update()
            c.draw(e.x, e.y)
            val lerp = Noise.noise(Time.time, 0f, 3.5f, 1f)
            val offsetH = 6 * lerp
            val offsetW = 14 * lerp

            SglDraw.drawLightEdge(
              e.x, e.y, (35 + offsetH) * e.workEfficiency(), 2.25f * e.workEfficiency(), (145 + offsetW) * e.workEfficiency(), 4 * e.workEfficiency()
            )

            Draw.z(Layer.bullet - 10)
            Draw.alpha(0.2f * e.workEfficiency() + lerp * 0.25f)
            SglDraw.gradientCircle(e.x, e.y, 72 * e.workEfficiency(), 6 + 5 * e.workEfficiency() + 2.3f * lerp, SglDrawConst.transColor)
            Draw.alpha(0.3f * e.workEfficiency() + lerp * 0.25f)
            SglDraw.gradientCircle(e.x, e.y, 41 * e.workEfficiency(), -6 * e.workEfficiency() - 2f * lerp, SglDrawConst.transColor)
            Draw.alpha(0.55f * e.workEfficiency() + lerp * 0.25f)
            SglDraw.gradientCircle(e.x, e.y, 18 * e.workEfficiency(), -3 * e.workEfficiency() - lerp, SglDrawConst.transColor)
            Draw.alpha(1f)
            SglDraw.drawLightEdge(
              e.x, e.y, (60 + offsetH) * e.workEfficiency(), 2.25f * e.workEfficiency(), 0f, 0.55f, (180 + offsetW) * e.workEfficiency(), 4 * e.workEfficiency(), 0f, 0.55f
            )
          }
        })
      }
    }

    destructor = object : Destructor("destructor") {
      init {
        requirements(Category.effect, ItemStack.with())
        size = 5

        placeablePlayer = false
        recipeIndfo = Core.bundle.get("infos.destructItems")

        draw = DrawMulti(
          DrawBottom(), object : DrawPlasma() {
            init {
              suffix = "_plasma_"
              plasma1 = SglDrawConst.matrixNet
              plasma2 = SglDrawConst.matrixNetDark
            }
          }, DrawDefault()
        )
      }
    }

    hadron_reconstructor = object : AtomSchematicCrafter("hadron_reconstructor") {
      init {
        requirements(
          Category.crafting, ItemStack.with(
            IItems.简并态中子聚合物, 80, IItems.铱, 120, IItems.充能FEX水晶, 120, IItems.矩阵合金, 90, IItems.气凝胶, 120, Items.surgeAlloy, 90
          )
        )
        size = 4
        itemCapacity = 24

        placeablePlayer = false

        craftEffect = SglFx.hadronReconstruct

        draw = DrawMulti(
          DrawBottom(), object : DrawPlasma() {
          init {
            suffix = "_plasma_"
            plasma1 = Pal.reactorPurple
            plasma2 = Pal.reactorPurple2
          }
        }, object : DrawBlock() {
          override fun draw(build: Building?) {
            val e = build as NormalCrafterBuild
            Draw.alpha(e.progress())
            if (e.producer!!.current != null) Draw.rect(e.producer!!.current!!.get(ProduceType.item)!!.items[0].item.uiIcon, e.x, e.y, 4f, 4f)
            Draw.color()
          }
        }, DrawDefault()
        )
      }
    }
  }
}