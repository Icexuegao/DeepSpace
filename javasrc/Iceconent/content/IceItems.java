package Iceconent.content;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;

public class IceItems extends Items {

    public static Item redIce, monocrystallineSilicon, iceCrystals;

    public static void load() {
        redIce = new Item("red-Ice", Color.valueOf("ff7171")) {{
            radioactivity = 0.05F;
        }};
        monocrystallineSilicon = new Item("monocrystalline-Silicon", Color.valueOf("575757ff")) {{
            charge = 2f;
            radioactivity = 0.1f;
            explosiveness = 0.1f;
            flammability = 0.1f;
        }};
        iceCrystals = new Item("ice-Crystals", Color.valueOf("FFFFFF")) {{
            charge = 0.5f;
            hardness = 3;
            alwaysUnlocked = true;
            flammability = 1f;

        }};
    }
}
