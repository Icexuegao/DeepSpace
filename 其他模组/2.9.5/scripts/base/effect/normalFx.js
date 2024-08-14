const lib = require("base/coflib");
exports.CenterTri = (e, radiu, speed, n, color) => {
    Draw.z(Layer.effect);
    Lines.stroke(radiu / 16, color || Pal.accent);
    for (let i = 0; i < n; i++) {
        let rot = speed * Time.time + i * 360 / n;
        let xy = lib.AngleTrns(rot, radiu * 1.5 + lib.float(0.05, 4, true));
        Lines.circle(e.x, e.y, radiu);
        Drawf.light(e.x + xy.x, e.y + xy.y, radiu * 1.25, Pal.accent, 0.6);
        Draw.rect(lib.region("arrows-back"), e.x + xy.x, e.y + xy.y, radiu / 2, radiu / 2, rot + 90);
    }
}

let statusLerp = 0;
exports.QuadrupleTri = (e, num, start, spacing) => {
    Draw.z(Layer.blockAdditive);
    statusLerp = Mathf.lerpDelta(statusLerp, e.power.status, 0.05);
    for (let l = 0; l < num; l++) {
        let length = start + l * spacing;
        Draw.color(Tmp.c1.set(Pal.darkMetal).lerp(e.team.color, statusLerp), Pal.darkMetal, Mathf.absin(Time.time + l * 50, 10, 1));
        for (let i = 0; i < 4; i++) {
            let rot = i * 90 + 45;
            let xy = lib.AngleTrns(rot, length);
            Draw.rect("launch-arrow", e.x + xy.x, e.y + xy.y, rot + 180);
        }
    }
}

exports.DoubleAim = (e, lifetime, length, width, spacing, size) => {
    let fin = Interp.pow2Out.apply(lifetime), fout = 1 - fin, arr = (length - size * 4) / spacing;
    let track = Mathf.curve(fin, 0, 0.25) * Mathf.curve(fout, 0, 0.3) * fin;
    Draw.z(Layer.effect);
    Lines.stroke(fin * 2, Pal.accent);
    for (let i = 0; i <= arr; i++) {
        Tmp.v1.trns(0, i * spacing);
        let f = Interp.pow3Out.apply(Mathf.clamp((fin * length - i * spacing) / spacing)) * (0.6 + track * 0.4) * Mathf.curve(Interp.pow10Out.apply(fout) * (arr - i + 1), 0, 0.01);
        for (let i of Mathf.signs) {
            Drawf.light(e.x + (Tmp.v1.x + size * 4) * i, e.y + Tmp.v1.y * i, 144 * Draw.scl * f * 1.25, Pal.accent, 0.6);
            Draw.rect(lib.region("aim"), e.x + (Tmp.v1.x + size * 4) * i, e.y + Tmp.v1.y * i, 144 * Draw.scl * f, 144 * Draw.scl * f, -90 * i);
        }
    }
    Tmp.v1.trns(0, 0, (2 - fin) * 8 * width);
    for (let i of Mathf.signs) {
        Lines.lineAngleCenter(e.x + Tmp.v1.x * i, e.y + Tmp.v1.y * i, 0, length * 2 * (0.4 + fin * 0.6) * Mathf.curve(Interp.pow10Out.apply(fout), 0, 0.01));
    }
}

exports.JumpIn = (unit, x, y) => {
    let size = unit.hitSize;
    Sounds.malignShoot.at(x, y);
    return new Effect(120, cons(e => {
        let rad = Interp.pow2In.apply(size * 1.2, 0, e.fin()) * 2;
        Draw.mixcol(e.color, 1);
        Draw.z((unit.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) - 0.01);
        Draw.rect(unit.fullIcon, e.x, e.y, rad, rad, e.rotation);
        Draw.reset();
        Draw.color(e.color);
        Drawf.light(e.x, e.y, rad * 1.25, Pal.accent, 0.6);
        let xy = lib.AngleTrns(e.rotation - 90, size / 2 * e.fout());
        Drawf.tri(e.x + xy.x, e.y + xy.y, size / 8 * e.fout(), size * 16 * e.fout(), e.rotation - 90);
    }))
}

exports.shiningCircle = (seed, time, x, y, radius, spikes, spikeDuration, durationRange, spikeWidth, spikeHeight, angleDrift) => {
    Fill.circle(x, y, radius);
    spikeWidth = Math.min(spikeWidth, 90);
    let idx = 0, v = [];
    for (let i = 0; i < spikes; i++) {
        let d = spikeDuration * (durationRange > 0 ? Mathf.randomSeed((seed + i) * 41, 1 - durationRange, 1 + durationRange) : 1);
        let timeOffset = Mathf.randomSeed((seed + i) * 314, 0, d);
        let timeSeed = Mathf.floor((time + timeOffset) / d);
        let fin = ((time + timeOffset) % d) / d;
        let fslope = (0.5 - Math.abs(fin - 0.5)) * 2;
        let angle = Mathf.randomSeed(Math.max(timeSeed, 1) + ((i + seed) * 245), 360);
        if (fslope > 0.0001) {
            idx = 0;
            let drift = angleDrift > 0 ? Mathf.randomSeed(Math.max(timeSeed, 1) + ((i + seed) * 162), -angleDrift, angleDrift) * fin : 0;
            for (let j = 0; j < 3; j++) {
                let ang = (j - 1) * spikeWidth + angle;
                Tmp.v1.trns(ang + drift, radius + (j == 1 ? (spikeHeight * fslope) : 0)).add(x, y);
                v[idx++] = Tmp.v1.x;
                v[idx++] = Tmp.v1.y;
            }
            Fill.tri(v[0], v[1], v[2], v[3], v[4], v[5]);
        }
    }
}

new Effect(60, e => {
    Draw.color(Color.sky);
    Angles.randLenVectors(e.id, 16, 120, e.rotation, 360, (x, y) => {
        for (var i = 0; i < 4; i++) {
            Drawf.tri(e.x + x * e.fin(Interp.pow2In), e.y + y * e.fin(Interp.pow2In), 4, 12, 90 * i);
        }
        Fx.missileTrail.at(e.x + x * e.fin(Interp.pow2In), e.y + y * e.fin(Interp.pow2In), 2, Color.sky);
        if (e.time > 0.9 * e.lifetime) {
            Lightning.create(null, Team.sharded, e.x + x * e.fin(Interp.pow2In), e.y + y * e.fin(Interp.pow2In), 360 * Math.random(), 0, 0, 0.9, ci)
        }
    })
})

exports.DotCircle = (draw, size, c, ct) => {
    let cc = c || lib.FF5845, cct = ct || lib.FF8663;
    draw.parts.addAll(
        Object.assign(new ShapePart(), {
            circle: true,
            hollow: true,
            y: -size / 2,
            moveY: size / 2,
            radius: 0,
            radiusTo: size,
            stroke: 0,
            strokeTo: 1.6,
            color: cc,
            colorTo: cct,
            layer: 110
        }),
        /*Object.assign(new ShapePart(), {
            circle: true,
            y: -size / 2,
            moveY: -size * 2 * 0.25,
            radius: 0,
            radiusTo: size / 8,
            color: cc,
            colorTo: cct,
            layer: 110
        }),*/
        Object.assign(new ShapePart(), {
            circle: true,
            y: -size / 2,
            moveY: size * 2 * 0.75,
            radius: 0,
            radiusTo: size / 8,
            color: cc,
            colorTo: cct,
            layer: 110
        })
    )
}