const lib = require("base/coflib");
const {newUnit, immunities} = require("unit/units");
const status = require("base/status");
const ability = require("base/ability");
const bullet = require("base/bullet");
const weapons = require("base/weapon");
const FX = require("base/Effect/fightFx");
const {陨石, 陨铁, 陨星} = require("unit/陨落");
require("unit/否决");
const {哨戒} = require("block/炮塔/哨戒");

let by = Pal.bulletYellowBack, pro = DrawPart.PartProgress;

function cwg(name) {
    let u = newUnit(name, LegsUnit, {
        health: 68700,
        hitSize: 48,
        armor: 77,
        speed: 0.8,
        groundLayer: 75,
        rotateSpeed: 1.2,
        legCount: 6,
        legLength: 36,
        stepShake: 0.2,
        legGroupSize: 3,
        legMoveSpace: 1,
        legExtension: -3,
        legBaseOffset: 16,
        legMaxLength: 1.1,
        legMinLength: 0.2,
        legLengthScl: 0.95,
        legForwardScl: 0.9,
        legSplashRange: 16,
        legSplashDamage: 150,
        hovering: true,
        lockLegBase: true,
        allowLegStep: true,
        outlineColor: lib.C1F1F1F,
        legContinuousMove: true
    });
    u.abilities.add(Object.assign(new RegenAbility(), {
        percentAmount: 1 / 100
    }), ability.DeathGiftAbility(160, status.复仇, 900, 0.1, 1000));
    return u;
}

let ck = cwg("摧枯");
let yb = Object.assign(bullet.ArmorBrokenBulletType(17, 1773, 35, 5, 5), {
    width: 6,
    height: 35,
    hitColor: by,
    trailColor: by,
    trailLength: 9,
    trailWidth: 1.5,
    trailInterval: 3,
    trailRotation: true,
    trailEffect: FX.shockShoot,
    status: status.破甲,
    statusDuration: 120,
    pierceDamageFactor: 0.4,
    hitEffect: Fx.hitSquaresColor,
    hitSound: Sounds.plasmaboom,
    smokeEffect: Fx.shootSmokeSmite,
    shootEffect: Fx.shootSmokeSquareBig
});
let b = Object.assign(bullet.SizeBulletType(11, 213, 40, 5), {
    width: 13,
    height: 14,
    hitColor: by,
    hitEffect: Fx.hitSquaresColor,
    shootEffect: Fx.shootSmokeSquare
});

let gun = Object.assign(weapons.SizeWeapon("摧枯速射", b), {
    x: 18.75,
    y: 15,
    recoil: 2,
    shake: 3,
    shootY: 8,
    reload: 25,
    rotate: true,
    shootCone: 5,
    rotateSpeed: 3,
    rotationLimit: 25,
    layerOffset: -0.001,
    shoot: Object.assign(new ShootPattern(), {
        shots: 3,
        shotDelay: 4
    }),
    shootSound: Sounds.cannon
});

ck.weapons.add(gun, Object.assign(weapons.ArmorWeapon("摧枯炮", yb), {
    x: 0,
    y: 5,
    shake: 7,
    recoil: 4,
    shootY: 33,
    reload: 145,
    rotate: true,
    shootCone: 1,
    mirror: false,
    recoilTime: 185,
    rotateSpeed: 1.2,
    rotationLimit: 45,
    minWarmup: 0.90,
    layerOffset: 0.001,
    cooldownTime: 185,
    shootWarmupSpeed: 0.06,
    shoot: Object.assign(new ShootAlternate(9), {
        shots: 2
    }),
    shootSound: Sounds.mediumCannon,
    parts: Seq.with(
        Object.assign(new RegionPart("-glow"), {
            progress: pro.warmup,
            outline: false,
            color: lib.Color("F03B0E"),
            blending: Blending.additive
        }),
        Object.assign(new RegionPart("-side"), {
            progress: pro.warmup.delay(0.2),
            heatProgress: pro.warmup.delay(0.2),
            moveX: 1.5,
            moveY: -1.5,
            under: true,
            mirror: true,
            heatColor: lib.Color("F03B0E")
        })
    )
}));


function wenyi(name) {
    let wy = newUnit(name, UnitEntity, {
        flying: true,
        lowAltitude: true,
        health: 57300,
        armor: 26,
        hitSize: 38,
        speed: 0.9,
        rotateSpeed: 1.6,
        engineSize: 6,
        engineOffset: 24,
        outlineColor: lib.C1F1F1F,
    });
    wy.weapons.add(Object.assign(weapons.TurretWeapon("文漪哨戒", Object.assign(bullet.TurretBulletType(13, 23, 哨戒), {
        hitColor: by,
        smokeEffect: Fx.shootSmokeSmite,
        shootEffect: Fx.shootSmokeSquareBig
    }), 15), {
        recoil: 1,
        shake: 2,
        reload: 300,
        layerOffset: -0.001
    }), weapons.SpawnWeapon("文漪陨石", 900, bullet.SpawnBulletType(240, 陨石)), weapons.SpawnWeapon("文漪陨铁", 1500, bullet.SpawnBulletType(240, 陨铁)));
    wy.abilities.add(Object.assign(new RegenAbility(), {
        percentAmount: 1 / 100
    }), ability.DeathGiftAbility(320, status.复仇, 900, 0.2, 500));
    return wy;
}

let wy1 = wenyi("文漪副本");
wy1.hidden = true;
let wy2 = wenyi("文漪");
wy2.abilities.add(ability.FlashbackAbility(0.1, 4, 80, 陨星, wy1));


let gl = cwg("甘霖");
gl.weapons.add(gun, weapons.RepairWeapon("甘霖修复", 0, -1, lib.FF5845, 17, 240));
gl.abilities.add(ability.DeathGiftAbility(320, status.屠戮, 900, 0.1, 1000), ability.ShieldAbility(4, 2400), Object.assign(ability.GuardFieldAbility(240, 200, 0.95), {
    y: -1,
    orbRadius: 12,
    orbMidScl: 0.4,
    orbSinScl: 8,
    orbSinMag: 1,
    particles: 24,
    particleSize: 4,
    particleLen: 32,
    rotateScl: 3,
    particleLife: 360,
    particleInterp: f => Interp.swing.apply(Interp.slope.apply(f))
}));

immunities(ck);
immunities(wy1);
immunities(wy2);
immunities(gl);