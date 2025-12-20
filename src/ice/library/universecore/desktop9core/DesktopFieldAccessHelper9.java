package ice.library.universecore.desktop9core;


import ice.library.universecore.desktopcore.DesktopFieldAccessHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@SuppressWarnings({"unchecked", "DuplicatedCode"})
public class DesktopFieldAccessHelper9 extends DesktopFieldAccessHelper {
    private static final boolean useUnsafe;

    static {
        boolean tmp;
        try{
            Class.forName("sun.misc.Unsafe");
            tmp = true;
        }catch(ClassNotFoundException e){
            tmp = false;
        }
        useUnsafe = tmp;
    }

    @Override
    protected Field getField0(Class<?> clazz, String field) throws NoSuchFieldException{
        Field res = clazz.getDeclaredField(field);
        Demodulator.makeModuleOpen(
                clazz.getModule(),
                clazz.getPackage(),
                DesktopFieldAccessHelper9.class.getModule()
        );
        res.setAccessible(true);

        if((res.getModifiers() & Modifier.FINAL) != 0){
            try{
                Field modifiers = Field.class.getDeclaredField("modifiers");
                Demodulator.makeModuleOpen(
                        Field.class.getModule(),
                        Field.class.getPackage(),
                        DesktopFieldAccessHelper9.class.getModule()
                );
                modifiers.setAccessible(true);
                modifiers.set(res, res.getModifiers() & ~Modifier.FINAL);
            }catch(Throwable e){
                throw new RuntimeException(e);
            }
        }

        return res;
    }

    @Override
    public void set(Object object, String field, byte value) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, byte value) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getByte(Object object, String field) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return UnsafeAccess.getByte(f, object);
            }
            else{
                return super.getByte(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getByteStatic(Class<?> clazz, String field) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return UnsafeAccess.getByteStatic(f);
            }
            else{
                return super.getByteStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, short value) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, short value) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public short getShort(Object object, String field) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return UnsafeAccess.getShort(f, object);
            }
            else{
                return super.getShort(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public short getShortStatic(Class<?> clazz, String field) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return UnsafeAccess.getByteStatic(f);
            }
            else{
                return super.getByteStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, int value) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, int value) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getInt(Object object, String field) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return UnsafeAccess.getInt(f, object);
            }
            else{
                return super.getInt(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getIntStatic(Class<?> clazz, String field) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return UnsafeAccess.getIntStatic(f);
            }
            else{
                return super.getIntStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, long value) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, long value) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getLong(Object object, String field) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return UnsafeAccess.getLong(f, object);
            }
            else{
                return super.getLong(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getLongStatic(Class<?> clazz, String field) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return UnsafeAccess.getLongStatic(f);
            }
            else{
                return super.getLongStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, float value) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, float value) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getFloat(Object object, String field) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return UnsafeAccess.getFloat(f, object);
            }
            else{
                return super.getFloat(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getFloatStatic(Class<?> clazz, String field) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return UnsafeAccess.getFloatStatic(f);
            }
            else{
                return super.getFloatStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, double value) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, double value) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDouble(Object object, String field) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return UnsafeAccess.getDouble(f, object);
            }
            else{
                return super.getDouble(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDoubleStatic(Class<?> clazz, String field) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return UnsafeAccess.getDoubleStatic(f);
            }
            else{
                return super.getDoubleStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, boolean value) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, boolean value) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBoolean(Object object, String field) {
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return UnsafeAccess.getBoolean(f, object);
            }
            else{
                return super.getBoolean(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean getBooleanStatic(Class<?> clazz, String field) {
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return UnsafeAccess.getBooleanStatic(f);
            }
            else{
                return super.getBooleanStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(Object object, String field, Object value){
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                UnsafeAccess.set(f, object, value);
            }
            else{
                super.set(object, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setStatic(Class<?> clazz, String field, Object value){
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, false);
                UnsafeAccess.setStatic(f, value);
            }
            else{
                super.setStatic(clazz, field, value);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T get(Object object, String field){
        try{
            if(useUnsafe){
                Field f = getField(object.getClass(), field, false);
                return (T) UnsafeAccess.get(f, object);
            }
            else{
                return super.get(object, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getStatic(Class<?> clazz, String field){
        try{
            if(useUnsafe){
                Field f = getField(clazz, field, true);
                return (T) UnsafeAccess.getStatic(f);
            }
            else{
                return super.getStatic(clazz, field);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }
}
