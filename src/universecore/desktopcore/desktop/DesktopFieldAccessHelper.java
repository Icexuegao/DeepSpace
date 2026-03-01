package universecore.desktopcore.desktop;

import arc.struct.ObjectMap;
import arc.util.Log;
import universecore.util.colletion.CollectionObjectMap;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static universecore.desktopcore.desktop.DesktopImpl.classHelper;

public class DesktopFieldAccessHelper implements FieldAccessHelper {
	protected static final ObjectMap<String, Field> empty = new ObjectMap<>();
	protected static final ObjectMap<Class<?>, ObjectMap<String, Field>> fieldMap = new ObjectMap<>();

	protected static final ObjectMap<Field, MethodHandle> getters = new ObjectMap<>();
	protected static final ObjectMap<Field, MethodHandle> setters = new ObjectMap<>();

	protected static final boolean useUnsafe;

	static {
		boolean tmp;

		try {
			Log.infoTag("Unsafe", "getUnsafe: " + Unsafer.unsafe);
			tmp = true;
		} catch (Throwable e) {
			Log.err(e);

			tmp = false;
		}
		useUnsafe = tmp;
	}

	public Field getField(Class<?> clazz, String name, boolean isStatic) throws NoSuchFieldException {
		Field field = fieldMap.get(clazz, empty).get(name);
		if (field != null) return field;

		if (isStatic) {
			Field f = classHelper.getField(clazz, name);
			if (f != null && (f.getModifiers() & Modifier.STATIC) != 0) {
				return f;
			}
		} else {
			Class<?> curr = clazz;
			while (curr != Object.class) {
				Field f = classHelper.getField(curr, name);
				if (f != null && (f.getModifiers() & Modifier.STATIC) == 0) {
					return f;
				}

				curr = curr.getSuperclass();
			}
		}

		throw new NoSuchFieldException();
	}

	protected MethodHandle getter(Field field) {
		return getters.get(field, () -> {
			try {
				return (field.getModifiers() & Modifier.STATIC) == 0 ?
                        DesktopImpl.lookup.findGetter(field.getDeclaringClass(), field.getName(), field.getType()) :
                        DesktopImpl.lookup.findStaticGetter(field.getDeclaringClass(), field.getName(), field.getType());
				//return lookup.unreflectGetter(field);
			} catch (IllegalAccessException | NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		});
	}

	protected MethodHandle setter(Field field) {
		return setters.get(field, () -> {
			try {
				return (field.getModifiers() & Modifier.STATIC) == 0 ?
						DesktopImpl.lookup.findSetter(field.getDeclaringClass(), field.getName(), field.getType()) :
                        DesktopImpl.lookup.findStaticSetter(field.getDeclaringClass(), field.getName(), field.getType());
				//return lookup.unreflectSetter(field);
			} catch (IllegalAccessException | NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void setByte(Object object, String name, byte value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setByte(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setByteStatic(Class<?> clazz, String name, byte value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setByteStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte getByte(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getByte(field, object) : (byte) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte getByteStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? Unsafer.getByteStatic(field) : (byte) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setShort(Object object, String name, short value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setShort(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setShortStatic(Class<?> clazz, String name, short value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setShortStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public short getShort(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getShort(field, object) : (short) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public short getShortStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? Unsafer.getShortStatic(field) : (short) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setInt(Object object, String name, int value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setInt(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setIntStatic(Class<?> clazz, String name, int value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setIntStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getInt(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getInt(field, object) : (int) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getIntStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? Unsafer.getIntStatic(field) : (int) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setLong(Object object, String name, long value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setLong(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setLongStatic(Class<?> clazz, String name, long value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setLongStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getLong(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getLong(field, object) : (long) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long getLongStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? Unsafer.getLongStatic(field) : (long) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setFloat(Object object, String name, float value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setFloat(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setFloatStatic(Class<?> clazz, String name, float value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setFloatStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float getFloat(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getFloat(field, object) : (float) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float getFloatStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? Unsafer.getFloatStatic(field) : (float) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setDouble(Object object, String name, double value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setDouble(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setDoubleStatic(Class<?> clazz, String name, double value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setDoubleStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getDouble(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getDouble(field, object) : (double) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double getDoubleStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? Unsafer.getDoubleStatic(field) : (double) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setChar(Object object, String name, char value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setChar(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setCharStatic(Class<?> clazz, String name, char value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setCharStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public char getChar(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getChar(field, object) : (char) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public char getCharStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? Unsafer.getCharStatic(field) : (char) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBoolean(Object object, String name, boolean value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setBoolean(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBooleanStatic(Class<?> clazz, String name, boolean value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setBooleanStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean getBoolean(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? Unsafer.getBoolean(field, object) : (boolean) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean getBooleanStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);
			return useUnsafe ? Unsafer.getBooleanStatic(field) : (boolean) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setObject(Object object, String name, Object value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.setObject(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setObjectStatic(Class<?> clazz, String name, Object value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setObjectStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? (T) Unsafer.getObject(field, object) : (T) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObjectStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? (T) Unsafer.getObjectStatic(field) : (T) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void set(Object object, String name, Object value) {
		try {
			Field field = getField(object.getClass(), name, false);

			if (useUnsafe) Unsafer.set(field, object, value);
			else setter(field).invoke(object, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setStatic(Class<?> clazz, String name, Object value) {
		try {
			Field field = getField(clazz, name, true);

			if (useUnsafe) Unsafer.setStatic(field, value);
			else setter(field).invoke(value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Object object, String name) {
		try {
			Field field = getField(object.getClass(), name, false);

			return useUnsafe ? (T) Unsafer.get(field, object) : (T) getter(field).invoke(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getStatic(Class<?> clazz, String name) {
		try {
			Field field = getField(clazz, name, true);

			return useUnsafe ? (T) Unsafer.getStatic(field) : (T) getter(field).invoke();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
