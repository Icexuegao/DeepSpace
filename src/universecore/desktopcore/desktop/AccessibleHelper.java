package universecore.desktopcore.desktop;

import java.lang.reflect.AccessibleObject;

public interface AccessibleHelper {
  default void makeAccessible(AccessibleObject object) {
    object.setAccessible(true);
  }

  default void makeClassAccessible(Class<?> clazz) {
  }
}
