package ice.content.block.power

import ice.content.IItems
import ice.content.ILiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.power.NuclearReactor
import universecore.ui.bundle.localization

class 热核裂变反应堆:NuclearReactor("heatNuclearReactor"){
  init {
    localization {
      zh_CN {
        localizedName = "热核裂变反应堆"
        description = "利用钍燃料进行核裂变反应产生大量电力,需要大量急冻液冷却以防止过热,否则将发生剧烈爆炸"
      }
    }
    fuelItem = IItems.钍锭
    health = 1200
    size = 3
    armor = 4f
    coolantPower = 1f
    heating = 0.00166f
    itemCapacity = 20
    liquidCapacity = 50f
    itemDuration = 600f
    powerProduction = 42f
    explosionRadius = 21
    explosionDamage = 7200
    consumeItems(IItems.钍锭, 1)
    consumeLiquids(ILiquids.急冻液, 0.036f)

    requirements(Category.power, IItems.导能回路, 50, IItems.铬锭, 380, IItems.铱板, 325, IItems.石英玻璃, 75, IItems.铅锭, 300)
    ambientSound = Sounds.loopHum
    ambientSoundVolume = 0.2f
  }
}