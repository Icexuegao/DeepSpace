package singularity.world.blocks.medium

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.geom.Geometry
import arc.struct.IntSet
import arc.struct.ObjectMap
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.Autotiler
import mindustry.world.blocks.Autotiler.SliceMode
import singularity.world.components.MediumBuildComp
import singularity.world.components.MediumComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.components.blockcomp.Takeable
import universecore.world.blocks.modules.ChainsModule
import kotlin.math.min

class MediumConduit(name: String?) : Block(name), MediumComp, Autotiler {
  var regions: Array<TextureRegion?> = arrayOfNulls(5)
  var tops: Array<TextureRegion?> = arrayOfNulls(5)
  override var mediumCapacity: Float = 16f
  override var lossRate: Float = 0.01f
  override var mediumMoveRate: Float = 0.325f
  override var outputMedium: Boolean = true

  init {
    rotate = true
    solid = false
    floating = true
    conveyorPlacement = true
    noUpdateDisabled = true
    unloadable = false
    buildType = Prov(::MediumConduitBuild)
  }

  override fun load() {
    super.load()
    for (i in 0..4) {
      regions[i] = Core.atlas.find(name + "_" + i)
      tops[i] = Core.atlas.find(name + "_top_" + i)
    }
  }

  override fun drawPlanConfigTop(req: BuildPlan, list: Eachable<BuildPlan?>?) {
    val bits = getTiling(req, list)

    if (bits == null) return

    Draw.scl(bits[1].toFloat(), bits[2].toFloat())
    Draw.rect(regions[bits[0]], req.drawx(), req.drawy(), (req.rotation * 90).toFloat())
    Draw.color()
    Draw.rect(tops[bits[0]], req.drawx(), req.drawy(), (req.rotation * 90).toFloat())
    Draw.scl()
  }

  override fun blends(tile: Tile?, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block?): Boolean {
    if (otherblock !is MediumBuildComp) return false
    val blockComp = otherblock as MediumComp
    return (blockComp.outputMedium || (lookingAt(tile, rotation, otherx, othery, otherblock))) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock)
  }

  public override fun icons(): Array<TextureRegion?> {
    return arrayOf(regions[0], tops[0])
  }

  inner class MediumConduitBuild : Building(), MediumBuildComp, ChainsBuildComp {
    override var mediumContains: Float = 0f
    var blendData: IntArray = IntArray(0)
    override var loadingInvalidPos = IntSet()
    override var chains = ChainsModule(this)

    override fun onProximityAdded() {
      super.onProximityAdded()
      onChainsAdded()
    }

    override fun write(write: Writes) {
      super.write(write)
      writeChains(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      readChains(read)
    }

    override fun onProximityRemoved() {
      super.onProximityRemoved()
      onChainsRemoved()
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      val temp = buildBlending(tile, rotation, null, true)
      blendData = temp.copyOf(temp.size)
    }

    override fun updateTile() {
      val next = this.tile.nearby(this.rotation)

      if (mediumContains > 0.001f) {
        if (next.build is MediumBuildComp) {
          val move = min(mediumContains, mediumMoveRate) * edelta()
          removeMedium(move)
          (next.build as MediumBuildComp).handleMedium(this, move)
        }
      }
    }

    override fun draw() {
      val rotation = rotdeg()
      val r = this.rotation

      Draw.z(Layer.blockUnder)
      for (i in 0..3) {
        if ((blendData[4] and (1 shl i)) != 0) {
          val dir = r - i
          val rot = if (i == 0) rotation else ((dir) * 90).toFloat()
          drawConduit(x + Geometry.d4x(dir) * Vars.tilesize * 0.75f, y + Geometry.d4y(dir) * Vars.tilesize * 0.75f, 0, rot, if (i != 0) SliceMode.bottom else SliceMode.top)
        }
      }

      Draw.z(Layer.block)

      Draw.scl(blendData[1].toFloat(), blendData[2].toFloat())
      drawConduit(x, y, blendData[0], rotation, SliceMode.none)
      Draw.reset()
    }

    protected fun drawConduit(x: Float, y: Float, bits: Int, rotation: Float, slice: SliceMode?) {
      Draw.rect(sliced(regions[bits], slice), x, y, rotation)

      Draw.rect(sliced(tops[bits], slice), x, y, rotation)
    }

    override var heaps = ObjectMap<String, Takeable.Heaps<*>>()
  }
}