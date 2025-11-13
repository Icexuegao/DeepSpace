package ice.library.scene.element.display

import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.library.scene.tex.IceColor
import mindustry.core.UI
import mindustry.gen.Icon
import mindustry.ui.Styles

class PowerDisplay(val amount: Float) : Table() {
    init {
        add(Stack().apply {
            add(Table { o: Table ->
                o.left()
                o.add(Image(Icon.power).apply { setColor(IceColor.b4) }).size(32f).scaling(Scaling.fit)
            })
            add(Table { t: Table ->
                t.left().bottom()
                t.add(if (amount >= 1000) UI.formatAmount(amount.toLong()) else "${amount.toInt()}")
                    .style(Styles.outlineLabel)
                t.pack()
            })
        })
    }
}
