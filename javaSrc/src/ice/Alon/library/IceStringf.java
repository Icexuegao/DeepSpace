package ice.Alon.library;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class IceStringf {
    /**
     * 判断sting是否全是数字组成
     */
    public static boolean isNumeric4(String str) {
        return str != null && str.chars().allMatch(Character::isDigit);
    }

    /**
     * 判断sting是否是数字和.组成
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        if (str.indexOf(".") > 0) {//判断是否有小数点
            if (str.indexOf(".") == str.lastIndexOf(".") && str.split("\\.").length == 2) { //判断是否只有一个小数点
                return pattern.matcher(str.replace(".", "")).matches();
            } else {
                return false;
            }
        } else {
            return pattern.matcher(str).matches();
        }
    }
}
