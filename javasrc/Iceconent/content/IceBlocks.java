package Iceconent.content;

import Iceconent.World.Block.chaixiehexin;
import Iceconent.World.Block.haodiancnagku;
import Iceconent.World.Block.wakuangpaota;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootSummon;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.draw.*;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.type.ItemStack.with;

public class IceBlocks {
    /**
     * 60/(挖掘时间+50*矿物硬度)*钻头大小的平方  等于钻头速度
     */
    public static Block maligns, container, zhanshuhexin, chaoxichongjidui, guijinglianchang, dianzixiezaiqi, drillMiniv;

    public static void load() {

        guijinglianchang = new GenericCrafter("gjlc") {{
            requirements(Category.crafting, ItemStack.with(Items.copper, 60, Items.lead, 45, Items.silicon, 50, Items.graphite, 20));
            outputItem = new ItemStack(IceItems.monocrystallineSilicon, 1);
            craftTime = 36f;
            craftEffect = IceEffect.gc1;
            health = 260;
            size = 3;
            hasPower = true;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-z", 9, true), new DrawDefault(), new DrawFlame(Color.valueOf("ff9c71")));
            consumeItems(ItemStack.with(Items.pyratite, 1, Items.silicon, 2));
            consumePower(1.8f);
        }};
        chaoxichongjidui = new ImpactReactor("cxcjd") {{
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
        dianzixiezaiqi = new Unloader("dzxzq") {{
            requirements(Category.effect, ItemStack.with(Items.titanium, 15, IceItems.redIce, 10, IceItems.monocrystallineSilicon, 5));
            speed = 1.0f;
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
        maligns = new PowerTurret("mmma") {{
            requirements(Category.turret, with(Items.carbide, 400, Items.beryllium, 2000, Items.silicon, 800, Items.graphite, 800, Items.phaseFabric, 300));


            Color haloColor = Color.valueOf("d370d3"), heatCol = Color.purple;
            float haloY = -15f, haloRotSpeed = 1.5f;


            float circleY = 25f, circleRad = 11f, circleRotSpeed = 3.5f, circleStroke = 1.6f;

            shootSound = Sounds.malignShoot;
            loopSound = Sounds.spellLoop;
            loopSoundVolume = 1.3f;

            shootType = new FlakBulletType(8f, 70f) {{
                sprite = "missile-large";

                lifetime = 45f;
                width = 12f;
                height = 22f;

                hitSize = 7f;
                shootEffect = Fx.shootSmokeSquareBig;
                smokeEffect = Fx.shootSmokeDisperse;
                ammoMultiplier = 1;
                hitColor = backColor = trailColor = lightningColor = haloColor;
                frontColor = Color.white;
                trailWidth = 3f;
                trailLength = 12;
                hitEffect = despawnEffect = Fx.hitBulletColor;
                buildingDamageMultiplier = 0.3f;

                trailEffect = Fx.colorSpark;
                trailRotation = true;
                trailInterval = 3f;
                lightning = 1;
                lightningCone = 15f;
                lightningLength = 20;
                lightningLengthRand = 30;
                lightningDamage = 20f;

                homingPower = 0.17f;
                homingDelay = 19f;
                homingRange = 160f;

                explodeRange = 160f;
                explodeDelay = 0f;

                flakInterval = 20f;
                despawnShake = 3f;

                fragBullet = new LaserBulletType(65f) {{
                    colors = new Color[]{haloColor.cpy().a(0.4f), haloColor, Color.white};
                    buildingDamageMultiplier = 0.25f;
                    width = 19f;
                    hitEffect = Fx.hitLancer;
                    sideAngle = 175f;
                    sideWidth = 1f;
                    sideLength = 40f;
                    lifetime = 22f;
                    drawSize = 400f;
                    length = 180f;
                    pierceCap = 2;
                }};

                fragSpread = fragRandomSpread = 0f;

                splashDamage = 0f;
                hitEffect = Fx.hitSquaresColor;
                collidesGround = true;
            }};


            drawer = new DrawTurret("reinforced-") {{
                parts.addAll(

                        //summoning circle
                        new ShapePart() {{

                            color = haloColor;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = circleStroke;
                            radius = circleRad;
                            layer = Layer.effect;
                            y = circleY;
                        }},

                        new ShapePart() {{
                            rotateSpeed = -circleRotSpeed;
                            color = haloColor;
                            sides = 4;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = circleStroke;
                            radius = circleRad - 1f;
                            layer = Layer.effect;
                            y = circleY;
                        }},

                        //outer squares

                        new ShapePart() {{
                            rotateSpeed = -circleRotSpeed;
                            color = haloColor;
                            sides = 4;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = circleStroke;
                            radius = circleRad - 1f;
                            layer = Layer.effect;
                            y = circleY;
                        }},

                        //inner square
                        new ShapePart() {{

                            rotateSpeed = -circleRotSpeed / 2f;
                            color = haloColor;
                            sides = 4;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 3f;
                            layer = Layer.effect;
                            y = circleY;
                        }},

                        //spikes on circle
                        new HaloPart() {{

                            color = haloColor;
                            tri = true;
                            shapes = 3;
                            triLength = 0f;
                            triLengthTo = 5f;
                            radius = 6f;
                            haloRadius = circleRad;
                            haloRotateSpeed = haloRotSpeed / 2f;
                            shapeRotation = 180f;
                            haloRotation = 180f;
                            layer = Layer.effect;
                            y = circleY;
                        }},

                        //actual turret
                        new RegionPart("-mouth") {{
                            heatColor = heatCol;
                            heatProgress = PartProgress.warmup;

                            moveY = -8f;
                        }}, new RegionPart("-end") {{
                            moveY = 0f;
                        }},

                        new RegionPart("-front") {{
                            heatColor = heatCol;
                            heatProgress = PartProgress.warmup;

                            mirror = true;
                            moveRot = 33f;
                            moveY = -4f;
                            moveX = 10f;
                        }}, new RegionPart("-back") {{
                            heatColor = heatCol;
                            heatProgress = PartProgress.warmup;

                            mirror = true;
                            moveRot = 10f;
                            moveX = 2f;
                            moveY = 5f;
                        }},

                        new RegionPart("-mid") {{
                            heatColor = heatCol;
                            heatProgress = PartProgress.recoil;

                            moveY = -9.5f;
                        }},

                        new ShapePart() {{

                            color = haloColor;
                            circle = true;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 10f;
                            layer = Layer.effect;
                            y = haloY;
                        }}, new ShapePart() {{

                            color = haloColor;
                            sides = 3;
                            rotation = 90f;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 4f;
                            layer = Layer.effect;
                            y = haloY;
                        }}, new HaloPart() {{

                            color = haloColor;
                            sides = 3;
                            shapes = 3;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 2f;
                            radius = 3f;
                            haloRadius = 10f + radius / 2f;
                            haloRotateSpeed = haloRotSpeed;
                            layer = Layer.effect;
                            y = haloY;
                        }},

                        new HaloPart() {{

                            color = haloColor;
                            tri = true;
                            shapes = 3;
                            triLength = 0f;
                            triLengthTo = 10f;
                            radius = 6f;
                            haloRadius = 16f;
                            haloRotation = 180f;
                            layer = Layer.effect;
                            y = haloY;
                        }}, new HaloPart() {{

                            color = haloColor;
                            tri = true;
                            shapes = 3;
                            triLength = 0f;
                            triLengthTo = 3f;
                            radius = 6f;
                            haloRadius = 16f;
                            shapeRotation = 180f;
                            haloRotation = 180f;
                            layer = Layer.effect;
                            y = haloY;
                        }},

                        new HaloPart() {{

                            color = haloColor;
                            sides = 3;
                            tri = true;
                            shapes = 3;
                            triLength = 0f;
                            triLengthTo = 10f;
                            shapeRotation = 180f;
                            radius = 6f;
                            haloRadius = 16f;
                            haloRotateSpeed = -haloRotSpeed;
                            haloRotation = 180f / 3f;
                            layer = Layer.effect;
                            y = haloY;
                        }},

                        new HaloPart() {{

                            color = haloColor;
                            sides = 3;
                            tri = true;
                            shapes = 3;
                            triLength = 0f;
                            triLengthTo = 4f;
                            radius = 6f;
                            haloRadius = 16f;
                            haloRotateSpeed = -haloRotSpeed;
                            haloRotation = 180f / 3f;
                            layer = Layer.effect;
                            y = haloY;
                        }});
                Color heatCol2 = heatCol.cpy().add(0.1f, 0.1f, 0.1f).mul(1.2f);
                for (int i = 1; i < 4; i++) {
                    int fi = i;
                    parts.add(new RegionPart("-spine") {{
                        outline = false;
                        progress = PartProgress.warmup.delay(fi / 5f);
                        heatProgress = PartProgress.warmup.add(p -> (Mathf.absin(3f, 0.2f) - 0.2f) * p.warmup);
                        mirror = true;
                        under = true;
                        layerOffset = -0.3f;
                        turretHeatLayer = Layer.turret - 0.2f;
                        moveY = 9f;
                        moveX = 1f + fi * 4f;
                        moveRot = fi * 60f - 130f;
                        color = Color.valueOf("bb68c3");
                        heatColor = heatCol2;
                        moves.add(new PartMove(PartProgress.recoil.delay(fi / 5f), 1f, 0f, 3f));
                    }});
                }
            }};
            velocityRnd = 0.15f;
            maxHeatEfficiency = 2f;
            warmupMaintainTime = 30f;
            shoot = new ShootSummon(0f, 0f, circleRad, 48f);
            minWarmup = 0.96f;
            shootWarmupSpeed = 0.03f;
            shootY = circleY - 5f;
            outlineColor = Pal.darkOutline;
            envEnabled |= Env.space;
            reload = 2f;
            range = 30000f;
            shootCone = 100f;
            scaledHealth = 370;
            rotateSpeed = 2f;
            recoil = 0.5f;
            recoilTime = 30f;
            shake = 3f;
        }};
    }
}
