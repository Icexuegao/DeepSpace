package singularity.world.meta;

import mindustry.world.meta.BlockGroup;
import universecore.util.handler.EnumHandler;

public class SglBlockGroup{
  private static final EnumHandler<BlockGroup> handler = new EnumHandler<>(BlockGroup.class);
  
  public static BlockGroup nuclear = handler.addEnumItemTail("nuclear", true);
}
