package ice.content.block.power

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import ice.IVars
import ice.content.IItems
import ice.graphics.IceColor
import ice.graphics.TextureRegionDelegate
import ice.library.util.toStringi
import ice.ui.bundle.BaseBundle
import ice.world.draw.DrawBuild
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.game.Team
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.ui.Bar
import mindustry.world.Tile
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.world.blocks.SglBlock

class WindGenerator : SglBlock("windGenerator") {
  var basePowerProduction = 70f
  var rotator: TextureRegion by TextureRegionDelegate("$name-rotator")
  var range: Int = 2
  init {
    BaseBundle.bundle {
      desc(zh_CN,"风力发电机","简易的风力发电机,效率跟随风场变化")
    }
    size = 2
    health = 100
    solid = true
    update = true
    buildType = Prov(::WindGeneratorBuild)
    hasPower = true
    outputsPower = true
    requirements(Category.power, IItems.铅锭, 20, IItems.黄铜锭, 30, IItems.铜锭, 15, IItems.单晶硅, 10)
    draw = DrawMulti(DrawDefault(), DrawBuild<WindGeneratorBuild> {
      Draw.rect(rotator, x, y,  totalProgress * powerEffecct())
    }, DrawRegion("-top"))
  }

  override fun setStats() {
    super.setStats()
    stats.add(Stat.basePowerGeneration, basePowerProduction, StatUnit.powerSecond)
  }

  override fun setBars() {
    super.setBars()
    if (hasPower && outputsPower) {
      addBar("power") {entity: WindGeneratorBuild ->
        Bar({
          "发电效率: ${(entity.powerEffecct() * 100).toStringi(0)}%"
        }, {Pal.powerBar}, entity::powerEffecct)
      }
    }
  }

  val d82 = arrayOf(
    Point2(3, 0),
    Point2(3, 3),
    Point2(0, 3),
    Point2(-3, 3),
    Point2(-3, 0),
    Point2(-3, -3),
    Point2(0, -3),
    Point2(3, -3),
  )
  override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
    var y=true
    d82.forEach {
      Vars.world.tile(tile.x + it.x, tile.y + it.y)?.build?.let {
        y=false
      }
    }
    return super.canPlaceOn(tile, team, rotation) &&y
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    Drawf.dashRect(IceColor.b4,(x-2.5f)*8f, (y-2.5f)*8f, (6)*8f, (6)*8f)
  }
  inner class WindGeneratorBuild : SglBuilding() {
    var totalProgress: Float = 0f
    var warmup: Float = 0f

    override fun drawSelect() {
      super.drawSelect()
      Drawf.dashRect(IceColor.b4,(tileX()-2.5f)*8f, (tileY()-2.5f)*8f, (6)*8f, (6)*8f)
    }
    override fun updateTile() {
      super.updateTile()
      if (shouldConsume()) {
        warmup = (Mathf.lerpDelta(warmup, 0f, 0.001f))
        return
      }

      warmup = if (shouldConsume() && consumeValid()) {
        (Mathf.lerpDelta(warmup, 1f, 0.006f))
      } else {
        (Mathf.lerpDelta(warmup, 0f, 0.001f))
      }

      totalProgress += delta()
    }

    fun powerEffecct(): Float {
      return powerProduction * 60 / basePowerProduction
    }

    override fun getPowerProduction(): Float {
      return (basePowerProduction + IVars.windField.getWindVector(tileX(), tileY()).magnitude * basePowerProduction) / 60f
    }
  }
}