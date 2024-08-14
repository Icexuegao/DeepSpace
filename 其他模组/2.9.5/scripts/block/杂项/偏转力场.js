const lib = require("base/coflib")
let range = 8 * 40;
let deflection = lib.newBlock("偏转力场", {
    drawPlace(x, y, rotation, valid) {
        this.super$drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * 8 + this.offset, y * 8 + this.offset, range, Pal.accent);
    },
    setStats() {
        this.super$setStats();
        this.stats.add(Stat.range, range / 8, StatUnit.blocks);
    },
    setBars() {
        this.super$setBars();
        this.removeBar("power");
        this.addBar("Power", func(e => new Bar(
            prov(() => lib.bundle("bar.ownPower", Strings.fixed(e.getMP() * 60 * 10, 0))),
            prov(() => Pal.powerBar),
            floatp(() => e.getMP())
        )));
    }
});
deflection.health = 240;
deflection.hasPower = true;
deflection.buildVisibility = BuildVisibility.sandboxOnly;
deflection.consPower = extend(ConsumePower, {
    requestedPower(entity) {
        if (entity.block.consPower == this) return entity.getMass() * 10;
        else return 0
    }
})


deflection.buildType = prov(() => {
    let mp = 0, mass = 0, Switch = false;
    return extend(Building, {
        getMass() {
            return mass;
        },
        getMP() {
            return mass * this.power.status;
        },
        drawSelect() {
            this.super$drawSelect();
            Drawf.dashCircle(this.x, this.y, range, Pal.accent);
        },
        buildConfiguration(table) {
            this.super$buildConfiguration(table)
            table.slider(0, 10, 0.5, mass, t => {
                mass = t
            }).size(80, 30)
            table.check("", Switch, t => {
                Switch = t
            })
            table.row()
            table.add(mass + "").update(t => {
                t.setText(mass + "")
            })
        },
        updateTile() {
            mp = mass * this.power.status;
            if (this.enabled) {
                this.block.consPower.capacity = mp * 60;
                Groups.bullet.intersect(this.x - range, this.y - range, range * 2, range * 2, b => {
                    let ang = Angles.angle(this.x, this.y, b.x, b.y),
                        ang1 = Angles.angle(b.x, b.y, this.x, this.y);
                    if (b.team != this.team) b.vel.rotateTo(Switch ? ang1 : ang, 0.5 * mp);
                });
                Units.nearbyEnemies(this.team, this.x, this.y, range, u => {
                    let ang = Angles.angle(this.x, this.y, u.x, u.y),
                        vec = new Vec2();
                    vec.trns(ang, 0.1 * mp)
                    Switch ? u.vel.sub(vec) : u.vel.add(vec)
                })
            }
        },
        status() {
            return this.power.status > 0 ? BlockStatus.active : BlockStatus.noInput;
        },
        write(write) {
            this.super$write(write);
            write.f(mass);
            write.bool(Switch);
        },
        read(read, revision) {
            this.super$read(read, revision);
            mass = read.f();
            Switch = read.bool();
        }
    })
})