package ice.world.content.blocks.effect

import arc.func.Func2
import arc.func.Prov
import arc.math.Mathf
import ice.content.block.turret.TurretBullets
import ice.graphics.IceColor
import singularity.world.blocks.product.NormalCrafter
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.LightningVertex
import universecore.graphics.lightnings.generator.RandomGenerator
import universecore.world.consumers.cons.ConsumeLiquidCond
import kotlin.random.Random

open class Noise2dBlock(name: String) : NormalCrafter(name) {
  init {
    buildType = Prov(::Noise2dBuild)
    size = 1
    update = true
    health = 10
    configurable = true
    buildType = Prov(::Noise2dBuild)
    newConsume().add(ConsumeLiquidCond<Noise2dBuild>().apply {
      minFlammability = 0.1f
      maxFlammability = 1f
    })
  }

  inner class Noise2dBuild : NormalCrafterBuild() {
    val branch: RandomGenerator = RandomGenerator()
    var lig = RandomGenerator().apply {
      maxLength = 100f
      maxDeflect = 55f

      branchChance = 0.2f
      minBranchStrength = 0.8f
      maxBranchStrength = 1f
      branchMaker = Func2 { vert: LightningVertex?, strength: Float? ->
        branch.maxLength = 60 * strength!!
        branch.originAngle = vert!!.angle + Mathf.random(-90, 90)
        branch
      }
    }
    var con = LightningContainer()

    override fun draw() {
      super.draw()
      con.draw(x, y)
    }

    override fun update() {
      super.update()
      if (timer.get(0, 111f)) {
        // con.create(lig)
        TurretBullets.lightning(120f, 60f, 10f, 20f, IceColor.b4) { bullet ->
          lig
        }.apply {
          speed = 0f
        }.create(this, x, y, Random.nextInt(0, 360).toFloat()).apply {

        }
      }
      con.update()
    }
  }
}