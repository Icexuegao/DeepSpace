const lib = require("base/coflib");
const NX = require("base/Effect/normalFx");
const {欧米茄} = require("unit/units");

const nuck = extend(UnitCargoLoader, "折跃枢纽", {
    canPlaceOn(tile, team, rotation) {
        if (lib.mapLimit()) return true;
        return Vars.state.teams.cores(team).size >= 4 && Vars.state.teams.get(team).getCount(this) < 4;
    },
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        if (lib.mapLimit()) return
        if (Vars.state.teams.cores(Vars.player.team()).size < 4) {
            this.drawPlaceText(lib.bundle("text-needCore", 4), x, y, valid);
        } else if (Vars.state.teams.get(Vars.player.team()).getCount(this) >= 4) {
            this.drawPlaceText(lib.limitBuild(this, 4), x, y, valid);
        }
    }
});
nuck.size = 9;

lib.setBuilding(UnitCargoLoader.UnitTransportSourceBuild, nuck, {
    spawned(id) {
        this.super$spawned(id);
        NX.JumpIn(欧米茄, this.x, this.y).at(this.x, this.y, 0, Pal.accent);
    },
    draw() {
        this.super$draw();
        if (this.unit == null) {
            if (this.buildProgress == 0) return
            NX.DoubleAim(this, this.buildProgress, 欧米茄.hitSize * 3, 1, 14, nuck.size);
        } else {
            NX.CenterTri(this, nuck.size * 4, 0.5, 4);
        }
        //NX.QuadrupleTri(this, 5, 7, 5);
    }/*,
	write(write) {
		this.super$write(write);
		write.f(this.buildProgress);
	},
	read(read, revision) {
		this.super$read(read, revision);
		this.buildProgress = read.f() ? read.f() : 0;
	}*/
})