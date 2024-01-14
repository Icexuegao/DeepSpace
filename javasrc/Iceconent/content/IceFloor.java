package Iceconent.content;

import arc.graphics.Color;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;

public class IceFloor {
    public static Block oreIceCrystals;

    public static void load() {
        oreIceCrystals = new OreBlock("ore-ice-crystals", IceItems.iceCrystals) {{
            playerUnmineable =false;
            mapColor = Color.white;
            useColor=true;
            oreDefault=true;
            oreThreshold =0.8f;
            oreScale =20;
            variants =0;
        }};
    }
}
