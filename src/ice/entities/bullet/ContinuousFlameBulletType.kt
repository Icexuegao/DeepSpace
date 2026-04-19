package ice.entities.bullet

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.gen.Bullet
import mindustry.graphics.Drawf
import mindustry.graphics.Layer

class ContinuousFlameBulletType(damage: Float = 0f) :ContinuousBulletType() {
  init {
    this.damage = damage
  }

  var lightStroke: Float = 40f
  var width: Float = 3.7f
  var oscScl: Float = 1.2f
  var oscMag: Float = 0.02f
  var divisions: Int = 25

  var drawFlare: Boolean = true
  var flareColor: Color = Color.valueOf("e189f5")
  var flareWidth: Float = 3f
  var flareInnerScl: Float = 0.5f
  var flareLength: Float = 40f
  var flareInnerLenScl: Float = 0.5f
  var flareLayer: Float = Layer.bullet - 0.0001f
  var flareRotSpeed: Float = 1.2f
  var rotateFlare: Boolean = false
  var lengthInterp: Interp = Interp.slope

  /** Lengths, widths, ellipse panning, and offsets, all as fractions of the base width and length. Stored as an 'interleaved' array of values: LWPO1 LWPO2 LWPO3...  */
  var lengthWidthPans: FloatArray = floatArrayOf(
    1.12f, 1.3f, 0.32f,
    1f, 1f, 0.3f,
    0.8f, 0.9f, 0.2f,
    0.5f, 0.8f, 0.15f,
    0.25f, 0.7f, 0.1f,
  )

  var colors = arrayOf(
    Color.valueOf("eb7abe").a(0.55f),
    Color.valueOf("e189f5").a(0.7f),
    Color.valueOf("907ef7").a(0.8f),
    Color.valueOf("91a4ff"),
    Color.white.cpy()
  )

  init {
    optimalLifeFract = 0.5f
    length = 120f
    hitEffect = Fx.hitFlameBeam
    hitSize = 4f
    drawSize = 420f
    lifetime = 16f
    hitColor = colors[1]!!.cpy().a(1f)
    lightColor = hitColor
    lightOpacity = 0.7f
    laserAbsorb = false
    ammoMultiplier = 1f
    pierceArmor = true
  }

  override fun draw(b: Bullet) {
    val mult = b.fin(lengthInterp)
    val realLength = Damage.findLength(b, length * mult, laserAbsorb, pierceCap)

    val sin = Mathf.sin(Time.time, oscScl, oscMag)

    for(i in colors.indices) {
      Draw.color(colors[i]!!.write(Tmp.c1).mul(0.9f).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)))
      Drawf.flame(
        b.x,
        b.y,
        divisions,
        b.rotation(),
        realLength * lengthWidthPans[i * 3] * (1f - sin),
        width * lengthWidthPans[i * 3 + 1] * mult * (1f + sin),
        lengthWidthPans[i * 3 + 2]
      )
    }

    if (drawFlare) {
      Draw.color(flareColor)
      Draw.z(flareLayer)

      val angle = Time.time * flareRotSpeed + (if (rotateFlare) b.rotation() else 0f)

      for(i in 0..3) {
        Drawf.tri(b.x, b.y, flareWidth, flareLength * (mult + sin), i * 90 + 45 + angle)
      }

      Draw.color()
      for(i in 0..3) {
        Drawf.tri(b.x, b.y, flareWidth * flareInnerScl, flareLength * flareInnerLenScl * (mult + sin), i * 90 + 45 + angle)
      }
    }

    Tmp.v1.trns(b.rotation(), realLength * 1.1f)
    Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, lightOpacity)
    Draw.reset()
  }

  override fun currentLength(b: Bullet): Float {
    return length * b.fin(lengthInterp)
  }

  override fun drawLight(b: Bullet?) {
    //no light drawn here
  }
}