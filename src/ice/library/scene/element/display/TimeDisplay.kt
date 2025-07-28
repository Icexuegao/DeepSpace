package ice.library.scene.element.display

import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.library.scene.texs.Colors
import ice.library.scene.texs.Texs
import ice.library.util.toStringi
import mindustry.ui.Styles

class TimeDisplay(amount: Float) : Table() {
    init {
        add(Stack().apply {
            add(Table { o: Table ->
                o.left()
                o.add(Image(Texs.time).apply { setColor(Colors.b4) }).size(32f).scaling(Scaling.fit)
            })
            add(Table { t: Table ->
                t.left().bottom()
                t.add((amount / 60f).toStringi(1)).style(Styles.outlineLabel)
                t.pack()
            })
        })
    }
}