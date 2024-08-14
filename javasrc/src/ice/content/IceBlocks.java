package ice.content;

import arc.graphics.Color;
import ice.asundry.world.IceAttribute;
import ice.world.blocks.effect.fleshAndBloodCoreBlock;
import ice.world.blocks.pumpChamber;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;

public class IceBlocks {
    /**
     * 环境
     */
    public static Block bloodSporophore, bloodSporophoreTree, bloodShoal, redIce, redIceWall, thickBlood, deepThickBlood;
    public static Block pumpChamber, fleshAndBloodhinge;

    public static void load() {
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
        /** 泵腔*/
        pumpChamber = new pumpChamber("pumpChamber") {{
            requirements(Category.liquid, ItemStack.with(IceItems.muscleTendon, 40, IceItems.bonesNap, 10, IceItems.namelessCut, 60));
        }};
        /** 血肉枢纽*/
        fleshAndBloodhinge = new fleshAndBloodCoreBlock("fleshAndBloodhinge") {{
            health = -1;
            size = 4;
            itemCapacity = 6000;
            squareSprite = false;
            requirements(Category.effect, ItemStack.with(IceItems.namelessCut, 2300, IceItems.bonesNap, 2000));
        }};
    }
}
