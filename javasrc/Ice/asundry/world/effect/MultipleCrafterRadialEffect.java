package Ice.asundry.world.effect;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Mathf.rand;
import static mindustry.content.Fx.v;

public class MultipleCrafterRadialEffect extends RadialEffect{
    public float life;
    public MultipleCrafterRadialEffect(float life) {
        if (life<60){
            life=60;
        }
        this.life=life;
        effect = new MultiEffect(new Effect(life, (EffectContainer e) -> {
            e.x -= 6;
            e.y -= 3;
            text(e);
        }), new Effect(life, (e) -> {
            e.x -= 6;
            e.y -= 15;
            text(e);
        }), effect = new Effect(life, (e) -> {
            e.x -= 25;
            e.y -= 15;
            text(e);
        }), new Effect(life, (e) -> {
            e.x -= 25;
            e.y -= 3;
            text(e);
        }));
        rotationOffset = 30f;
        rotationSpacing = 90;
        lengthOffset = 17;
        amount = 1;
    }

    private void text(EffectContainer e) {
        color(Pal.slagOrange);
        Draw.alpha(0.6F);
        rand.setSeed(e.id);
        for (int i = 0; i < 3; ++i) {
            float len = rand.random(6.0F);
            float rot = rand.range(40.0F) + e.rotation;
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 2.0F * b.fslope() + 0.2F);
            });
        }
    }
}
