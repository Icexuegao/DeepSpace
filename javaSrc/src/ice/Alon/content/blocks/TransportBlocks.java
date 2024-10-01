package ice.Alon.content.blocks;

import ice.Alon.content.IceItems;
import ice.Alon.world.blocks.transport.FleshAndBloodConveyor;
import ice.Alon.world.blocks.transport.Randomer;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.meta.BuildVisibility;

public class TransportBlocks {
    public static Block fleshAndBloodConveyor, electronicUninstaller, randomSource;

    public static void load() {
        fleshAndBloodConveyor = new FleshAndBloodConveyor("k3") {{
            speed=0.13f;
            size=1;
            health=50;
            requirements(Category.distribution,ItemStack.with(IceItems.namelessCut,2));
        }};
        /**电子装卸器 */
        electronicUninstaller = new Unloader("electronicUninstaller") {{
            requirements(Category.effect, ItemStack.with(IceItems.copperIngot, 10, IceItems.monocrystallineSilicon, 5));
            speed = 1.7142f;
            health = 100;
            size = 1;
            itemCapacity = 10;
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
