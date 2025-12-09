package ice.content

import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import ice.library.world.ContentLoad
import ice.content.block.Effect
import ice.game.IceTeam
import ice.graphics.IceColor
import ice.maps.planet.ardery.ArderyPlanetGenerator
import ice.ui.bundle.BaseBundle.Bundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.game.Team
import mindustry.graphics.g3d.HexMesh
import mindustry.graphics.g3d.HexSkyMesh
import mindustry.graphics.g3d.MultiMesh
import mindustry.graphics.g3d.SunMesh
import mindustry.type.ItemStack
import mindustry.type.Planet

object IPlanets : ContentLoad {
    val 伊甸 = Planet("eden", null, 6f).apply {
        bloom = true
        accessible = false
        iconColor = IceColor.b4
        meshLoader = Prov {
            SunMesh(
                this, 4, 5.0, 0.3, 1.7, 1.2, 1.0, 1.1f, Color.valueOf("95d6fa"), Color.valueOf("95d6fa"),
                Color.valueOf("abe0fd"), Color.valueOf("abe0fd"), Color.valueOf("afd6ec"), Color.valueOf("afd6ec")
            )
        }
        bundle {
            desc(zh_CN, "伊甸")
        }
    }
    var 阿德里 = Planet("ardery", 伊甸, 1f, 4).apply {
        generator = ArderyPlanetGenerator()
        generator.defaultLoadout = ice.library.Schematics.虔信方垒
        meshLoader = Prov { HexMesh(this, 6) }
        cloudMeshLoader = Prov {
            MultiMesh(
                HexSkyMesh(
                    this, 11, 0.15f, 0.13f, 5, Color().set(IceColor.r3).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f,
                    0.38f
                ),
                HexSkyMesh(
                    this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(IceColor.r2, 0.55f).a(0.75f), 2, 0.45f, 1f,
                    0.41f
                )
            )
        }
        iconColor = IceColor.r2
        sectorSeed = 1
        allowWaves = true
        defaultCore = Effect.虔信方垒
        startSector = 9
        //不能很好地与配置配合使用
        prebuildBase = false
        showRtsAIRule = true
        alwaysUnlocked = true
        landCloudColor = IceColor.r1.cpy().a(0.5f)
        atmosphereColor = IceColor.r1.cpy().a(0.5f)
        atmosphereRadIn = 0.02f
        atmosphereRadOut = 0.3f
        allowLaunchLoadout = true
        allowWaveSimulation = true
        allowSectorInvasion = true
        allowSelfSectorLaunch = true
        allowLegacyLaunchPads = true
        allowLaunchSchematics = true
        enemyCoreSpawnReplace = true
        launchCapacityMultiplier = 0.5f
        ruleSetter = Cons { r ->
            r.defaultTeam = IceTeam.教廷
            r.deconstructRefundMultiplier = 1f
            r.loadout = ItemStack.list(IItems.高碳钢, 100, IItems.低碳钢, 200, IItems.铅锭, 50)
            r.waveTeam = Team.crux
            r.placeRangeCheck = false
            r.showSpawns = false
            r.coreDestroyClear = true
        }
        bundle {
            desc(zh_CN, "阿德里")
        }
    }

}