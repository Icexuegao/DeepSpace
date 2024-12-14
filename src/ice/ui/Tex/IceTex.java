package ice.ui.Tex;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.ui.TextButton;
import ice.graphics.IceColors;
import ice.library.draw.IceDraw;
import mindustry.ui.Fonts;

import static ice.library.file.IceFiles.findPng;

public class IceTex {
    public static TextButton.TextButtonStyle ui按钮;
    public static Drawable background;

    public static TextureRegion time, arrow;

    static {
        background = IceDraw.createFlatDown("ice-background");
    }

    static {
        time = findPng("time");
        arrow = findPng("arrow");
    }

    static {
        ui按钮 = new TextButton.TextButtonStyle() {{
            over = up = IceDraw.createFlatDown("ice-flat-up-base1");
            down = IceDraw.createFlatDown("ice-flat-down-base1");
            font = Fonts.def;
            fontColor = IceColors.b1;
            disabledFontColor = Color.gray;
        }};
    }
}
