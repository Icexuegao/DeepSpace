package ice.Elegy.world.blocks;

import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

public class RangeFloorReplacementScript {
    //import
    //floor,the floor you want to use it to replace the floors locates here,
    //buildings,normally fill "this",
    //range,the range you want to replace,it is the side length of the square
    //use it in "Blocks"
    public static void floorReplace(Block replacingFloor,Building orderdBuilding, int range) {
        int x = orderdBuilding.tile.x;
        int y = orderdBuilding.tile.y;

        for (int a = x-range; a <= x + range; a++) {
            for (int b = y-range; b <= y + range; b++) {
                world.tile(a, b).setFloor((Floor) replacingFloor);
            }
        }

    }
}
