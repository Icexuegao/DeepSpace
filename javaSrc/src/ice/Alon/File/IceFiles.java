package ice.Alon.File;

import arc.files.Fi;
import arc.struct.ObjectMap;
import ice.Ice;
import mindustry.Vars;
import mindustry.mod.Mods;

/**
 * 怪了 我什么时候开始有写注释的习惯qwq
 */
public class IceFiles {
    /**
     * 用于获取mod文件的Map合集
     */
    public static ObjectMap<String, Fi> fis = new ObjectMap<>();
    /**
     * 获取mod文件
     */
    public static Mods.LoadedMod mod = Vars.mods.getMod(Ice.class);
    /**
     * 每次加载只遍历一次!!!
     */
    static boolean init = false;

    /**
     * 存在同名文件时,请不要使用该方法 请使用
     * pathFind
     */
    public static Fi find(String fiName) {
        init();
        Fi fi = fis.get(fiName);
        if (fi == null) {
            throw new NullPointerException("类:[" + IceFiles.class.getName() + "],索引文件:[" + fiName + "]为空!!!");
        }
        return fi;
    }

    /**
     * 从模组目录开始获取文件 支持转义字符串 举例"sprites/xxx/" 和"xxx\\xxx\\xx"
     */
    public static Fi pathFind(String pathFiName) {
        boolean contains = pathFiName.contains("\\");
        String[] split;
        if (contains) {
            split = pathFiName.split("\\\\");
        } else {
            split = pathFiName.split("/");
        }

        Fi child = mod.root.child(split[0]);
        for (int i = 1; i < split.length; i++) {
            child = child.child(split[i]);
        }
        return child;
    }

    /**
     * 其实我在考虑要不要放到主类里面 只加载一次 这样每次索引文件就不用判断了
     */
    public static void init() {
        if (!init) {
            recursion(mod.root);
            init = true;
        }
    }

    /**
     * 递归遍历所有文件
     * {list} 爽
     */
    public static void recursion(Fi root) {
        for (Fi fi : root.list()) {
            if (fi.isDirectory()) {
                fis.put(fi.name(), fi);
                recursion(fi);
            } else {
                fis.put(fi.name(), fi);
            }
        }
    }
}
