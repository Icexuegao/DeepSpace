package ice.content.unit

import arc.scene.ui.layout.Table
import ice.content.IStatus
import ice.content.unit.flying.Veto.ArmorBrokenBulletType
import ice.library.IFiles.appendModName
import ice.library.util.toColor
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.DeathGiftAbility
import mindustry.content.Fx
import mindustry.entities.abilities.RegenAbility
import mindustry.gen.LegsUnit
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.type.UnitType
import mindustry.type.Weapon

class Weaver : IceUnitType("unit_weaver", LegsUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "罗织", "敌特种多足步进机甲,能在短时间内编织密集的火力网", "以一种取之不尽的原生六足甲壳生物为基底,移除不必要的器官,进行代谢优化,植入控制芯片,然后整体置入标准外骨骼中")
    }
    health = 57900f
    hitSize = 32f
    armor = 57f
    drag = 0.05f
    speed = 1.08f
    groundLayer = 75f
    rotateSpeed = 3.6f
    legCount = 6
    legLength = 36f
    stepShake = 0.2f
    legGroupSize = 3
    legMoveSpace = 1f
    legExtension = -3f
    legBaseOffset = 14f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.95f
    legForwardScl = 0.9f
    legSplashRange = 12f
    legSplashDamage = 100f
    hovering = true
    singleTarget = true
    lockLegBase = true
    allowLegStep = true
    outlineColor = "1F1F1F".toColor()
    legContinuousMove = true

    abilities.add(RegenAbility().apply {
      percentAmount = 1f / 100f
    }, DeathGiftAbility(160f, IStatus.屠戮, 900f, 0.1f, 1000f))

    weapons.add(strafe(14f, 3.25f, true), strafe(-8.5f, -11.25f))
  }

  fun strafe(wx: Float, wy: Float, under: Boolean = false): Weapon {
    val arrmorBullet = ArmorBrokenBulletType(17f, 237f, 23f, 0.2f, 0.1f).apply {
      width = 4f
      height = 32f
      status = IStatus.破甲III
      statusDuration = 20f
      pierceArmor = false
      pierceDamageFactor = 0.8f
      hitEffect = Fx.hitSquaresColor
      hitColor = Pal.bulletYellowBack
      shootEffect = Fx.shootSmokeSquareSparse
    }

    return object : Weapon("unit_weaver-machineGun".appendModName()) {
      override fun addStats(u: UnitType, t: Table) {
        super.addStats(u, t)
        t.row()
        t.add("[lightgray]每次穿透衰减[stat]${arrmorBullet.pierceFactor()}%[lightgray]伤害").row()
        t.add("[lightgray]对[stat]护甲[lightgray]额外造成[stat]${arrmorBullet.armorDamage()}倍护甲[lightgray]的伤害并降低[stat]${arrmorBullet.armorReduce()}[lightgray]点护甲")
      }
    }.apply {
      x = wx
      y = wy
      recoil = 1f
      shake = 3f
      reload = 3f
      shootY = 19f
      rotate = true
      inaccuracy = 1f
      shootCone = 5f
      rotateSpeed = 3f
      rotationLimit = 25f
      cooldownTime = 65f
      layerOffset = if (under) -0.001f else 0f
      shootSound = Sounds.shootSalvo
      bullet = arrmorBullet
    }
  }
}