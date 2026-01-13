package universecore.util;

import java.lang.reflect.AccessibleObject;

public interface AccessibleHelper {
  void makeAccessible(AccessibleObject object);

  void makeClassAccessible(Class<?> clazz);
}
