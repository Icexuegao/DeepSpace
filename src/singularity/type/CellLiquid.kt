package singularity.type

import arc.Events
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.util.Nullable
import arc.util.Time
import arc.util.Tmp
import ice.world.content.liquid.IceLiquid
import mindustry.Vars
import mindustry.content.Liquids
import mindustry.entities.Puddles
import mindustry.game.EventType
import mindustry.gen.Puddle
import mindustry.graphics.Layer
import mindustry.type.Liquid
import mindustry.world.Tile
import kotlin.math.max
import kotlin.math.min

open class CellLiquid :IceLiquid {
  var colorFrom: Color? = Color.white.cpy()
  var colorTo: Color? = Color.white.cpy()
  var cells: Int = 6

  @Nullable
  var spreadTarget: Liquid? = null
  var maxSpread: Float = 0.75f
  var spreadConversion: Float = 1.2f
  var spreadDamage: Float = 0.11f
  var removeScaling: Float = 0.25f

  constructor(name: String, color: Color) :super(name, color)
  constructor(name: String, color: String) :super(name, color)

  override fun update(puddle: Puddle) {
    if (!Vars.state.rules.fire) return

    if (spreadTarget != null) {
      val scaling = Mathf.pow(Mathf.clamp(puddle.amount / Puddles.maxLiquid), 2f)
      var reacted = false

      for(point in Geometry.d4c) {
        val tile = puddle.tile.nearby(point)
        if (tile != null && tile.build != null && tile.build.liquids != null && tile.build.liquids.get(spreadTarget) > 0.0001f) {
          val amount = min(tile.build.liquids.get(spreadTarget), maxSpread * Time.delta * scaling)
          tile.build.liquids.remove(spreadTarget, amount * removeScaling)
          Puddles.deposit(tile, this, amount * spreadConversion)
          reacted = true
        }
      }

      //damage thing it is on
      if (spreadDamage > 0 && puddle.tile.build != null && puddle.tile.build.liquids != null && puddle.tile.build.liquids.get(spreadTarget) > 0.0001f) {
        reacted = true

        //spread in 4 adjacent directions around thing it is on
        val amountSpread = min(puddle.tile.build.liquids.get(spreadTarget) * spreadConversion, maxSpread * Time.delta) / 2f
        for(dir in Geometry.d4) {
          val other = puddle.tile.nearby(dir)
          if (other != null) {
            Puddles.deposit(puddle.tile, other, puddle.liquid, amountSpread)
          }
        }

        puddle.tile.build.damage(spreadDamage * Time.delta * scaling)
      }

      //spread to nearby puddles
      for(point in Geometry.d4) {
        val tile = puddle.tile.nearby(point)
        if (tile != null) {
          val other = Puddles.get(tile)
          if (other != null && other.liquid === spreadTarget) {
            //TODO looks somewhat buggy when outputs are occurring
            val amount = min(other.amount, max(maxSpread * Time.delta * scaling, other.amount * 0.25f * scaling))
            other.amount -= amount
            puddle.amount += amount
            reacted = true
            if (other.amount <= Puddles.maxLiquid / 3f) {
              other.remove()
              Puddles.deposit(tile, puddle.tile, this, max(amount, Puddles.maxLiquid / 3f))
            }
          }
        }
      }

      if (reacted && this === Liquids.neoplasm) {
        Events.fire<EventType.Trigger?>(EventType.Trigger.neoplasmReact)
      }
    }
  }

  override fun react(other: Liquid?, amount: Float, tile: Tile?, x: Float, y: Float): Float {
    if (other === spreadTarget) {
      return amount
    }
    return 0f
  }

  override fun drawPuddle(puddle: Puddle) {
    super.drawPuddle(puddle)

    val baseLayer =
      if (puddle.tile != null && puddle.tile.block().solid || puddle.tile.build != null) Layer.blockOver else Layer.debris - 0.5f

    val id = puddle.id
    val amount = puddle.amount
    val x = puddle.x
    val y = puddle.y
    val f = Mathf.clamp(amount / (Puddles.maxLiquid / 1.5f))
    val smag = if (puddle.tile.floor().isLiquid) 0.8f else 0f
    val sscl = 25f
    val length = max(f, 0.3f) * 9f

    rand.setSeed(id.toLong())
    for(i in 0..<cells) {
      Draw.z(baseLayer + i / 1000f + (id % 100) / 10000f)
      Tmp.v1.trns(rand.random(360f), rand.random(length))
      val vx = x + Tmp.v1.x
      val vy = y + Tmp.v1.y

      Draw.color(colorFrom, colorTo, rand.random(1f))

      Fill.circle(
        vx + Mathf.sin(Time.time + i * 532, sscl, smag),
        vy + Mathf.sin(Time.time + i * 53, sscl, smag),
        f * 3.8f * rand.random(0.35f, 1f) * Mathf.absin(Time.time + ((i + id) % 60) * 54, 75f * rand.random(1f, 2f), 1f)
      )
    }

    Draw.color()
  }
}
