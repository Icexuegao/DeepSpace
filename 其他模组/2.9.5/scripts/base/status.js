const lib = require("base/coflib");
const FX = require("base/Effect/fightFx");

let minDamage = Stat("minDamage");
let percentDamage = Stat("percentDamage");
let minHealth = Stat("minHealth");
let percentHealth = Stat("percentHealth");
let minShieldDamage = Stat("minShieldDamage");
let percentShieldDamage = Stat("percentShieldDamage");
let reduceArmor = Stat("reduceArmor");
let reduceMaxHealth = Stat("reduceMaxHealth");
let killHealth = Stat("killHealth");
let chainDamage = Stat("chainDamage");

function parasite(name) {
    let p = extend(UnitType, name, {
        lifetime: 1800,
        flying: true,
        lowAltitude: true,
        circleTarget: true,
        useUnitCap: false,
        createScorch: false,
        playerControllable: false,
        itemCapacity: 0,
        update(unit) {
            this.super$update(unit);
            if (unit.team == Team.crux && unit.statusBits().get(狂乱.id)) unit.apply(狂乱, 1800);
        },
        display(unit, table) {
            this.super$display(unit, table);
            table.getChildren().get(1).add(new Bar("存活时间", Pal.accent, () => 1 - unit.fin())).row();
        }
    })
    p.constructor = () => extend(TimedKillUnit, {});
    p.immunities.addAll(StatusEffects.burning, StatusEffects.melting, StatusEffects.wet, StatusEffects.freezing, StatusEffects.sporeSlowed, StatusEffects.sapped, StatusEffects.electrified);
    return exports[name] = p;
}

let 飞蠓 = parasite("飞蠓");
let 疟蚊 = parasite("疟蚊");
let 血俎 = parasite("血俎");

function newStatus(name, cons) {
    return exports[name] = extend(StatusEffect, name, cons || {});
}

function PercentStatus(name, percent /*百分比*/, min /*保底值*/, damage, obj) {
    return newStatus(name, {
        transitionDamage: damage || 0,
        update(unit, time) {
            this.super$update(unit, time);
            let UMAX = unit.type.health / 100 / 60; //每秒1%生命值
            let UP = UMAX * percent, Max = Math.max(UP, Math.abs(min));
            if (min < 0) unit.heal(Math.abs(Max));
            else unit.damageContinuousPierce(Max);
        },
        setStats() {
            this.super$setStats();
            if (min < 0) {
                this.stats.add(percentHealth, lib.bundle("percentHealth", percent));
                this.stats.add(minHealth, lib.bundle("minHealth", 60 * -min));
            } else {
                this.stats.add(percentDamage, lib.bundle("percentDamage", percent));
                this.stats.add(minDamage, lib.bundle("minDamage", 60 * min));
            }
        },
        init() {
            if (!obj) return
            if (obj.opposite) {
                for (let i = 0; i < obj.opposite.length; i++) {
                    this.opposite(obj.opposite[i]);
                }
            }
            if (!obj.affinity) return
            this.affinity(obj.affinity[0], (unit, result, time) => {
                unit.damagePierce(this.transitionDamage);
                obj.affinity[1].at(unit.x + Mathf.range(unit.bounds() / 2), unit.y + Mathf.range(unit.bounds() / 2));
                result.set(this, Math.min(time + result.time, 300));
            });
        }
    })
}

function ChainLightningStatus(name, range, reload, damage, status, statusDuration, color) {
    let timer = 0, all = new Seq();
    newStatus(name, {
        update(unit, time) {
            this.super$update(unit, time);
            if ((timer += Time.delta) >= reload) {
                all.clear();
                Units.nearby(null, unit.x, unit.y, range, other => {
                    if (other.team == unit.team && other.hittable) {
                        all.add(other);
                    }
                });
                all.sort(floatf(e => e.dst2(unit.x, unit.y)));
                if (all.size > 1) {
                    let other = all.get(1);
                    let absorber = Damage.findAbsorber(Vars.player.unit().team, unit.x, unit.y, other.getX(), other.getY());
                    if (absorber != null) other = absorber;
                    other.damagePierce(damage);
                    if (other instanceof Unit) {
                        other.unapply(this);
                        other.apply(this, reload + 30);
                    }
                    Sounds.spark.at(unit);
                    Fx.chainLightning.at(unit.x, unit.y, 0, color, other);
                    Fx.hitLaserBlast.at(other.x, other.y, unit.angleTo(other), color);
                } else {
                    unit.apply(status, statusDuration);
                    Sounds.pulseBlast.at(unit);
                }
                unit.damagePierce(damage);
                Fx.hitLaserBlast.at(unit.x, unit.y, 0, color);
                timer = 0;
            }
        },
        setStats() {
            this.super$setStats();
            this.stats.add(chainDamage, lib.bundle("chainDamage", damage));
        },
        init() {
            this.affinity(instableEnergy, (unit, result, time) => {
                unit.damagePierce(unit.type.health / 100 * 2);
                Fx.dynamicSpikes.wrap(lib.Color("C0ECFF"), unit.hitSize / 2).at(unit.x + Mathf.range(unit.bounds() / 2), unit.y + Mathf.range(unit.bounds() / 2));
                result.set(this, Math.min(time + result.time, 60));
            });
        }
    })
}


newStatus("EMP", {
    update(unit, time) {
        this.super$update(unit, time);
        if (unit.shield > 0) {
            let damage = Math.max(unit.type.health / 100, unit.shield / 100);
            unit.damageContinuousPierce(damage / 60);
        }
        //unit.abilities.remove();
        //if (time <= 3) unit.abilities = unit.type.abilities;
    },
    setStats() {
        this.super$setStats();
        this.stats.add(percentShieldDamage, lib.bundle("percentShieldDamage", 1));
        this.stats.add(minShieldDamage, lib.bundle("minShieldDamage", 1));
    }
});
newStatus("坍缩");
let frozen = newStatus("封冻", {
    /*i: 0,
    update(unit, time) {
        this.super$update(unit, time);
        if (time <= 3) {
            this.i = 0;
            return
        }
        if (this.i < 600) this.i++
        let ice = 1 - this.i / 600 * 0.4;
        if (unit.healthMultiplier > 0.4) unit.healthMultiplier *= ice;
        if (unit.speedMultiplier > 0.4) unit.speedMultiplier *= ice;
        if (unit.reloadMultiplier > 0.4) unit.reloadMultiplier *= ice;
    },*/
    transitionDamage: 36,
    init() {
        this.opposite(StatusEffects.burning, StatusEffects.melting);
        this.affinity(StatusEffects.blasted, (unit, result, time) => {
            unit.damagePierce(this.transitionDamage);
            if (unit.team == Vars.state.rules.waveTeam) {
                Events.fire(Trigger.blastFreeze);
            }
        });
    }
});
let opposite = [StatusEffects.wet, StatusEffects.freezing, frozen];
let affinity = [StatusEffects.tarred, Fx.burning];
PercentStatus("迅疗", 0.8, -6);
PercentStatus("熔融", 0.2, 0.25, 25, {
    opposite: opposite,
    affinity: affinity
});
PercentStatus("蚀骨", 1, 1.25, 50, {
    opposite: opposite,
    affinity: affinity
});
PercentStatus("日耀", 2, 2.5, 75, {
    opposite: opposite,
    affinity: affinity
});
newStatus("幻像", {
    update(unit, time) {
        this.super$update(unit, time);
        if (unit.healthf < 0.8) unit.kill();
    }
})
newStatus("过热");
newStatus("损毁");
newStatus("破甲", {
    sec: 0,
    update(unit, time) {
        this.super$update(unit, time);
        this.sec++;
        if (time <= 3) {
            unit.armor = unit.type.armor;
            return
        }
        if (this.sec % (60 / 8) == 0) unit.armor -= 1;
    },
    setStats() {
        this.super$setStats();
        this.stats.add(reduceArmor, lib.bundle("reduceArmor", 8, "(临时)"));
    }
})
newStatus("秽蚀", {
    sec: 0,
    update(unit, time) {
        this.super$update(unit, time);
        this.sec++;
        if (this.sec % (60 / 4) == 0) unit.armor -= 1;
        if (this.sec % (60 / 50) == 0) unit.maxHealth -= 1;
    },
    setStats() {
        this.super$setStats();
        this.stats.add(reduceArmor, lib.bundle("reduceArmor", 4, "(永久)"));
        this.stats.add(reduceMaxHealth, lib.bundle("reduceMaxHealth", 50));
    }
})
newStatus("屠戮");
newStatus("斩杀", {
    update(unit, time) {
        this.super$update(unit, time);
        let x = unit.x, y = unit.y, size = unit.hitSize;
        if (unit.maxHealth <= unit.type.health * 0.05 && !unit.dead) {
            unit.kill();
            FX.prismaticSpikes.wrap(lib.Color("F15454"), size).at(x, y);
            Damage.status(null, x, y, size * 1.5, this, 300, true, true);
            Damage.damage(null, x, y, size, unit.type.health * 0.05);
        }
        unit.maxHealth -= 10;
        if (unit.health > unit.maxHealth) unit.health = unit.maxHealth;
    },
    setStats() {
        this.super$setStats();
        this.stats.add(reduceMaxHealth, lib.bundle("reduceMaxHealth", 10 * 60));
        this.stats.add(killHealth, lib.bundle("killHealth", 5));
    }
});
newStatus("坚忍");
newStatus("庇护");
newStatus("突袭");
newStatus("鼓舞", {
    update(unit, time) {
        this.super$update(unit, time);
        Units.nearby(unit.team, unit.x, unit.y, unit.type.hitSize * 4, u => {
            if (u.team == unit.team && u != unit && !u.statusBits().get(this.id)) {
                u.damageMultiplier *= 1 + unit.damageMultiplier / 5;
                u.healthMultiplier *= 1 + unit.healthMultiplier / 5;
                u.speedMultiplier *= 1 + unit.speedMultiplier / 5;
                u.reloadMultiplier *= 1 + unit.reloadMultiplier / 5;
                u.heal(unit.maxHealth / 500 / 60);
            }
        })
    }
});
newStatus("复仇", {
    update(unit, time) {
        if (Mathf.chanceDelta(this.effectChance)) {
            Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2));
            this.effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, unit.rotation + 180, this.color);
        }
        /*Units.nearby(unit.team, unit.x, unit.y, unit.type.hitSize * 4, u => {
            if (u != unit && u.team == unit.team && u.type.outlineColor == lib.C1F1F1F && !u.statusBits().get(this.id)) u.apply(this, 300);
        });*/
    }
});
newStatus("反扑", {
    update(unit, time) {
        this.super$update(unit, time);
        let heal = 1 + 1.5 * (1 - unit.healthf());
        unit.speedMultiplier *= heal;
        unit.healthMultiplier *= heal;
        unit.damageMultiplier *= heal;
        unit.reloadMultiplier *= heal;
    },
    effect: FX.prismaticSpikes.wrap(lib.Color("FB7A83"), Mathf.random(8, 16))
});
let secondaryRadiation = newStatus("辐射");
newStatus("衰变", {
    update(unit, time) {
        this.super$update(unit, time);
        Damage.status(null, unit.x, unit.y, unit.hitSize * 1.5, secondaryRadiation, 300, true, true);
    },
    draw(unit) {
        this.super$draw(unit);
        Draw.z(Layer.shields);
        Draw.color(lib.Color("A170F4CC"));
        Fill.poly(unit.x, unit.y, 16, unit.hitSize * 1.5);
    }
});
let instableEnergy = PercentStatus("湍能", 1, 2);
ChainLightningStatus("连锁闪电", 8 * 30, 60, 150, instableEnergy, 600, lib.Color("C0ECFF"));

let 狂乱 = newStatus("狂乱");
let infection = newStatus("寄生", {
    death(u) {
        for (let i = 0; i < Math.floor(u.hitSize / 24); i++) {
            let unitX = u.x + Mathf.range(u.hitSize), unitY = u.y + Mathf.range(u.hitSize);
            血俎.spawn(u.team == Team.crux ? Team.sharded : Team.crux, unitX, unitY).rotation = Angles.angle(u.x, u.y, unitX, unitY);
        }
        for (let i = 0; i < Math.floor(u.hitSize % 24 / 18); i++) {
            let unitX = u.x + Mathf.range(u.hitSize), unitY = u.y + Mathf.range(u.hitSize);
            疟蚊.spawn(u.team == Team.crux ? Team.sharded : Team.crux, unitX, unitY).rotation = Angles.angle(u.x, u.y, unitX, unitY);
        }
        for (let i = 0; i < Math.floor(u.hitSize % 18 / 9); i++) {
            let unitX = u.x + Mathf.range(u.hitSize), unitY = u.y + Mathf.range(u.hitSize);
            飞蠓.spawn(u.team == Team.crux ? Team.sharded : Team.crux, unitX, unitY).rotation = Angles.angle(u.x, u.y, unitX, unitY);
        }
    }
});
飞蠓.immunities.add(infection);
疟蚊.immunities.add(infection);
血俎.immunities.add(infection);
let trigger = Seq.with(infection);

Events.on(UnitDestroyEvent, e => {
    let u = e.unit, bits = u.statusBits();
    if (bits.isEmpty()) return;
    trigger.each(s => bits.get(s.id), s => s.death(u));
});