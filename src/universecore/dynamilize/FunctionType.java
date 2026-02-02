package universecore.dynamilize;


import java.lang.invoke.MethodType;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**函数类型封装对象，记录函数的参数类型用于比对和搜索函数
 *
 * @author EBwilson */
public class FunctionType{
  /**复用回收区容量，改数值通常不需要设置，但如果您可能需要大规模的递归或大量的并发调用，那么您可能需要将这个限制设置为一个更高的数值*/
  public static int MAX_RECYCLE = 4096;

  private static final Class<?>[] EMPTY = new Class[0];
  private static final Stack<FunctionType> RECYCLE_POOL = new Stack<>();

  private Class<?>[] paramType;
  private int hash;

  private FunctionType(Class<?>... paramType){
    this.paramType = paramType;
    hash = Arrays.hashCode(paramType);
  }

  //此类存在频繁的调用，数据量小，使用流处理数据会产生不必要的性能花销，使用for遍历取代流处理
  public static Class<?>[] wrapper(Class<?>... clazz){
    for(int i = 0; i < clazz.length; i++){
      clazz[i] = wrapper(clazz[i]);
    }
    return clazz;
  }

  public static Class<?>[] unwrapped(Class<?>... clazz){
    for(int i = 0; i < clazz.length; i++){
      clazz[i] = unwrapped(clazz[i]);
    }
    return clazz;
  }

  public static Class<?> wrapper(Class<?> clazz){
    if(clazz == int.class) return Integer.class;
    if(clazz == float.class) return Float.class;
    if(clazz == long.class) return Long.class;
    if(clazz == double.class) return Double.class;
    if(clazz == byte.class) return Byte.class;
    if(clazz == short.class) return Short.class;
    if(clazz == boolean.class) return Boolean.class;
    if(clazz == char.class) return Character.class;
    return clazz;
  }

  public static Class<?> unwrapped(Class<?> clazz){
    if(clazz == Integer.class) return int.class;
    if(clazz == Float.class) return float.class;
    if(clazz == Long.class) return long.class;
    if(clazz == Double.class) return double.class;
    if(clazz == Byte.class) return byte.class;
    if(clazz == Short.class) return short.class;
    if(clazz == Boolean.class) return boolean.class;
    if(clazz == Character.class) return char.class;
    return clazz;
  }

  public static FunctionType inst(List<Class<?>> paramType){
    return inst(paramType.toArray(new Class[0]));
  }

  public static synchronized FunctionType inst(Class<?>... paramType){
    if(RECYCLE_POOL.isEmpty()) return new FunctionType(paramType);
    FunctionType res = RECYCLE_POOL.pop();
    res.paramType = paramType;
    res.hash = Arrays.hashCode(paramType);
    return res;
  }

  public static FunctionType inst(Method method){
    return inst(method.getParameterTypes());
  }

  public static FunctionType inst(Object... param){
    return inst(unwrapped(toTypes(param)));
  }

  public static Class<?>[] toTypes(Object... objects){
    Class<?>[] types = new Class[objects.length];

    for(int i = 0; i < types.length; i++){
      types[i] = objects[i] == null? void.class: objects[i].getClass();
    }

    return types;
  }

  public static Class<?>[] toTypes(List<?> objects){
    Class<?>[] types = new Class[objects.size()];

    for(int i = 0; i < types.length; i++){
      types[i] = objects.get(i) == null? void.class: objects.get(i).getClass();
    }

    return types;
  }

  public static FunctionType inst(FunctionType type){
    return inst(type.paramType);
  }

  public static FunctionType from(MethodType type){
    return inst(type.parameterArray());
  }

  public static FunctionType from(Executable method){
    return inst(method.getParameterTypes());
  }

  public static FunctionType generic(int argCount){
    Class<?>[] argTypes = new Class[argCount];
    Arrays.fill(argTypes, void.class);
    return inst(argTypes);
  }

  public static String signature(Method method){
    return method.getName() + FunctionType.from(method);
  }

  public static String signature(String name, Class<?>... argTypes){
    return name + FunctionType.inst(argTypes);
  }

  public static String signature(String name, FunctionType type){
    return name + type;
  }

  public static int typeNameHash(Class<?>[] types){
    return Arrays.hashCode(Arrays.stream(types).map(Class::getName).toArray());
  }

  public boolean match(Object... args){
    return match(unwrapped(toTypes(args)));
  }

  public boolean match(Class<?>... argsType){
    if(argsType.length != paramType.length) return false;

    for(int i = 0; i < paramType.length; i++){
      if(argsType[i] == void.class) continue;
      if(!paramType[i].isAssignableFrom(argsType[i])) return false;
    }

    return true;
  }

  public Class<?>[] getTypes(){
    return paramType;
  }

  public void recycle(){
    if(RECYCLE_POOL.size() >= MAX_RECYCLE) return;

    paramType = EMPTY;
    hash = -1;
    RECYCLE_POOL.push(this);
  }

  @Override
  public boolean equals(Object o){
    if(this == o) return true;
    if(!(o instanceof FunctionType that)) return false;
    return paramType.length == that.paramType.length && hashCode() == o.hashCode();
  }

  @Override
  public int hashCode(){
    return hash;
  }

}
