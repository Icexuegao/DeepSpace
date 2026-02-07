package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.StatusEffects
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret

class FlowingFire : ContinuousLiquidTurret("flowingFire") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "流火", "小型等离子炮塔\n持续消耗燃料以喷射高热的等离子火焰,近距离内十分有效")
    }
    health = 1800
    armor = 2f
    size = 2
    range = 115f
    recoil = 1f
    shootCone = 10f
    liquidConsumed = 0.2f
    loopSound = Sounds.loopTech
    shootSound = Sounds.none
    loopSoundVolume = 1f
    targetInterval = 5f
    targetUnderBlocks = false
    requirements(
      Category.turret, IItems.铜锭, 155, IItems.石英玻璃, 55, IItems.铬锭, 85, IItems.钍锭, 65
    )
    ammo(ILiquids.沼气, ContinuousFlameBulletType().apply {
      length = 120f
      damage = 30f
      oscScl = 0.8f
      oscMag = 0.02f
      status = StatusEffects.burning
      statusDuration = 30f
      drawFlare = true
      flareColor = Color.valueOf("FAAF87")
      flareWidth = 3f
      flareLength = 40f
      lengthInterp = Interp.slope
      flareInnerScl = 0.5f
      flareInnerLenScl = 0.5f
      rotateFlare = true
      flareRotSpeed = 1.2f
      colors = arrayOf(
        Color.valueOf("FAAF878C"), Color.valueOf("FAAF87B2"), Color.valueOf("FAAF87CC"), Color.valueOf("FAAF87"), Color.valueOf("FFFFFFCC")
      )
    })
  }
}