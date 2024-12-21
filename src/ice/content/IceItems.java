package ice.content;

import arc.graphics.Color;
import ice.type.content.IceItem;
import mindustry.content.Items;

import static ice.type.content.IceItem.OreItem;

public class IceItems {
    public static void load() {
        flesh();
        spore();
        sand();
        ore();
        ingot();
        products();
    }

    public static IceItem 脊髓末梢, 无名肉块, 碎骨, 肌腱;

    private static void flesh() {
        脊髓末梢 = new IceItem("spinalCordEnding", "bf3e47") {{
            营养浓度 = 0.1f;
        }};
        无名肉块 = new IceItem("namelessCut", "ff7171") {{
            营养浓度 = 0.5f;
        }};
        碎骨 = new IceItem("bonesNap", "ff7172") {{
            营养浓度 = 0.1f;
        }};
        肌腱 = new IceItem("muscleTendon", "ff7172") {{
            营养浓度 = 0.25f;
        }};
    }

    public static IceItem 金珀沙, 黄玉髓, 石英砂, 红冰;

    private static void sand() {
        红冰 = new IceItem("redIce", "ff7171") {{
            radioactivity = 0.05f;
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

    public static IceItem 晶状孢芽, 灼热孢团, 寂温疮体, 血囊孢子;

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

    public static OreItem 硫钴矿, 方铅矿, 闪锌矿, 黄铜矿, 赤铁矿, 铬铁矿, 锆英石, 生煤, 金矿, 石英;

    private static void ore() {
        石英 = new OreItem("quartz", Color.white, 1);
        生煤 = new OreItem("rawCoal", Color.black, 2);
        铬铁矿 = new OreItem("chrome", Items.tungsten.color, 3);
        方铅矿 = new OreItem("galena", Items.lead.color, 2);
        赤铁矿 = new OreItem("hematite", "c6a699", 2);
        闪锌矿 = new OreItem("sphalerite", "578c80", 3);
        金矿 = new OreItem("goldOre", "f8df87", 4);
        黄铜矿 = new OreItem("copperPyrites", "eac73e", 3);
        锆英石 = new OreItem("azorite", "8c3e2d", 4);
        硫钴矿 = new OreItem("linnaeite", "#cfecf1", 3);
    }

    public static IceItem 铪锭, 金锭, 锌锭, 铜锭, 铅锭, 低碳钢, 高碳钢, 黄铜锭;

    private static void ingot() {
        低碳钢 = new IceItem("lowCarbonSteel", "d4d7e4");
        高碳钢 = new IceItem("highCarbonSteel", "bedfee");
        铜锭 = new IceItem("copperIngot", Items.copper.color);
        铅锭 = new IceItem("leadIngot", Items.lead.color);
        锌锭 = new IceItem("zincIngot", 闪锌矿.color);
        金锭 = new IceItem("goldIngot", 金矿.color);
        黄铜锭 = new IceItem("brassIngot", 黄铜矿.color);
        铪锭 = new IceItem("hafniIngot", "f7e5f3");
    }

    public static IceItem 石英玻璃, 石墨烯, 集成电路, 单晶硅, 精制煤;

    private static void products() {
        精制煤 = new IceItem("refineCoal", "151515");
        石英玻璃 = new IceItem("quartzGlass", "ebeef5");
        石墨烯 = new IceItem("graphene", "52578a");
        单晶硅 = new IceItem("monocrystallineSilicon", "575757ff");
        集成电路 = new IceItem("integratedCircuit", "53565c");
    }
}
