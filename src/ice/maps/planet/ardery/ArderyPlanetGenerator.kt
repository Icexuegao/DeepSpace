package ice.maps.planet.ardery

import arc.Core
import arc.func.Cons
import arc.graphics.Color
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.math.geom.Vec3
import arc.struct.*
import arc.util.Nullable
import arc.util.Structs
import arc.util.Tmp
import arc.util.noise.Ridged
import arc.util.noise.Simplex
import ice.content.IBlocks
import ice.content.IItems
import ice.content.IPlanets
import ice.library.struct.log
import mindustry.Vars
import mindustry.ai.Astar
import mindustry.ai.BaseRegistry.BasePart
import mindustry.content.Blocks
import mindustry.content.Liquids
import mindustry.game.Schematics
import mindustry.game.Team
import mindustry.game.Waves
import mindustry.gen.Iconc
import mindustry.maps.generators.BaseGenerator
import mindustry.maps.generators.PlanetGenerator
import mindustry.type.ItemStack
import mindustry.type.Sector
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.TileGen
import mindustry.world.Tiles
import mindustry.world.blocks.environment.SteamVent
import mindustry.world.blocks.storage.CoreBlock.CoreBuild
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ArderyPlanetGenerator : PlanetGenerator() {
    //交替的、较少直接的生成
    companion object {
        var indirectPaths: Boolean = false
        var metalDstScl: Double = 0.25
    }

    var basegen: BaseGenerator = BaseGenerator()
    var heightYOffset: Float = 42.7f
    var scl: Float = 6f
    var waterOffset: Float = 0.04f
    var heightScl: Float = 1.01f
    var tars: ObjectMap<Block, Block> = ObjectMap.of(
        ArrBlock.肿瘤地, ArrBlock.红土
    )
    val arr = ArrBlock.arr
    var water: Float = 2f / arr[0].size
    var basePos: Vec3 = Vec3(0.9341721, 0.0, 0.3568221)
    fun rawHeight(position: Vec3): Float {
        return (Mathf.pow(Simplex.noise3d(seed, 7.0, 0.5, (1f / 3f).toDouble(), (position.x * scl).toDouble(),
            (position.y * scl + heightYOffset).toDouble(), (position.z * scl).toDouble()) * heightScl,
            2.3f) + waterOffset) / (1f + waterOffset)
    }

    override fun onSectorCaptured(sector: Sector) {
        sector.planet.reloadMeshAsync()
    }

    override fun onSectorLost(sector: Sector) {
        sector.planet.reloadMeshAsync()
    }

    override fun beforeSaveWrite(sector: Sector) {
        sector.planet.reloadMeshAsync()
    }

    override fun isEmissive(): Boolean {
        return true
    }

    fun allowNumberedLaunch(s: Sector): Boolean {
        return s.hasBase() && (s.info.bestCoreType.size >= 4 || s.isBeingPlayed && Vars.state.rules.defaultTeam.cores()
            .contains { b: CoreBuild? -> b!!.block.size >= 4 })
    }

    override fun allowLanding(sector: Sector): Boolean {
        return sector.planet.allowLaunchToNumbered && (sector.hasBase() || sector.near().contains { s: Sector? ->
            this.allowNumberedLaunch(
                s!!)
        })
    }

    @Nullable
    override fun findLaunchCandidate(destination: Sector, @Nullable selected: Sector?): Sector? {
        return if (destination.preset == null || !destination.preset.requireUnlock) {
            if (selected != null && selected.isNear(destination) && allowNumberedLaunch(selected)) {
                selected
            } else {
                destination.near().find { s: Sector? -> this.allowNumberedLaunch(s!!) }
            }
        } else {
            super.findLaunchCandidate(destination, selected)
        }
    }

    override fun getLockedText(hovered: Sector, out: StringBuilder) {
        if ((hovered.preset == null || !hovered.preset.requireUnlock) && hovered.near()
                .contains { obj -> obj.hasBase() }
        ) {
            out.append("[red]").append(Iconc.cancel).append("[]").append(Blocks.coreFoundation.emoji())
                .append(Core.bundle.get("sector.foundationrequired"))
        } else {
            super.getLockedText(hovered, out)
        }
    }

    override fun getHeight(position: Vec3): Float {

        val height = rawHeight(position)
        return max(height, water)
    }

    override fun getColor(position: Vec3, out: Color) {
        var block = getBlock(position)
        //replace salt with sand color
        if (block === Blocks.salt) block = Blocks.sand
        out.set(block.mapColor).a(1f - block.albedo)
    }

    override fun getEmissiveColor(position: Vec3, out: Color) {
        var dst = 999f
        var captureDst = 999f
        var lightScl = 0f
        IPlanets.阿德里.sectors.forEach { sector ->
            if (sector.hasEnemyBase() && !sector.isCaptured()) {
                dst = min(dst, position.dst(
                    sector.tile.v) - (if (sector.preset != null) sector.preset.difficulty / 10f * 0.03f - 0.03f else 0f))
            } else if (sector.hasBase()) {
                val cdst = position.dst(sector.tile.v)
                if (cdst < captureDst) {
                    captureDst = cdst
                    lightScl = sector.info.lightCoverage
                }
            }
        }


        lightScl = min(lightScl / 50000f, 1.3f)
        if (lightScl < 1f) lightScl = Interp.pow5Out.apply(lightScl)
        val freq = 0.05f
        if (if (position.dst(basePos) < 0.55f) dst * metalDstScl + Simplex.noise3d(seed + 1, 3.0, 0.4, 5.5,
                position.x.toDouble(), (position.y + 200f).toDouble(),
                position.z.toDouble()) * 0.08f + (if ((basePos.dst(
                    position) + 0.00f) % freq < freq / 2f
            ) 1f else 0f) * 0.07f < 0.08f /* || dst <= 0.0001f*/ else dst * metalDstScl + Simplex.noise3d(seed, 3.0,
                0.4, 9.0, position.x.toDouble(), (position.y + 370f).toDouble(), position.z.toDouble()) * 0.06f < 0.045
        ) {
            out.set(Team.crux.color)
                .mul(0.8f + Simplex.noise3d(seed, 1.0, 1.0, 9.0, position.x.toDouble(), (position.y + 99f).toDouble(),
                    position.z.toDouble()) * 0.4f)
                .lerp(Team.sharded.color,
                    0.2f * Simplex.noise3d(seed, 1.0, 1.0, 9.0, position.x.toDouble(), (position.y + 999f).toDouble(),
                        position.z.toDouble())).toFloatBits()
        } else if (captureDst * metalDstScl + Simplex.noise3d(seed, 3.0, 0.4, 9.0, position.x.toDouble(),
                (position.y + 600f).toDouble(), position.z.toDouble()) * 0.07f < 0.05 * lightScl
        ) {
            out.set(Team.sharded.color).mul(
                0.7f + Simplex.noise3d(seed, 1.0, 1.0, 9.0, position.x.toDouble(), (position.y + 99f).toDouble(),
                    position.z.toDouble()) * 0.4f)
                .lerp(Team.crux.color,
                    0.3f * Simplex.noise3d(seed, 1.0, 1.0, 9.0, position.x.toDouble(), (position.y + 999f).toDouble(),
                        position.z.toDouble())).toFloatBits()
        }
    }

    public override fun genTile(position: Vec3, tile: TileGen) {
        tile.floor = getBlock(position)
        tile.block = tile.floor.asFloor().wall
        if (Ridged.noise3d(seed + 1, position.x.toDouble(), position.y.toDouble(), position.z.toDouble(), 2,
                22f) > 0.31
        ) {
            tile.block = Blocks.air
        }
    }

    override fun getSizeScl(): Float {
        return 3200f
    }

    override fun getSectorSize(sector: Sector): Int {
        val res = (sector.rect.radius * sizeScl).toInt()
        return 2 * if (res % 2 == 0) res else res + 1
    }

    fun getBlock(position: Vec3): Block {
        var height = rawHeight(position)
        val px = position.x * scl
        val py = position.y * scl
        val pz = position.z * scl
        val rad = scl
        var temp = Mathf.clamp(abs(py * 2f) / (rad))
        val tnoise = Simplex.noise3d(seed, 7.0, 0.56, (1f / 3f).toDouble(), px.toDouble(),
            (py + 999f - 0.1f).toDouble(), pz.toDouble())
        temp = Mathf.lerp(temp, tnoise, 0.5f)
        height *= 1.2f
        height = Mathf.clamp(height)
        val tar = Simplex.noise3d(seed, 4.0, 0.55, (1f / 2f).toDouble(), px.toDouble(), (py + 999f).toDouble(),
            pz.toDouble()) * 0.3f + position.dst(0f, 0f, 1f) * 0.2f
        val clamp = Mathf.clamp((temp * arr.size).toInt(), 0, arr.size - 1)
        val clamp1 = Mathf.clamp((height * arr[clamp].size).toInt(), 0, arr[clamp].size - 1)
        val res = arr[clamp][clamp1]
        if (tar > 0.5f) {
            return tars.get(res, res)
        } else {
            if (position.within(basePos, 0.65f)) {
                var dst = 999f

                IPlanets.阿德里.sectors.forEach { sector ->
                    if (sector.hasEnemyBase()) {
                        dst = min(dst, position.dst(sector.tile.v))
                    }
                }
                val freq = 0.05f
                val freq2 = 0.07f

                if (dst * 0.85f + Simplex.noise3d(seed, 3.0, 0.4, 5.5, position.x.toDouble(),
                        (position.y + 200f).toDouble(), position.z.toDouble()) * 0.015f + (if ((basePos.dst(
                            position) + 0.00f) % freq < freq / 2f
                    ) 1f else 0f) * 0.07f < 0.15f
                ) {
                    return if ((basePos.dst(
                            position) + 0.01f) % freq2 < freq2 * 0.65f
                    ) Blocks.metalFloor else Blocks.darkPanel6
                }
            }
            return res
        }
    }

    override fun noise(x: Float, y: Float, octaves: Double, falloff: Double, scl: Double, mag: Double): Float {
        val v = sector.rect.project(x, y).scl(5f)
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x.toDouble(), v.y.toDouble(),
            v.z.toDouble()) * mag.toFloat()
    }

    override fun generate() {
        class Room(var x: Int, var y: Int, var radius: Int) {
            var connected: ObjectSet<Room?> = ObjectSet<Room?>()

            init {
                connected.add(this)
            }

            fun join(x1: Int, y1: Int, x2: Int, y2: Int) {
                val nscl = rand.random(100f, 140f) * 6f
                val stroke = rand.random(3, 9)
                brush(pathfind(x1, y1, x2, y2, { tile: Tile? ->
                    (if (tile!!.solid()) 50f else 0f) + noise(
                        tile.x.toFloat(), tile.y.toFloat(), 2.0, 0.4, (1f / nscl).toDouble()) * 500
                }, Astar.manhattan), stroke)
            }

            fun connect(to: Room) {
                if (!connected.add(to) || to === this) return
                val midpoint = Tmp.v1.set(to.x.toFloat(), to.y.toFloat()).add(x.toFloat(), y.toFloat()).scl(0.5f)
                rand.nextFloat()

                if (indirectPaths) {
                    midpoint.add(Tmp.v2.set(1f, 0f).setAngle(
                        Angles.angle(to.x.toFloat(), to.y.toFloat(), x.toFloat(), y.toFloat()) + 90f * (if (rand.chance(
                                0.5)
                        ) 1f else -1f)).scl(Tmp.v1.dst(x.toFloat(), y.toFloat()) * 2f))
                } else {
                    //add randomized offset to avoid straight lines
                    midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x.toFloat(), y.toFloat())))
                }

                midpoint.sub(width / 2f, height / 2f).limit(width / 2f / Mathf.sqrt3).add(width / 2f, height / 2f)
                val mx = midpoint.x.toInt()
                val my = midpoint.y.toInt()

                join(x, y, mx, my)
                join(mx, my, to.x, to.y)
            }

            fun joinLiquid(x1: Int, y1: Int, x2: Int, y2: Int) {
                val nscl = rand.random(100f, 140f) * 6f
                val rad = rand.random(7, 11)
                val avoid = 2 + rad
                val path = pathfind(x1, y1, x2, y2, { tile: Tile? ->
                    (if (tile!!.solid() || !tile.floor().isLiquid) 70f else 0f) + noise(
                        tile.x.toFloat(), tile.y.toFloat(), 2.0, 0.4, (1f / nscl).toDouble()) * 500
                }, Astar.manhattan)
                path.each(Cons { t ->
                    //don't place liquid paths near the core
                    if (Mathf.dst2(t!!.x.toFloat(), t.y.toFloat(), x2.toFloat(), y2.toFloat()) <= avoid * avoid) {
                        return@Cons
                    }
                    for (x in -rad..rad) {
                        for (y in -rad..rad) {
                            val wx = t.x + x
                            val wy = t.y + y
                            if (Structs.inBounds(wx, wy, width, height) && Mathf.within(x.toFloat(), y.toFloat(),
                                    rad.toFloat())
                            ) {
                                val other = tiles.getn(wx, wy)
                                other.setBlock(Blocks.air)
                                if (Mathf.within(x.toFloat(), y.toFloat(),
                                        (rad - 1).toFloat()) && !other.floor().isLiquid
                                ) {
                                    val floor = other.floor()
                                    //TODO 不尊重受污染的地板
                                    other.setFloor(IBlocks.血池)
                                }
                            }
                        }
                    }
                })
            }

            fun connectLiquid(to: Room) {
                if (to === this) return
                val midpoint = Tmp.v1.set(to.x.toFloat(), to.y.toFloat()).add(x.toFloat(), y.toFloat()).scl(0.5f)
                rand.nextFloat()
                //add randomized offset to avoid straight lines
                midpoint.add(Tmp.v2.setToRandomDirection(rand).scl(Tmp.v1.dst(x.toFloat(), y.toFloat())))
                midpoint.sub(width / 2f, height / 2f).limit(width / 2f / Mathf.sqrt3).add(width / 2f, height / 2f)
                val mx = midpoint.x.toInt()
                val my = midpoint.y.toInt()

                joinLiquid(x, y, mx, my)
                joinLiquid(mx, my, to.x, to.y)
            }
        }

        cells(4)
        distort(10f, 12f)
        val constraint = 1.3f
        val radius = width / 2f / Mathf.sqrt3
        val rooms = rand.random(2, 5)
        val roomseq = Seq<Room>()

        for (i in 0..<rooms) {
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint))
            val rx = (width / 2f + Tmp.v1.x)
            val ry = (height / 2f + Tmp.v1.y)
            val maxrad = radius - Tmp.v1.len()
            val rrad = min(rand.random(9f, maxrad / 2f), 30f)
            roomseq.add(Room(rx.toInt(), ry.toInt(), rrad.toInt()))
        }
        //检查地图上的位置以放置玩家生成点。这需要在地图的角落里
        var spawn: Room? = null
        val enemies = Seq<Room>()
        val enemySpawns = rand.random(1, max((sector.threat * 4).toInt(), 1))
        val offset = rand.nextInt(360)
        val length = width / 2.55f - rand.random(13, 23)
        val angleStep = 5
        val waterCheckRad = 5
        run {
            var i = 0
            while (i < 360) {
                val angle = offset + i
                val cx = (width / 2 + Angles.trnsx(angle.toFloat(), length)).toInt()
                val cy = (height / 2 + Angles.trnsy(angle.toFloat(), length)).toInt()
                var waterTiles = 0
                //check for water presence
                for (rx in -waterCheckRad..waterCheckRad) {
                    for (ry in -waterCheckRad..waterCheckRad) {
                        val tile = tiles.get(cx + rx, cy + ry)
                        if (tile == null || tile.floor().liquidDrop != null) {
                            waterTiles++
                        }
                    }
                }

                if (waterTiles <= 4 || (i + angleStep >= 360)) {
                    roomseq.add(Room(cx, cy, rand.random(8, 15)).also { spawn = it })

                    for (j in 0..<enemySpawns) {
                        val enemyOffset = rand.range(60f)
                        Tmp.v1.set((cx - width / 2).toFloat(), (cy - height / 2).toFloat()).rotate(180f + enemyOffset)
                            .add((width / 2).toFloat(), (height / 2).toFloat())
                        val espawn = Room(Tmp.v1.x.toInt(), Tmp.v1.y.toInt(), rand.random(8, 16))
                        roomseq.add(espawn)
                        enemies.add(espawn)
                    }

                    break
                }
                i += angleStep
            }
        }
        //每个房间周围的净半径
        for (room in roomseq) {
            erase(room.x, room.y, room.radius)
        }
        //randomly connect rooms together
        val connections = rand.random(max(rooms - 1, 1), rooms + 3)
        for (i in 0..<connections) {
            roomseq.random(rand).connect(roomseq.random(rand))
        }

        for (room in roomseq) {
            spawn!!.connect(room)
        }
        val fspawn = spawn

        cells(1)
        val tlen = tiles.width * tiles.height
        var total = 0
        var waters = 0

        for (i in 0..<tlen) {
            val tile = tiles.geti(i)


            if (tile.block() === Blocks.air) {
                total++
                if (tile.floor().liquidDrop === Liquids.water) {
                    waters++
                }
            }
        }
        /*val naval = waters.toFloat() / total >= 0.19f
        //create water pathway if the map is flooded
        if (naval) {
            for (room in enemies) {
                room.connectLiquid(spawn!!)
            }
        }*/

        distort(10f, 6f)
        val redB = arrayOf(IBlocks.血浅滩.asFloor(), IBlocks.血痂地, IBlocks.红土, IBlocks.殷血粗沙, IBlocks.肿瘤地,
            IBlocks.红冰, IBlocks.软红冰, IBlocks.红霜, IBlocks.深血池, IBlocks.血池)
        //河流
        pass { x: Int, y: Int ->
            if (block.solid) return@pass
            val v = sector.rect.project(x.toFloat(), y.toFloat())
            val rr = Simplex.noise2d(sector.id, 2f.toDouble(), 0.6, (1f / 7f).toDouble(), x.toDouble(),
                y.toDouble()) * 0.1f
            val value = Ridged.noise3d(2, v.x.toDouble(), v.y.toDouble(), v.z.toDouble(), 1, 1f / 55f) + rr - rawHeight(
                v) * 0f
            val rrscl = rr * 44 - 2

            if (value > 0.17f && !Mathf.within(x.toFloat(), y.toFloat(), fspawn!!.x.toFloat(), fspawn.y.toFloat(),
                    12 + rrscl)
            ) {
                val deep = value > 0.17f + 0.1f && !Mathf.within(x.toFloat(), y.toFloat(), fspawn.x.toFloat(),
                    fspawn.y.toFloat(), 15 + rrscl)
                val sd = value < 0.17f + 0.05f && !Mathf.within(x.toFloat(), y.toFloat(), fspawn.x.toFloat(),
                    fspawn.y.toFloat(), 15 + rrscl)
                //不要把河流放在冰上，它们是结冰的
                //  忽略预先存在的液体
                val block1 = when (floor) {
                    in redB -> {
                        if (deep) IBlocks.深血池 else if (sd) IBlocks.血浅滩 else IBlocks.血池
                    }

                    IBlocks.潮汐石 -> IBlocks.潮汐水石
                    else -> {
                        var b = Blocks.cryofluid
                        if (Vars.content.block(floor.name + "Water") != null) {
                            b = if (deep) {
                                if (Vars.content.block(floor.name + "DeepWater") != null) {
                                    Vars.content.block(floor.name + "DeepWater")
                                } else Blocks.water
                            } else Vars.content.block(floor.name + "Water")
                        } else {
                            log {
                                floor.localizedName
                            }
                        }
                        b
                    }
                }
                if (block1 != null) {
                    floor = block1
                }
            }
        }
        /*  //海岸线设置
          pass { x: Int, y: Int ->
              val deepRadius = 3
              if (floor.asFloor().isLiquid && floor.asFloor().shallow) {
                  for (cx in -deepRadius..deepRadius) {
                      for (cy in -deepRadius..deepRadius) {
                          if ((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius) {
                              val wx = cx + x
                              val wy = cy + y
                              val tile = tiles.get(wx, wy)
                              if (tile != null && (!tile.floor().isLiquid || tile.block() !== Blocks.air)) {
                                  //found something solid, skip replacing anything
                                  return@pass
                              }
                          }
                      }
                  }

                  floor = if (floor === IBlocks.血浅滩) IBlocks.血池 else Blocks.water
              }
          }*/
        /* if (naval) {
             val deepRadius = 2
             //TODO 代码非常相似，但提取到单独的函数中很烦人
             pass { x: Int, y: Int ->
                 if (floor.asFloor().isLiquid && !floor.asFloor().isDeep && !floor.asFloor().shallow) {
                     for (cx in -deepRadius..deepRadius) {
                         for (cy in -deepRadius..deepRadius) {
                             if ((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius) {
                                 val wx = cx + x
                                 val wy = cy + y
                                 val tile = tiles.get(wx, wy)
                                 if (tile != null && (tile.floor().shallow || !tile.floor().isLiquid)) {
                                     //found something shallow, skip replacing anything
                                     return@pass
                                 }
                             }
                         }
                     }

                     floor = if (floor === Blocks.water) Blocks.deepwater else Blocks.taintedWater
                 }
             }
         }*/
        setOres()


        trimDark()

        median(2)

        inverseFloodFill(tiles.getn(spawn!!.x, spawn.y))

        tech(IBlocks.钢铁地板2, IBlocks.钢铁地板1, IBlocks.跨界钢板墙)

        pass { x, y ->
            //random 血痂地
            if (floor === ArrBlock.血痂地) {
                if (noise(x - 90f, y.toFloat(), 1.0, 0.68, 22.45) > 0.83) {
                    floor = ArrBlock.肿瘤地
                    Geometry.d8.forEach {
                        val floor1 = tiles[x + it.x, y + it.y]?.floor()
                        if (floor1 != ArrBlock.肿瘤地 && floor1 != ArrBlock.血痂地) return@pass
                    }
                    floor = ArrBlock.血痂地

                } else {
                    floor = ArrBlock.肿瘤地
                }
            }
        }
        val difficulty = sector.threat
        val ruinCount = rand.random(-2, 4)

        if (ruinCount > 0) {
            val ints = IntSeq(width * height / 4)
            val padding = 25
            //创建潜在职位列表
            for (x in padding..<width - padding) {
                for (y in padding..<height - padding) {
                    val tile = tiles.getn(x, y)
                    if (!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)) {
                        ints.add(tile.pos())
                    }
                }
            }

            ints.shuffle(rand)
            var placed = 0
            val diffRange = 0.4f
            //try each position
            var i = 0
            while (i < ints.size && placed < ruinCount) {
                val `val` = ints.items[i]
                val x = Point2.x(`val`).toInt()
                val y = Point2.y(`val`).toInt()
                //do not overwrite player spawn
                if (Mathf.within(x.toFloat(), y.toFloat(), spawn.x.toFloat(), spawn.y.toFloat(), 18f)) {
                    i++
                    continue
                }
                val range = difficulty + rand.random(diffRange)
                val tile = tiles.getn(x, y)
                var part: BasePart? = null
                if (tile.overlay().itemDrop != null) {
                    part = Vars.bases.forResource(tile.drop()).getFrac(range)
                } else if (tile.floor().liquidDrop != null && rand.chance(0.05)) {
                    part = Vars.bases.forResource(tile.floor().liquidDrop).getFrac(range)
                } else if (rand.chance(0.05)) { //ore-less parts are less likely to occur.
                    part = Vars.bases.parts.getFrac(range)
                }
                //实际放置零件
                if (part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, rand) { cx: Int, cy: Int ->
                        val other = tiles.getn(cx, cy)
                        if (other.floor().hasSurface()) {
                            other.setOverlay(Blocks.oreScrap)
                            for (j in 1..2) {
                                for (p in Geometry.d8) {
                                    val t = tiles.get(cx + p.x * j, cy + p.y * j)
                                    if (t != null && t.floor().hasSurface() && rand.chance(if (j == 1) 0.4 else 0.2)) {
                                        t.setOverlay(Blocks.oreScrap)
                                    }
                                }
                            }
                        }
                    }) {
                    placed++
                    val debrisRadius = max(part.schematic.width, part.schematic.height) / 2 + 3
                    Geometry.circle(x, y, tiles.width, tiles.height, debrisRadius) { cx: Int, cy: Int ->
                        val dst = Mathf.dst(cx.toFloat(), cy.toFloat(), x.toFloat(), y.toFloat())
                        val removeChance = Mathf.lerp(0.05f, 0.5f, dst / debrisRadius)
                        val other = tiles.getn(cx, cy)
                        if (other.build != null && other.isCenter) {
                            if (other.team() === Team.derelict && rand.chance(removeChance.toDouble())) {
                                other.remove()
                            } else if (rand.chance(0.5)) {
                                other.build.health = other.build.health - rand.random(other.build.health * 0.9f)
                            }
                        }
                    }
                }
                i++
            }
        }
        //remove invalid ores
        for (tile in tiles) {
            if (tile.overlay().needsSurface && !tile.floor().hasSurface()) {
                tile.setOverlay(Blocks.air)
            }
        }
        //处理其他地形生成
        pass { x, y ->
            when (floor) {
                IBlocks.潮汐石 -> {
                    if (!(abs(0.5f - noise((x - 90).toFloat(), y.toFloat(), 4.0, 0.8, 80.0)) > 0.035)) {
                        for (p in Geometry.d8) {
                            val other = tiles.get(x + p.x, y + p.y)
                            if ((other?.floor() == IBlocks.潮汐石 || other?.floor() == IBlocks.潮汐水石)) {
                                floor = IBlocks.潮汐水石
                            }
                        }
                    }
                    if (rand.chance(0.0005)) {
                        for (pos in SteamVent.offsets) {
                            val other = tiles[x + pos.x, y + pos.y]
                            if (other == null || ((other.floor() != IBlocks.潮汐石) && (other.floor() != IBlocks.潮汐水石)) || other.block().solid) {
                                return@pass
                            }
                        }
                        floor = IBlocks.潮汐喷口
                        for (pos in SteamVent.offsets) {
                            val other = tiles[pos.x + x + 1, pos.y + y + 1]
                            other.setFloor(IBlocks.潮汐喷口)
                        }
                    }
                }

                IBlocks.新月岩 -> {
                    if (rand.chance(0.0005)) {
                        for (pos in SteamVent.offsets) {
                            val other = tiles[x + pos.x, y + pos.y]
                            if (other == null || (other.floor() != IBlocks.新月岩) || other.block().solid) {
                                return@pass
                            }
                        }
                        floor = IBlocks.新月喷口
                        for (pos in SteamVent.offsets) {
                            val other = tiles[pos.x + x + 1, pos.y + y + 1]
                            other.setFloor(IBlocks.新月喷口)
                        }
                    }
                }

                IBlocks.云英岩 -> {
                    decoration(x, y, 0.013, IBlocks.云英石柱)
                }

                IBlocks.晶石地 -> {
                    decoration(x, y, 0.001, IBlocks.燃素晶簇)
                    if (rand.chance(0.01)) {
                        Geometry.d8.forEach {
                            if (tiles[x + it.x, y + it.y]?.block() == IBlocks.晶石墙 && block == Blocks.air) {
                                block = IBlocks.燃素晶簇
                            }
                        }
                    }

                }

                IBlocks.幽灵草 -> {
                    decoration(x, y, 0.017, IBlocks.幽灵簇)
                    decoration(x, y, 0.001, IBlocks.缠怨花)
                    if (rand.chance(0.04)) {
                        Geometry.d8.forEach {
                            if (tiles[x + it.x, y + it.y]?.block() == IBlocks.幽灵草墙 && block == Blocks.air) {
                                block = IBlocks.幽冥蕨
                                Geometry.d8.forEach { it ->
                                    if (rand.chance(0.2)) {
                                        val tile = tiles[x + it.x + rand.random(-3, 3), y + it.y + rand.random(-3, 3)]
                                        if (tile?.block() === Blocks.air) {
                                            tile.setBlock(IBlocks.幽冥蕨)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                IBlocks.肿瘤地 -> {
                    if (rand.chance(0.05)) {
                        var spawn = true
                        Geometry.d8.forEach {
                            if (tiles[x + it.x, y + it.y]?.floor() != IBlocks.肿瘤地) spawn = false
                        }
                        if (spawn) floor = IBlocks.骸骨地
                    }
                    if (rand.chance(0.001) && block == Blocks.air) {
                        var spawn = true
                        Geometry.d8.forEach {
                            val floor1 = tiles[x + it.x, y + it.y]?.floor()
                            if (floor1 != IBlocks.肿瘤地 && floor1 != IBlocks.骸骨地) spawn = false
                            if (tiles[x + it.x, y + it.y]?.block() != Blocks.air) spawn = false
                        }

                        if (spawn) {
                            block = IBlocks.肿瘤井
                            Geometry.d8.forEach {
                                tiles[x + it.x, y + it.y]?.setBlock(IBlocks.肿瘤井)
                            }
                        }

                    }
                    if (rand.chance(0.002) && block == Blocks.air) {
                        block = IBlocks.肉瘤菇
                    }
                    if (rand.chance(0.004) && block == IBlocks.肿瘤墙) {
                        block = IBlocks.肉瘤菇
                    }
                }

                IBlocks.凌冰 -> {
                    decoration(x, y, 0.012, IBlocks.霜寒草)
                    if (rand.chance(0.002)) {
                        block = IBlocks.凌冰尖刺
                        Geometry.d8.forEach {
                            if (rand.chance(0.2)) {
                                tiles[x + it.x, y + it.y]?.setBlock(block)
                            }
                        }
                    }

                }

                IBlocks.风蚀沙地 -> {
                    decoration(x, y, 0.002, IBlocks.利芽)
                    if (noise(x - 90f, y.toFloat(), 1.0, 0.68, 22.45) > 0.83) {
                        Geometry.d8.forEach {
                            val floor1 = tiles[x + it.x, y + it.y]?.floor()
                            if (floor1 != ArrBlock.风蚀沙地 && floor1 != IBlocks.风蚀砂地) return@pass
                        }
                        floor = IBlocks.风蚀砂地
                    }
                    if (rand.chance(0.0005)) {
                        for (pos in SteamVent.offsets) {
                            val other = tiles[x + pos.x, y + pos.y]
                            if (other == null || ((other.floor() != IBlocks.风蚀沙地) && (other.floor() != IBlocks.风蚀砂地)) || other.block().solid) {
                                return@pass
                            }
                        }
                        floor = IBlocks.风蚀喷口
                        for (pos in SteamVent.offsets) {
                            val other = tiles[pos.x + x + 1, pos.y + y + 1]
                            other.setFloor(IBlocks.风蚀喷口)
                        }
                    }

                }

                IBlocks.红霜 -> {
                    if (noise(x - 90f, y.toFloat(), 1.0, 0.68, 22.45) > 0.83) {
                        Geometry.d8.forEach {
                            val floor1 = tiles[x + it.x, y + it.y]?.floor()
                            if (floor1 != IBlocks.红霜 && floor1 != IBlocks.赤雪) return@pass
                        }
                        floor = IBlocks.赤雪
                    }
                }

                IBlocks.红土 -> {
                    if (abs(0.5f - noise((x - 40).toFloat(), y.toFloat(), 2.0, 0.7, 80.0)) > 0.25f && abs(
                            0.5f - noise(x.toFloat(), (y + sector.id * 10).toFloat(), 1.0, 1.0,
                                60.0)) > 0.41f && !(roomseq.contains { r: Room? ->
                            Mathf.within(x.toFloat(), y.toFloat(), r!!.x.toFloat(), r.y.toFloat(), 30f)
                        })
                    ) {
                        ore = Blocks.air
                        floor = IBlocks.灵液
                    }
                    if (rand.chance(0.0075)) {
                        if (floor == IBlocks.红土 && rand.chance(0.05)) block = IBlocks.殷红树
                        if (block == IBlocks.红土墙 && rand.chance(0.08)) block = IBlocks.殷红树
                    }
                }

                IBlocks.红冰 -> {
                    if (noise(x - 90f, y.toFloat(), 1.0, 0.68, 22.45) > 0.83) {
                        Geometry.d8.forEach {
                            val floor1 = tiles[x + it.x, y + it.y]?.floor()
                            if (floor1 != IBlocks.红冰 && floor1 != IBlocks.软红冰) return@pass
                        }
                        floor = IBlocks.软红冰
                    }
                    if ((floor == IBlocks.红冰) && rand.chance(0.0005)) {
                        block = IBlocks.殷红树
                    }
                }
            }
            //随机的东西
            run {
                for (i in 0..3) {
                    val near = tiles.get(x + Geometry.d4[i].x, y + Geometry.d4[i].y)
                    if (near != null && near.block() !== Blocks.air) {
                        return@run
                    }
                }
                if (rand.chance(0.01) && floor.asFloor().hasSurface() && block === Blocks.air) {
                    block = floor.asFloor().decoration
                }
            }
        }


        Schematics.placeLaunchLoadout(spawn.x, spawn.y)
        addStarItems()
        return
        for (espawn in enemies) {
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn)
        }

        if (sector.hasEnemyBase()) {
            basegen.generate(tiles, enemies.map { r: Room? -> tiles.getn(r!!.x, r.y) },
                tiles.get(spawn.x, spawn.y), Vars.state.rules.waveTeam, sector, difficulty)

            sector.info.attack = true
            Vars.state.rules.attackMode = sector.info.attack
        } else {
            sector.info.winWave = 10 + 5 * max(difficulty * 10, 1f).toInt()
            Vars.state.rules.winWave = sector.info.winWave
        }
        val waveTimeDec = 0.4f

        Vars.state.rules.waveSpacing = Mathf.lerp((60 * 65 * 2).toFloat(), 60f * 60f * 1f,
            max(difficulty - waveTimeDec, 0f))
        Vars.state.rules.waves = true
        Vars.state.rules.env = sector.planet.defaultEnv
        Vars.state.rules.enemyCoreBuildRadius = 600f
        //spawn air only when spawn is blocked
        Vars.state.rules.spawns = Waves.generate(difficulty, Rand(sector.id.toLong()), Vars.state.rules.attackMode,
            Vars.state.rules.attackMode && Vars.spawner.countGroundSpawns() == 0, false)//naval)

    }

    fun decoration(x: Int, y: Int, chance: Double, tb: Block) {
        for (i in 0..3) {
            val near = Vars.world.tile(x + Geometry.d4[i].x, y + Geometry.d4[i].y)
            if (near != null && near.block() !== Blocks.air) {
                return
            }
        }
        if (rand.chance(chance) && floor.asFloor().hasSurface() && block === Blocks.air) {
            block = tb
        }

    }

    fun addStarItems() {
        val items = Vars.player.core()?.items ?: return
        items.add(ItemStack.with(IItems.高碳钢, 200, IItems.低碳钢, 200, IItems.铜锭, 150, IItems.锌锭, 150).toList())
    }

    fun setOres() {
        val ores = Seq.with<Block>(IItems.金矿.oreBlock, IItems.锆英石.oreBlock, IItems.石英.oreBlock,
            IItems.赤铁矿.oreBlock, IItems.方铅矿.oreBlock, IItems.硫钴矿.oreBlock, IItems.黄铜矿.oreBlock,
            IItems.闪锌矿.oreBlock,IItems.生煤.oreBlock,IItems.铬铁矿.oreBlock)
        val poles = abs(sector.tile.v.y)
        val nmag = 0.5f
        val scl = 1f
        val addscl = 1.3f

        val frequencies = FloatSeq()
        for (i in 0..<ores.size) {
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.04f)
        }

        pass { x: Int, y: Int ->
            if (!floor.asFloor().hasSurface()) return@pass
            val offsetX = x - 4
            val offsetY = y + 23
            for (i in ores.size - 1 downTo 0) {
                val entry = ores.get(i)
                val freq = frequencies.get(i)
                if (abs(0.5f - noise(offsetX.toFloat(), (offsetY + i * 999).toFloat(), 2.0, 0.7,
                        (40 + i * 2).toDouble())) > 0.22f + i * 0.01 &&
                    abs(0.5f - noise(offsetX.toFloat(), (offsetY - i * 999).toFloat(), 1.0, 1.0,
                        (30 + i * 4).toDouble())) > 0.37f + freq
                ) {
                    ore = entry
                    break
                }
            }
        }
    }

    override fun postGenerate(tiles: Tiles?) {
        if (sector.hasEnemyBase()) {
            basegen.postGenerate()
            //spawn air enemies
            if (Vars.spawner.countGroundSpawns() == 0) {
                Vars.state.rules.spawns = Waves.generate(sector.threat, Rand(sector.id.toLong()),
                    Vars.state.rules.attackMode, true, false)
            }
        }
    }
}