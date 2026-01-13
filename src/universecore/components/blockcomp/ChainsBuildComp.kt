package universecore.components.blockcomp

import arc.struct.IntSet
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Posc
import universecore.world.blocks.chains.ChainsContainer
import universecore.world.blocks.modules.ChainsModule

/**链式方块的接口组件，这个组件提供方块连续放置时执行一些行为的能力，为一块连续结构上发生的变更添加了触发器
 *
 * @since 1.5
 * @author EBwilson
 */
interface ChainsBuildComp : BuildCompBase, Posc, Iterable<ChainsBuildComp> {
    companion object {
        val tempSeq: Seq<ChainsBuildComp> = Seq<ChainsBuildComp>()
    }
    /**这是个愚蠢的做法...但好像也没有什么很好的解决方式，简言之就是用于结构在存档重新装载时保持原有的结构而不发生改变 */
    // @Annotations.BindField(value = "loadingInvalidPos", initialize = "new arc.struct.IntSet()")
   var loadingInvalidPos: IntSet

    /**链式结构的容器，是连续结构保存和行为触发的核心 */ // @Annotations.BindField("chains")
    var chains:ChainsModule

    val chainsBlock: ChainsBlockComp
        get() = getBlock(ChainsBlockComp::class.java)

    // @Annotations.MethodEntry(entryMethod = "onProximityAdded")
    fun onChainsAdded() {
        for (other in chainBuilds()) {
            if (loadingInvalidPos.contains(other.tile!!.pos())) continue
            if (canChain(other) && other.canChain(this)) other.chains.container.add(chains.container)
        }
        if (!loadingInvalidPos.isEmpty) loadingInvalidPos.clear()
    }

    /**是否可以与目标建筑构成连续结构，只有与目标间互相均能连接时才能构成连续结构
     *
     * @param other 目标建筑
     */
    fun canChain(other: ChainsBuildComp): Boolean {
        if (!this.chainsBlock.chainable(other.chainsBlock)) return false

        return chains.container.inlerp(this, other)
    }

    //@Annotations.MethodEntry(entryMethod = "onProximityRemoved")
    fun onChainsRemoved() {
        chains.container.remove(this)
    }

    /**获取此建筑可以连接到的其他链式方块，注意，这返回的是一个共用容器，在需要时请保存其副本 */
    fun chainBuilds(): Seq<ChainsBuildComp> {
        tempSeq.clear()
        for (other in building.proximity) {
            if (other is ChainsBuildComp && canChain(other) && other.canChain(this)) {
                tempSeq.add(other as ChainsBuildComp)
            }
        }
        return tempSeq
    }

    /**迭代实现，这个组件可以直接通过for-each遍历所有的结构成员 */
    override fun iterator(): MutableIterator<ChainsBuildComp> {
        return chains.container.all.iterator()
    }

    /**在这个方块创建了新的[链式结构容器][ChainsContainer]时调用
     *
     * @param old 被替代的原容器
     */
    fun containerCreated(old: ChainsContainer?) {}

    /**在这个方块被添加到一个链式结构中时调用
     *
     * @param old 被添加到连续结构时，方块的当前容器会被目标容器取代，原有的这个被替代的原容器会从这个参数传入
     */
    fun chainsAdded(old: ChainsContainer) {}

    /**在这个方块从[链式结构容器][ChainsContainer]执行移除时调用
     *
     * @param children 此方块链接到的周围其他方块
     */
    fun chainsRemoved(children: Seq<ChainsBuildComp>) {}

    /**当一个[链式结构容器][ChainsContainer]执行遍历搜索时，其搜索并将该方块添加到容器时调用
     *
     * @param old 被添加到连续结构时，方块的当前容器会被目标容器取代，原有的这个被替代的原容器会从这个参数传入
     */
    fun chainsFlowed(old: ChainsContainer?) {}

    /**在这个连续结构任何部分发生变化**之后**调用 */
    fun onChainsUpdated() {}

    // @Annotations.MethodEntry(entryMethod = "write", paramTypes = "arc.util.io.Writes -> write")
    fun writeChains(write: Writes) {
        tempSeq.clear()
        for (building in building.proximity) {
            if (building is ChainsBuildComp && building.chains.container !== chains.container) {
                tempSeq.add(building)
            }
        }

        write.i(tempSeq.size)
        for (comp in tempSeq) {
            write.i(comp.tile!!.pos())
        }
    }

    // @Annotations.MethodEntry(entryMethod = "read", paramTypes = {"arc.util.io.Reads -> read", "byte"})
    fun readChains(read: Reads) {
        val size = read.i()
        for (i in 0..<size) {
            loadingInvalidPos.add(read.i())
        }
    }


}