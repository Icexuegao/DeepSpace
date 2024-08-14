package ice.content;

import ice.type.IceStatusEffect;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

public class IceStatus {
    public static StatusEffect electromagneticPulse;
    public static StatusEffect throb, resound, bleed;

    public static void load() {
        /**电磁脉冲 */
        electromagneticPulse = new IceStatusEffect("electromagnetic-Pulse") {{
            speedMultiplier = 0.2f;
            healthMultiplier = 0.9f;
        }};
        /** 流血*/
        bleed = new IceStatusEffect("bleed") {{
            show = true;
            damage = 1;
            color = Color.red;
            effect = Fx.absorb;
        }};
        /** 回想*/
        resound = new IceStatusEffect("resound") {{
            speedMultiplier = 0.5f;
            show = true;
            color = Color.red;
            effect = Fx.absorb;
        }};
        /**剧烈搏动 */
        throb = new IceStatusEffect("throb") {{
            healthMultiplier = 1.7f;
            speedMultiplier = 1.4f;
            show = true;
            color = Color.red;
            effect = Fx.absorb;
        }};
    }
}
