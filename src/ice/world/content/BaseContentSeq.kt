package ice.world.content

import arc.struct.Seq
import ice.Ice
import ice.world.content.item.IceItem
import ice.world.content.status.StatusEffect
import ice.world.content.unit.IceUnitType
import mindustry.Vars
import mindustry.type.Liquid
import mindustry.world.Block

object BaseContentSeq {
    val items = Seq<IceItem>().apply {
        Vars.content.items().forEach {
            if (it is IceItem) add(it)
        }
    }
    val liquids = Seq<Liquid>().apply {
        Vars.content.liquids().forEach {
            if (it.minfo.mod== Ice.mod)add(it)
        }
    }
    val status = Seq<StatusEffect>().apply {
        Vars.content.statusEffects().forEach {
            if (it is StatusEffect) add(it)
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