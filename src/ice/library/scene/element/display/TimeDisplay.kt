package ice.library.scene.element.display

import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.util.Scaling
import arc.util.Strings
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit
import singularity.graphic.SglDrawConst

class TimeDisplay(amount: Float) : Table() {
  init {
    stack(Table { o ->
      o.left()
      o.add(Image(SglDrawConst.time)).size(32f).scaling(Scaling.fit)
    }, Table { o ->
      o.left().bottom()
      o.add(Strings.autoFixed(amount / 60, 1) + StatUnit.seconds.localized()).style(Styles.outlineLabel)
      o.pack()
    })
  }
}