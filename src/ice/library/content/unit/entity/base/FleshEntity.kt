package ice.library.content.unit.entity.base

import arc.graphics.Color
import arc.math.Mathf
import ice.content.ILiquids
import ice.library.content.unit.entity.Schizovegeta
import ice.library.meta.IceEffects
import mindustry.entities.Effect
import mindustry.entities.Puddles
import mindustry.gen.Puddle

abstract class FleshEntity : Entity() {
    override fun destroy() {
        super.destroy()
        Effect.decal(Schizovegeta.Companion.scorchs.random(), x, y, Mathf.random(4) * 90f, 3600f, Color.white.cpy().a(0f))
        val puddle = Puddle.create()
        puddle.tile = tileOn()
        puddle.liquid = ILiquids.浓稠血浆
        puddle.amount = IceEffects.rand.random(20f, Puddles.maxLiquid)
        puddle.set(x, y)
        Puddles.register(puddle)
        puddle.add()
    }
}