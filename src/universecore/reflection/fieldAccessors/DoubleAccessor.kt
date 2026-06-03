package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class DoubleAccessor<O>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): Double = field.getDouble(instance)
  operator fun setValue(instance: O, property: KProperty<*>, value: Double) {
    checkFinal(field)
    field.setDouble(instance, value)
  }
}