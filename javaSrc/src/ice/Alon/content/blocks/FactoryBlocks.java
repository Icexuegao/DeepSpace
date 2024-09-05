package ice.Alon.content.blocks;

import ice.Alon.content.IceEffects;
import ice.Alon.content.items.IceItems;
import ice.Alon.world.blocks.factoryBlocks.EffectGenericCrafter;
import mindustry.entities.effect.MultiEffect;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;

public class FactoryBlocks {
    public static Block integratedFactory;

    public static void load() {
        /**集成分发器*/
        integratedFactory = new EffectGenericCrafter("integratedFactory") {{
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-top"));
            itemCapacity = 20;
            health = 200;
            outputItem = new ItemStack(IceItems.integratedCircuit, 1);
            consumeItems(ItemStack.with(IceItems.monocrystallineSilicon, 1, IceItems.graphene, 2, IceItems.quartzGlass, 1));
            craftTime = 60;
            craftEffect = new MultiEffect(IceEffects.lancerLaserShoot, IceEffects.lancerLaserChargeBegin, IceEffects.hitLaserBlast);
            size = 3;
            requirements(Category.crafting, ItemStack.with(IceItems.copperIngot, 19));
        }};
    }
}
