package ice.content.unit

import arc.scene.ui.layout.Table
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.bullet.LaserBulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.LiquidExplodeAbility
import mindustry.gen.Sounds
import mindustry.gen.TimedKillUnit
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.type.Weapon
import mindustry.ui.Bar
import mindustry.world.meta.BlockFlag

class FlyingMidges : IceUnitType("unit_flyingMidges", TimedKillUnit::class.java) {
  init {
    lifetime = 1800f
    flying = true
    lowAltitude = true
    circleTarget = true
    useUnitCap = false
    createScorch = false
    playerControllable = false
    itemCapacity = 0

    BaseBundle.bundle {
      desc(zh_CN, "飞蠓", "轻型空中突击单位.体型轻盈,行动敏捷,以其机动性在进攻中占据主导地位,集群作战中有显著优势")
    }
    targetFlags = arrayOf(
      BlockFlag.reactor,
      BlockFlag.generator,
      BlockFlag.battery,
      BlockFlag.factory
    )
    health = 360f
    armor = 1f
    hitSize = 9f
    range = 240f
    speed = 4f
    drag = 0.1f
    rotateSpeed = 20f
    engineOffset = 6f
    engineSize = 2.5f
    trailLength = 4
    outlineColor = "1F1F1F".toColor()
    abilities.add(LiquidExplodeAbility().apply {
      liquid = ILiquids.血肉赘生物
      amount = 9f
      radAmountScale = 5f
      radScale = 1f
      noiseMag = 6.5f
      noiseScl = 5f
    })
    weapons.add(Weapon().apply {
      x = 0f
      y = 4f
      reload = 15f
      shootY = 0f
      mirror = false
      autoTarget = true
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(25f).apply {
        lifetime = 21f
        length = 80f
        width = 8f
        recoil = -1f
        colors = arrayOf(
          "D75B6E".toColor(), "E78F92".toColor(), "FFF0F0".toColor()
        )
        status = IStatus.熔融
        statusDuration = 30f
        hitEffect = Fx.hitLancer
      }
    })
  }

  override fun display(unit: Unit, table: Table) {
    super.display(unit, table)
    unit as TimedKillUnit
    (table.getChildren().get(1) as Table).add(Bar("存活时间", Pal.accent) { 1 - unit.fin() }).row()
  }
}