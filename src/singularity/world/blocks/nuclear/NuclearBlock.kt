package singularity.world.blocks.nuclear

import arc.func.Cons
import arc.func.Floatc
import arc.func.Floatp
import arc.math.Mathf
import arc.scene.ui.Label
import arc.scene.ui.Slider
import arc.scene.ui.layout.Table
import mindustry.gen.Icon
import mindustry.ui.Styles
import singularity.world.blocks.SglBlock
import singularity.world.meta.SglBlockGroup

open class NuclearBlock(name: String) : SglBlock(name) {
    init {
        hasEnergy = true
        solid = true
        update = true
        group = SglBlockGroup.nuclear
    }

    protected fun buildEnergySlider(sli: Table, min: Float, max: Float, yet: Floatp, slid: Floatc) {
        sli.button(Icon.leftOpen, Styles.clearNonei, Runnable { slid.get(Mathf.clamp(Mathf.pow(2, Mathf.log2(yet.get()).toInt() - 1).toFloat(), min, max)) }).size(32f)
        sli.slider(Mathf.log2(min), Mathf.log2(max), 0.01f, Mathf.log2(yet.get()), Floatc { f: Float -> slid.get(Mathf.pow(2f, f)) }).size(200f, 40f).padLeft(8f).padRight(8f).update(Cons { s: Slider? -> s!!.setValue(Mathf.log2(yet.get())) })
        sli.button(Icon.rightOpen, Styles.clearNonei, Runnable { slid.get(Mathf.clamp(Mathf.pow(2, Mathf.log2(yet.get()).toInt() + 1).toFloat(), min, max)) }).size(32f)
        sli.add("").update(Cons { lable: Label? -> lable!!.setText(Mathf.round(yet.get()).toString() + "NF") })
    }
}