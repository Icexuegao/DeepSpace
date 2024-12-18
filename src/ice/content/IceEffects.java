package ice.content;

import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import ice.graphics.IceColors;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;

public class IceEffects {
    /**
     * 两个紫色三角
     */
    public static final Effect lancerLaserShoot1 = new Effect(21, e->{
        float x = e.x;
        float y = e.y;
        float width = 8f * e.fout();
        float length = 16f;
        if (e.rotation == 0) {
            e.rotation = 45;
        }
        color(IceColors.sp);
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
                color(IceColors.sp);
                Fill.circle(e.x, e.y, fin * 2f);
            }), /**
     * 紫色粒子效果
     */
    hitLaserBlast = new Effect(12, e->{
                stroke(e.fout() * 1.5f);
                randLenVectors(e.id, 8, e.finpow() * 17f, (x, y)->{
                    color(IceColors.sp);
                    float ang = Mathf.angle(x, y);
                    lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 3f);
                });

            });
}
