package ice.content.block.turret

import arc.Core
import arc.graphics.Color
import arc.util.Strings
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.graphiteCloud
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.world.blocks.turrets.SglTurret

class Curtain : SglTurret("curtain") {
  init {
    bundle {
      desc(zh_CN, "遮幕", "发射石墨炸弹,会制造一篇石墨云,一种朴素但有效的对空防御武器")
    }
    requirements(
      Category.turret, ItemStack.with(
        Items.titanium, 20, Items.graphite, 20, Items.lead, 12
      )
    )
    itemCapacity = 20
    range = 144f
    targetGround = false

    newAmmo(object : BasicBulletType(30f, 1.6f, "missile") {
      init {
        frontColor = Items.graphite.color.cpy().lerp(Color.white, 0.7f)
        backColor = Items.graphite.color
        width = 7f
        height = 12f
        lifetime = 90f
        ammoMultiplier = 1f
        hitShake = 0.35f
        scaleLife = true
        splashDamageRadius = 32f
        splashDamage = 12f
        collidesGround = false
        collidesTiles = false
        hitEffect = Fx.explosion
        trailEffect = Fx.smoke
        trailChance = 0.12f
        trailColor = Items.graphite.color

        hitSound = Sounds.explosion

        fragOnHit = true
        fragBullets = 1
        fragVelocityMin = 0f
        fragVelocityMax = 0f
        fragBullet = graphiteCloud(360f, 36f, true, ground = false, empDamage = 0.2f)
      }
    }, true) { bt, ammo: mindustry.entities.bullet.BulletType? ->
      bt!!.add(Core.bundle.format("bullet.damage", ammo!!.damage))
      bt.row()
      bt.add(Core.bundle.format("bullet.splashdamage", ammo.splashDamage.toInt(), Strings.fixed(ammo.splashDamageRadius / Vars.tilesize, 1)))
      bt.row()
      bt.add(Core.bundle.get("infos.curtainAmmo"))
      bt.row()
      bt.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]2[lightgray] " + Core.bundle.get("unit.seconds"))
    }
    consume!!.item(Items.graphite, 1)
    consume!!.time(90f)
  }
}