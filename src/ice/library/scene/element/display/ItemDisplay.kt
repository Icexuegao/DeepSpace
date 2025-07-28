package ice.library.scene.element.display

import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Scaling
import mindustry.core.UI
import mindustry.type.Item
import mindustry.ui.Styles

class ItemDisplay(val item: Item, val amount: Int = 0, val showName: Boolean = false) : Table() {
    init {
        add(Stack().apply {
            add(Table { o: Table ->
                o.left()
                o.add(Image(item.uiIcon)).size(32f).scaling(Scaling.fit)
            })

            if (amount != 0) {
                add(Table { t: Table ->
                    t.left().bottom()
                    t.add(if (amount >= 1000) UI.formatAmount(amount.toLong()) else amount.toString() + "")
                        .style(Styles.outlineLabel)
                    t.pack()
                })
            }
        })
        if (showName) add(item.localizedName).padLeft((if (4 + amount > 99) 4 else 0).toFloat())
    }
}