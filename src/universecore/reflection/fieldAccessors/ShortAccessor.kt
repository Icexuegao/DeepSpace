package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class ShortAccessor<O>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): Short = field.getShort(instance)
  operator fun setValue(instance: O, property: KProperty<*>, value: Short) {
    checkFinal(field)
    field.setShort(instance, value)
  }
}