package ice.asundry.world.content.block;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class UnitCoreBlock extends CoreBlock {
    public UnitType units = UnitTypes.mono;

    public UnitCoreBlock(String name) {
        super(name);
        buildType = UnitCoreBlockBuild::new;
    }


    public class UnitCoreBlockBuild extends CoreBlock.CoreBuild {
        public float time = 0f;
        public byte readUnitId = -1;
        public Unit unit;

        @Override
        public void updateTile() {
            super.updateTile();
        }

        float progressIncrease = 0;
        boolean first =false;
        @Override
        public void drawSelect() {
            if (!first){
                progressIncrease+=getProgressIncrease(60);
            }else {
                progressIncrease-=getProgressIncrease(60);
            }

            if (progressIncrease>=1){
             first=true;
         }else if (progressIncrease<=0){
             first=false;
         }

            Draw.alpha(progressIncrease);
            Fill.square(x, y, Vars.tilesize/2f);

            Log.info(progressIncrease);
        }

        @Override
        public void remove() {
            super.remove();
        }

        @Override
        public Building init(Tile tile, Team team, boolean shouldAdd, int rotation) {
            return super.init(tile, team, shouldAdd, rotation);
        }

        @Override
        public void read(Reads read, byte revision) {
            readUnitId = read.b();
            time = read.f();
            super.read(read, revision);
        }

        @Override
        public void write(Writes write) {
            write.b(readUnitId);
            write.f(time);
            super.write(write);
        }
    }

}