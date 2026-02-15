package ice.content.unit

import arc.graphics.Color
import arc.math.geom.Rect
import ice.content.IStatus
import ice.entities.bullet.base.BulletType
import ice.library.IFiles.appendModName
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.ShieldArcAbility
import mindustry.gen.Sounds
import mindustry.world.meta.BlockFlag

class HeavyPress : IceUnitType("heavyPress") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "重压", "装甲厚重的慢速单位,结构简单,生产工艺成熟\n依靠坚固的装甲承受打击并以履带持续碾压建筑造成伤害")
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
    setWeapon {
      x = 0f
      shake = 0f
      reload = 30f
      mirror = false
      display = false
      useAmmo = false
      shootCone = 360f
      shootSound = Sounds.none
      shootStatus = IStatus.突袭
      shootStatusDuration = 60f
      bullet = BulletType().apply {
        damage = 0f
        lifetime = 30f
        speed = 8f
        collidesAir = false
        instantDisappear = true
        shootEffect = Fx.none
        smokeEffect = Fx.none
        despawnEffect = Fx.none
        hitEffect = Fx.none
      }
    }
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
    })
  }
}