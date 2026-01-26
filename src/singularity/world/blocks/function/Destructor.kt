package singularity.world.blocks.function

import arc.Core
import arc.func.Cons
import arc.func.Cons2
import arc.func.Func
import arc.func.Prov
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.meta.Stats
import singularity.type.SglContents
import singularity.world.blocks.product.NormalCrafter
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsumers

open class Destructor(name: String) : NormalCrafter(name) {
    override fun init() {
        for (atomSchematic in SglContents.atomSchematics()) {
            super.newConsume()
            consume!!.item(atomSchematic.item, 1)
            consume!!.energy(8f)
            consume!!.selectable = Prov { atomSchematic.researchVisibility() }
            consume!!.setConsTrigger(Cons { e: ConsumerBuildComp? ->
                atomSchematic.destructing(1)
            })
            consume!!.time(6f)
        }

        super.init()
    }

    override fun setBars() {
        super.setBars()
        addBar<NormalCrafterBuild?>("progress", Func { e: NormalCrafterBuild? ->
            val schematic = if (e!!.consumeCurrent == -1) null else SglContents.atomSchematics().get(e.consumeCurrent)
            Bar(
                {
                    if (schematic != null)
                        Core.bundle.format("bar.destructProgress", schematic.destructed(), schematic.researchConsume)
                    else
                        Core.bundle.get("bar.noSelect")
                },
                { Pal.bar },
                { if (schematic == null) 0f else schematic.researchProgress() }
            )
        })
    }

    public override fun newConsume(): BaseConsumers {
        return BaseConsumers(false)
    }

    public override fun <T : ConsumerBuildComp> newOptionalConsume(validDef: Cons2<T, BaseConsumers>, displayDef: Cons2<Stats, BaseConsumers>): BaseConsumers? {
        return null
    }

}