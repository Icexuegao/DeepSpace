package universecore.android;

import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.util.Log;
import universecore.UncCore;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.Buffer;
import java.util.Objects;
import java.util.function.Function;


@SuppressWarnings("removal")
public class AndroidImpl {
  static final Cons<Throwable> exceptionHandler = e -> {
  };

  static Constructor<Lookup> constructor;

  static final ObjectMap<Class<?>, Lookup> lookupMap = new ObjectMap<>();
  static final Function<Class<?>, Lookup> lookupBuilder = clazz -> {
    try {
      return constructor.newInstance(clazz, 15);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  };

  static {

    try {
     // HiddenApi.load();
    } catch (Throwable e) {
      Log.err("It seems you platform is special. (But don't worry)", e);
    }
    UncCore.INSTANCE.setFieldAccessHelper(new AndroidFieldAccessHelper());
    UncCore.INSTANCE.setMethodInvokeHelper(new AndroidMethodInvokeHelper());

    try {
      constructor = Lookup.class.getDeclaredConstructor(Class.class, int.class);
      constructor.setAccessible(true);
    } catch (Throwable e) {
      Log.err(e);
    }
  }

  // Due to the lack of TRUSTED lookup in Android, each class needs to create an ALL_MODES lookup.

  public Lookup lookup(Class<?> clazz) {
    return lookupMap.get(clazz, lookupBuilder.apply(clazz));
  }

}
