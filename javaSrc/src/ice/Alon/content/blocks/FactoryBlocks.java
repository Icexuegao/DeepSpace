package ice.Alon.content.blocks;

import ice.Alon.content.items.IceItems;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;

public class FactoryBlocks {
    public static Block integratedFactory;
    public static void load() {
        /**集成分发器*/
        integratedFactory = new GenericCrafter("integratedFactory") {{
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-top"));
            itemCapacity = 20;
            health = 200;
            outputItem = new ItemStack(IceItems.integratedCircuit, 1);
            consumeItems(ItemStack.with(IceItems.monocrystallineSilicon, 1, IceItems.graphene, 2, IceItems.quartzGlass, 1));
            craftTime = 60;
            size = 3;
            requirements(Category.crafting, ItemStack.with(IceItems.copperIngot, 19));
        }};
    }
}
