package ice.asundry.BaseTool.codebase;

import mindustry.world.draw.DrawLiquidTile;

public class DrawTools {
    public static DrawLiquidTile setLiquidTileSize(DrawLiquidTile tile, float padLeft, float padRight, float padTop, float padBottom) {
        tile.padLeft = padLeft;
        tile.padRight = padRight;
        tile.padBottom = padBottom;
        tile.padTop = padTop;
        return tile;
    }
}
