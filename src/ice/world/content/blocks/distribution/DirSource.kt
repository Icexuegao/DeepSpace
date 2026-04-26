package ice.world.content.blocks.distribution;

import arc.func.*;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import ice.graphics.IStyles;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.*;
import singularity.world.blocks.SglBlock;
import singularity.world.blocks.turrets.SglTurret;
import universecore.world.consumers.*;

import java.util.Objects;

import static mindustry.Vars.content;
import static mindustry.type.ItemStack.with;

public class DirSource extends Block {
    public DrawBlock drawer;

    public DirSource(String name) {
        super(name);
        hasPower = true;
        outputsPower = true;
        consumesPower = false;
        conductivePower = true;
        rotateDraw = false;
        update = true;
        solid = true;
        canOverdrive = false;
        group = BlockGroup.transportation;
        schematicPriority = -9;
        selectionRows = 5;
        selectionColumns = 6;
        noUpdateDisabled = true;
        envEnabled = Env.any;
        placeableLiquid = true;
        alwaysUnlocked = true;
        rotate = true;
        regionRotated1 = 1;
        configurable = true;
        saveConfig = true;
        clearOnDoubleTap = true;
        quickRotate = false;
        drawer = new DrawMulti(
                new DrawDefault(),
                new DrawHeatOutput()
        );
        requirements(Category.distribution, BuildVisibility.sandboxOnly, with());
        config(int[].class, (DirSourceBuild tile, int[] index) -> {
            tile.index[0] = index[0];
            tile.index[1] = index[1];
            if (tile.target != null) {
                tile.target.items.clear();
                tile.target.liquids.clear();
            }
        });
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(new Stat("config", StatCat.function), table -> {
            table.row();
            table.table(Styles.grayPanel, frame -> {
                Image image = new Image(region);
                frame.add(image).tooltip(localizedName, true).size(40f).pad(12f).left().center();
                frame.table(label -> {
                    label.label(() -> localizedName).color(Pal.stat).growX().left().row();
                    label.label(() -> "May we love each other in the future\njust like we do now.").growX().left().row();
                }).growX().pad(12f).padLeft(0f).row();
            }).growX().pad(5f).row();
        });
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    public class DirSourceBuild extends Building implements HeatBlock {
        public Building target = null;
        public Seq<Seq<UnlockableContent>> consumes = new Seq<>();
        public Seq<Seq<UnlockableContent>> optionalCons = new Seq<>();
        public int[] index = new int[]{-1, -1};
        private final float uiSize = 40f;

        public void setCons(int v) {
            index[0] = v;
        }

        public int getCons() {
            return index[0];
        }

        public void setOpCons(int v) {
            index[1] = v;
        }

        public int getOpCons() {
            return index[1];
        }

        @Override
        public void drawSelect() {
            if (target != null)
                drawItemSelection(target.block);
        }

        @Override
        public void draw() {
            drawer.draw(this);
        }

        private void baseConsumersUnpack(Seq<Seq<UnlockableContent>> container, Seq<BaseConsumers> baseConsumers) {
            for (var consumers : baseConsumers) {
                Seq<UnlockableContent> row = new Seq<>();
                var consumeItemBase = consumers.get(ConsumeType.Companion.getItem());
                if (consumeItemBase != null)
                    for (var v : Objects.requireNonNull(consumeItemBase.getConsItems()))
                        row.add(v.item);
                var consumeLiquidBase = consumers.get(ConsumeType.Companion.getLiquid());
                if (consumeLiquidBase != null)
                    for (var v : Objects.requireNonNull(consumeLiquidBase.getConsLiquids()))
                        row.add(v.liquid);
                if (row.any())
                    container.add(row);
            }
        }

        private void configTable(Table table, Seq<Seq<UnlockableContent>> container, Prov<Integer> holder, Cons<Integer> consumer) {
            table.table(t -> {
                t.left().defaults().size(uiSize);
                ButtonGroup<ImageButton> group = new ButtonGroup<>();
                group.setMinCheckCount(0);
                for (int i = 0; i < container.size; ++i) {
                    final int idx = i;
                    var v = container.get(i).get(0);
                    ImageButton button = t.button(Tex.whiteui, Styles.clearNoneTogglei, 32f, () -> {
                    }).tooltip(v.localizedName, true).group(group).get();
                    button.changed(() -> {
                        consumer.get(button.isChecked() ? idx : -1);
                        configure(index);
                    });
                    button.getStyle().imageUp = new TextureRegionDrawable(v.uiIcon);
                    button.update(() -> button.setChecked(holder.get() == idx));
                }
            }).growX().left().row();
        }

        @Override
        public void onProximityUpdate() {
            target = front();
            consumes = new Seq<>();
            optionalCons = new Seq<>();
            if (target instanceof SglBlock.SglBuilding sglBuilding) {
                SglBlock sglBlock = (SglBlock) sglBuilding.block;
                baseConsumersUnpack(consumes, sglBlock.getConsumers());
                if (target instanceof SglTurret.SglTurretBuild) {
                    var baseConsumers = sglBlock.getOptionalCons();
                    if (baseConsumers.size > 0)
                        for (var v : Objects.requireNonNull(Objects.requireNonNull(baseConsumers.get(0).get(ConsumeType.Companion.getLiquid())).getConsLiquids()))
                            optionalCons.add(Seq.with(v.liquid));
                } else
                    baseConsumersUnpack(optionalCons, sglBlock.getOptionalCons());
            } else if (target != null) {
                for (var v : content.items())
                    if (target.block.consumesItem(v))
                        consumes.add(Seq.with(v));
                for (var v : content.liquids())
                    if (target.block.consumesLiquid(v))
                        optionalCons.add(Seq.with(v));
            } else {
                setCons(-1);
                setOpCons(-1);
            }
        }

        private void consUpdate(Seq<Seq<UnlockableContent>> container, Prov<Integer> holder) {
            if (holder.get() >= 0 && holder.get() < container.size)
                for (var v : container.get(holder.get()))
                    if (v instanceof Item item && target.acceptItem(this, item))
                        target.handleItem(this, item);
                    else if (v instanceof Liquid liquid && target.acceptLiquid(this, liquid))
                        target.handleLiquid(this, liquid, target.block.liquidCapacity - target.liquids.get(liquid));
        }

        @Override
        public void updateTile() {
            if (target instanceof SglBlock.SglBuilding sglBuilding) {
                SglBlock sglBlock = (SglBlock) sglBuilding.block;
                var energy = sglBuilding.getEnergyModule();
                energy.handle(sglBlock.getEnergyCapacity() - energy.getEnergy());
            } else if (target != null && !(target instanceof BaseTurret.BaseTurretBuild)) {
                // 两个大类共用容器还分别采用不同的方法, 这是极难维护的...有机会我会回来优化的, 至少需要一个新的思路.
                for (var row : consumes)
                    for (var v : row)
                        if (v instanceof Item item && target.acceptItem(this, item))
                            target.handleItem(this, item);
                        else if (v instanceof Liquid liquid && target.acceptLiquid(this, liquid))
                            target.handleLiquid(this, liquid, target.block.liquidCapacity - target.liquids.get(liquid));
                for (var row : optionalCons)
                    for (var v : row)
                        if (v instanceof Item item && target.acceptItem(this, item))
                            target.handleItem(this, item);
                        else if (v instanceof Liquid liquid && target.acceptLiquid(this, liquid))
                            target.handleLiquid(this, liquid, target.block.liquidCapacity - target.liquids.get(liquid));
            }
            if (target != null) {
                consUpdate(consumes, this::getCons);
                consUpdate(optionalCons, this::getOpCons);
            }
        }

        @Override
        public void buildConfiguration(Table table) {
            table.table(t -> t.background(Tex.paneLeft).image(Icon.download).size(uiSize).tooltip("tip", true).center()).size(uiSize).top();
            table.table(frame -> {
                frame.background(IStyles.INSTANCE.getPaneLeft());
                if (consumes.any() || optionalCons.any()) {
                    if (target instanceof SglBlock.SglBuilding) {
                        if (consumes.any()) {
                            ButtonGroup<Button> group = new ButtonGroup<>();
                            group.setMinCheckCount(0);
                            for (int i = 0; i < consumes.size; ++i) {
                                final int idx = i;
                                Button button = frame.button(t -> {
                                    t.left();
                                    for (var item : consumes.get(idx))
                                        t.image(item.uiIcon).maxSize(32f).tooltip(item.localizedName, true);
                                }, Styles.clearNoneTogglei, () -> {
                                }).minSize(uiSize).group(group).growX().get();
                                button.changed(() -> {
                                    setCons(button.isChecked() ? idx : -1);
                                    configure(new int[]{getCons(), getOpCons()});
                                });
                                button.update(() -> button.setChecked(getCons() == idx));
                                frame.row();
                            }
                        }
                        if (optionalCons.any()) {
                            configTable(frame, optionalCons, this::getOpCons, this::setOpCons);
//                            for (var row : optionalCons)
//                                for (var v : row)
//                                    frame.image(v.uiIcon).maxSize(32f).tooltip(v.localizedName, true);
                        }
                    } else if (target instanceof BaseTurret.BaseTurretBuild) {
                        configTable(frame, consumes, this::getCons, this::setCons);
                        configTable(frame, optionalCons, this::getOpCons, this::setOpCons);
                    } else if (target != null) {
                        if (consumes.any())
                            for (var row : consumes)
                                for (var v : row)
                                    frame.image(v.uiIcon).maxSize(32f).tooltip(v.localizedName, true);
                        if (optionalCons.any())
                            for (var row : optionalCons)
                                for (var v : row)
                                    frame.image(v.uiIcon).maxSize(32f).tooltip(v.localizedName, true);
                    }
                } else frame.image(Icon.cancel).size(uiSize).center().row();
            }).left().row();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }

        @Override
        public void handleItem(Building source, Item item) {}

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return false;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {}

        @Override
        public float getPowerProduction() {
            return 1000000f / 60f;
        }

        @Override
        public float heat() {
            return 1000f;
        }

        @Override
        public float heatFrac() {
            return 1f;
        }

        @Override
        public Object config() {
            return index;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(index[0]);
            write.i(index[1]);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            index[0] = read.i();
            index[1] = read.i();
        }
    }
}
