package Iceconent.content;

import mindustry.content.TechTree;

public class IceTechTree {

    public static void load() {
        IcePlanet.IcePlanet.techTree = TechTree.nodeRoot("IcePlanet", IceBlocks.monocrystallineSiliconFactory, () -> {

        });
    }
}
