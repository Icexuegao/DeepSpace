package ice.entities.effect

import arc.func.Cons
import arc.graphics.Color
import mindustry.entities.Effect
import mindustry.gen.EffectState
import mindustry.gen.Posc

class IEffect(apply: IEffect.() -> Unit) : Effect() {
    private var runTime = 0f
    private var run: Cons<EffectState> = Cons {}

    init {
        apply(this)
    }

    fun set(life: Float, renderer: Cons<EffectContainer>) {
        this.lifetime = life
        this.renderer = renderer
        this.clip = 50f
    }

    fun setRun(time: Float, run: Cons<EffectState>) {
        runTime = time
        this.run = run
    }

    override fun add(x: Float, y: Float, rotation: Float, color: Color?, data: Any?) {
        val entity = IEffectState()
        entity.effect = this
        entity.rotation = baseRotation + rotation
        entity.data = data
        entity.lifetime = lifetime
        entity.set(x, y)
        entity.color.set(color)
        if (followParent && data is Posc) {
            entity.parent = data
            entity.rotWithParent = rotWithParent
        }
        entity.add()
    }

    inner class IEffectState : EffectState() {
        var d = true
        override fun update() {
            if (effect is IEffect && d) {
                val effect1 = effect as IEffect
                if (fin() > effect1.runTime) {
                    effect1.run.get(this)
                    d = false
                }
            }
            super.update()
        }
    }
}