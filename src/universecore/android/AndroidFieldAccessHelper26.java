package universecore.android;

import arc.struct.ObjectMap;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings("unchecked")
public class AndroidFieldAccessHelper26 extends AndroidFieldAccessHelper {
    private static final ObjectMap<Field, MethodHandle> getters = new ObjectMap<>();
    private static final ObjectMap<Field, MethodHandle> setters = new ObjectMap<>();

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    protected void initField(Field field) {
        getters.get(field, () -> {
            try {
                return lookup.unreflectGetter(field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        setters.get(field, () -> {
            try {
                return lookup.unreflectSetter(field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void set(Object object, String field, Object value) {
        try {
            Field f = getField(object.getClass(), field, false);

            if ((f.getModifiers() & Modifier.FINAL) != 0) {
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, Object value) {
        try {
            Field f = getField(clazz, field, false);

            if ((f.getModifiers() & Modifier.FINAL) != 0) {
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T get(Object object, String field) {
        try {
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (T) getters.get(f).invoke(object);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getStatic(Class<?> clazz, String field) {
        try {
            Field f = getField(clazz, field, true);
            initField(f);
            return (T) getters.get(f).invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
