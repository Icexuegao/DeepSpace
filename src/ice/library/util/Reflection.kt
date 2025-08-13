@file:Suppress("UNCHECKED_CAST", "HasPlatformType")

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
 * @author EBwilson*/
package ice.library.util

import arc.util.OS
import sun.misc.Unsafe
import java.lang.reflect.*
import java.util.*
import java.util.Map.of
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface Accessor {
    fun makeAccessible(obj: AccessibleObject)
}

private val unsafe = {
    if (OS.isAndroid) {
        val f = Unsafe::class.java.getDeclaredField("theUnsafe")
        f.setAccessible(true)
        f.get(null) as Unsafe
    } else {
        val cstr = Unsafe::class.java.getDeclaredConstructor()
        cstr.setAccessible(true)
        cstr.newInstance()
    }
}()

private object DesktopAccessor : Accessor {
    private const val fieldFilterOffset: Long = 112L
    private val opensField: Field
    private val exportField: Field
    private val exportNative: Method?
    private val modifiers: Field

    init {
        ensureFieldOpen()
        val modelClass = Module::class.java
        opensField = modelClass.getDeclaredField("openPackages")
        exportField = modelClass.getDeclaredField("exportedPackages")

        makeModuleOpen(modelClass.module, "java.lang", DesktopAccessor::class.java.module)

        exportNative = modelClass.getDeclaredMethod("addExports0", modelClass, String::class.java, modelClass)
        exportNative.setAccessible(true)
        exportNative.invoke(null, modelClass.module, "java.lang", DesktopAccessor::class.java.module)

        makeModuleOpen(Field::class.java.module, Field::class.java.getPackage(), DesktopAccessor::class.java.module)
        modifiers = Field::class.java.getDeclaredField("modifiers").also {
            it.isAccessible = true
            it.setInt(it, it.getInt(it) and Modifier.FINAL.inv())
        }
    }

    override fun makeAccessible(obj: AccessibleObject) {
        if (obj is Method) {
            makeModuleOpen(obj.returnType.module, obj.returnType, DesktopAccessor::class.java.module)

            for (type in obj.parameterTypes) {
                makeModuleOpen(type.module, type, DesktopAccessor::class.java.module)
            }

            obj.isAccessible = true
        } else if (obj is Field) {
            makeModuleOpen(obj.type.module, obj.type, DesktopAccessor::class.java.module)

            if (Modifier.isFinal(obj.modifiers)) {
                val newModifiers = obj.modifiers and Modifier.FINAL.inv()
                modifiers.setInt(obj, newModifiers)
            }
            obj.isAccessible = true
        }
    }

    fun makeModuleOpen(from: Module, clazz: Class<*>, to: Module) {
        if (clazz.isArray) {
            makeModuleOpen(from, clazz.componentType, to)
        } else makeModuleOpen(from, clazz.getPackage(), to)
    }

    fun makeModuleOpen(from: Module, pac: Package?, to: Module) {
        if (checkModuleOpen(from, pac, to)) return

        makeModuleOpen(from, pac!!.name, to)
    }

    fun makeModuleOpen(from: Module, pac: String, to: Module) {
        exportNative?.invoke(null, from, pac, to)
        var opensMap = unsafe.getObjectVolatile(from,
            unsafe.objectFieldOffset(opensField)) as MutableMap<String, MutableSet<Module>>?
        if (opensMap == null) {
            opensMap = HashMap<String, MutableSet<Module>>()
            unsafe.putObjectVolatile(from, unsafe.objectFieldOffset(opensField), opensMap)
        }
        var exportsMap = unsafe.getObjectVolatile(from,
            unsafe.objectFieldOffset(exportField)) as MutableMap<String, MutableSet<Module>>?
        if (exportsMap == null) {
            exportsMap = HashMap<String, MutableSet<Module>>()
            unsafe.putObjectVolatile(from, unsafe.objectFieldOffset(exportField), exportsMap)
        }
        val opens = opensMap.computeIfAbsent(pac) { e -> HashSet<Module>() }
        val exports = exportsMap.computeIfAbsent(pac) { e -> HashSet<Module>() }

        try {
            opens.add(to)
        } catch (e: UnsupportedOperationException) {
            val lis = ArrayList(opens)
            lis.add(to)
            opensMap.put(pac, HashSet<Module>(lis))
        }

        try {
            exports.add(to)
        } catch (e: UnsupportedOperationException) {
            val lis = ArrayList(exports)
            lis.add(to)
            exportsMap.put(pac, HashSet<Module>(lis))
        }
    }

    fun checkModuleOpen(from: Module, pac: Package?, to: Module): Boolean {
        Objects.requireNonNull(from)
        Objects.requireNonNull(to)

        return pac?.let { from.isOpen(it.name, to) } ?: true
    }

    fun ensureFieldOpen() {
        val clazz = Class.forName("jdk.internal.reflect.Reflection")
        unsafe.putObjectVolatile(clazz, fieldFilterOffset, of<Class<*>, Set<String>>())
    }
}

private object AndroidAccessor : Accessor {
    val modifiers: Field = Field::class.java.getDeclaredField("modifiers").also { it.isAccessible = true }
    override fun makeAccessible(obj: AccessibleObject) {
        if (obj is Field && Modifier.isFinal(obj.modifiers)) {
            val newModifiers = obj.modifiers and Modifier.FINAL.inv()
            modifiers.set(obj, newModifiers)
        }
        obj.isAccessible = true
    }
}

val accessor = {
    try {
        Class.forName("java.lang.Module")
        DesktopAccessor
    } catch (e: ClassNotFoundException) {
        AndroidAccessor
    }
}()

inline fun <reified T> T.jtype() = T::class.java

// Field accessors
class FieldAccessor<O, T>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): T = field.get(instance) as T
    operator fun setValue(instance: O, property: KProperty<*>, value: T) = field.set(instance, value)
}

class ByteAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Byte = field.getByte(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Byte) = field.setByte(instance, value)
}

class ShortAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Short = field.getShort(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Short) = field.setShort(instance, value)
}

class IntAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Int = field.getInt(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Int) = field.setInt(instance, value)
}

class LongAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Long = field.getLong(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Long) = field.setLong(instance, value)
}

class FloatAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Float = field.getFloat(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Float) = field.setFloat(instance, value)
}

class DoubleAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Double = field.getDouble(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Double) = field.setDouble(instance, value)
}

class BooleanAccessor<O>(private val field: Field) {
    operator fun getValue(instance: O, property: KProperty<*>): Boolean = field.getBoolean(instance)
    operator fun setValue(instance: O, property: KProperty<*>, value: Boolean) = field.setBoolean(instance, value)
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

//static final
class FieldAccessorStaticFinal<U, T>(field: Field) : StaticAccess<U, T> {
    val base = unsafe.staticFieldBase(field)
    val offset = unsafe.staticFieldOffset(field)
    val isVolatile = Modifier.isVolatile(field.modifiers)
    override operator fun getValue(instance: U, property: KProperty<*>): T =
        if (isVolatile) unsafe.getObjectVolatile(base, offset) as T
        else unsafe.getObject(base, offset) as T

    override operator fun setValue(instance: U, property: KProperty<*>, value: T) =
        if (isVolatile) unsafe.putObjectVolatile(base, offset, value)
        else unsafe.putObject(base, offset, value)
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
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any> accessByte(name: String) = ByteAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Byte::class.java) throw IllegalArgumentException("field $it type is not byte")
    accessor.makeAccessible(it)
})

inline fun <reified O : Any> accessShort(name: String) = ShortAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Short::class.java) throw IllegalArgumentException("field $it type is not short")
    accessor.makeAccessible(it)
})

inline fun <reified O : Any> accessInt(name: String) = IntAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Int::class.java) throw IllegalArgumentException("field $it type is not int")
    accessor.makeAccessible(it)
})

inline fun <reified O : Any> accessLong(name: String) = LongAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Long::class.java) throw IllegalArgumentException("field $it type is not long")
    accessor.makeAccessible(it)
})

inline fun <reified O : Any> accessFloat(name: String) = FloatAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Float::class.java) throw IllegalArgumentException("field $it type is not float")
    accessor.makeAccessible(it)
})

inline fun <reified O : Any> accessDouble(name: String) = DoubleAccessor<O>(O::class.java.getDeclaredField(name).also {
    if (it.type != Double::class.java) throw IllegalArgumentException("field $it type is not double")
    accessor.makeAccessible(it)
})

inline fun <reified O : Any> accessBoolean(name: String) =
    BooleanAccessor<O>(O::class.java.getDeclaredField(name).also {
        if (it.type != Boolean::class.java) throw IllegalArgumentException("field $it type is not boolean")
        accessor.makeAccessible(it)
    })

// static
inline fun <U, reified T> KClass<*>.accessField(name: String) = this.java.getDeclaredField(name).also {
    if (!it.type.isAssignableFrom(T::class.java)) throw IllegalArgumentException(
        "field $it type is not instance of ${T::class.java}")
}.let {
    if (Modifier.isFinal(it.modifiers)) FieldAccessorStaticFinal(it)
    else {
        accessor.makeAccessible(it)
        FieldAccessorStatic<U, T>(it)
    }
}

fun <U> KClass<*>.accessByte(name: String) = ByteAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Byte::class.java) throw IllegalArgumentException("field $it type is not byte")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    accessor.makeAccessible(it)
})

fun <U> KClass<*>.accessShort(name: String) = ShortAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Short::class.java) throw IllegalArgumentException("field $it type is not short")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    accessor.makeAccessible(it)
})

fun <U> KClass<*>.accessInt(name: String) = IntAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Int::class.java) throw IllegalArgumentException("field $it type is not int")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    accessor.makeAccessible(it)
})

fun <U> KClass<*>.accessLong(name: String) = LongAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Long::class.java) throw IllegalArgumentException("field $it type is not long")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    accessor.makeAccessible(it)
})

fun <U> KClass<*>.accessFloat(name: String) = FloatAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Float::class.java) throw IllegalArgumentException("field $it type is not float")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    accessor.makeAccessible(it)
})

fun <U> KClass<*>.accessDouble(name: String) = DoubleAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Double::class.java) throw IllegalArgumentException("field $it type is not double")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    accessor.makeAccessible(it)
})

fun <U> KClass<*>.accessBoolean(name: String) = BooleanAccessorStatic<U>(this.java.getDeclaredField(name).also {
    if (it.type != Boolean::class.java) throw IllegalArgumentException("field $it type is not boolean")
    if (Modifier.isFinal(it.modifiers)) throw IllegalArgumentException(
        "The static final field is always immutable, you shouldn't reflect these fields")
    accessor.makeAccessible(it)
})

// Method invoker
class MethodInvoker0<O, R>(private val method: Method) : (O) -> R {
    override fun invoke(self: O) = method.invoke(self) as R
}

class MethodInvoker1<O, P1, R>(private val method: Method) : (O, P1) -> R {
    override fun invoke(self: O, p1: P1) = method.invoke(self, p1) as R
}

class MethodInvoker2<O, P1, P2, R>(private val method: Method) : (O, P1, P2) -> R {
    override fun invoke(self: O, p1: P1, p2: P2) = method.invoke(self, p1, p2) as R
}

class MethodInvoker3<O, P1, P2, P3, R>(private val method: Method) : (O, P1, P2, P3) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3) = method.invoke(self, p1, p2, p3) as R
}

class MethodInvoker4<O, P1, P2, P3, P4, R>(private val method: Method) : (O, P1, P2, P3, P4) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4) = method.invoke(self, p1, p2, p3, p4) as R
}

class MethodInvoker5<O, P1, P2, P3, P4, P5, R>(private val method: Method) : (O, P1, P2, P3, P4, P5) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) = method.invoke(self, p1, p2, p3, p4, p5) as R
}

class MethodInvoker6<O, P1, P2, P3, P4, P5, P6, R>(private val method: Method) : (O, P1, P2, P3, P4, P5, P6) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6) =
        method.invoke(self, p1, p2, p3, p4, p5, p6) as R
}

class MethodInvoker7<O, P1, P2, P3, P4, P5, P6, P7, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7) =
        method.invoke(self, p1, p2, p3, p4, p5, p6, p7) as R
}

class MethodInvoker8<O, P1, P2, P3, P4, P5, P6, P7, P8, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8) =
        method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8) as R
}

class MethodInvoker9<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9) =
        method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9) as R
}

class MethodInvoker10<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R {
    override fun invoke(self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10) =
        method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) as R
}

class MethodInvoker11<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R {
    override fun invoke(
        self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) as R
}

class MethodInvoker12<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R {
    override fun invoke(
        self: O, p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) as R
}

class MethodInvoker13<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) as R
}

class MethodInvoker14<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) as R
}

class MethodInvoker15<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) as R
}

class MethodInvoker16<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) as R
}

class MethodInvoker17<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) as R
}

class MethodInvoker18<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R>(private val method: Method) :
        (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) as R
}

class MethodInvoker19<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R>(
    private val method: Method
) : (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) as R
}

class MethodInvoker20<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R>(
    private val method: Method
) : (O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R {
    override fun invoke(
        self: O,
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19,
        p20: P20
    ) = method.invoke(self, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19,
        p20) as R
}

// static
class StaticInvoker0<R>(private val method: Method) : () -> R {
    override fun invoke() = method.invoke(null) as R
}

class StaticInvoker1<P1, R>(private val method: Method) : (P1) -> R {
    override fun invoke(p1: P1) = method.invoke(null, p1) as R
}

class StaticInvoker2<P1, P2, R>(private val method: Method) : (P1, P2) -> R {
    override fun invoke(p1: P1, p2: P2) = method.invoke(null, p1, p2) as R
}

class StaticInvoker3<P1, P2, P3, R>(private val method: Method) : (P1, P2, P3) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3) = method.invoke(null, p1, p2, p3) as R
}

class StaticInvoker4<P1, P2, P3, P4, R>(private val method: Method) : (P1, P2, P3, P4) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4) = method.invoke(null, p1, p2, p3, p4) as R
}

class StaticInvoker5<P1, P2, P3, P4, P5, R>(private val method: Method) : (P1, P2, P3, P4, P5) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) = method.invoke(null, p1, p2, p3, p4, p5) as R
}

class StaticInvoker6<P1, P2, P3, P4, P5, P6, R>(private val method: Method) : (P1, P2, P3, P4, P5, P6) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6) =
        method.invoke(null, p1, p2, p3, p4, p5, p6) as R
}

class StaticInvoker7<P1, P2, P3, P4, P5, P6, P7, R>(private val method: Method) : (P1, P2, P3, P4, P5, P6, P7) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7) =
        method.invoke(null, p1, p2, p3, p4, p5, p6, p7) as R
}

class StaticInvoker8<P1, P2, P3, P4, P5, P6, P7, P8, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8) =
        method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8) as R
}

class StaticInvoker9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9) =
        method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9) as R
}

class StaticInvoker10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10) =
        method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) as R
}

class StaticInvoker11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11) =
        method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11) as R
}

class StaticInvoker12<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R {
    override fun invoke(
        p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12) as R
}

class StaticInvoker13<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R {
    override fun invoke(
        p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13) as R
}

class StaticInvoker14<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14) as R
}

class StaticInvoker15<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15) as R
}

class StaticInvoker16<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16) as R
}

class StaticInvoker17<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17) as R
}

class StaticInvoker18<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18) as R
}

class StaticInvoker19<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R>(private val method: Method) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19) as R
}

class StaticInvoker20<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R>(
    private val method: Method
) : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19,
        p20: P20
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19,
        p20) as R
}

class StaticInvoker21<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R>(
    private val method: Method
) : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19,
        p20: P20,
        p21: P21
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20,
        p21) as R
}

class StaticInvoker22<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R>(
    private val method: Method
) : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19,
        p20: P20,
        p21: P21,
        p22: P22
    ) = method.invoke(null, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20,
        p21, p22) as R
}

inline fun <reified O : Any, reified R> accessMethod0(name: String) =
    MethodInvoker0<O, R>(O::class.java.getDeclaredMethod(name).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified R> accessMethod1(name: String) =
    MethodInvoker1<O, P1, R>(O::class.java.getDeclaredMethod(name, P1::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified R> accessMethod2(name: String) =
    MethodInvoker2<O, P1, P2, R>(O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified R> accessMethod3(name: String) =
    MethodInvoker3<O, P1, P2, P3, R>(
        O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java).also {
            checkReturnType(it, R::class.java)
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified R> accessMethod4(name: String) =
    MethodInvoker4<O, P1, P2, P3, P4, R>(
        O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java).also {
            checkReturnType(it, R::class.java)
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified R> accessMethod5(name: String) =
    MethodInvoker5<O, P1, P2, P3, P4, P5, R>(
        O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
            P5::class.java).also {
            checkReturnType(it, R::class.java)
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified R> accessMethod6(
    name: String
) = MethodInvoker6<O, P1, P2, P3, P4, P5, P6, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified R> accessMethod7(
    name: String
) = MethodInvoker7<O, P1, P2, P3, P4, P5, P6, P7, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified R> accessMethod8(
    name: String
) = MethodInvoker8<O, P1, P2, P3, P4, P5, P6, P7, P8, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified R> accessMethod9(
    name: String
) = MethodInvoker9<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified R> accessMethod10(
    name: String
) = MethodInvoker10<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified R> accessMethod11(
    name: String
) = MethodInvoker11<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified R> accessMethod12(
    name: String
) = MethodInvoker12<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified R> accessMethod13(
    name: String
) = MethodInvoker13<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified R> accessMethod14(
    name: String
) = MethodInvoker14<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java, P14::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified R> accessMethod15(
    name: String
) = MethodInvoker15<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java, P14::class.java, P15::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified R> accessMethod16(
    name: String
) = MethodInvoker16<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified R> accessMethod17(
    name: String
) = MethodInvoker17<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java,
        P17::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified R> accessMethod18(
    name: String
) = MethodInvoker18<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java,
        P17::class.java, P18::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified R> accessMethod19(
    name: String
) = MethodInvoker19<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java,
        P17::class.java, P18::class.java, P19::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified R> accessMethod20(
    name: String
) = MethodInvoker20<O, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R>(
    O::class.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
        P5::class.java, P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java,
        P11::class.java, P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java,
        P17::class.java, P18::class.java, P19::class.java, P20::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

//static
inline fun <C : Any, reified R> KClass<C>.accessMethod0(name: String) = StaticInvoker0<R>(this.java.getDeclaredMethod(
    name,
).also {
    checkReturnType(it, R::class.java)
    accessor.makeAccessible(it)
})

inline fun <C : Any, reified P1, reified R> KClass<C>.accessMethod1(name: String) =
    StaticInvoker1<P1, R>(this.java.getDeclaredMethod(name, P1::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified R> KClass<C>.accessMethod2(name: String) =
    StaticInvoker2<P1, P2, R>(this.java.getDeclaredMethod(name, P1::class.java, P2::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified R> KClass<C>.accessMethod3(name: String) =
    StaticInvoker3<P1, P2, P3, R>(
        this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java).also {
            checkReturnType(it, R::class.java)
            accessor.makeAccessible(it)
        })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified R> KClass<C>.accessMethod4(name: String) =
    StaticInvoker4<P1, P2, P3, P4, R>(
        this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java).also {
            checkReturnType(it, R::class.java)
            accessor.makeAccessible(it)
        })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified R> KClass<C>.accessMethod5(name: String) =
    StaticInvoker5<P1, P2, P3, P4, P5, R>(
        this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java,
            P5::class.java).also {
            checkReturnType(it, R::class.java)
            accessor.makeAccessible(it)
        })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified R> KClass<C>.accessMethod6(
    name: String
) = StaticInvoker6<P1, P2, P3, P4, P5, P6, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified R> KClass<C>.accessMethod7(
    name: String
) = StaticInvoker7<P1, P2, P3, P4, P5, P6, P7, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified R> KClass<C>.accessMethod8(
    name: String
) = StaticInvoker8<P1, P2, P3, P4, P5, P6, P7, P8, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified R> KClass<C>.accessMethod9(
    name: String
) = StaticInvoker9<P1, P2, P3, P4, P5, P6, P7, P8, P9, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified R> KClass<C>.accessMethod10(
    name: String
) = StaticInvoker10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified R> KClass<C>.accessMethod11(
    name: String
) = StaticInvoker11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified R> KClass<C>.accessMethod12(
    name: String
) = StaticInvoker12<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified R> KClass<C>.accessMethod13(
    name: String
) = StaticInvoker13<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified R> KClass<C>.accessMethod14(
    name: String
) = StaticInvoker14<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java, P14::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified R> KClass<C>.accessMethod15(
    name: String
) = StaticInvoker15<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java, P14::class.java, P15::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified R> KClass<C>.accessMethod16(
    name: String
) = StaticInvoker16<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified R> KClass<C>.accessMethod17(
    name: String
) = StaticInvoker17<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified R> KClass<C>.accessMethod18(
    name: String
) = StaticInvoker18<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java,
        P18::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified R> KClass<C>.accessMethod19(
    name: String
) = StaticInvoker19<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java,
        P18::class.java, P19::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

inline fun <C : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20, reified R> KClass<C>.accessMethod20(
    name: String
) = StaticInvoker20<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R>(
    this.java.getDeclaredMethod(name, P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
        P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
        P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java,
        P18::class.java, P19::class.java, P20::class.java).also {
        checkReturnType(it, R::class.java)
        accessor.makeAccessible(it)
    })

// Constructor invoker
class ConstructorInvoker0<O>(private val constructor: Constructor<O>) : () -> O {
    override fun invoke() = constructor.newInstance()
}

class ConstructorInvoker1<P1, O>(private val constructor: Constructor<O>) : (P1) -> O {
    override fun invoke(p1: P1) = constructor.newInstance(p1)
}

class ConstructorInvoker2<P1, P2, O>(private val constructor: Constructor<O>) : (P1, P2) -> O {
    override fun invoke(p1: P1, p2: P2) = constructor.newInstance(p1, p2)
}

class ConstructorInvoker3<P1, P2, P3, O>(private val constructor: Constructor<O>) : (P1, P2, P3) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3) = constructor.newInstance(p1, p2, p3)
}

class ConstructorInvoker4<P1, P2, P3, P4, O>(private val constructor: Constructor<O>) : (P1, P2, P3, P4) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4) = constructor.newInstance(p1, p2, p3, p4)
}

class ConstructorInvoker5<P1, P2, P3, P4, P5, O>(private val constructor: Constructor<O>) : (P1, P2, P3, P4, P5) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) = constructor.newInstance(p1, p2, p3, p4, p5)
}

class ConstructorInvoker6<P1, P2, P3, P4, P5, P6, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6) =
        constructor.newInstance(p1, p2, p3, p4, p5, p6)
}

class ConstructorInvoker7<P1, P2, P3, P4, P5, P6, P7, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7) =
        constructor.newInstance(p1, p2, p3, p4, p5, p6, p7)
}

class ConstructorInvoker8<P1, P2, P3, P4, P5, P6, P7, P8, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8) =
        constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8)
}

class ConstructorInvoker9<P1, P2, P3, P4, P5, P6, P7, P8, P9, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9) =
        constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9)
}

class ConstructorInvoker10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10) =
        constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)
}

class ConstructorInvoker11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> O {
    override fun invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11) =
        constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11)
}

class ConstructorInvoker12<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> O {
    override fun invoke(
        p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)
}

class ConstructorInvoker13<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> O {
    override fun invoke(
        p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10, p11: P11, p12: P12, p13: P13
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
}

class ConstructorInvoker14<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> O {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
}

class ConstructorInvoker15<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> O {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
}

class ConstructorInvoker16<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> O {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
}

class ConstructorInvoker17<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> O {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17)
}

class ConstructorInvoker18<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, O>(private val constructor: Constructor<O>) :
        (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> O {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18)
}

class ConstructorInvoker19<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, O>(
    private val constructor: Constructor<O>
) : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> O {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19)
}

class ConstructorInvoker20<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, O>(
    private val constructor: Constructor<O>
) : (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> O {
    override fun invoke(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10,
        p11: P11,
        p12: P12,
        p13: P13,
        p14: P14,
        p15: P15,
        p16: P16,
        p17: P17,
        p18: P18,
        p19: P19,
        p20: P20
    ) = constructor.newInstance(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19,
        p20)
}

inline fun <reified O : Any> accessConstructor0() = ConstructorInvoker0<O>(O::class.java.getConstructor().also {
    accessor.makeAccessible(it)
})

inline fun <reified O : Any, reified P1> accessConstructor1() =
    ConstructorInvoker1<P1, O>(O::class.java.getConstructor(P1::class.java).also {
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2> accessConstructor2() =
    ConstructorInvoker2<P1, P2, O>(O::class.java.getConstructor(P1::class.java, P2::class.java).also {
        accessor.makeAccessible(it)
    })

inline fun <reified O : Any, reified P1, reified P2, reified P3> accessConstructor3() =
    ConstructorInvoker3<P1, P2, P3, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4> accessConstructor4() =
    ConstructorInvoker4<P1, P2, P3, P4, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5> accessConstructor5() =
    ConstructorInvoker5<P1, P2, P3, P4, P5, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java)
            .also {
                accessor.makeAccessible(it)
            })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6> accessConstructor6() =
    ConstructorInvoker6<P1, P2, P3, P4, P5, P6, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7> accessConstructor7() =
    ConstructorInvoker7<P1, P2, P3, P4, P5, P6, P7, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8> accessConstructor8() =
    ConstructorInvoker8<P1, P2, P3, P4, P5, P6, P7, P8, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9> accessConstructor9() =
    ConstructorInvoker9<P1, P2, P3, P4, P5, P6, P7, P8, P9, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10> accessConstructor10() =
    ConstructorInvoker10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11> accessConstructor11() =
    ConstructorInvoker11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12> accessConstructor12() =
    ConstructorInvoker12<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13> accessConstructor13() =
    ConstructorInvoker13<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14> accessConstructor14() =
    ConstructorInvoker14<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java, P14::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15> accessConstructor15() =
    ConstructorInvoker15<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java, P14::class.java, P15::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16> accessConstructor16() =
    ConstructorInvoker16<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17> accessConstructor17() =
    ConstructorInvoker17<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18> accessConstructor18() =
    ConstructorInvoker18<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java,
            P18::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19> accessConstructor19() =
    ConstructorInvoker19<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java,
            P18::class.java, P19::class.java).also {
            accessor.makeAccessible(it)
        })

inline fun <reified O : Any, reified P1, reified P2, reified P3, reified P4, reified P5, reified P6, reified P7, reified P8, reified P9, reified P10, reified P11, reified P12, reified P13, reified P14, reified P15, reified P16, reified P17, reified P18, reified P19, reified P20> accessConstructor20() =
    ConstructorInvoker20<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, O>(
        O::class.java.getConstructor(P1::class.java, P2::class.java, P3::class.java, P4::class.java, P5::class.java,
            P6::class.java, P7::class.java, P8::class.java, P9::class.java, P10::class.java, P11::class.java,
            P12::class.java, P13::class.java, P14::class.java, P15::class.java, P16::class.java, P17::class.java,
            P18::class.java, P19::class.java, P20::class.java).also {
            accessor.makeAccessible(it)
        })

fun checkReturnType(met: Method, retType: Class<*>) {
    if (!(retType.isAssignableFrom(
            met.returnType) || (met.returnType == Void.TYPE && retType == Unit::class.java)) || (met.returnType == Unit::class.java && retType == Void.TYPE)
    ) throw IllegalArgumentException("method returned type ${met.returnType} is not instance of $retType")
}
