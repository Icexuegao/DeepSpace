package ice.ui.tex

import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.scene.style.Drawable
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.CheckBox
import arc.scene.ui.Slider.SliderStyle
import arc.scene.ui.TextButton
import ice.graphics.IceColors
import ice.library.drawf.IceDraw
import ice.library.file.IceFiles
import mindustry.ui.Fonts

object IceTex {
    val background: Drawable = IceDraw.create9("background")
    val frameButtonUp: Drawable = IceDraw.create9("frameButtonUp")
    val frameButtonDown: Drawable = IceDraw.create9("frameButtonDown")
    val barTop: Drawable = IceDraw.create9("barTop")
    val bar: Drawable = IceDraw.create9("bar")
    val sliderFrame:Drawable= IceDraw.create9("sliderFrame")
    val sliderKnob:Drawable=TextureRegionDrawable(IceFiles.findPng("sliderKnob"))

    val barBackground: TextureRegion = IceFiles.findPng("barBackground")
    val whiteui: TextureRegion = IceFiles.findPng("whiteui")
    val time: TextureRegion = IceFiles.findPng("time")
    val arrow: TextureRegion = IceFiles.findPng("arrow")
    val flower: TextureRegion = IceFiles.findPng("flower")
    val buttonDown: TextureRegion = IceFiles.findPng("buttonDown")
    val buttonUp: TextureRegion = IceFiles.findPng("buttonUp")
    val deepSpaceVer: TextureRegion = IceFiles.findPng("deepSpaceVer")


    val rootButton = TextButton.TextButtonStyle().apply {
        up = frameButtonUp
        over = up
        down = frameButtonDown
        font = Fonts.def
        fontColor = IceColors.b3
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
        fontColor = IceColors.b1
    }
    val defaultSlider = SliderStyle().apply {
        background =sliderFrame
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