package ice.Alon.content.blocks;

import arc.graphics.Color;
import ice.Alon.asundry.world.IceAttribute;
import ice.Alon.content.IceLiquids;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;

public class EnvironmentBlocks {
    /**
     * 环境
     */
    public static Block bloodCrystalSpikes, bloodSporophore, bloodSporophoreTree, bloodShoal, redIce, redIceWall, thickBlood, deepThickBlood;

    public static void load() {
        /** 血晶尖刺*/
        bloodCrystalSpikes = new TallBlock("bloodCrystalSpikes") {{
            variants = 3;
            clipSize = 128f;
        }};
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
        /**红冰 */
        redIce = new Floor("redIce") {{
            dragMultiplier = 0.35f;
            speedMultiplier = 0.9f;
            albedo = 0.65f;
        }};
        /**红冰墙 */
        redIceWall = new StaticWall("redIceWall") {{
            redIce.asFloor().wall = this;
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
}
