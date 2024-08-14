exports.modName = "curse-of-flesh";
exports.mod = Vars.mods.locateMod(exports.modName);

exports.maxDamage = Stat("maxDamage");
exports.regenAmount = Stat("regenAmount");
exports.damageReduction = Stat("damageReduction");

exports.addResearch = (content, research) => {
    if (!content) {
        throw new Error('content is null!');
    }
    if (!research.parent) {
        throw new Error('research.parent is empty!');
    }
    let researchName = research.parent;
    let customRequirements = research.requirements;
    let objectives = research.objectives;
    let lastNode = TechTree.all.find(boolf(t => t.content == content));
    if (lastNode != null) {
        lastNode.remove();
    }
    let node = new TechTree.TechNode(null, content, customRequirements !== undefined ? customRequirements : content.researchRequirements());
    if (objectives) {
        node.objectives.addAll(objectives);
    }
    let {planet} = research;
    if (planet) {
        node.planet = planet;
    }
    if (node.parent != null) {
        node.parent.children.remove(node);
    }
    // find parent node.
    let parent = TechTree.all.find(boolf(t => t.content.name.equals(researchName) || t.content.name.equals(exports.modName + "-" + researchName)));
    if (parent == null) {
        throw new Error("Content '" + researchName + "' isn't in the tech tree, but '" + content.name + "' requires it to be researched.");
    }
    // add this node to the parent
    if (!parent.children.contains(node)) {
        parent.children.add(node);
    }
    // reparent the node
    node.parent = parent;
}

exports.newBlock = (name, con) => {
    const b = extend(Block, name, con || {});
    b.solid = b.update = b.hasItems = b.destructible = b.configurable = true
    b.envEnabled = Env.any;
    b.group = BlockGroup.none;
    b.priority = TargetPriority.base;
    b.category = Category.effect;
    b.buildVisibility = BuildVisibility.shown;
    return b
}

exports.setBuilding = (building, block, con) => {
    block.buildType = prov(() => {
        if (building == Building) {
            return extend(building, con);
        } else {
            return extend(building, block, con);
        }
    })
}

exports.region = (name) => {
    return Core.atlas.find(exports.modName + "-" + name);
}

exports.bundle = (text, num, num1) => {
    if (num1) {
        return Core.bundle.format(text, num, num1);
    } else if (num) {
        return Core.bundle.format(text, num);
    } else {
        return Core.bundle.get(text);
    }
}

exports.limitBuild = (block, num) => {
    return block.localizedName + exports.bundle("text-limitBuild", num);
}

exports.float = (speed, length, slow) => {
    if (slow) return Mathf.sin(Time.time * speed) * length;
    return Mathf.absin(speed, length);
}

exports.Color = (R, G, B, A) => {
    if (G) return new Color(R, G, B, A || 1);
    else return Color.valueOf(R);
}

exports.C1F1F1F = exports.Color("1F1F1F");
exports.FF5845 = exports.Color("FF5845");
exports.FF8663 = exports.Color("FF8663");
exports.FEB380 = exports.Color("FEB380");

exports.zero = (num) => {
    return Math.max(num, 0);
}

exports.Randonge = (rx, ry, range) => {
    return {
        x: Mathf.range(range) + rx,
        y: Mathf.range(range) + ry
    }
}

exports.AngleTrns = (ang, rad, rad2) => {
    if (rad2) {
        return {
            x: Angles.trnsx(ang, rad, rad2),
            y: Angles.trnsy(ang, rad, rad2)
        }
    } else {
        return {
            x: Angles.trnsx(ang, rad),
            y: Angles.trnsy(ang, rad)
        }
    }
}

exports.DoubleTri = (x, y, width, length, angle, len) => {
    Drawf.tri(x, y, width, length, angle);
    Drawf.tri(x, y, width, length / (len || 4), angle + 180);
}

exports.trapezium = (x, y, top, bottom, height, rotation) => {
    let t = top / 2, b = bottom / 2, h = height / 2;
    Tmp.v1.trns(rotation, -t, h).add(x, y);
    let x1 = Tmp.v1.x, y1 = Tmp.v1.y;
    Tmp.v2.trns(rotation, t, h).add(x, y);
    let x2 = Tmp.v2.x, y2 = Tmp.v2.y;
    Tmp.v3.trns(rotation, b, -h).add(x, y);
    let x3 = Tmp.v3.x, y3 = Tmp.v3.y;
    Tmp.v4.trns(rotation, -b, -h).add(x, y);
    let x4 = Tmp.v4.x, y4 = Tmp.v4.y;
    Fill.quad(x1, y1, x2, y2, x3, y3, x4, y4);
}

exports.Halo = (obj) => {
    let def = {
        progress: DrawPart.PartProgress.warmup,
        mirror: true,
        x: 0,
        y: 0,
        moveX: 0,
        moveY: 0,
        shapeMR: 0,
        shapeR: 0,
        shapes: 2,
        radius: 4,
        radiusTo: -1,
        triL: 1,
        triLT: -1,
        haloRad: 0,
        haloRadTo: -1,
        haloRot: 0,
        haloRS: 0,
        color: exports.FF5845,
        colorTo: exports.FF8663
    }
    for (let k in def) {
        if (obj[k] == undefined) continue
        def[k] = obj[k]
    }
    return Object.assign(new HaloPart, {
        progress: def.progress,
        mirror: def.mirror,
        tri: true,
        x: def.x,
        y: def.y,
        moveX: def.moveX,
        moveY: def.moveY,
        shapeMoveRot: def.shapeMR,
        shapeRotation: def.shapeR,
        shapes: def.shapes,
        radius: def.radius,
        radiusTo: def.radiusTo,
        triLength: def.triL,
        triLengthTo: def.triLT,
        haloRadius: def.haloRad,
        haloRadiusTo: def.haloRadTo,
        haloRotation: def.haloRot,
        haloRotateSpeed: def.haloRS,
        color: def.color,
        colorTo: def.colorTo,
        layer: 110
    })
}

exports.DoubleHalo = (e, obj, num) => {
    if (!num) num = 4;
    e.parts.add(
        exports.Halo({
            progress: obj.progress,
            mirror: obj.mirror,
            x: obj.x,
            y: obj.y,
            moveX: obj.moveX,
            moveY: obj.moveY,
            shapeMR: obj.shapeMR,
            shapes: obj.shapes,
            radius: obj.radius,
            radiusTo: obj.radiusTo,
            triL: obj.triL,
            triLT: obj.triLT,
            haloRad: obj.haloRad,
            haloRadTo: obj.haloRadTo,
            haloRot: obj.haloRot,
            haloRS: obj.haloRS,
            color: obj.color,
            colorTo: obj.colorTo
        }),
        exports.Halo({
            progress: obj.progress,
            mirror: obj.mirror,
            x: obj.x,
            y: obj.y,
            moveX: obj.moveX,
            moveY: obj.moveY,
            shapeMR: obj.shapeMR,
            shapeR: 180,
            shapes: obj.shapes,
            radius: obj.radius,
            radiusTo: obj.radiusTo,
            triL: obj.triL / num || 1,
            triLT: obj.triLT / num || -1,
            haloRad: obj.haloRad,
            haloRadTo: obj.haloRadTo,
            haloRot: obj.haloRot,
            haloRS: obj.haloRS,
            color: obj.color,
            colorTo: obj.colorTo
        })
    )
}

exports.coolant = (block, amount, num) => {
    block.coolant = block.consumeCoolant(amount);
    if (num) block.coolantMultiplier = num;
}

exports.mapLimit = () => {
    return Vars.state.rules.editor;
}

/*exports.mapLimit = () => {
	return Vars.state == null || Vars.state.rules.editor || Vars.state.rules.infiniteResources;
}*/

exports.limitTurret = (type, name, limit) => {
    return extend(type, name, {
        canPlaceOn(tile, team, rotation) {
            if (exports.mapLimit()) return true;
            return Vars.state.teams.get(team).getCount(this) < limit;
        },
        drawPlace(x, y, rotation, valid) {
            this.super$drawPlace(x, y, rotation, valid);
            if (exports.mapLimit()) return
            if (Vars.state.teams.get(Vars.player.team()).getCount(this) < limit) return
            this.drawPlaceText(exports.limitBuild(this, limit), x, y, valid);
        }
    });
}

function turretBuild(type) {
    return type == ItemTurret ? ItemTurret.ItemTurretBuild : PowerTurret.PowerTurretBuild;
}

exports.SpeedUpTurret = (type, name, obj, min, change, limit) => {
    change = 1 / change;
    let b = extend(type, name, {
        setBars() {
            this.super$setBars();
            this.addBar("speedUp", func(e => new Bar(
                prov(() => exports.bundle("bar.speedUp", Strings.fixed(e.getTime() * 100, 0))),
                prov(() => exports.FF5845),
                floatp(() => e.getTime()))));
        },
        canPlaceOn(tile, team, rotation) {
            if (!limit || exports.mapLimit()) return true;
            return Vars.state.teams.get(team).getCount(this) < limit;
        },
        drawPlace(x, y, rotation, valid) {
            this.super$drawPlace(x, y, rotation, valid);
            if (!limit || exports.mapLimit()) return
            if (Vars.state.teams.get(Vars.player.team()).getCount(this) < limit) return
            this.drawPlaceText(exports.limitBuild(this, limit), x, y, valid);
        }
    });
    Object.assign(b, obj);
    b.buildType = () => {
        let speed = 0, speedup = 0;
        return extend(turretBuild(type), b, {
            getTime() {
                return speed;
            },
            baseReloadSpeed() {
                return this.efficiency * speedup > 1 ? speedup : 1;
            },
            updateTile() {
                this.super$updateTile();
                speedup = b.reload / min * speed;
                let target = this.isShooting() && this.canConsume() ? 1 : 0;
                speed = Mathf.approachDelta(speed, target, change * (target > 0 ? this.efficiency : 1));
            }
        })
    };
    return b;
}

exports.SpeedUp2Turret = (type, name, obj, min, add, less, limit) => {
    let b = extend(type, name, {
        setBars() {
            this.super$setBars();
            this.addBar("speedUp", func(e => new Bar(
                prov(() => exports.bundle("bar.speedUp", Strings.fixed(e.getTime() * 100, 0))),
                prov(() => exports.FF5845),
                floatp(() => e.getTime()))));
        },
        canPlaceOn(tile, team, rotation) {
            if (!limit || exports.mapLimit()) return true;
            return Vars.state.teams.get(team).getCount(this) < limit;
        },
        drawPlace(x, y, rotation, valid) {
            this.super$drawPlace(x, y, rotation, valid);
            if (!limit || exports.mapLimit()) return
            if (Vars.state.teams.get(Vars.player.team()).getCount(this) < limit) return
            this.drawPlaceText(exports.limitBuild(this, limit), x, y, valid);
        }
    });
    Object.assign(b, obj);
    less = (b.reload - min) / less;
    b.buildType = () => {
        let time = b.reload, speed = 1;
        return extend(turretBuild(type), b, {
            getTime() {
                return (b.reload - time) / (b.reload - min);
            },
            baseReloadSpeed() {
                return this.efficiency * speed;
            },
            updateTile() {
                this.super$updateTile();
                speed = b.reload / time;
                if (!(this.isShooting() && this.canConsume()) && b.reload > time) {
                    if (b.reload - time > less) time += less;
                    else time = b.reload;
                }
            },
            handleBullet(bullet, offsetX, offsetY, angleOffset) {
                if (time > min) time -= add;
            }
        })
    };
    return b;
}

exports.DamageUpTurret = (type, name, obj, max, add, less, limit) => {
    let b = extend(type, name, {
        setBars() {
            this.super$setBars();
            this.addBar("damageUp", func(e => new Bar(
                prov(() => exports.bundle("bar.damageUp", Strings.fixed(e.getCharmage(), 0))),
                prov(() => exports.FF5845),
                floatp(() => e.setBar()))));
        },
        canPlaceOn(tile, team, rotation) {
            if (!limit || exports.mapLimit()) return true;
            return Vars.state.teams.get(team).getCount(this) < limit;
        },
        drawPlace(x, y, rotation, valid) {
            this.super$drawPlace(x, y, rotation, valid);
            if (!limit || exports.mapLimit()) return
            if (Vars.state.teams.get(Vars.player.team()).getCount(this) < limit) return
            this.drawPlaceText(exports.limitBuild(this, limit), x, y, valid);
        }
    });
    Object.assign(b, obj);
    b.buildType = () => {
        let charmage = 0;
        return extend(turretBuild(type), b, {
            getCharmage() {
                return charmage + 100;
            },
            setBar() {
                return charmage / max;
            },
            updateTile() {
                this.super$updateTile();
                if (!this.isShooting() && charmage < max) {
                    charmage += add;
                }
            },
            handleBullet(bullet, offsetX, offsetY, angleOffset) {
                bullet.damage *= this.getCharmage() / 100;
                if (charmage > less) charmage -= less;
                else charmage = 0;
            }
        })
    };
    return b;
}