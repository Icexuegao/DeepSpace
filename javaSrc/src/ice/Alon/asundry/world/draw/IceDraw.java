package ice.Alon.asundry.world.draw;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class IceDraw {
    public static void lightningOrb(float x, float y, float radius, Color color1, Color color2){
        Draw.z(Layer.effect - 0.001f);
        Draw.color(color1);
        Fill.circle(x, y, radius);

        int n = 3;
        Draw.color(color2);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 3f, radius - 5f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 7f), radius * 4f, i * 360f / n - Time.globalTime / 3f + 110f);
        }
        n = 4;
        Draw.color(color1);
        for(int i = 0; i < n; i++){
            Tmp.v1.trns(i * 360f / n - Time.globalTime / 2f, radius - 3f).add(x, y);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Math.min(radius, 7f), radius * 5f, i * 360f / n - Time.globalTime / 2f + 100f);
        }

        Draw.z(Layer.effect + 0.002f);
        Draw.color();
        Drawf.tri(x, y, radius * 0.6f, radius * 1.7f, Time.time * 1.7f + 60f);
        Drawf.tri(x, y, radius * 0.6f, radius * 1.7f, Time.time * 1.7f + 60f + 180f);
        Fill.circle(x, y, radius * 0.8f);

        Draw.blend(Blending.additive);
        Lines.stroke(Math.min(1.5f, radius));
        Draw.color(color1);
        Lines.poly(x, y, Mathf.random(7) + 5, radius * 1.8f, Mathf.random(360f));
        Lines.stroke(Math.min(1f, radius));
        Draw.color(color2);
        Lines.poly(x, y, Mathf.random(7) + 5, radius * 2.2f, Mathf.random(360f));
        Draw.color();
        Draw.blend();

        if(Vars.renderer.lights.enabled()) Drawf.light(x, y, radius * 9f, color2, 1f);
    }
}
