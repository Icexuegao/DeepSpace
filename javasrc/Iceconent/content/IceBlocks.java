package Iceconent.content;

import Iceconent.World.Block.chaixiehexin;
import Iceconent.World.Block.haodiancnagku;
import Iceconent.World.Block.wakuangpaota;
import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawFlame;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.BlockGroup;

import static mindustry.type.ItemStack.with;

public class IceBlocks {
    /**
     * 60/(挖掘时间+50*矿物硬度)*钻头大小的平方  等于钻头速度
     */
    public static Block container, zhanshuhexin, tidalImpactReactor, monocrystallineSiliconFactory, electronicUninstaller, drillMiniv;

    public static void load() {

        monocrystallineSiliconFactory = new GenericCrafter("monocrystalline-Silicon-Factory") {{
            requirements(Category.crafting, ItemStack.with(Items.copper, 60, Items.lead, 45, Items.silicon, 50, Items.graphite, 20));
            outputItem = new ItemStack(IceItems.monocrystallineSilicon, 1);
            craftTime = 36f;
            craftEffect = IceEffect.gc1;
            health = 260;
            size = 3;
            hasPower = true;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-rotate", 9, true), new DrawDefault(), new DrawFlame(Color.valueOf("ff9c71")));
            consumeItems(ItemStack.with(Items.pyratite, 1, Items.silicon, 2));
            consumePower(1.8f);
        }};
        tidalImpactReactor = new ImpactReactor("tidal-ImpactReactor") {{
            requirements(Category.power, ItemStack.with(Items.lead, 4590, Items.copper, 4170, Items.thorium, 175, Items.graphite, 716, Items.plastanium, 120, Items.titanium, 510, Items.metaglass, 1400, Items.silicon, 1616));
            size = 4;
            health = 1000;
            hasLiquids = false;
            powerProduction = 254;
            buildCostMultiplier = 0.0584795321f;
            requiresWater = true;
            ambientSound = Sounds.pulse;
            ambientSoundVolume = 0.07f;
            consumePower(63.1666666f);
        }};
        electronicUninstaller = new Unloader("electronic-Uninstaller") {{
            requirements(Category.effect, ItemStack.with(Items.titanium, 15, IceItems.redIce, 10, IceItems.monocrystallineSilicon, 5));
            speed = 1.7142f;
            health = 100;
            size = 1;
            itemCapacity = 10;
            group = BlockGroup.transportation;
        }};
        drillMiniv = new wakuangpaota("drillpaota") {{
            range = 50f;
            itemCapacity = 25;
            hasPower = true;
            consumePower(1f);
            requirements(Category.production, with(Items.copper, 30, Items.graphite, 25, Items.titanium, 10, Items.silicon, 15));
        }};
        zhanshuhexin = new chaixiehexin("zhanshuhexin") {{
            update = true;
            /**update默认不开启,墙 */
            itemCapacity = 800;/** 容量  */
            unitType = UnitTypes.alpha;
            health = 1000;
            size = 3;/** 大小  */
            range = 120;/**  范围 */
            craftTime = 10f;/** 生产时间  */
            outputItem = new ItemStack(IceItems.redIce, 1);
            hasPower = true; /** 核心不能同时发电和用电  */
            PowerProduction = 6;/** 发电  */
            consumesPower = false;/** 消耗电力就是用电和存电  */
            outputsPower = true;/** 发电  */
            unitCapModifier = 1;/**  增加单位上线增加 */
            armor = 5;/** 护甲  */
            alwaysReplace = true;/** 允许替换？  */
            requirements(Category.effect, with(Items.copper, 100));/** 在哪个建造栏以及消耗物品  */
        }};
        container = new haodiancnagku("haodiancontainer") {{
            requirements(Category.effect, with(Items.titanium, 100));
            size = 2;
            itemCapacity = 300;
            scaledHealth = 55;
            consumePower(1f);
            consumesPower = true;
            coreMerge = true;
        }};

    }
}
