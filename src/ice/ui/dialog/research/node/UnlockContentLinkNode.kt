package ice.ui.dialog.research.node

import arc.Events
import arc.scene.actions.Actions
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.ImageButton.ImageButtonStyle
import arc.scene.ui.layout.Table
import ice.library.scene.tex.IStyles
import ice.library.scene.tex.IStyles.background44
import ice.library.scene.tex.IceColor
import ice.ui.addCR
import ice.ui.dialog.DataDialog
import ice.ui.dialog.MenusDialog
import ice.ui.dialog.research.ResearchDialog.SelectANodeEvent
import ice.ui.dialog.research.ResearchDialog.selectANode
import ice.ui.scenes
import mindustry.ctype.UnlockableContent
import mindustry.gen.Icon
import mindustry.ui.Styles

class UnlockContentLinkNode(
    val content: UnlockableContent, x: Float, y: Float
) : LinkNode(content.name, x, y, Button(IStyles.button2)) {
    init {
        (element as Button).apply {
            setSize(nodeSize)
            margin(10f)
            add(Image(content.uiIcon)).grow()
        }
    }

    override fun unlocked(): Boolean {
        return content.unlocked()
    }

    override fun show(table: Table) {
        table.image(content.uiIcon).size(50f).row()
        table.addCR(content.localizedName).row()
        table.addCR(content.description).grow().wrap().row()
        table.addCR(content.details).grow().wrap().row()
        val style = object : ImageButtonStyle() {
            init {
                down = Styles.flatDown
                up = Styles.none
                over = Styles.flatOver
                disabled = Styles.black8
                imageDisabledColor = IceColor.b4
                imageUpColor = IceColor.b4
            }
        }
        table.table { t ->
            t.button(Icon.cancel, style, 40f) {
                table.actions(Actions.alpha(0f, 0.25f), Actions.run {
                    selectANode = null
                    Events.fire(SelectANodeEvent())
                })
            }
            t.button(Icon.book, style, 40f) {
                DataDialog.showBlock(content)
                t.scenes = MenusDialog.scene
            }
        }
    }

    override fun update() {
        super.update()
        val button = (element as Button)
        if (unlocked()) {
            button.style = Button.ButtonStyle().apply {
                up = background44
                down = background44
                over = background44
            }
            return
        }
        parent.forEach {
            if (it.unlocked()) {
                button.style = IStyles.button3
                return
            }
        }

        button.style = IStyles.button4

    }
}