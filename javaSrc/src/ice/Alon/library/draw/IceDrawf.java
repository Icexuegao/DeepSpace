package ice.Alon.library.draw;

import arc.Core;
import arc.graphics.g2d.NinePatch;
import arc.graphics.g2d.TextureAtlas;
import arc.scene.style.Drawable;
import arc.scene.style.ScaledNinePatchDrawable;
import mindustry.world.draw.DrawLiquidTile;

/**
 * Draw使用的工具类
 */
public class IceDrawf {
    /**
     * 设置DrawLiquidTile的tile的大小
     */
    public static DrawLiquidTile setLiquidTileSize(DrawLiquidTile tile, float padLeft, float padRight, float padTop, float padBottom) {
        tile.padLeft = padLeft;
        tile.padRight = padRight;
        tile.padBottom = padBottom;
        tile.padTop = padTop;
        return tile;
    }
    /**分割九宫图*/
    public static Drawable createFlatDown(String name) {
        TextureAtlas.AtlasRegion region = Core.atlas.find(name);
        int[] splits = region.splits;
        ScaledNinePatchDrawable copy = new ScaledNinePatchDrawable(new NinePatch(region, splits[0], splits[1], splits[2], splits[3])) {
            public float getLeftWidth() {
                return 0;
            }
            public float getRightWidth() {
                return 0;
            }

            public float getTopHeight() {
                return 0;
            }

            public float getBottomHeight() {
                return 0;
            }
        };
        copy.setMinWidth(0);
        copy.setMinHeight(0);
        copy.setTopHeight(0);
        copy.setRightWidth(0);
        copy.setBottomHeight(0);
        copy.setLeftWidth(0);
        return copy;
    }
}
