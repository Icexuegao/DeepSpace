const my = require("base/物品");
const lib = require("base/coflib");
const bullet = require("base/bullet");
const status = require("base/status");
const HX = require("base/Effect/hitFx");
const FX = require("base/Effect/fightFx");
let by = Pal.bulletYellowBack;
let ic = lib.Color("C0ECFF"), icb = lib.Color("87CEEB");
let ab = bullet.ArmorBrokenBulletType;
let se = FX.shockShootLarge;

let zy = new ItemTurret("箴言");
zy.smokeEffect = HX.hitExplosionLarge;

let he = new MultiEffect(
    Object.assign(new WaveEffect(), {
        lifetime: 25,
        sizeTo: 44,
        strokeFrom: 3,
        colorFrom: by,
        colorTo: by
    }),
    Object.assign(new ParticleEffect(), {
        particles: 12,
        lifetime: 15,
        line: true,
        strokeFrom: 3,
        lenFrom: 15,
        lenTo: 0,
        length: 53,
        interp: Interp.pow5Out,
        sizeInterp: Interp.pow2In,
        colorFrom: by,
        colorTo: by,
        cone: 36
    }),
    Object.assign(new ParticleEffect(), {
        particles: 5,
        sizeFrom: 6,
        length: 48,
        lifetime: 35,
        interp: Interp.pow2Out,
        sizeInterp: Interp.pow2In,
        colorFrom: by,
        colorTo: by,
        cone: 36
    })
)
let icehe = new MultiEffect(
    Object.assign(new WaveEffect(), {
        lifetime: 25,
        sizeFrom: 0,
        sizeTo: 44,
        strokeFrom: 3,
        strokeTo: 0,
        colorFrom: icb,
        colorTo: ic
    }),
    Object.assign(new ParticleEffect(), {
        particles: 12,
        lifetime: 15,
        line: true,
        strokeFrom: 3,
        lenFrom: 15,
        lenTo: 0,
        length: 53,
        interp: Interp.pow5Out,
        sizeInterp: Interp.pow2In,
        colorFrom: icb,
        colorTo: ic,
        cone: 36
    }),
    Object.assign(new ParticleEffect(), {
        particles: 5,
        sizeFrom: 4,
        length: 48,
        lifetime: 35,
        interp: Interp.pow2Out,
        sizeInterp: Interp.pow2In,
        colorFrom: icb,
        colorTo: ic,
        cone: 36
    })
)

let gb = Object.assign(ab(10, 550, 60, 1.1, 0), {
    width: 6,
    height: 35,
    hitColor: by,
    trailColor: by,
    trailLength: 16,
    trailWidth: 1.5,
    hitEffect: he,
    shootEffect: se,
    reloadMultiplier: 1.4,
    hitSound: Sounds.plasmaboom,
    pierceDamageFactor: 0.7
})
let tb = Object.assign(ab(10, 900, 60, 1.2, 1), {
    width: 6,
    height: 35,
    hitColor: by,
    trailColor: by,
    trailLength: 16,
    trailWidth: 1.5,
    hitEffect: he,
    shootEffect: se,
    status: status.衰变,
    statusDuration: 240,
    hitSound: Sounds.plasmaboom
})
let yb = Object.assign(ab(11, 1070, 55, 2, 3), {
    width: 6,
    height: 35,
    hitColor: by,
    trailColor: by,
    trailLength: 16,
    trailWidth: 1.5,
    hitEffect: he,
    shootEffect: se,
    status: status.破甲,
    statusDuration: 120,
    ammoMultiplier: 2,
    hitSound: Sounds.plasmaboom,
    pierceDamageFactor: 0.4
})
let db = Object.assign(ab(6, 2450, 100, 0.6, 1), {
    sprite: "curse-of-flesh-crystal",
    width: 6,
    height: 35,
    hitColor: ic,
    frontColor: ic,
    backColor: icb,
    trailColor: ic,
    trailLength: 16,
    trailWidth: 1.5,
    hitEffect: icehe,
    shootEffect: se,
    status: status.封冻,
    statusDuration: 360,
    ammoMultiplier: 2,
    reloadMultiplier: 0.4,
    hitSound: Sounds.plasmaboom,
    pierceDamageFactor: 0.8
})
let sb = Object.assign(ab(12, 1350, 50.5, 1.6, 2), {
    width: 6,
    height: 35,
    frontColor: Pal.surge,
    trailColor: Pal.surge,
    hitColor: Pal.surge,
    trailLength: 16,
    trailWidth: 1.5,
    hitEffect: he,
    shootEffect: se,
    lightning: 7,
    lightningLength: 6,
    lightningDamage: 55,
    lightningLengthRand: 7,
    status: StatusEffects.shocked,
    hitSound: Sounds.plasmaboom,
    pierceDamageFactor: 0.2
})

zy.ammo(
    Items.graphite, gb,
    Items.thorium, tb,
    my.铱板, yb,
    my.低温化合物, db,
    Items.surgeAlloy, sb
);