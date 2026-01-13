package singularity.type;

import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import singularity.world.blocks.research.ResearchDevice;
import singularity.world.blocks.structure.BlockStructure;
import universecore.util.UncContentType;

public class SglContentType extends UncContentType{
  public static SglContentType ability, structure, atomSchematic, researchDevice;

  public static SglContentType[] allSglContentType;
  
  public SglContentType(String name, int ordinal, Class<? extends Content> contentClass, boolean display){
    super(name, ordinal, contentClass, display);
  }

  public SglContentType(String name, int ordinal, Class<? extends Content> contentClass){
    super(name, ordinal, contentClass);
  }
  
  public SglContentType(String name, Class<? extends Content> contentClass, boolean display){
    super(name, ContentType.values().length, contentClass, display);
  }

  public SglContentType(String name, Class<? extends Content> contentClass){
    super(name, contentClass);
  }
  
  public static void load(){
    ability = new SglContentType("ability", PlayerAbility.class);
    structure = new SglContentType("structure", BlockStructure.class);
    atomSchematic = new SglContentType("atomSchematic", AtomSchematic.class, false);
    researchDevice = new SglContentType("researchDevice", ResearchDevice.class);

    allSglContentType = new SglContentType[]{ability, structure, atomSchematic, researchDevice};
  }
}
