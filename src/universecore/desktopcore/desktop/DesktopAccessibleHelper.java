package universecore.desktopcore.desktop;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public class DesktopAccessibleHelper implements AccessibleHelper {
	static Field override;

	@Override
	public void makeAccessible(AccessibleObject object) {
		try {
			if (override == null) {
				override = AccessibleObject.class.getDeclaredField("override");
				override.setAccessible(true);
			}
			override.setBoolean(object, true);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void makeClassAccessible(Class<?> clazz) {
		//no action
	}
}
