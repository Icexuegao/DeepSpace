package singularity.world.blocks.product

import arc.func.Boolf
import arc.func.Cons
import arc.func.Intf
import arc.graphics.g2d.Draw
import arc.util.Structs
import mindustry.Vars
import mindustry.gen.Building
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.blocks.payloads.Payload
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Env
import singularity.world.components.PayloadBuildComp
import singularity.world.draw.DrawPayloadFactory
import universecore.world.consumers.ConsumeItemBase
import universecore.world.consumers.ConsumeType
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import kotlin.math.abs

open class PayloadCrafter(name: String) : NormalCrafter(name) {
    var itemCapacityMulti: Float = 2f
    var payloadCapacity: Int = 1
    var payloadSpeed: Float = 0.7f
    var payloadRotateSpeed: Float = 5f

    init {
        draw = object : DrawPayloadFactory<PayloadCrafterBuild?>() {
            init {
                spliceBits = Intf { obj: PayloadCrafterBuild? -> obj!!.blendBit() }
                drawPayload = Cons { e: PayloadCrafterBuild? ->
                    e!!.drawConstructingPayload()
                    e.drawPayload()
                }
            }
        }
        outputFacing = true
        outputsPayload = true
        rotate = true
        group = BlockGroup.payloads
        envEnabled = envEnabled or (Env.space or Env.underwater)
    }


    open inner class PayloadCrafterBuild : NormalCrafterBuild(), PayloadBuildComp {
        override fun takePayload(): Payload? {
            TODO("Not yet implemented")
        }
        override fun acceptUnitPayload(unit: Unit?): Boolean {
            return inputting() == null && !consumer!!.hasConsume() || consFilter!!.filter(this, ConsumeType.payload, unit!!.type, true)
        }

        override fun handlePayload(source: Building?, payload: Payload?) {
            TODO("Not yet implemented")
        }

        override fun canControlSelect(unit: Unit): Boolean {
            return acceptsPayload && !unit.spawnedByCore && unit.type.allowedInPayloads && payloads()!!.isEmpty() && acceptUnitPayload(unit) && unit.tileOn() != null && unit.tileOn().build === this
        }

        public override fun onControlSelect(player: Unit?) {
            handleUnitPayload(player, Cons { p: Payload? -> payloads()!!.add(p) })
        }

        public override fun shouldConsume(): Boolean {
            if (!super.shouldConsume()) return false
            return outputting() == null || abs(outputting()!!.x() - x) >= size * Vars.tilesize / 2f + 1 || abs(outputting()!!.y() - y) >= size * Vars.tilesize / 2f + 1
        }
        override fun acceptPayload(source: Building?, payload: Payload?): Boolean{
            return payloads()!!.total() < payloadCapacity()
}
        /*
        override fun acceptPayload(source: Building?, payload: Payload): Boolean {
            return (source === this || (acceptsPayload && inputting() == null && (!consumer!!.hasConsume()
                    || filter()!!.filter(this, ConsumeType.payload, payload.content(), true)))) && payloads()!!.total() < payloadCapacity()
        }*/

        public override fun sense(sensor: LAccess?): Double {
            if (sensor == LAccess.payloadCount) return payloads()!!.total().toDouble()
            return super.sense(sensor)
        }

        public override fun craftTrigger() {
            super.craftTrigger()
            if (!payloads()!!.isEmpty()) getPayload().set(x, y, rotdeg())
        }

        open fun drawConstructingPayload() {
            var p: ProducePayload<*>? = null
            if (producer!!.current != null && (producer!!.current!!.get<ProducePayload<*>>(ProduceType.payload).also { p = it }) != null) {
                Draw.draw(Layer.blockOver, Runnable { Drawf.construct(this, p!!.payloads[0].item, rotdeg() - 90f, progress(), workEfficiency(), totalProgress()) })
            }
        }

        public override fun acceptItem(source: Building, item: Item?): Boolean {
            val stack: ItemStack?
            return source.interactable(this.team) && hasItems
                    && (source === this || (!(consumer!!.hasConsume() || consumer!!.hasOptional()) || consFilter!!.filter(this, ConsumeType.item, item, acceptAll(ConsumeType.item))))
                    && items.get(item) < (if ((Structs.find<ItemStack>(consumer!!.current!!.get<ConsumeItemBase<*>>(ConsumeType.item)!!.consItems, Boolf { e: ItemStack? -> e!!.item === item }).also { stack = it }) != null) stack!!.amount * itemCapacityMulti else 0f)
        }
    }
}