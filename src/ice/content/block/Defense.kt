package ice.content.block

import arc.func.Cons2
import arc.func.Intf
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.scene.style.TextureRegionDrawable
import arc.struct.ObjectSet
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pool
import arc.util.pooling.Pools
import ice.content.IItems
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.defense.Wall
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.UnitSorts
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.LaserBulletType
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Building
import mindustry.gen.Bullet
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.AutoDoor
import mindustry.world.blocks.defense.ShieldWall
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Stats
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.SglUnitSorts
import singularity.world.blocks.defence.GameOfLife
import singularity.world.blocks.defence.PhasedRadar
import singularity.world.blocks.defence.SglWall
import singularity.world.draw.DrawDirSpliceBlock
import singularity.world.meta.SglStat
import universecore.world.consumers.BaseConsumers
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.generator.VectorLightningGenerator

@Suppress("unused")
object Defense : Load {
  val 铬墙: Block = Wall("chromeWall").apply {
    health = 450
    size = 1
    requirements(Category.defense, ItemStack.with(IItems.铬锭, 6))
    bundle {
      desc(zh_CN, "铬墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
    }
  }
  val 大型铬墙: Block = Wall("chromeWallLarge").apply {
    size = 2
    health = 铬墙.health * 4
    requirements(Category.defense, ItemStack.with(IItems.铬锭, 6 * 4))
    bundle {
      desc(zh_CN, "大型铬墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
    }
  }
  val 碳钢墙: Block = Wall("carbonSteelWall").apply {
    size = 1
    armor = 5f
    health = 320
    chanceDeflect = 0.1f
    requirements(Category.defense, ItemStack.with(IItems.高碳钢, 3, IItems.低碳钢, 3))
    bundle {
      desc(zh_CN, "碳钢墙", "保护己方建筑,挡下敌方炮弹")
    }
  }
  val 大型碳钢墙: Block = Wall("carbonSteelWallLarge").apply {
    size = 2
    armor = 5f
    chanceDeflect = 0.15f
    health = 碳钢墙.health * size * size
    requirements(Category.defense, ItemStack.with(IItems.高碳钢, 3 * size * size, IItems.低碳钢, 3 * size * size))
    bundle {
      desc(zh_CN, "大型碳钢墙", "保护己方建筑,挡下敌方炮弹")
    }
  }
  val 流金墙: Block = Wall("fluxGoldWall").apply {
    size = 1
    armor = 5f
    health = 1000
    requirements(Category.defense, ItemStack.with(IItems.金锭, 10))
    bundle {
      desc(zh_CN, "流金墙", "熔融金锭构筑的壁垒,随时间缓慢自愈", "财富值++")
    }
  }
  val 大型流金墙: Block = Wall("fluxGoldWallLarge").apply {
    size = 2
    armor = 5f
    health = 流金墙.health * 4
    requirements(Category.defense, IItems.金锭, 10)
    bundle {
      desc(zh_CN, "大型流金墙", "熔融金锭构筑的壁垒,随时间缓慢自愈", "财富值++")
    }
  }
  val 钴钢墙: Block = Wall("cobaltSteelWall").apply {
    size = 1
    health = 700
    requirements(Category.defense, IItems.钴钢, 8)
    bundle {
      desc(zh_CN, "钴钢墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
    }
  }
  val 大型钴钢墙: Block = Wall("cobaltSteelWallLarge").apply {
    size = 2
    health = 钴钢墙.health * 4
    requirements(Category.defense, IItems.钴钢, 32)
    bundle {
      desc(zh_CN, "大型钴钢墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
    }
  }
  val 铱墙: Block = Wall("iridiumWall").apply {
    armor = 5f
    health = 1800
    crushDamageMultiplier = 2.5f
    bundle {
      desc(zh_CN, "铱墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
    }
    requirements(Category.defense, IItems.铱板, 6)
  }
  val 大型铱墙: Block = Wall("iridiumWallLarge").apply {
    size = 2
    armor = 20f
    health = 7200
    crushDamageMultiplier = 2.5f
    bundle {
      desc(zh_CN, "大型铱墙", "坚固耐用,保护己方建筑,挡下敌方炮弹")
    }
    requirements(Category.defense, IItems.铱板, 24)
  }
  val 装甲闸门: Block = AutoDoor("armorGate").apply {
    size = 2
    armor = 20f
    health = 6400
    insulated = true
    absorbLasers = true
    placeableLiquid = true
    crushDamageMultiplier = 1f
    requirements(Category.defense, IItems.铱板, 24, IItems.导能回路, 16)
    bundle {
      desc(zh_CN, "装甲闸门", "安全可靠,坚实耐用,自动开关")
    }
  }
  val 陶钢墙: Block = Wall("potterySteelWall").apply {
    health = 1200
    armor = 3f
    floating = true
    insulated = true
    absorbLasers = true
    bundle {
      desc(
        zh_CN, "陶钢墙", "坚固耐用,保护己方建筑,挡下敌方炮弹,能吸收激光和电弧,会阻止电力节点自动连接,可以放置在岸边"
      )
    }
    requirements(Category.defense, IItems.陶钢, 6)
  }
  val 大型陶钢墙: Block = Wall("potterySteelWallLarge").apply {
    health = 4800
    armor = 12f
    size = 2
    floating = true
    insulated = true
    absorbLasers = true
    bundle {
      desc(
        zh_CN, "大型陶钢墙", "坚固耐用,保护己方建筑,挡下敌方炮弹,能吸收激光和电弧,会阻止电力节点自动连接,可以放置在岸边"
      )
    }
    requirements(Category.defense, IItems.陶钢, 24)
  }
  val 相位合金墙: Block = ShieldWall("phaseAlloyWall").apply {
    health = 1500
    armor = 6f
    flashHit = true
    chanceDeflect = 75f
    lightningChance = 0.5f
    lightningDamage = 25f
    lightningLength = 5
    shieldHealth = 600f
    regenSpeed = 1f
    breakCooldown = 300f
    crushDamageMultiplier = 1f
    requirements(Category.defense, IItems.导能回路, 2, IItems.金锭, 2, IItems.铪锭, 2)
    bundle {
      desc(zh_CN, "相位合金墙", "创建一个力场保护自身,具有大多数墙的特点")
    }
  }
  val 大型相位合金墙: Block = ShieldWall("phaseAlloyWallLarge").apply {
    health = 6000
    armor = 24f
    size = 2
    flashHit = true
    chanceDeflect = 75f
    lightningChance = 0.5f
    lightningDamage = 50f
    lightningLength = 20
    shieldHealth = 2400f
    regenSpeed = 4f
    breakCooldown = 1200f
    crushDamageMultiplier = 1f
    requirements(Category.defense, IItems.导能回路, 8, IItems.金锭, 8, IItems.铪锭, 8)
    bundle {
      desc(zh_CN, "大型相位合金墙", "创建一个力场保护自身,具有大多数墙的特点")
    }
  }
  val 生物钢墙: Block = Wall("biologicalSteelWall").apply {
    health = 8000
    armor = 8f
    insulated = true
    absorbLasers = true
    placeableLiquid = true
    crushDamageMultiplier = 0.5f
    healAmount = 200f
    damageReduction = 0.8f
    requirements(Category.defense, IItems.生物钢, 8, IItems.铱板, 4, IItems.陶钢, 4)
    bundle {
      desc(
        zh_CN, "生物钢墙", "坚固耐用,复合装甲结构使其可以减免部分伤害,而活性生物质夹层允许其快速自我修复,\n能吸收激光和电弧,会阻止电力节点自动连接,可以放置在深水中"
      )
    }
  }
  val 大型生物钢墙: Block = Wall("biologicalSteelWallLarge").apply {
    health = 32000
    armor = 32f
    size = 2
    healAmount = 800f
    damageReduction = 0.8f
    insulated = true
    absorbLasers = true
    placeableLiquid = true
    crushDamageMultiplier = 0.5f
    requirements(Category.defense, IItems.生物钢, 32, IItems.铱板, 16, IItems.陶钢, 16)
    bundle {
      desc(
        zh_CN, "大型生物钢墙", "坚固耐用,复合装甲结构使其可以减免部分伤害,而活性生物质夹层允许其快速自我修复,\n能吸收激光和电弧,会阻止电力节点自动连接,可以放置在深水中"
      )
    }
  }
  val 相控雷达 = PhasedRadar("phased_radar").apply {
    bundle {
      desc(zh_CN, "相控雷达", "雷达单元,可构成相控雷达阵列,锁定范围内的敌方空中单位,攻击被锁定的目标有概率造成额外大量伤害,杀伤倍率和概率都取决于阵列大小")
    }
    squareSprite=false
    requirements(Category.effect, ItemStack.with())
    newConsume()
    consume!!.power(1f)
    draw = DrawMulti(DrawDefault(), object : DrawDirSpliceBlock<PhasedRadar.PhasedRadarBuild?>() {
      init {
        simpleSpliceRegion = true
        spliceBits = Intf { e: PhasedRadar.PhasedRadarBuild? -> e!!.spliceDirBit }
        layerRec = false
      }
    }, object : DrawRegion("_rotator") {
      init {
        layer = Layer.blockOver
        rotateSpeed = 0.4f
      }
    })
  }
  val 强化合金墙 = Wall("strengthening_alloy_wall").apply {
    bundle {
      desc(zh_CN, "强化合金墙", "大型的强化合金制造的墙,十分坚固")
    }
    requirements(Category.defense, ItemStack.with(IItems.强化合金, 8))
    health = 900

  }
  val 大型强化合金墙 = Wall("strengthening_alloy_wall_large").apply {
    bundle {
      desc(zh_CN, "大型强化合金墙", "大型的强化合金制造的墙,十分坚固")
    }
    requirements(Category.defense, ItemStack.with(IItems.强化合金, 32))
    size = 2
    health = 900 * 4
  }
  val 简并态中子聚合物墙 = SglWall("neutron_polymer_wall").apply {
    bundle {
      desc(zh_CN, "简并态中子墙", "利用简并态中子聚合物建造的墙,强度极高,且可以产生巨大的引力吸引子弹,并吸收不太强的子弹以修补自身")
    }
    requirements(Category.defense, ItemStack.with(IItems.简并态中子聚合物, 8, IItems.强化合金, 4))
    health = 2400
    density = 1024f
    damageFilter = 72f
    absorbLasers = true
  }
  val 大型简并态中子墙 = SglWall("neutron_polymer_wall_large").apply {
    bundle {
      desc(zh_CN, "大型简并态中子墙", "利用简并态中子聚合物建造的大型的墙,强度极高,且可以产生巨大的引力吸引子弹,并吸收不太强的子弹以修补自身")
    }
    requirements(Category.defense, ItemStack.with(IItems.简并态中子聚合物, 32, IItems.强化合金, 16, IItems.气凝胶, 8))
    size = 2
    health = 2400 * 4
    density = 1024f
    damageFilter = 95f
    absorbLasers = true
    squareSprite=false
  }
  val 混沌矩阵 = GameOfLife("attack_matrix").apply {
    bundle {
      desc(
        zh_CN,
        "生命游戏-混沌矩阵",
        "庞大且有效的重型防御系统,反物质在力场的引导下,会按照生命游戏的规则在网格中不断演化,并衍生出可怕的效果\n具体来说,生命游戏所在的网格上,每一个单元格都是一个细胞,细胞只有两种状态,生与死,每一个细胞在一次刷新后的状态由它周围8个细胞决定,会遵循如下规则进行演化:\n[accent]当这个细胞处于死亡状态时[]\n[green]1.若周围的存活细胞数量为3则在下一刻转变为存活状态[]\n[gray]2.任何其他情况,细胞状态不变[]\n[accent]当这个细胞处于存活状态时[]\n[red]  1.若周围的存活细胞数量多于4(含4)则下一刻细胞死亡[]\n[red]2.若周围存活细胞数量少于2(不含2)则下一刻细胞死亡[] \n[gray]3.任何其他情况,细胞状态不变[]\n每一次刷新,所有存活的细胞寿命会增加1,在细胞死亡时,根据细胞的年龄会触发相应的效果,随细胞寿命的增加,强度会大幅度增强",
        "生命游戏这一概念原本是由数学家康威设计的一个计算机程序,早期,人们仅仅研究与探索了生命游戏在计算机程序程序级别的规律,其与自然科学的重叠几乎完全被忽视了,直到二十一世纪60年代,科学家发现在真空仓内的粒子涨落规律与生命游戏在形式上高度重合,生命游戏规则成为了一项科学规律\n在这一规则下进行受控的反物质湮灭可以得到最接近理论数据的能量利用效率,因此这一规则广泛被应用于能源与武装,不过,像混沌矩阵这样彻底的巨型网格本身还是一个过于大胆的尝试,所幸在巨大的学术压力下中止数次数次后该项目还是收获了非常突出的成果"
      )
    }
    requirements(Category.defense, ItemStack.with())
    size = 8
    health = 5200
    hasItems = true
    itemCapacity = 64
    energyCapacity = 16384f
    basicPotentialEnergy = 8192f
    squareSprite=false
    newConsume()
    consume!!.power(160f)
    consume!!.energy(20f)

    launchCons.time(600f)
    launchCons.energy(12f)
    launchCons.item(IItems.反物质, 1)
    launchCons.display = Cons2 { s: Stats?, c: BaseConsumers? ->
      s!!.add(SglStat.multiple, 4.toString() + "*cells")
    }

    draw = DrawMulti(
      DrawDefault(), object : DrawBlock() {
        override fun draw(build: Building?) {
          if (build is GameOfLife.GameOfLifeBuild) {

            Draw.z(Layer.effect)
            Draw.color(Color.white)

            Fill.square(build.x, build.y, 6 * build.depoly, 45f)

            Lines.stroke(1.5f * build.depoly)
            Lines.square(build.x, build.y, 8 + 8 * build.depoly, -Time.time * 2)
            Lines.stroke(2 * build.depoly)
            Lines.square(build.x, build.y, 10 + 18 * build.depoly, Time.time)

            Lines.stroke(3 * build.depoly)
            Lines.square(build.x, build.y, 40 * build.depoly, 45f)

            if (build.depoly >= 0.99f && !build.launched && build.activity) {
              Fill.square(build.x, build.y, 30 * build.warmup, 45f)
            }

            for (p in Geometry.d4) {
              Tmp.v1.set(p.x.toFloat(), p.y.toFloat()).scl(50 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f)
              Draw.rect((SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), build.x + Tmp.v1.x, build.y + Tmp.v1.y, 16 * build.warmup, 16 * build.warmup, Tmp.v1.angle() + 90)
            }
          }
        }
      })

    addDeathTrigger(0, GameOfLife.effectEnemy(StatusEffects.electrified, cellSize / 2 * 3.2f, 45f))
    addDeathTrigger(1, 2, GameOfLife.damage(260f, cellSize / 2 * 3.2f, 2f))
    addDeathTrigger(1, 2, GameOfLife.effectEnemy(StatusEffects.sapped, cellSize / 2 * 3.2f, 60f))

    addDeathTrigger(3, GameOfLife.shootBullet(object : BulletType() {
      init {
        damage = 420f
        speed = 6f
        lifetime = 72f
        homingRange = 300f
        homingPower = 0.12f

        pierceArmor = true

        status = StatusEffects.slow
        statusDuration = 60f

        despawnHit = true
        hitEffect = SglFx.spreadDiamondSmall
        hitColor = Color.white

        trailEffect = Fx.trailFade
        trailColor = Color.black
        trailLength = 30
        trailWidth = 2.5f
        layer = Layer.bullet - 1
      }

      override fun draw(b: Bullet) {
        super.draw(b)

        Draw.color(Color.black)
        Fill.square(b.x, b.y, 5f, Time.time * 5)

        Draw.color(Color.white)
        Lines.stroke(2f)
        Lines.square(b.x, b.y, 10f, -Time.time * 5)
      }
    }, object : ShootPattern() {
      init {
        shots = 4
      }

      override fun shoot(totalShots: Int, handler: BulletHandler) {
        for (i in 0..<shots) {
          handler.shoot(0f, 0f, (45 + i * 90).toFloat(), 0f)
        }
      }
    }))

    addDeathTrigger(4, 5, GameOfLife.fx(SglFx.crossLight))
    addDeathTrigger(4, GameOfLife.shootBullet(object : BulletType() {
      init {
        damage = 285f
        speed = 4.5f
        lifetime = 70f
        homingRange = 360f
        homingPower = 0.25f

        pierceArmor = true

        hitEffect = SglFx.spreadDiamondSmall
        hitColor = Color.white

        trailEffect = Fx.trailFade
        trailColor = Color.black
        trailLength = 24
        trailWidth = 2.5f
        layer = Layer.bullet - 1

        fragBullets = 1
        fragOnAbsorb = true
        fragAngle = 0f
        fragSpread = 0f
        fragRandomSpread = 0f
        fragBullet = object : LaserBulletType() {
          init {
            length = 280f
            damage = 280f
            layer = Layer.bullet - 1
            colors = arrayOf<Color?>(Color.black.cpy().a(0.4f), Color.black, Color.white)
          }
        }
      }

      override fun draw(b: Bullet) {
        super.draw(b)

        Draw.color(Color.black)
        Fill.square(b.x, b.y, 5f, Time.time * 5)

        Draw.color(Color.white)
        Lines.stroke(1.5f)
        Lines.square(b.x, b.y, 8f, -Time.time * 5)
        Lines.stroke(2.5f)
        Lines.square(b.x, b.y, 14f, Time.time * 5)
      }
    }, object : ShootPattern() {
      init {
        shots = 4
      }

      override fun shoot(totalShots: Int, handler: BulletHandler) {
        for (i in 0..<shots) {
          handler.shoot(0f, 0f, (i * 90).toFloat(), 0f)
        }
      }
    }))

    addDeathTrigger(5, GameOfLife.shootBullet(object : BulletType() {
      init {
        damage = 180f
        clipSize = 200f
        speed = 0f
        homingRange = 320f
        homingPower = 0f
        homingDelay = 240f
        lifetime = 240f
        hittable = false
        collides = false
        absorbable = false
        reflectable = false
        layer = Layer.bullet - 1
        pierce = true
        pierceCap = -1
        pierceBuilding = true
        removeAfterPierce = false

        status = StatusEffects.shocked
        statusDuration = 30f

        trailEffect = SglFx.spreadDiamond
        trailColor = Color.white
        trailInterval = 60f
        trailWidth = 2.5f
        trailLength = 26

        despawnEffect = SglFx.spreadField

        hitEffect = SglFx.spreadDiamondSmall
        hitColor = Color.white
      }

      val gen: VectorLightningGenerator = VectorLightningGenerator().apply {

        minInterval = 12f
        maxInterval = 26f
        maxSpread = 14f

      }

      override fun init(b: Bullet) {
        super.init(b)
        b.data = Pools.obtain(Data::class.java) { Data() }
      }

      override fun removed(b: Bullet) {
        if (b.data is Data) {
          val data = b.data
          Pools.free(data)
        }
        super.removed(b)
      }

      override fun update(b: Bullet) {
        super.update(b)
        val data = b.data
        if (data is Data) {
          data.container.update()

          if (b.timer(2, 6f)) {
            data.related.clear()
            data.aim = null

            for (unit in SglUnitSorts.findEnemies(3, b, 320f, UnitSorts.farthest)) {
              if (unit == null) continue

              if (data.aim == null) data.aim = unit

              if (!unit.within(b.x, b.y, 200f) || data.related.size >= 3) return

              if (data.related.add(unit)) {
                unit.damage(damage)
                unit.apply(status, statusDuration)
                hitEffect.at(unit.x, unit.y, hitColor)

                gen.vector.set(unit.x - b.x, unit.y - b.y)
                data.container.create(gen)
              }
            }
          }

          if (data.aim != null) {
            b.vel.lerpDelta(Tmp.v1.set(data.aim!!.x - b.x, data.aim!!.y - b.y).setLength(2f), 0.05f)
          }
        }
      }

      override fun draw(b: Bullet) {
        super.draw(b)

        Draw.color(Color.black)
        Fill.square(b.x, b.y, 4f, Time.time * 2)

        Draw.z(Layer.bullet)
        Draw.color(Color.white)
        Lines.stroke(1.5f)
        Lines.square(b.x, b.y, 8f, -Time.time * 3)
        Lines.stroke(2.5f)
        Lines.square(b.x, b.y, 16f, Time.time * 4)
        Lines.stroke(3f)
        Lines.square(b.x, b.y, 24f, -Time.time * 5)
        val data = b.data
        if (data is Data) {
          data.container.draw(b.x, b.y)
          for (other in data.related) {
            Draw.z(Layer.bullet - 1)
            Lines.stroke(5f, Color.black)
            Lines.line(b.x, b.y, other.x, other.y)
            Draw.z(Layer.bullet)
            Lines.stroke(3.25f, Color.white)
            Lines.line(b.x, b.y, other.x, other.y)
          }
        }
      }

      override fun continuousDamage(): Float {
        return damage * 10
      }
    }, object : ShootPattern() {
      init {
        shots = 3
      }

      override fun shoot(totalShots: Int, handler: BulletHandler) {
        handler.shoot(0f, 0f, 0f, 0f)
      }
    }))
  }

  class Data : Pool.Poolable {
    val container: LightningContainer = object : LightningContainer() {
      init {
        maxWidth = 6f
        minWidth = 4f

        time = 0f
        lifeTime = 18f
      }
    }
    val related: ObjectSet<Unit> = ObjectSet<Unit>()
    var aim: Unit? = null

    override fun reset() {
      related.clear()
      aim = null
    }
  }
}