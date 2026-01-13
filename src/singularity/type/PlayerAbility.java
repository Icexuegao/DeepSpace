package singularity.type;

import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;

public class PlayerAbility extends UnlockableContent {
  public PlayerAbility(String name) {
    super(name);
  }

  @Override
  public ContentType getContentType() {
    return SglContentType.ability.value;
  }
}
