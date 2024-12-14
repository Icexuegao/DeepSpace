package ice.content.blocks;

import ice.content.IceItems;
import ice.world.blocks.environment.IceOreBlock;
import mindustry.world.Block;

public class OreBlocks {
    public static Block 方铅矿, 闪锌矿, 石英矿, 铬铁矿, 金矿, 黄铜矿;

    public static void load() {
        铬铁矿 = new IceOreBlock("oreChrome", IceItems.铬铁矿) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
        石英矿 = new IceOreBlock("oreQuartz", IceItems.石英) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
        方铅矿 = new IceOreBlock("oreGalena", IceItems.方铅矿) {{
            useColor = true;
            mapColor = itemDrop.color;
            variants = 3;
        }};
        闪锌矿 = new IceOreBlock("oreSphalerite", IceItems.闪锌矿) {{
            useColor = true;
            mapColor = itemDrop.color;
            variants = 3;
        }};
        金矿 = new IceOreBlock("oreGold", IceItems.金矿) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
        黄铜矿 = new IceOreBlock("chalcopyrite", IceItems.黄铜矿) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
    }
}
