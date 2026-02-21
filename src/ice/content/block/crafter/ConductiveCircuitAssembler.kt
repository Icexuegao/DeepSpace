package ice.content.block.crafter

import arc.graphics.Color
import arc.math.Interp
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import singularity.world.blocks.product.NormalCrafter

class ConductiveCircuitAssembler : NormalCrafter("conductiveCircuitAssembler") {
  init {
    bundle {
      desc(
        zh_CN, "导能回路装配器", "持续开启相位时间场,减缓局部时间以同时进行多种精密零件的制作", "[#9B929D]为什么总有人管她叫灵魂熔炉[]"
      )
    }
    size = 5
    armor = 4f
    itemCapacity = 60
    canOverdrive = false
    updateEffect = Fx.mineBig
    craftEffect = WaveEffect().apply {
      lifetime = 180f
      sizeTo = 40f
      strokeFrom = 5f
      interp = Interp.circleOut
      colorFrom = Color.valueOf("B7B9C2")
      colorTo = Color.valueOf("B7B9C280")
    }
    draw = DrawMulti(DrawDefault(), DrawFlame().apply {
      flameColor = Color.valueOf("B7B9C2")
      lightRadius = 60f
      lightAlpha = 0.6f
      lightSinScl = 9.424778f
      lightSinMag = 6.2831855f
      flameRadius = 5f
      flameRadiusIn = 2f
      flameRadiusScl = 9.424778f
      flameRadiusMag = 3f
      flameRadiusInMag = 1.5f
    })
    ambientSound = Sounds.explosion
    ambientSoundVolume = 0.08f
    requirements(Category.crafting, IItems.铱板, 140, IItems.单晶硅, 50, IItems.铪锭, 30, IItems.铬锭, 100)

    newConsume().apply {
      time(120f)
      items(IItems.单晶硅, 9, IItems.铪锭, 3)
      power(15.25f)
    }
    newProduce().apply {
      items(IItems.导能回路, 6)
    }
  }
}