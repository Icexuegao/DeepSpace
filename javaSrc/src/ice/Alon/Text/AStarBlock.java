package ice.Alon.Text;

import arc.scene.ui.layout.Table;
import arc.util.Log;
import ice.Alon.library.PathfindAlgorithm.AStarPathFind;
import ice.Alon.library.drawUpdate.DrawUpdates;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;

public class AStarBlock extends Block {
    public AStarBlock(String name) {
        super(name);
        itemCapacity = 10;
        health = 100;
        size = 1;
        buildType = AStarBlockBuild::new;
        update = true;
        requirements(Category.effect, ItemStack.with(Items.copper, 10));
        configurable = true;
    }

    public class AStarBlockBuild extends Building {
        AStarPathFind ap;
        @Override
        public void remove() {
            ap.kill();
            super.remove();
        }

        @Override
        public void buildConfiguration(Table table) {
            table.button("a",()->{
                ap.kill();
                ap =new AStarPathFind(tile, Vars.player.core().tile);
                ap.youhua=false;
                DrawUpdates.Companion.getUpdateSeq().add(ap);
            });
            super.buildConfiguration(table);
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
          ap = new AStarPathFind(tile, Vars.player.core().tile);
            DrawUpdates.Companion.getUpdateSeq().add(ap);
            return super.init(tile, team, shouldAdd, rotation);
        }
    }
}
