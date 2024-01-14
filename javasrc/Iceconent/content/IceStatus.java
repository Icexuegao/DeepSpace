package Iceconent.content;

import arc.graphics.Color;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class IceStatus {
    public static StatusEffect zhansha;

    public static void load() {
        zhansha = new StatusEffect("zhansha") {
            {
                color = Color.valueOf("A04553");
                effect = new WaveEffect() {
                    {
                        lifetime = 60;
                        sides = 4;
                        sizeTo = 9;
                        sizeFrom = 2;
                        colorFrom = Color.valueOf("F15454");
                        colorTo = Color.valueOf("F15454");
                    }
                };
            }

            @Override
            public void update(Unit unit, float time) {
                super.update(unit, time);
                if (unit.health <= unit.maxHealth * 0.5) {
                    unit.kill();
                }
            }
        };
    }
}
