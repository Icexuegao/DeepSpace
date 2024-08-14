const lib = require("base/coflib");
const my = require("base/物品");

function Unloaders(name, speed) {
    let u = extend(Unloader, name, {});
    u.config(Item.__javaObject__, (tile, item) => tile.sortItem = item);
    u.configClear((tile) => tile.sortItem = null);
    u.health = 90;
    u.speed = speed;
    u.itemCapacity = 1;
    u.selectionColumns = 6;
    u.setupRequirements(
        Category.effect,
        BuildVisibility.shown,
        ItemStack.with(
            Items.graphite, 30,
            Items.titanium, 30,
            Items.silicon, 45,
            my.肃正协议, 1
        )
    );
    exports[name] = u;
    return u
};

let 核心卸载器 = Unloaders("核心卸载器", 1);
核心卸载器.buildType = prov(() => {
    let sortItem = null, items = null;
    return new JavaAdapter(Unloader.UnloaderBuild, {
        updateTile() {
            let core = this.team.core();
            sortItem = this.sortItem;
            items = this.items;
            if (sortItem != null && core.items.get(sortItem) > 0) {
                if (items.get(sortItem) < this.block.itemCapacity) {
                    core.items.remove(sortItem, 1);
                    items.add(sortItem, 1);
                }
            }
            ;
            this.dump();
        },
        draw() {
            this.super$draw();
            Draw.z(Layer.effect);
            this.sortItem == null ? Draw.color(Color.white) : Draw.color(this.sortItem.color);
            Draw.alpha(Mathf.sin(0.05 * Time.time));
            Lines.square(this.x, this.y, 4 * Mathf.sin(0.025 * Time.time), 45);
        }
    }, 核心卸载器);
});

let 核心装载器 = Unloaders("核心装载器", 1);
核心装载器.buildType = prov(() => {
    let sortItem = null, items = null, fire = 0;
    return new JavaAdapter(Unloader.UnloaderBuild, {
        acceptItem(source, item) {
            if (item == this.sortItem) {
                return this.items.get(item) < this.getMaximumAccepted(item);
            }
        },
        updateTile() {
            let core = this.team.core();
            sortItem = this.sortItem;
            items = this.items;
            fire = Vars.state.rules.coreIncinerates ? 20 : 1;
            if (sortItem != null && core.items.get(sortItem) < core.getMaximumAccepted(sortItem) / fire) {
                if (items.get(sortItem) == this.getMaximumAccepted(sortItem)) {
                    core.items.add(sortItem, 1);
                    items.remove(sortItem, 1);
                }
            }
        },
        draw() {
            this.super$draw();
            Draw.z(Layer.effect);
            this.sortItem == null ? Draw.color(Color.white) : Draw.color(this.sortItem.color);
            Draw.alpha(-Mathf.sin(0.05 * Time.time));
            Lines.square(this.x, this.y, 4 * Mathf.sin(0.025 * Time.time), 45);
        }
    }, 核心装载器);
});

const speed = 2;
const 液体装卸器 = extend(LiquidSource, "液体装卸器", {
    drawRequestConfig(req, list) {
        this.drawRequestConfigCenter(req, req.config, lib.region("液体装卸器-center"), true);
    },
});
液体装卸器.buildType = prov(() => {
    var dumpingTo = null;
    var offset = 0;
    var liquidBegin = null;
    var source = null;
    var proximity = null;
    return new JavaAdapter(LiquidSource.LiquidSourceBuild, {
        updateTile() {
            source = this.source;
            proximity = this.proximity;
            if (liquidBegin != source) {
                this.liquids.clear();
                liquidBegin = source;
            }
            for (var i = 0; i < proximity.size; i++) {
                var pos = (offset + i) % proximity.size;
                var other = proximity.get(pos);
                if (other.interactable(this.team) && other.block.hasLiquids && !(other instanceof LiquidBlock.LiquidBuild && other.block.size == 1) && source != null && other.liquids.get(source) > 0) {
                    dumpingTo = other;
                    if (this.liquids.currentAmount() < this.block.liquidCapacity) {
                        var amount = Math.min(speed, other.liquids.get(source));
                        this.liquids.add(source, amount);
                        other.liquids.remove(source, amount);
                    }
                }
            }
            if (proximity.size > 0) {
                offset++;
                offset %= proximity.size;
            }
            this.dumpLiquid(this.liquids.current());
        },
        canDumpLiquid(to, liquid) {
            return to != dumpingTo;
        },
        draw() {
            Draw.rect(lib.region("液体装卸器"), this.x, this.y);
            if (this.source != null) {
                Draw.color(this.source.color);
                Draw.rect(lib.region("液体装卸器-center"), this.x, this.y);
                Draw.color();
            }
        },
    }, 液体装卸器);
});
液体装卸器.health = 70;
液体装卸器.liquidCapacity = 10;
液体装卸器.selectionColumns = 6;
液体装卸器.requirements = ItemStack.with(
    Items.lead, 10,
    Items.metaglass, 10,
    Items.silicon, 20
);
液体装卸器.buildVisibility = BuildVisibility.shown;
液体装卸器.category = Category.liquid;
exports.液体装卸器 = 液体装卸器;