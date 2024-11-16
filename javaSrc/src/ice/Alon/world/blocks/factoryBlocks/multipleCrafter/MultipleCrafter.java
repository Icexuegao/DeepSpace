package ice.Alon.world.blocks.factoryBlocks.multipleCrafter;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import ice.Alon.File.IceFiles;
import ice.Alon.ui.Ui;
import ice.Alon.world.consumers.ConsumePowerMultiple;
import ice.Alon.world.meta.stat.IceStat;
import ice.Alon.world.meta.stat.IceStatValues;
import ice.Alon.world.meta.stat.IceStats;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.ItemImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.BlockFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.content;
import static mindustry.gen.Tex.pane;

public class MultipleCrafter extends Block {
    public FormulaStack formulas;
    public boolean dumpExtraLiquid = true;
    public boolean ignoreLiquidFullness = false;
    public boolean heatBlock = false;
    public DrawBlock drawer = new DrawDefault();

    public MultipleCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
        formulas = new FormulaStack();
        configurable = true;
        config(Integer.class, (build, value)->((MultipleCrafterBuilding) build).setIndex(value));
        buildType = ()->heatBlock ? new HeatMultipleCrafterBuilding() : new MultipleCrafterBuilding();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(IceStat.formulas, IceStatValues.formulas(formulas, this));
    }

    @Override
    public void setBars() {
        super.setBars();
        boolean added = false;
        boolean outPower = false;
        boolean consP = false;
        boolean needHeat = false;
        boolean outHeat = false;
        Seq<Liquid> addedLiquids = new Seq<>();
        for (var f : formulas.getFormulas()) {
            if (f.powerProduction > 0) outPower = true;
            if (f.heatOutput > 0) outHeat = true;
            if (f.heatRequirement > 0) needHeat = true;
            if (f.input != null) for (var cons : f.getInputs()) {
                if (cons instanceof ConsumePower) consP = true;
                if (cons instanceof ConsumeLiquid liq) {
                    added = true;
                    if (addedLiquids.contains(liq.liquid)) continue;
                    addedLiquids.add(liq.liquid);
                    addLiquidBar(liq.liquid);
                } else if (cons instanceof ConsumeLiquids multi) {
                    added = true;
                    for (var stack : multi.liquids) {
                        if (addedLiquids.contains(stack.liquid)) continue;
                        addedLiquids.add(stack.liquid);
                        addLiquidBar(stack.liquid);
                    }
                }
            }
            if (f.getOutputLiquids() != null) for (var out : f.getOutputLiquids()) {
                if (addedLiquids.contains(out.liquid)) continue;
                addedLiquids.add(out.liquid);
                addLiquidBar(out.liquid);
            }
            if (!added) {
                addLiquidBar(build->build.liquids.current());
            }
        }
        if (outPower) {
            addBar("outPower", (MultipleCrafterBuilding entity)->new Bar(()->Core.bundle.format("bar.poweroutput", Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)), ()->Pal.powerBar, ()->entity.efficiency));
        }
        if (consP) {
            addBar("power", (MultipleCrafterBuilding entity)->new Bar(()->(entity.consPower != null && entity.consPower.buffered) ? Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * entity.consPower.capacity) ? "<ERROR>" : UI.formatAmount((int) (entity.power.status * entity.consPower.capacity))) : Core.bundle.get("bar.power"), ()->Pal.powerBar, ()->Mathf.zero(entity.consPower == null ? 0 : entity.consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status));
        }
        if (!heatBlock) return;
        if (outHeat) {
            addBar("outHeat", (HeatMultipleCrafterBuilding entity)->new Bar("bar.heat", Pal.lightOrange, ()->entity.heat / entity.formula.heatOutput));
        }
        if (needHeat) {
            addBar("heat", (HeatMultipleCrafterBuilding entity)->new Bar(()->Core.bundle.format("bar.heatpercent", (int) entity.heatReq, (int) Math.min((entity.heatReq / entity.formula.heatRequirement * 100), entity.formula.maxHeatEfficiency)), ()->Pal.lightOrange, ()->entity.heatReq / entity.formula.heatRequirement));
        }
    }

    @Override
    public boolean rotatedOutput(int x, int y) {
        return false;
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    @Override
    public void init() {
        stats = new IceStats();
        super.init();
        formulas.apply(this);
        if (hasPower && consumesPower) {
            ArrayList<ConsumePower> cs = new ArrayList<>();
            for (var f : formulas.getFormulas()) {
                ConsumePower p = f.getConsPower();
                if (p != null) cs.add(p);
            }
            ConsumePower[] csa = new ConsumePower[]{};
            consPower = new ConsumePowerMultiple(cs.toArray(csa));
        }
        if (heatBlock) {
            if (drawer.getClass() == DrawDefault.class) {
                var drawMul = new DrawBlock[]{};
                var drawList = new ArrayList<DrawBlock>();
                drawList.add(new DrawDefault());
                boolean needHeat = false;
                boolean outputHeat = false;
                for (var f : formulas.getFormulas()) {
                    if (f.heatRequirement > 0) needHeat = true;
                    if (f.heatOutput > 0) outputHeat = true;
                }
                if (needHeat) drawList.add(new DrawHeatInput());
                if (outputHeat) drawList.add(new DrawHeatOutput());
                drawMul = drawList.toArray(drawMul);
                drawer = new DrawMulti(drawMul);
            }
            rotateDraw = false;
            rotate = true;
            drawArrow = true;
        }
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.finalIcons(this);
    }

    @Override
    public boolean outputsItems() {
        return formulas.outputItems();
    }

    @Override
    public void getRegionsToOutline(Seq<TextureRegion> out) {
        drawer.getRegionsToOutline(this, out);
    }

    public class MultipleCrafterBuilding extends Building {
        public float progress;
        public float totalProgress;
        public float warmup;
        public int formulaIndex = 0;
        public Formula formula = formulas.getFormula(formulaIndex);
        public ItemStack[] outputItems = formula.getOutputItems();
        public LiquidStack[] outputLiquids = formula.getOutputLiquids();
        public float powerProductionTimer = 0f;
        public ConsumePower consPower;

        @Override
        public void draw() {
            drawer.draw(this);
        }

        @Override
        public void drawLight() {
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public boolean shouldConsume() {
            if (outputItems != null) {
                for (var output : outputItems) {
                    if (items.get(output.item) + output.amount > itemCapacity) {
                        return false;
                    }
                }
            }

            if (outputLiquids != null && !ignoreLiquidFullness) {
                boolean allFull = true;
                for (var output : outputLiquids) {
                    if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                        if (!dumpExtraLiquid) {
                            return false;
                        }
                    } else {
                        //如果仍有剩余空间，则表示它未满所有液体
                        allFull = false;
                    }
                }

                //如果没有空间容纳任何液体，它就无法繁殖
                if (allFull) {
                    return false;
                }
            }

            return enabled;
        }

        @Override
        public void updateConsumption() {
            //everything is valid when cheating
            if (formula.getInputs() == null || cheating()) {
                potentialEfficiency = enabled && productionValid() ? 1f : 0f;
                efficiency = optionalEfficiency = shouldConsume() ? potentialEfficiency : 0f;
                updateEfficiencyMultiplier();
                return;
            }

            //disabled -> nothing works
            if (!enabled) {
                potentialEfficiency = efficiency = optionalEfficiency = 0f;
                return;
            }

            boolean update = shouldConsume() && productionValid();

            float minEfficiency = 1f;

            //assume efficiency is 1 for the calculations below
            efficiency = optionalEfficiency = 1f;

            //first pass: get the minimum efficiency of any consumer
            for (var cons : formula.getInputs()) {
                minEfficiency = Math.min(minEfficiency, cons.efficiency(self()));
            }

            //same for optionals
            for (var cons : formula.getInputs()) {
                optionalEfficiency = Math.min(optionalEfficiency, cons.efficiency(self()));
            }

            //efficiency is now this minimum value
            efficiency = minEfficiency;
            optionalEfficiency = efficiency;

            //assign "potential"
            potentialEfficiency = efficiency;

            //no updating means zero efficiency
            if (!update) {
                efficiency = optionalEfficiency = 0f;
            }

            updateEfficiencyMultiplier();

            //second pass: update every consumer based on efficiency
            if (update && efficiency > 0) {
                formula.update(this);
            }
            if (powerProductionTimer > 0) powerProductionTimer--;
        }

        @Override
        public void displayConsumption(Table table) {
            super.displayConsumption(table);
            formula.build(this, table);
        }

        @Override
        public void updateTile() {
            formula = formulas.getFormula(formulaIndex);
            outputItems = formula.getOutputItems();
            outputLiquids = formula.getOutputLiquids();
            consPower = formula.getConsPower();

            if (efficiency > 0) {

                progress += getProgressIncrease(formula.craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), formula.warmupSpeed);

                //continuously output based on efficiency
                if (outputLiquids != null) {
                    float inc = getProgressIncrease(1f);
                    for (var output : outputLiquids) {
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if (wasVisible && Mathf.chanceDelta(formula.updateEffectChance)) {
                    formula.updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, formula.warmupSpeed);
            }
            totalProgress += warmup * Time.delta;

            if (progress >= 1f) {
                craft();
            }

            dumpOutputs();
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            if (outputLiquids != null) {
                for (int i = 0; i < outputLiquids.length; i++) {
                    int dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;
                    if (dir != -1) {
                        Draw.rect(outputLiquids[i].liquid.fullIcon, x + Geometry.d4x(dir + rotation) * (size * Vars.tilesize / 2f + 4), y + Geometry.d4y(dir + rotation) * (size * Vars.tilesize / 2f + 4), 8f, 8f);
                    }
                }
            }
        }

        @Override
        public float getProgressIncrease(float baseTime) {
            if (ignoreLiquidFullness) {
                return super.getProgressIncrease(baseTime);
            }

            //通过它可以产生的最大液体量来限制进度增加
            float scaling = 1f, max = 1f;
            if (outputLiquids != null) {
                max = 0f;
                for (var s : outputLiquids) {
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            //当 dump excess 时，取最大值而不是最小值。
            return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling);
        }

        @Override
        public float getPowerProduction() {
            return powerProductionTimer > 0f ? formula.powerProduction * efficiency : 0f;
        }

        public float warmupTarget() {
            return 1f;
        }

        @Override
        public float warmup() {
            return warmup;
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        public void craft() {
            formulas.trigger(this);
            if (outputItems != null) {
                for (var output : outputItems) {
                    for (int i = 0; i < output.amount; i++) {
                        offload(output.item);
                    }
                }
            }

            if (wasVisible) {
                formula.craftEffect.at(x, y);
            }
            progress %= 1f;
            powerProductionTimer += formula.craftTime / efficiency + 1f;
        }

        public void dumpOutputs() {
            if (outputItems != null && timer(timerDump, dumpTime / timeScale)) {
                for (ItemStack output : outputItems) {
                    dump(output.item);
                }
            }

            if (outputLiquids != null) {
                for (int i = 0; i < outputLiquids.length; i++) {
                    int dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;

                    dumpLiquid(outputLiquids[i].liquid, 2f, dir);
                }
            }
        }

        public  void getContent(Table ta, Consume[] consume) {
            var k = new Object() {
                int k = 0;
            };
            for (Consume consume1 : consume) {
                ItemStack[] items;
                if (consume1 instanceof ConsumeItems item) {
                    items = item.items;
                    for (ItemStack item2 : items) {
                        ta.table((s)->{
                            s.add(new ItemImage(item2.item.uiIcon, item2.amount));
                        }).grow();
                    }
                }

                LiquidStack[] Liquids;
                if (consume1 instanceof ConsumeLiquids liquid) {
                    Liquids = liquid.liquids;
                    for (LiquidStack liquid2 : Liquids) {
                        ta.table((t)->{
                            t.add(new ItemImage(liquid2.liquid.uiIcon, (int) (liquid2.amount * 60)));
                        });
                    }
                }

                if (consume1 instanceof ConsumeLiquid liquid) {
                    ta.table((s)->{
                        s.add(new ItemImage(liquid.liquid.uiIcon, (int) (liquid.amount * 60)));
                    }).grow();

                }
                Seq<Item> seq = new Seq<>();
                if (consume1 instanceof ConsumeItemFlammable cf) {
                    content.items().each(i->cf.filter.get(i) && i.unlockedNow(), (item)->{
                        seq.add(item);
                        if (k.k < 3) {
                            ta.table((s)->{
                                s.image(item.uiIcon).size(30);
                                k.k++;
                            }).grow();
                        }

                    });

                    Table table = new Table();
                    table.visible = false;
                    seq.each((i)->{
                        int i1 = seq.indexOf(i);
                        table.image(i.uiIcon).size(30);
                        if ((i1 + 1) % 4 == 0) table.row();
                        table.setColor(1, 1, 1, 1);
                        table.setBackground(Ui.INSTANCE.getMenusDialog().getCreateFlatDown());
                        table.setSize(140, 140);
                        table.margin(10);
                    });

                    ta.table((td)->{
                        td.addChild(table);
                        td.add("...");
                        ta.hovered(()->{
                            table.visible(()->true);
                        });
                        ta.exited(()->{
                            table.visible(()->false);
                        });
                        table.update(()->{
                            table.setPosition(300 - td.x, td.y + (td.getHeight() / 2), Align.left);
                        });
                    }).grow();

                    // table.update(()->table.setPosition(ta.x + ta.getWidth(), ta.y + ta.getHeight(), Align.topLeft));

                }
            }
        }

        private Table add(Table t1,String s2) {
            t1.row();
            AtomicReference<Table> t4 = new AtomicReference<>();
            t1.table((t2)->{
                StringBuffer g = new StringBuffer(s2);
                int s = 35;
                int length = g.length();
                int i = length / s;
                for (int gh = 1; gh < i + 1; gh++) {
                    g.insert(gh * s, "\n");
                }
                t2.add(g);
                t4.set(t2);
            }).expand().left();
            return t4.get();
        }

        @Override
        public void buildConfiguration(Table table) {

            super.buildConfiguration(table);
            for (int i = 0; i < formulas.size(); i++) {
                int f = i;
                Formula formula1 = formulas.getFormula(i);
                table.button((t)->{
                    t.setStyle(Styles.clearTogglei);
                    t.setBackground(pane);
                    t.table((t1)->{
                        //  t1.setBackground(Tex.pane);
                        t1.table((t2)->{
                            /**输入ui*/
                            t2.setBackground(pane);
                            getContent(t2, formula1.getInputs());

                        }).expand().size(207, 56);
                        t1.table((t2)->{
                            t2.table((t3)->{
                                String[] progres = new String[22];
                                Arrays.fill(progres, ">");
                                for (int h = 0; h < progres.length; h++) {
                                    int k = h;
                                    t3.add(progres[h]).update((e)->{
                                        if (formula1 == formula) {
                                            if (progress > ((float) k / progres.length)) {
                                                e.setColor(formula1.formulaColor);
                                            } else {
                                                e.setColor(new Color(1, 1, 1, 1));
                                            }
                                        }
                                    });
                                }
                            }).expand();
                            t2.row();
                            t2.table((t3)->{
                                t3.table(t4->{
                                    t4.add("\ue810").size(22).color(Color.yellow);
                                    t4.add(":" + (formula1.getConsPower() == null ? 0 : formula1.getConsPower().usage * 60));
                                }).expand().left();
                                t3.table(t4->{

                                    t4.image(new TextureRegion(new Texture(IceFiles.find("time.png")))).size(22);
                                    t4.add(":" + formula1.craftTime / 60 + "秒");
                                }).expand().right();

                            }).expand().fillX();
                        }).expand();
                        t1.table((t2)->{
                            /**输出ui*/
                            t2.setBackground(pane);

                            if (formula1.outputItems != null) {
                                for (ItemStack outputItem : formula1.outputItems) {
                                    t2.add(new ItemImage(outputItem.item.uiIcon, outputItem.amount));
                                }
                            }
                            if (formula1.outputLiquids != null) {
                                for (LiquidStack outLiquid : formula1.getOutputLiquids()) {
                                    t2.add(new ItemImage(outLiquid.liquid.uiIcon, (int) (outLiquid.amount * 60)));
                                }
                            }
                        }).expand().size(207, 56);
                    }).grow();
                    t.row();
                    AtomicBoolean g1 = new AtomicBoolean(false);
                    AtomicReference<Table> add = new AtomicReference<>();
                    t.table((t1)->{

                        t1.button((t2)->{
                            t2.setStyle(Styles.nonet);
                            t2.add("工艺详情:");
                            Cell<Image> size1 = t2.image(Icon.rightOpen).size(27);
                            size1.update((u)->{
                                if (g1.get()) {
                                    u.setDrawable(Icon.downOpen);
                                } else {
                                    u.setDrawable(Icon.rightOpen);
                                }
                            });
                        }, ()->{
                            g1.set(!g1.get());
                            if (g1.get()) {
                                add.set(add(t1,formula1.displayName));
                            } else {
                                add.get().remove();
                            }

                        }).expand().left();
                    }).grow();
                    t.row();


                }, ()->{
                    configure(f);
                }).update((b)->{
                    b.setChecked(formulaIndex == f);
                }).pad(5).margin(10);
                table.row();
            }
          /*  table.table((ts)->{
                ts.image(Icon.zoom);
                TextField textField = new TextField() {{
                }};

                ts.add(textField);
                var item = new Object() {
                    Item item;
                };
                textField.update(()->{
                    item.item = content.item(textField.getText());
                });


                ts.image().update((b)->{
                    if (item.item != null) {
                        b.setDrawable(item.item.uiIcon);
                        b.setColor(1, 1, 1, 1f);
                    } else {
                        b.setColor(1, 1, 1, 0);
                    }
                }).size(64, 64);
                ts.setBackground(getCreateFlatDown());
            });
            table.row();
            table.table((t)->{
                for (int i = 0; i < formulas.size(); i++) {
                    int f = i;
                    Formula formula1 = formulas.getFormula(f);

                    t.button((b)->{
                        b.table((table1->{
                            getContent(table1, formula1.input, formula1.craftTime);
                            // table1.add(new ItemImage(formula1.content.uiIcon, 3));
                        })).grow();

                        b.table((t1)->{
                            t1.table((a)->{
                                for (Consume input : formula1.getInputs()) {
                                    if (input instanceof ConsumePower pwr) {
                                        a.add('\ue810' + ": " + pwr.usage * 60 + " 秒").color(Color.yellow);
                                    }
                                }
                            }).grow();
                            t1.row();
                            t1.table((a)->{
                                String[] values = {"=", "=", "=", "=", "=", "=", "=", "=", "=", "=", "=", "=", "=", "=", "=", ">"};
                                for (int h = 0; h < values.length; h++) {
                                    int k = h;
                                    a.add(values[h]).update((e)->{
                                        if (formula1 == formula) {
                                            if (progress > ((float) k / values.length)) {
                                                e.setColor(Color.red);
                                            } else {
                                                e.setColor(new Color(1, 1, 1, 1));
                                            }
                                        }
                                    });
                                }
                              *//*  String s1="====",s4=">";
                                a.add(s1).update((e)->{
                                    if (progress>=0.3){e.setColor(Color.yellow);}else {
                                        e.setColor(new Color(1, 1, 1, 1));
                                    };
                                });
                                a.add(s1).update((e)->{
                                    if (progress>=0.6){e.setColor(Color.yellow);}else {
                                        e.setColor(new Color(1, 1, 1, 1));
                                    };
                                });
                                a.add(s1).update((e)->{
                                    if (progress>=0.9){e.setColor(Color.yellow);}else {
                                        e.setColor(new Color(1, 1, 1, 1));
                                    };
                                });
                                a.add(s4).update((e)->{

                                });*//*
                            }).grow();
                            t1.row();
                            t1.table((a)->{
                                a.add('\ue830' + ": " + formula1.craftTime / 60 + "秒");
                            }).grow();
                        }).grow();

                        b.table((table1->{
                            if (formula1.outputItems != null) {
                                for (ItemStack outputItem : formula1.outputItems) {
                                    table1.add(new ItemImage(outputItem.item.uiIcon, outputItem.amount));
                                }
                            }
                            if (formula1.outputLiquids != null) {
                                for (LiquidStack outLiquid : formula1.getOutputLiquids()) {
                                    table1.add(new ItemImage(outLiquid.liquid.uiIcon, (int) (outLiquid.amount * 60)));
                                }
                            }
                        })).grow();

                        b.setStyle(Styles.clearTogglei);
                        b.update(()->b.setChecked(formulaIndex == f));
                    }, ()->{
                        configure(f);
                    }).size(300, 124).pad(5);
                    if (f % 2 == 1) t.row();
                }
            });*/
        }


        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress) return progress();
            return super.sense(sensor);
        }

        @Override
        public float progress() {
            return Mathf.clamp(progress);
        }

        @Override
        public int getMaximumAccepted(Item item) {
            return itemCapacity;
        }

        @Override
        public boolean shouldAmbientSound() {
            return efficiency > 0;
        }

        public void setIndex(int index) {
            formulaIndex = index;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.f(warmup);
            write.b(formulaIndex);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
            warmup = read.f();
            formulaIndex = read.b();
        }
    }

    public class HeatMultipleCrafterBuilding extends MultipleCrafterBuilding implements HeatBlock, HeatConsumer {
        public float heat;
        public float heatReq;
        public float[] sideHeat = new float[4];

        @Override
        public void updateTile() {
            super.updateTile();
            //热量以相同的速度接近目标，而不管效率如何
            heat = Mathf.approachDelta(heat, formula.heatOutput * efficiency, formula.warmupRate * delta());
            if (formula.heatRequirement > 0) {
                heatReq = calculateHeat(sideHeat);
            }
        }

        @Override
        public void updateEfficiencyMultiplier() {
            super.updateEfficiencyMultiplier();
            if (formula.heatRequirement > 0) {
                efficiency *= Math.min(Math.max(heatReq / formula.heatRequirement, cheating() ? 1f : 0f), formula.maxHeatEfficiency);
            }
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            return heat / formula.heatOutput;
        }

        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        @Override
        public float heatRequirement() {
            return formula.heatRequirement;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            heat = read.f();
        }
    }
}

