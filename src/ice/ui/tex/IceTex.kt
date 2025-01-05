package ice.ui.tex

import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Button
import arc.scene.ui.CheckBox
import arc.scene.ui.ImageButton.ImageButtonStyle
import arc.scene.ui.Slider.SliderStyle
import arc.scene.ui.TextButton.TextButtonStyle
import ice.library.drawf.IceDraw
import ice.library.IFiles
import mindustry.gen.Tex
import mindustry.ui.Fonts
import mindustry.ui.Styles

object IceTex {
    val background: Drawable = IceDraw.create9("background")
    val frameButtonUp: Drawable = IceDraw.create9("frameButtonUp")
    val frameButtonDown: Drawable = IceDraw.create9("frameButtonDown")
    val barTop: Drawable = IceDraw.create9("barTop")
    val sliderFrame: Drawable = IceDraw.create9("sliderFrame")
    val sliderKnob: Drawable = TextureRegionDrawable(IFiles.findPng("sliderKnob"))
    val black8: Drawable = (Tex.whiteui as TextureRegionDrawable).tint(0f, 0f, 0f, 0.8f)
    val pane2: Drawable = IceDraw.create9("pane2")


    val barBackground: TextureRegion = IFiles.findPng("barBackground")
    val whiteui: TextureRegion = IFiles.findPng("whiteui")
    val time: TextureRegion = IFiles.findPng("time")
    val arrow: TextureRegion = IFiles.findPng("arrow")
    val flower: TextureRegion = IFiles.findPng("flower")
    val buttonDown: TextureRegion = IFiles.findPng("buttonDown")
    val buttonUp: TextureRegion = IFiles.findPng("buttonUp")
    val deepSpaceVer: TextureRegion = IFiles.findPng("deepSpaceVer")

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

    val cleari = Button.ButtonStyle().apply {
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
        font = Fonts.def
        fontColor = Colors.b4
        disabledFontColor = Color.gray
    }

    val checkBoxStyle = CheckBox.CheckBoxStyle().apply {
        val off = TextureRegionDrawable(buttonDown)
        val on = TextureRegionDrawable(buttonUp)
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