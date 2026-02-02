package universecore.desktopcore.handler;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**反模块化工具， 仅提供了一个主要方法{@link Demodulator#makeModuleOpen(Module, Package, Module)}用于强制对需要的模块开放模块的软件包。
 * <p>此类行为可能完全打破模块化的访问保护，本身是不安全的，若不是必要情况，请尽量避免使用该类
 * <p><strong>此类仅在JDK9之后可用，避免在更早的版本引用此类的方法，且此类仅在desktop平台可用，安卓平台不可使用此类的任何行为</strong>
 *
 * @author EBwilson */
@SuppressWarnings({"unchecked"})
public class Demodulator {
  private static final long fieldFilterOffset = 112L;

  private static final Unsafe unsafe;

  private static final Field opensField;
  private static final Field exportField;

  private static final Method exportNative;

  static{
    try{
      Constructor<Unsafe> cstr = Unsafe.class.getDeclaredConstructor();
      cstr.setAccessible(true);
      unsafe = cstr.newInstance();

      ensureFieldOpen();

      opensField = Module.class.getDeclaredField("openPackages");
      exportField = Module.class.getDeclaredField("exportedPackages");

      makeModuleOpen(Module.class.getModule(), "java.lang", Demodulator.class.getModule());

      exportNative = Module.class.getDeclaredMethod("addExports0", Module.class, String.class, Module.class);
      exportNative.setAccessible(true);
      exportNative.invoke(null, Module.class.getModule(), "java.lang", Demodulator.class.getModule());
    }catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException |
           NoSuchFieldException e){
      throw new RuntimeException(e);
    }
  }

  public static void makeModuleOpen(Module from, Class<?> clazz, Module to){
    if (clazz.isArray()){
      makeModuleOpen(from, clazz.getComponentType(), to);
    }
    else makeModuleOpen(from, clazz.getPackage(), to);
  }

  public static void makeModuleOpen(Module from, Package pac, Module to){
    if(checkModuleOpen(from, pac, to)) return;

    makeModuleOpen(from, pac.getName(), to);
  }

  @SuppressWarnings("unchecked")
  public static void makeModuleOpen(Module from, String pac, Module to){
    try {
      if (exportNative != null) exportNative.invoke(null, from, pac, to);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    Map<String, Set<Module>> opensMap = (Map<String, Set<Module>>) unsafe.getObjectVolatile(from, unsafe.objectFieldOffset(opensField));
    if(opensMap == null){
      opensMap = new HashMap<>();
      unsafe.putObjectVolatile(from, unsafe.objectFieldOffset(opensField), opensMap);
    }

    Map<String, Set<Module>> exportsMap = (Map<String, Set<Module>>) unsafe.getObjectVolatile(from, unsafe.objectFieldOffset(exportField));
    if(exportsMap == null){
      exportsMap = new HashMap<>();
      unsafe.putObjectVolatile(from, unsafe.objectFieldOffset(exportField), exportsMap);
    }

    Set<Module> opens = opensMap.computeIfAbsent(pac, e -> new HashSet<>());
    Set<Module> exports = exportsMap.computeIfAbsent(pac, e -> new HashSet<>());

    try{
      opens.add(to);
    }catch(UnsupportedOperationException e){
      ArrayList<Module> lis = new ArrayList<>(opens);
      lis.add(to);
      opensMap.put(pac, new HashSet<>(lis));
    }

    try{
      exports.add(to);
    }catch(UnsupportedOperationException e){
      ArrayList<Module> lis = new ArrayList<>(exports);
      lis.add(to);
      exportsMap.put(pac, new HashSet<>(lis));
    }
  }

  public static boolean checkModuleOpen(Module from, Package pac, Module to){
    Objects.requireNonNull(from);
    Objects.requireNonNull(to);

    if(pac == null) return true;
    
    return from.isOpen(pac.getName(), to);
  }

  public static void ensureFieldOpen(){
    try{
      Class<?> clazz = Class.forName("jdk.internal.reflect.Reflection");
      Map<Class<?>, Set<String>> map = (Map<Class<?>, Set<String>>) unsafe.getObject(clazz, fieldFilterOffset);
      map.clear();
    }catch(ClassNotFoundException e){
      throw new RuntimeException(e);
    }
  }
}
