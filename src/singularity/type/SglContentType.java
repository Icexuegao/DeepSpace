package singularity.type;

import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import singularity.world.blocks.structure.BlockStructure;
import universecore.util.UncContentType;

public class SglContentType extends UncContentType{
  public static SglContentType  structure;

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
    structure = new SglContentType("structure", BlockStructure.class);

    allSglContentType = new SglContentType[]{ structure};
  }
}
