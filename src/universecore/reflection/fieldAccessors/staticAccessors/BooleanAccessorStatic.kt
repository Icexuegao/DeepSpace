package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class BooleanAccessorStatic<U>(private val field: Field) {
  operator fun getValue(instance: U, property: KProperty<*>): Boolean = field.getBoolean(instance)
  operator fun setValue(instance: U, property: KProperty<*>, value: Boolean) = field.setBoolean(instance, value)
}