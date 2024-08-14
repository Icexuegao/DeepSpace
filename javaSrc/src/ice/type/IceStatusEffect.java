package ice.type;

import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class IceStatusEffect extends StatusEffect {

    public IceStatusEffect(String name) {
        super(name);
        show=true;
    }

    @Override
    public void setStats() {
        super.setStats();
    }

    @Override
    public void update(Unit unit, float time) {
        super.update(unit, time);
    }
}
