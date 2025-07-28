package ice.library.type.meta.stat

import mindustry.world.meta.StatCat

object IceStatCats {
    val deepSpace = IceStatCat("deepSpace")

    class IceStatCat(name: String) : StatCat(name) {
        lateinit var localizedName: String
        override fun localized(): String {
            return localizedName
        }
    }
}
