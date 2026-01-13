package ice.ui.dialog.research.node

import arc.Core
import arc.Events
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.scene.actions.Actions
import arc.scene.ui.Button
import arc.scene.ui.Image
import arc.scene.ui.ImageButton
import arc.scene.ui.Label
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import ice.Ice
import ice.graphics.IStyles
import ice.graphics.IStyles.background44
import ice.graphics.IStyles.imageButtonClean
import ice.graphics.IceColor
import ice.library.scene.ui.*
import ice.library.util.toStringi
import ice.ui.menusDialog.DataDialog
import ice.ui.MenusDialog
import ice.ui.menusDialog.ResearchDialog.SelectANodeEvent
import ice.ui.menusDialog.ResearchDialog.selectANode
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.ItemStack

class UCLinkNode(val content: UnlockableContent, x: Float, y: Float
) : LinkNode(content.name, x, y, Button(IStyles.button2)) {
    /** Item requirements for this content.  */
    var requirements: Array<ItemStack> = content.researchRequirements()

    /** Requirements that have been fulfilled. Always the same length as the requirement array.  */
    var finishedRequirements: Array<ItemStack?>? = null

    companion object{
        var collapser = false
    }
    var timeUnlock = 100f
    var finishedtimeUnlock = 0f

    init {
        var size = 0f
        requirements.forEach {
            size += it.amount
        }
        timeUnlock = size
        setupRequirements()
        (element as Button).apply {
            setSize(nodeSize)
            margin(10f)
            add(Image(content.uiIcon)).grow()
        }
    }
    fun updateTime(delta: Float) {
        finishedtimeUnlock+=delta
        if (finishedtimeUnlock>=timeUnlock)finishedtimeUnlock=timeUnlock
    }

    fun setupRequirements() {
        this.finishedRequirements = arrayOfNulls(requirements.size)
        //load up the requirements that have been finished if settings are available
        for (i in requirements.indices) {
            finishedRequirements?.set(i, ItemStack(requirements[i].item,
                if (Core.settings == null) 0 else Core.settings.getInt(
                    "${Ice.name}-req-" + content.name + "-" + requirements[i].item.name)))
        }
        finishedtimeUnlock = Core.settings.getFloat("${Ice.name}-req-" + content.name + "-finishedtimeUnlock", 0f)
    }

    override fun acceptItem(source: Building?, item: Item?): Boolean {
        var itemh = false
        requirements.let { itemsStacks ->
            if (itemsStacks.isEmpty()) return@let
            for (i in itemsStacks.indices) {
                val stack = itemsStacks[i]
                if (stack.item == item && finishedRequirements!![i]!!.amount < stack.amount) {
                    itemh = true
                }
            }
        }
        return itemh
    }

    override fun handleItem(source: Building?, item: Item?) {
        super.handleItem(source, item)
        finishedRequirements?.let { itemsStacks ->
            if (itemsStacks.isEmpty()) return@let

            for (stack in itemsStacks) {
                if (stack?.item == item) {
                    stack?.amount += 1
                }
            }
        }
        save()
    }

    fun checkUnlock(): Boolean {
        var un = true
        requirements.let { itemsStacks ->
            for (i in itemsStacks.indices) {
                val stack = itemsStacks[i]
                val fins = finishedRequirements!![i]!!
                if (fins.amount < stack.amount) {
                    un = false
                }
            }
        }
        if (requirements.isEmpty()) un = false
        if (finishedtimeUnlock < timeUnlock) un = false
        return un
    }

    fun save() {
        //按物料类型保存已完成需求
        finishedRequirements?.let {
            for (stack in it) {
                Core.settings.put("${Ice.name}-req-" + content.name + "-" + stack!!.item.name, stack.amount)
            }
        }
        Core.settings.put("${Ice.name}-req-" + content.name + "-finishedtimeUnlock", finishedtimeUnlock)
    }

    fun reset() {
        finishedRequirements?.let {
            for (stack in it) {
                stack?.amount = 0
            }
        }
        finishedtimeUnlock = 0f
        content.clearUnlock()
        save()
    }

    fun unlock() {
        save()
        content.unlock()
    }

    override fun unlocked(): Boolean {
        var par = true
        parent.forEach {
            if (!it.unlocked()) {
                par = false
            }
        }

        return content.unlocked() && par
    }

    override fun shouldShown(): Boolean {
        return true
    }

    override fun show(table: Table) {
        table.image(content.uiIcon).size(50f).row()
        table.addCR(content.localizedName).row()
        table.addCR(content.description).grow().wrap().row()
        table.addCR(content.details).grow().wrap().row()

        requirements.let { itemsStacks ->
            if (itemsStacks.isEmpty()) return@let
            table.collapser({ table ->
                table.addLine().pad(2f)
                table.addCR({ "研究时间: $timeUnlock / $finishedtimeUnlock" }).update {
                    it.setText("研究时间: $timeUnlock / ${finishedtimeUnlock.toStringi(2)}")
                    it.setColor(if (finishedtimeUnlock >= timeUnlock) IceColor.b4 else Pal.remove)
                }
                table.addLine().pad(2f)
                table.iTableG { items ->
                    var slsitem = itemsStacks.first().item
                    items.table {
                        var tmp = slsitem
                        val flun = {
                            it.clearChildren()
                            it.image(slsitem.uiIcon).size(60f).row()
                            it.addCR(slsitem.localizedName).row()
                        }
                        flun()
                        it.update {
                            if (tmp != slsitem) {
                                tmp = slsitem
                                flun()
                            }
                        }
                    }
                    items.add(object : Table() {
                        override fun drawChildren() {
                            val size = itemsStacks.size
                            val thick = Scl.scl(3f)
                            Lines.stroke(thick, IceColor.b4)


                            Draw.alpha(color.a * parentAlpha)
                            val zj = (width - 2 * marginLeft) / 2

                            if (size > 1) {
                                Lines.line(x + marginLeft, y + height / 2f, x + zj, y + height / 2f)

                                val y1 = y + Scl.scl(20f) + marginBottom
                                val y2 = y + height - Scl.scl(20f) - marginTop
                                Lines.line(x + zj, y1, x + zj, y2)
                                (1..size).forEach { int ->
                                    (y2 - y1) / size
                                    val f = Scl.scl(40f) * int
                                    val df = f - Scl.scl(20f)
                                    val x2 = x + (width - marginLeft - marginRight)
                                    when (int) {
                                        1 -> {
                                            Lines.line(x + zj, y1, x2, y1)
                                        }

                                        size -> {
                                            Lines.line(x + zj, y2, x2, y2)
                                        }

                                        else -> {

                                            Lines.line(x + zj, y + df + marginBottom, x2, y + df + marginBottom)
                                        }
                                    }

                                }
                            } else {
                                Lines.line(x + marginLeft, y + height / 2f, x + (width - 2 * marginLeft),
                                    y + height / 2f)
                            }
                            super.drawChildren()
                        }
                    }).marginRight(3f).marginLeft(3f).grow()
                    items.iTableG {
                        for (i in itemsStacks.indices) {
                            it.iTableG { it ->
                                val flun = {
                                    it.clearChildren()
                                    it.left()
                                    it.image(itemsStacks[i].item.uiIcon).tapped {
                                        slsitem = itemsStacks[i].item
                                    }.size(40f)


                                    it.add(Label("")).update {
                                        val rt = finishedRequirements!![i]!!.amount
                                        val color =
                                            if (rt == 0) Pal.remove else if (rt < itemsStacks[i].amount) IceColor.b3 else IceColor.b4
                                        it.setColor(color)
                                        it.setText(" ${itemsStacks[i].amount} / $rt")
                                    }.row()
                                }
                                flun()
                            }.row()
                        }
                    }
                }.pad(3f).row()
                table.addLine().pad(2f)
            }, true) {
                collapser
            }.grow().row()
        }

        table.table { ta ->
            val button = ImageButton(Icon.cancel, imageButtonClean)
            button.clicked {
                table.actions(Actions.alpha(0f, 0.25f), Actions.run {
                    selectANode = null
                    Events.fire(SelectANodeEvent())
                })
            }
            button.resizeImage(40f)
            button.setDisabled {
                !MenusDialog.isShown()
            }
            ta.add(button)
            ta.button(Icon.book, imageButtonClean, 40f) {
                DataDialog.showBlock(content)
                ta.scenes = MenusDialog.scene
            }
            requirements.let { itemsStacks ->
                if (itemsStacks.isEmpty()) return@let
                ta.button(Icon.tree, imageButtonClean, 40f) {
                    collapser = !collapser
                }
            }
        }.row()
    }

    override fun update() {
        super.update()
        val button = (element as Button)
        var par = true
        parent.forEach {
            if (!it.unlocked()) {
                par = false
            }
        }
        if (checkUnlock())unlock()

        if (par) {
            if (unlocked()) {
                button.style = Button.ButtonStyle().apply {
                    up = background44
                    down = background44
                    over = background44
                }
            } else {
                //半解锁
                button.style = IStyles.button3
            }

        } else {
            //no
            button.style = IStyles.button4
        }
    }
}