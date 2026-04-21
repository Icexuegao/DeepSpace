package ice.content.block.effect


import ice.world.content.blocks.effect.ResBox

class 遗弃资源箱:ResBox("resBox"){
  init{
    localization {
      zh_CN {
        this.localizedName = "遗弃资源箱"
        description = "被废弃的存储箱,内部可能会存在少量资源"
      }
    }
    squareSprite = false
  }
}