package Ice.asundry.world.bullet;


import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Bullets;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Unitc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Tile;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.*;
import static arc.graphics.g2d.Lines.endLine;
import static mindustry.Vars.*;

public class ThickLightning {
    private static final  Effect thickLightning = new Effect(12f, 1300f, e -> {
        if(!(e.data instanceof Seq)) return;
        Seq<Vec2> lines = e.data();
        int n = Mathf.clamp(1 + (int)(e.fin() * lines.size), 1, lines.size);
        for(int i = 2; i >= 0; i--){
            stroke(4.5f * (i / 2f + 1f));
            color(i == 0 ? Color.white : e.color);
            alpha(i == 2 ? 0.5f : 1f);

            beginLine();
            for(int j = 0; j < n; j++){
                linePoint(lines.get(j).x, lines.get(j).y);
            }
            endLine(false);
        }

        if(renderer.lights.enabled()){
            for(int i = 0; i < n - 1; i++){
                Drawf.light(lines.get(i).x, lines.get(i).y, lines.get(i+1).x, lines.get(i+1).y, 40f, e.color, 0.9f);
            }
        }
    }),
            thickLightningHit = new Effect(80f, 100f, e -> {
                color(Color.white, e.color, e.fin());

                for(int i = 2; i >= 0; i--){
                    float s = 4.5f * (i / 2f + 1f) * e.fout();
                    color(i == 0 ? Color.white : e.color);
                    alpha((i == 2 ? 0.5f : 1f) * e.fout());
                    for(int j = 0; j < 6; j++){
                        Drawf.tri(e.x, e.y, 2f * s, (s + 35f + Mathf.randomSeed(e.id - j, 95f)) * e.fout(), Mathf.randomSeedRange(e.id + j, 360f));
                    }
                }

                Draw.z(Layer.effect - 0.001f);
                stroke(3f * e.fout(), e.color);
                float r = 55f * e.finpow();
                Fill.light(e.x, e.y, circleVertices(r), r, Tmp.c4.set(e.color).a(0f), Tmp.c3.set(e.color).a(e.fout()));
                circle(e.x, e.y, r);
                if(renderer.lights.enabled()) Drawf.light(e.x, e.y, r * 3.5f, e.color, e.fout(0.5f));
            }).layer(Layer.effect + 0.001f),

    thickLightningFade = new Effect(80f, 1300f, e -> {
        if(!(e.data instanceof Seq)) return;
        Seq<Vec2> lines = e.data();
        for(int i = 2; i >= 0; i--){
            stroke(4.5f * (i / 2f + 1f) * e.fout());
            color(i == 0 ? Color.white : e.color);
            alpha((i == 2 ? 0.5f : 1f) * e.fout());

            beginLine();
            for(Vec2 p : lines){
                linePoint(p.x, p.y);
            }
            endLine(false);
        }

        if(renderer.lights.enabled()){
            for(int i = 0; i < lines.size - 1; i++){
                Drawf.light(lines.get(i).x, lines.get(i).y, lines.get(i+1).x, lines.get(i+1).y, 40f, e.color, 0.9f * e.fout());
            }
        }
    }),

    thickLightningStrike = new Effect(80f, 100f, e -> {
        color(Color.white, e.color, e.fin());

        for(int i = 2; i >= 0; i--){
            float s = 4.5f * (i / 2f + 1f) * e.fout();
            color(i == 0 ? Color.white : e.color);
            alpha((i == 2 ? 0.5f : 1f) * e.fout());
            for(int j = 0; j < 3; j++){
                Drawf.tri(e.x, e.y, 2f * s, (s + 65f + Mathf.randomSeed(e.id - j, 95f)) * e.fout(), e.rotation + Mathf.randomSeedRange(e.id + j, 80f) + 180f);
            }
        }

        Draw.z(Layer.effect - 0.001f);
        stroke(3f * e.fout(), e.color);
        float r = 55f * e.finpow();
        Fill.light(e.x, e.y, circleVertices(r), r, Tmp.c4.set(e.color).a(0f), Tmp.c3.set(e.color).a(e.fout()));
        circle(e.x, e.y, r);
        if(renderer.lights.enabled()) Drawf.light(e.x, e.y, r * 3.5f, e.color, e.fout(0.5f));
    }).layer(Layer.effect + 0.001f)
            ;
    private static final Rand random = new Rand();
    private static final Rect rect = new Rect();
    private static final Seq<Unitc> entities = new Seq<>();
    private static final IntSet hit = new IntSet();
    private static final int maxChain = 8;
    private static final float hitRange = 60f;
    private static boolean bhit = false;
    private static int lastSeed = 0;

    /** Create a lighting branch at a location. Use Team.derelict to damage everyone. */
    public static void create(Team team, Color color, float damage, float x, float y, float targetAngle, int length){
        createLightningInternal(null, lastSeed++, team, color, damage, x, y, targetAngle, length);
    }

    /** Create a lighting branch at a location. Uses bullet parameters. */
    public static void create(Bullet bullet, Color color, float damage, float x, float y, float targetAngle, int length){
        createLightningInternal(bullet, lastSeed++, bullet.team, color, damage, x, y, targetAngle, length);
    }

    private static void createLightningInternal(@Nullable Bullet hitter, int seed, Team team, Color color, float damage, float x, float y, float rotation, int length){
        random.setSeed(seed);
        hit.clear();

        BulletType hitCreate = hitter == null || hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
        Seq<Vec2> lines = new Seq<>();
        bhit = false;

        //is this necessary?
        lines.add(new Vec2(x, y));

        for(int i = 0; i < length / 2; i++){
            hitCreate.create(null, team, x, y, rotation, damage, 1f, 1f, hitter);
            lines.add(new Vec2(x + Mathf.range(15f), y + Mathf.range(15f)));

            if(lines.size > 1){
                bhit = false;
                Vec2 from = lines.get(lines.size - 2);
                Vec2 to = lines.get(lines.size - 1);
                World.raycastEach(World.toTile(from.getX()), World.toTile(from.getY()), World.toTile(to.getX()), World.toTile(to.getY()), (wx, wy) -> {

                    Tile tile = world.tile(wx, wy);
                    if(tile != null && tile.build != null && tile.solid() && tile.team() != team){ //it is blocked by all blocks, not just insulated ones; This just looks way better.
                        bhit = true;
                        //snap it instead of removing
                        lines.get(lines.size - 1).set(wx * tilesize, wy * tilesize);
                        return true;
                    }
                    return false;
                });
                if(bhit) break;
            }

            rect.setSize(hitRange).setCenter(x, y);
            entities.clear();
            if(hit.size < maxChain){
                Units.nearbyEnemies(team, rect, u -> {
                    if(!hit.contains(u.id()) && (hitter == null || u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround))){
                        entities.add(u);
                    }
                });
            }

            Unitc furthest = Geometry.findFurthest(x, y, entities);

            if(furthest != null){
                hit.add(furthest.id());
                x = furthest.x();
                y = furthest.y();
            }else{
                rotation += random.range(20f);
                x += Angles.trnsx(rotation, hitRange / 2f);
                y += Angles.trnsy(rotation, hitRange / 2f);
            }
        }

        thickLightning.at(x, y, rotation, color, lines);
        // fInaL OR effECtiVElY fINAL
        float finalRotation = rotation;
        float finalX = x;
        float finalY = y;
        Time.run(thickLightning.lifetime, () -> {
            thickLightningFade.at(finalX, finalY, finalRotation, color, lines);
            int n = Mathf.random(5);
            if(bhit){
                for(int j = 0; j < n; j++) Lightning.create(team, color, damage * 0.2f, lines.peek().x, lines.peek().y, finalRotation + Mathf.range(30f), length / 3);
                thickLightningStrike.at(lines.peek().x, lines.peek().y, finalRotation, color);
            }
            else{
                for(int j = 0; j < n; j++) Lightning.create(team, color, damage * 0.2f, lines.peek().x, lines.peek().y, Mathf.random(360f), length / 3);
                thickLightningHit.at(lines.peek().x, lines.peek().y, finalRotation, color);
            }

            if(!headless){
                Effect.shake(6f, 5.5f, finalX, finalY);
            }
        });
    }
}
