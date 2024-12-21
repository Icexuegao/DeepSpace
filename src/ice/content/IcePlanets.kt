package ice.content

import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Vec3
import arc.util.Tmp
import arc.util.noise.Simplex
import ice.content.blocks.EnvironmentBlocks
import ice.graphics.IceColors
import mindustry.content.Blocks
import mindustry.content.Planets
import mindustry.game.Rules
import mindustry.game.Team
import mindustry.graphics.g3d.HexMesh
import mindustry.graphics.g3d.HexSkyMesh
import mindustry.graphics.g3d.MultiMesh
import mindustry.maps.generators.PlanetGenerator
import mindustry.type.Planet
import mindustry.type.Sector
import mindustry.world.Tile
import mindustry.world.TileGen
import mindustry.world.Tiles
import mindustry.world.blocks.environment.Floor
import kotlin.math.max

class IcePlanets {

    companion object {
        lateinit var alon: Planet

        fun load() {
            object : Planet("alon", Planets.sun, 1f, 3) {}.apply {
                generator = object : PlanetGenerator() {
                    var scl = 5f
                    var waterOffset = 0.07f
                    var water: Float = 2f / 20
                    override fun allowLanding(sector: Sector?): Boolean {
                        return true
                    }

                    override fun generate(tiles: Tiles?, sec: Sector?, seed: Int) {
                        this.tiles = tiles
                        this.seed = seed + baseSeed
                        sector = sec
                        width = tiles!!.width
                        height = tiles.height
                        rand.setSeed((sec!!.id + seed + baseSeed).toLong())

                        val gen = TileGen()
                        for (y in 0 until height) {
                            for (x in 0 until width) {
                                gen.reset()
                                val position =
                                    sector.rect.project(x / tiles.width.toFloat(), y / tiles.height.toFloat())
                                genTile(position, gen)
                                tiles[x, y] = Tile(x, y, gen.floor, gen.overlay, gen.block)
                            }
                        }

                        generate(tiles)
                    }

                    override fun genTile(position: Vec3?, tile: TileGen?) {
                        if (tile != null) {
                            tile.floor = EnvironmentBlocks.红冰
                        }
                        super.genTile(position, tile)
                    }

                    override fun getSectorSize(sector: Sector?): Int {
                        return super.getSectorSize(sector)
                    }

                    override fun generate(tiles: Tiles?) {
                        val get = tiles?.get(80, 80)
                        get?.setBlock(Blocks.coreShard, Team.sharded)
                        tiles?.eachTile {
                            val snoise = Simplex.noise2d(sector.id, 1.0, 0.1, 0.006, it.x.toDouble(), it.y.toDouble())
                            if (snoise >= 0.5) {

                                val liparite = EnvironmentBlocks.流纹岩
                                if (liparite is Floor) {
                                    it.setFloor(liparite)
                                    val noise2d = Simplex.noise2d(
                                        sector.id + 2, 1.0, 0.1, 0.006, it.x.toDouble(), it.y.toDouble()
                                    )
                                    if (noise2d >= 0.5) {
                                        it.setBlock(EnvironmentBlocks.流纹岩墙)
                                    }

                                }
                            }
                        }

                        super.generate(tiles)
                    }

                    override fun getHeight(position: Vec3?): Float {
                        val height = rawHeight(position!!)
                        //Log.info(height)
                        return max(height.toDouble(), water.toDouble()).toFloat()
                    }

                    fun rawHeight(position: Vec3): Float {
                        var position1 = position
                        position1 = Tmp.v33.set(position1).scl(scl)
                        return (Mathf.pow(
                            Simplex.noise3d(
                                seed,
                                7.0,
                                0.5,
                                (1f / 3f).toDouble(),
                                position1.x.toDouble(),
                                position1.y.toDouble(),
                                position1.z.toDouble()
                            ), 2.3f
                        ) + waterOffset) / (1f + waterOffset)
                    }

                    override fun getColor(position: Vec3?): Color {
                        return if (position!!.z > -0.05f && position.z < 0.05f) {
                            IceColors.b2
                        } else {
                            Color(Rand().nextFloat(), Rand().nextFloat(), Rand().nextFloat(), 0.3f)
                        }

                    }
                }
                alon = this
                meshLoader = Prov { HexMesh(this, 5) }
                cloudMeshLoader = Prov {
                    MultiMesh(
                        HexSkyMesh(
                            this, 2, 0.15f, 0.14f, 5, Color.valueOf("eba768").a(0.75f), 2, 0.42f, 1f, 0.43f
                        ), HexSkyMesh(
                            this, 3, 0.6f, 0.15f, 5, Color.valueOf("eea293").a(0.75f), 2, 0.42f, 1.2f, 0.45f
                        )
                    )
                }

                alwaysUnlocked = true
                landCloudColor = Color.valueOf("ed6542")
                atmosphereColor = Color.valueOf("f07218")
                startSector = 10
                atmosphereRadIn = 0.02f
                atmosphereRadOut = 0.3f
                tidalLock = true
                orbitSpacing = 2f
                totalRadius += 2.6f
                lightSrcTo = 0.5f
                lightDstFrom = 0.2f
                clearSectorOnLose = true
                defaultCore = Blocks.coreBastion
                iconColor = Color.valueOf("ff9266")
                allowWaves = true
                allowWaveSimulation = true
                allowSectorInvasion = true
                allowLaunchSchematics = true
                enemyCoreSpawnReplace = true
                allowLaunchLoadout = true
                //doesn't play well with configs
                //doesn't play well with configs
                prebuildBase = false
                ruleSetter = Cons { r: Rules ->
                    r.waveTeam = Team.crux
                    r.placeRangeCheck = false
                    r.showSpawns = false
                }
            }
        }
    }
}