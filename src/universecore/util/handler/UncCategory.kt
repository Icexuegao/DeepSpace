package universecore.util.handler

import arc.input.KeyBind
import mindustry.type.Category
import universe.util.reflect.accessEnum0
import universe.util.reflect.accessField

@Suppress("EnumValuesSoftDeprecate")
internal class UncCategory(val cat: Category, val bind: KeyBind?, val icon: String, var ordinal: Int = cat.ordinal) {
  companion object {
    val categoryHandler = Category::class.accessEnum0()
    var all: Array<Category> by Category::class.accessField("all")
  }

  constructor(name: String, ordinal: Int, bind: KeyBind?, icon: String) :this(categoryHandler.newEnumInstance(name, ordinal), bind, icon) {
    all = Category.values()
  }
}