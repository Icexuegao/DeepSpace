package universecore.desktopcore;

import arc.struct.ObjectMap;
import universecore.desktopcore.handler.Demodulator;
import universecore.dynamilize.FunctionType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressWarnings("DuplicatedCode")
public class DesktopMethodInvokeHelper9 extends DesktopMethodInvokeHelper{
  private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

  protected MethodHandle getMethod(Class<?> clazz, String name, FunctionType argTypes){
    ObjectMap<FunctionType, MethodHandle> map = methodPool.get(clazz, ObjectMap::new).get(name, ObjectMap::new);

    FunctionType type = FunctionType.inst(argTypes);
    MethodHandle res = map.get(type);

    if(res != null) return res;

    for(ObjectMap.Entry<FunctionType, MethodHandle> entry: map){
      if(entry.key.match(argTypes.getTypes())) return entry.value;
    }

    Class<?> curr = clazz;

    while(curr != null){
      try{
        Method met = curr.getDeclaredMethod(name, argTypes.getTypes());

        Demodulator.makeModuleOpen(curr.getModule(), curr, DesktopFieldAccessHelper9.class.getModule());
        met.setAccessible(true);
        res = lookup.unreflect(met);
      }
      catch(Throwable ignored){}

      if(res != null){
        map.put(FunctionType.inst(res.type().parameterArray()), res);
        break;
      }

      curr = curr.getSuperclass();
    }

    if(res != null) return res;

    curr = clazz;
    a: while(curr != null){
      for(Method method: curr.getDeclaredMethods()){
        if(!method.getName().equals(name)) continue;
        Class<?>[] methodArgs = method.getParameterTypes();

        FunctionType t;
        if((t = FunctionType.from(method)).match(methodArgs)){
          Demodulator.makeModuleOpen(curr.getModule(), curr, DesktopFieldAccessHelper9.class.getModule());
          method.setAccessible(true);

          try{
            res = lookup.unreflect(method);
          }catch(IllegalAccessException e){
            throw new RuntimeException(e);
          }
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

  protected MethodHandle getConstructor(Class<?> type, FunctionType argsType){
    ObjectMap<FunctionType, MethodHandle> map = methodPool.get(type, ObjectMap::new).get("<init>", ObjectMap::new);

    MethodHandle res = map.get(argsType);
    if(res != null) return res;

    for(ObjectMap.Entry<FunctionType, MethodHandle> entry: map){
      if(entry.key.match(argsType.getTypes())) return entry.value;
    }

    try{
      Constructor<?> met = type.getConstructor(argsType.getTypes());

      Demodulator.makeModuleOpen(type.getModule(), type, DesktopFieldAccessHelper9.class.getModule());
      met.setAccessible(true);

      res = lookup.unreflectConstructor(met);
    }catch(NoSuchMethodException|IllegalAccessException ignored){}

    if(res != null) return res;

    for(Constructor<?> constructor: type.getDeclaredConstructors()){
      FunctionType functionType;
      if((functionType = FunctionType.from(constructor)).match(argsType.getTypes())){
        try{
          Demodulator.makeModuleOpen(type.getModule(), type, DesktopFieldAccessHelper9.class.getModule());
          constructor.setAccessible(true);

          res = lookup.unreflectConstructor(constructor);
          map.put(functionType, res);
        }catch(IllegalAccessException e){
          throw new RuntimeException(e);
        }

        break;
      }
      functionType.recycle();
    }

    if(res != null) return res;

    throw new NoSuchMethodError("no such constructor in class: " + type + " with assignable parameter: " + argsType);
  }
}
