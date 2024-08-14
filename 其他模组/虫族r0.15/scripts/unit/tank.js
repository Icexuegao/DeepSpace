const status = require('zerg/status');
const liquid = require('zerg/liquid');
const Ef = require('zerg/Effect');
const {ReduceArmorBulletType} = require('zerg/base/bulletType')

function StatWeapon(name, stat, num) {
    return extend(Weapon, {
        name: name,
        addStats(u, t) {
            this.super$addStats(u, t);
            t.row();
            t.add(Core.bundle.format(stat, num));
        }
    });
}

function ShootEncircle(range, speed) {
    return extend(ShootPattern, {
        shoot(totalShots, handler, barrelIncrementer) {
            handler.shoot(0, 0, 0, 0,
                b => b.moveRelative(
                    Angles.trnsx(b.time * 6 * speed, 0, range),
                    Angles.trnsy(b.time * 6 * speed, 0, range)
                ))
            handler.shoot(0, 0, 0, 0,
                b => b.moveRelative(
                    Angles.trnsx(b.time * 6 * speed, 0, -1 * range),
                    Angles.trnsy(b.time * 6 * speed, 0, -1 * range)
                ))
        }
    })
}

const pioneer = new TankUnitType("pioneer");
exports.pioneer = pioneer;
Object.assign(pioneer, {
    targetAir: false,
    speed: 0.75,
    hitSize: 10,
    treadPullOffset: 3,
    treadRects: [new Rect(6, -24, 14, 48)],
    treadFrames: 8,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    rotateSpeed: 3,
    health: 400,
    armor: 3,
    itemCapacity: 0,
    constructor: () => new TankUnit.create(),
})
pioneer.weapons.add(
    Object.assign(new StatWeapon("zerg-pioneer-weapon", "reduceArmor", 1), {
        layerOffset: 0.0001,
        reload: 50,
        shootY: 6.25,
        recoil: 1,
        rotate: true,
        rotateSpeed: 3.7,
        mirror: false,
        x: 0,
        y: -1.5,
        heatColor: Color.red,
        cooldownTime: 50,
        shootSound: Sounds.mediumCannon,

        bullet: Object.assign(new ReduceArmorBulletType(4, 25, 1), {
            width: 5,
            height: 7,
            lifetime: 40,
            hitSize: 4,
            hitColor: Color.valueOf("d06b53"),
            backColor: Color.valueOf("d06b53"),
            trailColor: Color.valueOf("d06b53"),
            frontColor: Color.white,
            trailWidth: 1.7,
            trailLength: 5,
        })
    })
)

const brigadier = new TankUnitType("brigadier");
exports.brigadier = brigadier;
Object.assign(brigadier, {
    speed: 0.65,
    hitSize: 16,
    treadPullOffset: 3,
    treadRects: [new Rect(4, -32, 26, 72)],
    treadFrames: 8,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    rotateSpeed: 2.5,
    health: 900,
    armor: 5,
    itemCapacity: 0,
    constructor: () => new TankUnit.create(),
})
brigadier.weapons.add(
    Object.assign(new StatWeapon("zerg-brigadier-weapon", "reduceArmor", 2), {
        layerOffset: 0.0001,
        reload: 25,
        shootY: 10,
        recoil: 3,
        rotate: true,
        rotateSpeed: 3.7,
        mirror: false,
        x: 0,
        y: 0,
        heatColor: Color.red,
        cooldownTime: 50,
        shootSound: Sounds.mediumCannon,
        shoot: new ShootAlternate(3.5),

        bullet: Object.assign(new ReduceArmorBulletType(4, 55, 2), {
            width: 7,
            height: 9,
            lifetime: 40,
            hitSize: 12,
            hitColor: Color.valueOf("d06b53"),
            backColor: Color.valueOf("d06b53"),
            trailColor: Color.valueOf("d06b53"),
            frontColor: Color.white,
            trailWidth: 1.7,
            trailLength: 5,

            pierceCap: 3,
        })
    })
)

const kibbler = new TankUnitType("kibbler");
exports.kibbler = kibbler;
Object.assign(kibbler, {
    speed: 0.65,
    hitSize: 22,
    treadPullOffset: 0,
    crushDamage: 1.4,
    treadRects: [new Rect(14, -40, 35, 100)],
    treadFrames: 8,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    rotateSpeed: 2.5,
    health: 1700,
    armor: 10,
    itemCapacity: 0,
    constructor: () => new TankUnit.create(),
})
kibbler.weapons.add(
    Object.assign(new StatWeapon("zerg-kibbler-weapon", "reduceArmor", 3), {
        shootSound: Sounds.dullExplosion,
        layerOffset: 0.0001,
        reload: 90,
        shootY: 36 / 4,
        recoil: 3,
        rotate: true,
        rotateSpeed: 1.3,
        mirror: false,
        shootCone: 20,
        x: 0,
        y: 0,
        cooldownTime: 30,
        bullet: Object.assign(new ReduceArmorBulletType(4, 90, 3), {
            width: 10,
            height: 14,
            lifetime: 48,
            hitSize: 12,
            hitColor: Color.valueOf("d06b53"),
            backColor: Color.valueOf("d06b53"),
            trailColor: Color.valueOf("d06b53"),
            frontColor: Color.white,
            trailWidth: 2.1,
            trailLength: 7,

            pierceCap: 3,

            intervalBullets: 4,
            intervalRandomSpread: 0,
            intervalSpread: 10,
            bulletInterval: 8,
            intervalBullet: Object.assign(new BasicBulletType(), {
                lifetime: 15,
                damage: 18,
                width: 5,
                height: 5,
                speed: 3,
            })
        })
    })
)

const purge = new TankUnitType("purge");
exports.purge = purge;
Object.assign(purge, {
    squareShape: true,
    omniMovement: false,
    rotateMoveFirst: true,
    envDisabled: 0,
    speed: 0.5,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    treadRects: [new Rect(14, -24, 30, 80)],
    treadFrames: 8,
    hitSize: 29,
    rotateSpeed: 2,
    health: 9000,
    armor: 13,
    itemCapacity: 0,
    crushDamage: 2,
    constructor: () => new TankUnit.create(),
})
purge.weapons.add(
    Object.assign(new Weapon("zerg-purge-weapon"), {
        reload: 180,
        cooldownTime: 120,
        mirror: false,
        x: 0,
        y: 0,
        rotateSpeed: 1.4,
        rotate: true,
        shootY: 0.25,
        shake: 6,
        shootSound: Sounds.railgun,

        ejectEffect: Fx.none,
        recoil: 5.5,

        bullet: Object.assign(new RailBulletType(), {
            shootEffect: Fx.railShoot,
            length: 401,
            pointEffectSpace: 60,
            pierceEffect: Fx.railHit,
            pointEffect: Fx.railTrail,
            hitEffect: Fx.massiveExplosion,
            smokeEffect: Fx.shootBig2,
            damage: 750,
            pierceDamageFactor: 0.8,
            buildingDamageMultiplier: 1.1,
            recoil: 0.8,
        })
    })
)

const fearless = new TankUnitType("fearless")
exports.fearless = fearless;
Object.assign(fearless, {
    squareShape: true,
    omniMovement: false,
    rotateMoveFirst: true,
    envDisabled: 0,
    speed: 0.42,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    hitSize: 38,
    rotateSpeed: 1.5,
    health: 26000,
    armor: 17,
    itemCapacity: 0,
    crushDamage: 4,
    constructor: () => new TankUnit.create(),
})
fearless.weapons.add(
    Object.assign(new StatWeapon("zerg-fearless-weapon", "reduceArmor", 5), {
        layerOffset: 0.0001,
        reload: 45 / 2,
        shootY: 6,
        recoil: 4,
        rotate: true,
        rotateSpeed: 2.7,
        mirror: false,
        x: 0,
        y: -0.75,
        heatColor: Color.red,
        cooldownTime: 50,
        shootSound: Sounds.shotgun,
        shoot: new ShootAlternate(8),
        bullet: Object.assign(new ReduceArmorBulletType(8, 280, 5), {
            pierce: true,
            pierceBuilding: true,
            pierceCap: 4,
            smokeEffect: Fx.shootBigSmoke,
            shootEffect: Fx.shootBigColor,
            width: 16,
            height: 22.5,
            shrinkX: 0,
            shrinkY: 0,
            lifetime: 30,
            hitSize: 12,
            hitColor: Color.valueOf("d06b53"),
            backColor: Color.valueOf("d06b53"),
            trailColor: Color.valueOf("d06b53"),
            frontColor: Color.valueOf("ffffff"),
            trailWidth: 2.2,
            trailLength: 12,
            despawnEffect: Fx.hitBulletColor,
            hitEffect: Fx.hitBulletColor,
            fragBullets: 7,
            fragRandomSpread: 0,
            fragSpread: 45 / 6,
            fragVelocityMin: 2,
            fragVelocityMax: 2,
            fragLifeMin: 1,
            fragLifeMax: 1,
            fragBullet: Object.assign(new BasicBulletType(4, 10), {
                lifetime: 15,
                width: 6,
                height: 6,
                pierce: true,
                pierceBuilding: true,
                pierceCap: 2,
                frontColor: Color.valueOf("ffffff"),
                backColor: Color.valueOf("d06b53"),
            })
        })
    })
)

const gale = new TankUnitType("gale");
exports.gale = gale;
Object.assign(gale, {
    speed: 9 * 8 / 60,
    treadRects: [new Rect(6, -24, 16, 45)],
    treadFrames: 8,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    hitSize: 12,
    treadPullOffset: 3,
    rotateSpeed: 5,
    health: 300,
    armor: 2,
    itemCapacity: 0,
    targetAir: true,
    constructor: () => new TankUnit.create()
})
gale.weapons.add(
    Object.assign(new Weapon("zerg-gale-weapon"), {
        x: 0,
        y: -1.5,
        rotate: true,
        rotateSpeed: 5,
        mirror: false,
        layerOffset: 0.0001,
        shootY: 3,
        recoil: 1,
        reload: 25,
        shootSound: Sounds.missile,
        shoot: new ShootAlternate(3),
        bullet: Object.assign(new MissileBulletType(4, 10), {
            hitEffect: Fx.blastExplosion,
            lifetime: 64,
            width: 8,
            splashDamageRadius: 24,
            splashDamage: 40,
            trailChance: 0.1,
            hitColor: Color.valueOf("5c69cc"),
            backColor: Color.valueOf("5c69cc"),
            trailColor: Color.valueOf("5c69cc"),
            frontColor: Color.white,
        })
    })
)

const hurricane = new TankUnitType("hurricane")
exports.hurricane = hurricane;
Object.assign(hurricane, {
    speed: 8 * 8 / 60,
    treadRects: [new Rect(7, -32, 26, 72)],
    treadFrames: 8,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    hitSize: 18,
    treadPullOffset: 3,
    rotateSpeed: 3,
    health: 650,
    armor: 3,
    itemCapacity: 0,
    targetAir: true,
    constructor: () => new TankUnit.create()
})
hurricane.weapons.add(
    Object.assign(new Weapon("zerg-hurricane-weapon"), {
        x: 0,
        y: -1.75,
        rotate: true,
        rotateSpeed: 5,
        mirror: false,
        layerOffset: 0.0001,
        shootY: 3,
        recoil: 1,
        reload: 10,
        shootSound: Sounds.missile,
        shoot: Object.assign(new ShootAlternate(14 / 4), {
            barrels: 3
        }),
        bullet: Object.assign(new MissileBulletType(4, 10), {
            hitEffect: Fx.blastExplosion,
            lifetime: 68,
            width: 8,
            splashDamageRadius: 24,
            splashDamage: 40,
            trailChance: 0.1,
            hitColor: Color.valueOf("5c69cc"),
            backColor: Color.valueOf("5c69cc"),
            trailColor: Color.valueOf("5c69cc"),
            frontColor: Color.white,
        })
    })
)

const tornado = new TankUnitType("tornado")
exports.tornado = tornado;
Object.assign(tornado, {
    speed: 7.5 * 8 / 60,
    treadRects: [new Rect(-48, -48, 35, 104)],
    treadFrames: 8,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    hitSize: 22,
    treadPullOffset: 0,
    crushDamage: 0.2,
    rotateSpeed: 3,
    health: 1320,
    armor: 5,
    itemCapacity: 0,
    targetAir: true,
    constructor: () => new TankUnit.create()
})
tornado.weapons.add(
    Object.assign(new Weapon("zerg-tornado-weapon"), {
        layerOffset: 0.0001,
        reload: 120,
        shootY: 4,
        recoil: 3,
        rotate: true,
        rotateSpeed: 3.7,
        mirror: false,
        x: 0,
        y: 2,
        shoot: new ShootEncircle(1, 1.2),
        heatColor: Color.valueOf("7e8ae6"),
        shootSound: Sounds.shockBlast,
        bullet: Object.assign(new BasicBulletType(4.5, 100), {
            shootEffect: new MultiEffect(
                Fx.shootTitan,
                Object.assign(new WaveEffect(), {
                    colorTo: Color.valueOf("5c69cc"),
                    sizeTo: 26,
                    lifetime: 14,
                    strokeFrom: 4,
                })
            ),
            smokeEffect: Fx.shootSmokeTitan,
            hitColor: Color.valueOf("5c69cc"),
            despawnSound: Sounds.spark,

            sprite: "large-orb",
            trailEffect: Fx.missileTrail,
            trailInterval: 3,
            trailParam: 4,
            lifetime: 50,
            pierceBuilding: true,
            pierceCap: 1,
            width: 15,
            height: 15,
            backColor: Color.valueOf("5c69cc"),
            frontColor: Color.valueOf("5c69cc"),
            shrinkX: 0,
            shrinkY: 0,
            trailColor: Color.valueOf("5c69cc"),
            trailLength: 12,
            trailWidth: 2.2,
            despawnEffect: Object.assign(new ExplosionEffect(), {
                waveColor: Color.valueOf("5c69cc"),
                smokeColor: Color.gray,
                sparkColor: Color.valueOf("5c69cc"),
                waveStroke: 4,
                waveRad: 40,
            }),
            hitEffect: Object.assign(new ExplosionEffect(), {
                waveColor: Color.valueOf("5c69cc"),
                smokeColor: Color.gray,
                sparkColor: Color.valueOf("5c69cc"),
                waveStroke: 4,
                waveRad: 40,
            }),
            intervalBullet: Object.assign(new LightningBulletType(), {
                damage: 17,
                collidesAir: false,
                ammoMultiplier: 1,
                lightningColor: Color.valueOf("5c69cc"),
                lightningLength: 3,
                lightningLengthRand: 6,

                lightningType: Object.assign(new BulletType(0.0001, 0), {
                    lifetime: Fx.lightning.lifetime,
                    hitEffect: Fx.hitLancer,
                    despawnEffect: Fx.none,
                    status: StatusEffects.shocked,
                    statusDuration: 10,
                    hittable: false,
                    lightColor: Color.white,
                    buildingDamageMultiplier: 0.25,
                })
            }),
            bulletInterval: 5,
            intervalBullets: 2,
            lightningColor: Color.valueOf("5c69cc"),
            lightningDamage: 19,
            lightning: 12,
            lightningLength: 2,
            lightningLengthRand: 8,
        })
    })
)

const meteorite = new UnitType("meteorite");
exports.meteorite = meteorite;
Object.assign(meteorite, {
    speed: 0.24,
    treadRects: [new Rect(-72, -92, 42, 168)],
    treadFrames: 8,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    hitSize: 28,
    treadPullOffset: 0,
    crushDamage: 0.5,
    rotateSpeed: 0.75,
    omniMovement: false,
    rotateMoveFirst: true,
    health: 6000,
    armor: 6,
    itemCapacity: 0,
    targetAir: false,
    constructor: () => new TankUnit.create()
})
meteorite.weapons.add(
    Object.assign(new Weapon("zerg-meteorite-weapon"), {
        reload: 4.5 * 60,
        shootY: 72 / 4,
        recoil: 0,
        rotate: true,
        rotateSpeed: 1.5,
        mirror: false,
        x: 0,
        y: 0,
        inaccuracy: 3,
        velocityRnd: 0.05,
        heatColor: Color.valueOf("f9350f"),
        cooldownTime: 4 * 60,
        shootSound: Sounds.mediumCannon,

        bullet: Object.assign(new ArtilleryBulletType(2.5, 160, "shell"), {
            hitEffect: new MultiEffect(
                Fx.titanExplosion,
                Fx.titanSmoke
            ),
            despawnEffect: Fx.none,
            knockback: 2,
            lifetime: 184,
            height: 21,
            width: 19,
            splashDamageRadius: 72,
            splashDamage: 650,
            scaledSplashDamage: true,
            pierceArmor: true,
            backColor: Color.valueOf("5c69cccd"),
            hitColor: Color.valueOf("5c69cccd"),
            trailColor: Color.valueOf("5c69cccd"),
            frontColor: Color.white,
            ammoMultiplier: 1,
            hitSound: Sounds.titanExplosion,

            status: StatusEffects.blasted,

            trailLength: 32,
            trailWidth: 3.35,
            trailSinScl: 2.5,
            trailSinMag: 0.5,
            trailEffect: Fx.none,
            despawnShake: 7,

            shootEffect: Fx.shootTitan,
            smokeEffect: Fx.shootSmokeTitan,

            trailInterp: v => Math.max(Mathf.slope(v), 0.8),
            shrinkX: 0.2,
            shrinkY: 0.1,
        })
    }))

const alter = new UnitType("alter");
exports.alter = alter;
Object.assign(alter, {
    targetPriority: -1.5,
    outlineColor: Color.valueOf("464a59"),
    outlineRadius: 3,
    envDisabled: Env.none,
    healFlash: true,
    squareShape: true,
    omniMovement: false,
    rotateMoveFirst: true,
    speed: 0.9,
    hitSize: 11,
    treadRects: [new Rect(4, -20, 21, 80)],
    treadFrames: 8,
    treadPullOffset: 3,
    rotateSpeed: 3.3,
    health: 420,
    armor: 1,
    itemCapacity: 0,
    constructor: () => new TankUnit.create()
})
alter.weapons.add(
    Object.assign(new Weapon("zerg-alter-weapon"), {
        layerOffset: 0.0001,
        reload: 60,
        shootY: 2,
        recoil: 0,
        rotate: true,
        rotateSpeed: 5.7,
        mirror: false,
        x: 0,
        y: 0,
        heatColor: Color.valueOf("f9350f"),
        cooldownTime: 90,
        shootSound: Sounds.lasershoot,

        bullet: extend(BasicBulletType, {
            hitEntity(b, entity, health) {
                if (entity instanceof Unit) {
                    if (entity.health <= 100) {
                        entity.remove();

                        entity.type.spawn(b.team, entity.x, entity.y)
                    }
                }

                this.super$hitEntity(b, entity, health);
            },
            hitTile(b, build, x, y, initialHealth, direct) {
                if (build.team != b.team && build.health <= 100) {
                    build.remove()

                    build.tile.setBlock(build.block, b.team, build.rotation)
                }

                this.super$hitTile(b, build, x, y, initialHealth, direct);
            },
            speed: 3.5,
            damage: 50,
            sprite: "zerg-wave",
            width: 10,
            height: 13,
            lifetime: 52,
            despawnEffect: Ef.interfere,
            hitEffect: Ef.interfere,
            backColor: Color.valueOf("bf92f9"),
            frontColor: Color.valueOf("ffffff"),
            hittable: false,
            pierceArmor: true,
            homingRange: 60,
            homingPower: 0.1,
        })
    })
)