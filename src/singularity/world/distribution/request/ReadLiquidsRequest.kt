package singularity.world.distribution.request

import arc.func.Boolf2
import arc.struct.Seq
import mindustry.Vars
import mindustry.gen.Building
import mindustry.type.LiquidStack
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.components.distnet.DistElementBuildComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.GridChildType
import singularity.world.distribution.MatrixGrid.BuildingEntry
import singularity.world.distribution.buffers.LiquidsBuffer
import java.util.*
import kotlin.math.min

/**从网络中读取液体，此操作将液体从网络缓存读出并写入到目标缓存，网络缓存会优先提供已缓存液体，若不足则从网络子容器申请液体到网络缓存再分配 */
class ReadLiquidsRequest(sender: DistElementBuildComp, private val destination: LiquidsBuffer, private val reqLiquids: Seq<LiquidStack>) : DistRequestBase(sender) {
    private var source: LiquidsBuffer? = null

    public override fun priority(): Int {
        return 128
    }

    public override fun init(target: DistributeNetwork) {
        super.init(target)
        source = target.core!!.getBuffer(DistBufferType.liquidBuffer)
    }

    public override fun preHandleTask(): Boolean {
        if (tempLiquid == null || tempLiquid!!.size != Vars.content.liquids().size) {
            tempLiquid = FloatArray(Vars.content.liquids().size)
        }

        Arrays.fill(tempLiquid, 0f)
        for (stack in reqLiquids) {
            tempLiquid!![stack.liquid.id.toInt()] = stack.amount * destination.maxCapacity() - destination.get(stack.liquid) - source!!.get(stack.liquid)
        }

        liquidFor@ for (id in tempLiquid!!.indices) {
            if (tempLiquid!![id] <= 0) continue
            val liquid = Vars.content.liquid(id)
            for (grid in target!!.grids) {
                for (entry in grid!!.get<Building?>(
                    GridChildType.container,
                    Boolf2 { e: Building?, c: TargetConfigure? ->
                        e!!.block.hasLiquids
                                && e.liquids != null && e.liquids.get(liquid) > 0.001f && c!!.get(GridChildType.container, liquid)
                    }, temp
                )) {
                    if (tempLiquid!![id] < 0.001f || source!!.remainingCapacity() < 0.001f) continue@liquidFor
                    var move = min(tempLiquid!![id], entry.entity!!.liquids.get(liquid))
                    move = min(move, source!!.remainingCapacity())

                    move -= move % LiquidsBuffer.LiquidIntegerStack.packMulti
                    if (move > 0.001f) {
                        entry.entity!!.liquids.remove(liquid, move)
                        source!!.put(liquid, move)
                        source!!.dePutFlow(liquid, move)
                        tempLiquid!![id] -= move
                    }
                }
            }
        }
        return true
    }

    public override fun handleTask(): Boolean {
        var blockTest = false
        for (stack in reqLiquids) {
            val targetAmount = stack.amount * destination.maxCapacity()
            var move = min(targetAmount - destination.get(stack.liquid), source!!.get(stack.liquid))

            move -= move % LiquidsBuffer.LiquidIntegerStack.packMulti
            if (move <= 0) continue

            source!!.remove(stack.liquid, move)
            destination.put(stack.liquid, move)
            blockTest = true
        }
        return blockTest
    }

    override fun afterHandleTask(): Boolean {
        return true
    }

    companion object {
        private val temp = Seq<BuildingEntry<Building?>?>()
        private var tempLiquid: FloatArray? = null
    }
}