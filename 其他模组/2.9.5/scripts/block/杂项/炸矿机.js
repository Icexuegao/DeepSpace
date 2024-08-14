const lib = require("base/coflib");
let range = 10, amount = 0, num = 3;

let sf = lib.newBlock("setFloor", {});
lib.setBuilding(Building, sf, {
    buildConfiguration(table) {
        Vars.content.blocks().each(block => {
            if (block instanceof OreBlock && !block.wallOre) {
                table.button(Core.atlas.drawable(block.itemDrop.uiIcon), Styles.cleari, run(() => {
                    this.tile.circle(range, cons(tile => {
                        tile.setOverlay(block)
                    }));
                    this.kill();
                })).size(45);
                amount++;
                if (amount % num == 0) table.row()
            }
        })
    }
})
sf.buildVisibility = BuildVisibility.sandboxOnly;