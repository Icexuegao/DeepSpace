package ice.library.universecore.desktopcore;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import universecore.util.FieldAccessHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings({"unchecked", "DuplicatedCode"})
public class DesktopFieldAccessHelper implements FieldAccessHelper {
    private static final ObjectMap<Class<?>, ObjectMap<String, Field>> fieldMap = new ObjectMap<>();
    private static final ObjectMap<String, Field> EMP = new ObjectMap<>();

    private static final ObjectSet<Field> finalFields = new ObjectSet<>();

    private static final ObjectMap<Field, MethodHandle> getters = new ObjectMap<>();
    private static final ObjectMap<Field, MethodHandle> setters = new ObjectMap<>();

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public Field getField(Class<?> clazz, String field, boolean isStatic) throws NoSuchFieldException{
        Field res = fieldMap.get(clazz, EMP).get(field);
        if(res != null) return res;

        try{
            if(isStatic){
                return getField0(clazz, field);
            }
            else{
                Class<?> curr = clazz;
                while(curr != null){
                    try{
                        return getField0(curr, field);
                    }
                    catch(NoSuchFieldException ignored){}

                    curr = curr.getSuperclass();
                }
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }

        throw new NoSuchFieldException();
    }

    protected Field getField0(Class<?> clazz, String field) throws NoSuchFieldException{
        Field res = clazz.getDeclaredField(field);
        res.setAccessible(true);

        if((res.getModifiers() & Modifier.FINAL) != 0){
            try{
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.set(res, res.getModifiers() & ~Modifier.FINAL);

                finalFields.add(res);
            }catch(Throwable e){
                throw new RuntimeException(e);
            }
        }

        return res;
    }

    protected void initField(Field field){
        getters.get(field, () -> {
            try{
                return lookup.unreflectGetter(field);
            }catch(IllegalAccessException e){
                throw new RuntimeException(e);
            }
        });
        setters.get(field, () -> {
            try{
                return lookup.unreflectSetter(field);
            }catch(IllegalAccessException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void set(Object object, String field, byte value) {
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, byte value) {
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getByte(Object object, String field) {
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (byte) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getByteStatic(Class<?> clazz, String field) {
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (byte) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, short value) {
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, short value) {
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public short getShort(Object object, String field) {
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (short) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public short getShortStatic(Class<?> clazz, String field) {
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (short) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, int value) {
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, int value) {
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getInt(Object object, String field) {
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (int) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getIntStatic(Class<?> clazz, String field) {
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (int) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, long value) {
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, long value) {
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getLong(Object object, String field) {
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (long) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getLongStatic(Class<?> clazz, String field) {
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (long) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, float value) {
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, float value) {
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getFloat(Object object, String field) {
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (float) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getFloatStatic(Class<?> clazz, String field) {
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (float) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, double value) {
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, double value) {
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDouble(Object object, String field) {
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (double) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDoubleStatic(Class<?> clazz, String field) {
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (double) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, boolean value) {
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, boolean value) {
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBoolean(Object object, String field) {
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (boolean) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBooleanStatic(Class<?> clazz, String field) {
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (boolean) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, Object value){
        try{
            Field f = getField(object.getClass(), field, false);

            if(finalFields.contains(f)){
                f.set(object, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(object, value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, Object value){
        try{
            Field f = getField(clazz, field, false);

            if(finalFields.contains(f)){
                f.set(null, value);
                return;
            }

            initField(f);
            setters.get(f).invoke(value);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T get(Object object, String field){
        try{
            Field f = getField(object.getClass(), field, false);
            initField(f);
            return (T) getters.get(f).invoke(object);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getStatic(Class<?> clazz, String field){
        try{
            Field f = getField(clazz, field, true);
            initField(f);
            return (T) getters.get(f).invoke();
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }
}
