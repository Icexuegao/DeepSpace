package ice.content;

import ice.type.IceLiquid;
import mindustry.type.Liquid;

public class IceLiquids {
    public static Liquid thickPlasma;

    public static void load() {
        /**深血浆 */
        thickPlasma = new IceLiquid("thickPlasma", "cc3737") {{
            nutrientConcentration = 0.2f;
        }};
    }
}
