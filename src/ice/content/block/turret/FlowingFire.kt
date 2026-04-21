package ice.content.block.turret

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.content.ILiquids
import ice.entities.bullet.ContinuousFlameBulletType

import mindustry.content.StatusEffects
import mindustry.gen.Sounds
import mindustry.type.Category
import singularity.world.blocks.turrets.ContinuousTurret

class FlowingFire :ContinuousTurret("flowingFire") {

  init {
    localization {
      zh_CN {
        this.localizedName = "流火"
        description = "小型等离子炮塔\n持续消耗燃料以喷射高热的等离子火焰,近距离内十分有效"
      }
    }
    drawers
    health = 1800
    armor = 2f
    size = 2
    range = 115f
    recoil = 1f
    shootCone = 10f
    outlinedIcon=-1
    loopSound = Sounds.loopTech
    shootSound = Sounds.none
    loopSoundVolume = 1f
    targetInterval = 5f

    requirements(
      Category.turret, IItems.铜锭, 155, IItems.石英玻璃, 55, IItems.铬锭, 85, IItems.钍锭, 65
    )
    newAmmo(ContinuousFlameBulletType().apply {
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
    }, false) { a, b -> }
    consume!!.apply {
      liquid(ILiquids.沼气, 15f / 60f)
    }

    newAmmo(ContinuousFlameBulletType().apply {
      length = 140f
      damage = 20f
      oscScl = 0.8f
      oscMag = 0.02f
      status = StatusEffects.burning
      statusDuration = 30f
      drawFlare = true
      flareColor = ILiquids.氢气.color
      flareWidth = 3f
      flareLength = 40f
      lengthInterp = Interp.slope
      flareInnerScl = 0.5f
      flareInnerLenScl = 0.5f
      rotateFlare = true
      flareRotSpeed = 1.2f
      colors = arrayOf(
        ILiquids.氢气.color.cpy().mul(0.6f, 0.6f, 0.6f, 1f),
        ILiquids.氢气.color.cpy().mul(0.7f, 0.7f, 0.7f, 1f),
        ILiquids.氢气.color.cpy().mul(0.8f, 0.8f, 0.8f, 1f),
        ILiquids.氢气.color,
        Color.valueOf("FFFFFFCC")
      )
    })
    consume!!.apply {
      liquids(ILiquids.氢气, 10f / 60f, ILiquids.氧气, 10f / 60f)
    }
  }
}