package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class DoubleAccessorStatic<U>(private val field: Field) {
  operator fun getValue(instance: U, property: KProperty<*>): Double = field.getDouble(null)
  operator fun setValue(instance: U, property: KProperty<*>, value: Double) = field.setDouble(instance, value)
}