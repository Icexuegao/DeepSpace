package ice.library.file;

import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.util.Log;

import java.util.HashMap;

import static ice.Ice.ice;

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

    static {
        recursion(ice.root);
        recursionPng(ice.root.child("sprites"));
    }

    public static Fi find(String fiName, boolean... not) {
        Fi fi = fis.get(fiName);
        if (fi == null && !not[0]) Log.warn("类:[@],索引文件:[@]为空!!!", IceFiles.class.getName(), fiName);
        return fi;
    }

    /**
     * 从模组目录开始获取文件 支持转义字符串 举例"sprites/xxx/xx.fi" 和"xxx\\xxx\\xx"
     */
    public static Fi pathFind(String pathFiName) {
        String[] split = pathFiName.contains("\\") ? pathFiName.split("\\\\") : pathFiName.split("/");
        Fi child = ice.root.child(split[0]);
        for (int i = 1; i < split.length; i++) {
            child = child.child(split[i]);
        }
        return child;
    }

    /** 获取贴图 这可能是有风险的 因为name可能重名! */
    public static TextureRegion findPng(String name) {
        return pngs.get(name + ".png");
    }

    /**
     * 递归遍历所有文件 仅限内部使用
     * {list} 爽
     */
    private static void recursion(Fi root) {
        for (Fi fi : root.list()) {
            if (fi.isDirectory()) {
                fis.put(fi.name(), fi);
                recursion(fi);
            } else {
                fis.put(fi.name(), fi);
            }
        }
    }

    /** 仅限内部使用 */
    private static void recursionPng(Fi root) {
        for (Fi fi : root.list()) {
            if (fi.isDirectory()) {
                recursionPng(fi);
            } else if (fi.extension().equals("png")) {
                pngs.put(fi.name(), new TextureRegion(new Texture(fi)));
            }
        }
    }
}
