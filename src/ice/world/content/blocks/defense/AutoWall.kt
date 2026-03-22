package ice.world.content.blocks.defense

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import ice.core.StaticTile
import ice.graphics.TextureRegionDelegate
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer

class AutoWall(name: String) : Wall(name) {
  var regions: Array<TextureRegion?> = arrayOfNulls(48)
  var regionLarge: TextureRegion by TextureRegionDelegate("$name-large")
  val regionAtlas: TextureRegion by TextureRegionDelegate("$name-atlas")

  var baseColor: Color = Color.valueOf("#C8C8E4")

  init {
    allowRectanglePlacement = true
  }

  override fun load() {
    super.load()

    val width = regionAtlas.width + regionAtlas.x
    val height = regionAtlas.height + regionAtlas.y

    var idx = 0
    var arrY = regionAtlas.y
    while (arrY < height) {
      var arrX = regionAtlas.x
      while (arrX < width) {
        regions[idx++] = TextureRegion(regionAtlas.texture, arrX, arrY, 32, 32)
        arrX += 32
      }
      arrY += 32
    }
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    Drawf.dashSquare(
      baseColor,
      ((x and 1.inv()) * Vars.tilesize + 4).toFloat(),
      ((y and 1.inv()) * Vars.tilesize + 4).toFloat(),
      (2 * Vars.tilesize).toFloat()
    )
  }

  inner class AutoWallBuild : IceBuild() {
    private var index = 0
    private var isLarge = false

    fun checkBuild(other: Building): Boolean {
      return other is AutoWallBuild && other.team === team
    }

    override fun draw() {
      // 绘制瓦片和边缘
      if (!isLarge) Draw.rect(regions[(tileX() + tileY()) % 2 + 33], x, y)
      else if (tileX() == (tileX() and 1.inv()) && tileY() == (tileY() and 1.inv())) Draw.rect(
        regionLarge,
        x + 4,
        y + 4
      )
      if (index != 255) {
        Draw.z(Layer.block + 0.1f)
        Draw.rect(regions[StaticTile.tileMap[index]], x, y)
      }
    }

    override fun drawTeam() {
      // 当绘制-large贴图时,对覆盖的4个瓦片都覆盖team角标是没有必要的(并且这并不美观,至少Neil如此认为),因此我们可以仅绘制左下角的角标
      if (!(isLarge && !(tileX() == (tileX() and 1.inv()) && tileY() == (tileY() and 1.inv())))) {
        Draw.z(Layer.block + 0.2f)
        super.drawTeam()
      }
    }

    override fun updateProximity() {
      super.updateProximity()
      proximityTileUpdate()
      var other: Building
      for (point in StaticTile.proximityPoint) {
        other = Vars.world.build(tileX() + point.x, tileY() + point.y)
        if (other is AutoWallBuild && other.team === this.team) other.proximityTileUpdate()
      }
    }

    fun proximityTileUpdate() {
      val xLarge = tileX() and 1.inv()
      val yLarge = tileY() and 1.inv()

      index = 0
      var i = 0
      while (i < StaticTile.proximityPoint.size) {
        if (checkBuild(
            Vars.world.build(
              tileX() + StaticTile.proximityPoint[i].x, tileY() + StaticTile.proximityPoint[i].y
            )
          )
        ) index = index or (1 shl i)
        i += 1
      }

      isLarge =
        checkBuild(Vars.world.build(xLarge, yLarge)) && checkBuild(Vars.world.build(xLarge + 1, yLarge)) && checkBuild(
          Vars.world.build(xLarge, yLarge + 1)
        ) && checkBuild(Vars.world.build(xLarge + 1, yLarge + 1))
    }
  }
}