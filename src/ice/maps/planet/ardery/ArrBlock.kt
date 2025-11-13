package ice.maps.planet.ardery

import arc.struct.Seq
import ice.content.IBlocks
import mindustry.world.Block

internal object ArrBlock {
    val 肿瘤地 = IBlocks.肿瘤地// 苔藓
    val 血痂地 = IBlocks.血痂地//孢子苔藓
    val 红土 = IBlocks.红土//shale
    val 风蚀沙地 = IBlocks.风蚀沙地
    var arr = getArrBlock()
    fun getArrBlock(): Array<Array<Block>> {
        val bs = Seq<Array<Block>>()
        fun adof(vararg block: Block) {
            bs.add(arrayOf(*block))
        }
        IBlocks.apply {
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(潮汐石, 风蚀沙地, 流纹岩, 幽灵草, 云英岩, 晶石地, 光辉板岩, 皎月银沙, 金珀沙, 侵蚀层地, 灰烬地, 凌冰)
            adof(血浅滩, 肿瘤地, 血痂地, 红土, 凌冰, 新月岩, 殷血粗沙, 红冰, 凌冰, 红冰)
            adof(血浅滩, 血痂地, 红冰, 凌冰, 凌冰, 殷血粗沙, 红冰)
            adof(红霜, 肿瘤地, 红土, 红霜, 肿瘤地, 红霜, 肿瘤地, 红霜, 殷血粗沙, 肿瘤地, 红霜)
            adof(红霜, 肿瘤地, 肿瘤地, 肿瘤地, 红土, 红冰)
        }
        return bs.toArray(Array<Block>::class.java)

    }
}