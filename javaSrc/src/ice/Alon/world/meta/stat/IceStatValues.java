package ice.Alon.world.meta.stat;

import arc.flabel.FLabel;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

public class IceStatValues extends StatValues {

    public static StatValue number(float value, StatUnit unit, String color, Stat stat) {
        return number(value, unit, false, color, stat);
    }

    public static StatValue number(float value, StatUnit unit, boolean merge, String color, Stat stat) {
        return table -> {
            String l1 = color + (unit.icon == null ? "" : unit.icon + " ") + StatValues.fixValue(value) + "[]";
            String l2 = color + (unit.space ? " " : "") + unit.localized() + "[]";
            if (stat instanceof IceStat stat1) {
                if (merge) {
                    FLabel fLabel = new FLabel(l1 + l2);
                    fLabel.setDefaultToken(stat1.defaultToken);
                    table.add(fLabel).left();
                } else {
                    FLabel fLabel1 = new FLabel(l1);
                    FLabel fLabel2 = new FLabel(l2);
                    fLabel2.setDefaultToken(stat1.defaultToken);
                    fLabel1.setDefaultToken(stat1.defaultToken);
                    table.add(fLabel1).left();
                    table.add(fLabel2).left();
                }
            } else {
                if (merge) {
                    table.add(l1 + l2).left();
                } else {
                    table.add(l1).left();
                    table.add(l2).left();
                }
            }
        };
    }
}
