package singularity.graphic;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Tmp;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import singularity.Singularity;

public class SglDrawConst{
  public static final TextureRegion[] EMP_REGIONS = new TextureRegion[0];

  //Colors
  public static final Color transColor = new Color(0, 0, 0, 0);
  public static final Color fexCrystal = Color.valueOf("FF9584");
  public static final Color matrixNet = Color.valueOf("D3FDFF");
  public static final Color matrixNetDark = Color.valueOf("9ECBCD");
  public static final Color ion = Color.valueOf("#D1D19F");
  public static final Color dew = Color.valueOf("ff6214");
  public static final Color frost = Color.valueOf("AFF7FF");
  public static final Color winter = Color.valueOf("6CA5FF");

  //Text colors
  public static final String COLOR_ACCENT = "[accent]";

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

  public static void load(){
    transparent = Singularity.getModDrawable("transparent");
    sglLaunchLogo = Singularity.getModDrawable("launch_logo");
    squareMarker = Singularity.getModDrawable("square_marker");
    matrixArrow = Singularity.getModDrawable("matrix_arrow");
    sglIcon = Singularity.getModDrawable("sgl_icon");
    artistIcon = Singularity.getModDrawable("artist");
    codeIcon = Singularity.getModDrawable("code");
    translateIcon = Singularity.getModDrawable("translate");
    soundsIcon = Singularity.getModDrawable("sound");
    time = Singularity.getModDrawable("time");
    techPoint = Singularity.getModDrawable("tech_point");
    inspire = Singularity.getModDrawable("inspire");
    startIcon = Singularity.getModDrawable("icon_start");
    databaseIcon = Singularity.getModDrawable("icon_database");
    publicInfoIcon = Singularity.getModDrawable("icon_publicInfo");
    aboutIcon = Singularity.getModDrawable("icon_about");
    configureIcon = Singularity.getModDrawable("icon_configure");
    contributeIcon = Singularity.getModDrawable("icon_contribute");
    debuggingIcon = Singularity.getModDrawable("debugging");
    nuclearIcon = Singularity.getModDrawable("nuclear");
    matrixIcon = Singularity.getModDrawable("matrix");
    qqIcon = Singularity.getModDrawable("qq");
    telegramIcon = Singularity.getModDrawable("telegram");
    showInfos = Singularity.getModDrawable("show_infos");
    unShowInfos = Singularity.getModDrawable("unshow_infos");
    showRange = Singularity.getModDrawable("show_range");
    hold = Singularity.getModDrawable("hold");
    defaultShow = Singularity.getModDrawable("default_show");
    sgl2 = Singularity.getModDrawable("sgl-2");
    a_z = Singularity.getModDrawable("a_z");

    cursor = Singularity.getModAtlas("cursor");

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
