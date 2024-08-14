package ice.ui.stat;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.Stats;

public class IceStats extends Stats {
    public void addPercentThrob(Stat stat, float value, String color) {
        add(stat, IceStatValues.number((int) (value * 100), StatUnit.percent, color, stat));
    }
}
