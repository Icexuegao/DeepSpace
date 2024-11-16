package ice.Alon.content.blocks;

import arc.graphics.Color;
import ice.Alon.world.blocks.factoryBlocks.multipleCrafter.Formula;
import ice.Alon.world.blocks.factoryBlocks.multipleCrafter.MultipleCrafter;
import ice.Alon.library.draw.DrawLiquidOutputs;
import ice.Alon.content.IceEffects;
import ice.Alon.content.IceItems;
import ice.Alon.world.blocks.factoryBlocks.EffectGenericCrafter;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;

public class FactoryBlocks {
    public static Block integratedFactory, monocrystallineSiliconFactory,mineralProcessor;

    public static void load() {
        /**集成分发器*/
        integratedFactory = new EffectGenericCrafter("integratedFactory") {{
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-top"));
            itemCapacity = 20;
            health = 200;
            outputItem = new ItemStack(IceItems.integratedCircuit, 1);
            consumeItems(ItemStack.with(IceItems.monocrystallineSilicon, 1, IceItems.graphene, 2, IceItems.quartzGlass, 1));
            craftTime = 60;
            craftEffect = new MultiEffect(IceEffects.lancerLaserShoot1, IceEffects.lancerLaserChargeBegin, IceEffects.hitLaserBlast);
            size = 3;
            requirements(Category.crafting, ItemStack.with(IceItems.copperIngot, 19));
        }};
        /**单晶硅厂*/
        monocrystallineSiliconFactory = new GenericCrafter("monocrystallineSiliconFactory") {{
            requirements(Category.crafting, ItemStack.with(IceItems.redIce, 12));
            outputItem = new ItemStack(IceItems.monocrystallineSilicon, 1);
            craftTime = 60f;
            health = 360;
            size = 3;
            hasPower = true;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-rotate", 9, true), new DrawDefault(), new DrawFlame(Color.valueOf("ff9c71")));
            consumeItems(ItemStack.with(Items.pyratite, 1, IceItems.quartz, 3));
            consumePower(1.8f);
        }};
        /**矿物加工机*/
        mineralProcessor = new MultipleCrafter("mineralProcessor") {{
            outputsPower = true;
            requirements(Category.crafting, ItemStack.with(Items.copper, 20));
            size = 3;
            itemCapacity = 30;
            liquidCapacity = 30;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.slag, 2f), new DrawPistons() {{
                sinMag = 2f;
            }}, new DrawRegion(), new DrawLiquidOutputs("-output2", 2) {{
                alpha = 0.7f;
                color = Color.valueOf("9fff9c");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }}, new DrawLiquidOutputs("-output4", 2) {{
                alpha = 0.7f;
                color = Color.valueOf("9fff9c");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }}, new DrawFlame());
            formulas.addsFormulaArray(new Formula() {{
                craftTime = 120;
                craftEffect = Fx.absorb;
                setInput(new Consume[]{new ConsumeItems(ItemStack.with(Items.copper, 1)), new ConsumeLiquid(Liquids.cryofluid, 0.1f), new ConsumePower(0.1f, 0.0f, false)});
                setOutput(ItemStack.with(Items.titanium, 2));
                setOutput(LiquidStack.with(Liquids.water, 0.05f));
            }}, new Formula() {{
                displayName="11111";
                craftEffect = Fx.absorb;
                craftTime = 90;
                setInput(new Consume[]{new ConsumeItems(ItemStack.with(Items.copper, 1)), new ConsumeLiquid(Liquids.oil, 0.1f),});
                setOutput(ItemStack.with(Items.coal, 6));
            }}, new Formula() {{
                craftEffect =  Fx.absorb;
                craftTime = 120;
                setInput(new Consume[]{new ConsumeLiquid(Liquids.slag, 0.1f)});
                setOutput(ItemStack.with(Items.scrap, 6));
            }}, new Formula() {{
                craftEffect = Fx.absorb;
                craftTime = 30;
                setInput(new Consume[]{new ConsumeItemFlammable()});
                setOutput(ItemStack.with(Items.sand, 1));
            }}, new Formula() {{
                craftTime = 30;
                craftEffect = Fx.absorb;
                setInput(new Consume[]{new ConsumeLiquid(Liquids.water, 0.1f),});
                liquidOutputDirections = new int[]{1, 3};
                setOutput(LiquidStack.with(Liquids.slag, 0.05f, Liquids.arkycite, 0.05f));
            }});
        }};
    }
}
