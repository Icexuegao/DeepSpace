package ice.ai

import arc.Core
import mindustry.entities.units.AIController

class AIController : AIController() {
    var aimUnit = false // 用于瞄准的单位
    override fun updateWeapons() {
        if (!aimUnit&& Core.settings.getBool("autotarget")) super.updateWeapons()
    }
}