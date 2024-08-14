const my = require("base/物品");
const lib = require("base/coflib");
const units = require("unit/units");
const status = require("base/status");
const ability = require("base/ability");
const bullets = require("base/bullet");

let cm = units.newUnit("沧溟", UnitWaterMove, {
    health: 8800,
    armor: 14,
    hitSize: 33,
    drag: 0.16,
    accel: 0.22,
    speed: 0.78,
    trailScl: 2.6,
    rotateSpeed: 2,
    trailLength: 36,
    waveTrailX: 13,
    waveTrailY: -13,
    outlineColor: lib.C1F1F1F,
    ammoCapacity: 257,
    ammoType: new ItemAmmoType(my.铱板)
})

function gun(x, y, ang) {
    return Object.assign(new Weapon(lib.modName + "-沧溟机炮"), {
        x: x,
        y: y,
        recoil: 2,
        shootY: 2,
        reload: 36,
        rotate: true,
        shootCone: 20,
        rotateSpeed: 4,
        rotationLimit: 120,
        baseRotation: -ang,
        shootSound: Sounds.shootBig,
        bullet: Object.assign(new BasicBulletType(5, 55), {
            lifetime: 48,
            width: 6,
            height: 9,
            splashDamage: 25,
            splashDamageRadius: 16,
            shootEffect: Fx.shootBig,
            status: StatusEffects.blasted,
            hitEffect: Fx.flakExplosion
        })
    })
}

let wave = new Effect(50, e => {
    Draw.color(e.color.cpy().mul(1.5));
    Fx.rand.setSeed(e.id);
    Draw.z(Layer.scorch - 0.01);
    for (let i = 0; i < 3; i++) {
        Fx.v.trns(e.rotation + Fx.rand.range(40), Fx.rand.random(6 * e.finpow()));
        Fill.circle(e.x + Fx.v.x + Fx.rand.range(4), e.y + Fx.v.y + Fx.rand.range(4), Math.min(e.fout(), e.fin() * e.lifetime / 8) * 4 * Fx.rand.random(0.8, 1.1) + 0.3);
    }
})

let torpedo = Object.assign(new Weapon(lib.modName + "-沧溟鱼雷"), {
    x: 11.5,
    y: -10.5,
    recoil: 2,
    shootY: 2,
    reload: 300,
    rotate: true,
    shootCone: 35,
    rotateSpeed: 4,
    rotationLimit: 20,
    baseRotation: -30,
    //ignoreRotation: true,
    shootSound: Sounds.missileLarge,
    bullet: Object.assign(extend(BasicBulletType, {
        update(b) {
            this.super$update(b);
            if (b.tileOn()) {
                this.mixColorTo = b.tileOn().floor().mapColor;
                if (b.timer.get(2)) wave.at(b.x, b.y, b.tileOn().floor().mapColor);
            }
        }
    }), {
        damage: 850,
        lifetime: 300,
        speed: 0.6,
        width: 9.75,
        height: 15.75,
        shrinkY: 0,
        hitSize: 24,
        drag: -0.008,
        sprite: lib.modName + "-鱼雷",
        backSprite: lib.modName + "-none",
        homingRange: 720,
        homingPower: 0.02,
        rangeOverride: 480,
        layer: Layer.scorch,
        collideFloor: true,
        collidesAir: false,
        absorbable: false,
        keepVelocity: false,
        splashDamage: 650,
        splashDamageRadius: 64,
        shootEffect: Fx.none,
        smokeEffect: Fx.shootBigSmoke2,
        status: StatusEffects.blasted,
        hitSound: Sounds.plasmaboom,
        hitEffect: new MultiEffect(Fx.blastExplosion, Fx.flakExplosion)
    })
})
let main = Object.assign(new Weapon(lib.modName + "-沧溟主炮"), {
    x: 0,
    shake: 4,
    recoil: 3,
    shootY: 7,
    reload: 90,
    rotate: true,
    mirror: false,
    shootCone: 20,
    rotateSpeed: 2,
    ejectEffect: Fx.casing3,
    shootSound: Sounds.artillery,
    bullet: Object.assign(new ArtilleryBulletType(5, 0), {
        lifetime: 84,
        width: 15,
        height: 16,
        trailSize: 6,
        trailMult: 0.8,
        knockback: 2,
        hitShake: 4,
        collidesTiles: false,
        splashDamage: 545,
        splashDamageRadius: 40,
        shootEffect: Fx.shootBig2,
        status: StatusEffects.blasted,
        hitEffect: Fx.massiveExplosion,
        trailEffect: Fx.artilleryTrail,
        frontColor: Pal.missileYellow,
        backColor: Pal.missileYellowBack
    })
})


cm.weapons.add(gun(-6.75, 16.25, -20), gun(13.5, 7.75, 20), torpedo, main);
cm.abilities.add(new ArmorPlateAbility(), new ShieldRegenFieldAbility(80, 800, 240, 200), StatusFieldAbility(status.反扑, 360, 360, 160))