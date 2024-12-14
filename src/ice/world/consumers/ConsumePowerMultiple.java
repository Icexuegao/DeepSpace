package ice.world.consumers;

import ice.world.blocks.factoryBlocks.multipleCrafter.MultipleCrafter;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stats;

public class ConsumePowerMultiple extends ConsumePower {
    public ConsumePower[] cons;

    public ConsumePowerMultiple(ConsumePower[] consumePowers) {
        cons = consumePowers;
    }

    @Override
    public void apply(Block block) {
    }

    @Override
    public boolean ignore() {
        return true;
    }

    @Override
    public void display(Stats stats) {
    }

    @Override
    public float requestedPower(Building entity) {
        if (entity instanceof MultipleCrafter.MultipleCrafterBuilding b) {
            return b.consPower != null ? b.consPower.requestedPower(b) : 0f;
        }
        return 0;
    }
}
