包ice.content.unit.mech

进口弧.图形.混合
进口弧光.图形.颜色
进口arc.math.Interp
进口ice.content.IItems
进口ice.content.IStatus
进口ice.ities.bullet.激光子弹类型
进口ice.ities.bullet.base.BasicBulletType
进口冰.实体.效果.多效果
进口ice.ui.bundle.bundle
进口ice.ui.bundle.desc
进口ice.world.content.unit.IceUnitType
进口冰。世界。内容。单元。能力。ArmorPlateAbility
进口mindustry.content.Fx
进口心智产业。ities。能力。再生能力
进口心智产业.实体.效果.爆炸效果
进口mindustry.ities.effect.ParticleEffect
进口mindustry.ities.part.RegionPart
进口mindustry.gen.MechUnit
进口mindustry.gen.Sounds
进口mindustry.type.ammo.ItemAmmoType

班级死誓：IceUnitType("unit_deathOath"，MechUnit：：班级.Java){
  init {
捆绑{
DESC(zh_CN，"死誓", "重型地面突击单位.对远距离敌人发射穿透能量弹,对近距离敌人则切换为高热激光,会缓慢恢复生命值,开火时减少所受伤害")
    }
健康=29000f
铠装=15F
hitSize=30F
速度=0.36F
范围=388f
rotateSpeed=1.8F
riseSpeed=0.05F
canBoost=正确
低空=正确
boostMultiplier=2F
mechLandShake=4F
stepShake=0.75f
mechSideSway=0.6F
    mechFrontSway = 1.9f
    drownTimeMultiplier = 6F
    mechStepParticles = 正确
    ammoType = ItemAmmoType(IItems.钍锭)
    ammoCapacity = 240
    engineSize = 6F
    engineOffset = 15f

    engines.add(UnitEngine().apply {
      x = 15f
      y = -18F
      radius = 4F
      rotation = -90F
    }, UnitEngine().apply {
      x = -15f
      y = -18F
      radius = 4F
      rotation = -90F
    })

    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.5F
    }, RegenAbility().apply {
      percentAmount = 0.0125f
    })

    setWeapon("weapon1") {
      top = false
      x = 23f
      y = 1f
      recoil = 3f
      shake = 3f
      reload = 120f
      shootY = 24f
      shootCone = 5f
      alternate = false
      cooldownTime = 150f
      ejectEffect = Fx.casing3
      shootSound = Sounds.shootSpectre
      bullet = BasicBulletType(12f, 585f).apply {
        lifetime = 33f
        width = 16f
        height = 25f
        pierce = true
        pierceArmor = true
        absorbable = false
        reflectable = false
        status = IStatus.湍能
        statusDuration = 60f
        splashDamage = 195f
        splashDamageRadius = 80f
        lightColor = Color.valueOf("FF8663")
        frontColor = Color.white
        backColor = Color.valueOf("FF8663")
        hitColor = Color.valueOf("FF8663")
        trailChance = 1f
        trailInterval = 24f
        trailEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 25f
          baseLength = 9f
          length = 9f
          sizeFrom = 7f
          sizeTo = 0f
          cone = 360f
          interp = Interp.circleOut
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF8663")
        }
        shootEffect = Fx.shootSmokeTitan
        despawnSound = Sounds.explosionPlasmaSmall
        despawnEffect = Fx.bigShockwave
        hitEffect = MultiEffect(
          ExplosionEffect().apply {
            lifetime = 20f
            waveStroke = 2f
            waveColor = Color.valueOf("FF8663")
            sparkColor = Color.valueOf("FF8663")
            waveRad = 12f
            smokeSize = 0f
            smokeSizeBase = 0f
            sparks = 10
            sparkRad = 35f
            sparkLen = 4f
            sparkStroke = 1.5f
          }, ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 4f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 70f
            baseLength = 0f
            lifetime = 10f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.white
            cone = 60f
          }, Fx.hitSquaresColor
        )
      }
      parts.add(RegionPart().apply {
        suffix = "-glow"
        outline = false
        color = Color.valueOf("F03B0E")
        blending = Blending.additive
      })
    }

    setWeapon("weapon2") {
      x = 11.25f
      y = 1.25f
      shake = 3f
      recoil = 3f
      shootY = 7f
      reload = 60f
      rotate = true
      rotateSpeed = 2f
      rotationLimit = 45f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(235f).apply {
        length = 280f
        lifetime = 15f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          interp = Interp.fastSlow
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        colors = arrayOf(
          Color.valueOf("D75B6E"),
          Color.valueOf("E78F92"),
          Color.valueOf("FFF0F0")
        )
        status = IStatus.熔融
        statusDuration = 90f
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 10
          lifetime = 20f
          length = 75f
          cone = -360f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
      }
    }
  }
}
