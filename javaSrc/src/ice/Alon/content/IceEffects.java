package ice.Alon.content;

import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.graphics.Layer;

import static arc.graphics.g2d.Draw.rect;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.renderer;

public class IceEffects {
    public static final Effect airBubble;

    static {
        airBubble = new Effect(100f, e -> {
            randLenVectors(e.id, 1, e.fin() * 12f, (x, y) -> {
                rect(renderer.bubbles[Math.min((int) (renderer.bubbles.length * Mathf.curveMargin(e.fin(), 0.11f, 0.06f)), renderer.bubbles.length - 1)], e.x + x, e.y + y);
            });
        }).layer(Layer.flyingUnitLow + 1);
    }

}
