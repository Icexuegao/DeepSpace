package ice.content.blocks;

import ice.content.IceItems;
import ice.world.blocks.effects.ResBox;
import ice.world.blocks.effects.fleshAndBloodCoreBlock;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class EffectBlocks {
    public static Block 遗弃资源箱, 血肉枢纽;

    public static void laod() {
        遗弃资源箱 = new ResBox("resBox");
        血肉枢纽 = new fleshAndBloodCoreBlock("fleshAndBloodhinge") {{
            health = -1;
            size = 4;
            itemCapacity = 6000;
            squareSprite = false;
            requirements(Category.effect, ItemStack.with(IceItems.无名肉块, 2300, IceItems.碎骨, 2000));
        }};
    }
}
