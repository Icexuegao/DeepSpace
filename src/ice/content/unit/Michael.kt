package ice.content.unit

import arc.graphics.g2d.Draw
import ice.entities.bullet.RandomDamageBulletType
import ice.graphics.IceColor
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.entities.part.DrawPart
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.MultiPacker
import mindustry.graphics.MultiPacker.PageType

class Michael :IceUnitType("unit_michael") {
  init {
    localization {
      zh_CN {
        this.localizedName = "米迦勒"
      }
    }
    armor = 1f
    speed = 3.5f
    flying = true
    health = 250f
    hitSize = 13f
    mineTier = 2
    mineSpeed = 4f
    engineSize = 2.5f
    rotateSpeed = 7f
    itemCapacity = 30
    engineOffset = 8f
    circleTarget = true
    engineColor = IceColor.b4
    itemCapacity = 30
    buildSpeed = 2f
    buildRange = 8 * 40f
    drag = 0.05f
    accel = 0.11f
    outlines = false
    engineLayer= Layer.flyingUnitLow-1f
    setWeapon("weapon") {
      x = 5.875f
      y = 1.625f
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
        despawnEffect = IceEffects.基础子弹击中特效()
        hitEffect = despawnEffect
        homingPower = 0.05f
        homingRange = 50f
        shootEffect = IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b5)
      }
    }

  }

  override fun drawOutline(unit: Unit) {
    Draw.z(Draw.z() - 1f)
    Draw.color(outlineColor)
    Draw.rect("$name-outline", unit.x, unit.y, unit.rotation - 90)
  }



  override fun createIcons(packer: MultiPacker) {
    super.createIcons(packer)
    for(weapon in weapons) {
      if (!weapon.name.isEmpty() && (minfo.mod == null || weapon.name.startsWith(minfo.mod.name)) && (weapon.top || !packer.isOutlined(
          weapon.name
        ) || weapon.parts.contains { p: DrawPart? -> p!!.under })
      ) {
        makeOutline(
          PageType.main,
          packer,
          weapon.region,
          !weapon.top || weapon.parts.contains { p: DrawPart? -> p!!.under },
          outlineColor,
          outlineRadius
        )
      }
    }
  }
}