package iceKotlin.Alon

import mindustry.type.Item

class KtItems {
    object KtItems {
        var kt: Item? = null
        fun load() {
            kt = Item("ktjs")
        }

    }
}