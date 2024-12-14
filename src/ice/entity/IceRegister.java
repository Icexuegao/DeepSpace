package ice.entity;

import arc.func.Prov;
import arc.struct.ObjectIntMap;
import arc.struct.ObjectMap;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;

@SuppressWarnings("unchecked")
public final class IceRegister {
    private static final ObjectIntMap<Class<? extends Entityc>> ids = new ObjectIntMap<>();
    public static final ObjectMap<String, Prov<? extends Entityc>> map = new ObjectMap<>();

    public static <T extends Entityc> void put(String name, Class<T> type, Prov<? extends T> prov) {
        map.put(name, prov);
        ids.put(type, EntityMapping.register(name, prov));
    }

    public static int getId(Class<? extends Entityc> type) {
        return ids.get(type, -1);
    }

    public static Prov<Unit> getUnit(String name) {
        return (Prov<Unit>) map.get(name);
    }

    static {
      /*  put("饕餮-body", 蛇LegsUnit.class, 蛇LegsUnit::new);
        put("饕餮-end", 蛇LegsUnit.class, 蛇LegsUnit::new);
        put("饕餮", 蛇.蛇headLegsUnit.class, 蛇.蛇headLegsUnit::new);*/
    }
}
