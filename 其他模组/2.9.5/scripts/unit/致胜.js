const my = require("base/物品");
const lib = require("base/coflib");
const units = require("unit/units");
const status = require("base/status");
const ability = require("base/ability");
const bullets = require("base/bullet");
const FX = require("base/Effect/fightFx");
const NX = require("base/Effect/normalFx");
let FF5845 = lib.FF5845, FF8663 = lib.FF8663, FEB380 = lib.FEB380;

let win = units.newUnit("致胜", UnitEntity, {
    flying: true,
    lowAltitude: true,
    health: 577000,
    armor: 256,
    hitSize: 158,
    speed: 1.2,
    accel: 0.04,
    drag: 0.05,
    range: 960,
    aimDst: 80,
    lightRadius: 240,
    rotateSpeed: 0.6,
    engineSize: 0, //16.8
    engineOffset: 112,
    outlineColor: lib.C1F1F1F,
    ammoCapacity: 2400,
    ammoType: new ItemAmmoType(my.肃正协议, 600),
    draw(unit) {
        this.super$draw(unit);
        FX.PlayerAim(unit, FF8663, 2.5);
    }
})

win.setEnginesMirror(new UnitType.UnitEngine(36, -96, 12, -45));//40/102

Vars.content.statusEffects().each(s => {
    if (s.damageMultiplier < 1 || s.healthMultiplier < 0.9 || s.speedMultiplier < 1 || s.reloadMultiplier < 1 || s.dragMultiplier < 1 || s.transitionDamage > 0 || s.damage > 0 || s.reactive == true || s.disarm == true) win.immunities.add(s);
});
win.immunities.addAll(status.EMP, status.寄生, status.湍能, status.连锁闪电, status.损毁, status.破甲, status.辐射, status.衰变, status.坍缩);

let EFA = Object.assign(new EnergyFieldAbility(850, 300, 480), {
    y: -38.75,
    maxTargets: 10,
    status: status.连锁闪电,
    statusDuration: 120,
    sectors: 8,
    sectorRad: 0.0625,
    color: FF8663,
    hitBuildings: false,
    healPercent: 2,
    effectRadius: 1,
    rotateSpeed: 0.5
});
win.abilities.addAll(EFA, ability.ShieldAbility(40, 60000, 3, 22, FF8663), ability.StatusAbility(240, status.湍能, 60), ability.PurificationAbility(60), Object.assign(ability.GuardFieldAbility(320, 3000, 0.6), {
    y: -38.75,
    orbRadius: 0,
    orbSinScl: 8,
    orbSinMag: 1,
    particles: 24,
    particleSize: 4,
    particleLen: 32,
    rotateScl: 3,
    particleLife: 360,
    particleInterp: f => Interp.swing.apply(Interp.slope.apply(f))
}));

let be = Object.assign(new ExplosionEffect(), {
    lifetime: 50,
    waveStroke: 5,
    waveLife: 8,
    waveColor: FEB380,
    sparkColor: FF8663,
    smokeColor: FF8663,
    waveRad: 40,
    smokeSize: 4,
    smokes: 7,
    smokeSizeBase: 0,
    sparks: 10,
    sparkRad: 40,
    sparkLen: 6,
    sparkStroke: 2
});

let b = Object.assign(bullets.TwiceHomingBulletType(400), {
    damage: 325,
    speed: 6,
    width: 8,
    height: 8,
    trailWidth: 2,
    trailLength: 10,
    splashDamage: 185,
    splashDamageRadius: 40,
    trailColor: FF8663,
    backColor: FF8663,
    frontColor: FEB380,
    hitEffect: be,
    despawnEffect: be
});

let charge = new Effect(120, e => {
    let rand = new Rand();
    rand.setSeed(e.id);
    Angles.randLenVectors(e.id, 24, rand.random(90, 200) * Mathf.curve(e.fout(), 0.25, 1), (x, y) => {
        Draw.color(FF8663);
        let rad = rand.random(9, 18);
        Fill.circle(e.x + x, e.y + y, e.fin() * rad);
        Draw.color(FEB380);
        Fill.circle(e.x + x, e.y + y, e.fin() * rad / 2);
        Drawf.light(e.x + x, e.y + y, e.fin() * rad * 1.5, FF8663, 0.7);
    });
})

let main = Object.assign(extend(Weapon, {
    draw(unit, mount) {
        this.super$draw(unit, mount);
        let xy = lib.AngleTrns(unit.rotation, -38.75);
        let x = xy.x + unit.x, y = xy.y + unit.y, z = Draw.z();
        let time = Math.max(1 - mount.recoil, 0.5), size = 16 * time;
        Draw.z(110);
        Draw.color(FF5845);
        NX.shiningCircle(unit.id, Time.time, x, y, size, 8, 120, 0, 20 * time, 30 * time, 360);
        Draw.color(FF8663);
        NX.shiningCircle(unit.id, Time.time, x, y, size * 0.6, 8, 120, 0, 15 * time, 22.5 * time, 360);
        Draw.z(z);
    }
}), {
    x: 0,
    y: -38.75,
    recoil: 0,
    shootY: 0,
    reload: 1200,
    mirror: false,
    chargeSound: Sounds.lasercharge,
    shootSound: Sounds.laserblast,
    shoot: Object.assign(new ShootPattern(), {
        firstShotDelay: 120
    }),
    bullet: Object.assign(bullets.FortressBulletType(320, 12, b, 8, 360, 0), {
        lifetime: 600,
        speed: 12,
        width: 16,
        height: 16,
        trailWidth: 8,
        trailLength: 24,
        trailColor: FF8663,
        hitColor: FF5845,
        backColor: FF5845,
        frontColor: FF8663,
        chargeEffect: charge,
        shootEffect: FX.knellShoot,
        despawnEffect: Fx.titanExplosion.wrap(FF8663)
    })
})

function strafe(wx, wy) {
    return Object.assign(new Weapon(lib.modName + "-致胜副炮"), {
        x: wx,
        y: wy,
        shake: 4,
        recoil: 4,
        shootY: 24,
        reload: 600,
        shootCone: 5,
        alternate: false,
        layerOffset: -0.0001,
        shootSound: Sounds.plasmadrop,
        bullet: Object.assign(new BasicBulletType(16, 1950), {
            lifetime: 54,
            width: 16,
            height: 24,
            hitSize: 40,
            trailLength: 16,
            trailWidth: 4,
            trailColor: FF8663,
            hitColor: FF8663,
            absorbable: false,
            reflectable: false,
            pierce: true,
            pierceBuilding: true,
            ammoMultiplier: 1,
            status: status.连锁闪电,
            statusDuration: 150,
            shootEffect: Fx.shootTitan,
            smokeEffect: Fx.shootSmokeTitan,
            splashDamage: 850,
            splashDamageRadius: 80,
            buildingDamageMultiplier: 4,
            trailChance: 1,
            trailInterval: 30,
            trailEffect: Object.assign(new ParticleEffect(), {
                particles: 2,
                lifetime: 20,
                length: 10,
                baseLength: 16,
                sizeFrom: 4,
                sizeTo: 0,
                colorFrom: FF8663,
                colorTo: lib.Color("FEB38080"),
                cone: 360
            }),
            hitShake: 7,
            hitSound: Sounds.plasmaboom,
            hitEffect: new MultiEffect(
                Object.assign(new ParticleEffect(), {
                    particles: 4,
                    lifetime: 60,
                    sizeFrom: 0,
                    sizeTo: 15,
                    length: 20,
                    baseLength: 48,
                    interp: Interp.exp10Out,
                    sizeInterp: Interp.swingOut,
                    colorFrom: FF8663,
                    colorTo: lib.Color("FEB38000"),
                    cone: 360
                }),
                Object.assign(new ParticleEffect(), {
                    particles: 22,
                    lifetime: 25,
                    line: true,
                    strokeFrom: 3,
                    strokeTo: 0,
                    lenFrom: 20,
                    lenTo: 0,
                    length: 63,
                    interp: Interp.exp10Out,
                    colorFrom: FF8663,
                    colorTo: FEB380,
                    cone: 360
                }),
                Object.assign(new WaveEffect(), {
                    lifetime: 25,
                    sizeFrom: 0,
                    sizeTo: 66,
                    strokeFrom: 3,
                    strokeTo: 0,
                    colorFrom: FF8663,
                    colorTo: FEB380
                })
            ),
            fragBullets: 4,
            fragBullet: Object.assign(new BasicBulletType(5, 340, "curse-of-flesh-crystal"), {
                lifetime: 60,
                drag: 0.03,
                width: 8,
                height: 12,
                shrinkY: 0,
                trailWidth: 2,
                trailLength: 12,
                trailColor: FF8663,
                frontColor: FEB380,
                backColor: FF8663,
                hitColor: FEB380,
                pierce: true,
                homingRange: 60,
                homingPower: 0.08,
                status: status.湍能,
                statusDuration: 60,
                hitSound: Sounds.bang,
                hitEffect: Fx.hitSquaresColor,
                despawnEffect: Fx.hitSquaresColor
            })
        })
    })
}

let tracee = Object.assign(new ExplosionEffect(), {
    lifetime: 20,
    waveStroke: 2,
    waveColor: FF8663,
    sparkColor: FF8663,
    waveRad: 12,
    smokeSize: 0,
    smokeSizeBase: 0,
    sparks: 10,
    sparkRad: 35,
    sparkLen: 4,
    sparkStroke: 1.5,
});

function trace(wx, wy) {
    return Object.assign(new Weapon(lib.modName + "-致胜追踪"), {
        x: wx,
        y: wy,
        reload: 35,
        recoil: 2,
        shake: 3,
        shootY: 0,
        rotate: true,
        shootCone: 3,
        rotateSpeed: 2,
        rotationLimit: 150,
        cooldownTime: 15,
        shoot: Object.assign(new ShootPattern(), {
            shots: 3,
            shotDelay: 5
        }),
        shootSound: Sounds.cannon,
        bullet: Object.assign(bullets.HomingBulletType(11, 550, 88, 6), {
            backColor: FF8663,
            frontColor: FEB380,
            width: 4,
            height: 16,
            pierceCap: 3,
            pierceBuilding: true,
            trailWidth: 2,
            trailLength: 12,
            trailColor: FF8663,
            trailChance: 0.25,
            trailEffect: Object.assign(new ParticleEffect(), {
                particles: 1,
                lifetime: 25,
                sizeFrom: 3,
                sizeTo: 0,
                cone: 360,
                length: 23,
                sizeInterp: Interp.pow10In,
                colorFrom: FF8663,
                colorTo: FEB380
            }),
            hitEffect: tracee,
            despawnEffect: tracee
        })
    })
}

function lancer(wx, wy, ang) {
    return Object.assign(new Weapon(lib.modName + "-致胜近防"), {
        x: wx,
        y: wy,
        reload: 60,
        recoil: 2,
        shake: 3,
        rotate: true,
        shootCone: 15,
        rotateSpeed: 3,
        baseRotation: -ang,
        rotationLimit: 120,
        cooldownTime: 60,
        alternate: false,
        shootSound: Sounds.laser,
        bullet: Object.assign(new LaserBulletType(550), {
            length: 320,
            shootEffect: new Effect(24, e => {
                Draw.color(Pal.redLight, e.color, e.fin());
                for (let i of Mathf.signs) Drawf.tri(e.x, e.y, 9 * e.fout(), 72, e.rotation + 90 * i);
                Drawf.light(e.x, e.y, 180, e.color, 0.9 * e.fout());
            }),
            colors: [FEB380, FF8663, FF5845],
            hitColor: FF8663,
            ammoMultiplier: 1,
            status: status.熔融,
            statusDuration: 30,
            sideAngle: 22.5
        })
    })
}

let engine = Object.assign(new Weapon("致胜引擎"), {
    x: 0,
    y: -110,
    reload: 300,
    mirror: false,
    shootY: 0,
    baseRotation: 180,
    useAmmo: false,
    alwaysShooting: true,
    alwaysContinuous: true,
    shootSound: Sounds.none,
    bullet: Object.assign(new ContinuousFlameBulletType(300), {
        colors: [lib.Color("FF58458C"), lib.Color("FF5845B2"), lib.Color("FF8663CC"), lib.Color("FF8663"), lib.Color("FEB380CC")],
        lifetime: 30,
        width: 12,
        length: 90,
        drawFlare: false,
        status: status.熔融,
        statusDuration: 150,
        hitEffect: new MultiEffect(
            Object.assign(new ParticleEffect(), {
                line: true,
                particles: 7,
                lifetime: 15,
                length: 65,
                cone: 360,
                strokeFrom: 2.5,
                strokeTo: 0,
                lenFrom: 8,
                lenTo: 0,
                colorFrom: FF5845,
                colorTo: FEB380
            }),
            Fx.hitFlameBeam
        )
    })
})

win.weapons.addAll(main, strafe(63.75, -5.5), strafe(79.5, 1.75), trace(18.25, 48.75), trace(51, -17.25), lancer(20.5, 102.5, 10), lancer(24.5, 82, 25), lancer(38.25, 44.75, 35), engine);