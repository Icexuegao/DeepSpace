package singularity.type;

import arc.struct.Seq;
import mindustry.ctype.ContentType;
import singularity.world.blocks.research.ResearchDevice;

import static mindustry.Vars.content;

public class SglContents{
  public static final ContentType structure = SglContentType.structure.value;
  public static final ContentType atomSchematic = SglContentType.atomSchematic.value;
  public static final ContentType researchDevice = SglContentType.researchDevice.value;

  public static Seq<AtomSchematic> atomSchematics(){
    return content.getBy(atomSchematic);
  }

  public static AtomSchematic atomSchematic(int id){
    return content.getByID(atomSchematic, id);
  }

  public static AtomSchematic atomSchematic(String name){
    return content.getByName(atomSchematic, name);
  }

  public static Seq<ResearchDevice> researchDevices(){
    return content.getBy(researchDevice);
  }

  public static ResearchDevice researchDevice(int id) {
    return content.getByID(researchDevice, id);
  }

  public static ResearchDevice researchDevice(String name) {
    return content.getByName(researchDevice, name);
  }
}
