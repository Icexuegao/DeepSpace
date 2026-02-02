package universecore.android;

import arc.struct.ObjectMap;
import universecore.util.FieldAccessHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("DuplicatedCode")
public class AndroidFieldAccessHelper implements FieldAccessHelper{
  private static final ObjectMap<String, Field> EMP = new ObjectMap<>();
  private static final ObjectMap<Class<?>, ObjectMap<String, Field>> fieldMap = new ObjectMap<>();

  private static final Method getFieldMethod;

  static {
    try {
      getFieldMethod = Class.class.getMethod("getDeclaredField", String.class);
      getFieldMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

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

    throw new NoSuchFieldException("field " + field + " was not found in class: " + clazz);
  }

  protected Field getField0(Class<?> clazz, String field) throws NoSuchFieldException{
    Field res;
    try {
      res = (Field) getFieldMethod.invoke(clazz, field);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new NoSuchFieldException();
    }
    res.setAccessible(true);

    return res;
  }

  @Override
  public void set(Object object, String field, byte value) {
    try {
      getField(object.getClass(), field, false).setByte(object, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, byte value) {
    try {
      getField(clazz, field, false).setByte(null, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte getByte(Object object, String field) {
    try {
      return getField(object.getClass(), field, false).getByte(object);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte getByteStatic(Class<?> clazz, String field) {
    try {
      return getField(clazz, field, false).getByte(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(Object object, String field, short value) {
    try {
      getField(object.getClass(), field, false).setInt(object, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, short value) {
    try {
      getField(clazz, field, false).setShort(null, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public short getShort(Object object, String field) {
    try {
      return getField(object.getClass(), field, false).getShort(object);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public short getShortStatic(Class<?> clazz, String field) {
    try {
      return getField(clazz, field, false).getShort(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(Object object, String field, int value) {
    try {
      getField(object.getClass(), field, false).setInt(object, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, int value) {
    try {
      getField(clazz, field, false).setInt(null, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int getInt(Object object, String field) {
    try {
      return getField(object.getClass(), field, false).getInt(object);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int getIntStatic(Class<?> clazz, String field) {
    try {
      return getField(clazz, field, false).getInt(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(Object object, String field, long value) {
    try {
      getField(object.getClass(), field, false).setLong(object, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, long value) {
    try {
      getField(clazz, field, false).setLong(null, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public long getLong(Object object, String field) {
    try {
      return getField(object.getClass(), field, false).getLong(object);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public long getLongStatic(Class<?> clazz, String field) {
    try {
      return getField(clazz, field, false).getLong(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(Object object, String field, float value) {
    try {
      getField(object.getClass(), field, false).setFloat(object, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, float value) {
    try {
      getField(clazz, field, false).setFloat(null, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public float getFloat(Object object, String field) {
    try {
      return getField(object.getClass(), field, false).getFloat(object);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public float getFloatStatic(Class<?> clazz, String field) {
    try {
      return getField(clazz, field, false).getFloat(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(Object object, String field, double value) {
    try {
      getField(object.getClass(), field, false).setDouble(object, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, double value) {
    try {
      getField(clazz, field, false).setDouble(null, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public double getDouble(Object object, String field) {
    try {
      return getField(object.getClass(), field, false).getDouble(object);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public double getDoubleStatic(Class<?> clazz, String field) {
    try {
      return getField(clazz, field, false).getDouble(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(Object object, String field, boolean value) {
    try {
      getField(object.getClass(), field, false).setBoolean(object, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, boolean value) {
    try {
      getField(clazz, field, false).setBoolean(null, value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean getBoolean(Object object, String field) {
    try {
      return getField(object.getClass(), field, false).getBoolean(object);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean getBooleanStatic(Class<?> clazz, String field) {
    try {
      return getField(clazz, field, false).getBoolean(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(Object object, String field, Object value){
    try{
      getField(object.getClass(), field, false).set(object, value);
    }catch(IllegalAccessException | NoSuchFieldException e){
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setStatic(Class<?> clazz, String field, Object value){
    try{
      getField(clazz, field, true).set(null, value);
    }catch(IllegalAccessException | NoSuchFieldException e){
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(Object object, String field){
    try{
      return (T) getField(object.getClass(), field, false).get(object);
    }catch(IllegalAccessException | NoSuchFieldException e){
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getStatic(Class<?> clazz, String field){
    try{
      return (T) getField(clazz, field, true).get(null);
    }catch(IllegalAccessException | NoSuchFieldException e){
      throw new RuntimeException(e);
    }
  }
}
