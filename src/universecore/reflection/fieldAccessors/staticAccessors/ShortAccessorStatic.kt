package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class ShortAccessorStatic<U>(private val field: Field) {
  operator fun getValue(instance: U, property: KProperty<*>): Short = field.getShort(null)
  operator fun setValue(instance: U, property: KProperty<*>, value: Short) = field.setShort(null, value)
}