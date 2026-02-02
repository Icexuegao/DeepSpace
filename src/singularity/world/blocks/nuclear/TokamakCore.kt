package singularity.world.blocks.nuclear

import arc.Core
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.GlyphLayout
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.struct.IntSet
import arc.struct.Seq
import arc.util.Align
import arc.util.Nullable
import arc.util.Strings
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.struct.AttachedProperty
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.ui.Fonts
import mindustry.world.Tile
import mindustry.world.meta.BlockStatus
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.blocks.nuclear.TokamakOrbit.TokamakOrbitBuild
import singularity.world.blocks.product.NormalCrafter
import singularity.world.meta.SglBlockGroup
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.components.blockcomp.SpliceBlockComp
import universecore.components.blockcomp.SpliceBuildComp
import universecore.world.blocks.chains.ChainsContainer
import universecore.world.blocks.modules.ChainsModule
import universecore.world.meta.UncStat
import universecore.world.particles.MultiParticleModel
import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel
import universecore.world.particles.models.DrawDefaultTrailParticle
import universecore.world.particles.models.ShapeParticle
import universecore.world.particles.models.TimeParticle
import universecore.world.particles.models.TrailFadeParticle
import kotlin.math.max

open class TokamakCore(name: String) : NormalCrafter(name), SpliceBlockComp {

  companion object {
    var ChainsContainer.TOTAL_ITEM_CAPACITY by AttachedProperty(0)
    var ChainsContainer.TOTAL_LIQUID_CAPACITY by AttachedProperty(0f)
    var ChainsContainer.valid by AttachedProperty(false)
    var Particle.OWNER: TokamakCoreBuild? by AttachedProperty(null)
    var Particle.inCorner: Vec2? by AttachedProperty(null)
    const val INV: Float = 0.01f

    private val model: ParticleModel = MultiParticleModel(object : TrailFadeParticle() {
      init {
        linear = true
        trailFade = 0.01f
        colorLerpSpeed = 0.03f
        fadeColor = Pal.reactorPurple.cpy().a(0.4f)
      }
    }, object : TimeParticle() {
      init {
        defLifeMin = 240f
        defLifeMax = 360f
      }
    }, object : ParticleModel() {
      val cacheVecs = Seq<Vec2>()
      var cursor: Int = -1

      override fun update(particle: Particle) {
        val tile = particle.tileOn()
        if (tile != null) {
          if (tile.build is TokamakOrbitBuild) {
            var b = tile.build as TokamakOrbitBuild
            corner(particle, b.isCorner, b, (b.rotation * 90).toFloat(), b.facingThis.size == 1)
          } else if (tile.build is TokamakCoreBuild) {
            var build = (tile.build as TokamakCoreBuild)
            val valid: Boolean = build.structValid()
            val isCorner = valid && build.relativeTo(build.outLinked) != build.inLinked!!.relativeTo(build)

            corner(particle, isCorner, build, (if (valid) build.relativeTo(build.outLinked) * 90 else 0).toFloat(), valid)
          } else align(particle)
        } else align(particle)
      }

      private fun corner(p: Particle, isCorner: Boolean, b: Building, rot: Float, valid: Boolean) {
        if (isCorner && valid) {
          if (p.inCorner == null) {
            val angle = p.speed.angle()
            val dir = Mathf.round(angle / 90) % 4
            when (dir) {
              0 -> p.x = b.x - b.block.size * Vars.tilesize / 2f
              1 -> p.y = b.y - b.block.size * Vars.tilesize / 2f
              2 -> p.x = b.x + b.block.size * Vars.tilesize / 2f
              3 -> p.y = b.y + b.block.size * Vars.tilesize / 2f
            }
            val out: Vec2 = (if (cursor < 0) Vec2() else cacheVecs.get(cursor--))!!
            out.set(p.x - b.x, p.y - b.y)

            p.speed.setAngle(angle + Angles.angleDist(angle, rot) / 2)

            out.setAngle(2 * (p.speed.angle() + 90) - out.angle()).add(b.x, b.y)

            p.inCorner = out
          }
        } else if (!isCorner) {
          if (p.inCorner is Vec2) {
            val v = p.inCorner as Vec2
            p.x = v.x
            p.y = v.y
            p.speed.setAngle(rot)
            freeVec(v)
            p.inCorner = null
          }
        }
      }

      private fun align(p: Particle) {
        if (p.inCorner is Vec2) {
          val v = p.inCorner as Vec2
          p.x = v.x
          p.y = v.y
          val tile = Vars.world.tileWorld(p.x, p.y)
          when (tile.build) {
            is TokamakOrbitBuild -> {
              p.speed.setAngle((tile.build.rotation * 90).toFloat())

              freeVec(v)
              p.inCorner = null
            }

            is TokamakCoreBuild -> {
              val comps = tile.build as TokamakCoreBuild
              if (!comps.structValid()) {
                p.speed.setZero()
                return
              }

              p.speed.setAngle((comps.relativeTo(comps.outLinked) * 90).toFloat())

              freeVec(v)
              p.inCorner = null
            }

            else -> {
              freeVec(v)
              p.inCorner = null

              p.speed.setZero()
            }
          }
        } else p.speed.setZero()
      }

      private fun freeVec(v: Vec2) {
        v.setZero()
        cursor++
        if (cursor < cacheVecs.size) {
          cacheVecs.set(cursor, v)
        } else cacheVecs.add(v)
      }
    }, object : ShapeParticle() {
      override fun draw(particle: Particle) {
        SglDraw.drawBloomUnderBlock(particle) { p: Particle? -> super.draw(p!!) }
      }
    }, object : DrawDefaultTrailParticle() {
      override fun drawTrail(p: Particle) {
        SglDraw.drawBloomUnderBlock(p) { particle: Particle? -> super.drawTrail(particle!!) }
      }
    })
  }

  override val maxChainsWidth = 0
  override var maxChainsHeight = 0
  override var interCorner = false
  override var negativeSplice = false
  var particleDensity: Float = 0.1f

  init {
    hasEnergy = true
    solid = true
    update = true
    group = SglBlockGroup.nuclear
    buildType = Prov(::TokamakCoreBuild)
  }

  fun setFuel(energyOut: Float) {
    newProduce()
    produce!!.energy(energyOut).setMultiple { e: TokamakCoreBuild -> e.energyOutMulti }
    val res = BooleanArray(1)
    newConsume()!!.consValidCondition { b: TokamakCoreBuild? ->
      res[0] = true
      b!!.items.each { i: Item?, a: Int ->
        if (res[0] && a < itemCapacity * 0.33f) {
          res[0] = false
        }
      }
      res[0]
    }
  }

  override fun setStats() {
    super.setStats()
    stats.remove(UncStat.maxStructureSize)
    setChainsStats(stats)
  }

  override fun init() {
    super.init()
    for (consumer in consumers) {
      for (cons in consumer.all()) {
        cons.setMultiple<TokamakCoreBuild?> { e: TokamakCoreBuild? -> e!!.fuelConsMulti }
      }
    }
  }

  override fun chainable(other: ChainsBlockComp): Boolean {
    return other is TokamakOrbit
  }

  override fun setBars() {
    super.setBars()
    addBar<TokamakCoreBuild?>("efficiency") { e: TokamakCoreBuild? ->
      Bar({ Core.bundle.format("bar.efficiency", Strings.autoFixed(Mathf.round(Mathf.pow(e!!.warmup(), 3f) * 100).toFloat(), 1)) }, { Pal.powerBar }, { Mathf.pow(e!!.warmup(), 3f) })
    }
    addBar<TokamakCoreBuild?>("scale") { e: TokamakCoreBuild? ->
      Bar({ Core.bundle.format("bar.scale", Strings.autoFixed(e!!.fuelConsMulti, 1), Strings.autoFixed(e.energyOutMulti, 1)) }, { Pal.powerBar }, { if (e!!.scale > 0f) 1f else 0f })
    }
  }

  inner class TokamakCoreBuild : NormalCrafterBuild(), SpliceBuildComp {
    //   const val : String = "totalItemCapacity"
//        const val : String = "totalLiquidCapacity"

    override var loadingInvalidPos = IntSet()
    override var chains = ChainsModule(this)
    override var splice = 0

    @Nullable
    var outLinked: TokamakOrbitBuild? = null

    @Nullable
    var inLinked: TokamakOrbitBuild? = null
    var fuelConsMulti: Float = 0f
    var energyOutMulti: Float = 0f
    var scale: Int = 0
    var recooldown: Boolean = false

    override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building {
      super.init(tile, team, shouldAdd, rotation)
      chains.newContainer()

      return this
    }

    fun structValid(): Boolean {
      return chains.container.valid
    }

    override fun canChain(other: ChainsBuildComp): Boolean {
      return chainable(other.chainsBlock) && other is TokamakOrbitBuild && (relativeTo(other).toInt() == other.rotation || relativeTo(other).toInt() == (other.rotation + 2) % 4)
    }

    override fun status(): BlockStatus? {
      return if (!structValid()) BlockStatus.noOutput else super.status()
    }

    override fun onProximityAdded() {
      super.onProximityAdded()
      onChainsAdded()
    }

    override fun onProximityRemoved() {
      super.onProximityRemoved()
      onChainsRemoved()
    }

    override fun write(write: Writes) {
      super.write(write)
      writeChains(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      readChains(read)
    }

    override fun onProximityUpdate() {
      inLinked = null
      outLinked = null
      for (build in chainBuilds()) {
        if (build is TokamakOrbitBuild) {
          if (relativeTo(build).toInt() == build.rotation) {
            if (outLinked == null) {
              outLinked = build
            } else {
              outLinked = null
              inLinked = null
              break
            }
          }

          if (build.relativeTo(this).toInt() == build.rotation) {
            if (inLinked == null) {
              inLinked = build
            } else {
              outLinked = null
              inLinked = null
              break
            }
          }
        }
      }
      updateRegionBit()
    }

    override fun onChainsUpdated() {
      chains.container.valid = false

      if (inLinked == null) return

      for (comp in chains.container.all) {
        if (comp is TokamakCoreBuild && comp !== this) {
          return
        }
      }
      var curr = outLinked
      var cornerCount = 0
      var w = 0
      var h = 0
      var count = 0
      var itemCap = 0
      var liqCap = 0f
      var enclosed = false

      while (curr != null) {
        itemCap += curr.block.itemCapacity
        liqCap += curr.block.liquidCapacity
        val next = curr.facingNext
        if (next is TokamakOrbitBuild) {
          if (next.facingThis.size > 1) break

          if (curr.relativeTo(next).toInt() != next.rotation) {
            cornerCount++

            if (curr.rotation == 0 || curr.rotation == 2) {
              w = max(w, count * outLinked!!.block.size)
            } else {
              h = max(h, count * outLinked!!.block.size)
            }
            count = 0
          } else count++

          curr = next
        } else {
          if (next is TokamakCoreBuild && next === this) {
            if (curr.relativeTo(this) != relativeTo(outLinked)) {
              cornerCount++

              if (curr.rotation == 0 || curr.rotation == 2) {
                w = max(w, count * outLinked!!.block.size)
              } else {
                h = max(h, count * outLinked!!.block.size)
              }
              count = 0
            }

            enclosed = true
          }
          curr = null
        }

        if (cornerCount > 4) {
          break
        }
      }

      if (cornerCount == 4 && enclosed) {
        chains.container.valid = true
        scale = w * h
        val area = scale * INV

        fuelConsMulti = Mathf.sqrt(area) * (outLinked!!.block as TokamakOrbit).flueMulti
        energyOutMulti = area * (outLinked!!.block as TokamakOrbit).efficiencyPow

        chains.container.TOTAL_ITEM_CAPACITY = itemCap
        chains.container.TOTAL_LIQUID_CAPACITY = liqCap
      } else {
        chains.container.valid = false
        energyOutMulti = 0f
        fuelConsMulti = energyOutMulti
        scale = 0

        chains.container.TOTAL_ITEM_CAPACITY = 0
        chains.container.TOTAL_LIQUID_CAPACITY = 0f
      }
    }

    override fun getMaximumAccepted(item: Item?): Int {
      return if (structValid()) {
        chains.container.TOTAL_ITEM_CAPACITY
      } else 0
    }

    override fun getMaximumAccepted(liquid: Liquid?): Float {
      return if (structValid()) {
        chains.container.TOTAL_LIQUID_CAPACITY
      } else 0f
    }

    override fun consumeValid(): Boolean {
      return super.consumeValid() && (consumer.current == null || consumer.consEfficiency > 0.9f)
    }

    override fun shouldConsume(): Boolean {
      return structValid() && !recooldown && super.shouldConsume()
    }

    override fun drawStatus() {
      super.drawStatus()
      val status = if (!structValid()) Core.bundle.get("infos.structInvalid") else if (recooldown) Core.bundle.get("infos.recoolanting") else null

      if (status == null) return
      val layout = GlyphLayout.obtain()
      layout.setText(Fonts.outline, status)
      val w = layout.width * 0.185f
      val h = layout.height * 0.185f

      layout.free()
      Draw.color(Color.darkGray, 0.6f)
      Fill.quad(
        x - w / 2 - 2, y + size * Vars.tilesize / 2f + h + 2, x - w / 2 - 2, y + size * Vars.tilesize / 2f - 2, x + w / 2 + 2, y + size * Vars.tilesize / 2f - 2, x + w / 2 + 2, y + size * Vars.tilesize / 2f + h + 2
      )

      Fonts.outline.draw(status, x, y + size * Vars.tilesize / 2f + h, Color.white, 0.185f, false, Align.center)
    }

    override fun consEfficiency(): Float {
      return super.consEfficiency() * (warmup() * warmup())
    }

    override fun updateTile() {
      super.updateTile()

      chains.container.update()

      if (!consumeValid() || !structValid()) {
        recooldown = true
      } else if (warmup() <= 0.2f) {
        recooldown = false
      }

      if (structValid() && Mathf.chanceDelta((warmup() * warmup() * warmup() * particleDensity).toDouble())) {
        val blockSize = outLinked!!.block.size * Vars.tilesize
        when (relativeTo(outLinked).toInt()) {
          0 -> Tmp.v1.set(outLinked!!.x - blockSize / 2f, outLinked!!.y + Mathf.range(blockSize / 4f))
          1 -> Tmp.v1.set(outLinked!!.x + Mathf.range(blockSize / 4f), outLinked!!.y - blockSize / 2f)
          2 -> Tmp.v1.set(outLinked!!.x + blockSize / 2f, outLinked!!.y + Mathf.range(blockSize / 4f))
          3 -> Tmp.v1.set(outLinked!!.x + Mathf.range(blockSize / 4f), outLinked!!.y + blockSize / 2f)
        }
        Tmp.v2.set(Mathf.random(4f, 8f), 0f).setAngle((relativeTo(outLinked) * 90).toFloat())
        val p = model.create(
          Tmp.v1.x, Tmp.v1.y, SglDrawConst.matrixNet, Tmp.v2.x, Tmp.v2.y, Mathf.random(0.2f, 0.5f), Layer.block
        )
        p.maxCloudCounts = Mathf.random(40, 55)
        p.OWNER = this
        p.lifetime = Mathf.random(12.8f, 16.4f) * Mathf.sqrt(scale.toFloat())
      }
    }
  }
}