package ice.content

import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import ice.content.block.EffectBlocks
import ice.game.IceTeam
import ice.game.Schematics
import ice.graphics.IceColor
import universecore.world.Load
import ice.maps.planet.ardery.ArderyPlanetGenerator
import mindustry.game.Team
import mindustry.graphics.g3d.HexMesh
import mindustry.graphics.g3d.HexSkyMesh
import mindustry.graphics.g3d.MultiMesh
import mindustry.graphics.g3d.SunMesh
import mindustry.type.ItemStack
import singularity.type.IcePlanet

object IPlanets :Load {
  val 伊甸 = IcePlanet("eden", null, 6f).apply {
    localization {
      zh_CN {
        this.localizedName = "伊甸"
      }
    }
    bloom = true
    accessible = false
    iconColor = IceColor.b4
    meshLoader = Prov {
      SunMesh(
        this,
        4,
        5.0,
        0.3,
        1.7,
        1.2,
        1.0,
        1.1f,

        Color.valueOf("95d6fa"),
        Color.valueOf("95d6fa"),
        Color.valueOf("abe0fd"),
        Color.valueOf("abe0fd"),
        Color.valueOf("afd6ec"),
        Color.valueOf("afd6ec")
      )
    }

  }
 var 阿德里 =object : IcePlanet("ardery", 伊甸, 1f, 4){
   override fun postInit() {
     super.postInit()
   }
 }.apply {
    localization {
      zh_CN {
        this.localizedName = "阿德里"
      }
    }
    generator = ArderyPlanetGenerator()
    generator.defaultLoadout = Schematics.虔信方垒
    meshLoader = Prov { HexMesh(this, 6) }
    cloudMeshLoader = Prov {
      MultiMesh(
        HexSkyMesh(
          this, 11, 0.15f, 0.13f, 5, Color().set(IceColor.r3).mul(0.9f).a(0.75f), 2, 0.45f, 0.9f, 0.38f
        ), HexSkyMesh(
          this, 1, 0.6f, 0.16f, 5, Color.white.cpy().lerp(IceColor.r2, 0.55f).a(0.75f), 2, 0.45f, 1f, 0.41f
        )
      )
    }
    iconColor = IceColor.r2
    sectorSeed = 1
    allowWaves = true
    defaultCore = EffectBlocks.虔信方垒
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
  }
}