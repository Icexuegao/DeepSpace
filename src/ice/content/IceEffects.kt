package ice.content;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static ice.ui.tex.Colors.INSTANCE;

public class IceEffects {
    /**
     * @param lengthSize 该值决定火焰最终长度 子弹的话一般是速度*时间
     */
    public static Effect changeFlame(float lengthSize) {
        return new Effect(32f, 80f, e->{
            color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());
            randLenVectors(e.id, 8, e.finpow() * lengthSize, e.rotation, 10f, (x, y)->Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f));
        });
    }

    public static final Effect lancerLaserShoot1 = new Effect(21, e->{
        float x = e.x;
        float y = e.y;
        float width = 8f * e.fout();
        float length = 16f;
        if (e.rotation == 0) {
            e.rotation = 45;
        }
        color(INSTANCE.get紫色());
        Drawf.tri(x, y, width, length, e.rotation);
        Drawf.tri(x, y, width, length, 180 + e.rotation);
    }), /**
     * 紫色渐变圆球
     */
    lancerLaserChargeBegin = new Effect(60, e->{
                float margin = 1f - Mathf.curve(e.fin(), 0.9f);
                float fin = Math.min(margin, e.fin());
                color(Pal.spore);
                Fill.circle(e.x, e.y, fin * 4f);
                color(INSTANCE.get紫色());
                Fill.circle(e.x, e.y, fin * 2f);
            }), /**
     * 紫色粒子效果
     */
    hitLaserBlast = new Effect(12, e->{
                stroke(e.fout() * 1.5f);
                randLenVectors(e.id, 8, e.finpow() * 17f, (x, y)->{
                    color(INSTANCE.get紫色());
                    float ang = Mathf.angle(x, y);
                    lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 3f);
                });

            });
}
