package singularity.world.blocks.distribute.netcomponents

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.struct.IntSet
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.world.Tile
import singularity.world.blocks.distribute.DistNetBlock
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistributeNetwork
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.components.blockcomp.SpliceBlockComp
import universecore.components.blockcomp.SpliceBuildComp
import universecore.world.DirEdges
import universecore.world.blocks.modules.ChainsModule
import java.util.*

open class ComponentInterface(name: String) : DistNetBlock(name), SpliceBlockComp {
  override var negativeSplice: Boolean = false
  override var interCorner: Boolean = false
  var interfaceLinker: TextureRegion? = null
  var linker: TextureRegion? = null
  override var maxChainsWidth: Int = 40
  override var maxChainsHeight: Int = 40

  init {
    this.isNetLinker = true
    buildType = Prov(::ComponentInterfaceBuild)
  }

  public override fun load() {
    super.load()
    this.interfaceLinker = Core.atlas.find(this.name + "_linker")
    this.linker = Core.atlas.find(this.name + "_comp_linker")
  }

  override fun chainable(other: ChainsBlockComp): Boolean {
    return other === this
  }

  fun interCorner(): Boolean {
    return this.interCorner
  }

  fun negativeSplice(): Boolean {
    return this.negativeSplice
  }

  fun maxWidth(): Int {
    return this.maxChainsWidth
  }

  fun maxHeight(): Int {
    return this.maxChainsHeight
  }

  override fun setStats() {
    super.setStats()
    this.setChainsStats(this.stats)
  }

  inner class ComponentInterfaceBuild : DistNetBuild(), SpliceBuildComp {
    override var loadingInvalidPos: IntSet = IntSet()
    override var splice: Int = 0
    override var chains = ChainsModule(this)
    var links: Seq<ComponentInterfaceBuild?> = Seq<ComponentInterfaceBuild?>()
    var connects: Seq<DistElementBuildComp?> = Seq<DistElementBuildComp?>()
    var interSplice: Byte = 0
    var connectSplice: ByteArray = ByteArray(4)
    var mark: Boolean = false

    override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building {
      super.init(tile, team, shouldAdd, rotation)
      this.chains.newContainer()
      return this
    }

    override fun updateTile() {
      super.updateTile()
      if (this.mark) {
        this.updateNetLinked()
        (DistributeNetwork()).flow(this)
        this.mark = false
      }
    }

    override fun updateNetLinked() {
      super.updateNetLinked()
      this.links.clear()
      this.connects.clear()
      Arrays.fill(this.connectSplice, 0.toByte())

      for (building in this.proximity) {
        if (building is ComponentInterfaceBuild) {
          val inter = building
          if (this.canChain(inter)) {
            this.links.add(inter)
            continue
          }
        }

        if (building is DistNetBuild) {
          val device = building
          if (this.linkable(device) && device.linkable(this) && this.connectable(device)) {
            this.connects.add(device)
            val dir = this.relativeTo(device).toInt()
            val arr = DirEdges.get(this@ComponentInterface.size, dir)

            for (i in arr.indices) {
              val t = this.tile.nearby(arr[i])
              if (t != null && t.build === device) {
                val var10000 = this.connectSplice
                var10000[dir] = (var10000[dir].toInt() or (1 shl i).toByte().toInt()).toByte()
              }
            }
          }
        }
      }

      this.netLinked().addAll(this.links).addAll(this.connects)
    }

    public override fun onProximityAdded() {
      super.onProximityAdded()
      this.mark = true
      this.onChainsAdded()
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      this.updateNetLinked()
      this.mark = true
      this.updateRegionBit()
    }

    override fun networkRemoved(remove: DistElementBuildComp) {
      super.networkRemoved(remove)
      this.connects.remove(remove)
      if (remove is ComponentInterfaceBuild) {
        val inter = remove
        this.links.remove(inter)
      }

      this.mark = true
    }

    override fun updateRegionBit() {
      super.updateRegionBit()
      this.interSplice = 0

      for (i in 0..3) {
        if ((this.splice() and (1 shl i * 2)) != 0) {
          this.interSplice = (this.interSplice.toInt() or (1 shl i)).toByte()
        }
      }
    }

    fun connectable(other: DistNetBuild): Boolean {
      val dir = other.relativeTo(this).toInt()
      val t = other.tile
      val var4 = DirEdges.get(other.block.size, dir)
      val var5 = var4.size
      var var6 = 0

      while (true) {
        if (var6 >= var5) {
          return true
        }

        val point2 = var4[var6]
        val ot = t.nearby(point2)
        if (ot == null) {
          break
        }

        val var10 = ot.build
        if (var10 !is ComponentInterfaceBuild) {
          break
        }

        val inter = var10
        if (inter !== this && inter.distributor.network != this.distributor.network) {
          return false
        }

        ++var6
      }

      return false
    }

    override fun canChain(other: ChainsBuildComp): Boolean {
      return super.canChain(other) && (other.tileX() == this.tileX() || other.tileY() == this.tileY())
    }

    fun splice(): Int {
      return this.splice
    }

    fun splice(arr: Int) {
      this.splice = arr
    }

    fun loadingInvalidPos(): IntSet {
      return this.loadingInvalidPos
    }

    fun chains(): ChainsModule? {
      return this.chains
    }

    public override fun onProximityRemoved() {
      super.onProximityRemoved()
      this.onChainsRemoved()
    }

    override fun write(write: Writes) {
      super.write(write)
      this.writeChains(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.readChains(read)
    }
  }
}