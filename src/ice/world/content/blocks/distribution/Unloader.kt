package ice.world.content.blocks.distribution

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Eachable
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import ice.library.scene.ui.ItemSelection
import ice.library.struct.texture.TextureRegionDelegate
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.world.Block
import mindustry.world.blocks.storage.CoreBlock
import mindustry.world.blocks.storage.StorageBlock.StorageBuild
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.world.blocks.SglBlock
import kotlin.math.min

class Unloader(name: String) : SglBlock(name) {
  companion object {
    /** Cached result of content.items()  */
    lateinit var allItems: Array<Item>
  }

  var centerRegion: TextureRegion by TextureRegionDelegate(this.name + "-center", "unloader-center")

  var speed: Float = 1f
  var allowCoreUnload: Boolean = true

  init {
    update = true
    solid = true
    health = 70
    hasItems = true
    configurable = true
    saveConfig = true
    itemCapacity = 0
    noUpdateDisabled = true
    clearOnDoubleTap = true
    unloadable = false

    config(Item::class.java) { tile: UnloaderBuild, item: Item -> tile.sortItem = item }
    configClear { tile: UnloaderBuild -> tile.sortItem = null }
    buildType= Prov(::UnloaderBuild)
  }

  override fun init() {
    super.init()
    allItems = Vars.content.items().toArray(Item::class.java)
  }

  override fun setStats() {
    super.setStats()
    stats.add(Stat.speed, 60f / speed, StatUnit.itemsSecond)
  }

  override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan?>?) {
    drawPlanConfigCenter(plan, plan.config, "unloader-center")
  }

  override fun setBars() {
    super.setBars()
    removeBar("items")
  }

  class ContainerStat : Poolable {
    var building: Building? = null
    var loadFactor: Float = 0f
    var canLoad: Boolean = false
    var canUnload: Boolean = false
    /** Cached !(building instanceof StorageBuild)  */
    var notStorage: Boolean = false
    var lastUsed: Int = 0

    override fun reset() {
      building = null
    }
  }

  open inner class UnloaderBuild : SglBuilding() {
    var unloadTimer: Float = 0f
    var rotations: Int = 0
    var sortItem: Item? = null
    var dumpingFrom: ContainerStat? = null
    var dumpingTo: ContainerStat? = null
    val possibleBlocks: Seq<ContainerStat?> = Seq<ContainerStat?>(ContainerStat::class.java)

    protected val comparator: java.util.Comparator<ContainerStat?> = Comparator { x: ContainerStat?, y: ContainerStat? ->
      //sort so it gives priority for blocks that can only either receive or give (not both), and then by load, and then by last use
      //highest = unload from, lowest = unload to
      val unloadCore = java.lang.Boolean.compare(!x!!.notStorage, !y!!.notStorage) //priority to core and core containers always
      if (unloadCore != 0) return@Comparator unloadCore
      val unloadPriority =
        java.lang.Boolean.compare(x.canUnload && !x.canLoad, y.canUnload && !y.canLoad) //priority to receive if it cannot give
      if (unloadPriority != 0) return@Comparator unloadPriority
      val loadPriority =
        java.lang.Boolean.compare(x.canUnload || !x.canLoad, y.canUnload || !y.canLoad) //priority to give if it cannot receive
      if (loadPriority != 0) return@Comparator loadPriority
      val loadFactor = x.loadFactor.compareTo(y.loadFactor)
      if (loadFactor != 0) return@Comparator loadFactor
      y.lastUsed.compareTo(x.lastUsed) //inverted
    }

    private fun isPossibleItem(item: Item?): Boolean {
      var hasProvider = false
      var hasReceiver = false
      var isDistinct = false

      val pbi = possibleBlocks.items
      var i = 0
      val l = possibleBlocks.size
      while (i < l) {
        val pb: ContainerStat = pbi[i]!!
        val other = pb.building

        //set the stats of buildings in possibleBlocks while we are at it
        pb.canLoad = pb.notStorage && other!!.acceptItem(this, item)
        pb.canUnload = other!!.canUnload() && other.items != null && other.items.has(item)

        //thats also handling framerate issues and slow conveyor belts, to avoid skipping items if nulloader
        isDistinct = isDistinct or ((hasProvider && pb.canLoad) || (hasReceiver && pb.canUnload))
        hasProvider = hasProvider or pb.canUnload
        hasReceiver = hasReceiver or pb.canLoad
        i++
      }
      return isDistinct
    }

    override fun onProximityUpdate() {
      //filter all blocks in the proximity that will never be able to trade items

      super.onProximityUpdate()
      Pools.freeAll(possibleBlocks, true)
      possibleBlocks.clear()

      for (i in 0..<proximity.size) {
        val other = proximity.get(i)
        if (!other.interactable(team)) continue  //avoid blocks of the wrong team

        //partial check
        val canLoad = !(other is CoreBlock.CoreBuild || other is StorageBuild)
        val canUnload = other.canUnload() && (allowCoreUnload || canLoad) && other.items != null

        if (canLoad || canUnload) { //avoid blocks that can neither give nor receive items
          val pb = Pools.obtain(ContainerStat::class.java) { ContainerStat() }
          pb.building = other
          pb.notStorage = canLoad
          //TODO store the partial canLoad/canUnload?
          possibleBlocks.add(pb)
        }
      }
    }

    override fun updateTile() {
      super.updateTile()
      if (((consumer.consDelta().let { unloadTimer += it; unloadTimer }) < speed) || (possibleBlocks.size < 2)) return
      var item: Item? = null
      var any = false

      if (sortItem != null) {
        if (isPossibleItem(sortItem)) item = sortItem
      } else {
        //selects the next item for nulloaders
        //inspired of nextIndex() but for all "proximity" (possibleBlocks) at once, and also way more powerful
        var i = 0
        val l: Int = allItems.size
        while (i < l) {
          val id = (rotations + i + 1) % l
          val possibleItem: Item = allItems[id]

          if (isPossibleItem(possibleItem)) {
            item = possibleItem
            break
          }
          i++
        }
      }

      if (item != null) {
        rotations = item.id.toInt() //next rotation for nulloaders //TODO maybe if(sortItem == null)
        val pbi = possibleBlocks.items
        val pbs = possibleBlocks.size

        for (i in 0..<pbs) {
          val pb: ContainerStat = pbi[i]!!
          val other = pb.building
          val maxAccepted = other!!.getMaximumAccepted(item)
          pb.loadFactor = if (maxAccepted == 0 || other.items == null) 0f else other.items.get(item) / maxAccepted.toFloat()
          pb.lastUsed = (pb.lastUsed + 1) % Int.MAX_VALUE //increment the priority if not used
        }

        possibleBlocks.sort(comparator)

        dumpingTo = null
        dumpingFrom = null

        //choose the building to accept the item
        for (i in 0..<pbs) {
          if (pbi[i]!!.canLoad) {
            dumpingTo = pbi[i]
            break
          }
        }

        //choose the building to take the item from
        for (i in pbs - 1 downTo 0) {
          if (pbi[i]!!.canUnload) {
            dumpingFrom = pbi[i]
            break
          }
        }

        //trade the items
        if (dumpingFrom != null && dumpingTo != null && (dumpingFrom!!.loadFactor != dumpingTo!!.loadFactor || !dumpingFrom!!.canLoad)) {
          dumpingTo!!.building!!.handleItem(this, item)
          dumpingFrom!!.building!!.removeStack(item, 1)
          dumpingTo!!.lastUsed = 0
          dumpingFrom!!.lastUsed = 0
          any = true
        }
      }

      if (any) {
        unloadTimer %= speed
      } else {
        unloadTimer = min(unloadTimer, speed)
      }
    }

    override fun draw() {
      super.draw()

      Draw.color(if (sortItem == null) Color.clear else sortItem!!.color)
      Draw.rect(centerRegion, x, y)
      Draw.color()
    }

    override fun drawSelect() {
      super.drawSelect()
      drawItemSelection(sortItem)
    }

    override fun buildConfiguration(table: Table) {
      ItemSelection.buildTable(
        this@Unloader, table, Vars.content.items(), ::sortItem, ::configure, true
      )
    }

    override fun config(): Item? {
      return sortItem
    }
    override fun write(write: Writes) {
      super.write(write)
      write.s((if (sortItem == null) -1 else sortItem!!.id).toInt())
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      val id = read.s().toInt()
      sortItem = if (id == -1) null else Vars.content.item(id)
    }
  }

}