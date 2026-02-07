package ice.content.block.turret

import arc.Core
import arc.graphics.Color
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.StatusEffects
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.LightLaserBulletType
import singularity.world.blocks.turrets.ProjectileTurret
import singularity.world.draw.DrawSglTurret
import singularity.world.meta.SglStat

class Roentgen : ProjectileTurret("roentgen") {
  init {
    bundle {
      desc(zh_CN, "伦琴", "发射极具穿透力的高能激光束,杀伤力极强")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 120, IItems.气凝胶, 100, IItems.铝, 60, IItems.FEX水晶, 40, Items.silicon, 75, Items.surgeAlloy, 45
      )
    )
    size = 4
    range = 240f
    shootY = 12f
    cooldownTime = 60f

    moveWhileCharging = false
    shoot.firstShotDelay = 40f
    shootSound = Sounds.shootLaser

    newAmmo(object : LightLaserBulletType() {
      init {
        length = 240f
        damage = 225f
        empDamage = 180f
        lightColor = Pal.reactorPurple
        chargeEffect = MultiEffect(SglFx.colorLaserChargeBegin, SglFx.colorLaserCharge, Fx.lightningCharge)
        status = StatusEffects.electrified
        statusDuration = 12f
        hitColor = Pal.reactorPurple
        shootEffect = MultiEffect(
          SglFx.crossLightMini, Fx.circleColorSpark
        )

        colors = arrayOf<Color?>(
          Pal.reactorPurple.cpy().mul(1f, 1f, 1f, 0.4f), Pal.reactorPurple, Color.white
        )

        generator.maxSpread = 6f
      }
    })
    consume!!.time(30f)
    consume!!.power(12.4f)

    newAmmoCoating(Core.bundle.get("coating.crystal_fex"), SglDrawConst.fexCrystal, { b: mindustry.entities.bullet.BulletType? ->
      val res = b!!.copy() as LightLaserBulletType
      res.damage *= 1.25f
      res.colors = arrayOf<Color?>(
        SglDrawConst.fexCrystal.cpy().mul(1f, 1f, 1f, 0.4f), SglDrawConst.fexCrystal, Color.white
      )
      res.lightColor = SglDrawConst.fexCrystal
      res.empDamage *= 0.8f
      res.status = IStatus.结晶化
      res.statusDuration = 15f
      res
    }, { t ->
      t!!.add(SglStat.exDamageMultiplier.localized() + 125 + "%")
      t.row()
      t.add(Core.bundle.get("bullet.empDamageMulti") + 80 + "%")
      t.row()
      t.add(IStatus.结晶化.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"))
    })
    consume!!.time(60f)
    consume!!.liquid(ILiquids.FEX流体, 0.1f)

    newCoolant(1.5f, 20f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.1f)

    draw = object : DrawSglTurret() {}
  }
}