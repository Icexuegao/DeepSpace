package ice.maps.planet.ardery

import arc.Core
import arc.graphics.Color
import arc.math.Angles
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Geometry
import arc.math.geom.Point2
import arc.math.geom.Vec3
import arc.struct.*
import arc.util.Structs
import arc.util.Tmp
import arc.util.noise.Ridged
import arc.util.noise.Simplex
import ice.content.IBlocks
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
import mindustry.type.Sector
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.TileGen
import mindustry.world.Tiles
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.storage.CoreBlock.CoreBuild
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ArderyPlanetGenerator : PlanetGenerator() {
    init {
        defaultLoadout = Schematics.readBase64(
            "bXNjaAF4nGNgZmBmZmDJS8xNZWBKKWfgTkktTi7KLCjJzM9jYGBgy0lMSs0pZmCKjmVk4M1MTtXNzcxLLXLOL0oFyjKCEJAAADcaD4E=")
    }

    //替代的、较少的直接生成 （WIP）
    companion object {
        var alt: Boolean = false
    }

    val arr = ArrBlock.arr
    var basegen: BaseGenerator = BaseGenerator()
    var scl: Float = 5f
    var waterOffset: Float = 0.07f
    var genLakes: Boolean = false
    var dec: ObjectMap<Block, Block> = ObjectMap.of(
        Blocks.sporeMoss, Blocks.sporeCluster,
        IBlocks.红土, Blocks.sporeCluster,
        Blocks.taintedWater, Blocks.water,
        Blocks.darksandTaintedWater, Blocks.darksandWater
    )
    var tars: ObjectMap<Block, Block> = ObjectMap.of(
        Blocks.sporeMoss, Blocks.shale,
        IBlocks.红土, Blocks.shale
    )
    var water: Float = 2f / arr[0].size
    fun rawHeight(position: Vec3): Float {
        position.set(position).scl(scl)
        return (Mathf.pow(Simplex.noise3d(seed, 7.0, 0.5, 1.0 / 3, position.x.toDouble(), position.y.toDouble(),
            position.z.toDouble()), 2.3f) + waterOffset) / (1f + waterOffset)
    }
   override fun allowLanding(sector: Sector): Boolean {
        return sector.planet.allowLaunchToNumbered && (sector.hasBase() || sector.near().contains { s: Sector ->
            s.hasBase() &&
                    (s.info.bestCoreType.size >= 4 || s.isBeingPlayed && Vars.state.rules.defaultTeam.cores()
                        .contains { b: CoreBuild -> b.block.size >= 4 })
        })
    }

    override fun getLockedText(hovered: Sector, out: StringBuilder) {
        if ((hovered.preset == null || !hovered.preset.requireUnlock) && hovered.near()
                .contains { obj: Sector -> obj.hasBase() }
        ) {
            out.append("[red]").append(Iconc.cancel).append("[]").append(Blocks.coreFoundation.emoji()).append(
                Core.bundle["sector.foundationrequired"])
        } else {
            super.getLockedText(hovered, out)
        }
    }

    override fun getHeight(position: Vec3?): Float {
        val height = rawHeight(position!!)
        return max(height, water)
    }
    override fun getColor(position: Vec3, out: Color) {
        var block = getBlock(position)
        //replace salt with sand color
        if (block === Blocks.salt) block = Blocks.sand
        out.set(block.mapColor).a(1f - block.albedo)
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

    fun getBlock(position: Vec3): Block {
        var pos1 = position
        var height = rawHeight(pos1)
        Tmp.v31.set(pos1)
        pos1 = Tmp.v33.set(pos1).scl(scl)
        val rad = scl
        var temp = Mathf.clamp((abs((pos1.y * 2f).toDouble()) / (rad)).toFloat())
        val tnoise = Simplex.noise3d(seed, 7.0, 0.56, (1f / 3f).toDouble(), pos1.x.toDouble(),
            (pos1.y + 999f).toDouble(), pos1.z.toDouble())
        temp = Mathf.lerp(temp, tnoise, 0.5f)
        height *= 1.2f
        height = Mathf.clamp(height)
        val tar = Simplex.noise3d(seed, 4.0, 0.55, (1f / 2f).toDouble(), pos1.x.toDouble(),
            (pos1.y + 999f).toDouble(), pos1.z.toDouble()) * 0.3f + Tmp.v31.dst(0f, 0f, 1f) * 0.2f
        val res = arr[Mathf.clamp((temp * arr.size).toInt(), 0, arr[0].size - 1)][Mathf.clamp(
            (height * arr[0].size).toInt(), 0, arr[0].size - 1)]
        return if (tar > 0.5f) {
            tars[res, res]
        } else {
            res
        }
    }

    override fun noise(x: Float, y: Float, octaves: Double, falloff: Double, scl: Double, mag: Double): Float {
        val v = sector.rect.project(x, y).scl(5f)
        return Simplex.noise3d(seed, octaves, falloff, 1f / scl, v.x.toDouble(), v.y.toDouble(),
            v.z.toDouble()) * mag.toFloat()
    }

    override fun generate() {
        class Room(var x: Int, var y: Int, var radius: Int) {
            var connected: ObjectSet<Room> = ObjectSet()

            init {
                connected.add(this)
            }

            fun join(x1: Int, y1: Int, x2: Int, y2: Int) {
                val nscl = rand.random(100f, 140f) * 6f
                val stroke = rand.random(3, 9)
                brush(pathfind(x1, y1, x2, y2,
                    { tile: Tile ->
                        (if (tile.solid()) 50f else 0f) + noise(tile.x.toFloat(), tile.y.toFloat(), 2.0, 0.4,
                            (1f / nscl).toDouble()) * 500
                    }, Astar.manhattan), stroke)
            }

            fun connect(to: Room) {
                if (!connected.add(to) || to === this) return
                val midpoint = Tmp.v1.set(to.x.toFloat(), to.y.toFloat()).add(x.toFloat(), y.toFloat()).scl(0.5f)
                rand.nextFloat()

                if (alt) {
                    midpoint.add(Tmp.v2.set(1f, 0f).setAngle(
                        Angles.angle(to.x.toFloat(), to.y.toFloat(), x.toFloat(), y.toFloat()) + 90f * (if (rand.chance(
                                0.5)
                        ) 1f else -1f)).scl(Tmp.v1.dst(
                        x.toFloat(), y.toFloat()) * 2f))
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
                val path = pathfind(x1, y1, x2, y2, { tile: Tile ->
                    (if (tile.solid() || !tile.floor().isLiquid) 70f else 0f) + noise(tile.x.toFloat(),
                        tile.y.toFloat(), 2.0, 0.4, (1f / nscl).toDouble()) * 500
                }, Astar.manhattan)
                path.each { t: Tile ->
                    //don't place liquid paths near the core
                    if (Mathf.dst2(t.x.toFloat(), t.y.toFloat(), x2.toFloat(), y2.toFloat()) <= avoid * avoid) {
                        return@each
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
                                    //TODO does not respect tainted floors
                                    other.setFloor(
                                        (if (floor === Blocks.sand || floor === Blocks.salt) Blocks.sandWater else Blocks.darksandTaintedWater) as Floor)
                                }
                            }
                        }
                    }
                }
            }

            fun connectLiquid(to: Room?) {
                if (to === this) return
                val midpoint = Tmp.v1.set(to!!.x.toFloat(), to.y.toFloat()).add(x.toFloat(), y.toFloat()).scl(0.5f)
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

        for (i in 0 until rooms) {
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint))
            val rx = (width / 2f + Tmp.v1.x)
            val ry = (height / 2f + Tmp.v1.y)
            val maxrad = radius - Tmp.v1.len()
            val rrad = min(rand.random(9f, maxrad / 2f).toDouble(), 30.0).toFloat()
            roomseq.add(Room(rx.toInt(), ry.toInt(), rrad.toInt()))
        }
        //check positions on the map to place the player spawn. this needs to be in the corner of the map
        var spawn: Room? = null
        val enemies = Seq<Room>()
        val enemySpawns: Int = rand.random(1, max((sector.threat * 4).toInt(), 1))
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
                        val tile = tiles[cx + rx, cy + ry]
                        if (tile == null || tile.floor().liquidDrop != null) {
                            waterTiles++
                        }
                    }
                }

                if (waterTiles <= 4 || (i + angleStep >= 360)) {
                    roomseq.add(Room(cx, cy, rand.random(8, 15)).also { spawn = it })

                    for (j in 0 until enemySpawns) {
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
        //clear radius around each room
        for (room in roomseq) {
            erase(room.x, room.y, room.radius)
        }
        //randomly connect rooms together
        val connections: Int = rand.random(max(rooms - 1, 1), rooms + 3)
        for (i in 0 until connections) {
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

        for (i in 0 until tlen) {
            val tile = tiles.geti(i)
            if (tile.block() === Blocks.air) {
                total++
                if (tile.floor().liquidDrop === Liquids.water) {
                    waters++
                }
            }
        }
        val naval = waters.toFloat() / total >= 0.19f
        //create water pathway if the map is flooded
        if (naval) {
            for (room in enemies) {
                room.connectLiquid(spawn)
            }
        }

        distort(10f, 6f)
        //rivers
        pass { x: Int, y: Int ->
            if (block.solid) return@pass
            val v = sector.rect.project(x.toFloat(), y.toFloat())
            val rr = Simplex.noise2d(sector.id, 2f.toDouble(), 0.6, (1f / 7f).toDouble(), x.toDouble(),
                y.toDouble()) * 0.1f
            val value = Ridged.noise3d(2, v.x.toDouble(), v.y.toDouble(), v.z.toDouble(), 1, 1f / 55f) + rr - rawHeight(
                v) * 0f
            val rrscl = rr * 44 - 2
            if (value > 0.17f && !Mathf.within(x.toFloat(), y.toFloat(), fspawn!!.x.toFloat(),
                    fspawn.y.toFloat(), 12 + rrscl)
            ) {
                val deep = value > 0.17f + 0.1f && !Mathf.within(x.toFloat(), y.toFloat(), fspawn.x.toFloat(),
                    fspawn.y.toFloat(), 15 + rrscl)
                val spore = floor !== Blocks.sand && floor !== Blocks.salt
                //do not place rivers on ice, they're frozen
                //ignore pre-existing liquids
                if (!(floor === Blocks.ice || floor === Blocks.iceSnow || floor === Blocks.snow || floor.asFloor().isLiquid)) {
                    floor = if (spore) (if (deep) Blocks.taintedWater else Blocks.darksandTaintedWater) else (if (deep) Blocks.water else (if (floor === Blocks.sand || floor === Blocks.salt) Blocks.sandWater else Blocks.darksandWater))
                }
            }
        }
        //shoreline setup
        pass { x: Int, y: Int ->
            val deepRadius = 3
            if (floor.asFloor().isLiquid && floor.asFloor().shallow) {
                for (cx in -deepRadius..deepRadius) {
                    for (cy in -deepRadius..deepRadius) {
                        if ((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius) {
                            val wx = cx + x
                            val wy = cy + y
                            val tile = tiles[wx, wy]
                            if (tile != null && (!tile.floor().isLiquid || tile.block() !== Blocks.air)) {
                                //found something solid, skip replacing anything
                                return@pass
                            }
                        }
                    }
                }

                floor = if (floor === Blocks.darksandTaintedWater) Blocks.taintedWater else Blocks.water
            }
        }

        if (naval) {
            val deepRadius = 2
            //TODO code is very similar, but annoying to extract into a separate function
            pass { x: Int, y: Int ->
                if (floor.asFloor().isLiquid && !floor.asFloor().isDeep && !floor.asFloor().shallow) {
                    for (cx in -deepRadius..deepRadius) {
                        for (cy in -deepRadius..deepRadius) {
                            if ((cx) * (cx) + (cy) * (cy) <= deepRadius * deepRadius) {
                                val wx = cx + x
                                val wy = cy + y
                                val tile = tiles[wx, wy]
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
        }
        val ores = Seq.with(Blocks.oreCopper, Blocks.oreLead)
        val poles = abs(sector.tile.v.y.toDouble()).toFloat()
        val nmag = 0.5f
        val scl = 1f
        val addscl = 1.3f

        if (Simplex.noise3d(seed, 2.0, 0.5, scl.toDouble(), sector.tile.v.x.toDouble(), sector.tile.v.y.toDouble(),
                sector.tile.v.z.toDouble()) * nmag + poles > 0.25f * addscl
        ) {
            ores.add(Blocks.oreCoal)
        }

        if (Simplex.noise3d(seed, 2.0, 0.5, scl.toDouble(), (sector.tile.v.x + 1).toDouble(),
                sector.tile.v.y.toDouble(), sector.tile.v.z.toDouble()) * nmag + poles > 0.5f * addscl
        ) {
            ores.add(Blocks.oreTitanium)
        }

        if (Simplex.noise3d(seed, 2.0, 0.5, scl.toDouble(), (sector.tile.v.x + 2).toDouble(),
                sector.tile.v.y.toDouble(), sector.tile.v.z.toDouble()) * nmag + poles > 0.7f * addscl
        ) {
            ores.add(Blocks.oreThorium)
        }

        if (rand.chance(0.25)) {
            ores.add(Blocks.oreScrap)
        }
        val frequencies = FloatSeq()
        for (i in 0 until ores.size) {
            frequencies.add(rand.random(-0.1f, 0.01f) - i * 0.01f + poles * 0.04f)
        }

        pass { x: Int, y: Int ->
            if (!floor.asFloor().hasSurface()) return@pass
            val offsetX = x - 4
            val offsetY = y + 23
            for (i in ores.size - 1 downTo 0) {
                val entry = ores[i]
                val freq = frequencies[i]
                if (abs((0.5f - noise(offsetX.toFloat(), (offsetY + i * 999).toFloat(), 2.0, 0.7,
                        (40 + i * 2).toDouble())).toDouble()) > 0.22f + i * 0.01 &&
                    abs((0.5f - noise(offsetX.toFloat(), (offsetY - i * 999).toFloat(), 1.0, 1.0,
                        (30 + i * 4).toDouble())).toDouble()) > 0.37f + freq
                ) {
                    ore = entry
                    break
                }
            }
            if (ore === Blocks.oreScrap && rand.chance(0.33)) {
                floor = Blocks.metalFloorDamaged
            }
        }

        trimDark()

        median(2)

        inverseFloodFill(tiles.getn(spawn!!.x, spawn!!.y))

        tech()

        pass { x: Int, y: Int ->
            //random moss
            if (floor === Blocks.sporeMoss) {
                if (abs((0.5f - noise((x - 90).toFloat(), y.toFloat(), 4.0, 0.8, 65.0)).toDouble()) > 0.02) {
                    floor = IBlocks.红土
                }
            }
            //tar
            if (floor === Blocks.darksand) {
                if (abs((0.5f - noise((x - 40).toFloat(), y.toFloat(), 2.0, 0.7,
                        80.0)).toDouble()) > 0.25f && abs(
                        (0.5f - noise(x.toFloat(), (y + sector.id * 10).toFloat(), 1.0, 1.0,
                            60.0)).toDouble()) > 0.41f && !(roomseq.contains { r: Room ->
                        Mathf.within(x.toFloat(), y.toFloat(), r.x.toFloat(), r.y.toFloat(), 30f)
                    })
                ) {
                    floor = Blocks.tar
                }
            }
            //hotrock tweaks
            if (floor === Blocks.hotrock) {
                if (abs((0.5f - noise((x - 90).toFloat(), y.toFloat(), 4.0, 0.8, 80.0)).toDouble()) > 0.035) {
                    floor = Blocks.basalt
                } else {
                    ore = Blocks.air
                    var all = true
                    for (p in Geometry.d4) {
                        val other = tiles[x + p.x, y + p.y]
                        if (other == null || (other.floor() !== Blocks.hotrock && other.floor() !== Blocks.magmarock)) {
                            all = false
                        }
                    }
                    if (all) {
                        floor = Blocks.magmarock
                    }
                }
            } else if (genLakes && floor !== Blocks.basalt && floor !== Blocks.ice && floor.asFloor().hasSurface()) {
                val noise = noise((x + 782).toFloat(), y.toFloat(), 5.0, 0.75, 260.0, 1.0)
                if (noise > 0.67f && !roomseq.contains { e: Room ->
                        Mathf.within(x.toFloat(), y.toFloat(), e.x.toFloat(), e.y.toFloat(), 14f)
                    }) {
                    floor = if (noise > 0.72f) {
                        if (noise > 0.78f) Blocks.taintedWater else (if (floor === Blocks.sand) Blocks.sandWater else Blocks.darksandTaintedWater)
                    } else {
                        (if (floor === Blocks.sand) floor else Blocks.darksand)
                    }
                }
            }

            if (rand.chance(0.0075)) {
                //random spore trees
                var any = false
                var all = true
                for (p in Geometry.d4) {
                    val other = tiles[x + p.x, y + p.y]
                    if (other != null && other.block() === Blocks.air) {
                        any = true
                    } else {
                        all = false
                    }
                }
                if (any && ((block === Blocks.snowWall || block === Blocks.iceWall) || (all && block === Blocks.air && floor === Blocks.snow && rand.chance(
                        0.03)))
                ) {
                    block = if (rand.chance(0.5)) Blocks.whiteTree else Blocks.whiteTreeDead
                }
            }
            //random stuff
            run { // 使用标签
                for (i in 0 until 4) {
                    val near = tiles.get(x + Geometry.d4[i].x, y + Geometry.d4[i].y)
                    if (near != null && near.block() != Blocks.air) {
                        return@run // 跳出 dec 块
                    }
                }

                if (rand.chance(0.01) && floor.asFloor().hasSurface() && block == Blocks.air) {
                    block = dec.get(floor, floor.asFloor().decoration)
                }
            }
        }
        val difficulty = sector.threat
        val ruinCount = rand.random(-2, 4)

        if (ruinCount > 0) {
            val ints = IntSeq(width * height / 4)
            val padding = 25
            //create list of potential positions
            for (x in padding until width - padding) {
                for (y in padding until height - padding) {
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
                if (Mathf.within(x.toFloat(), y.toFloat(), spawn!!.x.toFloat(), spawn!!.y.toFloat(), 18f)) {
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
                //actually place the part
                if (part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, rand) { cx: Int, cy: Int ->
                        val other = tiles.getn(cx, cy)
                        if (other.floor().hasSurface()) {
                            other.setOverlay(Blocks.oreScrap)
                            for (j in 1..2) {
                                for (p in Geometry.d8) {
                                    val t = tiles[cx + p.x * j, cy + p.y * j]
                                    if (t != null && t.floor().hasSurface() && rand.chance(if (j == 1) 0.4 else 0.2)) {
                                        t.setOverlay(Blocks.oreScrap)
                                    }
                                }
                            }
                        }
                    }) {
                    placed++
                    val debrisRadius = (max(part.schematic.width.toDouble(),
                        part.schematic.height.toDouble()) / 2 + 3).toInt()
                    Geometry.circle(x, y, tiles.width, tiles.height, debrisRadius) { cx: Int, cy: Int ->
                        val dst = Mathf.dst(cx.toFloat(), cy.toFloat(), x.toFloat(), y.toFloat())
                        val removeChance = Mathf.lerp(0.05f, 0.5f, dst / debrisRadius)
                        val other = tiles.getn(cx, cy)
                        if (other.build != null && other.isCenter) {
                            if (other.team() === Team.derelict && rand.chance(removeChance.toDouble())) {
                                other.remove()
                            } else if (rand.chance(0.5)) {
                                other.build.health -= rand.random(other.build.health * 0.9f)
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

        Schematics.placeLaunchLoadout(spawn!!.x, spawn!!.y)

        for (espawn in enemies) {
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn)
        }

        if (sector.hasEnemyBase()) {
            basegen.generate(tiles, enemies.map { r: Room -> tiles.getn(r.x, r.y) },
                tiles[spawn!!.x, spawn!!.y], Vars.state.rules.waveTeam, sector, difficulty)

            sector.info.attack = true
            Vars.state.rules.attackMode = sector.info.attack
        } else {
            sector.info.winWave = 10 + 5 * max((difficulty * 10).toInt(), 1)
            Vars.state.rules.winWave = sector.info.winWave
        }
        val waveTimeDec = 0.4f

        Vars.state.rules.waveSpacing = Mathf.lerp((60 * 65 * 2).toFloat(), 60f * 60f * 1f,
            max((difficulty - waveTimeDec).toDouble(), 0.0).toFloat())
        Vars.state.rules.waves = true
        Vars.state.rules.env = sector.planet.defaultEnv
        Vars.state.rules.enemyCoreBuildRadius = 600f
        //spawn air only when spawn is blocked
        Vars.state.rules.spawns = Waves.generate(difficulty, Rand(sector.id.toLong()), Vars.state.rules.attackMode,
            Vars.state.rules.attackMode && Vars.spawner.countGroundSpawns() == 0, naval)
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