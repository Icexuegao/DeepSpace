package ice.content.unit

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Interval
import arc.util.io.Reads
import arc.util.io.Writes
import ice.audio.ISounds
import ice.content.ILiquids
import ice.content.IUnitTypes
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.base.FleshEntity
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.entities.Puddles
import mindustry.gen.Puddle
import kotlin.math.min

class Schizovegeta : IceUnitType("schizovegeta", SchizovegetaUnit::class.java) {
  init {
    bundle {
      desc(
        zh_CN, "青壤", "由血肉喷口缓慢孕育的活体培养囊,本身不具备攻击性,只会笨拙地蠕行移动.当其外膜在环境中自然破裂或被外力摧毁时,会释放出数颗至数十颗不等的肿瘤"
      )
    }
    speed = 0.3f
    health = 200f
    hitSize = 12f
    drawCell = false
    rotateSpeed = 1f
    outlineRadius = 3
    outlineColor = IceColor.r2
    createScorch = false
    deathSound = ISounds.chizovegeta

    legPhysicsLayer = false
    allowLegStep = true
    legStraightness = 0.3f
    stepShake = 0f
    legCount = 8
    legLength = 8f
    legGroupSize = 4
    lockLegBase = true
    legBaseUnder = true
    legContinuousMove = true
    legExtension = -2f
    legBaseOffset = 3f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.96f
    legForwardScl = 1.1f
    rippleScale = 0.2f
    legMoveSpace = 1f

    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
  }

  class SchizovegetaUnit : FleshEntity() {
    companion object {
      val bags: Array<TextureRegion> by lazy {
        Array(3) {
          Core.atlas.find(("schizovegeta-bag${it + 1}").appendModName())
        }
      }
      val scorchs: Array<TextureRegion> by lazy {
        Array(9) {
          Core.atlas.find(("schizovegeta-scorch-${it + 1}").appendModName())
        }
      }
    }

    val interval = Interval(2)
    var index = 0
    override fun drawBodyRegion(rotation: Float) {
      super.drawBodyRegion(rotation)
      Draw.rect(bags[index % 3], x, y, rotation)
    }

    override fun update() {
      super.update()
      if (interval.get(30f)) {
        index++
        if (index > 1000) index = 0
      }
      if (!interval.get(1, IceEffects.rand.random(20f, 40f))) return
      val solid = tileOn()?.floor()?.solid ?: true
      if (solid) return
      val p = Puddles.get(tileOn())
      if (!Vars.net.client() && p == null) {
        //do not create puddles clientside as that destroys syncing
        val puddle = Puddle.create()
        puddle.tile = tileOn()
        puddle.liquid = ILiquids.浓稠血浆
        puddle.amount = min(IceEffects.rand.random(20f), Puddles.maxLiquid)
        puddle.set(
          x + IceEffects.rand.random(-hitSize(), hitSize()), y + IceEffects.rand.random(-hitSize(), hitSize())
        )
        Puddles.register(puddle)
        puddle.add()
      }
    }

    override fun destroy() {
      (1..4).forEach { _ ->
        val x1 = IceEffects.rand.random(10f, 40f).run {
          if (IceEffects.rand.nextInt(2) > 0) this else -this
        }
        val y1 = IceEffects.rand.random(10f, 40f).run {
          if (IceEffects.rand.nextInt(2) > 0) this else -this
        }
        val create = IUnitTypes.丰穰之瘤.spawn(team, x, y, IceEffects.rand.random(0f, 360f))
        create.set(x + x1, y + y1)
        val puddle = Puddle.create()
        puddle.tile = Vars.world.tileWorld(create.x, create.y)
        puddle.liquid = ILiquids.浓稠血浆
        puddle.amount = IceEffects.rand.random(20f, 30f)
        puddle.set(create.x, create.y)
        Puddles.register(puddle)
        puddle.add()
      }
      super.destroy()
    }

    override fun read(read: Reads) {
      super.read(read)
      index = read.i()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.i(index)
    }
  }
}