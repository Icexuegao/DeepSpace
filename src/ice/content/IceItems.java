package ice.content;

import arc.graphics.Color;
import ice.type.IceItem;
import mindustry.content.Items;

public class IceItems {

    public static IceItem 金珀沙, 黄玉髓, 精制煤, 生煤, 金矿, 金锭,

    脊髓末梢, 无名肉块, 碎骨, 肌腱, 红冰, 血囊孢子,

    方铅矿, 闪锌矿, 黄铜矿, 赤铁矿, 石英, 石英砂,

    锌锭, 铜锭, 铅锭, 低碳钢, 高碳钢, 黄铜锭,

    石英玻璃, 石墨烯, 集成电路, 单晶硅,

    晶状孢芽, 灼热孢团, 寂温疮体, 铬铁矿;

    public static void load() {
        flesh();
        spore();
        sand();
        ingot();
        ore();
        products();
    }

    private static void flesh() {
        脊髓末梢 = new IceItem("spinalCordEnding", "bf3e47") {{
            营养浓度 = 0.1f;
        }};
        无名肉块 = new IceItem("namelessCut", "ff7171") {{
            营养浓度 = 1;
        }};
        碎骨 = new IceItem("bonesNap", "ff7172") {{
            营养浓度 = 0.1f;
        }};
        肌腱 = new IceItem("muscleTendon", "ff7172") {{
            营养浓度 = 0.25f;
        }};
    }

    private static void sand() {
        红冰 = new IceItem("redIce", "ff7171") {{
            radioactivity = 0.05f;
        }};
        石英 = new IceItem("quartz", Color.white) {{
            charge = 0.1f;
        }};
        石英砂 = new IceItem("quartzSand", "FFFFFF") {{
            hardness = 2;
        }};
        金珀沙 = new IceItem("goldSand", "f8efad") {{
            hardness = 2;
        }};
        黄玉髓 = new IceItem("canaryStone", "f5c782") {{
            hardness = 1;
        }};
    }

    private static void products() {
        精制煤 = new IceItem("refineCoal", "151515");
        石英玻璃 = new IceItem("quartzGlass", "ebeef5");
        石墨烯 = new IceItem("graphene", "52578a");
        单晶硅 = new IceItem("monocrystallineSilicon", "575757ff");
        集成电路 = new IceItem("integratedCircuit", "53565c");
    }

    private static void spore() {
        晶状孢芽 = new IceItem("crystallineSpore", Color.blue) {{
            flammability = 0.2f;
        }};
        灼热孢团 = new IceItem("pyroSpore", Color.yellow) {{
            explosiveness = 0.1f;
        }};
        寂温疮体 = new IceItem("lonelyHeatSoreSpore", Color.blue) {
        };
        血囊孢子 = new IceItem("bloodSpore", "ffa0a0") {{
            radioactivity = 0.05f;
            hardness = 1;
            frames = 5;
            frameTime = 15f;
        }};
    }

    private static void ore() {
        生煤 = new IceItem("rawCoal", Color.black, 2);
        铬铁矿 = new IceItem("chrome", Items.tungsten.color, 3);
        方铅矿 = new IceItem("galena", Items.lead.color, 2);
        赤铁矿 = new IceItem("hematite", "c6a699", 2);
        闪锌矿 = new IceItem("sphalerite", "578c80", 3);
        金矿 = new IceItem("goldOre", "f8df87", 4);
        黄铜矿 = new IceItem("copperPyrites", "eac73e", 3);
    }

    private static void ingot() {
        低碳钢 = new IceItem("lowCarbonSteel", "d4d7e4");
        高碳钢 = new IceItem("highCarbonSteel", "bedfee");
        铜锭 = new IceItem("copperIngot", Items.copper.color);
        铅锭 = new IceItem("leadIngot", Items.lead.color);
        锌锭 = new IceItem("zincIngot", "578c80");
        金锭 = new IceItem("goldIngot", "f8df87");
        黄铜锭 = new IceItem("brassIngot", "ffd87d");
    }
}
