package singularity.contents;

import arc.func.Boolf3;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import singularity.world.blocks.liquid.ClusterConduit;
import singularity.world.blocks.liquid.ClusterValve;
import singularity.world.blocks.liquid.ConduitRiveting;
import singularity.world.blocks.liquid.LiquidUnloader;

public class LiquidBlocks implements ContentList {
  /**集束管道*/
  public static Block cluster_conduit,
  /**管道铆*/
  conduit_riveting,
  /**过滤阀*/
  filter_valve,
  /**液体提取器*/
  liquid_unloader;
  
  @Override
  public void load() {
    cluster_conduit = new ClusterConduit("cluster_conduit"){{
      requirements(Category.liquid, ItemStack.with(Items.titanium, 8, SglItems.aerogel, 10, SglItems.aluminium, 10));
      liquidCapacity = 10;
      liquidPressure = 1.05f;
      health = 360;
    }};
    
    conduit_riveting = new FakeBlock(new ConduitRiveting("conduit_riveting"){{
      requirements(Category.liquid, ItemStack.with(Items.plastanium, 18, SglItems.aerogel, 10, SglItems.aluminium, 16));
      liquidCapacity = 10;
      health = 300;
    }}, (tile, team, rotation) -> tile.build instanceof ClusterConduit.ClusterConduitBuild b && b.rotation == rotation);

    filter_valve = new FakeBlock(new ClusterValve("filter_valve"){{
      requirements(Category.liquid, ItemStack.with(Items.titanium, 10, SglItems.aerogel, 15, Items.graphite, 12));
      liquidCapacity = 10;
      health = 300;
    }}, (tile, team, rotation) -> tile.build instanceof ClusterConduit.ClusterConduitBuild b && b.rotation == rotation);
    
    liquid_unloader = new LiquidUnloader("liquid_unloader"){{
      requirements(Category.liquid, ItemStack.with(Items.silicon, 20, SglItems.aluminium, 25, Items.graphite, 15));
      size = 1;
    }};
  }

  static class FakeBlock extends universecore.world.blocks.FakeBlock{
    public FakeBlock(Block maskedBlock, Boolf3<Tile, Team, Integer> placeValid) {
      super(maskedBlock, placeValid);
      maskedBlock.alwaysUnlocked = true;
    }
  }
}
