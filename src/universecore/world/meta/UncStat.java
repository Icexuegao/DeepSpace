package universecore.world.meta;

import arc.struct.Seq;
import ice.world.meta.IceStatCats;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import universecore.util.handler.FieldHandler;

public class UncStat{
  public static final Stat
      inputs = create("inputs", StatCat.crafting),
      maxStructureSize = create("maxStructureSize", IceStatCats.INSTANCE.get结构());

  private static Stat create(String name, StatCat cat){
    return create(name, Stat.all.size, cat);
  }

  private static Stat create(String name, int index, StatCat cat){
    Seq<Stat> all = Stat.all;
    Stat res = new Stat(name, cat);

    FieldHandler.setValueDefault(res, "id", index);
    all.insert(index, res);

    return res;
  }
}
