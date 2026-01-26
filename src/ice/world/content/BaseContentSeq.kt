package ice.world.content

import arc.struct.Seq
import ice.DeepSpace
import ice.world.content.item.IceItem
import mindustry.Vars
import mindustry.type.Liquid
import mindustry.type.StatusEffect
import mindustry.type.UnitType
import mindustry.world.Block

object BaseContentSeq {
    val items = Seq<IceItem>().apply {
        Vars.content.items().forEach {
            if (it is IceItem) add(it)
        }
    }
    val liquids = Seq<Liquid>().apply {
        Vars.content.liquids().forEach {
            if (it.minfo.mod== DeepSpace.mod)add(it)
        }
    }
    val status = Seq<StatusEffect>().apply {
        Vars.content.statusEffects().forEach {
            if (it.minfo.mod ==DeepSpace.mod) add(it)
        }
    }
    val blocks = Seq<Block>().apply {
        Vars.content.blocks().forEach {
            if (it.minfo.mod == DeepSpace.mod) add(it)
        }
    }
    val units = Seq<UnitType>().apply {
        Vars.content.units().forEach {
            if (it.minfo.mod == DeepSpace.mod ) add(it)
        }
    }
}