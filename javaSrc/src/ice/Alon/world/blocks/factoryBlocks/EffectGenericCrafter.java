package ice.Alon.world.blocks.factoryBlocks;

import arc.graphics.Color;
import ice.Alon.asundry.world.bullet.ThickLightning;
import ice.Alon.entities.IceLightning;
import ice.Alon.library.IceMathf;
import ice.Alon.world.meta.stat.IceStat;
import mindustry.content.StatusEffects;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.graphics.Drawf;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.production.GenericCrafter;

import java.util.Random;

/**
 * 状态工厂类,一段时间给予周围己方单位状态
 */
public class EffectGenericCrafter extends GenericCrafter {
    /**
     * 每隔一段时间给予一次状态
     */
    public float effectTime;
    /**
     * 给予的状态
     */
    public StatusEffect statusEffect = StatusEffects.wet;
    /**
     * 状态时间 ,秒为单位
     */
    public float statusTime = 15;
    /**
     * 范围
     */
    public float radius = 40;

    public EffectGenericCrafter(String name) {
        super(name);
        buildType = EffectGenericCrafterBuild::new;
        effectTime = 60;
    }

    @Override
    public void setStats() {
        stats.add(IceStat.effectTime, effectTime / 60 + " seconds");
        stats.add(IceStat.effect, statusEffect.localizedName);
        stats.add(IceStat.statusTime, statusTime + " seconds");
        stats.add(IceStat.radius, "[" + radius / 8 + "] tile");
        super.setStats();
    }

    @Override
    public void drawOverlay(float x, float y, int rotation) {
        Drawf.circles(x, y, radius);
        super.drawOverlay(x, y, rotation);
    }

    public class EffectGenericCrafterBuild extends GenericCrafterBuild {
        float i = 0;

        @Override
        public void updateTile() {
            i += getProgressIncrease(effectTime);
            IceMathf.goe1(i, ()->{
                Units.nearby(team, x, y, radius, (e)->e.apply(statusEffect, statusTime * 60));
                heal(maxHealth * 0.05f);
                i = 0;
                IceLightning.createLightningInternal(null,4,team,Color.valueOf("ed90df"),1,x,y,new Random().nextInt(360),8);

            });
            super.updateTile();
        }
    }
}
