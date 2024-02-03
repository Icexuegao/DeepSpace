package Iceconent.content;

import Iceconent.World.Block.power.PowerProductionCoreBlock;
import Iceconent.World.Block.chaixiehexin;
import Iceconent.World.Block.power.IceImpactReactor;
import Iceconent.World.Bullet.IceBasicBulletTypes;
import Iceconent.World.Bullet.IceMissileBulletTypes;
import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.*;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class IceBlocks {

    public static Block paota1, tidalImpactReactor, monocrystallineSiliconFactory, electronicUninstaller, zhanshuhexin, quantumCore;

    public static void load() {

        monocrystallineSiliconFactory = new GenericCrafter("monocrystalline-Silicon-Factory") {{
            requirements(Category.crafting, ItemStack.with(Items.copper, 60, Items.lead, 45, Items.silicon, 50, Items.graphite, 20));
            outputItem = new ItemStack(IceItems.monocrystallineSilicon, 1);
            craftTime = 36f;
            craftEffect = IceEffects.gc1;
            health = 360;
            size = 3;
            hasPower = true;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-rotate", 9, true), new DrawDefault(), new DrawFlame(Color.valueOf("ff9c71")));
            consumeItems(ItemStack.with(Items.pyratite, 1, IceItems.quartz, 3));
            consumePower(1.8f);
        }};
        tidalImpactReactor = new IceImpactReactor("tidal-Impact-Reactor") {{
            requirements(Category.power, ItemStack.with(Items.lead, 4590, Items.copper, 4170, Items.thorium, 175, Items.graphite, 716, Items.plastanium, 120, Items.titanium, 510, Items.metaglass, 1400, Items.silicon, 1616));
            size = 4;
            health = 1000;
            hasLiquids = false;
            powerProduction = 254;
            refreshEffect = 30;
            effect = IceEffects.hj;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPlasma(), new DrawDefault());
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
        quantumCore = new PowerProductionCoreBlock("quantum-Core") {{
            size = 3;
            health = 2500;
            itemCapacity = 4500;

            hasPower = true;
            outputsPower = true;
            powerProduction = 2;

            requirements(Category.effect, ItemStack.with(Items.copper, 10));
            buildVisibility = BuildVisibility.shown;
        }};
        paota1 = new ItemTurret("8") {{
            health = 5100;
            range = 600f;
            recoil = 4.6f;/** 后坐力  */
            size = 5;
            reload = 15f;
            shootY = 15f;
            rotateSpeed = 3.5f;
            targetGround = false;/** 攻击地面  */
            inaccuracy = 2;/** 精准度越小越准。  */
            shootWarmupSpeed = 0.03f;/** 动画块移动速度 */
            outlineColor = Pal.darkOutline;/** 贴图轮廓颜色。   */
            coolant = consume(new ConsumeLiquid(Liquids.water, 25f / 60f));/** 冷却剂  */
            coolantMultiplier = 2f;/** 每单位液体热容减少多少重载。*/
            shootCone = 40f;/** 瞄准射击是否允许有误差,如果和瞄准的物体相差shootCone度也可以进行射击。  */
            consumeAmmoOnce = true;/** 消耗弹药  */
            shootSound = Sounds.missile;
            requirements(Category.turret, with(Items.copper, 350));
            ammo(IceItems.iceCrystals, IceMissileBulletTypes.lj);
            drawer = new DrawTurret("reinforced-") {{
                parts.add(new RegionPart("-g-r") {{
                    heatProgress = PartProgress.recoil.add(0.25f).min(PartProgress.warmup);
                    heatColor = Color.valueOf("ff7171");
                    x = 2;
                    y = -2;
                    under = false;
                    moveX = -2f;
                    moveY = 2;
                }}, new RegionPart("-h") {{
                    heatProgress = PartProgress.recoil.add(0.25f).min(PartProgress.warmup);
                    heatColor = Color.valueOf("ff7171");
                    y = -1;
                    moveY = 1;
                }}, new RegionPart("-r") {{
                    heatProgress = PartProgress.recoil.add(0.25f).min(PartProgress.warmup);
                    heatColor = Color.valueOf("ff7171");
                    x = -2;
                    y = -2;
                    under = true;
                    moveX = 2;
                    moveY = 2;
                }});
            }};
            shoot = new ShootBarrel() {{
                shotDelay = 4;
                shots = 3;
                barrels = new float[]{-12, 3, 0, 0, 4, 0, 12, 3, 0};
            }};
        }};
        zhanshuhexin = new chaixiehexin("zhanshuhexin") {{
            destroyBullet = IceBasicBulletTypes.basicBulletType1;
            update = true;
            /**update默认不开启,墙 */
            itemCapacity = 800;/** 容量  */
            unitType = UnitTypes.alpha;
            health = 1;
            size = 3;/** 大小  */
            range = 80;/**  范围 */
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

    }
}
