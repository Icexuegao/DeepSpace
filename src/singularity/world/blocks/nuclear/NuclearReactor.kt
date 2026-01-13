package singularity.world.blocks.nuclear

import arc.Core
import arc.Events
import arc.audio.Sound
import arc.func.Floatf
import arc.func.Prov
import arc.math.Angles
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Strings
import arc.util.Tmp
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.game.EventType
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.ui.Bar
import mindustry.world.meta.Stats
import singularity.contents.SglItems
import singularity.world.SglFx
import singularity.world.blocks.product.NormalCrafter
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import singularity.world.particles.SglParticleModels
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsumers
import universecore.world.consumers.ConsumeType
import kotlin.math.max

open class NuclearReactor(name: String) : NormalCrafter(name) {
    var maxHeat: Float = 100f
    var productHeat: Float = 0.2f
    var smokeThreshold: Float = 50f
    var explosionRadius: Int = 19
    var explosionDamageBase: Int = 350
    var explodeEffect: Effect = SglFx.reactorExplode
    var coolants: Seq<BaseConsumers?> = Seq<BaseConsumers?>()
    var fuels: Seq<BaseConsumers> = Seq<BaseConsumers>()
    var explosionSound: Sound = Sounds.explosion
    var explosionSoundVolume: Float = 3.5f
    var explosionSoundPitch: Float = 0.8f
    var consItems: ObjectSet<Item?> = ObjectSet<Item?>()

    init {
        hasEnergy = true
        oneOfOptionCons = false
        outputEnergy = true
        autoSelect = true
        canSelect = false
        buildType= Prov(::NuclearReactorBuild)
    }

    fun newReact(fuel: Item, time: Float, output: Float, prodWaste: Boolean) {
        newConsume()
        consume!!.time(time)
        consume!!.item(fuel, 1)

        newProduce()
        produce!!.energy(output)
        if (prodWaste) produce!!.item(SglItems.nuclear_waste, 1)

        fuels.add(consume)
    }

    fun addCoolant(consHeat: Float) {
        val cons = newOptionalConsume({ e: ConsumerBuildComp, c: BaseConsumers ->
            val entity: NuclearReactorBuild = e.getBuilding(NuclearReactorBuild::class.java)
            entity.heat -= consHeat * entity.delta()
            entity.heat = max(entity.heat, 0f)
        }, { s: Stats?, c: BaseConsumers? ->
            s!!.add(SglStat.effect) { t: Table? ->
                t!!.row()
                t.add(
                    (Core.bundle.get("misc.absorbHeat") + ": " + Strings.autoFixed(consHeat * 60, 2) + SglStatUnit.heat.localized() + Core.bundle.get("misc.perSecond"))
                )
            }
        })
        Floatf { e: ConsumerBuildComp -> e.building.delta() }.also {
            cons?.consDelta = it
        }
        cons?.setConsDelta { e: ConsumerBuildComp -> e.building.delta()
        }?. consValidCondition { e:NuclearReactorBuild ->
            e.heat > 0
        } ?.optionalAlwaysValid = true
    }

    fun addTransfer(output: ItemStack) {
        newOptionalProduct()
        consume!!.optionalAlwaysValid = false
        produce!!.item(output.item, output.amount)
    }

    fun addTransfer(output: LiquidStack) {
        newOptionalProduct()
        consume!!.optionalAlwaysValid = false
        produce!!.liquid(output.liquid, output.amount)
    }

    override fun setStats() {
        super.setStats()
        stats.add(SglStat.heatProduct, Strings.autoFixed(productHeat * 60, 2) + SglStatUnit.heat.localized() + Core.bundle.get("misc.perSecond"))
        stats.add(SglStat.maxHeat, maxHeat, SglStatUnit.heat)
    }

    override fun setBars() {
        super.setBars()
        addBar<NuclearReactorBuild?>("efficiency") { e: NuclearReactorBuild? ->
            Bar({ Core.bundle.get("misc.efficiency") + ": " + Strings.autoFixed(e!!.smoothEfficiency * 100, 0) + "%" }, { Pal.accent }, { e!!.smoothEfficiency })
        }
        addBar<NuclearReactorBuild?>("heat") { e: NuclearReactorBuild? ->
            Bar(
                Core.bundle.get("misc.heat"), Pal.lightOrange
            ) { e!!.heat / maxHeat }
        }
    }

    override fun init() {
        super.init()
        for (cons in consumers) {
            for (stack in cons.get(ConsumeType.item)!!.consItems!!) {
                consItems.add(stack.item)
            }
        }
    }

    inner class NuclearReactorBuild : NormalCrafterBuild() {
        var heat: Float = 0f
        var smoothEfficiency: Float = 0f

        override fun consEfficiency(): Float {
            return fuelItemsTotal() / itemCapacity * super.consEfficiency() * (1 - Mathf.clamp((items.get(SglItems.nuclear_waste) - itemCapacity / 3f) / (itemCapacity), 0f, 1f))
        }

        override fun shouldConsume(): Boolean {
            return super.shouldConsume() && (items == null || items.get(SglItems.nuclear_waste) < itemCapacity)
        }

        override fun updateTile() {
            super.updateTile()
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, consEfficiency(), 0.02f)
            heat += productHeat * consumer.consDelta()

            if (heat > maxHeat) {
                onOverTemperature()
            }

            dump(SglItems.nuclear_waste)

            if (heat > smokeThreshold) {
                val smoke = 1.0f + (heat - smokeThreshold) / (maxHeat - smokeThreshold)
                if (Mathf.chance(smoke / 20.0 * delta())) {
                    Fx.reactorsmoke.at(
                        x + Mathf.range(size * Vars.tilesize / 2f), y + Mathf.range(size * Vars.tilesize / 2f)
                    )
                }
            }
        }

        override fun sense(sensor: LAccess?): Double {
            if (sensor == LAccess.heat) {
                return (heat / maxHeat).toDouble()
            }

            return super.sense(sensor)
        }

        fun onOverTemperature() {
            Events.fire(EventType.Trigger.thoriumReactorOverheat)
            kill()
        }

        override fun onDestroyed() {
            var fuel = 0
            for (cons in fuels) {
                for (stack in cons.get(ConsumeType.item)!!.consItems!!) {
                    fuel += items.get(stack.item)
                }
            }
            super.onDestroyed()

            if ((fuel < itemCapacity / 3f && heat < maxHeat / 2) || !Vars.state.rules.reactorExplosions) return

            Effect.shake(8f, 120f, x, y)
            val strength = (explosionDamageBase * fuel).toFloat()
            Damage.damage(x, y, explosionRadius.toFloat() * Vars.tilesize, strength)

            explosionSound.at(x, y, explosionSoundPitch, explosionSoundVolume)

            explodeEffect.at(x, y, 0f, explosionRadius.toFloat() * Vars.tilesize)
            Angles.randLenVectors(System.nanoTime(), Mathf.random(28, 36), 3f, 7.5f) { x: Float, y: Float ->
                val len = Tmp.v1.set(x, y).len()
                SglParticleModels.floatParticle.create(this.x, this.y, Pal.reactorPurple, x, y, Mathf.random(5f, 7f) * ((len - 3) / 4.5f))
            }
        }

        fun fuelItemsTotal(): Float{
            var result = 0f
            for (cons in fuels) {
                for (stack in cons.get(ConsumeType.item)!!.consItems!!) {
                    result += items.get(stack.item)
                }
            }
            return result
        }

        override fun acceptItem(source: Building, item: Item?): Boolean {
            return super.acceptItem(source, item) && (!consItems.contains(item) || (consItems.contains(item) && fuelItemsTotal() < itemCapacity))
        }
    }
}