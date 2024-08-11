package Ice.asundry.ContentParser;

import Ice.type.IceItem;
import arc.struct.ObjectMap;
import mindustry.mod.ClassMap;

/**
 * 生成的类,将简单类名映射到具体类。用于JSON模块。
 */
public class IceClassMap {
    public static final ObjectMap<String, Class<?>> classes = ClassMap.classes;

    static {
        classes.put("IceItem", IceItem.class);
    }
}
