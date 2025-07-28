package ice.library.type.components

import arc.struct.Seq
import mindustry.gen.Building
import mindustry.gen.Unit

class BuildInterface {
    interface BuildSeq {
        val builds: Seq<out Building>
    }
    interface UnitSeq {
        val units: Seq<out Unit>
    }

    interface BuildWorldLoadEndEvent {
        fun worldLoadEvent()
    }
}

