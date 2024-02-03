package Iceconent.content;

import static mindustry.content.TechTree.*;

public class IceTechTree {
    public static TechNode k;

    public static void load() {
        IcePlanet.aDri.techTree = nodeRoot("aDri", IceBlocks.monocrystallineSiliconFactory, () -> {
        });
        k = nodeRoot("d", IceItems.redIce, () -> {
            node(IceSectorPresets.s1);
        });
    }
}
