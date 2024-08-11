package Ice.asundry.Content;

import Ice.content.IceItems;

import static mindustry.content.TechTree.node;
import static mindustry.content.TechTree.nodeRoot;

public class TreeTech {

    public static void load() {
        IcePlanets.aDri.techTree = nodeRoot("aDri", Ice.content.IceBlocks.fleshAndBloodhinge, () -> {
            node(IceItems.redIce, () -> {
                node(IceItems.iceCrystals, () -> {
                    node(IceItems.monocrystallineSilicon, () -> {
                        node(IceItems.bloodSpore, () -> {
                            node(IceItems.copperIngot,()->{
                                node(IceBlocks.monocrystallineSiliconFactory);
                            });
                        });
                    });
                });
            });
        });
    }
}
