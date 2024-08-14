const lib = require("base/coflib");
const units = require("unit/units");
const status = require("base/status");
const ability = require("base/ability");
const bullets = require("base/bullet");
const FX = require("base/Effect/fightFx");
let range = 8 * 45, baserot = 0, num = 4, trailLength = 40, table = new Table();
let FF5845 = lib.FF5845, FF8663 = lib.FF8663;

let Ob = units.newUnit("观察者", UnitEntity, {
    flying: true,
    hidden: true,
    faceTarget: false,
    lowAltitude: true,
    drawBody: false,
    drawCell: false,
    health: 96000,
    armor: 108,
    hitSize: 32,
    speed: 18 / 7.5,
    rotateSpeed: 4,
    range: 8 * 60,
    engineSize: 0,
    itemCapacity: 0,
    ammoType: new PowerAmmoType(600000),
    ammoCapacity: 2400,
    update(unit) {
        this.super$update(unit);
        Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2, range * 2, cons(b => {
            if (b.within(unit, range) && b.team != unit.team) {
                let ang = Angles.angle(unit.x, unit.y, b.x, b.y);
                b.vel.rotateTo(ang, 1 - unit.healthf());
            }
        }));
    },
    draw(unit) {
        this.super$draw(unit);
        FX.PlayerAim(unit, FF5845);
        unit.shadowAlpha = 0;
        Draw.color(FF5845);
        Draw.z(Layer.effect);
        let Player = Vars.player.unit();
        let target = Units.closestTarget(unit.team, unit.x, unit.y, range);
        let eyerot = Angles.angle(unit.x, unit.y, unit.aimX, unit.aimY);
        if (target == null && unit != Player && unit.dst(Player) < 240) {
            eyerot = Angles.angle(unit.x, unit.y, Player.x, Player.y);
        }
        baserot = Angles.moveToward(baserot, eyerot, 6 * Time.delta);
        let xy = lib.AngleTrns(baserot, 8);
        Fill.circle(unit.x + xy.x, unit.y + xy.y, 4);
        Draw.color(lib.Color("000000"));
        Draw.z(99.9);
        Fill.circle(unit.x, unit.y, 16);
        Draw.reset();
    }
}, {
    collision(other, x, y) {
        this.super$collision(other, x, y);
        let b = other.owner;
        if (other instanceof Bullet && b != null && this.dst(b) > range) {
            this.x = b.x;
            this.y = b.y;
        }
    }
})

function jn() {
    table.clear();
    table.visibility = () => Vars.ui.hudfrag.shown && !Vars.ui.minimapfrag.shown();
    table.background(Styles.black6);
    table.table(Tex.windowEmpty, cons(t => {
        t.button(Icon.add, Styles.flati, 40, () => {
            Vars.player.unit().heal();
        }).size(45).disabled(Vars.player.unit().healthf() > 0.5).row();
        t.button(Icon.refresh, Styles.flati, 40, () => {
            let p = Vars.player.unit(), core = Vars.player.closestCore();
            p.x = Core.camera.position.x = core.x;
            p.y = Core.camera.position.y = core.y;
        }).size(45).row();
        t.button(Icon.save, Styles.flati, 40, () => {
            let dialog = new BaseDialog("tttest");
            dialog.cont.table(cons(a => {
                a.add("测试一下");
                dialog.addCloseButton();
                dialog.show();
            })).grow().center().maxWidth(420);
        }).size(45);
    }))
}

Events.on(ClientLoadEvent, cons(e => {
    jn();
    Vars.ui.hudGroup.fill(cons(t => {
        t.visibility = () => Vars.player.unit().type == Ob;
        t.left().add(table);
    }));
}));
exports.Ob = Ob;

Vars.content.statusEffects().each(s => {
    if (s.damageMultiplier < 1 || s.healthMultiplier < 0.9 || s.speedMultiplier < 1 || s.reloadMultiplier < 1 || s.dragMultiplier < 1 || s.transitionDamage > 0 || s.damage > 0 || s.reactive == true || s.disarm == true) Ob.immunities.add(s);
});
Ob.immunities.addAll(status.熔融, status.蚀骨, status.日耀, status.寄生);

Ob.abilities.add(Object.assign(new RegenAbility(), {
    percentAmount: 1 / 40
}), ability.StatusAbility(120, status.日耀, 300), ability.PurificationAbility());

let shootE = Object.assign(new ParticleEffect(), {
    line: true,
    particles: 12,
    lifetime: 20,
    length: 45,
    cone: 30,
    lenFrom: 6,
    lenTo: 6,
    strokeFrom: 3,
    strokeTo: 0,
    interp: Interp.fastSlow,
    lightColor: FF5845,
    colorFrom: FF8663,
    colorTo: FF5845
});
let hitE = Object.assign(new ParticleEffect(), {
    line: true,
    particles: 12,
    lifetime: 20,
    length: 75,
    cone: 360,
    lenFrom: 6,
    lenTo: 6,
    strokeFrom: 3,
    strokeTo: 0,
    lightColor: FF5845,
    colorFrom: FF8663,
    colorTo: FF5845
});

Ob.weapons.add(Object.assign(extend(Weapon, {}), {
    x: 0,
    recoil: 0,
    shake: 1,
    shootY: 8,
    reload: 900,
    mirror: false,
    rotate: true,
    rotateSpeed: 6,
    shootCone: 0.05,
    shootSound: Sounds.malignShoot,
    bullet: Object.assign(extend(PointBulletType, {}), {
        damage: 480,
        lifetime: 60,
        speed: 8,
        status: status.蚀骨,
        statusDuration: 30,
        trailSpacing: 26,
        trailEffect: Object.assign(new ParticleEffect(), {
            region: "curse-of-flesh-crystal",
            baseRotation: -90,
            particles: 1,
            length: 1,
            baseLength: 1,
            lifetime: 45,
            sizeFrom: 16,
            sizeTo: 0,
            colorFrom: FF5845,
            colorTo: FF8663,
            cone: 0
        }),
        fragBullets: 1,
        fragBullet: Object.assign(bullets.BlockHoleBulletType(240, 5, 240), {
            damage: 0,
            lifetime: 600,
            speed: 0,
            spin: 2,
            width: 32,
            height: 32,
            shrinkY: 0,
            collides: false,
            frontColor: FF8663,
            backColor: FF5845,
            status: status.蚀骨,
            statusDuration: 300,
            splashDamage: 800,
            splashDamageRadius: 80,
            sprite: "curse-of-flesh-star",
            despawnShake: 12,
            despawnSound: Sounds.plasmaboom,
            despawnEffect: new MultiEffect(
                Fx.dynamicSpikes.wrap(FF5845, 75),
                Fx.mineImpactWave.wrap(FF8663, 112.5)
            ),
            fragBullets: 4,
            fragVelocityMin: 1,
            fragSpread: 90,
            fragRandomSpread: 0,
            fragBullet: Object.assign(bullets.EnergyFieldBulletType(8 * 20, 30, 75, 10, status.连锁闪电, 60, FF5845, hitE), {
                damage: 0,
                lifetime: 300,
                speed: 2,
                shrinkY: 0,
                height: 18,
                width: 8,
                collides: false,
                frontColor: FF8663,
                backColor: FF5845,
                homingPower: 0.08,
                homingRange: 600,
                sprite: "curse-of-flesh-arrows",
                trailColor: FF5845,
                trailLength: 60,
                trailWidth: 6,
                trailSinScl: 2.5,
                trailSinMag: 0.75,
                trailInterp: v => Math.max(Mathf.slope(v), 0.4)
            })
        })
    })
}))

for (let i = 0; i < num; i++) {
    let j = i;
    Ob.weapons.add(Object.assign(extend(Weapon, {
        update(unit, mount) {
            this.super$update(unit, mount);
            let angle = 360 / num * j;
            let spd = Time.time * 2;
            let thisXY = lib.AngleTrns(spd * 1 + angle, 8 * 9);
            let posXY = lib.AngleTrns(spd * 2.5 + angle, 8 * 1);
            this.x = thisXY.x + posXY.x;
            this.y = thisXY.y + posXY.y;
            if (this.trail == null) this.trail = new Trail(trailLength);
            this.trail.length = trailLength;
            let xy = lib.AngleTrns(unit.rotation, this.x, this.y);
            this.trail.update(unit.x + xy.x, unit.y + xy.y);
        },
        draw(unit, mount) {
            this.super$draw(unit, mount);
            let xy = lib.AngleTrns(unit.rotation, this.x, this.y);
            Draw.color(FF5845);
            Draw.z(Layer.effect);
            Fill.circle(unit.x + xy.x, unit.y + xy.y, 2);
            if (this.trail != null) this.trail.draw(FF5845, 2);
            Draw.reset();
        }
    }), {
        mirror: false,
        reload: 300,
        shake: 1,
        recoil: 0,
        shootY: 0,
        shootCone: 3,
        rotate: true,
        rotateSpeed: 1.2,
        alternate: false,
        alwaysContinuous: true,
        shootSound: Sounds.laserbeam,
        bullet: Object.assign(bullets.PointLaser(480, 4, true), {
            damage: 600,
            color: FF8663,
            beamEffect: hitE,
            beamEffectInterval: 6,
            beamEffectSize: 3.5,
            status: status.熔融,
            statusDuration: 30,
            damageInterval: 5,
            oscMag: 0.1,
            shake: 0.4
        })
    }))
}

Ob.parts.add(
    Object.assign(new ShapePart(), {
        circle: true,
        hollow: true,
        radius: 16,
        stroke: 1.6,
        color: FF5845,
        colorTo: FF8663,
        layer: 110
    }),
    lib.Halo({
        mirror: false,
        shapes: 4,
        radius: 6,
        triL: 4,
        haloRad: 16,
        haloRS: -1
    }),
    lib.Halo({
        mirror: false,
        shapeR: 180,
        shapes: 4,
        radius: 6,
        triL: 2,
        haloRad: 15,
        haloRS: -1
    })
)

lib.DoubleHalo(Ob, {
    shapes: 2,
    triL: 12,
    haloRad: 24,
    haloRS: 0.25
})
lib.DoubleHalo(Ob, {
    shapes: 2,
    triL: 24,
    haloRad: 28,
    haloRot: 90,
    haloRS: -0.25
})