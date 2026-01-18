@file:Suppress("UNCHECKED_CAST")

package universecore.components.blockcomp

import mindustry.gen.Building
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.modules.ItemModule
import mindustry.world.modules.LiquidModule

/**建筑组件的基本接口，其实应该使用一个组建管理器构建建筑等，但anuke的做法使得这很困难
 *
 * @author EBwilson
 * @since 1.0
 */
interface BuildCompBase {
    /**泛型检查的获取建筑的方块，这需要该实现类是[Building]的子类
     *
     * @param clazz 返回的方块类型
     */
    fun <T> getBlock(clazz: Class<T>): T {
        val block = this.building.block
        if (clazz.isAssignableFrom(block.javaClass)) {
            return block as T
        } else throw Exception("返回的方块类型 不匹配")
    }

    val block: Block
        /**获取此建筑的[Block]，需要 */
        get() = getBlock(Block::class.java)

    /**有检查的泛型获取该类型实例
     * @param clazz 返回的基类类型
     *
     * @throws ClassCastException 如果包含该接口的类并不是[Building]的子类
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getBuilding(clazz: Class<T>): T {
        if (clazz.isAssignableFrom(javaClass)) return this as T
        throw ClassCastException("$javaClass cannot cast to $clazz")
    }

    val building: Building
        /**获取Building
         *
         * @throws ClassCastException 如果包含该接口的类并不是[Building]的子类
         */
        get() {
            if (Building::class.java.isAssignableFrom(javaClass)) return this as Building
            throw ClassCastException(javaClass.toString() + " cannot cast to " + Building::class.java)
        }

    /**无检查的泛型获取该类型实例 */
    @Suppress("UNCHECKED_CAST")
    fun <T> getBuild(): T {
        return this as T
    }

    fun buildingRaw(): Building {
        return this as Building
    }

    val tile: Tile?
        /**获取该方块的tile */
        get() = buildingRaw().tile

    /**获得items模块 */ //BindField("items")
    fun items(): ItemModule? {
        return buildingRaw().items
    }

    /**获得liquids模块 */ //BindField("liquids")
    fun liquids(): LiquidModule? {
        return buildingRaw().liquids
    }
}
