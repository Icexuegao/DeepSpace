package singularity.graphic

import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.util.Tmp
import mindustry.gen.Tex
import mindustry.graphics.Pal
import singularity.Singularity

object SglDrawConst {
  @JvmField val EMP_REGIONS: Array<TextureRegion?> = arrayOfNulls<TextureRegion>(0)

  //Colors
  @JvmStatic val transColor: Color = Color(0f, 0f, 0f, 0f)
  @JvmStatic val fexCrystal: Color = Color.valueOf("FF9584")
  @JvmStatic val matrixNet: Color = Color.valueOf("D3FDFF")
  @JvmStatic val matrixNetDark: Color = Color.valueOf("9ECBCD")
  @JvmStatic val ion: Color = Color.valueOf("#D1D19F")
  @JvmStatic val dew: Color = Color.valueOf("ff6214")
  @JvmStatic val frost: Color = Color.valueOf("AFF7FF")
  @JvmStatic val winter: Color = Color.valueOf("6CA5FF")

  //Text colors
  const val COLOR_ACCENT: String = "[accent]"

  //Texture regions
  lateinit var transparent: Drawable
  lateinit var sglLaunchLogo: Drawable
  lateinit var squareMarker: Drawable
  lateinit var matrixArrow: Drawable
  lateinit var sglIcon: Drawable
  lateinit var artistIcon: Drawable
  lateinit var codeIcon: Drawable
  lateinit var translateIcon: Drawable
  lateinit var soundsIcon: Drawable
  lateinit var time: Drawable
  lateinit var techPoint: Drawable
  lateinit var inspire: Drawable

  lateinit var startIcon: Drawable
  lateinit var databaseIcon: Drawable
  lateinit var publicInfoIcon: Drawable
  lateinit var aboutIcon: Drawable
  lateinit var configureIcon: Drawable
  lateinit var contributeIcon: Drawable
  lateinit var debuggingIcon: Drawable
  lateinit var nuclearIcon: Drawable
  lateinit var matrixIcon: Drawable

  lateinit var qqIcon: Drawable
  lateinit var telegramIcon: Drawable
  lateinit var showInfos: Drawable
  lateinit var unShowInfos: Drawable
  lateinit var showRange: Drawable
  lateinit var hold: Drawable
  lateinit var defaultShow: Drawable
  lateinit var grayUI: Drawable
  lateinit var padGrayUI: Drawable
  lateinit var darkgrayUI: Drawable
  lateinit var grayUIAlpha: Drawable
  lateinit var padGrayUIAlpha: Drawable
  lateinit var darkgrayUIAlpha: Drawable
  lateinit var sgl2: Drawable
  lateinit var a_z: Drawable

  var cursor: TextureRegion? = null

  fun load() {
    transparent = Singularity.getModDrawable<Drawable?>("transparent")
    sglLaunchLogo = Singularity.getModDrawable("launch_logo")
    squareMarker = Singularity.getModDrawable<Drawable?>("square_marker")
    matrixArrow = Singularity.getModDrawable<Drawable?>("matrix_arrow")
    sglIcon = Singularity.getModDrawable<Drawable?>("sgl_icon")
    artistIcon = Singularity.getModDrawable<Drawable?>("artist")
    codeIcon = Singularity.getModDrawable<Drawable?>("code")
    translateIcon = Singularity.getModDrawable<Drawable?>("translate")
    soundsIcon = Singularity.getModDrawable<Drawable?>("sound")
    time = Singularity.getModDrawable<Drawable?>("time")
    techPoint = Singularity.getModDrawable<Drawable?>("tech_point")
    inspire = Singularity.getModDrawable<Drawable?>("inspire")
    startIcon = Singularity.getModDrawable<Drawable?>("icon_start")
    databaseIcon = Singularity.getModDrawable<Drawable?>("icon_database")
    publicInfoIcon = Singularity.getModDrawable<Drawable?>("icon_publicInfo")
    aboutIcon = Singularity.getModDrawable<Drawable?>("icon_about")
    configureIcon = Singularity.getModDrawable<Drawable?>("icon_configure")
    contributeIcon = Singularity.getModDrawable<Drawable?>("icon_contribute")
    debuggingIcon = Singularity.getModDrawable<Drawable?>("debugging")
    nuclearIcon = Singularity.getModDrawable<Drawable?>("nuclear")
    matrixIcon = Singularity.getModDrawable<Drawable?>("matrix")
    qqIcon = Singularity.getModDrawable<Drawable?>("qq")
    telegramIcon = Singularity.getModDrawable<Drawable?>("telegram")
    showInfos = Singularity.getModDrawable<Drawable?>("show_infos")
    unShowInfos = Singularity.getModDrawable<Drawable?>("unshow_infos")
    showRange = Singularity.getModDrawable<Drawable?>("show_range")
    hold = Singularity.getModDrawable<Drawable?>("hold")
    defaultShow = Singularity.getModDrawable<Drawable?>("default_show")
    sgl2 = Singularity.getModDrawable<Drawable?>("sgl-2")
    a_z = Singularity.getModDrawable<Drawable?>("a_z")

    cursor = Singularity.getModAtlas("cursor")

    grayUI = (Tex.whiteui as TextureRegionDrawable).tint(Pal.darkerGray)
    padGrayUI = (Tex.whiteui as TextureRegionDrawable).tint(Pal.darkerGray)
    padGrayUI!!.setLeftWidth(8f)
    padGrayUI!!.setRightWidth(8f)
    padGrayUI!!.setTopHeight(8f)
    padGrayUI!!.setBottomHeight(8f)
    darkgrayUI = (Tex.whiteui as TextureRegionDrawable).tint(Pal.darkestGray)

    grayUIAlpha = (Tex.whiteui as TextureRegionDrawable).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f))
    padGrayUIAlpha = (Tex.whiteui as TextureRegionDrawable).tint(Tmp.c1.set(Pal.darkerGray).a(0.7f))
    padGrayUIAlpha!!.setLeftWidth(8f)
    padGrayUIAlpha!!.setRightWidth(8f)
    padGrayUIAlpha!!.setTopHeight(8f)
    padGrayUIAlpha!!.setBottomHeight(8f)
    darkgrayUIAlpha = (Tex.whiteui as TextureRegionDrawable).tint(Tmp.c1.set(Pal.darkestGray).a(0.7f))
  }
}