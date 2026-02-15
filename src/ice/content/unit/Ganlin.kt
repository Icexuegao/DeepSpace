package ice.content.unit

import arc.graphics.Color
import arc.math.Interp
import arc.scene.ui.layout.Table
import ice.content.IStatus
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.DeathGiftAbility
import ice.world.content.unit.ability.RepairFieldAbility
import ice.world.content.unit.ability.ShieldAbility
import mindustry.content.Fx
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.type.UnitType
import mindustry.type.Weapon
import mindustry.type.weapons.RepairBeamWeapon

class Ganlin : IceUnitType("ganlin") {
  init {
    bundle {
      desc(zh_CN, "甘霖", "以生物钢作为主要材料,辅以陶钢作为电磁屏蔽层,一般装备甚至无法留下划痕,同时在澎湃的能量输出下,其回复速度令人惊异,\n控制中枢与动力炉紧密相连,在内部结构大规模受损导致动力炉失稳融毁后会一同损毁\n因此,即使工程部门收集到了如此多残骸也难以了解其中枢构造")
    }
    health = 68700f
    hitSize = 48f
    armor = 77f
    speed = 1.08f
    rotateSpeed = 1.2f
    legCount = 6
    legLength = 36f
    stepShake = 0.2f
    legGroupSize = 3
    legMoveSpace = 1f
    legExtension = -3f
    legBaseOffset = 16f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.95f
    legForwardScl = 0.9f
    legSplashRange = 16f
    legSplashDamage = 150f
    hovering = true
    lockLegBase = true
    allowLegStep = true
    outlineColor = Color.valueOf("1F1F1F")
    legContinuousMove = true
    abilities.add(RegenAbility().apply {
      percentAmount = 1f / 100f
    })
    abilities.add(DeathGiftAbility(160f, IStatus.复仇, 900f, 0.1f, 1000f))
    abilities.add(DeathGiftAbility(320f, IStatus.屠戮, 900f, 0.1f, 1000f))
    abilities.add(ShieldAbility(240f, 2400f))
    abilities.add(RepairFieldAbility(200f, 500f, 0.02f).apply {
      y = -1f
      range = 240f
      orbRadius = 12f
      orbMidScl = 0.4f
      orbSinScl = 8f
      orbSinMag = 1f
      particles = 24
      particleSize = 4f
      particleLen = 32f
      rotateScl = 3f
      particleLife = 360f
      particleInterp = Interp(Interp.swing::apply)
    })
    weapons.add(RepairBeamWeapon("甘霖修复".appendModName()).apply {
      x = 0f
      y = -1f
      shootY = 0f
      mirror = false
      laserColor = Color.valueOf("FF5845")
      repairSpeed = 17f
      bullet = BulletType(0f, 0f).apply {
        maxRange = 240f
      }
    })
    var b = object : BasicBulletType() {
      init {
        speed = 8f
        damage = 213f
        lifetime = 45f
        shrinkY = 0f
        ammoMultiplier = 1f
        status = IStatus.损毁
        statusDuration = 180f
        pierce = true
        pierceBuilding = true
        pierceDamageFactor = 0.6f
        width = 13f
        height = 14f
        drag = -0.01f
        hitColor = Pal.bulletYellowBack
        hitEffect = Fx.hitSquaresColor
        shootEffect = Fx.shootSmokeSquare
      }

      override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
        var size = if (entity is Unit) entity.hitSize else (entity as Building).block.size * 8f
        val unit = b.owner as Unit
        var scale = unit.hitSize / if (size > 1) unit.hitSize / size else 1f
        b.damage = damage * scale
        super.hitEntity(b, entity, health)
      }
    }
    weapons.add(object : Weapon("ganlin-weapon".appendModName()) {
      override fun addStats(u: UnitType, t: Table) {
        super.addStats(u, t)
        t.row()
        t.add("[lightgray]对[stat]中小型[lightgray]单位/方块进行[red]压制").row()
      }
    }.apply {
      bullet = b
      x = 18.75f
      y = 15f
      recoil = 2f
      shake = 3f
      shootY = 8f
      reload = 25f
      rotate = true
      shootCone = 5f
      rotateSpeed = 3f
      rotationLimit = 25f
      layerOffset = -0.001f
      shootStatus = IStatus.鼓舞
      shootStatusDuration = 120f
      shoot = ShootPattern().apply {
        shots = 3
        shotDelay = 4f
      }
      shootSound = Sounds.shootCollaris

    })
  }
}