package universecore.components.blockcomp

import arc.scene.ui.layout.Table
import mindustry.gen.Building

/**具有二级配置菜单的方块组件，在方块配置需要打开二级配置时需要这个接口 */
interface SecondableConfigBuildComp {
    /**二级配置面板的建立方法
     *
     * @param table 面板的布局表格，对这个列表进行编辑
     * @param target 二级配置进行的目标建筑
     */
    fun buildSecondaryConfig(table: Table, target: Building)
}