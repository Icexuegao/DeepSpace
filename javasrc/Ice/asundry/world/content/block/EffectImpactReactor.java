package Ice.asundry.world.content.block;

import arc.graphics.Color;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.blocks.power.ImpactReactor;

public class EffectImpactReactor extends ImpactReactor {
    public Color effectColor;
    public Effect effect;
    public float refreshEffect = 60;

    public EffectImpactReactor(String name) {
        super(name);
        effectColor = Color.valueOf("ffdf9d");
        update = true;
        effect = Fx.none;
        buildType = EffectImpactReactorBuild::new;
    }

    public class EffectImpactReactorBuild extends ImpactReactorBuild {

        /**
         * 获取加载进度
         */
        public float progress;

        @Override
        public void updateTile() {
            progress += getProgressIncrease(refreshEffect);
            if (warmup == 1 && progress >= 1) {
                progress = 0;
                effect.at(x, y, effectColor);
            }
            super.updateTile();
        }

        @Override
        public float getPowerProduction() {
            return super.getPowerProduction();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
        }
    }
}
