package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class BooleanAccessor<O>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): Boolean = field.getBoolean(instance)
  operator fun setValue(instance: O, property: KProperty<*>, value: Boolean) {
    checkFinal(field)
    field.setBoolean(instance, value)
  }
}
