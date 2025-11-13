package ice.library.content

import arc.struct.Seq
import ice.Ice
import ice.library.content.item.IceItem
import ice.library.content.liquid.IceLiquid
import ice.library.content.status.IceStatusEffect
import ice.library.content.unit.type.IceUnitType
import mindustry.Vars
import mindustry.world.Block

object BaseContentSeq {
    val items = Seq<IceItem>().apply {
        Vars.content.items().forEach {
            if (it is IceItem) add(it)
        }
    }
    val liquids = Seq<IceLiquid>().apply {
        Vars.content.liquids().forEach {
            if (it is IceLiquid) add(it)
        }
    }
    val status = Seq<IceStatusEffect>().apply {
        Vars.content.statusEffects().forEach {
            if (it is IceStatusEffect) add(it)
        }
    }
    val blocks = Seq<Block>().apply {
        Vars.content.blocks().forEach {
            if (it.minfo.mod == Ice.mod) add(it)
        }
    }
    val units = Seq<IceUnitType>().apply {
        Vars.content.units().forEach {
            if (it is IceUnitType) add(it)
        }
    }
}