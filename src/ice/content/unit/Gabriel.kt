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

class Gabriel : IceUnitType("gabriel") {
  init {
    armor = 1f
    speed = 3.5f
    flying = true
    health = 150f
    hitSize = 13f
    mineTier = 2
    mineSpeed = 4f
    engineSize = 2.5f
    rotateSpeed = 7f
    itemCapacity = 30
    engineOffset = 6f
    circleTarget = true
    engineColor = IceColor.b4
    itemCapacity = 30
    buildSpeed = 0.75f
    buildRange = 8 * 40f
    drag = 0.05f
    accel = 0.11f
    outlines = false
    setWeapon("weapon") {
      y += 0.8f
      x = 4.3f
      reload = 10f
      layerOffset = -0.2f
      rotateSpeed = 9f
      shootSound = Sounds.shootAlpha
      bullet = RandomDamageBulletType(8, 14, 3f).apply {
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
    bundle {
      desc(zh_CN, "加百列")
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