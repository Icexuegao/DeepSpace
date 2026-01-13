package singularity.world.components.distnet

import mindustry.ctype.Content
import mindustry.ctype.ContentType
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.distribution.GridChildType
import universecore.components.blockcomp.BuildCompBase

interface IOPointComp : BuildCompBase {
    //  @Annotations.BindField("parentMat")
    fun parent(): DistMatrixUnitBuildComp? {
        return null
    }

    //@Annotations.BindField("parentMat")
    fun parent(valur: DistMatrixUnitBuildComp?) {}

    //  @Annotations.BindField("config")
    fun gridConfig(): TargetConfigure? {
        return null
    }

    // @Annotations.BindField("config")
    fun gridConfig(value: TargetConfigure?) {}

    val iOBlock: IOPointBlockComp?
        get() = getBlock(IOPointBlockComp::class.java)

    fun configTypes(): Array<GridChildType?>? {
        return this.iOBlock!!.configTypes.toSeq().toArray<GridChildType?>(GridChildType::class.java)
    }

    fun configContentTypes(): Array<ContentType?>? {
        return this.iOBlock!!.supportContentType.toSeq().toArray<ContentType?>(ContentType::class.java)
    }

    fun valid(unit: DistMatrixUnitBuildComp?, type: GridChildType?, content: Content?): Boolean
}