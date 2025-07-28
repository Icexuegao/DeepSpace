package ice.library.scene.texs

import arc.graphics.Color
import arc.graphics.Texture
import arc.graphics.g2d.TextureRegion
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button.ButtonStyle
import arc.scene.ui.CheckBox
import arc.scene.ui.ImageButton.ImageButtonStyle
import arc.scene.ui.Slider.SliderStyle
import arc.scene.ui.TextButton.TextButtonStyle
import ice.library.IFiles
import ice.library.struct.asDrawable
import mindustry.gen.Tex
import mindustry.ui.Fonts
import mindustry.ui.Styles

object Texs {
    val background11: Drawable = IFiles.createNinePatch("background1-1")
    val background21: Drawable = IFiles.createNinePatch("background2-1")
    val background22: Drawable = IFiles.createNinePatch("background2-2")
    val background23: Drawable = IFiles.createNinePatch("background2-3")
    val background31: Drawable = IFiles.createNinePatch("background3-1")
    val background32: Drawable = IFiles.createNinePatch("background3-2")
    val background41: Drawable = IFiles.createNinePatch("background4-1")
    val background42: Drawable = IFiles.createNinePatch("background4-2")
    val background43: Drawable = IFiles.createNinePatch("background4-3")
    val background44: Drawable = IFiles.createNinePatch("background4-4")
    val background61: Drawable = IFiles.createNinePatch("background6-1")
    val background62: Drawable = IFiles.createNinePatch("background6-2")
    val background71: Drawable = IFiles.createNinePatch("background7-1")
    val background81: Drawable = IFiles.createNinePatch("background8-1")
    val background91: Drawable = IFiles.createNinePatch("background9-1")
    val background101: Drawable = IFiles.createNinePatch("background10-1")
    val background111:Drawable=IFiles.createNinePatch("background11-1")
    val background121:Drawable=IFiles.createNinePatch("background12-1")

    //
    val xuerou: Drawable = IFiles.createNinePatch("科技树-血肉")
    val achievementUnlock = IFiles.findIcePng("achievement-unlock")
    val achievementLock = IFiles.findIcePng("achievement-lock")
    val achievement = IFiles.findIcePng("achievement")
    val floor = IFiles.findIcePng("floor")
    val tanksui = IFiles.findIcePng("thanks")
    val search = IFiles.findIcePng("search")
    val button2 = object : ImageButtonStyle() {}.apply {
        up = background41
        over = background41
        down = background42
        checked = background44
        disabled = background43
        //imageDisabled= background10
    }
    val button: ImageButtonStyle = object : ImageButtonStyle() {
    }.apply {
        up = background41
        over = background41
        down = background42
        disabled = background43
        //imageDisabled= background10
    }
    val button3: ImageButtonStyle = object : ImageButtonStyle() {
    }.apply {
        up = background41
        over = background41
        down = background42
        imageUpColor = Colors.b4
        imageDownColor = Colors.r1
        imageOverColor = Colors.b4
        //imageDisabled= background10
    }
    val button4= ButtonStyle().apply {
        up= background43
        down= background43
        over=background43
    }
    val button1 = object : TextButtonStyle() {}.apply {
        up = background61
        over = background61
        down = background62
        checked = background62
        font = Fonts.def
        fontColor = Color.white
        overFontColor = Colors.b4
        disabledFontColor = Color.gray
    }
    val frameButtonUp: Drawable = IFiles.createNinePatch("frameButtonUp")
    val frameButtonDown: Drawable = IFiles.createNinePatch("frameButtonDown")
    val barTop: Drawable = IFiles.createNinePatch("barTop")
    val sliderFrame: Drawable = IFiles.createNinePatch("sliderFrame")
    val sliderKnob: Drawable = IFiles.findIcePng("sliderKnob").asDrawable()
    val black8: Drawable = (Tex.whiteui as TextureRegionDrawable).tint(0f, 0f, 0f, 0.8f)
    val barBackground: TextureRegion = IFiles.findIcePng("barBackground")
    val missionaryIcon: TextureRegion = IFiles.findIcePng("missionaryIcon")
    val missionaryIconTurNingRight1: TextureRegion = IFiles.findIcePng("missionaryIconTurNing1")
    val missionaryIconTurNingRight2: TextureRegion = IFiles.findIcePng("missionaryIconTurNing2")
    val missionaryIconTurNingLeft1: TextureRegion = IFiles.findIcePng("missionaryIconTurNing1").apply {
        flip(true, false)
    }
    val missionaryIconTurNingLeft2: TextureRegion = IFiles.findIcePng("missionaryIconTurNing2").apply {
        flip(true, false)
    }
    val whiteui: TextureRegion = IFiles.findIcePng("whiteui")
    val time: TextureRegion = IFiles.findIcePng("time").apply {
        texture.setFilter(Texture.TextureFilter.linear)
    }
    val arrow: TextureRegion = IFiles.findIcePng("arrow")
    val arrow1=IFiles.findIcePng("arrow1")

    val flower: TextureRegion = IFiles.findIcePng("flower").apply {
        texture.setFilter(Texture.TextureFilter.linear)
    }
    val buttonDown: TextureRegion = IFiles.findIcePng("buttonDown")
    val buttonUp: TextureRegion = IFiles.findIcePng("buttonUp")
    val deepSpaceVer: TextureRegion = IFiles.findIcePng("deepSpaceVer")
    val backgroundButton = ButtonStyle().apply {
        up = background21
        over = background21
        down = background22
        checked = background22
        checkedOver = background22
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
        overFontColor = Colors.b4
        disabledFontColor = Color.gray/*  imageDisabledColor = Color.lightGray
          imageUpColor = */
    }
    val cleari = ButtonStyle().apply {
        down = Styles.flatDown
        up = black8
        over = Styles.flatOver
        disabled = Styles.black8
        /* font = Fonts.outline
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
        fontColor = Colors.b4
        disabledFontColor = Color.gray
    }
    val checkBoxStyle = CheckBox.CheckBoxStyle().apply {
        val off = buttonDown.asDrawable()
        val on = buttonUp.asDrawable()
        checkboxOn = on
        checkboxOnOver = on
        checkboxOnDisabled = on
        checkboxOff = off
        checkboxOver = off
        checkboxOffDisabled = off
        font = Fonts.def
        fontColor = Colors.b1
    }
    val defaultSlider = SliderStyle().apply {
        background = sliderFrame
        knob = sliderKnob
        knobOver = sliderKnob
        knobDown = sliderKnob
    }

    fun getComplexityStyle(): CheckBox.CheckBoxStyle {
        return object : CheckBox.CheckBoxStyle() {}.apply {
            checkboxOn = checkBoxStyle.checkboxOn
            checkboxOnOver = checkboxOn
            checkboxOnDisabled = checkboxOn
            checkboxOff = checkBoxStyle.checkboxOff
            checkboxOver = checkboxOff
            checkboxOffDisabled = checkboxOff
            font = Fonts.def
            fontColor = Color.gray
        }
    }
}