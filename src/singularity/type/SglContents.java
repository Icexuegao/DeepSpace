package singularity.type;

import arc.struct.Seq;
import mindustry.ctype.ContentType;
import singularity.world.blocks.research.ResearchDevice;

import static mindustry.Vars.content;

public class SglContents{
  public static final ContentType structure = SglContentType.structure.value;
  public static final ContentType researchDevice = SglContentType.researchDevice.value;

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
