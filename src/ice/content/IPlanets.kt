package ice.content

import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import mindustry.game.Rules
import mindustry.game.Team
import mindustry.graphics.Pal
import mindustry.graphics.g3d.HexMesh
import mindustry.graphics.g3d.HexSkyMesh
import mindustry.graphics.g3d.MultiMesh
import mindustry.graphics.g3d.SunMesh
import mindustry.maps.planet.SerpuloPlanetGenerator
import mindustry.type.Planet

object IPlanets {
    val 伊甸 = Planet("eden", null, 6f).apply {
        bloom = true
        accessible = false
        meshLoader = Prov {
            SunMesh(this, 4, 5.0, 0.3, 1.7, 1.2, 1.0, 1.1f, Color.valueOf("95d6fa"), Color.valueOf("95d6fa"),
                Color.valueOf("abe0fd"), Color.valueOf("abe0fd"), Color.valueOf("afd6ec"), Color.valueOf("afd6ec"))
        }
    }
     var 阿德里= object : Planet("ardery", 伊甸, 1f, 4) {
        override fun init() {
            super.init()
            sectors.forEach {
                it.info.bestCoreType = IBlocks.开采核心
            }
        }
    }.apply {
        defaultCore = IBlocks.开采核心
        generator = SerpuloPlanetGenerator()
        meshLoader = Prov { HexMesh(this, 6) }
        cloudMeshLoader = Prov {
            MultiMesh(
                HexSkyMesh(this, 11, 0.15f, 0.13f, 5, Color().set(Pal.spore).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f,
                    0.38f),
                HexSkyMesh(this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(Pal.spore, 0.55f).a(0.75f), 2, 0.45f, 1f,
                    0.41f)
            )
        }

        launchCapacityMultiplier = 0.5f
        sectorSeed = 1
        allowWaves = true
        allowLegacyLaunchPads = true
        allowWaveSimulation = true
        allowSectorInvasion = true
        allowLaunchSchematics = true
        enemyCoreSpawnReplace = true
        allowLaunchLoadout = true
        //不能很好地与配置配合使用
        prebuildBase = false
        ruleSetter = Cons { r: Rules ->
            r.waveTeam = Team.crux
            r.placeRangeCheck = false
            r.showSpawns = false
            r.coreDestroyClear = true
        }
        showRtsAIRule = true
        iconColor = Color.valueOf("7d4dff")
        atmosphereColor = Color.valueOf("3c1b8f")
        atmosphereRadIn = 0.02f
        atmosphereRadOut = 0.3f
        startSector = 9
        alwaysUnlocked = true
        allowSelfSectorLaunch = true
        landCloudColor = Pal.spore.cpy().a(0.5f)
    }
    fun load()= Unit
}