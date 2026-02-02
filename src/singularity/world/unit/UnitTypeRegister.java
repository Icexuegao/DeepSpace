package singularity.world.unit;

import arc.struct.ObjectMap;
import mindustry.gen.EntityMapping;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import singularity.Sgl;
import singularity.contents.SglUnits;
import universecore.util.handler.MethodHandler;

import java.lang.reflect.Field;
import java.util.Arrays;

public class UnitTypeRegister {
  private static final ObjectMap<Class<? extends Unit>, Unit> classCache = new ObjectMap<>();

  public static void registerAll(){
    for (Field field : SglUnits.class.getFields()) {
      if (UnitType.class.isAssignableFrom(field.getType())){
        UnitEntityType anno = field.getAnnotation(UnitEntityType.class);
        if (anno != null){
          Class<? extends Unit> type = anno.value();

          int id = classCache.get(type, () -> MethodHandler.newInstanceDefault(type)).classId();

          if (EntityMapping.idMap.length <= id){
            EntityMapping.idMap = Arrays.copyOf(EntityMapping.idMap, EntityMapping.idMap.length*2);
          }

          if (EntityMapping.idMap[id] == null)
            EntityMapping.idMap[id] = () -> MethodHandler.newInstanceDefault(type);

          String name;
          EntityMapping.nameMap.put(name = Sgl.modName + "-" + field.getName(), EntityMapping.map(id));
          EntityMapping.customIdMap.put(id, name);
        }
      }
    }
  }
}
