package universecore.desktopcore.desktop;

import arc.func.Prov;
import arc.struct.ObjectMap;
import universecore.dynamilize.FunctionType;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static universecore.desktopcore.desktop.DesktopClassHelper.*;
import static universecore.desktopcore.desktop.DesktopImpl.classHelper;
import static universecore.desktopcore.desktop.DesktopImpl.lookup;

public class DesktopMethodInvokeHelper implements MethodInvokeHelper {
	protected static final ObjectMap<Class<?>, ObjectMap<String, ObjectMap<FunctionType, MethodHandle>>> methodPool = new ObjectMap<>();

	protected static final Prov<ObjectMap<String, ObjectMap<FunctionType, MethodHandle>>> prov1 = () -> new ObjectMap<>();
	protected static final Prov<ObjectMap<FunctionType, MethodHandle>> prov2 = () -> new ObjectMap<>();

	protected MethodHandle getMethod(Class<?> clazz, String name, FunctionType argTypes) throws NoSuchMethodException, IllegalAccessException {
      ObjectMap<FunctionType, MethodHandle> map = methodPool.get(clazz, prov1).get(name, prov2);

		FunctionType type = FunctionType.inst(argTypes);
		MethodHandle res = map.get(type);

		if (res != null) return res;

		for (ObjectMap.Entry<FunctionType, MethodHandle> entry : map) {
			if (entry.key.match(argTypes.getTypes())) return entry.value;
		}

		Class<?> curr = clazz;

		while (curr != null) {
			Method method = classHelper.getMethod(curr, name, argTypes.getTypes());

			if (method != null) {
				method.setAccessible(true);
				res = lookup.unreflect(method);
			}

			if (res != null) {
				map.put(inst(res.type()), res);
				break;
			}

			curr = curr.getSuperclass();
		}

		if (res != null) return res;

		curr = clazz;
		a:
		while (curr != null) {
			for (Method method : classHelper.getMethods(curr)) {
				if (!method.getName().equals(name)) continue;
				Class<?>[] methodArgs = (Class<?>[]) mtypes.get(method);

				FunctionType t;
				if ((t = from(method)).match(methodArgs)) {
					method.setAccessible(true);

					res = lookup.unreflect(method);
					map.put(t, res);
					break a;
				}
				t.recycle();
			}

			curr = curr.getSuperclass();
		}

		if (res == null)
			throw new NoSuchMethodException("no such method " + name + " in class: " + clazz + " with assignable parameter: " + argTypes);

		return res;
	}

	protected MethodHandle getConstructor(Class<?> clazz, FunctionType argsType) throws IllegalAccessException, NoSuchMethodException {
      ObjectMap<FunctionType, MethodHandle> map = methodPool.get(clazz, prov1).get("<init>", prov2);

		MethodHandle res = map.get(argsType);
		if (res != null) return res;

		for (ObjectMap.Entry<FunctionType, MethodHandle> entry : map) {
			if (entry.key.match(argsType.getTypes())) return entry.value;
		}

		Constructor<?> cstr = classHelper.getConstructor(clazz, argsType.getTypes());
		if (cstr != null) {
			cstr.setAccessible(true);
			res = lookup.unreflectConstructor(cstr);
		}

		if (res != null) return res;

		for (Constructor<?> constructor : classHelper.getConstructors(clazz)) {
			FunctionType functionType;
			if ((functionType = from(constructor)).match(argsType.getTypes())) {
				constructor.setAccessible(true);

				res = lookup.unreflectConstructor(constructor);
				map.put(functionType, res);

				break;
			}
			functionType.recycle();
		}

		if (res != null) return res;

		throw new NoSuchMethodException("no such constructor in class: " + clazz + " with assignable parameter: " + argsType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T invoke(Object object, String name, Object... args) {
		FunctionType type = FunctionType.inst(args);
		try {
			return (T) Reflects.invokeVirtual(object, getMethod(object.getClass(), name, type), args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T invokeStatic(Class<?> clazz, String name, Object... args) {
		FunctionType type = FunctionType.inst(args);
		try {
			return (T) Reflects.invokeStatic(getMethod(clazz, name, type), args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T newInstance(Class<T> clazz, Object... args) {
		FunctionType type = FunctionType.inst(args);
		try {
			return (T) Reflects.invokeStatic(getConstructor(clazz, type), args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T invoke(Object object, String name, Class<?>[] parameterTypes, Object... args) {
		FunctionType type = FunctionType.inst(parameterTypes);
		try {
			return (T) Reflects.invokeVirtual(object, getMethod(object.getClass(), name, type), args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T invokeStatic(Class<?> clazz, String name, Class<?>[] parameterTypes, Object... args) {
		FunctionType type = FunctionType.inst(parameterTypes);
		try {
			return (T) Reflects.invokeStatic(getMethod(clazz, name, type), args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... args) {
		FunctionType type = FunctionType.inst(parameterTypes);
		try {
			return (T) Reflects.invokeStatic(getConstructor(clazz, type), args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	public static FunctionType inst(MethodType methodType) {
		return FunctionType.inst((Class<?>[]) ptypes.get(methodType));
	}

	public static FunctionType from(Method method) {
		return FunctionType.inst((Class<?>[]) mtypes.get(method));
	}

	public static FunctionType from(Constructor<?> constructor) {
		return FunctionType.inst((Class<?>[]) ctypes.get(constructor));
	}
}
