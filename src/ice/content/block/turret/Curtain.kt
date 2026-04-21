package ice.content.block.turret

import arc.Core
import arc.graphics.Color
import arc.util.Strings
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.graphiteCloud
import ice.entities.bullet.base.BasicBulletType

import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.gen.Sounds
import mindustry.type.Category
import singularity.world.blocks.turrets.SglTurret

class Curtain : SglTurret("curtain") {
  init {
    localization {
      zh_CN {
        this.localizedName = "遮幕"
        description = "发射石墨炸弹,会制造一篇石墨云,一种朴素但有效的对空防御武器"
      }
    }
    requirements(Category.turret, IItems.铬锭, 20, IItems.锌锭, 20, IItems.铅锭, 12)
    itemCapacity = 20
    range = 144f
    targetGround = false

    newAmmo(object : BasicBulletType(1.6f, 30f, "missile-large") {
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
    consume!!.item(IItems.生煤, 1)
    consume!!.time(90f)

    newAmmo(object : BasicBulletType(1.6f, 30f, "missile-large") {
      init {
        frontColor = Items.graphite.color.cpy().lerp(Color.white, 0.7f)
        backColor = Items.graphite.color
        width = 7f
        height = 12f
        lifetime = 100f
        ammoMultiplier = 0.8f
        hitShake = 0.35f
        scaleLife = true
        splashDamageRadius = 52f
        splashDamage = 16f
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
    consume!!.item(IItems.石墨烯, 1)
    consume!!.time(90f)
  }
}