package ice.content;

import arc.graphics.Color;
import ice.type.content.IceStatusEffect;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

public class IceStatus {
    public static StatusEffect 电磁脉冲, 破甲1, 破甲2, 破甲3, 穿甲, 搏动, 回想, 流血;

    public static void load() {
        穿甲 = new IceStatusEffect("armorPiercing") {{
            speedMultiplier = 1.5f;
            armorBreakPercent = 0.8f;
        }};
        破甲3 = new IceStatusEffect("armorBreak3") {{
            speedMultiplier = 1.1f;
            armorBreak = 30;
        }};
        破甲2 = new IceStatusEffect("armorBreak2") {{
            speedMultiplier = 1.1f;
            armorBreak = 20;
        }};
        破甲1 = new IceStatusEffect("armorBreak1") {{
            speedMultiplier = 1.1f;
            armorBreak = 10;
        }};
        电磁脉冲 = new IceStatusEffect("electromagneticPulse") {{
            speedMultiplier = 0.2f;
            healthMultiplier = 0.9f;
        }};
        流血 = new IceStatusEffect("bleed") {{
            damage = 1;
            color = Color.red;
            effect = Fx.absorb;
        }};
        回想 = new IceStatusEffect("resound") {{
            speedMultiplier = 0.5f;
            color = Color.red;
            effect = Fx.absorb;
        }};
        搏动 = new IceStatusEffect("throb") {{
            healthMultiplier = 1.7f;
            speedMultiplier = 1.4f;
            color = Color.red;
            effect = Fx.absorb;
        }};
    }
}
