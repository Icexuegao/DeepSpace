package ice.asundry.Var;
import ice.asundry.Content.IceBlocks;
import arc.func.Prov;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class IceTile extends Tile {


    public IceTile(int x, int y) {
        super(x, y);
    }

    public void setFloor(Floor floor) {
       this.floor=floor;
    }
    public void clearBlock(Block block) {
        this.block=block;
    }
    public void clearBuild(){
        build=null;
    }
    public void set() {
        Block block = IceBlocks.randomer;
        newSetBlock(block, Team.sharded, 1, block::newBuilding);
    }

    public void newSetBlock(Block type, Team team, int rotation, Prov<Building> entityprov) {
        changing = true;
        if (type.isStatic() || this.block.isStatic()) {
            recache();
            recacheWall();
        }
        preChanged();

        block = type;
        changeBuild(team, entityprov, (byte) Mathf.mod(rotation, 4));

        if (build != null) {
            build.team(team);
        }



        //设置多块
        if (block.isMultiblock()) {
            int size = block.size;
            int offset = -(size - 1) / 2;
            Building entity = this.build;
            Block block = this.block;

            //两次传球，第一次解围，第二次传球
            for (int pass = 0; pass < 2; pass++) {
                for (int dx = 0; dx < size+2; dx++) {
                    for (int dy = 0; dy < size; dy++) {
                        int worldx = dx + offset + x;
                        int worldy = dy + offset + y;
                        if (!(worldx == x && worldy == y)) {
                            /*IceTile other =new IceTile(worldx, worldy);*/
                            Tile other = Vars.world.tile(worldx, worldy);
                            IceTile otherTile = new IceTile(worldx, worldy);
                            if (other != null) {
                                if (pass == 0) {
                                    //第一遍:删除现有的块-如果存在重叠，这应该会自动触发删除
                                    //TODO pointless setting air to air?

//                                    other.setBlock(Blocks.air);
                                    Log.info("block清除了");
                                } else {
                                    //第二遍:分配更改的数据
                                    //将实体和类型分配给块，以便它们充当此块的代理
                                    otherTile.floor=other.floor();
                                    otherTile.overlay=other.overlay();

                                    if (block==Blocks.air){
                                        otherTile.block=Blocks.air;
                                        otherTile.build = null;
                                    }else {
                                        otherTile.block=type;
                                        otherTile.build = entity;
                                    }
                                    Vars.world.tiles.set(worldx, worldy, otherTile);
                                }
                            }
                        }
                    }
                }
            }

            this.build = entity;
            this.block = block;
        }
        changed();
        changing = false;
    }

    @Override
    public void setBlock(Block type, Team team, int rotation, Prov<Building> entityprov) {
        changing = true;

        if(type.isStatic() || this.block.isStatic()){
            recache();
            recacheWall();
        }

        preChanged();

        this.block = type;
        changeBuild(team, entityprov, (byte)Mathf.mod(rotation, 4));

        if(build != null){
            build.team(team);
        }

        //设置多块
        if(block.isMultiblock()){
            int offset = -(block.size - 1) / 2;
            Building entity = this.build;
            Block block = this.block;

            //两次传球，第一次解围，第二次传球
            for(int pass = 0; pass < 2; pass++){
                for(int dx = 0; dx < block.size+2; dx++){
                    for(int dy = 0; dy < block.size; dy++){
                        int worldx = dx + offset + x;
                        int worldy = dy + offset + y;
                        if(!(worldx == x && worldy == y)){
                            Tile other = Vars.world.tile(worldx, worldy);
                            IceTile otherTile = new IceTile(worldx, worldy);
                            if(other != null){
                                if(pass == 0){
                                    //第一遍:删除现有的块-如果存在重叠，这应该会自动触发删除
                                    //TODO pointless setting air to air?
                                    other.setBlock(Blocks.air);
                                }else{
                                    //第二遍:分配更改的数据
                                    //将实体和类型分配给块，以便它们充当此块的代理

                                    otherTile.floor=other.floor();
                                    otherTile.overlay=other.overlay();
                                    if (type.name.equals("build2")){
                                        otherTile.block=Blocks.air;
                                        otherTile.build = null;
                                    }else {
                                        otherTile.build = entity;
                                        otherTile.block=type;
                                    }
                                    Vars.world.tiles.set(worldx, worldy, otherTile);
                                }
                            }
                        }
                    }
                }
            }

            this.build = entity;
            this.block = type;
        }

        changed();
        changing = false;
        super.setBlock(type, team, rotation, entityprov);
    }
}
