package ice.asundry.world.content.block;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.Button;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import ice.ui.stat.IceStat;
import ice.ui.stat.IceStats;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.draw.*;
import mindustry.world.meta.BlockFlag;

import java.util.ArrayList;

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
        config(Integer.class, (build, value) -> ((MultipleCrafterBuilding) build).setIndex(value));
        buildType = () -> heatBlock ? new HeatMultipleCrafterBuilding() : new MultipleCrafterBuilding();
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(IceStat.nutrientConcentration, StatValues.formulas(formulas, this));
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
                addLiquidBar(build -> build.liquids.current());
            }
        }
        if (outPower) {
            addBar("outPower", (MultipleCrafterBuilding entity) -> new Bar(() -> Core.bundle.format("bar.poweroutput", Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)), () -> Pal.powerBar, () -> entity.efficiency));
        }
        if (consP) {
            addBar("power", (MultipleCrafterBuilding entity) -> new Bar(() -> (entity.consPower != null && entity.consPower.buffered) ? Core.bundle.format("bar.poweramount", Float.isNaN(entity.power.status * entity.consPower.capacity) ? "<ERROR>" : UI.formatAmount((int) (entity.power.status * entity.consPower.capacity))) : Core.bundle.get("bar.power"), () -> Pal.powerBar, () -> Mathf.zero(entity.consPower == null ? 0 : entity.consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f ? 1f : entity.power.status));
        }
        if (!heatBlock) return;
        if (outHeat) {
            addBar("outHeat", (HeatMultipleCrafterBuilding entity) -> new Bar("bar.heat", Pal.lightOrange, () -> entity.heat / entity.formula.heatOutput));
        }
        if (needHeat) {
            addBar("heat", (HeatMultipleCrafterBuilding entity) -> new Bar(() -> Core.bundle.format("bar.heatpercent", (int) entity.heatReq, (int) Math.min((entity.heatReq / entity.formula.heatRequirement * 100), entity.formula.maxHeatEfficiency)), () -> Pal.lightOrange, () -> entity.heatReq / entity.formula.heatRequirement));
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
                        //if there's still space left, it's not full for all liquids
                        allFull = false;
                    }
                }

                //if there is no space left for any liquid, it can't reproduce
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

            //limit progress increase by maximum amount of liquid it can produce
            float scaling = 1f, max = 1f;
            if (outputLiquids != null) {
                max = 0f;
                for (var s : outputLiquids) {
                    float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
                    scaling = Math.min(scaling, value);
                    max = Math.max(max, value);
                }
            }

            //when dumping excess take the maximum value instead of the minimum.
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

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);
            table.pane(t -> {
                ButtonGroup<Button> group = new ButtonGroup<>();
                for (int i = 0; i < formulas.size(); i++) {
                    int finalI = i;
                    var b = t.button(finalI + "g", Styles.squareTogglet, () -> {
                        group.setChecked(finalI + "g");
                        configure(finalI);
                    }).size(45f).get();
                    group.add(b);
                }
                group.setChecked(formulaIndex + "g");
            });
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

