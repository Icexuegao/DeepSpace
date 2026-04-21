package ice.content.block.effect


import ice.world.content.blocks.effect.OrientationProjector

class 定向超速器:OrientationProjector("orientationProjector"){
  init{
    localization {
      zh_CN {
        this.localizedName = "定向超速器"
        description = "提升范围内选定链接的建筑的工作效率,链接数量有限"
      }
    }
    size = 2
    buildSize = 5
    range = 8 * 20f

  }
}