package singularity.world.blocks.structure;

import arc.math.geom.Point2;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import singularity.type.SglContents;
import singularity.world.components.StructBlockComp;
import singularity.world.components.StructCoreBuildComp;
import singularity.world.components.StructCoreComp;
import universecore.components.blockcomp.ChainsBlockComp;
import universecore.components.blockcomp.ChainsBuildComp;

public class BlockStructure extends UnlockableContent{
  private final IntMap<StructBlockComp> structure = new IntMap<>();
  private final ObjectSet<StructBlockComp> structBlocks = new ObjectSet<>();

  private final ObjectMap<StructBlockComp, Integer> anyWhere = new ObjectMap<>();

  private final StructCoreComp core;
  
  boolean initialized;

  /**从一个json读取一个多方块结构，并将这个结构分配给相应的结构核心
   * <p>一个标准的结构json格式如下：
   * <pre>{@code
   * {
   *   name: <structName>,
   *   core: <coreBlockName>,
   *   anyWhere:[
   *     {block: <blockName>, require: <minAmount>},
   *     {block: <blockName>, require: <minAmount>},
   *     ......
   *   ],
   *   struct:[
   *     {block: <blockName>, offsetX: <dx>, offsetY: <dy>},
   *     {block: <blockName>, offsetX: <dx>, offsetY: <dy>},
   *     ......
   *   ]
   * }
   * }</pre>*/
  public static BlockStructure parseJson(String json){
    Jval struct = Jval.read(json);

    String name = struct.getString("name");
    String core = struct.getString("core");
    StructCoreComp coreBlock = (StructCoreComp) Vars.content.block(core);
    BlockStructure res = new BlockStructure(name, coreBlock);

    for (Jval block : struct.get("anyWhere").asArray()) {
      StructBlockComp c = (StructBlockComp) Vars.content.block(block.getString("name"));
      res.structBlocks.add(c);
      res.anyWhere.put(
          c,
          block.getInt("require", 0)
      );
    }
    for (Jval block : struct.get("struct").asArray()) {
      StructBlockComp c = (StructBlockComp) Vars.content.block(block.getString("name"));
      res.structBlocks.add(c);
      res.structure.put(
          Point2.pack(block.getInt("offsetX", 0), block.getInt("offsetY", 0)),
          c
      );
    }

    return res;
  }

  public BlockStructure(String name, StructCoreComp core){
    super(name);
    this.core = core;
    core.addStruct(this);
    initialized = false;
  }

  public BlockStructure(String name, StructCoreComp core, IntMap<StructBlockComp> structure, ObjectMap<StructBlockComp, Integer> anyWhere){
    super(name);
    this.core = core;
    this.structure.putAll(structure);
    this.anyWhere.putAll(anyWhere);
    this.structBlocks.clear();
    for (StructBlockComp block : this.structure.values()) {
      structBlocks.add(block);
    }
    for (StructBlockComp blockComp : this.anyWhere.keys()) {
      structBlocks.add(blockComp);
    }
    core.addStruct(this);
    initialized = true;
  }
  
  @Override
  public ContentType getContentType(){
    return SglContents.structure;
  }
  
  public boolean match(StructCoreBuildComp core){
    if(core.getStructCore() != this.core) return false;

    for(ChainsBuildComp comp: core.getChains().container.all){
      ChainsBlockComp block;
      if((block = structure.get(comp.tileOn().pos())) != null){
        if(block != core.getChainsBlock()) return false;
      }
    }

    return true;
  }

  public boolean structAccept(StructBlockComp block){
    return structBlocks.contains(block);
  }

  public boolean accept(StructBlockComp block){
    return structAccept(block) || anyWhere.containsKey(block);
  }
}
