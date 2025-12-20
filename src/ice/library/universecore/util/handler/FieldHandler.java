package ice.library.universecore.util.handler;

import ice.library.universecore.UncCore;

import java.util.WeakHashMap;

/**
 * 字段操作的静态方法集合，包含了读取，写入等操作，所有引用抛出异常都被catch并封装到{@link RuntimeException}，无需手动try或抛出
 * @author EBwilson
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FieldHandler<T> {
    private static final WeakHashMap<Class, FieldHandler> defaultHandlers = new WeakHashMap<>();

    private final Class<T> clazz;

    public FieldHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 设定指定对象的选中属性值,将无视该属性的访问修饰符和final修饰符,如果目标对象为null,则设置的字段为static。
     * <n>除非字段是静态的,否则不允许传入空目标对象
     * @param object 要更改属性值的对象
     * @param key 要进行更改的属性名称
     * @param value 要写入的值
     * @throws NullPointerException 若传入的目标对象为null同时字段不是静态的
     */
    public void setValue(T object, String key, Object value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    /**
     * 获取指定的字段值，并返回它，如果字段不存在则会以抛出异常结束，如果目标对象为null，则设置的字段为static
     * 除非字段是静态的，否则不允许传入空目标对象
     * @param object 目标对象，若字段非静态则要求不为空，若字段为静态的，将忽略此参数
     * @param key 字段名称
     * @return 字段的值，如果它存在的话
     * @throws NullPointerException 若传入的目标对象为null同时字段不是静态的
     */
    public <R> R getValue(T object, String key) {
        return object == null ? UncCore.INSTANCE.getFieldAccessHelper().getStatic(clazz, key) : UncCore.INSTANCE.getFieldAccessHelper().get(object, key);
    }

    public void setValue(T object, String key, byte value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    public byte getByteValue(T object, String key) {
        if (object == null) return UncCore.INSTANCE.getFieldAccessHelper().getByteStatic(clazz, key);
        else return UncCore.INSTANCE.getFieldAccessHelper().getByte(object, key);
    }

    public void setValue(T object, String key, short value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    public short getShortValue(T object, String key) {
        if (object == null) return UncCore.INSTANCE.getFieldAccessHelper().getShortStatic(clazz, key);
        else return UncCore.INSTANCE.getFieldAccessHelper().getShort(object, key);
    }

    public void setValue(T object, String key, int value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    public int getIntValue(T object, String key) {
        if (object == null) return UncCore.INSTANCE.getFieldAccessHelper().getIntStatic(clazz, key);
        else return UncCore.INSTANCE.getFieldAccessHelper().getInt(object, key);
    }

    public void setValue(T object, String key, long value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    public long getLongValue(T object, String key) {
        if (object == null) return UncCore.INSTANCE.getFieldAccessHelper().getLongStatic(clazz, key);
        else return UncCore.INSTANCE.getFieldAccessHelper().getLong(object, key);
    }

    public void setValue(T object, String key, float value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    public float getFloatValue(T object, String key) {
        if (object == null) return UncCore.INSTANCE.getFieldAccessHelper().getFloatStatic(clazz, key);
        else return UncCore.INSTANCE.getFieldAccessHelper().getFloat(object, key);
    }

    public void setValue(T object, String key, double value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    public double getDoubleValue(T object, String key) {
        if (object == null) return UncCore.INSTANCE.getFieldAccessHelper().getDoubleStatic(clazz, key);
        else return UncCore.INSTANCE.getFieldAccessHelper().getDouble(object, key);
    }

    public void setValue(T object, String key, boolean value) {
        if (object == null) UncCore.INSTANCE.getFieldAccessHelper().setStatic(clazz, key, value);
        else UncCore.INSTANCE.getFieldAccessHelper().set(object, key, value);
    }

    public boolean getBooleanValue(T object, String key) {
        if (object == null) return UncCore.INSTANCE.getFieldAccessHelper().getBooleanStatic(clazz, key);
        else return UncCore.INSTANCE.getFieldAccessHelper().getBoolean(object, key);
    }

    /**
     * 使用默认规则构造一个处理器对象并缓存，使用这个默认处理器来执行setValue操作
     * @see FieldHandler#setValue(Object, String, Object)
     */
    public static void setValueDefault(Object obj, String key, Object value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    /**
     * 使用默认规则构造一个处理器对象并缓存，使用这个默认处理器来执行静态的getValue操作
     * @see FieldHandler#setValue(Object, String, Object)
     */
    public static void setValueDefault(Class<?> clazz, String key, Object value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    /**
     * 使用默认规则构造一个处理器对象并缓存，使用这个默认处理器来执行getValue操作
     * @see FieldHandler#getValue(Object, String)
     */
    public static <T> T getValueDefault(Object obj, String key) {
        return (T) cachedHandler(obj.getClass()).getValue(obj, key);
    }

    /**
     * 使用默认规则构造一个处理器对象并缓存，使用这个默认处理器来执行静态的getValue操作
     * @see FieldHandler#getValue(Object, String)
     */
    public static <T> T getValueDefault(Class<?> clazz, String key) {
        return (T) cachedHandler(clazz).getValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, byte value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, byte value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static byte getByteDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getByteValue(obj, key);
    }

    public static byte getByteDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getByteValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, short value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, short value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static short getShortDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getShortValue(obj, key);
    }

    public static short getShortDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getShortValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, int value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, int value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static int getIntDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getIntValue(obj, key);
    }

    public static int getIntDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getIntValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, float value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, float value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static float getFloatDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getFloatValue(obj, key);
    }

    public static float getFloatDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getFloatValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, double value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, double value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static double getDoubleDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getDoubleValue(obj, key);
    }

    public static double getDoubleDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getDoubleValue(null, key);
    }

    public static void setValueDefault(Object obj, String key, boolean value) {
        cachedHandler(obj.getClass()).setValue(obj, key, value);
    }

    public static void setValueDefault(Class<?> clazz, String key, boolean value) {
        cachedHandler(clazz).setValue(null, key, value);
    }

    public static boolean getBooleanDefault(Object obj, String key) {
        return cachedHandler(obj.getClass()).getBooleanValue(obj, key);
    }

    public static boolean getBooleanDefault(Class<?> clazz, String key) {
        return cachedHandler(clazz).getBooleanValue(null, key);
    }

    private static FieldHandler cachedHandler(Class<?> clazz) {
        return defaultHandlers.computeIfAbsent(clazz, e -> new FieldHandler(clazz));
    }

    public static void decache(Class<?> clazz) {
        defaultHandlers.remove(clazz);
    }

    /** 清空所有当前缓存的处理器 */
    public static void clearDefault() {
        defaultHandlers.clear();
    }
}
