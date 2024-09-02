package ice.Alon.asundry.Content;


import arc.graphics.Color;
import ice.Alon.asundry.world.IceAttribute;
import ice.Alon.asundry.world.bullet.IceMissileBulletTypes;
import ice.Alon.asundry.world.bullet.ThickLightningBulletType;
import ice.Alon.asundry.world.content.block.*;
import ice.Alon.asundry.world.content.block.turret.FindTargetTurret;
import ice.Alon.asundry.world.draw.IceDrawLiquidOutputs;
import ice.Alon.asundry.world.draw.IceDrawPistons;
import ice.Alon.asundry.world.effect.MultipleCrafterRadialEffect;
import ice.Alon.content.items.IceItems;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;

public class IceBlocks {
    public static Block k1, k2, iceRedTree, bloodCrystalSpikes, redSandWater, thickBlood,

    deepThickBlood, oreIceCrystals, taijing, tarnation, randomer,

    mineralProcessor, paota1, tidalImpactReactor,

    monocrystallineSiliconFactory, k3, quantumCore;

    public static void load() {
        k3 = new Conveyor("k3") {{
            buildVisibility = BuildVisibility.shown;
            speed = 0.03f;
            requirements(Category.crafting, ItemStack.with(Items.copper, 1));
            junctionReplacement = Blocks.copperWall;
        }};
        k2 = new UnitCoreBlock("k2") {{
            buildVisibility = BuildVisibility.shown;
            size = 3;
            health = 3;
        }};
        k1 = new WallCrafter("2") {{
            requirements(Category.production, ItemStack.with(Items.graphite, 25, Items.beryllium, 20));
            consumePower(11 / 60f);
            drillTime = 110f;
            size = 2;
            attribute = IceAttribute.bloodSpore;
            output = IceItems.bloodSpore;
            fogRadius = 2;
            ambientSound = Sounds.drill;
            ambientSoundVolume = 0.04f;
        }};
        oreIceCrystals = new OreBlock("ore-src.Ice-Crystals", IceItems.iceCrystals) {{
            playerUnmineable = false;/**玩家能否挖掘 */
            mapColor = Color.white;/**地图颜色 */
            wallOre = false;/**墙矿？ */
            useColor = true;/**颜色用于小地图 */
            oreDefault = true;
            oreThreshold = 0.8f;
            oreScale = 20;
            variants = 3;/**贴图数量 */
        }};
        taijing = new Conveyor("钛晶传送带") {{
            speed = 13;
            buildVisibility = BuildVisibility.shown;
            health = 50;
        }};
        monocrystallineSiliconFactory = new GenericCrafter("monocrystallineSiliconFactory") {{
            requirements(Category.crafting, ItemStack.with(IceItems.redIce, 12));
            outputItem = new ItemStack(IceItems.monocrystallineSilicon, 1);
            craftTime = 36f;
            craftEffect = Ix.gc1;
            health = 360;
            size = 3;
            hasPower = true;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-rotate", 9, true), new DrawDefault(), new DrawFlame(Color.valueOf("ff9c71")));
            consumeItems(ItemStack.with(Items.pyratite, 1, IceItems.quartz, 3));
            consumePower(1.8f);
        }};
        tidalImpactReactor = new EffectImpactReactor("tidalImpactReactor") {{
            requirements(Category.power, ItemStack.with(Items.lead, 4590, Items.copper, 4170, Items.thorium, 175, Items.graphite, 716, Items.plastanium, 120, Items.titanium, 510, Items.metaglass, 1400, Items.silicon, 1616));
            size = 4;
            health = 1000;
            hasLiquids = false;
            powerProduction = 254;
            refreshEffect = 30;
            effect = Ix.hj;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPlasma(), new DrawDefault());
            buildCostMultiplier = 0.0584795321f;
            requiresWater = true;
            ambientSound = Sounds.pulse;
            ambientSoundVolume = 0.07f;
            consumePower(63.1666666f);
        }};

        quantumCore = new PowerProductionCore("quantumCore") {{
            size = 3;
            health = 2500;
            itemCapacity = 4500;
            consumesPower = false;
            hasPower = true;
            outputsPower = true;
            powerProduction = 2;
            requirements(Category.effect, ItemStack.with(IceItems.redIce, 10));
            buildVisibility = BuildVisibility.shown;
        }};
        tarnation = new PowerTurret("tarnation") {{
            requirements(Category.turret, ItemStack.with(Items.lead, 6000));
            range = 540f;
            shoot.firstShotDelay = 130f;
            recoil = 9f;
            reload = 60;
            shake = 6f;
            shootEffect = Ix.tarnationShoot;
            smokeEffect = Fx.none;
            heatColor = Color.red;
            size = 6;
            health = 280 * size * size;
            targetAir = true;
            shootSound = Sounds.plasmadrop;
            rotateSpeed = 2f;
            unitSort = (u, x, y) -> -u.maxHealth;
            consumePower(60f);
            shootType = new ThickLightningBulletType(3048, /**Pal.lancerLaser*/Color.valueOf("a9d8ff")) {{
                buildingDamageMultiplier = 0.3f;

                //TODO:这只会发出一种tarnationLine效果，这绝对应该改变，但我对未经测试就改变效果持谨慎态度。-Anuke
                //蓄力漩涡特效 chargeEffect = new MultiEffect(IceEffects.tarnationCharge, IceEffects.tarnationLines);
            }};
        }};
        paota1 = new FindTargetTurret("8") {{
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
            requirements(Category.turret, ItemStack.with(Items.copper, 350));
            ammo(IceItems.redIce, IceMissileBulletTypes.lj, IceItems.iceCrystals);
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

        mineralProcessor = new MultipleCrafter("mineralProcessor") {{
            outputsPower = true;
            requirements(Category.crafting, ItemStack.with(Items.copper, 20));
            size = 3;
            itemCapacity = 30;
            liquidCapacity = 30;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(Liquids.slag, 2f), new DrawPistons() {{
                sinMag = 2f;
            }}, new IceDrawPistons() {{
                sides = 2;
                lenOffset = -4;
            }}, new DrawRegion(), new IceDrawLiquidOutputs("-output2", 2) {{
                alpha = 0.7f;
                color = Color.valueOf("9fff9c");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }}, new IceDrawLiquidOutputs("-output4", 2) {{
                alpha = 0.7f;
                color = Color.valueOf("9fff9c");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }}, new DrawFlame());
            formulas.addsFormulaArray(new Formula() {{
                craftTime = 120;
                craftEffect = new MultipleCrafterRadialEffect(craftTime);
                setInput(new Consume[]{new ConsumeItems(ItemStack.with(Items.copper, 1)), new ConsumeLiquid(Liquids.cryofluid, 0.1f), new ConsumePower(0.1f, 0.0f, false)});
                setOutput(ItemStack.with(Items.titanium, 2));
                setOutput(LiquidStack.with(Liquids.water, 0.05f));
            }}, new Formula() {{
                craftEffect = new MultipleCrafterRadialEffect(craftTime);
                craftTime = 90;
                setInput(new Consume[]{new ConsumeItems(ItemStack.with(Items.copper, 1)), new ConsumeLiquid(Liquids.oil, 0.1f),});
                setOutput(ItemStack.with(Items.coal, 6));
            }}, new Formula() {{
                craftEffect = new MultipleCrafterRadialEffect(craftTime);
                craftTime = 120;
                setInput(new Consume[]{new ConsumeLiquid(Liquids.slag, 0.1f)});
                setOutput(ItemStack.with(Items.scrap, 6));
            }}, new Formula() {{
                craftEffect = new MultipleCrafterRadialEffect(craftTime);
                craftTime = 30;
                setInput(new Consume[]{new ConsumeItemFlammable()});
                setOutput(ItemStack.with(Items.sand, 1));
            }}, new Formula() {{
                craftTime = 30;
                craftEffect = new MultipleCrafterRadialEffect(craftTime);
                setInput(new Consume[]{new ConsumeLiquid(Liquids.water, 0.1f),});
                liquidOutputDirections = new int[]{1, 3};
                setOutput(LiquidStack.with(Liquids.slag, 0.05f, Liquids.arkycite, 0.05f));
            }});
        }};

    }
}
