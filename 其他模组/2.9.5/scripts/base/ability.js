const lib = require("base/coflib");
const NX = require("base/Effect/normalFx");

exports.PurificationAbility = (reload) => { //清除状态
    let timer = new Interval();
    if (!reload) reload = 600;
    return extend(Ability, {
        localized() {
            return lib.bundle("PurificationAbility", reload / 60);
        },
        update(unit) {
            if (timer.get(reload)) unit.clearStatuses();
        }
    })
}

exports.FlashbackAbility = (percent, amount, offset, spawnUnit, clone) => { //闪回
    return extend(Ability, {
        localized() {
            return lib.bundle("FlashbackAbility");
        },
        update(unit) {
            if (unit.healthf() > percent) return
            let spawner = Vars.spawner.getSpawns();
            if (spawner.size > 0) {
                unit.clearStatuses();
                Sounds.shockBlast.at(unit);
                let random = Mathf.random(0, spawner.size - 1);
                unit.x = spawner.get(random).x * 8;
                unit.y = spawner.get(random).y * 8;
                for (let i = 0; i < amount; i++) {
                    let xy = lib.AngleTrns(360 / amount * i, offset);
                    let ux = unit.x + xy.x, uy = unit.y + xy.y;
                    spawnUnit.spawn(unit.team, ux, uy).rotation = unit.rotation;
                    NX.JumpIn(spawnUnit, ux, uy).at(ux, uy, unit.rotation - 90, unit.team.color);
                }
                unit.remove();
                clone.spawn(unit.team, unit.x, unit.y).rotation = unit.rotation;
            }
        }
    })
}

exports.GuardFieldAbility = (range, damage, speed, side) => { //守护力场
    let stroke = 2;
    if (!side) side = 3;
    return extend(SuppressionFieldAbility, {
        localized() {
            return lib.bundle("GuardFieldAbility");
        },
        update(unit) {
            this.color = this.particleColor = unit.team.color;
            let r = Tmp.r1.setSize(range * 2).setCenter(unit.x, unit.y);
            Groups.bullet.intersect(r.x, r.y, r.width, r.height, b => {
                if (b.team != unit.team && b.type.hittable && unit.within(b, range)) {
                    let inp = Interp.exp5.apply(Mathf.clamp(1 - unit.dst(b) / range));
                    b.vel.scl(Mathf.lerp(1, speed, inp));
                    b.damage -= damage / 60 * inp;
                    if (b.damage <= 0) b.absorb();
                }
            })
        },
        draw(unit) {
            this.super$draw(unit);
            Draw.z(Layer.effect);
            Lines.stroke(stroke, this.color);
            Tmp.v1.trns(unit.rotation - 90, this.x, this.y).add(unit.x, unit.y);
            let rx = Tmp.v1.x, ry = Tmp.v1.y;
            for (let i = 0; i < side; i++) {
                let rot = i * 360 / side + Time.time;
                Lines.arc(rx, ry, range, 1 / side * 0.8, rot);
            }
            Drawf.light(rx, ry, range * 1.5, this.color, stroke * 0.8);
            Draw.reset();
        }
    })
}

exports.ShieldAbility = (amount, max, tier, size, color) => { //持续护盾
    return extend(Ability, {
        localized() {
            return lib.bundle("ShieldAbility", amount, max);
        },
        update(unit) {
            if (unit.shield < max) unit.shield += amount;
        },
        draw(unit) {
            if (!tier) return
            Draw.color(color);
            Draw.z(Layer.shields);
            for (let i = 1; i <= tier; i++) {
                for (let j = 0; j < 360; j += 360 / (i * 6)) {
                    let xy = lib.AngleTrns(j + 30, size * i * 2 - (j % 60 != 0 ? size / 2.1547 * i : size / 2 / 2.1547 * i));
                    let ex = xy.x + unit.x, ey = xy.y + unit.y;
                    Fill.poly(ex, ey, 6, size * Mathf.absin(-Time.time / 2 + i * 10, 8, 0.8 * unit.shield / max));
                }
            }
        }
    })
}

exports.SwapHealthAbility = (percent, reload) => { //交换生命值
    let all = new Seq(), timer = new Interval();
    if (!reload) reload = 3600;
    if (!percent) percent = 0.05;
    return extend(Ability, {
        localized() {
            return lib.bundle("SwapHealthAbility", reload / 60, percent * 100);
        },
        update(unit) {
            if (!timer.get(reload) || unit.healthf() >= percent) return
            all.clear();
            Units.nearby(null, unit.x, unit.y, unit.type.range, u => {
                if (u.team != unit.team) all.add(u);
            });
            all.sort(floatf(e => 1 - e.healthf()));
            if (all.size > 0) {
                let other = all.get(0);
                FX.chainLightningFade.at(unit.x, unit.y, 0, Pal.heal, other);
                let ohpf = other.healthf();
                other.health = unit.healthf() * other.maxHealth;
                unit.health = ohpf * unit.maxHealth;
            }
        },
        displayBars(unit, bars) {
            bars.add(new Bar("换血冷却", Pal.accent, new Floatp(() => i / reload))).row();
        }
    })
}

exports.DeathGiftAbility = (range, status, duration, percent, amount) => { //死亡状态/恢复场
    return extend(Ability, {
        death(unit) {
            Units.nearby(unit.team, unit.x, unit.y, range, u => {
                u.apply(status, duration);
                u.heal(u.maxHealth * percent + amount);
            });
        },
        localized() {
            return lib.bundle("DeathGiftAbility");
        }
    })
}

exports.StatusAbility = (range, status, duration) => { //敌方状态场
    return extend(Ability, {
        localized() {
            return status.localizedName + lib.bundle("StatusAbility");
        },
        update(unit) {
            Damage.status(unit.team, unit.x, unit.y, range, status, duration, true, true);
        },
        copy() {
            return exports.StatusAbility(range, status, duration);
        }
    })
}

exports.HealthRequireAbility = (percent, status, status2) => { //状态切换
    return extend(Ability, {
        localized() {
            return lib.bundle("HealthRequireAbility", percent * 100);
        },
        update(unit) {
            if (unit.healthf() >= percent) unit.apply(status, 60);
            else if (status2) unit.apply(status2, 60);
        },
        copy() {
            return exports.HealthRequireAbility(percent, status, status2);
        }
    })
}

function Rotator(name, x, y, s, speed, rot) {
    Draw.rect(name, x, y, s, s, speed);
    //Draw.rect(name + "-glow", x, y, -speed / 2);
    Draw.rect(name + "-top", x, y, rot);
}

exports.RotatorAbility = (name, x, y, speed, s, mirror) => { //螺旋桨
    return extend(Ability, {
        display: false,
        draw(unit) {
            let rot = unit.rotation - 90;
            let xy = lib.AngleTrns(rot, x, y);
            let xy2 = lib.AngleTrns(rot, -x, y);
            let Speed = Time.time * speed * 6;
            let ux = unit.x + xy.x, uy = unit.y + xy.y;
            let nx = unit.x + xy2.x, ny = unit.y + xy2.y;
            Rotator(unit.type.name + "-" + name, ux, uy, s, Speed, rot);
            if (mirror) Rotator(unit.type.name + "-" + name, nx, ny, s, -Speed, rot);
        }
    })
}

exports.MindControlFieldAbility = (damage, threshold, reload, range) => { //精控
    let hasInterfere = false, time = 0;
    return extend(Ability, {
        localized() {
            return lib.bundle("MindControlFieldAbility");
        },
        update(unit) {
            if ((time += Time.delta) >= reload) {
                Units.nearbyEnemies(unit.team, unit.x, unit.y, range, other => {
                    hasInterfere = true;
                    if (other.health <= threshold) {
                        other.team = unit.team,
                            other.heal();
                    } else {
                        other.health -= damage;
                    }
                })
                Units.nearbyBuildings(unit.x, unit.y, range, other => {
                    if (other.team != unit.team) {
                        hasInterfere = true;
                        if (other.health <= threshold) {
                            other.team = unit.team,
                                other.heal();
                            Ef.interfere.at(other)
                        } else {
                            other.health -= damage;
                        }
                    }
                })
                if (hasInterfere) {
                    extend(WaveEffect, {
                        lifetime: 25,
                        sizeFrom: 0,
                        sizeTo: size,
                        strokeFrom: 1.5,
                        strokeTo: 0,
                        colorFrom: lib.Color("AFFFFF"),
                        colorTo: unit.team.color
                    }).at(unit);
                }
                hasInterfere = false;
                time = 0;
            }
        }
    })
}