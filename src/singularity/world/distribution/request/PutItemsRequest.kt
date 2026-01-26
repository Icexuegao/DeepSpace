package singularity.world.distribution.request

import arc.struct.Seq
import mindustry.Vars
import mindustry.type.ItemStack
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.buffers.ItemsBuffer
import universecore.util.Empties
import java.util.*
import kotlin.math.min

/**向网络中写入物品，这一操作将物品写入网络的缓存中，处理结束由网络将缓存分配给网络中的子容器 */
class PutItemsRequest(sender: DistElementBuildComp, protected val source: ItemsBuffer, protected val reqItems: Seq<ItemStack>) : DistRequestBase(sender) {
    protected var destination: ItemsBuffer? = null
    protected var all: Boolean = false
    protected var lastHandles: IntArray = IntArray(Vars.content.items().size)

    constructor(sender: DistElementBuildComp, source: ItemsBuffer) : this(sender, source, Empties.nilSeq<ItemStack?>()) {
        allItemPut()
    }

    fun allItemPut() {
        all = true
    }

    public override fun priority(): Int {
        return 64
    }

    public override fun init(target: DistributeNetwork) {
        super.init(target)
        destination = target.core?.getBuffer(DistBufferType.itemBuffer)?:return
    }

    override fun preHandleTask(): Boolean {
        return true
    }

    public override fun handleTask(): Boolean {
        Arrays.fill(lastHandles, 0)
        if (all) {
            for (packet in source) {
                val move = min(packet.amount(), destination!!.remainingCapacity())
                if (move <= 0) continue

                lastHandles[packet.id()] = move

                packet.remove(move)
                destination!!.put(packet.get(), move)
            }
            return true
        } else {
            var blockTest = false
            for (stack in reqItems) {
                val move = min(source.get(stack.item), destination!!.remainingCapacity())

                if (move <= 0) continue

                lastHandles[stack.item.id.toInt()] = move

                source.remove(stack.item, move)
                destination!!.put(stack.item, move)
                blockTest = true
            }
            return blockTest
        }
    }

    override fun afterHandleTask(): Boolean {
        for (id in lastHandles.indices) {
            val item = Vars.content.item(id)
            var rem = min(lastHandles[id], destination!!.get(item))
            rem = min(rem, source.remainingCapacity())
            if (rem <= 0) continue

            destination!!.remove(item, rem)
            destination!!.deReadFlow(item, rem)
            source.put(item, rem)
            source.dePutFlow(item, rem)
        }

        return true
    }
}