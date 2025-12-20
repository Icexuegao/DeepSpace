package ice.library.universecore.util;


public interface FieldAccessHelper{
    void set(Object object, String field, byte value);

    void setStatic(Class<?> clazz, String field, byte value);

    byte getByte(Object object, String field);

    byte getByteStatic(Class<?> clazz, String field);

    void set(Object object, String field, short value);

    void setStatic(Class<?> clazz, String field, short value);

    short getShort(Object object, String field);

    short getShortStatic(Class<?> clazz, String field);

    void set(Object object, String field, int value);

    void setStatic(Class<?> clazz, String field, int value);

    int getInt(Object object, String field);

    int getIntStatic(Class<?> clazz, String field);

    void set(Object object, String field, long value);

    void setStatic(Class<?> clazz, String field, long value);

    long getLong(Object object, String field);

    long getLongStatic(Class<?> clazz, String field);

    void set(Object object, String field, float value);

    void setStatic(Class<?> clazz, String field, float value);

    float getFloat(Object object, String field);

    float getFloatStatic(Class<?> clazz, String field);

    void set(Object object, String field, double value);

    void setStatic(Class<?> clazz, String field, double value);

    double getDouble(Object object, String field);

    double getDoubleStatic(Class<?> clazz, String field);

    void set(Object object, String field, boolean value);

    void setStatic(Class<?> clazz, String field, boolean value);

    boolean getBoolean(Object object, String field);

    boolean getBooleanStatic(Class<?> clazz, String field);

    void set(Object object, String field, Object value);

    void setStatic(Class<?> clazz, String field, Object value);

    <T> T get(Object object, String field);

    <T> T getStatic(Class<?> clazz, String field);

}
