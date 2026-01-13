package singularity.world.blocks.distribute

import arc.func.Prov
import arc.struct.ObjectMap
import arc.struct.OrderedSet
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.gen.Building
import mindustry.world.blocks.payloads.Payload
import singularity.world.blocks.distribute.matrixGrid.RequestHandlers
import singularity.world.components.PayloadBuildComp
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.distribution.GridChildType

class PayloadIOPoint(name: String) : IOPoint(name) {
    init {
        acceptsPayload = true
        outputsPayload = acceptsPayload
        buildType= Prov(::PayloadIOPointBuild)
    }

    public override fun setupRequestFact() {
    }

    override var requestFactories= ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandlers.RequestHandler<*>>>()
    override var configTypes= OrderedSet<GridChildType>()
    override var supportContentType=OrderedSet<ContentType>()
    inner class PayloadIOPointBuild : IOPointBuild(), PayloadBuildComp {
        override fun transBack() {
        }

        override fun resourcesSiphon() {
        }

        override fun resourcesDump() {
        }

        override fun valid(unit: DistMatrixUnitBuildComp?, type: GridChildType?, content: Content?): Boolean {
            return false
        }

        override fun takePayload(): Payload? {
           return payloads()!!.take()
        }

        override fun handlePayload(source: Building?, payload: Payload?) {
            if (source !== this) {
                inputting(payload)
            } else {
                payloads()!!.add(payload)
                stackAlpha(1f)
            }
        }
    }
}