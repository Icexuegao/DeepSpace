package ice.world.blocks.liquid;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.meta.Stat;

import static ice.library.drawf.IceDraw.*;
import static mindustry.Vars.content;

public class LiquidClassifier extends Block {
    public TextureRegion top = LoadText(name + "-top");
    public TextureRegion bottom = LoadText(name + "-bottom");

    public LiquidClassifier(String name) {
        super(name);
        buildType = LiquidClassifierBuild::new;
        hasLiquids = true;
        liquidCapacity=0f;
        destructible = true;
        underBullets = true;
        update = true;
        instantTransfer = true;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        outputsLiquid = true;
        config(Liquid.class, (LiquidClassifier.LiquidClassifierBuild tile, Liquid liquid) -> tile.sortLiquid = liquid);
        configClear((LiquidClassifier.LiquidClassifierBuild tile) -> tile.sortLiquid = null);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.maxConsecutive);
        stats.remove(Stat.liquidCapacity);
    }

    @Override
    public void setBars() {
        addBar("health", entity -> new Bar("stat.health", Pal.health, entity::healthf).blink(Color.white));
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, name + "-top2");
    }

    @Override
    public void drawPlanConfigCenter(BuildPlan plan, Object content, String region) {
        if (content instanceof Liquid l) {
            Draw.color(l.color);
            Draw.rect(region, plan.drawx(), plan.drawy());
            Draw.color();
            Draw.rect(top, plan.drawx(), plan.drawy());
        }
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
            ItemSelection.buildTable(block, table, content.liquids(), () -> sortLiquid, this::configure, selectionRows, selectionColumns);
            super.buildConfiguration(table);
        }

        public MultipleLiquidBlock.MultipleBlockBuild build;

        @Override
        public void updateTile() {
            if (sortLiquid != null) {
                proximity.forEach(l -> {
                    if (l instanceof MultipleLiquidBlock.MultipleBlockBuild) {
                        ((MultipleLiquidBlock.MultipleBlockBuild) l).liquid = sortLiquid;
                        build = (MultipleLiquidBlock.MultipleBlockBuild) l;
                    } else {
                        if (build != null) {
                            build.transferLiquid(l, l.block.liquidCapacity, sortLiquid);
                        }
                    }
                });
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(sortLiquid == null ? -1 : sortLiquid.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int id = read.i();
            sortLiquid = id == -1 ? null : content.liquid(id);
        }
    }
}
