package universecore.util.handler;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**枚举处理器，提供了一些对enum的操作方法，可以创建枚举实例并将其放入枚举的values
 * <p>这个处理器是一个实例工厂，你需要对目标枚举类构造一个枚举处理器实例才可以进行操作
 * <p>由于属性是私有final，需要使用反射设置，此处引用了{@link FieldHandler}来完成反射操，在你得到枚举处理器的实例后即可直接引用处理器提供的方法
 * <p><strong>注意，鉴于mindustry源代码对枚举的声明，被操作的枚举可能带有一个数组保存所有枚举实例，但这仅仅是在初始化时调用了values()
 * <p>但这个数组是一个拷贝，如果你对枚举新增了项目，那么你最好将那个数组重新指定为变更后的values()</strong>
 * @author EBwilson
 * @since 1.0*/
@SuppressWarnings("unchecked")
public class EnumHandler<T extends Enum<?>>{
    private final FieldHandler<T> fieldHandler;
    private final MethodHandler<T> methodHandler;
    private final Class<T> clazz;

    /**用目标枚举的类型构造一个没有构造函数实现的枚举处理器，如果枚举具有构造器实现，那么你必须提供构造函数的外部实现，否则可能会产生意想不到的错误
     * @param clazz 处理器处理的目标枚举类型*/
    public EnumHandler(Class<T> clazz){
        this.clazz = clazz;
        fieldHandler = new FieldHandler<>(clazz);
        methodHandler = new MethodHandler<>(clazz);
    }

    /**用指定的名称，枚举序数以及构造器参数来实例化一个枚举对象，
     * 如果你不将它放入枚举的子条目列表中，那么它的序数可以任意指定，尽管这可能引起不必要的错误
     * @param name 枚举的名字
     * @param ordinal 枚举的序数
     * @param param 传入给构造函数的参数
     * @return 一个具有指定名称与序数的枚举实例*/
    public T newEnumInstance(String name, int ordinal, Object... param){
        try{
            Object[] params = new Object[param.length + 2];

            params[0] = name;
            params[1] = ordinal;

            System.arraycopy(param, 0, params, 2, param.length);

            return methodHandler.newInstance(params);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    /**创建一个枚举实例，并将其插入到枚举的末尾
     * @param addition 创建的实例的名称
     * @param param 附加的构造器参数列表*/
    public T addEnumItemTail(String addition, Object... param){
        try{
            Method method = clazz.getMethod("values");
            method.setAccessible(true);
            return addEnumItem(addition, ((Object[])method.invoke(null)).length, param);
        }
        catch(SecurityException | NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    /**创建一个枚举实例，并将其插入到该枚举项列表中的指定位置
     * 原位置的枚举实例以及其后方的实例都将后移，枚举序数随列表的更新同步更新
     * @param addition 创建的实例的名称
     * @param ordinal 插入的位置对应的序数
     * @param param 附加的构造器参数列表*/
    public T addEnumItem(String addition, int ordinal, Object... param){
        T newEnum = newEnumInstance(addition, ordinal, param);
        rearrange(newEnum, ordinal);
        return newEnum;
    }

    /**将指定枚举项放到指定序数的位置，并重设所有枚举项的序数到自己正确的位置
     * 枚举序数必须正确指定在0到枚举当前项目总数之间，新增项目的序数可以等于当前总数
     * @param instance 要插入的目标枚举实例
     * @param ordinal 要插入到的目标位置的枚举序数
     * @throws IndexOutOfBoundsException 如果指定的序数大于或等于枚举的当前元素总数，或者新增项目的序数大于元素总量*/
    public void rearrange(T instance, int ordinal){
        Field valuesField = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("$VALUES")) {
                valuesField = field;
                break;
            }
        }

        try{
            assert valuesField != null;
            Method method = clazz.getMethod("values");
            T[] arr = (T[])method.invoke(null);
            ArrayList<T> values = new ArrayList<>(Arrays.asList(arr));
            if(values.contains(instance) && ordinal >= values.size()) throw new IndexOutOfBoundsException("rearrange a exist item, ordinal should be less than amount of all items, (ordinal: " + ordinal + ", amount: " + values.size() + ")");
            else if(ordinal > values.size()) throw new IndexOutOfBoundsException("add a new item, ordinal should be equal or less than amount of all items, (ordinal: " + ordinal + ", amount: " + values.size() + ")");

            values.remove(instance);

            values.add(ordinal, instance);

            fieldHandler.setValue(null, valuesField.getName(), values.toArray((T[]) Array.newInstance(clazz, 0)));
        }
        catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException(e);
        }
    }
}
