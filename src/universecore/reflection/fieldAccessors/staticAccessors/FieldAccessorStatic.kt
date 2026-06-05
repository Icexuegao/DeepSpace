package universecore.reflection.fieldAccessors.staticAccessors

import java.lang.reflect.Field
import kotlin.reflect.KProperty

class FieldAccessorStatic<U, T>(private val field: Field) :StaticAccess<U, T> {
  override operator fun getValue(instance: U, property: KProperty<*>): T = field.get(null) as T
  override operator fun setValue(instance: U, property: KProperty<*>, value: T) = field.set(null, value)
}