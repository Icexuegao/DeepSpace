package ice.world.content.unit.entity.base

import arc.graphics.Color
import arc.math.Mathf
import ice.content.ILiquids
import ice.world.content.unit.entity.Schizovegeta
import ice.world.meta.IceEffects
import mindustry.entities.Effect
import mindustry.entities.Puddles
import mindustry.gen.Puddle

abstract class FleshEntity : Entity() {
  override fun destroy() {
    super.destroy()
    repeat((hitSize / 3).toInt()) {
      Effect.decal(Schizovegeta.scorchs.random(), x + Mathf.random(-hitSize, hitSize), y + Mathf.random(-hitSize, hitSize), Mathf.random(4) * 90f, 3600f, Color.white.cpy().a(0f))
    }
    val puddle = Puddle.create()
    puddle.tile = tileOn()?:return
    puddle.liquid = ILiquids.浓稠血浆
    puddle.amount = IceEffects.rand.random(20f, Puddles.maxLiquid)
    puddle.set(x+ Mathf.random(-hitSize, hitSize), y+ Mathf.random(-hitSize, hitSize))
    Puddles.register(puddle)
    puddle.add()
  }
}