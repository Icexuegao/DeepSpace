const lib = require("base/coflib");
const bullets = require("base/bullet");

let pro = DrawPart.PartProgress;
let EA8878 = lib.Color("EA8878"), F03B0E = lib.Color("F03B0E"), FF8663 = lib.FF8663;
let spikes = new MultiEffect(
    Fx.hitSquaresColor,
    Fx.dynamicSpikes.wrap(EA8878, 36),
    Object.assign(new ParticleEffect(), {
        particles: 7,
        lifetime: 28,
        length: 20,
        cone: 360,
        sizeFrom: 5,
        interp: Interp.circleOut,
        colorFrom: EA8878,
        colorTo: FF8663
    })
)
let b = Object.assign(bullets.TorrentBulletType(12, 475, 110, 2), {
    width: 8,
    height: 8,
    knockback: 0.5,
    ammoMultiplier: 1,
    splashDamage: 335,
    splashDamageRadius: 40,
    status: StatusEffects.melting,
    statusDuration: 60,
    trailLength: 10,
    trailWidth: 4,
    trailColor: lib.FF8663,
    backColor: lib.FF8663,
    frontColor: lib.FEB380,
    hitColor: EA8878,
    shootEffect: Fx.none,
    smokeEffect: Fx.shootSmokeSquareSparse,
    hitShake: 1,
    despawnShake: 2,
    hitEffect: spikes,
    despawnEffect: spikes,
    parts: Seq.with(Object.assign(new FlarePart(), {
        progress: pro.life,
        color1: EA8878,
        stroke: 6,
        radius: 0,
        radiusTo: 40
    }))
});

let hl = lib.limitTurret(PowerTurret, "洪流", 4);
hl.shootType = b;
hl.unitSort = (u, x, y) => -u.hitSize + Mathf.dst2(u.x, u.y, x, y) / 6400;

hl.drawer = (() => {
    const d = new DrawTurret();
    for (let i = 1; i < 4; i++) {
        d.parts.add(Object.assign(new RegionPart("-wing"), {
            progress: pro.warmup.delay(i / 5),
            heatProgress: pro.warmup.sin((4 - i) * 10, 10, 1),
            under: true,
            mirror: true,
            outline: false,
            moveX: 1.6 + i * 6.4,
            moveY: 14.4,
            moveRot: i * 60 - 130,
            layerOffset: -0.3,
            heatColor: F03B0E,
            color: lib.Color("FF9C5A"),
            turretHeatLayer: 50 - 0.2
        }))
    }
    for (let i = 0; i < 4; i++) {
        d.parts.add(Object.assign(new RegionPart("-wing-side"), {
            progress: pro.warmup.delay(i / 5),
            heatProgress: pro.warmup.sin(i * 10, 10, 1),
            under: true,
            mirror: true,
            outline: false,
            moveX: 24 - i,
            moveY: -8.8 - i * 3,
            moveRot: -i * 30,
            layerOffset: -0.3,
            heatColor: F03B0E,
            color: lib.Color("FF9C5A"),
            turretHeatLayer: 50 - 0.2
        }))
    }
    d.parts.addAll(
        Object.assign(new RegionPart("-shot"), {
            heatProgress: pro.warmup,
            moveY: -14,
            heatColor: F03B0E
        }),
        Object.assign(new RegionPart("-bottom"), {
            heatProgress: pro.warmup,
            heatColor: F03B0E
        }),
        Object.assign(new RegionPart("-front"), {
            heatProgress: pro.warmup,
            mirror: true,
            moveX: 16,
            moveY: -6.4,
            moveRot: 33,
            heatColor: F03B0E
        }),
        Object.assign(new RegionPart("-back"), {
            heatProgress: pro.warmup,
            mirror: true,
            moveRot: -12,
            heatColor: F03B0E,
            turretHeatLayer: 50 - 0.000001
        }),
        Object.assign(new RegionPart("-back"), {
            heatProgress: pro.warmup,
            mirror: true,
            moveX: 3.2,
            moveY: 8,
            moveRot: 12,
            heatColor: F03B0E
        }),
        Object.assign(new RegionPart("-mid"), {
            heatProgress: pro.warmup,
            moveY: -15,
            heatColor: F03B0E
        }),
        Object.assign(new ShapePart(), {
            y: 40,
            circle: true,
            hollow: true,
            stroke: 0,
            strokeTo: 1.6,
            radius: 17.6,
            color: FF8663,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            y: 40,
            sides: 3,
            hollow: true,
            stroke: 0,
            strokeTo: 1.6,
            radius: 16,
            color: FF8663,
            rotateSpeed: -2,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            y: 40,
            sides: 3,
            hollow: true,
            stroke: 0,
            strokeTo: 1.6,
            radius: 5,
            color: FF8663,
            rotateSpeed: 1,
            layer: 110
        }),
        lib.Halo({
            mirror: false,
            y: 40,
            triL: 0,
            triLT: 8,
            shapes: 4,
            radius: 9.6,
            shapeR: 180,
            haloRad: 17,
            haloRS: 0.75,
            color: FF8663,
            colorTo: FF8663
        }),
        Object.assign(new ShapePart(), {
            y: -32,
            circle: true,
            hollow: true,
            stroke: 0,
            strokeTo: 1.6,
            radius: 10,
            color: FF8663,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            y: -32,
            sides: 4,
            hollow: true,
            stroke: 0,
            strokeTo: 1.2,
            radius: 4.8,
            rotateSpeed: 0.5,
            color: FF8663,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            y: -32,
            sides: 4,
            hollow: true,
            stroke: 0,
            strokeTo: 1.2,
            radius: 8.4,
            rotateSpeed: -0.5,
            color: FF8663,
            layer: 110
        }),
        Object.assign(new ShapePart(), {
            y: -32,
            moveY: -6,
            circle: true,
            hollow: true,
            stroke: 0,
            strokeTo: 2,
            radius: 16,
            color: FF8663,
            layer: 110
        })
    )
    lib.DoubleHalo(d, {
        mirror: false,
        y: -32,
        moveY: -6,
        shapes: 3,
        radius: 0,
        radiusTo: 6,
        triL: 12,
        haloRS: 1,
        haloRad: 21,
        color: FF8663
    })
    lib.DoubleHalo(d, {
        x: 12,
        y: -44,
        shapes: 1,
        radius: 0,
        radiusTo: 4.8,
        triL: 28,
        haloRot: -135,
        color: FF8663
    })
    lib.DoubleHalo(d, {
        haloRot: 180,
        mirror: false,
        y: -50,
        shapes: 1,
        radius: 0,
        radiusTo: 6,
        triL: 36,
        color: FF8663
    })
    return d;
})();