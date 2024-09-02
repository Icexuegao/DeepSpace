package ice.Alon.content.items

import ice.Alon.type.IceItem

class KtItems : Any() {
    companion object {
        lateinit var crystallineSpore: IceItem
        lateinit var pyroSpore: IceItem
        lateinit var lonelyHeatSoreSpore: IceItem
        fun load() {
            /** 晶状孢芽 */
            crystallineSpore = object : IceItem("crystallineSpore") {}.apply {
                flammability = 0.2f
            }
            /** 灼热孢团 */
            pyroSpore = object : IceItem("pyroSpore") {}.apply {
                explosiveness = 0.1f
            }
            /** 寂温疮体 */
            lonelyHeatSoreSpore = object : IceItem("lonelyHeatSoreSpore") {}.apply { }
        }
    }
}