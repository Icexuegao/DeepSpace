package singularity.world.blocks.product

import arc.func.Cons2
import mindustry.world.meta.Stats
import singularity.type.SglContents
import singularity.world.products.Producers
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsumers

open class AtomSchematicCrafter(name: String) : MediumCrafter(name) {
    public override fun init() {
        for (atomSchematic in SglContents.atomSchematics()) {
            consumers.add(atomSchematic.request)
            super.newProduce()
            produce!!.item(atomSchematic.item, 1)
        }

        super.init()
    }

    public override fun newConsume(): BaseConsumers {
         throw Exception()
    }

    public override fun <T : ConsumerBuildComp> newOptionalConsume(validDef: Cons2<T, BaseConsumers>, displayDef: Cons2<Stats, BaseConsumers>): BaseConsumers {
        throw Exception()
    }

    public override fun newProduce(): Producers {
        throw Exception()
    }
}