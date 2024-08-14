const lib = require("base/coflib");
exports.coloredHitSmall = new Effect(14, e => {
    Draw.color(Color.white, e.color, e.fin());
    e.scaled(7, s => {
        Lines.stroke(0.5 + s.fout());
        Lines.circle(e.x, e.y, s.fin() * 5);
    });
    Lines.stroke(0.5 + e.fout());
    Angles.randLenVectors(e.id, 5, e.fin() * 15, (x, y) => Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3 + 1));
})

exports.coloredHitLarge = new Effect(21, e => {
    Draw.color(Color.white, e.color, e.fin());
    e.scaled(8, s => {
        Lines.stroke(0.5 + s.fout());
        Lines.circle(e.x, e.y, s.fin() * 11);
    });
    Lines.stroke(0.5 + e.fout());
    Angles.randLenVectors(e.id, 6, e.fin() * 35, e.rotation + 180, 45, (x, y) => Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 7 + 1));
})

exports.empHit = new Effect(50, 100, e => {
    let rad = 70;
    e.scaled(7, b => {
        Draw.color(Pal.heal, b.fout());
        Fill.circle(e.x, e.y, rad);
    });
    Draw.color(Pal.heal);
    Lines.stroke(e.fout() * 3);
    Lines.circle(e.x, e.y, rad);
    let points = 10, offset = Mathf.randomSeed(e.id, 360);
    for (let i = 0; i < points; i++) {
        let angle = i * 360 / points + offset;
        Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6, 50 * e.fout(), angle);
    }
    Fill.circle(e.x, e.y, 12 * e.fout());
    Draw.color();
    Fill.circle(e.x, e.y, 6 * e.fout());
    Drawf.light(e.x, e.y, rad * 1.6, Pal.heal, e.fout());
})

exports.hitExplosionLarge = new Effect(30, 200, e => {
    Draw.color(Pal.missileYellow);
    e.scaled(12, s => {
        Lines.stroke(s.fout() * 2 + 0.5);
        Lines.circle(e.x, e.y, s.fin() * 60);
    });
    Draw.color(Color.gray);
    Angles.randLenVectors(e.id, 8, 2 + 42 * e.finpow(), (x, y) => {
        Fill.circle(e.x + x, e.y + y, e.fout() * 5 + 0.5);
    });
    Draw.color(Pal.missileYellowBack);
    Lines.stroke(e.fout() * 1.5);
    Angles.randLenVectors(e.id + 1, 5, 1 + 56 * e.finpow(), (x, y) => Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1 + e.fout() * 5));
    Drawf.light(e.x, e.y, 60, Pal.missileYellowBack, 0.8 * e.fout());
})

exports.hitExplosionMassive = new Effect(70, 370, e => {
    e.scaled(17, s => {
        Draw.color(Color.white, Color.lightGray, e.fin());
        Lines.stroke(s.fout() + 0.5);
        Lines.circle(e.x, e.y, e.fin() * 185);
    });
    Draw.color(Color.gray);
    Angles.randLenVectors(e.id, 12, 5 + 135 * e.finpow(), (x, y) => {
        Fill.circle(e.x + x, e.y + y, e.fout() * 22 + 0.5);
        Fill.circle(e.x + x / 2, e.y + y / 2, e.fout() * 9);
    });
    Draw.color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
    Lines.stroke(1.5 * e.fout());
    Angles.randLenVectors(e.id + 1, 14, 1 + 160 * e.finpow(), (x, y) => Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1 + e.fout() * 3));
})

exports.withstand = new Effect(16, e => {
    Lines.stroke(e.rotation / 2 * e.fout(), Pal.accent);
    Lines.square(e.x, e.y, e.rotation * 6, 45);
})

exports.maxDamage = new Effect(60, e => {
    Draw.color(lib.FF5845, lib.FF8663, e.fin());
    Lines.stroke(e.rotation / 2 * e.fout());
    Lines.square(e.x, e.y, e.rotation * 4 + e.rotation * 4 * e.finpow(), 45);
})

exports.shieldMaterializing = new Effect(40, e => {
    let offset = 20, cone = 2;
    let angle = cone / 2 * 180 * e.fin();
    let angleOff = angle / 2;

    Lines.stroke(1.25 * e.fslope(), Pal.heal);
    Tmp.v1.trns(e.rotation - 180, offset);
    let x = Tmp.v1.x, y = Tmp.v1.y;
    Lines.arc(e.x + x, e.y + y, offset * e.fin(), cone / 2 * e.fin(), e.rotation - angle - angleOff);
    Lines.arc(e.x + x, e.y + y, offset * e.fin() * 0.6, cone / 2 * e.fin(), e.rotation + angle - angleOff);
})