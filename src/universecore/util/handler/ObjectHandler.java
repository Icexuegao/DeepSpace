package universecore.util.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/** 对对象的一些实用工具集，用于将对象的字段属性拷贝到另一个 */
public class ObjectHandler {
  public static Consumer<Throwable> exceptionHandler = e -> {
  };

  /**
   * 将来源对象的所有属性值完整的复制到目标对象，目标对象必须是来源对象的类型或者子类
   * @param source 属性的源对象
   * @param target 复制属性到的目标对象
   * @throws IllegalArgumentException 如果目标对象不分配自来源类
   */
  public static <S, T extends S> void copyField(S source, T target) {
    copyFieldAsBlack(source, target);
  }

  /**
   * 将来源对象的不在黑名单中的属性的值复制到目标对象，目标对象必须是来源对象的类型或者子类
   * @param source 属性的源对象
   * @param target 复制属性到的目标对象
   * @param blackList 字段黑名单
   */
  public static <S, T extends S> void copyFieldAsBlack(S source, T target, String... blackList) {
    Class<?> curr = source.getClass();
    Set<String> black = new HashSet<>(Arrays.asList(blackList));
    Set<String> fields = new HashSet<>();

    while (curr != Object.class) {
      for (Field field : curr.getDeclaredFields()) {
        if (Modifier.isStatic(field.getModifiers())) continue;

        fields.add(field.getName());
      }

      curr = curr.getSuperclass();
    }

    copyFieldAsWhite(source, target, fields.stream().filter(e -> !black.contains(e)).toArray(String[]::new));
  }

  /**
   * 将来源对象的指定属性值复制到目标对象，目标对象必须是来源对象的类型或者子类
   * @param source 属性的源对象
   * @param target 复制属性到的目标对象
   * @param whiteList 字段白名单
   */
  public static <S, T extends S> void copyFieldAsWhite(S source, T target, String... whiteList) {
    for (String s : whiteList) {
      try {
        FieldHandler.setValueDefault(target, s, FieldHandler.getValueDefault(source, s));
      } catch (Throwable e) {
        exceptionHandler.accept(e);
      }
    }
  }
}
