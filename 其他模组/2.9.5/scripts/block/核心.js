const lib = require("base/coflib");
const my = require("base/物品");
const HX = require("base/Effect/hitFx");
let pos = 24, range = 340 + pos;
const core1 = extend(CoreBlock, "先遣核心", {
    canBreak(tile) {
        return Vars.state.teams.cores(tile.team()).size > 1;
    },
    canReplace(other) {
        return other.alwaysReplace;
    },
    canPlaceOn(tile, team, rotation) {
        if (lib.mapLimit()) return true;
        return Vars.state.teams.cores(team).size < 8;
    },
    drawPlace(x, y, rotation, valid) {
        if (lib.mapLimit()) return
        if (Vars.state.teams.cores(Vars.player.team()).size < 8) return
        this.drawPlaceText(lib.bundle("text-limitCore", 8), x, y, valid);
    }
});
exports.先遣核心 = core1;

let powerOut = 1800, powerOut2 = powerOut * 2;
const core2 = extend(CoreBlock, "终极核心", {
    canReplace(other) {
        return true;
    },
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * 8 + this.offset, y * 8 + this.offset, 384, Pal.accent);
    },
    setStats() {
        this.super$setStats();
        this.stats.add(lib.damageReduction, lib.bundle("damageReduction", 25));
        this.stats.add(Stat.basePowerGeneration, powerOut, StatUnit.powerSecond);
    },
    setBars() {
        this.super$setBars();
        this.addBar("power", func(e => new Bar(
            prov(() => lib.bundle("bar.poweroutput", Strings.fixed(powerOut, 1))),
            prov(() => Pal.powerBar),
            floatp(() => 1)
        )));
    }
});
core2.hasPower = true;
core2.outputsPower = true;
core2.consumesPower = false;
core2.configurable = true;

const core3 = extend(CoreBlock, "最终教条", {
    canReplace(other) {
        return true;
    },
    canPlaceOn(tile, team, rotation) {
        if (lib.mapLimit()) return true;
        return tile.block() instanceof CoreBlock && tile.block().size == 8 && Vars.state.teams.get(team).getCount(this) < 4;
    },
    drawPlace(x, y, rotation, valid) {
        Drawf.dashCircle(x * 8 + this.offset, y * 8 + this.offset, range, Pal.accent);
        if (lib.mapLimit()) return
        if (Vars.world.tile(x, y).block().size != 8) {
            this.drawPlaceText(lib.bundle("text-needCoreBase"), x, y, valid);
        } else if (Vars.state.teams.get(Vars.player.team()).getCount(this) >= 4) {
            this.drawPlaceText(lib.limitBuild(this, 4), x, y, valid);
        }
    },
    setStats() {
        this.super$setStats();
        this.stats.add(lib.damageReduction, lib.bundle("damageReduction", 50));
        this.stats.add(lib.maxDamage, lib.bundle("maxDamage", 5400));
        this.stats.add(Stat.basePowerGeneration, powerOut2, StatUnit.powerSecond);
    },
    setBars() {
        this.super$setBars();
        this.addBar("power", func(e => new Bar(
            prov(() => lib.bundle("bar.poweroutput", Strings.fixed(powerOut2, 1))),
            prov(() => Pal.powerBar),
            floatp(() => 1)
        )));
    }
});
core3.hasPower = true;
core3.outputsPower = true;
core3.consumesPower = false;
core3.configurable = true;

function Pay(block) {
    return new BuildPayload(block, Team.derelict)
}

function cool(to, from, erekir) {
    if (erekir) {
        if (Liquids.water.unlockedNow()) {
            if (to.acceptLiquid(from, Liquids.water)) {
                to.handleLiquid(from, Liquids.water, 1);
            }
        }
    } else {
        if (Liquids.cryofluid.unlockedNow()) {
            if (to.acceptLiquid(from, Liquids.cryofluid)) {
                to.handleLiquid(from, Liquids.cryofluid, 1);
            }
        } else if (Liquids.water.unlocked()) {
            if (to.acceptLiquid(from, Liquids.water)) {
                to.handleLiquid(from, Liquids.water, 1);
            }
        }
    }
}

function CoreTurret(build, block, from, to, erekir) {
    build.buildType = prov(() => {
        const p = Pay(block), pb = p.build;
        return extend(CoreBlock.CoreBuild, build, {
            updateTile() {
                this.super$updateTile();
                if (pb.team != this.team) pb.team = this.team;
                p.update(null, this);
                if (to.unlockedNow()) {
                    if (pb.acceptItem(this, to)) {
                        pb.handleItem(this, to);
                    }
                } else {
                    if (pb.acceptItem(this, from)) {
                        pb.handleItem(this, from);
                    }
                }
                cool(pb, this, erekir);
                p.set(this.x, this.y, pb.payloadRotation);
            },
            draw() {
                this.super$draw();
                p.draw();
            },
            drawSelect() {
                this.super$drawSelect();
                Drawf.dashCircle(this.x, this.y, block.range, Pal.accent);
            }
        })
    });
}

CoreTurret(Blocks.coreShard, Blocks.duo, Items.copper, Items.graphite);
CoreTurret(Blocks.coreFoundation, Blocks.salvo, Items.graphite, Items.thorium);
CoreTurret(Blocks.coreNucleus, Blocks.cyclone, Items.plastanium, Items.surgeAlloy);
CoreTurret(Blocks.coreBastion, Blocks.breach, Items.beryllium, Items.tungsten, true);
CoreTurret(Blocks.coreCitadel, Blocks.diffuse, Items.graphite, Items.graphite, true);
CoreTurret(Blocks.coreAcropolis, Blocks.smite, Items.surgeAlloy, Items.surgeAlloy, true);

let Ω = new ItemTurret("Ω");
const TItems = Seq.with(Items.graphite, Items.thorium, Items.phaseFabric, Items.surgeAlloy, my.铱板, my.低温化合物, my.生物钢, my.铈凝块);
core2.config(Item, new Cons2((build, item) => build.setBullet(item)));
core3.config(Item, new Cons2((build, item) => build.setBullet(item)));
core2.buildType = prov(() => {
    const p = Pay(Ω), pb = p.build;
    let Bullet = null;
    return extend(CoreBlock.CoreBuild, core2, {
        getPowerProduction() {
            return powerOut / 60;
        },
        setBullet(item) {
            Bullet = item;
        },
        buildConfiguration(table) {
            ItemSelection.buildTable(this.block, table, TItems, () => Bullet, value => this.configure(value));
        },
        updateTile() {
            this.super$updateTile();
            if (pb.team != this.team) pb.team = this.team;
            p.update(null, this);
            if (Bullet != null) {
                if (pb.acceptItem(this, Bullet)) {
                    pb.handleItem(this, Bullet);
                }
            } else {
                if (pb.acceptItem(this, Items.thorium)) {
                    pb.handleItem(this, Items.thorium);
                }
            }
            cool(pb, this);
            p.set(this.x, this.y, pb.payloadRotation);
        },
        draw() {
            this.super$draw();
            p.draw();
        },
        drawSelect() {
            this.super$drawSelect();
            Drawf.dashCircle(this.x, this.y, 340, Pal.accent);
        },
        handleDamage(amount) {
            amount = Math.min(amount * 10, amount + this.block.armor);
            return Damage.applyArmor(amount * 0.75, this.block.armor);
        },
        write(write) {
            this.super$write(write);
            write.s(Bullet == null ? -1 : Bullet.id);
        },
        read(read, revision) {
            this.super$read(read, revision);
            let id = read.s();
            Bullet = id == -1 ? null : Vars.content.item(id);
        }
    })
});

core3.buildType = prov(() => {
    const pays = [Pay(Ω), Pay(Ω), Pay(Ω), Pay(Ω)];
    let Bullet = null, dodge = false;
    return extend(CoreBlock.CoreBuild, core3, {
        getPowerProduction() {
            return powerOut2 / 60;
        },
        setBullet(item) {
            Bullet = item;
        },
        buildConfiguration(table) {
            ItemSelection.buildTable(this.block, table, TItems, () => Bullet, value => this.configure(value));
        },
        updateTile() {
            this.super$updateTile();
            for (let p of pays) {
                let pb = p.build;
                if (pb.team != this.team) pb.team = this.team;
                p.update(null, this);
                if (Bullet != null) {
                    if (pb.acceptItem(this, Bullet)) {
                        pb.handleItem(this, Bullet);
                    }
                } else {
                    if (pb.acceptItem(this, Items.thorium)) {
                        pb.handleItem(this, Items.thorium);
                    }
                }
                cool(pb, this);
                let xy = lib.AngleTrns(90 * pays.indexOf(p), pos);
                p.set(this.x + xy.x + xy.y, this.y + xy.x - xy.y, pb.payloadRotation);
            }
        },
        draw() {
            this.super$draw();
            for (let p of pays) p.draw();
        },
        drawSelect() {
            this.super$drawSelect();
            Drawf.dashCircle(this.x, this.y, range, Pal.accent);
        },
        handleDamage(amount) {
            if (amount <= 1) return 0
            dodge = !dodge;
            if (dodge) {
                HX.withstand.at(this, this.block.size);
                return 0
            }
            if (amount > this.maxHealth / 20) {
                HX.maxDamage.at(this, this.block.size);
                return this.maxHealth / 20;
            }
            amount = Math.min(amount * 10, amount + this.block.armor);
            return Damage.applyArmor(amount * 0.5, this.block.armor);
        },
        onDestroyed() {
            this.super$onDestroyed();
            if (!this.floor().solid && !this.floor().isLiquid) {
                Effect.decal(lib.region("rubble-" + this.block.size), this.x, this.y, Math.round(Mathf.random(4)) * 90);
            }
        },
        write(write) {
            this.super$write(write);
            //write.bool(dodge);
            write.s(Bullet == null ? -1 : Bullet.id);
            for (let p of pays) TypeIO.writePayload(write, p);
        },
        read(read, revision) {
            this.super$read(read, revision);
            let id = read.s();
            //dodge = read.bool();
            Bullet = id == -1 ? null : Vars.content.item(id);
            for (let p of pays) p = TypeIO.readPayload(read);
        }
    })
});