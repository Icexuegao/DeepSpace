package singularity.world.blocks.distribute

import arc.func.Prov
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.ctype.Content
import mindustry.gen.Building
import mindustry.world.blocks.payloads.Payload
import singularity.world.components.PayloadBuildComp
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.distribution.GridChildType
import singularity.world.modules.PayloadModule

class PayloadIOPoint(name: String) : IOPoint(name) {
  init {
    this.acceptsPayload = true
    this.outputsPayload = true
    buildType = Prov(::PayloadIOPointBuild)
  }

  override fun setupRequestFact() {
  }

  inner class PayloadIOPointBuild : IOPointBuild(), PayloadBuildComp {
    override var payloads: PayloadModule = PayloadModule()
    override var carried: Boolean = false
    override var outputLocking: Boolean = false
    override var stackAlpha: Float = 0f
    override var blendBit: Int = 0
    override var outputting: Payload? = null
    override var inputting: Payload? = null
    override var payloadRotateSpeed: Float = 0f
    override var payloadSpeed: Float = 0f
    override var payloadCapacity: Int = 0

    override fun transBack() {
    }

    override fun resourcesSiphon() {
    }

    override fun resourcesDump() {
    }

    override fun valid(unit: DistMatrixUnitBuildComp?, type: GridChildType?, content: Content?): Boolean {
      return false
    }

    override fun onRemoved() {
      super.onRemoved()
      this.payloadBuildRemoved()
    }

    override fun onProximityUpdate() {
      super.onProximityUpdate()
      this.payloadProximityUpdated()
    }

    override fun pickedUp() {
      super.pickedUp()
      this.payloadPickedUp()
    }

    override fun drawTeamTop() {
      super.drawTeamTop()
      this.drawTeamTopEntry()
    }

    override fun takePayload(): Payload? {
      return super<PayloadBuildComp>.takePayload()
    }

    override fun updateTile() {
      super.updateTile()
      this.updatePayloads()
    }

    override fun handlePayload(source: Building?, payload: Payload?) {
      super<IOPointBuild>.handlePayload(source, payload)
      super<PayloadBuildComp>.handlePayload(source, payload)
    }

    override fun write(write: Writes) {
      super.write(write)
      this.writePayloads(write)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      this.readPayloads(read, revision)
    }
  }
}