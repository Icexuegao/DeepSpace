package universecore.android;

import arc.func.Prov;
import arc.struct.ObjectMap;
import universecore.desktopcore.desktop.MethodInvokeHelper;
import universecore.dynamilize.FunctionType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AndroidMethodInvokeHelper implements MethodInvokeHelper {
	protected static final ObjectMap<Class<?>, ObjectMap<String, ObjectMap<FunctionType, Method>>> methodPool = new ObjectMap<>();
	protected static final ObjectMap<Class<?>, ObjectMap<FunctionType, Constructor<?>>> cstrMap = new ObjectMap<>();

	protected static final Prov<ObjectMap<String, ObjectMap<FunctionType, Method>>> prov1 = () -> new ObjectMap<>();
	protected static final Prov<ObjectMap<FunctionType, Method>> prov2 = () -> new ObjectMap<>();
	protected static final Prov<ObjectMap<FunctionType, Constructor<?>>> prov3 = () -> new ObjectMap<>();

	protected Method getMethod(Class<?> clazz, String name, FunctionType argTypes) throws NoSuchMethodException {
		ObjectMap<FunctionType, Method> map = methodPool.get(clazz, prov1).get(name, prov2);

		FunctionType type = FunctionType.inst(argTypes);
		Method res = map.get(type);

		if (res != null) return res;

		for (ObjectMap.Entry<FunctionType, Method> entry : map) {
			if (entry.key.match(argTypes.getTypes())) return entry.value;
		}

		Class<?> curr = clazz;

		while (curr != null) {
			try {
				res = curr.getDeclaredMethod(name, argTypes.getTypes());
			} catch (Throwable ignored) {}

			if (res != null) {
				res.setAccessible(true);
				map.put(FunctionType.from(res), res);
				break;
			}

			curr = curr.getSuperclass();
		}

		if (res != null) return res;

		curr = clazz;
		a:
		while (curr != null) {
			for (Method method : curr.getDeclaredMethods()) {
				if (!method.getName().equals(name)) continue;

				FunctionType t;
				if ((t = FunctionType.from(method)).match(argTypes.getTypes())) {
					method.setAccessible(true);
					res = method;
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

	@SuppressWarnings("unchecked")
	protected <T> Constructor<T> getConstructor(Class<T> clazz, FunctionType argsType) throws NoSuchMethodException {
		ObjectMap<FunctionType, Constructor<?>> map = cstrMap.get(clazz, prov3);

		Constructor<T> res = (Constructor<T>) map.get(argsType);
		if (res != null) return res;

		for (ObjectMap.Entry<FunctionType, Constructor<?>> entry : map) {
			if (entry.key.match(argsType.getTypes())) return (Constructor<T>) entry.value;
		}

		try {
			res = clazz.getConstructor(argsType.getTypes());
			res.setAccessible(true);
		} catch (NoSuchMethodException ignored) {}

		if (res != null) return res;

		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			FunctionType functionType;
			if ((functionType = FunctionType.from(constructor)).match(argsType.getTypes())) {
				map.put(functionType, constructor);
				res = (Constructor<T>) constructor;
				res.setAccessible(true);

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
			return (T) getMethod(object.getClass(), name, type).invoke(object, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
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
			return (T) getMethod(clazz, name, type).invoke(null, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	@Override
	public <T> T newInstance(Class<T> clazz, Object... args) {
		FunctionType funcType = FunctionType.inst(args);
		try {
			return getConstructor(clazz, funcType).newInstance(args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException(e);
		} finally {
			funcType.recycle();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T invoke(Object object, String name, Class<?>[] parameterTypes, Object... args) {
		FunctionType type = FunctionType.inst(parameterTypes);
		try {
			return (T) getMethod(object.getClass(), name, type).invoke(object, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
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
			return (T) getMethod(clazz, name, type).invoke(null, args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		} finally {
			type.recycle();
		}
	}

	@Override
	public <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... args) {
		FunctionType funcType = FunctionType.inst(parameterTypes);
		try {
			return getConstructor(clazz, funcType).newInstance(args);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException(e);
		} finally {
			funcType.recycle();
		}
	}
}
