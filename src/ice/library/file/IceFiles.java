package ice.library.file;

import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import ice.Ice;
import mindustry.Vars;
import mindustry.mod.Mods;

import java.util.HashMap;

/**
 * 怪了 我什么时候开始有写注释的习惯qwq
 */
public class IceFiles {
    /** 所有的png集合 */
    private static final HashMap<String, TextureRegion> pngs = new HashMap<>();
    /**
     * 用于获取mod文件的Map合集
     */
    public static ObjectMap<String, Fi> fis = new ObjectMap<>();
    /**
     * 获取mod文件
     */
    public static Mods.LoadedMod mod = Vars.mods.getMod(Ice.class);

    static {
        recursion(mod.root);
        recursionPng(mod.root.child("sprites"));
    }

    /**
     * 存在同名文件时,请不要使用该方法 请使用
     * pathFind
     */
    public static Fi find(String fiName) {
        return find(fiName, false);
    }

    public static Fi find(String fiName, boolean not) {
        Fi fi = fis.get(fiName);
        if (fi == null && !not) {
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

    /** 获取贴图 这可能是有风险的 因为name可能重名! */
    public static TextureRegion findPng(String name) {
        return pngs.get(name + ".png");
    }

    public static void recursionPng(Fi root) {
        for (Fi fi : root.list()) {
            if (fi.isDirectory()) {
                recursionPng(fi);
            } else if (fi.extension().equals("png")) {
                pngs.put(fi.name(), new TextureRegion(new Texture(fi)));
            }
        }
    }
}
