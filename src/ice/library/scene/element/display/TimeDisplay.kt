package ice.library.scene.element.display

import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Scaling
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IceColor
import ice.library.util.toStringi
import mindustry.ui.Styles

class TimeDisplay(amount: Float) : Table() {
    init {
        add(Stack().apply {
            add(Table { o: Table ->
                o.left()
                o.add(Image(IStyles.time).apply { setColor(IceColor.b4) }).size(32f).scaling(Scaling.fit)
            })
            add(Table { t: Table ->
                t.left().bottom()
                val f = amount / 60f
                val text = f.toStringi(1)
                t.add(text).style(Styles.outlineLabel)
                t.pack()
            })
        })
    }
}