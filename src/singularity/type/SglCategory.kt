package singularity.type

import ice.DeepSpace.modName
import ice.library.world.Load
import mindustry.type.Category
import universecore.UncCore.categories

object SglCategory: Load{
  /** 各种生产/传输核能的设备;消耗核能的工厂应当保持在crafting中  */
  var nuclear: Category = categories.add("nuclear", "$modName-nuclear")
  /** 矩阵网络相关的主要方块  */
  var matrix: Category = categories.add("matrix", "$modName-matrix")
  /** 静态环境方块  */
  var environment: Category = categories.add("environment", "$modName-environment")
}