package singularity.type

import arc.func.Prov
import arc.graphics.Color
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.struct.ObjectMap
import arc.struct.Seq
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.entities.Puddles
import mindustry.gen.Puddle
import mindustry.type.Liquid
import mindustry.world.Tile
import universecore.util.Empties

class ReactLiquid : Liquid {
  companion object {
    fun effectWith(fx: Effect, delScl: Float): ReactHandler {
      return effectWith(fx, 0.16f, delScl)
    }

    fun effectWith(fx: Effect, chance: Float, delScl: Float): ReactHandler {
      return effectWith(fx, chance, Color.white, delScl)
    }

    fun effectWith(fx: Effect, chance: Float, color: Color?, delScl: Float): ReactHandler {
      return ReactHandler { liquid: Liquid?, amount: Float, x: Float, y: Float ->
        if (Mathf.chanceDelta((chance * Mathf.clamp(amount * 2f, 0f, 2f)).toDouble())) fx.at(x, y, color)
        amount * delScl
      }
    }
  }

  private val reacts = ObjectMap<Liquid?, Seq<ReactHandler>?>()
  private val anyReact = Seq<ReactHandler>()

  var init: Runnable = Runnable {}

  constructor(name: String) : super(name)

  constructor(name: String, color: Color) : super(name, color)

  fun reactWith(other: Liquid, reacting: ReactHandler) {
    reacts.get(other) { Seq() }!!.add(reacting)
  }

  fun reactAny(reacting: ReactHandler?) {
    anyReact.add(reacting)
  }

  override fun init() {
    super.init()
    init.run()
  }

  override fun react(other: Liquid?, amount: Float, tile: Tile?, x: Float, y: Float): Float {
    var res = 0f
    for (react in reacts.get(other, Empties.nilSeq<ReactHandler?>())!!) {
      res += react.reactWith(other, amount, x, y)
    }

    for (react in anyReact) {
      res += react.reactWith(other, amount, x, y)
    }

    return res
  }

  override fun update(puddle: Puddle) {
    val tile = puddle.tile
    for (p in Geometry.d4) {
      val other = Vars.world.tile(tile.x + p.x, tile.y + p.y) ?: continue
      val otherPuddle = Puddles.get(other) ?: continue

      val x = (puddle.x + otherPuddle.x) / 2f
      val y = (puddle.y + otherPuddle.y) / 2f

      for (cons in reacts.get(otherPuddle.liquid, Empties.nilSeq<ReactHandler>())!!) {
        puddle.amount += cons.reactWith(otherPuddle.liquid, puddle.accepting, x, y)
      }

      for (func in anyReact) {
        puddle.amount += func.reactWith(otherPuddle.liquid, puddle.accepting, x, y)
      }
    }
  }

  fun interface ReactHandler {
    fun reactWith(other: Liquid?, amount: Float, x: Float, y: Float): Float
  }
}