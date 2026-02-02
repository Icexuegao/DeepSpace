package singularity.world.distribution.request

import arc.struct.Seq
import mindustry.Vars
import mindustry.type.LiquidStack
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.buffers.LiquidsBuffer
import java.util.*
import kotlin.math.min

/**向网络中写入液体，这一操作液体写入网络的缓存中，处理结束由网络将缓存分配给网络中的子容器 */
class PutLiquidsRequest @JvmOverloads constructor(sender: DistElementBuildComp, protected val source: LiquidsBuffer, protected val reqLiquids: Seq<LiquidStack>? = null) : DistRequestBase(sender) {
    protected var destination: LiquidsBuffer? = null
    protected val all: Boolean
    protected var lastHandles: FloatArray = FloatArray(Vars.content.liquids().size)

    init {
        all = reqLiquids == null
    }

    public override fun priority(): Int {
        return 64
    }

    public override fun init(target: DistributeNetwork) {
        super.init(target)
        destination = target.core!!.getBuffer(DistBufferType.liquidBuffer)
    }

    override fun preHandleTask(): Boolean {
        return true
    }

    override fun handleTask(): Boolean {
        Arrays.fill(lastHandles, 0f)
        if (all) {
            for (packet in source) {
                var move = min(packet.amount(), destination!!.remainingCapacity())
                move -= move % LiquidsBuffer.LiquidIntegerStack.packMulti

                if (move < 0.001f) continue

                lastHandles[packet.id()] = move

                packet.remove(move)
                destination!!.put(packet.get(), move)
            }
            return true
        } else {
            var test = false
            for (stack in reqLiquids!!) {
                var move = min(source.maxCapacity() * stack.amount, min(source.get(stack.liquid), destination!!.remainingCapacity()))
                move -= move % LiquidsBuffer.LiquidIntegerStack.packMulti

                if (move < 0.001f) continue

                lastHandles[stack.liquid.id.toInt()] = move

                source.remove(stack.liquid, move)
                destination!!.put(stack.liquid, move)
                test = true
            }
            return test
        }
    }

    override fun afterHandleTask(): Boolean {
        for (id in lastHandles.indices) {
            val liquid = Vars.content.liquid(id)
            var rem = min(lastHandles[id], destination!!.get(liquid))
            rem = min(rem, source.remainingCapacity())

            rem -= rem % LiquidsBuffer.LiquidIntegerStack.packMulti
            if (rem <= 0) continue

            destination!!.remove(liquid, rem)
            destination!!.deReadFlow(liquid, rem)
            source.put(liquid, rem)
            source.dePutFlow(liquid, rem)
        }

        return true
    }
}