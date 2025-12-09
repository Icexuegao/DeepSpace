package ice.graphics;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Tmp;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;

public class SglDrawConst {
  public static final TextureRegion[] EMP_REGIONS = new TextureRegion[0];


  //Texture regions
  public static Drawable transparent, sglLaunchLogo, squareMarker, matrixArrow, sglIcon, artistIcon, codeIcon, translateIcon,
      soundsIcon, time, techPoint, inspire,

      startIcon, databaseIcon, publicInfoIcon, aboutIcon, configureIcon, contributeIcon, debuggingIcon, nuclearIcon, matrixIcon,

      qqIcon, telegramIcon,
      showInfos, unShowInfos, showRange, hold, defaultShow,
      grayUI, padGrayUI, darkgrayUI,
      grayUIAlpha, padGrayUIAlpha, darkgrayUIAlpha,
      sgl2, a_z;

  public static TextureRegion cursor;
  public static <T extends Drawable> T getModDrawable(String name) {
    return Core.atlas.getDrawable( "ice-" + name);
  }
  public static void load(){
   transparent = getModDrawable("transparent");
    sglLaunchLogo = getModDrawable("launch_logo");
    squareMarker = getModDrawable("square_marker");
    matrixArrow = getModDrawable("matrix_arrow");
    sglIcon = getModDrawable("sgl_icon");
    artistIcon = getModDrawable("artist");
    codeIcon = getModDrawable("code");
    translateIcon = getModDrawable("translate");
    soundsIcon = getModDrawable("sound");
    time = getModDrawable("time");
    techPoint = getModDrawable("tech_point");
    inspire = getModDrawable("inspire");
    startIcon = getModDrawable("icon_start");
    databaseIcon = getModDrawable("icon_database");
    publicInfoIcon = getModDrawable("icon_publicInfo");
    aboutIcon = getModDrawable("icon_about");
    configureIcon = getModDrawable("icon_configure");
    contributeIcon = getModDrawable("icon_contribute");
    debuggingIcon = getModDrawable("debugging");
    nuclearIcon = getModDrawable("nuclear");
    matrixIcon = getModDrawable("matrix");
    qqIcon = getModDrawable("qq");
    telegramIcon = getModDrawable("telegram");
    showInfos = getModDrawable("show_infos");
    unShowInfos = getModDrawable("unshow_infos");
    showRange = getModDrawable("show_range");
    hold = getModDrawable("hold");
    defaultShow = getModDrawable("default_show");
    sgl2 = getModDrawable("sgl-2");
    a_z = getModDrawable("a_z");

 //   cursor = getModAtlas("cursor");

    grayUI = ((TextureRegionDrawable) Tex.whiteui).tint(Pal.darkerGray);
    padGrayUI = ((TextureRegionDrawable) Tex.whiteui).tint(Pal.darkerGray);
    padGrayUI.setLeftWidth(8);
    padGrayUI.setRightWidth(8);
    padGrayUI.setTopHeight(8);
    padGrayUI.setBottomHeight(8);
    darkgrayUI = ((TextureRegionDrawable) Tex.whiteui).tint(Pal.darkestGray);

    grayUIAlpha = ((TextureRegionDrawable) Tex.whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
    padGrayUIAlpha = ((TextureRegionDrawable) Tex.whiteui).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f));
    padGrayUIAlpha.setLeftWidth(8);
    padGrayUIAlpha.setRightWidth(8);
    padGrayUIAlpha.setTopHeight(8);
    padGrayUIAlpha.setBottomHeight(8);
    darkgrayUIAlpha = ((TextureRegionDrawable) Tex.whiteui).tint(Tmp.c1.set(Pal.darkestGray).a(0.7f));
  }
}
