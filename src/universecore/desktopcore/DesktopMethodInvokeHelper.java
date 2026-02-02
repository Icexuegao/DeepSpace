package universecore.desktopcore;

import arc.struct.ObjectMap;
import universecore.dynamilize.FunctionType;
import universecore.util.MethodInvokeHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressWarnings("DuplicatedCode")
public class DesktopMethodInvokeHelper implements MethodInvokeHelper{
  protected static final ObjectMap<Class<?>, ObjectMap<String, ObjectMap<FunctionType, MethodHandle>>> methodPool = new ObjectMap<>();

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
        met.setAccessible(true);
        res = lookup.unreflect(met);
      }catch(Throwable ignored){}

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
      met.setAccessible(true);
      res = lookup.unreflectConstructor(met);
    }catch(NoSuchMethodException|IllegalAccessException ignored){}

    if(res != null) return res;

    for(Constructor<?> constructor: type.getDeclaredConstructors()){
      FunctionType functionType;
      if((functionType = FunctionType.from(constructor)).match(argsType.getTypes())){
        try{
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

  @SuppressWarnings("unchecked")
  @Override
  public <T> T invoke(Object owner, String method, Object... args){
    FunctionType type = FunctionType.inst(args);
    try{
      return (T) invoke(owner, getMethod(owner.getClass(), method, type), args);
    }catch(Throwable e){
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
      return (T) invokeStatic(getMethod(clazz, method, type), args);
    }catch(Throwable e){
      throw new RuntimeException(e);
    }finally{
      type.recycle();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T newInstance(Class<T> clazz, Object... args){
    FunctionType type = FunctionType.inst(args);
    try{
      return (T) invokeStatic(getConstructor(clazz, type), args);
    }catch(Throwable e){
      throw new RuntimeException(e);
    }finally{
      type.recycle();
    }
  }

  public Object invokeStatic(MethodHandle handle, Object... args){
    try{
      return switch(args.length){
        case 0 -> handle.invoke();
        case 1 -> handle.invoke(args[0]);
        case 2 -> handle.invoke(args[0], args[1]);
        case 3 -> handle.invoke(args[0], args[1], args[2]);
        case 4 -> handle.invoke(args[0], args[1], args[2], args[3]);
        case 5 -> handle.invoke(args[0], args[1], args[2], args[3], args[4]);
        case 6 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
        case 7 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        case 8 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        case 9 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        case 10 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9]);
        case 11 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10]);
        case 12 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11]);
        case 13 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12]);
        case 14 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13]);
        case 15 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14]);
        case 16 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
        case 17 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
        case 18 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
        case 19 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
        case 20 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19]);
        case 21 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20]);
        case 22 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21]);
        case 23 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22]);
        case 24 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23]);
        case 25 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24]);
        case 26 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
        case 27 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
        case 28 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
        case 29 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
        case 30 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
            args[29]);
        case 31 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
            args[29], args[30]);
        case 32 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
            args[29], args[30], args[31]);
        default -> handle.invokeWithArguments(args);
      };
    }catch(Throwable e){
      throw new RuntimeException(e);
    }
  }

  public Object invoke(Object inst, MethodHandle handle, Object... args){
    try{
      return switch(args.length){
        case 0 -> handle.invoke(inst);
        case 1 -> handle.invoke(inst, args[0]);
        case 2 -> handle.invoke(inst, args[0], args[1]);
        case 3 -> handle.invoke(inst, args[0], args[1], args[2]);
        case 4 -> handle.invoke(inst, args[0], args[1], args[2], args[3]);
        case 5 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4]);
        case 6 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5]);
        case 7 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        case 8 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        case 9 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        case 10 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9]);
        case 11 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10]);
        case 12 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11]);
        case 13 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12]);
        case 14 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13]);
        case 15 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14]);
        case 16 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
        case 17 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
        case 18 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
        case 19 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
        case 20 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19]);
        case 21 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20]);
        case 22 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21]);
        case 23 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22]);
        case 24 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23]);
        case 25 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24]);
        case 26 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
        case 27 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
        case 28 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
        case 29 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
        case 30 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
            args[29]);
        case 31 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
            args[29], args[30]);
        case 32 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8],
            args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18],
            args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28],
            args[29], args[30], args[31]);
        default -> handle.invokeWithArguments(args);
      };
    }catch(Throwable e){
      throw new RuntimeException(e);
    }
  }
}
