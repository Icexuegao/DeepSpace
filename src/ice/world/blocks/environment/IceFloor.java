package ice.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import mindustry.world.blocks.environment.Floor;

public class IceFloor extends Floor {
    public static ObjectMap<String, Floor> floors = new ObjectMap<>();

    public IceFloor(String name, int variants) {
        super(name, variants);
        floors.put(name, this);
    }

    @Override
    public TextureRegion[] icons() {
        if (variants==0)return new TextureRegion[]{Core.atlas.find(name)};
        return new TextureRegion[]{Core.atlas.find(name + "1")};
    }
}
