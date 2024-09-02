package ice.Alon.world.blocks.liquids;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class LiquidClassifier extends Block {
    public TextureRegion top;
    public TextureRegion bottom;

    public LiquidClassifier(String name) {
        super(name);
        buildType = LiquidClassifierBuild::new;
        liquidCapacity = 10;
        hasLiquids = true;
        update = true;
        destructible = true;
        underBullets = true;
        instantTransfer = true;
        group = BlockGroup.liquids;
        configurable = true;
        unloadable = false;
        saveConfig = true;
        clearOnDoubleTap = true;
        outputsLiquid = true;
        config(Liquid.class, (LiquidClassifier.LiquidClassifierBuild tile, Liquid item) -> tile.sortItem = item);
        configClear((LiquidClassifier.LiquidClassifierBuild tile) -> tile.sortItem = null);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.maxConsecutive);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, "center", true);
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{bottom, top};
    }

    @Override
    public void load() {
        top = Core.atlas.find(name + "-top");
        bottom = Core.atlas.find(name + "-bottom");
        super.load();
    }

    public class LiquidClassifierBuild extends Building {
        Liquid sortItem;

        @Override
        public void draw() {
            if (sortItem == null) {
                Draw.rect(bottom, x, y);
                Draw.rect(top, x, y);
            } else {
                Draw.rect(bottom, x, y);
                Draw.color(sortItem.color);
                Fill.square(x, y, tilesize / 2f - 0.00001f);
                Draw.color();
                if (Core.settings.getBool("arcchoiceuiIcon")) Draw.rect(sortItem.uiIcon, x, y, 4f, 4f);
                Draw.rect(top, x, y);
            }
        }

        @Override
        public void configured(Unit player, Object value) {
            super.configured(player, value);
            if (!headless) {
                renderer.minimap.update(tile);
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(block, table, content.liquids(), () -> sortItem, this::configure, selectionRows, selectionColumns);
            super.buildConfiguration(table);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == sortItem;
        }

        @Override
        public void update() {
            if (sortItem != null) {
                dumpLiquid(sortItem);
            }
            super.update();
        }

    }
}
