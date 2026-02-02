package singularity.core;

import arc.files.Fi;
import arc.util.Log;
import arc.util.serialization.Jval;
import singularity.Sgl;
import singularity.ui.fragments.entityinfo.HealthBarStyle;
import universecore.util.handler.MethodHandler;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ModConfig {
  private static final int configVersion = 10;
  private static final Field[] configs = ModConfig.class.getFields();

  //basic/基础设置
  //主菜单界面设置
  @Order(0f)
  public boolean disableModMainMenu;
  @Order(1f)
  public boolean showModMenuWenLaunch;
  @Order(2f)
  public boolean mainMenuUniverseBackground;
  @Order(3f)
  public boolean staticMainMenuBackground;
  @Order(4f)
  public float[] defaultCameraPos;
  @Order(5f)
  public boolean movementCamera;
  @Order(5.5f)
  public int maxNotifyHistories;

  //游戏目标内信息显示
  @Order(6f)
  public boolean showInfos;
  @Order(7f)
  public float statusInfoAlpha;
  @Order(8f)
  public float flushInterval;
  @Order(9f)
  public int maxDisplay;
  @Order(10f)
  public float showInfoScl;
  @Order(11f)
  public float holdDisplayRange;

  @Order(12f)
  public HealthBarStyle healthBarStyle=HealthBarStyle.shape;

  @Order(13f)
  public float statusSize;
  @Order(14f)
  public boolean showStatusTime;

  //UI视觉
  @Order(15f)
  public boolean enableBlur;
  @Order(16f)
  public int blurLevel;
  @Order(17f)
  public float backBlurLen;

  //Advanced/高级设置
  @Order(25f)
  public boolean enableModsInterops;
  @Order(26f)
  public boolean interopAssignUnitCosts;
  @Order(27f)
  public boolean interopAssignEmpModels;
  @Order(28f)
  public boolean modReciprocal;
  @Order(29f)
  public boolean modReciprocalContent;

  //debug/调试设置
  @Order(30f)
  public boolean loadInfo;
  @Order(31f)
  public boolean debugMode;

  private String lastContext;

  public void load() {
   /* if (!Sgl.configFile.exists()) {
      Sgl.internalConfigDir.child("mod_config.hjson").copyTo(Sgl.configFile);
      Log.info("Configuration file is not exist, copying the default configuration");
      load(Sgl.configFile);
    } else {
      if (!load(Sgl.configFile)) {
        Fi backup;
        Sgl.configFile.copyTo(backup = Sgl.configDirectory.child("mod_config.hjson.bak"));
        Sgl.internalConfigDir.child("mod_config.hjson").copyTo(Sgl.configFile);
        Log.info("default configuration file version updated, eld config should be override(backup file for old file was created)");
        load(Sgl.configFile);
        String tmp = lastContext;
        load(backup, true);
        lastContext = tmp;

        save();
      }
    }*/

    if (loadInfo) printConfig();
  }

  public void printConfig() {
    StringBuilder results = new StringBuilder();

    for (Field cfg : configs) {
      try {
        results.append("  ").append(cfg.getName()).append(" = ").append(cfg.get(this)).append(";").append(System.lineSeparator());
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    Log.info("Mod config loaded! The config data:[" + System.lineSeparator() + results + "]");
  }

  public boolean load(Fi file) {
    return load(file, false);
  }

  public boolean load(Fi file, boolean loadOld) {
    int n;
    char[] part = new char[8192];
    StringBuilder sb = new StringBuilder();
    try (Reader r = file.reader()) {
      while ((n = r.read(part, 0, part.length)) != -1) {
        sb.append(part, 0, n);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    lastContext = sb.toString();
    Jval config = Jval.read(lastContext);

    boolean old = config.get("configVersion").asInt() != configVersion;

    if (!loadOld && old) return false;

    for (Field cfg : configs) {
      if (!config.has(cfg.getName())) continue;

      String temp = config.get(cfg.getName()).toString();
      try {
        cfg.set(this, warp(cfg.getType(), temp));
      } catch (IllegalArgumentException|IllegalAccessException e) {
        Log.err(e);
      }
    }

    return !old;
  }

  public void save() {
    try {
      save(Sgl.configFile);
    } catch (IOException e) {
      Log.err(e);
    }
  }

  @SuppressWarnings({"HardcodedFileSeparator", "unchecked"})
  public void save(Fi file) throws IOException {
   if (true)return;
    Jval tree = Jval.newObject();

    Jval.JsonMap map = tree.asObject();
    map.put("configVersion", Jval.valueOf(configVersion));

    Field[] configs = ModConfig.class.getFields();
    Arrays.sort(configs, (f1, f2) -> {
      float f = f1.getAnnotation(Order.class).value() - f2.getAnnotation(Order.class).value();
      if (f == 0) return 0;
      return f > 0 ? 1 : -1;
    });
    try {
      for (Field cfg : configs) {
        String key = cfg.getName();
        Object obj = cfg.get(this);
        if (obj == null) {
          if (CharSequence.class.isAssignableFrom(cfg.getType())) {
            map.put(key, Jval.valueOf(""));
          } else if (cfg.getType().isArray()) {
            map.put(key, Jval.newArray());
          } else if (cfg.getType().isEnum()) {
            map.put(key, Jval.valueOf(firstEnum((Class<? extends Enum<?>>) cfg.getType()).name()));
          }
        } else map.put(key, pack(obj));
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    StringWriter writer = new StringWriter();
    tree.writeTo(writer, Jval.Jformat.formatted);

    String str = writer.getBuffer().toString();
    BufferedReader r1 = new BufferedReader(new StringReader(str));
    BufferedReader r2 = new BufferedReader(new StringReader(lastContext));

    BufferedWriter write = new BufferedWriter(file.writer(false));

    String line;
    while ((line = r2.readLine()) != null) {
      int i;
      String after = "";
      if ((i = line.indexOf("//")) != -1) {
        if (line.substring(0, i).trim().isEmpty()) {
          write.write(line);
        } else after = line.substring(i);
      } else if (line.isEmpty()) {
        write.write("");
      }

      if (!line.isEmpty() && (i == -1 || !after.isEmpty())) write.write(r1.readLine());

      write.write(after);

      write.write(System.lineSeparator());
      write.flush();
    }
    write.close();
    r1.close();
    r2.close();
  }

  private static Jval pack(Object value) {
    Class<?> type = value.getClass();
    if (type == Integer.class) return Jval.valueOf((int) value);
    else if (type == Byte.class) return Jval.valueOf((byte) value);
    else if (type == Short.class) return Jval.valueOf((short) value);
    else if (type == Boolean.class) return Jval.valueOf((boolean) value);
    else if (type == Long.class) return Jval.valueOf((long) value);
    else if (type == Character.class) return Jval.valueOf((char) value);
    else if (type == Float.class) return Jval.valueOf((float) value);
    else if (type == Double.class) return Jval.valueOf((double) value);
    else if (CharSequence.class.isAssignableFrom(type)) return Jval.valueOf((String) value);
    else if (type.isArray()) return packArray(value);
    else if (type.isEnum()) return Jval.valueOf(((Enum<?>) value).name());
    else throw new RuntimeException("invalid type: " + type);
  }

  private static Jval packArray(Object array) {
    if (!array.getClass().isArray()) throw new RuntimeException("given object was not an array");

    int len = Array.getLength(array);
    Jval res = Jval.newArray();
    Jval.JsonArray arr = res.asArray();
    for (int i = 0; i < len; i++) {
      arr.add(pack(Array.get(array, i)));
    }

    return res;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <T> T warp(Class<T> type, String value) {
    if (type == int.class) return (T) Integer.valueOf(value);
    else if (type == byte.class) return (T) Byte.valueOf(value);
    else if (type == short.class) return (T) Short.valueOf(value);
    else if (type == boolean.class) return (T) Boolean.valueOf(value);
    else if (type == long.class) return (T) Long.valueOf(value);
    else if (type == char.class) return (T) Character.valueOf(value.charAt(0));
    else if (type == float.class) return (T) Float.valueOf(value);
    else if (type == double.class) return (T) Double.valueOf(value);
    else if (CharSequence.class.isAssignableFrom(type)) return (T) value;
    else if (type.isArray()) return toArray(type, value);
    else if (type.isEnum()) return (T) Enum.valueOf((Class) type, value);
    else throw new RuntimeException("invalid type: " + type);
  }

  @SuppressWarnings("unchecked")
  private static <T> T toArray(Class<T> type, String value) {
    if (!type.isArray()) throw new RuntimeException("class " + type + " was not an array");
    Jval.JsonArray a = Jval.read(value).asArray();
    Class<?> eleType = type.getComponentType();
    Object res = Array.newInstance(eleType, a.size);
    for (int i = 0; i < a.size; i++) {
      Array.set(res, i, warp(eleType, a.get(i).toString()));
    }

    return (T) res;
  }

  private static <T> T firstEnum(Class<T> type) {
    if (!type.isEnum()) throw new RuntimeException("class " + type + " was not an enum");
    return MethodHandler.invokeDefault(type, "values");
  }

  public void reset() {
    Sgl.configFile.copyTo(Sgl.configDirectory.child("mod_config.hjson.bak"));
    Sgl.configFile.delete();

    Log.info("[Singularity][INFO] mod config has been reset, old config file saved to file named \"mod_config.hjson.bak\"");
    load();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  private @interface Order {
    float value();
  }
}
