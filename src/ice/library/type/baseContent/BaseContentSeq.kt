package ice.library.type.baseContent

import arc.struct.Seq
import ice.library.type.baseContent.item.IceItem
import ice.library.type.baseContent.liquid.IceLiquid
import ice.library.type.baseContent.status.IceStatusEffect
import mindustry.type.UnitType
import mindustry.world.Block

object BaseContentSeq {
    val items: Seq<IceItem> = Seq(true,20)
    val liquids: Seq<IceLiquid> = Seq(true,20)
    val status: Seq<IceStatusEffect> = Seq(true,20)
    val blocks: Seq<Block> = Seq(true,20)
    val units: Seq<UnitType> = Seq(true,20)
}