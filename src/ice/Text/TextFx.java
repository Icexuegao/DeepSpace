package ice.Text;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.math.Angles.randLenVectors;

public class TextFx {

  public static final Effect jumpTrail = new Effect(120f, 5000, e -> {
        UnitType type = e.data();

        color(type.engineColor == null ? e.color : type.engineColor);

        if(type.engineLayer > 0) Draw.z(type.engineLayer);
        else Draw.z((type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) - 0.001f);
        Draw.alpha(e.fin());
        for(int index = 0; index < type.engines.size; index++){
            UnitType.UnitEngine engine = type.engines.get(index);

            if(Angles.angleDist(engine.rotation, -90) > 75)return;
            float ang = Mathf.slerp(engine.rotation, -90, 0.75f);

            //noinspection SuspiciousNameCombination
            Tmp.v1.trns(e.rotation, engine.y, -engine.x);

            e.scaled(80, i -> {
                DrawFunc(i.x + Tmp.v1.x, i.y + Tmp.v1.y, engine.radius * 1.5f * i.fout(Interp.slowFast), 3000 * engine.radius / (type.engineSize + 4), i.rotation + ang - 90);
                Fill.circle(i.x + Tmp.v1.x, i.y + Tmp.v1.y, engine.radius * 1.5f * i.fout(Interp.slowFast));
            });

            randLenVectors(e.id + index, 22, 400 * engine.radius / (type.engineSize + 4), e.rotation + ang - 90, 0f, (x, y) -> lineAngle(e.x + x + Tmp.v1.x, e.y + y + Tmp.v1.y, Mathf.angle(x, y), e.fout() * 60));
        }

        Draw.color();
        Draw.mixcol(e.color, 1);
      Draw.alpha(e.fin());
        Draw.rect(type.fullIcon, e.x, e.y, type.fullIcon.width * e.fout(Interp.pow2Out) * Draw.scl * 1.2f, type.fullIcon.height * e.fout(Interp.pow2Out) * Draw.scl * 1.5f, e.rotation - 90f);
        Draw.reset();
    });
    public static void DrawFunc(float x, float y, float width, float length, float angle){
        float wx = Angles.trnsx(angle + 90, width), wy = Angles.trnsy(angle + 90, width);
        Fill.tri(x + wx, y + wy, x - wx, y - wy, Angles.trnsx(angle, length) + x, Angles.trnsy(angle, length) + y);
    }
}
