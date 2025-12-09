package ice.world.meta

import ice.ui.bundle.BaseBundle
import mindustry.world.meta.Stat
import mindustry.world.meta.StatCat

class IceStat(name: String, category: StatCat = StatCat.general) : Stat(name, category), BaseBundle.Bundle {
    override var localizedName = name
    override fun localized() = localizedName
}