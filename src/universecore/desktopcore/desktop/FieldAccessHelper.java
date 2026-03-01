package universecore.desktopcore.desktop;

public interface  FieldAccessHelper {
   void setByte(Object var1, String var2, byte var3);

   void setByteStatic(Class<?> var1, String var2, byte var3);

   byte getByte(Object var1, String var2);

   byte getByteStatic(Class<?> var1, String var2);

    void setShort(Object var1, String var2, short var3);

   void setShortStatic(Class<?> var1, String var2, short var3);

   short getShort(Object var1, String var2);

    short getShortStatic(Class<?> var1, String var2);

    void setInt(Object var1, String var2, int var3);

    void setIntStatic(Class<?> var1, String var2, int var3);

    int getInt(Object var1, String var2);

    int getIntStatic(Class<?> var1, String var2);

    void setLong(Object var1, String var2, long var3);

    void setLongStatic(Class<?> var1, String var2, long var3);

   long getLong(Object var1, String var2);

   long getLongStatic(Class<?> var1, String var2);

   void setFloat(Object var1, String var2, float var3);

    void setFloatStatic(Class<?> var1, String var2, float var3);

   float getFloat(Object var1, String var2);

   float getFloatStatic(Class<?> var1, String var2);

    void setDouble(Object var1, String var2, double var3);

   void setDoubleStatic(Class<?> var1, String var2, double var3);

    double getDouble(Object var1, String var2);

    double getDoubleStatic(Class<?> var1, String var2);

   void setChar(Object var1, String var2, char var3);

    void setCharStatic(Class<?> var1, String var2, char var3);

    char getChar(Object var1, String var2);

    char getCharStatic(Class<?> var1, String var2);

    void setBoolean(Object var1, String var2, boolean var3);

   void setBooleanStatic(Class<?> var1, String var2, boolean var3);

   boolean getBoolean(Object var1, String var2);

    boolean getBooleanStatic(Class<?> var1, String var2);

    void setObject(Object var1, String var2, Object var3);

   void setObjectStatic(Class<?> var1, String var2, Object var3);

    <T> T getObject(Object var1, String var2);

    <T> T getObjectStatic(Class<?> var1, String var2);

   void set(Object var1, String var2, Object var3);

    void setStatic(Class<?> var1, String var2, Object var3);

   <T> T get(Object var1, String var2);

    <T> T getStatic(Class<?> var1, String var2);
}
