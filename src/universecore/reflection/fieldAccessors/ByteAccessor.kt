package universecore.reflection.fieldAccessors

import universecore.reflection.checkFinal
import java.lang.reflect.Field
import kotlin.reflect.KProperty

class ByteAccessor<O>(private val field: Field) {
  operator fun getValue(instance: O, property: KProperty<*>): Byte = field.getByte(instance)
  operator fun setValue(instance: O, property: KProperty<*>, value: Byte) {
    checkFinal(field)
    field.setByte(instance, value)
  }
}