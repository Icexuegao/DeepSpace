package Iceconent.content;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import mindustry.maps.generators.PlanetGenerator;

public class IcePlanetGenerator2 extends PlanetGenerator {

    @Override
    public float getHeight(Vec3 vec3) {
        return 0;
    }

    @Override
    public Color getColor(Vec3 vec3) {
        return null;
    }

    @Override
    public boolean skip(Vec3 position) {
        return super.skip(position);
    }
}
