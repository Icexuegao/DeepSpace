package singularity.contents.override

import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import mindustry.world.draw.DrawRegion
import singularity.contents.SglItems
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawMultiSgl
import universecore.util.OverrideContentList

class OverrideBlocks : OverrideContentList {
    override fun load() {
        doOverrideContent(
            Blocks.melter.also { oldMelter = it },
            object : NormalCrafter("melter_override") {
                init {
                    requirements(Category.crafting, ItemStack.with(Items.copper, 30, Items.lead, 35, Items.graphite, 45))
                    autoSelect = true
                    canSelect = false
                    health = 200
                    hasPower = true
                    hasLiquids = hasPower

                    newConsume()
                    consume!!.items(*ItemStack.with(Items.scrap, 1))
                    consume!!.power(1f)
                    consume!!.time(10f)
                    newProduce()
                    produce!!.liquid(Liquids.slag, 0.2f)

                    newConsume()
                    consume!!.items(*ItemStack.with(SglItems.black_crystone, 1))
                    consume!!.power(1f)
                    consume!!.time(10f)
                    newProduce()
                    produce!!.liquid(Liquids.slag, 0.2f)

                    draw = DrawMultiSgl(DrawRegion("-bottom"), DrawLiquidTile(), DrawDefault())
                }
            }.also { Blocks.melter = it }
        )

        doOverrideContent(
            Blocks.pulverizer.also { oldPulverizer = it },
            object : NormalCrafter("pulverizer_override") {
                init {
                    requirements(Category.crafting, ItemStack.with(Items.copper, 30, Items.lead, 25))
                    craftEffect = Fx.pulverize
                    updateEffect = Fx.pulverizeSmall
                    hasPower = true
                    hasItems = hasPower
                   // ambientSound = Sounds.grinding
                    ambientSoundVolume = 0.025f
                    autoSelect = true
                    canSelect = false

                    newConsume()
                    consume!!.time(40f)
                    consume!!.item(Items.scrap, 1)
                    consume!!.power(0.50f)
                    newProduce()
                    produce!!.item(Items.sand, 1)

                    newConsume()
                    consume!!.time(40f)
                    consume!!.item(SglItems.black_crystone, 1)
                    consume!!.power(0.50f)
                    newProduce()
                    produce!!.item(Items.sand, 1)

                    draw = DrawMultiSgl(
                        DrawDefault(),
                        object : DrawRegion("-rotator") {
                            init {
                                spinSprite = true
                                rotateSpeed = 2f
                            }
                        },
                        DrawRegion("-top")
                    )
                }
            }.also { Blocks.pulverizer = it }
        )
    }

    companion object {
        var oldMelter: Block? = null
        var oldPulverizer: Block? = null
    }
}