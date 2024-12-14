package ice.world.blocks.environment;

import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;

public class IceStaticWall extends StaticWall {
    public IceStaticWall(String name) {
        super(name);
        Floor floor = IceFloor.floors.get(name.replace("Wall", ""));
        if (floor != null) floor.asFloor().wall = this;
    }
}
