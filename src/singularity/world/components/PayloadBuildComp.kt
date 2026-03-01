package singularity.world.components

import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Nullable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.gen.Building
import mindustry.gen.Unit
import mindustry.type.PayloadSeq
import mindustry.world.blocks.payloads.Payload
import mindustry.world.blocks.payloads.PayloadBlock
import mindustry.world.blocks.payloads.PayloadBlock.PayloadBlockBuild
import singularity.world.modules.PayloadModule
import universecore.components.blockcomp.BuildCompBase
import kotlin.math.abs
import kotlin.math.max

interface PayloadBuildComp : BuildCompBase {
  //  @Annotations.BindField("payloadCapacity")
  var payloadCapacity: Int

  //  @Annotations.BindField("payloadSpeed")
  var payloadSpeed: Float

  //   @Annotations.BindField("payloadRotateSpeed")
  var payloadRotateSpeed: Float

  //  @Annotations.BindField("inputting")
  var inputting: Payload?
  var outputting: Payload?

  //   @Annotations.BindField("blendBit")
  var blendBit: Int

  //  @Annotations.BindField("stackAlpha")
  var stackAlpha: Float

  //   @Annotations.BindField("outputLocking")
  var outputLocking: Boolean

  //  @Annotations.BindField("carried")
  var carried: Boolean

  //  @Annotations.BindField(value = "payloads", initialize = "new singularity.world.modules.PayloadModule()")
  var payloads: PayloadModule

  fun handleOutputPayload(): Float {
    if (outputting != null) {
      val outputVec = outputtingOffset()
      outputting!!.set(
        Mathf.approachDelta(outputting!!.x(), building.x + outputVec.x, payloadSpeed), Mathf.approachDelta(outputting!!.y(), building.y + outputVec.y, payloadSpeed), Angles.moveToward(outputting!!.rotation(), Tmp.v1.set(outputVec).add(building).sub(outputting).angle(), payloadRotateSpeed * Time.delta)
      )
      return 1 - Mathf.clamp(Mathf.len(building.x + outputVec.x - outputting!!.x(), building.y + outputVec.y - outputting!!.y()) / outputVec.len())
    }

    return 0f
  }

  fun outputtingOffset(): Vec2 {
    return tempVec.set(
      Angles.trnsx((building.rotation * 90).toFloat(), block.size * Vars.tilesize / 2f), Angles.trnsy((building.rotation * 90).toFloat(), block.size * Vars.tilesize / 2f)
    )
  }

  fun blends(direction: Int): Boolean {
    return PayloadBlock.blends(building, direction)
  }

  fun handleInputPayload(): Float {
    if (inputting != null) {
      inputting!!.set(
        Mathf.approachDelta(inputting!!.x(), building.x, payloadSpeed), Mathf.approachDelta(inputting!!.y(), building.y, payloadSpeed), Mathf.approachDelta(inputting!!.rotation(), (building.rotation * 90).toFloat(), payloadRotateSpeed)
      )
      return 1 - Mathf.len(inputting!!.x() - building.x, inputting!!.y() - building.y) / (block.size * Vars.tilesize / 2f)
    }

    return 0f
  }

  //   @MethodEntry(entryMethod = "onRemoved")
  fun payloadBuildRemoved() {
    if (!carried) {
      for (payload in payloads.iterate()) {
        payload.dump()
      }
    }
  }

  //   @MethodEntry(entryMethod = "onProximityUpdate")
  fun payloadProximityUpdated() {
    var bit = 0
    for (i in 0..3) {
      if (blends(i)) bit = bit or (1 shl i)
    }
    blendBit = (bit)
  }

  // @MethodEntry(entryMethod = "pickedUp")
  fun payloadPickedUp() {
    carried = true
  }

  // @MethodEntry(entryMethod = "drawTeamTop")
  fun drawTeamTopEntry() {
    carried = false
  }

  // @get:MethodEntry(entryMethod = "getPayloads", override = true)
    /*val payloads: PayloadSeq
        get() {
            temp.clear()
            for (payload in payloads!!.iterate()) {
                temp.add(payload.content())
            }
            return temp
        }*/

  // @MethodEntry(entryMethod = "takePayload", override = true)
  fun takePayload(): Payload? {
    return payloads.take()
  }

  // @get:MethodEntry(entryMethod = "getPayload", override = true)
  /*  val payload: Payload?
        get() = payloads().get()*/

  // @MethodEntry(entryMethod = "acceptPayload", paramTypes = ["mindustry.gen.Building -> source", "mindustry.world.blocks.payloads.Payload -> payload"], override = true)
  /*  fun acceptPayload(source: Building?, payload: Payload?): Boolean {
        return payloads!!.total() < payloadCapacity()
    }*/

  fun popPayload() {
    if (outputLocking) return
    outputting = (takePayload())
    outputLocking = (outputting != null)
    if (outputLocking) stackAlpha = (0f)
  }

  fun acceptUnitPayload(unit: Unit): Boolean {
    return inputting == null
  }

  //  @MethodEntry(entryMethod = "updateTile")
  fun updatePayloads() {
    if (!outputLocking) {
      stackAlpha = (Mathf.approachDelta(stackAlpha, (if (inputting != null && outputting == null) 0 else 1).toFloat(), payloadSpeed / (block.size * Vars.tilesize / 2f)))
    }

    for (payload in payloads.iterate()) {
      payload.update(null, building)
    }
    val offset = outputtingOffset()
    val targetTile: Building?
    var front = false
    if (max(abs(offset.x), abs(offset.y)) <= block.size / 2f * Vars.tilesize + 0.5f) {
      targetTile = building.front()
      front = true
    } else targetTile = Vars.world.buildWorld(building.x + offset.x, building.y + offset.y)
    val canDump = targetTile == null || !targetTile.tile.solid()
    val canMove = targetTile != null && (targetTile.block.acceptsPayload || targetTile.block.outputsPayload) && targetTile.interactable(building.team)

    if (!outputLocking && (canDump || canMove)) popPayload()
    val inputProgress = handleInputPayload()
    val outputProgress = handleOutputPayload()

    if (inputProgress >= 0.999f) {
      if (payloads.total() < payloadCapacity) {
        payloads.add(inputting)
        stackAlpha = (1f)
        inputting = (null)
      }
    }

    if (canDump && !canMove) {
      PayloadBlock.pushOutput(outputting, outputProgress)
    }

    if (outputProgress >= 0.999f) {
      if (canMove) {
        if (targetTile.acceptPayload(building, outputting)) {
          val rot = outputting!!.rotation()
          targetTile.handlePayload(building, outputting)

          if (!front && targetTile is PayloadBlockBuild<*>) {
            targetTile.payload.set(targetTile.x, targetTile.y, rot)
            targetTile.payVector.setZero()
            targetTile.payRotation = rot
          }

          released(outputting)
          outputting = (null)
          outputLocking = false
        }
      } else if (canDump) {
        if (outputting!!.dump()) {
          released(outputting)
          outputting = (null)
          outputLocking = false
        }
      }
    }
  }

  fun released(payload: Payload?) {}

  // @MethodEntry(entryMethod = "handlePayload", paramTypes = ["mindustry.gen.Building -> source", "mindustry.world.blocks.payloads.Payload -> payload"], override = true)
  fun handlePayload(source: Building?, payload: Payload?) {
    if (source !== this) {
      inputting = (payload)
    } else {
      payloads.add(payload)
      stackAlpha = (1f)
    }
  }

  fun drawPayload() {
    if (inputting != null) {
      inputting!!.draw()
    }
    if (outputting != null) {
      outputting!!.draw()
    }
    val p: Payload? = this.payloads.get()
    if (p != null) {
      Draw.scl(stackAlpha)
      Draw.alpha(stackAlpha)
      p.draw()
      Draw.reset()
    }
  }

  //   @MethodEntry(entryMethod = "write", paramTypes = "arc.util.io.Writes -> write")
  fun writePayloads(write: Writes) {
    payloads.write(write)
    writepay(inputting, write)
    writepay(outputting, write)
    write.f(stackAlpha)
    write.bool(outputLocking)
  }

  fun writepay(@Nullable payload: Payload?, write: Writes) {
    if (payload == null) {
      write.bool(false)
    } else {
      write.bool(true)
      payload.write(write)
    }
  }

  // @MethodEntry(entryMethod = "read", paramTypes = ["arc.util.io.Reads -> read", "byte -> revision"])
  fun readPayloads(read: Reads, revision: Byte) {
    payloads.read(read, revision < building.version())
    inputting = (Payload.read(read))
    outputting = (Payload.read(read))
    stackAlpha = (read.f())
    outputLocking = (read.bool())
  }

  companion object {
    val temp: PayloadSeq = PayloadSeq()
    val tempVec: Vec2 = Vec2()
  }
}