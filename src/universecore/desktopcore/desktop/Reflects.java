package universecore.desktopcore.desktop;

import arc.func.Boolf;
import arc.func.Prov;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import mindustry.Vars;
import org.jetbrains.annotations.Nullable;
import universecore.UncCore;
import universecore.dynamilize.FunctionType;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public final class Reflects {
  static final ObjectMap<String, Field> targetFieldMap = new ObjectMap();

  private Reflects() {
  }

  public static String defs(Class<?> type) {
    if (type != Boolean.TYPE && type != Boolean.class) {
      if (type != Byte.TYPE && type != Byte.class && type != Short.TYPE && type != Short.class && type != Integer.TYPE && type != Integer.class && type != Long.TYPE && type != Long.class && type != Character.TYPE && type != Character.class) {
        return type != Float.TYPE && type != Float.class && type != Double.TYPE && type != Double.class ? "null" : "0.0";
      } else {
        return "0";
      }
    } else {
      return "false";
    }
  }

  public static Object def(Class<?> type) {
    if (type != Boolean.TYPE && type != Boolean.class) {
      if (type != Byte.TYPE && type != Byte.class) {
        if (type != Short.TYPE && type != Short.class) {
          if (type != Integer.TYPE && type != Integer.class) {
            if (type != Long.TYPE && type != Long.class) {
              if (type != Character.TYPE && type != Character.class) {
                if (type != Float.TYPE && type != Float.class) {
                  return type != Double.TYPE && type != Double.class ? null : (double)0.0F;
                } else {
                  return 0.0F;
                }
              } else {
                return '\u0000';
              }
            } else {
              return 0L;
            }
          } else {
            return 0;
          }
        } else {
          return Short.valueOf((short)0);
        }
      } else {
        return 0;
      }
    } else {
      return false;
    }
  }

  public static Class<?>[] typeOf(Class<?>... types) {
    return types;
  }

  public static <T> Prov<T> supply(Class<T> type, Class<?>[] parameterTypes, Object... args) {
    try {
      Constructor<T> cons = type.getDeclaredConstructor(parameterTypes);
      return () -> {
        try {
          return cons.newInstance(args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
          throw new RuntimeException(e);
        }
      };
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T>  Class<T> findClass(String name) {
    try {
      return name == null ? null : (Class<T>) Class.forName(name, true, Vars.mods.mainLoader());
    } catch (ClassNotFoundException var2) {
      return null;
    }
  }

  public static @Nullable Field findField(Class<?> type, String name) {
    while(type != null) {
      Field field = UncCore.INSTANCE.getClassHelper().getField(type, name);
      if (field != null) {
        return field;
      }

      type = type.getSuperclass();
    }

    return null;
  }

  public static @Nullable Field findField(Class<?> type, Boolf<Field> filler) {
    while(type != null) {
      Field[] fields = UncCore.INSTANCE.getClassHelper().getFields(type);

      for(Field field : fields) {
        if (filler.get(field)) {
          return field;
        }
      }

      type = type.getSuperclass();
    }

    return null;
  }

  public static @Nullable Method findMethod(Class<?> type, String name, Class<?>... parameterTypes) {
    while(type != null) {
      Method method = UncCore.INSTANCE.getClassHelper().getMethod(type, name, parameterTypes);
      if (method != null) {
        return method;
      }

      type = type.getSuperclass();
    }

    return null;
  }

  public static <T> @Nullable Constructor<T> findConstructor(Class<T> type, Class<?>... args) {
    Constructor<T>[] constructors = UncCore.INSTANCE.getClassHelper().getConstructors(type);

    for(Constructor<T> constructor : constructors) {
      if (Arrays.equals(constructor.getParameterTypes(), args)) {
        return constructor;
      }
    }

    return null;
  }

  public static boolean isInstanceButNotSubclass(Object obj, Class<?> type) {
    if (type.isInstance(obj)) {
      try {
        return !getClassSubclassHierarchy(obj.getClass()).contains(type);
      } catch (ClassCastException var3) {
        return false;
      }
    } else {
      return false;
    }
  }

  public static Set<Class<?>> getClassSubclassHierarchy(Class<?> clazz) {
    Class<?> c = clazz.getSuperclass();

    ObjectSet<Class<?>> hierarchy;
    for(hierarchy = new ObjectSet(); c != Object.class; c = c.getSuperclass()) {
      hierarchy.add(c);
      Class<?>[] interfaces = c.getInterfaces();
      hierarchy.addAll(interfaces);
    }

    return (Set<Class<?>>) hierarchy;
  }

  public static boolean isAssignable(Field sourceType, Field targetType) {
    return sourceType != null && targetType != null && targetType.getType().isAssignableFrom(sourceType.getType());
  }

  public static boolean isAssignable(Class<?>[] sourceTypes, Class<?>[] targetTypes) {
    if (sourceTypes.length != targetTypes.length) {
      return false;
    } else {
      for(int i = 0; i < sourceTypes.length; ++i) {
        if (sourceTypes[i] != targetTypes[i] && !targetTypes[i].isAssignableFrom(sourceTypes[i])) {
          return false;
        }
      }

      return true;
    }
  }

  public static boolean isAssignableWithBoxing(Class<?>[] sourceTypes, Class<?>[] targetTypes) {
    if (sourceTypes.length != targetTypes.length) {
      return false;
    } else {
      for(int i = 0; i < sourceTypes.length; ++i) {
        if (!isAssignableWithBoxing(sourceTypes[i], targetTypes[i])) {
          return false;
        }
      }

      return true;
    }
  }

  public static boolean isAssignableWithBoxing(Class<?> sourceType, Class<?> targetType) {
    return targetType.isAssignableFrom(sourceType) || targetType.isPrimitive() && FunctionType.wrapper(targetType).isAssignableFrom(sourceType) || sourceType.isPrimitive() && targetType.isAssignableFrom(FunctionType.wrapper(sourceType));
  }

  public static Object invokeStatic(MethodHandle handle, Object... args) throws Throwable {
    Object var10000;
    switch (args.length) {
      case 0 -> var10000 = handle.invoke();
      case 1 -> var10000 = handle.invoke(args[0]);
      case 2 -> var10000 = handle.invoke(args[0], args[1]);
      case 3 -> var10000 = handle.invoke(args[0], args[1], args[2]);
      case 4 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3]);
      case 5 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4]);
      case 6 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
      case 7 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
      case 8 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
      case 9 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
      case 10 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
      case 11 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
      case 12 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
      case 13 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
      case 14 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
      case 15 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14]);
      case 16 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
      case 17 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
      case 18 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
      case 19 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
      case 20 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19]);
      case 21 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20]);
      case 22 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21]);
      case 23 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22]);
      case 24 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23]);
      case 25 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24]);
      case 26 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
      case 27 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
      case 28 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
      case 29 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
      case 30 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28], args[29]);
      case 31 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28], args[29], args[30]);
      case 32 -> var10000 = handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28], args[29], args[30], args[31]);
      default -> var10000 = handle.invokeWithArguments(args);
    }

    return var10000;
  }

  public static Object invokeVirtual(Object object, MethodHandle handle, Object... args) throws Throwable {
    Object var10000;
    switch (args.length) {
      case 0:
        var10000 = handle.invoke(object);
        break;
      case 1:
        var10000 = handle.invoke(object, args[0]);
        break;
      case 2:
        var10000 = handle.invoke(object, args[0], args[1]);
        break;
      case 3:
        var10000 = handle.invoke(object, args[0], args[1], args[2]);
        break;
      case 4:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3]);
        break;
      case 5:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4]);
        break;
      case 6:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5]);
        break;
      case 7:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        break;
      case 8:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        break;
      case 9:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        break;
      case 10:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
        break;
      case 11:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
        break;
      case 12:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11]);
        break;
      case 13:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12]);
        break;
      case 14:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13]);
        break;
      case 15:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14]);
        break;
      case 16:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
        break;
      case 17:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16]);
        break;
      case 18:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17]);
        break;
      case 19:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18]);
        break;
      case 20:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19]);
        break;
      case 21:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20]);
        break;
      case 22:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21]);
        break;
      case 23:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22]);
        break;
      case 24:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23]);
        break;
      case 25:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24]);
        break;
      case 26:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25]);
        break;
      case 27:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26]);
        break;
      case 28:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27]);
        break;
      case 29:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28]);
        break;
      case 30:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28], args[29]);
        break;
      case 31:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28], args[29], args[30]);
        break;
      case 32:
        var10000 = handle.invoke(object, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15], args[16], args[17], args[18], args[19], args[20], args[21], args[22], args[23], args[24], args[25], args[26], args[27], args[28], args[29], args[30], args[31]);
        break;
      default:
        Object[] methodArgs = new Object[args.length + 1];
        methodArgs[0] = object;
        System.arraycopy(args, 0, methodArgs, 1, args.length);
        var10000 = handle.invokeWithArguments(methodArgs);
    }

    return var10000;
  }
}
