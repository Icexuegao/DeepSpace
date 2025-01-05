package ice.world.blocks.effects.digitalStorage

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.scene.ui.Label
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import ice.ui.TableExtend.tableG
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.consumers.ConsumeItems
import mindustry.world.modules.ItemModule

class DigitalStorage(name: String) : Block(name) {


    init {
        health = 400
        size = 3
        hasItems = false
        destructible = true
        conductivePower = true
        hasPower=true
        unloadable = false
        acceptsItems = true
        conveyorPlacement = true
        requirements(Category.effect, ItemStack.with(Items.copper, 1))
        buildType = Prov(DigitalStorage::DigitalStorageBuild)
        configurable = true
        update = true

    }

    class DigitalStorageBuild : Building() {
        private val buildings = Seq<Building>()

        override fun draw() {
            super.draw()
            Draw.color(Color.red)
            Fill.rect(x, y, 8f, 8f)
            buildings.forEach {
                Draw.z(100f)
                Fill.rect(it.x, it.y, 8f, 8f)
            }

        }


        private val digital = ItemModule()
        private val digitalMax = 500

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            buildings.clear()
            proximity.forEach {
                fg(it.tile)
            }
        }

        override fun update() {
            onProximityUpdate()
            digital.updateFlow()
            updateBuilding()
            super.update()
        }

        private fun updateBuilding() {
            buildings.forEach { building ->
                val block1 = building.block
                if (block1 is GenericCrafter) {
                    //输入请求
                    block1.outputItems?.forEach { outputItems ->

                        val get = building.items.get(outputItems.item)
                        if (get > 0 && acceptItem(building, outputItems.item)) {
                            building.items.remove(outputItems.item, 1)
                            handleItem(building, outputItems.item)
                        }

                    }

                    //输出
                    building.block.findConsumer<ConsumeItems> { cons -> cons is ConsumeItems }.items.forEach items@{ consitem ->
                        if (!digital.has(consitem.item)) return@items
                        if (building.acceptItem(this, consitem.item)) {
                            digital.remove(consitem.item, 1)
                            building.handleItem(this, consitem.item)
                        }
                    }
                }
                if (building is DigitalUnloader.DigitalUnloaderBuild) {
                    if (building.sortItem != null && digital.get(building.sortItem) > 0 && building.acceptItem(
                            this, building.sortItem
                        )
                    ) {
                        building.handleItem(this, building.sortItem)
                        digital.remove(building.sortItem, 1)
                    }
                }
            }
        }

        private fun fg(tile: Tile) {
            if (buildings.contains(tile.build)) return
            if (tile.build is DigitalConduit.DigitalConduitBuild) {
                (tile.build as DigitalConduit.DigitalConduitBuild).building = this
                buildings.addUnique(tile.build)
                each(tile)
            }
            if (tile.build != null) {
                buildings.addUnique(tile.build)
            }
        }

        fun each(tile: Tile) {
            val build = Vars.world.tile(tile.x + 1, tile.y.toInt())
            if (build != null) {
                fg(build)
            }
            val build1 = Vars.world.tile(tile.x - 1, tile.y.toInt())
            if (build != null) {
                fg(build1)
            }
            val build2 = Vars.world.tile(tile.x.toInt(), tile.y + 1)
            if (build != null) {
                fg(build2)
            }
            val build3 = Vars.world.tile(tile.x.toInt(), tile.y - 1)
            if (build != null) {
                fg(build3)
            }
        }

        override fun acceptItem(source: Building, i: Item): Boolean {
            return source !is DigitalUnloader.DigitalUnloaderBuild && digital.get(i) < digitalMax
        }

        override fun handleItem(source: Building, i: Item) {
            digital.add(i, 1)
        }

        override fun buildConfiguration(table: Table) {
            table.clear()
            var i = 0
            table.table(Tex.pane) {
                Vars.content.items().forEach { item ->
                    if (digital.get(item) <= 0) return@forEach
                    it.tableG {
                        i++
                        it.image(item.uiIcon).size(32f).padRight(2f).tooltip(item.localizedName)
                        val fLabel = Label("${digital.get(item)}")
                        fLabel.update {
                            fLabel.setText("${digital.get(item)}")
                        }
                        it.add(fLabel).size(80f, 40f)
                    }
                    if (i % 6 == 0) it.row()
                }
            }
            super.buildConfiguration(table)
        }

        override fun write(write: Writes) {
            digital.write(write)
            super.write(write)
        }

        override fun read(read: Reads, revision: Byte) {
            digital.read(read)
            super.read(read, revision)
        }
    }
}