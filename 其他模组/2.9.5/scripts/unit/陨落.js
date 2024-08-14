const lib = require("base/coflib");
const {newUnit, immunities} = require("unit/units");
const {熔融, 损毁, 破甲} = require("base/status");
const ability = require("base/ability");
const bullet = require("base/bullet");
const weapons = require("base/weapon");

let by = Pal.bulletYellowBack, FFA665 = lib.Color("FFA665");

let ys = newUnit("陨石", UnitEntity, {
    flying: true,
    lowAltitude: true,
    health: 3450,
    armor: 13,
    hitSize: 18,
    speed: 2.4,
    accel: 0.08,
    drag: 0.04,
    rotateSpeed: 4.8,
    engineSize: 3,
    engineOffset: 12,
    healColor: FFA665,
    outlineColor: lib.C1F1F1F
});
exports.陨石 = ys;
ys.weapons.add(Object.assign(new Weapon("curse-of-flesh-陨石炮"), {
    x: -6,
    y: 5.5,
    recoil: 1,
    shake: 0.5,
    reload: 35,
    mirror: false,
    shootCone: 5,
    inaccuracy: 1,
    rotationLimit: 25,
    cooldownTime: 65,
    ejectEffect: Fx.casing1,
    shoot: Object.assign(new ShootPattern(), {
        shots: 4,
        shotDelay: 4
    }),
    layerOffset: -0.001,
    shootSound: Sounds.shoot,
    bullet: Object.assign(new BasicBulletType(7, 37), {
        width: 8,
        height: 12,
        lifetime: 27,
        hitColor: by,
        hitEffect: Fx.hitSquaresColor,
        shootEffect: Fx.shootSmokeSquare
    })
}), weapons.RepairWeapon("陨石修复", 0.5, -2, FFA665, 5, 120), weapons.PointDefenseWeapon("陨石点防", 0.5, -2, FFA665, 9, 67, 216));

let EFA1 = Object.assign(new EnergyFieldAbility(155, 85, 160), {
    x: 0.5,
    y: -2,
    healPercent: 2,
    effectRadius: 2,
    maxTargets: 20,
    statusDuration: 180,
    color: FFA665,
    status: StatusEffects.melting
});
ys.abilities.add(EFA1);


let yt = newUnit("陨铁", UnitEntity, {
    flying: true,
    lowAltitude: true,
    health: 10700,
    armor: 21,
    hitSize: 23,
    speed: 1.9,
    accel: 0.04,
    drag: 0.016,
    rotateSpeed: 3.1,
    engineSize: 5,
    engineOffset: 14,
    healColor: FFA665,
    outlineColor: lib.C1F1F1F
});
exports.陨铁 = yt;

function strafe(wx, wy) {
    return Object.assign(new Weapon("curse-of-flesh-沧溟机炮"), {
        x: wx,
        y: wy,
        recoil: 1,
        shake: 1,
        reload: 43,
        rotate: true,
        mirror: false,
        shootCone: 5,
        inaccuracy: 1,
        rotateSpeed: 6,
        cooldownTime: 65,
        ejectEffect: Fx.casing2,
        shoot: Object.assign(new ShootPattern(), {
            shots: 2,
            shotDelay: 4
        }),
        shootSound: Sounds.shootBig,
        bullet: Object.assign(new BasicBulletType(7, 73), {
            width: 8,
            height: 12,
            lifetime: 39,
            hitColor: by,
            splashDamage: 25,
            splashDamageRadius: 16,
            status: 损毁,
            statusDuration: 60,
            hitEffect: Fx.flakExplosion,
            shootEffect: Fx.shootSmokeSquare,
            despawnEffect: Fx.hitSquaresColor
        })
    })
}

yt.weapons.add(strafe(-2.25, 1.5), Object.assign(new Weapon("curse-of-flesh-陨铁激光"), {
    x: 8,
    y: 4,
    recoil: 0,
    shake: 3,
    reload: 85,
    mirror: false,
    shootCone: 5,
    cooldownTime: 115,
    shoot: Object.assign(new ShootPattern(), {
        shots: 2,
        shotDelay: 10
    }),
    shootSound: Sounds.laser,
    bullet: Object.assign(new LaserBulletType(334), {
        width: 25,
        length: 230,
        sideAngle: 20,
        sideWidth: 1.5,
        sideLength: 80,
        colors: [
            lib.Color("EC7458AA"),
            lib.Color("FF9C5A"),
            Color.white],
        shootEffect: Fx.shockwave
    })
}), weapons.RepairWeapon("陨铁修复", -3.75, -6.75, FFA665, 8, 160), weapons.PointDefenseWeapon("陨铁点防", -3.75, -6.75, FFA665, 7, 85, 216));

let EFA2 = Object.assign(new EnergyFieldAbility(225, 85, 200), {
    x: -3.75,
    y: -6.75,
    healPercent: 2,
    effectRadius: 2,
    maxTargets: 20,
    statusDuration: 180,
    color: FFA665,
    status: StatusEffects.melting
});
yt.abilities.add(EFA2);


let yx = newUnit("陨星", UnitEntity, {
    flying: true,
    lowAltitude: true,
    health: 25300,
    armor: 29,
    hitSize: 44,
    speed: 1.3,
    accel: 0.04,
    drag: 0.04,
    rotateSpeed: 2.3,
    engineSize: 0,
    engineOffset: 24,
    healColor: FFA665,
    outlineColor: lib.C1F1F1F,
    engines: Seq.with(UnitType.UnitEngine(3, -22, 6, -90))
});
exports.陨星 = yx;

let b = Blocks.afflict.shootType;
b.recoil = 3;
b.hitColor = b.backColor = b.trailColor = FFA665;
let b2 = b.fragBullet;
b2.hitEffect.colorFrom = b2.despawnEffect.colorTo = FFA665;
b2.hitColor = b2.backColor = b2.trailColor = FFA665;
b.intervalBullet = b2;

yx.weapons.addAll(strafe(-11.25, 20.75), strafe(-15, 12.25), strafe(14.75, 3.5), Object.assign(new Weapon("curse-of-flesh-陨星副炮"), {
    x: -12.75,
    y: -17.25,
    recoil: 2,
    shake: 3,
    reload: 35,
    mirror: false,
    shootCone: 5,
    cooldownTime: 65,
    ejectEffect: Fx.casing1,
    shoot: Object.assign(new ShootPattern(), {
        shots: 4,
        shotDelay: 4
    }),
    shootSound: Sounds.shoot,
    bullet: Object.assign(new BasicBulletType(9, 73), {
        width: 8,
        height: 12,
        lifetime: 41,
        hitColor: by,
        splashDamage: 47,
        splashDamageRadius: 32,
        status: 破甲,
        statusDuration: 75,
        hitEffect: Fx.flakExplosion,
        shootEffect: Fx.shootSmokeSquare,
        despawnEffect: Fx.hitSquaresColor
    })
}), Object.assign(new Weapon("curse-of-flesh-陨星主炮"), {
    x: 3.25,
    y: 5,
    recoil: 0,
    shake: 3,
    reload: 165,
    mirror: false,
    shootCone: 20,
    cooldownTime: 185,
    ejectEffect: Fx.casing4,
    shootSound: Sounds.cannon,
    bullet: b,
}), weapons.RepairWeapon("陨星修复", 3, -7.75, FFA665, 11, 200), weapons.PointDefenseWeapon("陨星点防", 3, -7.75, FFA665, 5, 131, 256));

let EFA3 = Object.assign(new EnergyFieldAbility(255, 85, 240), {
    x: 3,
    y: -7.75,
    healPercent: 2,
    effectRadius: 3,
    maxTargets: 20,
    statusDuration: 180,
    color: FFA665,
    status: 熔融
});

yx.abilities.add(EFA3, Object.assign(new ShieldArcAbility(), {
    x: 33,
    regen: 5,
    max: 3000,
    cooldown: 60 * 8,
    angleOffset: 90,
    angle: 75,
    radius: 63,
    width: 5,
    whenShooting: false
}), Object.assign(new ShieldArcAbility(), {
    x: -6,
    y: -6,
    regen: 5,
    max: 3000,
    cooldown: 60 * 8,
    angleOffset: -90,
    angle: 96,
    radius: 36,
    width: 5,
    whenShooting: false
}));

immunities(ys);
immunities(yt);
immunities(yx);