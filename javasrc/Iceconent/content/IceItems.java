package Iceconent.content;

import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;

public class IceItems extends Items {

    public static Item quartz, iceSpore, redIce, monocrystallineSilicon, iceCrystals;

    public static void load() {
        iceSpore = new Item("ice-Spore", Color.valueOf("aaffff")) {{
            flammability = 0.2f;
            hardness = 2;
            frames = 5;
            frameTime = 15;
            radioactivity = 0.12f;
        }};
        iceCrystals = new Item("ice-Crystals", Color.valueOf("FFFFFF")) {{
            charge = 0.5f;
            hardness = 3;
            alwaysUnlocked = true;
            flammability = 1f;
        }};
        redIce = new Item("red-Ice", Color.valueOf("ff7171")) {{
            radioactivity = 0.05F;
        }};
        quartz = new Item("quartz", Color.white) {{
            charge = 0.1f;
        }};
        monocrystallineSilicon = new Item("monocrystalline-Silicon", Color.valueOf("575757ff")) {{
            charge = 0.2f;
            radioactivity = 0.1f;
            explosiveness = 0.1f;
        }};

    }
}
