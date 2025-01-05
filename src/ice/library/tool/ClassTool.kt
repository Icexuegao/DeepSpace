package ice.library.tool

import arc.func.Cons
import java.lang.reflect.Field
import java.lang.reflect.Method

object ClassTool {
    fun getFields(objects: Any): Array<Field> {
        return objects.javaClass.superclass.fields
    }

    fun getDeclaredFields(objects: Any): Array<Field> {
        return objects.javaClass.superclass.declaredFields
    }
    fun getField(objects: Any,name:String): Field {
        val declaredField = objects.javaClass.getDeclaredField(name)
        declaredField.isAccessible=true
        return declaredField
    }
    /**获取该类所有声明字段 并消耗*/
    inline fun <reified E> getField(name: String, objects: Any, cons: Cons<E> = Cons {}): E {
        val declaredField = objects.javaClass.getDeclaredField(name)
        declaredField.isAccessible = true
        val get = declaredField.get(objects)
        if (get is E) {
            cons.get(get)
            return get
        } else throw Exception("反射转换错误:将[${get.javaClass.name}]转换[${E::class.java.name}]失败")
    }

    inline fun <reified C> getDeclaredMethod(name: String): Method {
        val declaredMethod = C::class.java.getDeclaredMethod(name, ByteArray::class.java, Int::class.java)
        declaredMethod.isAccessible = true
        return declaredMethod
    }

}