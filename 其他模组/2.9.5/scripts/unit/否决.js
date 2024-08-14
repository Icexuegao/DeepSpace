const lib = require("base/coflib");
const units = require("unit/units");
const status = require("base/status");
const ability = require("base/ability");
const bullet = require("base/bullet");
const weapons = require("base/weapon");
const FX = require("base/Effect/fightFx");
let by = Pal.bulletYellow, byb = Pal.bulletYellowBack;

let vote = units.newUnit("否决", UnitEntity, {
    flying: true,
    lowAltitude: true,
    health: 173000,
    armor: 173,
    hitSize: 87,
    speed: 0.8,
    accel: 0.04,
    drag: 0.05,
    rotateSpeed: 0.6,
    engineSize: 8,
    engineOffset: 45,
    outlineColor: lib.C1F1F1F
})

vote.setEnginesMirror(new UnitType.UnitEngine(12.25, -64.25, 6, -90));

vote.abilities.addAll(ShieldRegenFieldAbility(450, 1800, 160, 240));

/*vote.weapons.add(Object.assign(new Weapon("否决主炮"), {
	x: 0,
	recoil: 0,
	reload: 480,
	mirror: false,
	chargeSound: Sounds.lasercharge,
	shootSound: Sounds.laserblast,
	shoot: Object.assign(new ShootPattern(), {
		firstShotDelay: 120
	}),
	bullet: Object.assign(bullet.HomingMainBulletType(13, 550, 40, 6, true, [33, 48, 52]), {
		width: 16,
		height: 32,
		hitColor: byb,
		trailWidth: 4,
		trailLength: 12,
		trailColor: byb,
		chargeEffect: new Effect(120, e => {
			let rand = new Rand();
			rand.setSeed(e.id);
			Angles.randLenVectors(e.id, 24, rand.random(90, 200) * Mathf.curve(e.fout(), 0.25, 1), (x, y) => {
				Draw.color(byb);
				let rad = rand.random(9, 18);
				Fill.circle(e.x + x, e.y + y, e.fin() * rad);
				Draw.color(Color.white);
				Fill.circle(e.x + x, e.y + y, e.fin() * rad / 2);
				Drawf.light(e.x + x, e.y + y, e.fin() * rad * 1.5, byb, 0.7);
			});
		}),
		hitEffect: Fx.titanExplosion.wrap(byb),
		despawnEffect: Fx.titanExplosion.wrap(byb),
		fragBullets: 3,
		fragBullet: Object.assign(new BasicBulletType(5, 50), {
			lifetime: 88,
			drag: -0.01,
			hitColor: byb,
			trailWidth: 2,
			trailLength: 8,
			trailColor: byb,
			hitEffect: Object.assign(new ExplosionEffect(), {
				lifetime: 50,
				waveStroke: 5,
				waveLife: 8,
				waveColor: by,
				sparkColor: byb,
				smokeColor: byb,
				waveRad: 40,
				smokeSize: 4,
				smokes: 7,
				smokeSizeBase: 0,
				sparks: 10,
				sparkRad: 40,
				sparkLen: 6,
				sparkStroke: 2
			}),
			despawnEffect: Fx.hitSquaresColor
		})
	})
}))*/

for (let i of Mathf.signs) {
    vote.weapons.add(Object.assign(new Weapon("否决副炮"), {
        x: 22 * i,
        y: 60,
        shake: 4,
        recoil: 0,
        shootY: 0,
        reload: 85,
        shootCone: 5,
        inaccuracy: 10,
        shoot: Object.assign(new ShootHelix(), {
            shots: 3,
            shotDelay: 10,
            mag: 3.6,
            scl: 18
        }),
        shootSound: Sounds.plasmadrop,
        bullet: Object.assign(bullet.HomingMainBulletType(2, 450, 96, 6, false, [33 * i, 48 * i, 52 * i]), {
            width: 12,
            height: 18,
            drag: -0.02,
            hitColor: byb,
            trailWidth: 3,
            trailLength: 12,
            trailColor: byb,
            trailChance: 1,
            trailEffect: Object.assign(new ParticleEffect(), {
                particles: 2,
                lifetime: 20,
                length: 10,
                baseLength: 16,
                sizeFrom: 4,
                sizeTo: 0,
                colorFrom: byb,
                colorTo: lib.Color("F9C27A80"),
                cone: 360
            }),
            weaveMag: 2,
            weaveScale: 6,
            homingDelay: 33,
            homingRange: 108,
            homingPower: 0.08,
            shootEffect: Fx.shootTitan,
            smokeEffect: Fx.shootSmokeTitan,
            status: status.破甲,
            statusDuration: 150,
            splashDamage: 325,
            splashDamageRadius: 40,
            hitShake: 3,
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
                    colorFrom: byb,
                    colorTo: lib.Color("F9C27A00"),
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
                    colorFrom: byb,
                    colorTo: by,
                    cone: 360
                }),
                Object.assign(new WaveEffect(), {
                    lifetime: 25,
                    sizeFrom: 0,
                    sizeTo: 66,
                    strokeFrom: 3,
                    strokeTo: 0,
                    colorFrom: byb,
                    colorTo: by
                })
            ),
            fragBullets: 3,
            fragBullet: Object.assign(new BasicBulletType(5, 225, "curse-of-flesh-arrows"), {
                lifetime: 96,
                width: 8,
                height: 18,
                shrinkY: 0,
                drag: -0.01,
                hitColor: byb,
                pierce: true,
                weaveMag: 2,
                weaveScale: 6,
                trailWidth: 2,
                trailLength: 8,
                trailColor: byb,
                status: status.损毁,
                statusDuration: 60,
                hitEffect: Fx.hitSquaresColor,
                despawnEffect: Fx.hitSquaresColor
            })
        })
    }))
}


let trace = Object.assign(weapons.ArmorWeapon("否决炮", Object.assign(bullet.ArmorBrokenBulletType(17, 657, 35, 1.2, 1), {
    width: 6,
    height: 35,
    hitColor: byb,
    trailColor: byb,
    trailLength: 9,
    trailWidth: 1.5,
    trailInterval: 3,
    trailRotation: true,
    trailEffect: FX.shockShoot,
    status: status.破甲,
    statusDuration: 120,
    pierceDamageFactor: 0.4,
    hitEffect: Object.assign(new ExplosionEffect(), {
        lifetime: 20,
        waveStroke: 2,
        waveColor: byb,
        sparkColor: byb,
        waveRad: 12,
        smokeSize: 0,
        smokeSizeBase: 0,
        sparks: 10,
        sparkRad: 35,
        sparkLen: 4,
        sparkStroke: 1.5,
    }),
    despawnEffect: Fx.hitSquaresColor,
    hitSound: Sounds.plasmaboom,
    smokeEffect: Fx.shootSmokeSmite,
    shootEffect: Fx.shootSmokeSquareBig
})), {
    x: 26.5,
    y: 6,
    shake: 3,
    recoil: 2,
    shootY: 4,
    reload: 65,
    rotate: true,
    shootCone: 5,
    recoilTime: 85,
    rotateSpeed: 2,
    rotationLimit: 150,
    cooldownTime: 85,
    shootSound: Sounds.cannon
});

function lancer(wx, wy) {
    return Object.assign(new Weapon(lib.modName + "-致胜近防"), {
        x: wx,
        y: wy,
        reload: 60,
        recoil: 2,
        shake: 3,
        rotate: true,
        shootCone: 15,
        rotateSpeed: 3,
        alternate: false,
        cooldownTime: 60,
        shootSound: Sounds.laser,
        bullet: Object.assign(new LaserBulletType(550), {
            length: 320,
            shootEffect: new Effect(24, e => {
                Draw.color(Pal.redLight, e.color, e.fin());
                for (let i of Mathf.signs) Drawf.tri(e.x, e.y, 9 * e.fout(), 72, e.rotation + 90 * i);
                Drawf.light(e.x, e.y, 180, e.color, 0.9 * e.fout());
            }),
            colors: [lib.FEB380, lib.FF8663, lib.FF5845],
            hitColor: lib.FF8663,
            ammoMultiplier: 1,
            status: status.熔融,
            statusDuration: 30,
            sideAngle: 22.5
        })
    })
}

let engine = Object.assign(new Weapon("否决引擎"), {
    x: 0,
    y: -44,
    reload: 300,
    mirror: false,
    shootY: 0,
    baseRotation: 180,
    useAmmo: false,
    alwaysShooting: true,
    alwaysContinuous: true,
    shootSound: Sounds.none,
    bullet: Object.assign(new ContinuousFlameBulletType(75), {
        colors: [lib.Color("FF58458C"), lib.Color("FF5845B2"), lib.Color("FF8663CC"), lib.Color("FF8663"), lib.Color("FEB380CC")],
        lifetime: 30,
        width: 4,
        length: 45,
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
                colorFrom: lib.FF5845,
                colorTo: lib.FEB380
            }),
            Fx.hitFlameBeam
        )
    })
})

vote.weapons.addAll(trace, lancer(24.25, 31), lancer(19.25, -32.5)/*, engine*/);