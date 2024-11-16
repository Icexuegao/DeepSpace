package ice.Alon.world.blocks.liquids;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class LiquidClassifier extends Block {
    public TextureRegion top;
    public TextureRegion bottom;

    public LiquidClassifier(String name) {
        super(name);
        buildType = LiquidClassifierBuild::new;
        liquidCapacity = 10;
        hasLiquids = true;
        destructible = true;
        underBullets = true;
        update = true;
        instantTransfer = true;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        outputsLiquid = true;
        config(Liquid.class, (LiquidClassifier.LiquidClassifierBuild tile, Liquid liquid)->tile.sortLiquid = liquid);
        configClear((LiquidClassifier.LiquidClassifierBuild tile)->tile.sortLiquid = null);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.maxConsecutive);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, name + "-top2", true);
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
        public Liquid sortLiquid;

        @Override
        public void draw() {
            Draw.rect(bottom, x, y);
            if (sortLiquid != null) {
                LiquidBlock.drawTiledFrames(size, x, y, 0f, sortLiquid, 1f);
            }
            Draw.rect(top, x, y);
        }

        /** 复制蓝图会调用 */
        @Override
        public Liquid config() {
            return sortLiquid;
        }

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(block, table, content.liquids(), ()->sortLiquid, this::configure, selectionRows, selectionColumns);
            super.buildConfiguration(table);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == sortLiquid;
        }

        @Override
        public void updateTile() {
            if (sortLiquid != null) {
                dumpLiquid(sortLiquid);
            }
        }
        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortLiquid == null ? -1 : sortLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortLiquid = id == -1 ? null : content.liquid(id);
        }
    }
}
