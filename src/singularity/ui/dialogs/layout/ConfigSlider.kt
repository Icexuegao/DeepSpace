package singularity.ui.dialogs.layout

import arc.func.Floatc
import arc.func.Floatp
import arc.func.Func
import arc.math.Mathf
import arc.scene.event.Touchable
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.util.Strings
import ice.graphics.IStyles
import ice.graphics.IceColor.b4

open class ConfigSlider :ConfigEntry {
  var slided: Floatc
  var curr: Floatp
  var show: Func<Float, String>
  var min: Float
  var max: Float
  var step: Float

  constructor(name: String, slided: Floatc, curr: Floatp, min: Float, max: Float, step: Float) :super(name) {
    var step = step
    this.slided = slided
    this.curr = curr
    this.min = min
    this.max = max
    this.step = step

    val fix: Int
    step %= 1f
    var i = 0
    while(true) {
      if (Mathf.zero(step)) {
        fix = i
        break
      }
      step *= 10f
      step %= 1f
      i++
    }

    this.show = Func { f -> Strings.autoFixed(f, fix) }
  }

  constructor(
    name: String, show: Func<Float, String>, slided: Floatc, curr: Floatp, min: Float, max: Float, step: Float
  ) :super(name) {
    this.show = show
    this.slided = slided
    this.curr = curr
    this.min = min
    this.max = max
    this.step = step
  }

  override fun buildCfg(table: Table) {

    /* if (str == null) {
       table.add("").update { l: Label? ->
         l!!.setColor(b4)
         l.setText(show.get(curr.get()))
       }.padRight(0f)
     }
     table.slider(min, max, step, curr.get(), slided).width(360f).height(45f).padLeft(4f).update { s: Slider? ->
       s!!.setValue(curr.get())
       s.isDisabled = disabled.get()
     }.get().setStyle(defaultSlider)*/

    table.stack(Table().apply {
      slider(min, max, step, curr.get(), slided).width(360f).height(45f).padLeft(4f).update { s ->
        s!!.setValue(curr.get())
        s.isDisabled = (disabled.get())
      }.get().setStyle(IStyles.defaultSlider)
    }, Table().apply {
      if (str == null) {
        add("").update { l: Label? ->
          l!!.setColor(b4)
          l.setText(show.get(curr.get()))
        }.touchable(Touchable.disabled).expand().padRight(10f).right()
      }
    })
  }

}