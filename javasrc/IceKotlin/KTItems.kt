package IceKotlin

import mindustry.type.Item

class KTItems {
    companion object {
        var kt: Item? = null
        fun load() {
            kt = object : Item("kt") {
                override fun init() {
                    flammability = 1f
                }
            }
        }
    }

}