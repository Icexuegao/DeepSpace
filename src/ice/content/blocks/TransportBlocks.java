package ice.content.blocks;

import ice.content.IceItems;
import ice.world.blocks.transport.Randomer;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.meta.BuildVisibility;

public class TransportBlocks {
    public static Block 电子装卸器, 随机源;

    public static void load() {
        电子装卸器 = new Unloader("electronicUninstaller") {{
            requirements(Category.effect, ItemStack.with(IceItems.铜锭, 10, IceItems.单晶硅, 5));
            speed = 1.7142f;
            health = 100;
            size = 1;
            itemCapacity = 10;
        }};
        随机源 = new Randomer("randomSource") {{
            size = 1;
            requirements(Category.distribution, ItemStack.with(Items.copper, 1));
            alwaysUnlocked = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
    }
}
