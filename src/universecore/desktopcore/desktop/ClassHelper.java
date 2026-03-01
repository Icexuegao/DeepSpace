package universecore.desktopcore.desktop;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassHelper {
  default Field getField(Class<?> clazz, String name) {
    try {
      return clazz.getDeclaredField(name);
    } catch (NoSuchFieldException var4) {
      return null;
    }
  }

  default Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
    try {
      return clazz.getDeclaredMethod(name, parameterTypes);
    } catch (NoSuchMethodException var5) {
      return null;
    }
  }

  default <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
    try {
      return clazz.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException var4) {
      return null;
    }
  }

  default Field[] getFields(Class<?> clazz) {
    return clazz.getDeclaredFields();
  }

  default Method[] getMethods(Class<?> clazz) {
    return clazz.getDeclaredMethods();
  }

  default <T> Constructor<T>[] getConstructors(Class<T> clazz) {
    return (Constructor<T>[]) clazz.getDeclaredConstructors();
  }

  default void setPublic(Class<?> clazz) {
  }

  <T> T allocateInstance(Class<? extends T> var1);

  Class<?> defineClass(String var1, byte[] var2, ClassLoader var3);
}

