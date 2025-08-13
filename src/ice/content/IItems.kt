package ice.content

import ice.library.baseContent.BaseContentSeq
import ice.library.baseContent.item.IceItem
import ice.library.baseContent.item.OreItem
import mindustry.Vars

object IItems {
    fun load() {
        Vars.content.items().forEach {
            if (it is IceItem) {
                BaseContentSeq.items.add(it)
            }
        }
    }

    val 脊髓末梢 = IceItem("spinalCordEnding", "bf3e47").apply {
        nutrientConcentration = 0.1f
    }
    val 无名肉块 = IceItem("namelessCut", "bf3e47").apply {
        nutrientConcentration = 0.5f
    }
    val 碎骨 = IceItem("bonesNap", "bf3e47").apply {
        nutrientConcentration = 0.1f
    }
    val 肌腱 = IceItem("muscleTendon", "bf3e47").apply {
        nutrientConcentration = 0.25f
    }
    val 骨钢 = IceItem("fleshSteel", "bf3e47").apply {}
    val 金珀沙 = IceItem("goldSand", "f8efad").apply {
        hardness = 2
    }
    val 黄玉髓 = IceItem("canaryStone", "f5c782").apply {
        hardness = 1
    }
    val 红冰 = IceItem("redIce", "ff7171").apply {
        radioactivity = 0.05f
    }
    val 晶状孢芽 = IceItem("crystallineSpore", "52578a").apply {
        flammability = 0.2f
    }
    val 灼热孢团 = IceItem("pyroSpore", "eac73e").apply {
        explosiveness = 0.1f
    }
    val 寂温疮体 = IceItem("lonelyHeatSoreSpore", "b3f1ff").apply {}
    val 血囊孢子 = IceItem("bloodSpore", "ffa0a0").apply {
        radioactivity = 0.05f
        hardness = 1
        frames = 5
        frameTime = 15f
    }
    val 石英 = OreItem("quartz", "ffffff", 1)
    val 生煤 = OreItem("rawCoal", "151515", 2)
    val 燃素水晶= IceItem("phlogistonCrystal","b38f8d")
    val 铬铁矿 = OreItem("chrome", "768a9a", 3)
    val 方铅矿 = OreItem("galena", "8c7fa9", 2)
    val 赤铁矿 = OreItem("hematite", "c6a699", 2)
    val 闪锌矿 = OreItem("sphalerite", "578c80", 3)
    val 金矿 = OreItem("goldOre", "f8df87", 4)
    val 黄铜矿 = OreItem("copperPyrites", "eac73e", 3)
    val 锆英石 = OreItem("azorite", "8c3e2d", 4)
    val 硫钴矿 = OreItem("linnaeite", "cfecf1", 3)
    val 低碳钢 = IceItem("lowCarbonSteel", "d4d7e4")
    val 高碳钢 = IceItem("highCarbonSteel", "bedfee")
    val 铜锭 = IceItem("copperIngot", "d99d73")
    val 铅锭 = IceItem("leadIngot", "8c7fa9")
    val 锌锭 = IceItem("zincIngot", "578c80")
    val 金锭 = IceItem("goldIngot", "f8df87")
    val 铬锭= IceItem("chromeIngot","C8C8E4")
    val 黄铜锭 = IceItem("brassIngot", "eac73e")
    val 铪锭 = IceItem("hafniIngot", "f7e5f3")
    val 钴锭 = IceItem("cobaltIngot", "b3f1ff")
    val 精制煤 = IceItem("refineCoal", "151515")
    val 石英玻璃 = IceItem("quartzGlass", "ebeef5")
    val 复合陶瓷 = IceItem("compositeCeramic", "ebeef5")
    val 石墨烯 = IceItem("graphene", "52578a")
    val 单晶硅 = IceItem("monocrystallineSilicon", "575757ff")
    val 电子元件 = IceItem("integratedCircuit", "53565c")
    val 暮光合金= IceItem("duskIngot","d6f1ff")

}
