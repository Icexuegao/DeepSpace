package ice.Alon.library;

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
}
