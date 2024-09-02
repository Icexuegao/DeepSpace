package ice.Alon.content.blocks;

import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;

import static ice.Alon.content.items.IceItems.*;

public class OreBlocks {
    public static Block oreGalena, oreSphalerite, oreQuartz;

    public static void load() {
        oreQuartz = new OreBlock("oreQuartz", quartz) {{
            variants = 3;
            mapColor = itemDrop.color;
        }};
        oreGalena = new OreBlock("oreGalena", galena) {{
            mapColor = itemDrop.color;
            variants = 3;
        }};
        oreSphalerite = new OreBlock("oreSphalerite", sphalerite) {{
            useColor = true;
            mapColor = itemDrop.color;
            variants = 3;
        }};
    }
}
