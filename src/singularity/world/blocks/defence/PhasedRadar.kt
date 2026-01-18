package singularity.world.blocks.defence

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.scene.ui.layout.Table
import arc.struct.IntSet
import arc.struct.ObjectSet
import arc.util.Interval
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.struct.AttachedProperty
import mindustry.Vars
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.contents.OtherContents
import singularity.graphic.SglDraw
import singularity.world.blocks.SglBlock
import singularity.world.meta.SglStat
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.SpliceBlockComp
import universecore.components.blockcomp.SpliceBuildComp
import universecore.world.blocks.chains.ChainsContainer
import universecore.world.blocks.modules.ChainsModule
import kotlin.math.min

open class PhasedRadar(name: String) : SglBlock(name), SpliceBlockComp {
  companion object {
    var ChainsContainer.BUILD: PhasedRadarBuild? by AttachedProperty(null)
  }

  override var maxChainsWidth: Int = 16
  override var maxChainsHeight: Int = 16
  override var interCorner = false
  override var negativeSplice = false
  var range: Int = 48
  var scanTime: Float = 15f
  private val timeId: Int

  init {
    update = true
    solid = true
    conductivePower = true
    timeId = timers++
    canOverdrive = false
    buildType = Prov(::PhasedRadarBuild)
  }

  override fun chainable(other: ChainsBlockComp): Boolean {
    return other === this
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    Lines.stroke(1f)
    Draw.color(Pal.placing)
    Drawf.circles(x * Vars.tilesize + offset, y * Vars.tilesize + offset, (range * Vars.tilesize).toFloat())
  }

  override fun setStats() {
    super.setStats()
    stats.add(Stat.range, range.toFloat(), StatUnit.blocks)
    stats.add(SglStat.maxTarget, 10f)
    stats.add(SglStat.effect) { t: Table? ->
      t!!.defaults().left().padLeft(5f)
      t.row()
      t.table { a: Table? ->
        a!!.image(OtherContents.locking.uiIcon).size(25f)
        a.add(OtherContents.locking.localizedName).color(Pal.accent)
      }
      t.row()
      t.add(Core.bundle.get("infos.phaseRadarEff"))
    }
    setChainsStats(stats)
  }

  inner class PhasedRadarBuild : SglBuilding(), SpliceBuildComp {
    override var loadingInvalidPos = IntSet()
    override var chains = ChainsModule(this)
    override var splice: Int = 0
      set(value) {
        field = value
        spliceDirBit = 0
        for (i in 0..3) {
          if ((splice and (1 shl i * 2)) != 0) spliceDirBit = spliceDirBit or (1 shl i)
        }
      }
    var spliceDirBit: Int = 0
    var centerPos: Vec2 = Vec2()
    var locking: ObjectSet<Unit> = ObjectSet<Unit>()

    override fun create(block: Block?, team: Team?): Building {
      super.create(block, team)
      timer = Interval(timers)

      return this
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      updateRegionBit()
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

    override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building {
      super.init(tile, team, shouldAdd, rotation)
      chains.newContainer()
      return this
    }

    override fun containerCreated(old: ChainsContainer?) {
      chains.container.BUILD = this
    }

    override fun updateTile() {
      if (consumeValid()) {
        if (chains.container.BUILD !== this) return

        if (timer(timeId, scanTime)) {
          for (unit in Groups.unit) {
            var lenValid = false
            if (unit.team !== team && unit.isFlying && ((Mathf.len(unit.x - centerPos.x, unit.y - centerPos.y) < range * Vars.tilesize).also { lenValid = it }) && !locking.contains(unit) && locking.size < min(chains.container.all.size, 10)) {
              locking.add(unit)
            } else if (unit.isFlying && !lenValid) {
              if (locking.remove(unit)) {
                unit.unapply(OtherContents.locking)
              }
            }
          }
        }

        for (unit in locking) {
          if (!unit.isAdded) {
            locking.remove(unit)
            continue
          }

          unit.apply(OtherContents.locking, 0.05f * Mathf.log(1.01f, (chains.container.all.size + 1).toFloat()))
        }
      }
    }

    override fun chainsAdded(old: ChainsContainer) {
      chainsFlowed(old)
    }

    override fun chainsFlowed(old: ChainsContainer?) {
      val statDisplay: PhasedRadarBuild? = chains.container.BUILD
      if (statDisplay != this) {
        if (statDisplay!!.y >= y && statDisplay.x <= building.x) {
          chains.container.BUILD = this
          centerPos.set(chains.container.minX().toFloat(), chains.container.minY().toFloat()).scl(Vars.tilesize.toFloat()).add(chains.container.width() / 2f * Vars.tilesize, chains.container.height() / 2f * Vars.tilesize)
        }
      }
    }

    override fun drawStatus() {
      if (this.block.enableDrawStatus && consumers.size > 0 && chains.container.BUILD === this) {
        val multiplier = if (block.size > 1 || chains.container.all.size > 1) 1.0f else 0.64f
        val brcx = this.tile.drawx() + (this.block.size * 8).toFloat() / 2.0f - 8 * multiplier / 2
        val brcy = this.tile.drawy() - (this.block.size * 8).toFloat() / 2.0f + 8 * multiplier / 2
        Draw.z(71.0f)
        Draw.color(Pal.gray)
        Fill.square(brcx, brcy, 2.5f * multiplier, 45.0f)
        Draw.color(status()!!.color)
        Fill.square(brcx, brcy, 1.5f * multiplier, 45.0f)
        Draw.color()
      }
    }

    override fun drawSelect() {
      super.drawSelect()
      Lines.stroke(2.5f)
      Draw.color(Pal.placing)
      Draw.alpha(0.4f)
      val b: PhasedRadarBuild = chains.container.BUILD!!
      val drawX = b.centerPos.x
      val drawY = b.centerPos.y
      Fill.circle(drawX, drawY, (range * Vars.tilesize).toFloat())
      Draw.alpha(1f)
      Drawf.circles(drawX, drawY, (range * Vars.tilesize).toFloat())

      if (!consumeValid()) return
      Tmp.v1.set(range * Vars.tilesize - 2.5f, 0f).rotate(-Time.time * 1.5f)
      val dx = Tmp.v1.x
      val dy = Tmp.v1.y

      Lines.stroke(6f)
      Tmp.v2.set(1f, 0f).setAngle(Tmp.v1.angle() + 90)
      SglDraw.gradientLine(
        drawX + Tmp.v2.x * 1.75f, drawY + Tmp.v2.y * 3, drawX + Tmp.v2.x * 4.25f + dx, drawY + Tmp.v2.y * 4.25f + dy, Pal.placing, Tmp.c1.set(Pal.placing).a(0f), 1
      )
      Lines.stroke(2.5f, Pal.placing)
      Lines.line(drawX, drawY, drawX + dx, drawY + dy)
    }
  }
}