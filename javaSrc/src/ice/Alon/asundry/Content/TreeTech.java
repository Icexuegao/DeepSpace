package ice.Alon.asundry.Content;

import ice.Alon.content.items.IceItems;
import mindustry.content.TechTree;

import static mindustry.content.TechTree.node;
import static mindustry.content.TechTree.nodeRoot;

public class TreeTech {

    public static void load() {
        IcePlanets.aDri.techTree = TechTree.nodeRoot("aDri", ice.Alon.content.IceBlocks.fleshAndBloodhinge, () -> {
            TechTree.node(IceItems.redIce, () -> {
                TechTree.node(IceItems.iceCrystals, () -> {
                    TechTree.node(IceItems.monocrystallineSilicon, () -> {
                        TechTree.node(IceItems.bloodSpore, () -> {
                            TechTree.node(IceItems.copperIngot,()->{
                                TechTree.node(IceBlocks.monocrystallineSiliconFactory);
                            });
                        });
                    });
                });
            });
        });
    }
}
