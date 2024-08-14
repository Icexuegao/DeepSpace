const lib = require("base/coflib");
const {异种} = require("unit/units");
let yz = extend(Wall, "异种", {
    update: true,
    canPlaceOn(tile, team, rotation) {
        return team.data().countType(异种) < Units.getCap(team);
    }
});
lib.setBuilding(Wall.WallBuild, yz, {
    updateTile() {
        this.super$updateTile();
        let u = 异种.spawn(this);
        u.rotation = 90;
        this.kill();
    },
    onDestroyed() {
    }
});