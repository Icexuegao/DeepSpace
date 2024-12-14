package ice.world.blocks.factoryBlocks.multipleCrafter;

import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

import java.util.Arrays;

public class Formula {
    public String displayName = "1145";
    public Color formulaColor = Color.black;
    public Consume[] input;
    public ItemStack[] outputItems;
    public LiquidStack[] outputLiquids;
    public float craftTime = 60f;
    public int[] liquidOutputDirections = {-1};
    public Effect craftEffect = Fx.none;
    public Effect updateEffect = Fx.none;
    public float updateEffectChance = 0.04f;
    public float warmupSpeed = 0.019f;
    public float powerProduction = 0f;
    //The block must be heatBlock
    public float heatOutput = 0f;
    public float heatRequirement = 0f;
    public float warmupRate = 0.15f;

    public float maxHeatEfficiency = 1f;

    public void setInput(Consume[] input) {
        this.input = input;
    }

    public Consume[] getInputs() {
        return input;
    }

    public void setOutput(ItemStack[] outputItems) {
        formulaColor = outputItems[0].item.color;
        this.outputItems = outputItems;
    }

    public void setOutput(LiquidStack[] outputLiquids) {
        formulaColor = outputLiquids[0].liquid.color;
        this.outputLiquids = outputLiquids;
    }

    public ItemStack[] getOutputItems() {
        return outputItems;
    }

    public LiquidStack[] getOutputLiquids() {
        return outputLiquids;
    }

    public Formula set(Consume[] in, ItemStack[] outputItems, LiquidStack[] outputLiquids) {
        input = in;
        this.outputItems = outputItems;
        this.outputLiquids = outputLiquids;
        return this;
    }

    public Formula consPower(float value) {
        this.powerProduction = value;
        return this;
    }

    public ConsumePower getConsPower() {
        for (var c : input) {
            if (c instanceof ConsumePower p) {
                return p;
            }
        }
        return null;
    }

    public void apply(Block block) {
        if (input == null) return;
        for (var c : input) {
            c.apply(block);
        }
        if (powerProduction > 0) {
            block.hasPower = true;
            block.outputsPower = true;
        }
    }

    public void update(Building build) {
        if (input == null) return;
        for (var c : input) {
            c.update(build);
        }
    }

    public void trigger(Building build) {
        if (input == null) return;
        for (var c : input) {
            c.trigger(build);
        }
    }

    public void display(Stats stats, Block block) {
        stats.timePeriod = craftTime;
        if (input != null) for (var c : input) {
            c.display(stats);
        }
        if ((block.hasItems && block.itemCapacity > 0) || outputItems != null) {
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if (outputItems != null) {
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if (outputLiquids != null) {
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }
        if (powerProduction > 0) {
            stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
        }
    }

    public void build(Building build, Table table) {
        if (input == null) return;
        table.pane(t->{
            for (var c : input) {
                c.build(build, t);
            }
        });
    }

    @Override
    public String toString() {
        return "Formula{" + "input=" + Arrays.toString(input) + ", outputItems=" + Arrays.toString(outputItems) + ", outputLiquids=" + Arrays.toString(outputLiquids) + ", craftTime=" + craftTime + ", powerProduction=" + powerProduction + '}';
    }
}
