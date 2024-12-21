package ice.library.drawf;

import arc.Core;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.NinePatch;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.style.ScaledNinePatchDrawable;
import ice.Ice;
import ice.library.file.IceFiles;
import mindustry.world.draw.DrawLiquidTile;

import static arc.util.Tmp.v1;

/**
 * Draw使用的工具类
 */
public class IceDraw {
    /** 绘制⚪ */
    public static void arc(float x, float y, float radius, float scaleFactor, float innerAngel, float rotate) {
        int sides = 40 + (int) (radius * scaleFactor);

        float step = (float) 360 / sides;
        int sing = innerAngel > 0 ? 1 : -1;
        innerAngel = Math.min(Math.abs(innerAngel), 360f);

        Lines.beginLine();

        float overed = 0;
        for (float ang = 0; ang <= innerAngel - step; ang += step) {
            overed += step;
            v1.set(radius, 0).setAngle(ang * sing + rotate);
            Lines.linePoint(x + v1.x, y + v1.y);
        }

        if (innerAngel >= 360 - 0.01f) {
            Lines.endLine(true);
            return;
        }

        if (overed < innerAngel) {
            v1.set(radius, 0).setAngle(innerAngel * sing + rotate);
            Lines.linePoint(x + v1.x, y + v1.y);
        }
        Lines.endLine();
    }

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

    /** 分割九宫图 */
    public static Drawable create9(String name) {
        TextureAtlas.AtlasRegion region = Core.atlas.find(Ice.NAME + "-" + name);
        ScaledNinePatchDrawable copy = getScaledNinePatchDrawable(region);
        copy.setMinWidth(0);
        copy.setMinHeight(0);
        copy.setTopHeight(0);
        copy.setRightWidth(0);
        copy.setBottomHeight(0);
        copy.setLeftWidth(0);
        return copy;
    }

    private static ScaledNinePatchDrawable getScaledNinePatchDrawable(TextureAtlas.AtlasRegion region) {
        int[] splits = region.splits;
        return new ScaledNinePatchDrawable(new NinePatch(region, splits[0], splits[1], splits[2], splits[3])) {
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
    }

    public static TextureRegion LoadText(String name) {
        return new TextureAtlas.AtlasRegion(IceFiles.findPng(name.replace(Ice.NAME + "-", "")));
    }
}
