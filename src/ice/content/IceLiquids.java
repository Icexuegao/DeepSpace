package ice.content;

import ice.type.content.IceLiquid;
import mindustry.type.Liquid;

public class IceLiquids {
    public static Liquid 血浆, 沼气, 硫酸, 氦气;

    public static void load() {
        氦气 = new IceLiquid("helium", "f2ffbd") {{
            explosiveness = 0.3f;
            flammability = 1f;
            gas=true;
        }};
        沼气 = new IceLiquid("methane", "bb2912") {{
            gas = true;
            explosiveness = 0.5f;
            flammability = 0.8f;
        }};
        血浆 = new IceLiquid("thickPlasma", "cc3737") {{
            nutrientConcentration = 0.2f;
        }};
        硫酸 = new IceLiquid("vitriol", "ffaa5f") {{
            viscosity = 0.7f;
            boilPoint = 1.7f;
        }};
    }
}
