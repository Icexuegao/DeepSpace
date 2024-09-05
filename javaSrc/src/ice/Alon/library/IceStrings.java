package ice.Alon.library;
/**字符串工具类*/
public class IceStrings {
    /**判断sting是否全是数字组成*/
    public static boolean isNumeric4(String str) {
        return str != null && str.chars().allMatch(Character::isDigit);
    }
}
