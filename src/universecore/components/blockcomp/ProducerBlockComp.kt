package universecore.components.blockcomp

import arc.struct.Seq
import singularity.world.products.Producers
import universecore.world.producers.BaseProducers

/**生产者方块的组件，令方块具有记录输出资源配方的功能
 *
 * @author EBwilson
 * @since 1.0
 */
interface ProducerBlockComp : ConsumerBlockComp {
  //  @Annotations.BindField(value = "producers", initialize = "new arc.struct.Seq<>()")
  /**生产清单的列表 */
  var producers: Seq<BaseProducers>

  /**创建一张新的生产清单加入容器，并返回它 */
  fun newProduce(): Producers {
    val produce = Producers()
    producers.add(produce)
    return produce
  }

  /**初始化匹配消耗生产列表，在init()最后调用 */
  // @MethodEntry(entryMethod = "init")
  fun initProduct() {
    var b = producers.size
    val a = consumers.size/*控制produce添加/移除配方以使配方同步*/
    while (a > b) {
      val p = BaseProducers()
      producers.add(p)
      b++
    }
    while (a < b) {
      b--
      producers.remove(b)
    }

    for (index in 0..<producers.size) {
      producers.get(index)!!.cons = consumers.get(index)
    }
  }
}