const lib = require("base/coflib");
exports.PlayerAim = (e, color, size, speed) => {
    let x = 0, y = 0;
    if (!size) size = 1;
    if (e instanceof Unit) {
        if (!e.isPlayer()) return;
        x = e.aimX, y = e.aimY;
    } else {
        if (!e.isControlled()) return;
        x = e.targetPos.getX(), y = e.targetPos.getY();
    }
    let tris = 3;
    Draw.color(color);
    Draw.z(Layer.effect);
    for (let i = 0; i < tris; i++) {
        let ang = i * 360 / tris + Time.time;
        let xy = lib.AngleTrns(ang, (24 + lib.float(speed || 4, 4)) * size);
        lib.DoubleTri(x + xy.x, y + xy.y, size * 4, size * 16, ang + 180);
    }
}

exports.LockPoint = (x, y, size, speed) => {
    let float = lib.float(3.14 * 1.5 * speed, size / 4);
    Fill.square(x, y, size, 45);
    for (let i = 0; i < 4; i++) {
        let rot = i * 90, xy = lib.AngleTrns(rot, 10 * size + float), xy2 = lib.AngleTrns(rot, 4 * size + float);
        lib.DoubleTri(x + xy.x, y + xy.y, 1 * size + float, 8 * size + float, rot, 1);
        lib.trapezium(x + xy2.x + xy2.y, y - xy2.x + xy2.y, 6 * size + float, 8 * size + float, size + float, 45 + rot);
    }
}

exports.prismaticSpikes = new Effect(45, 100, e => {
    Lines.stroke(e.fout() * 2, e.color);
    let rad = 4 + e.finpow() * e.rotation, xy = lib.AngleTrns(0, rad);
    Lines.quad(e.x, e.y + xy.x, e.x + xy.x * 2, e.y, e.x, e.y - xy.x, e.x - xy.x * 2, e.y);
    for (let i = 0; i < 4; i++) Drawf.tri(e.x, e.y, 6, e.rotation * 1.5 * e.fout(), i * 90);
    Draw.color();
    for (let i = 0; i < 4; i++) Drawf.tri(e.x, e.y, 3, e.rotation * 0.5 * e.fout(), i * 90);
    Drawf.light(e.x, e.y, rad * 1.6, Pal.heal, e.fout());
})

exports.railShoot = new Effect(24, e => {
    Draw.color(e.color);
    for (let i of Mathf.signs) {
        Drawf.tri(e.x, e.y, 13 * e.fout(), 85, e.rotation + 90 * i);
    }
    Drawf.light(e.x, e.y, 180, e.color, 0.9 * e.fout());
})

exports.knellShoot = new Effect(120, e => {
    Draw.color(Tmp.c1.set(e.color).lerp(lib.FF8663, e.fout()));
    for (let i = 0; i < 4; i++) {
        Drawf.tri(e.x, e.y, 12 * e.fout(), 90, e.rotation + 90 * i + e.finpow() * 112);
    }
    for (let h = 1; h <= 5; h++) {
        let mul = h % 2;
        let rm = 1 + mul * 0.5;
        let rot = 90 + (1 - e.finpow()) * Mathf.randomSeed(e.id + (mul * 2), 210 * rm, 360 * rm);
        for (let i = 0; i < 2; i++) {
            let m = i == 0 ? 1 : 0.5;
            let w = 24 * e.fout() * m;
            let length = (8 * 3 / (2 - mul)) * 3;
            let fxPos = Tmp.v1.trns(rot, length - 12);
            length *= Interp.PowOut(25).apply(e.fout());

            Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, length * m, rot + 180);
            Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, length / 3 * m, rot);

            Draw.alpha(0.5);
            Drawf.tri(e.x, e.y, w, length * m, rot + 360);
            Drawf.tri(e.x, e.y, w, length / 3 * m, rot);
            Fill.square(fxPos.x + e.x, fxPos.y + e.y, 3 * e.fout(), rot + 45);
        }
    }
})

exports.aimShoot = (color, lifetime, range, width, spacing) => {
    const basedata = {
        length: range
    };
    return new Effect(lifetime, range * 2, cons(e => {
        let data = e.data ? e.data : basedata;
        let track = Mathf.curve(e.fin(Interp.pow2Out), 0, 0.25) * Mathf.curve(e.fout(Interp.pow4Out), 0, 0.3) * e.fin();
        Lines.stroke(track * 2, color);
        for (let i = 0; i <= data.length / spacing; i++) {
            Tmp.v1.trns(e.rotation, i * spacing);
            let f = Interp.pow3Out.apply(Mathf.clamp((e.fin() * data.length - i * spacing) / spacing)) * (0.6 + track * 0.4);
            Drawf.light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144 * Draw.scl * f * 1.25, Pal.accent, 0.6);
            Draw.rect(lib.region("aim"), e.x + Tmp.v1.x, e.y + Tmp.v1.y, 144 * Draw.scl * f, 144 * Draw.scl * f, e.rotation - 90);
        }
        Tmp.v1.trns(e.rotation, 0, (2 - track) * 8 * width);
        for (let i of Mathf.signs) {
            Lines.lineAngle(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, e.rotation, data.length * (0.75 + track / 4) * Mathf.curve(e.fout(Interp.pow5Out), 0, 0.1));
        }
    }))
}

exports.chainLightningFade = new Effect(25, 500, e => {
    if (!(e.data instanceof Position)) return;
    let p = e.data;
    let tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
    Tmp.v1.set(p).sub(e.x, e.y).nor();

    let normx = Tmp.v1.x, normy = Tmp.v1.y, range = e.rotation;
    let links = Mathf.ceil(dst / range), spacing = dst / links;

    Lines.stroke(2.5 * Mathf.curve(e.fout(), 0, 0.7), e.color);

    Lines.beginLine();
    Lines.linePoint(e.x, e.y);
    Fx.rand.setSeed(e.id);

    let fin = Mathf.curve(e.fin(), 0, 0.5);
    for (let i = 0; i < links * fin; i++) {
        let nx, ny;
        if (i == links - 1) {
            nx = tx;
            ny = ty;
        } else {
            let len = (i + 1) * spacing;
            Tmp.v1.setToRandomDirection(Fx.rand).scl(range / 2);
            nx = e.x + normx * len + Tmp.v1.x;
            ny = e.y + normy * len + Tmp.v1.y;
        }
        Lines.linePoint(nx, ny);
    }
    Lines.endLine();
})
exports.chainLightningFade.followParent = false;

exports.chainLightningFade2 = new Effect(25, 500, e => {
    if (!(e.data instanceof Position)) return;
    let p = e.data;
    let tx = e.x, ty = e.y, dst = Mathf.dst(p.getX(), p.getY(), tx, ty);
    Tmp.v1.set(e.x, e.y).sub(p).nor();

    let normx = Tmp.v1.x, normy = Tmp.v1.y, range = e.rotation;
    let links = Mathf.ceil(dst / range), spacing = dst / links;

    Lines.stroke(2.5 * Mathf.curve(e.fout(), 0, 0.7), e.color);

    Lines.beginLine();
    Lines.linePoint(p);
    Fx.rand.setSeed(e.id);

    let fin = Mathf.curve(e.fout(), 0, 0.5);
    for (let i = 0; i < links * fin; i++) {
        let nx, ny;
        if (i == links - 1) {
            nx = tx;
            ny = ty;
        } else {
            let len = (i + 1) * spacing;
            Tmp.v1.setToRandomDirection(Fx.rand).scl(range / 2);
            nx = p.getX() + normx * len + Tmp.v1.x;
            ny = p.getY() + normy * len + Tmp.v1.y;
        }
        Lines.linePoint(nx, ny);
    }
    Lines.endLine();
})
exports.chainLightningFade2.followParent = false;

exports.chainLightningFadeReversed = new Effect(25, 500, e => {
    if (!(e.data instanceof Position)) return;
    let p = e.data;
    let tx = e.x, ty = e.y, dst = Mathf.dst(p.getX(), p.getY(), tx, ty);
    Tmp.v1.set(e.x, e.y).sub(p).nor();

    let normx = Tmp.v1.x, normy = Tmp.v1.y, range = e.rotation;
    let links = Mathf.ceil(dst / range), spacing = dst / links;

    Lines.stroke(2.5 * Mathf.curve(e.fout(), 0, 0.7), e.color);

    Lines.beginLine();
    Lines.linePoint(p);
    Fx.rand.setSeed(e.id);

    let fin = Mathf.curve(e.fin(), 0, 0.5);
    for (let i = 0; i < links * fin; i++) {
        let nx, ny;
        if (i == links - 1) {
            nx = tx;
            ny = ty;
        } else {
            let len = (i + 1) * spacing;
            Tmp.v1.setToRandomDirection(Fx.rand).scl(range / 2);
            nx = p.getX() + normx * len + Tmp.v1.x;
            ny = p.getY() + normy * len + Tmp.v1.y;
        }
        Lines.linePoint(nx, ny);
    }
    Lines.endLine();
})
exports.chainLightningFadeReversed.followParent = false;

exports.casing8Double = new Effect(75, e => {
    Draw.color(Pal.lightOrange, Pal.lightishGray, Pal.lightishGray, e.fin());
    Draw.alpha(e.fout(0.5));
    Draw.z(Layer.bullet);
    let rot = Math.abs(e.rotation) + 90;
    for (let i of Mathf.signs) {
        let len = (8 + e.finpow() * 40) * i;
        let lr = rot + Mathf.randomSeedRange(e.id + i + 6, 20 * e.fin()) * i;
        let xy = lib.AngleTrns(lr, len);
        Draw.rect("casing",
            e.x + xy.x + Mathf.randomSeedRange(e.id + i + 7, 3 * e.fin()),
            e.y + xy.y + Mathf.randomSeedRange(e.id + i + 8, 3 * e.fin()),
            8, 16, rot + e.fin() * 50 * i
        );
    }
})

exports.shockShoot = new Effect(30, e => {
    Draw.color(e.color, Color.white, e.fin());
    let interp = Interp.pow5In.apply(e.fin());
    Lines.stroke((1.5 + 2 * e.fout()) * Interp.pow5In.apply(e.fslope()));
    Lines.ellipse(e.x, e.y, 5, 16 * interp, 8 * interp, e.rotation - 90);
})

exports.shockShootLarge = new Effect(30, e => {
    Draw.color(e.color, Color.white, e.fin());
    let interp = Interp.pow2In.apply(e.fin());
    Lines.stroke((1.5 + 2 * e.fout()) * Interp.pow5In.apply(e.fslope()));
    for (let i = 3; i > 0; i--) {
        let xy = lib.AngleTrns(e.rotation, (3 - i) * 5);
        Lines.ellipse(e.x + xy.x, e.y + xy.y, 4, 6 * i * interp, 3 * i * interp, e.rotation - 90);
    }
})

exports.curveShoot = new Effect(60, e => {
    let rot = e.rotation, escapeLen = 26;
    let xy = lib.AngleTrns(rot, 15), x = xy.x, y = xy.y;

    Draw.color(Color.white, e.color, e.fin());
    Lines.stroke(1.4 * e.fslope());
    Tmp.v1.trns(rot + Math.sin(Time.time * 0.01) * 90, Mathf.dst(e.x, e.y, e.x + x, e.y + y));
    Tmp.v2.trns(rot + 90, escapeLen * Interp.pow5In.apply(e.fin()));
    Lines.curve(
        e.x, e.y,
        e.x, e.y,
        e.x + Tmp.v1.x, e.y + Tmp.v1.y,
        e.x + x + Tmp.v2.x, e.y + y + Tmp.v2.y, rot
    );
    Lines.curve(
        e.x, e.y,
        e.x, e.y,
        e.x - Tmp.v1.x, e.y - Tmp.v1.y,
        e.x + x - Tmp.v2.x, e.y + y - Tmp.v2.y, rot
    );
})

exports.destruct = new Effect(120, e => {
    let intensity = 2, baseLifetime = 26 + intensity * 15;
    e.lifetime = 43 + intensity * 35;
    Draw.color(Color.gray);
    Draw.alpha(0.9);
    for (let i = 0; i < 4; i++) {
        let lenScl = Mathf.randomSeed(e.id * 2 + i, 0.4, 1);
        let fi = i;
        e.scaled(e.lifetime * lenScl, b => {
            Angles.randLenVectors(e.id + fi - 1, Mathf.floor(3 * intensity), 14 * intensity * e.fin(Interp.pow10Out), (x, y) => {
                Fill.circle(e.x + x, e.y + y, b.fout(Interp.pow5Out) * (2 + intensity) * 1.8);
            });
        });

        e.scaled(baseLifetime, b => {
            b.scaled(5 + intensity * 2.5, i => {
                Lines.stroke((3.1 + intensity / 5) * i.fout());
                Lines.circle(e.x, e.y, (3 + i.fin() * 14) * intensity);
                Drawf.light(e.x, e.y, i.fin() * 14 * 2 * intensity, Color.white, 0.9 * e.fout());
            });

            Draw.color(lib.Color("FF9C5A"), Color.gray, e.fin());
            Lines.stroke(0.9 * b.fout() * intensity);

            Draw.z(Layer.effect);
            Angles.randLenVectors(e.id + 1, Math.floor(9 * intensity), 40 * intensity * e.finpow() + 0.001, (x, y) => {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), b.fout() * 2 * (3 + intensity));
                Drawf.light(e.x + x, e.y + y, (e.finpow() + 0.001 * 4 * (3 + intensity)) * 3.5, Draw.getColor(), 0.8);
            });
        });
    }
})

exports.launchUp = new Effect(540, e => {
    let alpha = e.fout(Interp.pow5Out), r = 3;
    let scale = (1 - alpha) * 1.3 + 1;
    let {region, timer} = e.data;
    let x = e.x + e.fin(Interp.pow2In) * (12 + Mathf.randomSeedRange(e.id + 3, 4));
    let y = e.y + e.fin(Interp.pow5In) * (100 + Mathf.randomSeedRange(e.id + 2, 30));
    let rotation = e.fin() * (130 + Mathf.randomSeedRange(e.id, 50));

    Draw.z(Layer.effect + 0.001);
    Draw.color(e.color, Pal.engine);
    let rad = 0.2 + e.fslope();
    Draw.alpha(alpha);
    Fill.light(x, y, 10, 25 * (rad + scale - 1), Tmp.c2.set(Pal.engine), Tmp.c1.set(lib.Color("FFBB6400")));
    for (let i = 0; i < 4; i++) Drawf.tri(x, y, 6, 40 * (rad + scale - 1), i * 90 + rotation);

    Draw.color();
    Draw.z(Layer.weather - 1);

    scale *= region.scl();
    let rw = region.width * scale, rh = region.height * scale;

    Draw.alpha(alpha);
    Draw.rect(region, x, y, rw, rh, rotation);
    Tmp.v1.trns(225, e.fin(Interp.pow3In) * 250);

    Draw.z(Layer.flyingUnit + 1);
    Draw.color(0, 0, 0, 0.22 * alpha);
    Draw.rect(region, x + Tmp.v1.x, y + Tmp.v1.y, rw, rh, rotation);

    Draw.reset();
    if (timer.get(4 - e.fin() * 2)) {
        Fx.rocketSmoke.at(x + Mathf.range(r), y + Mathf.range(r), e.fin());
    }
})

exports.launchDown = new Effect(180, e => {
    let alpha = e.fin(Interp.pow5Out), r = 3;
    let scale = (1 - alpha) * 1.3 + 1;
    let {region, timer} = e.data;
    let x = e.x + e.fout(Interp.pow2Out) * (12 + Mathf.randomSeedRange(e.id + 3, 4));
    let y = e.y + e.fout(Interp.pow5Out) * (100 + Mathf.randomSeedRange(e.id + 2, 30));
    let rotation = e.fout() * (130 + Mathf.randomSeedRange(e.id, 50));

    Draw.z(Layer.effect + 0.001);
    Draw.color(e.color, Pal.engine);
    let rad = 0.2 + e.fslope();
    Draw.alpha(alpha);
    Fill.light(x, y, 10, 25 * (rad + scale - 1), Tmp.c2.set(Pal.engine), Tmp.c1.set(lib.Color("FFBB6400")));
    for (let i = 0; i < 4; i++) Drawf.tri(x, y, 6, 40 * (rad + scale - 1), i * 90 + rotation);

    Draw.color();
    Draw.z(Layer.weather - 1);

    scale *= region.scl();
    let rw = region.width * scale, rh = region.height * scale;

    Draw.alpha(alpha);
    Draw.rect(region, x, y, rw, rh, rotation);
    Tmp.v1.trns(225, e.fout(Interp.pow3Out) * 250);

    Draw.z(Layer.flyingUnit + 1);
    Draw.color(0, 0, 0, 0.22 * alpha);
    Draw.rect(region, x + Tmp.v1.x, y + Tmp.v1.y, rw, rh, rotation);

    Draw.reset();
    if (timer.get(4 - e.fout() * 2)) {
        Fx.rocketSmoke.at(x + Mathf.range(r), y + Mathf.range(r), e.fout());
    }
})