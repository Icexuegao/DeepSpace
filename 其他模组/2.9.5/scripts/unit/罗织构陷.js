const lib = require("base/coflib");
const {newUnit, immunities} = require("unit/units");
const status = require("base/status");
const ability = require("base/ability");
const bullet = require("base/bullet");
const weapons = require("base/weapon");

function tarantula(name) {
    let u = newUnit(name, LegsUnit, {
        health: 57900,
        hitSize: 32,
        armor: 57,
        drag: 0.05,
        speed: 1.08,
        groundLayer: 75,
        rotateSpeed: 3.6,
        legCount: 6,
        legLength: 36,
        stepShake: 0.2,
        legGroupSize: 3,
        legMoveSpace: 1,
        legExtension: -3,
        legBaseOffset: 14,
        legMaxLength: 1.1,
        legMinLength: 0.2,
        legLengthScl: 0.95,
        legForwardScl: 0.9,
        legSplashRange: 12,
        legSplashDamage: 100,
        hovering: true,
        singleTarget: true,
        lockLegBase: true,
        allowLegStep: true,
        outlineColor: lib.C1F1F1F,
        legContinuousMove: true
    });
    u.abilities.add(Object.assign(new RegenAbility(), {
        percentAmount: 1 / 100
    }), ability.DeathGiftAbility(160, status.屠戮, 900, 0.1, 1000));
    return u;
}

let lz = tarantula("罗织");

function strafe(wx, wy, under) {
    return Object.assign(weapons.ArmorWeapon("罗织机枪", Object.assign(bullet.ArmorBrokenBulletType(17, 137, 23, 0.2, 0.1), {
        width: 4,
        height: 32,
        status: status.破甲,
        statusDuration: 20,
        pierceArmor: false,
        pierceDamageFactor: 0.8,
        hitEffect: Fx.hitSquaresColor,
        hitColor: Pal.bulletYellowBack,
        shootEffect: Fx.shootSmokeSquareSparse
    })), {
        x: wx,
        y: wy,
        recoil: 1,
        shake: 3,
        reload: 3,
        shootY: 19,
        rotate: true,
        inaccuracy: 1,
        shootCone: 5,
        rotateSpeed: 3,
        rotationLimit: 25,
        cooldownTime: 65,
        layerOffset: under ? -0.001 : 0,
        shootSound: Sounds.shootSnap
    })
}

lz.weapons.add(strafe(14, 3.25, true), strafe(-8.5, -11.25));

let gx = tarantula("构陷");

function missile(wx, wy, under) {
    return Object.assign(new Weapon("curse-of-flesh-构陷导弹"), {
        x: wx,
        y: wy,
        recoil: 2,
        shake: 3,
        shootY: 7,
        reload: 255,
        inaccuracy: 5,
        shootCone: 30,
        alternate: false,
        baseRotation: -25,
        cooldownTime: 65,
        shoot: Object.assign(new ShootPattern(), {
            shots: 4,
            shotDelay: 4
        }),
        layerOffset: under ? -0.001 : 0,
        shootSound: Sounds.missileLarge,
        bullet: Object.assign(new MissileBulletType(5, 57, "missile-large"), {
            width: 12,
            height: 18,
            hitShake: 3,
            lifetime: 118,
            makeFire: true,
            despawnShake: 3,
            homingDelay: 10,
            homingRange: 80,
            homingPower: 0.08,
            status: StatusEffects.blasted,
            splashDamage: 273,
            splashDamageRadius: 33,
            scaledSplashDamage: true,
            trailChance: 1,
            trailEffect: Fx.smeltsmoke,
            hitEffect: new MultiEffect(Fx.blastExplosion, Fx.hitSquaresColor),
            hitColor: Pal.missileYellowBack,
            shootEffect: Fx.shootSmokeSquare
        })
    })
}

gx.weapons.add(missile(13.75, 11.5, true), missile(10.5, 0.5), missile(8.25, -13));

immunities(lz);
immunities(gx);