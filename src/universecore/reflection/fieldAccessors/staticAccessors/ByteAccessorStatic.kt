package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class ByteAccessorStatic<U>(private val field: Field) {
  operator fun getValue(instance: U, property: KProperty<*>): Byte = field.getByte(null)
  operator fun setValue(instance: U, property: KProperty<*>, value: Byte) = field.setByte(null, value)
}