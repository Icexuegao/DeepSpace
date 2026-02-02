package singularity.recipes;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import org.jetbrains.annotations.NotNull;
import singularity.graphic.SglDrawConst;
import tmi.recipe.types.SingleItemMark;

public class EnergyMarker extends SingleItemMark {
  public static final EnergyMarker INSTANCE = new EnergyMarker();

  private EnergyMarker(){
    super("energy-marker");
  }

  @NotNull
  @Override
  public String getLocalizedName() {
    return Core.bundle.get("category.neutron");
  }

  @NotNull
  @Override
  public TextureRegion getIcon() {
    return ((TextureRegionDrawable) SglDrawConst.nuclearIcon).getRegion();
  }
}
