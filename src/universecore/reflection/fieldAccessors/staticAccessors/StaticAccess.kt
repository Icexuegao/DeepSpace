package universecore.reflection.fieldAccessors.staticAccessors

import kotlin.reflect.KProperty

interface StaticAccess<U, T> {
  operator fun getValue(instance: U, property: KProperty<*>): T
  operator fun setValue(instance: U, property: KProperty<*>, value: T)
}