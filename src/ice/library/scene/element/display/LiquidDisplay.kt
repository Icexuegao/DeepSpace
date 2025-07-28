package ice.library.scene.element.display

import arc.graphics.Color
import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Scaling
import arc.util.Strings
import mindustry.Vars.iconMed
import mindustry.type.Liquid
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit

class LiquidDisplay(
    val liquid: Liquid, val amount: Float = 0f, val perSecond: Boolean = false, val localizedName: Boolean = true
) : Table() {
    init {
        add(object : Stack() {
            init {
                add(Image(liquid.uiIcon).setScaling(Scaling.fit))

                if (amount != 0f) {
                    val t: Table = Table().left().bottom()
                    t.add(Strings.autoFixed(amount, 2)).style(Styles.outlineLabel)
                    add(t)
                }
            }
        }).size(iconMed).apply {
            if (perSecond||localizedName)padRight(3f + (if (amount != 0f && Strings.autoFixed(amount, 2).toFloat() > 2f) 8 else 0))
        }

        if (perSecond) {
            add(StatUnit.perSecond.localized()).padLeft(2f).padRight(5f).color(Color.lightGray)
                .style(Styles.outlineLabel)
        }

        if (localizedName)add(liquid.localizedName)
    }
}
