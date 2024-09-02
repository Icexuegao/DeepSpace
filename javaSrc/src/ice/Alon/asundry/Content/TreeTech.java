package ice.Alon.asundry.Content;

import ice.Alon.content.blocks.EffectBlocks;
import ice.Alon.content.items.IceItems;
import mindustry.content.TechTree;

public class TreeTech {

    public static void load() {
        IcePlanets.aDri.techTree = TechTree.nodeRoot("aDri", EffectBlocks.fleshAndBloodhinge, () -> {
            TechTree.node(IceItems.redIce, () -> {
                TechTree.node(IceItems.iceCrystals, () -> {
                    TechTree.node(IceItems.monocrystallineSilicon, () -> {
                        TechTree.node(IceItems.bloodSpore, () -> {
                            TechTree.node(IceItems.copperIngot, () -> {
                                TechTree.node(IceBlocks.monocrystallineSiliconFactory);
                            });
                        });
                    });
                });
            });
        });
    }
}
