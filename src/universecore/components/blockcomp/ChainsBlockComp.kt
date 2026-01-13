package universecore.components.blockcomp

import mindustry.world.meta.Stats
import universecore.world.meta.UncStat

/**链式方块的方块信息组件，用于block，为[链式建筑][ChainsBuildComp]提供必要的描述属性
 *
 * @since 1.5
 * @author EBwilson
 */
interface ChainsBlockComp {
    /**一个连续结构的最大x轴跨度 */ // @Annotations.BindField("maxChainsWidth")
    val maxChainsWidth: Int

    /**一个连续结构的最大y轴跨度 */ // @Annotations.BindField("")
    var maxChainsHeight: Int

    /**这个方块是否能与目标方块组成连续结构，需要两个块之间互相都能够链接才能构成连续结构
     *
     * @param other 目标方块
     */
    fun chainable(other: ChainsBlockComp): Boolean {
        return javaClass.isAssignableFrom(other.javaClass)
    }

    /**设置方块的统计数据，通常你不需要操作这个行为 */ //  @Annotations.MethodEntry(entryMethod = "setStats", context = "stats -> stats")
    fun setChainsStats(stats: Stats) {
        stats.add(UncStat.maxStructureSize, "@x@", maxChainsWidth,maxChainsHeight)
    }
}