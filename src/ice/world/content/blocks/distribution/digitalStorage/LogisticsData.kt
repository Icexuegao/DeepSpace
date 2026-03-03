package ice.world.content.blocks.distribution.digitalStorage

import ice.library.struct.UnsafeLinkedList
import ice.library.struct.graph.UserData
import mindustry.world.blocks.production.GenericCrafter

class LogisticsData(
    val build: LogisticsBlock.LogisticsBuild? = null,
    val crafter: GenericCrafter.GenericCrafterBuild? = null
) : UserData<LogisticsData>() {
    val conduits = UnsafeLinkedList<HubConduit.DigitalConduitBuild>()
    val inputs = UnsafeLinkedList<LogisticsInput.DigitalInputBuild>()
    val outputs = UnsafeLinkedList<LogisticsOutput.DigitalUnloaderBuild>()
    val crafterLinks = UnsafeLinkedList<GenericCrafter.GenericCrafterBuild>()
    var hub: LogisticsHub.DigitalStorageBuild? = null

    override fun maintain(left: LogisticsData?, right: LogisticsData?) {
        conduits.clear()
        inputs.clear()
        outputs.clear()
        crafterLinks.clear()
        hub = null

        val hasLeft = left != null
        val hasRight = right != null

        if (build != null)
            when (build.block().blockType) {
                LogisticsBlock.Type.CONDUIT -> conduits += build as HubConduit.DigitalConduitBuild

                LogisticsBlock.Type.INPUT -> inputs += build as LogisticsInput.DigitalInputBuild

                LogisticsBlock.Type.OUTPUT -> outputs += build as LogisticsOutput.DigitalUnloaderBuild

                LogisticsBlock.Type.HUB -> hub = build as LogisticsHub.DigitalStorageBuild
            }

        if (crafter != null) crafterLinks += crafter

        if (hasLeft) {
            conduits += left.conduits
            inputs += left.inputs
            outputs += left.outputs
            crafterLinks += left.crafterLinks
            if (hub == null) hub = left.hub
        }

        if (hasRight) {
            conduits += right.conduits
            inputs += right.inputs
            outputs += right.outputs
            crafterLinks += right.crafterLinks
            if (hub == null) hub = right.hub
        }
    }
}