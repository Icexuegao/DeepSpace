package universecore.desktopcore.desktop;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static universecore.desktopcore.desktop.DesktopImpl.lookup;
import static universecore.desktopcore.desktop.Unsafer.unsafe;

public class DesktopClassHelper implements ClassHelper {
	static MethodHandle getFields, getMethods, getConstructors;
	static VarHandle mtypes, ctypes, ptypes;

	static void init() throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException {
		getFields = lookup.findVirtual(Class.class, "getDeclaredFields0", MethodType.methodType(Field[].class, boolean.class));
		getMethods = lookup.findVirtual(Class.class, "getDeclaredMethods0", MethodType.methodType(Method[].class, boolean.class));
		getConstructors = lookup.findVirtual(Class.class, "getDeclaredConstructors0", MethodType.methodType(Constructor[].class, boolean.class));

		mtypes = lookup.findVarHandle(Method.class, "parameterTypes", Class[].class);
		ctypes = lookup.findVarHandle(Constructor.class, "parameterTypes", Class[].class);
		ptypes = lookup.findVarHandle(MethodType.class, "ptypes", Class[].class);
	}

	@Override
	public Field getField(Class<?> type, String name) {
		try {
			Field[] fields = (Field[]) getFields.invokeExact(type, false);
			for (Field field : fields) {
				if (field.getName().equals(name)) return field;
			}
			return null;
		} catch (Throwable e) {
			return ClassHelper.super.getField(type, name);
		}
	}

	@Override
	public Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
		try {
			Method[] methods = (Method[]) getMethods.invokeExact(type, false);
			for (Method method : methods) {
				if (method.getName().equals(name) && Arrays.equals((Class<?>[]) DesktopClassHelper.mtypes.get(method), parameterTypes)) return method;
			}
			return null;
		} catch (Throwable e) {
			return ClassHelper.super.getMethod(type, name, parameterTypes);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) {
		try {
			Constructor<T>[] constructors = (Constructor<T>[]) getConstructors.invokeExact(type, false);
			for (Constructor<T> constructor : constructors) {
				if (Arrays.equals((Class<?>[]) ctypes.get(constructor), parameterTypes)) return constructor;
			}
			return null;
		} catch (Throwable e) {
			return ClassHelper.super.getConstructor(type, parameterTypes);
		}
	}

	@Override
	public Field[] getFields(Class<?> type) {
		try {
			return (Field[]) getFields.invokeExact(type, false);
		} catch (Throwable e) {
			return type.getDeclaredFields();
		}
	}

	@Override
	public Method[] getMethods(Class<?> type) {
		try {
			return (Method[]) getMethods.invokeExact(type, false);
		} catch (Throwable e) {
			return type.getDeclaredMethods();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Constructor<T>[] getConstructors(Class<T> type) {
		try {
			return (Constructor<T>[]) getConstructors.invokeExact(type, false);
		} catch (Throwable e) {
			return ClassHelper.super.getConstructors(type);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T allocateInstance(Class<? extends T> clazz) {
		Objects.requireNonNull(clazz);

		try {
			return (T) unsafe.allocateInstance(clazz);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> defineClass(String name, byte[] bytes, ClassLoader loader) throws ClassFormatError {
		return unsafe.defineClass(name, bytes, 0, bytes.length, loader, null);
	}
}
