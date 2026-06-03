package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class FloatAccessor<O>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): Float = field.getFloat(instance)
  operator fun setValue(instance: O, property: KProperty<*>, value: Float) {
    checkFinal(field)
    field.setFloat(instance, value)
  }
}