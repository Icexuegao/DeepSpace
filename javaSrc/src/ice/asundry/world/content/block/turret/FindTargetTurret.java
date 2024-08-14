package ice.asundry.world.content.block.turret;

import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class FindTargetTurret extends ItemTurret {
    public FindTargetTurret(String name) {
        super(name);
        buildType = FindTargetTurretBuild::new;
    }

    public class FindTargetTurretBuild extends ItemTurretBuild {
        @Override
        protected void findTarget() {
            target = Units.bestEnemy(team, x, y, range, e -> !e.dead()&& e.type== UnitTypes.mono, unitSort);
        }
    }
}
