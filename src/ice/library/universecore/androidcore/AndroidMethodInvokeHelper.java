package ice.library.universecore.androidcore;

import arc.struct.ObjectMap;
import ice.library.universecore.dynamilize.FunctionType;
import ice.library.universecore.util.MethodInvokeHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AndroidMethodInvokeHelper implements MethodInvokeHelper {
  private static final ObjectMap<Class<?>, ObjectMap<String, ObjectMap<FunctionType, Method>>> methodPool = new ObjectMap<>();
  private static final ObjectMap<Class<?>, ObjectMap<FunctionType, Constructor<?>>> cstrMap = new ObjectMap<>();

  protected Method getMethod(Class<?> clazz, String name, FunctionType argTypes){
    ObjectMap<FunctionType, Method> map = methodPool.get(clazz, ObjectMap::new).get(name, ObjectMap::new);

    FunctionType type = FunctionType.inst(argTypes);
    Method res = map.get(type);

    if(res != null) return res;

    for(ObjectMap.Entry<FunctionType, Method> entry: map){
      if(entry.key.match(argTypes.getTypes())) return entry.value;
    }

    Class<?> curr = clazz;

    while(curr != null){
      try{
        res = curr.getDeclaredMethod(name, argTypes.getTypes());
      }catch(Throwable ignored){}

      if(res != null){
        res.setAccessible(true);
        map.put(FunctionType.from(res), res);
        break;
      }

      curr = curr.getSuperclass();
    }

    if(res != null) return res;

    curr = clazz;
    a: while(curr != null){
      for(Method method: curr.getDeclaredMethods()){
        if(!method.getName().equals(name)) continue;

        FunctionType t;
        if((t = FunctionType.from(method)).match(argTypes.getTypes())){
          method.setAccessible(true);
          res = method;
          map.put(t, res);
          break a;
        }
        t.recycle();
      }

      curr = curr.getSuperclass();
    }

    if(res == null)
      throw new NoSuchMethodError("no such method " + name + " in class: " + clazz + " with assignable parameter: " + argTypes);

    return res;
  }

  @SuppressWarnings("unchecked")
  protected <T> Constructor<T> getConstructor(Class<T> type, FunctionType argsType){
    ObjectMap<FunctionType, Constructor<?>> map = cstrMap.get(type, ObjectMap::new);

    Constructor<T> res = (Constructor<T>) map.get(argsType);
    if(res != null) return res;

    for(ObjectMap.Entry<FunctionType, Constructor<?>> entry: map){
      if(entry.key.match(argsType.getTypes())) return (Constructor<T>) entry.value;
    }

    try{
      res = type.getConstructor(argsType.getTypes());
      res.setAccessible(true);
    }catch(NoSuchMethodException ignored){}

    if(res != null) return res;

    for(Constructor<?> constructor: type.getDeclaredConstructors()){
      FunctionType functionType;
      if((functionType = FunctionType.from(constructor)).match(argsType.getTypes())){
        map.put(functionType, constructor);
        res = (Constructor<T>) constructor;
        res.setAccessible(true);

        break;
      }
      functionType.recycle();
    }

    if(res != null) return res;

    throw new NoSuchMethodError("no such constructor in class: " + type + " with assignable parameter: " + argsType);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T invoke(Object owner, String method, Object... args){
    FunctionType type = FunctionType.inst(args);
    try{
      return (T) getMethod(owner.getClass(), method, type).invoke(owner, args);
    }catch(IllegalAccessException|InvocationTargetException e){
      throw new RuntimeException(e);
    }finally{
      type.recycle();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T invokeStatic(Class<?> clazz, String method, Object... args){
    FunctionType type = FunctionType.inst(args);
    try{
      return (T) getMethod(clazz, method, type).invoke(null, args);
    }catch(IllegalAccessException|InvocationTargetException e){
      throw new RuntimeException(e);
    }finally{
      type.recycle();
    }
  }

  @Override
  public <T> T newInstance(Class<T> type, Object... args){
    FunctionType funcType = FunctionType.inst(args);
    try{
      return getConstructor(type,funcType).newInstance(args);
    }catch(IllegalAccessException|InvocationTargetException|InstantiationException e){
      throw new RuntimeException(e);
    }finally{
      funcType.recycle();
    }
  }
}
