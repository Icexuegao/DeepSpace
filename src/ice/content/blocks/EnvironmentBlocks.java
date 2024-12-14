package ice.content.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import ice.content.IceLiquids;
import ice.world.blocks.environment.IceFloor;
import ice.world.blocks.environment.IceStaticWall;
import ice.world.meta.IceAttribute;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.*;

public class EnvironmentBlocks {
    /**
     * 地板
     */
    public static Floor 红冰, 金珀沙, 皎月银沙, 风蚀沙地, 光辉板岩, 风蚀砂地, 云英岩, 流纹岩, 潮汐石, 侵蚀层地;
    /**
     * 墙
     */
    public static StaticWall 光辉板岩墙, 侵蚀层地墙, 皎月银沙墙, 风蚀沙墙, 云英岩墙, 金珀沙墙, 红冰墙, 潮汐石墙, 流纹岩墙;
    /**
     * Prop
     */
    public static Block 血晶尖刺, 血孢子丛, 风蚀沙柱, 大风蚀沙柱, 血孢子树, 利芽;
    /**
     * 水地板
     */
    public static Floor 血浅滩, 血池, 深血池, 潮汐水石;

    public static void load() {
        floor();
        wall();
        liquid();
        prop();
    }

    private static void prop() {
        血晶尖刺 = new TallBlock("bloodCrystalSpikes") {{
            variants = 3;
            clipSize = 128f;
        }};
        利芽 = new TallBlock("edgeBud");
        血孢子树 = new TreeBlock("bloodSporophoreTree") {{
            attributes.set(IceAttribute.bloodSpore, 1);
        }};
        血孢子丛 = new Prop("bloodSporophore") {{
            hasShadow = true;
            variants = 3;
            breakSound = Sounds.plantBreak;
        }};
        风蚀沙柱 = new TallBlock("windErodedSandPillar") {
            {
                hasShadow = true;
                rotationRand = 360;
                variants = 2;
            }

            @Override
            public void drawBase(Tile tile) {
                float rot = Mathf.randomSeedRange(tile.pos() + 1, rotationRand);
                Draw.rect(variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region, tile.worldx(), tile.worldy(), rot);
                Draw.z(Layer.power);
            }
        };
        大风蚀沙柱 = new TallBlock("windErodedSandPillarBig") {
            {
                hasShadow = true;
                size = 2;
                variants = 1;
                rotationRand = 45;
            }

            @Override
            public void drawBase(Tile tile) {
                int i1 = tile.x + 1;
                int i2 = tile.y + 1;
                Tile t1 = Vars.world.tile(i1, tile.y);
                Tile t2 = Vars.world.tile(i1, i2);
                Tile t3 = Vars.world.tile(tile.x, tile.y);
                if (t1.block() == t2.block() && t2.block() == t3.block()) {
                    float rot = Mathf.randomSeedRange(tile.pos() + 1, rotationRand);
                    Draw.z(Layer.power + 1);
                    Draw.rect(variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region, tile.worldx() + 4, tile.worldy() + 4, rot);
                }
            }
        };
    }

    private static void liquid() {
        潮汐水石 = new IceFloor("nightTideStoneWater", 3) {{
            cacheLayer = CacheLayer.water;
            liquidDrop = Liquids.water;
        }};
        血池 = new IceFloor("thickBlood", 0) {{
            speedMultiplier = 0.5f;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = IceLiquids.血浆;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        深血池 = new IceFloor("deepThickBlood", 0) {{
            speedMultiplier = 0.2f;
            liquidDrop = IceLiquids.血浆;
            liquidMultiplier = 1.5f;
            isLiquid = true;
            status = StatusEffects.wet;
            statusDuration = 120f;
            drownTime = 200f;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        血浅滩 = new ShallowLiquid("bloodShoal") {{
            mapColor = Color.valueOf("ff656a");
            cacheLayer = CacheLayer.water;
            shallow = true;
            variants = 0;
            itemDrop = Items.sand;
            liquidDrop = IceLiquids.血浆;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
    }

    private static void floor() {
        侵蚀层地 = new IceFloor("erosionalSlate", 3);
        皎月银沙 = new IceFloor("silverSand", 3);
        光辉板岩 = new IceFloor("brillianceSlate", 3);
        云英岩 = new IceFloor("greisen", 3);
        潮汐石 = new IceFloor("nightTideStone", 4);
        红冰 = new IceFloor("redIce", 3);
        金珀沙 = new IceFloor("goldPearlGrit", 3);
        风蚀沙地 = new IceFloor("windErodedSand", 3);
        风蚀砂地 = new IceFloor("windErodedGrit", 3);
        流纹岩 = new IceFloor("liparite", 3);
    }

    private static void wall() {
        侵蚀层地墙 = new IceStaticWall("erosionalSlateWall");
        皎月银沙墙 = new IceStaticWall("silverSandWall");
        光辉板岩墙 = new IceStaticWall("brillianceSlateWall");
        云英岩墙 = new IceStaticWall("greisenWall");
        潮汐石墙 = new IceStaticWall("nightTideStoneWall");
        流纹岩墙 = new IceStaticWall("lipariteWall");
        风蚀沙墙 = new IceStaticWall("windErodedSandWall");
        金珀沙墙 = new IceStaticWall("goldPearlGritWall");
        红冰墙 = new IceStaticWall("redIceWall");
    }
}
