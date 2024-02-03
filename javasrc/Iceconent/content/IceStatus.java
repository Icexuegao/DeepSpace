package Iceconent.content;

import Iceconent.World.IceStats;
import arc.graphics.Color;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class IceStatus {

    public static StatusEffect electromagneticPulse;

    public static void load() {
        electromagneticPulse = new StatusEffect("electromagnetic-Pulse") {
            {
                speedMultiplier = 0.2f;
                healthMultiplier = 0.9f;
                buildSpeedMultiplier = 0;
                color = Color.valueOf("9dd2ff");
                effect = new WaveEffect() {{
                    lifetime = 120;
                    sides = 4;/** 几条边，定义形状 */
                    sizeTo = 0;/** 结束  */
                    sizeFrom = 5;/**  开始 */
                    colorFrom = Color.valueOf("9dd2ff");
                    colorTo = Color.valueOf("def3fc");
                }};
            }

            @Override
            public void setStats() {
                stats.addPercent(IceStats.behead, 0.3f);
                super.setStats();
            }

            @Override
            public void update(Unit unit, float time) {
                super.update(unit, time);
                if (unit.health <= unit.maxHealth * 0.3f) {
                    unit.kill();
                }
            }
        };
    }
}
