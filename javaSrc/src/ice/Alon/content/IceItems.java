package ice.Alon.content;

import arc.graphics.Color;
import ice.Alon.type.IceItem;
import mindustry.content.Items;
import mindustry.type.Item;

public class IceItems {

    public static Item goldSand, canaryStone, refineCoal, rawCoal, goldOre, goldIngot, spinalCordEnding, namelessCut, bonesNap, muscleTendon, redIce, bloodSpore,

    galena, sphalerite, chalcopyrite, hematite, quartz, quartzSand,

    zincIngot, copperIngot, leadIngot, ironIngot, brassIngot,

    quartzGlass, graphene, integratedCircuit, monocrystallineSilicon,

    crystallineSpore, pyroSpore, lonelyHeatSoreSpore, chrome;

    public static void load() {
        /** 晶状孢芽 */
        crystallineSpore = new IceItem("crystallineSpore") {{
            flammability = 0.2f;
        }};
        /** 灼热孢团 */
        pyroSpore = new IceItem("pyroSpore") {{
            explosiveness = 0.1f;
        }};
        /** 寂温疮体 */
        lonelyHeatSoreSpore = new IceItem("lonelyHeatSoreSpore") {
        };
        /**血囊孢子*/
        bloodSpore = new IceItem("bloodSpore", "ffa0a0") {{
            radioactivity = 0.05f;
            hardness = 1;
            frames = 5;
            frameTime = 15f;
        }};
        /**脊髓末梢*/
        spinalCordEnding = new IceItem("spinalCordEnding", "bf3e47") {{
            nutrientConcentration = 0.1f;
        }};
        /**无名肉块*/
        namelessCut = new IceItem("namelessCut", "ff7171") {{
            nutrientConcentration = 1;
        }};
        /**碎骨*/
        bonesNap = new IceItem("bonesNap", "ff7172") {{
            nutrientConcentration = 0.1f;
        }};
        /** 肌腱*/
        muscleTendon = new IceItem("muscleTendon", "ff7172") {{
            nutrientConcentration = 0.25f;
        }};
        /** 红冰*/
        redIce = new IceItem("redIce", "ff7171") {{
            radioactivity = 0.05f;
        }};
        /**生煤*/
        rawCoal = new IceItem("rawCoal", "404040") {{
            hardness = 2;
        }};
        /**精制煤*/
        refineCoal = new IceItem("refineCoal", "151515") {{
            hardness = 0;
        }};

        /**石英 */
        quartz = new IceItem("quartz", Color.white) {{
            charge = 0.1f;
        }};
        /**石英砂 */
        quartzSand = new IceItem("quartzSand", "FFFFFF") {{
            hardness = 2;
        }};
        /**金珀沙*/
        goldSand = new IceItem("goldSand", "f8efad") {{
            hardness = 2;
        }};
        /**黄玉髓*/
        canaryStone = new IceItem("canaryStone", "f5c782") {{
            hardness = 1;
        }};
        /**石英玻璃 */
        quartzGlass = new IceItem("quartzGlass") {
        };
        /**石墨烯 */
        graphene = new IceItem("graphene", "52578a") {
        };
        /**单晶硅 */
        monocrystallineSilicon = new IceItem("monocrystallineSilicon", "575757ff") {{
            charge = 0.1f;
        }};
        /** 集成电路*/
        integratedCircuit = new IceItem("integratedCircuit") {
        };
        ore();
        ingot();
    }

    private static void ore() {
        /**铬矿石*/
        chrome = new IceItem("chrome") {{
            hardness = 3;
        }};
        /** 方铅矿*/
        galena = new IceItem("galena", Items.lead.color) {{
            hardness = 2;
        }};
        /**赤铁矿 */
        hematite = new IceItem("hematite", "c6a699") {{
            hardness = 2;
        }};
        /**闪锌矿 */
        sphalerite = new IceItem("sphalerite", "578c80") {{
            hardness = 3;
        }};
        /**金矿*/
        goldOre = new IceItem("goldOre", "f8df87") {{
            hardness = 4;
        }};
        /** 黄铜矿*/
        chalcopyrite = new IceItem("copperPyrites", "eac73e") {{
            hardness = 3;
        }};
    }

    private static void ingot() {
        /**铁锭 */
        ironIngot = new IceItem("ironIngot", "d4d7e4") {{
            hardness = 3;
        }};
        /**铜锭 */
        copperIngot = new IceItem("copperIngot", Items.copper.color) {
        };
        /**铅锭 */
        leadIngot = new IceItem("leadIngot", Items.lead.color);
        /** 锌锭*/
        zincIngot = new IceItem("zincIngot", "578c80") {
        };
        /**金锭*/
        goldIngot = new IceItem("goldIngot", "f8df87") {
        };
        /**黄铜锭 */
        brassIngot = new IceItem("brassIngot", "ffd87d") {
        };
    }
}
