package ice.ui.dialog.research.node

import arc.func.Prov
import arc.scene.Element
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.Tooltip
import ice.library.scene.texs.Texs
import ice.library.scene.texs.Texs.background44
import mindustry.ctype.UnlockableContent

class UnlockContentLinkNode(
    val content: UnlockableContent, x: Float, y: Float, ep: Prov<Element> = Prov {
        val b = Button(Texs.button2)
        b.setSize(nodeSize)
        b.margin(10f)
        b.add(Image(content.uiIcon)).grow()
        b.addListener(Tooltip {
            it.margin(15f)
            it.background(Texs.background71)
            it.add(content.localizedName).grow().row()
            it.image(content.uiIcon).size(50f).grow().padBottom(5f).row()
            it.add(content.description).grow().padBottom(5f).wrap().row()
            it.add(content.details).grow().padBottom(5f).wrap().row()

            it.pack()
        })
        b
    }
) : LinkNode(content.name, x, y, ep) {
    override fun unlocked(): Boolean {
        return content.unlocked()
    }

    val button = (element as Button)
    override fun update() {
        super.update()
        if (unlocked()) {
            button.style = Button.ButtonStyle().apply {
                up= background44
                down= background44
                over= background44
            }
            return
        }
        parent?.let {
            if (it.unlocked()) {
                button.style = Texs.button3
                return
            }
        }
        button.style = Texs.button4

    }
}