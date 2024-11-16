package ice.Alon.content.blocks;

import ice.Alon.content.IceItems;
import ice.Alon.world.blocks.environment.IceOreBlock;
import mindustry.world.Block;

public class OreBlocks {
    public static Block oreGalena, oreSphalerite, oreQuartz, oreChrome, oreGold, chalcopyrite;

    public static void load() {
        oreChrome = new IceOreBlock("oreChrome",IceItems.chrome) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
        oreQuartz = new IceOreBlock("oreQuartz", IceItems.quartz) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
        oreGalena = new IceOreBlock("oreGalena", IceItems.galena) {{
            useColor = true;
            mapColor = itemDrop.color;
            variants = 3;
        }};
        oreSphalerite = new IceOreBlock("oreSphalerite", IceItems.sphalerite) {{
            useColor = true;
            mapColor = itemDrop.color;
            variants = 3;
        }};
        /**金矿*/
        oreGold = new IceOreBlock("oreGold", IceItems.goldOre) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
        chalcopyrite = new IceOreBlock("chalcopyrite", IceItems.chalcopyrite) {{
            useColor = true;
            variants = 3;
            mapColor = itemDrop.color;
        }};
    }
}
