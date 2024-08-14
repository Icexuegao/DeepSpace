const lib = require("base/coflib");
const white = new Floor("white");
white.variants = 0;
const black = new Floor("black");
black.variants = 0;

const ant = lib.newBlock("ant");
Object.assign(WallBreaker, {
    rotate: true,
    configurable: false
});
lib.setBuilding(Building, {
    updateTile() {
        if (this.time.get(30)) {
            if (this.tile.floor() == black) {
                this.rotation -= 1;
                this.tile.setFloor(white)
            } else {
                this.rotation += 1;
                this.tile.setFloor(black);
            }
            let xy = lib.AngleTrns(rotation * 90, 1, 0);
            Vars.world.tile(xy.x + this.tileX(), xy.y + this.tileY()).setBlock(ant, this.team, this.rotation)
            this.tile.setAir();
        }
    }
})