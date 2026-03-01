package universecore.desktopcore.desktop;

import ice.Ice;
import sun.reflect.ReflectionFactory;
import universecore.UncCore;

import java.lang.invoke.MethodHandles.Lookup;

public class DesktopImpl {
  static Lookup lookup;
  static ClassHelper classHelper;

  static FieldAccessHelper fieldAccessHelper;

  static MethodInvokeHelper methodInvokeHelper;

  static AccessibleHelper accessibleHelper;

  static {
    try {
      lookup = (Lookup) ReflectionFactory.getReflectionFactory().newConstructorForSerialization(Lookup.class, Lookup.class.getDeclaredConstructor(Class.class, Class.class, int.class)).newInstance(Ice.class, null, -1);

      Demodulator.init();
      Demodulator.openModules();

      DesktopClassHelper.init();
      classHelper = new DesktopClassHelper();

      fieldAccessHelper = new DesktopFieldAccessHelper();
      methodInvokeHelper = new DesktopMethodInvokeHelper();
      accessibleHelper = new DesktopAccessibleHelper();

      UncCore.INSTANCE.setClassHelper(classHelper);
      UncCore.INSTANCE.setFieldAccessHelper(fieldAccessHelper);
      UncCore.INSTANCE.setMethodInvokeHelper(methodInvokeHelper);
      UncCore.INSTANCE.setAccessibleHelper(accessibleHelper);

    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
