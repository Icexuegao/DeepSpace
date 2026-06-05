package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class IntAccessorStatic<U>(private val field: Field) {
  operator fun getValue(instance: U, property: KProperty<*>): Int = field.getInt(null)
  operator fun setValue(instance: U, property: KProperty<*>, value: Int) = field.setInt(null, value)
}