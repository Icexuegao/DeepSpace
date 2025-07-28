package ice.ui.fragment

import arc.Core
import arc.func.Boolp
import arc.scene.Group
import arc.scene.event.Touchable
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import ice.library.scene.element.ProgressBar
import ice.library.scene.texs.IStyles
import ice.library.scene.ui.layout.CrosswiseCollapser

object BossHealthFragment {
    val table = Table()
    val group = WidgetGroup()
    var df = false
    var i = 1f
    var f = false
    val bar = ProgressBar(IStyles.pa2) { i }
    fun build(parent: Group) {
        parent.addChild(group)
        group.setFillParent(true)
        group.touchable = Touchable.childrenOnly
        group.visibility = Boolp { true }


        bar.update {
            if (i <= 0) f = false
            if (i >= 1) f = true

            if (f) i -= 0.01f / 60f else i += 0.01f / 60f
        }

        val col = CrosswiseCollapser({
            it.add(bar)
        }) { df }
        table.setPosition((Core.graphics.width - col.width) / 2,
            (Core.graphics.height - bar.height) / 2 + 400f - 20f)
        table.update {
            table.setPosition((Core.graphics.width - col.width) / 2,
                (Core.graphics.height - bar.height) / 2 + 400f - 20f)
        }


        table.add(col).grow()
        // table.image(IFiles.findPng("box"))
        group.addChild(table)

    }

}