const lib = require("base/coflib");
const FX = require("base/Effect/fightFx");
const NX = require("base/Effect/normalFx");
const {损毁, 湍能, 坍缩} = require("base/status");

exports.PulseEnergyBulletType = (speed, lifetime, power, B, size, reload) => {
    let range = Math.max(B.speed * B.lifetime, B.length) - 8;
    return extend(BasicBulletType, {
        speed: speed,
        lifetime: lifetime,
        hittable: false,
        collides: false,
        absorbable: false,
        reflectable: false,
        update(b) {
            this.super$update(b);
            let target = Units.closestTarget(b.team, b.x, b.y, range);
            if (target != null && b.timer.get(reload)) {
                for (let i = 0; i < 5; i++) Time.run(10 * i, () => {
                    let ang = Angles.angle(b.x, b.y, target.x, target.y);
                    let xy = lib.AngleTrns(ang, size);
                    B.shootEffect.at(xy.x, xy.y, ang, B.hitColor, b);
                    let bu = B.create(b, b.x, b.y, ang);
                    //FX.chainLightningFade.at(b.x, b.y, Mathf.random(16, 24), lib.FF8663, bu);
                });
            }
            Damage.status(b.team, b.x, b.y, size * 2, 湍能, 60, true, true);
            if (!target && Vars.state.rules.waves && b.team == Vars.state.rules.defaultTeam) {
                let spawn = Geometry.findClosest(b.x, b.y, Vars.spawner.getSpawns()) || b.closestEnemyCore();
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(spawn), Time.delta * power));
            }
        },
        draw(b) {
            Draw.blend(Blending.additive);
            Lines.stroke(2);
            let s = Mathf.absin(3, 0.3);
            Draw.color(B.hitColor, 0.7);
            Fill.circle(b.x, b.y, size / 20 + s);
            Lines.circle(b.x, b.y, size / 5 + s);
            Lines.circle(b.x, b.y, size + s);
            let rand = Fx.rand;
            rand.setSeed(b.id);
            Lines.stroke(2.2);
            for (let i = 0; i < size * 2; i++) {
                let fin = (rand.random(1) + Time.time / size) % 1, fout = 1 - fin;
                let len = size * Interp.pow2Out.apply(fin);
                let angle = rand.random(360), xy = lib.AngleTrns(angle, len);
                Lines.lineAngle(b.x + xy.x, b.y + xy.y, angle, 7 * fout);
            }
            Draw.blend();
            Draw.reset();
            let xy = lib.Randonge(b.x, b.y, size * 2);
            if (Vars.state.isPaused() || b.time >= b.lifetime - 26) return
            if (Mathf.chance(0.05)) FX.chainLightningFade.at(xy.x, xy.y, Mathf.random(8, 16), B.hitColor, b);
        }
    })
}

exports.HomingMainBulletType = (speed, damage, lifetime, power, mirror, angle) => {
    return extend(BasicBulletType, {
        shrinkY: 0,
        speed: speed,
        damage: damage,
        lifetime: lifetime,
        reflectable: false,
        createFrags(b, x, y) {
            let e = b.owner;
            for (let i = 0; i < this.fragBullets; i++) {
                if (mirror) for (let j of Mathf.signs) {
                    let ang = e.rotation + 180 + this.fragAngle * j + ((i - this.fragBullets / 2 + 0.5) * this.fragSpread) + (angle ? angle[i] * j : 0);
                    this.fragBullet.create(b, b.team, e.x, e.y, ang, 1, 1, frag => {
                        if (frag.time < 40 / frag.type.speed) return
                        frag.vel.setAngle(Angles.moveToward(frag.rotation(), frag.angleTo(x, y), Time.delta * power));
                    });
                }
                if (!mirror) {
                    let ang = e.rotation + 180 + this.fragAngle + ((i - this.fragBullets / 2 + 0.5) * this.fragSpread) + (angle ? angle[i] : 0);
                    this.fragBullet.create(b, b.team, e.x, e.y, ang, 1, 1, frag => {
                        if (frag.time < 40 / frag.type.speed) return
                        frag.vel.setAngle(Angles.moveToward(frag.rotation(), frag.angleTo(x, y), Time.delta * power));
                        frag.type.updateWeaving(b);
                    });
                }
            }
        }
    })
}

exports.VerticalBulletType = (damage, lifetime, range) => {
    return extend(BulletType, {
        damage: 0,
        lifetime: lifetime,
        hittable: false,
        collides: false,
        ammoMultiplier: 1,
        shootEffect: Fx.none,
        smokeEffect: Fx.none,
        hitEffect: Fx.none,
        despawnEffect: Fx.none,
        instantDisappear: true,
        splashDamage: damage,
        splashDamageRadius: range,
        scaledSplashDamage: true,
        //splashDamagePierce: true,
        hitColor: Pal.bulletYellowBack,
        update(b) {
            b.remove();
        }
    })
}

exports.SpawnBulletType = (range, unit) => {
    return extend(BulletType, {
        damage: 0,
        speed: 8,
        lifetime: range * 2 / 8,
        hittable: false,
        collides: false,
        shootEffect: Fx.none,
        smokeEffect: Fx.none,
        hitEffect: Fx.none,
        despawnEffect: Fx.none,
        instantDisappear: true,
        despawnUnit: unit,
        createUnits(b, x, y) {
            let e = b.owner, ux = x + Mathf.range(range), uy = y + Mathf.range(range);
            this.despawnUnit.spawn(b.team, ux, uy).rotation = e.rotation;
            NX.JumpIn(this.despawnUnit, ux, uy).at(ux, uy, e.rotation - 90, e.team.color);
        }
    })
}

exports.TurretBulletType = (speed, lifetime, turret) => {
    return extend(BasicBulletType, {
        sprite: "curse-of-flesh" + turret.localizedName,
        shrinkY: 0,
        damage: 0,
        hittable: false,
        collides: false,
        scaleLife: true,
        absorbable: false,
        reflectable: false,
        speed: speed,
        lifetime: lifetime,
        hitEffect: Fx.none,
        despawnEffect: Fx.none,
        despawnSound: Sounds.place,
        createUnits(b, x, y) {
            let tile = Vars.world.tile(x / 8, y / 8);
            if (tile.block() == Blocks.air) tile.setBlock(turret, b.team);
        },
        turretName() {
            return turret.localizedName;
        }
    })
}

exports.StickyBombBulletType = (speed, damage, lifetime, range) => {
    let e, flash;
    return extend(BasicBulletType, {
        sprite: "large-bomb",
        shrinkY: 0,
        pierceCap: 1,
        speed: speed,
        damage: 10,
        lifetime: lifetime,
        fragOnHit: false,
        hitEffect: Fx.none,
        splashDamage: damage,
        splashDamageRadius: range,
        removeAfterPierce: false,
        update(b) {
            this.super$update(b);
            if (e && b.hasCollided(e.id)) {
                b.x = e.x;
                b.y = e.y;
            } else if (!e && this.splashDamage != 0) {
                this.splashDamage = 0;
            }
        },
        hitEntity(b, entity, health) {
            this.super$hitEntity(b, entity, health);
            b.vel.scl(0);
            e = entity;
            this.splashDamage = damage;
        },
        draw(b) {
            this.super$draw(b);
            Draw.alpha(0.6);
            flash += (1 + b.fin() * 10) * Time.delta;
            Draw.color(lib.Color("FF957580"), lib.Color("FF9575"), Mathf.absin(flash, 9, 1));
            Draw.rect(Core.atlas.find(b.type.sprite), b.x, b.y);
        }
    })
}

exports.SizeBulletType = (speed, damage, lifetime) => {
    return extend(BasicBulletType, {
        shrinkY: 0,
        speed: speed,
        damage: damage,
        lifetime: lifetime,
        ammoMultiplier: 1,
        status: 损毁,
        statusDuration: 180,
        pierce: true,
        pierceBuilding: true,
        pierceDamageFactor: 0.6,
        hitEntity(b, entity, health) {
            let size, e = entity;
            if (e instanceof Unit) size = e.hitSize;
            else size = e.block.size * 8;
            let scale = b.owner.hitSize / size > 1 ? b.owner.hitSize / size : 1;
            b.damage = damage * scale;
            this.super$hitEntity(b, e, health);
        }
    });
}

exports.TorrentBulletType = (speed, damage, lifetime, power) => {
    return extend(BasicBulletType, {
        sprite: "circle",
        shrinkY: 0,
        speed: speed,
        damage: damage,
        lifetime: lifetime,
        update(b) {
            this.super$update(b);
            b.vel.setAngle(Angles.moveToward(b.rotation(), b.owner.rotation, Time.delta * power));
        }
    })
}

exports.HomingBulletType = (speed, damage, lifetime, power) => {
    let r, s, t;
    return extend(BasicBulletType, {
        speed: speed,
        damage: damage,
        lifetime: lifetime,
        reflectable: false,
        update(b) {
            this.super$update(b);
            let e = b.owner;
            if (e instanceof Unit) {
                r = e.range;
                s = e.isShooting;
                t = b.angleTo(e.aimX, e.aimY);
            } else {
                r = e.block.range;
                s = e.isShooting();
                t = b.angleTo(e.targetPos || b);
            }
            if (b.dst(e) > r * 1.2) b.time = b.lifetime + 1;
            else if (s) b.vel.setAngle(Angles.moveToward(b.rotation(), t, Time.delta * power));
        },
        draw(b) {
            this.drawTrail(b);
            Tmp.v1.trns(b.rotation(), b.type.height / 2);
            for (let i of Mathf.signs) {
                Tmp.v2.trns(b.rotation() - 90, b.type.width * i, -b.type.height);
                Draw.color(b.type.backColor);
                Fill.tri(Tmp.v1.x + b.x, Tmp.v1.y + b.y, -Tmp.v1.x + b.x, -Tmp.v1.y + b.y, Tmp.v2.x + b.x, Tmp.v2.y + b.y);
                Draw.color(b.type.frontColor);
                Fill.tri(Tmp.v1.x / 2 + b.x, Tmp.v1.y / 2 + b.y, -Tmp.v1.x / 2 + b.x, -Tmp.v1.y / 2 + b.y, Tmp.v2.x / 2 + b.x, Tmp.v2.y / 2 + b.y);
            }
        }
    })
}

exports.TwiceHomingBulletType = (range) => {
    return extend(BasicBulletType, {
        drag: 0.01,
        shrinkY: 0,
        lifetime: 1800,
        sprite: "circle",
        update(b) {
            this.super$update(b);
            if (b.time < 80 / b.type.speed) return
            let target = Units.closestTarget(b.team, b.x, b.y, range);
            if (!target) {
                let tar = Units.closestTarget(b.team, b.x, b.y, range * 1.2);
                if (tar) b.vel.trns(Angles.angle(b.x, b.y, tar.x, tar.y), b.type.speed);
                if (!tar) b.time = b.lifetime + 1;
                return
            }
            b.vel.trns(b.rotation(), b.type.speed);
            b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), Time.delta * b.type.speed / 2));
        }
    })
}

exports.FortressBulletType = (range, reload, B, num, random, spread) => {
    if (range == 0) range = Math.min(B.speed * B.lifetime, B.speed * (1 - Mathf.pow(1 - B.drag, B.lifetime)) / B.drag);
    return extend(BasicBulletType, {
        drag: 0.01,
        damage: 0,
        hittable: false,
        collides: false,
        absorbable: false,
        reflectable: false,
        update(b) {
            this.super$update(b);
            let target = Units.closestTarget(b.team, b.x, b.y, range);
            if (target != null && b.timer.get(reload)) {
                for (let i = 0; i < num; i++) {
                    B.create(b, b.x, b.y, Angles.angle(target.x, target.y, b.x, b.y) + Mathf.range(random || 360) + ((i - (num - 1) / 2) * (spread || 0)));
                }
            }
        },
        draw(b) {
            this.drawTrail(b);
            let size = b.type.height;
            Draw.color(b.type.backColor);
            NX.shiningCircle(b.id, Time.time, b.x, b.y, size, 5, 30, 0, size * 1.5, size, 360);
            Draw.color(b.type.frontColor);
            NX.shiningCircle(b.id, Time.time, b.x, b.y, size * 0.65, 5, 30, 0, size, size * 0.9, 360);
        }
    })
}

exports.RandomLightningBulletType = (damage, range, radius, reload, num, effect) => {
    return extend(BasicBulletType, {
        hittable: false,
        absorbable: false,
        reflectable: false,
        ammoMultiplier: 1,
        update(b) {
            this.super$update(b);
            if (b.time >= b.lifetime - 26) return
            if (b.timer.get(reload)) {
                for (let i = 0; i < num; i++) {
                    let xy = lib.Randonge(b.x, b.y, range), x = xy.x, y = xy.y;
                    Damage.damage(b.team, x, y, radius, damage);
                    Damage.status(null, x, y, radius, 坍缩, 60, true, true);
                    FX.chainLightningFadeReversed.at(x, y, Mathf.random(16, 24), lib.FF8663, b);
                    Sounds.plasmaboom.at(x, y);
                    Effect.shake(4, 30, x, y);
                    effect.at(x, y);
                }
            }
        }
    })
}

exports.HealthBulletType = (speed, damage, lifetime, percent) => {
    return extend(BasicBulletType, {
        shrinkY: 0,
        speed: speed,
        damage: damage,
        lifetime: lifetime,
        ammoMultiplier: 1,
        hitEntity(b, entity, health) {
            b.damage = damage * (1 + (1 - entity.healthf()) * percent);
            this.super$hitEntity(b, entity, health);
        }
    });
}

exports.ArmorBrokenBulletType = (speed, damage, lifetime, percent, num) => {
    return extend(BasicBulletType, {
        shrinkY: 0,
        speed: speed,
        damage: damage,
        lifetime: lifetime,
        ammoMultiplier: 1,
        pierce: true,
        pierceArmor: true,
        pierceBuilding: true,
        pierceDamageFactor: 0.6,
        hitEntity(b, entity, health) {
            b.damage = damage + lib.zero(entity.armor * percent);
            entity.armor -= num;
            this.super$hitEntity(b, entity, health);
        },
        armorDamage() {
            return percent
        },
        armorReduce() {
            return num
        },
        pierceFactor() {
            return this.pierceDamageFactor * 100;
        }
    });
}

exports.MindControlBulletType = () => {
    return extend(BasicBulletType, {
        hitEntity(b, entity, health) {
            this.super$hitEntity(b, entity, health);
            if (entity instanceof Unit && entity.healthf() <= 0.05) {
                entity.team = b.team;
                entity.heal();
                Fx.spawn.at(entity.x, entity.y);
            }
        }
    })
}

exports.BlockHoleBulletType = (range, suction /*力度*/, damage, percent) => {
    return extend(BasicBulletType, {
        shrinkY: 0,
        damage: 0,
        speed: 0,
        hittable: false,
        collides: false,
        absorbable: false,
        reflectable: false,
        ammoMultiplier: 1,
        update(b) {
            this.super$update(b);
            Units.nearbyEnemies(b.team, b.x, b.y, range, u => {
                let dst = 1 - u.dst(b) / range;
                u.impulse(Tmp.v3.set(b).sub(u).nor().scl(80 * suction * (dst + 1)));
                Damage.status(b.team, b.x, b.y, range, 坍缩, 60, true, true);
                u.damageContinuousPierce((u.type.health * percent / (this.lifetime / 60) + damage) * dst / 60);
            })
        },
        draw(b) {
            this.drawTrail(b);
            let xy = lib.Randonge(b.x, b.y, range);
            if (Vars.state.isPaused() || b.time >= b.lifetime - 26) return
            if (Mathf.chance(0.2)) FX.chainLightningFade.at(xy.x, xy.y, Mathf.random(8, 16), lib.FF8663, b);
            Draw.reset();
        }
    })
}

exports.EnergyFieldBulletType = (range, reload, damage, maxTargets, status, statusDuration, color, hitEffect) => {
    let timer = 0, all = new Seq();
    return extend(BasicBulletType, {
        hittable: false,
        absorbable: false,
        reflectable: false,
        update(b) {
            this.super$update(b);
            if ((timer += Time.delta) >= reload) {
                all.clear();
                Units.nearby(null, b.x, b.y, range, other => {
                    if (other.team != b.team) all.add(other);
                });
                Units.nearbyBuildings(b.x, b.y, range, build => {
                    if (build.team != b.team) all.add(build);
                });
                all.sort(floatf(e => e.dst2(b.x, b.y)));
                let max = Math.min(all.size, maxTargets);
                for (let i = 0; i < max; i++) {
                    let other = all.get(i);
                    var absorber = Damage.findAbsorber(b.team, b.x, b.y, other.getX(), other.getY());
                    if (absorber != null) other = absorber;
                    Fx.chainLightning.at(b.x, b.y, 0, color, other);
                    if (hitEffect) {
                        hitEffect.at(other);
                    } else {
                        Fx.hitLaserBlast.at(other.x, other.y, b.angleTo(other), color);
                    }
                    other.damage(damage);
                    if (other instanceof Unit) other.apply(status, statusDuration);
                    Sounds.spark.at(b);
                }
                timer = 0;
            }
        }
    })
}

exports.PointLaser = (range, speed, rotate) => {
    return extend(PointLaserBulletType, {
        update(b) {
            this.updateTrail(b);
            this.updateHoming(b);
            this.updateWeaving(b);
            this.updateTrailEffects(b);
            this.updateBulletInterval(b);
            let u = b.owner, ang = 0;
            if (u instanceof Unit) {
                let shootLength = Math.min(u.dst(u.aimX, u.aimY), range);
                let curLength = u.dst(b.aimX, b.aimY);
                let resultLength = Mathf.approachDelta(curLength, shootLength, speed);
                if (rotate) {
                    ang = Angles.angle(u.x, u.y, u.aimX, u.aimY);
                } else {
                    ang = u.rotation;
                }
                Tmp.v1.trns(ang, resultLength).add(u.x, u.y);
                let xy = lib.AngleTrns(ang, range - 1);
                if (curLength > range) {
                    b.aimX = u.x + xy.x;
                    b.aimY = u.y + xy.y;
                } else {
                    b.aimX = Tmp.v1.x;
                    b.aimY = Tmp.v1.y;
                }
                if (b.timer.get(0, this.damageInterval)) {
                    Damage.collidePoint(b, b.team, this.hitEffect, b.aimX, b.aimY);
                }
                if (b.timer.get(1, this.beamEffectInterval)) {
                    this.beamEffect.at(b.aimX, b.aimY, this.beamEffectSize * b.fslope(), this.hitColor);
                }
                if (this.shake != null && this.shake > 0) {
                    Effect.shake(this.shake, this.shake, b);
                }
            }
        }
    })
}