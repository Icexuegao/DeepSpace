package ice.content.block.turret

import arc.Core
import arc.graphics.Color
import arc.util.Strings
import ice.content.IItems
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.graphiteCloud
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Items
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.meta.StatUnit
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.turrets.EmpArtilleryBulletType
import singularity.world.blocks.turrets.SglTurret

class Mist: SglTurret("mist") {
  init{
    bundle {
      desc(zh_CN, "迷雾", "一门重型对地复合石墨大炮,发射4颗填充了松散石墨的炮弹,爆炸后会产生一片会带有电磁脉冲的石墨云")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 100, IItems.气凝胶, 120, Items.titanium, 100, Items.graphite, 80, Items.lead, 85
      )
    )
    size = 3

    itemCapacity = 36
    range = 300f
    minRange = 40f
    inaccuracy = 8f
    targetAir = false
    velocityRnd = 0.2f
    shake = 2.5f
    recoil = 6f
    recoilTime = 120f
    cooldownTime = 120f

    shootY = 0f
    shootEffect = MultiEffect(
      SglFx.crossLightMini, object : WaveEffect() {
        init {
          colorFrom = SglDrawConst.frost
          colorTo = Color.lightGray
          lifetime = 12f
          sizeTo = 40f
          strokeFrom = 6f
          strokeTo = 0.3f
        }
      })

    scaledHealth = 180f

    shootSound = Sounds.shootPulsar

    shoot.shots = 4
    newAmmo(object : EmpArtilleryBulletType(3f, 20f) {
      init {
        empDamage = 40f
        empRange = 20f

        knockback = 1f
        lifetime = 80f
        height = 12f
        width = height
        collidesTiles = false
        splashDamageRadius = 20f
        splashDamage = 35f

        damage = 0f

        frontColor = Items.graphite.color.cpy().lerp(Color.white, 0.7f)
        backColor = Items.graphite.color

        fragOnHit = true
        fragBullets = 1
        fragVelocityMin = 0f
        fragVelocityMax = 0f
        fragBullet = graphiteCloud(360f, 40f, air = true, ground = true, empDamage = 0.35f)
      }
    }) { t, b: mindustry.entities.bullet.BulletType? ->
      t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
      t.row()
      t.table { table ->
        table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.35f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
        table.row()
        table.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]6[lightgray] " + Core.bundle.get("unit.seconds"))
      }.padLeft(15f)
    }
    consume!!.item(Items.graphite, 6)
    consume!!.time(120f)
  }
}