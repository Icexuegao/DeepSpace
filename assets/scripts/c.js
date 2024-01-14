const white = Object.assign(new Floor("white"),{variants: 0})
const black = Object.assign(new Floor("black"),{variants: 0});

const ant = new Block("ant");
Object.assign(ant,{
    update: true,
    rotate: true,
    solid: true,
    buildVisibility: BuildVisibility.shown,
})
ant.buildType = prov(() => extend(Building, {
    tox:0,
    toy:0,
    time:0,
    updateTile(){
        this.time += Time.delta;
        if(this.time >= 30){
            if(this.tile.floor() == black){
                this.rotation -= 1
                
                this.ox = Angles.trnsx(this.rotation * 90, 1, 0);
                this.oy = Angles.trnsy(this.rotation * 90, 1, 0);
                
                this.tile.setFloor(white)
            }else{
                this.rotation += 1
                
                this.ox = Angles.trnsx(this.rotation * 90, 1, 0);
                this.oy = Angles.trnsy(this.rotation * 90, 1, 0);
                
                this.tile.setFloor(black);
            }
            Vars.world.tile(
                this.ox + this.tileX(),
                this.oy + this.tileY()
            ).setBlock(ant,this.team,this.rotation)
            
            this.tile.setAir();
            
            this.time = 0
        }
    }
}))