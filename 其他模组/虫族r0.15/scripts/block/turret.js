const item = require('zerg/item');
const liquid = require('zerg/liquid');
const status = require('zerg/status');
const {ReduceArmorBulletType, RicochetBulletType, SniperBulletType} = require('zerg/base/bulletType');
const {SniperRailEffect} = require("zerg/Effect");

function AddCoolant(turret, amount) {
    return turret.coolant = turret.consumeCoolant(amount);
}

const guard = new ItemTurret("guard");
exports.guard = guard;
Object.assign(guard, {
    shootY: 3,
    reload: 40,
    range: 120,
    shootCone: 15,
    ammoUseEffect: Fx.casing1,
    health: 360,
    inaccuracy: 1,
    rotateSpeed: 10,
    shoot: Object.assign(new ShootPattern(), {
        shots: 3,
        shotDelay: 3,
    }),
    alwaysUnlocked: true,
    buildVisibility: BuildVisibility.shown,
    category: Category.turret,
    requirements: ItemStack.with(
        item.nickel, 55,
    )
})
AddCoolant(guard, 0.1)
guard.ammo(
    item.nickel, Object.assign(new RicochetBulletType(3, 11), {
        width: 2,
        height: 9,
        lifetime: 60,
        ammoMultiplier: 3,
    }),
    Items.graphite, Object.assign(new RicochetBulletType(3.5, 17), {
        width: 2,
        height: 12,
        reloadMultiplier: 0.6,
        ammoMultiplier: 6,
        lifetime: 60,
    })
)

const obstruct = new ItemTurret("obstruct");
exports.obstruct = obstruct;
Object.assign(obstruct, {
    shootY: 28 / 4,
    reload: 6,
    range: 220,
    targetGround: false,
    shootCone: 45,
    ammoUseEffect: Fx.casing1,
    health: 750,
    size: 2,
    rotateSpeed: 15,
    inaccuracy: 5,
    shootSound: Sounds.shootSnap,
    researchCostMultiplier: 0.25,
    shoot: new ShootAlternate(6),
    buildVisibility: BuildVisibility.shown,
    category: Category.turret,
    requirements: ItemStack.with(
        Items.graphite, 20,
        item.nickel, 55,
    ),
    velocityRnd: 0.2
})
AddCoolant(obstruct, 0.1)
obstruct.ammo(
    Items.graphite, Object.assign(new BasicBulletType(5, 13), {
        width: 5,
        height: 12,
        ammoMultiplier: 8,
        lifetime: 45,
        hitEffect: Fx.flakExplosionBig,
        collidesGround: false,
        pierceCap: 1,
        fragBullet: Object.assign(new BasicBulletType(3, 3), {
            width: 1,
            height: 1,
            lifetime: 120,
            pierceCap: 1,
            drag: 0.3,
            collidesGround: false,
            hitEffect: Fx.none,
            despawnEffect: Fx.none,
            lightOpacity: 0,
        })
    }),
    item.crystal, Object.assign(new BasicBulletType(5, 23), {
        width: 5,
        height: 12,
        ammoMultiplier: 12,
        lifetime: 45,
        hitEffect: Fx.flakExplosionBig,
        collidesGround: false,
        pierceCap: 1,
        fragBullets: 9,
        fragVelocityMin: 1,
        fragVelocityMax: 7,
        fragBullet: Object.assign(new BasicBulletType(3, 5), {
            width: 1,
            height: 1,
            lifetime: 150,
            pierceCap: 1,
            drag: 0.3,
            collidesGround: false,
            hitEffect: Fx.none,
            despawnEffect: Fx.none,
            lightOpacity: 0,
        })
    })
)

const nexus = new ItemTurret("nexus");
exports.nexus = nexus;
Object.assign(nexus, {
    reload: 20,
    size: 2,
    range: 8 * 24,
    shootCone: 8,
    health: 1040,
    inaccuracy: 0,
    rotateSpeed: 4.7,
    ammoUseEffect: Fx.casing1,
    shootSound: Sounds.shootBig,
    shoot: Object.assign(new ShootPattern(), {
        shots: 5,
        shotDelay: 2.5,
    }),
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.graphite, 80,
        item.nickel, 100,
        item.manganese, 40,
    ),
})
AddCoolant(nexus, 0.2);
nexus.ammo(
    item.nickel, Object.assign(new RicochetBulletType(3.5, 11), {
        width: 2,
        height: 9,
        ammoMultiplier: 3,
        lifetime: 60,
    }),
    Items.graphite, Object.assign(new RicochetBulletType(4, 17), {
        width: 2,
        height: 12,
        reloadMultiplier: 0.75,
        ammoMultiplier: 6,
        lifetime: 50,
    }),
    Items.silicon, Object.assign(new BasicBulletType(4, 17), {
        width: 2,
        height: 12,
        reloadMultiplier: 1.5,
        ammoMultiplier: 6,
        lifetime: 50,
        homingRange: 32,
        homingPower: 0.04,
    }),
    item.manganese, Object.assign(new RicochetBulletType(4, 21), {
        width: 2,
        height: 12,
        shootEffect: Fx.shootBig,
        smokeEffect: Fx.shootBigSmoke,
        ammoMultiplier: 4,
        lifetime: 60,
        fragBullets: 3,
        fragBullet: Object.assign(new RicochetBulletType(3, 5), {
            width: 5,
            height: 12,
            shrinkY: 1,
            lifetime: 5,
            despawnEffect: Fx.none,
        })
    }),
    item.chromium, Object.assign(new BasicBulletType(4, 23), {
        width: 2,
        height: 9,
        ammoMultiplier: 3,
        lifetime: 60,
        pierceCap: 3,
    }),
    item.sulfone, Object.assign(new RicochetBulletType(3, 19), {
        width: 10,
        height: 12,
        frontColor: Color.valueOf("ede892"),
        backColor: Color.valueOf("d9c668"),
        status: StatusEffects.burning,
        hitEffect: new MultiEffect(
            Fx.hitBulletSmall,
            Fx.fireHit
        ),
        ammoMultiplier: 8,
        reloadMultiplier: 1.5,
        splashDamage: 16,
        splashDamageRadius: 32,
        makeFire: true,
        lifetime: 64,
    }),
)

const bomb = new ItemTurret("bomb");
exports.bomb = bomb;
Object.assign(bomb, {
    reload: 90,
    size: 2,
    range: 8 * 32,
    shootCone: 15,
    health: 960,
    inaccuracy: 1,
    rotateSpeed: 3.5,
    ammoUseEffect: Fx.casing1,
    targetAir: false,
    shootSound: Sounds.bang,
    shoot: Object.assign(new ShootPattern(), {
        shots: 4,
        shotDelay: 4,
    }),
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.graphite, 80,
        item.nickel, 100,
        item.manganese, 40,
    ),
})
AddCoolant(bomb, 0.2);
bomb.ammo(
    Items.graphite, Object.assign(new ArtilleryBulletType(3, 20), {
        knockback: 0.8,
        lifetime: 86,
        width: 11,
        height: 11,
        collidesTiles: false,
        splashDamageRadius: 24,
        splashDamage: 33,
    }),
    Items.silicon, Object.assign(new ArtilleryBulletType(3, 20), {
        knockback: 0.8,
        lifetime: 86,
        width: 11,
        height: 11,
        collidesTiles: false,
        splashDamageRadius: 24,
        splashDamage: 33,
        reloadMultiplier: 1.5,
        homingRange: 32,
        homingPower: 0.04,
    }),
    item.crystal, Object.assign(new ArtilleryBulletType(3, 20), {
        knockback: 0.8,
        lifetime: 86,
        width: 11,
        height: 11,
        collidesTiles: false,
        splashDamageRadius: 24,
        splashDamage: 37,
        backColor: Color.valueOf("7e8ae6"),
        frontColor: Color.white,
        ammoMultiplier: 4,
        fragBullets: 3,
        fragBullet: Object.assign(new BasicBulletType(3, 5), {
            width: 5,
            height: 12,
            shrinkY: 1,
            lifetime: 20,
            backColor: Pal.gray,
            frontColor: Color.white,
            despawnEffect: Fx.none,
        })
    }),
    item.energic, Object.assign(new ArtilleryBulletType(3, 20), {
        knockback: 0.8,
        lifetime: 86,
        width: 11,
        height: 11,
        collidesTiles: false,
        splashDamageRadius: 24,
        splashDamage: 42,
        backColor: Color.valueOf("fa7f7f"),
        frontColor: Color.white,
        ammoMultiplier: 4,
        reloadMultiplier: 1.25,
        fragBullets: 3,
        fragBullet: Object.assign(new LightningBulletType(), {
            damage: 9,
            collidesAir: false,
            ammoMultiplier: 1,
            lightningColor: Color.valueOf("fa7f7f"),
            lightningLength: 2,
            lightningLengthRand: 6,
        })
    }),
    item.sulfone, Object.assign(new ArtilleryBulletType(3.5, 25), {
        hitEffect: Fx.blastExplosion,
        knockback: 0.8,
        lifetime: 80,
        width: 13,
        height: 13,
        collidesTiles: false,
        splashDamageRadius: 8 * 3.25,
        splashDamage: 55,
        status: StatusEffects.burning,
        statusDuration: 60 * 15,
        frontColor: Color.valueOf("ede892"),
        backColor: Color.valueOf("d9c668"),
        trailColor: Color.valueOf("ede892"),
        makeFire: true,
        trailEffect: Fx.incendTrail,
        ammoMultiplier: 4,
        reloadMultiplier: 1.25,
    }),
)

const b = extend(BulletType, {
    speed: 0.01,
    damage: 0,
    collidesGround: false,
    collidesAir: false,
    collides: false,
    absorbable: false,
    hittable: false,
    lifetime: 60,
    hitEffect: Fx.none,
    despawnEffect: Fx.none,
    lightOpacity: 0,
})

const soak = new LiquidTurret("soak");
exports.soak = soak
Object.assign(soak, {
    size: 2,
    recoil: 0,
    reload: 3,
    inaccuracy: 5,
    shootCone: 50,
    liquidCapacity: 20,
    shootEffect: Fx.shootLiquid,
    range: 120,
    unitSort: UnitSorts.strongest,
    scaledHealth: 250,
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.silicon, 15,
        item.nickel, 100,
        item.crystal, 30,
    ),
})
soak.ammo(
    Liquids.water, Object.assign(new LiquidBulletType(Liquids.water), {
        knockback: 0.7,
        drag: 0.01,
        lifetime: 37,
        layer: Layer.bullet - 2,
    }),
    Liquids.oil, Object.assign(new LiquidBulletType(Liquids.oil), {
        drag: 0.01,
        lifetime: 37,
        layer: Layer.bullet - 2,
    }),
    Liquids.slag, Object.assign(new LiquidBulletType(Liquids.slag), {
        speed: 5.5,
        knockback: 0.7,
        damage: 5,
        drag: 0.01,
        lifetime: 60,
        layer: Layer.bullet - 2,
    }),
    liquid.acid, Object.assign(new LiquidBulletType(liquid.acid), {
        damage: 5,
        drag: 0.01,
        lifetime: 37,
        layer: Layer.bullet - 2,
    }),
    liquid.dissolvant, extend(LiquidBulletType, liquid.dissolvant, {
        damage: 3,
        drag: 0.01,
        lifetime: 37,
        layer: Layer.bullet - 2,
        update(b) {
            this.super$update(b);

            let tile = Vars.world.tileWorld(b.x, b.y);
            if (tile != null) {
                let other = Puddles.get(tile);
                if (other != null && other.liquid == Liquids.neoplasm) {
                    if (other.amount > 20) {
                        other.amount -= 20
                        b.absorb()
                    } else {
                        other.remove()
                    }
                }
            }
        }
    })
)
soak.buildType = prov(() => extend(LiquidTurret.LiquidTurretBuild, soak, {
    findTarget() {
        this.super$findTarget()

        if (this.liquids.current() == liquid.dissolvant) {
            this.tile.circle((this.block.range - 1) / 8, cons(tile => {
                let other = Puddles.get(tile);
                if (other != null && other.liquid == Liquids.neoplasm && other.amount > 0.01 && this.target == null) {
                    this.target = b.create(this, Team.derelict, tile.worldx(), tile.worldy(), this.rotation - 180)
                }
            }))
        }
    }
}))

const spiral = new PowerTurret("spiral");
exports.spiral = spiral;
Object.assign(spiral, {
    reload: 60,
    shootCone: 12,
    size: 2,
    range: 20 * 8,
    health: 1050,
    rotateSpeed: 6,
    recoil: 2,
    shootY: 7,
    targetAir: false,
    shootEffect: Fx.lightningShoot,
    shootSound: Sounds.blaster,
    heatColor: Color.red,
    shoot: new ShootMulti(
        new ShootPattern(),
        Object.assign(new ShootHelix(), {
            mag: 3,
            scl: 4,
        }),
        new ShootPattern(),
    ),
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.silicon, 30,
        item.nickel, 120,
        item.manganese, 75,
    ),
    shootType: Object.assign(new BasicBulletType(4, 30), {
        width: 7,
        height: 12,
        lifetime: 48,
        shootEffect: Fx.sparkShoot,
        smokeEffect: Fx.shootBigSmoke,
        hitColor: Pal.lancerLaser,
        backColor: Pal.lancerLaser,
        trailColor: Pal.lancerLaser,
        frontColor: Color.white,
        trailWidth: 1.5,
        trailLength: 5,
        hitEffect: Fx.hitBulletColor,
        despawnEffect: Fx.hitBulletColor,
        lightningColor: Pal.lancerLaser,
        lightningDamage: 9,
        lightning: 9,
        lightningLength: 1,
        lightningLengthRand: 6,
    })
})
spiral.consumePower(3.7)
AddCoolant(spiral, 0.15);

const lacerate = new PowerTurret("lacerate");
exports.lacerate = lacerate;
Object.assign(lacerate, {
    range: 120,
    reload: 60,
    shootCone: 30,
    shootY: 2,
    size: 2,
    health: 1250,
    rotateSpeed: 4,
    recoil: 4,
    targetAir: false,
    moveWhileCharging: false,
    accurateDelay: false,
    shootEffect: Fx.lancerLaserShoot,
    shootSound: Sounds.shotgun,
    shoot: new ShootSpread(3, 15),
    heatColor: Color.red,
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.silicon, 60,
        item.nickel, 100,
        item.manganese, 30,
        item.crystal, 50,
    ),
    shootType: Object.assign(new ShrapnelBulletType(), {
        length: 130,
        damage: 110,
        width: 13,
    })
})
lacerate.consumePower(6.8);

/*const parallax = new TractorBeamTurret("parallax")
Object.assign(parallax,{
	category: Category.turret,
	buildVisibility: BuildVisibility.shown,
	size: 2,
	force: 0,
	scaledForce: 0,
	range: 240,
	damage: 0,
	targetGround: true,
	scaledHealth: 160,
	rotateSpeed: 10,
	hasPower: true
})
parallax.consumePower(3)
parallax.buildType = prov(() => extend(TractorBeamTurret.TractorBeamBuild, parallax, {
	updateTile(){
		this.super$updateTile();
		if(this.target != null){
			let length = Math.pow(Math.pow(this.target.x - this.x, 2) + Math.pow(this.target.y - this.y, 2), 0.5)
			Units.nearby(this.target.team, this.target.x, this.target.y, length * 0.258819, other => {
				other.impulse(
					Math.cos(Angles.angle(this.x, this.y, other.x, other.y)* Math.PI / 180) * 50,
					Math.sin(Angles.angle(this.x, this.y, other.x, other.y)* Math.PI / 180) * 50
				)
			})
		}
	}
}))*/
//requirements(Category.turret, with(Items.silicon, 120, Items.titanium, 90, Items.graphite, 30));

const lumen = new ItemTurret("lumen");
exports.lumen = lumen;
Object.assign(lumen, {
    health: 1040,
    size: 3,
    reload: 90,
    targetAir: false,
    range: 8 * 25,
    maxAmmo: 20,
    shootCone: 5,
    rotateSpeed: 4.5,
    recoil: 4,
    ammoPerShot: 5,
    shootSound: Sounds.mediumCannon,
    buildVisibility: BuildVisibility.shown,
    category: Category.turret,
    requirements: ItemStack.with(
        item.nickel, 150,
        item.crystal, 75,
        item.manganese, 100,
        item.biomassSteel, 150,
    )
})
AddCoolant(lumen, 0.15);
lumen.ammo(
    item.crystal, Object.assign(new ReduceArmorBulletType(6, 350, 7), {
        ammoMultiplier: 1,
        width: 7,
        height: 21,
        lifetime: 33.4,
        hitSize: 4,
        hitColor: Color.valueOf("7e8ae6"),
        backColor: Color.valueOf("7e8ae6"),
        trailColor: Color.valueOf("7e8ae6"),
        frontColor: Color.white,
        trailWidth: 2,
        trailLength: 5,

        hitEffect: Fx.flakExplosionBig,

        pierce: true,
        pierceBuilding: true,
        collidesAir: false,
        pierceCap: 20,

        knockback: 12,

        intervalBullets: 3,
        bulletInterval: 1,
        intervalBullet: Object.assign(new BasicBulletType(), {
            lifetime: 1,
            damage: 5,
            width: 3,
            height: 3,
            frontColor: Color.valueOf("7e8ae6"),
            backColor: Color.valueOf("7e8ae6"),
            hitColor: Color.valueOf("7e8ae6"),
        }),
        fragBullets: 13,
        fragBullet: Object.assign(new BasicBulletType(), {
            lifetime: 3,
            damage: 12,
            width: 3,
            height: 3,
            frontColor: Color.valueOf("7e8ae6"),
            backColor: Color.valueOf("7e8ae6"),
            hitColor: Color.valueOf("7e8ae6"),
        })
    }),
    item.energic, Object.assign(new ReduceArmorBulletType(6, 370, 7), {
        ammoMultiplier: 1,
        width: 7,
        height: 21,
        lifetime: 33.4,
        hitSize: 4,
        hitColor: Color.valueOf("fa7f7f"),
        backColor: Color.valueOf("fa7f7f"),
        trailColor: Color.valueOf("fa7f7f"),
        frontColor: Color.white,
        trailWidth: 2,
        trailLength: 5,

        hitEffect: Fx.flakExplosionBig,

        pierce: true,
        pierceBuilding: true,
        collidesAir: false,
        pierceCap: 15,

        reloadMultiplier: 1.25,

        knockback: 12,
        intervalBullets: 5,
        bulletInterval: 2,
        intervalRandomSpread: 20,
        intervalSpread: 0,
        intervalAngle: 180,
        intervalBullet: Object.assign(new LightningBulletType(), {
            damage: 15,
            collidesAir: false,
            ammoMultiplier: 1,
            lightningColor: Color.valueOf("fa7f7f"),
            lightningLength: 2,
            lightningLengthRand: 6,
        }),
        lightningColor: Color.valueOf("fa7f7f"),
        lightningDamage: 9,
        lightning: 7,
        lightningLength: 1,
        lightningLengthRand: 6,
    })
)

const deluge = new LiquidTurret("deluge");
exports.deluge = deluge;
Object.assign(deluge, {
    size: 3,
    recoil: 0,
    reload: 12,
    inaccuracy: 5,
    velocityRnd: 0.2,
    shootCone: 50,
    liquidCapacity: 50,
    shootEffect: Fx.shootLiquid,
    shoot: Object.assign(new ShootAlternate(), {
        shotDelay: 2,
        shots: 8,
        barrels: 4,
        spread: 3,
    }),
    range: 200,
    scaledHealth: 250,
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.silicon, 150,
        item.nickel, 350,
        item.manganese, 250,
        item.crystal, 75,
    ),
})
deluge.ammo(
    Liquids.water, Object.assign(new LiquidBulletType(Liquids.water), {
        speed: 5.5,
        knockback: 0.7,
        drag: 0.01,
        lifetime: 60,
        layer: Layer.bullet - 2,
    }),
    Liquids.slag, Object.assign(new LiquidBulletType(Liquids.slag), {
        speed: 5.5,
        knockback: 0.7,
        damage: 5,
        drag: 0.01,
        lifetime: 60,
        layer: Layer.bullet - 2,
    }),
    Liquids.oil, Object.assign(new LiquidBulletType(Liquids.oil), {
        speed: 5.5,
        drag: 0.01,
        lifetime: 60,
        layer: Layer.bullet - 2,
    }),
    liquid.acid, Object.assign(new LiquidBulletType(liquid.acid), {
        speed: 5.5,
        damage: 5,
        drag: 0.01,
        lifetime: 60,
        layer: Layer.bullet - 2,
    }),
    liquid.dissolvant, extend(LiquidBulletType, liquid.dissolvant, {
        speed: 5.5,
        damage: 3,
        drag: 0.01,
        lifetime: 60,
        layer: Layer.bullet - 2,
        update(b) {
            this.super$update(b);

            let tile = Vars.world.tileWorld(b.x, b.y);
            if (tile != null) {
                let other = Puddles.get(tile);
                if (other != null && other.liquid == Liquids.neoplasm) {
                    if (other.amount > 20) {
                        other.amount -= 20
                        b.absorb()
                    } else {
                        other.remove()
                    }
                }
            }
        }
    })
)
deluge.buildType = prov(() => extend(LiquidTurret.LiquidTurretBuild, deluge, {
    findTarget() {
        this.super$findTarget()

        if (this.liquids.current() == liquid.dissolvant) {
            this.tile.circle((this.block.range - 1) / 8, cons(tile => {
                let other = Puddles.get(tile);
                if (other != null && other.liquid == Liquids.neoplasm && other.amount > 0.01 && this.target == null) {
                    this.target = b.create(this, Team.derelict, tile.worldx(), tile.worldy(), this.rotation - 180)
                }
            }))
        }
    }
}))

const blowtorth = new ContinuousLiquidTurret("blowtorth");
exports.blowtorth = blowtorth;
Object.assign(blowtorth, {
    liquidConsumed: 18 / 60,
    targetInterval: 5,
    range: 140,
    shootY: 0,

    size: 3,

    loopSound: Sounds.torch,
    shootSound: Sounds.none,
    loopSoundVolume: 3,
    buildVisibility: BuildVisibility.shown,
    category: Category.turret,
    requirements: ItemStack.with(
        Items.silicon, 200,
        item.nickel, 400,
        item.manganese, 150,
        item.biomassSteel, 75,
    ),
})
blowtorth.ammo(
    Liquids.hydrogen, Object.assign(new ContinuousFlameBulletType(), {
        damage: 90,
        length: 145,
        pierceCap: 3,

        colors: [
            Color.valueOf("5fd4ff8d"),
            Color.valueOf("85d6f4b3"),
            Color.valueOf("a2dcf1cd"),
            Color.valueOf("c4e4f0ff"),
            Color.white
        ],

        status: StatusEffects.melting,
        statusDuration: 120,

        flareColor: Color.valueOf("5fd4ff8d"),
        lightColor: Color.valueOf("5fd4ff8d"),
        hitColor: Color.valueOf("5fd4ff8d"),
    }),
    liquid.acetylene, Object.assign(new ContinuousFlameBulletType(), {
        damage: 1600 / 12,
        length: 160,
        pierceCap: 3,
        rangeChange: 10,

        ammoMultiplier: 0.75,

        colors: [
            Color.valueOf("ca1ff58d"),
            Color.valueOf("d757f8b3"),
            Color.valueOf("e48ffacd"),
            Color.valueOf("f2c7fcff"),
            Color.white
        ],

        status: StatusEffects.melting,
        statusDuration: 120,

        flareColor: Color.valueOf("ca1ff58d"),
        lightColor: Color.valueOf("ca1ff58d"),
        hitColor: Color.valueOf("ca1ff58d"),
    })
)

const sange = new ItemTurret("sange");
exports.sange = sange;
Object.assign(sange, {
    reload: 4,
    range: 38 * 8,
    shootCone: 20,
    health: 3600,
    size: 4,
    rotateSpeed: 5,
    recoil: 0.5,
    recoilTime: 30,
    shake: 3,
    ammoPerShot: 3,
    shootSound: Sounds.shootBig,
    shoot: new ShootMulti(
        new ShootPattern(),
        new ShootPattern(),
        extend(ShootPattern, {
            shoot(totalShots, handler, barrelIncrementer) {
                handler.shoot(0, 0, Mathf.range(45 / 2), 0)
                handler.shoot(0, 0, Mathf.range(45 / 2), 0)
            }
        })
    ),
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.silicon, 250,
        item.nickel, 400,
        item.crystal, 300,
        item.manganese, 250,
    ),
})
sange.ammo(
    Items.graphite, Object.assign(new BasicBulletType(6, 38), {
        sprite: "missile-large",

        lifetime: 52,
        width: 12,
        height: 22,

        hitSize: 7,
        shootEffect: Fx.shootSmokeSquareBig,
        smokeEffect: Fx.shootSmokeDisperse,
        ammoMultiplier: 4,
        reloadMultiplier: 1.2,
        knockback: 0.3,
        frontColor: Color.white,
        trailWidth: 3,
        trailLength: 12,
        hitEffect: Fx.hitBulletColor,
        despawnEffect: Fx.hitBulletColor,

        trailEffect: Fx.colorSpark,
        trailRotation: true,
        trailInterval: 3,

        despawnShake: 3,

        collidesGround: true,
    }),
    item.chromium, Object.assign(new BasicBulletType(8, 66), {
        sprite: "missile-large",

        lifetime: 40,
        width: 12,
        height: 22,

        hitSize: 7,
        shootEffect: Fx.shootSmokeSquareBig,
        smokeEffect: Fx.shootSmokeDisperse,
        ammoMultiplier: 2,
        knockback: 0.7,
        pierceCap: 3,
        frontColor: Color.white,
        trailWidth: 3,
        trailLength: 12,
        hitEffect: Fx.hitBulletColor,
        despawnEffect: Fx.hitBulletColor,

        trailEffect: Fx.colorSpark,
        trailRotation: true,
        trailInterval: 3,

        despawnShake: 3,

        collidesGround: true,
    }),
    item.sulfone, Object.assign(new BasicBulletType(6, 45), {
        sprite: "missile-large",

        lifetime: 52,
        width: 12,
        height: 22,

        hitSize: 7,
        shootEffect: Fx.shootSmokeSquareBig,
        smokeEffect: Fx.shootSmokeDisperse,
        ammoMultiplier: 4,
        knockback: 0.5,
        pierceCap: 2,
        frontColor: Color.valueOf("ede892"),
        backColor: Color.valueOf("d9c668"),
        hitColor: Color.valueOf("ede892"),
        trailColor: Color.valueOf("ede892"),
        trailWidth: 3,
        trailLength: 12,
        hitEffect: Fx.hitBulletColor,
        despawnEffect: Fx.hitBulletColor,

        trailEffect: Fx.colorSpark,
        trailRotation: true,
        trailInterval: 3,

        despawnShake: 3,

        collidesGround: true,
        status: StatusEffects.burning,
        statusDuration: 60 * 15,
    })
)

function ToxicAbility(damage, reload, range) {
    return extend(Ability, {
        i: 0,
        j: 75,
        update(unit) {
            this.i += Time.delta
            this.j += Time.delta
            if (this.i >= reload) {
                Units.nearby(null, unit.x, unit.y, range, other => {
                    other.health -= damage;
                    other.apply(status.poisoned, 60 * 15);
                })
                Units.nearbyBuildings(unit.x, unit.y, range, b => {
                    b.health -= damage / 4
                    if (b.health <= 0) {
                        b.kill()
                    }
                })
                this.i = 0
            }
            if (this.j >= 15) {
                Fx.titanSmoke.at(
                    unit.x + Mathf.range(range * 0.7071),
                    unit.y + Mathf.range(range * 0.7071),
                    Color.valueOf("92AB117F")
                )
                this.j -= 15
            }
        },
        /*draw(unit){
            Draw.color(Color.red)

            for(let i = 0; i < 2; i++){
                let rot = i * 180 + Time.time * 1;
                Lines.arc(unit.x, unit.y, range, 0.2, rot);
            }
        }*/
    })
}

const bottle = new UnitType("bottle");
Object.assign(bottle, {
    speed: 0,
    isEnemy: false,
    envDisabled: 0,
    targetable: false,
    hittable: false,
    playerControllable: false,
    createWreck: false,
    createScorch: false,
    logicControllable: false,
    useUnitCap: false,
    allowedInPayloads: false,
    constructor: () => new TimedKillUnit.create(),
    physics: false,
    bounded: false,
    hidden: true,
    lifetime: 60 * 15,
    health: 10000,
    drawMinimap: false,
    flying: false,
    drawCell: false,
    deathSound: Sounds.none,
})
bottle.abilities.add(
    new ToxicAbility(10, 15, 80)
)
bottle.immunities.addAll(status.poisoned)

const midnight = new LiquidTurret("midnight");
exports.midnight = midnight;
Object.assign(midnight, {
    size: 4,
    recoil: 2,
    reload: 180,
    inaccuracy: 5,
    shootCone: 50,
    liquidCapacity: 30,
    ammoPerShot: 10,
    minRange: 190,
    ammoUseEffect: Fx.casing1,
    targetAir: false,
    extinguish: false,
    shootSound: Sounds.artillery,
    range: 8 * 40,
    unitSort: UnitSorts.farthest,
    scaledHealth: 250,
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
    requirements: ItemStack.with(
        Items.silicon, 250,
        item.nickel, 400,
        item.crystal, 300,
        item.manganese, 250,
    ),
})
midnight.ammo(
    liquid.yperite, Object.assign(new ArtilleryBulletType(4, 150), {
        lifetime: 130,
        height: 19,
        width: 17,
        ammoMultiplier: 1,
        backColor: Color.valueOf("515e09"),
        frontColor: Color.white,
        trailColor: Color.valueOf("515e09"),
        shrinkX: 0,
        shrinkY: 0,
        hitShake: 0,
        hitSound: Sounds.none,
        despawnUnit: bottle
    })
)
midnight.drawer = new DrawTurret();
midnight.drawer.parts.addAll(
    Object.assign(new RegionPart("-barrel"), {
        progress: DrawPart.PartProgress.recoil.curve(Interp.pow2In),
        moveY: -5 * 4 / 3,
        heatColor: Color.valueOf("f03b0e"),
        mirror: false,
    }),
    Object.assign(new RegionPart("-side"), {
        heatProgress: DrawPart.PartProgress.recoil,
        progress: DrawPart.PartProgress.warmup,
        mirror: true,
        moveX: 2 * 4 / 3,
        moveY: -0.5,
        moveRot: -40,
        under: true,
        heatColor: Color.red
    })
);

function CometMissile(name, bullet) {
    return extend(MissileUnitType, name, {
        speed: 21,
        maxRange: 6,
        lifetime: 210,
        engineColor: Pal.techBlue,
        trailColor: Pal.techBlue,
        engineLayer: Layer.effect,
        engineSize: 3.1,
        engineOffset: 10,
        rotateSpeed: 0.25,
        trailLength: 18,
        missileAccelTime: 210,
        lowAltitude: true,
        loopSound: Sounds.missileTrail,
        loopSoundVolume: 0.6,
        deathSound: Sounds.largeExplosion,
        deathExplosionEffect: Fx.massiveExplosion,
        targetAir: false,

        fogRadius: 6,

        health: 210,
        init() {
            this.super$init();

            this.weapons.add(
                Object.assign(new Weapon(), {
                    shootCone: 360,
                    mirror: false,
                    reload: 1,
                    shootOnDeath: true,
                    shake: 10,
                    bullet: bullet
                })
            )
        }
    })
}

const comet = new ItemTurret("comet")
exports.comet = comet
Object.assign(comet, {
    reload: 600,
    range: 160 * 8,
    shootCone: 10,
    unitSort: UnitSorts.farthest,
    health: 5250,
    size: 5,
    rotateSpeed: 3,
    recoil: 0.5,
    recoilTime: 30,
    shake: 5,
    maxAmmo: 60,
    ammoPerShot: 30,
    coolantMultiplier: 0.75,
    shootSound: Sounds.dullExplosion,
    category: Category.turret,
    buildVisibility: BuildVisibility.shown,
})
AddCoolant(comet, 0.2)
comet.ammo(
    Items.graphite, Object.assign(new BasicBulletType(0, 1), {
        shootEffect: Fx.shootBig,
        //smokeEffect: Fx.shootSmokeMissile,
        ammoMultiplier: 2,

        spawnUnit: CometMissile("comet-graphite-missile", Object.assign(new ExplosionBulletType(1600, 88), {
            hitColor: Pal.techBlue,
            shootEffect: new MultiEffect(
                Fx.massiveExplosion,
                Fx.scatheExplosion,
                Fx.scatheLight,
                Object.assign(new WaveEffect(), {
                    lifetime: 10,
                    strokeFrom: 4,
                    sizeTo: 130,
                    colorFrom: Pal.techBlue,
                    colorTo: Pal.techBlue,
                    lightColor: Pal.techBlue
                })
            ),

            collidesAir: false,
            buildingDamageMultiplier: 0.3,

            ammoMultiplier: 1,
            fragVelocityMin: 3,
            fragVelocityMax: 4,
            fragLifeMin: 0.8,
            fragLifeMax: 1.25,
            fragBullets: 6,
            fragRandomSpread: 60,
            fragBullet: Object.assign(new ArtilleryBulletType(1, 38), {
                buildingDamageMultiplier: 0.3,
                drag: 0.02,
                hitEffect: Fx.massiveExplosion,
                despawnEffect: Fx.scatheSlash,
                knockback: 0.8,
                lifetime: 10,
                width: 18,
                height: 18,
                collidesTiles: false,
                splashDamageRadius: 24,
                backColor: Pal.techBlue,
                trailColor: Pal.techBlue,
                hitColor: Pal.techBlue,
                frontColor: Color.white,
                smokeEffect: Fx.shootBigSmoke2,
                despawnShake: 7,
                lightRadius: 30,
                lightColor: Pal.techBlue,
                lightOpacity: 0.5,

                trailLength: 20,
                trailWidth: 3.5,
                trailEffect: Fx.none,
            }),
        }))
    }),
    item.biomassSteel, Object.assign(new BasicBulletType(0, 1), {
        shootEffect: Fx.shootBig,
        //smokeEffect: Fx.shootSmokeMissile,
        ammoMultiplier: 1,

        spawnUnit: CometMissile("comet-biomass-steel-missile", Object.assign(new ExplosionBulletType(3000, 8 * 11.5), {
            hitColor: Pal.techBlue,
            shootEffect: new MultiEffect(
                Fx.massiveExplosion,
                Fx.scatheExplosion,
                Fx.scatheLight,
                Object.assign(new WaveEffect(), {
                    lifetime: 10,
                    strokeFrom: 4,
                    sizeTo: 130,
                    colorFrom: Pal.techBlue,
                    colorTo: Pal.techBlue,
                    lightColor: Pal.techBlue
                })
            ),

            collidesAir: false,
            buildingDamageMultiplier: 0.3,

            ammoMultiplier: 1,
            fragVelocityMin: 1,
            fragVelocityMax: 4,
            fragLifeMin: 0.8,
            fragLifeMax: 1.25,
            fragBullets: 10,
            fragRandomSpread: 36,
            fragBullet: Object.assign(new ArtilleryBulletType(1, 48), {
                buildingDamageMultiplier: 0.3,
                drag: 0.02,
                hitEffect: Fx.massiveExplosion,
                despawnEffect: Fx.scatheSlash,
                knockback: 0.8,
                lifetime: 10,
                width: 18,
                height: 18,
                collidesTiles: false,
                splashDamageRadius: 24,
                backColor: Pal.techBlue,
                trailColor: Pal.techBlue,
                hitColor: Pal.techBlue,
                frontColor: Color.white,
                smokeEffect: Fx.shootBigSmoke2,
                despawnShake: 7,
                lightRadius: 30,
                lightColor: Pal.techBlue,
                lightOpacity: 0.5,

                trailLength: 20,
                trailWidth: 3.5,
                trailEffect: Fx.none,
            }),
        }))
    })
)