package ice.Alon.world.blocks.environment;

import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;

public class IceOreBlock extends OreBlock {


    public IceOreBlock(String name, Item ore) {
        super(name, ore);
    }

    @Override
    public void drawBase(Tile tile) {
      /*  if (draw) {
            Draw.rect(variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))], tile.worldx(), tile.worldy());
            Draw.z(Layer.floor + 1);
        }*/
        super.drawBase(tile);
    }
}
