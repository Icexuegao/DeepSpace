package singularity.world.blocks.turrets

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Interp
import arc.util.Tmp
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.generator.VectorLightningGenerator
import kotlin.math.min

open class LightLaserBulletType : EmpLightningBulletType() {
  var laserEffect: Effect = Fx.lancerLaserShootSmoke
  var length: Float = 80f
  var innerScl: Float = 0.75f
  var edgeScl: Float = 1.2f
  var innerWidth: Float = 3f
  var width: Float = 6f
  var edgeWidth: Float = 10f

  var colors = arrayOf(Pal.lancerLaser.cpy().mul(1f, 1f, 1f, 0.4f), Pal.lancerLaser, Color.white)

  var lightnings: Int = 2
  var lightningTime: Float = 5f
  var generator: VectorLightningGenerator = object : VectorLightningGenerator() {
    override fun remove() {

    }

    init {
      minInterval = 4f
      maxInterval = 12f
      maxSpread = 9f
    }
  }
  var lightningMinWidth: Float = 1.8f
  var lightningMaxWidth: Float = 2.75f

  init {
    speed = 0f
    lifetime = 30f
    collides = false
    absorbable = false
    hittable = false

    keepVelocity = false

    hitEffect = Fx.hitLaserBlast
    shootEffect = Fx.hitLancer

    despawnEffect = Fx.none

    pierce = true
  }

  override fun init() {
    super.init()
    range = length
    hitColor = colors[1]
    drawSize = range
  }

  override fun init(b: Bullet, cont: LightningContainer) {
    Sounds.shootSpectre.at(b.x, b.y, 1.2f)

    cont.lifeTime = lifetime
    cont.time = lightningTime
    cont.lerp = Interp.linear
    cont.minWidth = lightningMinWidth
    cont.maxWidth = lightningMaxWidth

    Damage.collideLaser(b, range, false, true, -1)
    laserEffect.at(b.x, b.y, b.rotation(), b.fdata * 0.75f)
    generator.vector.set(b.fdata, 0f).setAngle(b.rotation())

    for (i in 0..<lightnings) {
      cont.create(generator)
    }
  }

  override fun draw(b: Bullet) {
    super.draw(b)

    val len = min(b.fdata, range) / 2

    val fin2 = b.fin(Interp.pow2)
    val out2 = 1 - fin2
    Tmp.v1.set(len / 3, 0f).setAngle(b.rotation()).scl(1 + fin2)
    val dx = b.x + Tmp.v1.x
    val dy = b.y + Tmp.v1.y

    Draw.color(colors[0])
    Drawf.tri(dx, dy, edgeWidth * out2, (len / 3 - 6) * (1 + fin2) * edgeScl, b.rotation() - 180)
    Drawf.tri(dx, dy, edgeWidth * out2, len * (1 + 1.85f * fin2), b.rotation())

    Draw.color(colors[1])
    Drawf.tri(dx, dy, width * out2, (len / 3 - 6) * (1 + fin2), b.rotation() - 180)
    Drawf.tri(dx, dy, width * out2, len * (1 + 1.85f * fin2), b.rotation())

    Draw.color(colors[2])
    Drawf.tri(dx, dy, innerWidth * out2, (len / 3 - 6) * (1 + fin2) * innerScl, b.rotation() - 180)
    Drawf.tri(dx, dy, innerWidth * out2, len * (1 + 1.85f * fin2), b.rotation())
  }
}