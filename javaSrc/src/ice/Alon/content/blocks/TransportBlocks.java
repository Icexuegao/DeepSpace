package ice.Alon.content.blocks;

import ice.Alon.world.blocks.transport.Randomer;
import ice.Alon.content.items.IceItems;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;

public class TransportBlocks {
    public static Block electronicUninstaller,randomSource;
    public static void load(){
        /**电子装卸器 */
        electronicUninstaller = new Unloader("electronicUninstaller") {{
            requirements(Category.effect, ItemStack.with(IceItems.copperIngot, 10, IceItems.monocrystallineSilicon, 5));
            speed = 1.7142f;
            health = 100;
            size = 1;
            itemCapacity = 10;
            group = BlockGroup.transportation;
        }};
        /**随机源 */
        randomSource = new Randomer("randomSource") {{
            size = 1;
            requirements(Category.distribution, ItemStack.with(Items.copper, 1));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.shown;
        }};
    }
}
