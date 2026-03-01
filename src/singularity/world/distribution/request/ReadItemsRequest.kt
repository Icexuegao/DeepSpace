package singularity.world.distribution.request

import arc.func.Boolf2
import arc.struct.Seq
import mindustry.Vars
import mindustry.gen.Building
import mindustry.type.ItemStack
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.GridChildType
import singularity.world.distribution.MatrixGrid.BuildingEntry
import singularity.world.distribution.buffers.ItemsBuffer
import java.util.*
import kotlin.math.min

/**从网络中读取物品，此操作将物品从网络缓存读出并写入到目标缓存，网络缓存会优先提供已缓存物品，若不足则从网络子容器申请物品到网络缓存再分配 */
class ReadItemsRequest(sender: DistElementBuildComp, private val destination: ItemsBuffer, private val reqItems: Seq<ItemStack>) : DistRequestBase(sender) {
    private var source: ItemsBuffer? = null
    private val total: Float

    init {
        var i = 0
        for (stack in reqItems) {
            i += stack.amount
        }

        total = i.toFloat()
    }

    public override fun priority(): Int {
        return 128
    }

    public override fun init(target: DistributeNetwork) {
        super.init(target)
        source = target.core?.getBuffer(DistBufferType.itemBuffer)?:return
    }

    public override fun preHandleTask(): Boolean {
        if (tempItems == null || tempItems!!.size != Vars.content.items().size) {
            tempItems = IntArray(Vars.content.items().size)
        }

        Arrays.fill(tempItems, 0)

        for (stack in reqItems) {
            tempItems!![stack.item.id.toInt()] = min(stack.amount / total * destination.maxCapacity() - destination.get(stack.item), (stack.amount - source!!.get(stack.item)).toFloat()).toInt()
        }

        itemFor@ for (id in tempItems!!.indices) {
            if (tempItems!![id] <= 0) continue
            val item = Vars.content.item(id)
            for (grid in target!!.grids) {
                for (entry in grid!!.get<Building?>(
                    GridChildType.container,
                    Boolf2 { e: Building?, c: TargetConfigure? -> e!!.block.hasItems && e.items != null && e.items.get(item) > 0 && c!!.get(GridChildType.container, item) },
                    temp
                )) {
                    if (tempItems!![id] <= 0) continue@itemFor
                    if (source!!.remainingCapacity() <= 0) {
                        break@itemFor
                    }
                    var move = min(entry.entity!!.items.get(item), tempItems!![id])
                    move = min(move, source!!.remainingCapacity())

                    if (move > 0) {
                        move = entry.entity.removeStack(item, move)
                        source!!.put(item, move)
                        source!!.dePutFlow(item, move)
                        tempItems!![id] -= move
                    }
                }
            }
        }

        return true
    }

    override fun handleTask(): Boolean {
        var blockTest = false
        for (stack in reqItems) {
            var move = min(stack.amount, source!!.get(stack.item))
            move = min(move, destination.remainingCapacity())
            move = min(stack.amount / total * destination.maxCapacity() - destination.get(stack.item), move.toFloat()).toInt()

            if (move <= 0) continue

            source!!.remove(stack.item, move)
            destination.put(stack.item, move)
            blockTest = true
        }
        return blockTest
    }

    override fun afterHandleTask(): Boolean {
        return true
    }

    companion object {
        private val temp = Seq<BuildingEntry<Building?>?>()
        private var tempItems: IntArray? = null
    }
}