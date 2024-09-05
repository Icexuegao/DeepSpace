package ice.Alon.content;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

import java.util.Random;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;

public class IceEffects {
    /**
     * 两个紫色三角
     */
    public static final Effect lancerLaserShoot = new Effect(21, e->{
        float x = e.x;
        float y = e.y;
        float width = 8f * e.fout();
        float length = 25f;
        e.rotation = 45;
        color(Color.valueOf("ed90df"));
        Drawf.tri(x, y, width, length, e.rotation);
        Drawf.tri(x, y, width, length, 180 + e.rotation);
    }),

    /**
     * 紫色闪电
     */
    lightning = new Effect(10f, 500f, e->{
        if (!(e.data instanceof Seq)) return;
        Seq<Vec2> lines = e.data();

        stroke(3f * e.fout());
        color(e.color, Color.white, e.fin());

        for (int i = 0; i < lines.size - 1; i++) {
            Vec2 cur = lines.get(i);
            Vec2 next = lines.get(i + 1);

            Lines.line(cur.x, cur.y, next.x, next.y, false);
            if (i==lines.size-2){
               IceEffects.lancerLaserShoot.at(next.x, next.y,  new Random().nextInt(360), Color.valueOf("ed90df"));
            }
        }

        for (Vec2 p : lines) {
            Fill.circle(p.x, p.y, Lines.getStroke() / 2f);
        }
    }),

    /**
     * 紫色渐变圆球
     */
    lancerLaserChargeBegin = new Effect(60, e->{
        float margin = 1f - Mathf.curve(e.fin(), 0.9f);
        float fin = Math.min(margin, e.fin());
        color(Pal.spore);
        Fill.circle(e.x, e.y, fin * 4f);
        color(Color.valueOf("ed90df"));
        Fill.circle(e.x, e.y, fin * 2f);
    }),

    /**
     * 紫色粒子效果
     */
    hitLaserBlast = new Effect(12, e->{
        stroke(e.fout() * 1.5f);
        randLenVectors(e.id, 8, e.finpow() * 17f, (x, y)->{
            color(Color.valueOf("ed90df"));
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1f);
        });

    });
}
