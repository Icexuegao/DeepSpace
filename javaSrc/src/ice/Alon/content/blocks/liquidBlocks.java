package ice.Alon.content.blocks;

import ice.Alon.content.items.IceItems;
import ice.Alon.world.blocks.liquids.LiquidClassifier;
import ice.Alon.world.blocks.liquids.MultipleLiquidBlock;
import ice.Alon.world.blocks.liquids.pumpChamber;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class liquidBlocks {
    public static Block pumpChamber, fluidJunction, liquidClassifier;

    public static void load() {
        /**液体分类器 */
        liquidClassifier = new LiquidClassifier("liquidClassifier") {{
            size = 1;
            requirements(Category.liquid, ItemStack.with(Items.copper, 1));
        }};
        /** 泵腔*/
        pumpChamber = new pumpChamber("pumpChamber") {{
            requirements(Category.liquid, ItemStack.with(IceItems.muscleTendon, 40, IceItems.bonesNap, 10, IceItems.namelessCut, 60));
        }};
        /**流体枢纽 */
        fluidJunction = new MultipleLiquidBlock("fluidJunction") {{
            liquidCapacity = 1000;
            size = 3;
            health = size * size * 100;
            requirements(Category.liquid, ItemStack.with(Items.copper, 10));
        }};
    }
}
