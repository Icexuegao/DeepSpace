package ice.world.content.blocks.effect

import arc.func.Func2
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.util.Tmp
import ice.IVars
import ice.content.IItems
import mindustry.content.Items
import singularity.world.blocks.SglBlock
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.LightningVertex
import universecore.graphics.lightnings.generator.RandomGenerator

open class Noise2dBlock(name: String) : SglBlock(name) {
  init {
    buildType = Prov(::Noise2dBuild)
    size = 1
    update = true
    health = 10
    configurable = true
    buildType = Prov(::Noise2dBuild)
    newConsume().apply {
      items(IItems.铜锭,1)
      time(50f)
    }
  }

  override fun setStats() {
    super.setStats()
    for (baseConsumers in consumers) {
      baseConsumers.display(stats)
    }
  }


  inner class Noise2dBuild : SglBuilding() {
    val branch: RandomGenerator = RandomGenerator()

    var con = LightningContainer()

    override fun draw() {
    //  Vars.world.tiles.forEach {
     //   Draw.color(Tmp.c1.set(Color.blue).lerp(Color.red,  IVars.windField.getMovingNoiseValue(it.x.toInt(), it.y.toInt())))
      //  Fill.rect(it.x*8f, it.y*8f, 8f, 8f)
    //  }
      super.draw()
      Tmp.v1.set(0f,24f).setAngle(IVars.windField.getMovingNoiseValue(tileX(), tileY()))
     // con.draw(x, y)
      Draw.rect(Items.copper.uiIcon, x+Tmp.v1.x, y+Tmp.v1.y)
    }

    override fun update() {
      super.update()
      if (timer.get(0, 111f)) {
       /* // con.create(lig)
        TurretBullets.lightning(120f, 60f, 10f, 20f, IceColor.b4) { bullet ->
          lig
        }.apply {
          speed = 0f
        }.create(this, x, y, Random.nextInt(0, 360).toFloat()).apply {

        }*/
      }
      con.update()
    }
  }
}