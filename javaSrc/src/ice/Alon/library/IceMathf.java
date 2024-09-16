package ice.Alon.library;

/**
 * 自己用的Mod数学库
 */
public class IceMathf {
    /**
     * 判断x是否>=1,并执行lambda方法
     */
    public static boolean goe1(float x, IceFunctionalInterface mode) {
        if (x >= 1) {
            mode.mode();
            return true;
        }
        return false;
    }

    /**
     * 判断x是否>=1
     */
    public static boolean goe1(float x) {
        return x >= 1;
    }

    /**
     * 60
     */
    public static boolean goe60(float x, IceFunctionalInterface mode) {
        if (x >= 60) {
            mode.mode();
            return true;
        }
        return false;
    }
    public static boolean goexy(float x,float y, IceFunctionalInterface mode) {
        if (x >= y) {
            mode.mode();
            return true;
        }
        return false;
    }
}
