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
import mindustry.content.StatusEffects
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

class BloodAltar : IceUnitType("unit_bloodAltar", TimedKillUnit::class.java) {
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
      desc(zh_CN, "血俎", "中型空中突击单位.由疟蚊改进而成,体表覆盖坚韧的几丁质甲壳.阵亡后会分裂出飞蠓并洒下一滩血肉赘生物")
    }
    health = 3240f
    armor = 3f
    hitSize = 24f
    range = 240f
    speed = 4f
    drag = 0.1f
    rotateSpeed = 20f
    engineOffset = 8f
    engineSize = 3f
    trailLength = 5
    outlineColor = "1F1F1F".toColor()
    targetFlags = arrayOf(
      BlockFlag.reactor, BlockFlag.generator, BlockFlag.battery, BlockFlag.factory
    )
    abilities.add(LiquidExplodeAbility().apply {
      liquid = ILiquids.血肉赘生物
      amount = 24f
    })

    abilities.add(SpawnDeathAbility().apply {
      unit = IUnitTypes.疟蚊
      amount = 1
    })

    abilities.add(SpawnDeathAbility().apply {
      unit = IUnitTypes.飞蠓
      randAmount = 1
    })

    weapons.add(Weapon().apply {
      x = 0f
      y = 3f
      reload = 15f
      shootY = 0f
      mirror = false
      autoTarget = true
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(60f).apply {
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
      x = 5.25f
      y = 8f
      shootY = 0f
      reload = 6f
      rotate = true
      shootCone = 45f
      alternate = false
      shootSound = Sounds.shootSap
      bullet = SapBulletType().apply {
        damage = 60f
        length = 120f
        sapStrength = 1.5f
        color = "E88EC9".toColor()
        hitColor = "E88EC9".toColor()
        status = StatusEffects.sapped
        statusDuration = 30f
        recoil = -1f
        knockback = -5f
        shootEffect = Fx.shootSmall
      }
    })

    weapons.add(Weapon().apply {
      x = 6.5f
      y = 7f
      shootY = 0f
      reload = 6f
      rotate = true
      shootCone = 45f
      alternate = false
      shootSound = Sounds.shootSap
      bullet = SapBulletType().apply {
        damage = 60f
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
  }

  override fun display(unit: Unit, table: Table) {
    super.display(unit, table)
    unit as TimedKillUnit
    (table.getChildren().get(1) as Table).add(Bar("存活时间", Pal.accent) { 1 - unit.fin() }).row()
  }
}