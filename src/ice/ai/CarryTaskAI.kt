package ice.ai

import ice.library.content.blocks.distribution.droneNetwork.DroneDeliveryTerminal.DroneDeliveryTerminalBuild
import mindustry.entities.units.AIController
import mindustry.gen.Building
import mindustry.gen.Call
import mindustry.type.ItemStack

/**
 * 无人机运输任务AI控制器
 * 负责控制无人机从源建筑获取物品并运输到目标建筑
 */
class CarryTaskAI : AIController() {
    companion object {
        // 转移物品的有效距离
        private const val TRANSFER_DISTANCE = 20f

        // 空闲时与源建筑保持的距离
        private const val IDLE_DISTANCE = 40f
    }

    /**
     * 运输任务数据类
     * @param source 源建筑（物品提供方）
     * @param target 目标建筑（物品接收方）
     * @param items 需要运输的物品
     */
    data class ItemTask(
        val source: DroneDeliveryTerminalBuild,
        val target: Building,
        val items: ItemStack
    )

    // 当前任务的源建筑
    var source: DroneDeliveryTerminalBuild? = null

    // 当前执行的任务
    private var task: ItemTask? = null

    /**
     * 添加新的运输任务
     * @param task 要添加的任务
     */
    fun addTask(task: ItemTask) {
        source = task.source
        this.task = task
    }

    /**
     * 清除当前任务
     */
    fun clearTask() {
        task = null
    }

    /**
     * 获取当前任务
     */
    fun getTask() = task

    /**
     * 检查是否有正在执行的任务
     * @return 如果有任务返回true，否则返回false
     */
    fun hasTask(): Boolean = task != null

    /**
     * 更新移动逻辑
     * 根据当前状态控制无人机移动
     */
    override fun updateMovement() {
        // 如果有任务，执行任务逻辑
        task?.let { currentTask ->
            executeTask(currentTask)
            return
        }
        // 没有任务时，返回源建筑附近
        source?.let { sourceBuilding ->
            returnToSource(sourceBuilding)
        }
    }

    /**
     * 执行运输任务
     * @param task 当前要执行的任务
     */
    private fun executeTask(task: ItemTask) {
        val unitStack = unit.stack
        // 如果无人机已有正确数量和类型的物品，则前往目标建筑
        if (hasCorrectItems(unitStack, task.items)) {
            deliverToTarget(task)
        } else {
            // 否则前往源建筑获取物品
            collectFromSource(task)
        }
    }

    /**
     * 检查无人机是否携带了正确数量和类型的物品
     * @param unitStack 无人机当前携带的物品
     * @param requiredItems 任务需要的物品
     * @return 如果物品匹配返回true，否则返回false
     */
    private fun hasCorrectItems(unitStack: ItemStack, requiredItems: ItemStack): Boolean {
        return unitStack.item == requiredItems.item && unitStack.amount == requiredItems.amount
    }

    /**
     * 将物品运送到目标建筑
     * @param task 当前任务
     */
    private fun deliverToTarget(task: ItemTask) {
        moveTo(task.target, 0f)
        unit.lookAt(task.target)
        // 如果足够接近目标建筑，转移物品
        if (task.target.within(unit, TRANSFER_DISTANCE)) {
            Call.transferItemTo(
                unit,
                task.items.item,
                task.items.amount,
                task.target.x,
                task.target.y,
                task.target
            )
            clearTask()
        }
    }

    /**
     * 从源建筑收集物品
     * @param task 当前任务
     */
    private fun collectFromSource(task: ItemTask) {
        source?.let { sourceBuilding ->
            // 检查源建筑是否有足够的物品
            if (sourceBuilding.items.has(task.items.item, task.items.amount)) {
                moveTo(task.source, 0f)
                unit.lookAt(task.source)
                // 如果足够接近源建筑，取走物品
                if (task.source.within(unit, TRANSFER_DISTANCE)) {
                    Call.takeItems(task.source, task.items.item, task.items.amount, unit)
                }
            } else {
                // 源建筑物品不足，取消任务
                clearTask()
            }
        }
    }

    /**
     * 返回源建筑附近
     * @param sourceBuilding 源建筑
     */
    private fun returnToSource(sourceBuilding: DroneDeliveryTerminalBuild) {
        moveTo(sourceBuilding, IDLE_DISTANCE)
        unit.lookAt(sourceBuilding)
    }

}