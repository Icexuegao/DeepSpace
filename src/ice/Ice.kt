package ice

import arc.Events
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.util.Tmp
import ice.audio.SoundControl
import ice.content.*
import ice.content.block.IBlocks
import ice.core.SaveIO
import ice.core.SettingValue
import ice.entities.ArcFieldBulletType
import ice.entities.bullet.base.IceBullet
import ice.game.IceTeam
import ice.library.EventType
import ice.library.IFiles
import ice.library.Schematics
import ice.library.struct.log
import ice.ui.MenusDialog
import ice.ui.UI
import ice.ui.bundle.BaseBundle
import ice.ui.bundle.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.Noise2dBlock
import ice.world.meta.IAttribute
import mindustry.Vars
import mindustry.content.Fx
import mindustry.ctype.UnlockableContent
import mindustry.entities.part.RegionPart
import mindustry.gen.Sounds
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.mod.Mod
import mindustry.type.Category
import mindustry.world.meta.BuildVisibility
import singularity.Recipes
import singularity.Singularity
import singularity.type.SglCategory
import singularity.type.SglContentType
import singularity.world.blocks.turrets.ContinuousTurret
import singularity.world.draw.DrawSglTurret
import tmi.RecipeEntryPoint
import universe.util.reflect.Enums.accessEnum0
import universecore.UncCore

@RecipeEntryPoint(Recipes::class)
open class Ice :Mod() {

  companion object {
    val singularity = Singularity()
  }

  enum class A {
    d, f, w
  }

  init {
    val ad = A::class.accessEnum0()
    ad.newEnumInstance("xaw", 1)
    A.entries.toTypedArray().forEach {
      log { it.name + "  " + it.ordinal }
    }
  }

  init {
    DeepSpace.globals.load()
    // researches.init();
    Events.on(mindustry.game.EventType.MusicRegisterEvent::class.java) {
      SoundControl.init()
    }
    IFiles.setup()
    IAttribute.setup()
    SglCategory.setup()
    UncCore.setup()
    SettingValue.setup()
    IceBullet.setup()
    EventType.setup()
    IceTeam.setup()
    IVars.setup()
  }

  override fun init() {
    UncCore.init()
    singularity.init()
    //  SglTechTreeDialog().show()
    UI.init()
    Schematics.init()
    MenusDialog.init()
    SaveIO.init()


    Vars.content.each {
      if (it.minfo.mod == DeepSpace.mod && it is UnlockableContent) it.unlock()
    }

  }

  override fun loadContent() {
    val turret = object :ContinuousTurret("mendTower") {
      init {
        bundle {
          desc(zh_CN, "洛华", "使用方菱折射投射出扇形修复光束覆盖建筑进行修复")
        }

        buildType = Prov {
          object :ContinuousTurretBuild() {
            val mat = CubeCalculator()
            val mat2 = CubeCalculator()


            override fun draw() {
              super.draw()
              // 绘制每条边

              Draw.z(Layer.effect)
              Lines.stroke(0.5f, Pal.heal)
              mat.size = 2f * warmup()
              mat.edges.forEach { (startIdx, endIdx) ->
                val start = mat.projectedPoints[startIdx]
                val end = mat.projectedPoints[endIdx]
                Tmp.v1.set(shootX, shootY).rotate(rotationu - 90f)
                val ox = Tmp.v1.x
                val oy = Tmp.v1.y
                Lines.line(start.x + x + ox, start.y + y + oy, end.x + x + ox, end.y + y + oy)
              }

              mat2.size = 0.8f * warmup()
              mat2.edges.forEach { (startIdx, endIdx) ->
                val start = mat2.projectedPoints[startIdx]
                val end = mat2.projectedPoints[endIdx]
                Tmp.v2.set(shootX, shootY).rotate(rotationu - 90f)
                val ox = Tmp.v2.x
                val oy = Tmp.v2.y
                Lines.line(start.x + x + ox, start.y + y + oy, end.x + x + ox, end.y + y + oy)
              }

              Draw.reset()

            }

            override fun updateTile() {
              super.updateTile()
              mat.update(0.005f)
              mat2.update(-0.003f)
            }
          }
        }
      }
    }
    turret.apply {
      requirements(Category.effect, IItems.单晶硅, 30, IItems.绿藻块, 10, IItems.石英玻璃, 40, IItems.高碳钢, 30, IItems.金锭, 20)
      size = 3
      shootSound = Sounds.none
      shootY = 8.8f
      targetAir=false
      targetGround = true
      buildingFilter={b,build ->
        b.team==build.team
      }
      targetHealing = true
      rotateSpeed = 8f
      range = 20f * 8f
      newAmmo( object :ArcFieldBulletType() {}.apply {
        shootEffect=Fx.none
        smokeEffect= Fx.none
        damage = 0.1f
        healAmount = 10f / 60f
        hitColor = Pal.heal
        hitEffect = Fx.none
        collidesGround = true
        collidesTeam = true
      })
      consume!!.liquid(ILiquids.氯气, 0.5f)

      val parts = (drawers as DrawSglTurret).parts
      parts.add(RegionPart("-mid"))
      parts.add(RegionPart("-blade-l").apply {
        x = -6.375f
        y = 2.75f
        moveX = -2f
        moveY = -1.25f
        moveRot = 10f
      }, RegionPart("-blade-r").apply {
        x = 6.375f
        y = 2.75f
        moveX = 2f
        moveY = -1.25f
        moveRot = -10f
      })

    }
    //载入所有新内容类型
    SglContentType.load()

    Noise2dBlock("noise2d").apply {
      requirements(SglCategory.matrix, IItems.钴锭, 10)
    }
    IItems.load()
    AtomSchematics.load()
    ILiquids.load()
    IStatus.load()
    IUnitTypes.load()
    IBlocks.load()
    IWeathers.load()
    IPlanets.load()

    singularity.loadContent()
    BaseBundle.load()



    Vars.content.blocks().forEach {
      if (!it.hasBuilding() && it.category == Category.distribution) {
        it.category = SglCategory.environment
        it.buildVisibility = BuildVisibility.sandboxOnly
      }
    }
  }
}
