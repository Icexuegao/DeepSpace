package universecore.scene.element.display

import arc.graphics.Color
import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Scaling
import universecore.util.toTrimmedString
import ice.world.meta.IStatValues.withTooltip
import mindustry.Vars.iconMed
import mindustry.type.Liquid
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit

class LiquidDisplay(
  val liquid: Liquid, val amount: Float = 0f, val perSecond: Boolean = false, showName: Boolean = true
) :Table() {
  init {
    add(object :Stack() {
      init {
        add(Image(liquid.uiIcon).setScaling(Scaling.fit))

        if (amount != 0f) {
          val t: Table = Table().left().bottom()
          t.add((amount * 60f).toTrimmedString(1)).style(Styles.outlineLabel)
          add(t)
        }
      }
    }).height(iconMed)

    if (perSecond && amount != 0f) {
      add(StatUnit.perSecond.localized()).padLeft(2f).padRight(5f).color(Color.lightGray).style(Styles.outlineLabel)
    }

    if (showName) add(liquid.localizedName)

    withTooltip(this, liquid, true)
  }
}
