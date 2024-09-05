package ice.Alon.content.blocks;

import ice.Alon.content.items.IceItems;
import ice.Alon.world.blocks.effects.fleshAndBloodCoreBlock;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class EffectBlocks {
    public static Block fleshAndBloodhinge;
    public static void laod() {
        /** 血肉枢纽*/
        fleshAndBloodhinge = new fleshAndBloodCoreBlock("fleshAndBloodhinge") {{
            health = -1;
            size = 4;
            itemCapacity = 6000;
            squareSprite = false;
            requirements(Category.effect, ItemStack.with(IceItems.namelessCut, 2300, IceItems.bonesNap, 2000));
        }};
    }
}
