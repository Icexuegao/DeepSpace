package ice.content;

import ice.type.IceLiquid;
import mindustry.type.Liquid;

public class IceLiquids {
    public static Liquid 血浆, 沼气;

    public static void load() {
        沼气 = new IceLiquid("methane", "bb2912") {{
            gas = true;
            explosiveness=0.5f;
            flammability = 0.8f;
        }};
        血浆 = new IceLiquid("thickPlasma", "cc3737") {{
            nutrientConcentration = 0.2f;
        }};
    }
}
