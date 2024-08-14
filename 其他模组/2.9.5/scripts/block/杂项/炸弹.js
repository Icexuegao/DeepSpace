const lib = require("base/coflib");
const my = require("base/物品");
const stats = {
    itemUse: Stat("itemUse", StatCat.function),
    countDown: Stat("countDown", StatCat.function)
};

function Boom(name, range, from, to, itemUse, req) {
    const b = lib.newBlock(name, {
        setStats() {
            this.super$setStats();
            this.stats.add(Stat.damage, range * range);
            this.stats.add(Stat.range, range / 8, StatUnit.blocks);
            this.stats.add(stats.countDown, b.count, StatUnit.seconds);
            this.stats.add(stats.itemUse, StatValues.items(ItemStack.with(itemUse.item, itemUse.amount)));
        },
        canPlaceOn(tile, team, rotation) {
            return tile.floor() == from;
        },
        drawPlace(x, y, rotation, valid) {
            this.super$drawPlace(x, y, rotation, valid);
            Drawf.dashSquare(Pal.accent, x * 8 + this.offset, y * 8 + this.offset, range * 2);
        }
    });
    b.requirements = req;
    b.configurable = false;
    b.buildType = prov(() => extend(Building, {
        time: 60 * b.count,
        flash: 0,
        prog() {
            return 1 - (this.time / b.count / 60);
        },
        updateTile() {
            if (this.items.has(itemUse.item, itemUse.amount)) {
                this.time--
                if (this.time == 0) {
                    this.tile.circle(range / 8 + 1, cons(tile => {
                        if (tile.block() == Blocks.air) {
                            if (tile.floor() == from) tile.setFloor(to);
                        }
                    }));
                    Fx.titanSmoke.at(this.x, this.y, from.mapColor);
                    Fx.titanExplosion.at(this.x, this.y, from.mapColor);
                    Damage.damage(this.x, this.y, range, range * range);
                    Sounds.pulseBlast.at(this);
                    Effect.shake(range / 8, range / 8, this)
                    this.tile.setFloor(to);
                    this.tile.remove();
                }
            }
        },
        drawSelect() {
            this.super$drawSelect();
            Drawf.dashSquare(Pal.accent, this.x, this.y, range * 2);
        },
        acceptItem(source, item) {
            return itemUse.item == item && this.items.get(item) < this.getMaximumAccepted(item);
        },
        draw() {
            this.super$draw();
            Draw.z(Layer.effect);
            Lines.stroke(2, Pal.accent);
            Draw.alpha(this.items.get(itemUse.item) / itemUse.amount);
            Lines.arc(this.x, this.y, range, 1 - this.prog(), 90);
            if (this.prog()) {
                Draw.alpha(0.6);
                Draw.mixcol(lib.Color("FFFFFF00"), lib.Color("FF9575A3"), this.prog());
                Draw.rect(lib.region(name), this.x, this.y);
                this.flash += (1 + this.prog() * 10) * Time.delta;
                Draw.color(lib.Color("FF957580"), lib.Color("FF9575"), Mathf.absin(this.flash, 9, 1));
                Draw.rect(lib.region(name + "-top"), this.x, this.y);
            }
        },
        write(write) {
            this.super$write(write);
            write.f(this.time);
        },
        read(read, revision) {
            this.super$read(read, revision);
            this.time = read.f();
        }
    }))
    return exports[name] = b;
}

Object.assign(Boom("破冰器", 20, Blocks.ice, Blocks.deepwater, {
    item: Items.blastCompound,
    amount: 30
}, ItemStack.with(
    my.铱板, 35,
    my.导能回路, 25
)), {
    count: 5,
    itemCapacity: 30
})