package ice.type.weather;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import ice.content.IceLiquids;
import mindustry.gen.WeatherState;
import mindustry.type.Liquid;
import mindustry.type.Weather;

import static mindustry.Vars.renderer;
import static mindustry.Vars.world;

public class RedRainWeather extends Weather {
    public float yspeed = 5f, xspeed = 1.5f, padding = 16f, density = 1200f, stroke = 0.75f, sizeMin = 8f, sizeMax = 40f, splashTimeScale = 22f;
    public Liquid liquid = IceLiquids.血浆;
    public TextureRegion[] splashes = new TextureRegion[12];
    public Color color = Color.valueOf("ff3430");

    public RedRainWeather(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        for (int i = 0; i < splashes.length; i++) {
            splashes[i] = Core.atlas.find("splash-" + i);
        }
    }

    @Override
    public void drawOver(WeatherState state) {
        drawRain(sizeMin, sizeMax, xspeed, yspeed, density, state.intensity, stroke, color);
    }

    @Override
    public void drawUnder(WeatherState state) {
        drawSplashes(splashes, sizeMax, density, state.intensity, state.opacity, splashTimeScale, stroke, color, liquid);
    }

    public static void drawRain(float sizeMin, float sizeMax, float xspeed, float yspeed, float density, float intensity, float stroke, Color color) {
        rand.setSeed(0);
        float padding = sizeMax * 0.9f;

        Tmp.r1.setCentered(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth() / renderer.minScale(), Core.graphics.getHeight() / renderer.minScale());
        Tmp.r1.grow(padding);
        Core.camera.bounds(Tmp.r2);
        /**个数*/
        int total = (int) (Tmp.r1.area() / density * intensity);

        Lines.stroke(stroke);
        float alpha = Draw.getColor().a;
        Draw.color(color);

        for (int i = 0; i < total; i++) {
            float scly = rand.random(0.5f, 1f);
            float sclx = rand.random(0.5f, 1f);
            float size = rand.random(sizeMin, sizeMax);
            float x = (rand.random(0f, world.unitWidth()) + Time.time * xspeed * sclx);
            float y = (rand.random(0f, world.unitHeight()) - Time.time * yspeed * scly);
            float tint = rand.random(1f) * alpha;

            /**防止跟随玩家移动*/
            x -= Tmp.r1.x;
            y -= Tmp.r1.y;

            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;

            if (Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2)) {
                Draw.alpha(tint);
                //  Draw.rect(Items.copper.uiIcon,x,y,Time.time);
                Lines.lineAngle(x, y, Angles.angle(xspeed * sclx, -yspeed * scly), size / 2f);
            }
        }
    }
}
