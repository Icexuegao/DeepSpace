const my = require("base/物品");
const lib = require("base/coflib");
const status = require("base/status");
let C8CA9E8 = lib.Color("8CA9E8"), D1EFFF = lib.Color("D1EFFF");
let b = Object.assign(new LaserBulletType(3565), {
    length: 8000,
    width: 40,
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
    statusDuration: 120,
    sideAngle: 22.5,
    buildingDamageMultiplier: 0.05,
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

let mg = lib.SpeedUpTurret(PowerTurret, "暮光", {
    size: 6,
    range: 2160,
    reload: 1200,
    shootType: b,
    shake: 8,
    recoil: 5,
    shootY: 0,
    shootCone: 45,
    rotateSpeed: 2,
    recoilTime: 180,
    cooldownTime: 180,
    canOverdrive: false,
    minWarmup: 0.98,
    shootWarmupSpeed: 0.08,
    warmupMaintainTime: 120,
    fogRadiusMultiuplier: 0.2,
    unitSort: UnitSorts.strongest,
    shootSound: Sounds.malignShoot
}, 120, 3600, 8);
mg.consumePower(165);
mg.consumeLiquid(Liquids.cryofluid, 1.5);
mg.setupRequirements(
    Category.turret,
    BuildVisibility.shown,
    ItemStack.with(
        Items.copper, 3300,
        Items.lead, 2800,
        Items.silicon, 2350,
        my.铱板, 1500,
        my.导能回路, 1900,
        my.陶钢, 900,
        Items.phaseFabric, 585,
        my.肃正协议, 1
    )
);

let pro = DrawPart.PartProgress;
mg.drawer = (() => {
    const d = new DrawTurret();
    for (let i = 0; i < 5; i++) {
        d.parts.add(
            Object.assign(new RegionPart("-aim"), {
                heatProgress: pro.warmup.sin((4 - i) * 10, 20, 1),
                heatLight: true,
                drawRegion: false,
                y: (i + 1) * 24 / (1 - i / 24),
                xScl: 1 - i * 0.1,
                yScl: 1 - i * 0.1,
                heatColor: lib.Color("D1EFFF")
            })
        )
    }
    d.parts.add(
        Object.assign(new RegionPart("-glow"), {
            heatProgress: pro.warmup,
            heatColor: lib.Color("F03B0E"),
            drawRegion: false
        })
    )
    return d;
})();