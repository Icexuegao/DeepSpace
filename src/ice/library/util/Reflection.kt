@file:Suppress("UNCHECKED_CAST", "HasPlatformType")

package ice.library.util

/**Kotlin reflection DSL utilities provide a range of convenient and type-safe reflection tools.
 *
 * You can use those reflection wrappers in the same way as origin field access and method calls, just like:
 *
 * ```kotlin
 * //member field
 * var TargetType.field: FieldType by accessField("fieldName")
 * var TargetType.numberRef by accessInt("number")
 * //static field
 * var staticField: FieldType by TargetClass::class.accessField("staticFieldName")
 *
 * //member method
 * val methodRef: TargetType.(ArgType) -> ReturnType = accessMethod1("methodName")
 * //static method
 * val staticRef: (ArgType1, ArgType2) -> ReturnType = TargetClass::class.accessMethod2("methodName")
 *
 * val constructorRef: (ArgType) -> TargetType = accessConstructor()
 * ```
 *
 * then you can use dot expression to access it in valid scope, just like the origin field and method:
 *
 * ```kotlin
 * val newInstance = constructorRef(arg)
 * instance.field = "fieldValue"
 * instance.numberRef = 123
 * staticField = "fieldValue"
 * val result = instance.methodRef(arg)
 * val resultStatic = staticRef(arg1, arg2)
 * ```
 *
 * @version 1.0
 * @author EBwilson*/
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


inline fun <reified T> T.jtype() = T::class.java
val KClass<*>.j: Class<*> get() = java
private fun checkFinal(field: Field) {
    if (Modifier.isFinal(field.modifiers)) throw IllegalArgumentException("Not support modify final field")
}

// Field accessors
class FieldAccessor<O, T>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): T = field.get(instance) as T
    operator fun setValue(instance: O, property: KProperty<*>, value: T) {
        checkFinal(field)
        field.set(instance, value)
    }
}

class ByteAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Byte = field.getByte(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Byte) {
        checkFinal(field)
        field.setByte(instance, value)
    }
}

class ShortAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Short = field.getShort(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Short) {
        checkFinal(field)
        field.setShort(instance, value)
    }
}

class IntAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Int = field.getInt(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Int) {
        checkFinal(field)
        field.setInt(instance, value)
    }
}

class LongAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Long = field.getLong(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Long) {
        checkFinal(field)
        field.setLong(instance, value)
    }
}

class FloatAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Float = field.getFloat(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Float) {
        checkFinal(field)
        field.setFloat(instance, value)
    }
}

class DoubleAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Double = field.getDouble(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Double) {
        checkFinal(field)
        field.setDouble(instance, value)
    }
}

class BooleanAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Boolean = field.getBoolean(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Boolean) {
        checkFinal(field)
        field.setBoolean(instance, value)
    }
}

interface StaticAccess<U, T> {
    operator fun getValue(instance: U, property: KProperty<*>): T
    operator fun setValue(instance: U, property: KProperty<*>, value: T)
}

// static
class FieldAccessorStatic<U, T>(private val field: Field) : StaticAccess<U, T> {
    override operator fun getValue(instance: U, property: KProperty<*>): T = field.get(null) as T
    override operator fun setValue(instance: U, property: KProperty<*>, value: T) = field.set(null, value)
}

class ByteAccessorStatic<U>(private val field: Field) {
    operator fun getValue(instance: U, property: KProperty<*>): Byte = field.getByte(null)
    operator fun setValue(instance: U, property: KProperty<*>, value: Byte) = field.setByte(null, value)
}

class ShortAccessorStatic<U>(private val field: Field) {
    operator fun getValue(instance: U, property: KProperty<*>): Short = field.getShort(null)
    operator fun setValue(instance: U, property: KProperty<*>, value: Short) = field.setShort(null, value)
}

class IntAccessorStatic<U>(private val field: Field) {
    operator fun getValue(instance: U, property: KProperty<*>): Int = field.getInt(null)
    operator fun setValue(instance: U, property: KProperty<*>, value: Int) = field.setInt(null, value)
}

class LongAccessorStatic<U>(private val field: Field) {
    operator fun getValue(instance: U, property: KProperty<*>): Long = field.getLong(null)
    operator fun setValue(instance: U, property: KProperty<*>, value: Long) = field.setLong(null, value)
}

class FloatAccessorStatic<U>(private val field: Field) {
    operator fun getValue(instance: U, property: KProperty<*>): Float = field.getFloat(null)
    operator fun setValue(instance: U, property: KProperty<*>, value: Float) = field.setFloat(instance, value)
}

class DoubleAccessorStatic<U>(private val field: Field) {
    operator fun getValue(instance: U, property: KProperty<*>): Double = field.getDouble(null)
    operator fun setValue(instance: U, property: KProperty<*>, value: Double) = field.setDouble(instance, value)
}

class BooleanAccessorStatic<U>(private val field: Field) {
    operator fun getValue(instance: U, property: KProperty<*>): Boolean = field.getBoolean(instance)
    operator fun setValue(instance: U, property: KProperty<*>, value: Boolean) = field.setBoolean(instance, value)
}

inline fun <reified O : Any, reified T> accessField(name: String) =
    FieldAccessor<O, T>(O::class.java.getDeclaredField(name).also {
        if (!it.type.isAssignableFrom(T::class.java)) throw IllegalArgumentException(
            "field $it type is not instance of ${T::class.java}")
        it.isAccessible = true
    })

inline fun <reified O : Any> accessByte(name: String) = ByteAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Byte::class.java) throw IllegalArgumentException("field $it type is not byte")
    it.isAccessible = true
})

inline fun <reified O : Any> accessShort(name: String) = ShortAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Short::class.java) throw IllegalArgumentException("field $it type is not short")
    it.isAccessible = true
})

inline fun <reified O : Any> accessInt(name: String) = IntAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Int::class.java) throw IllegalArgumentException("field $it type is not int")
    it.isAccessible = true
})

inline fun <reified O : Any> accessLong(name: String) = LongAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Long::class.java) throw IllegalArgumentException("field $it type is not long")
    it.isAccessible = true
})

inline fun <reified O : Any> accessFloat(name: String) = FloatAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Float::class.java) throw IllegalArgumentException("field $it type is not float")
    it.isAccessible = true
})

inline fun <reified O : Any> accessDouble(name: String) = DoubleAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Double::class.java) throw IllegalArgumentException("field $it type is not double")
    it.isAccessible = true
})

inline fun <reified O : Any> accessBoolean(name: String) =
    BooleanAccessor<O>(O::class.java.getDeclaredField(name).also {
        if (it.type != Boolean::class.java) throw IllegalArgumentException("field $it type is not boolean")
        it.isAccessible = true
    })

// static
private fun checkPrimitiveFinal(field: Field) {
    if (Modifier.isFinal(field.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
}

inline fun <U, reified T> KClass<*>.accessField(name: String) =
    FieldAccessorStatic<U, T>(this.java.getDeclaredField(name).also {
        if (!it.type.isAssignableFrom(T::class.java)) throw IllegalArgumentException(
            "field $it type is not instance of ${T::class.java}")
      //  if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
     //       "静态 final 字段始终是不可变的，您不应该反映这些字段")
        it.isAccessible = true
    })

fun <U> KClass<*>.accessByte(name: String) = ByteAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Byte::class.java) throw IllegalArgumentException("field $it type is not byte")
    checkPrimitiveFinal(it)
    it.isAccessible = true
})

fun <U> KClass<*>.accessShort(name: String) = ShortAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Short::class.java) throw IllegalArgumentException("field $it type is not short")
    checkPrimitiveFinal(it)
    it.isAccessible = true
})

fun <U> KClass<*>.accessInt(name: String) = IntAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Int::class.java) throw IllegalArgumentException("field $it type is not int")
    checkPrimitiveFinal(it)
    it.isAccessible = true
})

fun <U> KClass<*>.accessLong(name: String) = LongAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Long::class.java) throw IllegalArgumentException("field $it type is not long")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    it.isAccessible = true
})

fun <U> KClass<*>.accessFloat(name: String) = FloatAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Float::class.java) throw IllegalArgumentException("field $it type is not float")
    checkPrimitiveFinal(it)
    it.isAccessible = true
})

fun <U> KClass<*>.accessDouble(name: String) = DoubleAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Double::class.java) throw IllegalArgumentException("field $it type is not double")
    checkPrimitiveFinal(it)
    it.isAccessible = true
})

fun <U> KClass<*>.accessBoolean(name: String) = BooleanAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Boolean::class.java) throw IllegalArgumentException("field $it type is not boolean")
    checkPrimitiveFinal(it)
    it.isAccessible = true
})

fun checkReturnType(met: Method, retType: Class<*>) {
    if (!(retType.isAssignableFrom(
            met.returnType) || (met.returnType == Void.TYPE && retType == Unit::class.java)) || (met.returnType == Unit::class.java && retType == Void.TYPE)
    ) throw IllegalArgumentException("method returned type ${met.returnType} is not instance of $retType")
}
