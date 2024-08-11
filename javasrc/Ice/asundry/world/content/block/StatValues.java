package Ice.asundry.world.content.block;

import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;

public class StatValues {
    public static StatValue formulas(FormulaStack formulas, Block block) {
        return t -> t.table(table -> {
            table.left();
            for (int f = 0; f < formulas.size(); f++) {
                int finalF = f;
                table.table(Styles.grayPanel, tab -> {
                    tab.left();
                    formula(formulas.getFormula(finalF), block).display(tab);
                }).growX().pad(5).row();
            }
        }).left();
    }

    public static StatValue formula(Formula formula, Block block) {
        return t -> {
            t.table(table -> {
                Stats stats = new Stats();
                formula.display(stats, block);
                displayStats(stats, table);
            }).left().margin(10);
            t.row();
        };
    }

    public static void displayStats(Stats stats, Table table) {
        for (StatCat cat : stats.toMap().keys()) {
            OrderedMap<Stat, Seq<StatValue>> map = stats.toMap().get(cat);

            if (map.size == 0) continue;

            if (stats.useCategories) {
                table.add("@category." + cat.name).color(Pal.accent).fillX();
                table.row();
            }

            for (Stat stat : map.keys()) {
                table.table(inset -> {
                    inset.left();
                    inset.add("[lightgray]" + stat.localized() + ":[] ").left().top();
                    Seq<StatValue> arr = map.get(stat);
                    for (StatValue value : arr) {
                        value.display(inset);
                        inset.add().size(10f);
                    }
                }).fillX();
                table.row();
            }
        }
    }
}
