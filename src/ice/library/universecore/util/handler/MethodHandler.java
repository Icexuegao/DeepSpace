package ice.library.universecore.util.handler;

import ice.library.universecore.UncCore;

import java.util.HashMap;

/**对方法调用的实用工具集，包含调用，实例化等
 *
 * @since 1.2
 * @author EBwilson*/
@SuppressWarnings("unchecked")
public class MethodHandler<T>{
    private static final HashMap<Class<?>, MethodHandler<?>> defaultMap = new HashMap<>();

    private final Class<T> clazz;

    public MethodHandler(Class<T> clazz){
        this.clazz = clazz;
    }

    /**对一个对象调用其给定名称和参数类型的方法，这不受访问修饰符影响，参数中的null会按通用位处理，即null位任何类型都可以进行匹配
     *
     * @param object 调用所方法执行的目标对象
     * @param name 方法名称
     * @param args 传递给方法的参数列表
     * @return 目标方法的返回值*/
    public <R> R invoke(T object, String name, Object... args){
        return UncCore.INSTANCE.getMethodInvokeHelper() .invoke(object, name, args);
    }

    /**调用该处理器指定的类型中具有指定名称和参数类型的静态方法，这不受访问修饰符影响，参数中的null会按通用位处理，即null位任何类型都可以进行匹配
     *
     * @param name 方法名称
     * @param args 传递给方法的参数列表
     * @return 目标方法的返回值*/
    public <R> R invokeStatic(String name, Object... args){
        return UncCore.INSTANCE.getMethodInvokeHelper().invokeStatic(clazz, name, args);
    }

    /**实例化这个处理器指定的类，获得一个该类型的实例，传入的参数中的null会按通用位处理，即null位任何类型都可以进行匹配
     *
     * @param args 传递给构造器的参数列表*/
    public T newInstance(Object... args){
        return UncCore.INSTANCE.getMethodInvokeHelper().newInstance(clazz, args);
    }

    /**使用默认准则创建一个方法处理器并缓存它，使用它来进行方法调用操作
     * @see MethodHandler#invoke(Object, String, Object...) */
    public static <Obj, Return> Return invokeDefault(Obj object, String name, Object... args){
        return ((MethodHandler<Obj>)defaultMap.computeIfAbsent(object.getClass(), e -> new MethodHandler<>(object.getClass()))).invoke(object, name, args);
    }

    /**使用默认准则创建一个方法处理器并缓存它，使用它来进行方法静态调用操作
     * @see MethodHandler#invokeStatic(String, Object...)*/
    public static <Type, Return> Return invokeDefault(Class<Type> clazz, String name, Object... args){
        return defaultMap.computeIfAbsent(clazz, e -> new MethodHandler<>(clazz)).invokeStatic(name, args);
    }

    /**使用默认准则创建一个方法处理器，使用它来进行方法调用操作，但不缓存这个处理器
     * @see MethodHandler#invoke(Object, String, Object...) */
    public static <Obj, Return> Return invokeTemp(Obj object, String name, Object... args){
        return ((MethodHandler<Obj>) new MethodHandler<>(object.getClass())).invoke(object, name, args);
    }

    /**使用默认准则创建一个方法处理器，使用它来进行静态方法调用，但不缓存这个处理器
     * @see MethodHandler#invokeStatic(String, Object...) */
    public static <Type, Return> Return invokeTemp(Class<Type> clazz, String name, Object... args){
        return new MethodHandler<>(clazz).invokeStatic(name, args);
    }


    /**使用默认准则创建一个方法处理器并缓存它，使用它来进行构造函数调用
     * @see MethodHandler#newInstance(Object...) */
    public static <Type> Type newInstanceDefault(Class<Type> clazz, Object... args){
        return (Type) defaultMap.computeIfAbsent(clazz, e -> new MethodHandler<>(clazz)).newInstance(args);
    }


    /**使用默认准则创建一个方法处理器，使用它来调用构造函数，但不缓存这个处理器
     * @see MethodHandler#newInstance(Object...) */
    public static <Type> Type newInstanceTemp(Class<Type> clazz, Object... args){
        return new MethodHandler<>(clazz).newInstance(args);
    }
}
