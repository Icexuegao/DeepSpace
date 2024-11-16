package ice.Alon.content;

import arc.graphics.Color;
import ice.Alon.type.IceStatusEffect;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

public class IceStatus {
    public static StatusEffect electromagneticPulse, armorBreak1, armorBreak2, armorBreak3, armorPiercing;
    public static StatusEffect throb, resound, bleed;

    public static void load() {
        /**穿甲*/
        armorPiercing = new IceStatusEffect("armorPiercing") {{
            speedMultiplier = 1.5f;
            armorBreakPercent=0.8f;
        }};
        /**破甲3*/
        armorBreak3 = new IceStatusEffect("armorBreak3") {{
            speedMultiplier = 1.1f;
            armorBreak = 30;
        }};
        /**破甲2*/
        armorBreak2 = new IceStatusEffect("armorBreak2") {{
            speedMultiplier = 1.1f;
            armorBreak = 20;
        }};
        /**破甲1*/
        armorBreak1 = new IceStatusEffect("armorBreak1") {{
            speedMultiplier = 1.1f;
            armorBreak = 10;
        }};
        /**电磁脉冲 */
        electromagneticPulse = new IceStatusEffect("electromagneticPulse") {{
            speedMultiplier = 0.2f;
            healthMultiplier = 0.9f;
        }};
        /**流血*/
        bleed = new IceStatusEffect("bleed") {{
            damage = 1;
            color = Color.red;
            effect = Fx.absorb;
        }};
        /**回想*/
        resound = new IceStatusEffect("resound") {{
            speedMultiplier = 0.5f;
            color = Color.red;
            effect = Fx.absorb;
        }};
        /**剧烈搏动 */
        throb = new IceStatusEffect("throb") {{
            healthMultiplier = 1.7f;
            speedMultiplier = 1.4f;
            color = Color.red;
            effect = Fx.absorb;
        }};
    }
}
