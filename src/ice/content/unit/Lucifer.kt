package ice.content.unit

import arc.graphics.g2d.Draw
import ice.entities.bullet.RandomDamageBulletType
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.entities.part.DrawPart
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType

class Lucifer : IceUnitType("lucifer") {
  init {
    armor = 1f
    accel = 0.2f
    health = 200f
    speed = 3.5f
    flying = true
    hitSize = 16f
    drag = 0.05f
    accel = 0.11f
    accel = 0.4f
    mineTier = 3
    mineSpeed = 5f
    engineSize = 3f
    rotateSpeed = 7f
    itemCapacity = 40
    engineOffset = 8f
    circleTarget = true
    engineColor = IceColor.b4
    itemCapacity = 30
    buildSpeed = 0.75f
    buildRange = 8 * 40f
    outlines = false
    setWeapon("weapon1") {
      x = 5f
      y = 4f
      layerOffset = -0.2f
      reload = 15f
      shootSound = Sounds.shootAlpha
      bullet = RandomDamageBulletType(14, 20, 4f).apply {
        width = 4f
        height = 12f
        backColor = IceColor.b4
        frontColor = IceColor.b4
        lightColor = IceColor.b4
        trailColor = IceColor.b4
        trailWidth = 1.2f
        trailLength = 4
        shootY += 1
        lifetime = 60f
        despawnEffect = IceEffects.基础子弹击中特效
        hitEffect = despawnEffect
        homingPower = 0.05f
        homingRange = 50f
        shootEffect = IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b5)
      }
    }
    setWeapon("weapon2") {
      x = 7.5f
      y = 0f
      layerOffset = -0.2f
      reload = 30f
      shootSound = Sounds.shootAlpha
      bullet = RandomDamageBulletType(1, 30, 6f).apply {
        width = 4f
        height = 12f
        backColor = IceColor.b4
        frontColor = IceColor.b4
        lightColor = IceColor.b4
        shootY += 1
        lifetime = 60f
        homingPower = 0.05f
        homingRange = 50f
        despawnEffect = IceEffects.基础子弹击中特效
        hitEffect = despawnEffect
        shootEffect = IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b5)
      }
    }
    bundle {
      desc(zh_CN, "路西法")
    }
  }

  override fun drawOutline(unit: Unit) {
    Draw.z(Draw.z() - 1f)
    Draw.color(outlineColor)
    Draw.rect("$name-outline", unit.x, unit.y, unit.rotation - 90)
  }

  override fun createIcons(packer: MultiPacker) {
    super.createIcons(packer)
    for (weapon in weapons) {
      if (!weapon.name.isEmpty() && (minfo.mod == null || weapon.name.startsWith(minfo.mod.name)) && (weapon.top || !packer.isOutlined(weapon.name) || weapon.parts.contains { p: DrawPart? -> p!!.under })) {
        makeOutline(PageType.main, packer, weapon.region, !weapon.top || weapon.parts.contains { p: DrawPart? -> p!!.under }, outlineColor, outlineRadius)
      }
    }
  }
}