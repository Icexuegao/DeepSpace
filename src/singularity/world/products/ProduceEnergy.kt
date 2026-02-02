package singularity.world.products

import arc.scene.ui.layout.Table
import mindustry.gen.Building
import mindustry.world.meta.Stats
import singularity.world.components.NuclearEnergyBuildComp
import singularity.world.consumers.SglConsumeEnergy
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.producers.BaseProduce
import universecore.world.producers.ProduceType

class ProduceEnergy<T>(var product: Float) : BaseProduce<T>() where T : Building, T : NuclearEnergyBuildComp, T : ProducerBuildComp {
    public override fun type(): ProduceType<ProduceEnergy<*>> {
        return SglProduceType.energy
    }

    public override fun buildIcons(table: Table) {
        SglConsumeEnergy.buildNuclearIcon(table!!, product)
    }

    public override fun merge(baseProduce: BaseProduce<T>) {
        if (baseProduce is ProduceEnergy<*>) {
            product += baseProduce.product

            return
        }
        throw IllegalArgumentException("only merge product with same type")
    }

    public override fun produce(entity: T) {
    }

    public override fun update(entity: T) {
        entity!!.handleEnergy(product * parent!!.cons!!.delta(entity) * multiple(entity))
        if (entity.getEnergy() > entity.energyCapacity()) entity.energy()!!.set(entity.energyCapacity())
    }

    public override fun valid(entity: T): Boolean {
        return true
    }

    public override fun dump(entity: T) {
        entity!!.dumpEnergy()
    }

    public override fun display(stats: Stats) {
        stats.add(SglStat.productEnergy, product * 60, SglStatUnit.neutronFluxSecond)
    }
}