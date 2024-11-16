package ice.Alon.content.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import ice.Alon.content.IceLiquids;
import ice.Alon.world.meta.IceAttribute;
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
    public static Floor redIce, goldPearlGrit, silverSand, windErodedSand,

    brillianceSlate,

    windErodedGrit, greisen, liparite, nightTideStone;
    /**
     * 墙
     */
    public static StaticWall brillianceSlateWall, silverSandWall, windErodedSandWall, greisenWall, goldPearlGritWall, redIceWall, nightTideStoneWall, lipariteWall;
    /**
     * Prop
     */
    public static Block bloodCrystalSpikes, bloodSporophore, windErodedSandPillar, windErodedSandPillarBig,

    bloodSporophoreTree, edgeBud;
    /**
     * 水地板
     */
    public static Floor bloodShoal, thickBlood, deepThickBlood, nightTideStoneWater;

    public static void load() {
        floor();
        wall();
        liquid();
        prop();
    }

    private static void prop() {
        /** 血晶尖刺*/
        bloodCrystalSpikes = new TallBlock("bloodCrystalSpikes") {{
            variants = 3;
            clipSize = 128f;
        }};
        /**利芽*/
        edgeBud = new TallBlock("edgeBud");
        /**血孢子树*/
        bloodSporophoreTree = new TreeBlock("bloodSporophoreTree") {{
            attributes.set(IceAttribute.bloodSpore, 1);
        }};
        /** 血孢子丛*/
        bloodSporophore = new Prop("bloodSporophore") {{
            hasShadow = true;
            variants = 3;
            breakSound = Sounds.plantBreak;
        }};
        /**风蚀沙柱*/
        windErodedSandPillar = new TallBlock("windErodedSandPillar") {
            {
                hasShadow = true;
                rotationRand = 360;
                variants = 2;
            }

            @Override
            public void drawBase(Tile tile) {
                float rot = Mathf.randomSeedRange(tile.pos() + 1, rotationRand);
                Draw.rect(variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region, tile.worldx(), tile.worldy(), rot);
                Draw.z(Layer.power );
            }
        };
        /**风蚀沙柱2*/
        windErodedSandPillarBig = new TallBlock("windErodedSandPillarBig") {
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
        /**潮汐水石*/
        nightTideStoneWater = new Floor("nightTideStoneWater") {{
            cacheLayer = CacheLayer.water;
            liquidDrop = Liquids.water;
            variants = 3;
        }};
        /**血水池*/
        thickBlood = new Floor("thickBlood") {{
            speedMultiplier = 0.5f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 90f;
            liquidDrop = IceLiquids.thickPlasma;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        /**深血水池 */
        deepThickBlood = new Floor("deepThickBlood") {{
            speedMultiplier = 0.2f;
            variants = 0;
            liquidDrop = IceLiquids.thickPlasma;
            liquidMultiplier = 1.5f;
            isLiquid = true;
            status = StatusEffects.wet;
            statusDuration = 120f;
            drownTime = 200f;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
        /**血浅滩 */
        bloodShoal = new ShallowLiquid("bloodShoal") {{
            mapColor = Color.valueOf("ff656a");
            cacheLayer = CacheLayer.water;
            shallow = true;
            variants = 0;
            itemDrop = Items.sand;
            liquidDrop = IceLiquids.thickPlasma;
            speedMultiplier = 0.8f;
            statusDuration = 50f;
            albedo = 0.9f;
            supportsOverlay = true;
        }};
    }

    private static void floor() {
        /**皎月银沙*/
        silverSand = new Floor("silverSand") {{
            variants = 3;
        }};
        /**光辉板岩*/
        brillianceSlate = new Floor("brillianceSlate") {{
            variants = 3;
        }};
        /**云英岩*/
        greisen = new Floor("greisen") {{
            variants = 3;
        }};
        /**潮汐石*/
        nightTideStone = new Floor("nightTideStone") {{
            variants = 4;
        }};
        /**红冰 */
        redIce = new Floor("redIce") {
            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region};
            }

            {
                variants = 3;
                speedMultiplier = 1.1f;
                albedo = 0.65f;
            }
        };
        /**金珀沙*/
        goldPearlGrit = new Floor("goldPearlGrit") {{
            variants = 3;
        }};
        /**风蚀沙地*/
        windErodedSand = new Floor("windErodedSand") {{
            variants = 3;
        }};
        /**风蚀砂地*/
        windErodedGrit = new Floor("windErodedGrit") {{
            variants = 3;
        }};
        /**流纹岩*/
        liparite = new Floor("liparite") {{
            variants = 3;
        }};
    }

    private static void wall() {
        /**皎月银沙墙*/
        silverSandWall = new StaticWall("silverSandWall") {{
            silverSand.asFloor().wall = this;
        }};
        /**光辉板岩墙*/
        brillianceSlateWall = new StaticWall("brillianceSlateWall") {{
            brillianceSlate.asFloor().wall = this;
        }};
        /**云英岩墙*/
        greisenWall = new StaticWall("greisenWall") {{
            greisen.asFloor().wall = this;
        }};
        /**潮汐石墙*/
        nightTideStoneWall = new StaticWall("nightTideStoneWall") {{
            nightTideStone.asFloor().wall = this;
        }};
        /**流纹岩墙*/
        lipariteWall = new StaticWall("lipariteWall") {{
            liparite.asFloor().wall = this;
        }};
        /**风蚀沙墙*/
        windErodedSandWall = new StaticWall("windErodedSandWall") {{
            windErodedSand.asFloor().wall = this;
        }};
        /**金珀沙墙*/
        goldPearlGritWall = new StaticWall("goldPearlGritWall") {{
            goldPearlGrit.asFloor().wall = this;
        }};
        /**红冰墙 */
        redIceWall = new StaticWall("redIceWall") {{
            redIce.asFloor().wall = this;
        }};
    }
}
