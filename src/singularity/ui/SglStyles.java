package singularity.ui;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Button;
import arc.scene.ui.Dialog;
import arc.scene.ui.Slider;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import singularity.graphic.SglDrawConst;
import singularity.ui.fragments.entityinfo.HealthBarStyle;

import static mindustry.gen.Tex.*;
import static singularity.ui.SglUI.uiBlur;

public class SglStyles{
  public static TextureRegionDrawable BLUR_BACK;

  public static Slider.SliderStyle sliderLine;
  public static Button.ButtonStyle underline, sideButtonRight;
  public static Dialog.DialogStyle blurBack, transparentBack, transGrayBack;

  public static void load(){
    HealthBarStyle.loadAll();

    BLUR_BACK = new TextureRegionDrawable(Core.atlas.white()) {
      @Override
      public void draw(float x, float y, float width, float height) {
        uiBlur.directDraw(() -> super.draw(x, y, width, height));

        Styles.black5.draw(x, y, width, height);
      }

      @Override
      public void draw(float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        uiBlur.directDraw(() -> super.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation));

        Styles.black5.draw(x, y, originX, originY, width, height, scaleX, scaleY, rotation);
      }
    };

    sliderLine = new Slider.SliderStyle(){{
      background = Core.atlas.drawable("singularity-slider_line_back");
      knob = sliderKnob;
      knobOver = sliderKnobOver;
      knobDown = sliderKnobDown;
    }};
    
    underline = new Button.ButtonStyle(){{
      up = Tex.underline;
      down = underlineWhite;
      over = underlineOver;
    }};

    sideButtonRight = new Button.ButtonStyle(){{
      up = Tex.buttonSideRight;
      down = Tex.buttonSideRightDown;
      over = Tex.buttonSideRightOver;
    }};

    blurBack = new Dialog.DialogStyle(){{
      stageBackground = BLUR_BACK;
      titleFont = Fonts.def;
      background = windowEmpty;
      titleFontColor = Pal.accent;
    }};

    transparentBack = new Dialog.DialogStyle(){{
      stageBackground = SglDrawConst.transparent;
      titleFont = Fonts.outline;
      background = SglDrawConst.transparent;
      titleFontColor = Pal.accent;
    }};

    transGrayBack = new Dialog.DialogStyle(){{
      stageBackground = SglDrawConst.grayUIAlpha;
      titleFont = Fonts.outline;
      background = windowEmpty;
      titleFontColor = Pal.accent;
    }};
  }
}
