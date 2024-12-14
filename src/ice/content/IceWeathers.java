package ice.content;

import ice.type.weather.RedRainWeather;
import mindustry.content.StatusEffects;
import mindustry.gen.Sounds;
import mindustry.type.Weather;
import mindustry.world.meta.Attribute;

public class IceWeathers {
    public static Weather redRainWeather;

    public static void load() {
        redRainWeather = new RedRainWeather("redRainWeather") {{
            attrs.set(Attribute.light, -0.2f);
            attrs.set(Attribute.water, 0.2f);
            status = StatusEffects.none;
            sound = Sounds.rain;
            soundVol = 0.25f;
        }};
    }
}
