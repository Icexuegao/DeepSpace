package singularity.world.components.distnet

import arc.struct.Seq
import mindustry.Vars
import singularity.world.distribution.DistributeNetwork
import singularity.world.modules.DistributeModule
import universecore.components.blockcomp.BuildCompBase

interface DistElementBuildComp : BuildCompBase {
    fun distributor(): DistributeModule?

    // @Annotations.BindField("priority")
    fun priority(): Int {
        return 0
    }

    //@Annotations.BindField("priority")
    fun priority(priority: Int) {}

    // @Annotations.BindField(value = "netLinked", initialize = "new arc.struct.Seq<>()")
    fun netLinked(): Seq<DistElementBuildComp>? {
        return null
    }

    // @Annotations.BindField("matrixEnergyBuffered")
    fun matrixEnergyBuffered(): Float {
        return 0f
    }

    //  @Annotations.BindField("matrixEnergyBuffered")
    fun matrixEnergyBuffered(set: Float) {}

    fun updateNetStat() {}

    fun networkValided() {}

    fun networkUpdated() {}

    fun linked(target: DistElementBuildComp?) {}

    fun delinked(target: DistElementBuildComp?) {}

    fun linkable(other: DistElementBuildComp?): Boolean {
        return true
    }

    val distBlock: DistElementBlockComp?
        get() = getBlock(DistElementBlockComp::class.java)

    fun networkRemoved(remove: DistElementBuildComp) {
        distributor()!!.distNetLinks.removeValue(remove.building.pos())
    }

    fun link(target: DistElementBuildComp) {
        if (!linkable(target) || !target.linkable(this)) return

        if (this.distBlock!!.isNetLinker) distributor()!!.distNetLinks.addUnique(target.building.pos())
        if (target.distBlock!!.isNetLinker) target.distributor()!!.distNetLinks.addUnique(building.pos())

        updateNetLinked()
        target.updateNetLinked()

        if (target.distBlock!!.isNetLinker) {
            distributor()!!.network.add(target.distributor()!!.network)
        } else {
            target.distributor()!!.network.remove(target)
            distributor()!!.network.add(target)
        }

        target.linked(this)
        linked(target)
    }

    fun deLink(target: DistElementBuildComp) {
        distributor()!!.distNetLinks.removeValue(target.building.pos())
        target.distributor()!!.distNetLinks.removeValue(building.pos())

        updateNetLinked()
        target.updateNetLinked()

        for (element in distributor()!!.network) {
            element.networkRemoved(target)
        }

        DistributeNetwork().flow(this)
        DistributeNetwork().flow(target)

        target.delinked(this)
        delinked(target)
    }

    fun updateNetLinked() {
        netLinked()!!.clear()

        if (this.distBlock!!.isNetLinker) {
            for (i in 0..<distributor()!!.distNetLinks.size) {
                val entity = Vars.world.tile(distributor()!!.distNetLinks.get(i))
                if (entity == null || entity.build !is DistElementBuildComp) continue
                if (!netLinked()!!.contains(entity.build as DistElementBuildComp?)) netLinked()!!.add(entity.build as DistElementBuildComp?)
            }
        }
    }

    // @Annotations.MethodEntry(entryMethod = "onProximityAdded")
    fun distNetAdd() {
        updateNetLinked()
        for (comp in netLinked()!!) {
            if (this.distBlock!!.isNetLinker) {
                comp.distributor()!!.network.add(distributor()!!.network)
            } else comp.distributor()!!.network.add(this)
        }
    }

    // @Annotations.MethodEntry(entryMethod = "onProximityRemoved")
    fun distNetRemove() {
        distributor()!!.distNetLinks.clear()
        distributor()!!.network.remove(this)
        updateNetLinked()
    }

    fun frequencyUse(): Int {
        return this.distBlock!!.topologyUse()
    }

    fun matrixEnergyConsume(): Float {
        return this.distBlock!!.matrixEnergyUse()
    }

    fun matrixEnergyProduct(): Float {
        return 0f
    }
}