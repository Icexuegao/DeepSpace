package ice.type.content;

import arc.Core;
import arc.struct.Seq;
import ice.world.meta.stat.IceStat;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

/** 每一种独特的状态都要判断防止与其他状态冲突 */
public class IceStatusEffect extends StatusEffect {
    public static Seq<IceStatusEffect> stuts = new Seq<>();
    /** 破甲百分比 */
    public float armorBreakPercent = 0f;
    /** 破甲数量 */
    public int armorBreak = 0;
    /** 破甲状态消失后是否恢复护甲 */
    public boolean armorRecovery = false;

    public IceStatusEffect(String name) {
        super(name);
        stuts.add(this);
        localizedName = Core.bundle.get(getContentType() + "." + name + ".name", name);
        description = Core.bundle.getOrNull(getContentType() + "." + name + ".description");
        details = Core.bundle.getOrNull(getContentType() + "." + name + ".details");
        show = true;
    }

    @Override
    public void setStats() {
        if (armorBreakPercent != 0) stats.addPercent(IceStat.破甲, armorBreakPercent);
        if (armorBreak != 0) stats.add(IceStat.破甲, armorBreak);
        super.setStats();
    }

    @Override
    public void applied(Unit unit, float time, boolean extend) {
        /**扣除百分比*/
        if (armorBreakPercent != 0) {
            unit.armor *= armorBreakPercent;
        }

        /**扣除护甲*/
        if (armorBreak != 0) {
            if (unit.type.armor >= armorBreak) {
                unit.armor -= armorBreak;
            } else {
                unit.armor -= unit.type.armor;
            }
        }

        super.applied(unit, time, extend);
    }

    @Override
    public void update(Unit unit, float time) {
        /**时间结束恢复护甲*/
        if (!(armorBreak == 0) && !armorRecovery && time <= 60) {
            armorRecovery = true;
            if (unit.type.armor >= armorBreak) {
                unit.armor += armorBreak;
            } else {
                unit.armor += unit.type.armor;
            }
        }
        super.update(unit, time);
    }


    @Override
    public boolean isHidden() {
        return !show;
    }
}
