package Ice.asundry.Content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import Ice.asundry.world.draw.IceDraw;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.randLenVectors;
import static arc.math.Mathf.rand;
import static mindustry.content.Fx.v;

public class Ix {
    public static final Effect tarnationCharge = new Effect(130f, 180f, e -> {
        color(Pal.lancerLaser, Color.white, e.fin());
        stroke(5f * e.fin());
        circle(e.x, e.y, e.fout() * 15f * 5f);
        IceDraw.lightningOrb(e.x, e.y, 10f * e.finpow(), Pal.lancerLaser, Pal.sapBullet);

    }),

    tarnationShoot = new Effect(40f, 240f, e -> {
        stroke(4f * e.fout(), Pal.sapBullet);
        circle(e.x, e.y, 10f + e.fin() * 40f);
        color(Pal.lancerLaser);
        Fill.circle(e.x, e.y, 10f * e.fout(0.5f));
        spark(e.x, e.y, e.finpow() * 40f + 28f, 18f * e.fout(), Mathf.randomSeed(e.id, 360f));
        color();
        Fill.circle(e.x, e.y, 10f * 0.8f * e.fout(0.5f));
        spark(e.x, e.y, e.finpow() * 40f + 20f, 12f * e.fout(), Mathf.randomSeed(e.id, 360f));
        Draw.blend(Blending.additive);
        Lines.stroke(1.5f * e.fout());
        color(Pal.lancerLaser);
        Lines.poly(e.x, e.y, Mathf.random(7) + 11, 10f * 1.8f + e.finpow() * 90f, Mathf.random(360f));
        Lines.stroke(e.fout());
        color(Pal.sapBullet);
        Lines.poly(e.x, e.y, Mathf.random(7) + 11, 10f * 2.2f + e.finpow() * 110f, Mathf.random(360f));
        color();
        Draw.blend();
    }),

    tarnationLines = new Effect(15f, e -> {
        color();
        stroke(2.5f * e.fin());
        randLenVectors(e.id, 5, e.fout() * 15f * 3.5f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 10f);
        });
    }),

    gc = new Effect(60.0F, (e) -> Angles.randLenVectors(e.id, 6, 8.0F + e.fin() * 5.0F, (x, y) -> {
        color(Color.valueOf("ff7439"), e.color, e.fin());
        Fill.square(e.x + x, e.y + y, 0.5F + e.fout() * 2.0F, 45.0F);
    })),

    gc1 = new Effect(80.0F, (e) -> Angles.randLenVectors(e.id, 0.2F + e.fin(), 4, 6.3F, (x, y, fin, out) -> {
        color(Color.valueOf("ff9c71"), Pal.coalBlack, e.finpowdown());
        Fill.circle(e.x + x, e.y + y, out * 2.0F + 0.35F);
    })),

    hj = new MultiEffect(new WaveEffect() {{
        layer = 30;
        colorFrom = Color.valueOf("b7d9e3");
        sizeFrom = 10F;
        sizeTo = 0F;
    }}, new ParticleEffect() {{
        colorFrom = Color.valueOf("b7d9e3");
        sizeFrom = 1F;
        sizeTo = 0;
    }}),

    hjs = new Effect(32f, e -> {
        color(Color.valueOf("ff7171"), e.color, e.fin());
        rand.setSeed(e.id);
        for (int i = 0; i < 13; i++) {
            float rot = e.rotation + rand.range(26f);
            v.trns(rot, rand.random(e.finpow() * 30f));
            Fill.poly(e.x + v.x, e.y + v.y, 4, e.fout() * 4f + 0.2f, rand.random(360f));
        }
    }),

    hj1 = new MultiEffect(new WaveEffect() {{
        colorFrom = Color.valueOf("ff7171");
        sizeFrom = 0;
        sizeTo = 24;
        lifetime = 20;
    }}, new ParticleEffect() {{
        lifetime = 20;
        colorFrom = Color.valueOf("ff7171");
        line = true;
        lenFrom = 0;
        lenTo = 16;
    }}), hj2 = new MultiEffect(new WaveEffect() {{
        colorFrom = Color.valueOf("cdf4ff");
        sizeFrom = 0;
        sizeTo = 24;
        lifetime = 20;
    }}, new ParticleEffect() {{
        lifetime = 20;
        colorFrom = Color.valueOf("cdf4ff");
        line = true;
        lenFrom = 0;
        lenTo = 16;
    }});

    public static void spark(float x, float y, float size, float width, float r) {
        Drawf.tri(x, y, width, size, r);
        Drawf.tri(x, y, width, size, r + 180f);
        Drawf.tri(x, y, width, size, r + 90f);
        Drawf.tri(x, y, width, size, r + 270f);
    }
}


