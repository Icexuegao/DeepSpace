const lib = require("base/coflib");

const WallBreaker = lib.newBlock("墙体拆除器", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        let xy = lib.AngleTrns(rotation * 90, 1, 0);
        for (let i = 1; i <= 5; i++) {
            Drawf.dashSquare(Pal.accent, x * 8 + this.offset + xy.x * 8 * i, y * 8 + this.offset + xy.y * 8 * i, 8);
        }
    }
});
Object.assign(WallBreaker, {
    size: 1,
    health: 100,
    rotate: true,
    configurable: false
});

WallBreaker.requirements = ItemStack.with(
    Items.copper, 480,
    Items.graphite, 200,
    Items.silicon, 180,
    Items.plastanium, 80
);

WallBreaker.buildType = prov(() => extend(Building, {
    time: 360,
    i: 1,
    updateTile() {
        this.super$updateTile();
        this.dump();
        this.time -= Time.delta;
        if (this.time > 0) Vars.ui.showLabel("拆除中,剩余[accent]" + (Math.round(this.time / 60)) + "[]秒", 0.01, this.x, this.y);
        if (this.time <= 0) {
            let xy = lib.AngleTrns(this.rotation * 90, 1, 0);
            Vars.world.tile(this.tileX() + xy.x * this.i, this.tileY() + xy.y * this.i).setAir();
            if (this.i <= 5) {
                this.i += 1;
                this.time = 360;
                this.items.add(Items.sand, 20);
            } else if (this.items.get(Items.sand) == 0) {
                this.kill();
            }
        }
    }
}))