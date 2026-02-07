package ice.world.content.blocks.distribution.conveyor

import arc.func.Cons
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.util.Eachable
import arc.util.Nullable
import arc.util.Tmp
import ice.world.content.blocks.distribution.conveyor.Autotiler.AutotilerHolder.directionals
import mindustry.entities.units.BuildPlan
import mindustry.world.Block
import mindustry.world.Edges
import mindustry.world.Tile
import java.util.*

interface Autotiler {
  /**
   * Holds some static temporary variables, required due to some RoboVM bugs
   */
  object AutotilerHolder {
    val blendresult: IntArray = IntArray(5)
    val directionals: Array<BuildPlan?> = arrayOfNulls(4)
  }

  /**
   * The mode to slice a texture at.
   */
  enum class SliceMode {
    none,
    bottom,
    top
  }

  /**
   * @param input The TextureRegion to be sliced
   * @param mode The SliceMode to be applied
   * @return The sliced texture
   */
  fun sliced(input: TextureRegion?, mode: SliceMode?): TextureRegion? {
    return if (mode == SliceMode.none) input else if (mode == SliceMode.bottom) botHalf(input) else topHalf(input)
  }

  /** @return The top half of the input
   */
  fun topHalf(input: TextureRegion?): TextureRegion {
    val region = Tmp.tr1
    region.set(input)
    region.setWidth(region.width / 2)
    return region
  }

  /** @return The bottom half of the input
   */
  fun botHalf(input: TextureRegion?): TextureRegion {
    val region = Tmp.tr1
    region.set(input)
    val width = region.width
    region.setWidth(width / 2)
    region.x = region.x + width
    return region
  }

  @Nullable
  fun getTiling(req: BuildPlan, list: Eachable<BuildPlan>): IntArray? {
    if (req.tile() == null) return null
    val directionals = directionals

    Arrays.fill(directionals, null)
    //TODO this is O(n^2), very slow, should use quadtree or intmap or something instead
    list.each(Cons { other: BuildPlan? ->
      if (other!!.breaking || other === req) return@Cons
      var i = 0
      for (point in Geometry.d4) {
        val x = req.x + point.x
        val y = req.y + point.y
        if (x >= other.x - (other.block.size - 1) / 2 && x <= other.x + (other.block.size / 2) && y >= other.y - (other.block.size - 1) / 2 && y <= other.y + (other.block.size / 2)) {
          directionals[i] = other
        }
        i++
      }
    })

    return buildBlending(req.tile(), req.rotation, directionals, req.worldContext)
  }

  /**
   * @return an array of blending values:
   * [0]: the type of connection:
   * - 0: straight
   * - 1: curve (top)
   * - 2: straight (bottom)
   * - 3: all sides
   * - 4: straight (top)
   * [1]: X scale
   * [2]: Y scale
   * [3]: a 4-bit mask with bits 0-3 indicating blend state in that direction (0 being 0 degrees, 1 being 90, etc)
   * [4]: same as [3] but only blends with non-square sprites
   */
  fun buildBlending(tile: Tile?, rotation: Int, directional: Array<BuildPlan?>?, world: Boolean): IntArray {
    val blendresult = AutotilerHolder.blendresult
    blendresult[0] = 0
    blendresult[2] = 1
    blendresult[1] = blendresult[2]

    val num = if (blends(tile!!, rotation, directional, 2, world) && blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 3, world)) 0 else if (blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 3, world)) 1 else if (blends(tile, rotation, directional, 1, world) && blends(tile, rotation, directional, 2, world)) 2 else if (blends(tile, rotation, directional, 3, world) && blends(tile, rotation, directional, 2, world)) 3 else if (blends(tile, rotation, directional, 1, world)) 4 else if (blends(
        tile, rotation, directional, 3, world
      )
    ) 5 else -1
    transformCase(num, blendresult)

    // Calculate bitmask for direction.
    blendresult[3] = 0

    for (i in 0..3) {
      if (blends(tile, rotation, directional, i, world)) {
        blendresult[3] = blendresult[3] or (1 shl i)
      }
    }

    // Calculate direction for non-square sprites.
    blendresult[4] = 0

    for (i in 0..3) {
      val realDir = Mathf.mod(rotation - i, 4)
      if (blends(tile, rotation, directional, i, world) && (tile != null && tile.nearbyBuild(realDir) != null && !tile.nearbyBuild(realDir).block.squareSprite)) {
        blendresult[4] = blendresult[4] or (1 shl i)
      }
    }

    return blendresult
  }

  /**
   * Transforms the autotiler setting the connection and the y-scale
   *
   * @param num The number to use to transform the array
   * @param bits The blending value array
   */
  fun transformCase(num: Int, bits: IntArray) {
    when (num) {
      0 -> bits[0] = 3
      1 -> bits[0] = 4
      2 -> bits[0] = 2
      3 -> {
        bits[0] = 2
        bits[2] = -1
      }

      4 -> {
        bits[0] = 1
        bits[2] = -1
      }

      5 -> bits[0] = 1
    }
  }

  /**
   * Check if a position is facing the secondary position at a rotation
   *
   * @param x The x coordinate of position 1
   * @param y The y coordinate of position 1
   * @param rotation The rotation of the tile on (x, y)
   *
   * @param x2 The x coordinate of position 2
   * @param y2 The y coordinate of position 2
   *
   * @return If position 1 is facing position 2 at a certain angle
   */
  fun facing(x: Int, y: Int, rotation: Int, x2: Int, y2: Int): Boolean {
    return Point2.equals(x + Geometry.d4(rotation).x, y + Geometry.d4(rotation).y, x2, y2)
  }

  fun blends(tile: Tile, rotation: Int, directional: Array<BuildPlan?>?, direction: Int, checkWorld: Boolean): Boolean {
    val realDir = Mathf.mod(rotation - direction, 4)
    if (directional != null && directional[realDir] != null) {
      val req = directional[realDir]
      if (blends(tile, rotation, req!!.x, req.y, req.rotation, req.block)) {
        return true
      }
    }
    return checkWorld && blends(tile, rotation, direction)
  }

  // TODO docs -- use for direction?
  fun blends(tile: Tile, rotation: Int, direction: Int): Boolean {
    val other = tile.nearbyBuild(Mathf.mod(rotation - direction, 4))
    return other != null && other.team === tile.team() && blends(tile, rotation, other.tileX(), other.tileY(), other.rotation, other.block)
  }

  fun blendsArmored(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
    return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery) || ((!otherblock.rotatedOutput(otherx, othery, tile) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null && Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile).toInt() == rotation) || (otherblock.rotatedOutput(otherx, othery, tile) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x.toInt(), tile.y.toInt())))
  }

  /** @return whether this other block is *not* looking at this one.
   */
  fun notLookingAt(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
    return !(otherblock.rotatedOutput(otherx, othery, tile) && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x.toInt(), tile.y.toInt()))
  }

  /** @return whether this tile is looking at the other tile, or the other tile is looking at this one.
   * If the other tile does not rotate, it is always considered to be facing this one.
   */
  fun lookingAtEither(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean {
    //block is facing the other
    return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery) ||  //does not output to rotated direction
            !otherblock.rotatedOutput(otherx, othery, tile) ||  //other block is facing this one
            Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x.toInt(), tile.y.toInt())
  }

  /**
   * Check if a position is facing the secondary position at a rotation
   *
   * @param tile The origin tile that is or is not facing the `otherblock`
   * @param rotation The rotation of the tile on (x, y)
   * @param otherx The x coordinate of position 2
   * @param othery The y coordinate of position 2
   * @return whether this tile is looking at the other tile.
   */
  fun lookingAt(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherblock: Block): Boolean {
    val facing = Edges.getFacingEdge(otherblock, otherx, othery, tile)
    return facing != null && Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, facing.x.toInt(), facing.y.toInt())
  }

  fun blends(tile: Tile, rotation: Int, otherx: Int, othery: Int, otherrot: Int, otherblock: Block): Boolean
}