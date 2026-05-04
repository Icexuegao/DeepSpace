package ice.content.unit

import arc.graphics.Color
import arc.math.geom.Rect
import ice.content.IStatus
import ice.core.IFiles.appendModName

import ice.world.content.unit.IceUnitType
import mindustry.entities.abilities.Ability
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.ShieldArcAbility
import mindustry.gen.Unit
import mindustry.world.meta.BlockFlag

class HeavyPress : IceUnitType("heavyPress") {
  init {
    localization {
      zh_CN {
        localizedName = "重压"
        description = "中型地面突击单位.持续以履带碾压对敌方建筑.正面投射弧形护盾抵御攻击,碾压时减少所受伤害"
        details = "钢铁的履带滚滚向前"
      }
    }
    squareShape = true
    omniMovement = false

    health = 11400f
    armor = 37f
    hitSize = 24f
    speed = 0.6f
    range = 1f
    rotateSpeed = 1.2f
    outlineColor = Color.valueOf("1F1F1F")
    hovering = true
    targetAir = false
    faceTarget = false
    rotateMoveFirst = false
    targetPriority = 4f
    treadFrames = 18
    crushDamage = 20f
    treadPullOffset = 4
    targetFlags = arrayOf(BlockFlag.turret)
    treadRects = arrayOf(Rect(-55f, -61f, 27f, 130f))

    abilities.add(ShieldArcAbility().apply {
      region = "heavyPress-shield".appendModName()
      whenShooting = false
      y = -6f
      max = 3600f
      regen = 3f
      cooldown = 600f
      radius = 30f
      angle = 80f
      width = 5f
    })
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 1f
    },DWDFAbility())
  }
  class DWDFAbility :Ability(){
    override fun update(unit: Unit) {
      if (unit.isShooting){
        unit.apply(IStatus.突袭, 60f)
      }
    }
  }
}