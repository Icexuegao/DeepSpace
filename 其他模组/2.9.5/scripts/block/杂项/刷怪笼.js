const lib = require("base/coflib");

let unitspawn = new ShockMine("刷怪笼");
unitspawn.buildType = prov(() => {
    let Units = [];
    return extend(ShockMine.ShockMineBuild, unitspawn, {
        unitOn(unit) {
            if (unit.team == this.team && !Units.contains(unit)) Units[0] = unit;
        },
        updateTile() {
            if (Units[0] == null) return
            if (this.enabled && this.timer.get(60 / Units[0].type.hitSize)) {
                let us = Units[0].spawn(U.team, U.x, U.y);
                us.rotation = Mathf.random(0, 360);
            }
        }
    })
})
unitspawn.size = 2;
unitspawn.health = 30;
unitspawn.setupRequirements(
    Category.effect,
    BuildVisibility.shown,
    ItemStack.with(
        Items.copper, 200,
        Items.lead, 150
    )
);