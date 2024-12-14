package ice.content.blocks;

import ice.content.IceItems;
import ice.world.blocks.liquids.LiquidClassifier;
import ice.world.blocks.liquids.MultipleLiquidBlock;
import ice.world.blocks.liquids.pumpChamber;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class liquidBlocks {
    public static Block 泵腔, 流体枢纽, 液体分类器;

    public static void load() {
        液体分类器 = new LiquidClassifier("liquidClassifier") {{
            size = 1;
            requirements(Category.liquid, ItemStack.with(Items.copper, 1));
        }};
        泵腔 = new pumpChamber("pumpChamber") {{
            requirements(Category.liquid, ItemStack.with(IceItems.肌腱, 40, IceItems.碎骨, 10, IceItems.无名肉块, 60));
        }};
        流体枢纽 = new MultipleLiquidBlock("fluidJunction") {{
            liquidCapacity = 1000;
            size = 3;
            health = size * size * 100;
            requirements(Category.liquid, ItemStack.with(Items.copper, 10));
        }};
    }
}
