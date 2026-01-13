package singularity.world.blocks.research;

import arc.struct.Seq;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import singularity.type.SglContents;

public class ResearchDevice extends UnlockableContent {
  public final Seq<ResearchDevice> compatibles = new Seq<>();

  public int cost = 1; // 1 cost by 4 size
  public int provTechPoints = 0;

  public ResearchDevice(String name) {
    super(name);
  }

  public boolean isCompatible(ResearchDevice other) {
    return other == this || compatibles.contains(e -> e == other || e.isCompatible(other));
  }

  public void setCompatibles(ResearchDevice... devices){
    compatibles.addAll(devices);
  }

  @Override
  public ContentType getContentType() {
    return SglContents.researchDevice;
  }
}
