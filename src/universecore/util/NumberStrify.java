package universecore.util;

/**将数字按一定准则转化为字符串的实用工具集
 *
 * @since 1.5
 * @author EBwilson*/
public class NumberStrify{
  private static final String[] byteUnit = {
      "B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB"
  };

  /**将数字转换为计算机存储容量计数表示，但不带有单位
   *
   * @param number 要被转换的数字
   * @param retain 保留的小数位数*/
  @SuppressWarnings("StringRepeatCanBeUsed")
  public static String toByteFixNonUnit(double number, int retain){
    boolean isNegative = false;
    if(number < 0){
      number = -number;
      isNegative = true;
    }

    double base = 1;
    for(int i = 0; i<byteUnit.length; i++){
      if(base*1024 > number){
        break;
      }
      base *= 1024;
    }

    String[] arr = Double.toString(number/base).split("\\.");
    int realRetain = Math.min(retain, arr[1].length());

    StringBuilder end = new StringBuilder();
    for(int i = 0; i < retain - realRetain; i++){
      end.append("0");
    }

    return (isNegative? "-": "") + arr[0] + (retain == 0? "": "." + arr[1].substring(0, realRetain) + end);
  }

  /**将数字转换为计算机存储容量计数表示
   *
   * @param number 要被转换的数字
   * @param retain 保留的小数位数*/
  @SuppressWarnings("StringRepeatCanBeUsed")
  public static String toByteFix(double number, int retain){
    boolean isNegative = false;
    if(number < 0){
      number = -number;
      isNegative = true;
    }

    int index = 0;
    double base = 1;
    for(int i = 0; i<byteUnit.length; i++){
      if(base*1024 > number){
        break;
      }
      base *= 1024;
      index++;
    }

    String[] arr = Double.toString(number/base).split("\\.");
    int realRetain = Math.min(retain, arr[1].length());

    StringBuilder end = new StringBuilder();
    for(int i = 0; i < retain - realRetain; i++){
      end.append("0");
    }

    return (isNegative? "-": "") + arr[0] + (retain == 0? "": "." + arr[1].substring(0, realRetain) + end + byteUnit[index]);
  }
}
