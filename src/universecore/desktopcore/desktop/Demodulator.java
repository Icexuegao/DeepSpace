package universecore.desktopcore.desktop;

import ice.Ice;
import jdk.internal.module.Modules;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static universecore.desktopcore.desktop.DesktopImpl.lookup;

/**
 * The anti modularity tool only provides one main method {@link Demodulator#makeOpenModule(Module, String, Module)}
 * to force software packages that open modules to the required modules.
 * <p>This class behavior may completely break the modular access protection and is inherently insecure. If it is
 * not necessary, please try to avoid using this class.
 * <p><strong>This class is only available after Java 9 to avoid referencing methods of this class in earlier versions,
 * and it is only available on the desktop platform. Any behavior of this class is not allowed on the
 * Android platform.</strong>
 *
 * @author Eipusino
 */
public final class Demodulator {
	//public static Map<Class<?>, Set<String>> fieldFilterMap;

	static MethodHandle implAddOpens;

	private Demodulator() {}

	// The exceptions thrown during initialization are collectively handled in a try-catch block.
	static void init() throws NoSuchMethodException, IllegalAccessException {
		implAddOpens = lookup.findVirtual(Module.class, "implAddOpens", MethodType.methodType(void.class, String.class, Module.class));
	}

	public static void makeOpenModule(Module from, Class<?> clazz, Module to) {
		if (clazz.isArray()) {
			makeOpenModule(from, clazz.getComponentType(), to);
		} else {
			makeOpenModule(from, clazz.getPackage(), to);
		}
	}

	public static void makeOpenModule(Module from, Package pac, Module to) {
		if (pac == null) return;

		makeOpenModule(from, pac.getName(), to);
	}

	/**
	 * @param from To open the module of the package
	 * @param pac The package name of the module to export the package
	 * @param to The module to be exported to.
	 */
	public static void makeOpenModule(Module from, String pac, Module to) {
		if (from.isOpen(pac, to)) return;

		Modules.addExports(from, pac, to);
	}

	public static void makeOpenModule(Module from, String pac) {
		if (from.isOpen(pac)) return;

		Modules.addExports(from, pac);
	}

	static void openModule(Module from, String pn, Module to) throws Throwable {
		implAddOpens.invokeExact(from, pn, to);
	}

	static void openModules() throws Throwable {
		Module base = Object.class.getModule(), main = Ice.class.getModule();

		openModule(base, "java.lang", main);
		openModule(base, "java.lang.reflect", main);
		openModule(base, "jdk.internal.misc", main);
		openModule(base, "jdk.internal.module", main);
		openModule(base, "jdk.internal.reflect", main);
		openModule(base, "sun.nio.ch", main);
	}

	// We directly call the private native method within Class to bypass filtering, so there is no need to do so.
	/*@SuppressWarnings("unchecked")
	static void ensureFieldOpen() {
		try {
			fieldFilterMap = (Map<Class<?>, Set<String>>) lookup.findStaticGetter(Reflection.class, "fieldFilterMap", Map.class).invokeExact();
			fieldFilterMap.clear();
		} catch (Throwable e) {
			Log.err(e);
		}
	}*/
}
