package universecore.components.blockcomp

import universecore.world.DirEdges

/**拼接方块的建筑组件，使方块会自动同步周围的方块状态并记录，通常用于绘制方块连续的连接材质
 *
 * **这是个不稳定的API，后续可能会调整为更加通用且高效的形式，这会造成API变更，慎用**
 *
 * @since 1.5
 * @author EBwilson
 */
interface SpliceBuildComp : ChainsBuildComp {
    //@Annotations.BindField("splice")
    /*default int splice(){
    return 0;
  }*/
    // @Annotations.BindField("splice")
    var splice: Int
    val getSplice: Int
        get() {
            var result = 0

            t@ for (i in 0..7) {
                var other: SpliceBuildComp? = null
                for (p in DirEdges.get8(block!!.size, i)) {
                    if (other == null) {
                        val nearby = building.nearby(p.x, p.y)
                        val bool = nearby is SpliceBuildComp
                        if (bool && (nearby as SpliceBuildComp).chains.container === chains.container) {
                            other = (nearby as SpliceBuildComp)
                        } else {
                            continue@t
                        }
                    } else if (other !== building.nearby(p.x, p.y)) {
                        continue@t
                    }
                }
                result = result or (1 shl i)
            }

            return result
        }

    // @Annotations.MethodEntry(entryMethod = "onProximityUpdate")
    fun updateRegionBit() {
        splice = getSplice
    }
}