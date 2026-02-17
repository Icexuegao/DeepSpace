package ice.content.unit

import arc.scene.ui.layout.Table
import ice.content.ILiquids
import ice.content.IStatus
import ice.content.IUnitTypes
import ice.entities.bullet.LaserBulletType
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.LiquidExplodeAbility
import mindustry.entities.abilities.SpawnDeathAbility
import mindustry.entities.bullet.SapBulletType
import mindustry.gen.Sounds
import mindustry.gen.TimedKillUnit
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.type.Weapon
import mindustry.ui.Bar
import mindustry.world.meta.BlockFlag

class Mosquito : IceUnitType("unit_mosquito", TimedKillUnit::class.java) {
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
      desc(zh_CN, "疟蚊", "轻型空中突击单位.由飞蠓改进而成,体型略有增长,体表覆盖轻便的几丁质甲壳,在保持机动性的同时获得了一定的防护能力.阵亡后会洒下一滩血肉赘生物.")
    }
    targetFlags = arrayOf(
      BlockFlag.reactor,
      BlockFlag.generator,
      BlockFlag.battery,
      BlockFlag.factory
    )
    health = 1080f
    armor = 3f
    hitSize = 18f
    range = 240f
    speed = 4f
    drag = 0.1f
    rotateSpeed = 20f
    engineOffset = 6f
    engineSize = 2.7f
    trailLength = 4
    outlineColor = "1F1F1F".toColor()

    weapons.add(Weapon().apply {
      x = 0f
      y = 10f
      reload = 15f
      shootY = 0f
      mirror = false
      autoTarget = true
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(45f).apply {
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

    weapons.add(Weapon().apply {
      x = 5f
      y = 10f
      shootY = 0f
      reload = 6f
      rotate = true
      shootCone = 45f
      alternate = false
      shootSound = Sounds.shootSap
      bullet = SapBulletType().apply {
        damage = 45f
        length = 120f
        sapStrength = 1.5f
        color = "E88EC9".toColor()
        hitColor = "E88EC9".toColor()
        status = IStatus.寄生
        statusDuration = 30f
        recoil = -1f
        knockback = -5f
        shootEffect = Fx.shootSmall
      }
    })

    abilities.add(LiquidExplodeAbility().apply {
      liquid = ILiquids.血肉赘生物
      amount = 18f
    })

    abilities.add(SpawnDeathAbility().apply {
      unit = IUnitTypes.飞蠓
      amount = 2
      randAmount = 1
    })
  }

  override fun display(unit: Unit, table: Table) {
    super.display(unit, table)
    unit as TimedKillUnit
    (table.getChildren().get(1) as Table).add(Bar("存活时间", Pal.accent) { 1 - unit.fin() }).row()
  }
}