package ice.content.block.power

import arc.Core
import arc.Events
import arc.func.Cons
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Point2
import arc.struct.Seq
import arc.util.Strings
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import ice.IVars
import ice.graphics.IceColor
import ice.library.struct.texture.LazyTextureSingleDelegate
import ice.library.util.toStringi
import ice.world.draw.DrawBuild
import ice.world.draw.DrawFull
import ice.world.draw.DrawMulti
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.game.EventType
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.input.Placement
import mindustry.ui.Bar
import mindustry.world.Tile
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.world.blocks.SglBlock

class WindGenerator(name: String) : SglBlock(name) {
  companion object {
    val builds = Seq<WindGeneratorBuild>(false)

    init {
      Events.on(EventType.BlockBuildEndEvent::class.java) {
        Core.app.post {
          builds.forEach {
            it.ifBuild()
          }
        }
      }
    }
  }

  var basePowerProduction = 70f
  var rotator: TextureRegion by LazyTextureSingleDelegate("${this.name}-rotator")
  var range: Int = 2
  val vtt = Vars.tilesize.toFloat()
  private val side: Float by lazy {(2 * range + size) * vtt}

  init {
    solid = true
    update = true
    buildType = Prov(::WindGeneratorBuild)
    hasPower = true
    outputsPower = true
    placeableLiquid = true
    drawers = DrawMulti(DrawDefault(), DrawBuild<WindGeneratorBuild> {
      Draw.z(Layer.turret)
      Draw.rect(rotator, x, y, totalProgress)
    }, DrawRegion("-top"), DrawFull("full"))
  }

  override fun setStats() {
    super.setStats()
    stats.add(Stat.basePowerGeneration, basePowerProduction, StatUnit.powerSecond)
  }

  override fun setBars() {
    super.setBars()
    if (hasPower && outputsPower) {
      addBar("powerEffecct") {entity: WindGeneratorBuild ->
        Bar({
          "发电效率: ${(entity.powerEffecct() * 100).toStringi(0)}%"
        }, {Pal.powerBar}, entity::powerEffecct)
      }
      addBar("powerProduction") {entity: WindGeneratorBuild ->
        Bar(
          {
            Core.bundle.format(
              "bar.poweroutput", Strings.fixed(entity.powerProduction * 60 * entity.timeScale(), 1)
            )
          }, {Pal.powerBar}, entity::powerEffecct
        )
      }
    }
  }

  fun each(x: Int, y: Int, lenght: Int, ct: Cons<Tile>) {
    for (ox in (x..<x + lenght)) {
      for (oy in (y..<y + lenght)) {
        val tile: Tile = Vars.world.tile(ox, oy) ?: continue
        ct.get(tile)
      }
    }
  }

  fun isCanOver(tile: Tile, build: WindGeneratorBuild?): Boolean {
    var k = true
    var posx = tile.x + sizeOffset
    var posy = tile.y + sizeOffset
    posx -= range
    posy -= range
    each(posx, posy, range * 2 + size) {
      build?.let {building ->
        if (building == it.build) return@each
      }

      if (it.build != null && it.block().solid) {
        k = false
        build?.tmpTile?.addUnique(it)
      }
      if (it.build == null && it.block() != Blocks.air) {
        k = false
        build?.tmpTile?.addUnique(it)
      }
    }
    return k
  }

  override fun changePlacementPath(points: Seq<Point2>, rotation: Int) {
    Placement.calculateNodes(points, this, rotation) {point: Point2, other: Point2 ->
      other.dst(point).toInt() in (size..size + range)
    }
  }

  override fun canPlaceOn(tile: Tile, team: Team, rotation: Int): Boolean {
    return super.canPlaceOn(tile, team, rotation) && isCanOver(tile, null)
  }

  override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
    super.drawPlace(x, y, rotation, valid)
    Drawf.dashRect(IceColor.b4, (x + sizeOffset - range - 0.5f) * vtt, (y + sizeOffset - range - 0.5f) * vtt, side, side)
  }

  inner class WindGeneratorBuild : SglBuilding() {

    var totalProgress: Float = 0f
    var warmup: Float = 0f
    var valid = true
    override fun init(tile: Tile, team: Team, shouldAdd: Boolean, rotation: Int): Building {
      builds.add(this)
      return super.init(tile, team, shouldAdd, rotation)
    }

    override fun read(read: Reads, revision: Byte) {
      super.read(read, revision)
      warmup = read.f()
    }

    override fun write(write: Writes) {
      super.write(write)
      write.f(warmup)
    }

    override fun remove() {
      builds.remove(this)
      super.remove()
    }

    override fun afterReadAll() {
      super.afterReadAll()
      ifBuild()
    }

    override fun drawSelect() {
      super.drawSelect()
      tmpTile.forEach {
        it.build?.let {it1 ->
          Drawf.selected(it1, Tmp.c1.set(Pal.remove).a(Mathf.absin(4f, 1f)))
        }
      }
      Drawf.dashRect(IceColor.b4, (tileX() + sizeOffset - range - 0.5f) * vtt, (tileY() + sizeOffset - range - 0.5f) * vtt, side, side)
    }

    fun ifBuild() {
      tmpTile.clear()
      valid = isCanOver(tile, this)
    }

    val tmpTile = Seq<Tile>()

    override fun updateTile() {
      super.updateTile()

      warmup = if (valid && enabled) Mathf.lerpDelta(warmup, 1f, 0.006f) else Mathf.lerpDelta(warmup, 0f, 0.006f)

      totalProgress += delta() * warmup * powerEffecct()
    }

    fun powerEffecct() = powerProduction * 60 / basePowerProduction * warmup

    override fun getPowerProduction(): Float {
      return (basePowerProduction / 3f * 2f + IVars.windField.getMovingNoiseValue(tileX(), tileY()) * basePowerProduction) / 60f * warmup
    }
  }
}