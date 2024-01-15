package Iceconent.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;

public class IceEffect {
    public static final Effect gc, gc1;

    static {
        gc /**  方块 */ = new Effect(60.0F, (e) -> Angles.randLenVectors(e.id, 6, 8.0F + e.fin() * 5.0F, (x, y) -> {
            Draw.color(Color.valueOf("ff7439"), e.color, e.fin());
            Fill.square(e.x + x, e.y + y, 0.5F + e.fout() * 2.0F, 45.0F);
        }));
        gc1  /**  烟雾 */ = new Effect(80.0F, (e) -> Angles.randLenVectors(e.id, 0.2F + e.fin(), 4, 6.3F, (x, y, fin, out) -> {
            Draw.color(Color.valueOf("ff9c71"), Pal.coalBlack, e.finpowdown());
            Fill.circle(e.x + x, e.y + y, out * 2.0F + 0.35F);
        }));
    }
}
