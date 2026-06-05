package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class FloatAccessorStatic<U>(private val field: Field) {
  operator fun getValue(instance: U, property: KProperty<*>): Float = field.getFloat(null)
  operator fun setValue(instance: U, property: KProperty<*>, value: Float) = field.setFloat(instance, value)
}