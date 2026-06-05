package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class LongAccessor<O>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): Long = field.getLong(instance)
  operator fun setValue(instance: O, property: KProperty<*>, value: Long) {
    checkFinal(field)
    field.setLong(instance, value)
  }
}