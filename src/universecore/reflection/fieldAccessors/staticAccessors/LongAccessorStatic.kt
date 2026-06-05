package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class LongAccessorStatic<U>(private val field: Field) {
  operator fun getValue(instance: U, property: KProperty<*>): Long = field.getLong(null)
  operator fun setValue(instance: U, property: KProperty<*>, value: Long) = field.setLong(null, value)
}