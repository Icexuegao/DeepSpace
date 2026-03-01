package universecore.desktopcore.desktop;

public interface MethodInvokeHelper {
  <T> T invoke(Object var1, String var2, Object... var3);

  <T> T invokeStatic(Class<?> var1, String var2, Object... var3);

  <T> T newInstance(Class<T> var1, Object... var2);

  <T> T invoke(Object var1, String var2, Class<?>[] var3, Object... var4);

  <T> T invokeStatic(Class<?> var1, String var2, Class<?>[] var3, Object... var4);

  <T> T newInstance(Class<T> var1, Class<?>[] var2, Object... var3);
}
