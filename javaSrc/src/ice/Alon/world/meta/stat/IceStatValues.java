package ice.Alon.world.meta.stat;

import arc.flabel.FLabel;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import ice.Alon.world.blocks.factoryBlocks.multipleCrafter.Formula;
import ice.Alon.world.blocks.factoryBlocks.multipleCrafter.FormulaStack;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.*;

public class IceStatValues {

    public static StatValue number(float value, StatUnit unit, String color, Stat stat) {
        return number(value, unit, false, color, stat);
    }

    public static StatValue number(float value, StatUnit unit, boolean merge, String color, Stat stat) {
        return table->{
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
    public static StatValue formulas(FormulaStack formulas, Block block) {
        return t->t.table(table->{
            table.left();
            for (int f = 0; f < formulas.size(); f++) {
                int finalF = f;
                table.table(Styles.grayPanel, tab->{
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
