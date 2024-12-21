package ice.parse;

import arc.struct.ObjectMap;
import ice.type.content.IceUnitType;
import mindustry.mod.ClassMap;

public class IceClassMap {
    public static void load() {
    }

    public final static ObjectMap<String, Class<?>> classmap = ClassMap.classes;

    static {
        classmap.put("IceUnitType", IceUnitType.class);
    }
}
