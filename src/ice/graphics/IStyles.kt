package ice.graphics

import arc.graphics.Color
import arc.graphics.Texture
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button.ButtonStyle
import arc.scene.ui.ImageButton.ImageButtonStyle
import arc.scene.ui.Slider.SliderStyle
import arc.scene.ui.TextButton.TextButtonStyle
import arc.scene.ui.layout.Scl
import ice.game.IceTeam
import ice.library.IFiles
import ice.library.scene.layout.ProgressAttribute
import ice.library.struct.asDrawable
import mindustry.gen.Tex
import mindustry.ui.Fonts
import mindustry.ui.Styles

object IStyles {
  val background11 = IFiles.createNinePatch("background1-1")
  val background21 = IFiles.createNinePatch("background2-1")
  val background22 = IFiles.createNinePatch("background2-2")
  val background23 = IFiles.createNinePatch("background2-3")
  val background31 = IFiles.createNinePatch("background3-1")
  val background32 = IFiles.createNinePatch("background3-2")
  val background33 = IFiles.createNinePatch("background3-3")
  val background41 = IFiles.createNinePatch("background4-1")
  val background42 = IFiles.createNinePatch("background4-2")
  val background43 = IFiles.createNinePatch("background4-3")
  val background44 = IFiles.createNinePatch("background4-4")
  val background45 = IFiles.createNinePatch("background4-5")
  val background61 = IFiles.createNinePatch("background6-1")
  val background62 = IFiles.createNinePatch("background6-2")
  val background71 = IFiles.createNinePatch("background7-1")
  val background81 = IFiles.createNinePatch("background8-1")
  val background91 = IFiles.createNinePatch("background9-1")
  val background101 = IFiles.createNinePatch("background10-1")
  val background111 = IFiles.createNinePatch("background11-1")
  val background121 = IFiles.createNinePatch("background12-1")
  val background122 = IFiles.createNinePatch("background12-2")
  val background131 = IFiles.createNinePatch("background13-1")
  val achievementUnlock = IFiles.findModPng("achievement-unlock")
  val achievementLock = IFiles.findModPng("achievement-lock")
  val achievement = IFiles.findModPng("achievement")

  val paneLeft=IFiles.createNinePatch("pane-left")

  val remains = IFiles.findModPng("remains").asDrawable()
  val tanksui = IFiles.findModPng("thanks")
  val search = IFiles.findModPng("search")
  val button2 = object : ImageButtonStyle() {}.apply {
    up = background41
    over = background41
    down = background42
    checked = background44
    disabled = background43
    //imageDisabled= background10
  }
  val button5 = object : ImageButtonStyle() {}.apply {
    up = background44
    over = background44
    down = background44
    checked = background44
    disabled = background43
    //imageDisabled= background10
  }
  val button6 = ImageButtonStyle().apply {
    up = background45
    over = background45
    down = background45
    checked = background45
    disabled = background43
    //imageDisabled= background10
  }
  val button: ImageButtonStyle = object : ImageButtonStyle() {}.apply {
    up = background41
    over = background41
    down = background42
    disabled = background43
    //imageDisabled= background10
  }
  val button3: ImageButtonStyle = object : ImageButtonStyle() {}.apply {
    up = background41
    over = background41
    down = background42
    imageUpColor = IceColor.b4
    imageDownColor = IceColor.r1
    imageOverColor = IceColor.b4
    //imageDisabled= background10
  }
  val button4 = ButtonStyle().apply {
    up = background43
    down = background43
    over = background43
  }
  val button1 = object : TextButtonStyle() {}.apply {
    up = background61
    over = background61
    down = background62
    checked = background62
    font = Fonts.def
    fontColor = Color.white
    overFontColor = IceColor.b4
    disabledFontColor = Color.gray
  }
  val frameButtonUp = IFiles.createNinePatch("frameButtonUp")
  val frameButtonDown = IFiles.createNinePatch("frameButtonDown")
  val barTop = IFiles.createNinePatch("barTop")
  val sliderFrame = IFiles.createNinePatch("sliderFrame")
  val sliderKnob = IFiles.findModPng("sliderKnob").asDrawable()
  val black8 = (Tex.whiteui as TextureRegionDrawable).tint(0f, 0f, 0f, 0.8f)
  val barBackground = IFiles.findModPng("barBackground")
  val barBottlom: Drawable = IFiles.createNinePatch("barBottom")
  val missionaryIcon = IFiles.findModPng("missionaryIcon")
  val missionaryIconTurNingRight1 = IFiles.findModPng("missionaryIconTurNing1")
  val missionaryIconTurNingRight2 = IFiles.findModPng("missionaryIconTurNing2")
  val missionaryIconTurNingLeft1 = IFiles.findModPng("missionaryIconTurNing1").apply {
    flip(true, false)
  }
  val missionaryIconTurNingLeft2 = IFiles.findModPng("missionaryIconTurNing2").apply {
    flip(true, false)
  }
  val whiteui = IFiles.findModPng("whiteui")
  val time = IFiles.findModPng("time").apply {
    texture.setFilter(Texture.TextureFilter.linear)
  }
  val afehs = IFiles.findModPng(IceTeam.教廷.name)
  val empire = IFiles.findModPng(IceTeam.帝国.name)
  val arrow = IFiles.findModPng("arrow")
  val publicInfoIcon= IFiles.findModPng("upWel")

  val menusButton_host = IFiles.findModPng("menusButton_host").asDrawable()
  val menusButton_exit = IFiles.findModPng("menusButton_exit").asDrawable()
  val menusButton_remains= IFiles.findModPng("menusButton_remains").asDrawable()
  val menusButton_contribute = IFiles.findModPng("menusButton_contribute").asDrawable()
  val menusButton_infos= IFiles.findModPng("menusButton_infos").asDrawable()
  val menusButton_configure= IFiles.findModPng("menusButton_configure").asDrawable()
  val menusButton_publicInfo= IFiles.findModPng("menusButton_publicInfo").asDrawable()
  val menusButton_tech_point= IFiles.findModPng("menusButton_tech_point").asDrawable()
  val menusButton_database= IFiles.findModPng("menusButton_database").asDrawable()

  val arrow1 = IFiles.findModPng("arrow1")
  val flower = IFiles.findModPng("flower").apply {
    texture.setFilter(Texture.TextureFilter.linear)
  }
  val buttonDown = IFiles.findModPng("buttonDown")
  val buttonUp = IFiles.findModPng("buttonUp")
  val deepSpaceVer = IFiles.findModPng("deepSpaceVer")
  val backgroundButton = ButtonStyle().apply {
    up = background21
    over = background21
    down = background22
    checked = background22
    checkedOver = background22
  }
  val imageButtonClean = object : ImageButtonStyle() {
    init {
      down = Styles.flatDown
      up = Styles.none
      over = Styles.flatOver
      disabled = Styles.black8
      imageDisabledColor = IceColor.b4
      imageUpColor = IceColor.b4
    }
  }

  fun getWhiteui(color: Color): Drawable {
    return (Tex.whiteui as TextureRegionDrawable).tint(color)
  }

  fun getBlack(float: Float): Drawable {
    return (Tex.whiteui as TextureRegionDrawable).tint(0f, 0f, 0f, float)
  }

  val imageCleari = ImageButtonStyle().apply {
    down = Styles.flatDown
    up = black8
    over = Styles.flatOver
    disabled = Styles.black8
    imageDisabledColor = Color.lightGray
    imageUpColor = Color.white
  }

  fun getColorImageCleari(float: Float): ImageButtonStyle {
    return ImageButtonStyle().apply {
      down = Styles.flatDown
      up = (Tex.whiteui as TextureRegionDrawable).tint(0f, 0f, 0f, float)
      over = Styles.flatOver
      disabled = Styles.black8
      imageDisabledColor = Color.lightGray
      imageUpColor = Color.white
    }
  }

  val txtCleari = TextButtonStyle().apply {
    down = Styles.flatDown
    up = black8
    over = Styles.flatOver
    disabled = Styles.black8

    font = Fonts.def
    fontColor = Color.white
    overFontColor = IceColor.b4
    disabledFontColor = Color.gray/*  imageDisabledColor = Color.lightGray
          imageUpColor = */
  }
  val cleari = ButtonStyle().apply {
    down = Styles.flatDown
    up = black8
    over = Styles.flatOver
    disabled = Styles.black8/* font = Fonts.outline
     fontColor = Color.white
     overFontColor = Pal.accent
     disabledFontColor = Color.gray*//*  imageDisabledColor = Color.lightGray
          imageUpColor = */
  }
  val rootButton = TextButtonStyle().apply {
    up = frameButtonUp
    over = up
    down = frameButtonDown
    checked = frameButtonDown
    font = Fonts.def
    fontColor = IceColor.b4
    disabledFontColor = Color.gray
  }
  val rootCleanButton = TextButtonStyle().apply {
    up = frameButtonUp
    over = up
    down = frameButtonDown
    checked = frameButtonUp
    font = Fonts.def
    fontColor = IceColor.b4
    disabledFontColor = Color.gray
  }
  val checkBoxStyle = ImageButtonStyle().apply {
    val off = buttonDown.asDrawable()
    val on = buttonUp.asDrawable()
    checked = on
    imageChecked = on
    down = on
    over = off
    up = off
  }
  val cleanBoxStyle = ImageButtonStyle().apply {
    val off = buttonDown.asDrawable()
    val on = buttonUp.asDrawable()
    over = on
    up = on
    down = off
  }
  val defaultSlider = SliderStyle().apply {
    background = sliderFrame
    knob = sliderKnob
    knobOver = sliderKnob
    knobDown = sliderKnob
  }
  var pa1 = ProgressAttribute(barBackground, barTop).apply {
    color = IceColor.b4
    starX = 5f
    starY = Scl.scl(24f)
    drawHeight = Scl.scl(43f)
  }
  const val scal = 0.7f
  var pa2 = ProgressAttribute(
    IFiles.findModPng("bossProgress-box"), barTop, IFiles.findModPng("bossProgress-right-cover"), scal
  ).apply {
    color = IceColor.b4.cpy().a(1f)
    drawHeight = (barBackground.height - 80f) * scal
    starX = 24f * scal
    starY = 38f * scal
  }
}