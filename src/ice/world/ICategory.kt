package ice.world

import ice.library.universecore.UncCore
import ice.library.world.Load
import mindustry.type.Category

object ICategory : Load {
    val 矩阵: Category = UncCore.categories.add("matrix", 4, "ice-matrix")
    val 中子能: Category = UncCore.categories.add("nuclear", 5, "ice-nuclear")
}