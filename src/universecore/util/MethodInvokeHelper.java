package universecore.util;

public interface MethodInvokeHelper{
  <T> T invoke(Object owner, String method, Object... args);

  <T> T invokeStatic(Class<?> clazz, String method, Object... args);

  <T> T newInstance(Class<T> type, Object... args);
}
