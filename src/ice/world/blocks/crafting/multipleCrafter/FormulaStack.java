package ice.world.blocks.crafting.multipleCrafter;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.Block;

public class FormulaStack {
    private Seq<Formula> formulas;

    public FormulaStack() {
        formulas = new Seq<>();
    }

    public FormulaStack(Seq<Formula> formulas) {
        this.formulas = formulas;
    }

    public static FormulaStack with(Formula... formulas) {
        return new FormulaStack(new Seq<>(formulas));
    }

    public Seq<Formula> getFormulas() {
        return formulas;
    }

    public void setFormulas(Seq<Formula> formulas) {
        this.formulas = formulas;
    }

    public Formula getFormula(int index) {
        return formulas.get(index);
    }

    public void setFormula(int index, Formula formula) {
        formulas.set(index, formula);
    }

    public void addFormula(Formula formula) {
        formulas.add(formula);
    }

    public void addsFormulaArray(Formula... formula) {
        Seq<Formula> seq = new Seq<>();
        seq.add(formula);
        for (int i = 0; i < seq.size; i++) {
            formulas.add(seq.get(i));
        }
    }

    public boolean outputItems(int index) {
        return getFormula(index).getOutputItems() != null;
    }

    public boolean outputItems() {
        for (var f : formulas) {
            if (f.getOutputItems() != null) return true;
        }
        return false;
    }

    public void trigger(Building build) {
        for (var f : formulas) {
            f.trigger(build);
        }
    }

    public void apply(Block block) {
        for (var f : formulas) {
            f.apply(block);
        }
    }

    public int size() {
        return formulas.size;
    }
}
