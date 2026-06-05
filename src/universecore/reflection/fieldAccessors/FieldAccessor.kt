package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class FieldAccessor<O, T>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): T = field.get(instance) as T
  operator fun setValue(instance: O, property: KProperty<*>, value: T) {
    checkFinal(field)
    field.set(instance, value)
  }
}