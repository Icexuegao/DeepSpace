package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class IntAccessor<O>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): Int = field.getInt(instance)
  operator fun setValue(instance: O, property: KProperty<*>, value: Int) {
    checkFinal(field)
    field.setInt(instance, value)
  }
}