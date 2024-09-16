package ice.Alon.content.items;

import arc.graphics.Color;
import ice.Alon.type.IceItem;
import mindustry.content.Items;
import mindustry.type.Item;

public class IceItems {

    public static Item iceCrystals,

    spinalCordEnding,namelessCut, bonesNap, muscleTendon, redIce, bloodSpore,

    galena, sphalerite, copperPyrites, hematite, quartz, quartzSand,

    zincIngot, copperIngot, leadIngot, ironIngot, brassIngot,

    quartzGlass, graphene, integratedCircuit, monocrystallineSilicon;

    public static void load() {
        /**冰晶 */
        iceCrystals = new IceItem("iceCrystals", "FFFFFF") {{
            charge = 0.5f;
            hardness = 3;
            flammability = 1f;
        }};

        /**血囊孢子 */
        bloodSpore = new IceItem("bloodSpore", "ffa0a0") {{
            radioactivity = 0.05f;
            hardness = 1;
            frames = 5;
            frameTime = 15f;
        }};
        /** 脊髓末梢*/
        spinalCordEnding =new IceItem("spinalCordEnding","bf3e47"){{
            frames = 3;
            frameTime = 30f;
            nutrientConcentration =0.1f;
        }};
        /**无名肉块 */
        namelessCut = new IceItem("namelessCut", "ff7171") {{
            nutrientConcentration = 1;
        }};
        /**碎骨 */
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
        /** 方铅矿*/
        galena = new IceItem("galena") {{
            hardness = 2;
        }};
        /**石英 */
        quartz = new IceItem("quartz", Color.white) {{
            charge = 0.1f;
        }};
        /**石英砂 */
        quartzSand = new IceItem("quartzSand", "FFFFFF") {{
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
        /** 黄铜矿*/
        copperPyrites = new IceItem("copperPyrites", "eac73e") {{
            hardness = 3;
        }};
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
        /**黄铜锭 */
        brassIngot = new IceItem("brassIngot", "ffd87d") {
        };
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
    }
}
