package ice.world.content.blocks.science

import arc.func.Prov
import arc.math.Mathf
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.Time
import ice.content.IItems
import ice.graphics.IStyles
import ice.graphics.IceColor
import ice.ui.MenusDialog
import ice.ui.dialog.research.node.UCLinkNode
import ice.ui.menusDialog.ResearchDialog
import ice.ui.menusDialog.ResearchDialog.selectANode
import ice.world.content.blocks.IceBlockComponents.calwavetimeremain
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.draw.DrawMulti
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Iconc
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.Bar
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawRegion

class Laboratory(name: String) : IceBlock(name) {

    init {
        size = 4
        solid = true
        health = 2500
        update = true
        hasPower = true
        canOverdrive = false
        configurable = true
        hasItems = true
        itemCapacity = 20
        buildType = Prov(::LaboratoryBuild)
        requirements(Category.effect, ItemStack.with(IItems.石英玻璃, 50))
        drawers = DrawMulti(DrawRegion("-bottom"), DrawDefault(), DrawRegion("-light", 2f, true),
            DrawGlowRegion("-light").apply {
                color = IceColor.b4
                rotate = true
                rotateSpeed = 2f
            })
    }

    override fun setBars() {
        super.setBars()
        addBar("crafting") { build: LaboratoryBuild ->
            Bar({
                Iconc.crafting + " " + Strings.fixed(build.progress * 100f, 0) + " %" + calwavetimeremain(
                    build.progress, build.getProgressIncrease(build.craftTime) * build.timeScale() * 60 / Time.delta)
            }, {
                val ammo = Pal.ammo.cpy()
                ammo.lerp(IceColor.b4, build.progress)
            }, { build.progress })
        }
    }

    inner class LaboratoryBuild : IceBuild() {
        var progress: Float = 0f
        var totalProgress: Float = 0f
        var warmup: Float = 0f
        var craftTime = 360f

        override fun buildConfiguration(table: Table) {
            selectANode?.let {
                if (!it.shouldShown()) return@let
                table.table(IStyles.background101) { ta ->
                    ta.margin(18f)
                    it.show(ta)
                    ta.actions(Actions.alpha(0f), Actions.alpha(1f, 0.25f))
                    ta.pack()
                }.width(400f).row()
            }
            table.table {
                it.button(Icon.bookOpen, IStyles.imageButtonClean, 40f) {
                    if (!MenusDialog.isShown) MenusDialog.show()
                    MenusDialog.button.hide()
                    MenusDialog.button = ResearchDialog
                    MenusDialog.button.build(MenusDialog.conts)
                }.update {
                    table.pack()
                }.row()
            }

        }

        override fun updateTile() {
            selectANode?.update()

            warmup = if (efficiency > 0) {
                progress = 0f
                if (selectANode is UCLinkNode) {
                    val node = selectANode as UCLinkNode
                    if (!node.unlocked()) {
                        node.updateTime( delta())
                        progress = node.finishedtimeUnlock / node.timeUnlock
                    }
                }
                Mathf.approachDelta(warmup, 1f, 0.1f)
            } else {
                Mathf.approachDelta(warmup, 0f, 0.1f)
            }
            totalProgress += warmup * Time.delta


            items.each { item, amount ->
                selectANode?.let {
                    if (it.acceptItem(this, item)) {
                        items.set(item, amount - 1)
                        handleItem(this, item)
                    }
                }
            }
        }

        override fun acceptItem(source: Building?, item: Item?): Boolean {
            return items.get(item) < getMaximumAccepted(item) && selectANode?.acceptItem(source, item) ?: false
        }

        override fun handleItem(source: Building?, item: Item?) {
            if (selectANode?.acceptItem(source, item) ?: false) {
                selectANode?.handleItem(source, item)
            } else {
                super.handleItem(source, item)
            }
        }


        override fun warmup(): Float {
            return warmup
        }

        override fun totalProgress(): Float {
            return totalProgress
        }
    }
}