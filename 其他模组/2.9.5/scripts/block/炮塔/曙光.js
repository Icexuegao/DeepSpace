const my = require("base/物品");
const lib = require("base/coflib");
const status = require("base/status");
let C8CA9E8 = lib.Color("8CA9E8"), D1EFFF = lib.Color("D1EFFF");
let b = Object.assign(new LaserBulletType(1650), {
    length: 480,
    shootEffect: Object.assign(new ParticleEffect(), {
        line: true,
        particles: 12,
        lifetime: 20,
        length: 45,
        cone: 30,
        lenFrom: 6,
        lenTo: 6,
        strokeFrom: 3,
        strokeTo: 0,
        lightColor: C8CA9E8,
        colorFrom: D1EFFF,
        colorTo: C8CA9E8
    }),
    colors: [lib.Color("6569C9"), C8CA9E8, D1EFFF],
    ammoMultiplier: 1,
    status: status.湍能,
    statusDuration: 30,
    sideAngle: 22.5,
    hitEffect: Object.assign(new ParticleEffect(), {
        line: true,
        particles: 12,
        lifetime: 20,
        length: 75,
        cone: 360,
        lenFrom: 6,
        lenTo: 6,
        strokeFrom: 3,
        strokeTo: 0,
        lightColor: C8CA9E8,
        colorFrom: D1EFFF,
        colorTo: C8CA9E8
    })
})

let sg = lib.SpeedUpTurret(PowerTurret, "曙光", {
    health: 13720,
    size: 7,
    range: 360,
    reload: 132,
    shootType: b,
    shake: 4,
    recoil: 4,
    shootY: 15,
    shootCone: 45,
    rotateSpeed: 2,
    recoilTime: 90,
    cooldownTime: 90,
    canOverdrive: false,
    minWarmup: 0.98,
    shootWarmupSpeed: 0.08,
    warmupMaintainTime: 120,
    unitSort: UnitSorts.strongest,
    shootSound: Sounds.laser
}, 12, 600);
sg.consumePower(85);
sg.setupRequirements(
    Category.turret,
    BuildVisibility.shown,
    ItemStack.with(
        Items.lead, 4400,
        Items.graphite, 3200,
        my.铱板, 2100,
        my.导能回路, 1900,
        my.陶钢, 840,
        Items.phaseFabric, 670
    )
);

sg.drawer = new DrawMulti(
    (() => {
        const d = new DrawTurret();
        d.parts.addAll(
            Object.assign(new RegionPart("-glow"), {
                heatProgress: DrawPart.PartProgress.warmup,
                drawRegion: false,
                heatColor: lib.Color("F03B0E")
            }),
            /*Object.assign(new ShapePart(), {
                progress: DrawPart.PartProgress.warmup.sin(15, 20, 0.6),
                hollow: true,
                circle: true,
                y: -80,
                radius: 16,
                stroke: 4,
                color: FF5845,
                colorTo: FF8663,
                layer: 110
            })*/
        )
        /*lib.DoubleHalo(d, {
            progress: DrawPart.PartProgress.warmup.sin(15, 20, 0.6),
            y: -80,
            radius: 3,
            triL: 24,
            haloRS: -1,
            haloRot: 90,
            haloRad: 32,
            color: FF5845,
            colorTo: FF8663
        })*/
        return d;
    })(),
    Object.assign(new DrawPulseShape(false), {
        color: D1EFFF,
        timeScl: 120,
        stroke: 2,
        minStroke: 0,
        layer: 49
    })
)