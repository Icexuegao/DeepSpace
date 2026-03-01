package singularity.world.components

import arc.func.Boolf
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.Seq
import ice.library.struct.log
import singularity.world.modules.NuclearEnergyModule
import universecore.components.blockcomp.BuildCompBase
import universecore.components.blockcomp.Takeable
import kotlin.math.max
import kotlin.math.min

/**这个接口表明此Building是具有核能的方块，需要在create当中初始化一个NuclearEnergyModule */
interface NuclearEnergyBuildComp : BuildCompBase, Takeable {
    // @Annotations.BindField("hasEnergy")
    fun hasEnergy(): Boolean

    // @Annotations.BindField("resident")
    val resident: Float
    // @Annotations.BindField("energyCapacity")
    fun energyCapacity(): Float

    // @Annotations.BindField("outputEnergy")
    fun outputEnergy(): Boolean

    // @Annotations.BindField("consumeEnergy")
    fun consumeEnergy(): Boolean

    // @Annotations.BindField("basicPotentialEnergy")
    fun basicPotentialEnergy(): Float

    // @Annotations.BindField("maxEnergyPressure")
    fun maxEnergyPressure(): Float

    /**用于获得该方块的核能模块 */
    //  @Annotations.BindField("energy")
    fun energy(): NuclearEnergyModule

    // initialize = "new arc.struct.Seq<>()")
    fun energyLinked(): Seq<NuclearEnergyBuildComp>

    /**将核能面板显示出来，或者显示别的什么东西 */
    fun displayEnergy(table: Table) {
        if (hasEnergy()) energy().display(table)
    }

    /**操作核能量的方法,通常直接引用EnergyModule的handle方法 */
    fun handleEnergy(value: Float) {
        energy().handle(value)
    }

    /**转运核能量到指定的块中,速度取决于势能差 */
    fun moveEnergy(next: NuclearEnergyBuildComp): Float {
        if (!next.hasEnergy() || !next.acceptEnergy(this)) return 0f
        val rate = getEnergyMoveRate(next)

        if (rate > 0.01f) {
            val energyDiff = getEnergyPressure(next)
            if (energyDiff > next.maxEnergyPressure()) next.onOverpressure(energyDiff)

            handleEnergy(-rate)
            next.handleEnergy(rate)

            energyMoved(next, rate)
        }
        return rate
    }

    fun getEnergyPressure(other: NuclearEnergyBuildComp): Float {
        if (!other.hasEnergy()) return 0f
        return Mathf.maxZero(this.outputPotential - other.inputPotential - other.basicPotentialEnergy())
    }

    // @MethodEntry(entryMethod = "update", insert = Annotations.InsertPosition.HEAD)
    fun updateEnergy() {
        if (hasEnergy()) energy().update()
    }

    /**获取该块对目标块的核能传输速度 */
    fun getEnergyMoveRate(next: NuclearEnergyBuildComp): Float {
        if (!next.hasEnergy() || !next.acceptEnergy(this) || this.outputPotential < next.inputPotential) return 0f
        val energyDiff = getEnergyPressure(next)
        var flowRate = (min(energyDiff * energyDiff / 60, energyDiff) - next.resident) * building.delta()
        flowRate = min(flowRate, next.energyCapacity() - next.getEnergy())
        flowRate = min(flowRate, this.getEnergy())

        return max(flowRate, 0f)
    }

    fun proximityNuclearBuilds(): Seq<NuclearEnergyBuildComp> {
        tmp.clear()

        for (building in building.proximity) {
            if (building is NuclearEnergyBuildComp && building.hasEnergy()) {
                tmp.add(building)
            }
        }

        return tmp
    }

    fun getEnergy(): Float {
        return energy().energy
    }

    val inputPotential: Float
        get() = energy().energy
    val outputPotential: Float
        get() = energy().energy


    /**返回该块是否接受核能输入 */
    fun acceptEnergy(source: NuclearEnergyBuildComp): Boolean {
        return building.interactable(source.building.team) && hasEnergy() && energy().energy < energyCapacity()
    }

    /**向连接的方块输出核能量，如果那个方块接受的话 */
    fun dumpEnergy(dumpTargets: Seq<NuclearEnergyBuildComp?> = proximityNuclearBuilds() as Seq<NuclearEnergyBuildComp?>) {
        val dump = getNext("energy", dumpTargets, Boolf { e: NuclearEnergyBuildComp? ->
            if (e == null || e === this) return@Boolf false
            e.acceptEnergy(this) && getEnergyMoveRate(e) > 0
        })
        if (dump != null) {
            moveEnergy(dump)
        }
    }

    fun energyMoved(next: NuclearEnergyBuildComp?, rate: Float) {}

    /**当能压过载以后触发的方法
     * @param energyPressure 此时的方块间核势能差值
     */
    fun onOverpressure(energyPressure: Float)

    companion object {
        val tmp = Seq<NuclearEnergyBuildComp>(false)
    }
}
